package com.taskmanager.widgets;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.taskmanager.app.R;

public class DialogMenu extends Dialog {

	public DialogMenu(Context context, int layoutResID) {
		this(context, R.style.CustomDialogAnimation, layoutResID);
	}
	
	public DialogMenu(Context context, int theme, int layoutResID) {
		super(context, theme);
		init(layoutResID);
	}

	private void init(int layoutResID){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(layoutResID);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.copyFrom(getWindow().getAttributes());
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//		params.gravity = Gravity.CENTER;
		params.dimAmount = 0.5f;
		params.flags=WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		getWindow().setAttributes(params);
		getWindow().setBackgroundDrawableResource(R.color.transparent);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		setCanceledOnTouchOutside(true);
//		show();
	}
}
