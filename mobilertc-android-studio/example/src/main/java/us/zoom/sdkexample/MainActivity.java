package us.zoom.sdkexample;

import us.zoom.sdk.InstantMeetingOptions;
import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingError;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.StartMeetingParams4NormalUser;
import us.zoom.sdk.StartMeetingParamsWithoutLogin;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitializeListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements Constants, MeetingServiceListener, ZoomSDKAuthenticationListener {

	private final static String TAG = "ZoomSDKExample";
	
	private EditText mEdtMeetingNo;
	private EditText mEdtMeetingPassword;
	private EditText mEdtVanityId;
	private Button mBtnStartInstantMeeting;
	private Button mBtnPreMeeting;
	private Button mBtnLoginOut;
	private final static int STYPE = MeetingService.USER_TYPE_API_USER;
	private final static String DISPLAY_NAME = "ZoomUS SDK";

	private boolean mbPendingStartMeeting = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);

		mEdtMeetingNo = (EditText)findViewById(R.id.edtMeetingNo);
		mEdtVanityId = (EditText)findViewById(R.id.edtVanityUrl);
		mEdtMeetingPassword = (EditText)findViewById(R.id.edtMeetingPassword);
		mBtnStartInstantMeeting = (Button)findViewById(R.id.btnLoginUserStartInstant);
		mBtnPreMeeting = (Button)findViewById(R.id.btnPreMeeting);
		mBtnLoginOut = (Button)findViewById(R.id.btnLogout);


		registerListener();
	}
	
	private void registerListener() {
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		zoomSDK.addAuthenticationListener(this);
		MeetingService meetingService = zoomSDK.getMeetingService();
		if(meetingService != null) {
			meetingService.addListener(this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshUI();
	}

	@Override
	protected void onDestroy() {
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		zoomSDK.removeAuthenticationListener(this);
		if(zoomSDK.isInitialized()) {
			MeetingService meetingService = zoomSDK.getMeetingService();
			meetingService.removeListener(this);
		}
		
		super.onDestroy();
	}

	private void refreshUI() {
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		if(zoomSDK.isInitialized()) {
			mBtnStartInstantMeeting.setVisibility(zoomSDK.isLoggedIn() ? View.VISIBLE: View.GONE);
			mBtnPreMeeting.setVisibility(zoomSDK.isLoggedIn() ? View.VISIBLE: View.GONE);
			mBtnLoginOut.setVisibility(zoomSDK.isLoggedIn() ? View.VISIBLE: View.GONE);
		}
	}


	public void onClickBtnJoinMeeting(View view) {
		String meetingNo = mEdtMeetingNo.getText().toString().trim();
		String meetingPassword = mEdtMeetingPassword.getText().toString().trim();

		String vanityId = mEdtVanityId.getText().toString().trim();
		
		if(meetingNo.length() == 0 && vanityId.length() == 0) {
			Toast.makeText(this, "You need to enter a meeting number/ vanity id which you want to join.", Toast.LENGTH_LONG).show();
			return;
		}

		if(meetingNo.length() != 0 && vanityId.length() !=0) {
			Toast.makeText(this, "Both meeting number and vanity id have value,  just set one of them", Toast.LENGTH_LONG).show();
			return;
		}
		
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		
		if(!zoomSDK.isInitialized()) {
			Toast.makeText(this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
			return;
		}
		
		MeetingService meetingService = zoomSDK.getMeetingService();
		
		JoinMeetingOptions opts = new JoinMeetingOptions();
//		opts.no_driving_mode = true;
//		opts.no_invite = true;
//		opts.no_meeting_end_message = true;
//		opts.no_titlebar = true;
//		opts.no_bottom_toolbar = true;
//		opts.no_dial_in_via_phone = true;
//		opts.no_dial_out_to_phone = true;
//		opts.no_disconnect_audio = true;
//		opts.no_share = true;
//		opts.invite_options = InviteOptions.INVITE_VIA_EMAIL + InviteOptions.INVITE_VIA_SMS;
//		opts.no_audio = true;
//		opts.no_video = true;
//		opts.meeting_views_options = MeetingViewsOptions.NO_BUTTON_SHARE;
//		opts.no_meeting_error_message = true;
//		opts.participant_id = "participant id";
		JoinMeetingParams params = new JoinMeetingParams();

		params.displayName = DISPLAY_NAME;
		params.password = meetingPassword;

		if(vanityId.length() != 0) {
			params.vanityID = vanityId;
		} else {
			params.meetingNo = meetingNo;
		}
		int ret = meetingService.joinMeetingWithParams(this, params);
		
		Log.i(TAG, "onClickBtnJoinMeeting, ret=" + ret);
	}
	
	public void onClickBtnStartMeeting(View view) {
		String meetingNo = mEdtMeetingNo.getText().toString().trim();
		String vanityId = mEdtVanityId.getText().toString().trim();

		if(meetingNo.length() == 0 && vanityId.length() == 0) {
			Toast.makeText(this, "You need to enter a meeting number/ vanity  which you want to join.", Toast.LENGTH_LONG).show();
			return;
		}

		if(meetingNo.length() != 0 && vanityId.length() !=0) {
			Toast.makeText(this, "Both meeting number and vanity  have value,  just set one of them", Toast.LENGTH_LONG).show();
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
				meetingService.returnToMeeting(this);
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
		
		StartMeetingOptions opts = new StartMeetingOptions();
//		opts.no_driving_mode = true;
//		opts.no_invite = true;
//		opts.no_meeting_end_message = true;
//		opts.no_titlebar = true;
//		opts.no_bottom_toolbar = true;
//		opts.no_dial_in_via_phone = true;
//		opts.no_dial_out_to_phone = true;
//		opts.no_disconnect_audio = true;
//		opts.no_share = true;
//		opts.invite_options = InviteOptions.INVITE_ENABLE_ALL;
//		opts.no_audio = true;
//		opts.no_video = true;
//		opts.meeting_views_options = MeetingViewsOptions.NO_BUTTON_SHARE + MeetingViewsOptions.NO_BUTTON_VIDEO;
//		opts.no_meeting_error_message = true;

		int ret = 0;
		if(zoomSDK.isLoggedIn()) {
			StartMeetingParams4NormalUser params = new StartMeetingParams4NormalUser();
			if(vanityId.length() != 0) {
				params.vanityID = vanityId;
			} else {
				params.meetingNo = meetingNo;
			}
			ret = meetingService.startMeetingWithParams(this, params, opts);

		} else {
			StartMeetingParamsWithoutLogin params = new StartMeetingParamsWithoutLogin();
			params.userId = USER_ID;
			params.zoomToken = ZOOM_TOKEN;
			params.userType = STYPE;
			params.displayName = DISPLAY_NAME;
			params.zoomAccessToken = ZOOM_ACCESS_TOKEN;

			if (vanityId.length() != 0) {
				params.vanityID = vanityId;
			} else {
				params.meetingNo = meetingNo;
			}
			ret = meetingService.startMeetingWithParams(this, params, opts);
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

	public void onClickBtnPreMeeting(View view) {
		Intent intent = new Intent(this, PreMeetingExampleActivity.class);
		startActivity(intent);
	}

	public void onClickBtnLogout(View view) {
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		if(!zoomSDK.logoutZoom()) {
			Toast.makeText(this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void onMeetingStatusChanged(MeetingStatus meetingStatus, int errorCode,
									   int internalErrorCode) {
		Log.i(TAG, "onMeetingStatusChanged, meetingStatus=" + meetingStatus + ", errorCode=" + errorCode
				+ ", internalErrorCode=" + internalErrorCode);
		
		if(meetingStatus == MeetingStatus.MEETING_STATUS_FAILED && errorCode == MeetingError.MEETING_ERROR_CLIENT_INCOMPATIBLE) {
			Toast.makeText(this, "Version of ZoomSDK is too low!", Toast.LENGTH_LONG).show();
		}
		
		if(mbPendingStartMeeting && meetingStatus == MeetingStatus.MEETING_STATUS_IDLE) {
			mbPendingStartMeeting = false;
			onClickBtnStartMeeting(null);
		}
	}

	@Override
	public void onBackPressed() {
		if(ZoomSDK.getInstance().isLoggedIn()) {
			moveTaskToBack(true);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onZoomSDKLoginResult(long l) {

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
		ZoomSDK.getInstance().logoutZoom();
	}

	private void showLoginView() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}

}
