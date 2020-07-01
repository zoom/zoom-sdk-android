package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import us.zoom.sdk.MobileRTCVideoUnitAspectMode;
import us.zoom.sdk.MobileRTCVideoUnitRenderInfo;
import us.zoom.sdk.MobileRTCVideoView;
import us.zoom.sdksample.R;

public class AttenderVideoAdapter extends RecyclerView.Adapter<AttenderVideoAdapter.ViewHold> {

   public  interface ItemClickListener {
        void onItemClick(View view, int position, long userId);
    }

    List<Long> userList = new ArrayList<>();

    Context context;

    private int itemSize = 200;

    private ItemClickListener listener;

    int selected = -1;

    View selectedView;


    public AttenderVideoAdapter(Context context, int viewWidth, ItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        if (viewWidth > 0) {
            itemSize = (viewWidth - 40) / 4;
        }
    }

    public void updateSize(int size) {
        itemSize = size;
        notifyDataSetChanged();
    }


    @Override
    public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attend, parent, false);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        params.width = itemSize;
        params.height = itemSize;
        view.setLayoutParams(params);

        view.setOnClickListener(onClickListener);

        return new ViewHold(view);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Long userId = (Long) view.getTag();
            if (userId == getSelectedUserId()) {
                return;
            }
            if (null != listener) {
                int position = userList.indexOf(userId);
                if (position >= userList.size()) {
                    return;
                }
                listener.onItemClick(view, position, userId);
                if (null != selectedView) {
                    selectedView.setBackgroundResource(0);
                }
                view.setBackgroundResource(R.drawable.video_bg);
                selectedView = view;
                selected = position;
            }
        }
    };

    public void setUserList(List<Long> userList) {
        this.userList.clear();
        if (null != userList) {
            this.userList.addAll(userList);
        }
    }

    @Override
    public void onBindViewHolder(ViewHold holder, int position) {
        Long userId = userList.get(position);
        holder.videoView.getVideoViewManager().removeAllAttendeeVideoUnit();
        holder.videoView.getVideoViewManager().addAttendeeVideoUnit(userId, holder.renderInfo);
        holder.root.setTag(userId);
        holder.videoView.setTag(position);

        if (position == selected) {
            if (null != selectedView) {
                selectedView.setBackgroundResource(0);
            }
            holder.root.setBackgroundResource(R.drawable.video_bg);
            selectedView = holder.root;
        } else {
            holder.root.setBackgroundResource(0);
        }
    }

    public void addUserList(List<Long> list) {
        for (Long userId : list) {
            if (!userList.contains(userId)) {
                userList.add(userId);
                notifyItemInserted(userList.size());
            }
        }
    }

    public long getSelectedUserId() {
        if (selected >= 0 && selected < userList.size()) {
            return userList.get(selected);
        }
        return -1;
    }

    public void removeUserList(List<Long> list) {
        if(null==list)
        {
            return;
        }
        for (Long userId : list) {
            if (userList.indexOf(userId) >= 0) {
                int index = userList.indexOf(userId);
                userList.remove(index);
                if (index == selected) {
                    selected = 0;
                    notifyItemChanged(selected);
                }
                notifyItemRemoved(index);
            }
        }
    }

    @Override
    public int getItemCount() {
        return null == userList ? 0 : userList.size();
    }

    class ViewHold extends RecyclerView.ViewHolder {

        View root;
        MobileRTCVideoView videoView;
        MobileRTCVideoUnitRenderInfo renderInfo;

        ViewHold(View view) {
            super(view);
            root = view;
            videoView = view.findViewById(R.id.item_videoView);
            videoView.setZOrderMediaOverlay(true);
            renderInfo = new MobileRTCVideoUnitRenderInfo(0, 0, 100, 100);
            renderInfo.aspect_mode = MobileRTCVideoUnitAspectMode.VIDEO_ASPECT_PAN_AND_SCAN;
            renderInfo.is_border_visible = true;
//            renderInfo.aspect_mode = MobileRTCVideoUnitAspectMode.VIDEO_ASPECT_FULL_FILLED;
        }

    }
}
