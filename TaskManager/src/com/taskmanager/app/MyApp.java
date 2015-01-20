package com.taskmanager.app;

import android.app.Application;
import android.widget.TextView;

public class MyApp extends Application {
	private static MyApp singleInstance; 
	public Object mSyncObject = new Object();
	
	public TextView commentButtonTextView;
	
	@Override
	public void onCreate() {
		super.onCreate();
		singleInstance = this;
	}
	
	public static MyApp getInstance(){
		return singleInstance;
	}
	
}
