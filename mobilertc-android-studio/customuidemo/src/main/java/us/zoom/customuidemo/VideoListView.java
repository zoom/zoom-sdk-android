package us.zoom.customuidemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

import us.zoom.sdk.MobileRTCVideoUnitAspectMode;
import us.zoom.sdk.MobileRTCVideoUnitRenderInfo;
import us.zoom.sdk.MobileRTCVideoView;
import us.zoom.sdk.ZoomSDK;

public class VideoListView extends FrameLayout {

    RecyclerView recyclerView;

    List<Long> inMeetingUserList;

    private Context mContext;

    int scale = 1;

    private static final String TAG = "VideoListView";


    public VideoListView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public VideoListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_video_list, this, false);

        recyclerView = view.findViewById(R.id.recycler_video_list);

        ((DefaultItemAnimator)(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        addView(view);

    }

    public void refreshUserList(List<Long> userList) {
        if(userList != null) {
            inMeetingUserList = userList;
        } else {
            if(inMeetingUserList != null)
                inMeetingUserList.clear();
        }

        if (null == recyclerView.getAdapter()) {
            recyclerView.setAdapter(new VideoAdapter());
        }
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public void addUserList(List<Long> userList) {
        if(inMeetingUserList != null && userList != null) {
            for(Long userId : userList) {
                if(!inMeetingUserList.contains(userId)) {
                    inMeetingUserList.add(userId);
                    if (null == recyclerView.getAdapter()) {
                        recyclerView.setAdapter(new VideoAdapter());
                    }
                    recyclerView.getAdapter().notifyItemInserted(inMeetingUserList.indexOf(userId));
                }
            }
        }
    }

    public void removeUserList(List<Long> userList) {
        if(inMeetingUserList != null && userList != null) {
            for(Long userId : userList) {
                if(inMeetingUserList.contains(userId)) {
                    int index = inMeetingUserList.indexOf(userId);
                    inMeetingUserList.remove(userId);
                    if (null == recyclerView.getAdapter()) {
                        recyclerView.setAdapter(new VideoAdapter());
                    }
                    recyclerView.getAdapter().notifyItemRemoved(index);
                }
            }
        }
    }

    class Holder extends RecyclerView.ViewHolder {

        MobileRTCVideoView rtcVideoView;

        public Holder(View view) {
            super(view);
            rtcVideoView = view.findViewById(R.id.videoViewItem);
            rtcVideoView.setZOrderMediaOverlay(true);//For surfaceview overlay
        }
    }

    class VideoAdapter extends RecyclerView.Adapter<Holder> {

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getItemCount() {
            return null == inMeetingUserList ? 0 : inMeetingUserList.size() * scale;
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {

            Log.i(TAG, "onBindViewHolder position:" + position);

            int index = position % inMeetingUserList.size();

            final long userId = inMeetingUserList.get(index);

            holder.rtcVideoView.setTag(userId);
            holder.rtcVideoView.getVideoViewManager().removeAllVideoUnits();

            final MobileRTCVideoUnitRenderInfo renderInfo = new MobileRTCVideoUnitRenderInfo(0, 0, 100, 100);
            renderInfo.aspect_mode = MobileRTCVideoUnitAspectMode.VIDEO_ASPECT_ORIGINAL;
            renderInfo.is_border_visible = true;

            holder.rtcVideoView.getVideoViewManager().addAttendeeVideoUnit(userId, renderInfo);

        }


        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.i(TAG, "onCreateViewHolder position:" + viewType);
            View view = LayoutInflater.from(mContext).inflate(R.layout.videoview_item, parent, false);
            view.getLayoutParams().width = parent.getWidth()/4;
            Holder holder = new Holder(view);
            return holder;
        }


    }


}
