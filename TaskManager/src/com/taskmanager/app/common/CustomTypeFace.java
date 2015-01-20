package com.taskmanager.app.common;

import android.content.Context;
import android.graphics.Typeface;

public class CustomTypeFace {
	public static Typeface getRobotoMedium(Context context){
		return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium_0.ttf");
	}
	
	public static Typeface getRobotoLight(Context context){
		return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light_0.ttf");
	}
	
	public static Typeface getRobotoRegular(Context context){
		return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
	}
	
	public static Typeface getRobotoBlack(Context context){
		return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Black_0.ttf");
	}
	
	public static Typeface getRobotoBold(Context context){
		return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold_0.ttf");
	}
	
	public static Typeface getRobotoRegularItalic(Context context){
		return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Italic_1.ttf");
	}
	
	public static Typeface getTypeface(Context context, int type){
		if(type == 1){
			return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light_0.ttf");
		}
		else if(type == 2){
			return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
		}
		else if(type == 3){
			return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium_0.ttf");
		}
		else if(type == 4){
			return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Black_0.ttf");
		}
		else if(type == 5){
			return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold_0.ttf");
		}
		else if(type == 6){
			return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Italic_1.ttf");
		}
		return null;
	}
}
