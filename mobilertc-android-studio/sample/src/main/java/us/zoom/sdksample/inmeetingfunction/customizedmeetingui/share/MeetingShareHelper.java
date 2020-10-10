package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.share;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import us.zoom.sdk.InMeetingService;
import us.zoom.sdk.InMeetingShareController;
import us.zoom.sdk.MobileRTCSDKError;
import us.zoom.sdk.MobileRTCShareView;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdksample.R;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.AndroidAppUtil;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.MyMeetingActivity;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.adapter.SimpleMenuAdapter;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.adapter.SimpleMenuItem;

public class MeetingShareHelper {

    private final static String TAG = MeetingShareHelper.class.getSimpleName();

    private final int MENU_SHARE_SCREEN = 0;
    private final int MENU_SHARE_IMAGE = 1;
    private final int MENU_SHARE_WEBVIEW = 2;
    private final int MENU_WHITE_BOARD = 3;

    public interface MeetingShareUICallBack {
        void showShareMenu(PopupWindow popupWindow);

        MobileRTCShareView getShareView();
    }

    private InMeetingShareController mInMeetingShareController;

    private InMeetingService mInMeetingService;

    private MeetingShareUICallBack callBack;

    private Activity activity;

    public MeetingShareHelper(Activity activity, MeetingShareUICallBack callBack) {
        mInMeetingShareController = ZoomSDK.getInstance().getInMeetingService().getInMeetingShareController();
        mInMeetingService = ZoomSDK.getInstance().getInMeetingService();
        this.activity = activity;
        this.callBack = callBack;
    }

    public void onClickShare() {
        if (mInMeetingShareController.isOtherSharing()) {
            showOtherSharingTip();
            return;
        }
        if (!mInMeetingShareController.isSharingOut()) {
            showShareActionPopupWindow();
        } else {
            stopShare();
        }
    }

    public boolean isSenderSupportAnnotation(long userId) {
        return mInMeetingShareController.isSenderSupportAnnotation(userId);
    }

    public boolean isSharingScreen() {
        return mInMeetingShareController.isSharingScreen();
    }

    public boolean isOtherSharing() {
        return mInMeetingShareController.isOtherSharing();
    }

    public boolean isSharingOut() {

        return mInMeetingShareController.isSharingOut();
    }

    public MobileRTCSDKError startShareScreenSession(Intent intent) {
        return mInMeetingShareController.startShareScreenSession(intent);
    }


    public void stopShare() {
        mInMeetingShareController.stopShareScreen();
        if (null != callBack) {
            MobileRTCShareView shareView = callBack.getShareView();
            if (shareView != null) {
                mInMeetingShareController.stopShareView();
                shareView.setVisibility(View.GONE);
            }
        }
    }

