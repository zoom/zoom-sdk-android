package us.zoom.sdksample.inmeetingfunction.zoommeetingui;

import android.graphics.Bitmap;
import android.os.Bundle;

import us.zoom.sdk.MeetingActivity;

public class CustomZoomUIActivity extends MeetingActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStartShare() {
        super.onStartShare();
    }

    @Override
    protected void onStopShare() {
        super.onStopShare();
    }

    @Override
    protected Bitmap getShareBitmap() {
        return super.getShareBitmap();
    }
}
