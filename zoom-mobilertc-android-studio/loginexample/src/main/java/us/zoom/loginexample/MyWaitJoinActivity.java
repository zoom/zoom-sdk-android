package us.zoom.loginexample;

import us.zoom.androidlib.util.AndroidAppUtil;
import us.zoom.sdk.MeetingError;
import us.zoom.sdk.MeetingEvent;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.ZoomSDK;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MyWaitJoinActivity extends Activity implements View.OnClickListener, MeetingServiceListener{
	
	private final static String TAG = "ZoomSDK";

	private Button mLeave;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.wait_join_activity);
		
		Intent intent = getIntent();
		String topic = intent.getStringExtra(AndroidAppUtil.EXTRA_TOPIC);
		long meetingId = intent.getLongExtra(AndroidAppUtil.EXTRA_MEETING_ID, 0);		
		boolean isRepeat = intent.getBooleanExtra(AndroidAppUtil.EXTRA_IS_REPEAT, false);		
		String date = intent.getStringExtra(AndroidAppUtil.EXTRA_DATE);
		String time = intent.getStringExtra(AndroidAppUtil.EXTRA_TIME);
		
		TextView txtTopic = (TextView)findViewById(R.id.txtTopic);	
		if(topic != null)
			txtTopic.setText("Topic: " + topic);
		
		TextView txtMeetingId = (TextView)findViewById(R.id.txtMeetingId);
		if(meetingId > 0)
			txtMeetingId.setText("Meeting ID: " + meetingId);
		
		TextView txtIsRepeat = (TextView)findViewById(R.id.txtIsRepeat);
		txtIsRepeat.setText("Is Repeat Meeting: " + isRepeat);
		
		TextView txtTime = (TextView)findViewById(R.id.txtTime);
		if(time != null)
			txtTime.setText("Time: " + time);
		
		TextView txtDate = (TextView)findViewById(R.id.txtDate);
		
		if(date != null)
			txtDate.setText("Date: " + date);	
		
		mLeave = (Button)findViewById(R.id.btnLeave);
		mLeave.setOnClickListener(this);
		
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		MeetingService meetingService = zoomSDK.getMeetingService();
		if(meetingService != null) {
			meetingService.addListener(this);
		}
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btnLeave) {			
			onClickLeave();
		}
	}
	
	private void onClickLeave() {
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		MeetingService meetingService = zoomSDK.getMeetingService();
		if(meetingService != null) {
			meetingService.leaveCurrentMeeting(false);
		}
		finish();
	}
	
	@Override
	public void onBackPressed() {
		onClickLeave();
	}
	
	@Override
	public void onMeetingEvent(int meetingEvent, int errorCode,
			int internalErrorCode) {
		
		Log.i(TAG, "onMeetingEvent, meetingEvent=" + meetingEvent + ", errorCode=" + errorCode
				+ ", internalErrorCode=" + internalErrorCode);
		
		if(meetingEvent == MeetingEvent.MEETING_READY_TO_JOIN) {
			finish();
		}
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
}
