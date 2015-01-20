package com.taskmanager.webservice.json;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.taskmanager.background.SyncModule;
import com.taskmanager.bean.CheckRegistrationDTO;
import com.taskmanager.bean.CreateTaskDTO;
import com.taskmanager.bean.MessageListDTO;
import com.taskmanager.bean.OTPValidateDTO;
import com.taskmanager.bean.TaskInfoDto;
import com.taskmanager.bean.registrationDTO;
import com.taskmanager.bean.taskListDTO;
import com.taskmanager.entites.TaskEntity;
import com.taskmanager.util.ApplicationConstant;
import com.taskmanager.util.ApplicationUtil;

public class JSONFactory {
	static JSONFactory instance;
	private static Context context;
	String className;
	/**
	 * Class constructor
	 */
	public  JSONFactory(){

		// Logger object used to log messages for exception.

	}

	public static JSONFactory getInstance(Context context1)
	{
		if(instance==null)
		{
			instance= new JSONFactory();
		}
		context  = context1;
		return instance;

	}


	/**
	 * method to parse registration JSON response
	 * @param response
	 * @return
	 */
	public  registrationDTO parseRegistrationResponse(String response) {
		// TODO Auto-generated method stub
		registrationDTO responseDTO = new registrationDTO();
		try {
			JSONObject responseList = new JSONObject(response);
			JSONObject responseObj = responseList.getJSONObject("response");
			responseDTO.setxToken(responseObj.getString("token"));
			responseDTO.setStatus(responseObj.getString("status"));
			responseDTO.setDisplayMessage(responseObj.getString("messageDescription"));
			responseDTO.setOprSys("Android");


		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return responseDTO;
	}


	/**
	 * method to parse task List JSON response
	 * @param response
	 * @return
	 */
	public  ArrayList<taskListDTO> parseTaskListResponse(String response) {
		// TODO Auto-generated method stub
		ArrayList<taskListDTO> taskListResponses = new ArrayList<taskListDTO>();
		try {

			JSONObject responseList = new JSONObject(response);
			JSONObject responseObj = responseList.getJSONObject("response");
			String status = responseObj.getString("status");
			ApplicationConstant.SYNC_SERVICE_STATUS = status;
			JSONObject taskList = responseObj.getJSONObject("taskList");
			
			////System.out.println("lastUpdatedTime taskList--->"+taskList);
			////System.out.println("task list status--->"+status);  
			if(status.trim().equalsIgnoreCase("00"))
			{
				String lastUpdatedTime = "";
				JSONArray taskArray = new JSONArray();
				Iterator iter1 = taskList.keys();
				while(iter1.hasNext())
				{
					String key = (String)iter1.next();
					if(key.equals("lastUpdateTime"))
					{
						lastUpdatedTime = taskList.getString("lastUpdateTime");
						////System.out.println("lastUpdatedTime--->"+lastUpdatedTime);
						TaskEntity taskEntityObj = new TaskEntity();
						taskEntityObj.updateUserTableData("Task_Last_Updated", lastUpdatedTime,context);
						taskEntityObj = null;
					}
					else if(key.equals("task"))
					{
						taskArray = taskList.getJSONArray("task");
					}
					else if(key.equals("changedAssignies"))
					{
						JSONArray changeAssigneeArray = taskList.getJSONArray("changedAssignies");
						for(int k=0;k<changeAssigneeArray.length();k++)
						{
							JSONObject ChangeAssigntaskObj=changeAssigneeArray.getJSONObject(k);
							//ChangeAssigntaskObj.getString("taskId");
							if(!ApplicationUtil.getValidNumber(ChangeAssigntaskObj.getString("newAssignee")).equalsIgnoreCase(ApplicationUtil.getValidNumber(ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context))))
							{
								TaskEntity changeTaskEntity = new TaskEntity();
								changeTaskEntity.deleteChangeAssignee(ChangeAssigntaskObj.getString("taskId"),context);
								
							}
						}
					}
				}

				for(int i =0; i<taskArray.length(); i++ )
				{
					taskListDTO task = new taskListDTO();
					JSONObject taskObj =  taskArray.getJSONObject(i);
					String taskFrom = "";
					String taskTo = "";
					String priority = "";
					Iterator iter = taskObj.keys(); 
					while(iter.hasNext()){
						String key = (String)iter.next();
						////System.out.println("key is----> "+key);
						//ApplicationConstant.taskAdded = true;
						if(key.equals("taskId"))
						{
							////System.out.println("task Id parse--->"+taskObj.getString("taskId"));
							task.setTaskId(taskObj.getString("taskId"));
						}
						else if(key.equals("groupId"))
						{
							task.setGroupId(taskObj.getString("groupId"));
						}
						else if(key.equals("assignFrom"))
						{
							task.setAssignFrom(taskObj.getString("assignFrom"));
							taskFrom = taskObj.getString("assignFrom");
						}
						else if(key.equals("assignFromName"))
						{
							task.setAssignFromName(taskObj.getString("assignFromName"));
						}
						else if(key.equals("assignTo"))
						{
							task.setAssignTo(taskObj.getString("assignTo"));
							taskTo = taskObj.getString("assignTo");
						}

						else if(key.equals("assignToName"))
						{
							task.setAssignToName(taskObj.getString("assignToName"));
						}
						else if(key.equals("closerDate"))
						{
							task.setClosureDateTime(taskObj.getString("closerDate"));
						}
						else if(key.equals("targetDate"))
						{
							task.setTargetDateTime(taskObj.getString("targetDate"));
						}
						else if(key.equals("reminderTime"))
						{
							task.setReminderTime(taskObj.getString("reminderTime"));
						}
						else if(key.equals("taskDescription"))
						{
							task.setTaskDesc(taskObj.getString("taskDescription"));
						}
						else if(key.equals("priority"))
						{
							priority = taskObj.getString("priority");
							task.setPriority(taskObj.getString("priority"));
						}
						else if(key.equals("priorityDescription"))
						{
							task.setPriorityDesc(taskObj.getString("priorityDescription"));
						}
						else if(key.equals("favouraite"))
						{
							task.setFavouraite(taskObj.getString("favouraite"));
						}
						else if(key.equals("message"))
						{
							task.setIsMessage(taskObj.getString("message"));
						}
						else if(key.equals("status"))
						{
							task.setStatus(taskObj.getString("status"));
						}
						else if(key.equals("creationDate"))
						{
							task.setCreationDate(taskObj.getString("creationDate"));
						}
						else if(key.equals("totalMessage"))
						{
							task.setTotalMessage(taskObj.getString("totalMessage"));
						}
						else if(key.equals("unreadMessage"))
						{
							task.setUnreadMessage(taskObj.getString("unreadMessage"));
						}
						else if(key.equals("closedBy"))
						{
							task.setClosedBy(taskObj.getString("closedBy"));
						}
						else if(key.equals("url"))
						{
							task.setTaskUrl(taskObj.getString("url"));
						}
					}
					if(priority.trim().equalsIgnoreCase("4"))
						task.setFireFlag("true");
					else
						task.setFireFlag("false");

					if(ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context).trim().contains(taskFrom) && ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context).trim().contains(taskTo))
					{
						task.setTaskType("self");
					}
					else
					{
						if(ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context).trim().contains(taskTo))
						{
							task.setTaskType("inbox");
						}
						else
						{
							if(ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context).trim().contains(taskFrom))
							{
								task.setTaskType("sent");
							}
						}
					}




