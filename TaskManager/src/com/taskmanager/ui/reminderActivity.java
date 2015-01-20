package com.taskmanager.ui;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taskmanager.app.R;
import com.taskmanager.background.ReminderReceiver;
import com.taskmanager.background.SyncModule;
import com.taskmanager.util.ApplicationUtil;

/**
 * Activity for reminder popup
 * @author mayankb
 *
 */
public class reminderActivity extends Activity {


	private Context ctx;
	String taskId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ctx = this;

		taskId = getIntent().getStringExtra("TaskId");
		ArrayList<ArrayList<String>> taskDesc = ApplicationUtil.getInstance().uploadListFromDB("Task", new String[]{"Task_desc"}, "Task_ID ='"+taskId+"'", ctx);
		if(taskDesc!= null && !taskDesc.isEmpty())
			displayDialog(ctx, taskDesc.get(0).get(0));

	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	} 


	/**
	 * This metthod display popup 
	 * @param msg
	 * @param view
	 */
	private void displayDialog(final Context ctx, String msg)
	{

		final Dialog dialog = new Dialog(ctx,android.R.style.Theme_Translucent_NoTitleBar) ;

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.reminder_popup);
		dialog.setCancelable(false);
		TextView popupMsg =(TextView) dialog.findViewById(R.id.popupmsg);
		popupMsg.setText(msg);
		LinearLayout dismiss = (LinearLayout)dialog.findViewById(R.id.dismissicon);
		dismiss.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				finish();

			}
		});
		LinearLayout done = (LinearLayout)dialog.findViewById(R.id.doneicon);
		done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ContentValues initialValues = new ContentValues();
				initialValues.put("Status", "CLOSE");
				initialValues.put("IsStatusSync", "N");
				ApplicationUtil.getInstance().updateDataInDB("Task", initialValues, ctx, "Task_ID ='"+taskId+"'", null);
				SyncModule syncObj = new SyncModule(ctx);
				syncObj.UpdateStatus();
				syncObj = null;
				dialog.dismiss();
				finish();

			}
		});
		LinearLayout Snooze = (LinearLayout)dialog.findViewById(R.id.snoozeicon);
		Snooze.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addReminder(10*60, taskId);
				dialog.dismiss();
				finish();

			}
		});
		dialog.show();

	}

	/**
	 * method to add reminder
	 * @param difference
	 * @param Id
	 */
	private void addReminder(int difference, String Id) 
	{
		// TODO Auto-generated method stub
		Intent intent = new Intent(reminderActivity.this, ReminderReceiver.class);
		intent.putExtra("TaskId", Id);
		PendingIntent sender = PendingIntent.getBroadcast(ctx,0, intent, 0);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.SECOND, difference);

		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
	}

	

	
}
