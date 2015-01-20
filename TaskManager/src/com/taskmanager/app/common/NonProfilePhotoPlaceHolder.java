package com.taskmanager.app.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.taskmanager.app.R;

public class NonProfilePhotoPlaceHolder {

	public static int getPhotoPlaceHolder(String name){
		if(name == null || name.trim().isEmpty()){
			return R.drawable.holder_7;
		}
		
		String source = String.valueOf(name.charAt(0));
		
		Pattern pattern = Pattern.compile("[a-cA-C]");
		Matcher matcher = pattern.matcher(source);
		if(matcher.matches()){
			return R.drawable.holder_7;
		}
		
		pattern = Pattern.compile("[d-fD-F]");
		matcher = pattern.matcher(source);
		if(matcher.matches()){
			return R.drawable.holder_2;
		}
		
		pattern = Pattern.compile("[g-iG-I]");
		matcher = pattern.matcher(source);
		if(matcher.matches()){
			return R.drawable.holder_3;
		}
		
		pattern = Pattern.compile("[j-lJ-L]");
		matcher = pattern.matcher(source);
		if(matcher.matches()){
//			return R.drawable.holder_4;
			return R.drawable.holder_3;
		}
		
		pattern = Pattern.compile("[m-oM-O]");
		matcher = pattern.matcher(source);
		if(matcher.matches()){
//			return R.drawable.holder_4;
			return R.drawable.holder_5;
		}
		
		pattern = Pattern.compile("[p-rP-R]");
		matcher = pattern.matcher(source);
		if(matcher.matches()){
			return R.drawable.holder_5;
		}
		
		pattern = Pattern.compile("[s-vS-V]");
		matcher = pattern.matcher(source);
		if(matcher.matches()){
			return R.drawable.holder_6;
		}
		
		return R.drawable.holder_1;
	}
	
	public static int getHashPlaceHolder(String name){
		if(name == null || name.trim().isEmpty()){
			return R.drawable.hash_5;
		}
		
		String source = String.valueOf(name.charAt(0));
		
		Pattern pattern = Pattern.compile("[a-eA-E]");
		Matcher matcher = pattern.matcher(source);
		if(matcher.matches()){
			return R.drawable.hash_1;
		}
		
		pattern = Pattern.compile("[f-jF-J]");
		matcher = pattern.matcher(source);
		if(matcher.matches()){
			return R.drawable.hash_2;
		}
		
		pattern = Pattern.compile("[k-nK-N]");
		matcher = pattern.matcher(source);
		if(matcher.matches()){
			return R.drawable.hash_3;
		}
		
		pattern = Pattern.compile("[o-rO-R]");
		matcher = pattern.matcher(source);
		if(matcher.matches()){
			return R.drawable.hash_4;
		}
		
		pattern = Pattern.compile("[s-vS-V]");
		matcher = pattern.matcher(source);
		if(matcher.matches()){
			return R.drawable.hash_5;
		}
		
		return R.drawable.hash_5;
	}
}
