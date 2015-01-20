/**
 * 
 */
package com.taskmanager.ui;

import com.taskmanager.app.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * @author ibm_admin
 * 
 */
public class PartailAlertActivitity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.partial_activity);
	}
}
