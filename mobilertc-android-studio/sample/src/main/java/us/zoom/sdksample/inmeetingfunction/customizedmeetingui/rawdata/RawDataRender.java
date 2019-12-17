package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.rawdata;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;

import us.zoom.rawdatarender.RawDataBufferType;
import us.zoom.rawdatarender.ZoomTextureViewRender;
import us.zoom.sdk.IZoomSDKVideoRawDataDelegate;
import us.zoom.sdk.MobileRTCRawDataError;
import us.zoom.sdk.MobileRTCSDKError;
import us.zoom.sdk.ZoomSDKRawDataType;
import us.zoom.sdk.ZoomSDKRenderer;
import us.zoom.sdk.ZoomSDKVideoRawData;
import us.zoom.sdk.ZoomSDKVideoResolution;

//note  use ZoomSurfaceViewRender for performance
//use ZoomTextureViewRender for  ui animation
public class RawDataRender extends ZoomTextureViewRender {

    private static final String TAG = "RawDataRenderer";

    private static HandlerThread handlerThread;

    private static Handler handler;

    private long mUserId = -1;

    private ZoomSDKRawDataType mRawDataType;

    private boolean isSubscribeSuccess = false;

    private ZoomSDKRenderer rawDataHelper;

    private long cacheUserId;

    private ZoomSDKRawDataType cacheRawDataType;


    public RawDataRender(Context context) {
        super(context);
        init();
    }

    public RawDataRender(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setBufferType(RawDataBufferType.BYTE_ARRAY);
        initRender();
        startRender();
        rawDataHelper = new ZoomSDKRenderer(videoRendererSink);
        rawDataHelper.setRawDataResolution(ZoomSDKVideoResolution.VideoResolution_360P);

        if (null == handlerThread) {
            handlerThread = new HandlerThread("RawDataCanvas");
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper());
        }
    }

    public void setRawDataResolution(ZoomSDKVideoResolution resolution) {
        rawDataHelper.setRawDataResolution(resolution);
    }

    public ZoomSDKVideoResolution getResolution() {
        return rawDataHelper.getResolution();
    }

    /**
     * recycle view : move out then move in sometime cache hit  without call onBindViewHolder
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // onDetachedFromWindow  unSubscribe video and stop render get better performance
        startRender();
        if (null != cacheRawDataType && cacheUserId >= 0) {
            subscribe(cacheUserId, cacheRawDataType);
        }
        cacheRawDataType = null;
        cacheUserId = -1;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // onDetachedFromWindow  unSubscribe video get better performance. eg recycleView
        stopRender();
        if (null != mRawDataType && mUserId >= 0) {
            cacheUserId = mUserId;
            cacheRawDataType = mRawDataType;
        }
        unSubscribe();
    }

    public void onVideoStatusChange(boolean isOn) {
        if (!isOn) {
            clearImage(true);
        }
    }

    IZoomSDKVideoRawDataDelegate videoRendererSink = new IZoomSDKVideoRawDataDelegate() {

        @Override
        public void onUserRawDataStatusChanged(UserRawDataStatus status) {
            if (status == UserRawDataStatus.RawData_Off) {
                clearImage(0.0F, 0.0F, 0.0F, 1.0F);
            }
        }

        @Override
        public void onVideoRawDataFrame(final ZoomSDKVideoRawData zoomSDKRawData) {
            boolean isMainThread = Thread.currentThread() == Looper.getMainLooper().getThread();
            if (isMainThread && zoomSDKRawData.canAddRef()) {
                zoomSDKRawData.addRef();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        drawI420YUV(zoomSDKRawData.getyBuffer(), zoomSDKRawData.getuBuffer(), zoomSDKRawData.getvBuffer(),
                                zoomSDKRawData.getStreamWidth(), zoomSDKRawData.getStreamHeight(), zoomSDKRawData.getRotation(), 30);
                        zoomSDKRawData.releaseRef();
                    }
                });
            } else {
                drawI420YUV(zoomSDKRawData.getyBuffer(), zoomSDKRawData.getuBuffer(), zoomSDKRawData.getvBuffer(),
                        zoomSDKRawData.getStreamWidth(), zoomSDKRawData.getStreamHeight(), zoomSDKRawData.getRotation(), 30);

            }

        }
    };

    public MobileRTCRawDataError subscribe(long userId, ZoomSDKRawDataType type) {

        MobileRTCRawDataError ret = rawDataHelper.subscribe(userId, type);

        mUserId = userId;
        mRawDataType = type;

        if (ret == MobileRTCRawDataError.MobileRTCRawData_Success) {
            isSubscribeSuccess = true;
        } else {
            isSubscribeSuccess = false;
        }
        cacheRawDataType = null;
        cacheUserId = -1;
        Log.d(TAG, "subscribe result: userId=" + userId + " ret=" + ret);
        return ret;
    }

    public void clearImage(boolean black) {
        if (black) {
            clearImage(0.0F, 0.0F, 0.0F, 1.0F);
        } else {
            clearImage(0.0F, 0.0F, 0.0F, 0.0F);
        }
    }


    public long getUserId() {
        return mUserId;
    }


    public int getVideoType() {
        return mRawDataType.ordinal();
    }

    public boolean isSubscribeSuccess() {
        return isSubscribeSuccess;
    }

    public MobileRTCRawDataError unSubscribe() {
        if (!isSubscribeSuccess) {
            return MobileRTCRawDataError.MobileRTCRawData_Success;
        }
        MobileRTCRawDataError ret = rawDataHelper.unSubscribe();
        Log.d(TAG, "unSubscribe: ret=" + ret + ":" + "mUserId=" + mUserId);
        if (ret == MobileRTCRawDataError.MobileRTCRawData_Success) {
            mUserId = -1;
            isSubscribeSuccess = false;
        }
        return ret;
    }

    public void release() {
        rawDataHelper = null;
    }
}

