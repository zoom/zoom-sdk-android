package us.zoom.sdksample.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdksample.R;
import us.zoom.sdksample.initsdk.InitAuthSDKCallback;
import us.zoom.sdksample.initsdk.InitAuthSDKHelper;
import us.zoom.sdksample.startjoinmeeting.UserLoginCallback;

public class InitAuthSDKActivity extends Activity implements InitAuthSDKCallback, UserLoginCallback.ZoomDemoAuthenticationListener, OnClickListener {

    private final static String TAG = "ZoomSDKExample";

    private Button mBtnEmailLogin;
    private Button mBtnSSOLogin;
    private Button mBtnWithoutLogin;
    private View layoutJoin;
    private View mProgressPanel;
    private EditText numberEdit;
    private EditText nameEdit;
    private ZoomSDK mZoomSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mZoomSDK = ZoomSDK.getInstance();
        if (mZoomSDK.isLoggedIn()) {
            finish();
            showEmailLoginUserStartJoinActivity();
            return;
        }

        setContentView(R.layout.init_auth_sdk);

        mBtnEmailLogin = (Button) findViewById(R.id.btnEmailLogin);
        mBtnEmailLogin.setOnClickListener(this);

        mBtnSSOLogin = (Button) findViewById(R.id.btnSSOLogin);
        mBtnSSOLogin.setOnClickListener(this);

        mBtnWithoutLogin = (Button) findViewById(R.id.btnWithoutLogin);
        mBtnWithoutLogin.setOnClickListener(this);
        mProgressPanel = (View) findViewById(R.id.progressPanel);

        layoutJoin = findViewById(R.id.layout_join);
        numberEdit = findViewById(R.id.edit_join_number);
        nameEdit = findViewById(R.id.edit_join_name);
        mProgressPanel.setVisibility(View.GONE);

        if (savedInstanceState == null) {
            InitAuthSDKHelper.getInstance().initSDK(this, this);
        }

        if (mZoomSDK.isInitialized()) {
            mBtnEmailLogin.setVisibility(View.VISIBLE);
            mBtnSSOLogin.setVisibility(View.VISIBLE);
            mBtnWithoutLogin.setVisibility(View.VISIBLE);
            layoutJoin.setVisibility(View.VISIBLE);
            ZoomSDK.getInstance().getMeetingSettingsHelper().enable720p(true);
        } else {
            mBtnEmailLogin.setVisibility(View.GONE);
            mBtnSSOLogin.setVisibility(View.GONE);
            mBtnWithoutLogin.setVisibility(View.GONE);
            layoutJoin.setVisibility(View.GONE);
        }
    }

    @Override
    public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
        Log.i(TAG, "onZoomSDKInitializeResult, errorCode=" + errorCode + ", internalErrorCode=" + internalErrorCode);

        if (errorCode != ZoomError.ZOOM_ERROR_SUCCESS) {
            Toast.makeText(this, "Failed to initialize Zoom SDK. Error: " + errorCode + ", internalErrorCode=" + internalErrorCode, Toast.LENGTH_LONG).show();
        } else {
            ZoomSDK.getInstance().getMeetingSettingsHelper().enable720p(true);
            Toast.makeText(this, "Initialize Zoom SDK successfully.", Toast.LENGTH_LONG).show();
            if (mZoomSDK.tryAutoLoginZoom() == ZoomApiError.ZOOM_API_ERROR_SUCCESS) {
                UserLoginCallback.getInstance().addListener(this);
                showProgressPanel(true);
            } else {
                showProgressPanel(false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnEmailLogin) {
            showEmailLoginActivity();
        } else if (v.getId() == R.id.btnSSOLogin) {
            showSSOLoginActivity();
        } else if (v.getId() == R.id.btnWithoutLogin) {
            showAPIUserActivity();
        }
    }

    @Override
    public void onZoomSDKLoginResult(long result) {
        if ((int) result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
            showEmailLoginUserStartJoinActivity();
            finish();
        } else {
            showProgressPanel(false);
        }
    }

    @Override
    public void onZoomSDKLogoutResult(long result) {

    }

    @Override
    public void onZoomIdentityExpired() {
        if (mZoomSDK.isLoggedIn()) {
            mZoomSDK.logoutZoom();
        }
    }

    public void onClickJoin(View view) {
        ZoomSDK.getInstance().getMeetingSettingsHelper().setCustomizedMeetingUIEnabled(false);
        String number = numberEdit.getText().toString();
        String name = nameEdit.getText().toString();

        JoinMeetingParams params = new JoinMeetingParams();
        params.meetingNo = number;
        params.displayName = name;
        ZoomSDK.getInstance().getMeetingService().joinMeetingWithParams(this, params);
    }

    private void showProgressPanel(boolean show) {
        if (show) {
            mBtnEmailLogin.setVisibility(View.GONE);
            mBtnSSOLogin.setVisibility(View.GONE);
            mBtnWithoutLogin.setVisibility(View.GONE);
            mProgressPanel.setVisibility(View.VISIBLE);
            layoutJoin.setVisibility(View.GONE);
        } else {
            mBtnWithoutLogin.setVisibility(View.VISIBLE);
            mBtnEmailLogin.setVisibility(View.VISIBLE);
            mBtnSSOLogin.setVisibility(View.VISIBLE);
            mProgressPanel.setVisibility(View.GONE);
            layoutJoin.setVisibility(View.VISIBLE);
        }
    }

    private void showEmailLoginActivity() {
        Intent intent = new Intent(this, EmailUserLoginActivity.class);
        startActivity(intent);
    }

    private void showSSOLoginActivity() {
        Intent intent = new Intent(this, SSOUserLoginActivity.class);
        startActivity(intent);
    }

    private void showAPIUserActivity() {
        Intent intent = new Intent(this, APIUserStartJoinMeetingActivity.class);
        startActivity(intent);
    }

    private void showEmailLoginUserStartJoinActivity() {
        Intent intent = new Intent(this, LoginUserStartJoinMeetingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UserLoginCallback.getInstance().removeListener(this);
    }
}