package us.zoom.customuidemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import us.zoom.customuidemo.Share.AnnotateToolbar;
import us.zoom.customuidemo.Share.CustomShareView;
import us.zoom.sdk.CameraDevice;
import us.zoom.sdk.InMeetingChatMessage;
import us.zoom.sdk.InMeetingEventHandler;
import us.zoom.sdk.InMeetingRemoteController;
import us.zoom.sdk.InMeetingService;
import us.zoom.sdk.InMeetingServiceListener;
import us.zoom.sdk.InMeetingShareController;
import us.zoom.sdk.InMeetingShareController.InMeetingShareListener;
import us.zoom.sdk.InMeetingVideoController;
import us.zoom.sdk.InMeetingAudioController;
import us.zoom.sdk.InMeetingUserInfo;
import us.zoom.sdk.InMeetingUserList;
import us.zoom.sdk.MobileRTCRenderInfo;
import us.zoom.sdk.MobileRTCSDKError;
import us.zoom.sdk.MobileRTCShareView;
import us.zoom.sdk.MobileRTCVideoUnitAspectMode;
import us.zoom.sdk.MobileRTCVideoUnitRenderInfo;
import us.zoom.sdk.MobileRTCVideoView;
import us.zoom.sdk.MobileRTCVideoViewManager;
import us.zoom.sdk.ZoomSDK;

public class MyMeetingActivity extends FragmentActivity implements InMeetingServiceListener,InMeetingRemoteController.InMeetingRemoteControlListener,InMeetingShareListener,View.OnClickListener {
	
	private final static String TAG = MyMeetingActivity.class.getSimpleName();

	public final static int REQUEST_PLIST     = 1001;

	public final static int REQUEST_CAMERA_CODE = 1010;
	public final static int REQUEST_AUDIO_CODE = 1011;

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
	private TextView mConnectingText;
	private View mMeetingBottomToolbar;
	private View mMeetingTopToolbar;

	private boolean mIsToolbarHide = false;
	private boolean mMeetingFailed = false;

	private long mCurShareUserId = -1;

	private MobileRTCVideoView mDefaultVideoView;
	private MobileRTCVideoView mDefaultVideoView1;
	private MobileRTCVideoView mGalleryVideoView;
	private MobileRTCVideoViewManager mDefaultVideoViewMgr;
	private MobileRTCVideoViewManager mDefaultVideoViewMgr1;
	private MobileRTCVideoViewManager mGalleryVideoViewMgr;

	private InMeetingService mInMeetingService;

	private InMeetingShareController mInMeetingShareController;
    private InMeetingVideoController mInMeetingVideoController;
    private InMeetingAudioController mInMeetingAudioController;
	private InMeetingRemoteController mInMeetingRemoteController;

    private Intent mScreenInfoData;

	private MobileRTCShareView mShareView;
	private AnnotateToolbar mDrawingView;
	private ViewPager mMeetingVideoView;
	private List<View> mSenceList = new ArrayList<View>();

	private View mNormalSenceView;
	private View mGallerySenceView;

	private View mPopupWindowLayout;
	PagerAdapter mPagerAdapter;

	private CustomShareView customShareView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mInMeetingService = ZoomSDK.getInstance().getInMeetingService();
		if(mInMeetingService == null) {
			finish();
			return;
		}

		mInMeetingShareController = mInMeetingService.getInMeetingShareController();
        mInMeetingVideoController = mInMeetingService.getInMeetingVideoController();
        mInMeetingAudioController = mInMeetingService.getInMeetingAudioController();
		mInMeetingRemoteController = mInMeetingService.getInMeetingRemoteController();
		
		setContentView(R.layout.my_meeting_layout);

		mMeetingVideoView = (ViewPager) findViewById(R.id.meetingVideoView);
		mShareView = (MobileRTCShareView)findViewById(R.id.sharingView);
		mDrawingView=(AnnotateToolbar) findViewById(R.id.drawingView);;
		mWaitJoinView = (View)findViewById(R.id.waitJoinView);

		LayoutInflater inflater = getLayoutInflater();

