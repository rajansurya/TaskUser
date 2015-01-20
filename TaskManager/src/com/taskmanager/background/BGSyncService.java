/*package com.taskmanager.background;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.taskmanager.util.ApplicationConstant;

*//**
 * service to handle syncing for application
 * @author mayankb
 *
 *//*
public class BGSyncService extends Service 
{

	private Context context;
	private Thread meThread;
	@Override
	public IBinder onBind(Intent arg0) 
	{
		return null;
	}

	@Override
	public void onCreate() 
	{
		System.out.println("service oncreate called");
		ApplicationConstant.STOP_SYNC_THREAD = true;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		context=this;
	//	ApplicationConstant.SYNC_SERVICE_FLAG = true;
		System.out.println("service onstart called");
		//ApplicationConstant.SYNC_FLAG = true;
		meThread=new Thread(new SyncModule(context));
		meThread.start();


	}

	@Override
	public void onDestroy() 
	{
		ApplicationConstant.STOP_SYNC_THREAD = false;
		System.out.println("service destroy called");
		
		if(meThread!=null)
		{
			meThread.interrupt();
		}
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return START_STICKY;
	}

}
*/