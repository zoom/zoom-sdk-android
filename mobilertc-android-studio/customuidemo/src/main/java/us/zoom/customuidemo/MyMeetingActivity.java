package us.zoom.customuidemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import us.zoom.customuidemo.Share.AnnotateToolbar;
import us.zoom.customuidemo.Share.CustomShareView;
import us.zoom.sdk.CameraDevice;
import us.zoom.sdk.InMeetingAudioController;
import us.zoom.sdk.InMeetingChatMessage;
import us.zoom.sdk.InMeetingEventHandler;
import us.zoom.sdk.InMeetingRemoteController;
import us.zoom.sdk.InMeetingService;
import us.zoom.sdk.InMeetingServiceListener;
import us.zoom.sdk.InMeetingShareController;
import us.zoom.sdk.InMeetingShareController.InMeetingShareListener;
import us.zoom.sdk.InMeetingUserInfo;
import us.zoom.sdk.InMeetingVideoController;
import us.zoom.sdk.InMeetingWaitingRoomController;
import us.zoom.sdk.InMeetingWebinarController;
import us.zoom.sdk.InMeetingWebinarController.InMeetingWebinarListener;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.MobileRTCRenderInfo;
import us.zoom.sdk.MobileRTCSDKError;
import us.zoom.sdk.MobileRTCShareView;
import us.zoom.sdk.MobileRTCVideoUnitAspectMode;
import us.zoom.sdk.MobileRTCVideoUnitRenderInfo;
import us.zoom.sdk.MobileRTCVideoView;
import us.zoom.sdk.MobileRTCVideoViewManager;
import us.zoom.sdk.ZoomSDK;

public class MyMeetingActivity extends FragmentActivity implements  MeetingServiceListener, InMeetingServiceListener,InMeetingRemoteController.InMeetingRemoteControlListener,InMeetingShareListener, InMeetingWebinarListener, View.OnClickListener {
	
    private final static String TAG = MyMeetingActivity.class.getSimpleName();

    public final static int REQUEST_PLIST = 1001;

    public final static int REQUEST_CAMERA_CODE = 1010;
    public final static int REQUEST_AUDIO_CODE = 1011;

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

    protected final static int REQUEST_SHARE_SCREEN_PERMISSION = 1001;
    protected final static int REQUEST_SYSTEM_ALERT_WINDOW = 1002;

    private View mBtnLeave;
    private View mBtnShare;
    private View mBtnCamera;
    private View mBtnAudio;
    private View mBtnSwitchCamera;
    private View mBtnSwitchLoudSpeaker;

    private ImageView mAudioStatusImg;
    private ImageView mVideoStatusImg;
    private ImageView mShareStatusImg;
    private ImageView mLoudSpeakerStatusImg;

    private View mMeetingMoreImg;

    private View mWaitJoinView;
    private View mWaitRoomView;
    private TextView mConnectingText;
    private View mMeetingBottomToolbar;
    private View mMeetingTopToolbar;

    private boolean mIsToolbarHide = false;
    private boolean mMeetingFailed = false;

    private long mCurShareUserId = -1;

    private MobileRTCVideoView mDefaultVideoView;
    private VideoListView mVideoListView;
    private MobileRTCVideoViewManager mDefaultVideoViewMgr;

    private MeetingService mMeetingService;
    private InMeetingService mInMeetingService;

    private InMeetingShareController mInMeetingShareController;
    private InMeetingVideoController mInMeetingVideoController;
    private InMeetingAudioController mInMeetingAudioController;
    private InMeetingRemoteController mInMeetingRemoteController;
    private InMeetingWebinarController mInMeetingWebinarController;
    private InMeetingWaitingRoomController mInMeetingWaitingRoomController;

    private Intent mScreenInfoData;

    private MobileRTCShareView mShareView;
    private AnnotateToolbar mDrawingView;
    private FrameLayout mMeetingVideoView;

    private FrameLayout mNormalSenceView;

    private View mPopupWindowLayout;

    private CustomShareView customShareView;

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

        mInMeetingShareController = mInMeetingService.getInMeetingShareController();
        mInMeetingVideoController = mInMeetingService.getInMeetingVideoController();
        mInMeetingAudioController = mInMeetingService.getInMeetingAudioController();
        mInMeetingRemoteController = mInMeetingService.getInMeetingRemoteController();
        mInMeetingWebinarController = mInMeetingService.getInMeetingWebinarController();
        mInMeetingWaitingRoomController = mInMeetingService.getInMeetingWaitingRoomController();

        setContentView(R.layout.my_meeting_layout);

        mMeetingVideoView = (FrameLayout) findViewById(R.id.meetingVideoView);
        mShareView = (MobileRTCShareView) findViewById(R.id.sharingView);
        mDrawingView = (AnnotateToolbar) findViewById(R.id.drawingView);

        mWaitJoinView = (View) findViewById(R.id.waitJoinView);
        mWaitRoomView = (View) findViewById(R.id.waitingRoom);

