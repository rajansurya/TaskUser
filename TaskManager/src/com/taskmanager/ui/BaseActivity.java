package com.taskmanager.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.taskmanager.app.fragmnets.BaseFragment;
import com.taskmanager.app.fragmnets.TaskTabsFragment;

public class BaseActivity extends FragmentActivity {
	protected TaskTabsFragment mBaseTabsFragment;
	
	protected BaseActivity mThisActivity;
	protected ViewGroup mMainLayout;
	
	private BaseFragment mRequestedActivityForResultFragment;
	
	@Override
	protected void onCreate(Bundle bundle) {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		super.onCreate(bundle);
		mThisActivity = BaseActivity.this;
	}
	
	public void showAlertDialog(String title, String message) { 
        AlertDialog.Builder builder = new AlertDialog.Builder(this); 
        builder.setTitle(title) 
        .setMessage(message) 
        .setCancelable(false) 
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() { 
            @Override
            public void onClick(DialogInterface dialog, int which) { 
            } 
        }).create().show(); 
    } 
	
	public void showAlertDialogWithFinishAction(String title, String message) { 
        AlertDialog.Builder builder = new AlertDialog.Builder(this); 
        builder.setTitle(title) 
        .setMessage(message) 
        .setCancelable(false) 
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() { 
            @Override
            public void onClick(DialogInterface dialog, int which) { 
            	mThisActivity.finish();
            } 
        }).create().show(); 
    } 
	
	public TaskTabsFragment getBaseTabsFragment() {
		return mBaseTabsFragment;
	}
	
	public void startActivityForResult(BaseFragment fragment, Intent intent, int requestCode) {
		mRequestedActivityForResultFragment = fragment;
		super.startActivityForResult(intent, requestCode);
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if(mRequestedActivityForResultFragment != null){
			mRequestedActivityForResultFragment.onActivityResult(arg0, arg1, arg2);
		}
	}
}
