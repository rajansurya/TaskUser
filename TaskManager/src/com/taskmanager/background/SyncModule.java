package com.taskmanager.background;

import java.io.File;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.SQLException;
import android.os.Environment;
import android.os.Handler;

import com.google.android.gcm.GCMRegistrar;
import com.taskmanager.app.R;
import com.taskmanager.bean.ChangeAssigneeDto;
import com.taskmanager.bean.ChangeAssigneeResponseDto;
import com.taskmanager.bean.CreateTaskDTO;
import com.taskmanager.bean.MessageDto;
import com.taskmanager.bean.MessageListDTO;
import com.taskmanager.bean.OTPValidateDTO;
import com.taskmanager.bean.ResponseDto;
import com.taskmanager.bean.TaskInfoDto;
import com.taskmanager.bean.registrationDTO;
import com.taskmanager.bean.taskListDTO;
import com.taskmanager.database.DBAdapter;
import com.taskmanager.dto.TaskInfoEntity;
import com.taskmanager.ui.AllTaskActivity;
import com.taskmanager.ui.CommonUtilsUi;
import com.taskmanager.util.ApplicationConstant;
import com.taskmanager.util.ApplicationUtil;
import com.taskmanager.util.CommonUtil;
import com.taskmanager.util.DateUtils;
import com.taskmanager.webservice.BGConnector;
import com.taskmanager.webservice.BGConnectorImpl;


public class SyncModule implements Runnable {
	private static Context context;
	public static final File SD_CARD = Environment
			.getExternalStorageDirectory();
	private ProgressDialog progressDialog;
	private Handler progressHandler;

	public SyncModule(AssetManager assets, Context context1) {
		context = context1;
	}

	public SyncModule(Context context1) {
		context = context1;
		

	}

	
	
	@Override
	public synchronized void run() {
		TaskSyncUtils.getInstance(context).syscTaskANdMessage(context);
		TaskSyncUtils.getInstance(context).fullSync(ApplicationUtil.getPreference(ApplicationConstant.regMobNo,context),context, null);
		/*
		SyncModule module=new SyncModule(context);
		module.syncTaskWithServer(context);

		Log.d("Sync Current Thread Name Is", Thread.currentThread().getName());

		

		while (ApplicationConstant.STOP_SYNC_THREAD) {
			try {
				
				
				ArrayList<ArrayList<String>> reminderList = new ArrayList<ArrayList<String>>();
				reminderList = ApplicationUtil.getInstance().uploadListFromDB("Task",
						new String[] { "Task_ID, Reminder_Time" },
						" Status = 'OPEN' AND TaskType != 'self' AND IsJunk = 'N'",
						context);
				Setreminder(reminderList);
				
				
				ArrayList<ArrayList<String>> userTable = ApplicationUtil.getInstance().uploadListFromDB("User", new String[] {"Status,XToken,Mobile_Number,Number_Validation"}, null, context);
				if(userTable != null && !userTable.isEmpty())
				{
					ApplicationConstant.XToken = userTable.get(0).get(1).trim();
				}
				
				//System.out.println("Sync Current Thread Name Is"
						+ Thread.currentThread().getName());
				
				if (ApplicationUtil.checkInternetConn(context)
						&& !ApplicationUtil.checkServiceRunningStatus()) {
					ArrayList<ArrayList<String>> TargateList = new ArrayList<ArrayList<String>>();
					TargateList = ApplicationUtil
							.getInstance()
							.uploadListFromDB(
									"Deadline",
									new String[] { "Task_ID, Target_Date, Task_Desc,Assign_From,Assign_From_Name" },
									" Status = 'OPEN' AND IsJunk = 'N'  AND Target_Date != \"\" AND TaskType ='inbox' AND IsDeadlineDisplay= 'N' COLLATE NOCASE",
									context);
					if(TargateList.size()>0)
					{
						SetTargetNotify(TargateList);
					}
				}
				
				

				if (ApplicationUtil.checkInternetConn(context)
						&& !ApplicationUtil.checkServiceRunningStatus()) {
					try {
						if(ApplicationConstant.createTask_Flag_run_handle==0)
						{
						//	createTask();
						}

					} catch (Exception e) {
						e.printStackTrace();
						
						 * Changed by Purendra On 19-10-13 Multiple calling
						 

						
						 * try{ createTask();
						 * 
						 * 
						 * }catch (Exception ex) { ex.printStackTrace();
						 * 
						 * }
						 
					}
				} else
					//System.out.println("offline mode----> login");

				if (ApplicationUtil.checkInternetConn(context)
						&& !ApplicationUtil.checkServiceRunningStatus()) {
					try {
						//UpdateTask();
						

					} catch (Exception e) {
						e.printStackTrace();
						
						 * Changed by Purendra On 19-10-13 Multiple calling
						 

						
						 * try{ UpdateTask();
						 * 
						 * 
						 * }catch (Exception ex) { ex.printStackTrace();
						 * 
						 * }
						 
					}
				} else
					//System.out.println("offline mode----> login");
				if (ApplicationUtil.checkInternetConn(context)
						&& !ApplicationUtil.checkServiceRunningStatus()) {
					
				} else
					//System.out.println("offline mode----> login");
				if (ApplicationUtil.checkInternetConn(context)
						&& !ApplicationUtil.checkServiceRunningStatus()) {
					try {
						if(ApplicationConstant.message_Task_Flag_run_handle==0)
						{
							//UpdateMessages();
						}

					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						
						 * Changed by Purendra On 19-10-13 Multiple calling
						 

						
						 * try{ UpdateMessages();
						 * 
						 * 
						 * }catch (Exception ex) { // TODO: handle exception
						 * ex.printStackTrace();
						 * 
						 * }
						 
					}
				} else
					//System.out.println("offline mode----> login");

				if (ApplicationUtil.checkInternetConn(context)
						&& !ApplicationUtil.checkServiceRunningStatus()) {
					try {
						UpdateFav();

					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						
						 * Changed by Purendra On 19-10-13 Multiple calling
						 

						
						 * try{ UpdateFav();
						 * 
						 * 
						 * }catch (Exception ex) { // TODO: handle exception
						 * 
						 * ex.printStackTrace();
						 * 
						 * }
						 
					}
				//} 
			else
					//System.out.println("offline mode----> login");
				if (ApplicationUtil.checkInternetConn(context)
						&& !ApplicationUtil.checkServiceRunningStatus()) {
					try {
						// getting task list
						UpdatePriority();

					} catch (Exception e) {
						// TODO: handle exception

						e.printStackTrace();
						
						 * Changed by Purendra On 19-10-13 Multiple calling
						 

						
						 * try{ UpdatePriority();
						 * 
						 * 
						 * }catch (Exception ex) { // TODO: handle exception
						 * 
						 * ex.printStackTrace();
						 * 
						 * }
						 
					}
				} else
					//System.out.println("offline mode----> login");
				if (ApplicationUtil.checkInternetConn(context)
						&& !ApplicationUtil.checkServiceRunningStatus()) {
					try {
						// getting task list
						UpdateReminder();

					} catch (Exception e) {
						// TODO: handle exception

						e.printStackTrace();
						
						 * Changed by Purendra On 19-10-13 Multiple calling
						 

						
						 * try{ UpdateReminder();
						 * 
						 * 
						 * }catch (Exception ex) { // TODO: handle exception
						 * 
						 * ex.printStackTrace();
						 * 
						 * }
						 
					}
				} else
					//System.out.println("offline mode----> login");
				if (ApplicationUtil.checkInternetConn(context)
						&& !ApplicationUtil.checkServiceRunningStatus()) {
					try {
						// getting task list
						UpdateStatus();

					} catch (Exception e) {
						// TODO: handle exception

						e.printStackTrace();

						
						 * try{ UpdateStatus();
						 * 
						 * 
						 * }catch (Exception ex) { // TODO: handle exception
						 * 
						 * ex.printStackTrace();
						 * 
						 * }
						 
					}
				} else
					//System.out.println("offline mode----> login");

				if (ApplicationUtil.checkInternetConn(context)
						&& !ApplicationUtil.checkServiceRunningStatus()) {
					try {
						// getting task list
						getTaskList();

					} catch (Exception e) {
						// TODO: handle exception

						e.printStackTrace();

						
						 * try{ getTaskList();
						 * 
						 * 
						 * }catch (Exception ex) { // TODO: handle exception
						 * 
						 * ex.printStackTrace();
						 * 
						 * }
						 
					}
				} else
					//System.out.println("offline mode----> login");
				if (ApplicationUtil.checkInternetConn(context)
						&& !ApplicationUtil.checkServiceRunningStatus()) {
					try {
						// getting task Message list
						getTaskMessageList();

					} catch (Exception e) {
						// TODO: handle exception

						e.printStackTrace();

						
						 * try{ getTaskMessageList();
						 * 
						 * 
						 * }catch (Exception ex) { // TODO: handle exception
						 * 
						 * ex.printStackTrace();
						 * 
						 * }
						 
					}
				} else
					//System.out.println("offline mode----> login");

				if (ApplicationConstant.needUIRefresh) {
					//System.out.println("Reload list called--From thread--"
							+ ApplicationConstant.needUIRefresh);
					ApplicationConstant.needUIRefresh = false;
					reloadListOnScreen();
				}

				try {
					ApplicationConstant.SYNC_FLAG = false;
					//System.out.println("thread sleeping for"
							+ ApplicationConstant.SYNC_SLEEPTIME * 1000);
					Thread.sleep(ApplicationConstant.SYNC_SLEEPTIME * 1000);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}

				if (ApplicationConstant.Is_First)
					ApplicationConstant.Is_First = false;

			} catch (Exception e) {
				e.printStackTrace();

			}
			if(ApplicationUtil.checkInternetConn(context))
					{
						reloadListOnScreen();
					}
			
			
			
			//System.out.println("hello");
		}

	*/}

