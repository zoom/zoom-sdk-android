package us.zoom.sdkexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitializeListener;

public class WelcomeActivity extends Activity implements Constants, ZoomSDKInitializeListener, ZoomSDKAuthenticationListener ,OnClickListener{

	private final static String TAG = "ZoomSDKExample";
	
	private Button mBtnLogin;
	private Button mBtnWithoutLogin;
	private View mProgressPanel;
	private ZoomSDK mZoomSDK;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        mZoomSDK = ZoomSDK.getInstance();
		if(mZoomSDK.isLoggedIn()) {
			finish();
			showMainActivity();
			return;
		}
		
		setContentView(R.layout.welcome);
		
		mBtnLogin = (Button)findViewById(R.id.btnLogin);
		mBtnLogin.setOnClickListener(this);
		mBtnWithoutLogin = (Button)findViewById(R.id.btnWithoutLogin);
		mBtnWithoutLogin.setOnClickListener(this);
		mProgressPanel = (View)findViewById(R.id.progressPanel);

		mProgressPanel.setVisibility(View.GONE);
		
		if(savedInstanceState == null) {
            mZoomSDK.initialize(this, APP_KEY, APP_SECRET, WEB_DOMAIN, this);
		}

		if(mZoomSDK.isInitialized()) {
			mBtnLogin.setVisibility(View.VISIBLE);
			mBtnWithoutLogin.setVisibility(View.VISIBLE);
		} else {
			mBtnLogin.setVisibility(View.GONE);
			mBtnWithoutLogin.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
		Log.i(TAG, "onZoomSDKInitializeResult, errorCode=" + errorCode + ", internalErrorCode=" + internalErrorCode);
		
		if(errorCode != ZoomError.ZOOM_ERROR_SUCCESS) {
			Toast.makeText(this, "Failed to initialize Zoom SDK. Error: " + errorCode + ", internalErrorCode=" + internalErrorCode, Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "Initialize Zoom SDK successfully.", Toast.LENGTH_LONG).show();
			if(mZoomSDK.tryAutoLoginZoom() == ZoomApiError.ZOOM_API_ERROR_SUCCESS) {
                mZoomSDK.addAuthenticationListener(this);
				mBtnLogin.setVisibility(View.GONE);
				mBtnWithoutLogin.setVisibility(View.GONE);
				mProgressPanel.setVisibility(View.VISIBLE);
			} else {
				mBtnWithoutLogin.setVisibility(View.VISIBLE);
				mBtnLogin.setVisibility(View.VISIBLE);
				mProgressPanel.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btnLogin) {
			showLoginActivity();
		} else if(v.getId() == R.id.btnWithoutLogin) {
			showMainActivity();
		}
	}

	@Override
	public void onZoomSDKLoginResult(long result) {
		if((int)result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
			showMainActivity();
			finish();
		} else {
			mBtnLogin.setVisibility(View.VISIBLE);
			mBtnWithoutLogin.setVisibility(View.VISIBLE);
			mProgressPanel.setVisibility(View.GONE);
		}
	}

	@Override
	public void onZoomSDKLogoutResult(long result) {

	}

	@Override
	public void onZoomIdentityExpired() {
        if(mZoomSDK.isLoggedIn()) {
            mZoomSDK.logoutZoom();
        }
	}

	private void showLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}

	private void showMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
        mZoomSDK.removeAuthenticationListener(this);
	}
}