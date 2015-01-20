package com.taskmanager.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;

import com.taskmanager.app.R;

public class MsExchangeActivity extends Activity {
	private WebView webView;
	
	private ImageView cancel;
	
	private ImageView done;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.msexchange_mail_config);
		cancel=(ImageView)findViewById(R.id.msExchangeCancel);
		done=(ImageView)findViewById(R.id.msExchangeDone);
		
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*startActivity(new Intent(MsExchangeActivity.this, AllTaskActivity.class)); //
				finish();*/
				finish();
				
			}
		});
		
		done.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*startActivity(new Intent(MsExchangeActivity.this, AllTaskActivity.class)); //
				*/
				
			}
		});
		
	}

	
	
}
