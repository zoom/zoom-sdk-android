package us.zoom.sdksample.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import us.zoom.sdk.MeetingError;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdksample.initsdk.AuthConstants;
import us.zoom.sdksample.R;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.MyMeetingActivity;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.MeetingWindowHelper;
import us.zoom.sdksample.startjoinmeeting.apiuser.APIUserInfo;
import us.zoom.sdksample.startjoinmeeting.apiuser.APIUserInfoHelper;
import us.zoom.sdksample.startjoinmeeting.apiuser.ApiUserStartMeetingHelper;
import us.zoom.sdksample.startjoinmeeting.joinmeetingonly.JoinMeetingHelper;

import static us.zoom.sdksample.startjoinmeeting.apiuser.APIUserConstants.USER_ID;

public class APIUserStartJoinMeetingActivity extends Activity implements AuthConstants, MeetingServiceListener , View.OnClickListener{

	private final static String TAG = "ZoomSDKExample";
	
	private EditText mEdtMeetingNo;
	private EditText mEdtMeetingPassword;
	private EditText mEdtVanityId;
	private View mProgressPanel;
	private Button mBtnStartMeeting;
	private Button mBtnJoinMeeting;
	private Button mBtnSettings;
	private Button mReturnMeeting;

	private final static int STYPE = MeetingService.USER_TYPE_API_USER;
	private final static String DISPLAY_NAME = "ZoomUS SDK";

