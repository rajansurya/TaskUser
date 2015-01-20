/**
 * 
 */
package com.taskmanager.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract.Contacts;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taskmanager.app.R;
import com.taskmanager.background.DialogCallback;
import com.taskmanager.bean.ChangeAssigneeDto;
import com.taskmanager.dto.TaskInfoEntity;
import com.taskmanager.util.ApplicationConstant;
import com.taskmanager.util.ApplicationUtil;
import com.taskmanager.util.CommonUtil;
import com.taskmanager.wheel.widget.OnWheelChangedListener;
import com.taskmanager.wheel.widget.WheelView;
import com.taskmanager.wheel.widget.adapters.DayArrayAdapter;
import com.taskmanager.wheel.widget.adapters.NumericWheelAdapter;

/**
 * @author IBM_ADMIN
 *
 */
public class CommonUtilsUi {
	
	public static void getCustomeDialog(Context context,String text){
		
		final Dialog dialog = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar);
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.gravity = (Gravity.CENTER);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custome_alert_view);
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		dialog.getWindow().setAttributes(lp);
		Button okButton=	(Button)dialog.findViewById(R.id.okButton);
		TextView alertText=(TextView)dialog.findViewById(R.id.alertText);
		alertText.setText(text);
		okButton.setOnClickListener(new OnClickListener() {
		@Override
			public void onClick(View v) {
				dialog.dismiss();
				
			}
		});
		dialog.show();
	}
	
	
public static Dialog otpGerateDalog(Context context,String text,final DialogCallback callback){
		
		final Dialog dialog = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar);
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.gravity = (Gravity.CENTER);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custome_confirm_alert_view);
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		dialog.getWindow().setAttributes(lp);
		dialog.show();
		Button okButton=	(Button)dialog.findViewById(R.id.otpSubmitButton);
		Button cancel=	(Button)dialog.findViewById(R.id.genrateOTPButton);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
				public void onClick(View v) {
					dialog.dismiss();
					
				}
			});
		TextView alertText=(TextView)dialog.findViewById(R.id.alertText);
		alertText.setText(text);
		okButton.setOnClickListener(new OnClickListener() {
		@Override
			public void onClick(View v) {
			callback.execuet(dialog);
				
			}
		});
		return dialog;
	}
	
public static Dialog getCustomeConfirmDialog(Context context,String text,final DialogCallback callback){
		
		final Dialog dialog = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar);
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.gravity = (Gravity.CENTER);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custome_confirm_alert_view);
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		dialog.getWindow().setAttributes(lp);
		dialog.show();
		Button okButton=	(Button)dialog.findViewById(R.id.okButton);
		Button cancel=	(Button)dialog.findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
				public void onClick(View v) {
					dialog.dismiss();
					
				}
			});
		TextView alertText=(TextView)dialog.findViewById(R.id.alertText);
		alertText.setText(text);
		okButton.setOnClickListener(new OnClickListener() {
		@Override
			public void onClick(View v) {
			callback.execuet(dialog);
				
			}
		});
		return dialog;
	}
public static void getCustomeDialog(Context context,String text,final DialogCallback callback){
		
		final Dialog dialog = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar);
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.gravity = (Gravity.CENTER);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custome_alert_view);
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		dialog.getWindow().setAttributes(lp);
		dialog.show();
		Button okButton=	(Button)dialog.findViewById(R.id.okButton);
		TextView alertText=(TextView)dialog.findViewById(R.id.alertText);
		alertText.setText(text);
		okButton.setOnClickListener(new OnClickListener() {
		@Override
			public void onClick(View v) {
				//dialog.dismiss();
				callback.execuet(dialog);
				
			}
		});
	}
