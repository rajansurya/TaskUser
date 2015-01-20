package com.taskmanager.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.WeakHashMap;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.SQLException;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.taskmanager.app.R;
import com.taskmanager.background.SyncModule;
import com.taskmanager.database.DBAdapter;
import com.taskmanager.dto.TaskInfoEntity;
import com.taskmanager.ui.AllTaskActivity;
import com.taskmanager.webservice.BGConnector;
import com.taskmanager.webservice.BGConnectorImpl;

/**
 * Class name : ApplicationUtil This utility class have common functions used in
 * application
 * 
 * @version 1.0
 */
public class ApplicationUtil {
	private static  WeakHashMap<String, String> contactMap=new WeakHashMap<String, String>();

	private static ApplicationUtil instance = null;
	private BGConnector syncService = null;
	public static final String DB_PATH = Environment.getDataDirectory()
			+ File.separator + "data" + File.separator + "com.taskmanager"
			+ File.separator + "databases" + File.separator
			+ "taskmanager.sqlite";

	public static byte[] ImageArray;

	/**
	 * used to get instance of application
	 * 
	 * @return
	 */
	public static ApplicationUtil getInstance() {
		if (instance == null) {
			instance = new ApplicationUtil();
		}
		return instance;
	}

	// Used for call webservices
	public BGConnector getSyncServer(Context context) {
		if (syncService == null)
			syncService = new BGConnectorImpl(context);
		//System.out.println("in side util...............");
		return syncService;
	}

	/**
	 * method to check Internet connectivity
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean checkInternetConn(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			if (cm.getActiveNetworkInfo() != null
					&& cm.getActiveNetworkInfo().isAvailable()
					&& cm.getActiveNetworkInfo().isConnected()) {
					return true;

			} else {
				//System.out.println("Network Available------ > False");
				return false;
			}
		} catch (NullPointerException e) {
			;

			return false;
		} catch (Exception e) {
			;

			return false;
		}
	}

	// Function for fetching all record list from DB
	public ArrayList<ArrayList<String>> uploadListFromDB(String table,
			String[] colm, String where, Context context) {

		DBAdapter dbAdapter = DBAdapter.getInstance(context);
		try {
			//System.out.println("db opend-------------->");
			ArrayList<ArrayList<String>> listDBElemnt = dbAdapter
					.selectRecordsFromDBList(table, colm, where, null, null,
							null, null);
			//System.out.println("'--RESCCCCCCCCCCCCCCORDDDDD FOUND");
			// //System.out.println("listDBElemnt.size():-------? "
			// + listDBElemnt.size());

			return listDBElemnt;
		} catch (Exception e) {

			;
			//System.out.println("Exception is =" + e);

			return null;
		}

	}

	public Map getTaskList(String query, Context context) {
		HashMap entities = new HashMap();
		DBAdapter dbAdapter = DBAdapter.getInstance(context);

		try {
			dbAdapter.openReadDataBase();
			entities = (HashMap) dbAdapter.getTaskList(query, context);

		} catch (Exception exception) {

		} finally {
			dbAdapter.close();

		}
		return entities;
	}

	public Map getTaskListClose(String query, Context context) {
		HashMap entities = new HashMap();
		DBAdapter dbAdapter = DBAdapter.getInstance(context);

		try {
			dbAdapter.openReadDataBase();
			entities = (HashMap) dbAdapter.getTaskListClose(query, context);

		} catch (Exception exception) {

		} finally {
			if(dbAdapter!=null)
			dbAdapter.close();

		}
		return entities;
	}

	// Function for fetching all record list from DB by query
	public ArrayList<ArrayList<String>> selectListFromQuery(String query,
			Context context) {

		DBAdapter dbAdapter = DBAdapter.getInstance(context);
		try {
			try {
				dbAdapter.openDataBase();

			} catch (Exception e) {
				// TODO: handle exception

				;

			}
			ArrayList<ArrayList<String>> listDBElemnt = dbAdapter
					.selectRecordsFromDBArrayList(query);
			for (int i = 0; i < listDBElemnt.size(); i++) {
				ArrayList<String> rowElem = listDBElemnt.get(i);
				for (int j = 0; j < rowElem.size(); j++) {
				}
			}

			return listDBElemnt;
		} catch (SQLException e) {

			;
			return null;
		} finally {
			if(dbAdapter!=null)
			dbAdapter.close();
		}

	}

	public List<TaskInfoEntity> selectListFromQueryNew(String query,
			Context context) {
		ArrayList<TaskInfoEntity> listDBElemnt = null;
		DBAdapter dbAdapter = DBAdapter.getInstance(context);
		try {
			try {
				dbAdapter.openDataBase();

			} catch (Exception e) {
				// TODO: handle exception

				;

			}
			listDBElemnt = dbAdapter.selectRecordsFromDBArrayListNew(query);
		} catch (SQLException e) {

			;
			return null;
		} finally {
			if(dbAdapter!=null)
			dbAdapter.close();
		}
		return listDBElemnt;

	}

	public Map selectContactListFromQueryNew(String query, Context context) {
		HashMap listDBElemnt = null;
		DBAdapter dbAdapter = DBAdapter.getInstance(context);
		try {
			try {
				dbAdapter.openDataBase();

			} catch (Exception e) {
				// TODO: handle exception

				;

			}
			listDBElemnt = (HashMap) dbAdapter.getCreateContactList(query,
					context);
		} catch (SQLException e) {

			;
			return null;
		} finally {
			if(dbAdapter!=null)
			dbAdapter.close();
		}
		return listDBElemnt;

	}

	public Map selectListFromQueryNewJunk(String query, Context context) {
		Map listDBElemnt = null;
		DBAdapter dbAdapter = DBAdapter.getInstance(context);
		try {
			try {
				dbAdapter.openDataBase();

			} catch (Exception e) {
				// TODO: handle exception

				;

			}
			listDBElemnt = dbAdapter.selectRecordsFromDBArrayListJunk(query,
					context);
		} catch (SQLException e) {

			;
			return null;
		} finally {
			if(dbAdapter!=null)
			dbAdapter.close();
		}
		return listDBElemnt;

	}

	/**
	 * method to execute raw query
	 * 
	 * @param query
	 * @param context
	 * @return ArrayList<ArrayList<String>
	 * @throws Exception
	 */
	public ArrayList<ArrayList<String>> executeQuery(String query,
			Context context) throws Exception {

		ArrayList<ArrayList<String>> listDBElemnt = null;
		DBAdapter dbAdapter = DBAdapter.getInstance(context);

		try {
			dbAdapter.openDataBase();

			// dbAdapter.openDataBase();
			
			listDBElemnt = dbAdapter.exceuteQueryWithException(query);
			// whereClase, whereArgs, groupBy, having, orderBy)
			//System.out.println("'--RESCCCCCCCCCCCCCCORDDDDD FOUND");
			//System.out.println("listDBElemnt.size():" + listDBElemnt.size());
			for (int i = 0; i < listDBElemnt.size(); i++) {
				ArrayList<String> rowElem = listDBElemnt.get(i);
				for (int j = 0; j < rowElem.size(); j++) {
					// //System.out.println("'------->DB------" + i + "-"+
					// rowElem.get(j));
				}
			}
		} catch (Exception e) {
			// TODO: handle exception

			;

		} finally {
			if(dbAdapter!=null)
			dbAdapter.close();
		}

		// dbAdapter.close();
		return listDBElemnt;

	}