        LayoutInflater inflater = getLayoutInflater();

        mNormalSenceView = (FrameLayout)inflater.inflate(R.layout.layout_meeting_content_normal, null);
        mMeetingVideoView.addView(mNormalSenceView);
        mNormalSenceView.setOnClickListener(this);
        customShareView = (CustomShareView) mNormalSenceView.findViewById(R.id.custom_share_view);

        mBtnLeave = (View) findViewById(R.id.btnLeaveZoomMeeting);
        mBtnLeave.setOnClickListener(this);
        mBtnShare = (View) findViewById(R.id.btnShare);
        mBtnShare.setOnClickListener(this);

        mBtnCamera = (View) findViewById(R.id.btnCamera);
        mBtnCamera.setOnClickListener(this);
        mBtnAudio = (View) findViewById(R.id.btnAudio);
        mBtnAudio.setOnClickListener(this);
        mBtnSwitchLoudSpeaker = findViewById(R.id.btnSwitchLoudSpeaker);
        mBtnSwitchLoudSpeaker.setOnClickListener(this);

        mAudioStatusImg = (ImageView) findViewById(R.id.audioStatusImage);
        mVideoStatusImg = (ImageView) findViewById(R.id.videotatusImage);
        mShareStatusImg = (ImageView) findViewById(R.id.shareStatusImage);
        mLoudSpeakerStatusImg = (ImageView) findViewById(R.id.speakerStatusImage);

		mMeetingMoreImg = findViewById(R.id.moreActionImg);
		mMeetingMoreImg.setOnClickListener(this);

        mBtnSwitchCamera = (View)findViewById(R.id.btnSwitchCamera);
        mBtnSwitchCamera.setOnClickListener(this);

		mMeetingBottomToolbar = (View) findViewById(R.id.view_option_bottombar);
		mMeetingTopToolbar = (View) findViewById(R.id.view_option_topbar);
		mConnectingText = (TextView)findViewById(R.id.connectingTxt);

        mMeetingService.addListener(this);
		mInMeetingService.addListener(this);
		mInMeetingShareController.addListener(this);
		mInMeetingRemoteController.addListener(this);
        mInMeetingWebinarController.addListener(this);

        mDefaultVideoView = (MobileRTCVideoView) mNormalSenceView.findViewById(R.id.videoView);
        mDefaultVideoViewMgr = mDefaultVideoView.getVideoViewManager();

        mVideoListView = new VideoListView(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM;
        mVideoListView.setLayoutParams(lp);

        mPopupWindowLayout = getLayoutInflater().inflate(R.layout.popupwindow, null);

        refreshToolbar();
    }

    private void refreshToolbar() {
        if (mMeetingService.getMeetingStatus() == MeetingStatus.MEETING_STATUS_INMEETING) {
            if (mIsToolbarHide) {
                mMeetingBottomToolbar.setVisibility(View.GONE);
                mMeetingTopToolbar.setVisibility(View.GONE);
            } else {
                mMeetingBottomToolbar.setVisibility(View.VISIBLE);
                mMeetingTopToolbar.setVisibility(View.VISIBLE);
            }
            mConnectingText.setVisibility(View.GONE);
            mMeetingMoreImg.setVisibility(View.VISIBLE);
            updateAudioButton();
            updateVideoButton();
            updateShareButton();
            updateSwitchCameraButton();
            updateSwithLoudSpeakerButton();
        } else {
            if(mMeetingService.getMeetingStatus() == MeetingStatus.MEETING_STATUS_CONNECTING) {
                mConnectingText.setVisibility(View.VISIBLE);
            } else  {
                mConnectingText.setVisibility(View.GONE);
            }

            mMeetingBottomToolbar.setVisibility(View.GONE);
            mMeetingTopToolbar.setVisibility(View.GONE);
            mMeetingMoreImg.setVisibility(View.GONE);
        }

    }

	private void hideOrShowToolbar(boolean isHide) {
		mIsToolbarHide = isHide;
		refreshToolbar();
	}

	private void updateAudioButton() {
		if(mInMeetingAudioController.isAudioConnected()) {
			if(mInMeetingAudioController.isMyAudioMuted()) {
				mAudioStatusImg.setImageResource(R.drawable.icon_meeting_audio_mute);
			} else {
				mAudioStatusImg.setImageResource(R.drawable.icon_meeting_audio);
			}
		} else {
			mAudioStatusImg.setImageResource(R.drawable.icon_meeting_noaudio);
		}
	}

	private void updateVideoButton() {
		if(mInMeetingVideoController.isMyVideoMuted()) {
			mVideoStatusImg.setImageResource(R.drawable.icon_meeting_video_mute);
		} else {
			mVideoStatusImg.setImageResource(R.drawable.icon_meeting_video);
		}
	}

	private void updateSwitchCameraButton() {
		if(mInMeetingVideoController.isMyVideoMuted()) {
			mBtnSwitchCamera.setVisibility(View.GONE);
		} else {
			mBtnSwitchCamera.setVisibility(View.VISIBLE);
		}
	}

