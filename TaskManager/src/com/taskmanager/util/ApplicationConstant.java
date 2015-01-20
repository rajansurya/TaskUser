package com.taskmanager.util;





/**
 * 
 * ApplicationUtil defines various utilities functionality used through out the application  
 * 
 * @version 1.0
 *
 */ 

public class ApplicationConstant {
	public static String PREMIUM="FREE";
	public static String BUILD_VERSION="1.0";
	public static String TERMS_URL="http://massigner.com/TermsPrivacy.htm";
	public static  final int CONNECTION_TIME_OUT=7000;
	public static  final int READ_TIME_OUT=7000;
	public static String XToken = ""; // token for user received from service
	public static String MobileNo = ""; // registered mobile no.
	public static String ApplicantName = ""; // registered Name.
	public static String LAST_UPDATED_DATE = "lastUpdated";
	public static String OTP = ""; // OTP received while registering
	public static int SYNC_SLEEPTIME =15000000; // sync time in seconds
	public static boolean STOP_SERVICE_FLAG = false;
	public static boolean SYNC_FLAG = false; // sync status
//	public static boolean SYNC_SERVICE_FLAG = true;
	public static boolean Is_First = true; // check for first time sync
	public static String SYNC_SERVICE_STATUS = ""; // sync Service status
	public static String REG_STATUS = "REG_STATUS"; 

	public static boolean STOP_SYNC_THREAD = true; //this flag is used to stop service thread after service is destroyed
	public static String BASE_URL="http://massigner.com";
	public static String SUPPORT_URL="http://massigner.com/mobile/support.html";
	//public static String BASE_URL="http://192.168.5.36:7777";

//	public static boolean CREATE_TASK_CALLED = false; 
	
	public static int createTask_Flag_run_handle=0;
	public static int message_Task_Flag_run_handle=0;
	public static boolean flag_Ui_Refresh = false;//flag is used to refresh UI after sync
	public static String checkRegistrationStatus = "";
	//public static SharedPreferences pref = null;
	public static String xtoken = "xtoken";
	public static String regMobNo = "regmbNo";
	public static String EMAIL_ID = "emailId";
	public static String REG_NAME = "regName";
	public static String regId = "regId";
	public static String url_term = "http://www.google.com";
	public static final String ASSIGNER_NUMBER="9999999999";
	public static final String ASSIGNER_NAME="mAssigner";


	//live urls
	public static String Sync_Message_Task_URL = BASE_URL+"/task-manager-api/task/syncTaskAndMessage";
	public static String Registration_URL  = BASE_URL+"/task-manager-api/registration";
	public static String Recieve_OTP_URL =BASE_URL+"/task-manager-api/otp/";
	public static String Validate_OTP_URL =BASE_URL+"/task-manager-api/otp/validate";
	public static String Request_OTP_URL = BASE_URL+"/task-manager-api/otp/generate";
	public static String Resend_OTP_URL = BASE_URL+"/task-manager-api/otp/generate";
	public static String Create_Task_URL = BASE_URL+"/task-manager-api/task/create";
	public static String Sync_Task_List_URL = BASE_URL+"/task-manager-api/task/fetchAllTaskAndMessage";
	public static String Sync_URL = BASE_URL+"/task-manager-api/task/sync";
	public static String Sync_Self_Task_List_URL = BASE_URL+"/task-manager-api/task/self";
	public static String Sync_Assigned_Task_List_URL = BASE_URL+"/task-manager-api/task/assigned";
	public static String Sync_Received_Task_List_URL = BASE_URL+"/task-manager-api/task/recieved";
	public static String Update_Task_URL = BASE_URL+"/task-manager-api/task/";
	public static String Update_Target_Date= BASE_URL+"/task-manager-api/task/";
	
	public static String Set_Reminder_Task_URL = BASE_URL+"/task-manager-api/task/";
	public static String Set_Favouraite_Task_URL = BASE_URL+"/task-manager-api/task/";
	
	public static String Set_ADD_LATER_TASK_URL = BASE_URL+"/task-manager-api/task/";
	
	public static String Set_RMOVE_LATER_TASK_URL = BASE_URL+"/task-manager-api/task/";
	public static String Set_Priority_Task_URL = BASE_URL+"/task-manager-api/task/";
	public static String Set_Status_Task_URL = BASE_URL+"/task-manager-api/task/";
	public static String Message_List_URL = BASE_URL+"/task-manager-api/task/";
	public static String Create_Message_URL = BASE_URL+"/task-manager-api/task/";
	public static String Mark_Msg_Read_URL1 = BASE_URL+"/task-manager-api/task/";
	public static String Mark_Msg_Read_URL2 = "/message/";
	//public static String Validation_URL =BASE_URL+"/task-manager-api/autenticate/test/";
	public static String changeAssignee = BASE_URL+"/task-manager-api/task/";
	public static String checkRegistarationStatus=BASE_URL+"/task-manager-api/registration/status";
	//public static String checkStatus=BASE_URL+"/task-manager-api/autenticate/test/";
	//public static String pushNotification_URL = BASE_URL+"/task-manager-api/registerDevice/android";
	public static String pushNotification_URL = BASE_URL+"/task-manager-api/pushNotification/registerDevice/android";
	
	public static String markMessageAsRead = BASE_URL+"/task-manager-api/pushNotification/registerDevice/android";
	
	public static String markTaskAsRead = BASE_URL+"/task-manager-api/pushNotification/registerDevice/android";

	
	//========================================================
	
	
	public static  String dialogTitle = "Task Manager";
	public  static String dialogMsg ="Entered text is not valid.";
	
	// G task auth url 
	 public static final String SCOPE = "oauth2:https://www.googleapis.com/auth/tasks";
	 
	 public static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
	 public    static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;
	 public   static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;
//http://massigner.com/

}
