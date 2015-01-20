
package com.taskmanager.database;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Typeface;
import android.os.Environment;

import com.taskmanager.app.R;
import com.taskmanager.bean.NotationDto;
import com.taskmanager.dto.Contact;
import com.taskmanager.dto.MessageInfoEntity;
import com.taskmanager.dto.TaskInfoEntity;
import com.taskmanager.dto.UserInfoEntity;
import com.taskmanager.entites.TaskEntity;
import com.taskmanager.util.ApplicationConstant;
import com.taskmanager.util.ApplicationUtil;
import com.taskmanager.util.CommonUtil;
import com.taskmanager.webservice.BGConnector;
import com.taskmanager.webservice.BGConnectorImpl;

/**
 * Database related operations
 * 
 * @author mayankb
 * 
 */
public class DBAdapter extends SQLiteOpenHelper {

	public static final String [] notationName={"#Work","#Travel","#Shopping", "#Party"};

	// Path of DB on local storage
	// public static String DB_PATH ;
	public static final String DB_PATH = Environment.getDataDirectory()
			+ File.separator + "data" + File.separator + "com.taskmanager.app"
			+ File.separator + "databases";

	public static final String DB_NAME = File.separator + "taskmanager.sqlite";

	private SQLiteDatabase myDataBase;
	private SQLiteDatabase myDataBaseForum;
	private final Context myContext;
	private static DBAdapter dbAdapter;
	//	private static DBAdapter mDBConnection;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	private DBAdapter(Context context) {
		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}

	public static DBAdapter getInstance(Context context){
		if(dbAdapter==null){
			dbAdapter=new DBAdapter(context);
		}
		return dbAdapter;
	}

	/**
	 * Creates an empty database on the system and rewrites it with your own
	 * database.
	 **/
	public  void createDataBase() throws IOException {
		boolean dbExist = checkDataBase();
		if (dbExist) {
			//System.out.println("database exists");
			// do nothing - database already exist
		} else {
			//System.out.println("database does not exist");
			createTable();

		}
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private synchronized boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException ex) {
			// database does't exist yet.
			//ex.printStackTrace();

		}
		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}


	/**
	 * Open the database
	 * 
	 * @throws SQLException
	 */
	public synchronized  void openDataBase() throws SQLException {
		try {

			if(myDataBase!=null&&myDataBase.isOpen()){
				return;
			}
			String myPath = DB_PATH + DB_NAME;
			//  SQLiteDatabase.
			myDataBase = SQLiteDatabase.openDatabase(myPath, null,SQLiteDatabase.OPEN_READWRITE);
			/* if(checkDataBase()){
                    myDataBase = SQLiteDatabase.openDatabase(myPath, null,SQLiteDatabase.OPEN_READWRITE);
                    }else {
                            createDataBase();
                            myDataBase = SQLiteDatabase.openDatabase(myPath, null,SQLiteDatabase.OPEN_READWRITE);
                    }*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized  void openReadDataBase() throws SQLException {
		try {
			/*if(myDataBase!=null&&myDataBase.isOpen()){
				return;
			}*/
			String myPath = DB_PATH + DB_NAME;
			myDataBase = SQLiteDatabase.openDatabase(myPath, null,SQLiteDatabase.OPEN_READWRITE);
			/* if(checkDataBase()){
                myDataBase = SQLiteDatabase.openDatabase(myPath, null,SQLiteDatabase.OPEN_READWRITE);
                }else {
                        createDataBase();
                        myDataBase = SQLiteDatabase.openDatabase(myPath, null,SQLiteDatabase.OPEN_READWRITE);
                }*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Open the database
	 * 
	 * 
	 * 
	 * @throws SQLException
	 */
	public synchronized void openDataBaseForum() throws SQLException {
		String myPath = DB_PATH + DB_NAME;
		myDataBaseForum = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);
	}


	/**
	 * Close the database if exist
	 */
	@Override
	public synchronized  void close() {

		try {

			if(myDataBase!=null){
				//myDataBase.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}

	/**
	 * Call on creating data base for example for creating tables at run time
	 */
	@Override
	public synchronized void onCreate(SQLiteDatabase db) {

	}

	/**
	 * can used for drop tables then call onCreate(db) function to create tables
	 * again - upgrade
	 */
	@Override
	public synchronized void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		
		/*String NOTAION_KEYWORD = "CREATE TABLE 'NOTAION_KEYWORD' ('ID' INTEGER PRIMARY KEY  , 'NAME' VARCHAR)";
		db.execSQL(NOTAION_KEYWORD);
		
		String NOTAION_TASK_MAPPING = "CREATE TABLE 'NOTAION_TASK_MAPPING' ('ID' INTEGER PRIMARY KEY, 'NAME' VARCHAR,'Task_ID' VARCHAR)";
		db.execSQL(NOTAION_TASK_MAPPING);*/
	}

	// ----------------------- CRUD Functions ------------------------------

	/**
	 * This function used to select the records from DB.
	 * 
	 * @param tableName
	 * @param tableColumns
	 * @param whereClase
	 * @param whereArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return
	 * @return A Cursor object, which is positioned before the first entry.
	 */
	public synchronized ArrayList<ArrayList<String>> selectRecordsFromDB(
			String tableName, String[] tableColumns, String whereClase,
			String whereArgs[], String groupBy, String having, String orderBy) {
		Cursor cursor = null;
		ArrayList<ArrayList<String>> retList = new ArrayList<ArrayList<String>>();
		try {

			ArrayList<String> list = new ArrayList<String>();
			openDataBase();

			cursor = myDataBase.query(tableName, tableColumns, whereClase,
					whereArgs, groupBy, having, orderBy);

			if (cursor != null) {

				if (cursor.moveToFirst()) {
					do {
						list = new ArrayList<String>();
						for (int i = 0; i < cursor.getColumnCount(); i++) {
							list.add(cursor.getString(i));

						}
						retList.add(list);
					} while (cursor.moveToNext());
				}

			} else
				//System.out.println("cursor null");

				return retList;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			close();
			return retList;
		}
	}

	public synchronized Cursor selectDistinctRecordsFromDB(String tableName,
			String[] tableColumns, String whereClase, String whereArgs[],
			String groupBy, String having, String orderBy) {
		return myDataBaseForum.query(true, tableName, tableColumns, whereClase,
				whereArgs, groupBy, having, orderBy, "");
		// return myDataBase.query(tableName, tableColumns, whereClase,
		// whereArgs,
		// groupBy, having, orderBy);
	}

	/**
	 * select records from db and return in list
	 * 
	 * @param tableName
	 * @param tableColumns
	 * @param whereClase
	 * @param whereArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return ArrayList>
	 */

	/**
	 * apply raw query and return result in list
	 * 
	 * @param query
	 * @param selectionArgs
	 * @return ArrayList<ArrayList<String>>
	 */
	public synchronized ArrayList<ArrayList<String>> selectRecordsFromDBList(

			String tableName, String[] tableColumns, String whereClase,
			String whereArgs[], String groupBy, String having, String orderBy) {
		ArrayList<ArrayList<String>> retList = new ArrayList<ArrayList<String>>();
		Cursor cursor = null;
		try {



			openDataBase();


			ArrayList<String> list = new ArrayList<String>();
			cursor = myDataBase.query(tableName, tableColumns, whereClase,
					whereArgs, groupBy, having, orderBy);
			if(cursor!=null)
			{
				if (cursor.moveToFirst()) {
					do {
						list = new ArrayList<String>();
						for (int i = 0; i < cursor.getColumnCount(); i++) {
							list.add(cursor.getString(i));

						}
						retList.add(list);
					} while (cursor.moveToNext());
				}
			}

			return retList;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

			return retList;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			close();
		}
	}

	/**
	 * apply raw query and return result in list
	 * 
	 * @param query
	 * @param selectionArgs
	 * @return ArrayList<ArrayList<String>>
	 */
	public synchronized ArrayList<ArrayList<String>> selectUserInfoFromDBList(
			String tableName, String[] tableColumns, String whereClase,
			String whereArgs[], String groupBy, String having, String orderBy) {

		ArrayList<ArrayList<String>> retList = new ArrayList<ArrayList<String>>();
		openDataBase();
		Cursor cursor = null;
		try {

			ArrayList<String> list = new ArrayList<String>();
			cursor = myDataBase.query(tableName, tableColumns, whereClase,
					whereArgs, groupBy, having, orderBy);
			if (cursor.moveToFirst()) {
				do {
					list = new ArrayList<String>();
					for (int i = 0; i < cursor.getColumnCount(); i++) {
						list.add(cursor.getString(i));
						if (i == 18)
							ApplicationUtil.ImageArray = cursor.getBlob(i);
					}
					retList.add(list);
				} while (cursor.moveToNext());
			}

			return retList;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

			return retList;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			close();
		}
	}

	/**
	 * This function used to update the Record in DB.
	 * 
	 * @param tableName
	 * @param initialValues
	 * @param whereClause
	 * @param whereArgs
	 * @return 0 in case of failure otherwise return no of row(s) are updated
	 */
	public synchronized int updateRecordsInDB(String tableName,
			ContentValues initialValues, String whereClause, String whereArgs[]) {
		int status = 0;
		try {
			openDataBase();
			status = myDataBase.update(tableName, initialValues, whereClause,
					whereArgs);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		} finally {
			close();

			return status;
		}
	}

	/**
	 * This function used to delete the Record in DB.
	 * 
	 * @param tableName
	 * @param whereClause
	 * @param whereArgs
	 * @return 0 in case of failure otherwise return no of row(s) are deleted.
	 */
	public synchronized int deleteRecordInDB(String tableName,
			String whereClause, String[] whereArgs) {
		int x = myDataBase.delete(tableName, whereClause, whereArgs);
		return x;
	}
	
	/*
	 * Function to delete records which are extra in database.
	 */
	
	public synchronized int deletecontactInDb(String tableName, StringBuilder number)
	{
		int count = 0;
		String qu = "Delete from " + tableName +" WHERE MOBILE_NUMBER NOT IN ("+number+")";
		Cursor cursor_temp = myDataBase.rawQuery(qu, null);
		
		if(cursor_temp.moveToFirst()) {
			count = cursor_temp.getInt(0);
		}
		return count;
	}
	
	public synchronized int CreateorUpdate(String tableName, String phonenumber)
    {
        int number = 0;
        Cursor c = null;
        try
        {	openDataBase();
            c = myDataBase.rawQuery("select MOBILE_NUMBER from "+ tableName +" where MOBILE_NUMBER = ?", new String[] {String.valueOf(phonenumber)});

            if(c.getCount() != 0)
                number = c.getCount();
        }
        catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(c!=null) c.close();
        }
        return number;
    }
	
	

	// --------------------- Select Raw Query Functions ---------------------

	/**
	 * apply raw Query
	 * 
	 * @param query
	 * @param selectionArgs
	 * @return Cursor
	 */
	public synchronized Cursor selectRecordsFromDB(String query,
			String[] selectionArgs) {
		return myDataBase.rawQuery(query, selectionArgs);
	}

	public List<String> getAsyncGroupId(){
		ArrayList<String> list=new ArrayList<String>();
		String query=" Select * from TASK_GROUP where TASK_SYSNC_STATUS='N'";
		Cursor cursor = null;
		openDataBase();
		cursor = myDataBase.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			do {
				list.add(cursor.getString(cursor.getColumnIndex("groupId")));
			} while (cursor.moveToNext());
		}
		close();
		return list;
	}

	public int getTotalMessageForTask(String taskId ){
		int count=0;
		Cursor mCount=null;
		try{
			openDataBase();
			mCount= myDataBase.rawQuery("select count(*) from Messaging where  Task_ID='"+taskId+"'", null);
			mCount.moveToFirst();
			count= mCount.getInt(0);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			mCount.close();
			myDataBase.close();

		}
		return count;
	}

	public void deleteTask(String groups){
		openDataBase();
		myDataBase.delete("TASK", "Group_ID='"+groups+"'",  null);

	}
	public void deteleteGroup(String groups){
		openDataBase();
		myDataBase.delete("TASK_GROUP", "groupId='"+groups+"'",  null);
		close();

	}

	public List<TaskInfoEntity> getAsyncTaskByGropId(String groupId){
		openDataBase();
		String query=" Select * from Task where IsTaskSync='N' and Group_ID='"+groupId+"'";
		List<TaskInfoEntity> entities=new ArrayList<TaskInfoEntity>();
		Cursor cursor = null;
		cursor = myDataBase.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			do {
				TaskInfoEntity entity = new TaskInfoEntity();
				UserInfoEntity assignFrom=new UserInfoEntity();
				UserInfoEntity assignTo=new UserInfoEntity();
				entity.setId(cursor.getString(0));
				assignFrom.setMobile_number(cursor.getString(1));
				assignFrom.setFirstName(cursor.getString(2));
				assignTo.setMobile_number(cursor.getString(3));
				assignTo.setFirstName(cursor.getString(4));
				entity.setUserInfoFrom(assignFrom);
				entity.setUserInfoTo(assignTo);
				entity.setTask_description(cursor.getString(5));
				entity.setPriority(cursor.getString(6));
				entity.setCloser_date(cursor.getString(8));
				entity.setTarget_date((cursor.getString(9)));
				entity.setFire_flag(cursor.getString(10));
				entity.setReminder_time(cursor.getString(11));
				entity.setIsReminder(cursor.getString(12));
				entity.setIsMessage(cursor.getString(13));
				entity.setStatus(cursor.getString(14));
				entity.setIsFavouraite(cursor.getString(15));
				entity.setTaskType(cursor.getString(16));
				entity.setCreation_date(cursor.getString(17));
				entity.setIsTaskSync(cursor.getString(cursor.getColumnIndex("IsTaskSync")));
				entity.setIsJunk(cursor.getString(cursor.getColumnIndex("IsJunk")));
				entity.setAlarmTime(cursor.getString(cursor.getColumnIndex("AlarmTime")));
				entity.setGroupId(cursor.getString(cursor.getColumnIndex("Group_ID")));
				entity.setIsAssigneeSync(cursor.getString(27));
				entity.setIsTargetSync(cursor.getString(28));
				entity.setIsStatusSync(cursor.getString(20));
				entity.setIsReminderSync(cursor.getString(21));
				entity.setTotalMessage(cursor.getString(29));
				entity.setUnreadMessage(cursor.getString(30));
				entity.setIsTaskRead(cursor.getString(cursor.getColumnIndex("IsTaskRead")));
				entity.setGroupId(cursor.getString(cursor.getColumnIndex("Group_ID")));
				entity.setClosedBy(cursor.getString(cursor.getColumnIndex("ClosedBy")));
				//}
				entities.add(entity);
			} while (cursor.moveToNext());
		}
		if(cursor!=null)
			cursor.close();
		close();
		return entities;
	}

	public String getTaskIds(Context context){
		Cursor cursor = null;
		String query=" Select Task_ID from Task where IsTaskSync='Y' ";
		StringBuilder taskIds=new StringBuilder();
		try{
			cursor = myDataBase.rawQuery(query, null);
			if (cursor.moveToFirst()) {
				do {
					String taskId = cursor.getString(cursor.getColumnIndex("Task_ID"));
					taskIds.append(taskId);
					taskIds.append(",");
				}while (cursor.moveToNext());
			}
		}catch(Exception exception){
			exception.printStackTrace();
		}
		return taskIds.toString();
	}
	public Map getTaskList(String query,final Context context ){
		HashMap mapValue= null;
		Cursor cursor = null;
		int fireCount = 0;
		int favCount = 0;

		List<TaskInfoEntity> entities=new ArrayList<TaskInfoEntity>();
		final String curentMsisdn=CommonUtil.getValidMsisdn(ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context));
		//
		try
		{
			openReadDataBase();
			cursor = myDataBase.rawQuery(query, null);
			if (cursor.moveToFirst()) {
				do {
					final TaskInfoEntity entity = new TaskInfoEntity();
					entity.setDisplayName(cursor.getString(cursor.getColumnIndex("DISPLAY_NAME")));
					entity.setId(cursor.getString(0));
					entity.setIsTaskSnooz(cursor.getString(cursor.getColumnIndex("isTaskSnooz")));
					entity.setTargetDateTime(cursor.getString(cursor.getColumnIndex("Target_Date_TIME")));
					String isActive = cursor.getString(cursor.getColumnIndex("isActive"));
					String status= cursor.getString(14);
					final String user_FromNo =cursor.getString(1);
					final String user_Assign_ToNO =CommonUtil.getValidMsisdn(cursor.getString(3));
					String assinee=cursor.getString(cursor.getColumnIndex("Assign_To_Name"));
					String isMsgSend=cursor.getString(cursor.getColumnIndex("isMsgSend"));
					String creation_date = cursor.getString(17);
					String isAlarmSet = cursor.getString(cursor.getColumnIndex("isAlarmSet"));
					String isTaskRead = cursor.getString(cursor.getColumnIndex("IsTaskRead"));
					String isFavourite = cursor.getString(15);
					String isTaskSync = cursor.getString(cursor.getColumnIndex("IsTaskSync"));
					String taskType = cursor.getString(16);
					String userFromName = cursor.getString(2);
					String proiority = cursor.getString(6);
					String taskUrl   = cursor.getString(cursor.getColumnIndex("TASK_URL"));
					entity.setIsTaskRead(isTaskRead);
					entity.setMsgCount(getTotalMessageForTask(cursor.getString(0)));
					if(("N".equalsIgnoreCase(isTaskRead)||"P".equalsIgnoreCase(isTaskRead))&&(!CommonUtil.getValidMsisdn(user_FromNo).equalsIgnoreCase(curentMsisdn))){
						new Thread(new Runnable() {

							@Override
							public void run() {
								BGConnector connector=new BGConnectorImpl();
								boolean flag=false;
								try {
									ContentValues initialValues = new ContentValues();
									initialValues.put("IsTaskRead", "P");
									ApplicationUtil.getInstance().updateDataInDB("Task", initialValues, context,"Task_ID = '" + entity.getId() + "'", null);
									flag = connector.markTaskAsRead(entity.getId(), ApplicationUtil.getPreference(ApplicationConstant.regMobNo,context),user_Assign_ToNO, ApplicationUtil.getPreference(ApplicationConstant.xtoken,context));
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								if(flag){
									ContentValues initialValues = new ContentValues();
									initialValues.put("IsTaskRead", "Y");
									ApplicationUtil.getInstance().updateDataInDB("Task", initialValues, context,"Task_ID = '" + entity.getId() + "'", null);
								}else{
									ContentValues initialValues = new ContentValues();
									initialValues.put("IsTaskRead", "P");
									ApplicationUtil.getInstance().updateDataInDB("Task", initialValues, context,"Task_ID = '" + entity.getId() + "'", null);
								}

							}

						}).start();
					}
					boolean favourite = Boolean.parseBoolean(cursor.getString(15));
					if(isAlarmSet!=null&& isAlarmSet.equalsIgnoreCase("Y"))
					{
						entity.setAlarmBackground(R.drawable.alarm_blue_icon);
					}
					else
					{
						entity.setAlarmBackground(R.drawable.alarm_blue_disable);
					}
					if (isTaskRead != null && isTaskRead.trim().equalsIgnoreCase("N")) {
						if(!curentMsisdn.equalsIgnoreCase(CommonUtil.getValidMsisdn(user_FromNo))){
							entity.setTextTypeface(Typeface.BOLD);
						}	

					}
					else
					{	
						entity.setTextTypeface(Typeface.NORMAL);
					}
					if("P".equalsIgnoreCase(isTaskRead)){
						entity.setTextTypeface(Typeface.NORMAL);
					}
					if (isTaskSync != null && isTaskSync.trim().equalsIgnoreCase("Y")) {
						if("Y".equalsIgnoreCase(isTaskRead)||user_Assign_ToNO.equalsIgnoreCase(curentMsisdn)){
							entity.setTaskSyncStatus(R.drawable.phone_withcheck_read);
						}else{
							entity.setTaskSyncStatus(R.drawable.phone_withcheck);
						}
					} else {
						entity.setTaskSyncStatus(R.drawable.phone_withuncheck);
					}

					if (taskType.trim().equalsIgnoreCase("Inbox")) {
						if (user_FromNo != null && !user_FromNo.trim().equals("")||user_FromNo!=null&&user_FromNo.trim().equals("")) {

							entity.setTaskAssigneeName(userFromName!=null?userFromName:user_FromNo);
							entity.setTaskStatusArrow(R.drawable.arriow_inward);

						}
					}
					else if (taskType.trim().equalsIgnoreCase("Sent")) {
						if (user_Assign_ToNO != null && !user_Assign_ToNO.trim().equals("")) {
							entity.setTaskAssigneeName(assinee!=null?assinee:user_Assign_ToNO);
							entity.setTaskStatusArrow(R.drawable.arriow_outward);
							entity.setUserRegistered(TaskEntity.isUserRegistered(context, user_Assign_ToNO));

						} else {
							entity.setTaskAssigneeName(assinee!=null?assinee:user_Assign_ToNO);
							entity.setTaskStatusArrow(R.drawable.arriow_outward);

						}
					} else {
						entity.setTaskAssigneeName("self");
						//entity.setMsgSyncStatusBackground(R.drawable.notefaction_check_read);
						//entity.setTaskSyncStatus(R.drawable.phone_withcheck_read);
						entity.setTextTypeface(Typeface.NORMAL);


					}

					boolean msgSync = false;
					int unreadMsg = 0;
					int readCount = 0;
					try {
						TaskEntity taskEntityObj = new TaskEntity();
						if (
								taskType != null) {
							if("true".equalsIgnoreCase(isMsgSend)){
								//unreadMsg=1;
								msgSync=true;
							}
							unreadMsg = taskEntityObj.unreadMessageCount(cursor.getString(0),context);
							readCount = taskEntityObj.isMessRead(cursor.getString(0),context);

							//msgSync = taskEntityObj.msgSyncStatus(cursor.getString(0),context);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}


					if(unreadMsg == 0)
					{

						if(msgSync){
							if(readCount==0){
								entity.setMsgSyncStatusBackground(R.drawable.notefaction_check_read);
								entity.setMsgCountColor(context.getResources().getColor(R.color.black));
							}else{

								entity.setMsgSyncStatusBackground(R.drawable.notefaction_check);
								entity.setMsgCountColor(context.getResources().getColor(R.color.black));
							}
						}else{
							//	entity.setIsSendMessageUnread("Y");
							entity.setMsgSyncStatusBackground(R.drawable.notefaction_cross);
							entity.setMsgCountColor(context.getResources().getColor(R.color.black));

						}		
					}else{
						if(taskType != null && taskType.trim().equalsIgnoreCase("inbox")||taskType.trim().equalsIgnoreCase("sent")&&unreadMsg>0){
							if(msgSync){
								entity.setMsgSyncStatusBackground(R.drawable.notefaction_checkwithredoutline);
								entity.setMsgCountColor(context.getResources().getColor(R.color.white));
								entity.setIsSendMessageUnread("N");
							}else{
								entity.setMsgSyncStatusBackground(R.drawable.notefaction_crosswithredoutline);
								//	entity.setIsSendMessageUnread("Y");
								entity.setMsgCountColor(context.getResources().getColor(R.color.white));

							}
						}
					}
					if(taskType.trim().equalsIgnoreCase("Inbox"))	{
						entity.setCalenderImage(R.drawable.calandericonline);
						entity.setEditIcon(R.drawable.edit_iconline);
						entity.setUserIcon(R.drawable.user_iconline);
						entity.setTextIconTextColor(context.getResources().getColor(R.color.light_gray));

					}else{
						entity.setCalenderImage(R.drawable.calander);
						entity.setEditIcon(R.drawable.edit_icon);
						entity.setUserIcon(R.drawable.user_icon);
						entity.setTextIconTextColor(context.getResources().getColor(R.color.name_blue));
					}





					if (taskType.trim().equalsIgnoreCase("Inbox")) 
					{
						entity.setFireBackground(R.drawable.fire_checkbox_line);

					}
					else
					{
						entity.setFireBackground(R.drawable.fire_checkbox);
					}
					if (proiority != null&& proiority.trim().length() > 0	&& proiority.trim().equalsIgnoreCase(context.getResources().getString(R.string.fire_priority_val))) {

						entity.setFireCheck(true);
						fireCount++;

					} else {

						entity.setFireCheck(false);
					}
					if (taskType.trim().equalsIgnoreCase("Inbox")||"CLOSE".equalsIgnoreCase(status)) 
					{
						entity.setFireBackground(R.drawable.fire_checkbox_line);
						entity.setFireEnable(false);
					}
					if(favourite)
					{
						favCount++;
					}
					if(taskUrl!=null && !taskUrl.trim().equals(""))
					{
						entity.setTaskUrl(taskUrl);
						entity.setUrlTaskBackground(context.getResources().getColor(R.color.reddish));
					}
					else
					{
						entity.setUrlTaskBackground(context.getResources().getColor(R.color.white));

					}




					UserInfoEntity assignFrom=new UserInfoEntity();
					UserInfoEntity assignTo=new UserInfoEntity();
					assignFrom.setMobile_number(cursor.getString(1));
					assignFrom.setFirstName(cursor.getString(2));
					assignTo.setMobile_number(user_Assign_ToNO);
					assignTo.setFirstName(assinee);
					entity.setUserInfoFrom(assignFrom);
					entity.setUserInfoTo(assignTo);
					entity.setTask_description(cursor.getString(5));
					entity.setPriority(cursor.getString(6));
					entity.setCloser_date(cursor.getString(8));
					entity.setTarget_date((cursor.getString(9)));
					entity.setFire_flag(cursor.getString(10));
					entity.setReminder_time(cursor.getString(11));
					entity.setIsReminder(cursor.getString(12));
					entity.setIsMessage(cursor.getString(13));
					entity.setStatus(status);
					entity.setIsFavouraite(cursor.getString(15));
					entity.setTaskType(cursor.getString(16));
					entity.setCreation_date(cursor.getString(17));
					entity.setIsTaskSync(cursor.getString(cursor.getColumnIndex("IsTaskSync")));
					entity.setIsJunk(cursor.getString(cursor.getColumnIndex("IsJunk")));
					entity.setAlarmTime(cursor.getString(cursor.getColumnIndex("AlarmTime")));
					entity.setGroupId(cursor.getString(cursor.getColumnIndex("Group_ID")));
					entity.setIsAssigneeSync(cursor.getString(27));
					entity.setIsTargetSync(cursor.getString(28));
					entity.setIsStatusSync(cursor.getString(20));
					entity.setIsReminderSync(cursor.getString(21));
					entity.setTotalMessage(cursor.getString(29));
					entity.setUnreadMessage(cursor.getString(30));//
					entity.setIsTaskRead(cursor.getString(cursor.getColumnIndex("IsTaskRead")));
					entity.setClosedBy(cursor.getString(cursor.getColumnIndex("ClosedBy")));
					entity.setIsActive(isActive);
					entity.setClosureDate(cursor.getString(cursor.getColumnIndex("Closure_Date")));
					entity.setCreationDateTime(cursor.getString(cursor.getColumnIndex("ClosureDate")));
					entity.setIsAlarmSet(cursor.getString(cursor.getColumnIndex("isAlarmSet")));
					entity.setAlarm_Date_Time(cursor.getString(cursor.getColumnIndex("Alarm_Date_Time")));
					entity.setUpdatedDate(cursor.getString(cursor.getColumnIndex("UPDATED_DATE")));
					if(entity.getTaskType().equalsIgnoreCase("self")){
						entity.setIsTaskSync("Y");
						entity.setTaskSyncStatus(R.drawable.phone_withcheck_read);

					}else{
						entity.setIsTaskSync(cursor.getString(cursor.getColumnIndex("IsTaskSync")));
	
					}
					entities.add(entity);
				} while (cursor.moveToNext());
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if(cursor!=null)			
				cursor.close();
		}
		mapValue = new HashMap();
		mapValue.put("list", entities);
		mapValue.put("fireCount", fireCount);
		mapValue.put("favCount", favCount);
		return mapValue;
	}


	public Map getTaskByTaskId(String taskId,Context context ){
		HashMap mapValue= null;
		Cursor cursor = null;
		int fireCount = 0;
		int favCount = 0;
		String query=" SELECT *  FROM  Task where Task_ID in("+taskId+")";
		List<TaskInfoEntity> entities=new ArrayList<TaskInfoEntity>();
		String curentMsisdn=ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context);
		//
		try
		{
			openDataBase();
			cursor = myDataBase.rawQuery(query, null);
			if (cursor.moveToFirst()) {
				do {
					TaskInfoEntity entity = new TaskInfoEntity();
					String isActive = cursor.getString(cursor.getColumnIndex("isActive"));
					String status= cursor.getString(14);
					String user_FromNo =cursor.getString(1);
					String user_Assign_ToNO =CommonUtil.getValidMsisdn(cursor.getString(3));
					String assinee=cursor.getString(cursor.getColumnIndex("Assign_To_Name"));
					String isMsgSend=cursor.getString(cursor.getColumnIndex("isMsgSend"));
					String creation_date = cursor.getString(17);
					String isAlarmSet = cursor.getString(cursor.getColumnIndex("isAlarmSet"));
					String isTaskRead = cursor.getString(cursor.getColumnIndex("IsTaskRead"));
					String isFavourite = cursor.getString(15);
					String isTaskSync = cursor.getString(cursor.getColumnIndex("IsTaskSync"));
					String taskType = cursor.getString(16);
					String userFromName = cursor.getString(2);
					String proiority = cursor.getString(6);
					String taskUrl   = cursor.getString(cursor.getColumnIndex("TASK_URL"));

					boolean favourite = Boolean.parseBoolean(cursor.getString(15));
					if(isAlarmSet!=null&& isAlarmSet.equalsIgnoreCase("Y"))
					{
						//holder.alarmIcon.setBackgroundResource(R.drawable.alarm_blue);
						entity.setAlarmBackground(R.drawable.alarm_blue_icon);
					}
					else
					{
						entity.setAlarmBackground(R.drawable.alarm_blue_disable);
					}
					entity.setTextTypeface(Typeface.NORMAL);
					if (isTaskSync != null && isTaskSync.trim().equalsIgnoreCase("Y")) {
						if("Y".equalsIgnoreCase(isTaskRead)||user_Assign_ToNO.equalsIgnoreCase(curentMsisdn)){
							entity.setTaskSyncStatus(R.drawable.phone_withcheck_read);
						}else{
							entity.setTaskSyncStatus(R.drawable.phone_withcheck);
						}
					} else {
						entity.setTaskSyncStatus(R.drawable.phone_withuncheck);
					}

					if (taskType.trim().equalsIgnoreCase("Inbox")) {
						if (user_FromNo != null && !user_FromNo.trim().equals("")||user_FromNo!=null&&user_FromNo.trim().equals("")) {

							entity.setTaskAssigneeName(userFromName!=null?userFromName:user_FromNo);
							entity.setTaskStatusArrow(R.drawable.arriow_inward);

						}
					}
					else if (taskType.trim().equalsIgnoreCase("Sent")) {
						if (user_Assign_ToNO != null && !user_Assign_ToNO.trim().equals("")) {
							entity.setTaskAssigneeName(assinee!=null?assinee:user_Assign_ToNO);
							entity.setTaskStatusArrow(R.drawable.arriow_outward);

						} else {
							entity.setTaskAssigneeName(assinee!=null?assinee:user_Assign_ToNO);
							entity.setTaskStatusArrow(R.drawable.arriow_outward);

						}
					} else {
						entity.setTaskAssigneeName("self");
						//	entity.setTaskSyncStatus(R.drawable.phone_withcheck_read);
						entity.setTextTypeface(Typeface.NORMAL);


					}

					boolean msgSync = false;
					int unreadMsg = 0;
					int readCount = 0;
					try {
						TaskEntity taskEntityObj = new TaskEntity();
						if (
								taskType != null) {
							if("true".equalsIgnoreCase(isMsgSend)){
								//unreadMsg=1;
								msgSync=true;
							}
							unreadMsg = taskEntityObj.unreadMessageCount(cursor.getString(0),context);
							readCount = taskEntityObj.isMessRead(cursor.getString(0),context);

							//msgSync = taskEntityObj.msgSyncStatus(cursor.getString(0),context);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}


					if(unreadMsg == 0)
					{

						if(msgSync){
							if(readCount==0){
								entity.setMsgSyncStatusBackground(R.drawable.notefaction_check_read);
								entity.setMsgCountColor(context.getResources().getColor(R.color.black));
							}else{
								entity.setIsSendMessageUnread("Y");
								entity.setMsgSyncStatusBackground(R.drawable.notefaction_check);
								entity.setMsgCountColor(context.getResources().getColor(R.color.black));
							}
						}else{
							entity.setMsgSyncStatusBackground(R.drawable.notefaction_cross);
							entity.setMsgCountColor(context.getResources().getColor(R.color.black));

						}		
					}else{
						if(taskType != null && taskType.trim().equalsIgnoreCase("inbox")||taskType.trim().equalsIgnoreCase("sent")&&unreadMsg>0){
							if(msgSync){
								entity.setMsgSyncStatusBackground(R.drawable.notefaction_checkwithredoutline);
								entity.setMsgCountColor(context.getResources().getColor(R.color.white));
							}else{
								entity.setMsgSyncStatusBackground(R.drawable.notefaction_crosswithredoutline);
								entity.setMsgCountColor(context.getResources().getColor(R.color.white));

							}
						}
					}
					if(taskType.trim().equalsIgnoreCase("Inbox"))	{
						entity.setCalenderImage(R.drawable.calandericonline);
						entity.setEditIcon(R.drawable.edit_iconline);
						entity.setUserIcon(R.drawable.user_iconline);
						entity.setTextIconTextColor(context.getResources().getColor(R.color.light_gray));

					}else{
						entity.setCalenderImage(R.drawable.calander);
						entity.setEditIcon(R.drawable.edit_icon);
						entity.setUserIcon(R.drawable.user_icon);
						entity.setTextIconTextColor(context.getResources().getColor(R.color.name_blue));
					}





					if (taskType.trim().equalsIgnoreCase("Inbox")) 
					{
						entity.setFireBackground(R.drawable.fire_checkbox_line);

					}
					else
					{
						entity.setFireBackground(R.drawable.fire_checkbox);
					}
					if (proiority != null&& proiority.trim().length() > 0	&& proiority.trim().equalsIgnoreCase(context.getResources().getString(R.string.fire_priority_val))) {

						entity.setFireCheck(true);
						fireCount++;

					} else {

						entity.setFireCheck(false);
					}
					if (taskType.trim().equalsIgnoreCase("Inbox")||"CLOSE".equalsIgnoreCase(status)) 
					{
						entity.setFireBackground(R.drawable.fire_checkbox_line);
						entity.setFireEnable(false);
					}
					if(favourite)
					{
						favCount++;
					}
					if(taskUrl!=null && !taskUrl.trim().equals(""))
					{
						entity.setTaskUrl(taskUrl);
						entity.setUrlTaskBackground(context.getResources().getColor(R.color.reddish));
					}
					else
					{
						entity.setUrlTaskBackground(context.getResources().getColor(R.color.white));

					}


					entity.setMsgCount(getTotalMessageForTask(cursor.getString(0)));

					UserInfoEntity assignFrom=new UserInfoEntity();
					UserInfoEntity assignTo=new UserInfoEntity();
					entity.setId(cursor.getString(0));
					assignFrom.setMobile_number(cursor.getString(1));
					assignFrom.setFirstName(cursor.getString(2));
					assignTo.setMobile_number(user_Assign_ToNO);
					assignTo.setFirstName(assinee);
					entity.setUserInfoFrom(assignFrom);
					entity.setUserInfoTo(assignTo);
					entity.setTask_description(cursor.getString(5));
					entity.setPriority(cursor.getString(6));
					entity.setCloser_date(cursor.getString(8));
					entity.setTarget_date((cursor.getString(9)));
					entity.setFire_flag(cursor.getString(10));
					entity.setReminder_time(cursor.getString(11));
					entity.setIsReminder(cursor.getString(12));
					entity.setIsMessage(cursor.getString(13));
					entity.setStatus(status);
					entity.setIsFavouraite(cursor.getString(15));
					entity.setTaskType(cursor.getString(16));
					entity.setCreation_date(cursor.getString(17));
					entity.setIsTaskSync(cursor.getString(cursor.getColumnIndex("IsTaskSync")));
					entity.setIsJunk(cursor.getString(cursor.getColumnIndex("IsJunk")));
					entity.setAlarmTime(cursor.getString(cursor.getColumnIndex("AlarmTime")));
					entity.setGroupId(cursor.getString(cursor.getColumnIndex("Group_ID")));
					entity.setIsAssigneeSync(cursor.getString(27));
					entity.setIsTargetSync(cursor.getString(28));
					entity.setIsStatusSync(cursor.getString(20));
					entity.setIsReminderSync(cursor.getString(21));
					entity.setTotalMessage(cursor.getString(29));
					entity.setUnreadMessage(cursor.getString(30));//
					entity.setIsTaskRead(cursor.getString(cursor.getColumnIndex("IsTaskRead")));
					entity.setClosedBy(cursor.getString(cursor.getColumnIndex("ClosedBy")));
					entity.setIsActive(isActive);
					entity.setClosureDate(cursor.getString(cursor.getColumnIndex("Closure_Date")));
					entity.setCreationDateTime(cursor.getString(cursor.getColumnIndex("ClosureDate")));
					entity.setIsAlarmSet(cursor.getString(cursor.getColumnIndex("isAlarmSet")));
					entity.setAlarm_Date_Time(cursor.getString(cursor.getColumnIndex("Alarm_Date_Time")));
					entity.setUpdatedDate(cursor.getString(cursor.getColumnIndex("UPDATED_DATE")));
					entities.add(entity);
				} while (cursor.moveToNext());
			}
			if(cursor!=null)			
				cursor.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if(cursor!=null)			
				cursor.close();
			close();
		}
		mapValue = new HashMap();
		mapValue.put("list", entities);
		mapValue.put("fireCount", fireCount);
		mapValue.put("favCount", favCount);
		return mapValue;
	}
	/**
	 * Close Task List
	 * @param query
	 * @param context
	 * @return
	 */
	public Map getTaskListClose(String query,final Context context ){
		HashMap mapValue= null;
		Cursor cursor = null;
		int fireCount = 0;
		int favCount = 0;

		List<TaskInfoEntity> entities=new ArrayList<TaskInfoEntity>();
		final String curentMsisdn=CommonUtil.getValidMsisdn(ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context));
		//
		try
		{
			openReadDataBase();
			cursor = myDataBase.rawQuery(query, null);
			if (cursor.moveToFirst()) {
				do {
					final TaskInfoEntity entity = new TaskInfoEntity();
					entity.setId(cursor.getString(0));
					entity.setIsTaskSnooz(cursor.getString(cursor.getColumnIndex("isTaskSnooz")));
					entity.setTargetDateTime(cursor.getString(cursor.getColumnIndex("Target_Date_TIME")));
					String isActive = cursor.getString(cursor.getColumnIndex("isActive"));
					String status= cursor.getString(14);
					final String user_FromNo =cursor.getString(1);
					final String user_Assign_ToNO =CommonUtil.getValidMsisdn(cursor.getString(3));
					String assinee=cursor.getString(cursor.getColumnIndex("Assign_To_Name"));
					String isMsgSend=cursor.getString(cursor.getColumnIndex("isMsgSend"));
					String creation_date = cursor.getString(17);
					String isAlarmSet = cursor.getString(cursor.getColumnIndex("isAlarmSet"));
					String isTaskRead = cursor.getString(cursor.getColumnIndex("IsTaskRead"));
					String isFavourite = cursor.getString(15);
					String isTaskSync = cursor.getString(cursor.getColumnIndex("IsTaskSync"));
					String taskType = cursor.getString(16);
					String userFromName = cursor.getString(2);
					String proiority = cursor.getString(6);
					String taskUrl   = cursor.getString(cursor.getColumnIndex("TASK_URL"));
					entity.setIsTaskRead(isTaskRead);
					entity.setMsgCount(getTotalMessageForTask(cursor.getString(0)));
					if(("N".equalsIgnoreCase(isTaskRead)||"P".equalsIgnoreCase(isTaskRead))&&(!CommonUtil.getValidMsisdn(user_FromNo).equalsIgnoreCase(curentMsisdn))){
						new Thread(new Runnable() {

							@Override
							public void run() {
								BGConnector connector=new BGConnectorImpl();
								boolean flag=false;
								try {
									ContentValues initialValues = new ContentValues();
									initialValues.put("IsTaskRead", "P");
									ApplicationUtil.getInstance().updateDataInDB("Task", initialValues, context,"Task_ID = '" + entity.getId() + "'", null);
									flag = connector.markTaskAsRead(entity.getId(), ApplicationUtil.getPreference(ApplicationConstant.regMobNo,context),user_Assign_ToNO, ApplicationUtil.getPreference(ApplicationConstant.xtoken,context));
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								if(flag){
									ContentValues initialValues = new ContentValues();
									initialValues.put("IsTaskRead", "Y");
									ApplicationUtil.getInstance().updateDataInDB("Task", initialValues, context,"Task_ID = '" + entity.getId() + "'", null);
								}else{
									ContentValues initialValues = new ContentValues();
									initialValues.put("IsTaskRead", "P");
									ApplicationUtil.getInstance().updateDataInDB("Task", initialValues, context,"Task_ID = '" + entity.getId() + "'", null);
								}

							}

						}).start();
					}
					boolean favourite = Boolean.parseBoolean(cursor.getString(15));
					if(isAlarmSet!=null&& isAlarmSet.equalsIgnoreCase("Y"))
					{
						entity.setAlarmBackground(R.drawable.alarm_blue_icon);
					}
					else
					{
						entity.setAlarmBackground(R.drawable.alarm_blue_disable);
					}
					if (isTaskRead != null && isTaskRead.trim().equalsIgnoreCase("N")) {
						if(!curentMsisdn.equalsIgnoreCase(CommonUtil.getValidMsisdn(user_FromNo))){
							entity.setTextTypeface(Typeface.BOLD);
						}	

					}
					else
					{	
						entity.setTextTypeface(Typeface.NORMAL);
					}
					if("P".equalsIgnoreCase(isTaskRead)){
						entity.setTextTypeface(Typeface.NORMAL);
					}
					if (isTaskSync != null && isTaskSync.trim().equalsIgnoreCase("Y")) {
						if("Y".equalsIgnoreCase(isTaskRead)||user_Assign_ToNO.equalsIgnoreCase(curentMsisdn)){
							entity.setTaskSyncStatus(R.drawable.phone_withcheck_read);
						}else{
							entity.setTaskSyncStatus(R.drawable.phone_withcheck);
						}
					} else {
						entity.setTaskSyncStatus(R.drawable.phone_withuncheck);
					}

					if (taskType.trim().equalsIgnoreCase("Inbox")) {
						if (user_FromNo != null && !user_FromNo.trim().equals("")||user_FromNo!=null&&user_FromNo.trim().equals("")) {

							entity.setTaskAssigneeName(userFromName!=null?userFromName:user_FromNo);
							entity.setTaskStatusArrow(R.drawable.arriow_inward);

						}
					}
					else if (taskType.trim().equalsIgnoreCase("Sent")) {
						if (user_Assign_ToNO != null && !user_Assign_ToNO.trim().equals("")) {
							entity.setTaskAssigneeName(assinee!=null?assinee:user_Assign_ToNO);
							entity.setTaskStatusArrow(R.drawable.arriow_outward);
							entity.setUserRegistered(TaskEntity.isUserRegistered(context, user_Assign_ToNO));

						} else {
							entity.setTaskAssigneeName(assinee!=null?assinee:user_Assign_ToNO);
							entity.setTaskStatusArrow(R.drawable.arriow_outward);

						}
					} else {
						entity.setTaskAssigneeName("self");
						//entity.setMsgSyncStatusBackground(R.drawable.notefaction_check_read);
						//entity.setTaskSyncStatus(R.drawable.phone_withcheck_read);
						entity.setTextTypeface(Typeface.NORMAL);


					}

					boolean msgSync = false;
					int unreadMsg = 0;
					int readCount = 0;
					try {
						TaskEntity taskEntityObj = new TaskEntity();
						if (
								taskType != null) {
							if("true".equalsIgnoreCase(isMsgSend)){
								//unreadMsg=1;
								msgSync=true;
							}
							unreadMsg = taskEntityObj.unreadMessageCount(cursor.getString(0),context);
							readCount = taskEntityObj.isMessRead(cursor.getString(0),context);

							//msgSync = taskEntityObj.msgSyncStatus(cursor.getString(0),context);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}


					if(unreadMsg == 0)
					{

						if(msgSync){
							if(readCount==0){
								entity.setMsgSyncStatusBackground(R.drawable.notefaction_check_read);
								entity.setMsgCountColor(context.getResources().getColor(R.color.black));
							}else{

								entity.setMsgSyncStatusBackground(R.drawable.notefaction_check);
								entity.setMsgCountColor(context.getResources().getColor(R.color.black));
							}
						}else{
							//	entity.setIsSendMessageUnread("Y");
							entity.setMsgSyncStatusBackground(R.drawable.notefaction_cross);
							entity.setMsgCountColor(context.getResources().getColor(R.color.black));

						}		
					}else{
						if(taskType != null && taskType.trim().equalsIgnoreCase("inbox")||taskType.trim().equalsIgnoreCase("sent")&&unreadMsg>0){
							if(msgSync){
								entity.setMsgSyncStatusBackground(R.drawable.notefaction_checkwithredoutline);
								entity.setMsgCountColor(context.getResources().getColor(R.color.white));
								entity.setIsSendMessageUnread("N");
							}else{
								entity.setMsgSyncStatusBackground(R.drawable.notefaction_crosswithredoutline);
								//	entity.setIsSendMessageUnread("Y");
								entity.setMsgCountColor(context.getResources().getColor(R.color.white));

							}
						}
					}
					if(taskType.trim().equalsIgnoreCase("Inbox"))	{
						entity.setCalenderImage(R.drawable.calandericonline);
						entity.setEditIcon(R.drawable.edit_iconline);
						entity.setUserIcon(R.drawable.user_iconline);
						entity.setTextIconTextColor(context.getResources().getColor(R.color.light_gray));

					}else{
						entity.setCalenderImage(R.drawable.calander);
						entity.setEditIcon(R.drawable.edit_icon);
						entity.setUserIcon(R.drawable.user_icon);
						entity.setTextIconTextColor(context.getResources().getColor(R.color.name_blue));
					}





					if (taskType.trim().equalsIgnoreCase("Inbox")) 
					{
						entity.setFireBackground(R.drawable.fire_checkbox_line);

					}
					else
					{
						entity.setFireBackground(R.drawable.fire_checkbox);
					}
					if (proiority != null&& proiority.trim().length() > 0	&& proiority.trim().equalsIgnoreCase(context.getResources().getString(R.string.fire_priority_val))) {

						entity.setFireCheck(true);
						fireCount++;

					} else {

						entity.setFireCheck(false);
					}
					if (taskType.trim().equalsIgnoreCase("Inbox")||"CLOSE".equalsIgnoreCase(status)) 
					{
						entity.setFireBackground(R.drawable.fire_checkbox_line);
						entity.setFireEnable(false);
					}
					if(favourite)
					{
						favCount++;
					}
					if(taskUrl!=null && !taskUrl.trim().equals(""))
					{
						entity.setTaskUrl(taskUrl);
						entity.setUrlTaskBackground(context.getResources().getColor(R.color.reddish));
					}
					else
					{
						entity.setUrlTaskBackground(context.getResources().getColor(R.color.white));

					}




					UserInfoEntity assignFrom=new UserInfoEntity();
					UserInfoEntity assignTo=new UserInfoEntity();
					assignFrom.setMobile_number(cursor.getString(1));
					assignFrom.setFirstName(cursor.getString(2));
					assignTo.setMobile_number(user_Assign_ToNO);
					assignTo.setFirstName(assinee);
					entity.setUserInfoFrom(assignFrom);
					entity.setUserInfoTo(assignTo);
					entity.setTask_description(cursor.getString(5));
					entity.setPriority(cursor.getString(6));
					entity.setCloser_date(cursor.getString(8));
					entity.setTarget_date((cursor.getString(9)));
					entity.setFire_flag(cursor.getString(10));
					entity.setReminder_time(cursor.getString(11));
					entity.setIsReminder(cursor.getString(12));
					entity.setIsMessage(cursor.getString(13));
					entity.setStatus(status);
					entity.setIsFavouraite(cursor.getString(15));
					entity.setTaskType(cursor.getString(16));
					entity.setCreation_date(cursor.getString(17));
					entity.setIsTaskSync(cursor.getString(cursor.getColumnIndex("IsTaskSync")));
					entity.setIsJunk(cursor.getString(cursor.getColumnIndex("IsJunk")));
					entity.setAlarmTime(cursor.getString(cursor.getColumnIndex("AlarmTime")));
					entity.setGroupId(cursor.getString(cursor.getColumnIndex("Group_ID")));
					entity.setIsAssigneeSync(cursor.getString(27));
					entity.setIsTargetSync(cursor.getString(28));
					entity.setIsStatusSync(cursor.getString(20));
					entity.setIsReminderSync(cursor.getString(21));
					entity.setTotalMessage(cursor.getString(29));
					entity.setUnreadMessage(cursor.getString(30));//
					entity.setIsTaskRead(cursor.getString(cursor.getColumnIndex("IsTaskRead")));
					entity.setClosedBy(cursor.getString(cursor.getColumnIndex("ClosedBy")));
					entity.setIsActive(isActive);
					entity.setClosureDate(cursor.getString(cursor.getColumnIndex("Closure_Date")));
					entity.setCreationDateTime(cursor.getString(cursor.getColumnIndex("ClosureDate")));
					entity.setIsAlarmSet(cursor.getString(cursor.getColumnIndex("isAlarmSet")));
					entity.setAlarm_Date_Time(cursor.getString(cursor.getColumnIndex("Alarm_Date_Time")));
					entity.setUpdatedDate(cursor.getString(cursor.getColumnIndex("UPDATED_DATE")));
					entities.add(entity);
				} while (cursor.moveToNext());
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if(cursor!=null)			
				cursor.close();
		}
		mapValue = new HashMap();
		mapValue.put("list", entities);
		mapValue.put("fireCount", fireCount);
		mapValue.put("favCount", favCount);
		return mapValue;
	}





	public Map getCreateContactList(String query,final Context context ){
		HashMap mapValue= null;
		Cursor cursor = null;
		int fireCount = 0;
		int favCount = 0;

		List<TaskInfoEntity> entities=new ArrayList<TaskInfoEntity>();
		final String curentMsisdn=CommonUtil.getValidMsisdn(ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context));
		//
		try
		{
			openReadDataBase();
			cursor = myDataBase.rawQuery(query, null);
			if (cursor.moveToFirst()) {
				do {
					final TaskInfoEntity entity = new TaskInfoEntity();
					entity.setDisplayName(cursor.getString(cursor.getColumnIndex("DISPLAY_NAME")));
					entity.setId(cursor.getString(0));
					entity.setIsTaskSnooz(cursor.getString(cursor.getColumnIndex("isTaskSnooz")));
					entity.setTargetDateTime(cursor.getString(cursor.getColumnIndex("Target_Date_TIME")));
					String isActive = cursor.getString(cursor.getColumnIndex("isActive"));
					String status= cursor.getString(14);
					final String user_FromNo =cursor.getString(1);
					final String user_Assign_ToNO =CommonUtil.getValidMsisdn(cursor.getString(3));
					String assinee=cursor.getString(cursor.getColumnIndex("Assign_To_Name"));
					String isMsgSend=cursor.getString(cursor.getColumnIndex("isMsgSend"));
					String creation_date = cursor.getString(17);
					String isAlarmSet = cursor.getString(cursor.getColumnIndex("isAlarmSet"));
					String isTaskRead = cursor.getString(cursor.getColumnIndex("IsTaskRead"));
					String isFavourite = cursor.getString(15);
					String isTaskSync = cursor.getString(cursor.getColumnIndex("IsTaskSync"));
					String taskType = cursor.getString(16);
					String userFromName = cursor.getString(2);
					String proiority = cursor.getString(6);
					String taskUrl   = cursor.getString(cursor.getColumnIndex("TASK_URL"));
					entity.setIsTaskRead(isTaskRead);
					entity.setMsgCount(getTotalMessageForTask(cursor.getString(0)));
					if(("N".equalsIgnoreCase(isTaskRead)||"P".equalsIgnoreCase(isTaskRead))&&(!CommonUtil.getValidMsisdn(user_FromNo).equalsIgnoreCase(curentMsisdn))){
						new Thread(new Runnable() {

							@Override
							public void run() {
								BGConnector connector=new BGConnectorImpl();
								boolean flag=false;
								try {
									ContentValues initialValues = new ContentValues();
									initialValues.put("IsTaskRead", "P");
									ApplicationUtil.getInstance().updateDataInDB("Task", initialValues, context,"Task_ID = '" + entity.getId() + "'", null);
									flag = connector.markTaskAsRead(entity.getId(), ApplicationUtil.getPreference(ApplicationConstant.regMobNo,context),user_Assign_ToNO, ApplicationUtil.getPreference(ApplicationConstant.xtoken,context));
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								if(flag){
									ContentValues initialValues = new ContentValues();
									initialValues.put("IsTaskRead", "Y");
									ApplicationUtil.getInstance().updateDataInDB("Task", initialValues, context,"Task_ID = '" + entity.getId() + "'", null);
								}else{
									ContentValues initialValues = new ContentValues();
									initialValues.put("IsTaskRead", "P");
									ApplicationUtil.getInstance().updateDataInDB("Task", initialValues, context,"Task_ID = '" + entity.getId() + "'", null);
								}

							}

						}).start();
					}
					boolean favourite = Boolean.parseBoolean(cursor.getString(15));
					if(isAlarmSet!=null&& isAlarmSet.equalsIgnoreCase("Y"))
					{
						entity.setAlarmBackground(R.drawable.alarm_blue_icon);
					}
					else
					{
						entity.setAlarmBackground(R.drawable.alarm_blue_disable);
					}
					if (isTaskRead != null && isTaskRead.trim().equalsIgnoreCase("N")) {
						if(!curentMsisdn.equalsIgnoreCase(CommonUtil.getValidMsisdn(user_FromNo))){
							entity.setTextTypeface(Typeface.BOLD);
						}	

					}
					else
					{	
						entity.setTextTypeface(Typeface.NORMAL);
					}
					if("P".equalsIgnoreCase(isTaskRead)){
						entity.setTextTypeface(Typeface.NORMAL);
					}
					if (isTaskSync != null && isTaskSync.trim().equalsIgnoreCase("Y")) {
						if("Y".equalsIgnoreCase(isTaskRead)||user_Assign_ToNO.equalsIgnoreCase(curentMsisdn)){
							entity.setTaskSyncStatus(R.drawable.phone_withcheck_read);
						}else{
							entity.setTaskSyncStatus(R.drawable.phone_withcheck);
						}
					} else {
						entity.setTaskSyncStatus(R.drawable.phone_withuncheck);
					}

					if (taskType.trim().equalsIgnoreCase("Inbox")) {
						if (user_FromNo != null && !user_FromNo.trim().equals("")||user_FromNo!=null&&user_FromNo.trim().equals("")) {

							entity.setTaskAssigneeName(userFromName!=null?userFromName:user_FromNo);
							entity.setTaskStatusArrow(R.drawable.arriow_inward);

						}
					}
					else if (taskType.trim().equalsIgnoreCase("Sent")) {
						if (user_Assign_ToNO != null && !user_Assign_ToNO.trim().equals("")) {
							entity.setTaskAssigneeName(assinee!=null?assinee:user_Assign_ToNO);
							entity.setTaskStatusArrow(R.drawable.arriow_outward);
							entity.setUserRegistered(TaskEntity.isUserRegistered(context, user_Assign_ToNO));

						} else {
							entity.setTaskAssigneeName(assinee!=null?assinee:user_Assign_ToNO);
							entity.setTaskStatusArrow(R.drawable.arriow_outward);

						}
					} else {
						entity.setTaskAssigneeName("self");
						//entity.setMsgSyncStatusBackground(R.drawable.notefaction_check_read);
						//entity.setTaskSyncStatus(R.drawable.phone_withcheck_read);
						entity.setTextTypeface(Typeface.NORMAL);


					}

					boolean msgSync = false;
					int unreadMsg = 0;
					int readCount = 0;
					try {
						TaskEntity taskEntityObj = new TaskEntity();
						if (
								taskType != null) {
							if("true".equalsIgnoreCase(isMsgSend)){
								//unreadMsg=1;
								msgSync=true;
							}
							unreadMsg = taskEntityObj.unreadMessageCount(cursor.getString(0),context);
							readCount = taskEntityObj.isMessRead(cursor.getString(0),context);

							//msgSync = taskEntityObj.msgSyncStatus(cursor.getString(0),context);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}


					if(unreadMsg == 0)
					{

						if(msgSync){
							if(readCount==0){
								entity.setMsgSyncStatusBackground(R.drawable.notefaction_check_read);
								entity.setMsgCountColor(context.getResources().getColor(R.color.black));
							}else{

								entity.setMsgSyncStatusBackground(R.drawable.notefaction_check);
								entity.setMsgCountColor(context.getResources().getColor(R.color.black));
							}
						}else{
							//	entity.setIsSendMessageUnread("Y");
							entity.setMsgSyncStatusBackground(R.drawable.notefaction_cross);
							entity.setMsgCountColor(context.getResources().getColor(R.color.black));

						}		
					}else{
						if(taskType != null && taskType.trim().equalsIgnoreCase("inbox")||taskType.trim().equalsIgnoreCase("sent")&&unreadMsg>0){
							if(msgSync){
								entity.setMsgSyncStatusBackground(R.drawable.notefaction_checkwithredoutline);
								entity.setMsgCountColor(context.getResources().getColor(R.color.white));
								entity.setIsSendMessageUnread("N");
							}else{
								entity.setMsgSyncStatusBackground(R.drawable.notefaction_crosswithredoutline);
								//	entity.setIsSendMessageUnread("Y");
								entity.setMsgCountColor(context.getResources().getColor(R.color.white));

							}
						}
					}
					if(taskType.trim().equalsIgnoreCase("Inbox"))	{
						entity.setCalenderImage(R.drawable.calandericonline);
						entity.setEditIcon(R.drawable.edit_iconline);
						entity.setUserIcon(R.drawable.user_iconline);
						entity.setTextIconTextColor(context.getResources().getColor(R.color.light_gray));

					}else{
						entity.setCalenderImage(R.drawable.calander);
						entity.setEditIcon(R.drawable.edit_icon);
						entity.setUserIcon(R.drawable.user_icon);
						entity.setTextIconTextColor(context.getResources().getColor(R.color.name_blue));
					}





					if (taskType.trim().equalsIgnoreCase("Inbox")) 
					{
						entity.setFireBackground(R.drawable.fire_checkbox_line);

					}
					else
					{
						entity.setFireBackground(R.drawable.fire_checkbox);
					}
					if (proiority != null&& proiority.trim().length() > 0	&& proiority.trim().equalsIgnoreCase(context.getResources().getString(R.string.fire_priority_val))) {

						entity.setFireCheck(true);
						fireCount++;

					} else {

						entity.setFireCheck(false);
					}
					if (taskType.trim().equalsIgnoreCase("Inbox")||"CLOSE".equalsIgnoreCase(status)) 
					{
						entity.setFireBackground(R.drawable.fire_checkbox_line);
						entity.setFireEnable(false);
					}
					if(favourite)
					{
						favCount++;
					}
					if(taskUrl!=null && !taskUrl.trim().equals(""))
					{
						entity.setTaskUrl(taskUrl);
						entity.setUrlTaskBackground(context.getResources().getColor(R.color.reddish));
					}
					else
					{
						entity.setUrlTaskBackground(context.getResources().getColor(R.color.white));

					}




					UserInfoEntity assignFrom=new UserInfoEntity();
					UserInfoEntity assignTo=new UserInfoEntity();
					assignFrom.setMobile_number(cursor.getString(1));
					assignFrom.setFirstName(cursor.getString(2));
					assignTo.setMobile_number(user_Assign_ToNO);
					assignTo.setFirstName(assinee);
					entity.setUserInfoFrom(assignFrom);
					entity.setUserInfoTo(assignTo);
					entity.setTask_description(cursor.getString(5));
					entity.setPriority(cursor.getString(6));
					entity.setCloser_date(cursor.getString(8));
					entity.setTarget_date((cursor.getString(9)));
					entity.setFire_flag(cursor.getString(10));
					entity.setReminder_time(cursor.getString(11));
					entity.setIsReminder(cursor.getString(12));
					entity.setIsMessage(cursor.getString(13));
					entity.setStatus(status);
					entity.setIsFavouraite(cursor.getString(15));
					entity.setTaskType(cursor.getString(16));
					entity.setCreation_date(cursor.getString(17));
					entity.setIsTaskSync(cursor.getString(cursor.getColumnIndex("IsTaskSync")));
					entity.setIsJunk(cursor.getString(cursor.getColumnIndex("IsJunk")));
					entity.setAlarmTime(cursor.getString(cursor.getColumnIndex("AlarmTime")));
					entity.setGroupId(cursor.getString(cursor.getColumnIndex("Group_ID")));
					entity.setIsAssigneeSync(cursor.getString(27));
					entity.setIsTargetSync(cursor.getString(28));
					entity.setIsStatusSync(cursor.getString(20));
					entity.setIsReminderSync(cursor.getString(21));
					entity.setTotalMessage(cursor.getString(29));
					entity.setUnreadMessage(cursor.getString(30));//
					entity.setIsTaskRead(cursor.getString(cursor.getColumnIndex("IsTaskRead")));
					entity.setClosedBy(cursor.getString(cursor.getColumnIndex("ClosedBy")));
					entity.setIsActive(isActive);
					entity.setClosureDate(cursor.getString(cursor.getColumnIndex("Closure_Date")));
					entity.setCreationDateTime(cursor.getString(cursor.getColumnIndex("ClosureDate")));
					entity.setIsAlarmSet(cursor.getString(cursor.getColumnIndex("isAlarmSet")));
					entity.setAlarm_Date_Time(cursor.getString(cursor.getColumnIndex("Alarm_Date_Time")));
					entity.setUpdatedDate(cursor.getString(cursor.getColumnIndex("UPDATED_DATE")));
					if(entity.getTaskType().equalsIgnoreCase("self")){
						entity.setIsTaskSync("Y");
						entity.setTaskSyncStatus(R.drawable.phone_withcheck_read);

					}else{
						entity.setIsTaskSync(cursor.getString(cursor.getColumnIndex("IsTaskSync")));
	
					}
					entities.add(entity);
				} while (cursor.moveToNext());
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if(cursor!=null)			
				cursor.close();
		}
		mapValue = new HashMap();
		mapValue.put("list", entities);
		mapValue.put("fireCount", fireCount);
		mapValue.put("favCount", favCount);
		return mapValue;
	}


	/**
	 * apply raw Query
	 * 
	 * @param query
	 * @param selectionArgs
	 * @return ArrayList
	 */
	public synchronized ArrayList<ArrayList<String>> selectRecordsFromDBArrayList(String query) {

		ArrayList<ArrayList<String>> retList = new ArrayList<ArrayList<String>>();
		openDataBase();
		Cursor cursor = null;
		try {
			ArrayList<String> list = new ArrayList<String>();
			cursor = myDataBase.rawQuery(query, null);

			if (cursor.moveToFirst()) {
				do {
					list = new ArrayList<String>();
					for (int i = 0; i < cursor.getColumnCount(); i++) {
						list.add(cursor.getString(i));
					}
					retList.add(list);
				} while (cursor.moveToNext());
			}

			return retList;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

			return retList;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			close();
		}
	}




	public synchronized ArrayList<TaskInfoEntity> selectRecordsFromDBArrayListNew(
			String query) {

		ArrayList<TaskInfoEntity> retList = new ArrayList<TaskInfoEntity>();
		//	openDataBase();
		Cursor cursor = null;
		try {
			cursor = myDataBase.rawQuery(query, null);

			if (cursor.moveToFirst()) {
				do {
					TaskInfoEntity entity = new TaskInfoEntity();
					entity.setAssignFromName(cursor.getString(0));
					entity.setAssign_from(cursor.getString(1));
					entity.setCount(cursor.getString(2));

					retList.add(entity);
				} while (cursor.moveToNext());
			}

			return retList;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

			return retList;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			//	close();
		}
	}



	public Map selectRecordsFromDBArrayListJunk(
			String query,Context context) {

		HashMap mapValue= null;
		Cursor cursor = null;
		int fireCount = 0;
		int favCount = 0;

		List<TaskInfoEntity> entities=new ArrayList<TaskInfoEntity>();

		try
		{
			//openDataBase();
			cursor = myDataBase.rawQuery(query, null);
			if (cursor.moveToFirst()) {
				do {
					TaskInfoEntity entity = new TaskInfoEntity();
					String isActive = cursor.getString(cursor.getColumnIndex("isActive"));
					String status= cursor.getString(cursor.getColumnIndex("Status"));
					String user_FromNo =cursor.getString(1);
					String user_Assign_ToNO =cursor.getString(3);
					String creation_date = cursor.getString(cursor.getColumnIndex("CreationDate"));
					String isAlarmSet = cursor.getString(cursor.getColumnIndex("isAlarmSet"));
					String isTaskRead = cursor.getString(cursor.getColumnIndex("IsTaskRead"));
					String isFavourite = cursor.getString(cursor.getColumnIndex("IsFavouraite"));
					String isTaskSync = cursor.getString(cursor.getColumnIndex("IsTaskSync"));
					String taskType = cursor.getString(16);
					String userFromName = cursor.getString(2);
					String userToName = cursor.getString(4);
					String proiority = cursor.getString(6);
					String closurBy=cursor.getString(cursor.getColumnIndex("ClosedBy"));
					boolean favourite = Boolean.parseBoolean(cursor.getString(15));
					String taskUrl   = cursor.getString(cursor.getColumnIndex("TASK_URL"));


					if(isAlarmSet!=null&& isAlarmSet.equalsIgnoreCase("Y"))
					{
						//holder.alarmIcon.setBackgroundResource(R.drawable.alarm_blue);
						entity.setAlarmBackground(R.drawable.alarm_blue_icon);
					}
					else
					{
						entity.setAlarmBackground(R.drawable.alarm_blue_disable);
					}
					if (isTaskRead != null && isTaskRead.trim().equalsIgnoreCase("N")) {
						entity.setTextTypeface(Typeface.BOLD);
					}
					if (isTaskSync != null && isTaskSync.trim().equalsIgnoreCase("Y")) {
						entity.setTaskSyncStatus(R.drawable.phone_withcheck);
					} else {
						//holder.taskSyncStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.phone_withuncheck));
						entity.setTaskSyncStatus(R.drawable.phone_withuncheck);
					}

					//System.out.println(cursor.getString(5));

					if (taskType.trim().equalsIgnoreCase("Inbox")) {
						if (userFromName != null && !userFromName.trim().equals("")||user_FromNo!=null&&user_FromNo.trim().equals("")) {

							entity.setTaskAssigneeName(userFromName!=null?userFromName:user_FromNo);
							entity.setTaskStatusArrow(R.drawable.arriow_inward);
						}

						//					} else {
						//						entity.setTaskAssigneeName(ApplicationUtil.getContactNameFromPhoneNo(context,user_FromNo,userFromName) + " ");
						//						entity.setTaskStatusArrow(R.drawable.arriow_inward);
						//						
						//					}
					}
					else if (taskType.trim().equalsIgnoreCase("Sent")) {
						if (user_Assign_ToNO != null && !user_Assign_ToNO.trim().equals("")) {
							entity.setTaskAssigneeName(userToName!=null?userToName:user_Assign_ToNO);
							entity.setTaskStatusArrow(R.drawable.arriow_outward);
						}
						//					} else {
						//						entity.setTaskAssigneeName(ApplicationUtil.getContactNameFromPhoneNo(context,user_Assign_ToNO,userToName) + " ");
						//						entity.setTaskStatusArrow(R.drawable.arriow_outward);
						//				
						//					}
					} else {
						entity.setTaskAssigneeName("self");
						entity.setTaskStatusArrow(0);


					}

					boolean msgSync = false;
					int unreadMsg = 0;
					try {
						TaskEntity taskEntityObj = new TaskEntity();
						if (
								taskType != null
								&& !taskType.trim().equalsIgnoreCase("self")) {
							unreadMsg = taskEntityObj.unreadMessageCount(cursor.getString(0),context);
							msgSync = taskEntityObj.msgSyncStatus(cursor.getString(0),context);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}


					if(unreadMsg == 0)
					{

						if(msgSync){
							entity.setMsgSyncStatusBackground(R.drawable.notefaction_check);
							entity.setMsgCountColor(context.getResources().getColor(R.color.black));


						}else{
							entity.setMsgSyncStatusBackground(R.drawable.notefaction_cross);
							entity.setMsgCountColor(context.getResources().getColor(R.color.black));

						}		
					}else{
						if(taskType != null && taskType.trim().equalsIgnoreCase("inbox")||taskType.trim().equalsIgnoreCase("sent")&&unreadMsg>0){
							if(msgSync){
								entity.setMsgSyncStatusBackground(R.drawable.notefaction_checkwithredoutline);
								entity.setMsgCountColor(context.getResources().getColor(R.color.white));
							}else{
								entity.setMsgSyncStatusBackground(R.drawable.notefaction_crosswithredoutline);
								entity.setMsgCountColor(context.getResources().getColor(R.color.white));

							}
						}
					}


					if(taskType.trim().equalsIgnoreCase("Inbox"))	{
						entity.setCalenderImage(R.drawable.calandericonline);
						entity.setEditIcon(R.drawable.edit_iconline);
						entity.setUserIcon(R.drawable.user_iconline);
						entity.setTextIconTextColor(context.getResources().getColor(R.color.light_gray));

					}else{
						entity.setCalenderImage(R.drawable.calander);
						entity.setEditIcon(R.drawable.edit_icon);
						entity.setUserIcon(R.drawable.user_icon);
						entity.setTextIconTextColor(context.getResources().getColor(R.color.name_blue));
					}

					if (taskType.trim().equalsIgnoreCase("Inbox")) 
					{
						entity.setFireBackground(R.drawable.fire_checkbox_line);

					}
					else
					{
						entity.setFireBackground(R.drawable.fire_checkbox);
					}
					if (proiority != null&& proiority.trim().length() > 0	&& proiority.trim().equalsIgnoreCase(context.getResources().getString(R.string.fire_priority_val))) {

						entity.setFireCheck(true);
						fireCount++;

					} else {

						entity.setFireCheck(false);
					}
					if (taskType.trim().equalsIgnoreCase("Inbox")||"CLOSE".equalsIgnoreCase(status)) 
					{
						entity.setFireBackground(R.drawable.fire_checkbox_line);
						entity.setFireEnable(false);
					}
					if(favourite)
					{
						favCount++;
					}
					if(taskUrl!=null && !taskUrl.trim().equals(""))
					{
						entity.setTaskUrl(taskUrl);
						entity.setUrlTaskBackground(context.getResources().getColor(R.color.grey));
					}
					else
					{
						entity.setUrlTaskBackground(context.getResources().getColor(R.color.white));

					}

					UserInfoEntity assignFrom=new UserInfoEntity();
					UserInfoEntity assignTo=new UserInfoEntity();
					entity.setId(cursor.getString(0));
					assignFrom.setMobile_number(cursor.getString(1));
					assignFrom.setFirstName(cursor.getString(2));
					assignTo.setMobile_number(cursor.getString(3));
					assignTo.setFirstName(cursor.getString(4));
					entity.setUserInfoFrom(assignFrom);
					entity.setUserInfoTo(assignTo);
					entity.setTask_description(cursor.getString(5));
					entity.setPriority(cursor.getString(6));
					entity.setTarget_date((cursor.getString(7)));
					entity.setReminder_time(cursor.getString(8));
					entity.setIsFavouraite(cursor.getString(9));

					entity.setTaskType(cursor.getString(10));
					entity.setCreation_date(cursor.getString(cursor.getColumnIndex("CreationDate")));
					entity.setIsTaskSync(cursor.getString(cursor.getColumnIndex("IsTaskSync")));
					entity.setAlarmTime(cursor.getString(cursor.getColumnIndex("AlarmTime")));
					entity.setIsTaskRead(cursor.getString(cursor.getColumnIndex("IsTaskRead")));
					entity.setClosedBy(cursor.getString(cursor.getColumnIndex("ClosedBy")));
					entity.setClosureDate(cursor.getString(cursor.getColumnIndex("Closure_Date")));
					entity.setStatus(cursor.getString(cursor.getColumnIndex("Status")));
					entity.setIsJunk(cursor.getString(cursor.getColumnIndex("IsJunk")));
					entity.setUpdatedDate(cursor.getString(cursor.getColumnIndex("UPDATED_DATE")));

					entities.add(entity);
				} while (cursor.moveToNext());
			}


		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();


		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			//	close();
		}
		mapValue = new HashMap();
		mapValue.put("list", entities);
		mapValue.put("fireCount", fireCount);
		mapValue.put("favCount", favCount);

		return mapValue;
	}

	/**
	 * apply raw Query
	 * 
	 * @param query
	 * @param selectionArgs
	 * @return ArrayList
	 */
	public synchronized ArrayList<ArrayList<String>> exceuteQueryWithException(
			String query) throws Exception {

		ArrayList<ArrayList<String>> retList = new ArrayList<ArrayList<String>>();
		//openDataBase();
		Cursor cursor = null;

		//System.out.println("apk execute query----> " + query);

		ArrayList<String> list = new ArrayList<String>();
		cursor = myDataBase.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				list = new ArrayList<String>();
				for (int i = 0; i < cursor.getColumnCount(); i++) {
					list.add(cursor.getString(i));
				}
				retList.add(list);
			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		//close();

		return retList;

	}

	/**
	 * This function used to insert the Record in DB.
	 * 
	 * @param tableName
	 * @param nullColumnHack
	 * @param initialValues
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public synchronized long insertRecordsInDB(String tableName,
			String nullColumnHack, ContentValues initialValues) {
		long status = -1;
		try {
			openDataBase();
			status = myDataBase.insert(tableName, nullColumnHack, initialValues);
			return status;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();


		} finally {
			//close();
			return status;
		}
	}

	public synchronized void insertRecordsDeadlineDb(String tableName,
			String sql) {

		try {
			openDataBase();
			myDataBase.execSQL(sql);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();


		} finally {
			close();

		}
	}

	/**
	 * This function used to replace the string.
	 * 
	 * @param tableName
	 * @param nullColumnHack
	 * @param initialValues
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public synchronized String replaceByDB(String content, String patternStr,
			String replceStr) {
		String replacedStr = "";

		try {
			content = URLEncoder.encode(content, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		}

		//System.out.println("string after url encode is----> " + content);

		Cursor cursor = null;
		try {
			openDataBase();
			String sql = "SELECT replace(" + content + ", "
					+ URLEncoder.encode(patternStr, "UTF-8") + ", "
					+ URLEncoder.encode(replacedStr, "UTF-8") + ")";
			//System.out.println("string replace query is---? " + sql);
			cursor = myDataBase.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				do {

					for (int i = 0; i < cursor.getColumnCount(); i++) {
						replacedStr = cursor.getString(i);
					}

				} while (cursor.moveToNext());
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

			//System.out.println("exception in string replcae for table name ");
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}

			close();

			return replacedStr;
		}
	}

	public synchronized void executeSQL(String sql) {
		try {
			openDataBase();
			myDataBase.execSQL(sql);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		} finally {
			close();
		}
	}

	public void createTable() {
		try {
			//System.out.println("create db called");
			File dbfile = new File(DB_PATH + DB_NAME);
			File dbpath = new File(DB_PATH);

			if (!dbpath.exists())
				dbpath.mkdir();
			SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile,
					null);

			// SQLiteDatabase db =
			// SQLiteDatabase.openOrCreateDatabase(DB_PATH+DB_NAME, null);

			String userTable = "CREATE TABLE 'User' ('id' INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , 'First_Name' VARCHAR, 'Middle_Name' VARCHAR, 'last_name' VARCHAR, 'XToken' VARCHAR, 'Mobile_Number' VARCHAR, 'Email_Id' VARCHAR, 'Operating_System' VARCHAR, 'Registration_Date' VARCHAR, 'Status' VARCHAR, 'Last_Login_Date' VARCHAR, 'Task_Last_Updated' VARCHAR DEFAULT 0, 'Number_Validation' VARCHAR)";
			db.execSQL(userTable);

			String taskTable = "CREATE TABLE 'Task' ( 'Task_ID' VARCHAR PRIMARY KEY , 'Assign_From' VARCHAR,'Assign_From_Name' VARCHAR, 'Assign_To' VARCHAR, 'Assign_To_Name' VARCHAR, 'Task_Desc' VARCHAR, 'Priority' VARCHAR, 'Priority_Desc' VARCHAR, 'Closure_Date' VARCHAR, 'Target_Date' VARCHAR, 'Fire_Flag' VARCHAR, 'Reminder_Time' VARCHAR , 'IsReminder' VARCHAR, 'IsMessage' VARCHAR,  'Status' VARCHAR, 'IsFavouraite' VARCHAR, 'TaskType' VARCHAR, 'CreationDate' VARCHAR, 'IsFAvSync' VARCHAR DEFAULT 'Y', 'IsPrioritySync' VARCHAR DEFAULT 'Y', 'IsStatusSync' VARCHAR  DEFAULT 'Y', 'IsReminderSync' VARCHAR  DEFAULT 'Y', 'IsTaskUpdateSync' VARCHAR  DEFAULT 'Y', 'IsTaskSync' VARCHAR  DEFAULT 'Y', 'IsJunk' VARCHAR, 'AlarmTime' VARCHAR, 'Group_ID' VARCHAR,  'IsAssigneeSync' VARCHAR DEFAULT 'Y',  'IsTargetSync' VARCHAR DEFAULT 'Y',  'TotalMessage' VARCHAR DEFAULT 0,  'UnreadMessage' VARCHAR DEFAULT 0,  'IsTaskRead' VARCHAR DEFAULT 'Y',  'ClosedBy' VARCHAR,'isActive' VARCHAR  DEFAULT 'Y' , 'oldAssignee' VARCHAR,'CreationDateTime' NUMERIC,'ClosureDate' NUMERIC,'TASK_SYNC_TYPE' VARCHAR,'isTaskNew' VARCHAR,'isMsgSend' VARCHAR,'isAlarmSet' VARCHAR  DEFAULT 'N','Alarm_Date_Time' VARCHAR,TASK_URL VARCHAR,UPDATED_DATE VARCHAR,UPDATION_DATE_TIME NUMERIC,DISPLAY_NAME VARCHAR,transactionId VARCHAR)";//,UPDATED_DATE VARCHAR

			db.execSQL(taskTable);
			String index=	"CREATE INDEX Task_index ON Task(Status,CreationDateTime,ClosureDate)";
			db.execSQL(index);

			String OTP = "CREATE TABLE 'OTP' ('id' INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , 'Registration_ID' VARCHAR, 'Mobile_Number' VARCHAR, 'OTP' VARCHAR, 'OTP_Status' VARCHAR)";
			db.execSQL(OTP);

			String Messaging = "CREATE TABLE 'Messaging' ( 'Message_ID' VARCHAR PRIMARY KEY, 'Task_ID' VARCHAR, 'M_From' VARCHAR, 'M_From_Name' VARCHAR, 'M_To' VARCHAR, 'M_To_Name' VARCHAR, 'M_Desc' VARCHAR, 'Creation_Date' VARCHAR, 'IsRead' VARCHAR, 'IsMessageSync' VARCHAR DEFAULT 'Y','CreationDateTime' NUMERIC,'MSG_TYPE' VARCHAR,transactionId VARCHAR)";
			db.execSQL(Messaging);
			String index1=	"CREATE INDEX TMessaging_index ON Task(Task_ID)";
			db.execSQL(index1);

			String deadlineTable = "CREATE TABLE 'Deadline' ( 'Task_ID' VARCHAR PRIMARY KEY , 'Assign_From' VARCHAR,'Assign_From_Name' VARCHAR, 'Assign_To' VARCHAR, 'Assign_To_Name' VARCHAR, 'Task_Desc' VARCHAR, 'Priority' VARCHAR, 'Priority_Desc' VARCHAR, 'Closure_Date' VARCHAR, 'Target_Date' VARCHAR, 'Fire_Flag' VARCHAR, 'Reminder_Time' VARCHAR , 'IsReminder' VARCHAR, 'IsMessage' VARCHAR,  'Status' VARCHAR, 'IsFavouraite' VARCHAR, 'TaskType' VARCHAR, 'CreationDate' VARCHAR, 'IsFAvSync' VARCHAR DEFAULT 'Y', 'IsPrioritySync' VARCHAR DEFAULT 'Y', 'IsStatusSync' VARCHAR  DEFAULT 'Y', 'IsReminderSync' VARCHAR  DEFAULT 'Y', 'IsTaskUpdateSync' VARCHAR  DEFAULT 'Y', 'IsTaskSync' VARCHAR  DEFAULT 'Y', 'IsJunk' VARCHAR, 'AlarmTime' VARCHAR, 'Group_ID' VARCHAR,  'IsAssigneeSync' VARCHAR DEFAULT 'Y',  'IsTargetSync' VARCHAR DEFAULT 'Y',  'TotalMessage' VARCHAR DEFAULT 0,  'UnreadMessage' VARCHAR DEFAULT 0,  'IsTaskRead' VARCHAR DEFAULT 'Y',  'ClosedBy' VARCHAR,'IsDeadlineDisplay' VARCHAR,'CreationDateTime' NUMERIC,'ClosureDate' NUMERIC,'isActive' VARCHAR  DEFAULT 'Y' )";


			db.execSQL(deadlineTable);

			String TASK_GROUP = "CREATE TABLE 'TASK_GROUP' ( 'groupId' VARCHAR PRIMARY KEY,'TASK_SYSNC_STATUS' VARCHAR )";
			db.execSQL(TASK_GROUP);

			/*String USERS_CONTACT = "CREATE TABLE 'USERS_CONTACT' ('MOBILE_NUMBER' VARCHAR PRIMARY KEY, 'NAME' VARCHAR,'REG_STATUS' VARCHAR )";

			db.execSQL(USERS_CONTACT);*/

			String USER_REG_STATUS = "CREATE TABLE 'USERS_CONTACT' ('MOBILE_NUMBER' VARCHAR PRIMARY KEY, 'NAME' VARCHAR,'REG_STATUS' VARCHAR )";
			db.execSQL(USER_REG_STATUS);
			
			String NOTAION_KEYWORD = "CREATE TABLE 'NOTAION_KEYWORD' ('ID' INTEGER PRIMARY KEY  , 'NAME' VARCHAR)";
			db.execSQL(NOTAION_KEYWORD);
			
			String NOTAION_TASK_MAPPING = "CREATE TABLE 'NOTAION_TASK_MAPPING' ('ID' INTEGER PRIMARY KEY, 'NAME' VARCHAR,'Task_ID' VARCHAR)";
			db.execSQL(NOTAION_TASK_MAPPING);
			
			
			db.close();
		} catch (SQLiteException e) {
			e.printStackTrace();

		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}

	
	public void createNewTable() {
		try {
			//System.out.println("create db called");
			File dbfile = new File(DB_PATH + DB_NAME);
			File dbpath = new File(DB_PATH);

			if (!dbpath.exists())
				dbpath.mkdir();
			SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile,	null);

				
			String NOTAION_KEYWORD = "CREATE TABLE 'NOTAION_KEYWORD' ('ID' INTEGER PRIMARY KEY  , 'NAME' VARCHAR)";
			db.execSQL(NOTAION_KEYWORD);
			
			String NOTAION_TASK_MAPPING = "CREATE TABLE 'NOTAION_TASK_MAPPING' ('ID' INTEGER PRIMARY KEY, 'NAME' VARCHAR,'Task_ID' VARCHAR)";
			db.execSQL(NOTAION_TASK_MAPPING);
			
			
			db.close();
		} catch (SQLiteException e) {
			e.printStackTrace();

		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}


	public void alterTable() {
		try {
			//System.out.println("create db called");
			File dbfile = new File(DB_PATH + DB_NAME);
			File dbpath = new File(DB_PATH);

			if (!dbpath.exists())
				dbpath.mkdir();
			SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile,	null);

				
			String AddTarget_Date_TIME = " alter table Task add column Target_Date_TIME NUMERIC";
			try {
				db.execSQL(AddTarget_Date_TIME);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String AddisTaskSnooz = " alter table Task add column isTaskSnooz VARCHAR default 'N' ";
			try {
				db.execSQL(AddisTaskSnooz);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			db.close();
		} catch (SQLiteException e) {
			e.printStackTrace();

		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}
	
	public void addDefaultCategories(){
		try {
			
			for (String nota : notationName) {
				if(!isNotaionExist(nota)){
					Integer noTaionId= new SecureRandom().nextInt(1000);
					ContentValues notationValues = new ContentValues();
					notationValues.put("NAME", nota);
					notationValues.put("ID", noTaionId);
					createNotaion(notationValues);
				}
			}
			
			
			
		} catch (Exception e) {
			
		}
	
		
	}

	public Context getMyContext() {
		return myContext;
	}

	public List<TaskInfoEntity> getAsycnTaskList(String query ){
		Cursor cursor = null;
		List<TaskInfoEntity> entities=new ArrayList<TaskInfoEntity>();


		try
		{
			//openDataBase();
			cursor = myDataBase.rawQuery(query, null);
			if (cursor.moveToFirst()) {
				do {
					TaskInfoEntity entity = new TaskInfoEntity();
					UserInfoEntity assignFrom=new UserInfoEntity();
					UserInfoEntity assignTo=new UserInfoEntity();
					entity.setId(cursor.getString(0));
					assignFrom.setMobile_number(cursor.getString(1));
					assignFrom.setFirstName(cursor.getString(2));
					assignTo.setMobile_number(cursor.getString(3));
					assignTo.setFirstName(cursor.getString(4));
					entity.setUserInfoFrom(assignFrom);
					entity.setUserInfoTo(assignTo);
					entity.setTask_description(cursor.getString(5));
					entity.setPriority(cursor.getString(6));
					entity.setCloser_date(cursor.getString(8));
					entity.setTarget_date(cursor.getString(cursor.getColumnIndex("Target_Date")));
					entity.setFire_flag(cursor.getString(10));
					entity.setReminder_time(cursor.getString(11));
					entity.setIsReminder(cursor.getString(12));
					entity.setIsMessage(cursor.getString(13));
					entity.setStatus(cursor.getString(14));
					entity.setIsFavouraite(cursor.getString(15));
					entity.setTaskType(cursor.getString(16));
					entity.setCreation_date(cursor.getString(17));
					entity.setIsTaskSync(cursor.getString(cursor.getColumnIndex("IsTaskSync")));
					entity.setIsMessageSend(cursor.getString(cursor.getColumnIndex("isMsgSend")));
					entity.setIsJunk(cursor.getString(cursor.getColumnIndex("IsJunk")));
					entity.setAlarmTime(cursor.getString(cursor.getColumnIndex("AlarmTime")));
					entity.setGroupId(cursor.getString(cursor.getColumnIndex("Group_ID")));
					entity.setTransactionId(cursor.getString(cursor.getColumnIndex("transactionId")));
					entity.setIsFAvSync(cursor.getString(cursor.getColumnIndex("IsFAvSync")));
					entity.setIsAssigneeSync(cursor.getString(27));
					entity.setIsTaskUpdateSync(cursor.getString(cursor.getColumnIndex("IsTaskUpdateSync")));
					entity.setIsPrioritySync(cursor.getString(cursor.getColumnIndex("IsPrioritySync")));
					entity.setIsTargetSync(cursor.getString(28));
					entity.setIsStatusSync(cursor.getString(20));
					entity.setIsReminderSync(cursor.getString(21));
					entity.setTotalMessage(cursor.getString(29));
					entity.setUnreadMessage(cursor.getString(30));//
					entity.setIsTaskRead(cursor.getString(cursor.getColumnIndex("IsTaskRead")));
					entity.setClosedBy(cursor.getString(cursor.getColumnIndex("ClosedBy")));
					entity.setIsActive(cursor.getString(cursor.getColumnIndex("isActive")));
					entity.setClosureDate(cursor.getString(cursor.getColumnIndex("Closure_Date")));
					entity.setCreationDateTime(cursor.getString(cursor.getColumnIndex("ClosureDate")));
					entity.setTaskSyncType(cursor.getString(cursor.getColumnIndex("TASK_SYNC_TYPE")));
					entity.setUpdatedDate(cursor.getString(cursor.getColumnIndex("UPDATED_DATE")));
					entity.setOldAssignee(cursor.getString(cursor.getColumnIndex("oldAssignee")));
					entity.setNewAssigne(cursor.getString(cursor.getColumnIndex("Assign_To")));


					entities.add(entity);
				} while (cursor.moveToNext());
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if(cursor!=null)			
				cursor.close();
			//close();
		}
		return entities;
	}


	public List<MessageInfoEntity> getMessageByTaskId(String taskId){

		String query="  Select * from Messaging where Task_ID='"+taskId+"' order by  CreationDateTime desc";
		List<MessageInfoEntity> entities=new ArrayList<MessageInfoEntity>();
		openDataBase();
		Cursor cursor = null;
		try{
			cursor = myDataBase.rawQuery(query, null);
			if (cursor.moveToFirst()) {

				do {
					MessageInfoEntity entity = new MessageInfoEntity();
					UserInfoEntity assignFrom=new UserInfoEntity();
					UserInfoEntity assignTo=new UserInfoEntity();
					entity.setMessageId(cursor.getString(cursor.getColumnIndex("Message_ID")));
					assignFrom.setMobile_number(cursor.getString(cursor.getColumnIndex("M_From")));
					assignFrom.setFirstName(cursor.getString(cursor.getColumnIndex("M_From_Name")));
					assignTo.setMobile_number(cursor.getString(cursor.getColumnIndex("M_To")));
					assignTo.setFirstName(cursor.getString(cursor.getColumnIndex("M_To_Name")));
					entity.setMessage_description(cursor.getString(cursor.getColumnIndex("M_Desc")));
					entity.setMessageSysnStatus(cursor.getString(cursor.getColumnIndex("IsMessageSync")));
					entity.setMessageReadStatus(cursor.getString(cursor.getColumnIndex("IsRead")));
					entity.setUserInfoFrom(assignFrom);
					entity.setUserInfoTo(assignTo);
					entity.setCreatedDate(cursor.getString(cursor.getColumnIndex("Creation_Date")));
					entities.add(entity);
				} while (cursor.moveToNext());
			}}catch (Exception e) {
				e.printStackTrace();
			}finally{
				cursor.close();
				myDataBase.close();

			}
		return entities;
	}


	public List<MessageInfoEntity> getNewMessageByTaskId(String taskId){

		String query="  Select * from Messaging where Task_ID='"+taskId+"' order by  CreationDateTime ";
		List<MessageInfoEntity> entities=new ArrayList<MessageInfoEntity>();
		//openDataBase();
		Cursor cursor = null;
		try{
			cursor = myDataBase.rawQuery(query, null);
			if (cursor.moveToFirst()) {

				do {
					MessageInfoEntity entity = new MessageInfoEntity();
					UserInfoEntity assignFrom=new UserInfoEntity();
					UserInfoEntity assignTo=new UserInfoEntity();
					entity.setMessageId(cursor.getString(cursor.getColumnIndex("Message_ID")));
					assignFrom.setMobile_number(cursor.getString(cursor.getColumnIndex("M_From")));
					assignFrom.setFirstName(cursor.getString(cursor.getColumnIndex("M_From_Name")));
					assignTo.setMobile_number(cursor.getString(cursor.getColumnIndex("M_To")));
					assignTo.setFirstName(cursor.getString(cursor.getColumnIndex("M_To_Name")));
					entity.setMessage_description(cursor.getString(cursor.getColumnIndex("M_Desc")));
					entity.setMessageSysnStatus(cursor.getString(cursor.getColumnIndex("IsMessageSync")));
					entity.setMessageReadStatus(cursor.getString(cursor.getColumnIndex("IsRead")));
					entity.setTransactionId(cursor.getString(cursor.getColumnIndex("transactionId")));
					entity.setUserInfoFrom(assignFrom);
					entity.setUserInfoTo(assignTo);
					entity.setCreatedDate(cursor.getString(cursor.getColumnIndex("Creation_Date")));
					entities.add(entity);
				} while (cursor.moveToNext());
			}}catch (Exception e) {
				e.printStackTrace();
			}finally{
				cursor.close();

			}
		return entities;
	}

	public void updateTask(ContentValues contentValues,String taskId){	
		try {
			myDataBase.update("TAsk", contentValues, "Task_ID ='" + taskId + "'", null);
		} catch(SQLiteConstraintException constraintException){
			myDataBase.delete("TAsk", "Task_ID ='" + taskId + "'", null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}


	}
	
	public void updateNotaionMapping(ContentValues contentValues,String taskId){	
		try {
			myDataBase.update("NOTAION_TASK_MAPPING", contentValues, "Task_ID ='" + taskId + "'", null);
		} catch(SQLiteConstraintException constraintException){
			myDataBase.delete("NOTAION_TASK_MAPPING", "Task_ID ='" + taskId + "'", null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}


	}

	public void updateMessage(ContentValues contentValues,String taskId){	
		try {
			myDataBase.update("Messaging", contentValues, "Message_ID ='" + taskId + "'", null);
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	public long createMessage(ContentValues contentValues){
		long status=-1;

		try {
			status = myDataBase.insert("Messaging", null, contentValues);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}

	public long createTask(ContentValues contentValues){
		long status=-1;

		try {
			status = myDataBase.insert("TAsk", null, contentValues);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}



	public List<MessageInfoEntity> getAsyncMessageByTaskId(String taskId){

		String query="  Select * from Messaging where Task_ID='"+taskId+"' AND IsMessageSync='N' order by  CreationDateTime ";
		List<MessageInfoEntity> entities=new ArrayList<MessageInfoEntity>();

		Cursor cursor = null;
		try{
			cursor = myDataBase.rawQuery(query, null);
			if (cursor.moveToFirst()) {

				do {
					MessageInfoEntity entity = new MessageInfoEntity();
					UserInfoEntity assignFrom=new UserInfoEntity();
					UserInfoEntity assignTo=new UserInfoEntity();
					entity.setMessageId(cursor.getString(cursor.getColumnIndex("Message_ID")));
					assignFrom.setMobile_number(cursor.getString(cursor.getColumnIndex("M_From")));
					assignFrom.setFirstName(cursor.getString(cursor.getColumnIndex("M_From_Name")));
					assignTo.setMobile_number(cursor.getString(cursor.getColumnIndex("M_To")));
					assignTo.setFirstName(cursor.getString(cursor.getColumnIndex("M_To_Name")));
					entity.setMessage_description(cursor.getString(cursor.getColumnIndex("M_Desc")));
					entity.setMessageSysnStatus(cursor.getString(cursor.getColumnIndex("IsMessageSync")));
					entity.setMessageReadStatus(cursor.getString(cursor.getColumnIndex("IsRead")));
					entity.setUserInfoFrom(assignFrom);
					entity.setUserInfoTo(assignTo);
					entity.setCreatedDate(cursor.getString(cursor.getColumnIndex("Creation_Date")));
					entities.add(entity);
				} while (cursor.moveToNext());
			}}catch (Exception e) {
				e.printStackTrace();
			}finally{
				cursor.close();
				//myDataBase.close();

			}
		return entities;
	}
	public synchronized long saveRecord(String tableName,
			String nullColumnHack, ContentValues initialValues) {
		long status = -1;
		try {
			status = myDataBase.insert(tableName, nullColumnHack, initialValues);
			return status;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();


		} finally {
			return status;
		}

	}


	public synchronized int updateRecord(String tableName, ContentValues initialValues, String whereClause, String whereArgs[]) {
		int status = 0;
		try {
			status = myDataBase.update(tableName, initialValues, whereClause, whereArgs);
			System.out.println("Here si squery... " + status) ;
		} catch (Exception e) { 
			// TODO: handle exception
			e.printStackTrace();

		} finally {
			//	close();

			return status;
		}
	}

	//String isAlarmSet = cursor.getString(cursor.getColumnIndex("isAlarmSet"));
	public boolean isTaskExist(String taskId){
		Cursor cursor=null;
		try{
			String query=" SELECT Task_ID FROM  Task where Task_ID='"+taskId+"'";
			cursor = myDataBase.rawQuery(query,null);
			if(cursor.moveToFirst()){
				return true; 
			}else{
				return false;
			}

		}catch(Exception exception){
			exception.printStackTrace();
		}finally{
			cursor.close();
		}
		return false;
	}
	public String getModyFiedDate(String taskId){
		String date =null;
		Cursor cursor=null;
		try{
			String query=" SELECT UPDATED_DATE FROM  Task where Task_ID='"+taskId+"'";
			cursor = myDataBase.rawQuery(query,null);
			if(cursor.moveToFirst()){  
				date = cursor.getString(cursor.getColumnIndex("UPDATED_DATE"));
			}

		}catch(Exception exception){
			exception.printStackTrace();
		}finally{
			cursor.close();
		}
		return date;
	}
	public boolean isMessageExist(String messageId){

		Cursor cursor=null;
		try{
			String query=" SELECT Message_ID FROM  Messaging where Message_ID='"+messageId+"'";
			cursor = myDataBase.rawQuery(query,null);
			if(cursor.moveToFirst()){
				return true; 
			}else{
				return false;
			}

		}catch(Exception exception){
			exception.printStackTrace();
		}finally{
			cursor.close();
		}
		return false;

	}

	public int getTotalOpenTask(){
		int count=0;
		Cursor mCount=null;
		try{
			//	openDataBase();
			mCount= myDataBase.rawQuery("select count(*) from Task WHERE TaskType in('self','inbox','sent') AND Status = 'OPEN' AND  isTaskSnooz!='Y' AND IsJunk ='N'", null);//SELECT  *  FROM Task WHERE TaskType in('self','inbox','sent') AND Status = 'OPEN' AND IsJunk ='N'   ORDER BY  CreationDateTime  DESC
			mCount.moveToFirst();
			count= mCount.getInt(0);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			mCount.close();
			//myDataBase.close();

		}
		return count;
	}
	
	public int countQuery(String query){
		int count=0;
		Cursor mCount=null;
		try{
			//	openDataBase();
			mCount= myDataBase.rawQuery(query, null);//SELECT  *  FROM Task WHERE TaskType in('self','inbox','sent') AND Status = 'OPEN' AND IsJunk ='N'   ORDER BY  CreationDateTime  DESC
			mCount.moveToFirst();
			count= mCount.getInt(0);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			mCount.close();
			//myDataBase.close();

		}
		return count;
	}

	public int getTotalCloseTask(){
		int count=0;
		Cursor mCount=null;
		try{
			//openDataBase();
			mCount= myDataBase.rawQuery("select count(*) from Task WHERE TaskType in('self','inbox','sent')  AND isTaskSnooz!='Y' AND Status = 'CLOSE' ", null);//SELECTchangeTaskEntity  *  FROM Task WHERE TaskType in('self','inbox','sent') AND Status = 'OPEN' AND IsJunk ='N'   ORDER BY  CreationDateTime  DESC
			mCount.moveToFirst();
			count= mCount.getInt(0);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			mCount.close();
			//myDataBase.close();

		}
		return count;
	}
	public int getTotalJunkTask(){
		int count=0;
		Cursor mCount=null;
		try{
			//openDataBase();
			mCount= myDataBase.rawQuery("select count(*) from Task WHERE TaskType in('self','inbox','sent') AND isTaskSnooz!='Y'  AND IsJunk ='Y'", null);//SELECT  *  FROM Task WHERE TaskType in('self','inbox','sent') AND Status = 'OPEN' AND IsJunk ='N'   ORDER BY  CreationDateTime  DESC
			mCount.moveToFirst();
			count= mCount.getInt(0);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			mCount.close();
			//myDataBase.close();

		}
		return count;
	}

	public List<String> getMSGReadAsyncTaskIds(){
		String query="  Select Task_ID from Messaging where MSG_TYPE='READNONSYNC'";
		Cursor cursor=null;
		List<String> taskIds=new ArrayList<String>();
		try{
			cursor = myDataBase.rawQuery(query, null);
			if (cursor.moveToFirst()) {

				do {

					taskIds.add(cursor.getString(cursor.getColumnIndex("Task_ID")));
				} while (cursor.moveToNext());
			}}catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursor!=null)
					cursor.close();
				//myDataBase.close();

			}

		return taskIds;

	}

	public List<Contact> getNonRegContacList(){
		ArrayList<Contact> contacts=new ArrayList<Contact>();
		String query="  Select * from USERS_CONTACT where REG_STATUS!='REGISTERED' order by NAME ";
		Cursor cursor=null;

		try{
			//openDataBase();
			try {
				cursor = myDataBase.rawQuery(query, null);
			} catch (Exception e) {
				openDataBase();
				cursor = myDataBase.rawQuery(query, null);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (cursor.moveToFirst()) {

				do {
					Contact contact=new Contact();	
					String name=cursor.getString(cursor.getColumnIndex("NAME"));
					String phoneNumber=cursor.getString(cursor.getColumnIndex("MOBILE_NUMBER"));;
					contact.setName(name);
					contact.setNumber(phoneNumber);
					contact.setRegStatus(cursor.getString(cursor.getColumnIndex("REG_STATUS")));
					contacts.add(contact);

				} while (cursor.moveToNext());
			}}catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursor!=null)
					cursor.close();

			}


		return contacts;
	}

	public List<Contact> getContacList(){
		ArrayList<Contact> contacts=new ArrayList<Contact>();
		String query="  Select * from USERS_CONTACT order by REG_STATUS DESC, lower(NAME)";
		Cursor cursor=null;

		try{
			//openDataBase();
			try {
				cursor = myDataBase.rawQuery(query, null);
			} catch (Exception e) {
				openDataBase();
				cursor = myDataBase.rawQuery(query, null);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (cursor.moveToFirst()) {

				do {
					Contact contact=new Contact();	
					String name=cursor.getString(cursor.getColumnIndex("NAME"));
					String phoneNumber=cursor.getString(cursor.getColumnIndex("MOBILE_NUMBER"));
					contact.setRegStatus(cursor.getString(cursor.getColumnIndex("REG_STATUS")));
					contact.setName(name);
					contact.setNumber(phoneNumber);
					contacts.add(contact);
					/*if(resContact!=null){
	        		List<String> numbers=	resContact.getNumbers();
	        		numbers.add(phoneNumber);
	        	}else{
		        	contact.setName(name);
		        	contact.getNumbers().add(phoneNumber);
		        	contacts.add(contact);
	        	}*/
				} while (cursor.moveToNext());
			}}catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursor!=null)
					cursor.close();

			}


		return contacts;
	}//REGISTERED
	private Contact isNameExist(String name,List<Contact> list) {

		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			Contact contact = (Contact) iterator.next();
			if(name.equalsIgnoreCase(contact.getName())){
				return contact;
			}


		}
		return null;
	}
	
	public boolean isUserRegistered(String mobileNumber){
		String query="  Select * from USERS_CONTACT where MOBILE_NUMBER='"+mobileNumber+"' and REG_STATUS='REGISTERED'";
		Cursor cursor=null;

		try{
			//openDataBase();
			try {
				cursor = myDataBase.rawQuery(query, null);
			} catch (Exception e) {
				openDataBase();
				cursor = myDataBase.rawQuery(query, null);
				e.printStackTrace();
			}
			if (cursor.moveToFirst()) {
			
				return true;
			}else{
				return false;
			}
			
		}catch (Exception e) {
			}finally{
				if(cursor!=null)
					cursor.close();

			}


		return false;
	}
	
	public String [] getTaskByHash(String hashVaue){

		String query="  Select NAME from NOTAION_KEYWORD where NAME like  '%"+hashVaue+"%' order by NAME " ;
		
		String [] taskArray=null;
		Cursor cursor = null;
		try{
			cursor = myDataBase.rawQuery(query, null);
			taskArray=new String[cursor.getCount()];
			int i=0;
			if (cursor.moveToFirst()) {

				do {
					taskArray[i]=cursor.getString(cursor.getColumnIndex("NAME"));
					i++;
				} while (cursor.moveToNext());
			}}catch (Exception e) {
				e.printStackTrace();
			}finally{
				cursor.close();
				//myDataBase.close();

			}
		return taskArray;
	}
	
	
	public List<NotationDto> getHasGroup(String hashVaue){

		String query="  Select NAME, count(*) as count  from NOTAION_TASK_MAPPING  group by NAME " ;
		 List<NotationDto> notationList=new ArrayList<NotationDto>();
		Cursor cursor = null;
		try{
			cursor = myDataBase.rawQuery(query, null);
			int i=0;
			if (cursor.moveToFirst()) {

				do {
					NotationDto notationDto=new NotationDto();
					notationDto.setNotaionName(cursor.getString(cursor.getColumnIndex("NAME")));
					notationDto.setCount(cursor.getInt(cursor.getColumnIndex("count")));
					notationList.add(notationDto);
					
				} while (cursor.moveToNext());
			}}catch (Exception e) {
				e.printStackTrace();
			}finally{
				cursor.close();
				//myDataBase.close();

			}
		return notationList;
	}
	
	
	
	public long createNotaion(ContentValues contentValues){
		long status=-1;

		try {
			status = myDataBase.insert("NOTAION_KEYWORD", null, contentValues);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}
	
	
	public boolean isNotaionExist(String notaion){
		Cursor cursor=null;
		try{
			String query=" SELECT  * from  NOTAION_KEYWORD where NAME='"+notaion+"'";
			cursor = myDataBase.rawQuery(query,null);
			if(cursor.moveToFirst()){
				return true; 
			}else{
				return false;
			}

		}catch(Exception exception){
			exception.printStackTrace();
		}finally{
			cursor.close();
		}
		return false;
	}
	
	public boolean isTaskNotaionExist(String notaion,String taskId){
		Cursor cursor=null;
		try{
			String query=" SELECT  * from  NOTAION_TASK_MAPPING where NAME='"+notaion+"' AND Task_ID='"+taskId+"'";
			cursor = myDataBase.rawQuery(query,null);
			if(cursor.moveToFirst()){
				return true; 
			}else{
				return false;
			}

		}catch(Exception exception){
			exception.printStackTrace();
		}finally{
			cursor.close();
		}
		return false;
	}
	
	public long createNotaionTaskMapping(ContentValues contentValues){
		long status=-1;

		try {
			status = myDataBase.insert("NOTAION_TASK_MAPPING", null, contentValues);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}
	
	public boolean isRegisteredUser(String mobileNumber){
		String query = String.format("Select REG_STATUS from USERS_CONTACT where MOBILE_NUMBER = \'%s\'", mobileNumber);
		Cursor cursor=null;

		try{
			//openDataBase();
			try {
				cursor = myDataBase.rawQuery(query, null);
			} catch (Exception e) {
				openDataBase();
				cursor = myDataBase.rawQuery(query, null);
				e.printStackTrace();
			}
			if (cursor.moveToFirst()) {
				String status = cursor.getString(cursor.getColumnIndex("REG_STATUS"));
				if(status.equalsIgnoreCase("REGISTERED")){
					return true;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cursor!=null)
				cursor.close();

		}

		return false;
	}//isREGISTERED
}