	private void updateSwithLoudSpeakerButton() {
		if(mInMeetingAudioController.canSwitchAudioOutput()) {
			if(mInMeetingAudioController.getLoudSpeakerStatus()) {
				mLoudSpeakerStatusImg.setImageResource(R.drawable.icon_speaker_on);
			} else {
				mLoudSpeakerStatusImg.setImageResource(R.drawable.icon_speaker_off);
			}
			mBtnSwitchLoudSpeaker.setVisibility(View.VISIBLE);
		} else {
			mBtnSwitchLoudSpeaker.setVisibility(View.GONE);
		}
	}

    private void updateShareButton() {
        if(isMySelfWebinarAttendee()) {
            mBtnShare.setVisibility(View.GONE);
        } else {
            mBtnShare.setVisibility(View.VISIBLE);
            if (mInMeetingShareController.isSharingOut()) {
                mShareStatusImg.setImageResource(R.drawable.icon_share_resume);
            } else {
                mShareStatusImg.setImageResource(R.drawable.icon_share_pause);
            }
        }
    }

    private void updateAnnotationBar() {
        if (mCurShareUserId > 0 && !isMySelfWebinarAttendee()) {
            if (mInMeetingShareController.isSenderSupportAnnotation(mCurShareUserId)) {
                if (mInMeetingService.isMyself(mCurShareUserId) && !mInMeetingShareController.isSharingScreen()) {
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
        int newLayoutType = getNewVideoMeetingLayout();

        if(currentLayoutType != newLayoutType) {
            removeOldLayout(currentLayoutType);
            currentLayoutType = newLayoutType;
            addNewLayout(newLayoutType);
        }

        updateAnnotationBar();
    }

    private void updateVideoView(List<Long> userList, int action) {
        if (currentLayoutType == LAYOUT_TYPE_LIST_VIDEO || currentLayoutType == LAYOUT_TYPE_VIEW_SHARE) {
            if(isVideoListViewVisible()) {
                updateAttendeeVideos(userList, action);
            }
        }
    }

    private int getNewVideoMeetingLayout() {
        int newLayoutType = -1;
        if(mMeetingService.getMeetingStatus() == MeetingStatus.MEETING_STATUS_WAITINGFORHOST) {
            newLayoutType = LAYOUT_TYPE_WAITHOST;
            return newLayoutType;
        }

        if(mMeetingService.getMeetingStatus() == MeetingStatus.MEETING_STATUS_IN_WAITING_ROOM) {
            newLayoutType = LAYOUT_TYPE_IN_WAIT_ROOM;
            return newLayoutType;
        }

        if (mInMeetingShareController.isOtherSharing()) {
            newLayoutType = LAYOUT_TYPE_VIEW_SHARE;
        } else if (mInMeetingShareController.isSharingOut() && !mInMeetingShareController.isSharingScreen()) {
            newLayoutType = LAYOUT_TYPE_SHARING_VIEW;
        } else {
            List<Long> userlist = mInMeetingService.getInMeetingUserList();
            int userCount = 0;
            if (userlist != null) {
                userCount = userlist.size();
            }

            if(userCount > 1) {
                int preCount = userCount;
                for(int i=0; i< preCount; i++) {
                    InMeetingUserInfo userInfo = mInMeetingService.getUserInfoById(userlist.get(i));
                    if(mInMeetingService.isWebinarMeeting()) {
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
            } else if (userCount == 2) {
                newLayoutType = LAYOUT_TYPE_ONETOONE;
            } else {
                newLayoutType = LAYOUT_TYPE_LIST_VIDEO;
            }
        }
        return newLayoutType;
    }

    private void removeOldLayout(int type) {
        if(type == LAYOUT_TYPE_WAITHOST) {
            mWaitJoinView.setVisibility(View.GONE);
            mMeetingVideoView.setVisibility(View.VISIBLE);
        } else if(type == LAYOUT_TYPE_IN_WAIT_ROOM) {
            mWaitRoomView.setVisibility(View.GONE);
            mMeetingVideoView.setVisibility(View.VISIBLE);
        }else if (type == LAYOUT_TYPE_PREVIEW || type == LAYOUT_TYPE_ONLY_MYSELF || type == LAYOUT_TYPE_ONETOONE) {
            mDefaultVideoViewMgr.removeAllVideoUnits();
        } else if (type == LAYOUT_TYPE_LIST_VIDEO || type == LAYOUT_TYPE_VIEW_SHARE) {
            mDefaultVideoViewMgr.removeAllVideoUnits();
            mDefaultVideoView.setGestureDetectorEnabled(false);
            mVideoListView.refreshUserList(null);
            mNormalSenceView.removeView(mVideoListView);
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
        if(type == LAYOUT_TYPE_WAITHOST) {
            mWaitJoinView.setVisibility(View.VISIBLE);
            refreshToolbar();
            mMeetingVideoView.setVisibility(View.GONE);
        } else if(type == LAYOUT_TYPE_IN_WAIT_ROOM) {
            mWaitRoomView.setVisibility(View.VISIBLE);
            refreshToolbar();
            mMeetingVideoView.setVisibility(View.GONE);
        } else if (type == LAYOUT_TYPE_PREVIEW) {
            showPreviewLayout();
        } else if (type == LAYOUT_TYPE_ONLY_MYSELF) {
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

    private boolean isVideoListViewVisible(){
        int count = mNormalSenceView.getChildCount();
        for(int i = 0; i < count; i ++){
            View view = mNormalSenceView.getChildAt(i);
            if(view == mVideoListView){
                return true;
            }
        }

        return false;
    }

    private void addVideoListView() {
        if(!isVideoListViewVisible()) {
            mNormalSenceView.addView(mVideoListView);
        }
    }

    private void showPreviewLayout() {
        MobileRTCVideoUnitRenderInfo renderInfo1 = new MobileRTCVideoUnitRenderInfo(0, 0, 100, 100);
        mDefaultVideoView.setVisibility(View.VISIBLE);
        mDefaultVideoViewMgr.addPreviewVideoUnit(renderInfo1);
    }

    private void showOnlyMeLayout() {
        mDefaultVideoView.setVisibility(View.VISIBLE);
        MobileRTCVideoUnitRenderInfo renderInfo = new MobileRTCVideoUnitRenderInfo(0, 0, 100, 100);
        InMeetingUserInfo myUserInfo = mInMeetingService.getMyUserInfo();
        if(myUserInfo != null) {
            if(mInMeetingService.isWebinarMeeting()&& myUserInfo.getInMeetingUserRole() == InMeetingUserInfo.InMeetingUserRole.USERROLE_ATTENDEE) {
                mDefaultVideoViewMgr.addActiveVideoUnit(renderInfo);
            } else {
                mDefaultVideoViewMgr.addAttendeeVideoUnit(myUserInfo.getUserId(), renderInfo);
            }
        }
    }


    private void showOne2OneLayout() {
        mDefaultVideoView.setVisibility(View.VISIBLE);

        MobileRTCVideoUnitRenderInfo renderInfo = new MobileRTCVideoUnitRenderInfo(0, 0, 100, 100);
        //options.aspect_mode = MobileRTCVideoUnitAspectMode.VIDEO_ASPECT_PAN_AND_SCAN;
        mDefaultVideoViewMgr.addActiveVideoUnit(renderInfo);

        MobileRTCVideoUnitRenderInfo renderInfo1 = new MobileRTCVideoUnitRenderInfo(73, 73, 25, 20);
        renderInfo1.aspect_mode = MobileRTCVideoUnitAspectMode.VIDEO_ASPECT_ORIGINAL;
        renderInfo1.is_border_visible = true;
        mDefaultVideoViewMgr.addAttendeeVideoUnit(mInMeetingService.getMyUserID(), renderInfo1);
    }

    private void showVideoListLayout() {
        MobileRTCVideoUnitRenderInfo renderInfo = new MobileRTCVideoUnitRenderInfo(0, 0, 100, 100);
        //options.aspect_mode = MobileRTCVideoUnitAspectMode.VIDEO_ASPECT_PAN_AND_SCAN;
        mDefaultVideoViewMgr.addActiveVideoUnit(renderInfo);
        addVideoListView();
        List<Long> userlist = mInMeetingService.getInMeetingUserList();
        updateAttendeeVideos(userlist, 0);
    }

    private void showSharingViewOutLayout() {
        mMeetingVideoView.setVisibility(View.GONE);
        mShareView.setVisibility(View.VISIBLE);
    }

    private void updateAttendeeVideos(List<Long> userList, int action) {
        if(action == 0) {
            mVideoListView.refreshUserList(userList);
        } else if(action == 1) {
            mVideoListView.addUserList(userList);
        } else {
            mVideoListView.removeUserList(userList);
        }
    }

    private void showViewShareLayout() {
        if (!isMySelfWebinarAttendee()) {
            mDefaultVideoView.setVisibility(View.VISIBLE);
            mDefaultVideoView.setGestureDetectorEnabled(true);
            long shareUserId = mInMeetingService.activeShareUserID();
            MobileRTCRenderInfo renderInfo1 = new MobileRTCRenderInfo(0, 0, 100, 100);
            mDefaultVideoViewMgr.addShareVideoUnit(shareUserId, renderInfo1);
            addVideoListView();
            List<Long> userlist = mInMeetingService.getInMeetingUserList();
            updateAttendeeVideos(userlist, 0);

            customShareView.setMobileRTCVideoView(mDefaultVideoView);
            long myUserId = mInMeetingService.getMyUserID();
            boolean hasPriv = mInMeetingRemoteController.hasRemoteControlPrivilegeWithUserId(myUserId);
            boolean isRc = mInMeetingRemoteController.isRemoteController();
            customShareView.enableRC(hasPriv, isRc);
        } else {
            mDefaultVideoView.setVisibility(View.VISIBLE);
            mDefaultVideoView.setGestureDetectorEnabled(true);
            long shareUserId = mInMeetingService.activeShareUserID();
            MobileRTCRenderInfo renderInfo1 = new MobileRTCRenderInfo(0, 0, 100, 100);
            mDefaultVideoViewMgr.addShareVideoUnit(shareUserId, renderInfo1);
        }
    }

    private boolean isMySelfWebinarAttendee() {
        InMeetingUserInfo myUserInfo = mInMeetingService.getMyUserInfo();
        if(myUserInfo != null && mInMeetingService.isWebinarMeeting()) {
            return myUserInfo.getInMeetingUserRole() == InMeetingUserInfo.InMeetingUserRole.USERROLE_ATTENDEE;
        }
        return false;
    }

    private boolean isMySelfWebinarHostCohost() {
        InMeetingUserInfo myUserInfo = mInMeetingService.getMyUserInfo();
        if(myUserInfo != null && mInMeetingService.isWebinarMeeting()) {
            return myUserInfo.getInMeetingUserRole() == InMeetingUserInfo.InMeetingUserRole.USERROLE_HOST
                    || myUserInfo.getInMeetingUserRole() == InMeetingUserInfo.InMeetingUserRole.USERROLE_COHOST;
        }
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        checkRotation();
    }

    private void checkRotation() {
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int displayRotation = display.getRotation();
        mInMeetingVideoController.rotateMyVideo(displayRotation);
    }

	@Override
	protected void onResume() {
		super.onResume();
        checkShowVideoLayout();
		checkRotation();
		mDefaultVideoView.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mDefaultVideoView.onPause();
	}

	@Override
	protected void onDestroy() {
        super.onDestroy();
        if(mMeetingService != null) {
            mMeetingService.removeListener(this);
        }
		if(mInMeetingService!= null)
			mInMeetingService.removeListener(this);
		if(mInMeetingShareController != null)
			mInMeetingShareController.removeListener(this);
		if(null!=mInMeetingRemoteController)
		{
			mInMeetingRemoteController.removeListener(this);
		}
        if(mInMeetingWebinarController != null) {
            mInMeetingWebinarController.removeListener(this);
        }
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if(id == R.id.btnLeaveZoomMeeting) {
			showLeaveMeetingDialog();
		} else if(id == R.id.btnShare) {
			if(!mInMeetingShareController.isSharingOut()) {
				showShareActionPopupWindow();
			} else {
				stopShare();
			}
		} else if(id == R.id.btnCamera) {
            onClickBtnCamera();
        } else if(id == R.id.btnAudio) {
            onClickBtnAudio();
        } else if(id == R.id.btnSwitchCamera) {
            onClickBtnSwitchCamera();
        } else if(id == R.id.moreActionImg) {
            showMoreMenuPopupWindow();
		} else if(id == R.id.btnSwitchLoudSpeaker) {
			onClickBtnSwitchLoudSpeaker();
        } else if (view == mNormalSenceView) {
			hideOrShowToolbar(!mIsToolbarHide);
		}
	}
	
	@Override
	public void onBackPressed() {
        if (mMeetingService.getMeetingStatus() == MeetingStatus.MEETING_STATUS_INMEETING) {
			showMainActivity();
		} else {
			showLeaveMeetingDialog();
		}
	}

    private void onClickBtnCamera() {
		if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);
			return;
		}

        if(mInMeetingVideoController.isMyVideoMuted()) {
            if(mInMeetingVideoController.canUnmuteMyVideo()) {
				mInMeetingVideoController.muteMyVideo(false);
            }
        } else {
			mInMeetingVideoController.muteMyVideo(true);
        }
    }

    private void onClickBtnAudio() {
		if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_CODE);
			return;
		}
        if(mInMeetingAudioController.isAudioConnected()) {
            if(mInMeetingAudioController.isMyAudioMuted()) {
                if(mInMeetingAudioController.canUnmuteMyAudio()) {
                    mInMeetingAudioController.muteMyAudio(false);
                }
            } else {
                mInMeetingAudioController.muteMyAudio(true);
            }
        } else {
            mInMeetingAudioController.connectAudioWithVoIP();
        }
    }

    private void onClickBtnSwitchCamera() {
		if(mInMeetingVideoController.canSwitchCamera()) {
			List<CameraDevice> devices = mInMeetingVideoController.getCameraDeviceList();
			if (devices != null && devices.size() > 1) {
				final SimpleMenuAdapter cameraMenuAdapter = new SimpleMenuAdapter(this);
				for(CameraDevice device :devices) {
					cameraMenuAdapter.addItem(new CameraMenuItem(0, device.getDeviceName(), device.getDeviceId()));
				}

				ListView cameraList = (ListView) mPopupWindowLayout.findViewById(R.id.actionListView);
				final PopupWindow window = new PopupWindow(mPopupWindowLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                cameraList.setAdapter(cameraMenuAdapter);

                cameraList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

						CameraMenuItem item = (CameraMenuItem)cameraMenuAdapter.getItem(position);
						mInMeetingVideoController.switchCamera(item.getCameraId());
						window.dismiss();
					}
				});

				window.setFocusable(true);
				window.setOutsideTouchable(true);
				window.update();
				window.showAsDropDown(mBtnShare, 0, 20);
			} else {
				mInMeetingVideoController.switchToNextCamera();
			}
		}
    }

    private void onClickBtnSwitchLoudSpeaker() {
		if(mInMeetingAudioController.canSwitchAudioOutput()) {
            mInMeetingAudioController.setLoudSpeakerStatus(!mInMeetingAudioController.getLoudSpeakerStatus());
		}
	}

	private void startShareWebUrl() {
		boolean success = (mInMeetingShareController.startShareViewSession() == MobileRTCSDKError.SDKERR_SUCCESS);
		if (!success) {
				Log.i(TAG, "startShare is failed");
			return;
		} 	
		mShareView.setVisibility(View.VISIBLE);
		mShareView.setShareWebview("www.zoom.us");
		hideOrShowToolbar(true);
	}

	private void startShareImage() {
		boolean success = (mInMeetingShareController.startShareViewSession()== MobileRTCSDKError.SDKERR_SUCCESS);
		if (!success) {
			Log.i(TAG, "startShare is failed");
			return;
		}
		mShareView.setVisibility(View.VISIBLE);
		mShareView.setShareImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.zoom_intro1_share));
		hideOrShowToolbar(true);
	}

