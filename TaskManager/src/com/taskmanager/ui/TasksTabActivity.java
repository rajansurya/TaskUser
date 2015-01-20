package com.taskmanager.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.taskmanager.app.R;
import com.taskmanager.app.fragmnets.TaskTabsFragment;


public class TasksTabActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_tabs);
         
        mBaseTabsFragment = new TaskTabsFragment();
        mBaseTabsFragment.setTabsFragment(mBaseTabsFragment);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
//    	.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
    	transaction.replace(R.id.content_frame, mBaseTabsFragment);
    	transaction.commit();
    }
    
    @Override
    public void onBackPressed() {
    	if(mBaseTabsFragment != null && mBaseTabsFragment.onBackPressed()){
    		super.onBackPressed();
    	}
    }
}
