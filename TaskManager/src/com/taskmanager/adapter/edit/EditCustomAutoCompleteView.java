package com.taskmanager.adapter.edit;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class EditCustomAutoCompleteView extends AutoCompleteTextView {
	 
    public EditCustomAutoCompleteView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
     
    public EditCustomAutoCompleteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }
 
    public EditCustomAutoCompleteView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }
 
    // this is how to disable AutoCompleteTextView filter
    @Override
    protected void performFiltering(final CharSequence text, final int keyCode) {
        String filterText = "";
        super.performFiltering(filterText, keyCode);
    }
 
    /*
     * after a selection we have to capture the new value and append to the existing text
     */
    @Override
    protected void replaceText(final CharSequence text) {
    	String temp="";
    	String value=text.toString()+getText().toString();
    	if(value.indexOf(" ")==-1){
    		  super.replaceText(text);
    		  return;
    	}
    	String lastWord = value.substring(value.lastIndexOf(" ")+1);
    	String old=getText().toString();
    	if(lastWord.equalsIgnoreCase("#")){
    		temp=old+(text.toString().replace("#", ""));
    	}else{
       	 temp=(old.replace(lastWord, "")+(text.toString()));
	
    	}
        super.replaceText(temp);
    }
 
}