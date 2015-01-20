package com.taskmanager.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.taskmanager.app.R;
import com.taskmanager.util.ApplicationConstant;

public class TermAndConditionActivity extends Activity {

	private WebView webViewTerm;
	private static final String TAG = "Main";
	private ProgressDialog progressBar;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.termsandcondition);
		webViewTerm = (WebView) findViewById(R.id.webViewTerm);

		WebSettings settings = webViewTerm.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		settings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
		webViewTerm.setInitialScale(100);
		settings.setLoadWithOverviewMode(true);
		webViewTerm.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		progressBar = ProgressDialog.show(TermAndConditionActivity.this, "","Loading...");
		webViewTerm.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.i(TAG, "Processing webview url click...");
				view.loadUrl(url);
				return true;
			}

			public void onPageFinished(WebView view, String url) {
				Log.i(TAG, "Finished loading URL: " + url);
				if (progressBar.isShowing()) {
					progressBar.dismiss();
				}
			}

			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Log.e(TAG, "Error: " + description);
				Toast.makeText(TermAndConditionActivity.this,
						"Oh no! " + description, Toast.LENGTH_SHORT).show();
				alertDialog.setTitle("Error");
				alertDialog.setMessage(description);
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								return;
							}
						});
				alertDialog.show();
			}
		});
		webViewTerm.loadUrl(ApplicationConstant.TERMS_URL);
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent(TermAndConditionActivity.this, RegistrationActivity.class);
		startActivity(intent);
	}

}
