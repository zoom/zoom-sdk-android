package us.zoom.sdksample.ui.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import us.zoom.androidlib.app.ZMDialogFragment;
import us.zoom.androidlib.utils.ZmUIUtils;

public class BaseDialogFragment extends ZMDialogFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.width = (int) (ZmUIUtils.getMetricWith(getActivity()) * 0.75f);
        params.height = (int)(ZmUIUtils.getMetricHeight(getActivity()) * 0.5f);
        window.setAttributes(params);
    }
}
