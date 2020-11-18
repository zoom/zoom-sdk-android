package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zipow.videobox.ptapp.PTApp;

import java.util.List;

import us.zoom.sdk.IInterpretationLanguage;
import us.zoom.sdk.IMeetingInterpretationControllerEvent;
import us.zoom.sdk.InMeetingAnnotationController;
import us.zoom.sdk.InMeetingAudioController;
import us.zoom.sdk.InMeetingBOController;
import us.zoom.sdk.InMeetingInterpretationController;
import us.zoom.sdk.InMeetingService;
import us.zoom.sdk.InMeetingShareController;
import us.zoom.sdk.InMeetingUserInfo;
import us.zoom.sdk.InMeetingVideoController;
import us.zoom.sdk.InMeetingWebinarController;
import us.zoom.sdk.MobileRTCSDKError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKPreProcessRawData;
import us.zoom.sdk.ZoomSDKPreProcessor;
import us.zoom.sdk.ZoomSDKVideoSourceHelper;
import us.zoom.sdksample.BuildConfig;
import us.zoom.sdksample.R;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.rawdata.VirtualVideoSource;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.rawdata.WaterMarkData;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.rawdata.YUVConvert;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.adapter.SimpleMenuAdapter;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.adapter.SimpleMenuItem;
import us.zoom.sdksample.ui.QAActivity;

public class MeetingOptionBar extends FrameLayout implements View.OnClickListener {

    private static final String TAG = "MeetingOptionBar";
    private final int MENU_DISCONNECT_AUDIO = 0;
    private final int MENU_SHOW_PLIST = 4;

    //webinar host&cohost
    private final int MENU_AllOW_PANELIST_START_VIDEO = 5;
    private final int MENU_AllOW_ATTENDEE_CHAT = 6;

    private final int MENU_DISALLOW_PANELIST_START_VIDEO = 7;
    private final int MENU_DISALLOW_ATTENDEE_CHAT = 8;

    private final int MENU_SPEAKER_ON = 9;
    private final int MENU_SPEAKER_OFF = 10;

    private final int MENU_ANNOTATION_OFF = 11;
    private final int MENU_ANNOTATION_ON = 12;
    private final int MENU_ANNOTATION_QA = 13;
    private final int MENU_SWITCH_DOMAIN = 14;
    private final int MENU_CREATE_BO = 15;
    private final int MENU_LOWER_ALL_HANDS = 16;
    private final int MENU_RECLAIM_HOST = 17;

    private final int MENU_VIRTUAL_SOURCE = 18;
    private final int MENU_INTERNAL_SOURCE = 19;

    private final int MENU_INTERPRETATION = 20;

    private final int MENU_INTERPRETATION_ADMIN = 21;


    MeetingOptionBarCallBack mCallBack;

    View mContentView;

    View mBottomBar;
    View mTopBar;

    private View mBtnLeave;
    private View mBtnShare;
    private View mBtnCamera;
    private View mBtnAudio;
    private View mBtnSwitchCamera;

    private ImageView mAudioStatusImg;
    private ImageView mVideoStatusImg;
    private ImageView mShareStatusImg;
    private TextView mMeetingNumberText;
    private TextView mMeetingPasswordText;

    private TextView mMeetingAudioText;
    private TextView mMeetingVideoText;
    private TextView mMeetingShareText;


    private InMeetingService mInMeetingService;
    private InMeetingShareController mInMeetingShareController;
    private InMeetingVideoController mInMeetingVideoController;
    private InMeetingAudioController mInMeetingAudioController;
    private InMeetingWebinarController mInMeetingWebinarController;
    private InMeetingAnnotationController meetingAnnotationController;

    private InMeetingInterpretationController meetingInterpretationController;

    private Context mContext;

    public interface MeetingOptionBarCallBack {
        void onClickBack();

        void onClickSwitchCamera();

        void onClickLeave();

        void onClickAudio();

        void onClickVideo();

        void onClickShare();

        void onClickChats();

        void onClickDisconnectAudio();

        void onClickSwitchLoudSpeaker();

        void onClickAdminBo();

        void onClickLowerAllHands();

        void onClickReclaimHost();

        void showMoreMenu(PopupWindow popupWindow);

        void onHidden(boolean hidden);
    }


    public MeetingOptionBar(Context context) {
        super(context);
        init(context);
    }

    public MeetingOptionBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public MeetingOptionBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setCallBack(MeetingOptionBarCallBack callBack) {
        this.mCallBack = callBack;
    }

