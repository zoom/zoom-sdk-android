package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.share;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class ToolbarDragView extends RelativeLayout {

	private GestureDetector mGD;
	private ToolbarScrollListener mListener;
	public ToolbarDragView(Context context, AttributeSet attrs,
                           int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public ToolbarDragView(Context context, AttributeSet attrs,
                           int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ToolbarDragView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ToolbarDragView(Context context) {
		super(context);
	}
	
	public void setGestureDetectorListener(ToolbarScrollListener listener){
		mListener = listener;
		if(listener == null){
			mGD = null;
		}else{
			mGD = new GestureDetector(getContext(),listener);
			mGD.setIsLongpressEnabled(false);
		}
	}
	
	public static class ToolbarScrollListener extends SimpleOnGestureListener{
		public void onTouchEventUp(){
			
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mGD != null){
			if(mListener!= null && event.getAction() == MotionEvent.ACTION_UP){
				mListener.onTouchEventUp();
			}
			return mGD.onTouchEvent(event);
		}else{
			return super.onTouchEvent(event);
		}
		
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if(mGD != null){
			
			return mGD.onTouchEvent(event);
		}else{
			return super.onInterceptTouchEvent(event);
		}
	}
}
