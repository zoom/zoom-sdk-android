package us.zoom.customuidemo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import us.zoom.sdk.InMeetingChatMessage;
import us.zoom.sdk.InMeetingEventHandler;
import us.zoom.sdk.InMeetingService;
import us.zoom.sdk.InMeetingServiceListener;
import us.zoom.sdk.InMeetingShareController;
import us.zoom.sdk.InMeetingShareController.InMeetingShareListener;
import us.zoom.sdk.InMeetingVideoController;
import us.zoom.sdk.InMeetingAudioController;
import us.zoom.sdk.InMeetingUserInfo;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.MobileRTCRenderInfo;
import us.zoom.sdk.MobileRTCShareView;
import us.zoom.sdk.MobileRTCVideoUnitAspectMode;
import us.zoom.sdk.MobileRTCVideoUnitRenderInfo;
import us.zoom.sdk.MobileRTCVideoView;
import us.zoom.sdk.MobileRTCVideoViewManager;
import us.zoom.sdk.ZoomSDK;

import static android.content.Context.WINDOW_SERVICE;

public class MeetingContentFragment extends Fragment implements MeetingServiceListener, InMeetingServiceListener, InMeetingShareListener, View.OnClickListener {

	private final static String TAG = MeetingContentFragment.class.getSimpleName();

	public final static int REQUEST_CAMERA_CODE = 0;
	public final static int REQUEST_AUDIO_CODE = 1;

	private int currentLayoutType = -1;

	private final int LAYOUT_TYPE_PREVIEW = 0;
	private final int LAYOUT_TYPE_ONLY_MYSELF = 2;
	private final int LAYOUT_TYPE_ONETOONE = 3;
	private final int LAYOUT_TYPE_LIST_VIDEO = 4;
	private final int LAYOUT_TYPE_VIEW_SHARE = 5;
	private final int LAYOUT_TYPE_SHARING_VIEW = 6;

	protected final static int REQUEST_SHARE_SCREEN_PERMISSION = 1001;
    protected final static int REQUEST_SYSTEM_ALERT_WINDOW = 1002;

	private View mBtnLeave;
    private View mBtnCamera;
	private View mBtnAudio;
	private View mMeetingBottomToolbar;

    private ImageView mAudioStatusImg;
	private ImageView mVideoStatusImg;

	private View mWaitJoinView;
	private TextView mConnectingText;

