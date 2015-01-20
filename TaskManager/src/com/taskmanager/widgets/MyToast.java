package com.taskmanager.widgets;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

public class MyToast {

	public static void makeText(Context context, String text, int bgColor){
		Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.FILL_HORIZONTAL|Gravity.BOTTOM, 0, 0);
//		toast.getView().setBackgroundColor(bgColor);
		toast.getView().setBackgroundColor(Color.parseColor("#646464"));
		TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
		textView.setTextColor(Color.WHITE);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		toast.show();
	}
}