	private void startShareWhiteBoard() {
		boolean success = (mInMeetingShareController.startShareViewSession() == MobileRTCSDKError.SDKERR_SUCCESS);
		if (!success) {
			Log.i(TAG, "startShare is failed");
			return;
		}
		mShareView.setVisibility(View.VISIBLE);
		mShareView.setShareWhiteboard();
		hideOrShowToolbar(true);
	}
	
	private void stopShare(){
		if(mInMeetingShareController.isSharingScreen()) {
			mInMeetingShareController.stopShareScreen();
		}
		
		if(mShareView != null) {
			mInMeetingShareController.stopShareView();
			mShareView.setVisibility(View.GONE);
		}
	}

    private void showMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    public void onMeetingNeedPasswordOrDisplayName(boolean b, boolean b1, InMeetingEventHandler inMeetingEventHandler) {

	}

	@Override
	public void onWebinarNeedRegister() {
		showWebinarNeedRegisterDialog();
	}

	@Override
	public void onJoinWebinarNeedUserNameAndEmail(InMeetingEventHandler inMeetingEventHandler) {
		inMeetingEventHandler.setRegisterWebinarInfo("test", "test@example.com", false);
	}

	@Override
	public void onMeetingNeedColseOtherMeeting(InMeetingEventHandler inMeetingEventHandler) {
		showEndOtherMeetingDialog(inMeetingEventHandler);
	}

