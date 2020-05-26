package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.share;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.AndroidAppUtil;

public class ColorTable extends View {
	public final static int COLOR_ALPHA = 255;
	public final static int COLOR_RED = Color.argb(255, 0xEE, 0x1C, 0x25);
	public final static int COLOR_PINK = Color.argb(255, 0xec, 0x00, 0x8c);
	public final static int COLOR_YELLOW = Color.argb(255, 0xff, 0xff, 0x00);
	public final static int COLOR_GREEN = Color.argb(255, 0x0C, 0xFF, 0x01);
	public final static int COLOR_LIGHTBLUE = Color.argb(255, 0x00, 0xBA, 0xFF);
	public final static int COLOR_DARKBLUE = Color.argb(255, 0x00, 0x00, 0xff);
	public final static int COLOR_DARK_RED = Color.argb(255, 0xbd, 0x10, 0xe1);
	public final static int COLOR_BLACK = Color.argb(255, 0x00, 0x00, 0x00);
	
	private int[] mColors; 
	
	private final static int DFT_COLORS_CIRCLE_SIZE = 26;
	private final static int DFT_COLORS_CIRCLE_SPACE = 5;
	
	private Paint mPaint;

	
	private int circleSize; 
	private int space;
	private Context mContext;
	private IColorChangedListener listner;

	public ColorTable(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public ColorTable(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	private void init() {
		mColors = new int[] {COLOR_RED,  COLOR_PINK, COLOR_YELLOW, COLOR_GREEN, COLOR_LIGHTBLUE, COLOR_DARKBLUE, COLOR_DARK_RED, COLOR_BLACK}; 
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circleSize =  AndroidAppUtil.dip2px(mContext, DFT_COLORS_CIRCLE_SIZE);
		space = AndroidAppUtil.dip2px(mContext, DFT_COLORS_CIRCLE_SPACE);
	}

	protected void onDraw(Canvas canvas) {

		mPaint.setStyle(Paint.Style.FILL);
		if (getWidth() == 0) {
			return;
		}
		
		int circleTotalSize = circleSize * mColors.length;
		if(circleTotalSize <= getWidth()){
			space = (getWidth() - circleTotalSize)/(mColors.length + 1);
		}else{
			int spaceTotalW = space*(mColors.length + 1);
			if(spaceTotalW > getWidth()){
				space = 0;
			}
			circleSize = (getWidth()- space*(mColors.length + 1))/ mColors.length;
		}
		
//		circleSize = (getWidth()-(mColors.length+1)*space)/mColors.length;
		
		int circleRadio = circleSize/2;
		Log.e(VIEW_LOG_TAG, "space is "+space);
		int cx =space+circleRadio;
		int height = getHeight();
		for (int i = 0; i < mColors.length; i++) {
			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setColor(mColors[i]);
			canvas.drawCircle(cx, height/2, circleRadio, mPaint);
			Log.e(VIEW_LOG_TAG, "draw x is "+cx+" draw y is "+height/2);
			cx+=circleSize + space;
		}
	}

	public int getColorByIndex(int index){
		if(index < mColors.length)
			return mColors[index];
		else
			return COLOR_RED;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { 
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP: {
			float x = event.getX();
			int select = (int) ((x*mColors.length)/getWidth());
			if(select>mColors.length-1){
				select = mColors.length-1;
			}
			listner.onColorPicked(this, mColors[select]);
			break;
		}
		default:
			return true;
		}
		return true;
	}

	public void setOnColorChangedListener(IColorChangedListener listner) {
		this.listner = listner;
	}
}
