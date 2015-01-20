package com.taskmanager.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.taskmanager.app.R;

public class CustomButton extends TextView {
	private boolean isPressed;
	
	private boolean isStatusEnable;
	private Drawable enableIcon;
	private Drawable disableIcon;
	
	public CustomButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CutomButtonStyleable, 0, 0);
		
		try{
			isStatusEnable = array.getBoolean(R.styleable.CutomButtonStyleable_statusEnable, true);
			enableIcon = array.getDrawable(R.styleable.CutomButtonStyleable_enableIcon);
			disableIcon = array.getDrawable(R.styleable.CutomButtonStyleable_disableIcon);
		} finally {
			array.recycle();
		}
		
		init();
	}
	
	private void init(){
		setLeftDrawable(getLeftIcon());
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
	
	private Drawable getLeftIcon(){
		if(isStatusEnable){
			return enableIcon;
		} else {
			return disableIcon;
		}
	}
	
	private void setLeftDrawable(Drawable drawable){
		if(drawable == null){
			return;
		}
		setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
	}
	
	public void setEnableIcon(boolean status){
		isStatusEnable = status;
		setLeftDrawable(getLeftIcon());
	}
	
	public boolean isEnableIcon(){
		return isStatusEnable;
	}
}
