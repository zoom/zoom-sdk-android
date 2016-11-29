package us.zoom.loginexample;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import us.zoom.sdk.MeetingItem;
import us.zoom.sdk.PreMeetingService;
import us.zoom.sdk.PreMeetingServiceListener;
import us.zoom.sdk.ZoomSDK;

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
	public void onListMeeting(int result) {
		Log.i(TAG, "onListMeeting, result =" + result);
		mAdapter.clear();
		
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		
		if(zoomSDK.isInitialized()) {
			PreMeetingService preMeetingService = zoomSDK.getPreMeetingService();
			if(preMeetingService != null) {
				int count = preMeetingService.getMeetingCount();
				for(int index = 0; index < count; index++) {
					mAdapter.addItem(preMeetingService.getMeetingItemByIndex(index));
				}
			} 
		}	
		
		mAdapter.notifyDataSetChanged();
		
	}

	@Override
	public void onScheduleMeeting(int result) {
		// No op
		
	}

	@Override
	public void onUpdateMeeting(int result) {
		// No op
		
	}

	@Override
	public void onDeleteMeeting(int result) {
		// No op
	}
	
	private void onClickBtnDelete(MeetingItem item) {
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		
		if(zoomSDK.isInitialized()) {
			PreMeetingService preMeetingService = zoomSDK.getPreMeetingService();
			if(preMeetingService != null) {
				preMeetingService.deleteMeeting(item);;
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
			return item != null ? item.getMeetingNo() : 0;
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
				holder.txtMeetingNo = (TextView)convertView.findViewById(R.id.txtMeetingNo);
				holder.btnDelete = (Button)convertView.findViewById(R.id.btnDelete);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			holder.txtTopic.setText("Topic: " + item.getMeetingTopic());
			Date date=new Date(item.getStartTime());
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			holder.txtTime.setText("Time: " + sdf.format(date));
			holder.txtMeetingNo.setText("Meeting number: " + item.getMeetingNo());
			
			if(item.isPersonalMeeting()) {
				holder.btnDelete.setVisibility(View.GONE);
			} else {
				holder.btnDelete.setVisibility(View.VISIBLE);
				holder.btnDelete.setOnClickListener( new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						onClickBtnDelete(item);
					}
				});
			}
			return convertView;
		}
		
		class ViewHolder {
			public TextView txtTopic;
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

}
