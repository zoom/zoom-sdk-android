package us.zoom.customuidemo;

import us.zoom.sdk.InstantMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingError;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingSettingsHelper;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.StartMeetingParams4NormalUser;
import us.zoom.sdk.StartMeetingParamsWithoutLogin;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitializeListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends FragmentActivity implements Constants, ZoomSDKAuthenticationListener, MeetingServiceListener {

    private final static String TAG = MainActivity.class.getSimpleName();
	
	private EditText mEdtMeetingNo;
	private EditText mEdtMeetingPassword;
	private Button mBtnReturnToMeeting;
	private Button mBtnStartInstantMeeting;
	private Button mBtnLoginOut;
	
	private final static int STYPE = MeetingService.USER_TYPE_API_USER;
	private final static String DISPLAY_NAME = "ZoomUS SDK";

	private boolean mbPendingStartMeeting = false;

	//private boolean mIsShowMeetingUIInCurrActivity = false; //Set yes to show meeting ui in this activity

	//private Fragment mFragmentMeetingView;
	//private FragmentManager mFragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);

		mEdtMeetingNo = (EditText)findViewById(R.id.edtMeetingNo);
		mEdtMeetingPassword = (EditText)findViewById(R.id.edtMeetingPassword);
		mBtnReturnToMeeting = (Button)findViewById(R.id.btnReturnToMeeting);
		mBtnStartInstantMeeting = (Button)findViewById(R.id.btnLoginUserStartInstant);
		mBtnLoginOut = (Button)findViewById(R.id.btnLogout);

		//mFragmentMeetingView = new MeetingContentFragment();
		//mFragmentManager = getSupportFragmentManager();
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		zoomSDK.addAuthenticationListener(this);
		registerMeetingServiceListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshUI();
	}

	@Override
	protected void onDestroy() {
		ZoomSDK zoomSDK = ZoomSDK.getInstance();

		if(zoomSDK.isInitialized()) {
			MeetingService meetingService = zoomSDK.getMeetingService();
			meetingService.removeListener(this);
		}
		super.onDestroy();
	}

	private void refreshUI() {
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		mBtnReturnToMeeting.setVisibility(View.GONE);
		if(zoomSDK.isInitialized()) {
			MeetingService meetingService = zoomSDK.getMeetingService();
			if(meetingService.getMeetingStatus() == MeetingStatus.MEETING_STATUS_INMEETING
					|| meetingService.getMeetingStatus() == MeetingStatus.MEETING_STATUS_WAITINGFORHOST
					|| meetingService.getMeetingStatus() == MeetingStatus.MEETING_STATUS_IN_WAITING_ROOM
					|| meetingService.getMeetingStatus() == MeetingStatus.MEETING_STATUS_CONNECTING) {
				//if(!mIsShowMeetingUIInCurrActivity) {
					mBtnReturnToMeeting.setVisibility(View.VISIBLE);
				//} else {
				//	mBtnReturnToMeeting.setVisibility(View.GONE);
				//	showMeetingUi();
				//}
			} else if(meetingService.getMeetingStatus() == MeetingStatus.MEETING_STATUS_IDLE) {
				//FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
				//fragmentTransaction.remove(mFragmentMeetingView);
				//fragmentTransaction.commit();

				if(mbPendingStartMeeting) {
					mbPendingStartMeeting = false;
					onClickBtnStartMeeting(null);
				}
			}

			mBtnStartInstantMeeting.setVisibility(zoomSDK.isLoggedIn() ? View.VISIBLE: View.GONE);
			mBtnLoginOut.setVisibility(zoomSDK.isLoggedIn() ? View.VISIBLE: View.GONE);
		}
	}

	private void registerMeetingServiceListener() {
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		MeetingService meetingService = zoomSDK.getMeetingService();
		if(meetingService != null) {
			meetingService.addListener(this);
		}

		//Let SDK know we are using customized meeting UI
		MeetingSettingsHelper meetingSettingsHelper = zoomSDK.getMeetingSettingsHelper();
		if(meetingService != null) {
			meetingSettingsHelper.setCustomizedMeetingUIEnabled(true);
		}
	}

	public void onClickBtnJoinMeeting(View view) {
		String meetingNo = mEdtMeetingNo.getText().toString().trim();
		String meetingPassword = mEdtMeetingPassword.getText().toString().trim();
		
		if(meetingNo.length() == 0) {
			Toast.makeText(this, "You need to enter a meeting number which you want to join.", Toast.LENGTH_LONG).show();
			return;
		}
		
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		
		if(!zoomSDK.isInitialized()) {
			Toast.makeText(this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
			return;
		}

		JoinMeetingParams params = new JoinMeetingParams();
		params.meetingNo = meetingNo;
		params.displayName = DISPLAY_NAME;
		params.password = meetingPassword;
		MeetingService meetingService = zoomSDK.getMeetingService();
		int ret = meetingService.joinMeetingWithParams(this, params);

		Log.i(TAG, "onClickBtnJoinMeeting, ret=" + ret);
	}
	
	public void onClickBtnStartMeeting(View view) {
		String meetingNo = mEdtMeetingNo.getText().toString().trim();
		
		if(meetingNo.length() == 0) {
			Toast.makeText(this, "You need to enter a scheduled meeting number.", Toast.LENGTH_LONG).show();
			return;
		}
		
		ZoomSDK zoomSDK = ZoomSDK.getInstance();

		if(!zoomSDK.isInitialized()) {
			Toast.makeText(this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
			return;
		}
		
		final MeetingService meetingService = zoomSDK.getMeetingService();
		
		if(meetingService.getMeetingStatus() != MeetingStatus.MEETING_STATUS_IDLE) {
			long lMeetingNo = 0;
			try {
				lMeetingNo = Long.parseLong(meetingNo);
			} catch (NumberFormatException e) {
				Toast.makeText(this, "Invalid meeting number: " + meetingNo, Toast.LENGTH_LONG).show();
				return;
			}
			
			if(meetingService.getCurrentRtcMeetingNumber() == lMeetingNo) {
				return;
			}
			
			new AlertDialog.Builder(this)
				.setMessage("Do you want to leave current meeting and start another?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mbPendingStartMeeting = true;
						meetingService.leaveCurrentMeeting(false);
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				})
				.show();
			return;
		}

		int ret = 0;

		if(zoomSDK.isLoggedIn()) {

			StartMeetingParams4NormalUser params = new StartMeetingParams4NormalUser();
			params.meetingNo = meetingNo;
			ret = meetingService.startMeetingWithParams(this, params);

		} else {
			StartMeetingParamsWithoutLogin params = new StartMeetingParamsWithoutLogin();
			params.userId = USER_ID;
			params.zoomToken = ZOOM_TOKEN;
			params.zoomAccessToken = ZOOM_ACCESS_TOKEN;
			params.userType = STYPE;
			params.meetingNo = meetingNo;
			params.displayName = DISPLAY_NAME;
			ret = meetingService.startMeetingWithParams(this, params);
		}


		Log.i(TAG, "onClickBtnStartMeeting, ret=" + ret);

	}

	public void onClickBtnLoginUserStartInstant(View view) {
		ZoomSDK zoomSDK = ZoomSDK.getInstance();

		if(!zoomSDK.isInitialized()) {
			Toast.makeText(this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
			return;
		}

		MeetingService meetingService = zoomSDK.getMeetingService();

		InstantMeetingOptions opts = new InstantMeetingOptions();
//		opts.no_driving_mode = true;
//		opts.no_invite = true;
//		opts.no_meeting_end_message = true;
//		opts.no_titlebar = true;
//		opts.no_bottom_toolbar = true;
//		opts.no_dial_in_via_phone = true;
//		opts.no_dial_out_to_phone = true;
//		opts.no_disconnect_audio = true;
//		opts.no_share = true;

		int ret = meetingService.startInstantMeeting(this, opts);

		Log.i(TAG, "onClickBtnLoginUserStartInstant, ret=" + ret);
	}

	public void onClickBtnLogout(View view) {
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		if(!zoomSDK.logoutZoom()) {
			Toast.makeText(this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
		}
	}

	public void onClickBtnReturnToMeeting(View view) {
		Intent intent = new Intent(this, MyMeetingActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}

	private void showMeetingUi() {
//		if(mIsShowMeetingUIInCurrActivity) {
//			if (!mFragmentMeetingView.isAdded()) {
//				FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
//				fragmentTransaction.add(R.id.fragment_container, mFragmentMeetingView);
//				fragmentTransaction.commit();
//			}
//		} else {
			Intent intent = new Intent(this, MyMeetingActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.startActivity(intent);
//		}
	}
	
	@Override
	public void onMeetingStatusChanged(MeetingStatus status, int errorCode,
			int internalErrorCode) {

		Log.i(TAG, "onMeetingEvent, meetingEvent=" + status + ", errorCode=" + errorCode
				+ ", internalErrorCode=" + internalErrorCode);
		
		if(status == MeetingStatus.MEETING_STATUS_FAILED && errorCode == MeetingError.MEETING_ERROR_CLIENT_INCOMPATIBLE) {
			Toast.makeText(this, "Version of ZoomSDK is too low!", Toast.LENGTH_LONG).show();
		}


		if(status == MeetingStatus.MEETING_STATUS_CONNECTING /*&& !mIsShowMeetingUIInCurrActivity*/) {
			showMeetingUi();
		}

		refreshUI();

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//		final List<Fragment> fragments = getSupportFragmentManager().getFragments();
//		if (fragments != null) {
//			for (Fragment fragment : fragments) {
//				fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
//			}
//		}
	}

	@Override
	public void onZoomSDKLoginResult(long result) {
		if(result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
			Toast.makeText(this, "Login successfully", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Login failed result code = " + result, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onZoomSDKLogoutResult(long result) {
		if(result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
			Toast.makeText(this, "Logout successfully", Toast.LENGTH_SHORT).show();
			showLoginView();
			finish();
		} else {
			Toast.makeText(this, "Logout failed result code = " + result, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onZoomIdentityExpired() {
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		zoomSDK.logoutZoom();
	}

	private void showLoginView() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        if(zoomSDK.isLoggedIn()) {
            moveTaskToBack(true);
        } else {
            super.onBackPressed();
        }
	}
}
