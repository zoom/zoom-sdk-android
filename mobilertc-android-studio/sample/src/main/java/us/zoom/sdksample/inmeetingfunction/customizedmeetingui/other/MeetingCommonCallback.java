package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.other;


import us.zoom.sdk.InMeetingEventHandler;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.BaseCallback;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.BaseEvent;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.SimpleInMeetingListener;

public class MeetingCommonCallback extends BaseCallback<MeetingCommonCallback.CommonEvent> {


    public interface CommonEvent extends BaseEvent {

        void onWebinarNeedRegister();

        void onMeetingFail(int errorCode, int internalErrorCode);

        void onMeetingLeaveComplete(long ret);

        void onMeetingStatusChanged(MeetingStatus meetingStatus, int errorCode, int internalErrorCode);

        void onMeetingNeedPasswordOrDisplayName(boolean needPassword, boolean needDisplayName, InMeetingEventHandler handler);

        void onMeetingNeedColseOtherMeeting(InMeetingEventHandler inMeetingEventHandler);

        void onJoinWebinarNeedUserNameAndEmail(InMeetingEventHandler inMeetingEventHandler);

        void onFreeMeetingReminder(boolean isOrignalHost, boolean canUpgrade, boolean isFirstGift);

    }

    static MeetingCommonCallback instance;

    private MeetingCommonCallback() {
        init();
    }

    protected void init() {
        ZoomSDK.getInstance().getInMeetingService().addListener(commonListener);
        ZoomSDK.getInstance().getMeetingService().addListener(serviceListener);
    }

    public static MeetingCommonCallback getInstance() {
        if (null == instance) {
            synchronized (MeetingCommonCallback.class) {
                if (null == instance) {
                    instance = new MeetingCommonCallback();
                }
            }
        }
        return instance;
    }

    MeetingServiceListener serviceListener = new MeetingServiceListener() {
        @Override
        public void onMeetingStatusChanged(MeetingStatus meetingStatus, int errorCode, int internalErrorCode) {
            for (CommonEvent event : callbacks) {
                event.onMeetingStatusChanged(meetingStatus, errorCode, internalErrorCode);
            }
        }
    };

    SimpleInMeetingListener commonListener = new SimpleInMeetingListener() {
        @Override
        public void onWebinarNeedRegister() {
            for (CommonEvent event : callbacks) {
                event.onWebinarNeedRegister();
            }
        }

        public void onMeetingFail(int errorCode, int internalErrorCode) {
            for (CommonEvent event : callbacks) {
                event.onMeetingFail(errorCode, internalErrorCode);
            }
        }

        @Override
        public void onMeetingLeaveComplete(long ret) {
            for (CommonEvent event : callbacks) {
                event.onMeetingLeaveComplete(ret);
            }
        }


        @Override
        public void onMeetingNeedPasswordOrDisplayName(boolean needPassword, boolean needDisplayName, InMeetingEventHandler handler) {
            for (CommonEvent event : callbacks) {
                event.onMeetingNeedPasswordOrDisplayName(needPassword, needDisplayName, handler);
            }

        }

        @Override
        public void onMeetingNeedColseOtherMeeting(InMeetingEventHandler inMeetingEventHandler) {
            for (CommonEvent event : callbacks) {
                event.onMeetingNeedColseOtherMeeting(inMeetingEventHandler);
            }
        }

        @Override
        public void onJoinWebinarNeedUserNameAndEmail(InMeetingEventHandler inMeetingEventHandler) {
            for (CommonEvent event : callbacks) {
                event.onJoinWebinarNeedUserNameAndEmail(inMeetingEventHandler);
            }
        }

        @Override
        public void onSpotlightVideoChanged(boolean b) {

        }

        @Override
        public void onFreeMeetingReminder(boolean isOrignalHost, boolean canUpgrade, boolean isFirstGift) {
            for (CommonEvent event : callbacks) {
                event.onFreeMeetingReminder(isOrignalHost, canUpgrade, isFirstGift);
            }
        }
    };


}