    void init(Context context) {
        mContext = context;
        mContentView = LayoutInflater.from(context).inflate(R.layout.layout_meeting_option, this, false);
        addView(mContentView);

        mInMeetingService = ZoomSDK.getInstance().getInMeetingService();

        mInMeetingShareController = mInMeetingService.getInMeetingShareController();
        mInMeetingVideoController = mInMeetingService.getInMeetingVideoController();
        mInMeetingAudioController = mInMeetingService.getInMeetingAudioController();
        mInMeetingWebinarController = mInMeetingService.getInMeetingWebinarController();
        meetingAnnotationController = mInMeetingService.getInMeetingAnnotationController();

        meetingInterpretationController = mInMeetingService.getInMeetingInterpretationController();

//        mContentView.setOnClickListener(this);
        mBottomBar = findViewById(R.id.bottom_bar);
        mTopBar = findViewById(R.id.top_bar);
        mBtnLeave = findViewById(R.id.btnLeaveZoomMeeting);
        mBtnLeave.setOnClickListener(this);
        mBtnShare = findViewById(R.id.btnShare);
        mBtnShare.setOnClickListener(this);

        mBtnCamera = findViewById(R.id.btnCamera);
        mBtnCamera.setOnClickListener(this);
        mBtnAudio = findViewById(R.id.btnAudio);
        mBtnAudio.setOnClickListener(this);
        findViewById(R.id.btnPlist).setOnClickListener(this);

        mAudioStatusImg = findViewById(R.id.audioStatusImage);
        mVideoStatusImg = findViewById(R.id.videotatusImage);
        mShareStatusImg = findViewById(R.id.shareStatusImage);

        mMeetingAudioText = findViewById(R.id.text_audio);
        mMeetingVideoText = findViewById(R.id.text_video);
        mMeetingShareText = findViewById(R.id.text_share);


        findViewById(R.id.moreActionImg).setOnClickListener(this);

        mBtnSwitchCamera = findViewById(R.id.btnSwitchCamera);
        mBtnSwitchCamera.setOnClickListener(this);

        mMeetingNumberText = findViewById(R.id.meetingNumber);
        mMeetingPasswordText = findViewById(R.id.txtPassword);


        findViewById(R.id.btnBack).setOnClickListener(this);
    }

    Runnable autoHidden = new Runnable() {
        @Override
        public void run() {
            hideOrShowToolbar(true);
        }
    };

    public void hideOrShowToolbar(boolean hidden) {
        removeCallbacks(autoHidden);
        if (hidden) {
            setVisibility(View.INVISIBLE);
        } else {
            postDelayed(autoHidden, 3000);
            setVisibility(View.VISIBLE);
            bringToFront();
        }
        if (null != mCallBack) {
            mCallBack.onHidden(hidden);
        }
    }


    public int getBottomBarHeight() {
        return mBottomBar.getMeasuredHeight();
    }

    public int getBottomBarBottom() {
        return mBottomBar.getBottom();
    }

    public int getBottomBarTop() {
        return mBottomBar.getTop();
    }

    public int getTopBarHeight() {
        return mTopBar.getMeasuredHeight();
    }

    public View getSwitchCameraView() {
        return mBtnSwitchCamera;
    }

    public void updateMeetingNumber(String text) {
        if (null != mMeetingNumberText) {
            mMeetingNumberText.setText(text);
        }
    }

    public void updateMeetingPassword(String text) {
        if (null != mMeetingPasswordText) {
            if (!TextUtils.isEmpty(text)) {
                mMeetingPasswordText.setVisibility(VISIBLE);
                mMeetingPasswordText.setText(text);
            }else {
                mMeetingPasswordText.setVisibility(GONE);
            }
        }
    }

    public void refreshToolbar() {
        updateAudioButton();
        updateVideoButton();
        updateShareButton();
        updateSwitchCameraButton();
    }

    public void updateAudioButton() {
        if (mInMeetingAudioController.isAudioConnected()) {
            if (mInMeetingAudioController.isMyAudioMuted()) {
                mAudioStatusImg.setImageResource(R.drawable.icon_meeting_audio_mute);
            } else {
                mAudioStatusImg.setImageResource(R.drawable.icon_meeting_audio);
            }
        } else {
            mAudioStatusImg.setImageResource(R.drawable.icon_meeting_noaudio);
        }
    }

    public boolean isMySelfWebinarAttendee() {
        InMeetingUserInfo myUserInfo = mInMeetingService.getMyUserInfo();
        if (myUserInfo != null && mInMeetingService.isWebinarMeeting()) {
            return myUserInfo.getInMeetingUserRole() == InMeetingUserInfo.InMeetingUserRole.USERROLE_ATTENDEE;
        }
        return false;
    }

