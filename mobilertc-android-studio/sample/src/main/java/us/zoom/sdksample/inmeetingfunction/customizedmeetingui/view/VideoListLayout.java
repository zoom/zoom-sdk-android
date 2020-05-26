package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view;

import android.app.Service;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import us.zoom.sdksample.R;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.adapter.AttenderVideoAdapter;


public class VideoListLayout extends LinearLayout implements View.OnClickListener {

    View indicator;

    RecyclerView videoList;

    public VideoListLayout(Context context) {
        super(context);
    }

    public VideoListLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoListLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
    }

    private void init() {
        indicator = findViewById(R.id.videoList_indicator);
        videoList = findViewById(R.id.videoList);
        indicator.setOnClickListener(this);
        videoList.addOnScrollListener(onScrollListener);
        updateArrow();
        updateOrientation(getResources().getConfiguration().orientation);
    }

    @Override
    public void onClick(View v) {
        if (v == indicator) {
            if (indexOfChild(videoList) < 0) {
                addView(videoList);
            } else {
                removeView(videoList);
            }
            updateArrow();
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) {
            if (indexOfChild(videoList) < 0) {
                addView(videoList);
            }
            updateArrow();
        } else {
            removeView(videoList);
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = newConfig.orientation;
        updateArrow();
        updateOrientation(orientation);
    }

    private void updateOrientation(int orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
            if (Build.VERSION.SDK_INT >= 17) {
                params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            }
            WindowManager windowManager = (WindowManager) getContext().getSystemService(Service.WINDOW_SERVICE);
            int size = windowManager.getDefaultDisplay().getHeight() / 4;
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            params.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
            params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            setOrientation(LinearLayout.HORIZONTAL);
            setLayoutParams(params);
            videoList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            RecyclerView.Adapter adapter = videoList.getAdapter();
            if (adapter instanceof AttenderVideoAdapter) {
                ((AttenderVideoAdapter) adapter).updateSize(size);
            }
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
            if (Build.VERSION.SDK_INT >= 17) {
                params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
                params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }
            params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
//            params.addRule(RelativeLayout.ABOVE, R.id.view_option_bottombar);
            setOrientation(LinearLayout.VERTICAL);
            setLayoutParams(params);
            videoList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            WindowManager windowManager = (WindowManager) getContext().getSystemService(Service.WINDOW_SERVICE);
            int size = windowManager.getDefaultDisplay().getWidth() / 4;
            RecyclerView.Adapter adapter = videoList.getAdapter();
            if (adapter instanceof AttenderVideoAdapter) {
                ((AttenderVideoAdapter) adapter).updateSize(size);
            }
        }
    }

    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int first = linearLayoutManager.findFirstVisibleItemPosition();
                View firstView = linearLayoutManager.findViewByPosition(first);
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    int left = firstView.getLeft();
                    if (left < 0) {
                        int width = firstView.getMeasuredWidth();
                        if (-left >= width / 2) {
                            videoList.smoothScrollBy(width + left, 0);
                        } else {
                            videoList.smoothScrollBy(left, 0);
                        }
                    }
                } else {
                    int top = firstView.getTop();
                    if (top < 0) {
                        int width = firstView.getMeasuredWidth();
                        if (-top >= width / 2) {
                            videoList.smoothScrollBy(0, width + top);
                        } else {
                            videoList.smoothScrollBy(0, top);
                        }
                    }
                }
            }
        }
    };

    void updateArrow() {
        boolean visible = indexOfChild(videoList) > 0;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (visible) {
                indicator.setRotation(0);
            } else {
                indicator.setRotation(180);
            }
        } else {
            if (visible) {
                indicator.setRotation(270);
            } else {
                indicator.setRotation(90);
            }
        }
    }

}
