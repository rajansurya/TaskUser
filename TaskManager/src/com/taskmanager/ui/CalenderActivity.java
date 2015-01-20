package com.taskmanager.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.taskmanager.app.R;
import com.taskmanager.background.SyncModule;
import com.taskmanager.util.ApplicationConstant;
import com.taskmanager.util.ApplicationUtil;

/**
 * Activity for rendering calender screen
 * @author mayankb
 *
 */
public class CalenderActivity extends Activity implements OnClickListener
{
	private Context context;
	private Calendar calendarObj;

	private TextView currentMonth;
	private TextView currentYear;
	private ImageView prevMonth;
	private ImageView nextMonth;
	private GridView calendarView;
	private GridCellAdapter adapter;
	private int month, year ,date, presentMonth, presentYear,presentDate;
	private final DateFormat dateFormatter = new DateFormat();
	private static final String monthTemplate = "MMMM";
	private static final String YearTemplate = "yyyy";
	private int hour;
	private int min;
	private ImageView hourUp;
	private ImageView hourDown;
	private TextView hourText ;
	private ImageView minUp;
	private ImageView minDown;
	private TextView minText;
	private View prevSelection;
	private String selectedDate;
	private Spinner reminderSpinner;
	private RelativeLayout reminderMain;
	private long receivedTargetTimeStamp;
	private long changedTargetTimeStamp;
	private int reminderValueSelected;
	private String  reminderLabelValue;


	private String[] reminderValues = {"Off","5 min before","10 min before","30 min before","1 hour before","3 hour before","1 day before"};
	private ArrayAdapter reminderArrayAdapter;
	private TextView reminderType;

	private String action;
	private String targateDate;
	private String reminderTime;
	private long receivedReminderTimeStamp;
	private long changedReminderTimeStamp;
	private String[] targateValues;
	private String[] timeValues;

	private String taskId;
	private String taskDesc;

	private String alarmTime;
	private ImageView reminderIcon;

	//for CreateTaskActivity
	private String reminderVal = "";
	private String targetDateVal ="";
	//private Button btnClose;
	private LinearLayout linearClose = null;
	private LinearLayout linearOk= null;
	