	// funtion for inserting the data in the table
	public long saveDataInDB(String tableName, String nullColumnHack,
			ContentValues initialValues, Context context) {
		long status = -1;
		DBAdapter dbAdapter = DBAdapter.getInstance(context);
		try {
			try {
				dbAdapter.openDataBase();

			} catch (Exception e) {
				// TODO: handle exception

				;

			}

			// dbAdapter.openDataBase();
			//System.out.println("insert called-------------->");

			//System.out.println("db opend-------------->");
			status = dbAdapter.insertRecordsInDB(tableName, nullColumnHack,initialValues);
			if(status==-1){
				status = dbAdapter.insertRecordsInDB(tableName, nullColumnHack,initialValues);
			}
			//dbAdapter.close();
			//System.out.println("total inserted rows-------------->" + status);
			return status;
		} catch (SQLException e) {

			status = -1;
			;

		} finally {
			//System.out.println("save close in DB finally");
			if(dbAdapter!=null)
			dbAdapter.close();
		}

		return status;
	}

	// SAVE DEADLINE DATA IN DATABASE *************

	/*
	 * public void saveDeadlineData(taskListDTO taskListResponse, Context
	 * context) {
	 * 
	 * DBAdapter dbAdapter = DBAdapter.getInstance(context); try {
	 * //System.out.println("upload called-------------->"); try {
	 * dbAdapter.openDataBase();
	 * 
	 * } catch (Exception e) { // TODO: handle exception
	 * 
	 * ;
	 * 
	 * }
	 * 
	 * // dbAdapter.openDataBase();
	 * //System.out.println("insert called-------------->");
	 * 
	 * //System.out.println("db opend-------------->");
	 * 
	 * // String deadlineTable = //
	 * "CREATE TABLE 'Deadline' ( ' , 'IsReminderSync' VARCHAR  DEFAULT 'Y', 'IsTaskUpdateSync' VARCHAR  DEFAULT 'Y', 'IsTaskSync' VARCHAR  DEFAULT 'Y', 'IsJunk' VARCHAR, 'AlarmTime' VARCHAR, 'Group_ID' VARCHAR,  'IsAssigneeSync' VARCHAR DEFAULT 'Y',  'IsTargetSync' VARCHAR DEFAULT 'Y',  'TotalMessage' VARCHAR DEFAULT 0,  'UnreadMessage' VARCHAR DEFAULT 0,  'IsTaskRead' VARCHAR DEFAULT 'Y',  'ClosedBy' VARCHAR,'IsDeadlineDisplay' VARCHAR )"
	 * ; String column = "Task_ID,Assign_From,Assign_From_Name,Assign_To," +
	 * "Assign_To_Name,Task_Desc,Priority,Priority_Desc,Closure_Date," +
	 * "Target_Date,Fire_Flag,Reminder_Time,IsReminder,IsMessage,Status,IsFavouraite,"
	 * +
	 * "TaskType,CreationDate,IsFAvSync,IsPrioritySync,IsStatusSync,IsReminderSync,IsTaskUpdateSync,"
	 * + "IsTaskSync,IsJunk,AlarmTime,Group_ID,IsAssigneeSync,IsTargetSync," +
	 * "TotalMessage,UnreadMessage,IsTaskRead,ClosedBy,IsDeadlineDisplay";
	 * 
	 * String sql = "INSERT OR REPLACE INTO Deadline ( "+
	 * column+" ) VALUES ( "+"'"
	 * +taskListResponse.getTaskId()+"' ,"+"'"+taskListResponse
	 * .getAssignFrom()+"' ,"+"'"+taskListResponse.getAssignFromName()+"' ,"
	 * +"'"
	 * +taskListResponse.getAssignTo()+"' ,"+"'"+taskListResponse.getAssignToName
	 * ()+"' ,"+"'"+taskListResponse.getTaskDesc()+"' ,"+"'"+taskListResponse.
	 * getPriority()+
	 * "' ,"+"'"+taskListResponse.getPriorityDesc()+"' ,"+"'"+taskListResponse
	 * .getClosureDateTime
	 * ()+"' ,"+"'"+taskListResponse.getTargetDateTime()+"' ,"
	 * +"'"+taskListResponse
	 * .getFireFlag()+"' ,"+"'"+taskListResponse.getReminderTime
	 * ()+"' ,"+"'"+taskListResponse
	 * .getIsReminder()+"' ,"+"'"+taskListResponse.getIsMessage()+"' ," +
	 * "'"+taskListResponse
	 * .getStatus()+"' ,"+"'"+taskListResponse.getFavouraite(
	 * )+"' ,"+"'"+taskListResponse
	 * .getTaskType()+"' ,"+"'"+taskListResponse.getCreationDate()+"' ,"
	 * +"'"+taskListResponse.getf+"' ,"
	 * 
	 * +")";
	 * 
	 * // dbAdapter.insertRecordsDeadlineDb("Deadline", sql);
	 * 
	 * // //System.out.println("total inserted rows-------------->"+status);
	 * 
	 * } catch (Exception e) {
	 * 
	 * // status=-1; // ;
	 * 
	 * } finally { // //System.out.println("save close in DB finally");
	 * dbAdapter.close(); }
	 * 
	 * }
	 */