    public void updateShareButton() {
        if (isMySelfWebinarAttendee()) {
            mBtnShare.setVisibility(View.GONE);
        } else {
            mBtnShare.setVisibility(View.VISIBLE);
            if (mInMeetingShareController.isSharingOut()) {
                mMeetingShareText.setText("Stop share");
                mShareStatusImg.setImageResource(R.drawable.icon_share_pause);
            } else {
                mMeetingShareText.setText("Share");
                mShareStatusImg.setImageResource(R.drawable.icon_share_resume);
            }
        }
    }

    public void updateVideoButton() {
        if (mInMeetingVideoController.isMyVideoMuted()) {
            mVideoStatusImg.setImageResource(R.drawable.icon_meeting_video_mute);
        } else {
            mVideoStatusImg.setImageResource(R.drawable.icon_meeting_video);
        }
    }

    public void updateSwitchCameraButton() {
        if (mInMeetingVideoController.isMyVideoMuted()) {
            mBtnSwitchCamera.setVisibility(View.GONE);
        } else {
            mBtnSwitchCamera.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.btnBack: {
                if (null != mCallBack) {
                    mCallBack.onClickBack();
                }
                break;
            }
            case R.id.btnLeaveZoomMeeting: {
                if (null != mCallBack) {
                    mCallBack.onClickLeave();
                }
                break;
            }
            case R.id.btnShare: {
                if (null != mCallBack) {
                    mCallBack.onClickShare();
                }
                break;
            }
            case R.id.btnCamera: {
                if (null != mCallBack) {
                    mCallBack.onClickVideo();
                }
                break;
            }
            case R.id.btnAudio: {
                if (null != mCallBack) {
                    mCallBack.onClickAudio();
                }
                break;
            }
            case R.id.btnSwitchCamera: {
                if (null != mCallBack) {
                    mCallBack.onClickSwitchCamera();
                }
                break;
            }
            case R.id.moreActionImg: {
                showMoreMenuPopupWindow();
                break;
            }
            case R.id.btnPlist: {
                if (null != mCallBack) {
                    mCallBack.onClickChats();
                }
                break;
            }
            default: {
                setVisibility(INVISIBLE);
                break;
            }
        }

    }

    private boolean isMySelfWebinarHostCohost() {
        InMeetingUserInfo myUserInfo = mInMeetingService.getMyUserInfo();
        if (myUserInfo != null && mInMeetingService.isWebinarMeeting()) {
            return myUserInfo.getInMeetingUserRole() == InMeetingUserInfo.InMeetingUserRole.USERROLE_HOST
                    || myUserInfo.getInMeetingUserRole() == InMeetingUserInfo.InMeetingUserRole.USERROLE_COHOST;
        }
        return false;
    }

    private boolean isMySelfMeetingHostBoModerator() {
        InMeetingUserInfo myUserInfo = mInMeetingService.getMyUserInfo();
        if (myUserInfo != null && !mInMeetingService.isWebinarMeeting()) {
            InMeetingUserInfo.InMeetingUserRole role = myUserInfo.getInMeetingUserRole();
            return role == InMeetingUserInfo.InMeetingUserRole.USERROLE_HOST ||
                    role == InMeetingUserInfo.InMeetingUserRole.USERROLE_BREAKOUTROOM_MODERATOR;
        }
        return false;
    }


    private boolean isMySelfHost() {
        InMeetingUserInfo myUserInfo = mInMeetingService.getMyUserInfo();
        if (myUserInfo != null) {
            return myUserInfo.getInMeetingUserRole() == InMeetingUserInfo.InMeetingUserRole.USERROLE_HOST;
        }
        return false;
    }

    private boolean isMySelfHostCohost() {
        InMeetingUserInfo myUserInfo = mInMeetingService.getMyUserInfo();
        if (myUserInfo != null) {
            return myUserInfo.getInMeetingUserRole() == InMeetingUserInfo.InMeetingUserRole.USERROLE_HOST
                    || myUserInfo.getInMeetingUserRole() == InMeetingUserInfo.InMeetingUserRole.USERROLE_COHOST;
        }
        return false;
    }

