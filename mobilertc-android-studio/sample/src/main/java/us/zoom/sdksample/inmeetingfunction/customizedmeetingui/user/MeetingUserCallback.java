package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.user;

import java.util.List;

import us.zoom.sdk.ZoomSDK;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.BaseCallback;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.BaseEvent;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.SimpleInMeetingListener;

public class MeetingUserCallback extends BaseCallback<MeetingUserCallback.UserEvent> {

    public interface UserEvent extends BaseEvent {

        void onMeetingUserJoin(List<Long> list);

        void onMeetingUserLeave(List<Long> list);

        void onSilentModeChanged(boolean inSilentMode);
    }

    static MeetingUserCallback instance;

    private MeetingUserCallback() {
        init();
    }


    protected void init() {
        ZoomSDK.getInstance().getInMeetingService().addListener(userListener);
    }

    public static MeetingUserCallback getInstance() {
        if (null == instance) {
            synchronized (MeetingUserCallback.class) {
                if (null == instance) {
                    instance = new MeetingUserCallback();
                }
            }
        }
        return instance;
    }

    SimpleInMeetingListener userListener = new SimpleInMeetingListener() {


        @Override
        public void onMeetingUserJoin(List<Long> list) {

            for (UserEvent event : callbacks) {
                event.onMeetingUserJoin(list);
            }
        }

        @Override
        public void onMeetingUserLeave(List<Long> list) {
            for (UserEvent event : callbacks) {
                event.onMeetingUserLeave(list);
            }
        }

        @Override
        public void onSilentModeChanged(boolean inSilentMode) {
            for (UserEvent event : callbacks) {
                event.onSilentModeChanged(inSilentMode);
            }
        }

    };
}
