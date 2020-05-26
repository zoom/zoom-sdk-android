package us.zoom.sdksample.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdksample.R;
import us.zoom.sdksample.startjoinmeeting.UserLoginCallback;

public class SSOUserLoginActivity extends Activity implements UserLoginCallback.ZoomDemoAuthenticationListener, View.OnClickListener {

	private final static String TAG = "ZoomSDKExample";
    private EditText mEdtSSOToken;
    private Button mBtnSSOLogin;
	private View mProgressPanel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.sso_login_activity);

        mEdtSSOToken = (EditText)findViewById(R.id.edtSSOToken);
        mBtnSSOLogin = (Button)findViewById(R.id.btnLoginWithSSO);
        mBtnSSOLogin.setOnClickListener(this);

		mProgressPanel = (View)findViewById(R.id.progressPanel);
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		UserLoginCallback.getInstance().addListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		UserLoginCallback.getInstance().removeListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btnLoginWithSSO) {
            onClickBtnLoginWithSSO();
        }
	}

	private void onClickBtnLoginWithSSO() {
        String ssoToken = mEdtSSOToken.getText().toString().trim();
        if(ssoToken.length() == 0) {
            Toast.makeText(this, "You need to enter sso token.", Toast.LENGTH_LONG).show();
            return;
        }

        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        if(!(zoomSDK.loginWithSSOToken(ssoToken) == ZoomApiError.ZOOM_API_ERROR_SUCCESS)) {
            Toast.makeText(this, "ZoomSDK has not been initialized successfully or sdk is logging in.", Toast.LENGTH_LONG).show();
        } else {
            mBtnSSOLogin.setVisibility(View.GONE);
            mProgressPanel.setVisibility(View.VISIBLE);
        }
    }
	
	@Override
	public void onZoomSDKLoginResult(long result) {
		if(result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
			Toast.makeText(this, "Login successfully", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this, LoginUserStartJoinMeetingActivity.class);
			startActivity(intent);
			finish();
		} else {
			Toast.makeText(this, "Login failed result code = " + result, Toast.LENGTH_SHORT).show();
		}
		mBtnSSOLogin.setVisibility(View.VISIBLE);
		mProgressPanel.setVisibility(View.GONE);
	}

	@Override
	public void onZoomSDKLogoutResult(long result) {
		if(result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
			Toast.makeText(this, "Logout successfully", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Logout failed result code = " + result, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onZoomIdentityExpired() {
		//Zoom identity expired, please re-login;
	}

	@Override
	public void onZoomAuthIdentityExpired() {

	}
}