	private String alarm_date_time;
	//private String argetDateTime;
	private String isAlarmSet ;
	private int alarm_selected_date;
	private int alarm_selected_month;
	private int alarm_selected_year;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.calender_screen);
		getWindow().setBackgroundDrawable(new ColorDrawable(0));

		context = this;
		calendarObj = Calendar.getInstance(Locale.getDefault());
		month = calendarObj.get(Calendar.MONTH) + 1;
		presentMonth=month;
		
		
		year = calendarObj.get(Calendar.YEAR);
		presentYear = year;
		
		date = calendarObj.get(Calendar.DATE);
		presentDate = date;
		
		prevMonth = (ImageView) findViewById(R.id.prevMonth);
		prevMonth.setOnClickListener(this);

		currentMonth = (TextView) findViewById(R.id.currentMonth);
		currentYear = (TextView) findViewById(R.id.currentYear);
		currentMonth.setText(dateFormatter.format(monthTemplate, calendarObj.getTime()));
		currentYear.setText(" "+dateFormatter.format(YearTemplate, calendarObj.getTime()));
		nextMonth = (ImageView) findViewById(R.id.nextMonth);
		reminderType = (TextView) findViewById(R.id.alarmtype);
		reminderIcon = (ImageView) findViewById(R.id.alarmicon);

		reminderSpinner = (Spinner) findViewById(R.id.reminderspinner);
		reminderArrayAdapter = new ArrayAdapter(CalenderActivity.this, android.R.layout.simple_spinner_item,reminderValues);
		reminderArrayAdapter.setDropDownViewResource(R.drawable.spinner_style);
		reminderSpinner.setAdapter(reminderArrayAdapter);
		nextMonth.setOnClickListener(this);
		calendarView = (GridView) findViewById(R.id.calendar);
		reminderMain = (RelativeLayout) findViewById(R.id.reminderrelative);

		hourUp = (ImageView) findViewById(R.id.hourup);
		hourDown = (ImageView) findViewById(R.id.hourdown);
		hourText = (TextView) findViewById(R.id.hourtxt);
		minUp = (ImageView) findViewById(R.id.minup);
		minDown = (ImageView) findViewById(R.id.mindown);
		minText = (TextView) findViewById(R.id.mintxt);
		//btnClose = (Button)findViewById(R.id.btnClose);
		linearClose = (LinearLayout)findViewById(R.id.linearCancel);
		linearOk = (LinearLayout)findViewById(R.id.linearOk);
		
		LinearLayout  reminderLayout=(LinearLayout)findViewById(R.id.alarmtime);
		action = getIntent().getExtras().getString("action");
		if(!"shedulealarm".equalsIgnoreCase(action)){
			reminderLayout.setVisibility(View.GONE);
		}
		
		linearClose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				finish();
				
			}
		});
		
		linearOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String dateValText ="";
				String timeValText ="";
				String ReminderValText ="";

				if(action.trim().equalsIgnoreCase("createTask")) {
					if(selectedDate !=null && !selectedDate.trim().equals("")) {
						Date mDate= new Date(selectedDate);
						SimpleDateFormat mDateFormat= new SimpleDateFormat("dd/MM/yyyy");
						dateValText = mDateFormat.format(mDate);
					}
					else {
						Date mDate= new Date(System.currentTimeMillis());
						SimpleDateFormat mDateFormat= new SimpleDateFormat("dd/MM/yyyy");
						dateValText = mDateFormat.format(mDate);
					}
					if(min <= 9)
						timeValText = hour+":0"+min;
					else
						timeValText = hour+":"+min;

					String reminderTime = dateValText +" "+timeValText;
					targetDateVal = reminderTime;
					try {
						long reminderTimeStamp = ApplicationUtil.getUpdatedTimestampFromDate(reminderTime);
						if(reminderValueSelected == 0) {
							ReminderValText ="";
						}
						else if(reminderValueSelected == 1) { 
							reminderTimeStamp = reminderTimeStamp - 5*60;
							ReminderValText = ApplicationUtil.changeToDayMonthYear2(reminderTimeStamp);
						}
						else if(reminderValueSelected == 2) {
							reminderTimeStamp = reminderTimeStamp - 10*60;
							ReminderValText = ApplicationUtil.changeToDayMonthYear2(reminderTimeStamp);
						}
						else if(reminderValueSelected == 3) {
							reminderTimeStamp = reminderTimeStamp - 30*60;
							ReminderValText = ApplicationUtil.changeToDayMonthYear2(reminderTimeStamp);
						}
						else if(reminderValueSelected == 4) {
							reminderTimeStamp = reminderTimeStamp - 60*60;
							ReminderValText = ApplicationUtil.changeToDayMonthYear2(reminderTimeStamp);
						}
						else if(reminderValueSelected == 5) {
							reminderTimeStamp = reminderTimeStamp - 3*60*60;
							ReminderValText = ApplicationUtil.changeToDayMonthYear2(reminderTimeStamp);
						}
						else if(reminderValueSelected == 6) {
							reminderTimeStamp = reminderTimeStamp - 24*60*60;
							ReminderValText = ApplicationUtil.changeToDayMonthYear2(reminderTimeStamp);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				if(action.trim().equalsIgnoreCase("reminder")) {
					String targetDate = "";
					
					if(selectedDate != null && !selectedDate.trim().equals("")) {
						Date mDate= new Date(selectedDate);
						SimpleDateFormat mDateFormat= new SimpleDateFormat("dd/MM/yyyy");
						dateValText = mDateFormat.format(mDate);
					}
					else {
						Date mDate= new Date(System.currentTimeMillis());
						SimpleDateFormat mDateFormat= new SimpleDateFormat("dd/MM/yyyy");
						SimpleDateFormat mDateFormat1= new SimpleDateFormat("dd-MMM-yyyy");
						dateValText = mDateFormat.format(mDate);
						selectedDate = mDateFormat1.format(mDate);
					}
					if(min <= 9)
						timeValText = hour+":0"+min;
					else
						timeValText = hour+":"+min;
						targetDate = selectedDate +" "+timeValText;

					try {
						changedTargetTimeStamp = ApplicationUtil.getUpdatedTimestampFromDate2(targetDate);
						if(reminderValueSelected == 0)
							changedReminderTimeStamp =0;
						else if(reminderValueSelected == 1)
							changedReminderTimeStamp = changedTargetTimeStamp - 5*60;
						else if(reminderValueSelected == 2)
							changedReminderTimeStamp = changedTargetTimeStamp - 10*60;
						else if(reminderValueSelected == 3)
							changedReminderTimeStamp = changedTargetTimeStamp - 30*60;
						else if(reminderValueSelected == 4)
							changedReminderTimeStamp = changedTargetTimeStamp - 60*60;
						else if(reminderValueSelected == 5)
							changedReminderTimeStamp = changedTargetTimeStamp - 3*60*60;
						else if(reminderValueSelected == 6)
							changedReminderTimeStamp = changedTargetTimeStamp - 24*60*60;

					} catch (Exception e) {
						e.printStackTrace();
					}
					reminderVal = ReminderValText;
					String changedReminderDate = ApplicationUtil.changeToDayMonthYear2(changedReminderTimeStamp);
					if(changedReminderTimeStamp != receivedReminderTimeStamp) {
						ReminderValText = changedReminderDate;
						changeReminder(changedReminderDate+":00");
					}
					else {
						ReminderValText = changedReminderDate;
					}

					String changedDate = ApplicationUtil.changeToDayMonthYear2(changedTargetTimeStamp);
					if(receivedTargetTimeStamp != changedTargetTimeStamp) {
						changeTarget(changedDate+":00");
						targetDateVal = changedDate;
						Toast.makeText(context, getResources().getString(R.string.change_target_toast), Toast.LENGTH_LONG).show();
					}
					else {
						targetDateVal = changedDate;
						Toast.makeText(context, getResources().getString(R.string.same_target_toast), Toast.LENGTH_LONG).show();
					}
				}
				if(action.trim().equalsIgnoreCase("alarm")) {
					String alarmTimeVal = "";

					if(selectedDate !=null && !selectedDate.trim().equals("")) {
						Date mDate= new Date(selectedDate);
						SimpleDateFormat mDateFormat= new SimpleDateFormat("dd/MM/yyyy");
						alarmTimeVal = mDateFormat.format(mDate);
					}
					else {
						Date mDate= new Date(System.currentTimeMillis());
						SimpleDateFormat mDateFormat= new SimpleDateFormat("dd/MM/yyyy");
						alarmTimeVal = mDateFormat.format(mDate);
					}
					if(min <= 9)
						alarmTimeVal =alarmTimeVal+" "+  hour+":0"+min;
					else
						alarmTimeVal =alarmTimeVal+" "+  hour+":"+min;
					try {
						alarmTimeVal = alarmTimeVal+":00";
						long changedAlarmTimeStamp = ApplicationUtil.getUpdatedTimestampFromDate3(alarmTimeVal);
						long receivedAlarmTimeStamp = 0;
						if(alarmTime != null && alarmTime.trim().length()>0)
							receivedAlarmTimeStamp = ApplicationUtil.getUpdatedTimestampFromDate3(alarmTime);
						if(receivedAlarmTimeStamp != changedAlarmTimeStamp) {
							setAlarm(alarmTimeVal ,taskId);
							Toast.makeText(context, getResources().getString(R.string.change_alarm_toast), Toast.LENGTH_LONG).show();
						}
						else {
							Toast.makeText(context, getResources().getString(R.string.same_alarm_toast), Toast.LENGTH_LONG).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else if(action.trim().equals("shedulealarm")) {
					if(selectedDate !=null && !selectedDate.trim().equals("")) {
						Date mDate= new Date(selectedDate);
						SimpleDateFormat mDateFormat= new SimpleDateFormat("dd/MM/yyyy");
						dateValText = mDateFormat.format(mDate);
					}
					else {
						Date mDate= new Date(System.currentTimeMillis());
						SimpleDateFormat mDateFormat= new SimpleDateFormat("dd/MM/yyyy");
						dateValText = mDateFormat.format(mDate);
					}
					if(min <= 9)
						timeValText = hour+":0"+min;
					else
						timeValText = hour+":"+min;

					String reminderTime = dateValText +" "+timeValText;
					targetDateVal = reminderTime;
					try {
						long reminderTimeStamp = ApplicationUtil.getUpdatedTimestampFromDate(reminderTime);
						if(reminderValueSelected == 0) {
							ReminderValText ="";
						}
						else if(reminderValueSelected == 1) { 
							reminderTimeStamp = reminderTimeStamp - 5*60;
							ReminderValText = ApplicationUtil.changeToDayMonthYear2(reminderTimeStamp);
						}
						else if(reminderValueSelected == 2) {
							reminderTimeStamp = reminderTimeStamp - 10*60;
							ReminderValText = ApplicationUtil.changeToDayMonthYear2(reminderTimeStamp);
						}
						else if(reminderValueSelected == 3) {
							reminderTimeStamp = reminderTimeStamp - 30*60;
							ReminderValText = ApplicationUtil.changeToDayMonthYear2(reminderTimeStamp);
						}
						else if(reminderValueSelected == 4) {
							reminderTimeStamp = reminderTimeStamp - 60*60;
							ReminderValText = ApplicationUtil.changeToDayMonthYear2(reminderTimeStamp);
						}
						else if(reminderValueSelected == 5) {
							reminderTimeStamp = reminderTimeStamp - 3*60*60;
							ReminderValText = ApplicationUtil.changeToDayMonthYear2(reminderTimeStamp);
						}
						else if(reminderValueSelected == 6) {
							reminderTimeStamp = reminderTimeStamp - 24*60*60;
							ReminderValText = ApplicationUtil.changeToDayMonthYear2(reminderTimeStamp);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					ContentValues initialValues = new ContentValues();
					initialValues.put("isAlarmSet", "Y");
					initialValues.put("Alarm_Date_Time", targetDateVal);
					initialValues.put("UPDATED_DATE", ApplicationUtil.getGMTDate());
					initialValues.put("UPDATION_DATE_TIME",ApplicationUtil.getGMTLong());
					
					ApplicationUtil.getInstance().updateDataInDB("Task", initialValues, context, "Task_ID ='" + taskId + "'", null);
					
					Thread thread = new Thread() {
						public void run() {
							if(targetDateVal!=null)
								setAlarmManager(CalenderActivity.this,targetDateVal,taskId,taskDesc);
						};
					};
					thread.start();
				}
				
				Intent data = new Intent();
				data.putExtra("deadlineDateVal", dateValText);
				data.putExtra("deadlineTimeVal", timeValText);
				data.putExtra("reminderValueSelected", reminderLabelValue);
				data.putExtra("reminderDateVal", ReminderValText);
				data.putExtra("targetDateVal", targetDateVal);
				setResult(123, data);
				finish();
			}
		});

		if(getIntent().getExtras() != null) {
			action = getIntent().getExtras().getString("action");
			if(action.trim().equals("reminder")) {
				targateDate = getIntent().getExtras().getString("targateDate");
				reminderTime = getIntent().getExtras().getString("reminderTime");
				taskId = getIntent().getExtras().getString("TaskId");
				try {
					targateValues = ApplicationUtil.changeToDayMonthYear(ApplicationUtil.getUpdatedTimestampFromDate(targateDate)).split("\\|\\|");
					selectedDate = targateValues[0].trim();
					timeValues = targateValues[1].split(":");
					hourText.setText(timeValues[0].trim());
					minText.setText(timeValues[1].trim());

					if(reminderTime != null && reminderTime.trim().length()>0)
						receivedReminderTimeStamp = ApplicationUtil.getUpdatedTimestampFromDate(reminderTime); 
					else
						reminderSpinner.setSelection(0);
					changedTargetTimeStamp = receivedTargetTimeStamp = ApplicationUtil.getUpdatedTimestampFromDate(targateDate);
					long diff = (receivedTargetTimeStamp-receivedReminderTimeStamp)/ 60;
					if(diff ==5)
						reminderSpinner.setSelection(1);
					else if(diff ==10)
						reminderSpinner.setSelection(2);
					else if(diff ==30)
						reminderSpinner.setSelection(3);
					else if(diff ==60)
						reminderSpinner.setSelection(4);
					else if(diff ==3*60)
						reminderSpinner.setSelection(5);
					else if(diff ==24*60)
						reminderSpinner.setSelection(6);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else
				if(action.trim().equals("createTask")) {
					targateDate = getIntent().getExtras().getString("targateDate");
					reminderTime = getIntent().getExtras().getString("reminderTime");
					try {
						if(targateDate!=null){
							targateValues = ApplicationUtil.changeToDayMonthYear(ApplicationUtil.getUpdatedTimestampFromDate(targateDate)).split("\\|\\|");
							selectedDate = targateValues[0].trim();
							timeValues = targateValues[1].split(":");
							hourText.setText(timeValues[0].trim());
							minText.setText(timeValues[1].trim());
						}

						if(reminderTime != null && reminderTime.trim().length()>0)
							receivedReminderTimeStamp = ApplicationUtil.getUpdatedTimestampFromDate(reminderTime); 
						else
							reminderSpinner.setSelection(4);
						changedTargetTimeStamp = receivedTargetTimeStamp = ApplicationUtil.getUpdatedTimestampFromDate(targateDate);
						long diff = (receivedTargetTimeStamp-receivedReminderTimeStamp)/ 60;
						if(diff ==5)
							reminderSpinner.setSelection(1);
						else if(diff ==10)
							reminderSpinner.setSelection(2);
						else if(diff ==30)
							reminderSpinner.setSelection(3);
						else if(diff ==60)
							reminderSpinner.setSelection(4);
						else if(diff ==3*60)
							reminderSpinner.setSelection(5);
						else if(diff ==24*60)
							reminderSpinner.setSelection(6);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else if(action.trim().equals("alarm")) {
					taskId = getIntent().getExtras().getString("TaskId");
					alarmTime = getIntent().getExtras().getString("alarmTime");
					try {
						targateValues = ApplicationUtil.changeToDayMonthYear(ApplicationUtil.getUpdatedTimestampFromDate(alarmTime)).split("\\|\\|");
						selectedDate = targateValues[0].trim();
						timeValues = targateValues[1].split(":");
						hourText.setText(timeValues[0].trim());
						minText.setText(timeValues[1].trim());
					} catch (Exception e) {
						e.printStackTrace();
					}

					reminderMain.setVisibility(View.GONE);
				}
				else if(action.trim().equals("shedulealarm")) {
					try {
						taskId = getIntent().getExtras().getString("TaskId");
						taskDesc = getIntent().getExtras().getString("TaskDesc");
						alarm_date_time = getIntent().getExtras().getString("alarm_date_Time"); ;
						isAlarmSet= getIntent().getExtras().getString("isAlarmSet"); 
						
						if(alarm_date_time!=null&& !alarm_date_time.isEmpty()) {
							String[]  str_date = alarm_date_time.split(" ");
							if(str_date[0]!=null) {
								String alarm_day = str_date[0].split("/")[0];
								String alam_month = str_date[0].split("/")[1];
								String alam_year= str_date[0].split("/")[2];
								month = Integer.parseInt(alam_month);
								year = Integer.parseInt( alam_year);
								alarm_selected_date = (Integer.parseInt(alarm_day));
								alarm_selected_month =(Integer.parseInt(alam_month));
								alarm_selected_year=(Integer.parseInt(alam_year));
								setGridCellAdapterToDate(month,year);
							}
							
							if(str_date[1]!=null) {
								String alarm_hour = str_date[1].split(":")[0];
								String alarm_min = str_date[1].split(":")[1];
								hourText.setText(alarm_hour.trim());
								minText.setText(alarm_min.trim());
							}
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					reminderMain.setVisibility(View.GONE);
				}
		}

		// Initialised
		adapter = new GridCellAdapter(getApplicationContext(), R.id.num_events_per_day, month, year);
		adapter.notifyDataSetChanged();

		calendarView.setAdapter(adapter);

		hour = Integer.valueOf(hourText.getText().toString());
		min = Integer.valueOf(minText.getText().toString());

		hourUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(hour < 23) {
					hour++;
					hourText.setText(hour+"");
				}
				else
					if(hour == 23) {
						hour=0;
						hourText.setText(hour+"");
					}
			}
		});
		
		hourDown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(hour > 0) {
					hour--;
					hourText.setText(hour+"");
				}
				else
					if(hour == 0) {
						hour=23;
						hourText.setText(hour+"");
					}
			}
		});
		
		minUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(min < 59) {
					min++;
					minText.setText(min+"");
				}
				else
					if(min == 59) {
						min=0;
						minText.setText(min+"");
					}
			}
		});

		minDown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(min > 0) {
					min--;
					minText.setText(min+"");
				}
				else
					if(min == 0) {
						min=59;
						minText.setText(min+"");
					}
			}
		});

		reminderType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				reminderSpinner.performClick();
			}
		});

		reminderIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				reminderSpinner.performClick();
			}
		});

		reminderSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				reminderValueSelected = arg2;
				reminderLabelValue=arg0.getItemAtPosition(arg2).toString();
				reminderType.setText(arg0.getItemAtPosition(arg2).toString());
			}
		}); 
	}

	/**
	 * 
	 * @param month
	 * @param year
	 */
	private void setGridCellAdapterToDate(int month, int year)
	{
		adapter = new GridCellAdapter(getApplicationContext(), R.id.num_events_per_day, month, year);
		calendarObj.set(year, month - 1, calendarObj.get(Calendar.DAY_OF_MONTH));
		currentMonth.setText(dateFormatter.format(monthTemplate, calendarObj.getTime()));
		currentYear.setText(" "+dateFormatter.format(YearTemplate, calendarObj.getTime()));
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
	}

	@Override
	public void onClick(View v)
	{
		if (v == prevMonth)
		{
			if (month <= 1)
			{
				month = 12;
				year--;
			}
			else
			{
				month--;
			}
			setGridCellAdapterToDate(month, year);
		}
		if (v == nextMonth)
		{
			if (month > 11)
			{
				month = 1;
				year++;
			}
			else
			{
				month++;
			}
			setGridCellAdapterToDate(month, year);
		}

	} 

	@Override
	public void onDestroy()
	{
		super.onDestroy();


	}

	/*//@Override
	//public void onBackPressed() {
		// TODO Auto-generated method stub

		String dateValText ="";
		String timeValText ="";
		String ReminderValText ="";
		if(action.trim().equalsIgnoreCase("createTask"))
		{
			if(selectedDate !=null && !selectedDate.trim().equals(""))
			{
				Date mDate= new Date(selectedDate);
				SimpleDateFormat mDateFormat= new SimpleDateFormat("dd/MM/yyyy");
				dateValText = mDateFormat.format(mDate);
			}
			else
			{
				Date mDate= new Date(System.currentTimeMillis());
				SimpleDateFormat mDateFormat= new SimpleDateFormat("dd/MM/yyyy");
				dateValText = mDateFormat.format(mDate);
			}
			if(min <= 9)
				timeValText = hour+":0"+min;
			else
				timeValText = hour+":"+min;

			System.out.println("dateText====>"+dateValText);

			String reminderTime = dateValText +" "+timeValText;
			targetDateVal = reminderTime;
			try {
				long reminderTimeStamp = ApplicationUtil.getUpdatedTimestampFromDate(reminderTime);
				if(reminderValueSelected == 0)
				{
					ReminderValText ="";
				}
				else if(reminderValueSelected == 1)
				{

					reminderTimeStamp = reminderTimeStamp - 5*60;
					ReminderValText = ApplicationUtil.changeToDayMonthYear2(reminderTimeStamp);
				}
				else if(reminderValueSelected == 2)
				{
					reminderTimeStamp = reminderTimeStamp - 10*60;
					ReminderValText = ApplicationUtil.changeToDayMonthYear2(reminderTimeStamp);
				}
				else if(reminderValueSelected == 3)
				{
					reminderTimeStamp = reminderTimeStamp - 30*60;
					ReminderValText = ApplicationUtil.changeToDayMonthYear2(reminderTimeStamp);
				}
				else if(reminderValueSelected == 4)
				{
					reminderTimeStamp = reminderTimeStamp - 60*60;
					ReminderValText = ApplicationUtil.changeToDayMonthYear2(reminderTimeStamp);
				}
				else if(reminderValueSelected == 5)
				{
					reminderTimeStamp = reminderTimeStamp - 3*60*60;
					ReminderValText = ApplicationUtil.changeToDayMonthYear2(reminderTimeStamp);
				}
				else if(reminderValueSelected == 6)
				{
					reminderTimeStamp = reminderTimeStamp - 24*60*60;
					ReminderValText = ApplicationUtil.changeToDayMonthYear2(reminderTimeStamp);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(action.trim().equalsIgnoreCase("reminder"))
		{
			String targetDate = "";
			
			if(selectedDate != null && !selectedDate.trim().equals(""))
			{
				Date mDate= new Date(selectedDate);
				SimpleDateFormat mDateFormat= new SimpleDateFormat("dd/MM/yyyy");
				dateValText = mDateFormat.format(mDate);
			}
			else
			{
				Date mDate= new Date(System.currentTimeMillis());
				SimpleDateFormat mDateFormat= new SimpleDateFormat("dd/MM/yyyy");
				SimpleDateFormat mDateFormat1= new SimpleDateFormat("dd-MMM-yyyy");
				dateValText = mDateFormat.format(mDate);
				selectedDate = mDateFormat1.format(mDate);
			}
			if(min <= 9)
				timeValText = hour+":0"+min;
			else
				timeValText = hour+":"+min;
			System.out.println("Selected date is----"+selectedDate);
				targetDate = selectedDate +" "+timeValText;



			try {
				changedTargetTimeStamp = ApplicationUtil.getUpdatedTimestampFromDate2(targetDate);
				if(reminderValueSelected == 0)
					changedReminderTimeStamp =0;
				else if(reminderValueSelected == 1)
					changedReminderTimeStamp = changedTargetTimeStamp - 5*60;
				else if(reminderValueSelected == 2)
					changedReminderTimeStamp = changedTargetTimeStamp - 10*60;
				else if(reminderValueSelected == 3)
					changedReminderTimeStamp = changedTargetTimeStamp - 30*60;
				else if(reminderValueSelected == 4)
					changedReminderTimeStamp = changedTargetTimeStamp - 60*60;
				else if(reminderValueSelected == 5)
					changedReminderTimeStamp = changedTargetTimeStamp - 3*60*60;
				else if(reminderValueSelected == 6)
					changedReminderTimeStamp = changedTargetTimeStamp - 24*60*60;



			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			reminderVal = ReminderValText;
			String changedReminderDate = ApplicationUtil.changeToDayMonthYear2(changedReminderTimeStamp);
			if(changedReminderTimeStamp != receivedReminderTimeStamp)
			{
				Toast.makeText(context, getResources().getString(R.string.change_reminder_toast), Toast.LENGTH_LONG).show();
				ReminderValText = changedReminderDate;
				changeReminder(changedReminderDate+":00");
			}
			else
			{
				ReminderValText = changedReminderDate;
				Toast.makeText(context, getResources().getString(R.string.same_reminder_toast), Toast.LENGTH_LONG).show();
			}
			String changedDate = ApplicationUtil.changeToDayMonthYear2(changedTargetTimeStamp);
			if(receivedTargetTimeStamp != changedTargetTimeStamp)
			{
				changeTarget(changedDate+":00");
				targetDateVal = changedDate;
				Toast.makeText(context, getResources().getString(R.string.change_target_toast), Toast.LENGTH_LONG).show();
			}
			else
			{
				targetDateVal = changedDate;
				Toast.makeText(context, getResources().getString(R.string.same_target_toast), Toast.LENGTH_LONG).show();
			}



			
		}
		if(action.trim().equalsIgnoreCase("alarm"))
		{
			String alarmTimeVal = "";

			if(selectedDate !=null && !selectedDate.trim().equals(""))
			{
				Date mDate= new Date(selectedDate);
				SimpleDateFormat mDateFormat= new SimpleDateFormat("dd/MM/yyyy");
				alarmTimeVal = mDateFormat.format(mDate);
			}
			else
			{
				Date mDate= new Date(System.currentTimeMillis());
				SimpleDateFormat mDateFormat= new SimpleDateFormat("dd/MM/yyyy");
				alarmTimeVal = mDateFormat.format(mDate);
			}
			if(min <= 9)
				alarmTimeVal =alarmTimeVal+" "+  hour+":0"+min;
			else
				alarmTimeVal =alarmTimeVal+" "+  hour+":"+min;
			try {
				alarmTimeVal = alarmTimeVal+":00";
				long changedAlarmTimeStamp = ApplicationUtil.getUpdatedTimestampFromDate3(alarmTimeVal);
				long receivedAlarmTimeStamp = 0;
				if(alarmTime != null && alarmTime.trim().length()>0)
					receivedAlarmTimeStamp = ApplicationUtil.getUpdatedTimestampFromDate3(alarmTime);
				if(receivedAlarmTimeStamp != changedAlarmTimeStamp)
				{
					setAlarm(alarmTimeVal ,taskId);
					Toast.makeText(context, getResources().getString(R.string.change_alarm_toast), Toast.LENGTH_LONG).show();
				}
				else
				{
					Toast.makeText(context, getResources().getString(R.string.same_alarm_toast), Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		Intent data = new Intent();
		data.putExtra("deadlineDateVal", dateValText);
		data.putExtra("deadlineTimeVal", timeValText);
		data.putExtra("reminderDateVal", ReminderValText);
		data.putExtra("targetDateVal", targetDateVal);
		setResult(123, data);
		super.onBackPressed();
//		overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

	}*/

	private void changeReminder(final String changedReminder) {
		// TODO Auto-generated method stub
		ContentValues initialValues = new ContentValues();
		initialValues.put("Reminder_Time", changedReminder);
		initialValues.put("IsReminderSync", "N");
		initialValues.put("UPDATED_DATE", ApplicationUtil.getGMTDate());
		initialValues.put("UPDATION_DATE_TIME",ApplicationUtil.getGMTLong());
		
		ApplicationUtil.getInstance().updateDataInDB("Task", initialValues, context, "Task_ID = '"+taskId+"'", null);
		SyncModule syncObj = new SyncModule(context);
		if(changedReminder != null && !changedReminder.trim().equals(""))
			reminderVal = changedReminder;
		//syncObj.S();
		new Thread(){
			@Override
			public void run() {
		try {
			String status = ApplicationUtil.getInstance().getSyncServer(context)
				.updateReminder(taskId,changedReminder,ApplicationUtil.getPreference(ApplicationConstant.regMobNo,context));
			if("00".equals(status)){
				ContentValues initialValues = new ContentValues();
				initialValues.put("Reminder_Time", changedReminder);
				initialValues.put("IsReminderSync", "Y");
				ApplicationUtil.getInstance().updateDataInDB("Task",initialValues,
						context,
						"Task_ID ='" + taskId
						+ "'", null);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			}
			
		};
		syncObj = null;

	}
	private void changeTarget(String changedTarget) {
		ContentValues initialValues = new ContentValues();
		try {
			SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			initialValues.put("Target_Date_TIME",dateFormat.parse(changedTarget).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		initialValues.put("Target_Date", changedTarget);
		initialValues.put("IsTargetSync", "N");
		initialValues.put("UPDATED_DATE", ApplicationUtil.getGMTDate());
		initialValues.put("UPDATION_DATE_TIME",ApplicationUtil.getGMTLong());
		ApplicationUtil.getInstance().updateDataInDB("Task", initialValues, context, "Task_ID = '"+taskId+"'", null);
		SyncModule syncObj = new SyncModule(context);
		syncObj.UpdateTargetDate(taskId,changedTarget);
		syncObj = null;

	}

	// ///////////////////////////////////////////////////////////////////////////////////////
	// Inner Class
	public class GridCellAdapter extends BaseAdapter implements OnClickListener
	{

		private final Context _context;

		private final List<String> list;
		private static final int DAY_OFFSET = 1;
		private final String[] weekdays = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
		private final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
		private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
		private final int month, year;
		private int daysInMonth;
		private int currentDayOfMonth;
		private int currentWeekDay;
		private TextView gridcell;
		private TextView num_events_per_day;
		private final HashMap eventsPerMonthMap;
		private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");

		// Days in Current Month
		public GridCellAdapter(Context context, int textViewResourceId, int month, int year)
		{
			super();
			this._context = context;
			this.list = new ArrayList<String>();
			this.list.add("S");
			this.list.add("M");
			this.list.add("T");
			this.list.add("W");
			this.list.add("T");
			this.list.add("F");
			this.list.add("S");
			this.month = month;
			this.year = year;
			
			if(isAlarmSet!=null&&action!=null&&isAlarmSet.equalsIgnoreCase("Y")&&action!=null&&action.trim().equals("shedulealarm"))
			{
				Calendar calendar = Calendar.getInstance();
				calendar.set(year, month, alarm_selected_date);
				setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
				setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
		
			}
			else
			{

				Calendar calendar = Calendar.getInstance();
				setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
				setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
		
			}
			
				// Print Month
			printMonth(month, year);

			// Find Number of Events
			eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
		}
		private String getMonthAsString(int i)
		{
			return months[i];
		}

		private String getWeekDayAsString(int i)
		{
			return weekdays[i];
		}

		private int getNumberOfDaysOfMonth(int i)
		{
			return daysOfMonth[i];
		}

		public String getItem(int position)
		{
			return list.get(position);
		}

		@Override
		public int getCount()
		{
			return list.size();
		}

		/**
		 * Prints Month
		 * @param mm
		 * @param yy
		 */
		private void printMonth(int mm, int yy)
		{
			// The number of days to leave blank at
			// the start of this month.
			int trailingSpaces = 0;
			int leadSpaces = 0;
			int daysInPrevMonth = 0;
			int prevMonth = 0;
			int prevYear = 0;
			int nextMonth = 0;
			int nextYear = 0;

			int currentMonth = mm - 1;
			String currentMonthName = getMonthAsString(currentMonth);
			//			String currentWeekDayStr = getWeekDayAsString(currentMonth);
			daysInMonth = getNumberOfDaysOfMonth(currentMonth);

			// Gregorian Calendar : MINUS 1, set to FIRST OF MONTH
			GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);
			if (currentMonth == 11)
			{
				prevMonth = currentMonth - 1;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 0;
				prevYear = yy;
				nextYear = yy + 1;
			}
			else if (currentMonth == 0)
			{
				prevMonth = 11;
				prevYear = yy - 1;
				nextYear = yy;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 1;
			}
			else
			{
				prevMonth = currentMonth - 1;
				nextMonth = currentMonth + 1;
				nextYear = yy;
				prevYear = yy;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
			}

			// Compute how much to leave before before the first day of the
			// month.
			// getDay() returns 0 for Sunday.
			int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
			trailingSpaces = currentWeekDay;

			if (cal.isLeapYear(cal.get(Calendar.YEAR)) && mm == 1)
			{
				++daysInMonth;
			}

			// Trailing Month days
			for (int i = 0; i < trailingSpaces; i++)
			{
				list.add(String.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i) + "-GREY" + "-" + getMonthAsString(prevMonth) + "-" + prevYear);
			}

			// Current Month Days
			for (int i = 1; i <= daysInMonth; i++)
			{
				if(isAlarmSet!=null&&action!=null&&isAlarmSet.equalsIgnoreCase("Y")&&action!=null&&action.trim().equals("shedulealarm"))
				{
					if (i == getCurrentDayOfMonth() && currentMonth +1 == alarm_selected_month && yy== alarm_selected_year)
					{
						list.add(String.valueOf(i) + "-BLUE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
					}
					else
					{
						list.add(String.valueOf(i) + "-WHITE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
					}
				}
				else
				{
					if (i == getCurrentDayOfMonth() && currentMonth +1 == presentMonth && yy== presentYear )
					{
						list.add(String.valueOf(i) + "-BLUE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
					}
					else
					{
						list.add(String.valueOf(i) + "-WHITE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
					}
				}
			}

			// Leading Month days
			for (int i = 0; i < list.size() % 7; i++)
			{
				list.add(String.valueOf(i + 1) + "-GREY" + "-" + getMonthAsString(nextMonth) + "-" + nextYear);
			}
		}

		/**
		 * NOTE: YOU NEED TO IMPLEMENT THIS PART Given the YEAR, MONTH, retrieve
		 * ALL entries from a SQLite database for that month. Iterate over the
		 * List of All entries, and get the dateCreated, which is converted into
		 * day.
		 * 
		 * @param year
		 * @param month
		 * @return
		 */
		private HashMap findNumberOfEventsPerMonth(int year, int month)
		{
			HashMap map = new HashMap<String, Integer>();
			return map;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View row = convertView;
			if (row == null)
			{
				LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.calendar_day_gridcell, parent, false);

			}

			// Get a reference to the Day gridcell
			gridcell = (TextView) row.findViewById(R.id.num_events_per_day);

			gridcell.setOnClickListener(this);

			// ACCOUNT FOR SPACING

			if(position >= 0 && position <= 6)
			{
				gridcell.setText(list.get(position));
				gridcell.setClickable(false);
				gridcell.setTextColor(getResources().getColor(R.color.black));
				return row;
			}
			else
			{
				String[] day_color = list.get(position).split("-");
				String theday = day_color[0];
				String themonth = day_color[2];
				String theyear = day_color[3];

				if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null))
				{
					if (eventsPerMonthMap.containsKey(theday))
					{
						num_events_per_day = (TextView) row.findViewById(R.id.num_events_per_day);
						Integer numEvents = (Integer) eventsPerMonthMap.get(theday);
						num_events_per_day.setText(numEvents.toString());
					}
				}

				// Set the Day GridCell
				gridcell.setText(theday);
				gridcell.setTag(theday + "-" + themonth + "-" + theyear);
				if (day_color[1].equals("GREY"))
				{
					gridcell.setTextColor(Color.LTGRAY);
					gridcell.setText("");
					gridcell.setClickable(false);
				}
				if (day_color[1].equals("WHITE"))
				{

					if(presentYear > year)
					{
						if(selectedDate != null && !selectedDate.trim().equals("") && selectedDate.equals((String) gridcell.getTag()))
						{
							gridcell.setBackgroundColor(getResources().getColor(R.color.blue));
							gridcell.setTextColor(Color.WHITE);
						}
						else
						{
							gridcell.setTextColor(Color.LTGRAY);
						}
						gridcell.setClickable(false);
					}
					else
						if(presentYear == year && presentMonth > month)
						{
							if(selectedDate != null && !selectedDate.trim().equals("") && selectedDate.equals((String) gridcell.getTag()))
							{
								gridcell.setBackgroundColor(getResources().getColor(R.color.blue));
								gridcell.setTextColor(Color.WHITE);
							}
							else
							{
								gridcell.setTextColor(Color.LTGRAY);
							}
							gridcell.setClickable(false);

						}
						else
							if(presentYear == year && presentMonth == month && presentDate > Integer.valueOf(theday))

							{
								if(selectedDate != null && !selectedDate.trim().equals("") && selectedDate.equals((String) gridcell.getTag()))
								{
									gridcell.setBackgroundColor(getResources().getColor(R.color.blue));
									gridcell.setTextColor(Color.WHITE);
								}
								else
								{
									gridcell.setTextColor(Color.LTGRAY);
								}
								gridcell.setClickable(false);

							}
							else
							{
								if(selectedDate != null && !selectedDate.trim().equals("") && selectedDate.equals((String) gridcell.getTag()))
								{
									gridcell.setBackgroundColor(getResources().getColor(R.color.blue));
								}

								gridcell.setTextColor(Color.BLACK);
							}

				}
				if (day_color[1].equals("BLUE"))
				{
					gridcell.setTextColor(Color.WHITE);
					gridcell.setBackgroundColor(getResources().getColor(R.color.dark_gray));


				}

				return row;
			}
		}
		@Override
		public void onClick(View view)
		{
			if(prevSelection != null && !prevSelection.getTag().toString().trim().equals(""))
				prevSelection.setBackgroundColor(0);
			String date_month_year = (String) view.getTag();
			prevSelection = view;
			selectedDate = date_month_year;
			view.setBackgroundColor(getResources().getColor(R.color.blue));

			try
			{
				Date parsedDate = dateFormatter.parse(date_month_year);
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
		}

		public int getCurrentDayOfMonth()
		{
			return currentDayOfMonth;
		}

		private void setCurrentDayOfMonth(int currentDayOfMonth)
		{
			this.currentDayOfMonth = currentDayOfMonth;
		}
		public void setCurrentWeekDay(int currentWeekDay)
		{
			this.currentWeekDay = currentWeekDay;
		}
		public int getCurrentWeekDay()
		{
			return currentWeekDay;
		}
	}

	@Override
	protected void onUserLeaveHint() {
		// TODO Auto-generated method stub

		super.onUserLeaveHint();

	}

	private void setAlarm(String alarmTme, String taskId)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put("AlarmTime", alarmTme);
		ApplicationUtil.getInstance().updateDataInDB("Task", initialValues, context, "Task_ID ='"+taskId+"'", null);


	}
	/**
	 * Method used to set alarm
	 * @param context
	 * @param date
	 * @param id
	 */
	
	public void setAlarmManager(Context context, String date, String id,String taskDesc) {
		try {
			long reminderTimeStamp = 0;
			
				reminderTimeStamp = ApplicationUtil
						.getUpdatedTimestampFromDate(date);

			long currentTime = ApplicationUtil.getCurrentUnixTime();
			
			long difference = (reminderTimeStamp - currentTime);
			if(difference>0)
			{

				SimpleDateFormat smDtfm = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				// System.out.println("date is---->"+smDtfm.parse(date).getTime());
				long alarm_time = 0;
				try {
					alarm_time = smDtfm.parse(date).getTime();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
				
	
				Intent intent = new Intent(context.getApplicationContext(),
						com.taskmanager.background.AlarmTaskReciever.class);
				// intent.setAction("com.braoadcast.alarm");TaskDesc
				intent.putExtra("TaskId", id);
				intent.putExtra("TaskDesc", taskDesc);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						context.getApplicationContext(), Integer.parseInt(id), intent,
						Intent.FLAG_ACTIVITY_NEW_TASK);
				AlarmManager alarmManager = (AlarmManager) context
						.getSystemService(context.ALARM_SERVICE);
				alarmManager.set(AlarmManager.RTC_WAKEUP, alarm_time, pendingIntent);
			//}
		//	else
		//	{
		//		Toast.makeText(CalenderActivity.this, "Date or Time is not correct.", Toast.LENGTH_LONG).show();
		//	}
			}
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
	}
	
	


}