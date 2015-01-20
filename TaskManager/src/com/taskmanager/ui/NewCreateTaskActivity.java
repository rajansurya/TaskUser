package com.taskmanager.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.taskmanager.app.R;

public class NewCreateTaskActivity extends Activity {



	@Override
	protected void onCreate(Bundle savedInstanceState) {System.out.println("on create called");
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.create_task3);
	
	}

}


