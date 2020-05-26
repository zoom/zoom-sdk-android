package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.video;

import android.app.Service;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.List;

import us.zoom.sdk.CameraDevice;
import us.zoom.sdk.InMeetingVideoController;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdksample.R;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.adapter.CameraMenuItem;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.adapter.SimpleMenuAdapter;

public class MeetingVideoHelper {


    private InMeetingVideoController mInMeetingVideoController;

    private Context activity;

    private VideoCallBack callBack;

    public interface VideoCallBack {

        boolean requestVideoPermission();

        void showCameraList(PopupWindow popupWindow);

    }

    public MeetingVideoHelper(Context activity, VideoCallBack callBack) {
        this.activity = activity;
        this.callBack = callBack;
        mInMeetingVideoController = ZoomSDK.getInstance().getInMeetingService().getInMeetingVideoController();

    }

    public void checkVideoRotation(Context context) {
        Display display = ((WindowManager) context.getSystemService(Service.WINDOW_SERVICE)).getDefaultDisplay();
        int displayRotation = display.getRotation();
        mInMeetingVideoController.rotateMyVideo(displayRotation);
    }

    public void switchVideo() {
        if (null == callBack || !callBack.requestVideoPermission()) {
            return;
        }
        if (mInMeetingVideoController.isMyVideoMuted()) {
            if (mInMeetingVideoController.canUnmuteMyVideo()) {
                mInMeetingVideoController.muteMyVideo(false);
            }
        } else {
            mInMeetingVideoController.muteMyVideo(true);
        }
    }

    public void switchCamera() {
        if (mInMeetingVideoController.canSwitchCamera()) {
            List<CameraDevice> devices = mInMeetingVideoController.getCameraDeviceList();
            if (devices != null && devices.size() > 1) {
                final SimpleMenuAdapter cameraMenuAdapter = new SimpleMenuAdapter(activity);
                for (CameraDevice device : devices) {
                    cameraMenuAdapter.addItem(new CameraMenuItem(0, device.getDeviceName(), device.getDeviceId()));
                }

                View popupWindowLayout = LayoutInflater.from(activity).inflate(R.layout.popupwindow, null);

                ListView cameraList = (ListView) popupWindowLayout.findViewById(R.id.actionListView);
                final PopupWindow window = new PopupWindow(popupWindowLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.bg_transparent));
                cameraList.setAdapter(cameraMenuAdapter);

                cameraList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        CameraMenuItem item = (CameraMenuItem) cameraMenuAdapter.getItem(position);
                        mInMeetingVideoController.switchCamera(item.getCameraId());
                        window.dismiss();
                    }
                });

                window.setFocusable(true);
                window.setOutsideTouchable(true);
                window.update();
                if (null != callBack) {
                    callBack.showCameraList(window);
                }
            } else {
                mInMeetingVideoController.switchToNextCamera();
            }
        }
    }


}
