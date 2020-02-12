package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.share;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.util.Random;

import us.zoom.sdk.InMeetingAnnotationController;
import us.zoom.sdk.InMeetingAnnotationController.AnnotationToolType;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdksample.R;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.AndroidAppUtil;

public class AnnotateToolbar extends FrameLayout implements IColorChangedListener, View.OnClickListener{
	private ToolbarDragView mView;
	private ImageView mCloseBtn;
	private ImageView mAnnotateBtn;
	private View mToolbars;

	private int mLineWidth = 2; //dp

	private ColorTable mColorTable;
	private TextView txtLineWidth;
	private PopupWindow mColorTableView;
	private PopupWindow mSaveTableView;
	private SeekBar mLineWidthSeekBar;

	private ImageView mSpotlight;
	private ImageView mHighlight;
	private ImageView mPen;
	private ImageView mErase;
	private ImageView mArrow;
	private ImageView mClear;

	private View mColorIndicator;
	private ColorSelectedImage mColorImage;

	private final static int COLOR_SIZE_DEFAULT = 25;
	private final static int COLOR_SIZE_PRESSED = 33;
	private final static int DEFAULT_FONT_SIZE = 48;

	private final static String TAG = AnnotateToolbar.class.getSimpleName();

	private InMeetingAnnotationController mAnnotationController;

	public AnnotateToolbar(Context context) {
		super(context);
		init(context);
	}

