package com.taskmanager.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;

import com.taskmanager.domain.MessageInfoType;
import com.taskmanager.domain.MessageListType;
import com.taskmanager.domain.Task;
import com.taskmanager.domain.TaskInfoType;
import com.taskmanager.domain.TaskResponseListType;
import com.taskmanager.domain.TaskResponseType;
import com.taskmanager.entites.TaskEntity;

public class CommonUtil {
	private static String regNumber;
	
	public static String removeSpecialChar(String number){
		//String number="~<hhh!";
		if(number!=null){
			String regex = "[^a-zA-Z0-9]";
			number = number.replaceAll(regex, "");
		}
		return number;
		
	}
	public static String getRegNum(Context context){
		if(regNumber==null){
			regNumber=ApplicationUtil.getPreference(ApplicationConstant.regMobNo,context);
			regNumber=getValidMsisdn(regNumber);
		}
		return regNumber;
	}
	// for GCM ==========================

	// public static final String SERVER_URL =
	// "http://210.7.64.98/jetspring/jetwebservice/function.php/login/";

	// Google project id
	public static final String SENDER_ID = "296419359074";// "1023607973625";

	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "GCMDEMO";

	public static final String DISPLAY_MESSAGE_ACTION = "com.taskmanager.app";

	public static final String EXTRA_MESSAGE = "message";

	/**
	 * Notifies UI to display a message.
	 * <p>
	 * This method is defined in the common helper because it's used both by the
	 * UI and the background service.
	 * 
	 * @param context
	 *            application's context.
	 * @param message
	 *            message to be displayed.
	 */
	public static void displayMessage(Context context, String message) {
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}

