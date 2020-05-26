package us.zoom.sdksample.otherfeatures.scheduleforloginuser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import us.zoom.sdk.AccountService;
import us.zoom.sdk.Alternativehost;
import us.zoom.sdk.MeetingItem;
import us.zoom.sdk.PreMeetingService;
import us.zoom.sdk.PreMeetingServiceListener;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdksample.R;

public class PreMeetingExampleActivity extends Activity implements OnClickListener, PreMeetingServiceListener{
	
	private final static String TAG = "ZoomSDKExample";
	
	private ListView mListView;
	private Button mBtnSchedule;
	
	private MeetingsListAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pre_meeting_activity);
		
		mListView = (ListView)findViewById(R.id.meetingsListView);
		mBtnSchedule = (Button)findViewById(R.id.btnSchedule);
		mBtnSchedule.setOnClickListener(this);
		mAdapter = new MeetingsListAdapter(this);
		
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		
		if(zoomSDK.isInitialized()) {
			PreMeetingService preMeetingService = zoomSDK.getPreMeetingService();
			if(preMeetingService != null) {
				preMeetingService.listMeeting();
				preMeetingService.addListener(this);
			} else {
				Toast.makeText(this, "User not login.", Toast.LENGTH_LONG).show();
				finish();
			}
		}
		mListView.setAdapter(mAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

    @Override
    public void onListMeeting(int result, List<Long> meetingList) {
        Log.i(TAG, "onListMeeting, result =" + result);
        mAdapter.clear();

        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        PreMeetingService preMeetingService = zoomSDK.getPreMeetingService();
        if(preMeetingService != null) {
            if (meetingList != null) {
                for (long meetingUniqueId : meetingList) {
                    MeetingItem item = preMeetingService.getMeetingItemByUniqueId(meetingUniqueId);
                    if(item != null) {
                        mAdapter.addItem(item);
                    }
                }
            }
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
	public void onScheduleMeeting(int result, long meetingUniqueId) {
		// No op
		Log.d(TAG,"onScheduleMeeting result:"+result+" meetingUniqueId:"+meetingUniqueId);
	}

    @Override
    public void onUpdateMeeting(int result, long meetingUniqueId) {
	    // No op
		Log.d(TAG,"onUpdateMeeting result:"+result+" meetingUniqueId:"+meetingUniqueId);
    }


	@Override
	public void onDeleteMeeting(int result) {
		// No op
		Log.d(TAG,"onDeleteMeeting result:"+result);
	}
	
	private void onClickBtnDelete(MeetingItem item) {
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		
		if(zoomSDK.isInitialized()) {
			PreMeetingService preMeetingService = zoomSDK.getPreMeetingService();
			if(preMeetingService != null) {
				preMeetingService.deleteMeeting(item.getMeetingUniqueId());
            }
		}
	}
	
	@Override
	protected void onDestroy() {
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		
		if(zoomSDK.isInitialized()) {
			PreMeetingService preMeetingService = zoomSDK.getPreMeetingService();
			if(preMeetingService != null)
				preMeetingService.removeListener(this);
		}
		
		super.onDestroy();
	}
	
	
	class MeetingsListAdapter extends BaseAdapter{
		
		private ArrayList<MeetingItem> mItems = new ArrayList<MeetingItem>();
		private Context mContext;
		
		public MeetingsListAdapter(Context context) {
			mContext = context;
		}

		public void clear() {
			mItems.clear();
		}
		
		public void addItem(MeetingItem item) {
			assert(item != null);
			mItems.add(item);
		}
		
		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public Object getItem(int position) {
			if(position < 0 || position >= getCount())
				return null;
			
			return mItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			MeetingItem item = (MeetingItem)getItem(position);
			return item != null ? item.getMeetingUniqueId() : 0;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final MeetingItem item = (MeetingItem)getItem(position);
			ViewHolder holder = null;
			if(convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.meeting_item, null);				

				holder.txtTopic = (TextView)convertView.findViewById(R.id.txtTopic);
				holder.txtTime = (TextView)convertView.findViewById(R.id.txtTime);
				holder.txtHostName = (TextView)convertView.findViewById(R.id.txtHostName);
				holder.txtMeetingNo = (TextView)convertView.findViewById(R.id.txtMeetingNo);
				holder.btnDelete = (Button)convertView.findViewById(R.id.btnDelete);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}

			holder.txtMeetingNo.setText("Meeting number: " + item.getMeetingNumber());
			if(item.isPersonalMeeting()) {
				holder.btnDelete.setVisibility(View.GONE);
				holder.txtTopic.setText("Personal meeting id(PMI)");
				holder.txtHostName.setVisibility(View.GONE);
				holder.txtTime.setVisibility(View.GONE);
			} else {
				holder.txtTopic.setText("Topic: " + item.getMeetingTopic());
				holder.txtHostName.setText(getHostNameByEmail(item.getScheduleForHostEmail()));
				Date date=new Date(item.getStartTime());
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				holder.txtTime.setText("Time: " + sdf.format(date));
				if(item.isWebinarMeeting()) {
					holder.btnDelete.setVisibility(View.GONE);
				} else {
					holder.btnDelete.setVisibility(View.VISIBLE);
					holder.btnDelete.setOnClickListener( new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							onClickBtnDelete(item);
						}
					});
				}
			}
			return convertView;
		}
		
		class ViewHolder {
			public TextView txtTopic;
			public TextView txtHostName;
			public TextView txtTime;
			public TextView txtMeetingNo;
			public Button btnDelete;
		}
	}


	@Override
	public void onClick(View arg0) {
		if(arg0.getId() == R.id.btnSchedule) {
			onClickSchedule();
		}
	}

	private void onClickSchedule() {
		Intent intent = new Intent(this, ScheduleMeetingExampleActivity.class);
		startActivity(intent);
	}

	private String getHostNameByEmail(String email) {
		AccountService accountService = ZoomSDK.getInstance().getAccountService();
		if(accountService != null) {
			if(email.equals(accountService.getAccountEmail())) {
				return accountService.getAccountName();
			}

			List<Alternativehost> hostList = accountService.getCanScheduleForUsersList();

			if(hostList.size() < 1) return " ";

			for(Alternativehost host : hostList) {
				if(email.equals(host.getEmail())) {
					return host.getFirstName() + " "+ host.getLastName();
				}
			}
		}
		return " ";
	}

}
