package us.zoom.loginexample;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import us.zoom.androidlib.util.TimeZoneUtil;
import us.zoom.sdk.MeetingItem;
import us.zoom.sdk.PreMeetingService;
import us.zoom.sdk.PreMeetingServiceListener;
import us.zoom.sdk.ScheduleMeetingError;
import us.zoom.sdk.ZoomSDK;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduleMeetingExampleActivity extends Activity implements PreMeetingServiceListener, OnClickListener{
	
	private Button mBtnSchedule;
	
	private EditText mEdtTopic;
	private EditText mEdtPassword;
	private TextView mTxtDate;
	private TextView mTxtTimeFrom;
	private TextView mTxtTimeTo;
	private CheckBox mChkEnableJBH;
	private CheckBox mChkHostVideo;
	private CheckBox mChkAttendeeVideo;
	private CheckBox mChkUsePMI;
	private TextView mTxtTimeZoneName;
	
	private Calendar mDateFrom;
	private Calendar mDateTo;
	private String mTimeZoneId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.schedule_meeting);
		
		mBtnSchedule = (Button)findViewById(R.id.btnSchedule);
		mBtnSchedule.setOnClickListener(this);
		
		mEdtTopic = (EditText)findViewById(R.id.edtTopic);
		mEdtPassword = (EditText)findViewById(R.id.edtPassword);
		mChkEnableJBH = (CheckBox)findViewById(R.id.chkEnableJBH);
		mChkHostVideo = (CheckBox)findViewById(R.id.chkHostVideo);
		mChkAttendeeVideo = (CheckBox)findViewById(R.id.chkAttendeeVideo);
		mChkUsePMI = (CheckBox)findViewById(R.id.chkUsePMI);
		
		mTxtDate = (TextView)findViewById(R.id.txtDate);
		mTxtTimeFrom = (TextView)findViewById(R.id.txtTimeFrom);
		mTxtTimeTo = (TextView)findViewById(R.id.txtTimeTo);
		mTxtTimeZoneName = (TextView)findViewById(R.id.txtTimeZone);	
		
		initDateAndTime();
	}
	
	private void initDateAndTime(){
		mTimeZoneId = TimeZone.getDefault().getID();
		mTxtTimeZoneName.setText(TimeZoneUtil.getFullName(mTimeZoneId));
		
		Date timeFrom = new Date(System.currentTimeMillis() + 3600 * 1000);
		Date timeTo = new Date(System.currentTimeMillis() + 7200 * 1000);
		
		mDateFrom = Calendar.getInstance();
		mDateFrom.setTime(timeFrom);
		mDateFrom.set(Calendar.MINUTE, 0);
		mDateFrom.set(Calendar.SECOND, 0);
		
		mDateTo = Calendar.getInstance();
		mDateTo.setTime(timeTo);
		mDateTo.set(Calendar.MINUTE, 0);
		mDateTo.set(Calendar.SECOND, 0);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(mDateFrom.getTime());
		
		mTxtDate.setText(dateStr);
		if(mDateFrom.get(Calendar.MINUTE) < 10) {
			mTxtTimeFrom.setText(mDateFrom.get(Calendar.HOUR_OF_DAY) + ":0" + mDateFrom.get(Calendar.MINUTE));
		} else {
			mTxtTimeFrom.setText(mDateFrom.get(Calendar.HOUR_OF_DAY) + ":" + mDateFrom.get(Calendar.MINUTE));
		}
		if(mDateFrom.get(Calendar.MINUTE) < 10) {
			mTxtTimeTo.setText(mDateTo.get(Calendar.HOUR_OF_DAY) + ":0" + mDateTo.get(Calendar.MINUTE));
		} else {
			mTxtTimeTo.setText(mDateTo.get(Calendar.HOUR_OF_DAY) + ":" + mDateTo.get(Calendar.MINUTE));
		}
	}
	
	@Override
	public void onClick(View arg0) {
		if(arg0.getId() == R.id.btnSchedule) {
			onClickSchedule();	
		}
	}

	private void onClickSchedule() {
		String topic = mEdtTopic.getText().toString().trim();
		if (topic.length() == 0) {
			Toast.makeText(this, "Topic can not be empty", Toast.LENGTH_LONG).show();
			return;
		}
		
		String password = mEdtPassword.getText().toString().trim();
		
		mBtnSchedule.setEnabled(false);
		MeetingItem meetingItem = new MeetingItem();
		
		meetingItem.setMeetingTopic(topic);
		meetingItem.setStartTime(getBeginTime().getTime());
		meetingItem.setDurationInMinutes(getDurationInMinutes());
		meetingItem.setAsRecurringMeeting(false);
		meetingItem.setCanJoinBeforeHost(mChkEnableJBH.isChecked());
		meetingItem.setPassword(password);
		meetingItem.setHostVideoOff(mChkHostVideo.isChecked());
		meetingItem.setAttendeeVideoOff(mChkAttendeeVideo.isChecked());
		meetingItem.setUsePmiAsMeetingID(mChkUsePMI.isChecked());
		meetingItem.setTimeZoneId(mTimeZoneId);
				
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		
		if(zoomSDK.isInitialized()) {
			PreMeetingService preMeetingService = zoomSDK.getPreMeetingService();
			if(preMeetingService != null) {
				preMeetingService.scheduleMeeting(meetingItem);
				preMeetingService.addListener(this);
			} else {
				Toast.makeText(this, "User not login.", Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}
	
	private Date getBeginTime() {
		Date date = mDateFrom.getTime();
		date.setSeconds(0);
		return date;
	}
	
	private int getDurationInMinutes() {
		return (int)((mDateTo.getTimeInMillis() - mDateFrom.getTimeInMillis())/60000);
	}
	
	@Override
	public void onListMeeting(int result) {
		//No op		
	}

	@Override
	public void onScheduleMeeting(int result) {
		if(result == ScheduleMeetingError.SCHEDULE_MEETING_ERROR_SUCCESS) {
			Toast.makeText(this, "Schedule successfully", Toast.LENGTH_LONG).show();
			finish();
		} else {
			Toast.makeText(this, "Schedule failed result code =" + result, Toast.LENGTH_LONG).show();
			mBtnSchedule.setEnabled(true);
		}
		
	}

	@Override
	public void onUpdateMeeting(int result) {
		//No op	
		
	}

	@Override
	public void onDeleteMeeting(int result) {
		//No op			
	}
}
