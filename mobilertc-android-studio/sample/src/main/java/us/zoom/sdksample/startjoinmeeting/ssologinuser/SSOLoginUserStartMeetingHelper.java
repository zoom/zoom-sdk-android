package us.zoom.sdksample.startjoinmeeting.ssologinuser;

import android.content.Context;

import us.zoom.sdk.InstantMeetingOptions;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.StartMeetingParams4NormalUser;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdksample.inmeetingfunction.zoommeetingui.ZoomMeetingUISettingHelper;

public class SSOLoginUserStartMeetingHelper {
    private final static String TAG = "SSOLoginUserStart";

    private static SSOLoginUserStartMeetingHelper mSSOLoginUserStartMeetingHelper;

    private ZoomSDK mZoomSDK;

    private SSOLoginUserStartMeetingHelper() {
        mZoomSDK = ZoomSDK.getInstance();
    }

    public synchronized static SSOLoginUserStartMeetingHelper getInstance() {
        mSSOLoginUserStartMeetingHelper = new SSOLoginUserStartMeetingHelper();
        return mSSOLoginUserStartMeetingHelper;
    }

    public int startMeetingWithNumber(Context context, String meetingNo) {
        int ret = -1;
        MeetingService meetingService = mZoomSDK.getMeetingService();
        if(meetingService == null) {
            return ret;
        }

        StartMeetingOptions opts =ZoomMeetingUISettingHelper.getStartMeetingOptions();

//		opts.no_driving_mode = true;  //for disable zoom meeting ui driving mode
//		opts.no_invite = true; // for hide invite button on participant view
//		opts.no_meeting_end_message = true; // for disable to show meeting end dialog when meeting is end.
//		opts.no_titlebar = true; // for hide title bar on zoom meeting ui
//		opts.no_bottom_toolbar = true; // for hide bottom bar on zoom meeting ui
//		opts.no_dial_in_via_phone = true;
//		opts.no_dial_out_to_phone = true;
//		opts.no_disconnect_audio = true;
//		opts.no_share = true;
//		opts.invite_options = InviteOptions.INVITE_ENABLE_ALL;
//		opts.no_audio = true;
//		opts.no_video = true;
//		opts.meeting_views_options = MeetingViewsOptions.NO_BUTTON_SHARE + MeetingViewsOptions.NO_BUTTON_VIDEO;
//		opts.no_meeting_error_message = true;

        StartMeetingParams4NormalUser params = new StartMeetingParams4NormalUser();
        params.meetingNo = meetingNo;
        return meetingService.startMeetingWithParams(context, params, opts);
    }

    public int startMeetingWithVanityId(Context context, String vanityId) {
        int ret = -1;
        MeetingService meetingService = mZoomSDK.getMeetingService();
        if(meetingService == null) {
            return ret;
        }

        StartMeetingOptions opts = ZoomMeetingUISettingHelper.getStartMeetingOptions();


        StartMeetingParams4NormalUser params = new StartMeetingParams4NormalUser();
        params.vanityID = vanityId;
        return meetingService.startMeetingWithParams(context, params, opts);
    }

    public int startInstanceMeeting(Context context) {
        int ret = -1;
        MeetingService meetingService = mZoomSDK.getMeetingService();
        if(meetingService == null) {
            return ret;
        }

        InstantMeetingOptions opts = ZoomMeetingUISettingHelper.getInstantMeetingOptions();

        return meetingService.startInstantMeeting(context, opts);
    }
}
