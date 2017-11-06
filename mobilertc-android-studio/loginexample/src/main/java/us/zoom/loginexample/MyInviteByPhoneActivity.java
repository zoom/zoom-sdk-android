package us.zoom.loginexample;

import us.zoom.sdk.DialOutStatus;
import us.zoom.sdk.DialOutStatusListener;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.ZoomSDK;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MyInviteByPhoneActivity extends Activity implements View.OnClickListener, DialOutStatusListener{
	private static final String TAG = MyInviteByPhoneActivity.class.getSimpleName();
	private Button mBtnCall;
	private Button mBtnHangup;
	private EditText mEdtPhoneNumber;
	private MeetingService mMeetingService;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call_me_activity);
		
		mEdtPhoneNumber = (EditText)findViewById(R.id.edtPhoneNumber);
		mBtnCall = (Button)findViewById(R.id.btnCall);
		mBtnCall.setOnClickListener(this);
		mBtnHangup = (Button)findViewById(R.id.btnHangUp);
		mBtnHangup.setOnClickListener(this);
		
		initButtons();
		
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		mMeetingService = zoomSDK.getMeetingService();
		if(mMeetingService != null) {
			mMeetingService.addDialOutListener(this);
		}
	}
	
	@Override
	public void onDialOutStatusChanged(int status) {
		Log.d(TAG, "onDialOutStatusChanged status = " + status);
		Log.d(TAG, "onDialOutStatusChanged isInProgress = " +  mMeetingService.isDialOutInProgress());
		if(status == DialOutStatus.DIALOUT_STATUS_JOIN_SUC) {
			//finish();
			if(mMeetingService != null) {
				String number = mEdtPhoneNumber.getText().toString().trim();
				boolean isInProgress = mMeetingService.isDialOutInProgress();
				Log.d(TAG, "DIALOUT_STATUS_JOIN_SUC isInProgress = " + isInProgress);			
				boolean ret = mMeetingService.dialOutUser("+8617756076490", "222", false);
				Log.d(TAG, "onDialOutStatusChanged ret = " + ret);			
			}
		}
		updateButtons(status);
	}

	@Override
	public void onClick(View arg0) {
	
		if(arg0.getId() == R.id.btnCall) {
	
			if(mMeetingService != null) {
				String number = mEdtPhoneNumber.getText().toString().trim();
				boolean ret = mMeetingService.dialOutUser("+8617705606951", "111", false);
				Log.d(TAG, "onClick ret = " + ret);			

			}
		} else if(arg0.getId() == R.id.btnHangUp) {
			mMeetingService.cancelDialOut(false);
		}
	}
		
	private void initButtons() {
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		MeetingService meetingService = zoomSDK.getMeetingService();
		if(meetingService != null && meetingService.isDialOutInProgress()) {
			mBtnCall.setEnabled(false);
			mBtnHangup.setEnabled(true);
		} else {
			mBtnCall.setEnabled(true);
			mBtnHangup.setEnabled(false);
		}
	}
	
	private void updateButtons(int status) {
		
		switch(status) {
		case DialOutStatus.DIALOUT_STATUS_UNKNOWN:
		case DialOutStatus.DIALOUT_STATUS_ZOOM_CANCEL_CALL_FAIL:
		case DialOutStatus.DIALOUT_STATUS_ZOOM_CALL_CANCELED:
		case DialOutStatus.DIALOUT_STATUS_BUSY:
		case DialOutStatus.DIALOUT_STATUS_NOT_AVAILABLE:
		case DialOutStatus.DIALOUT_STATUS_USER_HANGUP:
		case DialOutStatus.DIALOUT_STATUS_OTHER_FAIL:
		case DialOutStatus.DIALOUT_STATUS_TIMEOUT:
			mBtnCall.setEnabled(true);
			mBtnHangup.setEnabled(false);
			break;
		case DialOutStatus.DIALOUT_STATUS_CALLING:
		case DialOutStatus.DIALOUT_STATUS_RINGING:
		case DialOutStatus.DIALOUT_STATUS_ACCEPTED:
		case DialOutStatus.DIALOUT_STATUS_JOIN_SUC:
			mBtnCall.setEnabled(false);
			mBtnHangup.setEnabled(true);
			break;
		case DialOutStatus.DIALOUT_STATUS_ZOOM_START_CANCEL_CALL:
			mBtnCall.setEnabled(false);
			mBtnHangup.setEnabled(false);
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		
		if(zoomSDK.isInitialized()) {
			MeetingService meetingService = zoomSDK.getMeetingService();
			meetingService.removeDialOutListener(this);
		}
		
		super.onDestroy();
	}

}
