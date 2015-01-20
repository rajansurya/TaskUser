package com.taskmanager.app.fragmnets;

import java.util.HashMap;
import java.util.Stack;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.taskmanager.app.R;

public class TaskTabsFragment extends BaseFragment {
	private String TAB_ALL = "All";
	private String TAB_SELF = "Self";
	private String TAB_IN = "In";
	private String TAB_OUT = "Out";
	private String TAB_FOCUS = "Focus";
	private String TAB_URGENT = "Urgent";
	
	/** A HashMap of stacks, where we use tab identifier as keys..*/
    private HashMap<String, Stack<BaseFragment>> mStacks;
    /** Save current tabs identifier in this..*/
    public static String currentTabTag;
	
	private TabHost mTabHost;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_task_tabs, container, false);
		
		initTabControls(view);
        
		return view;
	}
	
	private void initTabControls(View view){
		mStacks = new HashMap<String, Stack<BaseFragment>>();
        mStacks.put(TAB_ALL, new Stack<BaseFragment>());
        mStacks.put(TAB_SELF, new Stack<BaseFragment>());
        mStacks.put(TAB_IN, new Stack<BaseFragment>());
        mStacks.put(TAB_OUT, new Stack<BaseFragment>());
        mStacks.put(TAB_FOCUS, new Stack<BaseFragment>());
        mStacks.put(TAB_URGENT, new Stack<BaseFragment>());
        
        mTabHost = (TabHost) view.findViewById(android.R.id.tabhost);
        mTabHost.setOnTabChangedListener(mTabChangeListener);
        mTabHost.setup();
        
        addTab(view, TAB_ALL, TAB_ALL, 0);
        addTab(view, TAB_SELF, TAB_ALL, 0);
        addTab(view, TAB_IN, TAB_ALL,  0);
        addTab(view, TAB_OUT, TAB_ALL, 0);
        addTab(view, TAB_FOCUS, null, R.drawable.tab_fire_selector_selector);
        addTab(view, TAB_URGENT, null, R.drawable.tab_focus_now_selector_selector);
        
        mTabHost.setCurrentTab(4);
	}
	
	private void addTab(final View view, String tag, String tabName, int selectorResId){
		/* Setup your tab icons and content views.. Nothing special in this..*/
        TabHost.TabSpec spec    =   mTabHost.newTabSpec(tag);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return view.findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator(getTabView(tabName, selectorResId));
        mTabHost.addTab(spec);
	}
	
	private View getTabView(String tabName, int selectorResId) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.task_tab_item, null);
		TextView textView = (TextView) view.findViewById(R.id.TabTextView);
		if(selectorResId != 0){
			textView.setBackgroundResource(selectorResId);
		}
		if(tabName != null){
			textView.setText(tabName);
		}
		return view;
	}
	
	private TabHost.OnTabChangeListener mTabChangeListener = new TabHost.OnTabChangeListener() {
		@Override
		public void onTabChanged(String tabTitle) {
			currentTabTag = tabTitle;
			
			if(mStacks.get(tabTitle).size() == 0){
				BaseFragment fragment = null;

				if(tabTitle.equals(TAB_ALL)){
					fragment = new AllTaskFragment();
					fragment.setTabsFragment(mThisFragment);
					pushFragments(tabTitle, fragment, false, true);
				} else if(tabTitle.equals(TAB_SELF)){
					fragment = new SelfTaskFragment();
					fragment.setTabsFragment(mThisFragment);
					pushFragments(tabTitle, fragment, false, true);
				} else if(tabTitle.equals(TAB_IN)){
					fragment = new ReceivedTaskFragment();
					fragment.setTabsFragment(mThisFragment);
					pushFragments(tabTitle, fragment, false, true);
				} else if(tabTitle.equals(TAB_OUT)){
					fragment = new SentTaskFragment();
					fragment.setTabsFragment(mThisFragment);
					pushFragments(tabTitle, fragment, false, true);
				} else if(tabTitle.equals(TAB_FOCUS)){
					fragment = new FocusingTaskFragment();
					fragment.setTabsFragment(mThisFragment);
					pushFragments(tabTitle, fragment, false, true);
				} else if(tabTitle.equals(TAB_FOCUS)){
					fragment = new UrgentTaskFragment();
					fragment.setTabsFragment(mThisFragment);
					pushFragments(tabTitle, fragment, false, true);
				}
			}else {
				pushFragments(tabTitle, mStacks.get(tabTitle).lastElement(), false, false);
			}
		}
	};
	
	public void setCurrentTab(int index){
		mTabHost.setCurrentTab(index);
	}	
	
	public void pushFragments(String tag, BaseFragment fragment, boolean shouldAnimate, boolean shouldAdd){
      if(shouldAdd){
          mStacks.get(tag).push(fragment);
      }
      FragmentManager manager = getChildFragmentManager();
      FragmentTransaction transaction = manager.beginTransaction();
      if(shouldAnimate){
    	  transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
      }
      transaction.replace(R.id.realtabcontent, fragment);
      transaction.commit();
    }
	
	public void popFragments(){
		BaseFragment fragment = mStacks.get(currentTabTag).elementAt(mStacks.get(currentTabTag).size() - 2);
		mStacks.get(currentTabTag).pop();
		FragmentManager manager = getChildFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
		transaction.replace(R.id.realtabcontent, fragment);
		transaction.commit();
	}
	
	public boolean onBackPressed(){
		boolean isChildConsumeBackPressed = mStacks.get(currentTabTag).lastElement().onBackPressed();
		if(isChildConsumeBackPressed == false){
			return false;
		}
		if(mStacks.get(currentTabTag).size() == 1){
			return true;
		}
		popFragments();
		return false;
	}
}
