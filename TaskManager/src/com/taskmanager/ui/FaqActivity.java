package com.taskmanager.ui;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.taskmanager.app.R;

public class FaqActivity extends Activity {
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.faq);
		webView = (WebView) findViewById(R.id.webview);
		loadFromAssets();
	}

	public void loadFromAssets() {

		webView.loadUrl("file:///android_asset/faq.html"); 
		//webView.setInitialScale(80);
		webView.getSettings().setBuiltInZoomControls(true);

	}
	
}
