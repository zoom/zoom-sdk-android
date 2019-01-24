package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.adapter;

/**
 * Created by Jacky on 2018/1/23.
 */

public class SimpleMenuItem {

    private int mAction = 0;
    private String mLabel;

    public SimpleMenuItem() {

    }

    public SimpleMenuItem(int action, String label) {
        mAction = action;
        mLabel = label;
    }

    @Override
    public String toString() {
        return mLabel;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public int getAction() {
        return mAction;
    }

    public void setAction(int action) {
        mAction = action;
    }

}