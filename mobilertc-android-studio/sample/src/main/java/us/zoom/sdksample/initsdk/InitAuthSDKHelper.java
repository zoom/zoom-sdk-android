package us.zoom.sdksample.initsdk;

import android.content.Context;
import android.util.Log;

import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKInitializeListener;

/**
 * Init and auth zoom sdk first before using SDK interfaces
 */
public class InitAuthSDKHelper implements AuthConstants, ZoomSDKInitializeListener {

    private final static String TAG = "InitAuthSDKHelper";

    private static InitAuthSDKHelper mInitAuthSDKHelper;

    private ZoomSDK mZoomSDK;

    private InitAuthSDKCallback mInitAuthSDKCallback;

    private InitAuthSDKHelper() {
        mZoomSDK = ZoomSDK.getInstance();
    }

    public synchronized static InitAuthSDKHelper getInstance() {
        mInitAuthSDKHelper = new InitAuthSDKHelper();
        return mInitAuthSDKHelper;
    }

    /**
     * init sdk method
     */
    public void initSDK(Context context, InitAuthSDKCallback callback) {
        if(!mZoomSDK.isInitialized()) {
            mInitAuthSDKCallback = callback;
            mZoomSDK.initialize(context, SDK_KEY, SDK_SECRET, WEB_DOMAIN, this,true,5);
        }
    }

    /**
     * init sdk callback
     * @param errorCode defined in {@link us.zoom.sdk.ZoomError}
     * @param internalErrorCode Zoom internal error code
     */
    @Override
    public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
        Log.i(TAG, "onZoomSDKInitializeResult, errorCode=" + errorCode + ", internalErrorCode=" + internalErrorCode);

        if(mInitAuthSDKCallback != null) {
            mInitAuthSDKCallback.onZoomSDKInitializeResult(errorCode, internalErrorCode);
        }
    }
}