					taskListResponses.add(task);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return taskListResponses;
	}


	/**
	 * method to parse task messages list JSON response
	 * @param response
	 * @return
	 */
	public ArrayList<MessageListDTO> parseTaskMessageListResponse(String response) {

		ArrayList<MessageListDTO> taskMessageListResponses = new ArrayList<MessageListDTO>();
		try {
			JSONObject responseList = new JSONObject(response);
			JSONObject responseObj = responseList.getJSONObject("response");
			String status = responseObj.getString("status");
			////System.out.println("Message list status--->"+status);
			if(status.trim().equalsIgnoreCase("00"))
			{
				JSONObject messageList = responseObj.getJSONObject("messageList");
				if(messageList.has("message"))
				{
					JSONArray messages = messageList.getJSONArray("message");

				for(int i =0; i<messages.length(); i++ )
				{
					MessageListDTO message = new MessageListDTO();
					JSONObject messageObj =  messages.getJSONObject(i);
					Iterator iters = messageObj.keys();
					//ApplicationConstant.msgAdded = true;
					while(iters.hasNext())
					{
						String key = iters.next().toString();
						if(key.equals("taskId"))
						{
							message.setTaskId(messageObj.getString("taskId"));
						}
						else if(key.equals("messageId"))
						{
							message.setMsgId(messageObj.getString("messageId"));
						}
						else if(key.equals("assignFrom"))
						{
							message.setmFrom(messageObj.getString("assignFrom"));
						}
						else if(key.equals("assignFromName"))
						{
							message.setmFromName(messageObj.getString("assignFromName"));
						}
						else if(key.equals("assignTo"))
						{
							message.setmTo(messageObj.getString("assignTo"));
						}
						else if(key.equals("assignToName"))
						{
							message.setmToName(messageObj.getString("assignToName"));
						}
						else if(key.equals("messageDescription"))
						{
							message.setmDesc(messageObj.getString("messageDescription"));

						}
						else if(key.equals("createdTime"))
						{
							message.setCreationDate(messageObj.getString("createdTime"));
						}
					}
					taskMessageListResponses.add(message);
				}
			}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		// TODO Auto-generated method stub

		return taskMessageListResponses;
	}

	/**
	 * method to parse validate OTP JSON response
	 * @param response
	 * @return
	 */
	public OTPValidateDTO parseValidateOTPResponse(String response) {
		// TODO Auto-generated method stub

		OTPValidateDTO responseDTO = new OTPValidateDTO();
		try {

			JSONObject responseList = new JSONObject(response);
			JSONObject responseObj = responseList.getJSONObject("response");
			responseDTO.setOTP_status(responseObj.getString("status"));
			////System.out.println("respons status--->"+responseObj.getString("status"));
			if(responseDTO.getOTP_status().trim().equals("00"))
			{
				responseDTO.setOTP(ApplicationConstant.OTP);
				responseDTO.setMobileNo(ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context));
				responseDTO.setDisplayMessage(responseObj.getString("messageDescription"));
				JSONObject taskListObj =responseObj.getJSONObject("taskList");

				String lastUpdatedTime = "";
				
				JSONArray tasks = new JSONArray();
				Iterator iter1 = taskListObj.keys();
				while(iter1.hasNext())
				{
					String key = (String)iter1.next();
					if(key.equals("lastUpdateTime"))
					{
						lastUpdatedTime = taskListObj.getString("lastUpdateTime");
						////System.out.println("lastUpdatedTime--->"+lastUpdatedTime);
						TaskEntity taskEntityObj = new TaskEntity();
						taskEntityObj.updateUserTableData("Task_Last_Updated", lastUpdatedTime,context);
						taskEntityObj = null;
					}
					else if(key.equals("task"))
					{
						tasks = taskListObj.getJSONArray("task");
					}
				}



				////System.out.println("tasks--->"+tasks);
				ArrayList<taskListDTO> taskList = new ArrayList<taskListDTO>();
				for(int i =0; i<tasks.length(); i++)
				{
					JSONObject taskObj = tasks.getJSONObject(i);
					taskListDTO task = new taskListDTO();
					String taskFrom = "";
					String taskTo = "";
					String priority = "";
					Iterator iter = taskObj.keys(); 
					while(iter.hasNext()){
						String key = (String)iter.next();
						////System.out.println("key is----> "+key);
						if(key.equals("taskId"))
						{
							task.setTaskId(taskObj.getString("taskId"));
						}
						else if(key.equals("groupId"))
						{
							task.setGroupId(taskObj.getString("groupId"));
						}
						else if(key.equals("assignFrom"))
						{
							task.setAssignFrom(taskObj.getString("assignFrom")); 
							taskFrom = taskObj.getString("assignFrom");
						}
						else if(key.equals("assignFromName"))
						{
							task.setAssignFromName(taskObj.getString("assignFromName"));
						}
						else if(key.equals("assignTo"))
						{
							task.setAssignTo(taskObj.getString("assignTo"));
							taskTo = taskObj.getString("assignTo");
						}
						else if(key.equals("assignToName"))
						{
							task.setAssignToName(taskObj.getString("assignToName"));
						}
						else if(key.equals("taskDescription"))
						{
							task.setTaskDesc(taskObj.getString("taskDescription"));
						}
						else if(key.equals("priority"))
						{
							priority = taskObj.getString("priority");
							task.setPriority(taskObj.getString("priority"));
						}
						else if(key.equals("priorityDescription"))
						{
							task.setPriorityDesc(taskObj.getString("priorityDescription"));

						}
						else if(key.equals("closerDate"))
						{
							task.setClosureDateTime(taskObj.getString("closerDate"));

						}
						else if(key.equals("reminderTime"))
						{
							task.setReminderTime(taskObj.getString("reminderTime"));

						}
						else if(key.equals("targetDate"))
						{
							task.setTargetDateTime(taskObj.getString("targetDate"));

						}
						else if(key.equals("message"))
						{
							task.setIsMessage(taskObj.getString("message"));

						}
						else if(key.equals("status"))
						{
							task.setStatus(taskObj.getString("status"));

						}
						else if(key.equals("favouraite"))
						{
							task.setFavouraite(taskObj.getString("favouraite"));

						}
						else if(key.equals("creationDate"))
						{
							task.setCreationDate(taskObj.getString("creationDate"));
						}
						else if(key.equals("totalMessage"))
						{
							task.setTotalMessage(taskObj.getString("totalMessage"));
						}
						else if(key.equals("unreadMessage"))
						{
							task.setUnreadMessage(taskObj.getString("unreadMessage"));
						}

					}
					if(priority.trim().equalsIgnoreCase("4"))
						task.setFireFlag("true");
					else
						task.setFireFlag("false");

					if(ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context).trim().contains(taskFrom) && ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context).trim().contains(taskTo))
					{
						task.setTaskType("self");
					}
					else
					{
						if(ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context).trim().contains(taskTo))
						{
							task.setTaskType("inbox");
						}
						else
						{
							if(ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context).trim().contains(taskFrom))
							{
								task.setTaskType("sent");
							}
						}
					}


					taskList.add(task);
				}

				SyncModule syncObj = new SyncModule(context);
				try {
					syncObj.saveTaskListResponse(taskList);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				syncObj = null;
				//				saveTaskList(taskList);
			}


		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return responseDTO;
	}


	/**
	 * method to parse resends OTP JSON response
	 * @param response
	 * @return
	 */
	public String parseResendOTPResponse(String response) {
		// TODO Auto-generated method stub
		String responseMsg = "";
		JSONObject responseObj;
		try {
			responseObj = new JSONObject(response);
			responseMsg =responseObj.getString("errorMessage");
		} catch (JSONException e) { 
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return responseMsg;
	}


//	/**
//	 * method to save task list received from validate OTP response
//	 * @param taskList
//	 */
//	private void saveTaskList(ArrayList<taskListDTO> taskList) {
//		// TODO Auto-generated method stub
//
//		for(int i =0; i<taskList.size(); i++)
//		{
//			taskListDTO task = taskList.get(i);
//
//
//			ContentValues initialValues = new ContentValues();
//
//			initialValues.put("Task_ID", task.getTaskId());
//			initialValues.put("Group_ID", task.getGroupId());
//			initialValues.put("Assign_From", task.getAssignFrom());
//			initialValues.put("Assign_From_Name", task.getAssignFromName());
//			initialValues.put("Assign_To", task.getAssignTo());
//			initialValues.put("Assign_To_Name", task.getAssignToName());
//			initialValues.put("Task_Desc", task.getTaskDesc());
//			initialValues.put("Priority", task.getPriority());
//			initialValues.put("Priority_Desc", task.getPriorityDesc());
//			initialValues.put("Closure_Date", task.getClosureDateTime());
//			initialValues.put("Target_Date", task.getTargetDateTime());
//			initialValues.put("Fire_Flag", task.getFireFlag());
//			initialValues.put("Reminder_Time", task.getReminderTime());
//			initialValues.put("IsReminder", task.getIsReminder());
//			initialValues.put("IsMessage", task.getIsMessage());
//			initialValues.put("Status", task.getStatus());
//			initialValues.put("IsFavouraite", task.getFavouraite());
//			initialValues.put("TaskType", task.getTaskType());
//			initialValues.put("CreationDate", task.getCreationDate());
//			initialValues.put("IsTaskSync", "Y");
//			initialValues.put("IsJunk", "N");
//
//			ArrayList<ArrayList<String>> tasks = ApplicationUtil.getInstance().uploadListFromDB("Task", null, "Task_ID = '"+task.getTaskId()+"'", context);
//			if(tasks != null && !tasks.isEmpty())
//			{
//				ApplicationUtil.getInstance().updateDataInDB("Task", initialValues, context, "Task_ID = '"+task.getTaskId()+"'", null);
//			}
//
//			else
//			{
//				ApplicationUtil.getInstance().saveDataInDB("Task", null, initialValues, context);
//			}
//		}
//
//	}

	/**
	 * method to parse create task JSON response
	 * @param response
	 * @return
	 */
	public CreateTaskDTO parseCreateTaskResponse(String response) {
		// TODO Auto-generated method stub
		CreateTaskDTO createTask = new CreateTaskDTO();
		ArrayList<TaskInfoDto> arrayList=new ArrayList<TaskInfoDto>();
		try {
			JSONObject responseObj = new JSONObject(response);
			JSONObject responseStatus = responseObj.getJSONObject("response");
			//			createTask.setID(responseStatus.getString("taskId"));
			if(responseStatus.getString("status").equalsIgnoreCase("00")){
			createTask.setStatus(responseStatus.getString("status"));
			//"task":[{"taskId":"271","groupId":"15","assignFrom":"+919560900552","assignTo":"9560900552","assignToName":"Msksood","taskDescription":
			JSONArray taskList=responseStatus.getJSONObject("taskList").getJSONArray("task");
			for (int i = 0; i < taskList.length(); i++) {
				JSONObject jsonObject=taskList.getJSONObject(i);
				TaskInfoDto dto=new TaskInfoDto();
				dto.setTaskId(jsonObject.getString("taskId"));
				dto.setAssignFrom(jsonObject.getString("assignFrom"));
				dto.setAssignTo(jsonObject.getString("assignTo"));
				dto.setGroupId(jsonObject.getString("groupId"));
				dto.setPriority(jsonObject.getString("priority"));
				dto.setTaskDescription(jsonObject.getString("taskDescription"));
				dto.setTransactionId(jsonObject.getString("transactionId"));
				arrayList.add(dto);
			}
			
			if(responseStatus.has("notRegisteredMobileNumbers"))
			{
				ArrayList<String> notRegList = new ArrayList<String>();
				JSONArray taskNotRegisteredList=responseStatus.getJSONArray("notRegisteredMobileNumbers");
				for(int j=0;j<taskNotRegisteredList.length();j++)
				{
					notRegList.add(taskNotRegisteredList.get(j).toString());
				}
				createTask.setNotRegisteredNo(notRegList);
			}
			
			createTask.setTaskList(arrayList);
			createTask.setDisplayMessage(responseStatus.getString("messageDescription"));
			}else{
				createTask.setStatus("05");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return createTask;
	}


	/**
	 * method to parse change favorite JSON response
	 * @param response
	 * @return
	 */
	public String parseUpdateFavResponse(String response) {
		// TODO Auto-generated method stub
		String status = "";
		try {
			JSONObject responseObj = new JSONObject(response);
			JSONObject statusResponse = responseObj.getJSONObject("response");

			status = statusResponse.getString("status");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * method to parse change priority JSON response
	 * @param response
	 * @return
	 */
	public String parsePriorityFavResponse(String response) {
		// TODO Auto-generated method stub
		String status = "";
		try {
			JSONObject responseObj = new JSONObject(response);
			JSONObject statusResponse = responseObj.getJSONObject("response");

			status = statusResponse.getString("status");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}


	/**
	 * method to parse change status JSON response
	 * @param response
	 * @return
	 */
	public String parsestatusResponse(String response) {
		// TODO Auto-generated method stub
		String status = "";
		try {
			JSONObject responseObj = new JSONObject(response);
			JSONObject statusResponse = responseObj.getJSONObject("response");

			status = statusResponse.getString("status");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * method to parse change reminder JSON response
	 * @param response
	 * @return
	 */
	public String parseReminderResponse(String response) {
		// TODO Auto-generated method stub
		String status = "";
		try {
			JSONObject responseObj = new JSONObject(response);
			JSONObject statusResponse = responseObj.getJSONObject("response");

			status = statusResponse.getString("status");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * method to parse add message JSON response
	 * @param response
	 * @return
	 */
	public String parseMessageResponse(String response) {
		// TODO Auto-generated method stub
		String status = "";
		try {
			JSONObject responseObj = new JSONObject(response);
			JSONObject statusResponse = responseObj.getJSONObject("response");

			status = statusResponse.getString("status");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * method to parse task update JSON response
	 * @param response
	 * @return
	 */
	public String parseTaskUpdateResponse(String response) {
		// TODO Auto-generated method stub
		String status = "";
		try {
			JSONObject responseObj = new JSONObject(response);
			JSONObject statusResponse = responseObj.getJSONObject("response");

			status = statusResponse.getString("status");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}
	
	/**
	 * method to parse mark message as read JSON response
	 * @param response
	 * @return
	 */
	public String parseMarkMsgResponse(String response) {
		// TODO Auto-generated method stub
		String status = "";
		try {
			JSONObject responseObj = new JSONObject(response);
			JSONObject statusResponse = responseObj.getJSONObject("response");

			status = statusResponse.getString("status");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}
	/**
	 * method to parse checkRegistrationResponce
	 * @param response
	 * @return
	 */
	public  CheckRegistrationDTO parseCheckRegistrationResponce(String response) {
		// TODO Auto-generated method stub
		CheckRegistrationDTO CheckRegistationDTO = new CheckRegistrationDTO();
		try {
			JSONObject responseList = new JSONObject(response);
			JSONObject requestList = responseList.getJSONObject("request");
			JSONObject responseValue = responseList.getJSONObject("response");
			if(requestList.has("firstName"))
			{
				CheckRegistationDTO.setFirstName(requestList.getString("firstName"));//mobileNumber
			}
			CheckRegistationDTO.setRegStatus(responseValue.getString("regstatus"));
			CheckRegistationDTO.setMobileNumber(requestList.getString("mobileNumber"));
			CheckRegistationDTO.setStatus(responseValue.getString("status"));
			CheckRegistationDTO.setMessageDescription(responseValue.getString("messageDescription"));
			
			


		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return CheckRegistationDTO;
	}



}
