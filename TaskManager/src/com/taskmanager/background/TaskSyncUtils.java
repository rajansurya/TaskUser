/**
 * 
 */
package com.taskmanager.background;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;

import com.taskmanager.database.DBAdapter;
import com.taskmanager.domain.MessageInfoType;
import com.taskmanager.domain.MessageListType;
import com.taskmanager.domain.SyncTask;
import com.taskmanager.domain.SysncTaskResponseType;
import com.taskmanager.domain.Task;
import com.taskmanager.domain.TaskInfoType;
import com.taskmanager.domain.TaskResponseListType;
import com.taskmanager.domain.TaskResponseType;
import com.taskmanager.dto.MessageInfoEntity;
import com.taskmanager.dto.TaskInfoEntity;
import com.taskmanager.json.RequestMethod;
import com.taskmanager.json.RestClient;
import com.taskmanager.ui.AllTaskActivity;
import com.taskmanager.util.ApplicationConstant;
import com.taskmanager.util.ApplicationUtil;
import com.taskmanager.util.CommonUtil;
import com.taskmanager.webservice.BGConnector;
import com.taskmanager.webservice.BGConnectorImpl;
import com.taskmanager.webservice.json.JSONUtil;

/**
 * @author IBM_ADMIN
 * 
 */
public class TaskSyncUtils implements Runnable {
	private static TaskSyncUtils syncUtils=new TaskSyncUtils();
	private static Context context;
	
	public static TaskSyncUtils getInstance(Context cotx){
		context=cotx;
		if(syncUtils==null){
			return syncUtils=new TaskSyncUtils();
		}
		return syncUtils;
	}
	private  TaskSyncUtils() {
		// TODO Auto-generated constructor stub
	}
	/*public TaskSyncUtils(Context context) {
		this.context=context;
		
	}*/
	
	@Override
	public synchronized void  run() {
		if((!ApplicationUtil.checkInternetConn(context))){
			return ;
		}
		markMessageAsRead(context);
		String curentMsisdn=CommonUtil.getRegNum(context);
		syscTaskANdMessage(context);
		sync(curentMsisdn, context);
		
	}
	public   synchronized boolean fullSync(String mobileNumber,final Context context,String lastUpdatedDate) {
		boolean flag=false;	
		AllTaskActivity.isTaskOrMsgCreationInitated=true;
		
		
	try{	
		
		BGConnector connector=new BGConnectorImpl(context);
		Task task=connector.getTask(mobileNumber, lastUpdatedDate,context,ApplicationUtil.getPreference(ApplicationConstant.xtoken,context));
		if(task!=null){
			TaskResponseType response=task.getResponse();
			if(response!=null){
				TaskResponseListType taskListType=response.getTaskList();
				if(taskListType!=null){
					List<TaskInfoType> taskList=taskListType.getTask();
					if(taskList!=null){
						
						for (TaskInfoType taskObj : taskList) {
							
							if(isNewTask(taskObj.getTaskId(), context)){
								createTask(taskObj, context);
								//flag=true;
							}else{							
								updateTask(taskObj, context);
							}
							
							MessageListType messages=taskObj.getMessages();
							if(messages!=null){
								List<MessageInfoType> message=messages.getMessage();
								if(message!=null){
									for (MessageInfoType messageInfoType : message) {
										if(isNewMessage(messageInfoType.getMessageId()+"", context)){
											/*if(!CommonUtil.getValidMsisdn(mobileNumber).equals(CommonUtil.getValidMsisdn(messageInfoType.getAssignFrom()))){
												//flag=true;
											}*/
											createMessage(messageInfoType, context);

										}else{
											updateMessage(messageInfoType, context);
										}
									}
								}
							}

						}
					}
				}
			}
			
		}
		
	}catch (Exception e) {
			e.printStackTrace();
		}finally{
			AllTaskActivity.isTaskOrMsgCreationInitated=false;
		}
	return flag;

	}
	
