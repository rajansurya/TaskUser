package com.taskmanager.app.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Random;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.taskmanager.app.R;
import com.taskmanager.util.ApplicationUtil;

public class AppUtility {
	public static void hideKeyboard(Activity activity) {
		//start with an 'always hidden' command for the activity's window
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		//now tell the IMM to hide the keyboard FROM whatever has focus in the activity
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		View currentFocusedView = activity.getCurrentFocus();
		if(currentFocusedView != null) {
			inputMethodManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), 0);
		}
	}
	
	public static void showKeyboard(Activity activity) {
		//start with an 'always hidden' command for the activity's window
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}
	

	public static void setAlarmManager(Context context, String date, String id,String taskDesc) {
		try {
			long reminderTimeStamp = 0;
			reminderTimeStamp = ApplicationUtil .getUpdatedTimestampFromDate(date);
			long currentTime = ApplicationUtil.getCurrentUnixTime();
			long difference = (reminderTimeStamp - currentTime);

			if(difference>0) { 
				SimpleDateFormat smDtfm = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				long alarm_time = 0;
				try {
					alarm_time = smDtfm.parse(date).getTime();
				} catch (ParseException e) {
					e.printStackTrace();
				}

				Intent intent = new Intent(context.getApplicationContext(), com.taskmanager.background.AlarmTaskReciever.class);
				intent.putExtra("TaskId", id);
				intent.putExtra("TaskDesc", taskDesc);
				PendingIntent pendingIntent = PendingIntent.getBroadcast( context.getApplicationContext(), Integer.parseInt(id), intent, Intent.FLAG_ACTIVITY_NEW_TASK);
				AlarmManager alarmManager = (AlarmManager) context .getSystemService(context.ALARM_SERVICE);
				alarmManager.set(AlarmManager.RTC_WAKEUP, alarm_time, pendingIntent);
			}
		}  catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static int [] sideMenuHeaderBg = new int[]{R.drawable.bg_slider_header_1, R.drawable.bg_slider_header_2, 
		R.drawable.bg_slider_header_3, R.drawable.bg_slider_header_4, R.drawable.bg_slider_header_5};
	
	public static int getSideMenuHeaderBackground(){
		Random random = new Random();
		int number = random.nextInt(sideMenuHeaderBg.length);
		return sideMenuHeaderBg[number];
	}
	
	private static String [] quotes = new String[]{
		"Stop waiting for perfect conditions",
		"Don’t say yes to every request",
//		"Focus on your expertise-Outsource what you can",
		"Create a STOP Doing List",
		"Focus on what you can control",
//		"Increase your effectiveness through technology",
		"Use travel time to plan",
		"Keep on redefining what is urgent",
//		"Challenge your team members but not push them",
//		"Break the seal of hesitation for pending tasks",
		"Ruthlessly block out monkeys of others",
		"Monkeys should be fed or shot",
		"Monkeys in drawers become Gorillas",
		"Keep a count of monkeys on your back"};
	
	public static String getSideMenuHeaderQuote(){
		Random random = new Random();
		int number = random.nextInt(quotes.length);
		return quotes[number];
	}
}
