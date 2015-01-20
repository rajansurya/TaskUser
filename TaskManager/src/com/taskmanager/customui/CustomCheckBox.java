package com.taskmanager.customui;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * This extends the Android CheckBox to add some more padding so the text is not
 * on top of the CheckBox.
 */
public class CustomCheckBox extends CheckBox {

	public CustomCheckBox(Context context) {
		super(context);
	}

	public CustomCheckBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/*@Override
	public int getCompoundPaddingLeft() {
		final float scale = this.getResources().getDisplayMetrics().density;
		return (super.getCompoundPaddingLeft() + (int) (10.0f * scale + 0.5f));
	}*/
	
	
	@Override
	public int getCompoundPaddingLeft() {

	    // Workarround for version codes < Jelly bean 4.2
	    // The system does not apply the same padding. Explantion:
	    // http://stackoverflow.com/questions/4037795/android-spacing-between-checkbox-and-text/4038195#4038195

	    int compoundPaddingLeft = super.getCompoundPaddingLeft();

	    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
	        final float scale = this.getResources().getDisplayMetrics().density;
	       // return compoundPaddingLeft + (drawable != null ? (int)(10.0f * scale + 0.5f) : 0);
	        return (0 + (int) (20.0f * scale + 0.5f));
	    } else {
	        return compoundPaddingLeft;
	    }
	  

	}
}