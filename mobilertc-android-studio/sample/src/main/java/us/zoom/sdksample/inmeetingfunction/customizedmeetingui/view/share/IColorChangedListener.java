package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.share;

import android.view.View;

public interface IColorChangedListener {


	/**
	 * This method is called when the user changed the color.
	 * 
	 * This works in touch mode, by dragging the along the 
	 * color circle with the finger.
	 */
	void onColorChanged(View view, int newColor);
	
	/**
	 * This method is called when the user clicks the center button.
	 * 
	 * @param view
	 * @param newColor
	 */
	void onColorPicked(View view, int newColor);

}
