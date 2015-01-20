package com.taskmanager.webservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.taskmanager.bean.CheckRegistrationDTO;
import com.taskmanager.bean.CreateTaskDTO;
import com.taskmanager.bean.LoginDTO;
import com.taskmanager.bean.MessageListDTO;
import com.taskmanager.bean.OTPValidateDTO;
import com.taskmanager.bean.ResponseDto;
import com.taskmanager.bean.registrationDTO;
import com.taskmanager.bean.taskListDTO;
import com.taskmanager.database.DBAdapter;
import com.taskmanager.domain.ContactStatus;
import com.taskmanager.domain.RegistrationStatus;
import com.taskmanager.domain.Task;
import com.taskmanager.dto.Contact;
import com.taskmanager.dto.MsExchangeServerInfo;
import com.taskmanager.json.RequestMethod;
import com.taskmanager.json.RestClient;
import com.taskmanager.util.ApplicationConstant;
import com.taskmanager.util.ApplicationUtil;
import com.taskmanager.util.CommonUtil;
import com.taskmanager.webservice.json.JSONFactory;
import com.taskmanager.webservice.json.JSONUtil;

public class BGConnectorImpl implements BGConnector {
	private Context context;
	
	
	
	public BGConnectorImpl(Context context)
	{
		this.context = context;
	}

