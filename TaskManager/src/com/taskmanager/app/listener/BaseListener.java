package com.taskmanager.app.listener;

import com.taskmanager.app.fragmnets.BaseFragment;

public interface BaseListener { 
    public static interface FragmentChangeListener { 
        public void onFragmentChange(BaseFragment fragment, boolean addToBackStack); 
    } 
} 