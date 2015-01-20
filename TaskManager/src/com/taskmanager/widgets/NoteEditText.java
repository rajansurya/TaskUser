package com.taskmanager.widgets;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.taskmanager.app.R;

public class NoteEditText extends EditText {
	private Paint mPaint;
	private int enterPressed = 0;
	private int noOfLines = 30;

	public NoteEditText(Context context) {
		super(context);
	}

	public NoteEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		mPaint.setStrokeWidth(1);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setColor(Color.LTGRAY); 
		PathEffect effects = new DashPathEffect(new float[]{0,0,0,0},1);  
		this.setBackgroundDrawable(getResources().getDrawable(R.drawable.add_task_input_box));
		this.setGravity(Gravity.LEFT|Gravity.TOP);
		this.setPadding(12, 2, 10, 3);
		this.setTextSize(14);

		mPaint.setPathEffect(effects);

		this.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if(arg1 == KeyEvent.KEYCODE_ENTER)
				{
					noOfLines++;
				}
				return false;
			}
		});
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int height = this.getHeight();
		int lineHeight = this.getLineHeight();
		int lineNum = height / lineHeight;
		for (int i = 0; i < noOfLines; i++) {
			int y = (i + 1) * lineHeight;
			canvas.drawLine(12, y, this.getWidth() - 10, y, mPaint);
		}
	}
}