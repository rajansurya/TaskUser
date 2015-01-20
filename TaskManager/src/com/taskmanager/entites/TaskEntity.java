package com.taskmanager.entites;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;

import com.taskmanager.bean.NotationDto;
import com.taskmanager.database.DBAdapter;
import com.taskmanager.dto.TaskInfoEntity;
import com.taskmanager.util.ApplicationConstant;
import com.taskmanager.util.ApplicationUtil;
import com.taskmanager.util.CommonUtil;

/**
 * This class is an entity which define all the methods of tasks
 * @author mayankb
 *
 */
public class TaskEntity {

	//private static Context context;

	/**
	 * method to fetch any value from user table
	 * @param key
	 * @return
	 */
	public String getUserTableData(String key,Context context)
	{
		String value = "";
		ArrayList<ArrayList<String>> listDBElement = ApplicationUtil.getInstance().uploadListFromDB("User",new String[]{key}, "Mobile_Number ='"+ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context)+"'", context);
		if(listDBElement != null && !listDBElement.isEmpty())
		{
			value = listDBElement.get(0).get(0);
		}
		return value;

	}


	/**
	 * method to update any value in user table
	 * @param initialValues
	 * @return
	 */
	public int updateUserTableData(String Key, String Value,Context context)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(Key, Value);
		int value = ApplicationUtil.getInstance().updateDataInDB("User", initialValues, context, "Mobile_Number ='"+ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context)+"'", null);
		return value;

	}


	
	
	
	public Map getTaskListNew(boolean selfFlag ,boolean receivedFlag, boolean assignedFlag,boolean favFlag, boolean fireFlag, String TaskStatus, String sortBy,Context context,boolean isLater) throws Exception {
		HashMap listTaskDBList;
		String taskQuery = "";
		String order ="";
		String taskType = "";
		String isFavourite = " ";
		String isFire = " ";
		String additionalQuery = "";
		String todayDate = ApplicationUtil.changeToDayMonthYear4(System.currentTimeMillis()/1000-24*60*60);
		String in="";
		StringBuilder builder=new StringBuilder(" SELECT  *  FROM Task WHERE Status = 'OPEN' AND IsJunk ='N' ");
		if(fireFlag){
			builder.append(" AND Priority='4' ");
		}if(favFlag){
			builder.append(" AND IsFavouraite ='true'  ");
		}if(selfFlag){
			in+="'self',";
		}if(receivedFlag){
			in+="'inbox',";
		}if(assignedFlag){
			in+="'sent',";
		}
		if(!in.isEmpty()){
			in=in.substring(0,in.length()-1);
			builder.append(" AND TaskType in ("+in+")  ");
		}else{
			builder.append(" AND TaskType NOT in ('self','inbox','sent') ");
		}
		
		
		/*if(!"snooz".equalsIgnoreCase(sortBy))
		{
			builder.append(" AND isTaskSnooz!='Y' ");
		}else{*/
			if(isLater){
				if("snooz".equalsIgnoreCase(sortBy)){
					builder.append(" AND isTaskSnooz='Y' ");
				} 
				
			}else{
				builder.append(" AND isTaskSnooz!='Y' ");
			}
			
		//}
		
		 if("today".equalsIgnoreCase(sortBy)){
				Calendar calendar=Calendar.getInstance();
				int hours=calendar.get(Calendar.HOUR_OF_DAY);
				calendar.add(Calendar.HOUR_OF_DAY, 24-hours);
			// int hours=Calendar.getInstance().get(Calendar.HOUR);
			 long time=new Date().getTime()-60*60*20*1000;
			 builder.append(" AND  Target_Date_TIME>="+time+" AND Target_Date_TIME<="+(calendar.getTimeInMillis())+" order by UPDATION_DATE_TIME  DESC");
			
		}else if("tomorrow".equalsIgnoreCase(sortBy)){
			
			Calendar calendar=Calendar.getInstance();
			int hours=calendar.get(Calendar.HOUR_OF_DAY);
			calendar.add(Calendar.HOUR_OF_DAY, 24-hours);
			long time=calendar.getTimeInMillis()+100;
			 builder.append(" AND  Target_Date_TIME>="+time+" AND Target_Date_TIME<="+(calendar.getTimeInMillis()+(60*60*24*1000))+" order by UPDATION_DATE_TIME  DESC");
			
		}else if("next7Days".equalsIgnoreCase(sortBy))
		{
			
			long time=new Date().getTime()-60*60;
			 builder.append(" AND  Target_Date_TIME>="+time+" AND Target_Date_TIME<="+(new Date().getTime()+(60*60*24*1000*7))+" order by UPDATION_DATE_TIME  DESC");
		}
		else if("delayed".equalsIgnoreCase(sortBy))
		{//Target_Date
			long time=new Date().getTime()-60*60*10*1000;
			 builder.append(" AND Target_Date_TIME<"+(time)+" and Target_Date_TIME>0 order by UPDATION_DATE_TIME  DESC");
		}
		
		
		
		//sortBy.trim().equalsIgnoreCase("Priority")||sortBy.trim().equalsIgnoreCase("CreationDate")
		else if(sortBy.equalsIgnoreCase("Priority"))
		{
			builder.append(" order by CreationDateTime DESC ");
		}
		else if(sortBy.equalsIgnoreCase("CreationDate"))
		{
			builder.append(" order by UPDATION_DATE_TIME DESC ");//DISPLAY_NAME
			
		}else if(sortBy.equalsIgnoreCase("UPDATION_DATE_TIME"))
		{
			builder.append(" order by UPDATION_DATE_TIME DESC ");//DISPLAY_NAME
		}
		else if("Assign_From_Name".equalsIgnoreCase(sortBy))
		{
			builder.append(" order by DISPLAY_NAME,UPDATION_DATE_TIME ASC ");
		}
		
		else
		{
			builder.append(" order by UPDATION_DATE_TIME  DESC ");
		}
		
		//		String isAlarm = " ";
		/*String valuesFetch = " * ";

		if(selfFlag)
		{
			if(taskType.trim().equals(""))
				taskType = "TaskType = 'self' ";
			else
				taskType = taskType+" OR TaskType = 'self'";
		}
		if(receivedFlag)
		{
			if(taskType.trim().equals(""))
				taskType = "TaskType = 'inbox' ";
			else
				taskType = taskType+" OR TaskType = 'inbox'";
		}
		if(assignedFlag)
		{
			if(taskType.trim().equals(""))
				taskType = "TaskType = 'sent' ";
			else
				taskType = taskType+" OR TaskType = 'sent'";
		}
		//if(selfFlag&&receivedFlag&&assignedFlag)
		

		if(sortBy.trim().equalsIgnoreCase("Priority")||sortBy.trim().equalsIgnoreCase("CreationDate") )//|| sortBy.trim().equalsIgnoreCase("CreationDate")
			order = "DESC";
		else
			order = "ASC";

		if(favFlag)
			isFavourite = " AND IsFavouraite ='true' ";

		if(fireFlag)
			isFire = " AND Priority = '4' ";

		//		if(alarmFlag)
		//			isAlarm = " AND AlarmTime != '' ";
		if(TaskStatus.trim().equalsIgnoreCase("OPEN"))
		{
			//additionalQuery =  " OR (Status = 'CLOSE' AND Closure_Date >= '"+todayDate+"' AND   TaskType = 'sent' ) ";
			additionalQuery =  " OR (Status = 'OPEN'  AND   TaskType = 'sent' ) ";
		}
		else
			if(TaskStatus.trim().equalsIgnoreCase("CLOSE"))
			{
				//			additionalQuery =  " AND NOT(Closure_Date >= '"+todayDate+"' ) ";
			}
		//notification
		if(getNonSyncCount( context)==0)
		{
			if(sortBy.trim().equalsIgnoreCase("CreationDate"))
			{
				
				if(taskType.trim().equals(""))
					taskQuery = "SELECT "+valuesFetch+" FROM Task WHERE ( TaskType = '' AND  ((Status = '"+TaskStatus+"')" +additionalQuery+" )   AND IsJunk ='N' " +isFavourite + isFire+ ")   ORDER BY CreationDateTime  " + order;
				else
					taskQuery = "SELECT "+valuesFetch+" FROM Task WHERE ( ("+taskType+") AND ((Status = '"+TaskStatus+"')" +additionalQuery+" )   AND IsJunk ='N' " +isFavourite + isFire+  ")  ORDER BY CreationDateTime  " + order;
				
			}else
			{
			if(taskType.trim().equals(""))
				taskQuery = "SELECT "+valuesFetch+" FROM Task WHERE ( TaskType = '' AND  ((Status = '"+TaskStatus+"')" +additionalQuery+" )   AND IsJunk ='N' " +isFavourite + isFire+ ")   ORDER BY "+ sortBy+" COLLATE NOCASE " + order;
			else
				taskQuery = "SELECT "+valuesFetch+" FROM Task WHERE ( ("+taskType+") AND ((Status = '"+TaskStatus+"')" +additionalQuery+" )   AND IsJunk ='N' " +isFavourite + isFire+  ")  ORDER BY "+ sortBy+" COLLATE NOCASE " + order;
			}
		}
		else
		{
			if(taskType.trim().equals(""))
				//taskQuery = "SELECT "+valuesFetch+" FROM Task WHERE  IsJunk ='N' and ("+taskType+") order by istasksync asc,creationdate desc";
				taskQuery = "SELECT "+valuesFetch+" FROM Task WHERE ( TaskType = '' AND  ((Status = '"+TaskStatus+"')" +additionalQuery+" )   AND IsJunk ='N' " +isFavourite + isFire+ ")   ORDER BY "+ "istasksync asc,CreationDateTime desc" ;

			else
				//taskQuery = "SELECT "+valuesFetch+" FROM Task WHERE  IsJunk ='N' and("+taskType+") order by istasksync asc,creationdate desc";
				taskQuery = "SELECT "+valuesFetch+" FROM Task WHERE ( ("+taskType+") AND ((Status = '"+TaskStatus+"')" +additionalQuery+" )   AND IsJunk ='N' " +isFavourite + isFire+  ")  ORDER BY "+ "istasksync asc,CreationDateTime desc" ;


		}
		//System.out.println("Task Status---"+TaskStatus);
		//istasksync asc,creationdate desc 
		*/
		//String str=" SELECT  *  FROM Task WHERE TaskType in('self','inbox','sent') AND Status = 'OPEN' AND IsJunk ='N'   ORDER BY  CreationDateTime  DESC";





		//System.out.println("the task query is----> "+taskQuery);
		listTaskDBList = (HashMap) ApplicationUtil.getInstance().getTaskList(builder.toString(), context);
		return listTaskDBList;
	}
	
	
	public Map getCloseTaskList(boolean selfFlag ,boolean receivedFlag, boolean assignedFlag,boolean favFlag, boolean fireFlag,String sortBy,Context context) throws Exception 
	{
	
		
		HashMap listTaskDBList=new HashMap();
		String taskQuery = "";
		String order ="";
		String taskType = "";
		String isFavourite = " ";
		String isFire = " ";
		String additionalQuery = "";
		String todayDate = ApplicationUtil.changeToDayMonthYear4(System.currentTimeMillis()/1000-24*60*60);
		String in="";
		StringBuilder builder=new StringBuilder(" SELECT  *  FROM Task WHERE Status = 'CLOSE' and isTaskSnooz!='Y' AND TaskType in('self','inbox','sent')  order by UPDATION_DATE_TIME DESC");
		/*if(fireFlag){
			builder.append(" AND Priority='4' ");
		}if(favFlag){
			builder.append(" AND IsFavouraite ='true'  ");
		}if(selfFlag){
			in+="'self',";
		}if(receivedFlag){
			in+="'inbox',";
		}if(assignedFlag){
			in+="'sent',";
		}
		if(!in.isEmpty()){
			in=in.substring(0,in.length()-1);
			builder.append(" AND TaskType in ("+in+")  ");
		}else{
			builder.append(" AND TaskType NOT in ('self','inbox','sent') ");
		}
		
		//sortBy.trim().equalsIgnoreCase("Priority")||sortBy.trim().equalsIgnoreCase("CreationDate")
		if(sortBy.equalsIgnoreCase("Priority"))
		{
			builder.append(" order by CreationDateTime DESC ");
		}
		else if(sortBy.equalsIgnoreCase("CreationDate"))
		{
			builder.append(" order by UPDATION_DATE_TIME DESC ");//DISPLAY_NAME
			
		}else if(sortBy.equalsIgnoreCase("UPDATION_DATE_TIME"))
		{
			builder.append(" order by UPDATION_DATE_TIME DESC ");//DISPLAY_NAME
		}
		else if("Assign_From_Name".equalsIgnoreCase(sortBy))
		{
			builder.append(" order by DISPLAY_NAME,UPDATION_DATE_TIME DESC ");
		}
		else
		{
			builder.append(" order by UPDATION_DATE_TIME DESC ");
		}*/
		
		 listTaskDBList = (HashMap) ApplicationUtil.getInstance().getTaskListClose(builder.toString(), context);
		return listTaskDBList;
	}

	/**
	 * this method fetches message list for individual task
	 * @param TaskId
	 * @return
	 * @throws Exception
	 */
	

	/**
	 * This methods gives no of unread messages for any particular task
	 * @param TaskId
	 * @return
	 * @throws Exception
	 */
	public int unreadMessageCount(String TaskId,Context context) throws Exception 
	{
		int count =0;
		ArrayList<ArrayList<String>> messageDBList;
		String query = "SELECT Count(*) FROM Messaging WHERE Task_ID ='"+TaskId+"' AND IsRead ='N' AND MSG_TYPE='inbox'";
		messageDBList = ApplicationUtil.getInstance().executeQuery(query, context);
		//System.out.println("the task query is----> "+query);
		if(messageDBList != null && !messageDBList.isEmpty())
			count = Integer.parseInt(messageDBList.get(0).get(0));

		return count;

	}
	
	public int isMessRead(String TaskId,Context context) throws Exception 
	{
		int count =0;
		ArrayList<ArrayList<String>> messageDBList;
		String query = "SELECT Count(*) FROM Messaging WHERE Task_ID ='"+TaskId+"' AND IsRead ='N'";
		messageDBList = ApplicationUtil.getInstance().executeQuery(query, context);
		if(messageDBList != null && !messageDBList.isEmpty())
			count = Integer.parseInt(messageDBList.get(0).get(0));
		return count;

	}

	/**
	 * method to fetch junk tasks
	 * @return
	 * @throws Exception
	 */
	public  Map  getJunkTaskList(boolean selfFlag ,boolean receivedFlag, boolean assignedFlag,boolean favFlag, boolean fireFlag,String sortBy,Context context) throws Exception 
	{
/*		HashMap listTaskDBList;
		String taskQuery = "";
		String order ="";
		String taskType = "";
		String isFavourite = " ";
		String isFire = " ";
		//String additionalQuery = "";
		String TaskStatus = "OPEN";
		String todayDate = ApplicationUtil.changeToDayMonthYear4(System.currentTimeMillis()/1000-24*60*60);
		//		String isAlarm = " ";
		String valuesFetch = " * ";

		if(selfFlag)
		{
			if(taskType.trim().equals(""))
				taskType = "TaskType = 'self' ";
			else
				taskType = taskType+" OR TaskType = 'self'";
		}
		if(receivedFlag)
		{
			if(taskType.trim().equals(""))
				taskType = "TaskType = 'inbox' ";
			else
				taskType = taskType+" OR TaskType = 'inbox'";
		}
		if(assignedFlag)
		{
			if(taskType.trim().equals(""))
				taskType = "TaskType = 'sent' ";
			else
				taskType = taskType+" OR TaskType = 'sent'";
		}
		//if(selfFlag&&receivedFlag&&assignedFlag)
		

		if(sortBy.equalsIgnoreCase("Priority"))
		{
			//builder.append(" order by CreationDateTime DESC ");
			order = "order by CreationDateTime DESC";
		}
		else if(sortBy.equalsIgnoreCase("CreationDate"))
		{
			//builder.append(" order by UPDATION_DATE_TIME DESC ");
			order = "order by UPDATION_DATE_TIME DESC";
		}
		else
		{
			//builder.append(" order by "+ sortBy+" ASC ");
			order = " order by "+ sortBy+" ASC ";
		}

		if(favFlag)
			isFavourite = " AND IsFavouraite ='true' ";

		if(fireFlag)
			isFire = " AND Priority = '4' ";

		//		if(alarmFlag)
		//			isAlarm = " AND AlarmTime != '' ";
		if(TaskStatus.trim().equalsIgnoreCase("OPEN"))
		{
			//additionalQuery =  " OR (Status = 'CLOSE' AND Closure_Date >= '"+todayDate+"' AND   TaskType = 'sent' ) ";
			//additionalQuery =  " OR (Status = 'OPEN'  AND   TaskType = 'sent' ) ";
		}
		else
			if(TaskStatus.trim().equalsIgnoreCase("CLOSE"))
			{
				//			additionalQuery =  " AND NOT(Closure_Date >= '"+todayDate+"' ) ";
			}
		//notification
		//if(getNonSyncCount( context)==0)
		//{
			if(sortBy.trim().equalsIgnoreCase("CreationDate"))
			{
				
				if(taskType.trim().equals(""))
					taskQuery = "SELECT "+valuesFetch+" FROM Task WHERE ( TaskType = '' AND  ((Status = '"+TaskStatus+"'))" +"   AND IsJunk ='Y' " +isFavourite + isFire+ ")   ORDER BY UPDATION_DATE_TIME  " + order;
				else
					taskQuery = "SELECT "+valuesFetch+" FROM Task WHERE ( ("+taskType+") AND ((Status = '"+TaskStatus+"'))" +"  AND IsJunk ='Y' " +isFavourite + isFire+  ")  ORDER BY UPDATION_DATE_TIME  " + order;
				
			}else
			{
			if(taskType.trim().equals(""))
				taskQuery = "SELECT "+valuesFetch+" FROM Task WHERE ( TaskType = '' AND  ((Status = '"+TaskStatus+"'))" +"   AND IsJunk ='Y' " +isFavourite + isFire+ ")   ORDER BY "+ sortBy+" COLLATE NOCASE " + order;
			else
				taskQuery = "SELECT "+valuesFetch+" FROM Task WHERE ( ("+taskType+") AND ((Status = '"+TaskStatus+"'))" +"  AND IsJunk ='Y' " +isFavourite + isFire+  ")  ORDER BY "+ sortBy+" COLLATE NOCASE " + order;
			}
		}
		//else
		//{
			if(taskType.trim().equals(""))
				//taskQuery = "SELECT "+valuesFetch+" FROM Task WHERE  IsJunk ='N' and ("+taskType+") order by istasksync asc,creationdate desc";
				taskQuery = "SELECT "+valuesFetch+" FROM Task WHERE ( TaskType = '' AND  ((Status = '"+TaskStatus+"'))" +" AND IsJunk ='Y' " +isFavourite + isFire+ ") "+  order ;

			else
				//taskQuery = "SELECT "+valuesFetch+" FROM Task WHERE  IsJunk ='N' and("+taskType+") order by istasksync asc,creationdate desc";
				taskQuery = "SELECT "+valuesFetch+" FROM Task WHERE ( ("+taskType+") AND ((Status = '"+TaskStatus+"'))" +"   AND IsJunk ='Y' " +isFavourite + isFire+  ") "+  order ;


		//}
		//System.out.println("Task Status---"+TaskStatus);
		//istasksync asc,creationdate desc 

		//System.out.println("the task query is----> "+taskQuery);
		listTaskDBList = (HashMap) ApplicationUtil.getInstance().selectListFromQueryNewJunk(taskQuery, context);*/
		
		HashMap listTaskDBList=new HashMap();
		String taskQuery = "";
		String order ="";
		String taskType = "";
		String isFavourite = " ";
		String isFire = " ";
		String additionalQuery = "";
		String todayDate = ApplicationUtil.changeToDayMonthYear4(System.currentTimeMillis()/1000-24*60*60);
		String in="";
		StringBuilder builder=new StringBuilder(" SELECT  *  FROM Task WHERE IsJunk ='Y' AND TaskType in('self','inbox','sent') AND isTaskSnooz!='Y' order by UPDATION_DATE_TIME  DESC ");
		/*if(fireFlag){
			builder.append(" AND Priority='4' ");
		}if(favFlag){
			builder.append(" AND IsFavouraite ='true'  ");
		}if(selfFlag){
			in+="'self',";
		}if(receivedFlag){
			in+="'inbox',";
		}if(assignedFlag){
			in+="'sent',";
		}
		if(!in.isEmpty()){
			in=in.substring(0,in.length()-1);
			builder.append(" AND TaskType in ("+in+")  ");
		}else{
			builder.append(" AND TaskType NOT in ('self','inbox','sent') ");
		}
		
		//sortBy.trim().equalsIgnoreCase("Priority")||sortBy.trim().equalsIgnoreCase("CreationDate")
		if(sortBy.equalsIgnoreCase("Priority"))
		{
			builder.append(" order by CreationDateTime DESC ");
		}
		else if(sortBy.equalsIgnoreCase("CreationDate"))
		{
			builder.append(" order by UPDATION_DATE_TIME DESC ");//DISPLAY_NAME
			
		}else if(sortBy.equalsIgnoreCase("UPDATION_DATE_TIME"))
		{
			builder.append(" order by UPDATION_DATE_TIME DESC ");//DISPLAY_NAME
		}
		else if("Assign_From_Name".equalsIgnoreCase(sortBy))
		{
			builder.append(" order by DISPLAY_NAME,UPDATION_DATE_TIME DESC ");
		}*/
	/*	else
		{
			builder.append(" order by UPDATION_DATE_TIME  DESC ");
		}*/
		
		 listTaskDBList = (HashMap) ApplicationUtil.getInstance().getTaskList(builder.toString(), context);
		return listTaskDBList;
		
	}

	/**
	 * method to fetch contact wise list for tasks
	 * @param TaskType
	 * @param Status
	 * @return
	 * @throws Exception
	 */
	public List<TaskInfoEntity> getContactWiseList(String TaskType, String Status,Context context) throws Exception {
		ArrayList<TaskInfoEntity> listTaskDBList;
		String taskQuery = "";
		if(TaskType.trim().equalsIgnoreCase("inbox"))
		{
			taskQuery = "SELECT Assign_From_Name, Assign_From, count(*) FROM Task WHERE  TaskType ='inbox' AND Status ='"+Status+"' AND IsJunk ='N'  GROUP BY Assign_From ORDER BY Assign_From_Name ";
		}
		else if(TaskType.trim().equalsIgnoreCase("sent"))
			{
				taskQuery = "SELECT Assign_To_Name,Assign_To, count(*) FROM Task WHERE  TaskType ='sent'  AND Status ='"+Status+"' AND IsJunk ='N' GROUP BY Assign_To ORDER BY Assign_To_Name ";
			}

			else if(TaskType.trim().equalsIgnoreCase("self"))
			{
				taskQuery = "SELECT Assign_To_Name,Assign_To, count(*) FROM Task WHERE  TaskType ='self'  AND Status ='"+Status+"' AND IsJunk ='N' GROUP BY Assign_To ORDER BY Assign_To_Name ";
			}else if(TaskType.trim().equalsIgnoreCase("hashTask")){
				
				taskQuery = "SELECT Assign_To_Name,Assign_To, count(*) FROM Task WHERE  TaskType ='self'  AND Status ='"+Status+"' AND IsJunk ='N' GROUP BY Assign_To ORDER BY Assign_To_Name ";

			}

		listTaskDBList = (ArrayList<TaskInfoEntity>) ApplicationUtil.getInstance().selectListFromQueryNew(taskQuery, context);
		//System.out.println("the task query is----> "+taskQuery+"---"+listTaskDBList);
		return listTaskDBList;
	}
	
	
	public List<NotationDto> getHasWiseGroup(Context context) throws Exception {
		
		DBAdapter adapter=DBAdapter.getInstance(context);
		adapter.openDataBase();
		
		List<NotationDto> dtos=adapter.getHasGroup("#");
		adapter.close();
		return dtos;
	}
	
	
	


	/**
	 * method to get list of task against any particular contact
	 * @param TaskType
	 * @param Status
	 * @param Name
	 * @return
	 * @throws Exception
	 */

	public  Map getContactWiseTasks(String TaskType, String Status, String Value, String valType,boolean isFire,boolean isFav,Context context) throws Exception {
		HashMap listTaskDBList;
		StringBuilder queryString=new StringBuilder("Select * from Task WHERE Status = '"+Status+"'");
		String taskQuery = "";
		//String valuesFetch = " Task_Id, Assign_From, Assign_From_Name,Assign_To, Assign_To_Name, Task_Desc, Priority, Target_Date, Reminder_Time, IsFavouraite, TaskType, CreationDate, IsTaskSync, AlarmTime, IsTaskRead , ClosedBy, Closure_Date, Status,isActive ";
		String valuesFetch ="*";
		if(TaskType.trim().equalsIgnoreCase("inbox"))
		{
			if(valType.trim().equalsIgnoreCase("name"))
				queryString.append(" AND TaskType ='inbox'  AND Assign_From_Name = '"+Value+"' ");
				//taskQuery = " SELECT "+valuesFetch+" FROM Task WHERE Status = '"+Status+"' AND TaskType ='inbox'  AND Assign_From_Name = '"+Value+"' "+" ORDER BY UPDATION_DATE_TIME DESC";
			else
				queryString.append(" AND TaskType ='inbox'  AND Assign_From = '"+Value+"'");
			//	taskQuery = " SELECT "+valuesFetch+" FROM Task WHERE Status = '"+Status+"' AND TaskType ='inbox'  AND Assign_From = '"+Value+"' "+" ORDER BY UPDATION_DATE_TIME DESC";
		}
		else if(TaskType.trim().equalsIgnoreCase("sent")){
			if(TaskType.trim().equalsIgnoreCase("sent"))
			{
				if(valType.trim().equalsIgnoreCase("name"))
					queryString.append(" AND TaskType ='sent' AND Assign_To_Name = '"+Value+"'");
					//taskQuery = " SELECT "+valuesFetch+" FROM Task WHERE Status = '"+Status+"' AND TaskType ='sent'  AND Assign_To_Name = '"+Value+"' "+" ORDER BY UPDATION_DATE_TIME DESC";
				else
					queryString.append("  AND TaskType ='sent' AND Assign_To = '"+Value+"' ");
				//	taskQuery = " SELECT "+valuesFetch+" FROM Task WHERE Status = '"+Status+"' AND TaskType ='sent'  AND Assign_To = '"+Value+"' "+" ORDER BY UPDATION_DATE_TIME DESC";
			}
		}
		else if(TaskType.trim().equalsIgnoreCase("hashTask")){
			queryString.append("  AND Task_ID in (select Task_ID from NOTAION_TASK_MAPPING where NAME='"+Value+"' )");

		}else if(TaskType.trim().equalsIgnoreCase("byName")){
			queryString.append("  AND DISPLAY_NAME='"+Value+"' ");

		}
		
		if(isFire){
			queryString.append(" AND Priority='4' ");
		}
		if(isFav){
			queryString.append("AND IsFavouraite ='true' ");
		}
		queryString.append(" ORDER BY UPDATION_DATE_TIME DESC");
		//System.out.println("the task query is----> "+queryString+"---"+valType);
		listTaskDBList = (HashMap)ApplicationUtil.getInstance().selectContactListFromQueryNew(queryString.toString(), context);
		return listTaskDBList;
	}
	
	
	/**
	 * return all # tasks
	 * @param hasValue
	 * @param Status
	 * @param Value
	 * @param valType
	 * @param isFire
	 * @param isFav
	 * @param context
	 * @return
	 */
	public HashMap getHashCategoryTask(String hasValue, String Status, String Value,Context context){
		StringBuilder queryString=new StringBuilder("Select * from Task WHERE Status = '"+Status+"'");
		queryString.append(" AND Task_ID in (select Task_ID from NOTAION_TASK_MAPPING where NAME='"+Value+"' )");
		queryString.append(" ORDER BY UPDATION_DATE_TIME DESC");
		HashMap	listTaskDBList = (HashMap)ApplicationUtil.getInstance().selectContactListFromQueryNew(queryString.toString(), context);
		return listTaskDBList;
	
	}
	
	
	public  Map getTaskByName(String Status, String Value,Context context) throws Exception {
		
		HashMap listTaskDBList;
		StringBuilder queryString=new StringBuilder("Select * from Task WHERE Status = '"+Status+"'");
		 
			if("self".equalsIgnoreCase(Value)){
				queryString.append("  AND TaskType='self' ");

			}else{
				queryString.append("  AND DISPLAY_NAME='"+Value+"' ");
	
			}

		
		
		/*if(isFire){
			queryString.append(" AND Priority='4' ");
		}
		if(isFav){
			queryString.append("AND IsFavouraite ='true' ");
		}*/
		queryString.append(" ORDER BY UPDATION_DATE_TIME DESC");
		listTaskDBList = (HashMap)ApplicationUtil.getInstance().selectContactListFromQueryNew(queryString.toString(), context);
		return listTaskDBList;
	}


	/**
	 * This methods latest UNread Message Date to display
	 * @param TaskId
	 * @return
	 * @throws Exception
	 */
	public String latestUnreadMsgDate(String TaskId,Context context) throws Exception 
	{
		ArrayList<ArrayList<String>> messageDBList;
		String creationDate ="";
		String query = "SELECT Creation_Date FROM Messaging WHERE Task_ID ='"+TaskId+"' AND M_From ='"+ApplicationUtil.getValidNumber(ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context))+"'  ORDER BY Creation_Date DESC LIMIT 1";
		//System.out.println("the task query is----> "+query);
		messageDBList = ApplicationUtil.getInstance().executeQuery(query, context);
		if(messageDBList != null && messageDBList.size()>0)
			creationDate = messageDBList.get(0).get(0);

		return creationDate;

	}

	/**
	 * This method returns if any message is not synced to server
	 * @param TaskId
	 * @return
	 * @throws Exception
	 */
	public boolean msgSyncStatus(String TaskId,Context context) throws Exception 
	{
		int count =0;
		ArrayList<ArrayList<String>> messageDBList;
		String query = "SELECT Count(*) FROM Messaging WHERE Task_ID ='"+TaskId+"' AND IsMessageSync ='N'";
		
		messageDBList = ApplicationUtil.getInstance().executeQuery(query, context);
		//System.out.println("the task query is----> "+query);
		if(messageDBList != null && !messageDBList.isEmpty())
			count = Integer.parseInt(messageDBList.get(0).get(0));
		//System.out.println("the task query is----> "+query);
		if(count == 0)
			return true;
		else
			return false;

	}

	/**
	 * method to update values in DB
	 * @param tableName
	 * @param initalVal
	 * @param ctx
	 * @param where
	 * @param whereArgs
	 */
	public void updateTable(String tableName,  ContentValues initalVal, Context ctx, String where, String[] whereArgs)
	{
		ApplicationUtil.getInstance().updateDataInDB(tableName, initalVal, ctx, where, null);

	}

	public int getNonSyncCount(Context context ) throws Exception 
	{
		int count =0;
		ArrayList<ArrayList<String>> syncList;
		String query = "SELECT Count(*) FROM Task WHERE IsTaskSync ='N'";
		syncList = ApplicationUtil.getInstance().executeQuery(query, context);
		//System.out.println("the task query is----> "+query);
		if(syncList != null && !syncList.isEmpty())
			count = Integer.parseInt(syncList.get(0).get(0));

		return count;

	}
	/**
	 * method used to delete changed Assignee
	 * @param initialValues
	 * @return
	 */
	public void deleteChangeAssignee(String taskId,Context context)
	{
		//ContentValues initialValues = new ContentValues();
		//initialValues.put(Key, Value);
		//int value = ApplicationUtil.getInstance().updateDataInDB("User", initialValues, context, "Mobile_Number ='"+ApplicationConstant.pref.getString("regmbNo", null)+"'", null);
		/*ApplicationUtil.getInstance().deleteRowInTable(
				"Task",
				"WHERE Task_ID = '" + taskId
				+ "'",context );*/
			DBAdapter dbAdapter = DBAdapter.getInstance(context);
		
			try{
				String whereClause= "Task_ID = '" + taskId+ "'";
				dbAdapter.openDataBase();
				int result=dbAdapter.deleteRecordInDB("Task", whereClause, null);
				int tt=dbAdapter.deleteRecordInDB("NOTAION_TASK_MAPPING", "Task_ID = '" + taskId+ "'", null);
				dbAdapter.close();
				if(result!=0)
				{
					//ApplicationConstant.needUIRefresh=true;
				}

			}catch (Exception e) {
				// TODO: handle exception

				e.printStackTrace();

			}
			finally
			{
				dbAdapter.close();
			}
			
		// ApplicationUtil.getInstance().deleteRecordInDB("Task", "", context);
		
		
	}
	
	public int getTotalOpen(Context context)
	{
		int total = 0;
		DBAdapter dbAdapter = DBAdapter.getInstance(context);;
		try {
			dbAdapter.openReadDataBase();
			total=dbAdapter.getTotalOpenTask();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			dbAdapter.close();
		}
		return total;
		
	}
	public int getTotalClose(Context context)
	{
		int total = 0;
		DBAdapter dbAdapter = DBAdapter.getInstance(context);;
		try {
			dbAdapter.openReadDataBase();
			total=dbAdapter.getTotalCloseTask();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			dbAdapter.close();
		}
		return total;
		
	}
	public int getTotalJunk(Context context)
	{
		int total = 0;
		DBAdapter dbAdapter = DBAdapter.getInstance(context);;
		try {
			dbAdapter.openReadDataBase();
			total=dbAdapter.getTotalJunkTask();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			dbAdapter.close();
		}
		return total;
		
	}
	
	public int totalTodayTasks(Context context)
	{
		int total = 0;
		DBAdapter dbAdapter = DBAdapter.getInstance(context);;
		try {
			dbAdapter.openReadDataBase();
			Calendar calendar=Calendar.getInstance();
			int hours=calendar.get(Calendar.HOUR_OF_DAY);
			calendar.add(Calendar.HOUR_OF_DAY, 24-hours);
			 long time=new Date().getTime()-60*60*20*1000;
			total=dbAdapter.countQuery("SELECT  count(*) FROM Task WHERE Status = 'OPEN'  AND IsJunk ='N' AND  Target_Date_TIME>="+time+" AND Target_Date_TIME<="+(calendar.getTimeInMillis())+"");
				} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			dbAdapter.close();
		}
		return total;
		
	}
	
	
	
	public int tomorrowTasks(Context context)
	{
		int total = 0;
		DBAdapter dbAdapter = DBAdapter.getInstance(context);;
		try {
			dbAdapter.openReadDataBase();
			Calendar calendar=Calendar.getInstance();
			int hours=calendar.get(Calendar.HOUR_OF_DAY);
			calendar.add(Calendar.HOUR_OF_DAY, 24-hours);
			long time=calendar.getTimeInMillis()+100;
			total=dbAdapter.countQuery("SELECT  count(*) FROM Task WHERE Status = 'OPEN'  AND IsJunk ='N' AND  Target_Date_TIME>="+time+" AND Target_Date_TIME<="+(calendar.getTimeInMillis()+(60*60*24*1000))+"");
				} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			dbAdapter.close();
		}
		return total;
		
	}
	
	public int snoozCount(Context context)
	{
		int total = 0;
		DBAdapter dbAdapter = DBAdapter.getInstance(context);;
		try {
			dbAdapter.openReadDataBase();
			Calendar calendar=Calendar.getInstance();
			int hours=calendar.get(Calendar.HOUR_OF_DAY);
			calendar.add(Calendar.HOUR_OF_DAY, 24-hours);
			long time=calendar.getTimeInMillis()+100;
			total=dbAdapter.countQuery("SELECT  count(*) FROM Task WHERE Status = 'OPEN' AND  IsJunk ='N' AND   isTaskSnooz='Y' ");
				} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			dbAdapter.close();
		}
		return total;
		
	}
	
	
	public int urgencyCount(Context context)
	{
		int total = 0;
		DBAdapter dbAdapter = DBAdapter.getInstance(context);;
		try {
			dbAdapter.openReadDataBase();
			Calendar calendar=Calendar.getInstance();
			int hours=calendar.get(Calendar.HOUR_OF_DAY);
			calendar.add(Calendar.HOUR_OF_DAY, 24-hours);
			long time=calendar.getTimeInMillis()+100;
			total=dbAdapter.countQuery("SELECT  count(*) FROM Task WHERE Status = 'OPEN' AND  IsJunk ='N' AND  Priority='4'");
				} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			dbAdapter.close();
		}
		return total;
		
	}
	
	
	
	public int next7dayTasks(Context context)
	{
		int total = 0;
		DBAdapter dbAdapter = DBAdapter.getInstance(context);;
		try {
			dbAdapter.openReadDataBase();
			long time=new Date().getTime()-60*60;
			total=dbAdapter.countQuery("SELECT  count(*) FROM Task WHERE Status = 'OPEN'  AND IsJunk ='N' AND  Target_Date_TIME>="+time+" AND Target_Date_TIME<="+(new Date().getTime()+(60*60*24*1000*7))+"");
				} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			dbAdapter.close();
		}
		return total;
		
	}
	
	
	
	public int delayedTask(Context context)
	{
		int total = 0;
		DBAdapter dbAdapter = DBAdapter.getInstance(context);;
		try {
			dbAdapter.openReadDataBase();
			long time=new Date().getTime()-60*60*10*1000;
			total=dbAdapter.countQuery("SELECT  count(*) FROM Task WHERE Status = 'OPEN'  AND IsJunk ='N' AND Target_Date_TIME<"+(time)+" and Target_Date_TIME>0");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			dbAdapter.close();
		}
		return total;
		
	}
	
	public static boolean  isUserRegistered(Context context,String mobileNumber)
	{
		boolean flag =true;
		DBAdapter dbAdapter = DBAdapter.getInstance(context);;
		try {
			dbAdapter.openReadDataBase();
			flag=dbAdapter.isUserRegistered(CommonUtil.getValidMsisdn(mobileNumber));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			//dbAdapter.close();
		}
		return flag;
		
	}

}

