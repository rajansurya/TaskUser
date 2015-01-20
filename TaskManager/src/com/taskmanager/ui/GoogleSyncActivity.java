package com.taskmanager.ui;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.taskmanager.app.R;
import com.taskmanager.background.GetNameInForeground;
import com.taskmanager.util.ApplicationUtil;

public class GoogleSyncActivity extends Activity {
	private static final String TAG = "PlayHelloActivity";
	private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/tasks";
	public static final String EXTRA_ACCOUNTNAME = "extra_accountname";


	static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
	static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;
	static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;

	private String mEmail;

	private ImageView cancel;

	private ImageView done;
	private String accessToken;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.google_task_sync_confirm);
		cancel=(ImageView)findViewById(R.id.cancelbtnlinearGoogle);
		done=(ImageView)findViewById(R.id.addtaskbtnlinearGoogle);
		
		mEmail = ApplicationUtil.getPreference("email", GoogleSyncActivity.this);

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getAccessTocken();

				//startActivity(new Intent(GoogleSyncActivity.this, AllTaskActivity.class)); //
				//finish();

			}
		});

		done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(GoogleSyncActivity.this, AllTaskActivity.class)); //
				finish();

			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
			if (resultCode == RESULT_OK) {
				mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
				ApplicationUtil.savePreference("email", mEmail, GoogleSyncActivity.this);
				getUsername();
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "You must pick an account", Toast.LENGTH_SHORT).show();
			}
		} else if ((requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR ||
				requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
				&& resultCode == RESULT_OK) {
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/** Called by button in the layout */
	private void getAccessTocken() {
		int statusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (statusCode == ConnectionResult.SUCCESS) {
			getUsername();
		} else if (GooglePlayServicesUtil.isUserRecoverableError(statusCode)) {
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
					statusCode, this, 0 /* request code not used */);
			dialog.show();
		} else {
			// Toast.makeText(this, R.string.unrecoverable_error, Toast.LENGTH_SHORT).show();
		}
	}

	/** Attempt to get the user name. If the email address isn't known yet,
	 * then call pickUserAccount() method so the user can pick an account.
	 */
	private void getUsername() {
		if (mEmail == null) {
			pickUserAccount();
		} else {
			if (isDeviceOnline()) {
				new GetNameInForeground(GoogleSyncActivity.this, mEmail, SCOPE).execute();
			} else {
				Toast.makeText(this, "No network connection available", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/** Starts an activity in Google Play Services so the user can pick an account */
	private void pickUserAccount() {
		String[] accountTypes = new String[]{"com.google"};
		Intent intent = AccountPicker.newChooseAccountIntent(null, null,
				accountTypes, false, null, null, null, null);
		startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
	}

	/** Checks whether the device currently has a network connection */
	private boolean isDeviceOnline() {
		ConnectivityManager connMgr = (ConnectivityManager)
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}


	/**


	    /**
	 * This method is a hook for background threads and async tasks that need to provide the
	 * user a response UI when an exception occurs.
	 */
	public void handleException(final Exception e) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (e instanceof GooglePlayServicesAvailabilityException) {
					// The Google Play services APK is old, disabled, or not present.
					// Show a dialog created by Google Play services that allows
					// the user to update the APK
					int statusCode = ((GooglePlayServicesAvailabilityException)e)
							.getConnectionStatusCode();
					Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
							GoogleSyncActivity.this,
							REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
					dialog.show();
				} else if (e instanceof UserRecoverableAuthException) {
					// Unable to authenticate, such as when the user has not yet granted
					// the app access to the account, but the user can fix this.
					// Forward the user to an activity in Google Play services.
					Intent intent = ((UserRecoverableAuthException)e).getIntent();
					startActivityForResult(intent,
							REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
				}
			}
		});
	}


	public void setAccessTocket(String accessTocken){
		this.accessToken=accessTocken;
		Toast.makeText(GoogleSyncActivity.this, accessTocken, Toast.LENGTH_SHORT).show();
	}


}
