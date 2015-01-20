package com.taskmanager.background;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.taskmanager.app.R;
import com.taskmanager.ui.AllTaskActivity;

/**
 * Receiver to add alarm
 * @author mayankb
 *
 */

public class AlarmReceiver extends BroadcastReceiver{

	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		try {
			String taskId = intent.getStringExtra("TaskId");
			String alarmTaskDesc = intent.getStringExtra("TaskDesc");
			CharSequence tickerText = "Alarm for You!!!";
			System.out.println("Setalarm alarm added intent received");
			CharSequence contentTitle = "Alarm notification";
			NotificationManager notofManager = (NotificationManager)context. getSystemService(Context.NOTIFICATION_SERVICE);
			Intent notificationIntent = new Intent(context, AllTaskActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(context,0, notificationIntent, 0);
			Notification notification =  new NotificationCompat.Builder(context).setAutoCancel(true)
					.setContentTitle(contentTitle)
					.setContentText(alarmTaskDesc)
					.setContentIntent(contentIntent)
					.setSmallIcon(R.drawable.reminder_icon_blue)
					.setWhen(System.currentTimeMillis())
					.setTicker(tickerText)
					.build();
			notification.defaults |= Notification.DEFAULT_SOUND;
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			if(taskId!=null)
			notofManager.notify("Task Alarm",Integer.parseInt(taskId),notification);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}