    VirtualVideoSource virtualVideoSource;
    private void showMoreMenuPopupWindow() {
        final SimpleMenuAdapter menuAdapter = new SimpleMenuAdapter(mContext);
        if (mInMeetingAudioController.isAudioConnected()) {
            menuAdapter.addItem(new SimpleMenuItem(MENU_DISCONNECT_AUDIO, "Disconnect Audio"));
        }

        if (mInMeetingAudioController.canSwitchAudioOutput()) {
            if (mInMeetingAudioController.getLoudSpeakerStatus()) {
                menuAdapter.addItem(new SimpleMenuItem(MENU_SPEAKER_OFF, "Speak Off"));
            } else {
                menuAdapter.addItem(new SimpleMenuItem(MENU_SPEAKER_ON, "Speak On"));
            }
        }

        if (!isMySelfWebinarAttendee())
            menuAdapter.addItem((new SimpleMenuItem(MENU_SHOW_PLIST, "Paticipants")));

        if (meetingAnnotationController.canDisableViewerAnnotation()) {
            if (!meetingAnnotationController.isViewerAnnotationDisabled()) {
                menuAdapter.addItem((new SimpleMenuItem(MENU_ANNOTATION_OFF, "Disable Annotation")));
            } else {
                menuAdapter.addItem((new SimpleMenuItem(MENU_ANNOTATION_ON, "Enable Annotation")));
            }
        }

        if (isMySelfWebinarHostCohost()) {
            if (mInMeetingWebinarController.isAllowPanellistStartVideo()) {
                menuAdapter.addItem((new SimpleMenuItem(MENU_DISALLOW_PANELIST_START_VIDEO, "Disallow panelist start video")));
            } else {
                menuAdapter.addItem((new SimpleMenuItem(MENU_AllOW_PANELIST_START_VIDEO, "Allow panelist start video")));
            }

            if (mInMeetingWebinarController.isAllowAttendeeChat()) {
                menuAdapter.addItem((new SimpleMenuItem(MENU_DISALLOW_ATTENDEE_CHAT, "Disallow attendee chat")));
            } else {
                menuAdapter.addItem((new SimpleMenuItem(MENU_AllOW_ATTENDEE_CHAT, "Allow attendee chat")));
            }
        }

        if (BuildConfig.DEBUG) {
//            menuAdapter.addItem((new SimpleMenuItem(MENU_SWITCH_DOMAIN, "Switch Domain")));
        }

        if (BuildConfig.DEBUG) {
            InMeetingUserInfo myUserInfo = mInMeetingService.getMyUserInfo();
            if (myUserInfo != null && mInMeetingService.isWebinarMeeting()) {
                if (mInMeetingService.getInMeetingQAController().isQAEnabled()) {
                    menuAdapter.addItem((new SimpleMenuItem(MENU_ANNOTATION_QA, "QA")));
                }
            }
        }

        if (BuildConfig.DEBUG) {
            InMeetingInterpretationController interpretationController = ZoomSDK.getInstance().getInMeetingService().getInMeetingInterpretationController();
            if (interpretationController.isInterpretationEnabled() && !interpretationController.isInterpreter()
                    && interpretationController.isInterpretationStarted()) {
                menuAdapter.addItem((new SimpleMenuItem(MENU_INTERPRETATION, "Language Interpretation")));
            }
        }

        if (BuildConfig.DEBUG) {
            InMeetingInterpretationController interpretationController=   ZoomSDK.getInstance().getInMeetingService().getInMeetingInterpretationController();
            if (interpretationController.isInterpretationEnabled()&&isMySelfHost()) {
                menuAdapter.addItem((new SimpleMenuItem(MENU_INTERPRETATION_ADMIN, "Interpretation")));
            }
        }

        if (isMySelfMeetingHostBoModerator()) {
            InMeetingBOController boController = mInMeetingService.getInMeetingBOController();
            if (boController.isBOEnabled()) {
                menuAdapter.addItem((new SimpleMenuItem(MENU_CREATE_BO, "Breakout Rooms")));
            }
        }

//        menuAdapter.addItem((new SimpleMenuItem(MENU_VIRTUAL_SOURCE, "Virtual source")));
//        menuAdapter.addItem((new SimpleMenuItem(MENU_INTERNAL_SOURCE, "Internal source")));

        if (isMySelfHostCohost()) {
            menuAdapter.addItem((new SimpleMenuItem(MENU_LOWER_ALL_HANDS, "Lower All Hands")));
        }

        if (mInMeetingService.canReclaimHost()) {
            menuAdapter.addItem((new SimpleMenuItem(MENU_RECLAIM_HOST, "Reclaim Host")));
        }

        View popupWindowLayout = LayoutInflater.from(mContext).inflate(R.layout.popupwindow, null);

        ListView shareActions = (ListView) popupWindowLayout.findViewById(R.id.actionListView);
        final PopupWindow window = new PopupWindow(popupWindowLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_transparent));
        shareActions.setAdapter(menuAdapter);
        shareActions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SimpleMenuItem item = (SimpleMenuItem) menuAdapter.getItem(position);

