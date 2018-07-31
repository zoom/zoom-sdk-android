package us.zoom.loginexample;

import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitializeListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class WelcomeActivity extends Activity implements Constants, ZoomSDKInitializeListener, ZoomSDKAuthenticationListener ,OnClickListener{

	private final static String TAG = "ZoomSDKExample";
	
	private Button mBtnLogin;
	private View mProgressPanel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ZoomSDK sdk = ZoomSDK.getInstance();
		if(sdk.isLoggedIn()) {
			finish();
			showApiExampleActivity();
			return;
		}
		
		setContentView(R.layout.main);
		
		mBtnLogin = (Button)findViewById(R.id.btnLogin);
		mBtnLogin.setOnClickListener(this);
		mProgressPanel = (View)findViewById(R.id.progressPanel);
		mBtnLogin.setVisibility(View.GONE);
		mProgressPanel.setVisibility(View.GONE);
		
		if(savedInstanceState == null) {
			sdk.initialize(this, APP_KEY, APP_SECRET, WEB_DOMAIN, this);
		} 
	}
	
	@Override
	public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
		Log.i(TAG, "onZoomSDKInitializeResult, errorCode=" + errorCode + ", internalErrorCode=" + internalErrorCode);
		
		if(errorCode != ZoomError.ZOOM_ERROR_SUCCESS) {
			Toast.makeText(this, "Failed to initialize Zoom SDK. Error: " + errorCode + ", internalErrorCode=" + internalErrorCode, Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "Initialize Zoom SDK successfully.", Toast.LENGTH_LONG).show();
			ZoomSDK sdk = ZoomSDK.getInstance();
			if(sdk.tryAutoLoginZoom() == ZoomApiError.ZOOM_API_ERROR_SUCCESS) {
				sdk.addAuthenticationListener(this);
				mBtnLogin.setVisibility(View.GONE);
				mProgressPanel.setVisibility(View.VISIBLE);
			} else {
				mBtnLogin.setVisibility(View.VISIBLE);
				mProgressPanel.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btnLogin) {
			showLoginView();
		}		
	}

	@Override
	public void onZoomSDKLoginResult(long result) {
		if((int)result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
			showApiExampleActivity();
			finish();
		} else {
			mBtnLogin.setVisibility(View.VISIBLE);
			mProgressPanel.setVisibility(View.GONE);
		}
	}

	@Override
	public void onZoomSDKLogoutResult(long result) {
		//No op
		
	}

	@Override
	public void onZoomIdentityExpired() {
		//Zoom identity expired, please re-login;
	}

	private void showLoginView() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}
	
	private void showApiExampleActivity() {
		Intent intent = new Intent(this, WorkEmailUserExampleActivity.class);
		startActivity(intent);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		ZoomSDK sdk = ZoomSDK.getInstance();
		sdk.removeAuthenticationListener(this);

	}
}