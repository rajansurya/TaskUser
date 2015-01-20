package com.taskmanager.webservice;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.taskmanager.bean.CheckRegistrationDTO;
import com.taskmanager.bean.CreateTaskDTO;
import com.taskmanager.bean.LoginDTO;
import com.taskmanager.bean.MessageListDTO;
import com.taskmanager.bean.OTPValidateDTO;
import com.taskmanager.bean.ResponseDto;
import com.taskmanager.bean.registrationDTO;
import com.taskmanager.bean.taskListDTO;
import com.taskmanager.domain.Task;
import com.taskmanager.dto.Contact;
import com.taskmanager.dto.MsExchangeServerInfo;





public interface BGConnector  {
	
	
	public registrationDTO getRegistration(LoginDTO lgnDTO) throws Exception;
	public ArrayList<taskListDTO> getTaskList(String mobileNo, String lastUpdate) throws Exception;
	public ArrayList<MessageListDTO> getTaskMessageList(String TaskID) throws Exception;
	public OTPValidateDTO getOTPValidate() throws Exception;
	public String requestOTPmethod()  throws Exception;
	
	public String resendOTP(String mobileNo, String registrationID)  throws Exception;
	public String receiveOTP(String mobileNo)  throws Exception;
	public CreateTaskDTO createTask(taskListDTO task)  throws Exception;
	public String updateFav(String taskId, String FavVal,String mobilNumber) ;
	public String updatePriority(String taskId, String priority,String mobileNumber)  throws Exception;
	public String updateStatus(String taskId, String Status, boolean isAssignee, String mobileNumber)   throws Exception;
	public String updateReminder(String taskId, String Reminder,String mobileNumber) throws Exception;
	public String createMessage(MessageListDTO msg) throws Exception;
	public String MarkMsgRead(String assignTo, String taskId, String msgID) throws Exception;
	public String updateTask(String taskId, String Assign_from, String Desc,String assignTo, String Priority,String targetDate,String closerDate,String reminderTime,String message,String favouraite)  throws Exception;
	public String updateAssignee(String taskId, String Assign_To,String oldAssignee,String mobileNumber)  throws Exception;
	public String updateTarget(String taskId, String Assign_from,String Assign_To, String Desc, String Target, String Priority,String closerDate,String reminderTime,String message,String favouraite)  throws Exception;
	
	public String updateTarget(String taskId,String targetDate,String mobileNuber )  throws Exception;

	//public void validateNum(String mobileNo)  throws Exception;
	public CheckRegistrationDTO checkRegistrationStatus(String mobileno) throws Exception;
	
	public  ResponseDto createMessageNew(MessageListDTO msg) throws Exception ;
	
	
	public Task getTask(String mobileNumber,String lastUpdateDate,Context context,String deviceToken);
	
	public Task sync(String mobileNumber,String tasIds,Context context,String deviceToken);
	
	public Task getRecievedTask(String mobileNumber,String lastUpdateDate);

	public void registerDevice(String mobileNo,String deviceToken,String platform)  throws Exception;
	
	public boolean updateTaskContent(String mobileNo,String deviceToken,String taskId,String description)  throws Exception;
	/**
	 * 
	 * @param taskId
	 * @param mobileNumber
	 * @return
	 */
	public boolean markTaskAsRead(String taskId,String mobileNumber,String taskTo,String token) throws Exception;
	/**
	 * 
	 * @param taskId
	 * @param mobileNumber
	 * @return
	 */
	
	public boolean markMessageAsRead(String taskId,String mobileNumber,String messageTo,String token) throws Exception;
	
	/**
	 * 
	 * @param contacts
	 * @return
	 * @throws Exception
	 */
	public boolean checkRegStatusForUsers(List<Contact> contacts) throws Exception;
	
	public boolean deleteTask(String taskId,  String mobileNumber,Context context,String deviceToken)   throws Exception;
	
	public boolean getContactsListfromDB(List<Contact> contacts) throws Exception;                          // Ankit SIngh
	
	public boolean updateExchangeServerInfo(MsExchangeServerInfo serverInfo ,Context context,String deviceToken)throws Exception;
	
	
	public String addLaterTask(String taskId,String mobilNumber);
	
	public String removeLaterTask(String taskId,String mobilNumber);
	
	

}
