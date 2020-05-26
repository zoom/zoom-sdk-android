package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.share;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;

import us.zoom.sdk.InMeetingRemoteController;
import us.zoom.sdk.MobileRTCVideoView;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdksample.R;

public class CustomShareView extends FrameLayout implements RCFloatView.IRemoteControlButtonStatusListener, RCMouseView.MouseListener {

    private static final String TAG = "CustomShareView";

    private static final long DOUBLE_SCROLL_INTERVAL_TIME = 150;

    RCMouseView mouseView;

    RCFloatView floatView;

    GestureDetector gestureDetector;
    ScaleGestureDetector scaleGestureDetector;

    InMeetingRemoteController meetingRemoteController;

    private MobileRTCVideoView mobileRTCVideoView;

    private long lastScrollTime;

    private boolean inRemoteControllModel = false;

    private boolean inMouseActived = false;

    public CustomShareView(@NonNull Context context) {
        super(context);
        init();
    }

    public CustomShareView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomShareView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        meetingRemoteController = ZoomSDK.getInstance().getInMeetingService().getInMeetingRemoteController();

        View view = LayoutInflater.from(getContext()).inflate(R.layout.rcmource_layout, this, true);

        mouseView = (RCMouseView) view.findViewById(R.id.rc_mouse);
        mouseView.setListener(this);
        floatView = (RCFloatView) view.findViewById(R.id.rc_float);
        floatView.setRemoteControlButtonStatusListener(this);
        floatView.setTag(true);

        gestureDetector = new GestureDetector(new GestureDetectorListener());
        scaleGestureDetector = new ScaleGestureDetector(getContext(), scaleGestureListener);

    }

    public boolean isRemoteControlActive()
    {
        return inMouseActived;
    }

    public void setMobileRTCVideoView(MobileRTCVideoView mobileRTCVideoView) {
        this.mobileRTCVideoView = mobileRTCVideoView;
    }

    @Override
    public void onMouseMove(float x, float y) {
        Log.d(TAG, "onMouseMove :" + x + " :" + y);
        if (inMouseActived) {
            x = mobileRTCVideoView.getVideoViewManager().viewToShareContentX(x);
            y = mobileRTCVideoView.getVideoViewManager().viewToShareContentY(y);
            meetingRemoteController.remoteControlSingleMove(x, y);
        }
    }

    @Override
    public void onMouseClick(float x, float y) {
        Log.d(TAG, "onMouseClick :" + x + " :" + y);
        if (inMouseActived) {
            x = mobileRTCVideoView.getVideoViewManager().viewToShareContentX(x);
            y = mobileRTCVideoView.getVideoViewManager().viewToShareContentY(y);
            meetingRemoteController.remoteControlSingleTap(x, y);
        }
    }

    @Override
    public void onEnabledRC(boolean enabled) {
        mouseView.showRCMouse(enabled && inRemoteControllModel);
        inMouseActived = inRemoteControllModel && (mouseView.getVisibility() == VISIBLE);
    }

    private ScaleGestureDetector.OnScaleGestureListener scaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {

//            Log.d(TAG, "onScale:" + scaleGestureDetector.getScaleFactor() + " x:" + scaleGestureDetector.getCurrentSpanX() + " y:" + scaleGestureDetector.getCurrentSpanY());
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
//            Log.d(TAG, "onScaleBegin:" + scaleGestureDetector.getScaleFactor() + " x:" + scaleGestureDetector.getCurrentSpanX() + " y:" + scaleGestureDetector.getCurrentSpanY());
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
//            Log.d(TAG, "onScaleEnd:" + scaleGestureDetector.getScaleFactor() + " x:" + scaleGestureDetector.getCurrentSpanX() + " y:" + scaleGestureDetector.getCurrentSpanY());

        }
    };

    class GestureDetectorListener extends GestureDetector.SimpleOnGestureListener {

        public GestureDetectorListener() {
            super();
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.d(TAG, "onLongPress:" + ":" + inRemoteControllModel);
            if (inMouseActived) {
                float x = mobileRTCVideoView.getVideoViewManager().viewToShareContentX(e.getRawX());
                float y = mobileRTCVideoView.getVideoViewManager().viewToShareContentY(e.getRawY());
                Log.d(TAG, "onLongPress:" + ":" + x + ":" + y);
                meetingRemoteController.remoteControlLongPress(x, y);
            }
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            if (inMouseActived) {
                if (System.currentTimeMillis() - lastScrollTime >= DOUBLE_SCROLL_INTERVAL_TIME) {
                    int pointCount = e2.getPointerCount();
                    if (pointCount >= 2) {
                        lastScrollTime = System.currentTimeMillis();
                        meetingRemoteController.remoteControlDoubleScroll(0, distanceY < 0 ? -1 : 1);
                    }
                }
            }

            Log.d(TAG, "onScroll:" + distanceX + ":" + distanceY + " : " + e1.getPointerCount() + ":" + e2.getPointerCount());
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "MotionEvent:" + e);
            if (inMouseActived) {
                float x = mobileRTCVideoView.getVideoViewManager().viewToShareContentX(e.getRawX());
                float y = mobileRTCVideoView.getVideoViewManager().viewToShareContentY(e.getRawY());
                meetingRemoteController.remoteControlDoubleTap(x, y);
            }
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d(TAG, "onSingleTapConfirmed:" + e);
            if (inMouseActived) {
                float x = mobileRTCVideoView.getVideoViewManager().viewToShareContentX(e.getRawX());
                float y = mobileRTCVideoView.getVideoViewManager().viewToShareContentY(e.getRawY());
                meetingRemoteController.remoteControlSingleTap(x, y);
                mouseView.moveMouse(e.getRawX(), e.getRawY());
            }
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean retVal = scaleGestureDetector.onTouchEvent(event);
        retVal = gestureDetector.onTouchEvent(event) || retVal;
        return (inMouseActived && retVal) || super.onTouchEvent(event);
    }

    public boolean isInRemoteControllModel() {
        return inRemoteControllModel;
    }

    public void enableRC(boolean hasPrivele, boolean isController) {
        inRemoteControllModel = hasPrivele && isController;
        if (hasPrivele) {
            setVisibility(VISIBLE);
            floatView.showRCFloatView(true, (boolean) floatView.getTag(), isController);
            floatView.setTag(false);
        } else {
            setVisibility(INVISIBLE);
            floatView.showRCFloatView(false, false, isController);
        }

        inMouseActived = inRemoteControllModel && (mouseView.getVisibility() == VISIBLE);
    }


}