public static void getCustomeDialogWithClose(Context context,String text){
		
		final Dialog dialog = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar);
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.gravity = (Gravity.CENTER);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custome_alert_view);
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		dialog.getWindow().setAttributes(lp);
		dialog.show();
		Button okButton=	(Button)dialog.findViewById(R.id.okButton);
		TextView alertText=(TextView)dialog.findViewById(R.id.alertText);
		alertText.setText(text);
		okButton.setOnClickListener(new OnClickListener() {
		@Override
			public void onClick(View v) {
				dialog.dismiss();
				
			}
		});
	}
	

	public  void showAlarmCalender(final Context context,final TaskInfoEntity entity,final ImageView imageView ){
		final Dialog dialog = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar);
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.gravity = (Gravity.CENTER);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setGravity(Gravity.BOTTOM);
		dialog.setContentView(R.layout.calender_screen_new);
		dialog.getWindow().setAttributes(lp);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		LinearLayout linearOk=(LinearLayout) dialog.findViewById(R.id.linearOk);
		LinearLayout linearCancel=(LinearLayout) dialog.findViewById(R.id.linearCancel);
		wlp.gravity = Gravity.BOTTOM;
		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(wlp);
		final WheelView hours = (WheelView) dialog.findViewById(R.id.hour);
		NumericWheelAdapter hourAdapter = new NumericWheelAdapter(context, 0, 23);
		hourAdapter.setItemResource(R.layout.wheel_text_item);
		hourAdapter.setItemTextResource(R.id.text);
		hours.setViewAdapter(hourAdapter);
		hours.setCyclic(true);
		final WheelView mins = (WheelView) dialog.findViewById(R.id.mins);
		NumericWheelAdapter minAdapter = new NumericWheelAdapter(context, 0, 59);
		minAdapter.setItemResource(R.layout.wheel_text_item);
		minAdapter.setItemTextResource(R.id.text);
		mins.setViewAdapter(minAdapter);
		mins.setCyclic(true);
		int curIndex=0;
		Date alrmdate=null;
		Calendar calendarArm=Calendar.getInstance();
		if(entity.getAlarm_Date_Time()!=null){
			SimpleDateFormat dateFormat1=new SimpleDateFormat("dd/MM/yyyy HH:mm");
			try {
				alrmdate=dateFormat1.parse(entity.getAlarm_Date_Time());
				calendarArm.setTime(alrmdate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Calendar calendar = Calendar.getInstance();
		//calendar.set(Calendar.YEAR, 2014);
		calendar.set(2014, 0,1);
		//hours.setCurrentItem(calendar.get(Calendar.HOUR));
		//mins.setCurrentItem(calendar.get(Calendar.MINUTE));
		int daysCount=0;
		final WheelView day = (WheelView) dialog.findViewById(R.id.day);
		ArrayList<String> dates=new ArrayList<String>();
		for(int i=0;i<=364;i++){
			int day1 = -daysCount / 2 + i;
			Calendar newCalendar = (Calendar) calendar.clone();
			newCalendar.roll(Calendar.DAY_OF_YEAR, day1);
			dates.add(getDate(newCalendar.getTimeInMillis()));//alrmdate
		if(alrmdate!=null){
			if(getDate(newCalendar.getTimeInMillis()).equalsIgnoreCase(getDate(alrmdate.getTime()))){
				curIndex=i;
			}
		}else{
				if(getDate(Calendar.getInstance().getTimeInMillis()).equalsIgnoreCase(getDate(newCalendar.getTimeInMillis()))){
					curIndex=i;
				}
			}
		
		}
		
		DayArrayAdapter arrayAdapter=	new DayArrayAdapter(context, dates);
		day.setViewAdapter(arrayAdapter);
		day.setCyclic(true);
		day.setCurrentItem(curIndex);
		String	currentItem=	day.getItemVale();
		System.out.println(currentItem+"");
		SimpleDateFormat dateFormat=new SimpleDateFormat("dd,MMM,yyyy");
		try {
			finalCalendar.setTime(dateFormat.parse(currentItem));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Calendar currentDate=Calendar.getInstance();
		currentDate.setTime(new Date());
		finalh=currentDate.get(Calendar.HOUR_OF_DAY);
		finalM=currentDate.get(Calendar.MINUTE);
		if(entity.getAlarm_Date_Time()!=null){
			finalh=calendarArm.get(Calendar.HOUR_OF_DAY);
			finalM=calendarArm.get(Calendar.MINUTE);
		}
		hours.setCurrentItem(finalh);
		mins.setCurrentItem(finalM);
		//finalCalendar.add(Calendar.HOUR, calendar.get(Calendar.HOUR));
		//finalCalendar.add(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
		dialog.show();
		linearOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finalCalendar.add(Calendar.HOUR, finalh);
				finalCalendar.add(Calendar.MINUTE, finalM);
				Date date=finalCalendar.getTime();
				SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm");
				final String targetDateVal=dateFormat.format(finalCalendar.getTime());
				if(new Date().after(date)){
					dialog.dismiss();
					return;
				}
				ContentValues initialValues = new ContentValues();
				initialValues.put("isAlarmSet", "Y");
				initialValues.put("Alarm_Date_Time", targetDateVal);
				initialValues.put("UPDATED_DATE", ApplicationUtil.getGMTDate());
				initialValues.put("UPDATION_DATE_TIME",ApplicationUtil.getGMTLong());
				ApplicationUtil.getInstance().updateDataInDB("Task",initialValues, context,"Task_ID ='" + entity.getId() + "'", null);
				System.out.println(date);
				if(targetDateVal!=null){
					imageView.setBackgroundResource(R.drawable.alarm_blue_icon);
					entity.setAlarmBackground(R.drawable.alarm_blue_icon);
					entity.setAlarm_Date_Time(targetDateVal);
				}
				Thread thread = new Thread()
				{
					public void run() 
					{
						if(targetDateVal!=null)
							setAlarmManager(context,targetDateVal,entity.getId()+"",entity.getTask_description());
					};
				};
				thread.start();
				dialog.dismiss();
				
			}
		});
		linearCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
			}
		});
		day.addChangingListener(new OnWheelChangedListener() {
			
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				try {
					String	currentItem=	wheel.getItemVale();
					System.out.println(currentItem);
					SimpleDateFormat dateFormat=new SimpleDateFormat("dd,MMM,yyyy");
					finalCalendar.setTime(dateFormat.parse(currentItem));
					System.out.println(finalCalendar.toString());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
			}
		});
		hours.addChangingListener(new OnWheelChangedListener() {
			
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				finalh=newValue;
				
				
			}
		});
		mins.addChangingListener(new OnWheelChangedListener() {
	
	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		finalM=newValue;
		
		
	}
});
		 
	}
	
		

		
		
		public  void showAlarmCalenderOnCreate(final Context context,final TaskInfoEntity entity,final ImageView imageView ){
			final Dialog dialog = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar);
			WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
			lp.gravity = (Gravity.CENTER);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.getWindow().setGravity(Gravity.BOTTOM);
			dialog.setContentView(R.layout.calender_screen_new);
			dialog.getWindow().setAttributes(lp);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);
			Window window = dialog.getWindow();
			WindowManager.LayoutParams wlp = window.getAttributes();
			LinearLayout linearOk=(LinearLayout) dialog.findViewById(R.id.linearOk);
			LinearLayout linearCancel=(LinearLayout) dialog.findViewById(R.id.linearCancel);
			wlp.gravity = Gravity.BOTTOM;
			wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			window.setAttributes(wlp);
			final WheelView hours = (WheelView) dialog.findViewById(R.id.hour);
			NumericWheelAdapter hourAdapter = new NumericWheelAdapter(context, 0, 23);
			hourAdapter.setItemResource(R.layout.wheel_text_item);
			hourAdapter.setItemTextResource(R.id.text);
			hours.setViewAdapter(hourAdapter);
			hours.setCyclic(true);
			final WheelView mins = (WheelView) dialog.findViewById(R.id.mins);
			NumericWheelAdapter minAdapter = new NumericWheelAdapter(context, 0, 59);
			minAdapter.setItemResource(R.layout.wheel_text_item);
			minAdapter.setItemTextResource(R.id.text);
			mins.setViewAdapter(minAdapter);
			mins.setCyclic(true);
			int curIndex=0;
			Date alrmdate=null;
			Calendar calendarArm=Calendar.getInstance();
			if(entity.getAlarm_Date_Time()!=null){
				SimpleDateFormat dateFormat1=new SimpleDateFormat("dd/MM/yyyy HH:mm");
				try {
					alrmdate=dateFormat1.parse(entity.getAlarm_Date_Time());
					calendarArm.setTime(alrmdate);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Calendar calendar = Calendar.getInstance();
			//calendar.set(Calendar.YEAR, 2014);
			calendar.set(2014, 0,1);
			//hours.setCurrentItem(calendar.get(Calendar.HOUR));
			//mins.setCurrentItem(calendar.get(Calendar.MINUTE));
			int daysCount=0;
			final WheelView day = (WheelView) dialog.findViewById(R.id.day);
			ArrayList<String> dates=new ArrayList<String>();
			for(int i=0;i<=364;i++){
				int day1 = -daysCount / 2 + i;
				Calendar newCalendar = (Calendar) calendar.clone();
				newCalendar.roll(Calendar.DAY_OF_YEAR, day1);
				dates.add(getDate(newCalendar.getTimeInMillis()));//alrmdate
			if(alrmdate!=null){
				if(getDate(newCalendar.getTimeInMillis()).equalsIgnoreCase(getDate(alrmdate.getTime()))){
					curIndex=i;
				}
			}else{
					if(getDate(Calendar.getInstance().getTimeInMillis()).equalsIgnoreCase(getDate(newCalendar.getTimeInMillis()))){
						curIndex=i;
					}
				}
			
			}
			
			DayArrayAdapter arrayAdapter=	new DayArrayAdapter(context, dates);
			day.setViewAdapter(arrayAdapter);
			day.setCyclic(true);
			day.setCurrentItem(curIndex);
			String	currentItem=	day.getItemVale();
			System.out.println(currentItem+"");
			SimpleDateFormat dateFormat=new SimpleDateFormat("dd,MMM,yyyy");
			try {
				finalCalendar.setTime(dateFormat.parse(currentItem));
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Calendar currentDate=Calendar.getInstance();
			currentDate.setTime(new Date());
			finalh=currentDate.get(Calendar.HOUR_OF_DAY);
			finalM=currentDate.get(Calendar.MINUTE);
			if(entity.getAlarm_Date_Time()!=null){
				finalh=calendarArm.get(Calendar.HOUR_OF_DAY);
				finalM=calendarArm.get(Calendar.MINUTE);
			}
			hours.setCurrentItem(finalh);
			mins.setCurrentItem(finalM);
			//finalCalendar.add(Calendar.HOUR, calendar.get(Calendar.HOUR));
			//finalCalendar.add(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
			dialog.show();
			linearOk.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					finalCalendar.add(Calendar.HOUR, finalh);
					finalCalendar.add(Calendar.MINUTE, finalM);
					Date date=finalCalendar.getTime();
					SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm");
					final String targetDateVal=dateFormat.format(finalCalendar.getTime());
					if(new Date().after(date)){
						dialog.dismiss();
						return;
					}
					/*ContentValues initialValues = new ContentValues();
					initialValues.put("isAlarmSet", "Y");
					initialValues.put("Alarm_Date_Time", targetDateVal);
					initialValues.put("UPDATED_DATE", ApplicationUtil.getGMTDate());
					initialValues.put("UPDATION_DATE_TIME",ApplicationUtil.getGMTLong());
					ApplicationUtil.getInstance().updateDataInDB("Task",initialValues, context,"Task_ID ='" + entity.getId() + "'", null);*/
					//System.out.println(date);
					if(targetDateVal!=null){
						/*reminderIcon.setBackgroundDrawable(getResources().getDrawable(
								R.drawable.alarm_blue));*/
						imageView.setBackgroundResource(R.drawable.alarm_blue);
						entity.setAlarmBackground(R.drawable.alarm_blue_icon);
						entity.setAlarm_Date_Time(targetDateVal);
					}
					/*Thread thread = new Thread()
					{
						public void run() 
						{
							if(targetDateVal!=null)
								setAlarmManager(context,targetDateVal,entity.getId()+"",entity.getTask_description());
						};
					};
					thread.start();*/
					dialog.dismiss();
					
				}
			});
			linearCancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					
				}
			});
			day.addChangingListener(new OnWheelChangedListener() {
				
				@Override
				public void onChanged(WheelView wheel, int oldValue, int newValue) {
					try {
						String	currentItem=	wheel.getItemVale();
						System.out.println(currentItem);
						SimpleDateFormat dateFormat=new SimpleDateFormat("dd,MMM,yyyy");
						finalCalendar.setTime(dateFormat.parse(currentItem));
						System.out.println(finalCalendar.toString());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					
				}
			});
			hours.addChangingListener(new OnWheelChangedListener() {
				
				@Override
				public void onChanged(WheelView wheel, int oldValue, int newValue) {
					finalh=newValue;
					
					
				}
			});
			mins.addChangingListener(new OnWheelChangedListener() {
		
		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			finalM=newValue;
			
			
		}
	});
			 
		}
		Calendar finalCalendar=Calendar.getInstance();

		String finalDate="";
		  int finalh=0;
		 int finalM=0;
		
			private static String  getDate(long time){
				SimpleDateFormat dateFormat=new SimpleDateFormat("dd,MMM,yyyy");
				return dateFormat.format(time);
			}

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
			
			public void displayChangeAssignee(final Context context,final TaskInfoEntity entity,final TextView txtAssinorTask) {
				
				final Dialog dialog = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar);
				WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
				lp.gravity = (Gravity.CENTER);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.change_assignee_popup);
				dialog.getWindow().setAttributes(lp);
				final LinearLayout asigneeName = (LinearLayout) dialog
						.findViewById(R.id.asigneename);
				ImageView asigneeIcon = (ImageView) dialog
						.findViewById(R.id.asigneeicon);
				LinearLayout prevAssigneeList = (LinearLayout) dialog
						.findViewById(R.id.prevasigneeslist);
				Button doneBtn = (Button) dialog.findViewById(R.id.donebtn);
				dialog.setCancelable(true);

				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.setMargins(3, 3, 3, 3);
				final LinearLayout prevAssignee = new LinearLayout(context);
				prevAssignee.setGravity(Gravity.CENTER);
				prevAssignee.setLayoutParams(params);
				prevAssignee.setOrientation(LinearLayout.HORIZONTAL);
				prevAssignee.setPadding(10, 10, 10, 10);
				prevAssignee.setBackgroundDrawable(context.getResources().getDrawable(
						R.drawable.asignee_name));

				final LinearLayout changedAssignee = new LinearLayout(context);
				changedAssignee.removeAllViews();

				changedAssignee.setGravity(Gravity.CENTER);
				changedAssignee.setLayoutParams(params);
				changedAssignee.setOrientation(LinearLayout.HORIZONTAL);
				changedAssignee.setPadding(10, 10, 10, 10);
				changedAssignee.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.asignee_name));
				//System.out.println("globalAssigneeName"+globalAssigneeName);

				TextView txtchangeAssignee = new TextView(context);

					if("self".equalsIgnoreCase(entity.getTaskType())){
							txtchangeAssignee.setText("self");
						}else{
							txtchangeAssignee.setText(entity.getUserInfoTo().getFirstName());
						}
					txtchangeAssignee.setTextColor(context.getResources().getColor(R.color.black));
					txtchangeAssignee.setLayoutParams(params);
					txtchangeAssignee.setPadding(0, 0, 5, 0);
					prevAssignee.addView(txtchangeAssignee);
					prevAssigneeList.addView(prevAssignee);
					TextView assignee = new TextView(context);
				
				/*if (assignTo != null&&!assignTo.equalsIgnoreCase("")&&!assignToNum.equalsIgnoreCase("")) {
					if ((ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context) != null
							&& assignToNum != null
							&& ((ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context)).trim().contains(
									assignToNum.trim()))))
						assignee.setText("self");
					else
						assignee.setText(assignTo);*/
					assignee.setTextColor(context.getResources().getColor(R.color.black));
					assignee.setLayoutParams(params);
					assignee.setPadding(0, 0, 5, 0);
					changedAssignee.addView(assignee);

					asigneeName.addView(changedAssignee);
				


				Button btnClose = (Button) dialog.findViewById(R.id.btnClose);

				

				btnClose.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});

				asigneeName.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						asigneeName.removeAllViews();
						Intent intent = new Intent(Intent.ACTION_PICK,
								Contacts.CONTENT_URI);
					
						((Activity)context).startActivityForResult(intent, 1);
						//dialog.dismiss();

					}
				});
				asigneeIcon.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						asigneeName.removeAllViews();
						Intent intent = new Intent(Intent.ACTION_PICK,
								Contacts.CONTENT_URI);
						((Activity)context).startActivityForResult(intent, 1);
						//dialog.dismiss();
					}
				});


				
				doneBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String changedAssigneeNum="";
						// TODO Auto-generated method stub
						//System.out.println(""+taskid);
						////System.out.println(task);
						if (changedAssigneeNum != null
								&& changedAssigneeNum.trim().length() > 0) {
							
							//System.out.println(globalAssigneeName);
							//System.out.println(globalAssigneeNum);
							//System.out.println(changedAssigneeNum);
							//System.out.println(changedAssigneeName);
							ChangeAssigneeDto changeAssigneeDto = new ChangeAssigneeDto();
							changeAssigneeDto.setTaskId(entity.getId());
							changeAssigneeDto.setAssign_To_No(CommonUtil.getValidMsisdn(changedAssigneeNum));
							changeAssigneeDto.setAssign_To_Name("");
							changeAssigneeDto.setIsAssigneeSync("N");
							changeAssigneeDto.setMobileNumber(ApplicationUtil.getPreference(ApplicationConstant.regMobNo,context));
							changeAssigneeDto.setOldAssignee(CommonUtil.getValidMsisdn(entity.getUserInfoTo().getMobile_number()));
							Map ch_map = new HashMap();
							ch_map.put("chdto", changeAssigneeDto);
							ch_map.put("changeAssigneeview", txtAssinorTask);
							
						
							//loadTaskList();
							dialog.dismiss();
						}
					}
				});
				dialog.show();

			}


}
