package com.taskmanager.adapter.edit;

import java.util.Arrays;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.WindowManager;

import com.taskmanager.adapter.HashTaskDropDownAdapter;
import com.taskmanager.database.DBAdapter;
import com.taskmanager.ui.AllTaskActivity;
import com.taskmanager.util.CommonUtil;

public class EditTextChangedListener implements TextWatcher{
	 
    public static final String TAG = "EditCustomAutoCompleteView.java";
    Context context;
     private String current;
    public EditTextChangedListener(Context context){
        this.context = context;
    }
     
    @Override
    public void afterTextChanged(Editable s) {
    	
    	
        // TODO Auto-generated method stub
         
    }
 
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {
        // TODO Auto-generated method stub
         
    }
 
    @Override
    public void onTextChanged(CharSequence userInput, int start, int before, int count) {
    	AllTaskActivity mainActivity = ((AllTaskActivity) context);
    	
//    	if(userInput.length()>0)
//		{
//			addtaskediticon.setBackgroundResource(R.drawable.createtask_blue);
//			//enterTask.setVisibility(View.GONE);
//		}
//		else
//		{
//			addtaskediticon.setBackgroundResource(R.drawable.createtask);
//			//enterTask.setVisibility(View.VISIBLE);
//			addTask.setHint(getString(R.string.newtaskhint));                             // Changed by Ankit Singh
//		}
//        // if you want to see in the logcat what the user types
        Log.e(TAG, "User input: " + userInput);
        
        if(userInput.toString().indexOf("#")!=-1){
        	
        String input=CommonUtil.getFiestWord(userInput.toString());
        String lastWord = userInput.toString().substring(userInput.toString().lastIndexOf(" ")+1);
        if(lastWord.indexOf("#")!=-1){
        	DBAdapter adapter=DBAdapter.getInstance(context);
            adapter.openReadDataBase();
            
             
            // query the database based on the user input
            	mainActivity.editAutoCompletiTem = adapter.getTaskByHash(lastWord.toString());
            	adapter.close();
            	if(mainActivity.editAutoCompletiTem==null){
            		mainActivity.editAutoCompletiTem=new String[]{};
            	}
        }else{
        	mainActivity.editAutoCompletiTem=new String[]{};

        }
        
        	
        	
        }else{
        	mainActivity.editAutoCompletiTem=new String[]{};
        }
        

        // set our adapter
        mainActivity.editAdapter = new HashTaskDropDownAdapter(mainActivity, Arrays.asList(mainActivity.editAutoCompletiTem));
//        mainActivity.editAdapter = new ArrayAdapter<String> (mainActivity,android.R.layout.simple_list_item_1, mainActivity.editAutoCompletiTem){
//        	public View getView(int position, View convertView, ViewGroup parent) {
//        		View v = super.getView(position, convertView, parent);
//
//        		((TextView) v).setTextSize(12);                                          
//        		((TextView) v).setTextColor(context.getResources().getColor(R.color.black));                                             
//        		((TextView) v) .setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
//
//        		return v;
//        	}          
//        };
        mainActivity.editAdapter.notifyDataSetChanged();
//        mainActivity.editAdapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_dropdown_item_1line, mainActivity.editAutoCompletiTem);
        mainActivity.editAdapter = new HashTaskDropDownAdapter(mainActivity, Arrays.asList(mainActivity.editAutoCompletiTem));
        mainActivity.messageEdit.setAdapter(mainActivity.editAdapter);

        //current=userInput.toString();
    }

}