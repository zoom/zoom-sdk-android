package us.zoom.sdksample.inmeetingfunction.customizedmeetingui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import us.zoom.sdk.IBOAdmin;
import us.zoom.sdk.IBOAssistant;
import us.zoom.sdk.IBOAttendee;
import us.zoom.sdk.IBOAttendeeEvent;
import us.zoom.sdk.IBOData;
import us.zoom.sdk.IBODataEvent;
import us.zoom.sdk.IBOMeeting;
import us.zoom.sdk.IInterpretationLanguage;
import us.zoom.sdk.IMeetingInterpretationControllerEvent;
import us.zoom.sdk.IZoomRetrieveSMSVerificationCodeHandler;
import us.zoom.sdk.IZoomVerifySMSVerificationCodeHandler;
import us.zoom.sdk.InMeetingBOController;
import us.zoom.sdk.InMeetingEventHandler;
import us.zoom.sdk.InMeetingInterpretationController;
import us.zoom.sdk.InMeetingService;
import us.zoom.sdk.InMeetingUserInfo;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.MobileRTCRenderInfo;
import us.zoom.sdk.MobileRTCSMSVerificationError;
import us.zoom.sdk.MobileRTCShareView;
import us.zoom.sdk.MobileRTCVideoUnitRenderInfo;
import us.zoom.sdk.MobileRTCVideoView;
import us.zoom.sdk.MobileRTCVideoViewManager;
import us.zoom.sdk.SmsListener;
import us.zoom.sdk.SmsService;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKCountryCode;
import us.zoom.sdksample.R;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.audio.MeetingAudioCallback;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.audio.MeetingAudioHelper;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.bo.BOEventCallback;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.other.MeetingCommonCallback;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.remotecontrol.MeetingRemoteControlHelper;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.share.MeetingShareCallback;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.share.MeetingShareHelper;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.user.MeetingUserCallback;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.video.MeetingVideoCallback;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.video.MeetingVideoHelper;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.MeetingOptionBar;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.MeetingWindowHelper;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.RealNameAuthDialog;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.adapter.AttenderVideoAdapter;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.share.AnnotateToolbar;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.share.CustomShareView;
import us.zoom.sdksample.ui.APIUserStartJoinMeetingActivity;
import us.zoom.sdksample.ui.BreakoutRoomsAdminActivity;
import us.zoom.sdksample.ui.InitAuthSDKActivity;
import us.zoom.sdksample.ui.LoginUserStartJoinMeetingActivity;
import us.zoom.sdksample.ui.UIUtil;

import static us.zoom.sdk.MobileRTCSDKError.SDKERR_SUCCESS;

