package com.taskmanager.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.taskmanager.app.R;
import com.taskmanager.app.common.DateTimeUtil;
import com.taskmanager.util.ApplicationUtil;

@SuppressLint("SimpleDateFormat")
public class SnoozActivity extends FragmentActivity {
	private SnoozActivity mActivity;
	
	private TextView mReminderTitleTextView;
	private TextView mDateTextView;
	private TextView mTimetTextView;
	
	private int minute, hour, day, month, year;
	
	private final String REMINDER_SET = "Reminder set";
	private final String REMINDER_NOT_SET = "No reminder set";
	
	private String mTodayTomorrowDate;
	private final String TODAY = "Today";
	private final String TOMORROW = "Tomorrow";
	
	
	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_snooz);
		mActivity = this;
		
		mReminderTitleTextView = (TextView)findViewById(R.id.ReminderTitleTextView);
		mDateTextView = (TextView)findViewById(R.id.DateTextView);
		mTimetTextView = (TextView)findViewById(R.id.TimeTextView);
		
		mDateTextView.setOnClickListener(mClickListener);
		mTimetTextView.setOnClickListener(mClickListener);
		
		findViewById(R.id.BackButton).setOnClickListener(mClickListener);
		findViewById(R.id.SaveButton).setOnClickListener(mClickListener);
		
