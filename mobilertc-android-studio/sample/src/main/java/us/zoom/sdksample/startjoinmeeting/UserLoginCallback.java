package us.zoom.sdksample.startjoinmeeting;

import java.util.ArrayList;

import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;

public class UserLoginCallback implements ZoomSDKAuthenticationListener {

    private final static String TAG = "UserLoginCallback";

    private static UserLoginCallback mUserLoginCallback;

    private ArrayList<ZoomDemoAuthenticationListener> mListenerList = new ArrayList<>();

    public interface ZoomDemoAuthenticationListener {
        void onZoomSDKLoginResult(long result);

        void onZoomSDKLogoutResult(long result);

        void onZoomIdentityExpired();

        void onZoomAuthIdentityExpired();
    }

    private UserLoginCallback() {
        ZoomSDK.getInstance().addAuthenticationListener(this);
    }

    public synchronized static UserLoginCallback getInstance() {
        if (null == mUserLoginCallback) {
            mUserLoginCallback = new UserLoginCallback();
        }
        return mUserLoginCallback;
    }

    public void addListener(ZoomDemoAuthenticationListener listener) {
        if(!mListenerList.contains(listener))
            mListenerList.add(listener);
    }

    public void removeListener(ZoomDemoAuthenticationListener listener) {
        mListenerList.remove(listener);
    }

    /**
     * Called on ZoomSDK login success or failed
     * @param result {@link ZoomAuthenticationError}.ZOOM_AUTH_ERROR_SUCCESS for success
     */
    @Override
    public void onZoomSDKLoginResult(long result) {
        for(ZoomDemoAuthenticationListener listener : mListenerList) {
            if(listener != null) {
                listener.onZoomSDKLoginResult(result);
            }
        }
    }

    /**
     * Called on ZoomSDK logout success or failed
     * @param result {@link ZoomAuthenticationError}.ZOOM_AUTH_ERROR_SUCCESS for success
     */
    @Override
    public void onZoomSDKLogoutResult(long result) {
        for(ZoomDemoAuthenticationListener listener : mListenerList) {
            if(listener != null) {
                listener.onZoomSDKLogoutResult(result);
            }
        }
    }

    /**
     * Zoom identity expired, please re-login or generate new zoom access token via REST api
     */
    @Override
    public void onZoomIdentityExpired() {
        for(ZoomDemoAuthenticationListener listener : mListenerList) {
            if(listener != null) {
                listener.onZoomIdentityExpired();
            }
        }
    }

    /**
     * ZOOM jwt token is expired, please generate a new jwt token.
     */
    @Override
    public void onZoomAuthIdentityExpired() {
        for(ZoomDemoAuthenticationListener listener : mListenerList) {
            if(listener != null) {
                listener.onZoomAuthIdentityExpired();
            }
        }
    }
}
