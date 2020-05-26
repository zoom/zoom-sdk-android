package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.share;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import androidx.fragment.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class RCMouseView extends ImageView {

    private static final String TAG = "RCMouseView";

    public interface MouseListener {
        void onMouseMove(float x, float y);
        void onMouseClick(float x, float y);
    }

    private FragmentActivity mActivity;

    private long mPrevMouseDragTime;

    private Handler mHandler;

    int lastX, lastY;

    int maxWidth, maxHeight;

    private MouseListener listener;

    GestureDetector gestureDetector;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RCMouseView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public RCMouseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public RCMouseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RCMouseView(Context context) {
        super(context);
        init(context);
    }

    public void setListener(MouseListener listener) {
        this.listener = listener;
    }

    private void init(Context context) {
        mActivity = (FragmentActivity) context;
        mHandler = new Handler();

        gestureDetector = new GestureDetector(new RCMouseView.GestureDetectorListener());
    }

    private void moveRCMouse() {
        if (mActivity != null) {
            float x = getLeft() + getWidth() - 10;
            float y = getTop();
            if (null != listener) {
                listener.onMouseMove(x, y);
            }
        }
    }
    public void moveMouse(float rawX, float rawY) {
        View parent = (View)getParent();
        if(parent == null)
            return;

        int location[] = new int[2];
        parent.getLocationOnScreen(location);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.getLayoutParams();
        params.leftMargin = (int) rawX - location[0];
        params.topMargin = (int) rawY - location[1];
        this.setLayoutParams(params);
    }

    public void showRCMouse(boolean show) {
        if (show) {
            int width = ((ViewGroup) getParent()).getWidth();
            int height = ((ViewGroup) getParent()).getHeight();
            maxWidth = width;
            maxHeight = height;
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.getLayoutParams();
            params.topMargin = height / 2;
            params.leftMargin = width / 2;
            this.setLayoutParams(params);
            this.setVisibility(View.VISIBLE);
        } else {
            this.setVisibility(View.GONE);
        }
    }
    class GestureDetectorListener extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            float x = getLeft() + getWidth() - 10;
            float y = getTop();
            if (null != listener) {
                listener.onMouseClick(x, y);
            }
            return true;
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
       gestureDetector.onTouchEvent(event);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE: {
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;
                int left = getLeft() + dx;
                int top = getTop() + dy;
                if (!bound(left, top)) {
                    return true;
                }
                Log.d(TAG, "left :" + left + " top:" + top);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
                params.leftMargin = left;
                params.topMargin = top;
                setLayoutParams(params);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                long currentTime = System.currentTimeMillis();
                if ((currentTime - mPrevMouseDragTime) > 200) {
                    mPrevMouseDragTime = currentTime;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            moveRCMouse();
                        }
                    }, 200);
                }
                return true;
            }
        }
        return  true;
    }

    private boolean bound(int left, int top) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (width + left >= maxWidth || left < 0) {
            return false;
        }

        if (height + top >= maxHeight || top < 0) {
            return false;
        }
        return true;
    }

}