	// ***************

	/**
	 * method to delete specific row from table
	 * 
	 * @param tablename
	 * @param where
	 * @param context
	 */
	public void deleteRowInTable(String tablename, String where, Context context) {
		DBAdapter dbAdapter = DBAdapter.getInstance(context);
		try {
			// //System.out.println("upload called-------------->");
			try {
				dbAdapter.openDataBase();

			} catch (Exception e) {
				// TODO: handle exception

				;

			}

			// dbAdapter.openDataBase();

			String tbl_1 = "DELETE FROM " + tablename + " " + where;
			//System.out.println("delete quesry------------> " + tbl_1);
			// //System.out.println("delete Query  "+tbl_1);
			dbAdapter.executeSQL(tbl_1);

		} catch (SQLException e) {

			;
		} finally {
			//System.out.println("deleterowintable finally called");
			if(dbAdapter!=null)
			 dbAdapter.close();
		}
	}

	/**
	 * method to get current time in UTC
	 * 
	 * @return Timestamp
	 */
	public static long getCurrentUtcTime() {

		return new Date().getTime();
	}

	/**
	 * Get Current unix time
	 * 
	 * @return time in seconds
	 */
	public static long getCurrentUnixTime() {

		return System.currentTimeMillis() / 1000L;
	}

	/**
	 * method to return date in ( dd-MMMM-yyyy|| HH:mm) from given timestamp
	 * 
	 * @param date
	 * @return String Date
	 */
	public static String changeToDayMonthYear(long date) {

		//System.out.println("long date ius-----> " + date);
		DateFormat formatter = new SimpleDateFormat("dd-MMMM-yyyy|| HH:mm");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date * 1000);
		//System.out.println("Date is----> "+ formatter.format(calendar.getTime()));
		return formatter.format(calendar.getTime());
	}

	/**
	 * method to return date in ( dd/MM/yyyy||HH:mm) from given timestamp
	 * 
	 * @param date
	 * @return String Date
	 */
	public static String changeDateTime2(long date) {

		//System.out.println("long date ius-----> " + date);
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy||HH:mm");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date * 1000);
		return formatter.format(calendar.getTime());
	}

	/**
	 * method to return date in (dd MMM, yyyy||HH:mm) from given timestamp
	 * 
	 * @param date
	 * @return String Date
	 */
	public static String changeDateTime3(long date) {

		//System.out.println("long date ius-----> " + date);
		DateFormat formatter = new SimpleDateFormat("dd MMM, yyyy||HH:mm");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date * 1000);
		//System.out.println("Date is----> "+ formatter.format(calendar.getTime()));
		return formatter.format(calendar.getTime());
	}

	/**
	 * method to return date in (ddth, MMM HH:mm) from given timestamp
	 * 
	 * @param date
	 * @return String Date
	 */
	public static String changeDateTime4(long date) {

		//System.out.println("long date ius-----> " + date);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date * 1000);
		int day = Calendar.DAY_OF_MONTH;
		//String dateSuffix = getDayOfMonthSuffix(day);
		String dateSuffix = "";
		DateFormat formatter = new SimpleDateFormat("dd,, MMM HH:mm");
		String dateReturn = formatter.format(calendar.getTime()).replace(",,",
				dateSuffix);
		//System.out.println("Date is the----> " + dateReturn);
		return dateReturn;
	}

	/**
	 * method to return date in (dd MMM hh:mm a) from given timestamp
	 * 
	 * @param date
	 * @return String Date
	 */
	public static String changeDateTime5(long date) {

		//System.out.println("long date ius-----> " + date);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date * 1000);
		DateFormat formatter = new SimpleDateFormat("dd MMM hh:mm a");
		
		return formatter.format(calendar.getTime());
	}
	
	/**
	 * @param String date
	 * @return date
	 */
	public static String getDateTime6(String date) {
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			Date parsed = dateFormat.parse(date);
			dateFormat.applyPattern("dd MMM");//"dd MMM, yyyy||HH:mm"
			date = dateFormat.format(parsed);
			
		} catch(Exception e){
			Log.e("ApplicationUtil changeDateTime6", e.toString());
		}
		return date;
	}
	
	public static String getDateTime7(String date) {
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			Date parsed = dateFormat.parse(date);
			dateFormat.applyPattern("dd MMM HH:mm");//"dd MMM, yyyy||HH:mm"
			date = dateFormat.format(parsed);
			
		} catch(Exception e){
			Log.e("ApplicationUtil changeDateTime6", e.toString());
		}
		return date;
	}
	
	public static String getDateTime8(String date) {
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
			Date parsed = dateFormat.parse(date);
			dateFormat.applyPattern("dd MMM hh:mm a");//"dd MMM, yyyy||HH:mm"
			date = dateFormat.format(parsed);
			
		} catch(Exception e){
			Log.e("ApplicationUtil changeDateTime6", e.toString());
		}
		return date;
	}

	public static String getDateTime9(int day) {
		DateFormat formatter = new SimpleDateFormat("dd MMM");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, day);
		return formatter.format(calendar.getTime());
	}
	
	public static String getDateTime10(String date) {
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
			Date parsed = dateFormat.parse(date);
			dateFormat.applyPattern("dd MMM, hh:mm a");//"dd MMM, yyyy||HH:mm"
			date = dateFormat.format(parsed);
			
		} catch(Exception e){
			Log.e("ApplicationUtil changeDateTime6", e.toString());
		}
		return date;
	}
	
	public static String getDateTime11(String date) {
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			Date parsed = dateFormat.parse(date);
			dateFormat.applyPattern("dd MMM");
			date = dateFormat.format(parsed);
			
		} catch(Exception e){
			Log.e("ApplicationUtil changeDateTime6", e.toString());
		}
		return date;
	}
	
	public static String getDateTime12(String date) {
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			Date parsed = dateFormat.parse(date);
			dateFormat.applyPattern("dd MMM");
			date = dateFormat.format(parsed);
			
		} catch(Exception e){
			Log.e("ApplicationUtil changeDateTime6", e.toString());
		}
		return date;
	}
	
	public static String getDateTime13(String date) {
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			Date parsed = dateFormat.parse(date);
			dateFormat.applyPattern("hh:mm a");
			date = dateFormat.format(parsed);
			
		} catch(Exception e){
			Log.e("ApplicationUtil changeDateTime6", e.toString());
		}
		return date;
	}
	
	public static String getDateTime14(String date) {
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			Date parsed = dateFormat.parse(date);
			dateFormat.applyPattern("dd MMM hh:mm a");
			date = dateFormat.format(parsed);
			
		} catch(Exception e){
			Log.e("ApplicationUtil changeDateTime6", e.toString());
		}
		return date;
	}
	
	/**
	 * method to return date in ( dd/MM/yyyy HH:mm) from given timestamp
	 * 
	 * @param date
	 * @return String Date
	 */
	public static String changeToDayMonthYear2(long date) {

		//System.out.println("long date ius-----> " + date);
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date * 1000);
		
		return formatter.format(calendar.getTime());
	}

	/**
	 * method to return date in (dd MMM, yyyy) from given timestamp
	 * 
	 * @param date
	 * @return String Date
	 */
	public static String changeToDayMonthYear3(long date) {

		//System.out.println("long date ius-----> " + date);
		DateFormat formatter = new SimpleDateFormat("dd MMM, yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date * 1000);
		
		return formatter.format(calendar.getTime());
	}

	/**
	 * method to return date in ( dd/MM/yyyy HH:mm:ss) from given timestamp
	 * 
	 * @param date
	 * @return String Date
	 */
	public static String changeToDayMonthYear4(long date) {

		//System.out.println("long date ius-----> " + date);
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date * 1000);
		
		return formatter.format(calendar.getTime());
	}

	/**
	 * convert date to timestamp with another format("dd/MM/yyyy HH:mm")
	 * 
	 * @param date
	 * @return timestamp
	 * @throws Exception
	 */
	public static long getUpdatedTimestampFromDate(String date) {
		long value = 0L;
		try {
			SimpleDateFormat smDtfm = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			
			//System.out.println("date is---->" + smDtfm.parse(date).getTime());
			 value=smDtfm.parse(date).getTime() / 1000L;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			;
		}
		return value;
		// return smDtfm.parse(date).getTime();
	}
	
	public static long getTimeStamp(String date) {
		long value = 0L;
		try {
			SimpleDateFormat smDtfm = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			
			
			 value=smDtfm.parse(date).getTime();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			;
		}
		return value;
		// return smDtfm.parse(date).getTime();
	}
	/**
	 * convert date to timestamp with another format("dd/MM/yyyy HH:mm:ss")
	 * 
	 * @param date
	 * @return timestamp
	 * @throws Exception
	 */
	public static long getUpdatedTimestampFromDate3(String date) {
		if (date == null || date.isEmpty()) {
			return 0;
		}
		SimpleDateFormat smDtfm = null;
		long tt = 0;
		try {
			smDtfm = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			//System.out.println("date is---->" + smDtfm.parse(date).getTime());
			tt = smDtfm.parse(date).getTime() / 1000L;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// ;
		}
		return tt;
	}

	/**
	 * convert date to timestamp with another format("dd-MMM-yyyy HH:mm")
	 * 
	 * @param date
	 * @return timestamp
	 * @throws Exception
	 */
	public static long getUpdatedTimestampFromDate2(String date)
			throws Exception {
		SimpleDateFormat smDtfm = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
		//System.out.println("date is---->" + smDtfm.parse(date).getTime());
		return smDtfm.parse(date).getTime() / 1000L;
	}

	/**
	 * update data in table
	 * 
	 * @param tableName
	 * @param initialValues
	 * @param context
	 * @param whereClause
	 * @param whereArgs
	 * @return no of rows updated
	 */
	public int updateDataInDB(String tableName, ContentValues initialValues,
			Context context, String whereClause, String[] whereArgs) {
		int returnVal = 0;
		DBAdapter dbAdapter = DBAdapter.getInstance(context);
		try {
			// //System.out.println("upload called-------------->");
			try {
				dbAdapter.openDataBase();

			} catch (Exception e) {
				// TODO: handle exception

			//	;

			}
			// dbAdapter.openDataBase();
			returnVal = dbAdapter.updateRecordsInDB(tableName, initialValues, whereClause, whereArgs);

		} catch (SQLException e) {

			;
		} finally {
			//System.out.println("updateTimestamp finally called");
			if (dbAdapter != null)
				dbAdapter.close();
		}

		return returnVal;
	}

	/**
	 * 
	 * @return true if service is stopped false if service is running
	 */
	public static boolean checkServiceRunningStatus() {

		if (ApplicationConstant.STOP_SERVICE_FLAG)
			return true;
		else
			return false;
	}

	/**
	 * Checks the service status
	 * 
	 * @param serviceName
	 * @return boolean
	 */
	public static boolean isMyServiceRunning(Context context, String serviceName) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceName.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * method to get weather any particular no. saved in contact list or not
	 * 
	 * @param phoneNo
	 * @param context
	 * @return contact ID against no.
	 */
	public static String isContactRecorded(String phoneNo, Context context) {

		String contactId = null;
		if (phoneNo != null && !phoneNo.equalsIgnoreCase("")) {
			try {

				// progressDialog =
				// ProgressDialog.show(RegistrationActivity.this,
				// "",getResources().getString(R.string.progress));
				Uri uri = Uri.withAppendedPath(
						ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
						Uri.encode(phoneNo));
				String[] projection = new String[] {
						ContactsContract.PhoneLookup._ID,
						ContactsContract.PhoneLookup.DISPLAY_NAME };
				String selection = null;
				String[] selectionArgs = null;
				String sortOrder = ContactsContract.PhoneLookup.DISPLAY_NAME
						+ " COLLATE LOCALIZED ASC";
				ContentResolver cr = context.getContentResolver();
				if (cr != null) {
					Cursor resultCur = cr.query(uri, projection, selection,
							selectionArgs, sortOrder);
					if (resultCur != null) {
						while (resultCur.moveToNext()) {
							contactId = resultCur
									.getString(resultCur
											.getColumnIndex(ContactsContract.PhoneLookup._ID));
							break;
						}
						resultCur.close();
					} else {
					}
				}

			} catch (Exception sfg) {
				//System.out.println("exep false");
				Log.e("Error", "Error in loadContactRecord : " + sfg.toString());
				sfg.printStackTrace();

			}
		}

		return contactId;
	}

	/**
	 * method to generate notification
	 * 
	 * @param ctx
	 * @param tag
	 * @param ID
	 * @param ticker
	 * @param longTxt
	 * @param Title
	 */
	public static void createNotification(Context ctx, String tag, int ID,
			String ticker, String longTxt, String Title) {
		if (!ApplicationConstant.Is_First) {
			Intent intent = new Intent(ctx, AllTaskActivity.class);

			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);

			PendingIntent pIntent = PendingIntent
					.getActivity(ctx, 0, intent, 0);

			Notification notification = new NotificationCompat.Builder(ctx)
					.setAutoCancel(true).setContentTitle(Title)
					.setContentText(longTxt).setContentIntent(pIntent)
					.setSmallIcon(R.drawable.notiicon)
					.setWhen(System.currentTimeMillis()).setTicker(ticker)
					.build();
			NotificationManager notificationManager = (NotificationManager) ctx
					.getSystemService(Context.NOTIFICATION_SERVICE);
			notification.defaults |= Notification.DEFAULT_SOUND;
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notificationManager.notify(tag, ID, notification);
		} else {
			//System.out.println("It is first time launch app");
		}

	}

	/**
	 * this method returns name of running activity
	 */
	public static String runningActivty(Context ctx) {
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		String activity = taskInfo.get(0).topActivity.getClassName();

		return activity;

	}

	/**
	 * method to fetch phone number (from SIM or Whatsapp)
	 * 
	 * @param ctx
	 * @return number
	 */
	public static String fetchSimNo(Context ctx) {
		String number = "";
		TelephonyManager mTelephonyMgr;
		mTelephonyMgr = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		number = mTelephonyMgr.getLine1Number();
		if (number != null && number.trim().length() > 0)
			return number;
		else {
			AccountManager am = AccountManager.get(ctx);
			Account[] accounts = am.getAccounts();
			for (Account ac : accounts) {
				String acname = ac.name;
				String actype = ac.type;
				// Take your time to look at all available accounts
				//System.out.println("Accounts : " + acname + ", " + actype);

				if (actype.equals("com.whatsapp")) {
					number = ac.name;
				}
			}
			return number;
		}
	}

	public static String getDayOfMonthSuffix(int n) {
		/*
		 * if (n >= 11 && n <= 13) { return "th"; }
		 */
		if (n == 11 || n == 12 || n == 13) {
			return "th";
		}

		switch (n % 10) {
		case 1:
			return "st";
			// break;
		case 2:
			return "nd";
			// break;
		case 3:
			return "rd";
		default:
			return "th";
		}
	}

	public static String getSyncTime(String date) {
		String syncDate = date;
		try {
			long timestamp = getUpdatedTimestampFromDate3(date);
			//System.out.println("SyncTimeStamp---->" + timestamp);
			syncDate = changeToDayMonthYear4(timestamp - 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			;
		}

		return syncDate;

	}

	public static String getCurrentDateOfSystem() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Calendar cal = Calendar.getInstance();
		//System.out.println("time => " + dateFormat.format(cal.getTime()));

		String time_str = dateFormat.format(cal.getTime());
		//System.out.println("Time stamp is =" + time_str);
		// TV.setText(""+time_str);
		return time_str;
	}

	public static String getValidNumber(String mobno) {
		String number = mobno;
		number = number.replaceAll("[\\D]", "");
		return (number.length() > 10) ? (String) (number.subSequence(
				number.length() - 10, number.length())) : number;

	}

	public static String getCurrentDate() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String formattedDate = df.format(c.getTime());
		return formattedDate;
	}

	/*
	 * public static int compareTwoDates(String date1, String date2) { if (date1
	 * != null && date2 != null) { int retVal = date1.compareTo(date2);
	 * 
	 * if (retVal > 0) return 1; // date1 is greatet than date2 else if (retVal
	 * == 0) // both dates r equal return 0;
	 * 
	 * } return -1; // date1 is less than date2 }
	 */

	public static Date parseDate(String date) {
		SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy");
		Date dateObj = new Date();
		try {
			dateObj = curFormater.parse(date);
		} catch (ParseException e) {
			;
		}
		return dateObj;
	}

	public static Date getDateInGMT(String date) {
		if (date == null) {
			return null;
		}
		Date dateIs = null;
		DateFormat gmtFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		TimeZone gmtTime = TimeZone.getTimeZone("GMT");
		gmtFormat.setTimeZone(gmtTime);
		try {
			// //System.out.println("Test.main()"+gmtFormat.parse("31/10/2013 11:45:00"));
			dateIs = gmtFormat.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			;
		}
		return dateIs;

	}

	public static Date getLocalDateInGMT(Date date) {
		Date dateIs = null;
		DateFormat gmtFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		TimeZone gmtTime = TimeZone.getTimeZone("GMT");
		gmtFormat.setTimeZone(gmtTime);
		try {
			// //System.out.println("Test.main()"+gmtFormat.parse("31/10/2013 11:45:00"));
			dateIs = getDateInGMT(gmtFormat.format(date));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			;
		}
		return dateIs;

	}

	public static String dateToString(String date) {
		if (date == null)
			return null;
		String dateStr = null;
		try {
			DateFormat gmtFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			TimeZone gmtTime = TimeZone.getTimeZone("GMT");
			gmtFormat.setTimeZone(gmtTime);
			// //System.out.println("Test.main()"+dateToString(gmtFormat.parse("31/10/2013 12:01:22")));
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM HH:mm");
			dateStr = dateFormat.format(gmtFormat.parse(date));
		} catch (Exception e) {
			;
		}
		return dateStr;
	}

	public static String getDisplayDate(String date) {//New format
		String displayTime = " ";
		try {
			if (date == null)
				return null;
			String dateStr = null;
			DateFormat gmtFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			TimeZone gmtTime = TimeZone.getTimeZone("GMT");
			gmtFormat.setTimeZone(gmtTime);
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM HH:mm");
			long actualDateTime = gmtFormat.parse(date).getTime();
			dateStr = dateFormat.format(gmtFormat.parse(date));
			
			SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
			String time = timeFormat.format(gmtFormat.parse(date));
			
			long finalDate = System.currentTimeMillis() - actualDateTime;
			if (finalDate < (1000 * 60)) {
				int seconds = (int) (finalDate / 1000) % 60;
				displayTime = seconds + (seconds <= 1 ? " second ago" : " seconds ago");
			} else if (finalDate < (1000 * 60 * 60)) {
				int minutes = (int) ((finalDate / (1000 * 60)) % 60);
			
				displayTime = minutes + (minutes <= 1 ? " minute ago" : " minutes ago");
			} else if (finalDate < (1000 * 60 * 60 * 24)) {
				int hours = (int) ((finalDate / (1000 * 60 * 60)) % 24);
				
				displayTime = hours + (hours <= 1 ? " hour ago" : " hours ago");

			} else if (finalDate > (1000 * 60 * 60 * 24)
					&& finalDate < (1000 * 60 * 60 * 24 * 2)) {
				displayTime = "Yesterday at " + time;
				
			} else if (finalDate > (1000 * 60 * 60 * 24 * 2)
					&& finalDate < (1000 * 60 * 60 * 24 * 3)) {
				
				displayTime = 2 + " days ago at " + time;
			} else if (finalDate > (1000 * 60 * 60 * 24 * 3)
					&& finalDate < (1000 * 60 * 60 * 24 * 4)) {
				
				displayTime = 3 + " days ago at " + time;
			} else if (finalDate > (1000 * 60 * 60 * 24 * 4)
					&& finalDate < (1000 * 60 * 60 * 24 * 5)) {
				
				displayTime = 4 + " days ago at " + time;
			} else if (finalDate > (1000 * 60 * 60 * 24 * 5)
					&& finalDate < (1000 * 60 * 60 * 24 * 6)) {
				
				displayTime = 5 + " days ago at " + time;
			} else if (finalDate > (1000 * 60 * 60 * 24 * 6)
					&& finalDate < (1000 * 60 * 60 * 24 * 7)) {
				
				displayTime = 6 + " days ago at " + time;
			} else if (finalDate > (1000 * 60 * 60 * 24 * 7)
					&& finalDate < (1000 * 60 * 60 * 24 * 8)) {
				
				displayTime = 1 + " week ago at " + time;
			} else {
				SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd MMM");
				try {
					String dateTmp = dateFormat1 .format(new Date(actualDateTime));
					displayTime = dateTmp + " at " + time;
				} catch (Exception e) {
					displayTime = "";
				}
			}
		} catch (Exception e) 
		{
		}
		return displayTime;
	}
	
	/*public static String getDisplayDate(String date) {//old format
		String displayTime = " ";
		try {
			if (date == null)
				return null;
			String dateStr = null;
			DateFormat gmtFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			TimeZone gmtTime = TimeZone.getTimeZone("GMT");
			gmtFormat.setTimeZone(gmtTime);
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM HH:mm");
			long actualDateTime = gmtFormat.parse(date).getTime();
			dateStr = dateFormat.format(gmtFormat.parse(date));
			System.out.println("dateStr" + dateStr);
			long finalDate = System.currentTimeMillis() - actualDateTime;
			if (finalDate < (1000 * 60)) {
				int seconds = (int) (finalDate / 1000) % 60;
				System.out.println(seconds + " s");
				displayTime = seconds + " s";
			} else if (finalDate < (1000 * 60 * 60)) {
				int minutes = (int) ((finalDate / (1000 * 60)) % 60);
				System.out.println(minutes + " m");
				displayTime = minutes + " m";
			} else if (finalDate < (1000 * 60 * 60 * 24)) {
				int hours = (int) ((finalDate / (1000 * 60 * 60)) % 24);
				System.out.println(hours + " h");
				displayTime = hours + " h";

			} else if (finalDate > (1000 * 60 * 60 * 24)
					&& finalDate < (1000 * 60 * 60 * 24 * 2)) {
				System.out.println(1 + " d");
				displayTime = 1 + " d";

			} else if (finalDate > (1000 * 60 * 60 * 24 * 2)
					&& finalDate < (1000 * 60 * 60 * 24 * 3)) {
				System.out.println(2 + " d");
				displayTime = 2 + " d";
			} else if (finalDate > (1000 * 60 * 60 * 24 * 3)
					&& finalDate < (1000 * 60 * 60 * 24 * 4)) {
				System.out.println(3 + " d");
				displayTime = 3 + " d";
			} else if (finalDate > (1000 * 60 * 60 * 24 * 4)
					&& finalDate < (1000 * 60 * 60 * 24 * 5)) {
				System.out.println(4 + " d");
				displayTime = 4 + " d";
			} else if (finalDate > (1000 * 60 * 60 * 24 * 5)
					&& finalDate < (1000 * 60 * 60 * 24 * 6)) {
				System.out.println(5 + " d");
				displayTime = 5 + " d";
			} else if (finalDate > (1000 * 60 * 60 * 24 * 6)
					&& finalDate < (1000 * 60 * 60 * 24 * 7)) {
				System.out.println(6 + " d");
				displayTime = 6 + " d";
			} else if (finalDate > (1000 * 60 * 60 * 24 * 7)
					&& finalDate < (1000 * 60 * 60 * 24 * 8)) {
				System.out.println(7 + " d");
				displayTime = 7 + " d";
			} else {
				SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd MMM");
				try {
					String dateTmp = dateFormat1
							.format(new Date(actualDateTime));
					// System.out.println("Final date "+dateTmp);
					displayTime = dateTmp;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					displayTime = "";
				}
				System.out.println();
			}
		} catch (Exception e) 
		{
		}
		return displayTime;
	}*/
	
	public static void sendGet(String urls) throws Exception {

		String url = urls;// "http://www.google.com/search?q=mkyong";

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		// con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		//System.out.println("\nSending 'GET' request to URL : " + url);
		//System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		//System.out.println(response.toString());

	}

	/*
	 * public static String getStringNo(String key,Context context){
	 * ApplicationConstant.pref=
	 * context.getApplicationContext().getSharedPreferences("MyPref", 0); String
	 * mobNo= ApplicationConstant.pref.getString(key, null);
	 * //System.out.println("Preference token="+mobNo); return mobNo; }
	 */
	public static long getDateToMillisecond(String date) throws Exception {
		// long date = -1;
		if (date != null) {
			SimpleDateFormat smDtfm = new SimpleDateFormat(
					"dd/MM/yyyy HH:mm:ss");
			//System.out.println("date is---->" + smDtfm.parse(date).getTime());
			return smDtfm.parse(date).getTime();
		} else {
			return -1;
		}
	}

	public static void savePreference(String key, String value, Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor edit = pref.edit();
		edit.putString(key, value);
		edit.commit();

	}

	public static String getPreference(String key, Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		String keyValue = pref.getString(key, null);

		return keyValue;

	}

	public static int getDateDiffInHours(long date1, long date2) {
		try {
			long diff = date1 - date2;
			int hour = (int) ((diff) / (1000 * 60 * 60));
			return hour;
		} catch (Exception e) {
			;
			return -1;
		}
	}

	public static Date getCuureDate() {
		Date date = new Date();
		DateFormat gmtFormat = new SimpleDateFormat();
		TimeZone gmtTime = TimeZone.getTimeZone("GMT");
		gmtFormat.setTimeZone(gmtTime);
		//System.out.println("Current Time: " + date);
		//System.out.println("GMT Time: " + gmtFormat.format(date));
		return date;
	}

	public static String getGMTDate() {
		Date date = new Date();
		String strDate = "";
		DateFormat gmtFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		TimeZone gmtTime = TimeZone.getTimeZone("GMT");
		gmtFormat.setTimeZone(gmtTime);
		//System.out.println("Current Time: " + date);
		try {
			strDate = gmtFormat.format(date);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			;
		}
		return strDate;
	}

	public static long getGMTLong() {
		long actualDateTime = -1;
		try {
			DateFormat gmtFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			TimeZone gmtTime = TimeZone.getTimeZone("GMT");
			gmtFormat.setTimeZone(gmtTime);
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM HH:mm");
			actualDateTime = gmtFormat.parse(getGMTDate()).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			;
		}
		return actualDateTime;
	}

	public static long getGMTLong(String strDate) {
		if (strDate == null)
			return -1;
		long actualDateTime = -1;
		try {
			DateFormat gmtFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			TimeZone gmtTime = TimeZone.getTimeZone("GMT");
			gmtFormat.setTimeZone(gmtTime);
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM HH:mm");
			actualDateTime = gmtFormat.parse(strDate).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			;
		}
		return actualDateTime;
	}

	public static void sendSMS(String phonNo, String msg) {

		// if(phonNo!=null)
		// {
		// String phoneNumber = phonNo;
		//
		// String[] ph = phoneNumber.split(",");
		// for (String no : ph) {

		String message = msg + "." + "\n\n"+"\""+" (Message was sent via mAssigner app. Please download it from http://massigner.com/app to ease your life)";
        
		try {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(phonNo, null, message, null, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			;
		}
		// }
		//System.out.println("sms sended");
	}

	public static String getContactNameFromPhoneNo(Context mContext,String assigntoNum)//
	{
		
		if (ApplicationConstant.ASSIGNER_NUMBER.equals(CommonUtil.getValidMsisdn(assigntoNum))) {
			return ApplicationConstant.ASSIGNER_NAME;
		}
	
		if(contactMap==null){
			contactMap=new WeakHashMap<String, String>();
		}
		String firstName=ApplicationUtil.getPreference(ApplicationConstant.REG_NAME,mContext);
		
		String name=contactMap.get(CommonUtil.getValidMsisdn(assigntoNum));
		if(name!=null){
			return name;
		}
		if(CommonUtil.getRegNum(mContext).equals(CommonUtil.getValidMsisdn(assigntoNum))){
			contactMap.put(CommonUtil.getValidMsisdn(assigntoNum),firstName);
			return firstName;
		}
		//System.gc();
		String contactName = null;
		/*
		 * if(assigntoName!=null&&!assigntoName.isEmpty()) { contactName =
		 * assigntoName; return contactName; }
		 */
		if (assigntoNum != null && !assigntoNum.isEmpty()) {
			String assigntoNumWithoutReg = ApplicationUtil
					.getValidNumber(assigntoNum);

			if (assigntoNumWithoutReg.equals(ApplicationConstant.ASSIGNER_NUMBER)) {
				return ApplicationConstant.ASSIGNER_NAME;
			}

			try {
				if (assigntoNumWithoutReg != null) {

					ContentResolver localContentResolver = mContext
							.getContentResolver();
					Cursor contactLookupCursor = localContentResolver.query(Uri
							.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
									Uri.encode(assigntoNumWithoutReg)),
							new String[] { PhoneLookup.DISPLAY_NAME,
									PhoneLookup._ID }, null, null, null);
					try {
						while (contactLookupCursor.moveToNext()) {
							contactName = contactLookupCursor
									.getString(contactLookupCursor
											.getColumnIndexOrThrow(PhoneLookup.DISPLAY_NAME));
							String contactId = contactLookupCursor
									.getString(contactLookupCursor
											.getColumnIndexOrThrow(PhoneLookup._ID));
							if (contactName == null|| contactName.equalsIgnoreCase("")) {
								contactName = assigntoNumWithoutReg;
							}
						}
					} finally {
						contactLookupCursor.close();
					}
				}
				
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				;
			}

		} 
		
		if(contactName==null){
			if(CommonUtil.getRegNum(mContext).equals(CommonUtil.getValidMsisdn(assigntoNum))){
				contactMap.put(CommonUtil.getValidMsisdn(assigntoNum),firstName);
			}else{
				contactMap.put(CommonUtil.getValidMsisdn(assigntoNum),assigntoNum);
			}
			
			return assigntoNum;
			
		}
		contactMap.put(CommonUtil.getValidMsisdn(assigntoNum),contactName);
		return contactName;
	}

	/*
	 * public static boolean isTextContainSpecial(String text) { return
	 * text.matches("[a-zA-Z.? ]*");// regular exp for string }
	 */

	public static void showDialog(Context context, String title, String message) {
		final AlertDialog alertDialog = new AlertDialog.Builder(context)
				.create();

		// Setting Dialog Title
		alertDialog.setTitle(title);

		// Setting Dialog Message
		alertDialog.setMessage(message);

		// Setting Icon to Dialog
		alertDialog.setIcon(R.drawable.notiicon);

		// Setting OK Button
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.dismiss();
					}
				});

		// Showing Alert Message
		alertDialog.show();
	}

	public void showAlertDialog(Context context, String title, String message,
			Boolean status) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		// Setting Dialog Title
		alertDialog.setTitle(title);

		// Setting Dialog Message
		alertDialog.setMessage(message);

		if (status != null)
			// Setting alert dialog icon
			alertDialog.setIcon((status) ? R.drawable.notiicon
					: R.drawable.notiicon);

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}
	public static void registerOnDevice(Context context,String mobileNo,String regId) {
		 SyncModule syn = new SyncModule(context);
		 syn.registerDeviceOnServer(context,mobileNo,
		 regId);
		 syn=null;
		
		//ServerUtilities.register(context, "purendra", "purendra25@gmail.com", regId);
	}
	
	public static boolean compareDate (String date1,String date2)
	{
		Date d1= parseDate(date1);
		Date d2= parseDate(date2);
		return (d1.before(d2));
	}
	
	public static Date parseDateUpdated(String date) {
	    SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	    Date dateObj = new Date();
	    try {
	      dateObj = curFormater.parse(date);
	    } catch (ParseException e) {
	      ;
	    }
	    return dateObj;
	  }

	public static boolean comparetwoDate(String dates1,String dates2)
	{
		boolean dateflag= false;
		try{
			 
    		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        	Date date1 = sdf.parse(dates1);
        	Date date2 = sdf.parse(dates2);
 
        	//System.out.println(sdf.format(date1));
        	//System.out.println(sdf.format(date2));
 
        	if(date1.after(date2)){
        		dateflag = false;
        		////System.out.println("Date1 is after Date2");
        	}
 
        	if(date1.before(date2)){
        	//	//System.out.println("Date1 is before Date2");
        		dateflag = true;
        	}
 
        	if(date1.equals(date2)){
        	//	dateflag = false;
        		//System.out.println("Date1 is equal Date2");
        	}
 
    	}catch(ParseException ex){
    		ex.printStackTrace();
    	}
		return dateflag;
	}
	
	
	public static Date CheckDateFormat(String input)
	{
		Date date = null;
		if(input==null)
		{
			return null;
		}
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd MMM");
	try
	{
		dateFormat1.setLenient(false);
		date = dateFormat1.parse(input);
	}catch(Exception ex)
	{
		//ex.printStackTrace();
	}
		return date;
	}
	public static  boolean isApplicationBroughtToBackground(Context context) {
	    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningTaskInfo> tasks = am.getRunningTasks(1);
	    if (!tasks.isEmpty()) {
	        ComponentName topActivity = tasks.get(0).topActivity;
	        if (!topActivity.getPackageName().equals(context.getPackageName())) {
	            return true;
	        }
	    }

	    return false;
	}

}