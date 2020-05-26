package us.zoom.sdksample.ui;

import us.zoom.sdk.MeetingError;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdksample.initsdk.AuthConstants;
import us.zoom.sdksample.R;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.MyMeetingActivity;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.RawDataMeetingActivity;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.MeetingWindowHelper;
import us.zoom.sdksample.otherfeatures.scheduleforloginuser.PreMeetingExampleActivity;
import us.zoom.sdksample.startjoinmeeting.emailloginuser.EmailLoginUserStartMeetingHelper;
import us.zoom.sdksample.startjoinmeeting.joinmeetingonly.JoinMeetingHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginUserStartJoinMeetingActivity extends Activity implements AuthConstants, MeetingServiceListener, ZoomSDKAuthenticationListener {

    private final static String TAG = "ZoomSDKExample";

    private EditText mEdtMeetingNo;
    private EditText mEdtMeetingPassword;
    private EditText mEdtVanityId;
    private Button mBtnStartInstantMeeting;
    private Button mBtnPreMeeting;
    private Button mBtnLoginOut;
    private Button mBtnSettings;
    private Button mReturnMeeting;
    private final static int STYPE = MeetingService.USER_TYPE_API_USER;
    private final static String DISPLAY_NAME = "ZoomUS SDK";

    private boolean mbPendingStartMeeting = false;
    private boolean isResumed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_user_start_join);

        mEdtMeetingNo = (EditText) findViewById(R.id.edtMeetingNo);
        mEdtVanityId = (EditText) findViewById(R.id.edtVanityUrl);
        mEdtMeetingPassword = (EditText) findViewById(R.id.edtMeetingPassword);
        mBtnStartInstantMeeting = (Button) findViewById(R.id.btnLoginUserStartInstant);
        mBtnPreMeeting = (Button) findViewById(R.id.btnPreMeeting);
        mBtnLoginOut = (Button) findViewById(R.id.btnLogout);
        mBtnSettings = findViewById(R.id.btn_settings);
        mReturnMeeting = findViewById(R.id.btn_return);

        registerListener();
    }

    private void registerListener() {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        zoomSDK.addAuthenticationListener(this);
        MeetingService meetingService = zoomSDK.getMeetingService();
        if (meetingService != null) {
            meetingService.addListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;
        refreshUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResumed = false;
    }

    @Override
    protected void onDestroy() {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        zoomSDK.removeAuthenticationListener(this);//unregister ZoomSDKAuthenticationListener
        if (zoomSDK.isInitialized()) {
            MeetingService meetingService = zoomSDK.getMeetingService();
            meetingService.removeListener(this);//unregister meetingServiceListener
        }
        MeetingWindowHelper.getInstance().removeOverlayListener();

        super.onDestroy();
    }

    public void onClickBtnJoinMeeting(View view) {
        String meetingNo = mEdtMeetingNo.getText().toString().trim();
        String meetingPassword = mEdtMeetingPassword.getText().toString().trim();

        String vanityId = mEdtVanityId.getText().toString().trim();

        if (meetingNo.length() == 0 && vanityId.length() == 0) {
            Toast.makeText(this, "You need to enter a meeting number/ vanity id which you want to join.", Toast.LENGTH_LONG).show();
            return;
        }

        if (meetingNo.length() != 0 && vanityId.length() != 0) {
            Toast.makeText(this, "Both meeting number and vanity id have value,  just set one of them", Toast.LENGTH_LONG).show();
            return;
        }

        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (!zoomSDK.isInitialized()) {
            Toast.makeText(this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
            return;
        }

        MeetingService meetingService = zoomSDK.getMeetingService();


        int ret = -1;
        if (vanityId.length() != 0) {
            ret = JoinMeetingHelper.getInstance().joinMeetingWithVanityId(this, vanityId, meetingPassword);
        } else {
            ret = JoinMeetingHelper.getInstance().joinMeetingWithNumber(this, meetingNo, meetingPassword);
        }

        Log.i(TAG, "onClickBtnJoinMeeting, ret=" + ret);
    }

    public void onClickBtnStartMeeting(View view) {
        String meetingNo = mEdtMeetingNo.getText().toString().trim();
        String vanityId = mEdtVanityId.getText().toString().trim();

        if (meetingNo.length() == 0 && vanityId.length() == 0) {
            Toast.makeText(this, "You need to enter a meeting number/ vanity  which you want to join.", Toast.LENGTH_LONG).show();
            return;
        }

        if (meetingNo.length() != 0 && vanityId.length() != 0) {
            Toast.makeText(this, "Both meeting number and vanity  have value,  just set one of them", Toast.LENGTH_LONG).show();
            return;
        }

        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (!zoomSDK.isInitialized()) {
            Toast.makeText(this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
            return;
        }

        final MeetingService meetingService = zoomSDK.getMeetingService();

        if (meetingService.getMeetingStatus() != MeetingStatus.MEETING_STATUS_IDLE) {
            long lMeetingNo = 0;
            try {
                lMeetingNo = Long.parseLong(meetingNo);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid meeting number: " + meetingNo, Toast.LENGTH_LONG).show();
                return;
            }

            if (meetingService.getCurrentRtcMeetingNumber() == lMeetingNo) {
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
            ret = EmailLoginUserStartMeetingHelper.getInstance().startMeetingWithVanityId(this, vanityId);
        } else {
            ret = EmailLoginUserStartMeetingHelper.getInstance().startMeetingWithNumber(this, meetingNo);
        }
        Log.i(TAG, "onClickBtnStartMeeting, ret=" + ret);
    }

    public void onClickBtnLoginUserStartInstant(View view) {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (!zoomSDK.isInitialized()) {
            Toast.makeText(this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
            return;
        }

        int ret = EmailLoginUserStartMeetingHelper.getInstance().startInstanceMeeting(this);

        Log.i(TAG, "onClickBtnLoginUserStartInstant, ret=" + ret);
    }

    public void onClickReturnMeeting(View view) {
        MeetingWindowHelper.getInstance().hiddenMeetingWindow(true);
        Intent intent = new Intent(this, MyMeetingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void onClickBtnPreMeeting(View view) {
        Intent intent = new Intent(this, PreMeetingExampleActivity.class);
        startActivity(intent);
    }

    public void onClickBtnLogout(View view) {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        if (!zoomSDK.logoutZoom()) {
            Toast.makeText(this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
        }
    }

    public void onClickSettings(View view) {
        Intent intent = new Intent(this, MeetingSettingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onMeetingStatusChanged(MeetingStatus meetingStatus, int errorCode,
                                       int internalErrorCode) {
        Log.i(TAG, "onMeetingStatusChanged, meetingStatus=" + meetingStatus + ", errorCode=" + errorCode
                + ", internalErrorCode=" + internalErrorCode);

        if (meetingStatus == MeetingStatus.MEETING_STATUS_FAILED && errorCode == MeetingError.MEETING_ERROR_CLIENT_INCOMPATIBLE) {
            Toast.makeText(this, "Version of ZoomSDK is too low!", Toast.LENGTH_LONG).show();
        }

        if (mbPendingStartMeeting && meetingStatus == MeetingStatus.MEETING_STATUS_IDLE) {
            mbPendingStartMeeting = false;
            onClickBtnStartMeeting(null);
        }
        if (meetingStatus == MeetingStatus.MEETING_STATUS_CONNECTING) {
            showMeetingUi();
        }
        refreshUI();
    }

    private void refreshUI() {
        if(null==ZoomSDK.getInstance().getMeetingService())
        {
            return;
        }
        MeetingStatus meetingStatus = ZoomSDK.getInstance().getMeetingService().getMeetingStatus();
        if (meetingStatus == MeetingStatus.MEETING_STATUS_CONNECTING || meetingStatus == MeetingStatus.MEETING_STATUS_INMEETING
                || meetingStatus == MeetingStatus.MEETING_STATUS_RECONNECTING) {
            mBtnSettings.setVisibility(View.GONE);
            mReturnMeeting.setVisibility(View.VISIBLE);
        } else {
            mBtnSettings.setVisibility(View.VISIBLE);
            mReturnMeeting.setVisibility(View.GONE);
        }
        if (ZoomSDK.getInstance().getMeetingSettingsHelper().isCustomizedMeetingUIEnabled()) {
            if (meetingStatus == MeetingStatus.MEETING_STATUS_INMEETING && isResumed) {
                MeetingWindowHelper.getInstance().showMeetingWindow(this);
            } else {
                MeetingWindowHelper.getInstance().hiddenMeetingWindow(true);
            }
        }
    }

    private void showMeetingUi() {
        if (ZoomSDK.getInstance().getMeetingSettingsHelper().isCustomizedMeetingUIEnabled()) {
            SharedPreferences sharedPreferences = getSharedPreferences("UI_Setting", Context.MODE_PRIVATE);
            boolean enable = sharedPreferences.getBoolean("enable_rawdata", false);
            Intent intent = null;
            if (!enable) {
                intent = new Intent(this, MyMeetingActivity.class);
            } else {
                intent = new Intent(this, RawDataMeetingActivity.class);
            }
            intent.putExtra("from",1);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            this.startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (ZoomSDK.getInstance().isLoggedIn()) {
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
        if (result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
            Toast.makeText(this, "Logout successfully", Toast.LENGTH_SHORT).show();
            showLoginView();
        } else {
            Toast.makeText(this, "Logout failed result code = " + result, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onZoomIdentityExpired() {
        ZoomSDK.getInstance().logoutZoom();
    }

    @Override
    public void onZoomAuthIdentityExpired() {

    }

    private void showLoginView() {
        mEdtMeetingPassword.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ZoomSDK.getInstance().isInitialized()) {
                    Intent intent = new Intent(LoginUserStartJoinMeetingActivity.this, EmailUserLoginActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        },500);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MeetingWindowHelper.getInstance().onActivityResult(requestCode,this);
    }
}
