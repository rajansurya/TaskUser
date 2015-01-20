package com.taskmanager.autocom;

import com.google.android.gms.internal.cu;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class CustomAutoCompleteView extends AutoCompleteTextView {
	 
    public CustomAutoCompleteView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
     
    public CustomAutoCompleteView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    public CustomAutoCompleteView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
  
    private String mFilterText = "";
    
/*    @Override
    protected void performFiltering(final CharSequence text, final int keyCode) {
    	String filterText = "";
        int hashTaskLastIndex = -1;
    	int contactLastIndex = -1;
    	
    	if(text.toString().indexOf("#")!=-1){
    		hashTaskLastIndex = text.toString().lastIndexOf("#");
    	}
    	if(text.toString().indexOf("@")!=-1){
    		contactLastIndex = text.toString().lastIndexOf("@");
    	}
    	
    	if(hashTaskLastIndex > contactLastIndex){
    		mFilterText = text.toString().substring(hashTaskLastIndex);
    		filterText = text.toString().substring(hashTaskLastIndex);
    	}
    	else if(contactLastIndex > hashTaskLastIndex){
    		mFilterText = text.toString().substring(contactLastIndex);
    		filterText = text.toString().substring(contactLastIndex +1);
    	}
        
        super.performFiltering(filterText, keyCode);
    }
 
    @Override
    protected void replaceText(final CharSequence text) {
    	String totalString = getText().toString();
    	int lastIndex = totalString.lastIndexOf(mFilterText);
    	if(mFilterText.startsWith("#")){
    		totalString = totalString.substring(0, lastIndex) + text;
    	}
    	else if(mFilterText.startsWith("@")){
    		totalString = totalString.substring(0, lastIndex) + text;
    	}
    	mFilterText = "";
    	
        super.replaceText(totalString);
    }*/
    
    
    //Vaibhav
    /*@Override
    protected void performFiltering(final CharSequence text, final int keyCode) {
    	String filterText = "";
        int hashTaskLastIndex = -1;
    	int contactLastIndex = -1;
        String sText        = text.toString().trim();

    	if(sText.indexOf("#")!=-1){
    		hashTaskLastIndex = sText.lastIndexOf("#");
    	}
    	if(sText.indexOf("@")!=-1){
    		contactLastIndex = sText.lastIndexOf("@");
    	}

    	if(hashTaskLastIndex > contactLastIndex){
    		mFilterText = sText.substring(hashTaskLastIndex);
    		filterText = sText.substring(hashTaskLastIndex);
    	}
    	else if(contactLastIndex > hashTaskLastIndex){
    		mFilterText = sText.substring(contactLastIndex);
    		filterText = sText.substring(contactLastIndex +1);
    	}

        super.performFiltering(filterText, keyCode);
    }
 
    @Override
    protected void replaceText(final CharSequence text) {
    	String totalString = getText().toString();
    	int lastIndex = totalString.lastIndexOf(mFilterText);
        if(lastIndex != -1){
            totalString = totalString.substring(0, lastIndex) + text;
        }else{
            totalString = totalString + text;
        }

    	mFilterText = "";
    	
        super.replaceText(totalString);
    }*/
    
    //vishesh
    @Override
    protected void performFiltering(final CharSequence text, final int keyCode) {
    	int cursorIndex = getSelectionEnd();
    	String filterText = "";
    	int hashTaskLastIndex = -1;
    	int contactLastIndex = -1;
    	String sText = text.toString()/*.trim()*/;

    	if(sText.length() == cursorIndex){//last
    		if(sText.indexOf("#")!=-1){
    			hashTaskLastIndex = sText.lastIndexOf("#");
    		}
    		if(sText.indexOf("@")!=-1){
    			contactLastIndex = sText.lastIndexOf("@");
    		}

    		if(hashTaskLastIndex > contactLastIndex){
    			mFilterText = sText.substring(hashTaskLastIndex);
    			filterText = sText.substring(hashTaskLastIndex);
    		}
    		else if(contactLastIndex > hashTaskLastIndex){
    			mFilterText = sText.substring(contactLastIndex);
    			filterText = sText.substring(contactLastIndex +1);
    		}
    	}
    	else {//For middle
    		if(sText.indexOf("#")!=-1){
    			hashTaskLastIndex = sText.lastIndexOf("#", cursorIndex);
    		}
    		if(sText.indexOf("@")!=-1){
    			contactLastIndex = sText.lastIndexOf("@", cursorIndex);
    		}

    		if(hashTaskLastIndex > contactLastIndex){
    			mFilterText = sText.substring(hashTaskLastIndex, cursorIndex);
    			filterText = sText.substring(hashTaskLastIndex, cursorIndex);
    		}
    		else if(contactLastIndex > hashTaskLastIndex){
    			mFilterText = sText.substring(contactLastIndex, cursorIndex);
    			filterText = sText.substring(contactLastIndex +1, cursorIndex);
    		}
    	}

    	super.performFiltering(filterText, keyCode);
    }
 
    @Override
    protected void replaceText(final CharSequence text) {
    	int cursorIndex = getSelectionEnd();
    	String totalString = getText().toString();

    	if(totalString.length() == cursorIndex){//last
    		int lastIndex = totalString.lastIndexOf(mFilterText);
    		if(lastIndex != -1){
    			totalString = totalString.substring(0, lastIndex) + text;
    		}else{
    			totalString = totalString + text;
    		}
    	}
    	else {//For middle
    		String middlePartString = null;
    		int lastIndex = totalString.lastIndexOf(mFilterText, cursorIndex);
    		if(lastIndex != -1){
    			middlePartString = totalString.substring(0, lastIndex);
    			middlePartString = middlePartString + text;
    			middlePartString = middlePartString + totalString.substring((lastIndex + mFilterText.length()), totalString.length());
    			totalString = middlePartString;
    		}else{
    			totalString = totalString + text;
    		}
    	}

    	mFilterText = "";

    	super.replaceText(totalString);
    }
}