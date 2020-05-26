package us.zoom.sdksample.startjoinmeeting.emailloginuser;

import us.zoom.sdk.ZoomSDK;

public class EmailUserLoginHelper {
    private final static String TAG = "EmailUserLogin";

    private static EmailUserLoginHelper mEmailUserLoginHelper;

    private ZoomSDK mZoomSDK;

    private EmailUserLoginHelper() {
        mZoomSDK = ZoomSDK.getInstance();
    }

    public synchronized static EmailUserLoginHelper getInstance() {
        mEmailUserLoginHelper = new EmailUserLoginHelper();
        return mEmailUserLoginHelper;
    }

    /**
     * Login zoom with email/password
     * @param userName the user name/email
     * @param password password
     * @return error code defined in {@link us.zoom.sdk.ZoomApiError}
     */
    public int login(String userName, String password) {
        return mZoomSDK.loginWithZoom(userName, password);
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
