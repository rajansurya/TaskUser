package com.taskmanager.ui;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.google.android.gcm.GCMRegistrar;
import com.taskmanager.app.R;
import com.taskmanager.bean.CheckRegistrationDTO;
import com.taskmanager.database.DBAdapter;
import com.taskmanager.dto.Contact;
import com.taskmanager.util.ApplicationConstant;
import com.taskmanager.util.ApplicationUtil;
import com.taskmanager.util.CommonUtil;

/**
 * Splash activity for application
 * @author mayankb
 *
 */
public class SplashActivity extends Activity {
	private static final int SPLASH_TIME = 2000;
	private Context context;
	private ProgressDialog progressDialog;
	private Handler progressHandler;
	private String regId="";
	StringBuilder numbers= new StringBuilder();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		ApplicationUtil.savePreference("status", null, this);
		context= this;

		try{
			AppsFlyerLib.setAppsFlyerKey("Uhvi8EYZL9LoeN5ErCEwQE");
			AppsFlyerLib.sendTracking(getApplicationContext());
		}catch (Exception e)
		{}


		try {
			regId = GCMRegistrar.getRegistrationId(context);
			new Thread(new Runnable() {
				@Override
				public void run() {
					for (int j = 1; j <= 2; j++) {
						if (regId==null||regId.equals("")) {
							regId = GCMRegistrar.getRegistrationId(context);
							GCMRegistrar.register(context,CommonUtil.SENDER_ID);
						} else {
							if (GCMRegistrar.isRegisteredOnServer(context)) {
								System.out.println("already register with system");
								try {

									if(regId!=null&&regId.length()>0)
									{
										ApplicationUtil.registerOnDevice(context,ApplicationUtil.getPreference(ApplicationConstant.regMobNo,SplashActivity.this),regId);
										break;
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}



						}


					}

				}
			}).start();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		progressHandler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

			}
		};

		DBAdapter adapter = DBAdapter.getInstance(context);

		try {
			adapter.createDataBase();
			adapter.createNewTable();
			adapter.alterTable();
			adapter.addDefaultCategories();
			/*updateContactList(getContentResolver());                                  // Changed by ANkit Singh

			getcontatcslist();*/

		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			adapter.close();
		}

		try {
			new Thread(new Runnable() {
			@Override
				public void run() {
					updateContactList(getContentResolver());                                  // Changed by ANkit Singh
					getcontatcslist();
					
				}
			}).start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Handler handler = new Handler();
		handler.postDelayed(mTargetRunnable, SPLASH_TIME);
	}

	private Runnable mTargetRunnable = new Runnable() {
		private Thread meThread;

		@Override
		public void run() {

			String regStatus = "";
			String numberValidation = "";

			final String status=ApplicationUtil.getPreference(ApplicationConstant.REG_STATUS,SplashActivity.this);
			final String mobileNumber=ApplicationUtil.getPreference(ApplicationConstant.regMobNo,SplashActivity.this);
			if("COMPLETE".equalsIgnoreCase(status)){
				startActivity(new Intent(SplashActivity.this, AllTaskActivity.class));
				finish();

			}else{

				if (ApplicationUtil.checkInternetConn(context))	{	

					AsyncTask<Void, Void, String> asyncTask=new AsyncTask<Void, Void, String>() {
						String status;
						@Override
						protected String doInBackground(Void... params) {
							try {
								CheckRegistrationDTO	checkRegistartioDto = ApplicationUtil.getInstance().getSyncServer(context).checkRegistrationStatus(mobileNumber);
								if(checkRegistartioDto!=null){
									status=checkRegistartioDto.getRegStatus();
								}

							}catch (Exception e) {
								startActivity(new Intent(SplashActivity.this, RegistrationActivity.class));
								finish();
							}
							return status;

						}
						@Override
						protected void onPostExecute(String result) {
							if(!"COMPLETE".equalsIgnoreCase(result)){
								startActivity(new Intent(SplashActivity.this, RegistrationActivity.class));
								finish();
							}else{
								ContentValues initialValues = new ContentValues();
								initialValues.put("Number_Validation", "COMPLETE");
								ApplicationUtil.getInstance().updateDataInDB("User",initialValues,context,"Mobile_Number = '"+ ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context)+ "'", null);
								ApplicationUtil.savePreference(ApplicationConstant.REG_STATUS,"COMPLETE",SplashActivity.this);
								startActivity(new Intent(SplashActivity.this, AllTaskActivity.class));
								finish();
							}
						}
					};
					asyncTask.execute();
				}else{
					startActivity(new Intent(SplashActivity.this, RegistrationActivity.class));
				}

			} 

		}


	};


