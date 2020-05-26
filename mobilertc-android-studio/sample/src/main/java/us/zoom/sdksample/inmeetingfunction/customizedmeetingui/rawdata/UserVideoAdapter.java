package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.rawdata;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import us.zoom.rawdatarender.ZoomTextureViewRender;
import us.zoom.sdk.InMeetingUserInfo;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKRawDataType;
import us.zoom.sdk.ZoomSDKVideoResolution;
import us.zoom.sdksample.R;

public class UserVideoAdapter extends RecyclerView.Adapter<UserVideoAdapter.BaseHolder> {

    public interface ItemTapListener {
        void onSingleTap(long userId);
    }

    private ItemTapListener tapListener;

    private List<Long> userList = new ArrayList<>();

    private Context context;

    private long activeAudioUser = -1;

    private long selectedVideoUserId = -1;

    public UserVideoAdapter(Context context) {
        this.context = context;
    }

    public UserVideoAdapter(Context context, ItemTapListener listener) {
        this.context = context;
        tapListener = listener;
    }

    public void setActiveAudioUser(int activeVideoUser) {
        this.activeAudioUser = activeVideoUser;
    }


    public long getSelectedVideoUserId() {
        return selectedVideoUserId;
    }

    public void clear() {
        userList.clear();
        notifyDataSetChanged();
    }


    public void onUserVideoStatusChanged(long userId) {

        int index = userList.indexOf(userId);
        if (index > 0) {
            InMeetingUserInfo userInfo = ZoomSDK.getInstance().getInMeetingService().getUserInfoById(userId);
            InMeetingUserInfo.VideoStatus status = userInfo.getVideoStatus();
            if (!status.isSending()) {
                notifyItemChanged(index, "videoOff");
            }
        }

    }

    public void addAll() {
        userList.clear();
        List<Long> all = ZoomSDK.getInstance().getInMeetingService().getInMeetingUserList();
        List<Long> userList = new ArrayList<>(all.size());
        for (Long userId : all) {
            userList.add(userId);
        }
        onUserJoin(userList);
    }

    public void onUserJoin(List<Long> joinList) {
        for (Long user : joinList) {
            if (!userList.contains(user)) {
                userList.add(user);
                notifyItemInserted(userList.size());
            }
        }
        checkUserList();
    }


    public void onLeave() {
        userList.clear();
        notifyDataSetChanged();
    }

    private void checkUserList() {
        List<Long> all = ZoomSDK.getInstance().getInMeetingService().getInMeetingUserList();
        if (null != all) {
            if (all.size() != userList.size()) {
                userList.clear();
                for (Long userId : all) {
                    userList.add(userId);
                }
                notifyDataSetChanged();
            }
        }
    }

    public void onUserLeave(List<Long> leaveList) {
        if (leaveList.contains(selectedVideoUserId)) {
            selectedVideoUserId = -1;
        }
        for (Long user : leaveList) {
            int index = userList.indexOf(user);
            if (index >= 0) {
                userList.remove(index);
                notifyItemRemoved(index);
            }
        }
        checkUserList();
    }

    public void onUserAudioChange(List<Integer> changeList) {
        for (Integer userId : changeList) {
            int position = userList.indexOf(userId);
            notifyItemChanged(position, "audio");
        }
    }

    @NonNull
    @Override
    public BaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_video, parent, false);
        return new VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseHolder holder, int position) {

        onBindViewHolder(holder, position, null);
    }


    @Override
    public void onViewRecycled(@NonNull BaseHolder holder) {
        super.onViewRecycled(holder);
        VideoHolder viewHolder = (VideoHolder) holder;
        viewHolder.rawDataRender.unSubscribe();
    }

    @Override
    public void onBindViewHolder(@NonNull BaseHolder holder, int position, @NonNull List<Object> payloads) {

        long userId = userList.get(position);
        InMeetingUserInfo info = ZoomSDK.getInstance().getInMeetingService().getUserInfoById(userId);
        VideoHolder viewHolder = (VideoHolder) holder;
        if (null == payloads || payloads.size() == 0 || payloads.contains("video")) {
            subscribeVideo(userId, viewHolder);
        }

        if (null != info) {
            if (!info.getVideoStatus().isSending()) {
                viewHolder.rawDataRender.onVideoStatusChange(false);
            }
            viewHolder.userNameText.setText(info.getUserName());
        }

        if (selectedVideoUserId == userId) {
            viewHolder.itemView.setBackgroundResource(R.drawable.video_active_item_bg);
        } else {
            viewHolder.itemView.setBackgroundResource(R.drawable.video_item_bg);
        }
    }

    private void subscribeVideo(long userId, VideoHolder viewHolder) {
        viewHolder.rawDataRender.unSubscribe();
        viewHolder.rawDataRender.setVideoAspectModel(ZoomTextureViewRender.VideoAspect_Full_Filled);
        viewHolder.rawDataRender.setRawDataResolution(ZoomSDKVideoResolution.VideoResolution_90P);
        viewHolder.rawDataRender.subscribe(userId, ZoomSDKRawDataType.RAW_DATA_TYPE_VIDEO);
        viewHolder.rawDataRender.setTag(userId);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class BaseHolder extends RecyclerView.ViewHolder {
        protected View view;

        BaseHolder(View view) {
            super(view);
            this.view = view;
        }
    }

    class VideoHolder extends BaseHolder {

        RawDataRender rawDataRender;

        View itemView;

        TextView userNameText;


        VideoHolder(View view) {
            super(view);
            itemView = view;
            rawDataRender = view.findViewById(R.id.videoRawdataCanvas);


            rawDataRender.setVisibility(View.VISIBLE);
            rawDataRender.setRawDataResolution(ZoomSDKVideoResolution.VideoResolution_90P);
            rawDataRender.clearImage(true);

            userNameText = view.findViewById(R.id.item_user_name);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != tapListener) {
                        long userId = rawDataRender.getUserId();
                        if (userId <= 0 && null != rawDataRender.getTag()) {
                            userId = (long) rawDataRender.getTag();
                        }
                        if (userId > 0) {
                            if (userId == selectedVideoUserId) {
                                return;
                            }
                            int index = userList.indexOf(userId);
                            int oldActiveIndex = userList.indexOf(selectedVideoUserId);
                            selectedVideoUserId = userId;
                            tapListener.onSingleTap(userId);

                            notifyItemChanged(index, "active");
                            if (oldActiveIndex >= 0) {
                                notifyItemChanged(oldActiveIndex, "active");
                            } else {
//                                notifyDataSetChanged();
                            }

                        } else {
                            notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }
}
