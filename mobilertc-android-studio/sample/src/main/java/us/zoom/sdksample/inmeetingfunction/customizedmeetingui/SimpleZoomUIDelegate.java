package us.zoom.sdksample.inmeetingfunction.customizedmeetingui;

import android.app.Activity;
import android.content.Context;

import java.util.List;

import us.zoom.sdk.MeetingInviteMenuItem;
import us.zoom.sdk.ZoomUIDelegate;

public class SimpleZoomUIDelegate implements ZoomUIDelegate {
    @Override
    public boolean onClickInviteButton(Context context, List<MeetingInviteMenuItem> inviteMenuList) {
        return false;
    }

    @Override
    public void afterMeetingMinimized(Activity activity) {

    }
}