	public static String getContactName(Context context, String phoneNumber) {
		String contactName = "Unknown";
		try {
			ContentResolver cr = context.getContentResolver();
			Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));
			Cursor cursor = cr
					.query(uri, new String[] { PhoneLookup.DISPLAY_NAME },
							null, null, null);
			if (cursor == null) {
				return null;
			}
			contactName = null;
			if (cursor.moveToFirst()) {
				contactName = cursor.getString(cursor
						.getColumnIndex(PhoneLookup.DISPLAY_NAME));
			}

			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return contactName;
	}

	public static String getValidMsisdn(String mobileNumberFrom) {

		if (mobileNumberFrom == null) {
			return "";
		}
				
		String number = mobileNumberFrom.trim().replaceAll("[^0-9]","");
		number = number.replaceAll("\\s+","");

		try {
			if (number != null && number.length() > 10) {
				number = number.substring(number.length() - 10, number.length()).trim();
			}
		} catch (Exception e) {
			number = null;
		}
		return number;
	}

	public static Task getTaList(String json, Context context) throws Exception {
		Task task = new Task();
		TaskResponseType responseType = new TaskResponseType();
		task.setResponse(responseType);
		TaskResponseListType responseListType = new TaskResponseListType();
		responseType.setTaskList(responseListType);

		JSONObject responseList = new JSONObject(json);
		JSONObject responseObj = responseList.getJSONObject("response");
		String status = responseObj.getString("status");
		ApplicationConstant.SYNC_SERVICE_STATUS = status;
		JSONObject taskList = responseObj.getJSONObject("taskList");
		List<TaskInfoType> tasklistResponse = new ArrayList<TaskInfoType>();
		responseListType.setTask(tasklistResponse);
		// System.out.println("lastUpdatedTime taskList--->"+taskList);
		// System.out.println("task list status--->"+status);
		if (status.trim().equalsIgnoreCase("00")) {
			responseType.setStatus(status);

			String lastUpdatedTime = "";
			JSONArray taskArray = new JSONArray();
			Iterator iter1 = taskList.keys();
			while (iter1.hasNext()) {
				String key = (String) iter1.next();
				if (key.equals("lastUpdateTime")) {
					lastUpdatedTime = taskList.getString("lastUpdateTime");
					ApplicationUtil.savePreference(ApplicationConstant.LAST_UPDATED_DATE,lastUpdatedTime, context);

				} else if (key.equals("task")) {
					taskArray = taskList.getJSONArray("task");//
				} else if (key.equals("changedAssignies")) {

					JSONArray changeAssigneeArray = taskList
							.getJSONArray("changedAssignies");
					for (int k = 0; k < changeAssigneeArray.length(); k++) {
						JSONObject ChangeAssigntaskObj = changeAssigneeArray
								.getJSONObject(k);
						if (!ApplicationUtil.getValidNumber(ChangeAssigntaskObj.getString("newAssignee")).equalsIgnoreCase(ApplicationUtil.getValidNumber(ApplicationUtil
												.getPreference(
														ApplicationConstant.regMobNo,
														context)))) {
							try {
								TaskEntity changeTaskEntity = new TaskEntity();
								changeTaskEntity
										.deleteChangeAssignee(
												ChangeAssigntaskObj
														.getString("taskId"),
												context);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}

				}else if(key.equals("deletedTask")){
					JSONArray deletedTask = taskList.getJSONArray("deletedTask");
					for (int k = 0; k < deletedTask.length(); k++) {
						JSONObject deleteObj = deletedTask.getJSONObject(k);
						String taskId=deleteObj.getString("taskId");
						try {
							TaskEntity changeTaskEntity = new TaskEntity();
							changeTaskEntity.deleteChangeAssignee(taskId,context);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
								
				}else if(key.equals("registeredMobileNumbers")){
					JSONArray deletedTask = taskList.getJSONArray("registeredMobileNumbers");
					for (int k = 0; k < deletedTask.length(); k++) {
						JSONObject deleteObj = deletedTask.getJSONObject(k);
						String mobileNumber=deleteObj.getString("mobileNumber");
						try {
							ContentValues updateContact = new ContentValues();
							String number_str = CommonUtil.getValidMsisdn(mobileNumber);
							updateContact.put("REG_STATUS", "REGISTERED");
							ApplicationUtil.getInstance().updateDataInDB("USERS_CONTACT", updateContact,context, "MOBILE_NUMBER ='" + number_str + "'", null);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
								
				}
			}

			for (int i = 0; i < taskArray.length(); i++) {
				TaskInfoType taskInfoType = new TaskInfoType();

				MessageListType messagesListType = new MessageListType();
				taskInfoType.setMessages(messagesListType);
				List<MessageInfoType> messageList = new ArrayList<MessageInfoType>();
				messagesListType.setMessage(messageList);
				JSONObject taskObj = taskArray.getJSONObject(i);
				String taskFrom = "";
				String taskTo = "";
				String priority = "";
				Iterator iter = taskObj.keys();
				while (iter.hasNext()) {
					String key = (String) iter.next();
					// System.out.println("key is----> "+key);
					// ApplicationConstant.taskAdded = true;
					if (key.equals("taskId")) {
						// System.out.println("task Id parse--->"+taskObj.getString("taskId"));
						taskInfoType.setTaskId(taskObj.getString("taskId"));
					} else if (key.equals("groupId")) {
						taskInfoType.setGroupId(taskObj.getString("groupId"));
					} else if (key.equals("assignFrom")) {
						taskInfoType.setAssignFrom(taskObj
								.getString("assignFrom"));
						taskFrom = taskObj.getString("assignFrom");
					} else if (key.equals("assignFromName")) {
						taskInfoType.setAssignFromName(taskObj
								.getString("assignFromName"));
					} else if (key.equals("assignTo")) {
						taskInfoType.setAssignTo(taskObj.getString("assignTo"));
						taskTo = taskObj.getString("assignTo");
					}
//
					if (key.equals("laterTask")) {
					taskInfoType.setLaterTask(taskObj
							.getString("laterTask"));
				}
					else if (key.equals("assignToName")) {
						taskInfoType.setAssignToName(taskObj
								.getString("assignToName"));
					} else if (key.equals("closerDate")) {
						taskInfoType.setCloserDate(taskObj
								.getString("closerDate"));
					} else if (key.equals("targetDate")) {
						taskInfoType.setTargetDate(taskObj
								.getString("targetDate"));
					} else if (key.equals("reminderTime")) {
						taskInfoType.setReminderTime(taskObj
								.getString("reminderTime"));
					} else if (key.equals("modifiedBy")) {
						taskInfoType.setModifiedBy(taskObj
								.getString("modifiedBy"));
					} else if (key.equals("modifiedDate")) {
						taskInfoType.setModifyDate(taskObj
								.getString("modifiedDate"));

					} else if (key.equals("taskDescription")) {
						taskInfoType.setTaskDescription(taskObj
								.getString("taskDescription"));
					} else if (key.equals("priority")) {
						priority = taskObj.getString("priority");
						taskInfoType.setPriority(taskObj.getString("priority"));
					}

					else if (key.equals("favouraite")) {
						taskInfoType.setFavouraite(taskObj
								.getString("favouraite"));
					}else if (key.equals("transactionId")) {
						taskInfoType.setTransactionId(taskObj
								.getString("transactionId"));
					}

					else if (key.equals("status")) {
						taskInfoType.setStatus(taskObj.getString("status"));
					} else if (key.equals("creationDate")) {
						taskInfoType.setCreationDate(taskObj
								.getString("creationDate"));
					} else if (key.equals("totalMessage")) {
						taskInfoType.setTotalMessage(taskObj
								.getString("totalMessage"));
					} else if (key.equals("unreadMessage")) {
						taskInfoType.setUnreadMessage(taskObj
								.getString("unreadMessage"));
					} else if (key.equals("url")) {
						taskInfoType.setTaskUrl(taskObj.getString("url"));
					} 
					
					else if (key.equals("isMessageRead")) {
						taskInfoType.setIsMessageRead(taskObj
								.getString("isMessageRead"));
					}
					
					
					else if (key.equals("isTaskRead")) {
						taskInfoType.setIsTaskRead(taskObj.getString("isTaskRead"));
					}
					
					
					
					else if (key.equals("updateState")) {
						taskInfoType.setUpdateState(taskObj
								.getString("updateState"));
					}
					
					
					else if (key.equals("closedBy")) {
						taskInfoType.setClosedBy(taskObj.getString("closedBy"));
					} else if (key.equals("messages")) {

						JSONObject messagesOgject = taskObj
								.getJSONObject("messages");
						if (messagesOgject.length() != 0) {
							JSONArray msgArray = messagesOgject
									.getJSONArray("message");
							for (int j = 0; j < msgArray.length(); j++) {
								JSONObject messageObject = msgArray
										.getJSONObject(j);
								Iterator msgIterator = messageObject.keys();
								MessageInfoType infoType = new MessageInfoType();
								
								while (msgIterator.hasNext()) {
									String msgKey = (String) msgIterator.next();
									if (msgKey.equals("isMessageRead")) {

										infoType.setIsMessageRead(messageObject
												.getString("isMessageRead"));

									}
									if (msgKey.equals("messageId")) {

										infoType.setMessageId(Long.valueOf(messageObject
												.getString("messageId")));

									} if (msgKey.equals("transactionId")) {

										infoType.setTransactionId(messageObject
												.getString("transactionId"));

									}else if (msgKey.equals("assignFrom")) {
										infoType.setAssignFrom(messageObject
												.getString("assignFrom"));

									} else if (msgKey.equals("assignFromName")) {
										infoType.setAssignFromName(messageObject
												.getString("assignFromName"));

									} else if (msgKey.equals("assignTo")) {
										infoType.setAssignTo(messageObject
												.getString("assignTo"));

									} else if (msgKey.equals("assignToName")) {
										infoType.setAssignToName(messageObject
												.getString("assignToName"));

									} else if (msgKey.equals("createdTime")) {
										infoType.setCreatedTime(messageObject
												.getString("createdTime"));

									} else if (msgKey.equals("taskId")) {
										infoType.setTaskId(Long
												.valueOf(messageObject
														.getString("taskId")));

									} else if (msgKey
											.equals("messageDescription")) {
										infoType.setMessageDescription(messageObject
												.getString("messageDescription"));

									}

								}
								messageList.add(infoType);

							}

						}
					}
				}

				tasklistResponse.add(taskInfoType);
			}
		}
		return task;
	}
	public static Task getSyncTask(String json, Context context) throws Exception {
		Task task = new Task();
		TaskResponseType responseType = new TaskResponseType();
		task.setResponse(responseType);
		TaskResponseListType responseListType = new TaskResponseListType();
		responseType.setTaskList(responseListType);

		JSONObject responseList = new JSONObject(json);
		JSONObject responseObj = responseList.getJSONObject("response");
		String status = responseObj.getString("status");
		ApplicationConstant.SYNC_SERVICE_STATUS = status;
		JSONObject taskList = responseObj.getJSONObject("taskList");
		List<TaskInfoType> tasklistResponse = new ArrayList<TaskInfoType>();
		responseListType.setTask(tasklistResponse);
		// System.out.println("lastUpdatedTime taskList--->"+taskList);
		// System.out.println("task list status--->"+status);
		if (status.trim().equalsIgnoreCase("00")) {
			responseType.setStatus(status);

			String lastUpdatedTime = "";
			JSONArray taskArray = new JSONArray();
			Iterator iter1 = taskList.keys();
			while (iter1.hasNext()) {
				String key = (String) iter1.next();
				if (key.equals("lastUpdateTime")) {
					//lastUpdatedTime = taskList.getString("lastUpdateTime");
					//ApplicationUtil.savePreference(ApplicationConstant.LAST_UPDATED_DATE,lastUpdatedTime, context);

				} else if (key.equals("task")) {
					taskArray = taskList.getJSONArray("task");
				} else if (key.equals("changedAssignies")) {

					JSONArray changeAssigneeArray = taskList
							.getJSONArray("changedAssignies");
					for (int k = 0; k < changeAssigneeArray.length(); k++) {
						JSONObject ChangeAssigntaskObj = changeAssigneeArray
								.getJSONObject(k);
						if (!ApplicationUtil.getValidNumber(ChangeAssigntaskObj.getString("newAssignee")).equalsIgnoreCase(ApplicationUtil.getValidNumber(ApplicationUtil
												.getPreference(
														ApplicationConstant.regMobNo,
														context)))) {
							try {
								TaskEntity changeTaskEntity = new TaskEntity();
								changeTaskEntity
										.deleteChangeAssignee(
												ChangeAssigntaskObj
														.getString("taskId"),
												context);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}

				}
			}

			for (int i = 0; i < taskArray.length(); i++) {
				TaskInfoType taskInfoType = new TaskInfoType();

				MessageListType messagesListType = new MessageListType();
				taskInfoType.setMessages(messagesListType);
				List<MessageInfoType> messageList = new ArrayList<MessageInfoType>();
				messagesListType.setMessage(messageList);
				JSONObject taskObj = taskArray.getJSONObject(i);
				String taskFrom = "";
				String taskTo = "";
				String priority = "";
				Iterator iter = taskObj.keys();
				while (iter.hasNext()) {
					String key = (String) iter.next();
					// System.out.println("key is----> "+key);
					// ApplicationConstant.taskAdded = true;
					if (key.equals("taskId")) {
						// System.out.println("task Id parse--->"+taskObj.getString("taskId"));
						taskInfoType.setTaskId(taskObj.getString("taskId"));
					} else if (key.equals("groupId")) {
						taskInfoType.setGroupId(taskObj.getString("groupId"));
					} else if (key.equals("assignFrom")) {
						taskInfoType.setAssignFrom(taskObj
								.getString("assignFrom"));
						taskFrom = taskObj.getString("assignFrom");
					} else if (key.equals("assignFromName")) {
						taskInfoType.setAssignFromName(taskObj
								.getString("assignFromName"));
					} else if (key.equals("assignTo")) {
						taskInfoType.setAssignTo(taskObj.getString("assignTo"));
						taskTo = taskObj.getString("assignTo");
					}

					else if (key.equals("assignToName")) {
						taskInfoType.setAssignToName(taskObj
								.getString("assignToName"));
					} else if (key.equals("closerDate")) {
						taskInfoType.setCloserDate(taskObj
								.getString("closerDate"));
					} else if (key.equals("targetDate")) {
						taskInfoType.setTargetDate(taskObj
								.getString("targetDate"));
					} else if (key.equals("reminderTime")) {
						taskInfoType.setReminderTime(taskObj
								.getString("reminderTime"));
					} else if (key.equals("modifiedBy")) {
						taskInfoType.setModifiedBy(taskObj
								.getString("modifiedBy"));
					} else if (key.equals("modifiedDate")) {
						taskInfoType.setModifyDate(taskObj
								.getString("modifiedDate"));

					} else if (key.equals("taskDescription")) {
						taskInfoType.setTaskDescription(taskObj
								.getString("taskDescription"));
					} else if (key.equals("priority")) {
						priority = taskObj.getString("priority");
						taskInfoType.setPriority(taskObj.getString("priority"));
					}

					else if (key.equals("favouraite")) {
						taskInfoType.setFavouraite(taskObj
								.getString("favouraite"));
					}else if (key.equals("transactionId")) {
						taskInfoType.setTransactionId(taskObj
								.getString("transactionId"));
					}

					else if (key.equals("status")) {
						taskInfoType.setStatus(taskObj.getString("status"));
					} else if (key.equals("creationDate")) {
						taskInfoType.setCreationDate(taskObj
								.getString("creationDate"));
					} else if (key.equals("totalMessage")) {
						taskInfoType.setTotalMessage(taskObj
								.getString("totalMessage"));
					} else if (key.equals("unreadMessage")) {
						taskInfoType.setUnreadMessage(taskObj
								.getString("unreadMessage"));
					} else if (key.equals("url")) {
						taskInfoType.setTaskUrl(taskObj.getString("url"));
					} 
					
					else if (key.equals("isMessageRead")) {
						taskInfoType.setIsMessageRead(taskObj
								.getString("isMessageRead"));
					}
					
					
					else if (key.equals("isTaskRead")) {
						taskInfoType.setIsTaskRead(taskObj.getString("isTaskRead"));
					}
					
					
					
					else if (key.equals("updateState")) {
						taskInfoType.setUpdateState(taskObj
								.getString("updateState"));
					}
					
					
					else if (key.equals("closedBy")) {
						taskInfoType.setClosedBy(taskObj.getString("closedBy"));
					} else if (key.equals("messages")) {

						JSONObject messagesOgject = taskObj
								.getJSONObject("messages");
						if (messagesOgject.length() != 0) {
							JSONArray msgArray = messagesOgject
									.getJSONArray("message");
							for (int j = 0; j < msgArray.length(); j++) {
								JSONObject messageObject = msgArray
										.getJSONObject(j);
								Iterator msgIterator = messageObject.keys();
								MessageInfoType infoType = new MessageInfoType();
								
								while (msgIterator.hasNext()) {
									String msgKey = (String) msgIterator.next();
									if (msgKey.equals("isMessageRead")) {

										infoType.setIsMessageRead(messageObject
												.getString("isMessageRead"));

									}
									if (msgKey.equals("messageId")) {

										infoType.setMessageId(Long.valueOf(messageObject
												.getString("messageId")));

									} if (msgKey.equals("transactionId")) {

										infoType.setTransactionId(messageObject
												.getString("transactionId"));

									}else if (msgKey.equals("assignFrom")) {
										infoType.setAssignFrom(messageObject
												.getString("assignFrom"));

									} else if (msgKey.equals("assignFromName")) {
										infoType.setAssignFromName(messageObject
												.getString("assignFromName"));

									} else if (msgKey.equals("assignTo")) {
										infoType.setAssignTo(messageObject
												.getString("assignTo"));

									} else if (msgKey.equals("assignToName")) {
										infoType.setAssignToName(messageObject
												.getString("assignToName"));

									} else if (msgKey.equals("createdTime")) {
										infoType.setCreatedTime(messageObject
												.getString("createdTime"));

									} else if (msgKey.equals("taskId")) {
										infoType.setTaskId(Long
												.valueOf(messageObject
														.getString("taskId")));

									} else if (msgKey
											.equals("messageDescription")) {
										infoType.setMessageDescription(messageObject
												.getString("messageDescription"));

									}

								}
								messageList.add(infoType);

							}

						}
					}
				}

				tasklistResponse.add(taskInfoType);
			}
		}
		return task;
	}

	static void displayMessage1(Context context, String message) {
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}
	public static String getTransactionId(Context context ){
		long currentTime=System.currentTimeMillis();
		int randam=new Random().nextInt();
		String transactionId=currentTime+randam+""+getValidMsisdn(ApplicationUtil.getPreference(ApplicationConstant.regMobNo,context));
		return transactionId;
}
	
	public  static String getFiestWord(String sentence){
		
		if(sentence==null){
			return sentence;
		}
		if(sentence.indexOf("#")!=-1){
			sentence=sentence.substring(sentence.indexOf("#"),sentence.length());
		}
		
		String firstWord = sentence.replaceAll(" .*", "");
		return firstWord;
}
}
