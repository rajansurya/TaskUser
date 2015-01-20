package com.taskmanager.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.taskmanager.app.R;
import com.taskmanager.app.common.CustomTypeFace;

public class RobotoTextView extends TextView {

	public RobotoTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RobotoTextViewStyleable, 0, 0);
		
		try{
			int fontType = array.getInt(R.styleable.RobotoTextViewStyleable_font, 0);
			
			Typeface typeface = CustomTypeFace.getTypeface(context, fontType);
			if(typeface != null){
				setTypeface(typeface);
			}
		} finally {
			array.recycle();
		}
	}
}
