package com.taskmanager.app.fragmnets;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.taskmanager.app.common.AppUtility;
import com.taskmanager.app.listener.BaseListener.FragmentChangeListener;
import com.taskmanager.ui.BaseActivity;

public class BaseFragment extends Fragment{
	protected FragmentChangeListener mFragmentChangeListener; 
	protected BaseFragment mThisFragment;
	  
    public void setFragmentChangeListener(FragmentChangeListener fragmentChangeListener) { 
        this.mFragmentChangeListener = fragmentChangeListener; 
    } 
    
    protected BaseActivity getBaseActivity(){
    	return (BaseActivity)getActivity();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	mThisFragment = this;
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	AppUtility.hideKeyboard(getBaseActivity());
    }
    
    //For handling internal navigation of tabs
    public boolean onBackPressed(){
		return true;
	}
    //For handling internal navigation of tabs
    public void pushFragments(String tag, BaseFragment fragment,boolean shouldAnimate, boolean shouldAdd){
    }
    
    private BaseFragment tabsFragment;

	public BaseFragment getTabsFragment() {
		return tabsFragment;
	}

	public void setTabsFragment(BaseFragment tabsFragment) {
		this.tabsFragment = tabsFragment;
	}
	
	private BaseFragment childTabsFragment;

	public BaseFragment getChildTabsFragment() {
		return childTabsFragment;
	}

	public void setChildTabsFragment(BaseFragment childTabsFragment) {
		this.childTabsFragment = childTabsFragment;
	}
}