    public void showOtherSharingTip() {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.alert_other_is_sharing)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();

    }

    public void onShareActiveUser(long currentShareUserId, long userId) {
        if (currentShareUserId > 0 && mInMeetingService.isMyself(currentShareUserId)) {
            if (userId < 0 || !mInMeetingService.isMyself(userId)) { //My share stopped or other start share and stop my share
                mInMeetingShareController.stopShareView();
                mInMeetingShareController.stopShareScreen();
                return;
            }
        }
        if (mInMeetingService.isMyself(userId)) {
            if (mInMeetingShareController.isSharingOut()) {
                if (mInMeetingShareController.isSharingScreen()) {
                    mInMeetingShareController.startShareScreenContent();
                } else {
                    if (null != callBack) {
                        mInMeetingShareController.startShareViewContent(callBack.getShareView());
                    }
                }
            }
        }
    }


    public void showShareActionPopupWindow() {

        final SimpleMenuAdapter menuAdapter = new SimpleMenuAdapter(activity);

        if (Build.VERSION.SDK_INT >= 21) {
            menuAdapter.addItem(new SimpleMenuItem(MENU_SHARE_SCREEN, "Screen"));
        }
        menuAdapter.addItem(new SimpleMenuItem(MENU_SHARE_IMAGE, "Image"));
        menuAdapter.addItem(new SimpleMenuItem(MENU_SHARE_WEBVIEW, "Web url"));
        menuAdapter.addItem(new SimpleMenuItem(MENU_WHITE_BOARD, "WhiteBoard"));

        View popupWindowLayout = LayoutInflater.from(activity).inflate(R.layout.popupwindow, null);

        ListView shareActions = (ListView) popupWindowLayout.findViewById(R.id.actionListView);
        final PopupWindow window = new PopupWindow(popupWindowLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.bg_transparent));
        shareActions.setAdapter(menuAdapter);

        shareActions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mInMeetingShareController.isOtherSharing()) {
                    showOtherSharingTip();
                    window.dismiss();
                    return;
                }

                SimpleMenuItem item = (SimpleMenuItem) menuAdapter.getItem(position);
                if (item.getAction() == MENU_SHARE_WEBVIEW) {
                    startShareWebUrl();
                } else if (item.getAction() == MENU_SHARE_IMAGE) {
                    startShareImage();
                } else if (item.getAction() == MENU_SHARE_SCREEN) {
                    askScreenSharePermission();
                } else if (item.getAction() == MENU_WHITE_BOARD) {
                    startShareWhiteBoard();
                }
                window.dismiss();
            }
        });

        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.update();
        if (null != callBack) {
            callBack.showShareMenu(window);
        }
    }

    @SuppressLint("NewApi")
    protected void askScreenSharePermission() {
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        if (mInMeetingShareController.isOtherSharing()) {
            showOtherSharingTip();
            return;
        }
        MediaProjectionManager mgr = (MediaProjectionManager) activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mgr != null) {
            Intent intent = mgr.createScreenCaptureIntent();
            if (AndroidAppUtil.hasActivityForIntent(activity, intent)) {
                try {
                    activity.startActivityForResult(mgr.createScreenCaptureIntent(), MyMeetingActivity.REQUEST_SHARE_SCREEN_PERMISSION);
                } catch (Exception e) {
                    Log.e(TAG, "askScreenSharePermission failed");
                }
            }
        }
    }

    private void startShareImage() {
        if (mInMeetingShareController.isOtherSharing()) {
            showOtherSharingTip();
            return;
        }
        boolean success = (mInMeetingShareController.startShareViewSession() == MobileRTCSDKError.SDKERR_SUCCESS);
        if (!success) {
            Log.i(TAG, "startShare is failed");
            return;
        }
        if (null == callBack) {
            return;
        }
        MobileRTCShareView shareView = callBack.getShareView();
        shareView.setVisibility(View.VISIBLE);
        shareView.setShareImageBitmap(BitmapFactory.decodeResource(activity.getResources(), R.drawable.zoom_intro1_share));
    }

    private void startShareWebUrl() {
        if (mInMeetingShareController.isOtherSharing()) {
            showOtherSharingTip();
            return;
        }
        boolean success = (mInMeetingShareController.startShareViewSession() == MobileRTCSDKError.SDKERR_SUCCESS);
        if (!success) {
            Log.i(TAG, "startShare is failed");
            return;
        }
        if (null == callBack) {
            return;
        }
        MobileRTCShareView shareView = callBack.getShareView();
        shareView.setVisibility(View.VISIBLE);
        shareView.setShareWebview("www.zoom.us",true);
    }

    private void startShareWhiteBoard() {
        if (mInMeetingShareController.isOtherSharing()) {
            showOtherSharingTip();
            return;
        }
        if (null == callBack) {
            return;
        }
        MobileRTCShareView shareView = callBack.getShareView();

        boolean success = (mInMeetingShareController.startShareViewSession() == MobileRTCSDKError.SDKERR_SUCCESS);
        if (!success) {
            Log.i(TAG, "startShare is failed");
            return;
        }
        shareView.setVisibility(View.VISIBLE);
        shareView.setShareWhiteboard();
    }

}
