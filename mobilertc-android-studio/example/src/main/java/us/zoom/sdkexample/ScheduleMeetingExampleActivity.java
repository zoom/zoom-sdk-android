package us.zoom.sdkexample;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import us.zoom.androidlib.util.TimeZoneUtil;
import us.zoom.sdk.AccountService;
import us.zoom.sdk.Alternativehost;
import us.zoom.sdk.MeetingItem;
import us.zoom.sdk.PreMeetingError;
import us.zoom.sdk.PreMeetingService;
import us.zoom.sdk.PreMeetingServiceListener;
import us.zoom.sdk.ZoomSDK;

public class ScheduleMeetingExampleActivity extends Activity implements PreMeetingServiceListener, OnClickListener, AdapterView.OnItemSelectedListener {
	
	private Button mBtnSchedule;

	private AccountService mAccoutnService;
	
	private EditText mEdtTopic;
	private EditText mEdtPassword;
	private TextView mTxtDate;
	private TextView mTxtTimeFrom;
	private TextView mTxtTimeTo;
	private CheckBox mChkEnableJBH;
	private CheckBox mChkHostVideo;
	private CheckBox mChkAttendeeVideo;
	private CheckBox mChkVoip;
	private CheckBox mChkTelephony;
	private View mOptionTelephony;
	private CheckBox mChk3rdPartyAudio;
	private EditText mEdt3rdPartyAudio;
	private View mOption3rdPartyAudio;
	private CheckBox mChkUsePMI;
	private TextView mTxtTimeZoneName;

	private CheckBox mChkOnlySignJoin;
	private View mOptionOnlySignJoin;

	private CheckBox mChkScheduleFor;
	private View mOptionScheduleFor;
	private Spinner mSpDwonScheduleFor;
	
	private Calendar mDateFrom;
	private Calendar mDateTo;
	private String mTimeZoneId;

	private ScheduleForHostAdapter mAlterNativeHostdapter;
	private String mSelectScheduleForHostEmail = null;

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

		mChkVoip = (CheckBox)findViewById(R.id.chkVoip);
		mChkTelephony = (CheckBox)findViewById(R.id.chkTelephony);
		mChk3rdPartyAudio = (CheckBox)findViewById(R.id.chk3rdPartyAudio);
		mEdt3rdPartyAudio = (EditText)findViewById(R.id.edt3rdPartyAudio);

		mOptionTelephony = findViewById(R.id.optionTelephony);
		mOption3rdPartyAudio = findViewById(R.id.option3rdPartyAudio);

		mChkUsePMI = (CheckBox)findViewById(R.id.chkUsePMI);
		mChkOnlySignJoin = (CheckBox)findViewById(R.id.chkOnlySignCanJoin);
		mOptionOnlySignJoin = findViewById(R.id.optionOnlySignCanJoin);

		mChkScheduleFor = (CheckBox)findViewById(R.id.chkScheduleFor);
		mOptionScheduleFor = findViewById(R.id.optionScheduleFor);
		mSpDwonScheduleFor = (Spinner) findViewById(R.id.spDwonScheduleFor);
		
		mTxtDate = (TextView)findViewById(R.id.txtDate);
		mTxtTimeFrom = (TextView)findViewById(R.id.txtTimeFrom);
		mTxtTimeTo = (TextView)findViewById(R.id.txtTimeTo);
		mTxtTimeZoneName = (TextView)findViewById(R.id.txtTimeZone);

		if(ZoomSDK.getInstance().isInitialized()) {
			mAccoutnService = ZoomSDK.getInstance().getAccountService();

			if(mAccoutnService == null) {
				finish();
			}
		}
		
		initDateAndTime();
		intUI();
		setCheckBoxListener();
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

	private void intUI() {
		if(mAccoutnService != null) {
			switch (mAccoutnService.getDefaultAudioOption()) {
				case AUDIO_TYPE_VOIP:
					mChkVoip.setChecked(true);
					break;
				case AUDIO_TYPE_TELEPHONY:
					mChkTelephony.setChecked(true);
					break;
				case AUDIO_TYPE_VOIP_AND_TELEPHONEY:
					mChkVoip.setChecked(true);
					mChkTelephony.setChecked(true);
					break;
				case AUDIO_TYPE_THIRD_PARTY_AUDIO:
					mChk3rdPartyAudio.setChecked(true);
					mEdt3rdPartyAudio.setText(mAccoutnService.getThirdPartyAudioInfo());
					break;
			}

			if(!mAccoutnService.isTelephonySupported()) {
				mOptionTelephony.setVisibility(View.GONE);
				mChkTelephony.setChecked(false);
			}

			if(!mAccoutnService.isThirdPartyAudioSupported()) {
				mOption3rdPartyAudio.setVisibility(View.GONE);
				mEdt3rdPartyAudio.setVisibility(View.GONE);
				mChk3rdPartyAudio.setChecked(false);
			}

			if(!mAccoutnService.isSignedInUserMeetingOn()) {
				mOptionOnlySignJoin.setVisibility(View.GONE);
			}

			List<Alternativehost> hostList = mAccoutnService.getCanScheduleForUsersList();
			if(hostList != null && hostList.size()>0) {
				Alternativehost myself = new Alternativehost();
				myself.setEmail(mAccoutnService.getAccountEmail());
				myself.setFirstName(mAccoutnService.getAccountName());
				hostList.add(myself);
				mAlterNativeHostdapter = new ScheduleForHostAdapter(this,hostList);
				mSpDwonScheduleFor.setAdapter(mAlterNativeHostdapter);
				mSpDwonScheduleFor.setOnItemSelectedListener(this);
			} else {
				mOptionScheduleFor.setVisibility(View.GONE);
			}
		}
	}

