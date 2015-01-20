package com.taskmanager.ui;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.taskmanager.app.R;

public class HowToUseActivity extends Activity {
	private WebView webView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.howtouse);
		webView = (WebView) findViewById(R.id.webview);
		loadFromAssets();
	}

	public void loadFromAssets() {

		webView.loadUrl("file:///android_asset/How_to_use_Assigner.html"); 
		webView.getSettings().setBuiltInZoomControls(true);
	}
}
