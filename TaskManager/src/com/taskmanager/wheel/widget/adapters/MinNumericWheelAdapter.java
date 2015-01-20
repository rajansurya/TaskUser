/*
 *  Copyright 2011 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.taskmanager.wheel.widget.adapters;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taskmanager.app.R;

/**
 * Numeric Wheel adapter.
 */
public class MinNumericWheelAdapter extends AbstractWheelTextAdapter {

		protected MinNumericWheelAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

		private long[] dates=new long[400];
		// Count of days to be shown
		private final int daysCount = 364;

		// Calendar
		Calendar calendar;
		private ArrayList<String> hoursList;

		/**
		 * Constructor
		 */
		public MinNumericWheelAdapter(Context context, ArrayList<String> list) {
			super(context, R.layout.time2_day, NO_RESOURCE);
			//this.calendar = calendar;
			hoursList=list;
			setItemTextResource(R.id.text);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			
			/*int day = -daysCount / 2 + index;
			Calendar newCalendar = (Calendar) calendar.clone();
			newCalendar.roll(Calendar.DAY_OF_YEAR, day);*/

			View view = super.getItem(index, cachedView, parent);

			TextView monthday = (TextView) view.findViewById(R.id.text);
			//DateFormat format = new SimpleDateFormat("dd,MMM,yyyy");
			monthday.setText(hoursList.get(index));
			monthday.setTextColor(0xFF111111);
		//	dates[index]=newCalendar.getTimeInMillis();
			return view;
		}

		@Override
		public int getItemsCount() {
			return hoursList.size();
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
			return hoursList.get(index);
		}
		
		/*private String getDate(long time){
			SimpleDateFormat dateFormat=new SimpleDateFormat("ddMMyyyy");
			return dateFormat.format(time);
		}*/
}
