package com.taskmanager.app.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import com.taskmanager.app.R;

public class HashTagMatcher {
	public static interface WordClickedListener {
		public void wordClicked(CharSequence word);
	}
	
	public static Spannable getHashTagsSpannable(Context context, WordClickedListener listener, String source){
		SpannableString spannable = new SpannableString(source);
		if(source == null){
			return spannable;
		}
		
		
		Pattern pattern = Pattern.compile("#(\\b\\w+\\b)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(source);
		
		while(matcher.find()){
			String expression = matcher.group();
			int start = matcher.start();
			int end = start + expression.length();
			spannable.setSpan(new WordClickableSpan(listener), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			spannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.selected_tab_text_color)), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
			spannable.setSpan(new NoUnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return spannable;
	}
	
	private static class WordClickableSpan extends ClickableSpan {
		private WordClickedListener listener;
		
		public WordClickableSpan(WordClickedListener listener) {
			this.listener = listener;
		}
		
	    @Override
	    public void onClick(View widget) {
	        TextView tv = (TextView) widget;
	        Spanned s = (Spanned) tv.getText();
	        int start = s.getSpanStart(this);
	        int end = s.getSpanEnd(this);
	        CharSequence clickedWord = s.subSequence(start, end);
	        listener.wordClicked(clickedWord);
	    }
	}
	
	private static class NoUnderlineSpan extends UnderlineSpan {
	    @Override
	    public void updateDrawState(TextPaint ds) {
	        ds.setUnderlineText(false);
	    }
	}
}
