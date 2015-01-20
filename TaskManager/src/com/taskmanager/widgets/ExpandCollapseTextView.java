package com.taskmanager.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.taskmanager.app.R;

public class ExpandCollapseTextView extends TextView {
	private boolean ellipsis;
	private int maxCollapseLines;

	public ExpandCollapseTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ExpandCollapseTextView, 0, 0);
		
		try {
			maxCollapseLines = array.getInteger(R.styleable.ExpandCollapseTextView_maxCollapseLins, 3);
		} finally {
			array.recycle();
		}
		
		getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
	}
	
	private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
		@Override
		public void onGlobalLayout() {
			try{
				int lineCount = getLayout().getLineCount();
				int ellipsisCount = 0;
				if(lineCount > 0){
					ellipsisCount = getLayout().getEllipsisCount(lineCount -1);
				}

				if(ellipsisCount > 0){
					ellipsis = true;
				} else {
					ellipsis = false;
				}
			}catch(NullPointerException e){
				Log.e("ExpandCollapseTextView", e.toString());
			}
		}
	};

	public boolean isEllipsis() {
		return ellipsis;
	}
	public void setEllipsis(boolean ellipsis) {
		this.ellipsis = ellipsis;
	}
	public void expand(){
		setMaxLines(Integer.MAX_VALUE);
//		ObjectAnimator animation = ObjectAnimator.ofInt(this, "maxLines", 10);
//		animation.setDuration(1000);
//		animation.start();
	}
	public void collapse(){
		setMaxLines(maxCollapseLines);
	}
}
