package us.zoom.sdksample.otherfeatures.scheduleforloginuser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import us.zoom.androidlib.util.TimeZoneUtil;
import us.zoom.sdk.AccountService;
import us.zoom.sdk.Alternativehost;
import us.zoom.sdk.MeetingItem;
import us.zoom.sdk.MobileRTCDialinCountry;
import us.zoom.sdk.PreMeetingError;
import us.zoom.sdk.PreMeetingService;
import us.zoom.sdk.PreMeetingServiceListener;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdksample.R;

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

    private EditText mEdtSpecifiedDomains;

    private CheckBox mChkHostInChina;
    private View mOptionHostInChina;

    private CheckBox mChkScheduleFor;
    private View mOptionScheduleFor;
    private Spinner mSpDwonScheduleFor;

    private View mPanelAutoRecord;
    private CheckBox mChkAutoRecord;
    private CheckBox mChkLocalRecord;
    private CheckBox mChkCloudRecord;
    private View mOptionLocalRecord;
    private View mOptionCloudRecord;
    private View mPanelSwitchRocord;

    private Calendar mDateFrom;
    private Calendar mDateTo;
    private String mTimeZoneId;


    private TextView selectedCountryList;
    private ScheduleForHostAdapter mAlterNativeHostdapter;
    private String mSelectScheduleForHostEmail = null;

    private View mOptionPublicMeeting;
    private CheckBox mChkPublicMeeting;

    private View mOptionLanguageInterpretation;
    private CheckBox mChkLanguageInterpretation;


    private View mOptionEnableWaitingRoom;
    private CheckBox mChkWaitingRoom;

    private EditText mEditAlternativeHost;

    private PreMeetingService mPreMeetingService = null;

    MobileRTCDialinCountry mCountry;

    private View layoutCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.schedule_meeting);

        mBtnSchedule = (Button) findViewById(R.id.btnSchedule);
        mBtnSchedule.setOnClickListener(this);

        mEdtTopic = (EditText) findViewById(R.id.edtTopic);
        mEdtPassword = (EditText) findViewById(R.id.edtPassword);
        mChkEnableJBH = (CheckBox) findViewById(R.id.chkEnableJBH);
        mChkHostVideo = (CheckBox) findViewById(R.id.chkHostVideo);
        mChkAttendeeVideo = (CheckBox) findViewById(R.id.chkAttendeeVideo);

        mChkVoip = (CheckBox) findViewById(R.id.chkVoip);
        mChkTelephony = (CheckBox) findViewById(R.id.chkTelephony);
        mChk3rdPartyAudio = (CheckBox) findViewById(R.id.chk3rdPartyAudio);
        mEdt3rdPartyAudio = (EditText) findViewById(R.id.edt3rdPartyAudio);

        mOptionTelephony = findViewById(R.id.optionTelephony);
        mOption3rdPartyAudio = findViewById(R.id.option3rdPartyAudio);

        mChkUsePMI = (CheckBox) findViewById(R.id.chkUsePMI);
        mChkOnlySignJoin = (CheckBox) findViewById(R.id.chkOnlySignCanJoin);
        mOptionOnlySignJoin = findViewById(R.id.optionOnlySignCanJoin);

        mEdtSpecifiedDomains = (EditText) findViewById(R.id.edtSpecifiedDomains);

        mChkHostInChina = (CheckBox) findViewById(R.id.chkHostInChina);
        mOptionHostInChina = findViewById(R.id.optionHostInChina);

        mChkScheduleFor = (CheckBox) findViewById(R.id.chkScheduleFor);
        mOptionScheduleFor = findViewById(R.id.optionScheduleFor);
        mSpDwonScheduleFor = (Spinner) findViewById(R.id.spDwonScheduleFor);

        mOptionEnableWaitingRoom = findViewById(R.id.optionEnableWaitingRoom);
        mChkWaitingRoom = findViewById(R.id.chkWaitingRoom);

        mOptionLanguageInterpretation = findViewById(R.id.optionLanguageInterpretation);
        mChkLanguageInterpretation = findViewById(R.id.chkLanguageInterpretation);

        mEditAlternativeHost = findViewById(R.id.edidAlternativeHost);
      

        mOptionPublicMeeting = findViewById(R.id.optionPublicMeeting);
        mChkPublicMeeting = findViewById(R.id.chkPublicMeeting);


        mPanelAutoRecord = findViewById(R.id.panelAutoRecord);
        mChkAutoRecord = (CheckBox) findViewById(R.id.chkAutoRecord);
        mChkLocalRecord = (CheckBox) findViewById(R.id.chkLocalRecord);
        mChkCloudRecord = (CheckBox) findViewById(R.id.chkCloudRecord);
        mOptionLocalRecord = findViewById(R.id.optionLocalRecord);
        mOptionCloudRecord = findViewById(R.id.optionCloudRecord);
        mPanelSwitchRocord = findViewById(R.id.panelSwitchRocord);

        mTxtDate = (TextView) findViewById(R.id.txtDate);
        mTxtTimeFrom = (TextView) findViewById(R.id.txtTimeFrom);
        mTxtTimeTo = (TextView) findViewById(R.id.txtTimeTo);
        mTxtTimeZoneName = (TextView) findViewById(R.id.txtTimeZone);

        selectedCountryList = findViewById(R.id.selectCountry);
        layoutCountry = findViewById(R.id.layout_country);


        if (ZoomSDK.getInstance().isInitialized()) {
            mAccoutnService = ZoomSDK.getInstance().getAccountService();
            mPreMeetingService = ZoomSDK.getInstance().getPreMeetingService();
            mCountry = ZoomSDK.getInstance().getAccountService().getAvailableDialInCountry();
            if (mAccoutnService == null || mPreMeetingService == null) {
                finish();
            }
        }

        initDateAndTime();
        intUI();
        setCheckBoxListener();
    }

    private void initDateAndTime() {
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
        if (mDateFrom.get(Calendar.MINUTE) < 10) {
            mTxtTimeFrom.setText(mDateFrom.get(Calendar.HOUR_OF_DAY) + ":0" + mDateFrom.get(Calendar.MINUTE));
        } else {
            mTxtTimeFrom.setText(mDateFrom.get(Calendar.HOUR_OF_DAY) + ":" + mDateFrom.get(Calendar.MINUTE));
        }
        if (mDateFrom.get(Calendar.MINUTE) < 10) {
            mTxtTimeTo.setText(mDateTo.get(Calendar.HOUR_OF_DAY) + ":0" + mDateTo.get(Calendar.MINUTE));
        } else {
            mTxtTimeTo.setText(mDateTo.get(Calendar.HOUR_OF_DAY) + ":" + mDateTo.get(Calendar.MINUTE));
        }
    }

    private void intUI() {

        if (mAccoutnService != null) {
            if (mAccoutnService.isTurnOnAttendeeVideoByDefault()) {
                mChkHostVideo.setChecked(false);
            } else {
                mChkHostVideo.setChecked(true);
            }

            if (mAccoutnService.isTurnOnAttendeeVideoByDefault()) {
                mChkAttendeeVideo.setChecked(false);
            } else {
                mChkAttendeeVideo.setChecked(true);
            }

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
                    mEdt3rdPartyAudio.setText(mAccoutnService.getDefaultThirdPartyAudioInfo());
                    break;
            }

            if (!mAccoutnService.isTelephonySupported()) {
                mOptionTelephony.setVisibility(View.GONE);
                mChkTelephony.setChecked(false);
            }

            if (!mAccoutnService.isThirdPartyAudioSupported()) {
                mOption3rdPartyAudio.setVisibility(View.GONE);
                mEdt3rdPartyAudio.setVisibility(View.GONE);
                mChk3rdPartyAudio.setChecked(false);
            }

            if (mAccoutnService.isEnableJoinBeforeHostByDefault()) {
                mChkEnableJBH.setChecked(true);
            } else {
                mChkEnableJBH.setChecked(false);
            }

            if (!mAccoutnService.isSignedInUserMeetingOn()) {
                mOptionOnlySignJoin.setVisibility(View.GONE);
            }

            if (!mAccoutnService.isHostMeetingInChinaFeatureOn()) {
                mOptionHostInChina.setVisibility(View.GONE);
            }

            List<Alternativehost> hostList = mAccoutnService.getCanScheduleForUsersList();
            if (hostList != null && hostList.size() > 0) {
                Alternativehost myself = new Alternativehost();
                myself.setEmail(mAccoutnService.getAccountEmail());
                myself.setFirstName(mAccoutnService.getAccountName());
                myself.setLastName("");
                hostList.add(myself);
                mAlterNativeHostdapter = new ScheduleForHostAdapter(this, hostList);
                mSpDwonScheduleFor.setAdapter(mAlterNativeHostdapter);
                mSpDwonScheduleFor.setOnItemSelectedListener(this);
            } else {
                mOptionScheduleFor.setVisibility(View.GONE);
            }

            if (mAccoutnService.isLocalRecordingSupported() || mAccoutnService.isCloudRecordingSupported()) {
                if (!mAccoutnService.isLocalRecordingSupported()) {
                    mOptionLocalRecord.setVisibility(View.GONE);
                }

                if (!mAccoutnService.isCloudRecordingSupported()) {
                    mOptionCloudRecord.setVisibility(View.GONE);
                }

                switch (mAccoutnService.getDefaultAutoRecordType()) {
                    case AutoRecordType_CloudRecord:
                        mChkAutoRecord.setChecked(true);
                        mChkCloudRecord.setChecked(true);
                        mChkLocalRecord.setChecked(false);
                        break;
                    case AutoRecordType_LocalRecord:
                        mChkAutoRecord.setChecked(true);
                        mChkCloudRecord.setChecked(false);
                        mChkLocalRecord.setChecked(true);
                        break;
                    case AutoRecordType_Disabled:
                        mChkAutoRecord.setChecked(false);
                        mChkCloudRecord.setChecked(false);
                        mChkLocalRecord.setChecked(false);
                        mPanelSwitchRocord.setVisibility(View.GONE);
                        break;

                }
            } else {
                mPanelAutoRecord.setVisibility(View.GONE);
            }
        }
        refreshSelectCountry();
    }

    private void refreshSelectCountry() {
        if (null != mCountry) {

            StringBuilder sb = new StringBuilder();
            for (String str : mCountry.getSelectedCountries()) {
                sb.append(str + " ");
            }
            selectedCountryList.setText(sb.toString());
        }
    }

    private void setCheckBoxListener() {
        mChk3rdPartyAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mChkVoip.setChecked(false);
                    mChkTelephony.setChecked(false);
                    mEdt3rdPartyAudio.setVisibility(View.VISIBLE);
                    mEdt3rdPartyAudio.setText(mAccoutnService.getDefaultThirdPartyAudioInfo());
                } else {
                    mEdt3rdPartyAudio.setVisibility(View.GONE);
                }
            }
        });

        mChkUsePMI.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mOptionLanguageInterpretation.setVisibility(View.GONE);
                    mChkLanguageInterpretation.setChecked(false);
                } else {
                    mOptionLanguageInterpretation.setVisibility(View.VISIBLE);
                    mChkLanguageInterpretation.setChecked(false);
                }
            }
        });

        mChkVoip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mChk3rdPartyAudio.setChecked(false);
                }

            }
        });

        mChkTelephony.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    layoutCountry.setVisibility(View.VISIBLE);
                    mChk3rdPartyAudio.setChecked(false);
                } else {
                    layoutCountry.setVisibility(View.GONE);
                }
            }
        });

        mChkScheduleFor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSpDwonScheduleFor.setVisibility(View.VISIBLE);
                } else {
                    mSpDwonScheduleFor.setVisibility(View.GONE);
                }
            }
        });

        mChkLocalRecord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mChkCloudRecord.setChecked(false);
                }
            }
        });

        mChkCloudRecord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mChkLocalRecord.setChecked(false);
                }
            }
        });

        mChkAutoRecord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mPanelSwitchRocord.setVisibility(View.VISIBLE);
                    if (!mChkCloudRecord.isChecked() && !mChkLocalRecord.isChecked()) {
                        mChkLocalRecord.setChecked(true);
                    }
                } else {
                    mPanelSwitchRocord.setVisibility(View.GONE);
                }

            }
        });

        mChkOnlySignJoin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mEdtSpecifiedDomains.setVisibility(View.VISIBLE);
                    mEdtSpecifiedDomains.setText(domainsListToString(mAccoutnService.getDefaultCanJoinUserSpecifiedDomains()));
                } else {
                    mEdtSpecifiedDomains.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void onClick(View arg0) {
        if (arg0.getId() == R.id.btnSchedule) {
            onClickSchedule();
        }
    }

    public void onClickEditCountry(View view) {
        final ArrayList<String> allCountries = mCountry.getAllCountries();
        ArrayList<String> selectedCountries = mCountry.getSelectedCountries();
        if (null == allCountries || allCountries.size() <= 0) {
            return;
        }

        String items[] = new String[allCountries.size()];
        items = allCountries.toArray(items);

        final List<Integer> selectedIndex = new ArrayList<>();
        boolean selectItems[] = new boolean[allCountries.size()];
        for (int i = 0; i < allCountries.size(); i++) {
            String item = allCountries.get(i);
            if (null != selectedCountries && selectedCountries.indexOf(item) >= 0) {
                selectItems[i] = true;
                selectedIndex.add(Integer.valueOf(i));
            } else {
                selectItems[i] = false;
            }
        }


        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.zm_lbl_edit_dial_in_country_104883)
                .setNegativeButton(R.string.zm_btn_cancel, null).setPositiveButton(R.string.zm_btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ArrayList<String> arrayList = new ArrayList<>();

                        for (Integer index : selectedIndex) {
                            arrayList.add(allCountries.get(index));
                        }
                        mCountry.setSelectedCountries(arrayList);
                        refreshSelectCountry();
                    }
                })
                .setMultiChoiceItems(items, selectItems, new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (!isChecked) {
                            selectedIndex.remove(Integer.valueOf(which));
                        } else {
                            selectedIndex.add(Integer.valueOf(which));
                        }
                    }
                }).create();
        dialog.show();
    }

    private void onClickSchedule() {
        String topic = mEdtTopic.getText().toString().trim();
        if (topic.length() == 0) {
            Toast.makeText(this, "Topic can not be empty", Toast.LENGTH_LONG).show();
            return;
        }

        if (mPreMeetingService == null) return;

        String thirdPartyAudioInfo = mEdt3rdPartyAudio.getText().toString().trim();

        String password = mEdtPassword.getText().toString().trim();

        MeetingItem meetingItem = mPreMeetingService.createScheduleMeetingItem();

        meetingItem.setMeetingTopic(topic);
        meetingItem.setStartTime(getBeginTime().getTime());
        meetingItem.setDurationInMinutes(getDurationInMinutes());
        meetingItem.setCanJoinBeforeHost(mChkEnableJBH.isChecked());
        meetingItem.setPassword(password);
        meetingItem.setHostVideoOff(mChkHostVideo.isChecked());
        meetingItem.setAttendeeVideoOff(mChkAttendeeVideo.isChecked());

        meetingItem.setAvailableDialinCountry(mCountry);

        meetingItem.setEnableMeetingToPublic(mChkPublicMeeting.isChecked());
        meetingItem.setEnableLanguageInterpretation(mChkLanguageInterpretation.isChecked());
        meetingItem.setEnableWaitingRoom(mChkWaitingRoom.isChecked());

        String hosts = mEditAlternativeHost.getText().toString();
        if (!TextUtils.isEmpty(hosts)) {
            String[] emails = hosts.split(",");
            if (null != emails) {
                List<Alternativehost> list=new ArrayList<>(emails.length);
                for (String email : emails) {
                    list.add(new Alternativehost(email.trim()));
                }
                meetingItem.setAlternativeHostList(list);
            }
        }

        if (mChk3rdPartyAudio.isChecked()) {
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

        if (mAccoutnService.isSignedInUserMeetingOn()) {
            meetingItem.setOnlySignUserCanJoin(mChkOnlySignJoin.isChecked());
            if (mAccoutnService.isSpecifiedDomainsCanJoinFeatureOn()) {
                String domainString = mEdtSpecifiedDomains.getText().toString().trim();
                meetingItem.setSpecifiedDomains(domainStringToDomainList(domainString));
            }
        }

        if (mAccoutnService.isHostMeetingInChinaFeatureOn()) {
            meetingItem.setHostInChinaEnabled(mChkHostInChina.isChecked());
        }

        if (mChkScheduleFor.isChecked()) {
            if (mSelectScheduleForHostEmail != null && mSelectScheduleForHostEmail.length() > 0) {
                meetingItem.setScheduleForHostEmail(mSelectScheduleForHostEmail);
            }
        }

        meetingItem.setUsePmiAsMeetingID(mChkUsePMI.isChecked());
        meetingItem.setTimeZoneId(mTimeZoneId);
        //meetingItem.setRepeatType(MeetingItem.RepeatType.EveryMonth);


        if (mChkAutoRecord.isChecked()) {
            if (mChkLocalRecord.isChecked()) {
                meetingItem.setAutoRecordType(MeetingItem.AutoRecordType.AutoRecordType_LocalRecord);
            } else if (mChkCloudRecord.isChecked()) {
                meetingItem.setAutoRecordType(MeetingItem.AutoRecordType.AutoRecordType_CloudRecord);
            } else {
                meetingItem.setAutoRecordType(MeetingItem.AutoRecordType.AutoRecordType_Disabled);
            }
        }

        if (mPreMeetingService != null) {
            mPreMeetingService.addListener(this);
            PreMeetingService.ScheduleOrEditMeetingError error = mPreMeetingService.scheduleMeeting(meetingItem);
            if (error == PreMeetingService.ScheduleOrEditMeetingError.SUCCESS) {
                mBtnSchedule.setEnabled(false);
            } else {
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "User not login.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreMeetingService != null) {
            mPreMeetingService.removeListener(this);
        }
    }

    private Date getBeginTime() {
        Date date = mDateFrom.getTime();
        date.setSeconds(0);
        return date;
    }

    private int getDurationInMinutes() {
        return (int) ((mDateTo.getTimeInMillis() - mDateFrom.getTimeInMillis()) / 60000);
    }

    @Override
    public void onListMeeting(int result, List<Long> meetingList) {
        //No op
    }

    @Override
    public void onScheduleMeeting(int result, long meetingNumber) {
        if (result == PreMeetingError.PreMeetingError_Success) {
            Toast.makeText(this, "Schedule successfully. Meeting's unique id is " + meetingNumber, Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Schedule failed result code =" + result, Toast.LENGTH_LONG).show();
            mBtnSchedule.setEnabled(true);
        }

    }

    @Override
    public void onUpdateMeeting(int result, long meetingUniqueId) {
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

    private String domainsListToString(List<String> domainList) {
        if (domainList != null && domainList.size() > 0) {
            StringBuilder domainStringBuilder = new StringBuilder();
            for (int i = 0; i < domainList.size(); i++) {
                domainStringBuilder.append(domainList.get(i));
                if (i != domainList.size() - 1) {
                    domainStringBuilder.append(";");
                }
            }

            return domainStringBuilder.toString();
        }
        return "";
    }

    private List<String> domainStringToDomainList(String domainString) {
        if (!TextUtils.isEmpty(domainString)) {
            String[] domains = domainString.split(";");

            ArrayList<String> specifiedDomains = new ArrayList<String>();
            for (String domain : domains) {
                if (!TextUtils.isEmpty(domain)) {
                    specifiedDomains.add(domain);
                }
            }
            return specifiedDomains;
        }
        return null;
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
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.alterhost_item, null);

            if (convertView != null) {
                TextView txtHostName = convertView.findViewById(R.id.txtHostName);
                txtHostName.setText(mList.get(position).getFirstName() + " " + mList.get(position).getLastName());
            }
            return convertView;
        }
    }
}