		mNormalSenceView = inflater.inflate(R.layout.layout_meeting_content_normal, null);
		mNormalSenceView.setOnClickListener(this);
		customShareView = (CustomShareView) mNormalSenceView.findViewById(R.id.custom_share_view);
		mGallerySenceView = inflater.inflate(R.layout.layout_meeting_content_gallery, null);
		mGallerySenceView.setOnClickListener(this);
		mSenceList.add(mNormalSenceView);
		mSenceList.add(mGallerySenceView);

		mBtnLeave = (View)findViewById(R.id.btnLeaveZoomMeeting);
		mBtnLeave.setOnClickListener(this);
		mBtnShare = (View) findViewById(R.id.btnShare);
		mBtnShare.setOnClickListener(this);

        mBtnCamera = (View)findViewById(R.id.btnCamera);
        mBtnCamera.setOnClickListener(this);
		mBtnAudio = (View)findViewById(R.id.btnAudio);
		mBtnAudio.setOnClickListener(this);
		mBtnSwitchLoudSpeaker = findViewById(R.id.btnSwitchLoudSpeaker);
		mBtnSwitchLoudSpeaker.setOnClickListener(this);

		mAudioStatusImg = (ImageView)findViewById(R.id.audioStatusImage);
		mVideoStatusImg = (ImageView)findViewById(R.id.videotatusImage);
		mShareStatusImg = (ImageView)findViewById(R.id.shareStatusImage);
		mLoudSpeakerStatusImg = (ImageView)findViewById(R.id.speakerStatusImage);

		mMeetingMoreImg = findViewById(R.id.moreActionImg);
		mMeetingMoreImg.setOnClickListener(this);

        mBtnSwitchCamera = (View)findViewById(R.id.btnSwitchCamera);
        mBtnSwitchCamera.setOnClickListener(this);

		mMeetingBottomToolbar = (View) findViewById(R.id.view_option_bottombar);
		mMeetingTopToolbar = (View) findViewById(R.id.view_option_topbar);
		mConnectingText = (TextView)findViewById(R.id.connectingTxt);

		mInMeetingService.addListener(this);
		mInMeetingShareController.addListener(this);
		mInMeetingRemoteController.addListener(this);

		mDefaultVideoView = (MobileRTCVideoView)mNormalSenceView.findViewById(R.id.videoView);
		mDefaultVideoView1 = (MobileRTCVideoView)mNormalSenceView.findViewById(R.id.videoView1);

		mGalleryVideoView = (MobileRTCVideoView)mGallerySenceView.findViewById(R.id.galleryVideoView);

		mPopupWindowLayout = getLayoutInflater().inflate(R.layout.popupwindow, null);

		mPagerAdapter = new PagerAdapter() {
			@Override
			public int getCount() {
				return mSenceList.size();
			}

			@Override
			public boolean isViewFromObject(View view, Object object) {
				return view == object;
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
									Object object) {
				container.removeView(mSenceList.get(position));
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				container.addView(mSenceList.get(position));
				return mSenceList.get(position);
			}
		};

