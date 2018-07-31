package us.zoom.loginexample;

import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements ZoomSDKAuthenticationListener, View.OnClickListener {

	private final static String TAG = "ZoomSDKExample";
	
	private EditText mEdtUserName;
	private EditText mEdtPassord;
    private EditText mEdtSSOToken;
	private Button mBtnLogin;
    private Button mBtnSSOLogin;
	private View mProgressPanel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.login_activity);
		
		mEdtUserName = (EditText)findViewById(R.id.userName);
		mEdtPassord = (EditText)findViewById(R.id.password);
        mEdtSSOToken = (EditText)findViewById(R.id.edtSSOToken);

		mBtnLogin = (Button)findViewById(R.id.btnLogin);
		mBtnLogin.setOnClickListener(this);
        mBtnSSOLogin = (Button)findViewById(R.id.btnLoginWithSSO);
        mBtnSSOLogin.setOnClickListener(this);

		mProgressPanel = (View)findViewById(R.id.progressPanel);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		if(zoomSDK.isInitialized()) {
			zoomSDK.addAuthenticationListener(this);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		ZoomSDK zoomSDK = ZoomSDK.getInstance();	
		if(zoomSDK.isInitialized()) {
			zoomSDK.removeAuthenticationListener(this);
		}
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btnLogin) {
			onClickBtnLogin();
		} else if(v.getId() == R.id.btnLoginWithSSO) {
            onClickBtnLoginWithSSO();
        }
	}
	
	private void onClickBtnLogin() {
		String userName = mEdtUserName.getText().toString().trim();
		String password = mEdtPassord.getText().toString().trim();
		if(userName.length() == 0 || password.length() == 0) {
			Toast.makeText(this, "You need to enter user name and password.", Toast.LENGTH_LONG).show();
			return;
		}
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		if(!(zoomSDK.loginWithZoom(userName, password) == ZoomApiError.ZOOM_API_ERROR_SUCCESS)) {
			Toast.makeText(this, "ZoomSDK has not been initialized successfully or sdk is logging in.", Toast.LENGTH_LONG).show();
		} else {
			mBtnLogin.setVisibility(View.GONE);
			mProgressPanel.setVisibility(View.VISIBLE);
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
			Intent intent = new Intent(this, WorkEmailUserExampleActivity.class);
			startActivity(intent);
			finish();
		} else {
			Toast.makeText(this, "Login failed result code = " + result, Toast.LENGTH_SHORT).show();
		}
		mBtnLogin.setVisibility(View.VISIBLE);
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
}
