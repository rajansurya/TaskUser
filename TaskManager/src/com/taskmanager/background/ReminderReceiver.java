package com.taskmanager.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

/**
 * Receiver class to add reminder
 * @author mayankb
 *
 */
public class ReminderReceiver extends BroadcastReceiver
{ 
	@Override
	public void onReceive(Context context, Intent intent)
	{
		System.out.println("Intent"+intent);
		String taskId = intent.getStringExtra("TaskId");
		String taskdec = intent.getStringExtra("TaskDesc");
		System.out.println("TAsk Id "+taskId+"Taksdec"+taskdec);
		System.out.println("Setreminder Alarm Received--->"+taskId);
		Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		vibe.vibrate(1000);
		
		Toast.makeText(context, "This reminder message", Toast.LENGTH_SHORT).show();
		Intent i = new Intent(context, com.taskmanager.ui.reminderActivity.class);
		i.putExtra("TaskId", taskId);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.getApplicationContext().startActivity(i);
		

	}
	
	

}