	private MobileRTCVideoViewManager mVideoViewMgr;
	private InMeetingService mInMeetingService;
	private InMeetingShareController mInMeetingShareController;
	private InMeetingVideoController mInMeetingVideoController;
	private InMeetingAudioController mInMeetingAudioController;
	private MobileRTCShareView mShareView;
	private MobileRTCVideoView mMeetingVideoView;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.in_meeting_fragment, null);

		mInMeetingService = ZoomSDK.getInstance().getInMeetingService();
		if(mInMeetingService == null)
			return null;

		mInMeetingShareController = mInMeetingService.getInMeetingShareController();
		mInMeetingVideoController = mInMeetingService.getInMeetingVideoController();
		mInMeetingAudioController = mInMeetingService.getInMeetingAudioController();

		mMeetingVideoView = (MobileRTCVideoView) view.findViewById(R.id.videoView);
		mShareView = (MobileRTCShareView)view.findViewById(R.id.sharingView);
		mWaitJoinView = view.findViewById(R.id.waitJoinView);
		mConnectingText = (TextView)view.findViewById(R.id.connectingTxt);
		mMeetingBottomToolbar = view.findViewById(R.id.view_option_bottombar);

		mBtnLeave = view.findViewById(R.id.btnLeaveZoomMeeting);
		mBtnLeave.setOnClickListener(this);
		mBtnCamera = view.findViewById(R.id.btnCamera);
		mBtnCamera.setOnClickListener(this);
		mBtnAudio = view.findViewById(R.id.btnAudio);
		mBtnAudio.setOnClickListener(this);

		mAudioStatusImg = (ImageView)view.findViewById(R.id.audioStatusImage);
		mVideoStatusImg = (ImageView)view.findViewById(R.id.videotatusImage);

		mInMeetingService.addListener(this);
		mInMeetingShareController.addListener(this);
		refreshToolbar();
		return view;
	}

	private void refreshToolbar() {
		if(mInMeetingService.isMeetingConnected()) {
			mMeetingBottomToolbar.setVisibility(View.VISIBLE);
			mConnectingText.setVisibility(View.GONE);
			updateAudioButton();
			updateVideoButton();
		} else {
			mConnectingText.setVisibility(View.VISIBLE);
			mMeetingBottomToolbar.setVisibility(View.GONE);
		}

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

	private void checkShowVideoLayout() {
		mVideoViewMgr = mMeetingVideoView.getVideoViewManager();
		if (mVideoViewMgr != null) {
			int newLayoutType = -1;
			if (mInMeetingService.isMeetingConnected()) {
				if (mInMeetingShareController.isOtherSharing()) {
					newLayoutType = LAYOUT_TYPE_VIEW_SHARE;
				} else if (mInMeetingShareController.isSharingOut() && !mInMeetingShareController.isSharingScreen()) {
					newLayoutType = LAYOUT_TYPE_SHARING_VIEW;
				} else {
					int userCount = mInMeetingService.getInMeetingUserCount();
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
			} else {
				newLayoutType = LAYOUT_TYPE_PREVIEW;
			}
			removeOldLayout(currentLayoutType);
			currentLayoutType = newLayoutType;
			addNewLayout(newLayoutType);
		}

	}

	private void removeOldLayout(int type) {
		if(type == LAYOUT_TYPE_PREVIEW || type == LAYOUT_TYPE_ONLY_MYSELF || type == LAYOUT_TYPE_ONETOONE) {
			mVideoViewMgr.removeAllVideoUnits();;
		} else if(type == LAYOUT_TYPE_LIST_VIDEO || type == LAYOUT_TYPE_VIEW_SHARE) {
			mVideoViewMgr.removeAllVideoUnits();
		} else if(type == LAYOUT_TYPE_SHARING_VIEW) {
			mMeetingVideoView.setVisibility(View.VISIBLE);
			mShareView.setVisibility(View.GONE);
		}
	}

	private void addNewLayout(int type) {
		if (type == LAYOUT_TYPE_PREVIEW) {
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

	private void showPreviewLayout() {
		MobileRTCVideoUnitRenderInfo renderInfo1 = new MobileRTCVideoUnitRenderInfo(0, 0, 100, 100);
		mVideoViewMgr.addPreviewVideoUnit(renderInfo1);
	}

	private void showOnlyMeLayout(){
		MobileRTCVideoUnitRenderInfo renderInfo = new MobileRTCVideoUnitRenderInfo(0, 0, 100, 100);
		mVideoViewMgr.addAttendeeVideoUnit(mInMeetingService.getMyUserID(), renderInfo);
	}

	private void showOne2OneLayout(){

		MobileRTCVideoUnitRenderInfo renderInfo = new MobileRTCVideoUnitRenderInfo(0, 0, 100, 100);
		//options.aspect_mode = MobileRTCVideoUnitAspectMode.VIDEO_ASPECT_PAN_AND_SCAN;
		mVideoViewMgr.addActiveVideoUnit(renderInfo);

		MobileRTCVideoUnitRenderInfo renderInfo1 = new MobileRTCVideoUnitRenderInfo(73, 73, 25, 20);
		renderInfo1.aspect_mode = MobileRTCVideoUnitAspectMode.VIDEO_ASPECT_ORIGINAL;
		renderInfo1.is_border_visible =true;
		mVideoViewMgr.addAttendeeVideoUnit(mInMeetingService.getMyUserID(), renderInfo1);
	}

	//2*2 video unit renderInfo
	final int COL = 2;
	final int ROW = 2;
	private void showVideoListLayout() {
		mVideoViewMgr.removeAllVideoUnits();

		List<Long> userlist = mInMeetingService.getInMeetingUserList();
		if(userlist != null) {
			for (int i = 0; i < userlist.size(); i++) {
				if (i < 4) {
					int row = i / COL;
					int col = i % ROW;
					MobileRTCVideoUnitRenderInfo renderInfo1 = new MobileRTCVideoUnitRenderInfo(50 * col, 50 * row, 50, 50);
					renderInfo1.is_border_visible = true;
					mVideoViewMgr.addAttendeeVideoUnit(userlist.get(i), renderInfo1);
				}
			}
		}
	}

	private void showSharingViewOutLayout() {
		mMeetingVideoView.setVisibility(View.GONE);
		mShareView.setVisibility(View.VISIBLE);
	}

	private void showViewShareLayout() {
		long shareUserId = mInMeetingService.activeShareUserID();
		MobileRTCRenderInfo renderInfo1 = new MobileRTCRenderInfo(0, 0, 100, 100);
		mVideoViewMgr.addShareVideoUnit(shareUserId,renderInfo1);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		checkRotation();
	}

	private void checkRotation() {
		Display display = ((WindowManager) getActivity().getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		int displayRotation = display.getRotation();
		mInMeetingVideoController.rotateMyVideo(displayRotation);
	}

	@Override
	public void onResume() {
		super.onResume();
        checkShowVideoLayout();
		checkRotation();
		mMeetingVideoView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mMeetingVideoView.onPause();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mInMeetingService.removeListener(this);
		mInMeetingShareController.removeListener(this);
	}

	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		if(id == R.id.btnLeaveZoomMeeting) {
			showLeaveMeetingDialog();
		} else if(id == R.id.btnCamera) {
            onClickBtnCamera();
        } else if(id == R.id.btnAudio) {
            onClickBtnAudio();
        }
	}


    private void onClickBtnCamera() {
		if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);
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
		if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_CODE);
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

	@Override
	public void onMeetingNeedPasswordOrDisplayName(boolean b, boolean b1, InMeetingEventHandler inMeetingEventHandler) {

	}

	@Override
	public void onWebinarNeedRegister() {

	}

	@Override
	public void onJoinWebinarNeedUserNameAndEmail(InMeetingEventHandler inMeetingEventHandler) {

	}

	@Override
	public void onMeetingNeedColseOtherMeeting(InMeetingEventHandler inMeetingEventHandler) {

	}

	@Override
	public void onMeetingFail(int error, int interError) {
		showJoinFailDialog(error);
	}

	@Override
	public void onMeetingLeaveComplete(long ret) {
	}

	@Override
	public void onMeetingUserJoin(List<Long> userList) {
		checkShowVideoLayout();
	}

	@Override
	public void onMeetingUserLeave(List<Long> userList) {
		checkShowVideoLayout();
	}

	@Override
	public void onMeetingUserUpdated(long userId) {
		checkShowVideoLayout();
	}

	@Override
	public void onMeetingHostChanged(long userId) {

	}

	@Override
	public void onMeetingCoHostChanged(long l) {

	}

	@Override
	public void onSpotlightVideoChanged(boolean b) {

	}

	@Override
	public void onUserVideoStatusChanged(long userId) {
		updateVideoButton();
	}

	@Override
	public void onUserAudioStatusChanged(long userId) {
		updateAudioButton();
	}

	@Override
	public void onUserAudioTypeChanged(long userId) {
		updateAudioButton();
	}

	@Override
	public void onMyAudioSourceTypeChanged(int i) {

	}

	@Override
	public void onMicrophoneStatusError(InMeetingAudioController.MobileRTCMicrophoneError error) {
	}

	@Override
	public void onShareActiveUser(long userId) {

		Log.i(TAG, "onShareActiveUser: " + userId);

		if(mInMeetingService.isMyself(userId)) {
			if(mInMeetingShareController.isSharingOut()) {
				if (mInMeetingShareController.isSharingScreen()) {
					mInMeetingShareController.startShareScreenContent();
				} else {
					mInMeetingShareController.startShareViewContent(mShareView);
				}
			}
		} else {
		}

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

	}

    private void showLeaveMeetingDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		if(mInMeetingService.isMeetingConnected()) {
			if(mInMeetingService.isMeetingHost()) {
				builder.setTitle("End or leave meeting")
				.setPositiveButton("End", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mInMeetingService.leaveCurrentMeeting(true);
					}
				}).setNeutralButton("Leave", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mInMeetingService.leaveCurrentMeeting(false);
					}
				});
			} else {
				builder.setTitle("Leave meeting")
				.setPositiveButton("Leave", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mInMeetingService.leaveCurrentMeeting(false);
					}
				});
			}
		} else {
			builder.setTitle("Leave meeting")
				.setPositiveButton("Leave", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mInMeetingService.leaveCurrentMeeting(true);
					}
				});
		}
		builder.setNegativeButton("Cancel", null).create();
		builder.create().show();
	}

	private void showJoinFailDialog(int error) {
		AlertDialog dialog = new AlertDialog.Builder(getActivity())
				.setCancelable(false)
				.setTitle("Meeting Fail")
				.setMessage("Error:" + error)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mInMeetingService.leaveCurrentMeeting(true);
					}
				}).create();
		dialog.show();
	}

	public int checkSelfPermission(String permission){
		if(permission == null || permission.length() == 0){
			return PackageManager.PERMISSION_DENIED;
		}
		try {
			return getActivity().checkPermission(permission, android.os.Process.myPid(), android.os.Process.myUid());
		}catch (Throwable e){
			return PackageManager.PERMISSION_DENIED;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if(permissions == null || grantResults == null){
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
	public void onMeetingStatusChanged(MeetingStatus meetingStatus, int i, int i1) {
		if(meetingStatus == MeetingStatus.MEETING_STATUS_INMEETING) {
			refreshToolbar();
			checkShowVideoLayout();
		}
	}
}

