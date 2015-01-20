package com.taskmanager.app;

import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.taskmanager.background.TaskSyncUtils;
import com.taskmanager.database.DBAdapter;
import com.taskmanager.domain.MessageInfoType;
import com.taskmanager.domain.TaskInfoType;
import com.taskmanager.ui.AllTaskActivity;
import com.taskmanager.util.ApplicationConstant;
import com.taskmanager.util.ApplicationUtil;
import com.taskmanager.util.CommonUtil;
import com.taskmanager.webservice.json.JSONUtil;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";
	static int i=0;

	public GCMIntentService() {
		super(CommonUtil.SENDER_ID);
	}

	/**
	 * Method called on device registered
	 **/
	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "Device registered: regId = " + registrationId);
		try {
			ApplicationUtil.registerOnDevice(context,ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context),registrationId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");

	}

	private void markTaskAsRead(String taskId){
		ContentValues initialValues = new ContentValues();
		initialValues.put("Task_ID", taskId);
		initialValues.put("IsTaskRead", "Y");
		DBAdapter adapter=DBAdapter.getInstance(this);;
		adapter.openDataBase();
		adapter.updateRecord("Task", initialValues,"Task_ID='" + taskId + "'", null);
		adapter.close();
	}
	private void updateTask(String taskId){
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put("Task_ID", taskId);
			initialValues.put("IsTaskRead", "Y");
			DBAdapter adapter=DBAdapter.getInstance(this);;
			adapter.openDataBase();
			adapter.updateRecord("Task", initialValues,"Task_ID='" + taskId + "'", null);
			adapter.close();
		} catch (Exception e) {
			ContentValues initialValues = new ContentValues();
			initialValues.put("Task_ID", taskId);
			initialValues.put("IsTaskRead", "Y");
			DBAdapter adapter=DBAdapter.getInstance(this);;
			adapter.openDataBase();
			adapter.updateRecord("Task", initialValues,"Task_ID='" + taskId + "'", null);
			adapter.close();
		}
	}
	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "Received message");

		try {
			
			String senderNumber = intent.getExtras().getString("senderNumber");
			String senderName = intent.getExtras().getString("senderName");
			String message = intent.getExtras().getString("message");
			String type = intent.getExtras().getString("type");

			String title = intent.getExtras().getString("title");
			String messageInfo = intent.getExtras().getString("messageInfo");
			String status="";
			String alert = intent.getExtras().getString("alert");
			String name=ApplicationUtil.getContactNameFromPhoneNo(this, senderNumber);

			// Updating user status in DB 
			ContentValues initialValues = new ContentValues();
			String senderNum = senderNumber;
			senderNum = CommonUtil.getValidMsisdn(senderNum);
			senderNum = senderNum.replaceAll("\"", "");
			System.out.println("Sender Numbers are .............. " + senderNum);
			if(senderNum.length()==10)
			{
				initialValues.put("REG_STATUS", "REGISTERED");
				ApplicationUtil.getInstance().updateDataInDB("USERS_CONTACT", initialValues,context, "MOBILE_NUMBER ='" + senderNumber + "'", null);
			}
			alert=alert.replace("$sender$", name);
			if("TASK_READ_ACK".equalsIgnoreCase(type)){
				updateTask(intent.getExtras().getString("taskId"));
				generateNotification(context,alert,new Random().nextInt(),title,null);
				reloadListOnScreen(context);
				return;
			}

			if("task".equalsIgnoreCase(type)){
				TaskInfoType infoType=JSONUtil.getJavaObject(message, TaskInfoType.class);
				TaskInfoType taskObj = new TaskInfoType();
				status=infoType.getStatus();
				taskObj.setTaskId(infoType.getTaskId());
				taskObj.setAssignFrom(infoType.getAssignFrom());
				taskObj.setAssignFromName(infoType.getAssignFromName());
				taskObj.setAssignTo(infoType.getAssignTo());
				taskObj.setAssignToName(infoType.getAssignToName());
				taskObj.setCreationDate(infoType.getCreationDate());
				taskObj.setTaskDescription(infoType.getTaskDescription());
				taskObj.setPriority(infoType.getPriority());
				taskObj.setFire(infoType.getFire());
				taskObj.setClosedBy(infoType.getClosedBy());
				taskObj.setStatus(infoType.getStatus());
				taskObj.setFavouraite(infoType.getFavouraite());
				taskObj.setModifiedBy(infoType.getModifiedBy());
				taskObj.setTargetDate(infoType.getTargetDate());
				taskObj.setIsTaskRead(infoType.getIsTaskRead());
				taskObj.setModifyDate(infoType.getModyfiedDate());
				if(TaskSyncUtils.isNewTask(taskObj.getTaskId(), context)){
					TaskSyncUtils.createTask(taskObj, context);

				}else{

					TaskSyncUtils.updateTask(taskObj, context);
				}
				if(messageInfo!=null){
					MessageInfoType messageInfoType=JSONUtil.getJavaObject(messageInfo, MessageInfoType.class);
					if(TaskSyncUtils.isNewMessage(messageInfoType.getMessageId()+"", context)){
						TaskSyncUtils.createMessage(messageInfoType, context);

					}
				}

				if(name.equalsIgnoreCase(senderNumber)){
					status="Junk";
				}
				generateNotification(context, alert,Long.parseLong(infoType.getTaskId()),title,status);


			}else if("message".equalsIgnoreCase(type)){
				MessageInfoType infoType=JSONUtil.getJavaObject(message, MessageInfoType.class);
				if(TaskSyncUtils.isNewMessage(infoType.getMessageId()+"", context)){
					TaskSyncUtils.createMessage(infoType, context);

				}
				if(name.equalsIgnoreCase(senderNumber)){
					status="Junk";
				}
				generateNotification(context, alert,infoType.getMessageId(),title,status);
				System.out.println(infoType);
			}
		} catch (Exception e) {
			CommonUtil.displayMessage(context, e.toString());
		}





		//CommonUtil.displayMessage(context, alert);
		// notifies user
		reloadListOnScreen(context);
	}

	/**
	 * Method called on receiving a deleted message
	 * */
	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		String message = context.getString(R.string.gcm_deleted, total);
		// CommonUtil.displayMessage(context, message);
		// notifies user
		//  generateNotification(context, message);
	}

	/**
	 * Method called on Error
	 * */
	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
		// CommonUtil.displayMessage(context, context.getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		CommonUtil.displayMessage(context, context.getString(R.string.gcm_recoverable_error,
				errorId));
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private  void generateNotification(Context context, String message,long id,String title,String status) {


		ApplicationUtil.savePreference("status", status, context);
		int icon = R.drawable.notiicon;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager)
				context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);

		// title = context.getString(R.string.app_name);

		Intent notificationIntent = new Intent(context, AllTaskActivity.class);

		// set intent so it does not start a new activity
		/* notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
		 */
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent intent =PendingIntent.getActivity(context, 0, notificationIntent, 0);                                                                                                                                                     
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Play default notification sound
		//  notification.defaults |= Notification.DEFAULT_SOUND;
		notification.sound = Uri.parse("android.resource://com.taskmanager.app/raw/alert1");
		//notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");

		// Vibrate if vibrate is enabled
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify("tag",(int )id, notification);
		//notificationManager.cancelAll();
		// notificationManager.cancelAll();


	}
	private void reloadListOnScreen(Context context) {

		//System.out.println("Reload list called---"
		//		+ ApplicationConstant.needUIRefresh);
		Intent intent = new Intent(
				"com.taskmaganger.ui.AlltaskActivity.relaodList.broadcast");
		context.sendBroadcast(intent);


	}


}