	public synchronized static void syncReciedTAsk(String mobileNumber,Context context) {
		DBAdapter adapter=DBAdapter.getInstance(context);
		adapter.openDataBase();
		BGConnector connector=new BGConnectorImpl(context);
		Task task=connector.getRecievedTask(mobileNumber, null);
		if(task!=null){
			TaskResponseType response=task.getResponse();
			if(response!=null){
				TaskResponseListType taskListType=response.getTaskList();
				if(taskListType!=null){
					List<TaskInfoType> taskList=taskListType.getTask();
					if(taskList!=null){
						
						for (TaskInfoType taskObj : taskList) {
							if(isNewTask(taskObj.getTaskId(), context)){
								createTask(taskObj, context);
							}else{
								updateTask(taskObj, context);
							}
							
							MessageListType messages=taskObj.getMessages();
							if(messages!=null){
								List<MessageInfoType> message=messages.getMessage();
								if(message!=null){
									for (MessageInfoType messageInfoType : message) {
										if(isNewMessage(messageInfoType.getMessageId()+"", context)){
											createMessage(messageInfoType, context);
										}
									}
								}
							}

						}
					}
				}
			}
			adapter.close();
		}
		
		

	}
	
	public synchronized  void syscTaskANdMessage(Context context){
		AllTaskActivity.isTaskOrMsgCreationInitated=true;
		DBAdapter adapter=DBAdapter.getInstance(context);
		adapter.openDataBase();
		StringBuilder builder=new StringBuilder();
		try{
		
		builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		builder.append("<SyncTask xmlns=\"http://www.ibm.taskmanager.com/api/domain/task\">");
		builder.append("<request>");
		List<TaskInfoEntity> entities=adapter.getAsycnTaskList(" Select * from Task  where (IsTaskSync='N' or isMsgSend='false' or IsFAvSync='N' or IsAssigneeSync='N') and Task_ID!='default_task_1'");//or IsPrioritySync='Y'
		if(entities==null||entities.size()==0){
			return ;
		}
		for (TaskInfoEntity entity : entities) {
			
			if("NEW".equalsIgnoreCase(entity.getTaskSyncType())){
					builder.append("<task>");
					builder.append("<oldTaskId>"+entity.getId()+"</oldTaskId>");
					builder.append("<taskType>NEW</taskType>");
					builder.append("<assignFrom>"+CommonUtil.getValidMsisdn(entity.getUserInfoFrom().getMobile_number())+"</assignFrom>");
					builder.append("<assignTo>"+CommonUtil.getValidMsisdn(entity.getUserInfoTo().getMobile_number())+"</assignTo>");
					builder.append("<mobileNumber>"+ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context).trim()+"</mobileNumber>");
					builder.append("<taskDescription>"+entity.getTask_description()+"</taskDescription>");
					builder.append("<priority>"+entity.getPriority()+"</priority>");
					builder.append("<status>"+entity.getStatus()+"</status>");
					builder.append("<fire>"+entity.getFire_flag()+"</fire>");
					builder.append("<favouraite>"+entity.getIsFavouraite()+"</favouraite>");
					builder.append("<reminderTime>"+entity.getReminder_time()+"</reminderTime>");
					builder.append("<targetDate>"+entity.getTarget_date()+"</targetDate>");
					builder.append("<transactionId>"+entity.getTransactionId()+"</transactionId>");
					builder.append("<messages>");
					List<MessageInfoEntity> messageInfoEntities=adapter.getNewMessageByTaskId(entity.getId());
					for (MessageInfoEntity messageInfoEntity : messageInfoEntities) {
							builder.append("<message>");
							builder.append("<assignFrom>"+messageInfoEntity.getUserInfoFrom().getMobile_number()+"</assignFrom>");//transactionId
							builder.append("<oldMessageId>"+messageInfoEntity.getMessageId()+"</oldMessageId>");
							builder.append("<transactionId>"+messageInfoEntity.getTransactionId()+"</transactionId>");
							builder.append("<assignTo>"+messageInfoEntity.getUserInfoTo().getMobile_number()+"</assignTo>");
							builder.append("<messageDescription>"+messageInfoEntity.getMessage_description()+"</messageDescription>");
							builder.append("</message>");
					}
						
					builder.append("</messages>");
					builder.append("</task>");
			
			}else{
				String curentMsisdn=CommonUtil.getValidMsisdn(ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context));
				builder.append("<task>");
				builder.append("<taskId>"+entity.getId()+"</taskId>");
				builder.append("<mobileNumber>"+ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context)+"</mobileNumber>");
				builder.append("<oldTaskId>"+entity.getId()+"</oldTaskId>");
				
				if("N".equalsIgnoreCase(entity.getIsAssigneeSync())){
					builder.append("<taskType>CHANGEASSIGNE</taskType>");
					builder.append("<oldAssignee>"+CommonUtil.getValidMsisdn(entity.getOldAssignee())+"</oldAssignee>");
					builder.append("<newAssignee>"+CommonUtil.getValidMsisdn(entity.getUserInfoTo().getMobile_number())+"</newAssignee>");
				}else if("N".equalsIgnoreCase(entity.getIsTaskUpdateSync())){
					builder.append("<taskType>CHANGE_CONTENT</taskType>");
				}else if("N".equalsIgnoreCase(entity.getIsTargetSync())){
					builder.append("<taskType>CHANGE_TARGET</taskType>");
				}else if("N".equalsIgnoreCase(entity.getIsPrioritySync())){
					builder.append("<taskType>CHANGE_PRIORITY</taskType>");
				}else if("N".equalsIgnoreCase(entity.getIsStatusSync())){
					builder.append("<taskType>CHANGE_STATUS</taskType>");
				}else if(("false".equalsIgnoreCase(entity.getIsMessageSend())&&"y".equalsIgnoreCase(entity.getIsTaskSync()))||("N".equalsIgnoreCase(entity.getIsFAvSync()))){
					builder.append("<taskType>READONLY</taskType>");
					
				}else{
					builder.append("<taskType>OLD</taskType>");
				}
				
				builder.append("<assignFrom>"+CommonUtil.getValidMsisdn(entity.getUserInfoFrom().getMobile_number())+"</assignFrom>");
				builder.append("<assignTo>"+CommonUtil.getValidMsisdn(entity.getUserInfoTo().getMobile_number())+"</assignTo>");
				builder.append("<taskDescription>"+entity.getTask_description()+"</taskDescription>");
				builder.append("<priority>"+entity.getPriority()+"</priority>");
				builder.append("<status>"+entity.getStatus()+"</status>");
				builder.append("<fire>"+entity.getFire_flag()+"</fire>");
				builder.append("<favouraite>"+entity.getIsFavouraite()+"</favouraite>");
				builder.append("<reminderTime>"+entity.getReminder_time()+"</reminderTime>");
				builder.append("<targetDate>"+entity.getTarget_date()+"</targetDate>");
				builder.append("<transactionId>"+entity.getTransactionId()+"</transactionId>");
				builder.append("<messages>");
				List<MessageInfoEntity> messageInfoEntities=adapter.getAsyncMessageByTaskId(entity.getId());
				for (MessageInfoEntity messageInfoEntity : messageInfoEntities) {
					builder.append("<message>");
					builder.append("<assignFrom>"+messageInfoEntity.getUserInfoFrom().getMobile_number()+"</assignFrom>");
					builder.append("<oldMessageId>"+messageInfoEntity.getMessageId()+"</oldMessageId>");
					builder.append("<assignTo>"+messageInfoEntity.getUserInfoTo().getMobile_number()+"</assignTo>");
					builder.append("<transactionId>"+messageInfoEntity.getTransactionId()+"</transactionId>");
					builder.append("<messageDescription>"+messageInfoEntity.getMessage_description()+"</messageDescription>");
					builder.append("</message>");
				}
				
				builder.append("</messages>");
				builder.append("</task>");
			}
			
			
		}
		
		builder.append("</request></SyncTask>");
		}catch(Exception exception){
			System.out.println(exception);
		}finally{
			adapter.close();
		}
		
		try 
		{
			RestClient client=new RestClient(ApplicationConstant.Sync_Message_Task_URL);
			client.AddHeader("Content-Type", "application/xml");
			client.AddHeader("Accept", "application/json");
			client.AddHeader("X-TOKEN", ApplicationUtil.getPreference(ApplicationConstant.xtoken,context));
			String postData=builder.toString();
			client.setPostData(postData);
			client.Execute(RequestMethod.POST);
			String str=client.getResponse();
			updateTaskAndMessage(str, context);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}finally{
			AllTaskActivity.isTaskOrMsgCreationInitated=false;

		}

		
	}
	
	private synchronized  void updateTaskAndMessage(String response,Context context) throws Exception{
		DBAdapter adapter=DBAdapter.getInstance(context);
		
		try{
		SyncTask syncTask=JSONUtil.getJavaObject(response, SyncTask.class);
		SysncTaskResponseType sysncTaskResponseType=syncTask.getResponse();
		if(sysncTaskResponseType!=null){
		TaskResponseListType responseListType= sysncTaskResponseType.getTasks();
		if(responseListType!=null&&responseListType.getTask()!=null){
		List<TaskInfoType> taskInfoTypes=responseListType.getTask();
		for (TaskInfoType taskType : taskInfoTypes) {
			ContentValues initialValues=new ContentValues();
			initialValues.put("Task_ID", taskType.getTaskId());
			initialValues.put("Group_ID", taskType.getGroupId());
			initialValues.put("Assign_From", taskType.getAssignFrom());
			initialValues.put("Assign_From_Name", ApplicationUtil.getContactNameFromPhoneNo(context,taskType.getAssignFrom()));
			initialValues.put("Assign_To", taskType.getAssignTo());	
			initialValues.put("Assign_To_Name",ApplicationUtil.getContactNameFromPhoneNo(context,taskType.getAssignTo()));
			initialValues.put("CreationDate", taskType.getCreationDate());
			initialValues.put("IsTaskSync", "Y");
			initialValues.put("IsTaskUpdateSync", "Y");
			initialValues.put("IsPrioritySync", "Y");
			initialValues.put("IsStatusSync", "Y");
			initialValues.put("IsTargetSync", "Y");
			initialValues.put("IsAssigneeSync", "Y");
			initialValues.put("Target_Date_TIME", ApplicationUtil.getGMTLong(taskType.getTargetDate()));
			initialValues.put("IsFAvSync", "Y");
			initialValues.put("TASK_SYNC_TYPE", "OLD");//IsAssigneeSync
			initialValues.put("isMsgSend", "true");
					
			if("N".equalsIgnoreCase(taskType.getRegStatus())){
				ApplicationUtil.sendSMS(taskType.getAssignTo(), taskType.getTaskDescription());
				ContentValues updateContact = new ContentValues();
				String number_str = CommonUtil.getValidMsisdn(taskType.getAssignTo());
				System.out.println("Numbers are .............. " + number_str);
				updateContact.put("REG_STATUS", "N");
				ApplicationUtil.getInstance().updateDataInDB("USERS_CONTACT", updateContact,context, "MOBILE_NUMBER ='" + number_str + "'", null);
			}else if("Y".equalsIgnoreCase(taskType.getRegStatus())){
				ContentValues updateContact = new ContentValues();
				String number_str = CommonUtil.getValidMsisdn(taskType.getAssignTo());
				updateContact.put("REG_STATUS", "REGISTERED");
				ApplicationUtil.getInstance().updateDataInDB("USERS_CONTACT", updateContact,context, "MOBILE_NUMBER ='" + number_str + "'", null);
			}
			adapter.openDataBase();
			adapter.updateTask(initialValues, taskType.getOldTaskId());
			ContentValues notationTAskMapaingValues = new ContentValues();
			notationTAskMapaingValues.put("Task_ID", taskType.getTaskId());
			adapter.updateNotaionMapping(notationTAskMapaingValues, taskType.getOldTaskId());
			adapter.close();
			 MessageListType messageListType=	taskType.getMessages();
			 if(messageListType!=null){				 
				 List<MessageInfoType> messageInfoTypes= messageListType.getMessage();
				 for (MessageInfoType messageInfoType : messageInfoTypes) {
					 ContentValues messageValues=new ContentValues();
					 messageValues.put("Message_ID", messageInfoType.getMessageId());
					 messageValues.put("Task_ID", messageInfoType.getTaskId());
					 messageValues.put("M_From", messageInfoType.getAssignFrom());
					 messageValues.put("M_To", messageInfoType.getAssignTo());
					 messageValues.put("IsMessageSync", "Y");
					 adapter.openDataBase();
					 adapter.updateMessage(messageValues, messageInfoType.getOldMessageId());
					 adapter.close();
				}
			 }
			 
			}
		}
		
		}
		}catch(Exception exception)
		{exception.printStackTrace();}
		finally{
			try {
				adapter.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
		
	}
	
	public synchronized  static void createTask(TaskInfoType taskObj,Context context){
		
		ContentValues initialValues = new ContentValues();
		initialValues.put("Task_ID", taskObj.getTaskId());
		initialValues.put("Group_ID", taskObj.getGroupId());
		initialValues.put("IsJunk", "N");
		initialValues.put("Assign_From", taskObj.getAssignFrom());
		initialValues.put("Assign_From_Name", ApplicationUtil.getContactNameFromPhoneNo(context,taskObj.getAssignFrom()));
		initialValues.put("Assign_To", taskObj.getAssignTo());	
		initialValues.put("Assign_To_Name",ApplicationUtil.getContactNameFromPhoneNo(context,taskObj.getAssignTo()));
		initialValues.put("CreationDate",taskObj.getCreationDate());
		initialValues.put("Task_Desc", taskObj.getTaskDescription());
		initialValues.put("Target_Date", taskObj.getTargetDate());
		initialValues.put("Priority",taskObj.getPriority());
		initialValues.put("Closure_Date", taskObj.getCloserDate());
		initialValues.put("transactionId", taskObj.getTransactionId());
		initialValues.put("Reminder_Time", taskObj.getReminderTime());
		initialValues.put("IsFavouraite", taskObj.getFavouraite());
		initialValues.put("isTaskNew", "N");//
		initialValues.put("isMsgSend", "true");
		initialValues.put("IsTaskSync", "Y");//
		
		if("true".equalsIgnoreCase(taskObj.getLaterTask())){
			initialValues.put("isTaskSnooz", "Y");
		}else{
			initialValues.put("isTaskSnooz", "N");	
		}
		initialValues.put("Target_Date_TIME", ApplicationUtil.getGMTLong(taskObj.getTargetDate()));
		
		if("true".equalsIgnoreCase(taskObj.getIsTaskRead())){
			initialValues.put("IsTaskRead", "Y");
		}else{
			initialValues.put("IsTaskRead", "N");
		}
		
		initialValues.put("TASK_SYNC_TYPE", "OLD");
		
		if(CommonUtil.getRegNum(context).equals(CommonUtil.getValidMsisdn(taskObj.getAssignFrom())) && CommonUtil.getRegNum(context).equals(CommonUtil.getValidMsisdn(taskObj.getAssignTo())))
		{
			initialValues.put("TaskType", "self");
			initialValues.put("DISPLAY_NAME", "self");
		
		}
		else
		{
			
				if(CommonUtil.getRegNum(context).equals(CommonUtil.getValidMsisdn(taskObj.getAssignFrom())))
				{
					initialValues.put("TaskType", "sent");
					initialValues.put("DISPLAY_NAME", ApplicationUtil.getContactNameFromPhoneNo(context,taskObj.getAssignTo()));
					
				}else{
					initialValues.put("TaskType", "inbox");
					initialValues.put("DISPLAY_NAME", ApplicationUtil.getContactNameFromPhoneNo(context,taskObj.getAssignFrom()));
				}
			
		}

		
		if("CLOSE".equalsIgnoreCase(taskObj.getStatus())){
			initialValues.put("Status", "CLOSE");
			DBAdapter adapter=DBAdapter.getInstance(context);
			adapter.openDataBase();
			adapter.deleteRecordInDB("NOTAION_TASK_MAPPING", "Task_ID = '" + taskObj.getTaskId()+ "'", null);
			adapter.close();
		}else{
			initialValues.put("Status", "OPEN");
			SyncModule  module=new SyncModule(context);
			module.createNotaiont(taskObj.getTaskDescription(), taskObj.getTaskId());
		}
		
		if(CommonUtil.getRegNum(context).equals(CommonUtil.getValidMsisdn(taskObj.getClosedBy())))
		{
			initialValues.put("isActive", "N");
		}else{
			initialValues.put("isActive", "Y");
		}
		String id = ApplicationUtil.isContactRecorded(taskObj.getAssignFrom(), context);

		if ((id != null && id.trim().length() > 0)||CommonUtil.getValidMsisdn(ApplicationConstant.ASSIGNER_NUMBER).equals(CommonUtil.getValidMsisdn(taskObj.getAssignFrom()))|| CommonUtil.getRegNum(context).equals(CommonUtil.getValidMsisdn(taskObj.getAssignFrom()))){//|| ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context).trim().contains(CommonUtil.getValidMsisdn(taskObj.getAssignFrom()))
			initialValues.put("IsJunk", "N");
		}
		else{
			initialValues.put("IsJunk", "Y");
		}
		
		
		initialValues.put("CreationDateTime",ApplicationUtil.getGMTLong(taskObj.getCreationDate()));
	//	ApplicationUtil.savePreference(ApplicationConstant.LAST_UPDATED_DATE,taskObj.getCreationDate(), context);
		initialValues.put("ClosureDate", ApplicationUtil.getGMTLong(taskObj.getCloserDate()));
		initialValues.put("TASK_URL", taskObj.getTaskUrl());
		initialValues.put("UPDATED_DATE", taskObj.getCreationDate());
		try {
			initialValues.put("UPDATION_DATE_TIME", ApplicationUtil.getGMTLong(taskObj.getCreationDate()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//getDateToMillisecond(String date)
		
		
		
		//TASK_URL
		//ApplicationUtil.getInstance().saveDataInDB("Task", null, initialValues,context);
		DBAdapter adapte1=DBAdapter.getInstance(context);
		adapte1.openDataBase();
		adapte1.saveRecord("Task", null,	initialValues);
		adapte1.close();
	

	}
	
	public synchronized static void updateTask(TaskInfoType taskObj,Context context){
		if("READONLY".equalsIgnoreCase(taskObj.getUpdateState())){
			ContentValues initialValues = new ContentValues();
			initialValues.put("Task_ID", taskObj.getTaskId());
			initialValues.put("IsTaskRead", "Y");
			DBAdapter adapter=DBAdapter.getInstance(context);
			adapter.openDataBase();
			adapter.updateRecord("Task", initialValues,"Task_ID='" + taskObj.getTaskId() + "'", null);
			adapter.close();
			return;
		}
		ContentValues initialValues = new ContentValues();
		initialValues.put("Task_ID", taskObj.getTaskId());
		initialValues.put("Group_ID", taskObj.getGroupId());
		initialValues.put("IsTaskSync", "Y");
		
		if("true".equalsIgnoreCase(taskObj.getLaterTask())){
			initialValues.put("isTaskSnooz", "Y");
		}else{
			initialValues.put("isTaskSnooz", "N");	
		}
		
		String id = ApplicationUtil.isContactRecorded(taskObj.getAssignFrom(), context);

		if ((id != null && id.trim().length() > 0)||CommonUtil.getValidMsisdn(ApplicationConstant.ASSIGNER_NUMBER).equalsIgnoreCase(CommonUtil.getValidMsisdn(taskObj.getAssignFrom()))|| CommonUtil.getRegNum(context).equals(CommonUtil.getValidMsisdn(taskObj.getAssignFrom()))){//|| ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context).trim().contains(CommonUtil.getValidMsisdn(taskObj.getAssignFrom()))
				initialValues.put("IsJunk", "N");
		}
		else{
			initialValues.put("IsJunk", "Y");
		}
		if("true".equalsIgnoreCase(taskObj.getIsTaskRead())){
			initialValues.put("IsTaskRead", "Y");
		}else{
			initialValues.put("IsTaskRead", "N");
		}
		initialValues.put("Assign_From", taskObj.getAssignFrom());
		initialValues.put("Target_Date_TIME", ApplicationUtil.getGMTLong(taskObj.getTargetDate()));
		initialValues.put("Assign_From_Name", ApplicationUtil.getContactNameFromPhoneNo(context,taskObj.getAssignFrom()));
		initialValues.put("Assign_To", taskObj.getAssignTo());	
		initialValues.put("Assign_To_Name",ApplicationUtil.getContactNameFromPhoneNo(context,taskObj.getAssignTo()));
		//initialValues.put("CreationDate",taskObj.getCreationDate());
		initialValues.put("Task_Desc", taskObj.getTaskDescription());
		initialValues.put("Target_Date", taskObj.getTargetDate());
		initialValues.put("Priority",taskObj.getPriority());
		initialValues.put("Closure_Date", taskObj.getCloserDate());
		initialValues.put("Reminder_Time", taskObj.getReminderTime());
		initialValues.put("IsFavouraite", taskObj.getFavouraite());
		initialValues.put("isTaskNew", "N");
		initialValues.put("isMsgSend", "true");
		initialValues.put("TASK_SYNC_TYPE", "OLD");
		
		if("CLOSE".equalsIgnoreCase(taskObj.getStatus())){
			initialValues.put("Status", "CLOSE");
			DBAdapter adapter=DBAdapter.getInstance(context);
			adapter.openDataBase();
			adapter.deleteRecordInDB("NOTAION_TASK_MAPPING", "Task_ID = '" + taskObj.getTaskId()+ "'", null);
			adapter.close();
		}else{
			initialValues.put("Status", "OPEN");
			SyncModule  module=new SyncModule(context);
			module.createNotaiont(taskObj.getTaskDescription(), taskObj.getTaskId());
		}
		initialValues.put("TASK_URL", taskObj.getTaskUrl());
		initialValues.put("ClosureDate", ApplicationUtil.getGMTLong(taskObj.getCloserDate()));
		if(taskObj.getModifyDate()!=null&&!taskObj.getModifyDate().isEmpty())
		{
			initialValues.put("UPDATED_DATE", taskObj.getModifyDate());
			try {
				initialValues.put("UPDATION_DATE_TIME", ApplicationUtil.getGMTLong(taskObj.getModifyDate()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//getDateToMillisecond(String date)
		}
		DBAdapter adapter=DBAdapter.getInstance(context);
		adapter.openDataBase();
		
		adapter.updateRecord("Task", initialValues,"Task_ID='" + taskObj.getTaskId() + "'", null);
		adapter.close();
		
		
	}
	
	public static synchronized void createMessage(MessageInfoType messageInfoType,Context context){
		
		ContentValues messageContent = new ContentValues();
		messageContent.put("Message_ID",messageInfoType.getMessageId());
		messageContent.put("M_Desc", messageInfoType.getMessageDescription());
		messageContent.put("M_From", messageInfoType.getAssignFrom());
		messageContent.put("transactionId", messageInfoType.getTransactionId());
		messageContent.put("M_To", messageInfoType.getAssignTo());
		if("true".equalsIgnoreCase(messageInfoType.getIsMessageRead())){
			messageContent.put("IsRead", "Y");
		}else{
			messageContent.put("IsRead", "N");
		}
		messageContent.put("IsMessageSync", "Y");
		messageContent.put("Task_ID", messageInfoType.getTaskId());
		messageContent.put("M_From_Name",ApplicationUtil.getContactNameFromPhoneNo(context,messageInfoType.getAssignFrom()) );
		messageContent.put("M_To_Name",ApplicationUtil.getContactNameFromPhoneNo(context,messageInfoType.getAssignTo()));
		messageContent.put("Creation_Date", messageInfoType.getCreatedTime());
		if(messageInfoType.getCreatedTime()!=null)
		messageContent.put("CreationDateTime",ApplicationUtil.getDateInGMT(messageInfoType.getCreatedTime()).getTime());
		
		if(CommonUtil.getRegNum(context).equals(CommonUtil.getValidMsisdn(messageInfoType.getAssignFrom())) && messageInfoType!=null&&CommonUtil.getRegNum(context).equals(CommonUtil.getValidMsisdn(messageInfoType.getAssignTo())))
		{
			messageContent.put("MSG_TYPE", "self");
		
		}
		else
		{
			
				if(CommonUtil.getRegNum(context).equals(CommonUtil.getValidMsisdn(messageInfoType.getAssignFrom())))
				{
					messageContent.put("MSG_TYPE", "sent");
				}else{
					messageContent.put("MSG_TYPE", "inbox");
				}
			
		}
		DBAdapter adapter=DBAdapter.getInstance(context);
		
		try {
			
			adapter.openDataBase();
			adapter.saveRecord("Messaging", null,	messageContent);
			ContentValues initialValues = new ContentValues();
			initialValues.put("Task_ID", messageInfoType.getTaskId());
			//initialValues.put("IsTaskRead", "Y");
			initialValues.put("UPDATED_DATE", messageInfoType.getCreatedTime());
			try {
				initialValues.put("UPDATION_DATE_TIME", ApplicationUtil.getGMTLong(messageInfoType.getCreatedTime()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//getDateToMillisecond(String date)
			adapter.updateRecord("Task", initialValues,"Task_ID='" + messageInfoType.getTaskId() + "'", null);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			adapter.close();
		}
	}
	
public static synchronized void updateMessage(MessageInfoType messageInfoType,Context context){
		
		ContentValues messageContent = new ContentValues();
		messageContent.put("Message_ID",messageInfoType.getMessageId());
		messageContent.put("M_Desc", messageInfoType.getMessageDescription());
		messageContent.put("M_From", messageInfoType.getAssignFrom());
		messageContent.put("M_To", messageInfoType.getAssignTo());
		if("true".equalsIgnoreCase(messageInfoType.getIsMessageRead())){
			messageContent.put("IsRead", "Y");
		}else{
			messageContent.put("IsRead", "N");
		}
		messageContent.put("IsMessageSync", "Y");
		messageContent.put("Task_ID", messageInfoType.getTaskId());
		try {
			
			
			ApplicationUtil.getInstance().updateDataInDB("Messaging",messageContent, context, "Message_ID = '" + messageInfoType.getMessageId() + "'",null);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			
		}
	}
	
	private static boolean isAssigner(String assignFrom,Context context){
		return true;
	}
	public  static synchronized boolean isNewMessage(String messageId,Context context){
		DBAdapter adapter=DBAdapter.getInstance(context);
		adapter.openDataBase();
		boolean flag=adapter.isMessageExist(messageId)==false;;
		adapter.close();
		return flag;
	}
	
	public static synchronized boolean isNewTask(String taskId,Context context){
		DBAdapter adapter=DBAdapter.getInstance(context);
		adapter.openDataBase();
		boolean flag=adapter.isTaskExist(taskId)==false;;
		adapter.close();
		return flag;
	}
	
	public static String getModyfiedDate(String taskId,Context context){
		DBAdapter adapter=DBAdapter.getInstance(context);
		adapter.openDataBase();
		String flag=adapter.getModyFiedDate(taskId);;
		adapter.close();
		return flag;
	}
	
	private static synchronized boolean isRecievedTask(String assignFrom,Context context,DBAdapter adapter){
		
		return true;
	}
	
	private static void markMessageAsRead(final Context context){
		new Thread(new Runnable() {
			@Override
					public void run() {
						DBAdapter adapter=DBAdapter.getInstance(context);
						adapter.openDataBase();
						List<String> ids=adapter.getMSGReadAsyncTaskIds();
						for (String taskId : ids) {
							BGConnector connector=new BGConnectorImpl();
							try {
							boolean flag=connector.markMessageAsRead(taskId, ApplicationUtil.getPreference(ApplicationConstant.regMobNo,context),"", ApplicationUtil.getPreference(ApplicationConstant.xtoken,context));
							if(flag){
								ContentValues initialValues = new ContentValues();
								initialValues.put("IsRead", "y");
								initialValues.put("MSG_TYPE", "READ");
								ApplicationUtil.getInstance().updateDataInDB("Messaging",initialValues, context, "Task_ID = '" + taskId + "'",null);
							}else{
								ContentValues initialValues = new ContentValues();
								initialValues.put("IsRead", "y");
								initialValues.put("MSG_TYPE", "READNONSYNC");
								ApplicationUtil.getInstance().updateDataInDB("Messaging",initialValues, context, "Task_ID = '" + taskId + "'",null);
							}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}finally{
								adapter.close();
							}
						}
						
					}
				}).start();
	}
	
	public static  void sync(String mobileNumber,Context context){
		try{
			
			DBAdapter adapte1=DBAdapter.getInstance(context);
			adapte1.openDataBase();
			String taskIds=adapte1.getTaskIds(context);
			adapte1.close();
			
			BGConnector connector=new BGConnectorImpl(context);
			Task task=connector.sync(mobileNumber, taskIds, context, ApplicationUtil.getPreference(ApplicationConstant.xtoken,context));//(mobileNumber, lastUpdatedDate,context,ApplicationUtil.getPreference(ApplicationConstant.xtoken,context));
			if(task!=null){
				TaskResponseType response=task.getResponse();
				if(response!=null){
					TaskResponseListType taskListType=response.getTaskList();
					if(taskListType!=null){
						List<TaskInfoType> taskList=taskListType.getTask();
						if(taskList!=null){
							
							for (TaskInfoType taskObj : taskList) {
								
								if(isNewTask(taskObj.getTaskId(), context)){
									createTask(taskObj, context);
									//flag=true;
								}else{							
									updateTask(taskObj, context);
								}
								
								MessageListType messages=taskObj.getMessages();
								if(messages!=null){
									List<MessageInfoType> message=messages.getMessage();
									if(message!=null){
										for (MessageInfoType messageInfoType : message) {
											if(isNewMessage(messageInfoType.getMessageId()+"", context)){
												createMessage(messageInfoType, context);

											}else{
												updateMessage(messageInfoType, context);
											}
										}
									}
								}

							}
						}
					}
				}
				
			}
			
		
		}catch(Exception exception){
			
		}
		
	}
	
}