	/**
	 * method to receive task list
	 * 
	 * @return
	 */
	public String getTaskList() {
		
				try {
					
					TaskSyncUtils.getInstance(context).fullSync(ApplicationUtil.getPreference(ApplicationConstant.regMobNo,context),context, null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		return null;

	}

	/**
	 * method used to receive task messages
	 * 
	 * @return
	 */
	public String getTaskMessageList() {
		
				try {
					// "IsMessage = 'true'"
					ArrayList<ArrayList<String>> taskList = ApplicationUtil
							.getInstance().uploadListFromDB("Task",
									new String[] { "Task_ID,Status" }, null,
									context);
					for (int i = 0; i < taskList.size(); i++) {
						ArrayList<MessageListDTO> taskMessageListResponse = new ArrayList<MessageListDTO>();
						taskMessageListResponse = ApplicationUtil.getInstance()
								.getSyncServer(context)
								.getTaskMessageList(taskList.get(i).get(0));
						if (taskMessageListResponse != null
								&& !taskMessageListResponse.isEmpty()) {
							saveTaskMessageListResponse(
									taskMessageListResponse, taskList.get(i)
									.get(1));
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		return null;

	}

	/**
	 * method to request for resends OTP
	 * 
	 * @return responseMsg
	 */
	public String resendOTP() {
		// TODO Auto-generated method stub

		String responseMsg = "";
		try {
			responseMsg = ApplicationUtil
					.getInstance()
					.getSyncServer(context)
					.resendOTP(ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context),
							ApplicationConstant.XToken);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseMsg;
	}

	public void syncTaskWithServer(Context context){
		DBAdapter adapter= DBAdapter.getInstance(context);
		try {
		CreateTaskDTO	createTask=null;
		StringBuilder builder=new StringBuilder();
		String taskDes="";String taskFrom="";
		List<String> asyncGroupIds=adapter.getAsyncGroupId();
		for (String groupId : asyncGroupIds) {
			List<TaskInfoEntity> taskInfoEntities=adapter.getAsyncTaskByGropId(groupId);
			for (TaskInfoEntity taskInfoEntity : taskInfoEntities) {
				//check for self assignment
				if(taskInfoEntity.getTaskType().equalsIgnoreCase("self")){
					builder.append(taskInfoEntity.getUserInfoFrom().getMobile_number()+",");
				}else{
					builder.append(taskInfoEntity.getUserInfoTo().getMobile_number()+",");
				}
				
				taskDes=taskInfoEntity.getTask_description();
				taskFrom=taskInfoEntity.getUserInfoFrom().getMobile_number();	;
			}
			if(taskFrom!=null&&!taskFrom.isEmpty()){
			builder.replace(builder.length()-1, builder.length(), "");
			taskListDTO task=new taskListDTO();
			task.setAssignTo(builder.toString());
			task.setAssignFrom(taskFrom);
			task.setTaskDesc(taskDes);
			task.setReminderTime("");
			task.setPriority("2");
			task.setCreationDate(ApplicationUtil.getCurrentDateOfSystem());
			task.setClosureDateTime("");
			task.setReminderTime("");
			task.setFavouraite("false");
			task.setStatus("OPEN");
			task.setFavouraite("false");
			createTask = ApplicationUtil.getInstance().getSyncServer(context).createTask(task);
			
			if(createTask!=null&&createTask.getStatus().equals("00")){
				adapter.deleteTask(groupId);
				List<TaskInfoDto> list=createTask.getTaskList();
				List<String> listNotRegisterd=createTask.getNotRegisteredNo();
				ContentValues contentValues = new ContentValues();
				contentValues.put("groupId", list.get(0).getGroupId());
				contentValues.put("TASK_SYSNC_STATUS", "Y");
				ApplicationUtil.getInstance().saveDataInDB("TASK_GROUP", null, contentValues,context);
				for (TaskInfoDto taskInfoDto : list) {
					String assingFrom=taskInfoDto.getAssignFrom();
					if(assingFrom!=null)
						assingFrom=assingFrom.substring(assingFrom.length()-10,assingFrom.length()).trim();
					ContentValues initialValues = new ContentValues();
					initialValues.put("Task_ID", taskInfoDto.getTaskId());
					initialValues.put("Group_ID", taskInfoDto.getGroupId());
					initialValues.put("IsTaskSync", "Y");
					initialValues.put("IsJunk", "N");
					initialValues.put("Assign_From", assingFrom);
					initialValues.put("Assign_From_Name", ApplicationConstant.ApplicantName);
					initialValues.put("Assign_To", taskInfoDto.getAssignTo());
					initialValues.put("Assign_To_Name", task.getAssignToName());
					initialValues.put("CreationDate", task.getCreationDate());
					initialValues.put("Task_Desc", task.getTaskDesc());
					initialValues.put("Target_Date", task.getTargetDateTime());
					initialValues.put("Priority", task.getPriority());
					initialValues.put("Closure_Date", task.getClosureDateTime());
					initialValues.put("Reminder_Time", task.getReminderTime());
					initialValues.put("IsFavouraite", task.getFavouraite());
					initialValues.put("Status", task.getStatus());
					initialValues.put("CreationDate", DateUtils.dateToString(new Date(),DateUtils.ddmmYYYYHHmmSS)+"");
					initialValues.put("CreationDateTime", DateUtils.getDateFromString( DateUtils.dateToString(new Date(),DateUtils.ddmmYYYYHHmmSS), DateUtils.ddmmYYYYHHmmSS).getTime());
					initialValues.put("ClosureDate", -1);
					initialValues.put("isMsgSend", task.getIsMsgSendChecked());
					//initialValues.put("ClosureDate", -1);
			//	initialValues.put("ClosureDate", "");
				if (("\"" + task.getAssignFrom() + "\"").trim().equals(
						task.getAssignTo()))
					initialValues.put("TaskType", "self");
				else
					initialValues.put("TaskType", "sent");
				
				
				if(listNotRegisterd!=null&&!listNotRegisterd.isEmpty())
				{
					if(listNotRegisterd.contains(taskInfoDto.getAssignTo())&&task.getIsMsgSendChecked().equalsIgnoreCase("Y"))
					{
						ApplicationUtil.sendSMS(taskInfoDto.getAssignTo(), task.getTaskDesc());
					}
				}
					ApplicationUtil.getInstance().saveDataInDB("Task", null, initialValues,context);
				}
		
			
			}
			
			
		}
			if(createTask!=null)
			adapter.deteleteGroup(groupId);
		}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			//adapter.close();
		}
		
	}
	
	/**
	 * this method is to sync newly added task to service
	 * 
	 * @param task
	 * @return
	 */
	public synchronized ResponseDto  createTask(final taskListDTO task) {
		ResponseDto repsDto=new ResponseDto();
		AllTaskActivity.isTaskOrMsgCreationInitated=true;
		String transactionId=CommonUtil.getTransactionId(context);
		task.setTransactioId(transactionId);
		String assigneeList[]=task.getAssignTo().split(",");
		String taskIds="";
		if(task!=null)
		{
		String mobileNumberFrom=task.getAssignFrom();
		String mobileNumberTo=task.getAssignTo();
		
		mobileNumberTo = mobileNumberTo.replaceAll("\"", "");
		String[] all_num = mobileNumberTo.split(",");
		
		for (int i = 0; i < all_num.length; i++) 
		{
			ContentValues initialValues = new ContentValues();
			String number_str = all_num[i].trim();
			System.out.println("Numbers are .............. " + number_str);
			initialValues.put("REG_STATUS", "REGISTERED");
			
			ApplicationUtil.getInstance().updateDataInDB("USERS_CONTACT", initialValues,context, "MOBILE_NUMBER ='" + number_str + "'", null);
		}
		
		
		/*ContentValues initialValues = new ContentValues();
		DBAdapter dbAdapter=DBAdapter.getInstance(context);
		dbAdapter.openDataBase();
		initialValues.put("REG_STATUS", "Y");
		dbAdapter.updateRecord("Task", initialValues,"MOBILE_NUMBER='" + number + "'", null);
		dbAdapter.close();*/
		
		try{
		if(mobileNumberFrom!=null)
			mobileNumberFrom=mobileNumberFrom.substring(mobileNumberFrom.length()-10,mobileNumberFrom.length()).trim();
		if(mobileNumberTo!=null)
			mobileNumberTo=mobileNumberTo.substring(mobileNumberTo.length()-10,mobileNumberTo.length()).trim();
		}catch (Exception e)
		{
			
		}
		
			CreateTaskDTO createTask = null;
			try {
				if (ApplicationUtil.checkInternetConn(context))
				{
					createTask = ApplicationUtil.getInstance().getSyncServer(context).createTask(task);
													
				}
				
				//System.out.println("Create task is " + createTask);
				if(createTask!=null&&createTask.getStatus()!=null&&createTask.getStatus().equals("00")){
					List<TaskInfoDto> list=createTask.getTaskList();
					List<String> listNotRegisterd=createTask.getNotRegisteredNo();
				//	ContentValues contentValues = new ContentValues();
				//	contentValues.put("groupId", list.get(0).getGroupId());
				//	contentValues.put("TASK_SYSNC_STATUS", "Y");
				//	ApplicationUtil.getInstance().saveDataInDB("TASK_GROUP", null, contentValues,context);
					for (final TaskInfoDto taskInfoDto : list) {
						String assingFrom=taskInfoDto.getAssignFrom();
						if(assingFrom!=null){
							assingFrom=assingFrom.substring(assingFrom.length()-10,assingFrom.length()).trim();
						}
						ContentValues initialValues = new ContentValues();
						taskIds=taskIds+"'"+taskInfoDto.getTaskId()+"',";
						initialValues.put("Task_ID", taskInfoDto.getTaskId());
						initialValues.put("Group_ID", taskInfoDto.getGroupId());
						initialValues.put("IsTaskSync", "Y");
						initialValues.put("IsJunk", "N");
						initialValues.put("transactionId", transactionId);
						initialValues.put("Assign_From", assingFrom);
						initialValues.put("Assign_From_Name", ApplicationConstant.ApplicantName);
						initialValues.put("Assign_To", taskInfoDto.getAssignTo());
						initialValues.put("Assign_To_Name", ApplicationUtil.getContactNameFromPhoneNo(context, taskInfoDto.getAssignTo()));
						initialValues.put("CreationDate", task.getCreationDate());
						initialValues.put("Task_Desc", task.getTaskDesc());
						initialValues.put("Target_Date", task.getTargetDateTime());
						try {
							SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
							initialValues.put("Target_Date_TIME",dateFormat.parse(task.getTargetDateTime()).getTime());
						} catch (ParseException e) {
							e.printStackTrace();
						}
						initialValues.put("Priority", task.getPriority());
						initialValues.put("Closure_Date", task.getClosureDateTime());
						initialValues.put("Reminder_Time", task.getReminderTime());
						if(task.getReminderTime()!=null&&!task.getReminderTime().trim().isEmpty()){
							initialValues.put("isAlarmSet", "Y");
							initialValues.put("Alarm_Date_Time", task.getReminderTime());
							Thread thread = new Thread()
							{
								public void run() 
								{
									if(task.getReminderTime()!=null)
										new CommonUtilsUi().setAlarmManager(context,task.getReminderTime(),taskInfoDto.getTaskId()+"",task.getTaskDesc());
								};
							};
							thread.start();
						}else{
							initialValues.put("isAlarmSet", "N");
						}
						initialValues.put("IsFavouraite", task.getFavouraite());
						initialValues.put("IsTaskSync", "Y");
						initialValues.put("IsTaskRead", "N");
						initialValues.put("isMsgSend", "true");
						initialValues.put("Status", task.getStatus());
						initialValues.put("CreationDate", ApplicationUtil.getGMTDate());
						initialValues.put("CreationDateTime",ApplicationUtil.getGMTLong());
						initialValues.put("UPDATED_DATE", ApplicationUtil.getGMTDate());
						initialValues.put("UPDATION_DATE_TIME",ApplicationUtil.getGMTLong());
						initialValues.put("ClosureDate", -1);
						if (("\"" + task.getAssignFrom() + "\"").trim().equals(task.getAssignTo())){
							initialValues.put("TaskType", "self");
							initialValues.put("DISPLAY_NAME", "self");
							
						}else{
							initialValues.put("TaskType", "sent");
							initialValues.put("DISPLAY_NAME", ApplicationUtil.getContactNameFromPhoneNo(context, taskInfoDto.getAssignTo()));

						}
						
						if(listNotRegisterd!=null&&!listNotRegisterd.isEmpty())
						{
							System.out.println("I am in if list value .... " + listNotRegisterd);
																					
							for (int i = 0; i < listNotRegisterd.size(); i++) 
							{
								ContentValues initialValues1 = new ContentValues();
							
								String num_str = listNotRegisterd.get(i);
								num_str = num_str.replaceAll("\"", "");
								System.out.println("values are .... " + num_str);
								initialValues1.put("REG_STATUS", "N");
								
								ApplicationUtil.getInstance().updateDataInDB("USERS_CONTACT", initialValues1,context, "MOBILE_NUMBER ='" + num_str + "'", null);
							}
							
							if(listNotRegisterd.contains(taskInfoDto.getAssignTo()))
							{
								System.out.println("I am in if list value if sms.... " + listNotRegisterd.toString());
								ApplicationUtil.sendSMS(taskInfoDto.getAssignTo(), task.getTaskDesc());
							}
							
						}
						else
						{
							
						}
						
						ApplicationUtil.getInstance().saveDataInDB("Task", null, initialValues,context);						
						createNotaiont(task.getTaskDesc(), taskInfoDto.getTaskId());
						
					}
					
					
					
				}else{
					
					for (int i = 0; i < assigneeList.length; i++) {
						final int randomNo = (int) (Math.random() * 1000000);
						taskIds=taskIds+"'"+randomNo+"',";
						ContentValues initialValues = new ContentValues();
						initialValues.put("Task_ID", randomNo+"");
						initialValues.put("IsTaskSync", "N");
						initialValues.put("TASK_SYNC_TYPE", "NEW");
						initialValues.put("isTaskNew", "Y");
						initialValues.put("isMsgSend", "true");
						initialValues.put("IsJunk", "N");
						initialValues.put("transactionId", transactionId);
						initialValues.put("Group_ID", "");
						initialValues.put("Assign_From", mobileNumberFrom);
						initialValues.put("Assign_From_Name", ApplicationConstant.ApplicantName);
						initialValues.put("Assign_To", assigneeList[i]);
						initialValues.put("Assign_To_Name", ApplicationUtil.getContactNameFromPhoneNo(context,assigneeList[i]));
						initialValues.put("CreationDate", task.getCreationDate());
						initialValues.put("Task_Desc", task.getTaskDesc());
						initialValues.put("Target_Date", task.getTargetDateTime());
						try {
							SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
							initialValues.put("Target_Date_TIME",dateFormat.parse(task.getTargetDateTime()).getTime());
						} catch (ParseException e) {
							e.printStackTrace();
						}
						initialValues.put("Priority", task.getPriority());
						initialValues.put("Closure_Date", task.getClosureDateTime());
						initialValues.put("Reminder_Time", task.getReminderTime());
						if(task.getReminderTime()!=null&&!task.getReminderTime().trim().isEmpty()){
							initialValues.put("isAlarmSet", "Y");
							initialValues.put("Alarm_Date_Time", task.getReminderTime());
							Thread thread = new Thread()
							{
								public void run() 
								{
									if(task.getReminderTime()!=null)
										new CommonUtilsUi().setAlarmManager(context,task.getReminderTime(),randomNo+"",task.getTaskDesc());
								};
							};
							thread.start();
						}else{
							initialValues.put("isAlarmSet", "N");
						}
						initialValues.put("IsFavouraite", task.getFavouraite());
						initialValues.put("IsTaskRead", "N");
						initialValues.put("Status", task.getStatus());
						initialValues.put("CreationDate", ApplicationUtil.getGMTDate()+"");
						initialValues.put("CreationDateTime", ApplicationUtil.getGMTLong());
						initialValues.put("UPDATED_DATE",  ApplicationUtil.getGMTDate()+"");
						initialValues.put("UPDATION_DATE_TIME",ApplicationUtil.getGMTLong());
						initialValues.put("ClosureDate", -1);
						
						

						if (("\"" + task.getAssignFrom() + "\"").trim().equals(task.getAssignTo())){
							initialValues.put("TaskType", "self");
							initialValues.put("DISPLAY_NAME", "Self");
						}
						else{
							initialValues.put("TaskType", "sent");
							initialValues.put("DISPLAY_NAME", ApplicationUtil.getContactNameFromPhoneNo(context,assigneeList[i]));
							
						}

						ApplicationUtil.getInstance().saveDataInDB("Task", null, initialValues,context);
						createNotaiont(task.getTaskDesc(), randomNo+"");
					}
					
					if(createTask!=null&&"401".equalsIgnoreCase(createTask.getStatus())){
						repsDto.setStatus(createTask.getStatus());
						repsDto.setMessageDescription(createTask.getDisplayMessage());
						return repsDto;
					}
					
				}
				
				

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//
		AllTaskActivity.isTaskOrMsgCreationInitated=false;
		return repsDto;

	}
	
	public void updateTaskContent(final String taskId,final String taskDescription){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					BGConnector bgConnector=new BGConnectorImpl();
				boolean flag=bgConnector.updateTaskContent(ApplicationUtil.getPreference(ApplicationConstant.regMobNo,context), ApplicationUtil.getPreference(ApplicationConstant.xtoken,context), taskId, taskDescription);
				if(flag){
					ContentValues initialValues = new ContentValues();
					initialValues.put("IsTaskSync", "Y");
					initialValues.put("Task_Desc",taskDescription);
					initialValues.put("UPDATED_DATE", ApplicationUtil.getGMTDate());
					initialValues.put("UPDATION_DATE_TIME",ApplicationUtil.getGMTLong());
					initialValues.put("IsTaskUpdateSync", "Y");
					ApplicationUtil.getInstance().updateDataInDB("Task",initialValues, context,"Task_ID ='" + taskId + "'", null);
					createNotaiont(taskDescription, taskId);
				}else{
					ContentValues initialValues = new ContentValues();
					initialValues.put("IsTaskSync", "N");
					initialValues.put("Task_Desc",taskDescription);
					initialValues.put("UPDATED_DATE", ApplicationUtil.getGMTDate());
					initialValues.put("UPDATION_DATE_TIME",ApplicationUtil.getGMTLong());
					initialValues.put("IsTaskUpdateSync", "N");
					ApplicationUtil.getInstance().updateDataInDB("Task",initialValues, context,"Task_ID ='" + taskId + "'", null);
					createNotaiont(taskDescription, taskId);
				}
				
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}).start();
		
		
	}
	
	
	/*public synchronized String  createTask() {

		ArrayList<ArrayList<String>> listDBElement = ApplicationUtil
				.getInstance()
				.uploadListFromDB(
						"Task",
						new String[] { "Task_ID,Assign_From,Assign_To,Task_Desc,Target_Date,Priority,Closure_Date,Reminder_Time,IsFavouraite,Status" },
						"IsTaskSync ='N'", context);
		for (int i = 0; i < listDBElement.size(); i++) {

			taskListDTO task = new taskListDTO();
			task.setAssignFrom(listDBElement.get(i).get(1));
			task.setAssignTo(listDBElement.get(i).get(2));
			task.setTaskDesc(listDBElement.get(i).get(3));
			task.setTargetDateTime(listDBElement.get(i).get(4));
			task.setPriority(listDBElement.get(i).get(5));
			task.setClosureDateTime(listDBElement.get(i).get(6));
			task.setReminderTime(listDBElement.get(i).get(7));
			task.setFavouraite(listDBElement.get(i).get(8));
			task.setStatus(listDBElement.get(i).get(9));
			
			 * ContentValues initialValues = new ContentValues();
			 * initialValues.put("IsTaskSync", "Y");
			 * ApplicationUtil.getInstance().updateDataInDB("Task",
			 * initialValues, context, "Task_ID ='" +
			 * listDBElement.get(i).get(10) + "'", null);
			 

			CreateTaskDTO createTask = new CreateTaskDTO();
			try {
				if (ApplicationUtil.checkInternetConn(context))
				{
					createTask = ApplicationUtil.getInstance().getSyncServer()
						.createTask(task);
				}
				//System.out.println("Create task is " + createTask);
				// //System.out.println("Create Task Status--->"+createTask.getStatus());

				if (createTask != null
						&& createTask
						.getStatus()
						.trim()
						.equalsIgnoreCase(
								context.getResources().getString(
										R.string.success))) {
					ApplicationUtil.getInstance().deleteRowInTable(
							"Task",
							"WHERE Task_ID = '" + listDBElement.get(i).get(0)
							+ "'", context);
					getTaskList();
				}
				
				 * else { ContentValues initialValues1 = new ContentValues();
				 * initialValues1.put("IsTaskSync", "N");
				 * ApplicationUtil.getInstance().updateDataInDB("Task",
				 * initialValues, context, "Task_ID ='" +
				 * listDBElement.get(i).get(10) + "'", null); }
				 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//

		return null;

	}*/

	/**
	 * method to update task description and sync in service
	 * 
	 * @return
	 */
	/*public   String UpdateTask() {

		//System.out.println("Thread is executing.");
		ArrayList<ArrayList<String>> listDBElement = ApplicationUtil
				.getInstance()
				.uploadListFromDB(
						"Task",
						new String[] { "Task_ID,Assign_From,Task_Desc, Assign_To, Priority, Target_Date, Closure_Date,Reminder_Time,IsMessage, IsFavouraite,TASK_SYNC_TYPE" },
						"IsTaskUpdateSync ='N'", context);
		//System.out.println("Thread is listDBElement." + listDBElement);
		for (int i = 0; i < listDBElement.size(); i++) {

			try {

				//System.out.println("Inside try Block");
				String status = ApplicationUtil
						.getInstance()
						.getSyncServer()
						.updateTask(listDBElement.get(i).get(0),
								listDBElement.get(i).get(1),
								listDBElement.get(i).get(2),
								listDBElement.get(i).get(3),
								listDBElement.get(i).get(4),
								listDBElement.get(i).get(5),
								listDBElement.get(i).get(6),
								listDBElement.get(i).get(7),
								listDBElement.get(i).get(8),
								listDBElement.get(i).get(9));
				//System.out.println("Task Update Status--->" + status);
				if (status != null
						&& status.trim().equalsIgnoreCase(
								context.getResources().getString(
										R.string.success))) {
					//System.out.println("TAsk UPdate inside status = " + status);
					ContentValues initialValues = new ContentValues();
					initialValues.put("IsTaskUpdateSync", "Y");
					ApplicationUtil.getInstance().updateDataInDB("Task",
							initialValues, context,
							"Task_ID ='" + listDBElement.get(i).get(0) + "'",
							null);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;

	}*/
	
	
	
	
	
	public   String UpdateTask(com.taskmanager.bean.UpdateTask task) {

		try {
			String status;
		ContentValues initialValues = new ContentValues();
		//
		
		initialValues.put("IsTaskSync", "N");
		initialValues.put("IsJunk", "N");
		initialValues.put("Task_Desc",task.getTask_Desc());
		
		initialValues.put("UPDATED_DATE", ApplicationUtil.getGMTDate());
		initialValues.put("UPDATION_DATE_TIME",ApplicationUtil.getGMTLong());
		initialValues.put("ClosureDate", -1);
		initialValues.put("Task_Desc", task.getTask_Desc());
		initialValues.put("IsTaskUpdateSync", task.getIsTaskUpdateSync());
		initialValues.put("TASK_SYNC_TYPE", task.getTASK_SYNC_TYPE());
		//initialValues.put("", "");
		ApplicationUtil.getInstance().updateDataInDB("Task",
				initialValues, context,
				"Task_ID ='" + task.getTask_id() + "'", null);
		
		
			status = ApplicationUtil
					.getInstance()
					.getSyncServer(context)
					.updateTask(task.getTask_id(),
							task.getAssignFrom(),
							task.getTask_Desc(),
							task.getAssignTo(),
							task.getPriority(),
							task.getTarget_date(),"",
							task.getReminderTime(),
							"",
							"");
		
		//String taskId, String Assign_from, String Desc,String assignTo, String Priority,String targetDate,String closerDate,String reminderTime,String message,String favouraite
		//System.out.println("Task Update Status--->" + status);
		if (status != null
				&& status.trim().equalsIgnoreCase(
						context.getResources().getString(
								R.string.success))) {
			//System.out.println("TAsk UPdate inside status = " + status);
			ContentValues initialValues1 = new ContentValues();
			initialValues1.put("IsTaskUpdateSync", "Y");
			initialValues1.put("IsTaskSync", "Y");
			//initialValues1.put("Task_Desc", task.getTask_Desc());
			ApplicationUtil.getInstance().updateDataInDB("Task",
					initialValues1, context,
					"Task_ID ='" + task.getTask_id() + "'",
					null);
//			ApplicationUtil.getInstance().updateDataInDB("Task",
//					initialValues1, context,
//					"Group_ID ='" + task.getGroupId() + "'",
//					null);
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;	
	} 
		
	

	/**
	 * method to change favorite status of task
	 * 
	 * @return
	 */
	/*public String UpdateFav() {
		Thread thread = new Thread() {

			@Override
			public void run() {
				ArrayList<ArrayList<String>> listDBElement = ApplicationUtil
						.getInstance().uploadListFromDB("Task",
								new String[] { "Task_ID,IsFavouraite" },
								"IsFAvSync ='N'", context);
				for (int i = 0; i < listDBElement.size(); i++) {
					try {
						String status = ApplicationUtil
								.getInstance()
								.getSyncServer()
								.updateFav(listDBElement.get(i).get(0),
										listDBElement.get(i).get(1));
						//System.out.println("Status--->" + status);
						if (status.trim().equalsIgnoreCase(
								context.getResources().getString(
										R.string.success))) {
							ContentValues initialValues = new ContentValues();
							initialValues.put("IsFavSync", "Y");
							ApplicationUtil.getInstance().updateDataInDB(
									"Task",
									initialValues,
									context,
									"Task_ID ='" + listDBElement.get(i).get(0)
									+ "'", null);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		thread.start();
		return null;

	}*/

	/**
	 * method to change priority of task
	 * 
	 * @return
	 *//*
	public String UpdatePriority() {
		Thread thread = new Thread() {

			@Override
			public void run() {
				ArrayList<ArrayList<String>> listDBElement = ApplicationUtil
						.getInstance().uploadListFromDB("Task",
								new String[] { "Task_ID,Priority" },
								"IsPrioritySync ='N'", context);
				for (int i = 0; i < listDBElement.size(); i++) {
					try {
						String status = ApplicationUtil
								.getInstance()
								.getSyncServer(context)
								.updatePriority(listDBElement.get(i).get(0),
										listDBElement.get(i).get(1));
						//System.out.println("Status--->" + status);
						if (status.trim().equalsIgnoreCase(
								context.getResources().getString(
										R.string.success))) {
							ContentValues initialValues = new ContentValues();
							initialValues.put("IsPrioritySync", "Y");
							ApplicationUtil.getInstance().updateDataInDB(
									"Task",
									initialValues,
									context,
									"Task_ID ='" + listDBElement.get(i).get(0)
									+ "'", null);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		thread.start();
		return null;

	}*/

	/**
	 * @author maksood
	 * method to change status of task accordingly
	 * 
	 * @return  
	 */
	
	public void changeTaskStatus( final String taskId,final String taskStatus,final boolean isAssigner,final String mobileNumber){
	
		Thread thread = new Thread() {
			public void run() {
				try {
				
					ContentValues initialValues = new ContentValues();
	 				
						if("CLOSE".equalsIgnoreCase(taskStatus)){
							Date closerDate=ApplicationUtil.getLocalDateInGMT(new Date());
							if(isAssigner){
							initialValues.put("Status", taskStatus);
							initialValues.put("isActive", "N");
							initialValues.put("ClosedBy", CommonUtil.getValidMsisdn(mobileNumber));
							initialValues.put("ClosureDate",closerDate.getTime());
							//initialValues.put("UPDATED_DATE", ApplicationUtil.getGMTDate());
							//initialValues.put("UPDATION_DATE_TIME",ApplicationUtil.getGMTLong());
							}else{
								initialValues.put("Status", taskStatus);
								initialValues.put("isActive", "Y");
								initialValues.put("ClosedBy", CommonUtil.getValidMsisdn(mobileNumber));
								initialValues.put("ClosureDate",closerDate.getTime());
								
							}

						}else{
							initialValues.put("Status", taskStatus);
							initialValues.put("isActive", "Y");
							//initialValues.put("ClosureDate",ApplicationUtil.getLocalDateInGMT(new Date()).getTime());
						}
						
						
					
					//
					String status = ApplicationUtil.getInstance().getSyncServer(context).updateStatus(taskId, taskStatus, isAssigner, mobileNumber);
					if("00".equals(status)){
						initialValues.put("IsStatusSync", "Y");
						initialValues.put("IsTaskSync", "Y");
						ApplicationUtil.getInstance().updateDataInDB("Task",initialValues, context,"Task_ID = '" + taskId + "'", null);
					}else{
						initialValues.put("IsStatusSync", "N");
						initialValues.put("IsTaskSync", "N");
						ApplicationUtil.getInstance().updateDataInDB("Task",initialValues, context,"Task_ID = '" + taskId + "'", null);
					}
					//if ((ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context).trim().contains(
					//		 CommonUtil.getValidMsisdn(mobileNumber))))
					

				} catch (Exception e) {
					
				}
				
				
			};
		};
		thread.start();
		
	}
	
	public String UpdateStatus() {
		Thread thread = new Thread() {

			@Override
			public void run() {
				ArrayList<ArrayList<String>> listDBElement = ApplicationUtil
						.getInstance().uploadListFromDB("Task",new String[] { "Task_ID,Status,TaskType" },
								"IsStatusSync ='N'", context);
				for (int i = 0; i < listDBElement.size(); i++) {
					try {
						String status = "";

						if (listDBElement
								.get(i)
								.get(1)
								.trim()
								.equalsIgnoreCase(
										context.getResources().getString(
												R.string.close_status))) {
							if (listDBElement.get(i).get(2).trim()
									.equalsIgnoreCase("Inbox")) {
								status = ApplicationUtil
										.getInstance()
										.getSyncServer(context)
										.updateStatus(
												listDBElement.get(i).get(0),
												listDBElement.get(i).get(1),
												false,
												ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context));

							} else {
								status = ApplicationUtil
										.getInstance()
										.getSyncServer(context)
										.updateStatus(
												listDBElement.get(i).get(0),
												listDBElement.get(i).get(1),
												true,
												ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context));
								if (status.trim().equalsIgnoreCase(
										context.getResources().getString(
												R.string.success)))

									ApplicationUtil.getInstance()
									.deleteRowInTable(
											"Task",
											"WHERE Task_ID = '"
													+ listDBElement
													.get(i)
													.get(0)
													+ "'", context);
							}
						} else {
							status = ApplicationUtil
									.getInstance()
									.getSyncServer(context)
									.updateStatus(listDBElement.get(i).get(0),
											listDBElement.get(i).get(1), false,
											ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context));
						}

						//System.out.println("Status--->" + status);
						if (status.trim().equalsIgnoreCase(
								context.getResources().getString(
										R.string.success))) {
							ContentValues initialValues = new ContentValues();
							initialValues.put("IsStatusSync", "Y");
							ApplicationUtil.getInstance().updateDataInDB(
									"Task",
									initialValues,
									context,
									"Task_ID ='" + listDBElement.get(i).get(0)
									+ "'", null);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		thread.start();
		return null;

	}

	/**
	 * this method updates reminder time for task
	 * 
	 * @return
	 *//*
	public String UpdateReminder() {
		Thread thread = new Thread() {

			@Override
			public void run() {
				ArrayList<ArrayList<String>> listDBElement = ApplicationUtil
						.getInstance().uploadListFromDB("Task",
								new String[] { "Task_ID,Reminder_Time" },
								"IsReminderSync ='N'", context);
				for (int i = 0; i < listDBElement.size(); i++) {
					try {
						String status = ApplicationUtil
								.getInstance()
								.getSyncServer(context)
								.updateReminder(listDBElement.get(i).get(0),
										listDBElement.get(i).get(1));
						//System.out.println("Status--->" + status);
						if (status.trim().equalsIgnoreCase(
								context.getResources().getString(
										R.string.success))) {
							ContentValues initialValues = new ContentValues();
							initialValues.put("IsReminderSync", "Y");
							ApplicationUtil.getInstance().updateDataInDB(
									"Task",
									initialValues,
									context,
									"Task_ID ='" + listDBElement.get(i).get(0)
									+ "'", null);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		thread.start();
		return null;

	}*/

	
	/**
	 * this method add and sync new message from device to sevice
	 * 
	 * @return
	 */
	public synchronized ResponseDto createMessage(MessageDto messageDto ) {
		AllTaskActivity.isTaskOrMsgCreationInitated=true;
		ResponseDto responseDto=new ResponseDto();
			String status = "";
			//String transactionId=CommonUtil.getTransactionId(context);
			ContentValues initialValues2 = new ContentValues();
			ContentValues initialValues = new ContentValues();
		//	initialValues.put("UPDATED_DATE",  ApplicationUtil.getGMTDate()+"");
		//	initialValues.put("UPDATION_DATE_TIME",ApplicationUtil.getGMTLong());

			try {
				if (ApplicationUtil.checkInternetConn(context)){
					MessageListDTO msg = new MessageListDTO();
					msg.setTaskId(messageDto.getTask_ID());
					msg.setmFrom(messageDto.getM_From());
					msg.setmTo(messageDto.getM_To());
					msg.setmDesc(messageDto.getM_Desc());
					msg.setOldMessageId(messageDto.getMessage_ID());
					msg.setTransactioId(messageDto.getTransactionId());
					responseDto = ApplicationUtil.getInstance().getSyncServer(context).createMessageNew(msg);
				if("00".equalsIgnoreCase(responseDto.getStatus())){
						status="00";
						initialValues2.put("Message_ID", responseDto.getMessageId());
						initialValues2.put("IsMessageSync", "Y");
						initialValues2.put("MSG_TYPE", "READ");
						initialValues2.put("IsRead", "N");
						//initialValues2.put("transactionId", transactionId);
						initialValues.put("isMsgSend", "true");
					

					}else{//
						initialValues2.put("IsMessageSync", "N");
						initialValues2.put("MSG_TYPE", "READ");
						initialValues2.put("IsRead", "N");
						//initialValues2.put("transactionId", transactionId);
						initialValues.put("isMsgSend", "false");

					}
					

				}else{
					initialValues2.put("IsMessageSync", "N");
					initialValues2.put("MSG_TYPE", "READ");
					initialValues2.put("IsRead", "N");
					//initialValues2.put("transactionId", transactionId);
					initialValues.put("isMsgSend", "false");
				}
				ApplicationUtil.getInstance().updateDataInDB("Messaging",initialValues2, context,"Message_ID = '" + messageDto.getMessage_ID() + "'", null);
				ApplicationUtil.getInstance().updateDataInDB("Task",initialValues, context,"Task_ID = '" + messageDto.getTask_ID() + "'", null);
				//System.out.println("Create Message Status--->" + status);
				
			} catch (Exception e) {
				responseDto.setStatus("05");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		/*
		 * }}; thread.start();
		 */
		AllTaskActivity.isTaskOrMsgCreationInitated=false;
		return responseDto;

	}

	
	/**
	 * this method add and sync new message from device to sevice
	 * 
	 * @return
	 */
	/*public synchronized String UpdateMessages() {

		
		 * Thread thread = new Thread() {
		 * 
		 * 
		 * @Override public void run() {
		 
		ArrayList<ArrayList<String>> listDBElement = ApplicationUtil
				.getInstance()
				.uploadListFromDB(
						"Messaging",
						new String[] { "Task_ID,M_From,M_To,M_Desc, Message_ID" },
						"IsMessageSync ='N'", context);
		for (int i = 0; i < listDBElement.size(); i++) {
			MessageListDTO msg = new MessageListDTO();
			msg.setTaskId(listDBElement.get(i).get(0));
			msg.setmFrom(listDBElement.get(i).get(1));
			msg.setmTo(listDBElement.get(i).get(2));
			msg.setmDesc(listDBElement.get(i).get(3));
			String status = "";
			try {
				if (ApplicationUtil.checkInternetConn(context))
				{
					status = ApplicationUtil.getInstance().getSyncServer()
						.createMessage(msg);
				}
				//System.out.println("Create Message Status--->" + status);
				if (status!=null&& status.trim().equalsIgnoreCase(
						context.getResources().getString(R.string.success))) {
					ApplicationUtil.getInstance().deleteRowInTable(
							"Messaging",
							"WHERE Message_ID = '"
									+ listDBElement.get(i).get(4) + "'",
									context);
					getTaskMessageList();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		 * }}; thread.start();
		 
		return null;

	}*/

	/**
	 * this method add and sync new message from device to sevice
	 * 
	 * @return
	 */
	public String MarkMessageRead(final String assignTo, final String taskId,
			final String msgID) {
		Thread thread = new Thread() {

			@Override
			public void run() {

				String status = "";
				try {
					status = ApplicationUtil.getInstance().getSyncServer(context)
							.MarkMsgRead(assignTo, taskId, msgID);
					//System.out.println("Create Message Status--->" + status);
					if (status.trim().equalsIgnoreCase(
							context.getResources().getString(R.string.success))) {

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}
			}
		};
		thread.start();

		return null;

	}

	/**
	 * method to update assignee for task
	 */
	public ChangeAssigneeResponseDto changeAssignee( ChangeAssigneeDto changeDto) throws Exception{
		String status = "";
		ChangeAssigneeResponseDto ca_response = new ChangeAssigneeResponseDto();
		try {
			status = ApplicationUtil.getInstance().getSyncServer(context).updateAssignee(changeDto.getTaskId(),changeDto.getAssign_To_No(),
							changeDto.getOldAssignee(),changeDto.getMobileNumber());
			//System.out.println("Task ChangeAssignee Status--->" + status);
			if (status!=null&&status.trim().equalsIgnoreCase(context.getResources().getString(R.string.success))) {
				ca_response.setStatus(status);
				ContentValues initialValues = new ContentValues();
				initialValues.put("Assign_To",changeDto.getAssign_To_No());
				initialValues.put("Assign_To_Name", changeDto.getAssign_To_Name());
				initialValues.put("oldAssignee",changeDto.getOldAssignee() );
				initialValues.put("IsAssigneeSync", "Y");
				initialValues.put("IsTaskSync", "Y");
				initialValues.put("TaskType", "sent");
				initialValues.put("IsTaskRead", "N");
				//initialValues.put("UPDATED_DATE", ApplicationUtil.getGMTDate());
				//initialValues.put("UPDATION_DATE_TIME",ApplicationUtil.getGMTLong());
				
				ApplicationUtil.getInstance().updateDataInDB("Task",initialValues, context,"Task_ID ='" + changeDto.getTaskId() + "'",null);
			}
			else
			{
				ca_response.setStatus(status);
				ContentValues initialValues = new ContentValues();
				initialValues.put("Assign_To",changeDto.getAssign_To_No());
				initialValues.put("Assign_To_Name", changeDto.getAssign_To_Name());
				initialValues.put("oldAssignee",changeDto.getOldAssignee() );
				initialValues.put("IsAssigneeSync", "N");
				initialValues.put("IsTaskSync", "N");
				initialValues.put("IsTaskRead", "N");
				initialValues.put("TaskType", "sent");
				initialValues.put("UPDATED_DATE", ApplicationUtil.getGMTDate());
				initialValues.put("UPDATION_DATE_TIME",ApplicationUtil.getGMTLong());
				
				
				ApplicationUtil.getInstance().updateDataInDB("Task",initialValues, context,"Task_ID ='" + changeDto.getTaskId() + "'", null);
			}
			
			
		} catch (Exception e) {
			ca_response.setStatus(status);
			ContentValues initialValues = new ContentValues();
			initialValues.put("Assign_To",changeDto.getAssign_To_No());
			initialValues.put("Assign_To_Name", changeDto.getAssign_To_Name());
			initialValues.put("oldAssignee",changeDto.getOldAssignee() );
			initialValues.put("IsAssigneeSync", "N");
			initialValues.put("IsTaskSync", "N");
			initialValues.put("IsTaskRead", "N");
			initialValues.put("TaskType", "sent");
			initialValues.put("UPDATED_DATE", ApplicationUtil.getGMTDate());
			initialValues.put("UPDATION_DATE_TIME",ApplicationUtil.getGMTLong());
			ApplicationUtil.getInstance().updateDataInDB("Task",initialValues, context,"Task_ID ='" + changeDto.getTaskId() + "'", null);
			e.printStackTrace();
		}
		
//		Thread threadChangeAssignee = new Thread()
//		{
//			public void run() {
//				SyncModule syncChange = new SyncModule(context);
//				syncChange.changeAssignee();
//			};
//		};
//		threadChangeAssignee.start();
		return ca_response;
		
		
		

	}

	/**
	 * change task target date and sync
	 *//*
	public void UpdateTargetDate() {

		Thread thread = new Thread() {

			@Override
			public void run() {
				ArrayList<ArrayList<String>> listDBElement = ApplicationUtil
						.getInstance()
						.uploadListFromDB(
								"Task",
								new String[] { "Task_ID,Assign_From,Assign_To,Task_Desc,Target_Date ,Priority, Closure_Date,Reminder_Time,IsMessage, IsFavouraite" },
								"IsTargetSync ='N'", context);
				// "Task_ID,Assign_From,Task_Desc, Assign_To, Priority, Target_Date, Closure_Date,Reminder_Time,IsMessage, IsFavouraite"
				for (int i = 0; i < listDBElement.size(); i++) {
					try {

						String status = ApplicationUtil
								.getInstance()
								.getSyncServer(context)
								.updateTarget(listDBElement.get(i).get(0),
										listDBElement.get(i).get(1),
										listDBElement.get(i).get(2),
										listDBElement.get(i).get(3),
										listDBElement.get(i).get(4),
										listDBElement.get(i).get(5),
										listDBElement.get(i).get(6),
										listDBElement.get(i).get(7),
										listDBElement.get(i).get(8),
										listDBElement.get(i).get(9));
						//System.out.println("Task Update Status--->" + status);
						if (status.trim().equalsIgnoreCase(
								context.getResources().getString(
										R.string.success))) {
							ContentValues initialValues = new ContentValues();
							initialValues.put("IsTargetSync", "Y");
							initialValues.put("IsTaskSync", "Y");
							
							ApplicationUtil.getInstance().updateDataInDB(
									"Task",
									initialValues,
									context,
									"Task_ID ='" + listDBElement.get(i).get(0)
									+ "'", null);
						}else{
							ContentValues initialValues = new ContentValues();
							initialValues.put("IsTargetSync", "N");
							initialValues.put("IsTaskSync", "N");
							
							ApplicationUtil.getInstance().updateDataInDB(
									"Task",
									initialValues,
									context,
									"Task_ID ='" + listDBElement.get(i).get(0)
									+ "'", null);
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		};
		thread.start();

	}*/

	public void UpdateTargetDate(final String taskId,final String targetDate) {

		Thread thread = new Thread() {

			@Override
			public void run() {
				
			try {

			String status = ApplicationUtil.getInstance().getSyncServer(context).updateTarget(taskId,targetDate,ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context));
			if ("00".equalsIgnoreCase(status)) {
							ContentValues initialValues = new ContentValues();
							initialValues.put("IsTargetSync", "Y");
							initialValues.put("IsTaskSync", "Y");
						ApplicationUtil.getInstance().updateDataInDB("Task",initialValues,context,"Task_ID ='" + taskId+ "'", null);
						}else{
							ContentValues initialValues = new ContentValues();
							initialValues.put("IsTargetSync", "N");
							initialValues.put("IsTaskSync", "N");
							ApplicationUtil.getInstance().updateDataInDB("Task",initialValues,context,"Task_ID ='" + taskId+ "'", null);

						}
						
					} catch (Exception e) {
						ContentValues initialValues = new ContentValues();
						initialValues.put("IsTargetSync", "N");
						initialValues.put("IsTaskSync", "N");
						ApplicationUtil.getInstance().updateDataInDB("Task",initialValues,context,"Task_ID ='" + taskId+ "'", null);
					}
				}

			
		};
		thread.start();

	}

	/**
	 * method to save registration service response
	 * 
	 * @param responseDTO
	 */
	public void saveLoginResponse(registrationDTO responseDTO) {
		// TODO Auto-generated method stub
		ContentValues initialValues = new ContentValues();
		initialValues.put("first_Name", responseDTO.getFirstName());
		initialValues.put("middle_Name", responseDTO.getMiddleName());
		initialValues.put("last_Name", responseDTO.getLastName());
		initialValues.put("XToken", responseDTO.getxToken());
		initialValues.put("Mobile_Number", responseDTO.getMobileNo());
		initialValues.put("Email_Id", responseDTO.getEmail());
		initialValues.put("Operating_System", responseDTO.getOprSys());
		initialValues.put("Registration_Date", ApplicationUtil
				.changeToDayMonthYear2(ApplicationUtil.getCurrentUnixTime()));
		initialValues.put("Status", responseDTO.getStatus());
		initialValues.put("Last_Login_Date", ApplicationUtil
				.changeToDayMonthYear2(ApplicationUtil.getCurrentUnixTime()));
		ApplicationUtil.getInstance().saveDataInDB("User", null, initialValues,
				context);

	}

	/**
	 * method saves OTP validation response
	 * 
	 * @param responseDTO
	 */
	public void saveValidateOTPResponse(OTPValidateDTO responseDTO) {
		// TODO Auto-generated method stub

		ContentValues initialValues = new ContentValues();
		initialValues.put("Mobile_Number", responseDTO.getMobileNo());
		initialValues.put("OTP", responseDTO.getOTP());
		initialValues.put("OTP_Status", responseDTO.getOTP_status());

		ApplicationUtil.getInstance().saveDataInDB("OTP", null, initialValues,
				context);

	}

	/**
	 * method to save task list
	 * 
	 * @param taskListResponse
	 * @throws Exception 
	 */
	public void saveTaskListResponse(ArrayList<taskListDTO> taskListResponse) throws Exception {// TODO
		
		for (int i = 0; i < taskListResponse.size(); i++) {

			ContentValues initialValues = new ContentValues();
			taskListDTO response = new taskListDTO();
				response = taskListResponse.get(i);
			initialValues.put("Task_ID", response.getTaskId());
			
			initialValues.put("Group_ID", response.getGroupId());
			initialValues.put("Assign_From", response.getAssignFrom());
			initialValues.put("Assign_From_Name", response.getAssignFromName());
			initialValues.put("Assign_To", response.getAssignTo());
			initialValues.put("Task_Desc", response.getTaskDesc());
			initialValues.put("Closure_Date", response.getClosureDateTime());
			initialValues.put("Priority", response.getPriority());
			initialValues.put("Priority_Desc", response.getPriorityDesc());
			initialValues.put("Target_Date", response.getTargetDateTime());
			initialValues.put("Fire_Flag", response.getFireFlag());
			initialValues.put("IsFavouraite", response.getFavouraite());
			initialValues.put("IsReminder", response.getIsReminder());
			initialValues.put("Reminder_Time", response.getReminderTime());
			initialValues.put("IsMessage", response.getIsMessage());
			initialValues.put("Status", response.getStatus());
			initialValues.put("TaskType", response.getTaskType());
			initialValues.put("CreationDate", response.getCreationDate());
			initialValues.put("TotalMessage", response.getTotalMessage());
			initialValues.put("UnreadMessage", response.getUnreadMessage());
			initialValues.put("ClosedBy", response.getClosedBy());
			initialValues.put("IsTaskSync", "Y");
			initialValues.put("IsTaskRead", "N");
			initialValues.put("CreationDateTime",ApplicationUtil.getGMTLong(response.getCreationDate()));
			
			initialValues.put("ClosureDate", ApplicationUtil.getGMTLong(response.getClosureDateTime()));
			
			if(CommonUtil.getValidMsisdn(ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context)).equalsIgnoreCase(CommonUtil.getValidMsisdn(response.getClosedBy())))
			{
				initialValues.put("isActive", "N");
			}else{
				initialValues.put("isActive", "Y");
			}
			String id = ApplicationUtil.isContactRecorded(response.getAssignFrom(), context);

			//System.out.println("name...." + id);
			if ((id != null && id.trim().length() > 0)
					|| ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context).trim().contains(
							response.getAssignFrom()))
				initialValues.put("IsJunk", "N");
			else
				initialValues.put("IsJunk", "Y");

			ArrayList<ArrayList<String>> TaskList = ApplicationUtil
					.getInstance().uploadListFromDB("Task", null,
							"Task_ID='" + response.getTaskId() + "'", context);
			if (TaskList != null && !TaskList.isEmpty()) {
				

				//System.out.println("Reload Task---" + TaskList);
				if (response.getTaskType().trim().equalsIgnoreCase("inbox")
						&& response
						.getStatus()
						.trim()
						.equalsIgnoreCase(
								context.getResources().getString(
										R.string.close_status))
										&& !response.getStatus().trim()
										.equals(TaskList.get(0).get(14))) {
					ApplicationUtil.createNotification(
							context,
							"Close Task",
							Integer.parseInt(response.getTaskId()),
							context.getResources().getString(
									R.string.close_task_ticker),
									response.getTaskDesc() + "From [ "
											+ response.getAssignFromName() + " ]",
											context.getResources().getString(
													R.string.close_task_notify_msg));
					//ApplicationConstant.taskAdded = true; 
					//System.out.println("Reload task closed");
				}

				if (response.getTaskType().trim().equalsIgnoreCase("inbox")
						&& response
						.getPriority()
						.trim()
						.equalsIgnoreCase(
								context.getResources().getString(
										R.string.fire_priority_val))
										&& !response.getPriority().trim()
										.equals(TaskList.get(0).get(6))) {
					ApplicationUtil.createNotification(
							context,
							"Fire Task ",
							Integer.parseInt(response.getTaskId()),
							context.getResources().getString(
									R.string.fire_task_ticker),
									response.getTaskDesc() + "[ "
											+ response.getAssignFromName().toUpperCase() + " ]",
											context.getResources().getString(
													R.string.fire_task_notify_msg));
					//ApplicationConstant.taskAdded = true; 
					{
						//ApplicationConstant.needUIRefresh = true;
						//System.out.println("Reload task fire");
					}

				}

				
				ApplicationUtil.getInstance().updateDataInDB("Task",
						initialValues, context,
						"Task_ID ='" + response.getTaskId() + "'", null);
			//value used to update data in deadline database 	
				
				//value used to update data in database 	

				if ((!response.getPriority().equalsIgnoreCase(TaskList.get(0).get(6))) || (!response.getFavouraite().equalsIgnoreCase(TaskList.get(0).get(15))) || (!response.getTaskDesc().equalsIgnoreCase(TaskList.get(0).get(5)))) 
				{
					//ApplicationConstant.needUIRefresh = true;
					//ApplicationConstant.taskAdded = true; 
					//System.out.println("Reload task changes");
				}
				

				/*//
				||
				  TaskList.get(0).get(9).trim().length() > 0) ||
				  (TaskList.get(0).get(9) != null &&
				  TaskList.get(0).get(9).trim().length() > 0
				
				
				//
*/				
				
				  if (response.getTargetDateTime() != null &&
				  !response.getTargetDateTime().trim().equals("") &&
				  (TaskList.get(0).get(9) != null  && !response
				 .getTargetDateTime().trim() .equals(TaskList.get(0).get(9))))
				  {
					  if(response.getTargetDateTime()!=null&&!response.getTargetDateTime().isEmpty()&&!response.getTargetDateTime().equalsIgnoreCase(""))
						{
							initialValues.put("IsDeadlineDisplay", "N");
						
							ApplicationUtil.getInstance().updateDataInDB("Deadline",
									initialValues, context,
									"Task_ID ='" + response.getTaskId() + "'", null);
						} 
				  }
				 
				
				//System.out.println("Reload prev Vals--->"+TaskList.get(0).get(9)+"--");
				//System.out.println("Reload new Vals--->"+response.getTargetDateTime()+"--");
				

			} else {
				//System.out.println("Task Type---->" + response.getTaskType());
				if (response.getTaskType().trim().equalsIgnoreCase("inbox")
						&& response
						.getStatus()
						.trim()
						.equalsIgnoreCase(
								context.getResources().getString(
										R.string.open_status))) {
					initialValues.put("IsTaskRead", "N");
					
					ApplicationUtil.createNotification(
							context,
							"New Task ",
							Integer.parseInt(response.getTaskId()),
							context.getResources().getString(
									R.string.new_task_ticker),
									response.getTaskDesc() + " [ "
											+ response.getAssignFromName().toUpperCase() + " ]",
											context.getResources().getString(
													R.string.new_task_notify_msg));
					
				}
				
				ApplicationUtil.getInstance().saveDataInDB("Task", null,
						initialValues, context);
				if(response.getTargetDateTime()!=null&&!response.getTargetDateTime().isEmpty()&&!response.getTargetDateTime().equalsIgnoreCase(""))
				{
					initialValues.put("IsDeadlineDisplay", "N");
					ApplicationUtil.getInstance().saveDataInDB("Deadline", null,
					initialValues, context);
				}
				
			}

			if(ApplicationUtil.checkInternetConn(context))
			{
				
				reloadListOnScreen();
			}
			
		}

		
		
	}

	

	/**
	 * method to set local alarm for particular task
	 * 
	 * @param alarmList
	 */
	private void Setalarm(ArrayList<ArrayList<String>> alarmList) {
		// TODO Auto-generated method stub
		//System.out.println("Setalarm called----" + alarmList.size());

		for (int i = 0; i < alarmList.size(); i++) {
			//System.out.println("Setalarm i Val-->" + i);
			String alarmTaskId = alarmList.get(i).get(0);
			String alarmTime = alarmList.get(i).get(1);
			String alarmTaskDesc = alarmList.get(i).get(2);

			long alarmTimeStamp = 0;
			try {
				alarmTimeStamp = ApplicationUtil
						.getUpdatedTimestampFromDate3(alarmTime);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long currentTime = ApplicationUtil.getCurrentUnixTime();
			int difference = (int) (alarmTimeStamp - currentTime);
			
			if (difference <= 15 * 60 && difference > 0) {
				addAlarm(alarmTimeStamp, alarmTaskId, alarmTaskDesc);
			} else {
				//System.out.println("Setalarm Alarm not added");
			}

		}

	}

	/**
	 * method to call alarm service to set alarm for task
	 * 
	 * @param alarmTimeStamp
	 * @param alarmTaskId
	 * @param alarmTaskDesc
	 */

	private void addAlarm(long alarmTimeStamp, String alarmTaskId,
			String alarmTaskDesc) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(alarmTimeStamp * 1000);
		//System.out.println("Setalarm alarm Time--" + cal.getTimeInMillis());
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.putExtra("TaskId", alarmTaskId);
		intent.putExtra("TaskDesc", alarmTaskDesc);
		//System.out.println("Setalarm alarm added");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
				Integer.parseInt(alarmTaskId), intent, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
				pendingIntent);

	}

	/**
	 * method to save task messages
	 * 
	 * @param taskMessageListResponse
	 */
	private void saveTaskMessageListResponse(
			ArrayList<MessageListDTO> taskMessageListResponse, String taskStatus) {
		// TODO Auto-generated method stub

		for (int i = 0; i < taskMessageListResponse.size(); i++) {
			ContentValues initialValues = new ContentValues();
			MessageListDTO response = new MessageListDTO();
			response = taskMessageListResponse.get(i);
			initialValues.put("Message_ID", response.getMsgId());
			initialValues.put("Task_ID", response.getTaskId());
			initialValues.put("M_From", response.getmFrom());
			initialValues.put("M_From_Name", response.getmFromName());
			initialValues.put("M_To", response.getmTo());
			initialValues.put("M_To_Name", response.getmToName());
			initialValues.put("M_Desc", response.getmDesc());
			initialValues.put("Creation_Date", response.getCreationDate());
			initialValues.put("CreationDateTime", ApplicationUtil.getGMTLong(response.getCreationDate()));
			
			
			initialValues.put("IsMessageSync", "Y");

			ArrayList<ArrayList<String>> MessageList = ApplicationUtil
					.getInstance()
					.uploadListFromDB("Messaging", null,
							"Message_ID='" + response.getMsgId() + "'", context);
			if (MessageList != null && !MessageList.isEmpty()) {
				ApplicationUtil.getInstance().updateDataInDB("Messaging",
						initialValues, context,
						"Message_ID ='" + response.getMsgId() + "'", null);
			} else {
				if (!ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context).trim().contains(
						response.getmFrom())
						&& taskStatus.trim().equalsIgnoreCase(
								context.getResources().getString(
										R.string.open_status))) {
					initialValues.put("IsRead", "N");
					ApplicationUtil.createNotification(
							context,
							"New Message",
							Integer.parseInt(response.getMsgId()),
							context.getResources().getString(
									R.string.new_msg_ticker), (response
									.getmDesc().length()>25?response.getmDesc().substring(0,23)+"...":response.getmDesc())
									+" [ "
											+ response.getmFromName().toUpperCase() + " ]", context.getResources().getString(
													R.string.new_message_notify_msg));
					//ApplicationConstant.msgAdded = true;
				}

				else
					initialValues.put("IsRead", "Y");
				// initialValues.put("IsRead", "N");
				ApplicationUtil.getInstance().saveDataInDB("Messaging", null,
						initialValues, context);

				/*if (ApplicationConstant.msgAdded) {
					ApplicationConstant.flag_Ui_Refresh=true;
					reloadListOnScreen();
					ApplicationConstant.msgAdded = false;
				} */
				//else {
					////System.out.println("Reload list called--From save msg--"
					//		+ ApplicationConstant.needUIRefresh);
					//ApplicationConstant.needUIRefresh = true;
				//}*/
			}

		}

	}

	/**
	 * method gets list of reminders to fire
	 * 
	 * 226
	 * 
	 * @param reminderList
	 */
	private void Setreminder(ArrayList<ArrayList<String>> reminderList) {
		// TODO Auto-generated method stub
		//System.out.println("Setreminder called");
		if(reminderList!=null)
		{

		for (int i = 0; i < reminderList.size(); i++) {
			String reminderTask = reminderList.get(i).get(0);
			String reminderVal = reminderList.get(i).get(1);
			// String reminderVal =
			// "10/10/2013 16:18:00";//reminderList.get(i).get(1);
			// / String reminderVal =
			if (reminderVal != null && reminderVal.trim().length() > 0) {
				long reminderTimeStamp = 0;
				try {
					reminderTimeStamp = ApplicationUtil
							.getUpdatedTimestampFromDate(reminderVal);

					/*
					 * String time_str = reminderVal;
					 * 
					 * String[] s = time_str.split(" ");
					 * 
					 * for (int j = 0; j < s.length; j++) {
					 * //System.out.println("date  => " + s[j]); }
					 * 
					 * int year_sys = Integer.parseInt(s[0].split("/")[2]); int
					 * month_sys = Integer.parseInt(s[0].split("/")[1]); int
					 * day_sys = Integer.parseInt(s[0].split("/")[0]);
					 * 
					 * int hour_sys = Integer.parseInt(s[1].split(":")[0]); int
					 * min_sys = Integer.parseInt(s[1].split(":")[1]);
					 * 
					 * //System.out.println("year_sys  => " + year_sys);
					 * //System.out.println("month_sys  => " + month_sys);
					 * //System.out.println("day_sys  => " + day_sys);
					 * 
					 * //System.out.println("hour_sys  => " + hour_sys);
					 * //System.out.println("min_sys  => " + min_sys);
					 * GregorianCalendar calendar = new
					 * GregorianCalendar(year_sys,month_sys, day_sys,hour_sys,
					 * min_sys);
					 *//**
					 * Converting the date and time in to milliseconds elapsed
					 * since epoch
					 */
					/*
					 * //long alarm_time = calendar.getTimeInMillis();
					 * reminderTimeStamp=calendar.getTimeInMillis();
					 */

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long currentTime = ApplicationUtil.getCurrentUnixTime();
				
				long difference = (reminderTimeStamp - currentTime);

				if (difference > 0) {
					//System.out.println("difference" + difference);
					long remindme = reminderTimeStamp * 1000;
					setAlarmManager(context, reminderVal, reminderTask);
					// addReminder(remindme,reminderTask);
					// setAlarmManager(context, reminderVal,reminderTask);

				}
				/*
				 * if(difference <= 15*60 && difference>0) {
				 * //System.out.println("Setreminder Add reminder for ---"
				 * +reminderTask+"---"+difference);
				 * 
				 * } else {
				 * //System.out.println("Setreminder No reminder for ---"+
				 * reminderTask); }
				 */
			} 
				

		}
		}

	}

	/**
	 * method to call alarm service to set reminder for task
	 * 
	 * @param difference
	 * @param Id
	 */

	private void addReminder(long difference, String Id) {
		// TODO Auto-generated method stub
		//System.out.println("Setreminder--->" + difference);
		Intent intent = new Intent(context, ReminderReceiver.class);
		intent.putExtra("TaskId", Id);
		PendingIntent sender = PendingIntent.getBroadcast(context,
				Integer.parseInt(Id), intent, 0);
		// PendingIntent operation = PendingIntent.getActivity(context,
		// Integer.parseInt(Id), intent, Intent.FLAG_ACTIVITY_NEW_TASK);

		// Calendar calendar = Calendar.getInstance();
		// calendar.setTimeInMillis(System.currentTimeMillis());
		// calendar.add(Calendar.SECOND, difference);

		// Intent i = new
		// Intent("in.wptrafficanalyzer.servicealarmdemo.demoactivity");

		/** Creating a Pending Intent */
		// PendingIntent operation = PendingIntent.getActivity(getBaseContext(),
		// 0, i, Intent.FLAG_ACTIVITY_NEW_TASK);

		/** Getting a reference to the System Service ALARM_SERVICE */
		// AlarmManager alarmManager = (AlarmManager)
		// getBaseContext().getSystemService(ALARM_SERVICE);

		// Schedule the alarm!
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, difference, sender);

		//System.out.println("Setreminder Alarm Added");

	}

	
	/**
	 * method to broadcast screen to reload task list UI
	 */
	private void reloadListOnScreen() {

		////System.out.println("Reload list called---"
		//		+ ApplicationConstant.needUIRefresh);
		Intent intent = new Intent(
				"com.taskmaganger.ui.AlltaskActivity.relaodList.broadcast");
		context.sendBroadcast(intent);
		

	}

	public void setAlarmManager(Context context, String date, String id) {

		SimpleDateFormat smDtfm = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		// //System.out.println("date is---->"+smDtfm.parse(date).getTime());
		long alarm_time = 0;
		try {
			alarm_time = smDtfm.parse(date).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/**
		 * Creating a calendar object corresponding to the date and time set by
		 * the user
		 */
		// GregorianCalendar calendar = new GregorianCalendar(year,month,day,
		// hour, minute);

		/** Converting the date and time in to milliseconds elapsed since epoch */
		// long alarm_time = calendar.getTimeInMillis();

		/** Setting an alarm, which invokes the operation at alart_time */

		Intent intent = new Intent(context.getApplicationContext(),
				com.taskmanager.background.ReminderReceiver.class);
		// intent.setAction("com.braoadcast.alarm");
		intent.putExtra("TaskId", id);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				context.getApplicationContext(), Integer.parseInt(id), intent,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, alarm_time, pendingIntent);

	
	}

	public void registerDeviceOnServer(Context context, String mobileNo,
			String regid) {

		//for (int i = 1; i <= 5; i++) {
			

			//	try {
			//		if (i == 4) {
						//break;
				//	}
					try {
						ApplicationUtil.getInstance().getSyncServer(context)
								.registerDevice(mobileNo, regid, "Android");
						GCMRegistrar.setRegisteredOnServer(context, true);
						String message = context.getString(R.string.server_registered);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//Thread.sleep(1000);
					//return;

				//} catch (Exception e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
				//	break;
				//}

				

		}

		//}
	
	
	public void createNotaiont( String notationName,String taskId ){
		
		
		try {
			DBAdapter adapter=DBAdapter.getInstance(context);
			adapter.openDataBase();
			if(notationName.indexOf("#")!=-1){
				
				String notaion[]=notationName.split(" ");
				for (String notaionName : notaion) {
					if(notaionName.equalsIgnoreCase("#")){
						continue;
					}
					if(notaionName.indexOf("#")!=-1){
					Integer noTaionId= new SecureRandom().nextInt(1000);
					ContentValues notationValues = new ContentValues();
					notationValues.put("NAME", notaionName);
					notationValues.put("ID", noTaionId);
					ContentValues notationTAskMapaingValues = new ContentValues();
					notationTAskMapaingValues.put("NAME", notaionName);
					notationTAskMapaingValues.put("ID", new SecureRandom().nextInt(1000));
					notationTAskMapaingValues.put("Task_ID", taskId);
					
					if(!adapter.isNotaionExist(notaionName)){
						adapter.createNotaion(notationValues);
		
					}
					if(!adapter.isTaskNotaionExist(notaionName, taskId)){
						adapter.createNotaionTaskMapping(notationTAskMapaingValues);

					}
				
				}
				}
				adapter.close();
				//notationName=CommonUtil.getFiestWord(notationName);
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void createselfTask(final taskListDTO task){
			final int randomNo = (int) (Math.random() * 1000000);
			String transactionId=CommonUtil.getTransactionId(context);
			task.setTransactioId(transactionId);
			ContentValues initialValues = new ContentValues();
			initialValues.put("Task_ID", randomNo+"");
			initialValues.put("IsTaskSync", "N");
			initialValues.put("TASK_SYNC_TYPE", "NEW");
			initialValues.put("isTaskNew", "Y");
			initialValues.put("isMsgSend", "true");
			initialValues.put("IsJunk", "N");
			initialValues.put("transactionId", transactionId);
			initialValues.put("Group_ID", "");
			initialValues.put("Assign_From", CommonUtil.getRegNum(context));
			initialValues.put("Assign_From_Name", ApplicationUtil.getContactNameFromPhoneNo(context,CommonUtil.getRegNum(context)));
			initialValues.put("Assign_To", CommonUtil.getRegNum(context));
			initialValues.put("Assign_To_Name", ApplicationUtil.getContactNameFromPhoneNo(context,CommonUtil.getRegNum(context)));
			initialValues.put("CreationDate", task.getCreationDate());
			initialValues.put("Task_Desc", task.getTaskDesc());
			initialValues.put("Target_Date", task.getTargetDateTime());
			try {
				SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
				initialValues.put("Target_Date_TIME",dateFormat.parse(task.getTargetDateTime()).getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			initialValues.put("Priority", task.getPriority());
			initialValues.put("Closure_Date", task.getClosureDateTime());
			initialValues.put("Reminder_Time", task.getReminderTime());
			if(task.getReminderTime()!=null&&!task.getReminderTime().trim().isEmpty()){
				initialValues.put("isAlarmSet", "Y");
				initialValues.put("Alarm_Date_Time", task.getReminderTime());
				Thread thread = new Thread()
				{
					public void run() 
					{
						if(task.getReminderTime()!=null)
							new CommonUtilsUi().setAlarmManager(context,task.getReminderTime(),randomNo+"",task.getTaskDesc());
					};
				};
				thread.start();
			}else{
				initialValues.put("isAlarmSet", "N");
			}
			initialValues.put("IsFavouraite", task.getFavouraite());
			initialValues.put("IsTaskRead", "N");
			initialValues.put("Status", task.getStatus());
			initialValues.put("CreationDate", ApplicationUtil.getGMTDate()+"");
			initialValues.put("CreationDateTime", ApplicationUtil.getGMTLong());
			initialValues.put("UPDATED_DATE",  ApplicationUtil.getGMTDate()+"");
			initialValues.put("UPDATION_DATE_TIME",ApplicationUtil.getGMTLong());
			initialValues.put("ClosureDate", -1);
			initialValues.put("TaskType", "self");
			initialValues.put("DISPLAY_NAME", "Self");
			ApplicationUtil.getInstance().saveDataInDB("Task", null, initialValues,context);
			createNotaiont(task.getTaskDesc(), randomNo+"");
		
		
	}

}
