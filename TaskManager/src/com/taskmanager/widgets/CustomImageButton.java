package com.taskmanager.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageButton;

public class CustomImageButton extends ImageButton {
	private boolean isPressed;
	
	public CustomImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if(enabled){
			setAlpha(1.0f);
		} else {
			setAlpha(0.7f);
		}
	}
	
	@Override
	protected void drawableStateChanged() { 
		super.drawableStateChanged();
		/**Giving button pressed/dim effect on same image by changing alpha value*/
		try {
			int[] states = getDrawableState();
			StateLoop:
			for(int state : states) {
				if(android.R.attr.state_pressed == state){
					isPressed = true;
					break StateLoop;
				}
				isPressed = false;
			}
		} catch(NullPointerException e) {
			Log.e("drawableStateChanged NullPointerException", e.toString()); 
		}
		if(isPressed){//pressed
			setAlpha(0.7f);
		} else {//un-pressed
			setAlpha(1.0f);
		}
	}//end drawableStateChanged
}