                switch (item.getAction()) {
                    case MENU_DISCONNECT_AUDIO:
                        if (null != mCallBack) {
                            mCallBack.onClickDisconnectAudio();
                        }
                        break;
                    case MENU_SHOW_PLIST:
                        if (null != mCallBack) {
                            mCallBack.onClickChats();
                        }
                        break;
                    case MENU_AllOW_ATTENDEE_CHAT:
                        mInMeetingWebinarController.allowAttendeeChat();
                        break;
                    case MENU_AllOW_PANELIST_START_VIDEO:
                        mInMeetingWebinarController.allowPanelistStartVideo();
                        break;
                    case MENU_DISALLOW_ATTENDEE_CHAT:
                        mInMeetingWebinarController.disallowAttendeeChat();
                        break;
                    case MENU_DISALLOW_PANELIST_START_VIDEO:
                        mInMeetingWebinarController.disallowPanelistStartVideo();
                        break;
                    case MENU_SPEAKER_OFF:
                    case MENU_SPEAKER_ON: {
                        if (null != mCallBack) {
                            mCallBack.onClickSwitchLoudSpeaker();
                        }
                        break;
                    }
                    case MENU_ANNOTATION_ON: {
                        meetingAnnotationController.disableViewerAnnotation(false);
                        break;
                    }
                    case MENU_ANNOTATION_OFF: {
                        meetingAnnotationController.disableViewerAnnotation(true);
                        break;
                    }
                    case MENU_ANNOTATION_QA: {
                        mContext.startActivity(new Intent(mContext, QAActivity.class));
                        break;
                    }
                    case MENU_SWITCH_DOMAIN: {
                        boolean success = ZoomSDK.getInstance().switchDomain("zoom.us", true);
                        Log.d(TAG, "switchDomain:" + success);
                        break;
                    }
                    case MENU_CREATE_BO: {
                        if (null != mCallBack) {
                            mCallBack.onClickAdminBo();
                        }
                        break;
                    }
                    case MENU_LOWER_ALL_HANDS: {
                        if (null != mCallBack) {
                            mCallBack.onClickLowerAllHands();
                        }
                        break;
                    }
                    case MENU_RECLAIM_HOST: {
                        if (null != mCallBack) {
                            mCallBack.onClickReclaimHost();
                        }
                        break;
                    }
                    case MENU_VIRTUAL_SOURCE:
                    {
                        ZoomSDKVideoSourceHelper sourceHelper = ZoomSDK.getInstance().getVideoSourceHelper();
                        if (null == virtualVideoSource) {
                            virtualVideoSource = new VirtualVideoSource();
                        }
                        sourceHelper.setExternalVideoSource(virtualVideoSource);
                       break;
                    }
                    case MENU_INTERNAL_SOURCE:{
                        ZoomSDKVideoSourceHelper sourceHelper=ZoomSDK.getInstance().getVideoSourceHelper();
                        Bitmap waterMark = BitmapFactory.decodeResource(getResources(), R.drawable.zm_watermark_sdk);
                        byte[] yuv = YUVConvert.convertBitmapToYuv(waterMark);
                        final WaterMarkData data = new WaterMarkData(waterMark.getWidth(), waterMark.getHeight(), yuv);

                        sourceHelper.setPreProcessor(new ZoomSDKPreProcessor() {
                            @Override
                            public void onPreProcessRawData(ZoomSDKPreProcessRawData rawData) {
                                YUVConvert.addWaterMark(rawData, data, 140, 120, true);
                            }
                        });
                        break;
                    }
                    case MENU_INTERPRETATION: {
                        InMeetingInterpretationController interpre = ZoomSDK.getInstance().getInMeetingService().getInMeetingInterpretationController();
                        Log.d(TAG, "isStart:" + interpre.isInterpretationStarted() + " isInterpreter:" + interpre.isInterpreter());

                        if(interpre.isInterpretationStarted()&&!interpre.isInterpreter()){
                            MeetingInterpretationDialog.show(mContext);
                        }
                        break;
                    }
                    case MENU_INTERPRETATION_ADMIN:{
                        MeetingInterpretationAdminDialog.show(mContext);
                        break;
                    }
                }
                window.dismiss();
            }
        });

        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.update();
        if (null != mCallBack) {
            mCallBack.showMoreMenu(window);
        }
    }


    public boolean isShowing() {
        return getVisibility() == VISIBLE;
    }
}
