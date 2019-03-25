package us.zoom.sdksample.startjoinmeeting.apiuser;

import android.content.Context;
import android.util.Log;

import us.zoom.sdk.MeetingService;
import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.StartMeetingParamsWithoutLogin;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdksample.inmeetingfunction.zoommeetingui.ZoomMeetingUISettingHelper;

public class ApiUserStartMeetingHelper {
    private final static String TAG = "ApiUserStart";

    private static ApiUserStartMeetingHelper mApiUserStartMeetingHelper;

    private ZoomSDK mZoomSDK;

    private final static int STYPE = MeetingService.USER_TYPE_API_USER;
    private final static String DISPLAY_NAME = "ZoomUS SDK";

    private ApiUserStartMeetingHelper() {
        mZoomSDK = ZoomSDK.getInstance();
    }

    public synchronized static ApiUserStartMeetingHelper getInstance() {
        mApiUserStartMeetingHelper = new ApiUserStartMeetingHelper();
        return mApiUserStartMeetingHelper;
    }

    /**
     * Start meeting via meeting number
     * @param context Android context
     * @param meetingNo the meeting number
     */
    public int startMeetingWithNumber(Context context, String meetingNo) {
        int ret = -1;
        MeetingService meetingService = mZoomSDK.getMeetingService();
        if(meetingService == null) {
            return ret;
        }

        StartMeetingOptions opts =ZoomMeetingUISettingHelper.getStartMeetingOptions();

        StartMeetingParamsWithoutLogin params = new StartMeetingParamsWithoutLogin();
        APIUserInfo userInfo = APIUserInfoHelper.getAPIUserInfo();
        if (userInfo != null) {
            params.userId = userInfo.userId;
            params.zoomToken = userInfo.userZoomToken;
            params.userType = STYPE;
            params.displayName = DISPLAY_NAME;
            params.zoomAccessToken = userInfo.userZoomAccessToken;
            params.meetingNo = meetingNo;
            ret = meetingService.startMeetingWithParams(context, params, opts);
            Log.i(TAG, "startMeetingWithNumber, ret=" + ret);
        }
        return ret;
    }

    /**
     * Start meeting via meeting vanity id
     * @param context Android context
     * @param vanityId the meeting vanity id
     */
    public int startMeetingWithVanityId(Context context, String vanityId) {
        int ret = -1;
        MeetingService meetingService = mZoomSDK.getMeetingService();
        if(meetingService == null) {
            return ret;
        }

        StartMeetingOptions opts = ZoomMeetingUISettingHelper.getStartMeetingOptions();

        StartMeetingParamsWithoutLogin params = new StartMeetingParamsWithoutLogin();
        APIUserInfo userInfo = APIUserInfoHelper.getAPIUserInfo();
        if (userInfo != null) {
            params.userId = userInfo.userId;
            params.zoomToken = userInfo.userZoomToken;
            params.userType = STYPE;
            params.displayName = DISPLAY_NAME;
            params.zoomAccessToken = userInfo.userZoomAccessToken;
            params.vanityID = vanityId;
            ret = meetingService.startMeetingWithParams(context, params, opts);
            Log.i(TAG, "startMeetingWithVanityId, ret=" + ret);
        }
        return ret;
    }
}
