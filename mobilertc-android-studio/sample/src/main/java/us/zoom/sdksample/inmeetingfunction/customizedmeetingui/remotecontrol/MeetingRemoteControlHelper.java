package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.remotecontrol;

import android.util.Log;

import us.zoom.sdk.InMeetingRemoteController;
import us.zoom.sdk.InMeetingService;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.share.CustomShareView;

public class MeetingRemoteControlHelper implements InMeetingRemoteController.InMeetingRemoteControlListener {

    private final static String TAG = MeetingRemoteControlHelper.class.getSimpleName();

    private InMeetingRemoteController mInMeetingRemoteController;

    private InMeetingService mInMeetingService;

    private CustomShareView customShareView;

    public MeetingRemoteControlHelper(CustomShareView shareView) {
        this.customShareView = shareView;
        mInMeetingRemoteController = ZoomSDK.getInstance().getInMeetingService().getInMeetingRemoteController();
        mInMeetingRemoteController.addListener(this);
        mInMeetingService = ZoomSDK.getInstance().getInMeetingService();
    }

    public void refreshRemoteControlStatus() {
        long myUserId = mInMeetingService.getMyUserID();
        boolean hasPriv = mInMeetingRemoteController.hasRemoteControlPrivilegeWithUserId(myUserId);
        boolean isRc = mInMeetingRemoteController.isRemoteController();
        customShareView.enableRC(hasPriv, isRc);
    }

    public void onDestroy() {
        if (null != mInMeetingRemoteController) {
            mInMeetingRemoteController.removeListener(this);
        }
    }

    public boolean isEnableRemoteControl() {
        return customShareView.isRemoteControlActive();
    }


    @Override
    public void onUserGetRemoteControlPrivilege(long userId) {
        long myUserId = mInMeetingService.getMyUserID();
        boolean isMe = userId == myUserId;

        boolean hasPriv = mInMeetingRemoteController.hasRemoteControlPrivilegeWithUserId(myUserId);
        boolean isRc = mInMeetingRemoteController.isRemoteController();
        if (isMe) {
            customShareView.enableRC(hasPriv, isRc);
        }
        Log.d(TAG, "onUserGetRemoteControlPrivilege userId:" + userId + " myUserId:" + myUserId + " hasPriv:" + hasPriv + " isRc:" + isRc);
    }

    @Override
    public void remoteControlStarted(long userId) {
        long myUserId = mInMeetingService.getMyUserID();
        boolean isMe = userId == myUserId;

        boolean hasPriv = mInMeetingRemoteController.hasRemoteControlPrivilegeWithUserId(myUserId);
        boolean isRc = mInMeetingRemoteController.isRemoteController();

        Log.d(TAG, "remoteControlStarted userId:" + userId + " myUserId:" + myUserId + " hasPriv:" + hasPriv + " isRc:" + isRc);
        if (isMe) {
            if (isRc) {
                mInMeetingRemoteController.startRemoteControl();
            }
        } else {
            customShareView.enableRC(hasPriv, isRc);
        }
    }
}
