package us.zoom.sdksample.inmeetingfunction.zoommeetingui;


import us.zoom.sdk.InstantMeetingOptions;
import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.MeetingOptions;
import us.zoom.sdk.StartMeetingOptions;

public class ZoomMeetingUISettingHelper {

    private static StartMeetingOptions meetingOptions = new StartMeetingOptions();


    public static StartMeetingOptions getMeetingOptions() {
        //options setting sample : MeetingSettingActivity
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

        return meetingOptions;
    }

    public static StartMeetingOptions getStartMeetingOptions() {
        StartMeetingOptions opts = new StartMeetingOptions();
        fillMeetingOption(opts);
        return opts;
    }

    public static JoinMeetingOptions getJoinMeetingOptions() {
        JoinMeetingOptions opts = new JoinMeetingOptions();
        fillMeetingOption(opts);
        opts.no_audio = meetingOptions.no_audio;
        return opts;
    }

    private static MeetingOptions fillMeetingOption(MeetingOptions opts)
    {
        opts.no_driving_mode = meetingOptions.no_driving_mode;
        opts.no_invite = meetingOptions.no_invite;
        opts.no_meeting_end_message = meetingOptions.no_meeting_end_message;
        opts.no_titlebar = meetingOptions.no_titlebar;
        opts.no_bottom_toolbar = meetingOptions.no_bottom_toolbar;
        opts.no_dial_in_via_phone = meetingOptions.no_dial_in_via_phone;
        opts.no_dial_out_to_phone = meetingOptions.no_dial_out_to_phone;
        opts.no_disconnect_audio = meetingOptions.no_disconnect_audio;
        opts.no_share = meetingOptions.no_share;
        opts.invite_options = meetingOptions.invite_options;
        opts.no_video = meetingOptions.no_video;
        opts.meeting_views_options = meetingOptions.meeting_views_options;
        opts.no_meeting_error_message = meetingOptions.no_meeting_error_message;
        opts.participant_id = meetingOptions.participant_id;
        return opts;
    }

    public static InstantMeetingOptions getInstantMeetingOptions() {
        InstantMeetingOptions opts = new InstantMeetingOptions();
        fillMeetingOption(opts);
        return opts;
    }

}
