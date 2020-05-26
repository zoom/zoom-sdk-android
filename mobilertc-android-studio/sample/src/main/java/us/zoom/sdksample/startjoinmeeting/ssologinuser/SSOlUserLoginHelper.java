package us.zoom.sdksample.startjoinmeeting.ssologinuser;

import us.zoom.sdk.ZoomSDK;

public class SSOlUserLoginHelper {
    private final static String TAG = "SSOlUserLogin";

    private static SSOlUserLoginHelper mSSOlUserLoginHelper;

    private ZoomSDK mZoomSDK;

    private SSOlUserLoginHelper() {
        mZoomSDK = ZoomSDK.getInstance();
    }

    public synchronized static SSOlUserLoginHelper getInstance() {
        mSSOlUserLoginHelper = new SSOlUserLoginHelper();
        return mSSOlUserLoginHelper;
    }

    /**
     * Login zoom with sso token
     * @param token the sso user's token
     * @return error code defined in {@link us.zoom.sdk.ZoomApiError}
     */
    public int loginWithSSOToken(String token) {
        return mZoomSDK.loginWithSSOToken(token);
    }

    /**
     * Logout Zoom SDK.
     * @return true, if user can logout.
     */
    public boolean logout() {
        return mZoomSDK.logoutZoom();
    }

    /**
     * Check if Zoom user is logged in.
     * @return true, if user is logged.
     */
    public boolean isLoggedIn() {
        return mZoomSDK.isLoggedIn();
    }

    /**
     * Try auto login Zoom SDK with local zoom token.
     * @return error code defined in {@link us.zoom.sdk.ZoomApiError}
     */
    public int tryAutoLoginZoom() {
        return mZoomSDK.tryAutoLoginZoom();
    }
}