	private void setCheckBoxListener() {
		mChk3rdPartyAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					mChkVoip.setChecked(false);
					mChkTelephony.setChecked(false);
					mEdt3rdPartyAudio.setVisibility(View.VISIBLE);
					mEdt3rdPartyAudio.setText(mAccoutnService.getThirdPartyAudioInfo());
				} else {
					mEdt3rdPartyAudio.setVisibility(View.GONE);
				}
			}
		});

		mChkVoip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					mChk3rdPartyAudio.setChecked(false);
				}
			}
		});

		mChkTelephony.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					mChk3rdPartyAudio.setChecked(false);
				}
			}
		});

		mChkScheduleFor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					mSpDwonScheduleFor.setVisibility(View.VISIBLE);
				} else {
					mSpDwonScheduleFor.setVisibility(View.GONE);
				}
			}
		});

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

		String thirdPartyAudioInfo = mEdt3rdPartyAudio.getText().toString().trim();

		String password = mEdtPassword.getText().toString().trim();

		MeetingItem meetingItem = new MeetingItem();

		meetingItem.setMeetingTopic(topic);
		meetingItem.setStartTime(getBeginTime().getTime());
		meetingItem.setDurationInMinutes(getDurationInMinutes());
		meetingItem.setCanJoinBeforeHost(mChkEnableJBH.isChecked());
		meetingItem.setPassword(password);
		meetingItem.setHostVideoOff(mChkHostVideo.isChecked());
		meetingItem.setAttendeeVideoOff(mChkAttendeeVideo.isChecked());

		if(mChk3rdPartyAudio.isChecked()) {
			if (thirdPartyAudioInfo.length() == 0) {
				Toast.makeText(this, "Third party audio can not be empty", Toast.LENGTH_LONG).show();
				return;
			}
			meetingItem.setAudioType(MeetingItem.AudioType.AUDIO_TYPE_THIRD_PARTY_AUDIO);
			meetingItem.setThirdPartyAudioInfo(thirdPartyAudioInfo);
		} else {
			if (mChkVoip.isChecked() && mChkTelephony.isChecked()) {
				meetingItem.setAudioType(MeetingItem.AudioType.AUDIO_TYPE_VOIP_AND_TELEPHONEY);
			} else {
				if (!mChkVoip.isChecked()) {
					meetingItem.setAudioType(MeetingItem.AudioType.AUDIO_TYPE_TELEPHONY);
				} else {
					meetingItem.setAudioType(MeetingItem.AudioType.AUDIO_TYPE_VOIP);
				}
			}
		}

		if(mAccoutnService.isSignedInUserMeetingOn()) {
			meetingItem.setOnlySignUserCanJoin(mChkOnlySignJoin.isChecked());
		}

		if(mChkScheduleFor.isChecked()) {
			if(mSelectScheduleForHostEmail != null && mSelectScheduleForHostEmail.length()>0) {
				meetingItem.setScheduleForHostEmail(mSelectScheduleForHostEmail);
			}
		}

		meetingItem.setUsePmiAsMeetingID(mChkUsePMI.isChecked());
		meetingItem.setTimeZoneId(mTimeZoneId);
		//meetingItem.setRepeatType(MeetingItem.RepeatType.EveryMonth);
				
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		
		if(zoomSDK.isInitialized()) {
			PreMeetingService preMeetingService = zoomSDK.getPreMeetingService();
			if(preMeetingService != null) {
				PreMeetingService.ScheduleOrEditMeetingError error = preMeetingService.scheduleMeeting(meetingItem);
				if(error == PreMeetingService.ScheduleOrEditMeetingError.SUCCESS) {
                    mBtnSchedule.setEnabled(false);
                    preMeetingService.addListener(this);
                } else {
                    Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
                }
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
	public void onScheduleMeeting(int result, long meetingNumber) {
		if(result == PreMeetingError.PreMeetingError_Success) {
			Toast.makeText(this, "Schedule successfully. Meeting number is " + meetingNumber, Toast.LENGTH_LONG).show();
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

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		mSelectScheduleForHostEmail = mAlterNativeHostdapter.getItem(position).getEmail();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	class ScheduleForHostAdapter extends BaseAdapter {
		private List<Alternativehost> mList;
		private Context mContext;
		public ScheduleForHostAdapter(Context pContext, List<Alternativehost> pList) {
			this.mContext = pContext;
			this.mList = pList;
		}
		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Alternativehost getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater layoutInflater=LayoutInflater.from(mContext);
			convertView=layoutInflater.inflate(R.layout.alterhost_item, null);

			if(convertView != null) {
				TextView txtHostName = convertView.findViewById(R.id.txtHostName);
				txtHostName.setText(mList.get(position).getFirstName()+ " "+ mList.get(position).getLastName());
			}
			return convertView;
		}
	}
}
