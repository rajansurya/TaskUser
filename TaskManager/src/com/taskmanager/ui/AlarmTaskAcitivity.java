package com.taskmanager.ui;

import java.text.SimpleDateFormat;
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
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taskmanager.app.R;
import com.taskmanager.background.AlarmTaskReciever;
import com.taskmanager.background.SyncModule;
import com.taskmanager.util.ApplicationConstant;
import com.taskmanager.util.ApplicationUtil;
import com.taskmanager.util.CommonUtil;
import com.taskmanager.widgets.DialogMenu;

public class AlarmTaskAcitivity extends Activity {
	



	private Context ctx;
	String taskId = "";
	//String TaskDesc = "";
	private LinearLayout layout = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ctx = this;

		taskId = getIntent().getStringExtra("TaskId");
		ArrayList<ArrayList<String>> taskDesc = ApplicationUtil.getInstance().uploadListFromDB("Task", new String[]{"Task_Desc","Assign_From","Assign_To","TaskType"}, "Task_ID ='"+taskId+"'", ctx);
		if(taskDesc!= null && !taskDesc.isEmpty())
		{
			if(ApplicationUtil.getPreference(ApplicationConstant.regMobNo,AlarmTaskAcitivity.this) != null&&CommonUtil.getValidMsisdn(ApplicationUtil.getPreference(ApplicationConstant.regMobNo,AlarmTaskAcitivity.this)).equals(CommonUtil.getValidMsisdn(taskDesc.get(0).get(1)))&&CommonUtil.getValidMsisdn(ApplicationUtil.getPreference(ApplicationConstant.regMobNo,AlarmTaskAcitivity.this)).equals(CommonUtil.getValidMsisdn(taskDesc.get(0).get(2))))
			{
				displayDialog(ctx, taskDesc.get(0).get(0),"");
			}
			else if(taskDesc.get(0)!=null&&taskDesc.get(0).get(3).equalsIgnoreCase("inbox"))
			{
				String msginbox=taskDesc.get(0).get(0);
				if(taskDesc.get(0).get(1)!=null)
				displayDialog(ctx, msginbox,ApplicationUtil.getContactNameFromPhoneNo(ctx, taskDesc.get(0).get(1)));
			}
			else if(taskDesc.get(0)!=null&&taskDesc.get(0).get(3).equalsIgnoreCase("sent"))
			{
				String msg = taskDesc.get(0).get(0);
				if(taskDesc.get(0).get(2)!=null)
				displayDialog(ctx, msg,ApplicationUtil.getContactNameFromPhoneNo(ctx, taskDesc.get(0).get(2)));
			}
		}

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
	private void displayDialog(final Context ctx, String msg,String assignFrom) {
		String assignFromName= assignFrom;
		//String message = assignFromName+" "+msg;

		final DialogMenu dialog = new DialogMenu(ctx, R.layout.alarmtask_popup) ;
		dialog.setCancelable(false);
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		getWindow().setAttributes(params);
		dialog.show();
		
		TextView popupMsg =(TextView) dialog.findViewById(R.id.popupmsg);
		TextView txtassignFrom =(TextView) dialog.findViewById(R.id.assignFrom);
		txtassignFrom.setText(""+assignFromName);
		popupMsg.setText(msg);//assignFrom
		LinearLayout dismiss = (LinearLayout)dialog.findViewById(R.id.dismissicon);
		
		dismiss.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					ContentValues initialValues = new ContentValues();
					initialValues.put("isAlarmSet", "N");
					ApplicationUtil.getInstance().updateDataInDB("Task", initialValues, ctx, "Task_ID ='"+taskId+"'", null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog.dismiss();
				finish();

			}
		});
		
		LinearLayout done = (LinearLayout)dialog.findViewById(R.id.doneicon);
		done.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				ContentValues initialValues = new ContentValues();
				initialValues.put("Status", "CLOSE");
				initialValues.put("IsStatusSync", "N");
				initialValues.put("isAlarmSet", "N");
				ApplicationUtil.getInstance().updateDataInDB("Task", initialValues, ctx, "Task_ID ='"+taskId+"'", null);

			//	reloadListOnScreen();
				SyncModule module=new SyncModule(ctx);
				final String msisdn=CommonUtil.getValidMsisdn(ApplicationUtil.getPreference(ApplicationConstant.regMobNo, ctx));
				module.changeTaskStatus(taskId, "CLOSE", false, msisdn);
				dialog.dismiss();
				finish();
			}
		});
		
		LinearLayout Snooze = (LinearLayout)dialog.findViewById(R.id.snoozeicon);
		Snooze.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
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
	private void addReminder(int difference, String Id) {
		
		Calendar calendarRemind=Calendar.getInstance();
		calendarRemind.add(Calendar.SECOND, difference);
		SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String reminderDate=dateFormat.format(calendarRemind.getTime());
		ContentValues initialValues = new ContentValues();
		initialValues.put("Alarm_Date_Time", reminderDate);
		initialValues.put("UPDATED_DATE", ApplicationUtil.getGMTDate());
		initialValues.put("UPDATION_DATE_TIME",
				ApplicationUtil.getGMTLong());

		ApplicationUtil.getInstance().updateDataInDB("Task", initialValues, ctx, "Task_ID ='" + Id + "'", null);
		Intent intent = new Intent(AlarmTaskAcitivity.this, AlarmTaskReciever.class);
		intent.putExtra("TaskId", Id);
		PendingIntent sender = PendingIntent.getBroadcast(ctx,0, intent, 0);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.SECOND, difference);

		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
	}

	/**
	 * method to broadcast screen to reload task list UI
	 */
	private void reloadListOnScreen() {

		//System.out.println("Reload list called---"
		//		+ ApplicationConstant.needUIRefresh);
		Intent intent = new Intent(
				"com.taskmaganger.ui.AlltaskActivity.relaodList.broadcast");
		ctx.sendBroadcast(intent);
		

	}

	


}