	private boolean mbPendingStartMeeting = false;
	private boolean isResumed=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.api_user_start_join);

		mEdtMeetingNo = (EditText)findViewById(R.id.edtMeetingNo);
		mEdtVanityId = (EditText)findViewById(R.id.edtVanityUrl);
		mEdtMeetingPassword = (EditText)findViewById(R.id.edtMeetingPassword);

		mProgressPanel = (View)findViewById(R.id.progressPanel);

		mBtnStartMeeting = (Button) findViewById(R.id.btnStartMeeting);
		mBtnStartMeeting.setOnClickListener(this);

		mBtnJoinMeeting = (Button) findViewById(R.id.btnJoinMeeting);
		mBtnJoinMeeting.setOnClickListener(this);
		mBtnSettings = findViewById(R.id.btn_settings);
		mReturnMeeting = findViewById(R.id.btn_return);
		showProgressPanel(false);
		registerListener();
	}
	
	private void registerListener() {
		MeetingService meetingService = ZoomSDK.getInstance().getMeetingService();
		if(meetingService != null) {
			meetingService.addListener(this);//register meetingServiceListener
		}
	}



	@Override
	protected void onPause() {
		super.onPause();
		isResumed=false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		isResumed=true;
		refreshUI();
		if(APIUserInfoHelper.getAPIUserInfo() == null) {
			RetrieveUserInfoTask task = new RetrieveUserInfoTask();//retrieve api user token
			task.execute(USER_ID);
		}
	}

	@Override
	protected void onDestroy() {
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		if(zoomSDK.isInitialized()) {
			MeetingService meetingService = zoomSDK.getMeetingService();
			meetingService.removeListener(this);//unregister meetingServiceListener
		}
		MeetingWindowHelper.getInstance().removeOverlayListener();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btnStartMeeting) {
			onClickBtnStartMeeting();
		} else if(v.getId() == R.id.btnJoinMeeting) {
			onClickBtnJoinMeeting();
		}
	}


	private void showProgressPanel(boolean show) {
		if(show) {
			mBtnStartMeeting.setVisibility(View.GONE);
			mBtnJoinMeeting.setVisibility(View.GONE);
			mProgressPanel.setVisibility(View.VISIBLE);
		} else {
			mBtnStartMeeting.setVisibility(View.VISIBLE);
			mBtnJoinMeeting.setVisibility(View.VISIBLE);
			mProgressPanel.setVisibility(View.GONE);
		}
	}

	public void onClickReturnMeeting(View view) {
		MeetingWindowHelper.getInstance().hiddenMeetingWindow(true);
		Intent intent = new Intent(this, MyMeetingActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}

	public void onClickSettings(View view) {
		Intent intent = new Intent(this, MeetingSettingActivity.class);
		startActivity(intent);
	}

	public void onClickBtnJoinMeeting() {
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

		int ret = -1;
		if(vanityId.length() != 0) {
			ret = JoinMeetingHelper.getInstance().joinMeetingWithVanityId(this, vanityId, meetingPassword);
		} else {
			ret = JoinMeetingHelper.getInstance().joinMeetingWithNumber(this, meetingNo, meetingPassword);
		}
		Log.i(TAG, "onClickBtnJoinMeeting, ret=" + ret);
	}
	
	public void onClickBtnStartMeeting() {
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

		int ret = -1;
		if (vanityId.length() != 0) {
			ret = ApiUserStartMeetingHelper.getInstance().startMeetingWithVanityId(this, vanityId);
		} else {
			ret = ApiUserStartMeetingHelper.getInstance().startMeetingWithNumber(this, meetingNo);
		}

		Log.i(TAG, "onClickBtnStartMeeting, ret=" + ret);
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
			onClickBtnStartMeeting();
		}
		if (meetingStatus == MeetingStatus.MEETING_STATUS_CONNECTING) {
			showMeetingUi();
		}
		refreshUI();
	}

	private void refreshUI() {
		MeetingStatus meetingStatus = ZoomSDK.getInstance().getMeetingService().getMeetingStatus();
		if (meetingStatus == MeetingStatus.MEETING_STATUS_CONNECTING || meetingStatus == MeetingStatus.MEETING_STATUS_INMEETING
				|| meetingStatus == MeetingStatus.MEETING_STATUS_RECONNECTING) {
			mBtnSettings.setVisibility(View.GONE);
			mReturnMeeting.setVisibility(View.VISIBLE);
		} else {
			mBtnSettings.setVisibility(View.VISIBLE);
			mReturnMeeting.setVisibility(View.GONE);
		}
		if(ZoomSDK.getInstance().getMeetingSettingsHelper().isCustomizedMeetingUIEnabled())
		{
			if (meetingStatus == MeetingStatus.MEETING_STATUS_INMEETING&&isResumed) {
				MeetingWindowHelper.getInstance().showMeetingWindow(this);
			} else {
				MeetingWindowHelper.getInstance().hiddenMeetingWindow(true);
			}
		}
	}

	private void showMeetingUi() {
		if (ZoomSDK.getInstance().getMeetingSettingsHelper().isCustomizedMeetingUIEnabled()) {
			Intent intent = new Intent(this, MyMeetingActivity.class);
			intent.putExtra("from",2);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			this.startActivity(intent);
		}
	}

	private class RetrieveUserInfoTask extends AsyncTask<String, Void, APIUserInfo> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressPanel(true);
		}

		@Override
		protected APIUserInfo doInBackground(String... params) {
			String token = APIUserInfoHelper.getZoomToken(params[0]);
			String accessToken = APIUserInfoHelper.getZoomAccessToken(params[0]);

			if(token != null && !token.isEmpty() && accessToken != null && !accessToken.isEmpty()) {
				APIUserInfo apiUserInfo = new APIUserInfo(params[0], token, accessToken);
				APIUserInfoHelper.saveAPIUserInfo(apiUserInfo);
				return apiUserInfo;
			} else {
				return null;
			}
		}

		@Override
		protected void onPostExecute(APIUserInfo apiUserInfo) {
			super.onPostExecute(apiUserInfo);
			showProgressPanel(false);
			if(apiUserInfo == null)
				Toast.makeText(APIUserStartJoinMeetingActivity.this, "Faild to retrieve Api user info!", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		MeetingWindowHelper.getInstance().onActivityResult(requestCode,this);
	}
}