//		alarm_date_Time=30/10/2014 18:00
		
		String alarmDateTime = getIntent().getStringExtra("alarm_date_Time");
		String scheduleTaskDateTime = getIntent().getStringExtra("schedule_task_date_Time");
		
		if(alarmDateTime != null && alarmDateTime.trim().isEmpty() == false){
			mReminderTitleTextView.setText(REMINDER_SET);
			boolean isTodayTomorrow = isDateTodayTomorrow(alarmDateTime);
			if(isTodayTomorrow){
				initControlsFromAlarmDate(mTodayTomorrowDate, alarmDateTime);
			} else {
				initControlsFromAlarmDate(null, alarmDateTime);
			}
		} else if(scheduleTaskDateTime != null && scheduleTaskDateTime.trim().isEmpty() == false){
			initControlsFromScheduleTaskDate(scheduleTaskDateTime);
		}else {
			mReminderTitleTextView.setText(REMINDER_NOT_SET);
			initControlsFromTodaysDate();
		}
	}
	
	@Override
	public void onBackPressed() {
		overridePendingTransition(0, R.anim.slide_out_to_bottom);
		super.onBackPressed();
	}
	
	private boolean isDateTodayTomorrow(String alarmDateString){
		String formatedDate = ApplicationUtil.getDateTime6(alarmDateString+":00");
		
		if(formatedDate.equals(ApplicationUtil.getDateTime9(0))){
			mTodayTomorrowDate = TODAY;
			return true;
		} else if(formatedDate.equals(ApplicationUtil.getDateTime9(1))){
			mTodayTomorrowDate = TOMORROW;
			return true;
		} else {
			mTodayTomorrowDate = null;
			return false;
		}
	}
	
	private void initControlsFromAlarmDate(String todayTomorrow, String alarmDateTime){
		Calendar calendar = Calendar.getInstance();
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
			Date date = dateFormat.parse(alarmDateTime);
			calendar.setTime(date);
			
			minute = calendar.get(Calendar.MINUTE);
			hour = calendar.get(Calendar.HOUR_OF_DAY);
			
			day = calendar.get(Calendar.DAY_OF_MONTH);
			month = calendar.get(Calendar.MONTH);
			year = calendar.get(Calendar.YEAR);
			
			String amPm = calendar.get(Calendar.AM_PM) == 0 ? "AM" : "PM";
			mTimetTextView.setText(String.format("%02d:%02d %s", calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), amPm));

			if(todayTomorrow != null){
				mDateTextView.setText(todayTomorrow);
			} else {
				mDateTextView.setText(String.format("%d/%d/%d", day, month+1, year));
			}
		}catch(Exception e){
		}
	}
	
	private void initControlsFromScheduleTaskDate(String scheduleTaskDateTime){
		Calendar calendar = Calendar.getInstance();
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			Date date = dateFormat.parse(scheduleTaskDateTime);
			calendar.setTime(date);

			minute = calendar.get(Calendar.MINUTE);
			hour = calendar.get(Calendar.HOUR_OF_DAY);

			day = calendar.get(Calendar.DAY_OF_MONTH);
			month = calendar.get(Calendar.MONTH);
			year = calendar.get(Calendar.YEAR);

			if(hour < 14){
				hour = 18;
				minute = 0;
				mTimetTextView.setText("06:00 PM");
				mDateTextView.setText(String.format("%d/%d/%d", day, month+1, year));
			} else {
				hour = 10;
				minute = 0;
				mTimetTextView.setText("10:00 AM");

				//Adding one day for tomorrow's date
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				day = calendar.get(Calendar.DAY_OF_MONTH);
				month = calendar.get(Calendar.MONTH);

				mDateTextView.setText(String.format("%d/%d/%d", day, month+1, year));
			}
		}catch(Exception e){
		}
	}
	
	private void initControlsFromTodaysDate(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());

		minute = calendar.get(Calendar.MINUTE);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		
		day = calendar.get(Calendar.DAY_OF_MONTH);
		month = calendar.get(Calendar.MONTH);
		year = calendar.get(Calendar.YEAR);
		
		if(hour < 14){
			hour = 18;
			minute = 0;
			mTimetTextView.setText("06:00 PM");
//			mDateTextView.setText(String.format("%d/%d/%d", day, month+1, year));
			mDateTextView.setText(TODAY);
			mTodayTomorrowDate = TODAY;
		} else {
			hour = 10;
			minute = 0;
			mTimetTextView.setText("10:00 AM");
			
			//Adding one day for tomorrow's date
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			day = calendar.get(Calendar.DAY_OF_MONTH);
			month = calendar.get(Calendar.MONTH);
			
//			mDateTextView.setText(String.format("%d/%d/%d", day, month+1, year));
			mDateTextView.setText(TOMORROW);
			mTodayTomorrowDate = TOMORROW;
		}
	}
	
	private OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			int viewId = view.getId();
			
			if(viewId == R.id.DateTextView){
				DateTimeUtil dateTimeUtil = new DateTimeUtil(mActivity, mDateSetListener);
				if(day != 0 && year != 0){
					dateTimeUtil.setDate(day, month +1, year);
				}
				dateTimeUtil.showDatePicker();
			}
			else if(viewId == R.id.TimeTextView){
				DateTimeUtil dateTimeUtil = new DateTimeUtil(mActivity, mTimeSetListener);
				if(minute != 0 || hour != 0){
					dateTimeUtil.setTime(minute, hour);
				}
				dateTimeUtil.showTimePicker();
			}
			else if(viewId == R.id.SaveButton){
				setDataResult();
			}
			else if(viewId == R.id.BackButton){
				SnoozActivity.this.finish();
				overridePendingTransition(0, R.anim.slide_out_to_bottom);
			}
		}
	};
	
	private void setDataResult(){
		String dateTime = null;
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Calendar calendar = Calendar.getInstance();
		
		if(mTodayTomorrowDate != null && mTodayTomorrowDate.equals(TODAY)){
			calendar.add(Calendar.DAY_OF_MONTH, 0);
			dateTime = formatter.format(calendar.getTime());
			dateTime = dateTime +" "+ mTimetTextView.getText().toString();
		} else if(mTodayTomorrowDate != null && mTodayTomorrowDate.equals(TOMORROW)){
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			dateTime = formatter.format(calendar.getTime());
			dateTime = dateTime +" "+ mTimetTextView.getText().toString();
		} else {
			dateTime = mDateTextView.getText().toString() +" "+ mTimetTextView.getText().toString();
		}
		
		
		
		Intent intent = new Intent();
		intent.putExtra("reminderAlarmTime", dateTime);
		intent.putExtra("TaskId", getIntent().getStringExtra("TaskId"));
		intent.putExtra("TaskDesc", getIntent().getStringExtra("TaskDesc"));
		setResult(Activity.RESULT_OK, intent);
		finish();
		overridePendingTransition(0, R.anim.slide_out_to_bottom);
	}
	
	private DateTimeUtil.OnDateSetListener mDateSetListener = new DateTimeUtil.OnDateSetListener() {
		@Override
		public void onDateSet(int day, int month, int year) {
			SnoozActivity.this.day = day;
			SnoozActivity.this.month = month -1;
			SnoozActivity.this.year = year;
			
			Calendar calendar = Calendar.getInstance();
			int calendarYear = calendar.get(Calendar.YEAR);
			int calendarMonth = calendar.get(Calendar.MONTH) +1;
			int calendarDay = calendar.get(Calendar.DAY_OF_MONTH);
			
			if(calendarDay == day && calendarMonth == month && calendarYear == year){
				mTodayTomorrowDate = TODAY;
				mDateTextView.setText(TODAY);
			} else if((calendarDay +1 == day) && calendarMonth == month && calendarYear == year){
				mTodayTomorrowDate = TOMORROW;
				mDateTextView.setText(TOMORROW);
			} else {
				mTodayTomorrowDate = null;
				mDateTextView.setText(String.format("%d/%d/%d", day, month, year));
			}
			
			mDateTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.snooz_calendar_selected, 0, 0, 0);
			mDateTextView.setBackgroundColor(getResources().getColor(R.color.snooz_item_selected_bg_color));
			mDateTextView.setTextColor(getResources().getColor(R.color.snooz_item_selected_text_color));
		}
	};
	
	private DateTimeUtil.OnTimeSetListener mTimeSetListener = new DateTimeUtil.OnTimeSetListener() {
		@Override
		public void onTimeSet(int hour, int min) {
			SnoozActivity.this.hour = hour;
			SnoozActivity.this.minute = min;
			
			String amPm = hour > 12 ? "PM" : "AM";
			hour = hour > 12 ? hour - 12 : hour;
			mTimetTextView.setText(String.format("%02d:%02d %s", hour, min, amPm));
			mTimetTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.snooz_time_selected, 0, 0, 0);
			mTimetTextView.setBackgroundColor(getResources().getColor(R.color.snooz_item_selected_bg_color));
			mTimetTextView.setTextColor(getResources().getColor(R.color.snooz_item_selected_text_color));
		}
	};
}
