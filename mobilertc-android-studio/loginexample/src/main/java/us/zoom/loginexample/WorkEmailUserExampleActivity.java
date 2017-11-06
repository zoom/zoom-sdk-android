package us.zoom.loginexample;

import us.zoom.sdk.InstantMeetingOptions;
import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.MeetingError;
import us.zoom.sdk.MeetingEvent;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitializeListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class WorkEmailUserExampleActivity extends Activity implements Constants, MeetingServiceListener, ZoomSDKAuthenticationListener {

	private final static String TAG = "ZoomSDKExample";
	
	private EditText mEdtMeetingNo;
	private EditText mEdtMeetingPassword;
	
	private final static String DISPLAY_NAME = "ZoomUS SDK";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.email_user_example);

		mEdtMeetingNo = (EditText)findViewById(R.id.edtMeetingNo);
		mEdtMeetingPassword = (EditText)findViewById(R.id.edtMeetingPassword);

		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		if(zoomSDK.isInitialized()) {
			zoomSDK.addAuthenticationListener(this);
			MeetingService meetingService = zoomSDK.getMeetingService();
			if(meetingService != null) {
				meetingService.addListener(this);
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		ZoomSDK zoomSDK = ZoomSDK.getInstance();	
		if(zoomSDK.isInitialized()) {
			zoomSDK.removeAuthenticationListener(this);
			MeetingService meetingService = zoomSDK.getMeetingService();
			meetingService.removeListener(this);
		}
		
		super.onDestroy();
	}
	
	
	public void onClickBtnLogout(View view) {
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		if(!zoomSDK.logoutZoom()) {
			Toast.makeText(this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void onMeetingEvent(int meetingEvent, int errorCode,
			int internalErrorCode) {
		
		Log.i(TAG, "onMeetingEvent, meetingEvent=" + meetingEvent + ", errorCode=" + errorCode
				+ ", internalErrorCode=" + internalErrorCode);
		
		if(meetingEvent == MeetingEvent.MEETING_CONNECT_FAILED && errorCode == MeetingError.MEETING_ERROR_CLIENT_INCOMPATIBLE) {
			Toast.makeText(this, "Version of ZoomSDK is too low!", Toast.LENGTH_LONG).show();
		}
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
	
	public void onClickBtnLoginUserStart(View view) {
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
		
		MeetingService meetingService = zoomSDK.getMeetingService();
		
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
		
		int ret = meetingService.startMeeting(this, meetingNo, opts);
		
		Log.i(TAG, "onClickBtnLoginUserStart, ret=" + ret);
	}
	
	public void onClickBtnLoginUserJoin(View view) {
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

		int ret = meetingService.joinMeeting(this, meetingNo, DISPLAY_NAME, meetingPassword, opts);
		
		Log.i(TAG, "onClickBtnLoginUserJoin, ret=" + ret);
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
	
	
	private void showLoginView() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}
	
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}
}
