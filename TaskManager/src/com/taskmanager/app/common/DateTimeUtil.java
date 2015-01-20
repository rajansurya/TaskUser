package com.taskmanager.app.common;

import java.util.Calendar;

import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.taskmanager.app.R;

public class DateTimeUtil {
	//Date Time picker
	private static final String DATEPICKER_TAG = "datepicker";
	private static final String TIMEPICKER_TAG = "timepicker";
	private int YEAR, MONTH, DAY, HOUR, MINUTE;
	private String []DayOFWeek = {"Sunday","Monday","Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

	private FragmentActivity mActivity;
	private OnDateSetListener mDateListener;
	private OnTimeSetListener mTimeListener;
	
	public static interface OnDateSetListener {
		public abstract void onDateSet(int day, int month, int year);
	}
	
	public static interface OnTimeSetListener {
		public abstract void onTimeSet(int hour, int min);
	}
	
	public DateTimeUtil(FragmentActivity activity, OnDateSetListener listener){
		this(activity);
		mDateListener = listener;
	}
	
	public DateTimeUtil(FragmentActivity activity, OnTimeSetListener listener){
		this(activity);
		mTimeListener = listener;
	}
	
	public DateTimeUtil(FragmentActivity activity) {
		mActivity = activity;
		
		final Calendar myCalendar = Calendar.getInstance();
		YEAR 		= myCalendar.get(Calendar.YEAR);
		MONTH		=  myCalendar.get(Calendar.MONTH);
		DAY			= myCalendar.get(Calendar.DAY_OF_MONTH);
		HOUR 		= myCalendar.get(Calendar.HOUR_OF_DAY);
		MINUTE		= 	myCalendar.get(Calendar.MINUTE);
	}

	public void setDate(int day, int month, int year){
		DAY = day;
		MONTH = month -1;
		YEAR = year;
	}
	
	public void setTime(int min, int hour){
		MINUTE = min;
		HOUR = hour;
	}
	
	public void showDatePicker(){
		DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(mOnDateSetListener, YEAR, MONTH,DAY, true);
		datePickerDialog.setVibrate(true);
		datePickerDialog.setYearRange(1985, 2028);
		datePickerDialog.setCloseOnSingleTapDay(false);
		datePickerDialog.show(mActivity.getSupportFragmentManager(), DATEPICKER_TAG);
	}
	
	public void showTimePicker(){
		TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(mTimeSetListener, HOUR ,MINUTE, false, false);
		timePickerDialog.setVibrate(true);
		timePickerDialog.setCloseOnSingleTapMinute(false);
		timePickerDialog.show(mActivity.getSupportFragmentManager(), TIMEPICKER_TAG);
	}
	
	public void showChooseDatePopupWindow(View view) {
		View popupView = mActivity.getLayoutInflater().inflate(R.layout.popup_window_date, null);  

		final PopupWindow popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);

		// Removes default black background
		popupWindow.setBackgroundDrawable(new BitmapDrawable());

		TextView today = (TextView)popupView.findViewById(R.id.today);
		TextView tomorrow = (TextView)popupView.findViewById(R.id.tomorrow);
		TextView nextMon = (TextView)popupView.findViewById(R.id.nextMon);
		TextView selectDate = (TextView)popupView.findViewById(R.id.selectDate);

		final int dayofweek = dayOfWeek();
		nextMon.setText("Next "+DayOFWeek[dayofweek]);

		today.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				YEAR 		= Calendar.getInstance().get(Calendar.YEAR);
				MONTH		=  Calendar.getInstance().get(Calendar.MONTH);
				DAY			= Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
//				btndate.setText("Today");

				popupWindow.dismiss();
			}
		});
		
		tomorrow.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, 1);
				YEAR 		= c.get(Calendar.YEAR);
				MONTH		=  c.get(Calendar.MONTH);
				DAY			= c.get(Calendar.DAY_OF_MONTH);

//				btndate.setText("Tomorrow");
				popupWindow.dismiss();
			}
		});
		
		nextMon.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar date1 = Calendar.getInstance();
				date1.add(Calendar.DATE, 1);
				while (date1.get(Calendar.DAY_OF_WEEK) != (dayofweek+1)) {
					date1.add(Calendar.DATE, 1);
				}

				YEAR 		= date1.get(Calendar.YEAR);
				MONTH		=  date1.get(Calendar.MONTH);
				DAY			= date1.get(Calendar.DAY_OF_MONTH);

