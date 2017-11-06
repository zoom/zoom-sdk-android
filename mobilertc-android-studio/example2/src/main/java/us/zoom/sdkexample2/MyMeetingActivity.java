package us.zoom.sdkexample2;

import us.zoom.sdk.MeetingActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

public class MyMeetingActivity extends MeetingActivity {
	
	private Button btnLeaveZoomMeeting;
	private Button btnSwitchToNextCamera;
	private Button btnAudio;
	private Button btnParticipants;
	private Button btnShare;
	private Button btnStopShare;
	private Button btnMoreOptions;

	private View viewTabMeeting;
	private Button btnTabWelcome;
	private Button btnTabMeeting;
	private Button btnTabPage2;
	
	@Override
	protected int getLayout() {
		return R.layout.my_meeting_layout;
	}

	@Override
	protected boolean isAlwaysFullScreen() {
		return false;
	}
	
	@Override
	protected boolean isSensorOrientationEnabled() {
		return false;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		disableFullScreenMode();
		
		setupTabs();
		
		btnLeaveZoomMeeting = (Button)findViewById(R.id.btnLeaveZoomMeeting);
		btnSwitchToNextCamera = (Button)findViewById(R.id.btnSwitchToNextCamera);
		btnAudio = (Button)findViewById(R.id.btnAudio);
		btnParticipants = (Button)findViewById(R.id.btnParticipants);
		btnShare = (Button)findViewById(R.id.btnShare);
		btnStopShare = (Button)findViewById(R.id.btnStopShare);
		btnMoreOptions = (Button)findViewById(R.id.btnMoreOptions);

		btnLeaveZoomMeeting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showLeaveDialog();
			}
		});
		
		btnSwitchToNextCamera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switchToNextCamera();
			}
		});
		
		btnAudio.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//doAudioAction();
				if(!isAudioConnected()) {
					connectVoIP();
				} else {
					muteAudio(!isAudioMuted());
				}
			}
		});
		
		btnParticipants.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showParticipants();
			}
		});
		
		btnShare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showShareOptions();
			}
		});
		
		btnStopShare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				stopShare();
			}
		});
		
		btnMoreOptions.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showMoreOptions();
			}
		});

	}

	@Override
	public void onConfigurationChanged (Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		disableFullScreenMode();
	}

	private void disableFullScreenMode() {
		getWindow().setFlags(~WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams. FLAG_FULLSCREEN);
	}
	
	private void setupTabs() {
		viewTabMeeting = findViewById(R.id.viewTabMeeting);
		btnTabWelcome = (Button)findViewById(R.id.btnTabWelcome);
		btnTabMeeting = (Button)findViewById(R.id.btnTabMeeting);
		btnTabPage2 = (Button)findViewById(R.id.btnTabPage2);
		
		selectTab(MainActivity.TAB_MEETING);
		
		btnTabMeeting.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				selectTab(MainActivity.TAB_MEETING);
			}
		});
		
		btnTabWelcome.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				selectTab(MainActivity.TAB_WELCOME);
			}
		});
		
		btnTabPage2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				selectTab(MainActivity.TAB_PAGE_2);
			}
		});
	}
	
	private void selectTab(int tabId) {
		if(tabId == MainActivity.TAB_MEETING) {
			btnTabWelcome.setSelected(false);
			btnTabPage2.setSelected(false);
			btnTabMeeting.setSelected(true);
		} else {
			switchToMainActivity(tabId);
		}
	}
	
	private void switchToMainActivity(int tab) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.setAction(MainActivity.ACTION_RETURN_FROM_MEETING);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.putExtra(MainActivity.EXTRA_TAB_ID, tab);
		
		startActivity(intent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateButtonsStatus();
		
		// disable animation
		overridePendingTransition(0, 0);
	}

	@Override
	protected void onMeetingConnected() {
		updateButtonsStatus();
	}
	
	@Override
	protected void onSilentModeChanged(boolean inSilentMode) {
		updateButtonsStatus();
	}
	
	@Override
	protected void onStartShare() {
		btnShare.setVisibility(View.GONE);
		btnStopShare.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onStopShare() {
		btnShare.setVisibility(View.VISIBLE);
		btnStopShare.setVisibility(View.GONE);		
	}

	private void updateButtonsStatus() {
		
		boolean enabled = (isMeetingConnected() && !isInSilentMode());
		
		btnSwitchToNextCamera.setEnabled(enabled);
		btnAudio.setEnabled(enabled);
		btnParticipants.setEnabled(enabled);
		btnShare.setEnabled(enabled);
		btnMoreOptions.setEnabled(enabled);
		
		if(isSharingOut()) {
			btnShare.setVisibility(View.GONE);
			btnStopShare.setVisibility(View.VISIBLE);
		} else {
			btnShare.setVisibility(View.VISIBLE);
			btnStopShare.setVisibility(View.GONE);
		}
	}
}