		mMeetingVideoView.setAdapter(mPagerAdapter);
		mMeetingVideoView.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
					checkShowVideoLayout();

			}
		});
		refreshToolbar();
	}

	private void refreshToolbar() {
		if(mInMeetingService.isMeetingConnected()) {
			if(mIsToolbarHide) {
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
			mConnectingText.setVisibility(View.VISIBLE);
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
		if(mInMeetingShareController.isSharingOut()) {
			mShareStatusImg.setImageResource(R.drawable.icon_share_resume);
		} else {
			mShareStatusImg.setImageResource(R.drawable.icon_share_pause);
		}
	}

	private void updateAnnotationBar() {
		if(mCurShareUserId > 0) {
			if(mInMeetingShareController.isSenderSupportAnnotation(mCurShareUserId)) {
				if(mInMeetingService.isMyself(mCurShareUserId) && !mInMeetingShareController.isSharingScreen()) {
					mDrawingView.setVisibility(View.VISIBLE);
				} else {
					if(currentLayoutType == LAYOUT_TYPE_VIEW_SHARE && mMeetingVideoView.getCurrentItem() == 0) {
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
		mDefaultVideoViewMgr1 = mDefaultVideoView1.getVideoViewManager();
		mGalleryVideoViewMgr = mGalleryVideoView.getVideoViewManager();
		if (mDefaultVideoViewMgr != null && mDefaultVideoViewMgr1 != null && mGalleryVideoViewMgr != null) {
			if (mMeetingVideoView.getCurrentItem() == 0) {
				mGalleryVideoViewMgr.removeAllVideoUnits();
				int newLayoutType = -1;
				if (mInMeetingService.isMeetingConnected()) {
					if (mInMeetingShareController.isOtherSharing()) {
						newLayoutType = LAYOUT_TYPE_VIEW_SHARE;
					} else if (mInMeetingShareController.isSharingOut() && !mInMeetingShareController.isSharingScreen()) {
						newLayoutType = LAYOUT_TYPE_SHARING_VIEW;
					} else {
						InMeetingUserList userlist = mInMeetingService.getInMeetingUserList();
						int userCount = 0;
						if(userlist!= null) {
							userCount = userlist.getUserCount();
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
				} else {
					newLayoutType = LAYOUT_TYPE_PREVIEW;
				}
				removeOldLayout(currentLayoutType);
				currentLayoutType = newLayoutType;
				addNewLayout(newLayoutType);
			} else {
				mDefaultVideoViewMgr.removeAllVideoUnits();
				mDefaultVideoViewMgr1.removeAllVideoUnits();
				showGalleryVideoListLayout();
			}
		}

		updateAnnotationBar();
	}

	private void removeOldLayout(int type) {
		if(type == LAYOUT_TYPE_PREVIEW || type == LAYOUT_TYPE_ONLY_MYSELF || type == LAYOUT_TYPE_ONETOONE) {
			mDefaultVideoViewMgr.removeAllVideoUnits();;
		} else if(type == LAYOUT_TYPE_LIST_VIDEO || type == LAYOUT_TYPE_VIEW_SHARE) {
			mDefaultVideoViewMgr.removeAllVideoUnits();
			mDefaultVideoView.setGestureDetectorEnabled(false);
            mDefaultVideoView.setOnClickListener(this);
			mDefaultVideoViewMgr1.removeAllVideoUnits();
		} else if(type == LAYOUT_TYPE_SHARING_VIEW) {
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
		mDefaultVideoView.setVisibility(View.VISIBLE);
		mDefaultVideoViewMgr.addPreviewVideoUnit(renderInfo1);
		mDefaultVideoView1.setVisibility(View.GONE);
	}

	private void showOnlyMeLayout(){
		mDefaultVideoView.setVisibility(View.VISIBLE);
		mDefaultVideoView1.setVisibility(View.GONE);
		MobileRTCVideoUnitRenderInfo renderInfo = new MobileRTCVideoUnitRenderInfo(0, 0, 100, 100);
		mDefaultVideoViewMgr.addAttendeeVideoUnit(mInMeetingService.getMyUserID(), renderInfo);
	}

	private void showOne2OneLayout(){
		mDefaultVideoView.setVisibility(View.VISIBLE);
		mDefaultVideoView1.setVisibility(View.GONE);

		MobileRTCVideoUnitRenderInfo renderInfo = new MobileRTCVideoUnitRenderInfo(0, 0, 100, 100);
		//options.aspect_mode = MobileRTCVideoUnitAspectMode.VIDEO_ASPECT_PAN_AND_SCAN;
		mDefaultVideoViewMgr.addActiveVideoUnit(renderInfo);

		int viewWidth = mDefaultVideoView.getWidth();
		int viewHeight = mDefaultVideoView.getHeight();

		MobileRTCVideoUnitRenderInfo renderInfo1 = new MobileRTCVideoUnitRenderInfo(73, 73, 25, 20);
		renderInfo1.aspect_mode = MobileRTCVideoUnitAspectMode.VIDEO_ASPECT_ORIGINAL;
		renderInfo1.is_border_visible =true;
		mDefaultVideoViewMgr.addAttendeeVideoUnit(mInMeetingService.getMyUserID(), renderInfo1);
	}

	private void showVideoListLayout() {
		MobileRTCVideoUnitRenderInfo renderInfo = new MobileRTCVideoUnitRenderInfo(0, 0, 100, 100);
		//options.aspect_mode = MobileRTCVideoUnitAspectMode.VIDEO_ASPECT_PAN_AND_SCAN;
		mDefaultVideoViewMgr.addActiveVideoUnit(renderInfo);
		mDefaultVideoView1.setVisibility(View.VISIBLE);
		updateAttendeeVideos();
	}

	private void showSharingViewOutLayout() {
		mMeetingVideoView.setVisibility(View.GONE);
		mShareView.setVisibility(View.VISIBLE);
	}

	//2*2 video unit renderInfo
	final int COL = 2;
	final int ROW = 2;

	private void showGalleryVideoListLayout() {
		InMeetingUserList userlist = mInMeetingService.getInMeetingUserList();
		if (userlist != null) {
			for (int i = 0; i < userlist.getUserCount(); i++) {
				if (i < 4) {
					int row = i / COL;
					int col = i % ROW;

					InMeetingUserInfo user = userlist.getUserInfoByIndex(i);
					MobileRTCVideoUnitRenderInfo renderInfo1 = new MobileRTCVideoUnitRenderInfo(50 * col, 50 * row, 50, 50);
					renderInfo1.is_border_visible = true;
					mGalleryVideoViewMgr.addAttendeeVideoUnit(user.getUserId(), renderInfo1);
				}
			}
		}
	}

	private void updateAttendeeVideos() {
		mDefaultVideoViewMgr1.removePreviewVideoUnit();
		mDefaultVideoViewMgr1.removeAllAttendeeVideoUnit();

		InMeetingUserList userlist = mInMeetingService.getInMeetingUserList();
		if (userlist != null) {
			for (int i = 0; i < userlist.getUserCount(); i++) {
				if (i < 4) {
					int row = i / COL;
					int col = i % ROW;

					InMeetingUserInfo user = userlist.getUserInfoByIndex(i);
					MobileRTCVideoUnitRenderInfo renderInfo1 = new MobileRTCVideoUnitRenderInfo(50 * col, 50 * row, 50, 50);
					renderInfo1.is_border_visible = true;
					mDefaultVideoViewMgr1.addAttendeeVideoUnit(user.getUserId(), renderInfo1);
				}
			}
		}
	}

	private void showViewShareLayout() {
		mDefaultVideoView.setVisibility(View.VISIBLE);
        mDefaultVideoView.setOnClickListener(null);
		mDefaultVideoView.setGestureDetectorEnabled(true);
		long shareUserId = mInMeetingService.activeShareUserID();
		MobileRTCRenderInfo renderInfo1 = new MobileRTCRenderInfo(0, 0, 100, 100);
		mDefaultVideoViewMgr.addShareVideoUnit(shareUserId,renderInfo1);
		mDefaultVideoView1.setVisibility(View.VISIBLE);
		updateAttendeeVideos();

		customShareView.setMobileRTCVideoView(mDefaultVideoView);
		long myUserId = mInMeetingService.getMyUserID();
		boolean hasPriv = mInMeetingRemoteController.hasRemoteControlPrivilegeWithUserId(myUserId);
		boolean isRc = mInMeetingRemoteController.isRemoteController();
		customShareView.enableRC(hasPriv, isRc);
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
		mDefaultVideoView1.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mDefaultVideoView.onPause();
		mDefaultVideoView1.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mInMeetingService!= null)
			mInMeetingService.removeListener(this);
		if(mInMeetingShareController != null)
			mInMeetingShareController.removeListener(this);
		if(null!=mInMeetingRemoteController)
		{
			mInMeetingRemoteController.removeListener(this);
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
			showMoreMenuPopupWindow();;
		} else if(id == R.id.btnSwitchLoudSpeaker) {
			onClickBtnSwitchLoudSpeaker();
		} else if(view == mNormalSenceView || view == mGallerySenceView) {
			hideOrShowToolbar(!mIsToolbarHide);
		}
	}
	
	@Override
	public void onBackPressed() {
		if(mInMeetingService.isMeetingConnected()) {
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
	public void onMeetingReady() {
		Log.i(TAG, "onMeetingReady: " + mInMeetingService.isMeetingHost());
		refreshToolbar();
		checkShowVideoLayout();
	}

	@Override
	public void onMeetingNeedWaitHost() {
		Log.i(TAG, "onMeetingNeedWaitHost");
		mWaitJoinView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onMeetingReadyToJoin() {
		Log.i(TAG, "onMeetingReadyToJoin");
		mWaitJoinView.setVisibility(View.GONE);
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
	public void onMeetingUserJoin(long userId) {
		checkShowVideoLayout();
	}

	@Override
	public void onMeetingUserLeave(long userId) {
		checkShowVideoLayout();
	}

	@Override
	public void onMeetingUserUpdated(long userId) {
		checkShowVideoLayout();
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
		Toast.makeText(this, inMeetingChatMessage.getReceiverDisplayName() + ": "+ inMeetingChatMessage.getContent(), Toast.LENGTH_SHORT).show();
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

        switch(requestCode) {
            case REQUEST_SHARE_SCREEN_PERMISSION:
                if(resultCode != RESULT_OK){
                    if(us.zoom.videomeetings.BuildConfig.DEBUG)
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
		if(mInMeetingService.isMeetingConnected()) {
			if(mInMeetingService.isMeetingHost()) {
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
            if(AndroidAppUtil.hasActivityForIntent(this, intent)){
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
        if(data == null ){
            return;
        }
        if(Build.VERSION.SDK_INT >= 24 && ! Settings.canDrawOverlays(this)){
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            mScreenInfoData = data;
            startActivityForResult(intent,REQUEST_SYSTEM_ALERT_WINDOW);
        }else{
            mInMeetingShareController.startShareScreenSession(data);
        }
    }

	private final int MENU_SHARE_SCREEN = 0;
	private final int MENU_SHARE_IMAGE = 1;
	private final int MENU_SHARE_WEBVIEW = 2;
	private final int MENU_WHITE_BOARD = 3;

	private void showShareActionPopupWindow() {
		final SimpleMenuAdapter menuAdapter = new SimpleMenuAdapter(this);

		if(Build.VERSION.SDK_INT >= 21) {
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

				SimpleMenuItem item = (SimpleMenuItem)menuAdapter.getItem(position);
				if(item.getAction() == MENU_SHARE_WEBVIEW) {
					startShareWebUrl();
				} else if(item.getAction() == MENU_SHARE_IMAGE) {
					startShareImage();
				} else if(item.getAction() == MENU_SHARE_SCREEN) {
					askScreenSharePermission();
				} else if(item.getAction() == MENU_WHITE_BOARD) {
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

	private void showMoreMenuPopupWindow() {
		final SimpleMenuAdapter menuAdapter = new SimpleMenuAdapter(this);
		if(mInMeetingAudioController.isAudioConnected()) {
			menuAdapter.addItem(new SimpleMenuItem(MENU_DISCONNECT_AUDIO, "Disconnect Audio"));
		}
		if(mInMeetingShareController.isSharingOut()) {
			menuAdapter.addItem(new SimpleMenuItem(MENU_STOP_SHARE, "Stop Share"));
		}

		if(mIsToolbarHide) {
			menuAdapter.addItem(new SimpleMenuItem(MENU_SHOW_TOOLBAR, "Show Toolbar"));
		} else {
			menuAdapter.addItem(new SimpleMenuItem(MENU_HIDE_TOOLBAR, "Hide Toolbar"));
		}

		menuAdapter.addItem((new SimpleMenuItem(MENU_SHOW_PLIST, "Paticipants")));

		ListView shareActions = (ListView) mPopupWindowLayout.findViewById(R.id.actionListView);
		final PopupWindow window = new PopupWindow(mPopupWindowLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		shareActions.setAdapter(menuAdapter);
		shareActions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				SimpleMenuItem item = (SimpleMenuItem)menuAdapter.getItem(position);
				if(item.getAction() == MENU_DISCONNECT_AUDIO) {
					mInMeetingAudioController.disconnectAudio();
				} else if(item.getAction() == MENU_SHOW_TOOLBAR || item.getAction() == MENU_HIDE_TOOLBAR) {
					hideOrShowToolbar(!mIsToolbarHide);
				} else if(item.getAction() == MENU_STOP_SHARE) {
					stopShare();
				} else if(item.getAction() == MENU_SHOW_PLIST) {
					mInMeetingService.showZoomParticipantsUI(MyMeetingActivity.this, REQUEST_PLIST);
				}
				window.dismiss();
			}
		});

		window.setFocusable(true);
		window.setOutsideTouchable(true);
		window.update();
		window.showAsDropDown(mMeetingMoreImg, 0, 20);
	}

	public int checkSelfPermission(String permission){
		if(permission == null || permission.length() == 0){
			return PackageManager.PERMISSION_DENIED;
		}
		try {
			return checkPermission(permission, android.os.Process.myPid(), android.os.Process.myUid());
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
}

