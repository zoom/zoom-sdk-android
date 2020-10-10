package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.lang.ref.SoftReference;
import java.util.List;

import us.zoom.androidlib.utils.ZmCommonUtils;
import us.zoom.androidlib.utils.ZmOsUtils;
import us.zoom.sdk.InMeetingShareController;
import us.zoom.sdk.MobileRTCVideoUnitRenderInfo;
import us.zoom.sdk.MobileRTCVideoView;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdksample.R;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.MyMeetingActivity;

public class MeetingWindowHelper implements InMeetingShareController.InMeetingShareListener {
    public final static int REQUEST_SYSTEM_ALERT_WINDOW = 1020;

    private boolean mbAddedView = false;

    View windowView;

    int lastX, lastY;

    GestureDetector gestureDetector;

    MobileRTCVideoView mobileRTCVideoView;

    MobileRTCVideoUnitRenderInfo renderInfo;

    private WindowManager mWindowManager;

    private static MeetingWindowHelper instance;

    private SoftReference<Context> refContext;


    private MeetingWindowHelper() {

    }

    public static MeetingWindowHelper getInstance() {
        if (null == instance) {
            synchronized (MeetingWindowHelper.class) {
                if (null == instance) {
                    instance = new MeetingWindowHelper();
                }
            }
        }
        return instance;
    }

    public void showMeetingWindow(final Activity context) {

        ZoomSDK.getInstance().getInMeetingService().getInMeetingShareController().addListener(this);

        List<Long> userList = ZoomSDK.getInstance().getInMeetingService().getInMeetingUserList();
        if (null == userList || userList.size() < 2) {
            //only me
            return;
        }

        if (mbAddedView) {
            windowView.setVisibility(View.VISIBLE);
            addVideoUnit();
            return;
        }
        if(ZmOsUtils.isAtLeastN() && ! Settings.canDrawOverlays(context)){
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.getPackageName()));
            context.startActivityForResult(intent,REQUEST_SYSTEM_ALERT_WINDOW);
        }else{
            showMiniMeetingWindow(context);
        }
    }

    public void onActivityResult(int requestCode, Context context) {
        if(refContext != null && refContext.get() != null && refContext.get() == context){
            switch(requestCode) {
                case REQUEST_SYSTEM_ALERT_WINDOW:
                    if((ZmOsUtils.isAtLeastN() && !Settings.canDrawOverlays(context))){
                        return;
                    }
                    showMiniMeetingWindow(context);
                    break;
            }
        }
    }
    public void removeOverlayListener(){
    }

    private void showMiniMeetingWindow(final Context context){
        refContext = new SoftReference<>(context);
        if (null == mWindowManager) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }

        if (null == windowView) {
            windowView = LayoutInflater.from(context).inflate(R.layout.layout_meeting_window, null);
            mobileRTCVideoView = (MobileRTCVideoView) windowView.findViewById(R.id.active_video_view);
            renderInfo = new MobileRTCVideoUnitRenderInfo(0, 0, 100, 100);
            renderInfo.is_border_visible = true;
            gestureDetector = new GestureDetector(context, new SingleTapConfirm());
            windowView.setOnTouchListener(onTouchListener);
        }

        mWindowManager.addView(windowView, getLayoutParams(context));
        mbAddedView = true;
        addVideoUnit();
    }

    private void addVideoUnit() {
        InMeetingShareController shareController = ZoomSDK.getInstance().getInMeetingService().getInMeetingShareController();
        mobileRTCVideoView.getVideoViewManager().removeAllVideoUnits();
        final long shareUserId = MyMeetingActivity.mCurShareUserId;
        if (shareUserId > 0 && (shareController.isOtherSharing() || shareController.isSharingOut())) {
           mobileRTCVideoView.getVideoViewManager().addShareVideoUnit(shareUserId, renderInfo);
        } else {
            mobileRTCVideoView.getVideoViewManager().addActiveVideoUnit(renderInfo);
        }
        mobileRTCVideoView.onResume();
    }

    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {

            if (null != refContext && null != refContext.get()) {
                hiddenMeetingWindow(false);
                Context context = refContext.get();
                Intent intent = new Intent(context, MyMeetingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                context.startActivity(intent);
            }
            return true;

        }
    }


    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (gestureDetector.onTouchEvent(event)) {
                return true;
            }
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_MOVE:
                    WindowManager.LayoutParams params = (WindowManager.LayoutParams) view.getLayoutParams();

                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;
                    int left = params.x + dx;
                    int top = params.y + dy;

                    params.x = left;
                    params.y = top;
                    mWindowManager.updateViewLayout(windowView, params);
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    break;
            }
            return true;
        }
    };

    private WindowManager.LayoutParams getLayoutParams(Context context) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Settings.canDrawOverlays(context)) {
            lp.type = ZmCommonUtils.getSystemAlertWindowType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        } else {
            lp.type = ZmCommonUtils.getSystemAlertWindowType(WindowManager.LayoutParams.TYPE_TOAST);
        }
        lp.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        int width = mWindowManager.getDefaultDisplay().getWidth();
        int height = mWindowManager.getDefaultDisplay().getHeight();
        lp.format = PixelFormat.RGBA_8888;
        windowView.measure(-1, -1);
        lp.x = width - windowView.getMeasuredWidth() - 40;
        lp.y = 80;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.TOP | Gravity.LEFT;
        return lp;
    }


    public void hiddenMeetingWindow(boolean destroy) {
        if (null == windowView || null == mWindowManager || null == mobileRTCVideoView) {
            return;
        }
        mobileRTCVideoView.getVideoViewManager().removeAllVideoUnits();
        if (!destroy) {
            windowView.setVisibility(View.GONE);
        } else {
            try {
                mWindowManager.removeView(windowView);
            } catch (Exception e) {
            }
            mbAddedView = false;
            windowView = null;
            mobileRTCVideoView = null;
        }
//        ZoomSDK.getInstance().getInMeetingService().getInMeetingShareController().removeListener(this);
    }


    @Override
    public void onShareActiveUser(long userId) {
        if (mbAddedView && null != mobileRTCVideoView) {
            if (userId < 0) {
                mobileRTCVideoView.getVideoViewManager().removeAllVideoUnits();
                mobileRTCVideoView.getVideoViewManager().addActiveVideoUnit(renderInfo);
            } else {
                mobileRTCVideoView.getVideoViewManager().removeAllVideoUnits();
                MobileRTCVideoUnitRenderInfo renderInfo = new MobileRTCVideoUnitRenderInfo(0, 0, 100, 100);
                mobileRTCVideoView.getVideoViewManager().addShareVideoUnit(userId, renderInfo);
            }
        }
    }

    @Override
    public void onShareUserReceivingStatus(long userId) {

        Log.d("MeetingWindowHelper","userId:"+userId);

    }
}