	public void setAlarmManager(Context context,String date)
	{
		//Intent i = new Intent("com.taskmanager.background.ReminderReceiver");
		//Intent i = new Intent("com.taskmanager.background.ReminderReceiver");
		//Intent i = new Intent(context, com.taskmanager.ui.reminderActivity.class);
		//i.putExtra("TaskId", taskId);
		//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//context.startActivity(i);
		/** Creating a Pending Intent */
		//	PendingIntent operation = PendingIntent.getActivity(context.getApplicationContext(), 0, i, Intent.FLAG_ACTIVITY_NEW_TASK);

		/** Getting a reference to the System Service ALARM_SERVICE */
		//	AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(context.getApplicationContext().ALARM_SERVICE);

		/** Getting a reference to DatePicker object available in the MainActivity */
		//DatePicker dpDate = (DatePicker) findViewById(R.id.dp_date);

		/** Getting a reference to TimePicker object available in the MainActivity */
		//TimePicker tpTime = (TimePicker) findViewById(R.id.tp_time);

		//			int year = dpDate.getYear();
		//			int month = dpDate.getMonth();
		//			int day = dpDate.getDayOfMonth();
		//			int hour = tpTime.getCurrentHour();
		//			int minute = tpTime.getCurrentMinute();

		/*String date1 = date;//"10/10/2013 15:47:00";
			SimpleDateFormat smDtfm=new SimpleDateFormat("dd/MM/yyyy HH:mm");
			////System.out.println("date is---->"+smDtfm.parse(date).getTime());
			long alarm_time = 0;
			try {
				alarm_time = smDtfm.parse(date1).getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/

		/** Creating a calendar object corresponding to the date and time set by the user */
		//GregorianCalendar calendar = new GregorianCalendar(year,month,day, hour, minute);

		/** Converting the date and time in to milliseconds elapsed since epoch */
		//long alarm_time = calendar.getTimeInMillis();

		/** Setting an alarm, which invokes the operation at alart_time */

		Toast.makeText(context, "this is executed", Toast.LENGTH_SHORT).show();

		Intent intent = new Intent(context, com.taskmanager.background.ReminderReceiver.class);
		//intent.setAction("com.braoadcast.alarm");
		intent.putExtra("TaskId"," 121");
		intent.putExtra("TaskDesc", "hello");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 234324243, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
				+ (10 * 1000), pendingIntent);



		//int i = 10;
		/* Intent intent = new Intent(this, MyBroadcastReceiver.class);
		    PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 234324243, intent, 0);
		    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
		        + (i * 1000), pendingIntent);
		    Toast.makeText(this, "Alarm set in " + i + " seconds",
		        Toast.LENGTH_LONG).show();*/

		//alarmManager.set(AlarmManager.RTC_WAKEUP  , alarm_time , operation);

		/** Alert is set successfully */
		//  Toast.makeText(context.getApplicationContext(), "Alarm is set successfully",Toast.LENGTH_SHORT).show();
	}


	/**
	 * Method to fetch contacts from phone and copy them to database of phone.
	 * @param cr
	 */

	public  void updateContactList(ContentResolver cr) {
		try {
			final DBAdapter adapter=DBAdapter.getInstance(this);
			adapter.openDataBase();
			Uri uri=ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		//	Uri u2=ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
			Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
			while (phones.moveToNext())
			{
				String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)); //.replaceAll("[^a-zA-Z0-9\\s+]+","");
				String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				

				phoneNumber = CommonUtil.getValidMsisdn(phoneNumber);
				int flag_row_count = 0;
				flag_row_count = adapter.CreateorUpdate("USERS_CONTACT", phoneNumber);

				if(flag_row_count == 0)
				{
					ContentValues initialValues = new ContentValues();
					initialValues.put("NAME", name);
					initialValues.put("MOBILE_NUMBER", phoneNumber);
					initialValues.put("REG_STATUS", "N");
					numbers.append(phoneNumber);
					numbers.append(",");
					adapter.insertRecordsInDB("USERS_CONTACT", null, initialValues);
				}
				else if(flag_row_count>0)
				{
					ContentValues initialValues = new ContentValues();
					initialValues.put("NAME", name);
					numbers.append(phoneNumber);
					numbers.append(",");
					String selection = "MOBILE_NUMBER = ?";
					String[] selectionArgs = new String[]{phoneNumber};
					adapter.updateRecordsInDB("USERS_CONTACT", initialValues, selection, selectionArgs);
				}

			}
			numbers.replace(numbers.length()-1, numbers.length(), "");
			
			adapter.close();
			phones.close();
			Thread thread=	new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						adapter.openDataBase();
						//				BGConnector bgConnector=new BGConnectorImpl(SplashActivity.this);
						//				bgConnector.checkRegStatusForUsers(adapter.getNonRegContacList());               Checking status of registered contact stopped by ANkit
						adapter.close();
						//adapter.close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//adapter.close();

				}
			});
			thread.start();
			thread.join();
			//checkRegStatusForUsers
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getcontatcslist()
	{
		try{

			final DBAdapter adapter=DBAdapter.getInstance(this);

			/*BGConnector bgConnector=new BGConnectorImpl(SplashActivity.this);

			adapter.openDataBase();

			bgConnector.checkRegStatusForUsers(adapter.getContacList());*/
			adapter.openDataBase();

			int deleted_num = 0;
			//deleted_num = adapter.deleteRecordInDB("USERS_CONTACT", selection, selectionArgs);

	//		deleted_num = adapter.deletecontactInDb("USERS_CONTACT", numbers);

			List<Contact> con =  adapter.getContacList();

			for (Contact contact : con) {
				String number  = contact.getNumber();


			}

			adapter.close();
		}catch (Exception e)
		{

		}

	}

}