package us.zoom.sdksample.inmeetingfunction.customizedmeetingui;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import us.zoom.sdk.FreeMeetingNeedUpgradeType;
import us.zoom.sdk.InMeetingAudioController;
import us.zoom.sdk.InMeetingChatMessage;
import us.zoom.sdk.InMeetingEventHandler;
import us.zoom.sdk.InMeetingServiceListener;
import us.zoom.sdk.InMeetingShareController;
import us.zoom.sdk.InMeetingUserInfo;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKRawDataType;
import us.zoom.sdk.ZoomSDKVideoResolution;
import us.zoom.sdk.ZoomSDKVideoSourceHelper;
import us.zoom.sdksample.R;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.audio.MeetingAudioHelper;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.rawdata.AudioRawDataUtil;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.rawdata.RawDataRender;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.rawdata.UserVideoAdapter;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.rawdata.VirtualVideoSource;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.video.MeetingVideoHelper;

public class RawDataMeetingActivity extends FragmentActivity implements InMeetingServiceListener, MeetingServiceListener,  InMeetingShareController.InMeetingShareListener, View.OnClickListener, UserVideoAdapter.ItemTapListener {


    private final static String TAG = RawDataMeetingActivity.class.getSimpleName();

    public final static int REQUEST_PLIST = 1001;

    public final static int REQUEST_CAMERA_CODE = 1010;

    public final static int REQUEST_AUDIO_CODE = 1011;

    public final static int REQUEST_SHARE_SCREEN_PERMISSION = 1001;

    protected final static int REQUEST_SYSTEM_ALERT_WINDOW = 1002;

    RawDataRender bigVideo;

    View actionBarContainer;

    ImageView videoStatusImage;

    ImageView audioStatusImage;

    long myUserId;

    long currentShareUserId;

    private MeetingVideoHelper videoHelper;

    private MeetingAudioHelper audioHelper;

    private AudioRawDataUtil audioRawDataUtil;

    protected RecyclerView userVideoList;

    protected LinearLayout videoListContain;

    protected UserVideoAdapter adapter;

    private View switchToShare;

    VirtualVideoSource virtualVideoSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        setContentView(R.layout.activity_rawdata);
        bigVideo = findViewById(R.id.big_video);
        bigVideo.setOnClickListener(this);

        startPreview();

        videoHelper = new MeetingVideoHelper(this, videoCallBack);
        audioHelper = new MeetingAudioHelper(audioCallBack);

        actionBarContainer = findViewById(R.id.action_bar_container);
        videoStatusImage = findViewById(R.id.videotatusImage);

        audioStatusImage = findViewById(R.id.audioStatusImage);

        findViewById(R.id.btnCamera).setOnClickListener(this);
        findViewById(R.id.btnAudio).setOnClickListener(this);
        findViewById(R.id.btn_leave).setOnClickListener(this);
        findViewById(R.id.btnSwitchCamera).setOnClickListener(this);
        findViewById(R.id.btn_switch_source).setOnClickListener(this);

        switchToShare = findViewById(R.id.btn_switch_share);
        switchToShare.setOnClickListener(this);

        audioRawDataUtil = new AudioRawDataUtil(getApplicationContext());