	public AnnotateToolbar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AnnotateToolbar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context){
		mView = (ToolbarDragView)LayoutInflater.from(context).inflate(R.layout.annotatebar, null, false);
		mView.getLayoutParams();
		mView.setGestureDetectorListener( new GuestureListener());
		initAnnotateView(context);
		this.addView(mView);
	}

	private void initAnnotateView(Context context)
	{
		mAnnotateBtn = (ImageView) mView.findViewById(R.id.btnAnnotate);
		mAnnotateBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showAnnoToolbar();
			}
		});
		mCloseBtn = (ImageView) mView.findViewById(R.id.shareEditBtn);
		mCloseBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				closeAnnoToolbar();
			}
		});

		mSpotlight = (ImageView) mView.findViewById(R.id.btnSpotlight);
		mHighlight = (ImageView) mView.findViewById(R.id.btnHighlight);
		mPen = (ImageView) mView.findViewById(R.id.btnPen);
		mErase = (ImageView) mView.findViewById(R.id.btnErase);
		mColorIndicator = mView.findViewById(R.id.btnColorIndicator);

		mColorImage = (ColorSelectedImage)mColorIndicator.findViewById(R.id.colorImage);
		mArrow = (ImageView) mView.findViewById(R.id.btnArrow);
		mClear = (ImageView) mView.findViewById(R.id.btnClear);



		mClear.setVisibility(VISIBLE);

		mArrow.setVisibility(GONE);
		mSpotlight.setVisibility(GONE);

		mSpotlight.setOnClickListener(this);
		mHighlight.setOnClickListener(this);
		mPen.setOnClickListener(this);
		mErase.setOnClickListener(this);
		mColorIndicator.setOnClickListener(this);
		mArrow.setOnClickListener(this);
		mClear.setOnClickListener(this);
		mView.findViewById(R.id.btnRedo).setOnClickListener(this);
		mView.findViewById(R.id.btnUndo).setOnClickListener(this);

		mToolbars = mView.findViewById(R.id.drawingtools);
		mToolbars.setVisibility(GONE);
		
		View contentView = inflate(getContext(), R.layout.annocolorlayout, null);
		mColorTableView = new PopupWindow(contentView, LayoutParams.MATCH_PARENT, AndroidAppUtil.dip2px(context, 100));
		mColorTable = (ColorTable) contentView.findViewById(R.id.colorTable);
		txtLineWidth = (TextView)contentView.findViewById(R.id.txtLineWidth);
		mColorTableView.setBackgroundDrawable(getResources().getDrawable(R.drawable.zm_transparent));
		mColorTableView.setContentView(contentView);
		mColorTableView.setFocusable(true);
		mColorTableView.setOutsideTouchable(true);
		mColorTable.setOnColorChangedListener(this);

		mAnnotationController = ZoomSDK.getInstance().getInMeetingService().getInMeetingAnnotationController();
		mLineWidthSeekBar = (SeekBar) contentView.findViewById(R.id.seekbar);
		mLineWidthSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				mAnnotationController.setToolWidth(mLineWidth);
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {

			}

			@Override
			public void onProgressChanged(SeekBar arg0, int value, boolean arg2) {
				mLineWidth = value > 0 ? value : 1;
				updateLineWidthPromt();
			}
		});

		updateSelection(mPen);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	public boolean isAnnotationStarted()
	{
		return  getVisibility()==VISIBLE&& mToolbars.getVisibility()==VISIBLE;
	}


	private void startAnnotation()
	{

		mAnnotationController.startAnnotation();

		mLineWidth = 2;

		setRandomColor();
		mAnnotationController.setToolType(AnnotationToolType.ANNO_TOOL_TYPE_PEN);
		updateSelection(mPen);
		mArrow.setVisibility(mAnnotationController.isPresenter() ? GONE : VISIBLE);
		mSpotlight.setVisibility(mAnnotationController.isPresenter() ? VISIBLE : GONE);
	}

	public void stopAnnotation() {

		if(mAnnotationController == null) return;

		mAnnotationController.stopAnnotation();

		mAnnotationController.setToolType(AnnotationToolType.ANNO_TOOL_TYPE_PEN);
		updateSelection(mPen);
		if(null != mColorTableView && mColorTableView.isShowing())
			mColorTableView.dismiss();

		if(null != mSaveTableView && mSaveTableView.isShowing())
			mSaveTableView.dismiss();
	}
	
	private void updateSelection(View v) {
		if(null == v)
			return;

		mSpotlight.setSelected(false);
		mHighlight.setSelected(false);
		mPen.setSelected(false);
		mErase.setSelected(false);
		mArrow.setSelected(false);
		v.setSelected(true);
	}
	
	//set annoTool
	@Override  
	public void onClick(View v) 
	{
		if(mAnnotationController == null) return;

		if (v == mSpotlight)
		{
			mAnnotationController.setToolType(AnnotationToolType.ANNO_TOOL_TYPE_SPOTLIGHT);
		} 
		else if (v == mPen) 
		{
			mAnnotationController.setToolType(AnnotationToolType.ANNO_TOOL_TYPE_PEN);
		} 
		else if (v == mHighlight)
		{
			mAnnotationController.setToolType(AnnotationToolType.ANNO_TOOL_TYPE_HIGHLIGHTER);
		} 
		else if (v == mErase) 
		{
			mAnnotationController.setToolType(AnnotationToolType.ANNO_TOOL_TYPE_ERASER);
		} else if(v == mArrow)
		{
			mAnnotationController.setToolType(AnnotationToolType.ANNO_TOOL_TYPE_AUTO_ARROW2);
		}
		else if (v == mColorIndicator) 
		{
			if (mColorTableView.isShowing())
			{
				mColorTableView.dismiss();
			}
			else 
			{
				mColorTableView.showAsDropDown(mToolbars);
				updateLineWidthPromt();
			}
			return;
		}else if(v.getId()==R.id.btnUndo){
			mAnnotationController.undo();
		}else if(v.getId()==R.id.btnRedo){
			mAnnotationController.redo();
		}

		else if(v == mClear)
		{
			mAnnotationController.clear();
			return;
		}

		updateSelection(v);
	}
	
	private void updateLineWidthPromt()
	{
		if (mColorTableView.isShowing())
		{
			mLineWidthSeekBar.setProgress(mLineWidth);
			txtLineWidth.setText(String.valueOf(mLineWidth));
		}
	}

	@Override
	public void onColorChanged(View view, int newColor) {
	}

	@Override
	public void onColorPicked(View view, int newColor) {
		if(mAnnotationController != null)
			mAnnotationController.setToolColor(newColor);
		mColorImage.setColor(newColor);
	}

	private void closeAnnoToolbar()
	{
		if(null != mToolbars) {
			mToolbars.setVisibility(GONE);
			mAnnotateBtn.setVisibility(VISIBLE);
			stopAnnotation();
		}
	}

	private void showAnnoToolbar()
	{
		if(null != mToolbars) {
			mToolbars.setVisibility(VISIBLE);
			mAnnotateBtn.setVisibility(GONE);
			startAnnotation();
		}
	}

    private void setRandomColor()
	{
		Random rand = new Random();
		int index = rand.nextInt(9);
		int color = ColorTable.COLOR_RED;
		if(null != mColorTable){
			color = mColorTable.getColorByIndex(index);
		}
		if(mAnnotationController != null)
			mAnnotationController.setToolColor(color);
		mColorImage.setColor(color);
	}

	@Override
	protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
		if(visibility != VISIBLE) {
			closeAnnoToolbar();
		}
		super.onVisibilityChanged(changedView, visibility);
	}

	private class GuestureListener extends ToolbarDragView.ToolbarScrollListener {

		float mLastRawX = -1f;
		float mLastRawY = -1f;

		public GuestureListener(){
		}

		@Override
		public void onTouchEventUp() {
			//if a click event happen, the event is handle by child view, the method is not called,
			//so dragFinish should be also called in click listener
			mLastRawX = -1f;
			mLastRawY = -1f;
		}

		@SuppressWarnings("deprecation")
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
								float distanceX, float distanceY) {
			if(mView == null || mView.getParent() == null ){
				return true;
			}

			//hide color Table
			if(mColorTableView==null)
					return true;
			if(mColorTableView.isShowing())
			{
				mColorTableView.dismiss();
			}

			int dx , dy ;
			if((int)mLastRawX == -1 || (int) mLastRawY == -1 ){
				dx = (int) (e2.getRawX() - e1.getRawX());
				dy = (int) (e2.getRawY() - e1.getRawY());
			}else{
				dx = (int) (e2.getRawX() - mLastRawX);
				dy = (int) (e2.getRawY() - mLastRawY);
			}

			mLastRawX = e2.getRawX();
			mLastRawY = e2.getRawY();
			int width = AnnotateToolbar.this.getWidth();
			int height = AnnotateToolbar.this.getHeight();
			int top = AnnotateToolbar.this.getTop() + dy;
			int left = AnnotateToolbar.this.getLeft() + dx;
			//screen maybe rotate any time

			if(left < 0){
				left = 0;
			}

			if(left + width > AndroidAppUtil.getDisplayWidth(getContext())){
				left = AndroidAppUtil.getDisplayWidth(getContext()) - width;
			}

			if(top < 0){
				top = 0;
			}
			if(top + height > AndroidAppUtil.getDisplayHeight(getContext())){
				//out of top
				top = AndroidAppUtil.getDisplayHeight(getContext()) - height;
			}
			AnnotateToolbar.this.layout(left, top, left+width, top+height);
			return true;
		}
	}

}