public class MyMeetingActivity extends FragmentActivity implements View.OnClickListener, MeetingVideoCallback.VideoEvent,
        MeetingAudioCallback.AudioEvent, MeetingShareCallback.ShareEvent,
        MeetingUserCallback.UserEvent, MeetingCommonCallback.CommonEvent, SmsListener, BOEventCallback.BOEvent {

    private final static String TAG = MyMeetingActivity.class.getSimpleName();

    public final static int REQUEST_PLIST = 1001;

    public final static int REQUEST_CAMERA_CODE = 1010;

    public final static int REQUEST_AUDIO_CODE = 1011;

    public final static int REQUEST_SHARE_SCREEN_PERMISSION = 1001;

    protected final static int REQUEST_SYSTEM_ALERT_WINDOW = 1002;

    protected final static int REQUEST_SYSTEM_ALERT_WINDOW_FOR_MINIWINDOW = 1003;

    private int from = 0;

    private int currentLayoutType = -1;
    private final int LAYOUT_TYPE_PREVIEW = 0;
    private final int LAYOUT_TYPE_WAITHOST = 1;
    private final int LAYOUT_TYPE_IN_WAIT_ROOM = 2;
    private final int LAYOUT_TYPE_ONLY_MYSELF = 3;
    private final int LAYOUT_TYPE_ONETOONE = 4;
    private final int LAYOUT_TYPE_LIST_VIDEO = 5;
    private final int LAYOUT_TYPE_VIEW_SHARE = 6;
    private final int LAYOUT_TYPE_SHARING_VIEW = 7;
    private final int LAYOUT_TYPE_WEBINAR_ATTENDEE = 8;

    private View mWaitJoinView;
    private View mWaitRoomView;
    private TextView mConnectingText;
    private Button mBtnJoinBo, mBtnRequestHelp;

    private LinearLayout videoListLayout;

    private View layout_lans;

    private boolean mMeetingFailed = false;

    public static long mCurShareUserId = -1;

    private MobileRTCVideoView mDefaultVideoView;
    private MobileRTCVideoViewManager mDefaultVideoViewMgr;

    private MeetingAudioHelper meetingAudioHelper;

    private MeetingVideoHelper meetingVideoHelper;

    private MeetingShareHelper meetingShareHelper;

    private MeetingRemoteControlHelper remoteControlHelper;

    private MeetingService mMeetingService;

    private InMeetingService mInMeetingService;

    private SmsService smsService;

    private Intent mScreenInfoData;

    private MobileRTCShareView mShareView;
    private AnnotateToolbar mDrawingView;
    private FrameLayout mMeetingVideoView;

    private View mNormalSenceView;

    private CustomShareView customShareView;

    private RecyclerView mVideoListView;

    private AttenderVideoAdapter mAdapter;

    MeetingOptionBar meetingOptionBar;

    private GestureDetector gestureDetector;

    public static final int JOIN_FROM_UNLOGIN=1;

    public static  final int JOIN_FROM_APIUSER=2;

    public static  final int JOIN_FROM_LOGIN=3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mMeetingService = ZoomSDK.getInstance().getMeetingService();
        mInMeetingService = ZoomSDK.getInstance().getInMeetingService();
        if (mMeetingService == null || mInMeetingService == null) {
            finish();
            return;
        }

        if (null != getIntent().getExtras()) {
            from = getIntent().getExtras().getInt("from");
        }
        meetingAudioHelper = new MeetingAudioHelper(audioCallBack);
        meetingVideoHelper = new MeetingVideoHelper(this, videoCallBack);
        meetingShareHelper = new MeetingShareHelper(this, shareCallBack);

        registerListener();

        setContentView(R.layout.my_meeting_layout);

        gestureDetector = new GestureDetector(new GestureDetectorListener());
        meetingOptionBar = (MeetingOptionBar) findViewById(R.id.meeting_option_contain);
        meetingOptionBar.setCallBack(callBack);
        mMeetingVideoView = (FrameLayout) findViewById(R.id.meetingVideoView);
        mShareView = (MobileRTCShareView) findViewById(R.id.sharingView);
        mDrawingView = (AnnotateToolbar) findViewById(R.id.drawingView);

        mWaitJoinView = (View) findViewById(R.id.waitJoinView);
        mWaitRoomView = (View) findViewById(R.id.waitingRoom);

        LayoutInflater inflater = getLayoutInflater();

        mNormalSenceView = inflater.inflate(R.layout.layout_meeting_content_normal, null);
        mDefaultVideoView = (MobileRTCVideoView) mNormalSenceView.findViewById(R.id.videoView);

        customShareView = (CustomShareView) mNormalSenceView.findViewById(R.id.custom_share_view);
        remoteControlHelper = new MeetingRemoteControlHelper(customShareView);
        mMeetingVideoView.addView(mNormalSenceView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mConnectingText = (TextView) findViewById(R.id.connectingTxt);
        mBtnJoinBo = (Button) findViewById(R.id.btn_join_bo);
        mBtnRequestHelp = findViewById(R.id.btn_request_help);

        mVideoListView = (RecyclerView) findViewById(R.id.videoList);
        mVideoListView.bringToFront();

        videoListLayout = findViewById(R.id.videoListLayout);

        layout_lans=findViewById(R.id.layout_lans);

        mVideoListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new AttenderVideoAdapter(this, getWindowManager().getDefaultDisplay().getWidth(), pinVideoListener);
        mVideoListView.setAdapter(mAdapter);

        mBtnJoinBo.setOnClickListener(this);
        mBtnRequestHelp.setOnClickListener(this);

        refreshToolbar();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    MeetingVideoHelper.VideoCallBack videoCallBack = new MeetingVideoHelper.VideoCallBack() {
        @Override
        public boolean requestVideoPermission() {

            if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MyMeetingActivity.this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);
                return false;
            }
            return true;
        }

        @Override
        public void showCameraList(PopupWindow popupWindow) {
            popupWindow.showAsDropDown(meetingOptionBar.getSwitchCameraView(), 0, 20);
        }
    };

    MeetingAudioHelper.AudioCallBack audioCallBack = new MeetingAudioHelper.AudioCallBack() {
        @Override
        public boolean requestAudioPermission() {
            if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MyMeetingActivity.this, new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_CODE);
                return false;
            }
            return true;
        }

        @Override
        public void updateAudioButton() {
            meetingOptionBar.updateAudioButton();
        }
    };

    MeetingShareHelper.MeetingShareUICallBack shareCallBack = new MeetingShareHelper.MeetingShareUICallBack() {
        @Override
        public void showShareMenu(PopupWindow popupWindow) {
            popupWindow.showAtLocation((View) meetingOptionBar.getParent(), Gravity.BOTTOM | Gravity.CENTER, 0, 150);
        }

        @Override
        public MobileRTCShareView getShareView() {
            return mShareView;
        }
    };


    AttenderVideoAdapter.ItemClickListener pinVideoListener = new AttenderVideoAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position, long userId) {
            if (currentLayoutType == LAYOUT_TYPE_VIEW_SHARE || currentLayoutType == LAYOUT_TYPE_SHARING_VIEW) {
                return;
            }
            mDefaultVideoViewMgr.removeAllVideoUnits();
            MobileRTCVideoUnitRenderInfo renderInfo = new MobileRTCVideoUnitRenderInfo(0, 0, 100, 100);
            mDefaultVideoViewMgr.addAttendeeVideoUnit(userId, renderInfo);
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_join_bo:
                InMeetingBOController boController = mInMeetingService.getInMeetingBOController();
                IBOAttendee boAttendee = boController.getBOAttendeeHelper();
                if (boAttendee != null)
                    boAttendee.joinBo();
                break;
            case R.id.btn_request_help:
                attendeeRequestHelp();
                break;
        }
    }

    class GestureDetectorListener extends GestureDetector.SimpleOnGestureListener {

        public GestureDetectorListener() {
            super();
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            if (mDrawingView.isAnnotationStarted() || remoteControlHelper.isEnableRemoteControl()) {
                meetingOptionBar.hideOrShowToolbar(true);
                return true;
            }
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if ((videoListLayout.getVisibility() == View.VISIBLE && (e.getX() >= videoListLayout.getLeft() || e.getY() <= meetingOptionBar.getTopBarHeight())) || e.getY() >= meetingOptionBar.getBottomBarTop()) {
                    return true;
                }
            } else {
                if ((videoListLayout.getVisibility() == View.VISIBLE && (e.getY() >= videoListLayout.getTop() || e.getY() <= meetingOptionBar.getTopBarHeight())) || e.getY() >= meetingOptionBar.getBottomBarTop()) {
                    return true;
                }
            }
            if (mMeetingService.getMeetingStatus() == MeetingStatus.MEETING_STATUS_INMEETING) {
                meetingOptionBar.hideOrShowToolbar(meetingOptionBar.isShowing());
            }
            return true;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private void refreshToolbar() {
        if (mMeetingService.getMeetingStatus() == MeetingStatus.MEETING_STATUS_INMEETING) {
            mConnectingText.setVisibility(View.GONE);
            meetingOptionBar.updateMeetingNumber(mInMeetingService.getCurrentMeetingNumber() + "");
            meetingOptionBar.updateMeetingPassword( mInMeetingService.getMeetingPassword());
            meetingOptionBar.refreshToolbar();
        } else {
            if (mMeetingService.getMeetingStatus() == MeetingStatus.MEETING_STATUS_CONNECTING) {
                mConnectingText.setVisibility(View.VISIBLE);
            } else {
                mConnectingText.setVisibility(View.GONE);
            }
            meetingOptionBar.hideOrShowToolbar(true);
        }
    }


    private void updateAnnotationBar() {
        if (mCurShareUserId > 0 && !isMySelfWebinarAttendee()) {
            if (meetingShareHelper.isSenderSupportAnnotation(mCurShareUserId)) {
                if (mInMeetingService.isMyself(mCurShareUserId) && !meetingShareHelper.isSharingScreen()) {
                    mDrawingView.setVisibility(View.VISIBLE);
                } else {
                    if (currentLayoutType == LAYOUT_TYPE_VIEW_SHARE) {
                        mDrawingView.setVisibility(View.VISIBLE);
                    } else {
                        mDrawingView.setVisibility(View.GONE);
                    }
                }
            } else {
                mDrawingView.setVisibility(View.GONE);
            }

        } else {
            mDrawingView.setVisibility(View.GONE);
        }
    }

    private void checkShowVideoLayout() {

        mDefaultVideoViewMgr = mDefaultVideoView.getVideoViewManager();
        if (mDefaultVideoViewMgr != null) {
            int newLayoutType = getNewVideoMeetingLayout();
            if (currentLayoutType != newLayoutType || newLayoutType == LAYOUT_TYPE_WEBINAR_ATTENDEE) {
                removeOldLayout(currentLayoutType);
                currentLayoutType = newLayoutType;
                addNewLayout(newLayoutType);
            }
        }
        updateAnnotationBar();
    }

    private int getNewVideoMeetingLayout() {
        int newLayoutType = -1;
        if (mMeetingService.getMeetingStatus() == MeetingStatus.MEETING_STATUS_WAITINGFORHOST) {
            newLayoutType = LAYOUT_TYPE_WAITHOST;
            return newLayoutType;
        }

        if (mMeetingService.getMeetingStatus() == MeetingStatus.MEETING_STATUS_IN_WAITING_ROOM) {
            newLayoutType = LAYOUT_TYPE_IN_WAIT_ROOM;
            return newLayoutType;
        }

        if (mInMeetingService.isWebinarMeeting()) {
            if (isMySelfWebinarAttendee()) {
                newLayoutType = LAYOUT_TYPE_WEBINAR_ATTENDEE;
                return newLayoutType;
            }
        }

        if (meetingShareHelper.isOtherSharing()) {
            newLayoutType = LAYOUT_TYPE_VIEW_SHARE;
        } else if (meetingShareHelper.isSharingOut() && !meetingShareHelper.isSharingScreen()) {
            newLayoutType = LAYOUT_TYPE_SHARING_VIEW;
        } else {
            List<Long> userlist = mInMeetingService.getInMeetingUserList();
            int userCount = 0;
            if (userlist != null) {
                userCount = userlist.size();
            }

            if (userCount > 1) {
                int preCount = userCount;
                for (int i = 0; i < preCount; i++) {
                    InMeetingUserInfo userInfo = mInMeetingService.getUserInfoById(userlist.get(i));
                    if (mInMeetingService.isWebinarMeeting()) {
                        if (userInfo != null && userInfo.getInMeetingUserRole() == InMeetingUserInfo.InMeetingUserRole.USERROLE_ATTENDEE) {
                            userCount--;
                        }
                    }
                }
            }


            if (userCount == 0) {
                newLayoutType = LAYOUT_TYPE_PREVIEW;
            } else if (userCount == 1) {
                newLayoutType = LAYOUT_TYPE_ONLY_MYSELF;
            } else {
                newLayoutType = LAYOUT_TYPE_LIST_VIDEO;
            }
        }
        return newLayoutType;
    }

    private void removeOldLayout(int type) {
        if (type == LAYOUT_TYPE_WAITHOST) {
            mWaitJoinView.setVisibility(View.GONE);
            mMeetingVideoView.setVisibility(View.VISIBLE);
        } else if (type == LAYOUT_TYPE_IN_WAIT_ROOM) {
            mWaitRoomView.setVisibility(View.GONE);
            mMeetingVideoView.setVisibility(View.VISIBLE);
        } else if (type == LAYOUT_TYPE_PREVIEW || type == LAYOUT_TYPE_ONLY_MYSELF || type == LAYOUT_TYPE_ONETOONE) {
            mDefaultVideoViewMgr.removeAllVideoUnits();
        } else if (type == LAYOUT_TYPE_LIST_VIDEO || type == LAYOUT_TYPE_VIEW_SHARE) {
            mDefaultVideoViewMgr.removeAllVideoUnits();
            mDefaultVideoView.setGestureDetectorEnabled(false);
        } else if (type == LAYOUT_TYPE_SHARING_VIEW) {
            mShareView.setVisibility(View.GONE);
            mMeetingVideoView.setVisibility(View.VISIBLE);
        }

        if (type != LAYOUT_TYPE_SHARING_VIEW) {
            if (null != customShareView) {
                customShareView.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void addNewLayout(int type) {
        if (type == LAYOUT_TYPE_WAITHOST) {
            mWaitJoinView.setVisibility(View.VISIBLE);
            refreshToolbar();
            mMeetingVideoView.setVisibility(View.GONE);
        } else if (type == LAYOUT_TYPE_IN_WAIT_ROOM) {
            mWaitRoomView.setVisibility(View.VISIBLE);
            videoListLayout.setVisibility(View.GONE);
            refreshToolbar();
            mMeetingVideoView.setVisibility(View.GONE);
            mDrawingView.setVisibility(View.GONE);
        } else if (type == LAYOUT_TYPE_PREVIEW) {
            showPreviewLayout();
        } else if (type == LAYOUT_TYPE_ONLY_MYSELF||type==LAYOUT_TYPE_WEBINAR_ATTENDEE) {
            showOnlyMeLayout();
        } else if (type == LAYOUT_TYPE_ONETOONE) {
            showOne2OneLayout();
        } else if (type == LAYOUT_TYPE_LIST_VIDEO) {
            showVideoListLayout();
        } else if (type == LAYOUT_TYPE_VIEW_SHARE) {
            showViewShareLayout();
        } else if (type == LAYOUT_TYPE_SHARING_VIEW) {
            showSharingViewOutLayout();
        }
    }

    private void showPreviewLayout() {
        MobileRTCVideoUnitRenderInfo renderInfo1 = new MobileRTCVideoUnitRenderInfo(0, 0, 100, 100);
        mDefaultVideoView.setVisibility(View.VISIBLE);
        mDefaultVideoViewMgr.addPreviewVideoUnit(renderInfo1);
        videoListLayout.setVisibility(View.GONE);
    }

    private void showOnlyMeLayout() {
        mDefaultVideoView.setVisibility(View.VISIBLE);
        videoListLayout.setVisibility(View.GONE);
        MobileRTCVideoUnitRenderInfo renderInfo = new MobileRTCVideoUnitRenderInfo(0, 0, 100, 100);
        InMeetingUserInfo myUserInfo = mInMeetingService.getMyUserInfo();
        if (myUserInfo != null) {
            mDefaultVideoViewMgr.removeAllVideoUnits();
            if (isMySelfWebinarAttendee()) {
                if(mCurShareUserId>0)
                {
                    mDefaultVideoViewMgr.addShareVideoUnit(mCurShareUserId,renderInfo);
                }else {
                    mDefaultVideoViewMgr.addActiveVideoUnit(renderInfo);
                }
            } else {
                mDefaultVideoViewMgr.addAttendeeVideoUnit(myUserInfo.getUserId(), renderInfo);
            }
        }
    }


    private void showOne2OneLayout() {
        mDefaultVideoView.setVisibility(View.VISIBLE);
        videoListLayout.setVisibility(View.VISIBLE);

        MobileRTCVideoUnitRenderInfo renderInfo = new MobileRTCVideoUnitRenderInfo(0, 0, 100, 100);
        //options.aspect_mode = MobileRTCVideoUnitAspectMode.VIDEO_ASPECT_PAN_AND_SCAN;
        mDefaultVideoViewMgr.addActiveVideoUnit(renderInfo);

        mAdapter.setUserList(mInMeetingService.getInMeetingUserList());
        mAdapter.notifyDataSetChanged();

    }

    private void showVideoListLayout() {
        MobileRTCVideoUnitRenderInfo renderInfo = new MobileRTCVideoUnitRenderInfo(0, 0, 100, 100);
        //options.aspect_mode = MobileRTCVideoUnitAspectMode.VIDEO_ASPECT_PAN_AND_SCAN;
        mDefaultVideoViewMgr.addActiveVideoUnit(renderInfo);
        videoListLayout.setVisibility(View.VISIBLE);
        updateAttendeeVideos(mInMeetingService.getInMeetingUserList(), 0);
    }

    private void showSharingViewOutLayout() {
        mAdapter.setUserList(null);
        mAdapter.notifyDataSetChanged();
        videoListLayout.setVisibility(View.GONE);
        mMeetingVideoView.setVisibility(View.GONE);
        mShareView.setVisibility(View.VISIBLE);
    }


    private void updateAttendeeVideos(List<Long> userlist, int action) {
        if (action == 0) {
            mAdapter.setUserList(userlist);
            mAdapter.notifyDataSetChanged();
        } else if (action == 1) {
            mAdapter.addUserList(userlist);
        } else {

            Long userId = mAdapter.getSelectedUserId();
            if (userlist.contains(userId)) {
                List<Long> inmeetingUserList = mInMeetingService.getInMeetingUserList();
                if (inmeetingUserList.size() > 0) {
                    mDefaultVideoViewMgr.removeAllVideoUnits();
                    MobileRTCVideoUnitRenderInfo renderInfo = new MobileRTCVideoUnitRenderInfo(0, 0, 100, 100);
                    mDefaultVideoViewMgr.addAttendeeVideoUnit(inmeetingUserList.get(0), renderInfo);
                }
            }
            mAdapter.removeUserList(userlist);
        }
    }

    private void showViewShareLayout() {
        if (!isMySelfWebinarAttendee()) {
            mDefaultVideoView.setVisibility(View.VISIBLE);
            mDefaultVideoView.setOnClickListener(null);
            mDefaultVideoView.setGestureDetectorEnabled(true);
            long shareUserId = mInMeetingService.activeShareUserID();
            MobileRTCRenderInfo renderInfo1 = new MobileRTCRenderInfo(0, 0, 100, 100);
            mDefaultVideoViewMgr.addShareVideoUnit(shareUserId, renderInfo1);
            updateAttendeeVideos(mInMeetingService.getInMeetingUserList(), 0);

            customShareView.setMobileRTCVideoView(mDefaultVideoView);
            remoteControlHelper.refreshRemoteControlStatus();

        } else {
            mDefaultVideoView.setVisibility(View.VISIBLE);
            mDefaultVideoView.setOnClickListener(null);
            mDefaultVideoView.setGestureDetectorEnabled(true);
            long shareUserId = mInMeetingService.activeShareUserID();
            MobileRTCRenderInfo renderInfo1 = new MobileRTCRenderInfo(0, 0, 100, 100);
            mDefaultVideoViewMgr.addShareVideoUnit(shareUserId, renderInfo1);
        }

        mAdapter.setUserList(null);
        mAdapter.notifyDataSetChanged();
        videoListLayout.setVisibility(View.INVISIBLE);
    }

    private boolean isMySelfWebinarAttendee() {
        InMeetingUserInfo myUserInfo = mInMeetingService.getMyUserInfo();
        if (myUserInfo != null && mInMeetingService.isWebinarMeeting()) {
            return myUserInfo.getInMeetingUserRole() == InMeetingUserInfo.InMeetingUserRole.USERROLE_ATTENDEE;
        }
        return false;
    }

    private boolean isMySelfWebinarHostCohost() {
        InMeetingUserInfo myUserInfo = mInMeetingService.getMyUserInfo();
        if (myUserInfo != null && mInMeetingService.isWebinarMeeting()) {
            return myUserInfo.getInMeetingUserRole() == InMeetingUserInfo.InMeetingUserRole.USERROLE_HOST
                    || myUserInfo.getInMeetingUserRole() == InMeetingUserInfo.InMeetingUserRole.USERROLE_COHOST;
        }
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        meetingVideoHelper.checkVideoRotation(this);
        updateVideoListMargin(!meetingOptionBar.isShowing());
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(mMeetingService == null || mInMeetingService == null){
            return;
        }
        MeetingWindowHelper.getInstance().hiddenMeetingWindow(false);
        checkShowVideoLayout();
        meetingVideoHelper.checkVideoRotation(this);
        mDefaultVideoView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mMeetingService == null || mInMeetingService == null){
            return;
        }
        mDefaultVideoView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mMeetingService == null || mInMeetingService == null){
            return;
        }
        clearSubscribe();
    }

    private void clearSubscribe(){
        if(null!=mDefaultVideoViewMgr)
        {
            mDefaultVideoViewMgr.removeActiveVideoUnit();
        }
        if (null != mInMeetingService) {
            List<Long> userList = mInMeetingService.getInMeetingUserList();
            if (null != userList) {
                mAdapter.removeUserList(userList);
            }
        }
        currentLayoutType=-1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != remoteControlHelper) {
            remoteControlHelper.onDestroy();
        }
        unRegisterListener();
    }

    MeetingOptionBar.MeetingOptionBarCallBack callBack = new MeetingOptionBar.MeetingOptionBarCallBack() {
        @Override
        public void onClickBack() {
            onClickMiniWindow();
        }

        @Override
        public void onClickSwitchCamera() {
            meetingVideoHelper.switchCamera();
        }

        @Override
        public void onClickLeave() {
            showLeaveMeetingDialog();
        }

        @Override
        public void onClickAudio() {
            meetingAudioHelper.switchAudio();
        }

        @Override
        public void onClickVideo() {
            meetingVideoHelper.switchVideo();
        }

        @Override
        public void onClickShare() {
            meetingShareHelper.onClickShare();
        }

        @Override
        public void onClickChats() {
            mInMeetingService.showZoomParticipantsUI(MyMeetingActivity.this, REQUEST_PLIST);
        }

        @Override
        public void onClickDisconnectAudio() {
            meetingAudioHelper.disconnectAudio();
        }

        @Override
        public void onClickSwitchLoudSpeaker() {
            meetingAudioHelper.switchLoudSpeaker();
        }

        @Override
        public void onClickAdminBo() {
            Intent intent = new Intent(MyMeetingActivity.this, BreakoutRoomsAdminActivity.class);
            startActivity(intent);
        }

        @Override
        public void onClickLowerAllHands() {
            if (mInMeetingService.lowerAllHands() == SDKERR_SUCCESS)
                Toast.makeText(MyMeetingActivity.this, "Lower all hands successfully", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onClickReclaimHost() {
            if (mInMeetingService.reclaimHost() == SDKERR_SUCCESS)
                Toast.makeText(MyMeetingActivity.this, "Reclaim host successfully", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void showMoreMenu(PopupWindow popupWindow) {
            popupWindow.showAtLocation((View) meetingOptionBar.getParent(), Gravity.BOTTOM | Gravity.RIGHT, 0, 150);
        }

        @Override
        public void onHidden(boolean hidden) {
            updateVideoListMargin(hidden);
        }
    };


    private void onClickMiniWindow()
    {
        if (mMeetingService.getMeetingStatus() == MeetingStatus.MEETING_STATUS_INMEETING) {
            //stop share
            if (currentLayoutType == LAYOUT_TYPE_VIEW_SHARE) {
                mDefaultVideoViewMgr.removeShareVideoUnit();
                currentLayoutType = -1;
            }

            List<Long> userList = ZoomSDK.getInstance().getInMeetingService().getInMeetingUserList();
            if (null == userList || userList.size()<2) {
                showLeaveMeetingDialog();
                return;
            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && ! Settings.canDrawOverlays(this)){
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + this.getPackageName()));
                startActivityForResult(intent,REQUEST_SYSTEM_ALERT_WINDOW_FOR_MINIWINDOW);
            }else {
                showMainActivity();
            }
        } else {
            showLeaveMeetingDialog();
        }
    }

    @Override
    public void onBackPressed() {
        showLeaveMeetingDialog();
    }

    private void updateVideoListMargin(boolean hidden) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoListLayout.getLayoutParams();
        params.bottomMargin = hidden ? 0 : meetingOptionBar.getBottomBarHeight();
        if (Configuration.ORIENTATION_LANDSCAPE == getResources().getConfiguration().orientation) {
            params.bottomMargin = 0;
        }
        videoListLayout.setLayoutParams(params);
        videoListLayout.bringToFront();
    }


    private void showMainActivity() {
        Class clz = LoginUserStartJoinMeetingActivity.class;
        if (from == JOIN_FROM_UNLOGIN) {
            clz = InitAuthSDKActivity.class;
        } else if (from == JOIN_FROM_APIUSER) {
            clz = APIUserStartJoinMeetingActivity.class;
        }
        Intent intent = new Intent(this, clz);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        clearSubscribe();
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
                mInMeetingService.leaveCurrentMeeting(true);
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


    private void updateVideoView(List<Long> userList, int action) {
        if (currentLayoutType == LAYOUT_TYPE_LIST_VIDEO || currentLayoutType == LAYOUT_TYPE_VIEW_SHARE) {
            if (mVideoListView.getVisibility() == View.VISIBLE) {
                updateAttendeeVideos(userList, action);
            }
        }
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
                startShareScreen(data);
                break;
            case REQUEST_SYSTEM_ALERT_WINDOW:
                meetingShareHelper.startShareScreenSession(mScreenInfoData);
                break;
            case REQUEST_SYSTEM_ALERT_WINDOW_FOR_MINIWINDOW:
            {
                if(resultCode==RESULT_OK)
                {
                    showMainActivity();
                }else {
                    showLeaveMeetingDialog();
                }
                break;
            }
        }
    }


    boolean finished = false;

    @Override
    public void finish() {
        if (!finished) {
            showMainActivity();
        }
        finished = true;
        super.finish();
    }

    private void showLeaveMeetingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (mInMeetingService.isMeetingConnected()) {
            if (mInMeetingService.isMeetingHost()) {
                builder.setTitle("End or leave meeting")
                        .setPositiveButton("End", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                leave(true);
                            }
                        }).setNeutralButton("Leave", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        leave(false);
                    }
                });
            } else {
                builder.setTitle("Leave meeting")
                        .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                leave(false);
                            }
                        });
            }
        } else {
            builder.setTitle("Leave meeting")
                    .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            leave(false);
                        }
                    });
        }
        if (mInMeetingService.getInMeetingBOController().isInBOMeeting()) {
            builder.setNegativeButton("Leave BO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    leaveBo();
                }
            });
        } else {
            builder.setNegativeButton("Cancel", null);
        }
        builder.create().show();
    }

    private void leave(boolean end) {
        if (meetingShareHelper.isSharingOut()) {
            meetingShareHelper.stopShare();
        }
        finish();
        mInMeetingService.leaveCurrentMeeting(end);
    }

    private void leaveBo(){
        InMeetingBOController boController = mInMeetingService.getInMeetingBOController();
        IBOAssistant iboAssistant = boController.getBOAssistantHelper();
        if (iboAssistant != null) {
            iboAssistant.leaveBO();
        } else {
            IBOAttendee boAttendee = boController.getBOAttendeeHelper();
            if (boAttendee != null)
            {
                boAttendee.leaveBo();
            }else {
                leave(false);
            }
        }
    }

    private void showJoinFailDialog(int error) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Meeting Fail")
                .setMessage("Error:" + error)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create();
        dialog.show();
    }

    private void showWebinarNeedRegisterDialog(final InMeetingEventHandler inMeetingEventHandler) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Need register to join this webinar meeting ")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mInMeetingService.leaveCurrentMeeting(true);
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(null!=inMeetingEventHandler)
                        {
                            long time=System.currentTimeMillis();
                            inMeetingEventHandler.setRegisterWebinarInfo("test", time+"@example.com", false);
                        }
                    }
                }).create();
        dialog.show();
    }

    private void showEndOtherMeetingDialog(final InMeetingEventHandler handler) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Meeting Alert")
                .setMessage("You have a meeting that is currently in-progress. Please end it to start a new meeting.")
                .setPositiveButton("End Other Meeting", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.endOtherMeeting();
                    }
                }).setNeutralButton("Leave", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        mInMeetingService.leaveCurrentMeeting(true);
                    }
                }).create();
        dialog.show();
    }

    @SuppressLint("NewApi")
    protected void startShareScreen(Intent data) {
        if (data == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 24 && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            mScreenInfoData = data;
            startActivityForResult(intent, REQUEST_SYSTEM_ALERT_WINDOW);
        } else {
            meetingShareHelper.startShareScreenSession(data);
        }
    }

    public int checkSelfPermission(String permission) {
        if (permission == null || permission.length() == 0) {
            return PackageManager.PERMISSION_DENIED;
        }
        try {
            return checkPermission(permission, android.os.Process.myPid(), android.os.Process.myUid());
        } catch (Throwable e) {
            return PackageManager.PERMISSION_DENIED;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (permissions == null || grantResults == null) {
            return;
        }

        for (int i = 0; i < permissions.length; i++) {
            if (Manifest.permission.RECORD_AUDIO.equals(permissions[i])) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    meetingAudioHelper.switchAudio();
                }
            } else if (Manifest.permission.CAMERA.equals(permissions[i])) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    meetingVideoHelper.switchVideo();
                }
            }
        }
    }


    @Override
    public void onUserAudioStatusChanged(long userId) {
        meetingAudioHelper.onUserAudioStatusChanged(userId);
    }

    @Override
    public void onUserAudioTypeChanged(long userId) {
        meetingAudioHelper.onUserAudioTypeChanged(userId);
    }

    @Override
    public void onMyAudioSourceTypeChanged(int type) {
        meetingAudioHelper.onMyAudioSourceTypeChanged(type);
    }

    @Override
    public void onUserVideoStatusChanged(long userId) {
        meetingOptionBar.updateVideoButton();
        meetingOptionBar.updateSwitchCameraButton();
    }

    @Override
    public void onShareActiveUser(long userId) {
        meetingShareHelper.onShareActiveUser(mCurShareUserId, userId);
        mCurShareUserId = userId;
        meetingOptionBar.updateShareButton();
        checkShowVideoLayout();
    }

    @Override
    public void onSilentModeChanged(boolean inSilentMode) {
        if (inSilentMode)
            meetingShareHelper.stopShare();
    }

    @Override
    public void onShareUserReceivingStatus(long userId) {

    }

    @Override
    public void onMeetingUserJoin(List<Long> userList) {
        checkShowVideoLayout();
        updateVideoView(userList, 1);
    }

    @Override
    public void onMeetingUserLeave(List<Long> userList) {
        checkShowVideoLayout();
        updateVideoView(userList, 2);
    }

    @Override
    public void onWebinarNeedRegister() {
    }

    @Override
    public void onMeetingFail(int errorCode, int internalErrorCode) {
        mMeetingFailed = true;
        mMeetingVideoView.setVisibility(View.GONE);
        mConnectingText.setVisibility(View.GONE);
        showJoinFailDialog(errorCode);
    }

    @Override
    public void onMeetingLeaveComplete(long ret) {
        meetingShareHelper.stopShare();
        if (!mMeetingFailed)
            finish();
    }

    @Override
    public void onMeetingStatusChanged(MeetingStatus meetingStatus, int errorCode, int internalErrorCode) {
        checkShowVideoLayout();
        refreshToolbar();
    }

    @Override
    public void onMeetingNeedPasswordOrDisplayName(boolean needPassword, boolean needDisplayName, InMeetingEventHandler handler) {
        showPsswordDialog(needPassword, needDisplayName, handler);
    }

    @Override
    public void onMeetingNeedColseOtherMeeting(InMeetingEventHandler inMeetingEventHandler) {
        showEndOtherMeetingDialog(inMeetingEventHandler);
    }

    @Override
    public void onJoinWebinarNeedUserNameAndEmail(InMeetingEventHandler inMeetingEventHandler) {
        long time=System.currentTimeMillis();
        showWebinarNeedRegisterDialog(inMeetingEventHandler);
//        inMeetingEventHandler.setRegisterWebinarInfo("test", time+"@example.com", false);
    }

    @Override
    public void onFreeMeetingReminder(boolean isOrignalHost, boolean canUpgrade, boolean isFirstGift) {
        Log.d(TAG, "onFreeMeetingReminder:" + isOrignalHost +" "+ canUpgrade +" "+ isFirstGift);
    }

    @Override
    public void onNeedRealNameAuthMeetingNotification(List<ZoomSDKCountryCode> supportCountryList, String privacyUrl, IZoomRetrieveSMSVerificationCodeHandler handler) {
        Log.d(TAG, "onNeedRealNameAuthMeetingNotification:" + privacyUrl);
        Log.d(TAG, "onNeedRealNameAuthMeetingNotification getRealNameAuthPrivacyURL:" + ZoomSDK.getInstance().getSmsService().getRealNameAuthPrivacyURL());
        RealNameAuthDialog.show(this, handler);
    }

    @Override
    public void onRetrieveSMSVerificationCodeResultNotification(MobileRTCSMSVerificationError result, IZoomVerifySMSVerificationCodeHandler handler) {
        Log.d(TAG, "onRetrieveSMSVerificationCodeResultNotification:" + result);
    }

    @Override
    public void onVerifySMSVerificationCodeResultNotification(MobileRTCSMSVerificationError result) {
        Log.d(TAG, "onVerifySMSVerificationCodeResultNotification:" + result);
    }

    @Override
    public void onHelpRequestReceived(final String strUserID) {
        InMeetingBOController boController = mInMeetingService.getInMeetingBOController();
        final IBOAdmin iboAdmin = boController.getBOAdminHelper();
        if (iboAdmin != null) {
            String[] boAndUser = UIUtil.getBoNameUserNameByUserId(boController, strUserID);
            if (boAndUser == null || boAndUser.length != 2)
                return;
            new AlertDialog.Builder(MyMeetingActivity.this)
                    .setMessage(boAndUser[1] + " in " + boAndUser[0] + " asked for help.")
                    .setCancelable(false)
                    .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            iboAdmin.ignoreUserHelpRequest(strUserID);
                        }
                    })
                    .setPositiveButton("Join Breakout Room", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            iboAdmin.joinBOByUserRequest(strUserID);
                        }
                    }).create().show();
        }
    }

    private void unRegisterListener() {
        try {
            MeetingAudioCallback.getInstance().removeListener(this);
            MeetingVideoCallback.getInstance().removeListener(this);
            MeetingShareCallback.getInstance().removeListener(this);
            MeetingUserCallback.getInstance().removeListener(this);
            MeetingCommonCallback.getInstance().removeListener(this);
            BOEventCallback.getInstance().removeEvent(this);
            if(null!=smsService){
                smsService.removeListener(this);
            }
            ZoomSDK.getInstance().getInMeetingService().getInMeetingBOController().removeListener(mBOControllerListener);
        }catch (Exception e){
        }
    }


    private void registerListener() {
        smsService=ZoomSDK.getInstance().getSmsService();
        if(null!=smsService){
            smsService.addListener(this);
        }
        ZoomSDK.getInstance().getInMeetingService().getInMeetingBOController().addListener(mBOControllerListener);
        MeetingAudioCallback.getInstance().addListener(this);
        MeetingVideoCallback.getInstance().addListener(this);
        MeetingShareCallback.getInstance().addListener(this);
        MeetingUserCallback.getInstance().addListener(this);
        MeetingCommonCallback.getInstance().addListener(this);

        InMeetingInterpretationController meetingInterpretationController= ZoomSDK.getInstance().getInMeetingService().getInMeetingInterpretationController();
        meetingInterpretationController.setEvent(event);
    }

    private SimpleInMeetingBOControllerListener mBOControllerListener = new SimpleInMeetingBOControllerListener() {

        AlertDialog dialog;
        @Override
        public void onHasAttendeeRightsNotification(final IBOAttendee iboAttendee) {
            super.onHasAttendeeRightsNotification(iboAttendee);
            Log.d(TAG, "onHasAttendeeRightsNotification");
            iboAttendee.setEvent(iboAttendeeEvent);
            InMeetingBOController boController = mInMeetingService.getInMeetingBOController();
            if (boController.isInBOMeeting()) {
                mBtnJoinBo.setVisibility(View.GONE);
                mBtnRequestHelp.setVisibility(iboAttendee.isHostInThisBO() ? View.GONE : View.VISIBLE);
                meetingOptionBar.updateMeetingNumber(iboAttendee.getBoName());
            } else {
                mBtnRequestHelp.setVisibility(View.GONE);
                AlertDialog.Builder builder = new AlertDialog.Builder(MyMeetingActivity.this)
                        .setMessage("The host is inviting you to join Breakout Room: " + iboAttendee.getBoName())
                        .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mBtnJoinBo.setVisibility(View.VISIBLE);
                            }
                        })
                        .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                iboAttendee.joinBo();
                            }
                        })
                        .setCancelable(false);
                dialog = builder.create();
                dialog.show();
            }
        }

        @Override
        public void onHasDataHelperRightsNotification(IBOData iboData) {
            Log.d(TAG, "onHasDataHelperRightsNotification");
            iboData.setEvent(iboDataEvent);
        }

        @Override
        public void onLostAttendeeRightsNotification() {
            super.onLostAttendeeRightsNotification();
            Log.d(TAG, "onLostAttendeeRightsNotification");
            if (null != dialog && dialog.isShowing()) {
                dialog.dismiss();
            }
            mBtnJoinBo.setVisibility(View.GONE);
        }

        @Override
        public void onHasAdminRightsNotification(IBOAdmin iboAdmin) {
            super.onHasAdminRightsNotification(iboAdmin);
            Log.d(TAG, "onHasAdminRightsNotification");
            BOEventCallback.getInstance().addEvent(MyMeetingActivity.this);
        }
    };

    private IBODataEvent iboDataEvent = new IBODataEvent() {
        @Override
        public void onBOInfoUpdated(String strBOID) {
            InMeetingBOController boController = mInMeetingService.getInMeetingBOController();
            IBOData iboData = boController.getBODataHelper();
            if (iboData != null) {
                String boName = iboData.getCurrentBoName();
                if (!TextUtils.isEmpty(boName)) {
                    meetingOptionBar.updateMeetingNumber(boName);
                }
            }
        }

        @Override
        public void onUnAssignedUserUpdated() {

        }
    };

    private IBOAttendeeEvent iboAttendeeEvent = new IBOAttendeeEvent() {
        @Override
        public void onHelpRequestHandleResultReceived(ATTENDEE_REQUEST_FOR_HELP_RESULT eResult) {
            if(eResult == ATTENDEE_REQUEST_FOR_HELP_RESULT.RESULT_IGNORE) {
                new AlertDialog.Builder(MyMeetingActivity.this)
                        .setMessage("The host is currently helping others. Please try again later.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create().show();
            }
        }

        @Override
        public void onHostJoinedThisBOMeeting() {
            mBtnRequestHelp.setVisibility(View.GONE);
        }

        @Override
        public void onHostLeaveThisBOMeeting() {
            mBtnRequestHelp.setVisibility(View.VISIBLE);
        }
    };

    private void attendeeRequestHelp() {
        InMeetingBOController boController = mInMeetingService.getInMeetingBOController();
        final IBOAttendee boAttendee = boController.getBOAttendeeHelper();
        if (boAttendee != null) {
            new AlertDialog.Builder(this)
                    .setMessage("You can invite the host to this Breakout Room for assistance.")
                    .setCancelable(false)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setPositiveButton("Ask for Help", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            boAttendee.requestForHelp();
                        }
                    }).create().show();
        }
    }



    private IMeetingInterpretationControllerEvent event = new IMeetingInterpretationControllerEvent() {
        @Override
        public void onInterpretationStart() {

            Log.d(TAG, "onInterpretationStart:");
            updateLanguage();
        }

        @Override
        public void onInterpretationStop() {
            Log.d(TAG, "onInterpretationStop:");
            updateLanguage();
        }

        @Override
        public void onInterpreterListChanged() {
            Log.d(TAG, "onInterpreterListChanged:");
        }

        @Override
        public void onInterpreterRoleChanged(int userID, boolean isInterpreter) {
            Log.d(TAG, "onInterpreterRoleChanged:" + userID + ":" + isInterpreter);
            boolean isMyself = ZoomSDK.getInstance().getInMeetingService().isMyself(userID);
            if (isMyself) {
                if(isInterpreter){
                    Toast.makeText(getBaseContext(),R.string.zm_msg_interpreter_88102,Toast.LENGTH_SHORT).show();
                }
                updateLanguage();
            }
        }

        private void updateLanguage() {
            final InMeetingInterpretationController controller = ZoomSDK.getInstance().getInMeetingService().getInMeetingInterpretationController();
            if(controller.isInterpretationEnabled()&&controller.isInterpretationStarted()&&controller.isInterpreter()){
                layout_lans.setVisibility(View.VISIBLE);
            }else {
                layout_lans.setVisibility(View.GONE);
                return;
            }
            final TextView button1 = layout_lans.findViewById(R.id.btn_lan1);
            final TextView button2 = layout_lans.findViewById(R.id.btn_lan2);

            List<Integer> list = controller.getInterpreterLans();
            int lanId = controller.getInterpreterActiveLan();
            if (null != list && list.size() >= 2) {
                IInterpretationLanguage language1 = controller.getInterpretationLanguageByID(list.get(0));
                IInterpretationLanguage language2 = controller.getInterpretationLanguageByID(list.get(1));

                if (null != language1) {
                    button1.setText(language1.getLanguageName());
                }
                if (null != language2) {
                    button2.setText(language2.getLanguageName());
                }

                if (lanId == list.get(0)) {
                    button1.setSelected(true);
                    button2.setSelected(false);
                } else if(lanId==list.get(1)) {
                    button2.setSelected(true);
                    button1.setSelected(false);
                }else{
                    button2.setSelected(false);
                    button1.setSelected(false);
                }
            }

            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Integer> lans = controller.getInterpreterLans();
                    if (null != lans && lans.size() >= 2) {
                        controller.setInterpreterActiveLan(lans.get(0));
                    }
                    button2.setSelected(false);
                    button1.setSelected(true);
                }
            });



            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Integer> lans = controller.getInterpreterLans();
                    if (null != lans && lans.size() >= 2) {
                        controller.setInterpreterActiveLan(lans.get(1));
                    }
                    button1.setSelected(false);
                    button2.setSelected(true);
                }
            });
        }

        @Override
        public void onInterpreterActiveLanguageChanged(int userID, int activeLanID) {
            Log.d(TAG, "onInterpreterActiveLanguageChanged:" + userID + ":" + activeLanID);
        }

        @Override
        public void onInterpreterLanguageChanged(int lanID1, int lanID2) {
            Log.d(TAG, "onInterpreterLanguageChanged:" + lanID1 + ":" + lanID2);
            updateLanguage();
        }

        @Override
        public void onAvailableLanguageListUpdated(List<IInterpretationLanguage> pAvailableLanguageList) {
            Log.d(TAG, "onAvailableLanguageListUpdated:" + pAvailableLanguageList);
            updateLanguage();
        }
    };

}