        userVideoList = findViewById(R.id.userVideoList);
        videoListContain = findViewById(R.id.video_list_contain);
        adapter = new UserVideoAdapter(this, this);
        userVideoList.setItemViewCacheSize(0);
        userVideoList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        userVideoList.setAdapter(adapter);
        setScrollListener();
        ZoomSDK.getInstance().getInMeetingService().addListener(this);
        ZoomSDK.getInstance().getMeetingService().addListener(this);
        ZoomSDK.getInstance().getInMeetingService().getInMeetingShareController().addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bigVideo.unSubscribe();
        adapter.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeSubscribe();
    }

    private void resumeSubscribe() {
        subscribe(0, ZoomSDKRawDataType.RAW_DATA_TYPE_VIDEO);
        adapter.clear();
        List<Long> userInfoList = ZoomSDK.getInstance().getInMeetingService().getInMeetingUserList();
        if (null != userInfoList && userInfoList.size() > 0) {
            adapter.onUserJoin(userInfoList);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int displayRotation = display.getRotation();
        ZoomSDK.getInstance().getInMeetingService().getInMeetingVideoController().rotateMyVideo(displayRotation);
        if (null != actionBarContainer) {
            actionBarContainer.bringToFront();
        }
    }

    private void setScrollListener() {
        final int margin = 0;
        userVideoList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) userVideoList.getLayoutManager();
                    View view = linearLayoutManager.getChildAt(0);
                    int index = linearLayoutManager.findFirstVisibleItemPosition();
                    int left = view.getLeft();
                    if (left < 0) {
                        if (-left > view.getWidth() / 2) {
                            index = index + 1;
                            if (index == adapter.getItemCount() - 1) {
                                recyclerView.scrollBy(view.getWidth(), 0);
                            } else {
                                recyclerView.scrollBy(view.getWidth() + left + 2 * margin, 0);
                            }
                        } else {
                            recyclerView.scrollBy(left - 2 * margin, 0);
                        }
                        if (index == 0) {
                            recyclerView.scrollTo(0, 0);
                        }
                    }
                }
            }
        });

    }

    private void startPreview() {
        bigVideo.setRawDataResolution(ZoomSDKVideoResolution.VideoResolution_720P);
        bigVideo.subscribe(0, ZoomSDKRawDataType.RAW_DATA_TYPE_VIDEO);
        bigVideo.setVideoAspectModel(RawDataRender.VideoAspect_Full_Filled);
    }

    private void subscribe(long userId, ZoomSDKRawDataType type) {
        bigVideo.unSubscribe();
        if (type == ZoomSDKRawDataType.RAW_DATA_TYPE_VIDEO) {
            if (userId == ZoomSDK.getInstance().getInMeetingService().getMyUserID()) {
                bigVideo.setVideoAspectModel(RawDataRender.VideoAspect_Full_Filled);
            } else {
                bigVideo.setVideoAspectModel(RawDataRender.VideoAspect_PanAndScan);
            }
        } else {
            bigVideo.setVideoAspectModel(RawDataRender.VideoAspect_PanAndScan);
        }
        bigVideo.setRawDataResolution(ZoomSDKVideoResolution.VideoResolution_720P);
        bigVideo.subscribe(userId, type);
    }

    @Override
    public void onSingleTap(long userId) {

        if (currentShareUserId > 0) {
            switchToShare.setVisibility(View.VISIBLE);
        } else {
            switchToShare.setVisibility(View.GONE);
        }
        subscribe(userId, ZoomSDKRawDataType.RAW_DATA_TYPE_VIDEO);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCamera: {
                videoHelper.switchVideo();
                break;
            }
            case R.id.btnAudio: {
                audioHelper.switchAudio();
                break;
            }
            case R.id.big_video: {
                actionBarContainer.setVisibility(actionBarContainer.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
//                changeResolution();
                break;
            }
            case R.id.btn_leave: {
                releaseResource();
                ZoomSDK.getInstance().getMeetingService().leaveCurrentMeeting(false);
                break;
            }
            case R.id.btn_switch_share: {
                if (currentShareUserId > 0) {
                    switchToShare.setVisibility(View.GONE);
                    subscribe(currentShareUserId, ZoomSDKRawDataType.RAW_DATA_TYPE_SHARE);
                }
                break;
            }
            case R.id.btnSwitchCamera: {
                if (ZoomSDK.getInstance().getInMeetingService().getInMeetingVideoController().canSwitchCamera()) {
                    ZoomSDK.getInstance().getInMeetingService().getInMeetingVideoController().switchToNextCamera();
                }
                break;
            }
            case R.id.btn_switch_source: {
                ZoomSDKVideoSourceHelper sourceHelper = ZoomSDK.getInstance().getVideoSourceHelper();
                if (null == v.getTag() ||!((boolean) v.getTag())) {
                    if (null == virtualVideoSource) {
                        virtualVideoSource = new VirtualVideoSource();
                    }
                    v.setTag(true);
                    sourceHelper.setExternalVideoSource(virtualVideoSource);
                } else {
                    v.setTag(false);
                    sourceHelper.setExternalVideoSource(null);
                }
                break;
            }
        }
    }

    private void releaseResource()
    {
        bigVideo.unSubscribe();
        adapter.clear();
    }

    private void changeResolution() {
        int resolution = bigVideo.getResolution().ordinal();
        resolution++;
        if (resolution > ZoomSDKVideoResolution.VideoResolution_720P.ordinal()) {
            resolution = 0;
        }
        ZoomSDKVideoResolution videoResolution = ZoomSDKVideoResolution.fromValue(resolution);
        bigVideo.setRawDataResolution(videoResolution);
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        Display display = ((WindowManager) getSystemService(Service.WINDOW_SERVICE)).getDefaultDisplay();
//        int displayRotation = display.getRotation();
//        ZoomSDK.getInstance().getInMeetingService().getInMeetingVideoController().rotateMyVideo(displayRotation);
//        actionBarContainer.bringToFront();
//    }

    @Override
    public void onBackPressed() {
        ZoomSDK.getInstance().getMeetingService().leaveCurrentMeeting(false);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioRawDataUtil.unSubscribe();
        ZoomSDK.getInstance().getMeetingService().removeListener(this);
        ZoomSDK.getInstance().getInMeetingService().removeListener(this);
        ZoomSDK.getInstance().getInMeetingService().getInMeetingShareController().removeListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_SHARE_SCREEN_PERMISSION:
                if (resultCode != RESULT_OK) {
                    Log.d(TAG, "onActivityResult REQUEST_SHARE_SCREEN_PERMISSION no ok ");
                    break;
                }
//                startShareScreen(data);
                break;
            case REQUEST_SYSTEM_ALERT_WINDOW:
//                meetingShareHelper.startShareScreenSession(mScreenInfoData);
                break;
        }
    }

    MeetingAudioHelper.AudioCallBack audioCallBack = new MeetingAudioHelper.AudioCallBack() {
        @Override
        public boolean requestAudioPermission() {
            if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(RawDataMeetingActivity.this, new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_CODE);
                return false;
            }
            return true;
        }

        @Override
        public void updateAudioButton() {
            RawDataMeetingActivity.this.updateAudioButton();
        }
    };

    public void updateAudioButton() {
        InMeetingAudioController audioController = ZoomSDK.getInstance().getInMeetingService().getInMeetingAudioController();
        if (audioController.isAudioConnected()) {
            if (audioController.isMyAudioMuted()) {
                audioStatusImage.setImageResource(R.drawable.icon_meeting_audio_mute);
            } else {
                audioStatusImage.setImageResource(R.drawable.icon_meeting_audio);
            }
        } else {
            audioStatusImage.setImageResource(R.drawable.icon_meeting_noaudio);
        }
    }

    MeetingVideoHelper.VideoCallBack videoCallBack = new MeetingVideoHelper.VideoCallBack() {
        @Override
        public boolean requestVideoPermission() {

            if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(RawDataMeetingActivity.this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);
                return false;
            }
            return true;
        }

        @Override
        public void showCameraList(PopupWindow popupWindow) {
        }
    };

    @Override
    public void onMeetingStatusChanged(MeetingStatus meetingStatus, int errorCode, int internalErrorCode) {
        if (meetingStatus == MeetingStatus.MEETING_STATUS_IN_WAITING_ROOM) {
            myUserId = 0;
            bigVideo.unSubscribe();
            Toast.makeText(this, "In waiting room", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onMeetingNeedPasswordOrDisplayName(boolean needPassword, boolean needDisplayName, InMeetingEventHandler handler) {
        showPsswordDialog(needPassword, needDisplayName, handler);
    }

    Dialog builder;

    private void showPsswordDialog(final boolean needPassword, final boolean needDisplayName, final InMeetingEventHandler handler) {
        if (null != builder) {
            builder.dismiss();
        }
        builder = new Dialog(this, us.zoom.videomeetings.R.style.ZMDialog);
        builder.setTitle("Need password or displayName");
        builder.setContentView(R.layout.layout_input_password_name);

        final EditText pwd = builder.findViewById(R.id.edit_pwd);
        final EditText name = builder.findViewById(R.id.edit_name);
        builder.findViewById(R.id.layout_pwd).setVisibility(needPassword ? View.VISIBLE : View.GONE);
        builder.findViewById(R.id.layout_name).setVisibility(needDisplayName ? View.VISIBLE : View.GONE);

        builder.findViewById(R.id.btn_leave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
                ZoomSDK.getInstance().getInMeetingService().leaveCurrentMeeting(true);
            }
        });
        builder.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = pwd.getText().toString();
                String userName = name.getText().toString();
                if (needPassword && TextUtils.isEmpty(password) || (needDisplayName && TextUtils.isEmpty(userName))) {
                    builder.dismiss();
                    onMeetingNeedPasswordOrDisplayName(needPassword, needDisplayName, handler);
                    return;
                }
                builder.dismiss();
                handler.setMeetingNamePassword(password, userName);
            }
        });
        builder.show();
        pwd.requestFocus();
    }

    @Override
    public void onWebinarNeedRegister() {

    }

    @Override
    public void onJoinWebinarNeedUserNameAndEmail(InMeetingEventHandler handler) {

    }

    @Override
    public void onMeetingNeedColseOtherMeeting(InMeetingEventHandler handler) {

    }

    @Override
    public void onMeetingFail(int errorCode, int internalErrorCode) {

        Toast.makeText(this, "onMeetingFail:" + errorCode, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onMeetingLeaveComplete(long ret) {
        audioRawDataUtil.unSubscribe();
        releaseResource();
        finish();
    }

    @Override
    public void onMeetingUserJoin(List<Long> userList) {
        actionBarContainer.setVisibility(View.VISIBLE);

        adapter.onUserJoin(userList);
        if (adapter.getItemCount() > 0) {
            videoListContain.setVisibility(View.VISIBLE);
        }

        if (myUserId <= 0) {
            audioRawDataUtil.subscribeAudio();
            myUserId = ZoomSDK.getInstance().getInMeetingService().getMyUserID();
            findViewById(R.id.text_connecting).setVisibility(View.GONE);
        }
    }

    @Override
    public void onMeetingUserLeave(List<Long> userList) {
        adapter.onUserLeave(userList);
        if (adapter.getItemCount() == 0) {
            videoListContain.setVisibility(View.INVISIBLE);
        }
        if (userList.contains(bigVideo.getUserId())) {
            long myUserId = ZoomSDK.getInstance().getInMeetingService().getMyUserID();
            if (myUserId != 0) {
                subscribe(myUserId, ZoomSDKRawDataType.RAW_DATA_TYPE_VIDEO);
            } else {
                bigVideo.unSubscribe();
            }
        }
    }

    @Override
    public void onMeetingUserUpdated(long userId) {

    }

    @Override
    public void onMeetingHostChanged(long userId) {

    }

    @Override
    public void onMeetingCoHostChanged(long userId) {

    }

    @Override
    public void onActiveVideoUserChanged(long userId) {

    }

    @Override
    public void onActiveSpeakerVideoUserChanged(long userId) {

    }

    @Override
    public void onSpotlightVideoChanged(boolean on) {

    }

    @Override
    public void onUserVideoStatusChanged(long userId) {

        InMeetingUserInfo userInfo = ZoomSDK.getInstance().getInMeetingService().getUserInfoById(userId);
        if (null == userInfo) {
            return;
        }
        InMeetingUserInfo.VideoStatus status = userInfo.getVideoStatus();
        if (null == status) {
            return;
        }

        if (userId == myUserId) {
            if (!status.isSending()) {
                videoStatusImage.setImageResource(R.drawable.icon_meeting_video_mute);
            } else {
                videoStatusImage.setImageResource(R.drawable.icon_meeting_video);
            }
        }
        if (userId == bigVideo.getUserId()) {
            if (!status.isSending()) {
                bigVideo.onVideoStatusChange(false);
            }
        }

        adapter.onUserVideoStatusChanged(userId);
    }

    @Override
    public void onUserNetworkQualityChanged(long userId) {

    }

    @Override
    public void onMicrophoneStatusError(InMeetingAudioController.MobileRTCMicrophoneError error) {

    }

    @Override
    public void onUserAudioStatusChanged(long userId) {
        if (ZoomSDK.getInstance().getInMeetingService().isMyself(userId)) {
            updateAudioButton();
        }
    }

    @Override
    public void onHostAskUnMute(long userId) {

    }

    @Override
    public void onHostAskStartVideo(long userId) {

    }

    @Override
    public void onUserAudioTypeChanged(long userId) {
        if (ZoomSDK.getInstance().getInMeetingService().isMyself(userId)) {
            updateAudioButton();
        }
    }

    @Override
    public void onMyAudioSourceTypeChanged(int type) {
//        updateAudioButton();
    }

    @Override
    public void onLowOrRaiseHandStatusChanged(long userId, boolean isRaiseHand) {

    }

    @Override
    public void onMeetingSecureKeyNotification(byte[] key) {

    }

    @Override
    public void onChatMessageReceived(InMeetingChatMessage msg) {

    }


    @Override
    public void onShareActiveUser(long userId) {
        currentShareUserId = userId;
        if (currentShareUserId <= 0) {
            switchToShare.setVisibility(View.GONE);
        }
        if (userId > 0) {
            subscribe(currentShareUserId, ZoomSDKRawDataType.RAW_DATA_TYPE_SHARE);
        } else {
            long bigVideoUserId = adapter.getSelectedVideoUserId();
            if (bigVideoUserId <= 0) {
                bigVideoUserId = myUserId;
            }
            subscribe(bigVideoUserId, ZoomSDKRawDataType.RAW_DATA_TYPE_VIDEO);
        }

    }

    @Override
    public void onShareUserReceivingStatus(long userId) {

    }

    @Override
    public void onSilentModeChanged(boolean inSilentMode) {

    }

    @Override
    public void onFreeMeetingReminder(boolean isOrignalHost, boolean canUpgrade, boolean isFirstGift) {

    }

    @Override
    public void onMeetingActiveVideo(long userId) {

    }

    @Override
    public void onSinkAttendeeChatPriviledgeChanged(int i) {

    }

    @Override
    public void onSinkAllowAttendeeChatNotification(int i) {

    }

    @Override
    public void onUserNameChanged(long userId, String name) {

    }

    @Override
    public void onUserVideoStatusChanged(long userId, VideoStatus status) {

    }

    @Override
    public void onUserAudioStatusChanged(long userId, AudioStatus audioStatus) {
        
    }

    @Override
    public void onFreeMeetingNeedToUpgrade(FreeMeetingNeedUpgradeType type, String gifUrl) {

    }

    @Override
    public void onFreeMeetingUpgradeToGiftFreeTrialStart() {

    }

    @Override
    public void onFreeMeetingUpgradeToGiftFreeTrialStop() {

    }

    @Override
    public void onFreeMeetingUpgradeToProMeeting() {

    }
}