	@Override
	public void onMeetingFail(int error, int interError) {
		mMeetingFailed = true;
        mMeetingVideoView.removeAllViews();
		mMeetingVideoView.setVisibility(View.GONE);
		mConnectingText.setVisibility(View.GONE);
		showJoinFailDialog(error);
	}

	@Override
	public void onMeetingLeaveComplete(long ret) {

		stopShare(); // stop share to stop share thread
		if(!mMeetingFailed)
			finish();
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
    public void onMeetingUserUpdated(long userId) {
    }

	@Override
	public void onMeetingCoHostChanged(long l) {

	}

	@Override
	public void onMeetingHostChanged(long userId) {

	}

	@Override
	public void onSpotlightVideoChanged(boolean b) {

	}

	@Override
	public void onUserVideoStatusChanged(long userId) {
		updateVideoButton();
		updateSwitchCameraButton();
	}

	@Override
	public void onUserAudioStatusChanged(long userId) {
		if(mInMeetingService.isMyself(userId)) {
			updateAudioButton();
			updateSwithLoudSpeakerButton();
		}
	}

	@Override
	public void onUserAudioTypeChanged(long userId) {
		if(mInMeetingService.isMyself(userId)) {
			updateAudioButton();
			updateSwithLoudSpeakerButton();
		}
	}

	@Override
	public void onMyAudioSourceTypeChanged(int type) {
		updateSwithLoudSpeakerButton();
	}

	@Override
	public void onMicrophoneStatusError(InMeetingAudioController.MobileRTCMicrophoneError error) {
	}

	@Override
	public void onShareActiveUser(long userId) {

		Log.i(TAG, "onShareActiveUser: " + userId);

		if(mCurShareUserId > 0) {
			if(mInMeetingService.isMyself(mCurShareUserId)) {
				if (userId < 0) { //My share stopped

				} else if (!mInMeetingService.isMyself(userId)) { //other start share and stop my share
					mInMeetingShareController.stopShareView();
				}
			}
		} else if(mInMeetingService.isMyself(userId)) {
			if(mInMeetingShareController.isSharingOut()) {
				if (mInMeetingShareController.isSharingScreen()) {
					mInMeetingShareController.startShareScreenContent();
				} else {
					mInMeetingShareController.startShareViewContent(mShareView);
				}
			}
		}

		mCurShareUserId = userId;

		updateShareButton();
		checkShowVideoLayout();
	}

	@Override
	public void onShareUserReceivingStatus(long userId) {

	}

	@Override
	public void onLowOrRaiseHandStatusChanged(long userId, boolean b) {

	}

	@Override
	public void onMeetingSecureKeyNotification(byte[] bytes) {

	}

	@Override
	public void onChatMessageReceived(InMeetingChatMessage inMeetingChatMessage) {
        Toast.makeText(this, inMeetingChatMessage.getSenderDisplayName() + ": " + inMeetingChatMessage.getContent(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_SHARE_SCREEN_PERMISSION:
                if (resultCode != RESULT_OK) {
                    if (us.zoom.videomeetings.BuildConfig.DEBUG)
                        Log.d(TAG, "onActivityResult REQUEST_SHARE_SCREEN_PERMISSION no ok ");
                    break;
                }
                startShareScreen(data);
                break;
            case REQUEST_SYSTEM_ALERT_WINDOW:
                mInMeetingShareController.startShareScreenSession(mScreenInfoData);
                break;
        }
    }

    private void showLeaveMeetingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (mInMeetingService.isMeetingConnected()) {
            if (mInMeetingService.isMeetingHost()) {
                builder.setTitle("End or leave meeting")
                        .setPositiveButton("End", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                mInMeetingService.leaveCurrentMeeting(true);
                            }
                        }).setNeutralButton("Leave", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        mInMeetingService.leaveCurrentMeeting(false);
                    }
                });
            } else {
                builder.setTitle("Leave meeting")
                        .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                mInMeetingService.leaveCurrentMeeting(false);
                            }
                        });
            }
        } else {
            builder.setTitle("Leave meeting")
                    .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            mInMeetingService.leaveCurrentMeeting(true);
                        }
                    });
        }
        builder.setNegativeButton("Cancel", null).create();
        builder.create().show();
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

    private void showWebinarNeedRegisterDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Need register to join this webinar meeting ")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mInMeetingService.leaveCurrentMeeting(true);
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
    protected void askScreenSharePermission() {
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }

        MediaProjectionManager mgr = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mgr != null) {
            Intent intent = mgr.createScreenCaptureIntent();
            if (AndroidAppUtil.hasActivityForIntent(this, intent)) {
                try {
                    startActivityForResult(mgr.createScreenCaptureIntent(), REQUEST_SHARE_SCREEN_PERMISSION);
                } catch (Exception e) {
                    Log.e(TAG, "askScreenSharePermission failed");
                }
            }
        }
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
            mInMeetingShareController.startShareScreenSession(data);
        }
    }

    private final int MENU_SHARE_SCREEN = 0;
    private final int MENU_SHARE_IMAGE = 1;
    private final int MENU_SHARE_WEBVIEW = 2;
    private final int MENU_WHITE_BOARD = 3;

    private void showShareActionPopupWindow() {
        final SimpleMenuAdapter menuAdapter = new SimpleMenuAdapter(this);

        if (Build.VERSION.SDK_INT >= 21) {
            menuAdapter.addItem(new SimpleMenuItem(MENU_SHARE_SCREEN, "Screen"));
        }
        menuAdapter.addItem(new SimpleMenuItem(MENU_SHARE_IMAGE, "Image"));
        menuAdapter.addItem(new SimpleMenuItem(MENU_SHARE_WEBVIEW, "Web url"));
        menuAdapter.addItem(new SimpleMenuItem(MENU_WHITE_BOARD, "WhiteBoard"));

        ListView shareActions = (ListView) mPopupWindowLayout.findViewById(R.id.actionListView);
        final PopupWindow window = new PopupWindow(mPopupWindowLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        shareActions.setAdapter(menuAdapter);

        shareActions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SimpleMenuItem item = (SimpleMenuItem) menuAdapter.getItem(position);
                if (item.getAction() == MENU_SHARE_WEBVIEW) {
                    startShareWebUrl();
                } else if (item.getAction() == MENU_SHARE_IMAGE) {
                    startShareImage();
                } else if (item.getAction() == MENU_SHARE_SCREEN) {
                    askScreenSharePermission();
                } else if (item.getAction() == MENU_WHITE_BOARD) {
                    startShareWhiteBoard();
                }
                window.dismiss();
            }
        });

        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.update();
        window.showAsDropDown(mBtnShare, 0, 20);
    }

    private final int MENU_DISCONNECT_AUDIO = 0;
    private final int MENU_SHOW_TOOLBAR = 1;
    private final int MENU_HIDE_TOOLBAR = 2;
    private final int MENU_STOP_SHARE = 3;
    private final int MENU_SHOW_PLIST = 4;

    //webinar host&cohost
    private final int MENU_AllOW_PANELIST_START_VIDEO = 5;
    private final int MENU_AllOW_ATTENDEE_CHAT  = 6;

    private final int MENU_DISALLOW_PANELIST_START_VIDEO = 7;
    private final int MENU_DISALLOW_ATTENDEE_CHAT = 8;


    private void showMoreMenuPopupWindow() {
        final SimpleMenuAdapter menuAdapter = new SimpleMenuAdapter(this);
        if (mInMeetingAudioController.isAudioConnected()) {
            menuAdapter.addItem(new SimpleMenuItem(MENU_DISCONNECT_AUDIO, "Disconnect Audio"));
        }
        if (mInMeetingShareController.isSharingOut()) {
            menuAdapter.addItem(new SimpleMenuItem(MENU_STOP_SHARE, "Stop Share"));
        }

        if (mIsToolbarHide) {
            menuAdapter.addItem(new SimpleMenuItem(MENU_SHOW_TOOLBAR, "Show Toolbar"));
        } else {
            menuAdapter.addItem(new SimpleMenuItem(MENU_HIDE_TOOLBAR, "Hide Toolbar"));
        }

        if(!isMySelfWebinarAttendee())
            menuAdapter.addItem((new SimpleMenuItem(MENU_SHOW_PLIST, "Paticipants")));

        if(isMySelfWebinarHostCohost()) {
            if(mInMeetingWebinarController.isAllowPanellistStartVideo()) {
                menuAdapter.addItem((new SimpleMenuItem(MENU_DISALLOW_PANELIST_START_VIDEO, "Disallow panelist start video")));
            } else {
                menuAdapter.addItem((new SimpleMenuItem(MENU_AllOW_PANELIST_START_VIDEO, "Allow panelist start video")));
            }

            if(mInMeetingWebinarController.isAllowAttendeeChat()) {
                menuAdapter.addItem((new SimpleMenuItem(MENU_DISALLOW_ATTENDEE_CHAT, "Disallow attendee chat")));
            } else {
                menuAdapter.addItem((new SimpleMenuItem(MENU_AllOW_ATTENDEE_CHAT, "Allow attendee chat")));
            }

        }

        ListView shareActions = (ListView) mPopupWindowLayout.findViewById(R.id.actionListView);
        final PopupWindow window = new PopupWindow(mPopupWindowLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        shareActions.setAdapter(menuAdapter);
        shareActions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SimpleMenuItem item = (SimpleMenuItem) menuAdapter.getItem(position);

                switch (item.getAction()) {
                    case MENU_DISCONNECT_AUDIO:
                        mInMeetingAudioController.disconnectAudio();
                        break;
                    case MENU_SHOW_TOOLBAR:
                    case MENU_HIDE_TOOLBAR:
                        hideOrShowToolbar(!mIsToolbarHide);
                        break;
                    case MENU_STOP_SHARE:
                        stopShare();
                        break;
                    case MENU_SHOW_PLIST:
                        mInMeetingService.showZoomParticipantsUI(MyMeetingActivity.this, REQUEST_PLIST);
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
                }

                window.dismiss();
            }
        });

        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.update();
        window.showAsDropDown(mMeetingMoreImg, 0, 20);
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
                    onClickBtnAudio();
                }
            } else if (Manifest.permission.CAMERA.equals(permissions[i])) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    onClickBtnCamera();
                }
            }
        }
    }

    @Override
    public void onPromptAttendee2PanelistResult(long userId) {
        InMeetingUserInfo userInfo = mInMeetingService.getUserInfoById(userId);
        if(userInfo != null)
            Toast.makeText(this, userInfo.getUserName() +" be prompted to panelist", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDepromptPanelist2AttendeeResult(long userId) {
        InMeetingUserInfo userInfo = mInMeetingService.getUserInfoById(userId);
        if(userInfo != null)
            Toast.makeText(this, userInfo.getUserName() +" be deprompted to attendee", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSelfAllowTalkNotification() {
        Toast.makeText(this, "Allow to talk", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSelfDisallowTalkNotification() {
        Toast.makeText(this, "Disallow to talk", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAllowPanelistStartVideoNotification() {
        Toast.makeText(this, "Allow Panelist to start video", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisallowPanelistStartVideoNotification() {
        Toast.makeText(this, "Disallow Panelist to start video", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAllowAttendeeChatResult() {
        Toast.makeText(this, "Allow attendee to chat", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisallowAttendeeChatResult() {
        Toast.makeText(this, "Disallow attendee to chat", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttendeeAudioStatusNotification(long userId, boolean b, boolean b1) {
        Toast.makeText(this, "AttendeeAudioStatus: "+ userId + ", can talk="+ b + ", isunmuted=" + b1, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMeetingStatusChanged(MeetingStatus meetingStatus, int i, int i1) {
        checkShowVideoLayout();
        refreshToolbar();
    }
}