//				btndate.setText("" + YEAR + "-" + (MONTH+1) + "-" + DAY);
				popupWindow.dismiss();
			}
		});

		selectDate.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(mOnDateSetListener, YEAR, MONTH,DAY, true);
				datePickerDialog.setVibrate(true);
				datePickerDialog.setYearRange(1985, 2028);
				datePickerDialog.setCloseOnSingleTapDay(false);
				datePickerDialog.show(mActivity.getSupportFragmentManager(), DATEPICKER_TAG);

				popupWindow.dismiss();
			}
		});

		popupWindow.showAsDropDown(view, 0, 0);
	}
	
	public void showChooseTimePopupWindow(View view) { 
		View popupView = mActivity.getLayoutInflater().inflate(R.layout.popup_window_time, null);  

		final PopupWindow popupWindow = new PopupWindow( popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);

		// Removes default black background
		popupWindow.setBackgroundDrawable(new BitmapDrawable());

		TextView morning = (TextView)popupView.findViewById(R.id.morning);
		TextView afternoon = (TextView)popupView.findViewById(R.id.afternoon);
		TextView evening = (TextView)popupView.findViewById(R.id.evening);
		TextView night = (TextView)popupView.findViewById(R.id.night);
		TextView selecttime = (TextView)popupView.findViewById(R.id.selecttime);

		LinearLayout llmorning = (LinearLayout)popupView.findViewById(R.id.llmorning);
		LinearLayout llafternoon = (LinearLayout)popupView.findViewById(R.id.llafternoon);
		LinearLayout llEvening = (LinearLayout)popupView.findViewById(R.id.llEvening);
		LinearLayout llnight = (LinearLayout)popupView.findViewById(R.id.llnight);

		llmorning.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.HOUR_OF_DAY, 9);
				calendar.set(Calendar.MINUTE, 0);
				HOUR 		= calendar.get(Calendar.HOUR_OF_DAY);
				MINUTE		= 	calendar.get(Calendar.MINUTE);
//				btnTime.setText("Morning");

				popupWindow.dismiss();
			}
		});
		
		llafternoon.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.HOUR_OF_DAY, 12);
				calendar.set(Calendar.MINUTE, 0);
				HOUR 		= calendar.get(Calendar.HOUR_OF_DAY);
				MINUTE		= 	calendar.get(Calendar.MINUTE);
//				btnTime.setText("Afternoon");

				popupWindow.dismiss();
			}
		});
		
		llEvening.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.HOUR_OF_DAY, 17);
				calendar.set(Calendar.MINUTE, 0);
				HOUR 		= calendar.get(Calendar.HOUR_OF_DAY);
				MINUTE		= 	calendar.get(Calendar.MINUTE);
//				btnTime.setText("Evening");
				popupWindow.dismiss();
			}
		});

		llnight.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.HOUR_OF_DAY, 20);
				calendar.set(Calendar.MINUTE, 0);
				HOUR 		= calendar.get(Calendar.HOUR_OF_DAY);
				MINUTE		= 	calendar.get(Calendar.MINUTE);
//				btnTime.setText("Night");
				popupWindow.dismiss();
			}
		});
		
		selecttime.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(mTimeSetListener, HOUR ,MINUTE, false, false);
				timePickerDialog.setVibrate(true);
				timePickerDialog.setCloseOnSingleTapMinute(false);
				timePickerDialog.show(mActivity.getSupportFragmentManager(), TIMEPICKER_TAG);

				popupWindow.dismiss();
			}
		});

		popupWindow.showAsDropDown(view, 0, 0);
	}
	
	private int dayOfWeek() {
		return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
	}
	
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
			HOUR = hourOfDay;
			MINUTE = minute;
			String hourText = "";
			if(hourOfDay > 12) {
				hourText = ""+(hourOfDay - 12)+":"+ minute+" PM";
			}
			else {
				hourText = ""+(hourOfDay)+":"+ minute+" AM";
			}

			if(mTimeListener != null){
				mTimeListener.onTimeSet(HOUR, MINUTE);
			}
			
//			btnTime.setText("" + hourText);
//			Toast.makeText(mActivity, "new time: " + hourText + "-" + minute, Toast.LENGTH_LONG).show();
		}
	};

	private DatePickerDialog.OnDateSetListener mOnDateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
			YEAR = year;
			MONTH = month;
			DAY = day;
			
			if(mDateListener != null){
				mDateListener.onDateSet(DAY, MONTH +1, YEAR);
			}
			
//			deadlineDateTxt = day + "/" + (month+1) + "/" + year;
//			mDeadLineTextView.setText(deadlineDateTxt);
//			mDeadLineLayout.setVisibility(View.VISIBLE);
			
//			btndate.setText("" + year + "-" + (month+1) + "-" + day);
//			Toast.makeText(CreateTaskActivity.this, "new date:" + year + "-" + (month+1) + "-" + day, Toast.LENGTH_LONG).show();
		}
	};
}
