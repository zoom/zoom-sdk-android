package us.zoom.sdksample.inmeetingfunction.zoommeetingui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.zipow.videobox.share.ScreenShareMgr;
import com.zipow.videobox.view.ToolbarButton;

import us.zoom.sdksample.R;

public class MyToolbarButton extends ToolbarButton {

    public MyToolbarButton(Context context) {
        super(context);
        init();
    }

    public MyToolbarButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyToolbarButton(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        init();
    }

    private void init() {
        if (getId() == R.id.btnSaveAnnotation) {
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bitmap bitmap = ScreenShareMgr.getInstance().getScreenShareBitMap();
                    if(null!=bitmap)
                    {
                        Log.d("bitmap",bitmap.toString());
                    }
                }
            });
        }

    }
}
