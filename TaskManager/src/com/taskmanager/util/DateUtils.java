package com.taskmanager.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {
	public static final String ddMMYYYY="dd/MM/yyy";
	public static final String ddmmYYYYHHmm="dd/MM/yyyy HH:mm";
	public static final String ddmmYYYYHHmmSS="dd/MM/yyyy HH:mm:ss";
	//public static final String YYYYMMDDHHMM="YYYY-MM-DD HH:MM";
	public static void main(String[] args) throws Exception {
		
		SimpleDateFormat dateFormat=new SimpleDateFormat(ddmmYYYYHHmm);
		System.out.println(dateFormat.getTimeZone());
		System.out.println(dateFormat.format(new Date()));
	
	}
	
	public static Date getDateFromString(String dateStr,String format){
		Date date=null;
		if(dateStr==null)
			return null;
		try {
			SimpleDateFormat dateFormat=new SimpleDateFormat(format);
			date= dateFormat.parse(dateStr);
			//System.out.println("oooooooooooooooooo"+date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}
	
	
	public static String dateToString(Date date){
		if(date==null)
			return null;
		String dateStr=null;
		try {
			SimpleDateFormat dateFormat=new SimpleDateFormat(ddMMYYYY);
			dateStr= dateFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateStr;
	}
	
	
	public static String dateToString(Date date,String format){
		if(date==null)
			return null;
		String dateStr=null;
		try {
			TimeZone gmtTime = TimeZone.getTimeZone("GMT");
			SimpleDateFormat dateFormat=new SimpleDateFormat(format);
			dateFormat.setTimeZone(gmtTime);

			dateStr= dateFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateStr;
	}

	
	
}
