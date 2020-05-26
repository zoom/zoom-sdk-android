package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.share;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ColorSelectedImage extends ImageView {
	private int color = 0;
	private Paint mPaint;
	
	private void init(){
		this.setWillNotCacheDrawing(true);
		
		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setAntiAlias(true);
	}
	
	public ColorSelectedImage(Context context) {
		super(context);
		init();
	}

	public ColorSelectedImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
		mPaint.setColor(color);
		invalidate();
	}

	public void onDraw(Canvas canvas) {
		if(color == 0) {
			super.onDraw(canvas); 
		} else {
			mPaint.setColor(color);
			int height = this.getHeight();
			int width = this.getWidth(); 
			int radius = Math.min(height / 2, width / 2);
			canvas.drawCircle(width / 2, height / 2, radius-2, mPaint);
		}
	}
}
