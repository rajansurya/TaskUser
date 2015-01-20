package com.taskmanager.wheel.widget.adapters;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taskmanager.app.R;


/**
 * Day adapter
 * 
 */
public class DayArrayAdapter extends AbstractWheelTextAdapter {
	private long[] dates=new long[400];
	// Count of days to be shown
	private final int daysCount = 364;

	// Calendar
	Calendar calendar;
	private ArrayList<String> dateStringList;

	/**
	 * Constructor
	 */
	public DayArrayAdapter(Context context, ArrayList<String> list) {
		super(context, R.layout.time2_day, NO_RESOURCE);
		//this.calendar = calendar;
		dateStringList=list;
		setItemTextResource(R.id.time2_monthday);
	}

	@Override
	public View getItem(int index, View cachedView, ViewGroup parent) {
		
		/*int day = -daysCount / 2 + index;
		Calendar newCalendar = (Calendar) calendar.clone();
		newCalendar.roll(Calendar.DAY_OF_YEAR, day);*/

		View view = super.getItem(index, cachedView, parent);

		TextView monthday = (TextView) view.findViewById(R.id.time2_monthday);
		DateFormat format = new SimpleDateFormat("dd,MMM,yyyy");
		monthday.setText(dateStringList.get(index));
		monthday.setTextColor(0xFF111111);
	//	dates[index]=newCalendar.getTimeInMillis();
		return view;
	}

	@Override
	public int getItemsCount() {
		return dateStringList.size();
	}
	
	/*public int getCurrentIndex( ){
		for (int i = 0; i < dates.length; i++) {
			if(getDate(dates[i]).equalsIgnoreCase(getDate(new Date().getTime()))){
				return i;
			}
				
		}
		return 0;
	}*/
 
	
	
	@Override
	public CharSequence getItemText(int index) {
		return dateStringList.get(index);
	}
	
	/*private String getDate(long time){
		SimpleDateFormat dateFormat=new SimpleDateFormat("ddMMyyyy");
		return dateFormat.format(time);
	}*/
}
