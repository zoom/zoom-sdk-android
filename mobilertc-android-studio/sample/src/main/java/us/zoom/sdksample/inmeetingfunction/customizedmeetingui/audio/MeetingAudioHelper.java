package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.audio;


import us.zoom.sdk.InMeetingAudioController;
import us.zoom.sdk.InMeetingService;
import us.zoom.sdk.ZoomSDK;

public class MeetingAudioHelper {

    private InMeetingAudioController mInMeetingAudioController;

    private InMeetingService mInMeetingService;

    private AudioCallBack callBack;

    public interface AudioCallBack {

        boolean requestAudioPermission();

        void updateAudioButton();
    }

    public MeetingAudioHelper(AudioCallBack callBack) {
        this.callBack = callBack;
        mInMeetingAudioController = ZoomSDK.getInstance().getInMeetingService().getInMeetingAudioController();
        mInMeetingService = ZoomSDK.getInstance().getInMeetingService();
    }

    public void switchAudio() {
        if (null == callBack || !callBack.requestAudioPermission()) {
            return;
        }
        if (isAudioConnected()) {
            if (isMyAudioMuted()) {
                if (canUnmuteMyAudio()) {
                    muteMyAudio(false);
                }
            } else {
                muteMyAudio(true);
            }
        } else {
            connectAudioWithVoIP();
        }
    }

    public boolean isAudioConnected() {
        return mInMeetingAudioController.isAudioConnected();
    }

    public boolean isMyAudioMuted() {
        return mInMeetingAudioController.isMyAudioMuted();
    }

    public boolean canUnmuteMyAudio() {
        return mInMeetingAudioController.canUnmuteMyAudio();
    }

    public void muteMyAudio(boolean mute) {
        mInMeetingAudioController.muteMyAudio(mute);
    }

    public void connectAudioWithVoIP() {
        mInMeetingAudioController.connectAudioWithVoIP();
    }

    public boolean canSwitchAudioOutput() {
        return mInMeetingAudioController.canSwitchAudioOutput();
    }

    public boolean getLoudSpeakerStatus() {
        return mInMeetingAudioController.getLoudSpeakerStatus();
    }

    public void disconnectAudio() {
        mInMeetingAudioController.disconnectAudio();
    }

    public void switchLoudSpeaker() {
        if (mInMeetingAudioController.canSwitchAudioOutput()) {
            mInMeetingAudioController.setLoudSpeakerStatus(!mInMeetingAudioController.getLoudSpeakerStatus());
        }
    }

    public void onUserAudioStatusChanged(long userId) {
        if (mInMeetingService.isMyself(userId)) {
            if (null != callBack) {
                callBack.updateAudioButton();
            }
        }
    }

    public void onUserAudioTypeChanged(long userId) {
        if (mInMeetingService.isMyself(userId)) {
            if (null != callBack) {
                callBack.updateAudioButton();
            }
        }
    }

    public void onMyAudioSourceTypeChanged(int type) {

    }

}