	/**
	 * method to get registration done
	 */
	@Override
	public registrationDTO getRegistration(LoginDTO lgnDTO) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("registration called BGConnectorImpl");
		String dataValue = "{\"request\":{\"mobileNumber\":\""+lgnDTO.getPhoneNo()+"\",\"countryName\":\""+lgnDTO.getCountryName()+"\",\"firstName\":\""+CommonUtil.removeSpecialChar(lgnDTO.getfName())+"\",\"middleName\":\""+CommonUtil.removeSpecialChar(lgnDTO.getMiddleName())+"\",\"lastName\":\""+CommonUtil.removeSpecialChar(lgnDTO.getlName())+"\",\"emailId\":\""+lgnDTO.getEmail()+"\"}}";
		return registrationWebservice(dataValue);
	}

	private registrationDTO registrationWebservice(String request)	throws Exception {

		// Logger object used to log messages for exception.


		String url = ApplicationConstant.Registration_URL;
		PostMethod post = new PostMethod(url);
		if (request != null && request.length()>0) {

			//System.out.println("registration request--->"+request);
			post.setRequestBody(request);
			Header contenHeader = new Header();
			contenHeader.setName("Accept");
			contenHeader.setValue("application/json");
			Header contenHeader2 = new Header();
			contenHeader2.setName("Content-Type");
			contenHeader2.setValue("application/json");
			Header platForm = new Header();
			platForm.setName("X-PLATFORM");
			platForm.setValue("Android");
			Header buildVersion = new Header();
			buildVersion.setName("X-VERSION");
			buildVersion.setValue(ApplicationConstant.BUILD_VERSION);
			post.addRequestHeader(platForm);
			post.addRequestHeader(buildVersion);
			post.addRequestHeader(contenHeader);
			post.addRequestHeader(contenHeader2);



		}
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(ApplicationConstant.CONNECTION_TIME_OUT);
		client.getHttpConnectionManager().getParams().setSoTimeout(ApplicationConstant.READ_TIME_OUT);
		//System.out.println("registration called call URL--->" + url);
		// execute method and handle any error responses.
		registrationDTO ldtDTO = null;
		try {
			client.executeMethod(post);
			
			String in = post.getResponseBodyAsString();
			//System.out.println("registration called inputStream.." + in);

			if (in.equals("") == false) {
				ldtDTO = JSONFactory.getInstance(context).parseRegistrationResponse(in.toString());
				return ldtDTO;
			}
		} catch (HttpException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		}
		return ldtDTO;


	}


	/**
	 * method to call services for getting tasks
	 */

	@Override
	public ArrayList<taskListDTO> getTaskList(String mobileNo, String lastUpdate)
			throws Exception {
		// TODO Auto-generated method stub
		//String lastUpdate1="20/10/2013 13:31:51";
		String dataValue = "{\"request\":{\"mobileNumber\":\""+mobileNo+"\",\"lastUpdateTime\":\""+0+"\"}}";
		//String dataValue = "{\"request\":{\"mobileNumber\":\""+mobileNo+"\",\"lastUpdateTime\":\""+lastUpdate1+"\"}}";
		//20/10/2013 13:31:51

		return taskListWebservice(dataValue);


	}

	private ArrayList<taskListDTO> taskListWebservice(String request)	throws Exception {

		// Logger object used to log messages for exception.

		//System.out.println("Request--->"+request);
		//System.out.println("Token--->"+
		//		ApplicationUtil.getPreference(ApplicationConstant.xtoken, context);
		String url = ApplicationConstant.Sync_Task_List_URL;
		PostMethod post = new PostMethod(url);
		Header contenHeader = new Header();
		contenHeader.setName("Accept");
		contenHeader.setValue("application/json");
		Header contenHeader2 = new Header();
		contenHeader2.setName("Content-Type");
		contenHeader2.setValue("application/json");
		post.addRequestHeader(contenHeader);
		post.addRequestHeader(contenHeader2);
		Header xtoken = new Header();
		xtoken.setName("X-TOKEN");
		xtoken.setValue(ApplicationUtil.getPreference(ApplicationConstant.xtoken, context));
		post.addRequestHeader(xtoken);

		if (request != null) {
			post.setRequestBody(request);
		}
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(ApplicationConstant.CONNECTION_TIME_OUT);
		client.getHttpConnectionManager().getParams().setSoTimeout(ApplicationConstant.READ_TIME_OUT);
		/*if(post.getStatusCode()!=200){
			context.startActivity(new Intent(context, AllTaskActivity.class));
			return null; 
			
		}*/
		//System.out.println("Sync called call URL--->" + url);
		// execute method and handle any error responses.
		ArrayList<taskListDTO> ldtDTO = null;
		try {
			client.executeMethod(post);
			String in = post.getResponseBodyAsString();
			//System.out.println("Sync called inputStream.." + in);

			if (in.equals("") == false) {
				ldtDTO = JSONFactory.getInstance(context).parseTaskListResponse(in.toString());
				return ldtDTO;
			}
		} catch (HttpException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		}
		return ldtDTO;


	}

	/**
	 *  method to call service to fetch task messages
	 */
	@Override
	public ArrayList<MessageListDTO> getTaskMessageList(String TaskID) throws Exception {
		// TODO Auto-generated method stub
		String dataValue = "{\"request\":{\"taskId\":\""+TaskID+"\"}}";
		return taskMessageListWebservice(dataValue, TaskID);
	}

	private ArrayList<MessageListDTO> taskMessageListWebservice(String request, String TaskID)	throws Exception {
		String url = ApplicationConstant.Message_List_URL+TaskID+"/message/fetch";
		PostMethod post = new PostMethod(url);
		Header contenHeader = new Header();
		contenHeader.setName("Accept");
		contenHeader.setValue("application/json");
		Header contenHeader2 = new Header();
		contenHeader2.setName("Content-Type");
		contenHeader2.setValue("application/json");
		post.addRequestHeader(contenHeader);
		post.addRequestHeader(contenHeader2);
		Header xtoken = new Header();
		xtoken.setName("X-TOKEN");
		xtoken.setValue(ApplicationUtil.getPreference(ApplicationConstant.xtoken, context));
		//System.out.println("Xtoken"+ApplicationUtil.getPreference(ApplicationConstant.xtoken, context));
		//System.out.println("request"+request);
		post.addRequestHeader(xtoken);
		if (request != null) {
			post.setRequestBody(request);
		}
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(ApplicationConstant.CONNECTION_TIME_OUT);
		client.getHttpConnectionManager().getParams().setSoTimeout(ApplicationConstant.READ_TIME_OUT);
		ArrayList<MessageListDTO> ldtDTO = null;
		try {
			client.executeMethod(post);
			String in = post.getResponseBodyAsString();
			//System.out.println("registration called inputStream.." + in);

			if (in.equals("") == false) {
				ldtDTO = JSONFactory.getInstance(context).parseTaskMessageListResponse(in.toString());
				return ldtDTO;
			}
		} catch (HttpException e) {
			e.printStackTrace();
			ldtDTO = null;

		} catch (IOException e) {
			e.printStackTrace();
			ldtDTO = null;

		}
		return ldtDTO;


	}

	/**
	 *  method to call service to validate OTP
	 */
	@Override
	public OTPValidateDTO getOTPValidate() throws Exception {
		System.out.println("Here is in service ... " + ApplicationConstant.OTP);
		String data = "{\"request\":{\"mobileNumber\":\""+(ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context))+"\", \"otp\":\""+ApplicationConstant.OTP+"\"}}";
		//		{"request":{"mobileNumber":"123456789", "otp":"7838"}}
		return validateOTPWebservice(data);
		// TODO Auto-generated method stub

	}

	private OTPValidateDTO validateOTPWebservice(String request) throws Exception{
		String url = ApplicationConstant.Validate_OTP_URL;
		PostMethod post = new PostMethod(url);
		Header contenHeader = new Header();
		contenHeader.setName("Accept");
		contenHeader.setValue("application/json");
		Header contenHeader2 = new Header();
		contenHeader2.setName("Content-Type");
		contenHeader2.setValue("application/json");
		Header xtoken = new Header();
		xtoken.setName("X-TOKEN");
		xtoken.setValue(ApplicationUtil.getPreference(ApplicationConstant.xtoken, context));
		post.addRequestHeader(contenHeader);
		post.addRequestHeader(contenHeader2);
		post.addRequestHeader(xtoken);
		if (request != null && request.length()>0) {
			post.setRequestBody(request);

		}
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(ApplicationConstant.CONNECTION_TIME_OUT);
		client.getHttpConnectionManager().getParams().setSoTimeout(ApplicationConstant.READ_TIME_OUT);
		//System.out.println("registration called call URL--->" + url);
		// execute method and handle any error responses.
		OTPValidateDTO ldtDTO = null;
		try {
			client.executeMethod(post);
			String in = post.getResponseBodyAsString();
			//System.out.println("registration called inputStream.." + in);

			if (in.equals("") == false) {
				ldtDTO = JSONFactory.getInstance(context).parseValidateOTPResponse(in.toString());
				return ldtDTO;
			}
		} catch (HttpException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		}
		return ldtDTO;

	}

	
	/**
	 *  method to call service to validate OTP
	 */
	@Override
	public String requestOTPmethod() throws Exception {

		String data = "{\"request\":{\"mobileNumber\":\""+(ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context))+"\"}}";
		//		{"request":{"mobileNumber":"123456789", "otp":"7838"}}
		return requestOTPWebservice(data);
		// TODO Auto-generated method stub

	}

	private String requestOTPWebservice(String request) throws Exception{
		String response= "";
		String url = ApplicationConstant.Request_OTP_URL;
		PostMethod post = new PostMethod(url);
		Header contenHeader = new Header();
		contenHeader.setName("Accept");
		contenHeader.setValue("application/json");
		Header contenHeader2 = new Header();
		contenHeader2.setName("Content-Type");
		contenHeader2.setValue("application/json");
		Header xtoken = new Header();
		xtoken.setName("X-TOKEN");
		xtoken.setValue(ApplicationUtil.getPreference(ApplicationConstant.xtoken, context));
		post.addRequestHeader(contenHeader);
		post.addRequestHeader(contenHeader2);
		post.addRequestHeader(xtoken);
		if (request != null && request.length()>0) {
			post.setRequestBody(request);

		}
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(ApplicationConstant.CONNECTION_TIME_OUT);
		client.getHttpConnectionManager().getParams().setSoTimeout(ApplicationConstant.READ_TIME_OUT);
		//System.out.println("registration called call URL--->" + url);
		// execute method and handle any error responses.
		OTPValidateDTO ldtDTO = null;
		try {
			client.executeMethod(post);
			String in = post.getResponseBodyAsString();
			//System.out.println("registration called inputStream.." + in);

			/*if (in.equals("") == false) {
				response = JSONFactory.getInstance(context).parseValidateOTPResponse(in.toString());
				return response;
			}*/
			
		} catch (HttpException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		}
		return response;



	}
	
	
	
	
	
	

	/**
	 *  method to call service to resends OTP
	 */
	@Override
	public String resendOTP(String mobileNo, String registrationID)throws Exception {
		// TODO Auto-generated method stub
		//		{"operationName":"Resend_OTP"
		//			  "mobileNumber":"9560900552",
		//			  "regId":"123456"}
		NameValuePair[] data = {
				new NameValuePair("operationName","Resend_OTP"),
				new NameValuePair("mobileNumber",mobileNo),
				new NameValuePair("regId",registrationID),


		};
		return resendOTPWebservice(data);
	}


	private String resendOTPWebservice(NameValuePair[] request)	throws Exception {

		// Logger object used to log messages for exception.

		String response = "";
		String url = ApplicationConstant.Resend_OTP_URL;
		PostMethod post = new PostMethod(url);
		if (request != null) {
			post.setRequestBody(request);
		}
		HttpClient client = new HttpClient();
		//client.setConnectionTimeout(55000);
		//System.out.println("registration called call URL--->" + url);
		// execute method and handle any error responses.
		try {
			client.executeMethod(post);
			String in = post.getResponseBodyAsString();
			//System.out.println("registration called inputStream.." + in);

			if (in.equals("") == false) {
				response = JSONFactory.getInstance(context).parseResendOTPResponse(in.toString());
				return response;
			}
		} catch (HttpException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		}
		return response;


	}


	
	
	/**
	 * method to get OTP from service
	 */
	@Override
	public String receiveOTP(String mobileNo) throws Exception {
		// TODO Auto-generated method stub
		String url = ApplicationConstant.Recieve_OTP_URL+mobileNo;
		//System.out.println("OTP URL--->"+url);
		GetMethod get = new GetMethod(url);
		HttpClient client = new HttpClient();
		client.executeMethod(get);
		String OTP = get.getResponseBodyAsString();
		//System.out.println("OTP FOR---->"+mobileNo+"--"+OTP);
		return OTP;
	}

	/**
	 * method to call web service for adding new task
	 */
	@Override
	public synchronized CreateTaskDTO createTask(taskListDTO task) throws Exception {
		
		//System.out.println("createTask called with service Hits"+Thread.currentThread().getName());
		//System.out.println("createTask called with service Hits------>>>>>>>>>>"+task.getAssignFrom()+"==="+task.getTaskDesc());
		
		String dataValue = "{\"request\":{\"assignees\":["+task.getAssignTo()+"],\"assignFrom\": \""+task.getAssignFrom()+"\", \"transactionId\": \""+task.getTransactioId()+"\",\"taskDescription\": \""+task.getTaskDesc()+"\",\"priority\": \""+task.getPriority()+"\",\"status\": \""+task.getStatus()+"\",\"targetDate\": \""+task.getTargetDateTime()+"\",\"closerDate\": \""+task.getClosureDateTime()+"\",\"reminderTime\": \""+task.getReminderTime()+"\",\"message\": false,\"favouraite\": \""+task.getFavouraite()+"\"}}";

		return createTaskWebservice(dataValue);
	}

	private synchronized CreateTaskDTO createTaskWebservice(String request)	throws Exception {
		String url = ApplicationConstant.Create_Task_URL;
		PostMethod post = new PostMethod(url);
		Header contenHeader = new Header();
		contenHeader.setName("Accept");
		contenHeader.setValue("application/json");
		Header contenHeader2 = new Header();
		contenHeader2.setName("Content-Type");
		contenHeader2.setValue("application/json");
		post.addRequestHeader(contenHeader);
		post.addRequestHeader(contenHeader2);
		Header xtoken = new Header();
		 
		xtoken.setName("X-TOKEN");
		xtoken.setValue(ApplicationUtil.getPreference(ApplicationConstant.xtoken, context));
		post.addRequestHeader(xtoken);
		Header premium = new Header();
		premium.setName("X-PREMIUM");
		premium.setValue(ApplicationConstant.PREMIUM);
		post.addRequestHeader(premium);
		if (request != null) {
			post.setRequestBody(request);
		}
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(ApplicationConstant.CONNECTION_TIME_OUT);
		client.getHttpConnectionManager().getParams().setSoTimeout(ApplicationConstant.READ_TIME_OUT);
		//System.out.println("task create called request--->" + request);
		//System.out.println("task create called call URL--->" + url);
	//	402 Payment Required
		CreateTaskDTO ldtDTO = new CreateTaskDTO();
		try {
			client.executeMethod(post);
			String in = post.getResponseBodyAsString();
			int httpSatus=post.getStatusCode();
			if(httpSatus==401){
				ldtDTO.setStatus(httpSatus+"");
				ldtDTO.setDisplayMessage(post.getResponseBodyAsString());
				return ldtDTO;
			}
			JSONObject taskObject=new JSONObject(in);
			JSONObject responseObj = taskObject.getJSONObject("response");
			String status = responseObj.getString("status");
			String messageDescription=responseObj.getString("messageDescription");
			
			
			
			//System.out.println("task create called inputStream.." + in);

			if (in.equals("") == false) {
				Log.d("Inside CreateTaskDTO Request", "Message is"+in.toString());
				ldtDTO = JSONFactory.getInstance(context).parseCreateTaskResponse(in.toString());
				return ldtDTO;
			}
		} catch (HttpException e) {
			e.printStackTrace();
			ldtDTO = null;

		} catch (Exception e) {
			e.printStackTrace();
			ldtDTO = null;

		}
		return ldtDTO;


	}

	/**
	 * method to call web service for updating task favorite status
	 */
	@Override
	public String updateFav(String taskId, String FavVal,String mobileNumber)  {
		String data = "{\"request\":{\"taskId\":\""+taskId+"\", \"favouraite\":\""+FavVal+"\",\"mobileNumber\":\""+mobileNumber+"\"}}";
		return updateFavWebservice(data, taskId);
	}
	
	private String updateFavWebservice(String request, String TaskId) {
		String ldtDTO = null;
		String url = ApplicationConstant.Set_Favouraite_Task_URL+TaskId+"/setFavouraite";
		try {
		PostMethod post = new PostMethod(url);
		Header contenHeader = new Header();
		contenHeader.setName("Accept");
		contenHeader.setValue("application/json");
		Header contenHeader2 = new Header();
		contenHeader2.setName("Content-Type");
		contenHeader2.setValue("application/json");
		post.addRequestHeader(contenHeader);
		post.addRequestHeader(contenHeader2);
		Header xtoken = new Header();
		xtoken.setName("X-TOKEN");
		xtoken.setValue(ApplicationUtil.getPreference(ApplicationConstant.xtoken, context));
		post.addRequestHeader(xtoken);
		if (request != null) {
			post.setRequestBody(request);
		}
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(ApplicationConstant.CONNECTION_TIME_OUT);
		client.getHttpConnectionManager().getParams().setSoTimeout(ApplicationConstant.READ_TIME_OUT);
		//System.out.println("update Fav called call URL--->" + url);
		client.executeMethod(post);
		String in = post.getResponseBodyAsString();
		//System.out.println("update Fav called inputStream.." + in);

			if (in.equals("") == false) {
				ldtDTO = JSONFactory.getInstance(context).parseUpdateFavResponse(in.toString());
				return ldtDTO;
			}
		} catch (HttpException e) {
			e.printStackTrace();
			ldtDTO=null;

		} catch (IOException e) {
			ldtDTO=null;
			e.printStackTrace();

		}
		return ldtDTO;


	}


	/**
	 * method to call web service for updating task priority
	 */
	@Override
	public String updatePriority(String taskId, String priority,String mobileNumber)
			throws Exception {
		String data = "{\"request\":{\"mobileNumber\":\""+mobileNumber+"\",\"taskId\":\""+taskId+"\", \"priority\":\""+priority+"\"}}";
		//System.out.println(priority);
		return updatepriorityWebservice(data, taskId);
		// TODO Auto-generated method stub

	}

	private String updatepriorityWebservice(String request, String TaskId)	throws Exception {
		String url = ApplicationConstant.Set_Priority_Task_URL+TaskId+"/changePriority";
		PostMethod post = new PostMethod(url);
		Header contenHeader = new Header();
		contenHeader.setName("Accept");
		contenHeader.setValue("application/json");
		Header contenHeader2 = new Header();
		contenHeader2.setName("Content-Type");
		contenHeader2.setValue("application/json");
		post.addRequestHeader(contenHeader);
		post.addRequestHeader(contenHeader2);
		Header xtoken = new Header();
		xtoken.setName("X-TOKEN");
		xtoken.setValue(
				ApplicationUtil.getPreference(ApplicationConstant.xtoken, context));
		post.addRequestHeader(xtoken);

		if (request != null) {
			post.setRequestBody(request);
		}
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(ApplicationConstant.CONNECTION_TIME_OUT);
		client.getHttpConnectionManager().getParams().setSoTimeout(ApplicationConstant.READ_TIME_OUT);
		//System.out.println("update Fav called call URL--->" + url);
		// execute method and handle any error responses.
		String ldtDTO = null;
		try {
			client.executeMethod(post);
			String in = post.getResponseBodyAsString();
			//System.out.println("update Fav called inputStream.." + in);

			if (in.equals("") == false) {
				ldtDTO = JSONFactory.getInstance(context).parsePriorityFavResponse(in.toString());
				return ldtDTO;
			}
		} catch (HttpException e) {
			ldtDTO="05";
			e.printStackTrace();

		} catch (IOException e) {
			ldtDTO="05";
			e.printStackTrace();

		}
		return ldtDTO;


	}

	/**
	 * method to call web service for updating task status
	 */
	@Override
	public String updateStatus(String taskId, String Status, boolean isAssignee, String mobileNumber) throws Exception {
		// TODO Auto-generated method stub
		String data = "{\"request\":{\"taskId\":\""+taskId+"\", \"status\":\""+Status+"\", \"isAssiner\":\""+isAssignee+"\", \"mobileNumber\":\""+mobileNumber+"\"}}";
		//System.out.println(Status);
		return updateStatusWebservice(data, taskId);
		// TODO Auto-generated method stub


	}

	private String updateStatusWebservice(String request, String TaskId)	throws Exception {
		String url = ApplicationConstant.Set_Status_Task_URL+TaskId+"/changeStatus";
		PostMethod post = new PostMethod(url);
		Header contenHeader = new Header();
		contenHeader.setName("Accept");
		contenHeader.setValue("application/json");
		Header contenHeader2 = new Header();
		contenHeader2.setName("Content-Type");
		contenHeader2.setValue("application/json");
		post.addRequestHeader(contenHeader);
		post.addRequestHeader(contenHeader2);
		Header xtoken = new Header();
		xtoken.setName("X-TOKEN");
		xtoken.setValue(
				ApplicationUtil.getPreference(ApplicationConstant.xtoken, context));
		post.addRequestHeader(xtoken);

		if (request != null) {
			post.setRequestBody(request);
		}
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(ApplicationConstant.CONNECTION_TIME_OUT);
		client.getHttpConnectionManager().getParams().setSoTimeout(ApplicationConstant.READ_TIME_OUT);
		//System.out.println("update Status called call URL--->" + url+"--request---"+request);
		// execute method and handle any error responses.
		String ldtDTO = null;
		try {
			client.executeMethod(post);
			String in = post.getResponseBodyAsString();
			//System.out.println("update Status called inputStream.." + in);

			if (in.equals("") == false) {
				ldtDTO = JSONFactory.getInstance(context).parsestatusResponse(in.toString());
				return ldtDTO;
			}
		} catch (HttpException e) {
			ldtDTO="05";
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
			ldtDTO="05";

		}
		return ldtDTO;


	}

	/**
	 * method to call web service for updating reminder time
	 */
	@Override
	public String updateReminder(String taskId, String Reminder,String mobileNumber)
			throws Exception {
		// TODO Auto-generated method stub
		String data = "{\"request\":{\"mobileNumber\":\""+mobileNumber+"\",\"taskId\":\""+taskId+"\", \"reminderTime\":\""+Reminder+"\"}}";
		//System.out.println(Reminder);
		return updateReminderWebservice(data, taskId);
		// TODO Auto-generated method stub


	}

	private String updateReminderWebservice(String request, String TaskId)	throws Exception {
		String url = ApplicationConstant.Set_Reminder_Task_URL+TaskId+"/setReminder";
		PostMethod post = new PostMethod(url);
		Header contenHeader = new Header();
		contenHeader.setName("Accept");
		contenHeader.setValue("application/json");
		Header contenHeader2 = new Header();
		contenHeader2.setName("Content-Type");
		contenHeader2.setValue("application/json");
		post.addRequestHeader(contenHeader);
		post.addRequestHeader(contenHeader2);
		Header xtoken = new Header();
		xtoken.setName("X-TOKEN");
		xtoken.setValue(ApplicationUtil.getPreference(ApplicationConstant.xtoken, context));
		post.addRequestHeader(xtoken);

		if (request != null) {
			post.setRequestBody(request);
		}
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(ApplicationConstant.CONNECTION_TIME_OUT);
		client.getHttpConnectionManager().getParams().setSoTimeout(ApplicationConstant.READ_TIME_OUT);
		//System.out.println("update Fav called call URL--->" + url);
		String ldtDTO = null;
		try {
			client.executeMethod(post);
			String in = post.getResponseBodyAsString();
			//System.out.println("update Fav called inputStream.." + in);
			if (in.equals("") == false) {
				ldtDTO = JSONFactory.getInstance(context).parseReminderResponse(in.toString());
				return ldtDTO;
			}
		} catch (HttpException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		}
		return ldtDTO;


	}

	/**
	 * method to call web service for adding messages to a task
	 */
	@Override
	public synchronized  ResponseDto createMessageNew(MessageListDTO msg) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("createMessage called with service Hits"+Thread.currentThread().getName());
		//System.out.println("createTask called with service Hits------>>>>>>>>>>"+msg.getmFrom()+"==="+msg.getTaskId());
		String data = "{\"request\":{\"assignFrom\":\""+msg.getmFrom()+"\",\"transactionId\": \""+msg.getTransactioId()+"\",\"oldMessageId\":\""+msg.getOldMessageId()+"\", \"assignTo\":\""+msg.getmTo()+"\", \"taskId\":\""+msg.getTaskId()+"\", \"messageDescription\":\""+msg.getmDesc()+"\"}}";
		return updateMessageWebserviceNew(data, msg.getTaskId());


	}
	
	@Override
	public synchronized  String createMessage(MessageListDTO msg) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("createMessage called with service Hits"+Thread.currentThread().getName());
		//System.out.println("createTask called with service Hits------>>>>>>>>>>"+msg.getmFrom()+"==="+msg.getTaskId());
		String data = "{\"request\":{\"assignFrom\":\""+msg.getmFrom()+"\", \"assignTo\":\""+msg.getmTo()+"\", \"taskId\":\""+msg.getTaskId()+"\", \"messageDescription\":\""+msg.getmDesc()+"\"}}";
		return updateMessageWebservice(data, msg.getTaskId());


	}

	private synchronized  String updateMessageWebservice(String request, String TaskId)	throws Exception {
		String url = ApplicationConstant.Create_Message_URL+TaskId+"/message/create";
		PostMethod post = new PostMethod(url);
		Header contenHeader = new Header();
		contenHeader.setName("Accept");
		contenHeader.setValue("application/json");
		Header contenHeader2 = new Header();
		contenHeader2.setName("Content-Type");
		contenHeader2.setValue("application/json");
		post.addRequestHeader(contenHeader);
		post.addRequestHeader(contenHeader2);
		Header xtoken = new Header();
		xtoken.setName("X-TOKEN");
		xtoken.setValue(
				ApplicationUtil.getPreference(ApplicationConstant.xtoken, context));
		post.addRequestHeader(xtoken);

		if (request != null) {
			post.setRequestBody(request);
		}
		String ldtDTO = null;
		try {
			HttpClient client = new HttpClient();
			client.getHttpConnectionManager().getParams().setConnectionTimeout(ApplicationConstant.CONNECTION_TIME_OUT);
			client.getHttpConnectionManager().getParams().setSoTimeout(ApplicationConstant.READ_TIME_OUT);
			client.executeMethod(post);
			String in = post.getResponseBodyAsString();
			//System.out.println("update Fav called inputStream.." + in);

			if (in.equals("") == false) {
				ldtDTO = JSONFactory.getInstance(context).parseMessageResponse(in.toString());
				return ldtDTO;
			}
		} catch (HttpException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		}
		return ldtDTO;


	}
	
	private synchronized  ResponseDto updateMessageWebserviceNew(String request, String TaskId)	throws Exception {
		ResponseDto dto=new ResponseDto();
		String url = ApplicationConstant.Create_Message_URL+TaskId+"/message/create";
		PostMethod post = new PostMethod(url);
		Header contenHeader = new Header();
		contenHeader.setName("Accept");
		contenHeader.setValue("application/json");
		Header contenHeader2 = new Header();
		contenHeader2.setName("Content-Type");
		contenHeader2.setValue("application/json");
		post.addRequestHeader(contenHeader);
		post.addRequestHeader(contenHeader2);
		Header xtoken = new Header();
		xtoken.setName("X-TOKEN");
		xtoken.setValue(ApplicationUtil.getPreference(ApplicationConstant.xtoken, context));
		post.addRequestHeader(xtoken);

		if (request != null) {
			post.setRequestBody(request);
		}
		String ldtDTO = null;
		try {
			HttpClient client = new HttpClient();
			client.getHttpConnectionManager().getParams().setConnectionTimeout(ApplicationConstant.CONNECTION_TIME_OUT);
			client.getHttpConnectionManager().getParams().setSoTimeout(ApplicationConstant.READ_TIME_OUT);
			client.executeMethod(post);
			int httpSatus=post.getStatusCode();
			if(httpSatus==401){
				ResponseDto responseDto=new ResponseDto();
				responseDto.setStatus(httpSatus+"");
				responseDto.setMessageDescription(post.getResponseBodyAsString());
				return responseDto;
			}
			if(post.getStatusCode()!=200){
				ldtDTO="05";
			//	Toast.makeText(context, "Unauthorized", Toast.LENGTH_LONG).show();
				dto.setStatus(ldtDTO);
				return dto ;
			}
			String in = post.getResponseBodyAsString();
			//System.out.println("update Fav called inputStream.." + in);

			JSONObject responseObj = new JSONObject(in);
			JSONObject statusResponse = responseObj.getJSONObject("response");
			String	status = statusResponse.getString("status");
			ldtDTO=status;
			String	messageId = statusResponse.getString("messageId");
			dto.setMessageId(messageId);
		
		} catch (HttpException e) {
			ldtDTO="05";
			e.printStackTrace();

		} catch (Exception e) {
			ldtDTO="05";
			e.printStackTrace();

		}
		dto.setStatus(ldtDTO);
		return dto;


	}

	/**
	 * method to call web service for updating task Description
	 */
	@Override
	public String updateTask(String taskId, String Assign_from, String Desc,String assignTo, String Priority,String targetDate,String closerDate,String reminderTime,String message,String favouraite)
			throws Exception {

		//		<taskId>{taskId}</taskId>
		//	      <assignFrom>123456789</assignFrom>
		//	      <assignTo>9560900552</assignTo>
		//	      <taskDescription>dsadsadasdasdasdas ggsa SAs sa s</taskDescription>
		//	      <priority>4</priority>
		//	      <targetDate>10/12/2013</targetDate>
		//	      <closerDate>10/12/2013</closerDate>
		//	      <reminderTime>20/07/2013 15:40</reminderTime>
		//	      <message>false</message>
		//	      <favouraite>false</favouraite>
		String data = "{\"request\":{ \"taskId\":\""+taskId+"\", \"assignFrom\":\""+Assign_from+"\",\"assignTo\":\""+assignTo+"\", \"taskDescription\":\""+Desc+"\",\"priority\":\""+Priority+"\",\"targetDate\":\""+targetDate+"\",\"closerDate\":\""+closerDate+"\",\"reminderTime\":\""+reminderTime+"\",\"message\":\""+message+"\",\"favouraite\":\""+favouraite+"\"}}";
		return updateTaskWebservice(data, taskId);

	}

	private String updateTaskWebservice(String request, String TaskId)	throws Exception {
		String url = ApplicationConstant.Update_Task_URL+TaskId+"/update";
		PostMethod post = new PostMethod(url);
		Header contenHeader = new Header();
		contenHeader.setName("Accept");
		contenHeader.setValue("application/json");
		Header contenHeader2 = new Header();
		contenHeader2.setName("Content-Type");
		contenHeader2.setValue("application/json");
		post.addRequestHeader(contenHeader);
		post.addRequestHeader(contenHeader2);
		Header xtoken = new Header();
		xtoken.setName("X-TOKEN");
		xtoken.setValue(ApplicationUtil.getPreference(ApplicationConstant.xtoken, context));
		post.addRequestHeader(xtoken);

		if (request != null) {
			post.setRequestBody(request);
		}
		HttpClient client = new HttpClient();
		//client.setConnectionTimeout(55000);
		client.getHttpConnectionManager().getParams().setConnectionTimeout(3000);
		client.getHttpConnectionManager().getParams().setSoTimeout(3000);
		//System.out.println("update Task called call URL--->" + url);
		// execute method and handle any error responses.
		String ldtDTO = null;
		try {
			client.executeMethod(post);
			String in = post.getResponseBodyAsString();
			//System.out.println("update Task called inputStream.." + in);

			if (in.equals("") == false) {
				ldtDTO = JSONFactory.getInstance(context).parseTaskUpdateResponse(in.toString());
				return ldtDTO;
			}
		} catch (HttpException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		}
		return ldtDTO;


	}

	/**
	 * method to call web service for updating Assignee
	 */
	@Override
	public String updateAssignee(String taskId,String newAssign_To, String oldAssignee,String mobileNumber) throws Exception {
		String data = "{\"request\":{\"mobileNumber\":\""+mobileNumber+"\", \"taskId\":\""+taskId+"\", \"newAssignee\":\""+newAssign_To+"\", \"oldAssignee\":\""+oldAssignee+"\"}}";
		return updateAssigneeWebservice(data, taskId);

	}

	private String updateAssigneeWebservice(String request, String TaskId)	throws Exception {
		String url = ApplicationConstant.changeAssignee+TaskId+"/changeAssignee";
		PostMethod post = new PostMethod(url);
		Header contenHeader = new Header();
		contenHeader.setName("Accept");
		contenHeader.setValue("application/json");
		Header contenHeader2 = new Header();
		contenHeader2.setName("Content-Type");
		contenHeader2.setValue("application/json");
		post.addRequestHeader(contenHeader);
		post.addRequestHeader(contenHeader2);
		Header xtoken = new Header();
		xtoken.setName("X-TOKEN");
		xtoken.setValue(ApplicationUtil.getPreference(ApplicationConstant.xtoken, context));
		post.addRequestHeader(xtoken);

		if (request != null) {
			post.setRequestBody(request);
		}
		HttpClient client = new HttpClient();
		//client.setConnectionTimeout(55000);
		client.getHttpConnectionManager().getParams().setConnectionTimeout(ApplicationConstant.CONNECTION_TIME_OUT);
		client.getHttpConnectionManager().getParams().setSoTimeout(ApplicationConstant.READ_TIME_OUT);
		
		//System.out.println("update Task called call URL--->" + url);
		// execute method and handle any error responses.
		String ldtDTO = null;
		try {
			client.executeMethod(post);
			String in = post.getResponseBodyAsString();
			//System.out.println("update Task called inputStream.." + in);

			if (in.equals("") == false) {
				ldtDTO = JSONFactory.getInstance(context).parseTaskUpdateResponse(in.toString());
				return ldtDTO;
			}
		} catch (HttpException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		}
		return ldtDTO;


	}

	/**
	 * method to call web service for updating target time
	 */
	@Override
	public String updateTarget(String taskId, String Assign_from,String Assign_To, String Desc, String Target, String Priority,String closerDate,String reminderTime,String message,String favouraite) throws Exception {
	//	String data = "{\"request\":{ \"taskId\":\""+taskId+"\", \"assignFrom\":\""+Assign_from+"\",\"assignTo\":\""+Assign_To+"\", \"taskDescription\":\""+Desc+"\",\"priority\":\""+Priority+"\",\"targetDate\":\""+Target+"\",\"closerDate\":\""+closerDate+"\",\"reminderTime\":\""+reminderTime+"\",\"message\":\""+message+"\",\"favouraite\":\""+favouraite+"\"}}";
		
		String data = "{\"request\":{ \"taskId\":\""+taskId+"\",\"targetDate\":\""+Target+"\",\"mobileNumber\":\""+Assign_from+"\"}}";
		
		return updateTargetWebservice(data, taskId);

	}
	@Override
	public String updateTarget(String taskId, String targetDate,String mobileNuber)throws Exception {
		String data = "{\"request\":{ \"taskId\":\""+taskId+"\",\"targetDate\":\""+targetDate+"\",\"mobileNumber\":\""+mobileNuber+"\"}}";

		 return updateTargetWebservice(data, taskId);
	}
	

	private String updateTargetWebservice(String request, String TaskId)	throws Exception {
		//String url = ApplicationConstant.Update_Task_URL+TaskId+"/update";
		String url=ApplicationConstant.Update_Task_URL+TaskId+"/setTargetDate";
		PostMethod post = new PostMethod(url);
		Header contenHeader = new Header();
		contenHeader.setName("Accept");
		contenHeader.setValue("application/json");
		Header contenHeader2 = new Header();
		contenHeader2.setName("Content-Type");
		contenHeader2.setValue("application/json");
		post.addRequestHeader(contenHeader);
		post.addRequestHeader(contenHeader2);
		Header xtoken = new Header();
		xtoken.setName("X-TOKEN");
		xtoken.setValue(ApplicationUtil.getPreference(ApplicationConstant.xtoken, context));
		post.addRequestHeader(xtoken);

		if (request != null) {
			post.setRequestBody(request);
		}
		//client.setConnectionTimeout(55000);
		//System.out.println("update Target called call URL--->" + url);
		// execute method and handle any error responses.
		String ldtDTO = null;
		try {
			HttpClient client = new HttpClient();
			client.getHttpConnectionManager().getParams().setConnectionTimeout(ApplicationConstant.CONNECTION_TIME_OUT);
			client.getHttpConnectionManager().getParams().setSoTimeout(ApplicationConstant.READ_TIME_OUT);
			client.executeMethod(post);
			String in = post.getResponseBodyAsString();
			//System.out.println("update Target called inputStream.." + in);

			if (in.equals("") == false) {
				ldtDTO = JSONFactory.getInstance(context).parseTaskUpdateResponse(in.toString());
				ldtDTO="00";
				return ldtDTO;
			}
		} catch (HttpException e) {
			e.printStackTrace();

		} catch (IOException e) {
			ldtDTO="05";
			e.printStackTrace();

		}
		return ldtDTO;


	}

	@Override
	public String MarkMsgRead(String assignTo, String taskId, String msgID) throws Exception {
		// TODO Auto-generated method stub
		String data = "{\"request\":{ \"mobileNumber\":\""+assignTo+"\", \"taskId\":\""+taskId+"\",\"messageId\":\""+msgID+"\"}}";
		return MarkReadtWebservice(data,taskId, msgID);
		
	}
	
	private String MarkReadtWebservice(String request, String taskId, String msgID)	throws Exception {
		String url = ApplicationConstant.Mark_Msg_Read_URL1+taskId+ApplicationConstant.Mark_Msg_Read_URL2+msgID+"/markAsRead";
		PostMethod post = new PostMethod(url);
		Header contenHeader = new Header();
		contenHeader.setName("Accept");
		contenHeader.setValue("application/json");
		Header contenHeader2 = new Header();
		contenHeader2.setName("Content-Type");
		contenHeader2.setValue("application/json");
		post.addRequestHeader(contenHeader);
		post.addRequestHeader(contenHeader2);
		Header xtoken = new Header();
		xtoken.setName("X-TOKEN");
		xtoken.setValue(
				ApplicationUtil.getPreference(ApplicationConstant.xtoken, context));
		post.addRequestHeader(xtoken);

		if (request != null) {
			post.setRequestBody(request);
		}

		//System.out.println("update Target called call URL--->" + url);
		// execute method and handle any error responses.
		String ldtDTO = null;
		try {
			HttpClient client = new HttpClient();
			client.getHttpConnectionManager().getParams().setConnectionTimeout(ApplicationConstant.CONNECTION_TIME_OUT);
			client.getHttpConnectionManager().getParams().setSoTimeout(ApplicationConstant.READ_TIME_OUT);
			client.executeMethod(post);
			String in = post.getResponseBodyAsString();
			client.getHttpConnectionManager().getParams().setConnectionTimeout(3000);
			client.getHttpConnectionManager().getParams().setSoTimeout(3000);
			//System.out.println("update Target called inputStream.." + in);

			if (in.equals("") == false) {
				ldtDTO = JSONFactory.getInstance(context).parseMarkMsgResponse(in.toString());
				return ldtDTO;
			}
		} catch (HttpException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		}
		return ldtDTO;


	}

	/*@Override
	public void validateNum(String mobileNo) throws Exception {
			// TODO Auto-generated method stub
			String url = ApplicationConstant.Validation_URL+mobileNo;
			//System.out.println("Validation URL----"+url);
			GetMethod get = new GetMethod(url);
			HttpClient client = new HttpClient();
			client.executeMethod(get);
			String OTP = get.getResponseBodyAsString();
			//System.out.println("OTP FOR---->"+mobileNo+"--"+OTP);
		}*/

	@Override
	public CheckRegistrationDTO checkRegistrationStatus(String mobileno) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println(" Check registration called BGConnectorImpl");
		String dataValue = "{\"request\":{\"mobileNumber\":\""+mobileno+"\"}}";
		//System.out.println(dataValue);
		return checkRegistrationStatusWebservice(dataValue);
	
	}
	
	private CheckRegistrationDTO checkRegistrationStatusWebservice(String request)	throws Exception {

		// Logger object used to log messages for exception.


		String url = ApplicationConstant.checkRegistarationStatus;
		PostMethod post = new PostMethod(url);
		if (request != null && request.length()>0) {

			//System.out.println("registration request--->"+request);
			post.setRequestBody(request);
			Header contenHeader = new Header();
			contenHeader.setName("Accept");
			contenHeader.setValue("application/json");
			Header contenHeader2 = new Header();
			contenHeader2.setName("Content-Type");
			contenHeader2.setValue("application/json");
			post.addRequestHeader(contenHeader);
			post.addRequestHeader(contenHeader2);
			Header xtoken = new Header();
			xtoken.setName("X-TOKEN");
			xtoken.setValue(
					ApplicationUtil.getPreference(ApplicationConstant.xtoken, context));
			post.addRequestHeader(xtoken);


		}
			//client.setConnectionTimeout(55000);
		//System.out.println("check registration called call URL--->" + url);
		// execute method and handle any error responses.
		CheckRegistrationDTO checkldtDTO = null;
		try {
			HttpClient client = new HttpClient();
			client.getHttpConnectionManager().getParams().setConnectionTimeout(ApplicationConstant.CONNECTION_TIME_OUT);
			client.getHttpConnectionManager().getParams().setSoTimeout(ApplicationConstant.READ_TIME_OUT);
			client.executeMethod(post);
		
			String in = post.getResponseBodyAsString();
			//System.out.println("registration called inputStream.." + in);

			if (in.equals("") == false) {
				checkldtDTO = JSONFactory.getInstance(context).parseCheckRegistrationResponce(in.toString());
				return checkldtDTO;
			}
		} catch (HttpException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		}
		return checkldtDTO;


	}
	
	@Override
	public Task getTask(String mobileNumber, String lastUpdateDate,Context context,String deviceToken) {
		Task task=null;
		try {
			RestClient client=new RestClient(ApplicationConstant.Sync_Task_List_URL);
			client.AddHeader("Content-Type", "application/json");
			client.AddHeader("Accept", "application/json");
			client.AddHeader("X-TOKEN", deviceToken);
			String postData=" {\"request\": {\"mobileNumber\": \""+mobileNumber+"\",\"lastUpdateTime\":\""+lastUpdateDate+"\"}}";
			client.setPostData(postData);
			if(ApplicationUtil.getPreference(ApplicationConstant.LAST_UPDATED_DATE,context)==null){
				client.setSoTimeOut(1000*60*3);
			}else{
				client.setSoTimeOut(1000*30);
			}
			
			client.Execute(RequestMethod.POST);
			String str=client.getResponse();
			task=CommonUtil.getTaList(str,context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return task;
	}
	@Override
	public Task getRecievedTask(String mobileNumber, String lastUpdateDate) {
		Task task=null;
		try {
			RestClient client=new RestClient(ApplicationConstant.Sync_Task_List_URL);
			client.AddHeader("Content-Type", "application/json");
			client.AddHeader("Accept", "application/json");
			String postData=" {\"request\": {\"mobileNumber\": \""+mobileNumber+"\",\"lastUpdateTime\":\""+lastUpdateDate+"\"}}";
			client.setPostData(postData);
			client.setSoTimeOut(ApplicationConstant.READ_TIME_OUT);
			client.Execute(RequestMethod.POST);
			String str=client.getResponse();
			JSONObject taskObject=new JSONObject(str);
			
			
			
			task=CommonUtil.getTaList(str,context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return task;
	}

	@Override
	public void registerDevice(String mobileNo, String deviceToken,String platform) throws Exception {
		Task task=null;
		try {
			RestClient client=new RestClient(ApplicationConstant.pushNotification_URL);
			client.AddHeader("Content-Type", "application/json");
			client.AddHeader("Accept", "application/json");
			String postData=" {\"request\": {\"mobileNumber\": \""+mobileNo+"\",\"deviceToken\":\""+deviceToken+"\",\"platForm\":\""+platform+"\"}}";
			client.AddHeader("X-TOKEN",ApplicationUtil.getPreference(ApplicationConstant.xtoken,context));
			client.setPostData(postData);
			client.setSoTimeOut(ApplicationConstant.READ_TIME_OUT);
			client.Execute(RequestMethod.POST);
			String str=client.getResponse();
			JSONObject taskObject=new JSONObject(str);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public boolean markMessageAsRead(String taskId, String mobileNumber,String taskTo,String token) throws Exception {
		try {
			RestClient client=new RestClient(ApplicationConstant.BASE_URL+"/task-manager-api/task/"+taskId+"/markMessageAsRead");
			client.AddHeader("Content-Type", "application/json");
			client.AddHeader("Accept", "application/json");
			String postData=" {\"request\": {\"mobileNumber\": \""+mobileNumber+"\",\"taskId\":\""+taskId+"\"}}";
			client.AddHeader("X-TOKEN",token);
			client.setPostData(postData);
			client.Execute(RequestMethod.POST);
			String str=client.getResponse();
			JSONObject taskObject=new JSONObject(str);
			JSONObject responseObj = taskObject.getJSONObject("response");
			String status = responseObj.getString("status");
			if("00".equalsIgnoreCase(status)){
				return true;
			}else{
				return false;
			}
			} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean markTaskAsRead(String taskId, String mobileNumber,String taskTo, String token)
			throws Exception {
		try {
			RestClient client=new RestClient(ApplicationConstant.BASE_URL+"/task-manager-api/task/"+taskId+"/markTaskAsRead");
			client.AddHeader("Content-Type", "application/json");
			client.AddHeader("Accept", "application/json");
			String postData=" {\"request\": {\"mobileNumber\": \""+mobileNumber+"\",\"taskId\":\""+taskId+"\"}}";
			client.AddHeader("X-TOKEN",token);
			client.setPostData(postData);
			client.Execute(RequestMethod.POST);
			String str=client.getResponse();
			JSONObject taskObject=new JSONObject(str);
			JSONObject responseObj = taskObject.getJSONObject("response");
			String status = responseObj.getString("status");
			if("00".equalsIgnoreCase(status)){
				return true;
			}else{
				return false;
			}
			
			} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
		
	}
	
	public BGConnectorImpl() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean updateTaskContent(String mobileNo, String deviceToken,
			String taskId, String description) throws Exception {
		try {
			RestClient client=new RestClient(ApplicationConstant.BASE_URL+"/task-manager-api/task/"+taskId+"/updateDescription");
			client.AddHeader("Content-Type", "application/json");
			client.AddHeader("Accept", "application/json");
			String postData=" {\"request\": {\"mobileNumber\": \""+mobileNo+"\",\"taskId\":\""+taskId+"\",\"taskDescription\":\""+description+"\"}}";
			client.AddHeader("X-TOKEN",deviceToken);
			client.setPostData(postData);
			client.Execute(RequestMethod.POST);
			String str=client.getResponse();
			JSONObject taskObject=new JSONObject(str);
			JSONObject responseObj = taskObject.getJSONObject("response");
			String status = responseObj.getString("status");
			if("00".equalsIgnoreCase(status)){
				return true;
			}else{
				return false;
			}
			
			} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
		
	}
	@Override
	public boolean checkRegStatusForUsers(List<Contact> contacts)throws Exception {
	
		JSONObject checkStatus=new JSONObject();
		JSONArray contactList=new JSONArray();
		for (Contact contact : contacts) {
			JSONArray numbers=new JSONArray();
			JSONObject values=new JSONObject();
			numbers.put(contact.getNumber());
			values.put("mobilenumbers", numbers);
			values.put("regstatus", "N");
			contactList.put(values);
		}
		checkStatus.put("contactList", contactList);
		
		String request=checkStatus.toString();
		try {
			RestClient client=new RestClient(ApplicationConstant.BASE_URL+"/task-manager-api/registration/users/status");
			client.AddHeader("Content-Type", "application/json");
			client.AddHeader("Accept", "application/json");
			client.AddHeader("X-TOKEN",ApplicationUtil.getPreference(ApplicationConstant.xtoken,context));
			client.setPostData(request);
			client.Execute(RequestMethod.POST);
			String str=client.getResponse();
			RegistrationStatus registrationStatus=JSONUtil.getJavaObject(str,RegistrationStatus.class);
			List<ContactStatus> contactStatus=	registrationStatus.getContactList();
			for (ContactStatus contactStatus2 : contactStatus) {
				ContentValues initialValues = new ContentValues();
				for (String number : contactStatus2.getMobilenumbers()) {
					DBAdapter dbAdapter=DBAdapter.getInstance(context);
					dbAdapter.openDataBase();
					initialValues.put("REG_STATUS", contactStatus2.getRegstatus());
		//			dbAdapter.updateRecord("USERS_CONTACT", initialValues,"MOBILE_NUMBER='" + number + "'", null);
					dbAdapter.close();
				}
				
			}
			
			if("00".equalsIgnoreCase(registrationStatus.getStatus())){
				return true;
			}else{
				return false;
			}
			
			} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	@Override
	public Task sync(String mobileNumber, String tasIds, Context context,String deviceToken) {
		
		Task task=null;
		try {
			RestClient client=new RestClient(ApplicationConstant.Sync_URL);
			client.AddHeader("Content-Type", "application/json");
			client.AddHeader("Accept", "application/json");
			client.AddHeader("X-TOKEN", deviceToken);
			String postData=" {\"request\": {\"mobileNumber\": \""+mobileNumber+"\",\"taskIds\":\""+tasIds+"\",\"lastUpdateTime\":\""+ApplicationUtil.getPreference(ApplicationConstant.LAST_UPDATED_DATE,context)+"\"}}";
			client.setPostData(postData);
			client.setSoTimeOut(1000*60*3);
			client.Execute(RequestMethod.POST);
			String str=client.getResponse();
			task=CommonUtil.getTaList(str,context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return task;
	}
	
	@Override
	public boolean deleteTask(String taskId, String mobileNumber,Context context,String deviceToken)throws Exception {
		try {
			RestClient client=new RestClient(ApplicationConstant.BASE_URL+"/task-manager-api/task/"+taskId+"/close");
			client.AddHeader("Content-Type", "application/json");
			client.AddHeader("Accept", "application/json");
			String postData=" {\"request\": {\"mobileNumber\": \""+mobileNumber+"\",\"taskId\":\""+taskId+"\"}}";
			client.AddHeader("X-TOKEN",deviceToken);
			client.setPostData(postData);
			client.Execute(RequestMethod.POST);
			String str=client.getResponse();
			JSONObject taskObject=new JSONObject(str);
			JSONObject responseObj = taskObject.getJSONObject("response");
			String status = responseObj.getString("status");
			if("00".equalsIgnoreCase(status)){
				return true;
			}else{
				return false;
			}
			
			} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	@Override
	public boolean updateExchangeServerInfo(MsExchangeServerInfo serverInfo ,Context context,String deviceToken)throws Exception {
		try {
			RestClient client=new RestClient(ApplicationConstant.BASE_URL+"/task-manager-api/task/"+serverInfo.getEmaild()+"/close");
			client.AddHeader("Content-Type", "application/json");
			client.AddHeader("Accept", "application/json");
			String postData=" {\"request\": {\"mobileNumber\": \""+serverInfo.getMobileNumber()+"\",\"taskId\":\""+serverInfo.getPassword()+"\"}}";
			client.AddHeader("X-TOKEN",deviceToken);
			client.setPostData(postData);
			client.Execute(RequestMethod.POST);
			String str=client.getResponse();
			JSONObject taskObject=new JSONObject(str);
			JSONObject responseObj = taskObject.getJSONObject("response");
			String status = responseObj.getString("status");
			if("00".equalsIgnoreCase(status)){
				return true;
			}else{
				return false;
			}
			
			} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean getContactsListfromDB(List<Contact> contacts)throws Exception {
	
		
		for (Contact contact : contacts) {
			String number  = contact.getNumber();
			System.out.println("Here are the numbers ... " + number);
		}
	
	return false;
	}
	
	@Override
	public String addLaterTask(String taskId, String mobilNumber) {
		
		String request = "{\"request\":{\"taskId\":\""+taskId+"\",\"mobileNumber\":\""+mobilNumber+"\"}}";
		String ldtDTO = null;
		String url = ApplicationConstant.Set_Favouraite_Task_URL+taskId+"/addTaskLater";
		try {
		PostMethod post = new PostMethod(url);
		Header contenHeader = new Header();
		contenHeader.setName("Accept");
		contenHeader.setValue("application/json");
		Header contenHeader2 = new Header();
		contenHeader2.setName("Content-Type");
		contenHeader2.setValue("application/json");
		post.addRequestHeader(contenHeader);
		post.addRequestHeader(contenHeader2);
		Header xtoken = new Header();
		xtoken.setName("X-TOKEN");
		xtoken.setValue(ApplicationUtil.getPreference(ApplicationConstant.xtoken, context));
		post.addRequestHeader(xtoken);
		if (request != null) {
			post.setRequestBody(request);
		}
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(ApplicationConstant.CONNECTION_TIME_OUT);
		client.getHttpConnectionManager().getParams().setSoTimeout(ApplicationConstant.READ_TIME_OUT);
		//System.out.println("update Fav called call URL--->" + url);
		client.executeMethod(post);
		String in = post.getResponseBodyAsString();
		//System.out.println("update Fav called inputStream.." + in);

			if (in.equals("") == false) {
				ldtDTO = JSONFactory.getInstance(context).parseUpdateFavResponse(in.toString());
				return ldtDTO;
			}
		} catch (HttpException e) {
			e.printStackTrace();
			ldtDTO=null;

		} catch (IOException e) {
			ldtDTO=null;
			e.printStackTrace();

		}
		return ldtDTO;


	}
	
	@Override
	public String removeLaterTask(String taskId, String mobilNumber) {
		
		String request = "{\"request\":{\"taskId\":\""+taskId+"\",\"mobileNumber\":\""+mobilNumber+"\"}}";
		String ldtDTO = null;
		String url = ApplicationConstant.Set_Favouraite_Task_URL+taskId+"/removeTaskLater";
		try {
		PostMethod post = new PostMethod(url);
		Header contenHeader = new Header();
		contenHeader.setName("Accept");
		contenHeader.setValue("application/json");
		Header contenHeader2 = new Header();
		contenHeader2.setName("Content-Type");
		contenHeader2.setValue("application/json");
		post.addRequestHeader(contenHeader);
		post.addRequestHeader(contenHeader2);
		Header xtoken = new Header();
		xtoken.setName("X-TOKEN");
		xtoken.setValue(ApplicationUtil.getPreference(ApplicationConstant.xtoken, context));
		post.addRequestHeader(xtoken);
		if (request != null) {
			post.setRequestBody(request);
		}
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(ApplicationConstant.CONNECTION_TIME_OUT);
		client.getHttpConnectionManager().getParams().setSoTimeout(ApplicationConstant.READ_TIME_OUT);
		//System.out.println("update Fav called call URL--->" + url);
		client.executeMethod(post);
		String in = post.getResponseBodyAsString();
		//System.out.println("update Fav called inputStream.." + in);

			if (in.equals("") == false) {
				ldtDTO = JSONFactory.getInstance(context).parseUpdateFavResponse(in.toString());
				return ldtDTO;
			}
		} catch (HttpException e) {
			e.printStackTrace();
			ldtDTO=null;

		} catch (IOException e) {
			ldtDTO=null;
			e.printStackTrace();

		}
		return ldtDTO;


	}
}
	