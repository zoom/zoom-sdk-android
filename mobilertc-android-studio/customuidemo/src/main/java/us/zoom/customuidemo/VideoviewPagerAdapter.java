package us.zoom.customuidemo;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import us.zoom.sdk.InMeetingUserInfo;
import us.zoom.sdk.InMeetingUserList;
import us.zoom.sdk.MobileRTCVideoUnitRenderInfo;
import us.zoom.sdk.MobileRTCVideoView;
import us.zoom.sdk.MobileRTCVideoViewManager;

public class VideoviewPagerAdapter extends PagerAdapter {
    private InMeetingUserList mUserList;
    private LayoutInflater layoutInflater;

    public VideoviewPagerAdapter(Context context, InMeetingUserList list) {
        super();
        this.mUserList = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mUserList.getUserCount();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public float getPageWidth(int position) {
        return (float) 0.3;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        ((ViewPager) container).removeView(view);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.videoview_item, null);
        MobileRTCVideoView videoView = (MobileRTCVideoView)view.findViewById(R.id.videoViewItem);
        MobileRTCVideoViewManager manager = videoView.getVideoViewManager();
        InMeetingUserInfo userInfo = mUserList.getUserInfoByIndex(position);
        MobileRTCVideoUnitRenderInfo renderInfo = new MobileRTCVideoUnitRenderInfo(0, 0, 100, 100);
        if(manager != null && userInfo != null) {
            manager.addAttendeeVideoUnit(mUserList.getUserInfoByIndex(position).getUserId(), renderInfo);
        }
        container.addView(view);
        return view;
    }
}
