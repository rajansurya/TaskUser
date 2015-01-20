package com.taskmanager.ui;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.google.android.gcm.GCMRegistrar;
import com.taskmanager.app.R;
import com.taskmanager.app.common.AppUtility;
import com.taskmanager.background.DialogCallback;
import com.taskmanager.background.SyncModule;
import com.taskmanager.bean.CheckRegistrationDTO;
import com.taskmanager.bean.LoginDTO;
import com.taskmanager.bean.OTPValidateDTO;
import com.taskmanager.bean.registrationDTO;
import com.taskmanager.util.ApplicationConstant;
import com.taskmanager.util.ApplicationUtil;
import com.taskmanager.util.CommonUtil;

/**
 * Registration Screen
 * 
 * @author mayankb
 * 
 */
public class RegistrationActivity extends Activity {

	private Context context;
	private ProgressDialog progressDialog;
	// private ProgressDialog progressDialogCheckStatus;
	private boolean contactRetreived;
	private String regStatus;
	private String responseMsg = "";
	private String prevNo = "";
	private String validationStatus;
	// private boolean isNoValid = false;

	private EditText mobileNo;
	private EditText firstName;
	private EditText emailIdTxt;
	private Spinner hiddenSpinner;
	private RelativeLayout spinnerVirtual;
	private TextView country;
	private TextView countryCallingCode;

	private String first = "";

	private TextView done;

	//private LinearLayout invalidNoMsg;
	//private LinearLayout invalidName;

	private String userSimNo;
	private Handler registrationHandler;
	private int registerationTry = 0;
	private CheckRegistrationDTO checkRegistartioDto;
	private registrationDTO responseDTO1;
	// private Button btnEdit;
	public boolean flagCheckIsEdit = false;
	private TextView txtTerm = null;
	//private CheckBox checkTerm = null;
	private String txtWhatsAppNo = null;
	String regId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;

		setContentView(R.layout.newregistration1);

		hiddenSpinner = (Spinner) findViewById(R.id.hiddenspinner);
		spinnerVirtual = (RelativeLayout) findViewById(R.id.spinnervirtual);
		country = (TextView) findViewById(R.id.editTextCountry);
		countryCallingCode = (TextView) findViewById(R.id.countrycode);
		mobileNo = (EditText) findViewById(R.id.editTextPhoneNo);
		emailIdTxt = (EditText) findViewById(R.id.editEmailId);
		//	invalidNoMsg = (LinearLayout) findViewById(R.id.mobilemsglinearinner);
		//	invalidName = (LinearLayout) findViewById(R.id.namemsglinearinner);
		firstName = (EditText) findViewById(R.id.editTextName);
		done = (TextView) findViewById(R.id.Done);
		// btnEdit = (Button)findViewById(R.id.btnEdit);
		//checkTerm = (CheckBox) findViewById(R.id.checkBoxTerm);
		txtTerm = (TextView) findViewById(R.id.textViewTerm);
		mobileNo.setOnFocusChangeListener(focusListener);
		//	invalidNoMsg.setVisibility(View.INVISIBLE);
		firstName.setOnFocusChangeListener(focusListener);
		// Make sure the device has the proper dependencies.

		done.setOnClickListener(clickListener);
		// btnEdit.setOnClickListener(clickListener);
		txtTerm.setOnClickListener(clickListener);

		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(getApplicationContext());
		GCMRegistrar.checkManifest(getApplicationContext());
//		Intent intent = new Intent("com.taskmanager.app.GCMIntentService");
//		startService(intent);
		mobileNo.requestFocus();
		try {
			regId = GCMRegistrar.getRegistrationId(getApplicationContext());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		/**
		 * Appflyer Methods
		 */

		try{
			AppsFlyerLib.sendTracking(getApplicationContext());
			AppsFlyerLib.sendTrackingWithEvent(getApplicationContext(),"registration","");
		}catch (Exception e)
		{}



	}

	Handler progressHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			System.out.println("progress handler called RegistrationActivity");
			switch (msg.what) {

			case 0:
				if (progressDialog != null)
					progressDialog.dismiss();
				break;
			case 1:
				if (progressDialog != null)
					progressDialog.dismiss();
				Toast.makeText(context,	getResources().getString(R.string.service_error_toast),Toast.LENGTH_SHORT).show();
				mobileNo.setText("");
				break;

			case 2:
				if (progressDialog != null)
					progressDialog.dismiss();

				Toast.makeText(
						context,
						getResources().getString(
								R.string.registration_fail_toast),
								Toast.LENGTH_SHORT).show();
				// Looper.loop();
				break;
			case 3:
				/*
				 * if (progressDialog != null) { progressDialog.dismiss(); }
				 */
				registerDeviceIdOnServer(RegistrationActivity.this);
				checkRegistrationStatus();
				/*if (progressDialog != null)
					progressDialog.dismiss();

				final Dialog dialog = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar);
				WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
				lp.gravity = (Gravity.CENTER);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.otp_alert_view);
				dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
				dialog.getWindow().setAttributes(lp);
				dialog.show();
				Button otpSubmitButton=	(Button)dialog.findViewById(R.id.otpSubmitButton);
				Button genrateOTPButton=	(Button)dialog.findViewById(R.id.genrateOTPButton);
				genrateOTPButton.setOnClickListener(new OnClickListener() {
					@Override
						public void onClick(View v) {
							dialog.dismiss();

						}
					});

				otpSubmitButton.setOnClickListener(new OnClickListener() {
				@Override
					public void onClick(View v) {

					new AsyncTask<Void, Void, String>() {
						private ProgressDialog progressDialogReceived=null;
						@Override
						protected String doInBackground(Void... params) {
							return null;


						}
						@Override
						protected void onPostExecute(String result) {
							if(progressDialogReceived!=null)
							{
								progressDialogReceived.dismiss();
							}
						}
					@Override
					protected void onPreExecute() {
						progressDialogReceived = new ProgressDialog(RegistrationActivity.this);
						progressDialogReceived.setMessage("Loading...");
						progressDialogReceived.setCancelable(false);
						progressDialogReceived.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						progressDialogReceived.show();
					}


					}.execute();


					}
				});*/
				registerDeviceIdOnServer(RegistrationActivity.this);

				break;
			case 4:
				// if(progressDialogCheckStatus!=null)
				// {
				// progressDialogCheckStatus.dismiss();
				// }
				if (progressDialog != null) {
					progressDialog.dismiss();
				}
				SyncModule syncObj = new SyncModule(context);
				syncObj.saveLoginResponse(responseDTO1);
				ContentValues initialValues = new ContentValues();
				initialValues.put("Number_Validation", "CO MPLETE");
				ApplicationUtil.getInstance().updateDataInDB("User",initialValues,context,"Mobile_Number = '"+ ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context)+ "'", null);
				ApplicationUtil.savePreference(ApplicationConstant.REG_STATUS,"COMPLETE",RegistrationActivity.this);
//				startActivity(new Intent(RegistrationActivity.this,	GoogleSyncActivity.class));
				startActivity(new Intent(RegistrationActivity.this,	AllTaskActivity.class));
				AppUtility.hideKeyboard(RegistrationActivity.this);
				finish();

				break;
			case 5:
				if (progressDialog != null) {
					progressDialog.dismiss();
					Toast.makeText(context,
							getResources().getString(
									R.string.registration_fail_toast),
									Toast.LENGTH_LONG).show();
				}
				
				// if(progressDialogCheckStatus!=null)
				// {
				// progressDialogCheckStatus.dismiss();
				// }

				/*Toast.makeText(context,
						getResources().getString(
								R.string.registration_fail_toast),
								Toast.LENGTH_SHORT).show();*/
				break;
			default:
				if (progressDialog != null)
					progressDialog.dismiss();
				break;

			}
		}

	};

	// }

	private void checkRegistrationStatus() {
		// progressDialogCheckStatus =
		// ProgressDialog.show(RegistrationActivity.this, "",
		// "Please Validate SMS!"); // TODO
		// Auto-generated
		// method
		// stub
		// Thread
		Thread checkStatusThread = new Thread() {
			public void run() {
				for (int i = 0; i < 4; i++) {
					try {
						if (responseDTO1 != null&& responseDTO1.getMobileNo() != null) {
							checkRegistartioDto = ApplicationUtil.getInstance().getSyncServer(context).checkRegistrationStatus(responseDTO1.getMobileNo());
							if (checkRegistartioDto != null
									&& checkRegistartioDto
									.getStatus()
									.trim()
									.equalsIgnoreCase(
											getResources().getString(
													R.string.success))
													&& !checkRegistartioDto
													.getRegStatus()
													.trim()
													.equalsIgnoreCase(
															getResources()
															.getString(
																	R.string.checkpartial))
																	&& checkRegistartioDto
																	.getRegStatus()
																	.trim()
																	.equalsIgnoreCase(
																			getResources()
																			.getString(
																					R.string.checkcomplete)))

							{
								// TaskSyncUtils.fullSync(mobileNo.getText().toString(),
								// context,null);
								if (ApplicationConstant.pushNotification_URL == null
										|| CommonUtil.SENDER_ID == null
										|| ApplicationConstant.pushNotification_URL
										.length() == 0
										|| CommonUtil.SENDER_ID.length() == 0) {
									System.out.println("Configuration Error!");

									// stop executing code by return
									return;
								}

								new Thread(new Runnable() {

									@Override
									public void run() {
										for (int j = 1; j <= 2; j++) {
											if (regId.equals("")) {
												// Registration is not present, register
												// now with GCM
												// GCMRegistrar.register(this,
												// CommonUtil.SENDER_ID);
												regId = GCMRegistrar
														.getRegistrationId(context);
												GCMRegistrar.register(context,
														CommonUtil.SENDER_ID);
											} else {
												// Device is already registered on GCM

												if (GCMRegistrar
														.isRegisteredOnServer(context)) {

													System.out
													.println("already register with system");
													try {
														ApplicationUtil
														.savePreference(
																ApplicationConstant.regId,
																responseDTO1
																.getMobileNo(),
																RegistrationActivity.this);
														if(regId!=null&&regId.length()>0)
														{
															ApplicationUtil.registerOnDevice(context,responseDTO1.getMobileNo(),regId);
															break;
														}
													} catch (Exception e) {
														// TODO Auto-generated catch
														// block
														progressHandler.sendEmptyMessage(0);
														e.printStackTrace();
													}
												}

												// CommonUtilities.displayMessage(context,
												// message);

											}


										}

									}
								}).start();	


								//	SyncModule syncObj1 = new SyncModule(context);
								// syncObj1.
								//syncObj1.getTaskList();
								//	syncObj1 = null;
								progressHandler.sendEmptyMessage(4);
								break;
								/*
								 * if(ApplicationConstant.SYNC_SERVICE_STATUS.trim
								 * (
								 * ).equalsIgnoreCase(getResources().getString(R
								 * .string.success))) { ContentValues
								 * initialValues = new ContentValues();
								 * initialValues.put("Number_Validation",
								 * ApplicationConstant.SYNC_SERVICE_STATUS);
								 * ApplicationUtil
								 * .getInstance().updateDataInDB("User",
								 * initialValues, context,
								 * "Mobile_Number = '"+ApplicationConstant
								 * .pref.getString("regmbNo", null)+"'", null);
								 * if(!ApplicationUtil.isMyServiceRunning(
								 * RegistrationActivity.this,
								 * "com.taskmanager.background.BGSyncService"))
								 * { Intent intent = new Intent(context,
								 * BGSyncService.class); startService(intent); }
								 * progressHandler.sendEmptyMessage(4); break; }
								 */
								/*
								 * else { progressHandler.sendEmptyMessage(2);
								 * break; }
								 */
							}

							if (i == 3) {

								ApplicationUtil.getInstance().getSyncServer(context).requestOTPmethod();
								Get_ValidateOTP(progressHandler);

								
								break;

							}
						}
						Thread.sleep(5000);

					}

					catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						progressHandler.sendEmptyMessage(5);
						break;
					}
				}

			}

			// progressHandler.sendEmptyMessage();

		};
		checkStatusThread.start();

	}

	/*
	 *  * progressHandler1=new Handler() {
	 * 
	 * @Override public void handleMessage(Message msg) {
	 * super.handleMessage(msg);
	 * System.out.println("progress handler1 called RegistrationActivity");
	 * if(progressDialog!=null) progressDialog.dismiss(); startActivity(new
	 * Intent(RegistrationActivity.this, AllTaskActivity.class)); //
	 * overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right); }
	 * };
	 * 
	 * registrationHandler=new Handler() {
	 * 
	 * @Override public void handleMessage(Message msg) {
	 * super.handleMessage(msg);
	 * System.out.println("progress handler called RegistrationActivity");
	 * while(true) { Thread thread1 = new Thread() {
	 * 
	 * @Override public void run() { try { registerationTry++;
	 * ApplicationUtil.getInstance
	 * ().getSyncServer().validateNum(ApplicationConstant
	 * .pref.getString("regmbNo", null)); SyncModule syncObj1 = new
	 * SyncModule(); syncObj1.getTaskList(); syncObj1 = null;
	 * 
	 * } catch(Exception e) { e.printStackTrace(); } }
	 * 
	 * }; thread1.start(); try {
	 * if(ApplicationConstant.SYNC_SERVICE_STATUS.trim(
	 * ).equalsIgnoreCase(getResources().getString(R.string.success))) {
	 * ContentValues initialValues = new ContentValues();
	 * initialValues.put("Number_Validation",
	 * ApplicationConstant.SYNC_SERVICE_STATUS);
	 * ApplicationUtil.getInstance().updateDataInDB("User", initialValues,
	 * context,
	 * "Mobile_Number = '"+ApplicationConstant.pref.getString("regmbNo",
	 * null)+"'", null); Intent intent = new Intent(context,
	 * BGSyncService.class); startService(intent);
	 * progressHandler1.sendEmptyMessage(0); break; } else if(registerationTry
	 * == 6) { Looper.prepare(); Toast.makeText(context,
	 * getResources().getString(R.string.service_error_toast),
	 * Toast.LENGTH_SHORT).show(); Looper.loop();
	 * progressHandler.sendEmptyMessage(0); break; } thread1.sleep(3000); }
	 * catch (InterruptedException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * 
	 * 
	 * }
	 * 
	 * 
	 * }
	 * 
	 * };
	 */

	@Override
	protected void onResume() {
		super.onResume();
		Calendar calendar = Calendar.getInstance();
		String timeZoneID = calendar.getTimeZone().getID().toString();
		for (int i = 0; i < getResources().getStringArray(
				R.array.country_timezones_code).length; i++) {
			if (getResources().getStringArray(R.array.country_timezones_code)[i]
					.trim().contains(timeZoneID)) {
				hiddenSpinner.setSelection(i);
			}

		}

		spinnerVirtual.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hiddenSpinner.performClick();

			}
		});

		country.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hiddenSpinner.performClick();

			}
		});

		hiddenSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				country.setText(getResources().getStringArray(
						R.array.country_name_abb)[arg2]);
				countryCallingCode.setText(getResources().getStringArray(
						R.array.country_calling_codes)[arg2]);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		/*	userSimNo = ApplicationUtil.fetchSimNo(context);
		if (userSimNo != null && userSimNo.trim().length() > 0) {
			mobileNo.setText(userSimNo.substring(userSimNo.length() - 10,
					userSimNo.length()));
			// firstName.setSelection(10);

		}*/
		if (mobileNo.getText().toString().trim() != null
				&& !mobileNo.getText().toString().trim().equalsIgnoreCase("")) {
			txtWhatsAppNo = mobileNo.getText().toString().trim();
			if (checkPhoneNo(mobileNo.getText().toString().trim())) {
				if (!contactRetreived) {
					retrieveContactRecord(mobileNo.getText().toString().trim());
					if (first.trim().equalsIgnoreCase("me")) {
						first = "";
					}
					if (mobileNo != null
							&& !mobileNo.getText().toString().trim().equals("")) {

						firstName.setText(first);

					}
				}
			}
		}
		/*
		 * if(mobileNo.getText()!=null&&!mobileNo.getText().toString().trim().
		 * equalsIgnoreCase
		 * ("")&&firstName.getText()!=null&&!firstName.getText().
		 * toString().trim().equalsIgnoreCase("")) {
		 * mobileNo.setTextColor(context
		 * .getResources().getColor(R.color.dark_gray));
		 * mobileNo.setEnabled(false); firstName.setEnabled(false);
		 * flagCheckIsEdit = false;
		 * firstName.setTextColor(context.getResources()
		 * .getColor(R.color.dark_gray)); } else
		 * if(mobileNo.getText()!=null&&!mobileNo
		 * .getText().toString().trim().equalsIgnoreCase("")) {
		 * mobileNo.setTextColor
		 * (context.getResources().getColor(R.color.dark_gray));
		 * mobileNo.setEnabled(false); firstName.setEnabled(true);
		 * flagCheckIsEdit = false; } else
		 * if(mobileNo.getText()!=null&&mobileNo.
		 * getText().toString().trim().equalsIgnoreCase("")) { flagCheckIsEdit =
		 * true; }
		 */

	}

	/**
	 * method to retrieve contact records from phone book
	 * 
	 * @param phoneNo
	 */
	private void retrieveContactRecord(String phoneNo) {
		try {

			if (phoneNo != null && !phoneNo.equalsIgnoreCase("")) {
				String contactId = "";
				if (!prevNo.trim().equalsIgnoreCase(phoneNo)) {
					prevNo = phoneNo;
					contactRetreived = true;
					first = "";
					firstName.setText("");
					progressDialog = ProgressDialog.show(
							RegistrationActivity.this, "", getResources()
							.getString(R.string.progress));
					Uri uri = Uri.withAppendedPath(
							ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
							Uri.encode(phoneNo));
					String[] projection = new String[] {
							ContactsContract.PhoneLookup._ID,
							ContactsContract.PhoneLookup.DISPLAY_NAME };
					String selection = null;
					String[] selectionArgs = null;
					String sortOrder = ContactsContract.PhoneLookup.DISPLAY_NAME
							+ " COLLATE LOCALIZED ASC";
					ContentResolver cr = context.getContentResolver();
					if (cr != null) {
						Cursor resultCur = cr.query(uri, projection, selection,
								selectionArgs, sortOrder);
						if (resultCur != null) {
							while (resultCur.moveToNext()) {
								contactId = resultCur
										.getString(resultCur
												.getColumnIndex(ContactsContract.PhoneLookup._ID));

								break;
							}
							resultCur.close();

							String whereName = ContactsContract.Data.MIMETYPE
									+ " = ? AND "
									+ ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID
									+ " = " + contactId;
							String[] whereNameParams = new String[] { ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };
							Cursor nameCur = context
									.getContentResolver()
									.query(ContactsContract.Data.CONTENT_URI,
											null,
											whereName,
											whereNameParams,
											ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);

							while (nameCur.moveToNext()) {
								if (nameCur
										.getString(nameCur
												.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)) != null) {
									first = nameCur
											.getString(nameCur
													.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));

									if (nameCur
											.getString(nameCur
													.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME)) != null)
										first = first
										+ " "
										+ nameCur
										.getString(nameCur
												.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME));

									if (nameCur
											.getString(nameCur
													.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)) != null)
										first = first
										+ " "
										+ nameCur
										.getString(nameCur
												.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
								} else
									first = "";

								break;

							}

							nameCur.close();

						}

					}

					progressHandler.sendEmptyMessage(0);

				}
			}

		} catch (Exception e) {
			progressHandler.sendEmptyMessage(0);
			e.printStackTrace();
		}
	}

	/**
	 * This method display validation pop up
	 * 
	 * @param msg
	 * @param view
	 */
	private void displayValidationDialog(String msg) {
		final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage(msg).setCancelable(false)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});

		dialog.show();

	}

	/**
	 * Method to read OTP not in use
	 * 
	 * @return OTPS
	 */
	/*
	 * public String readSMS() {
	 * 
	 * Uri uriSMSURI = Uri.parse("content://sms/inbox"); long recievedTime = 0;
	 * String phNum ="+919560986662"; Cursor cur =
	 * getContentResolver().query(uriSMSURI, null, null, null,null); String sms
	 * = ""; while (cur.moveToNext()) {
	 * if(cur.getString(cur.getColumnIndex("address"
	 * )).toString().trim().equals(phNum) &&
	 * Long.parseLong(cur.getString(cur.getColumnIndex
	 * ("date")).toString())>recievedTime) { recievedTime =
	 * Long.parseLong(cur.getString(cur.getColumnIndex("date")).toString()); sms
	 * = cur.getString(cur.getColumnIndexOrThrow("body")).toString(); } } return
	 * sms;
	 * 
	 * }
	 *//**
	 * method to validate mobile no.
	 * 
	 * @param val
	 * @return
	 */
	boolean checkPhoneNo(String val) {
		try {
			if(val!=null&&val.length()<8){
				return false;
			}
			if(TextUtils.isDigitsOnly(val)){

				return true;
			} else {

				//invalidNoMsg.setVisibility(View.VISIBLE);
				first = "";
				// isNoValid = false;
				//firstName.setText("");
				return false;
			}
		} catch (Exception e) {
			return true;
		}
	}

	/**
	 * method to complete registration process
	 * 
	 * @param lgnDTO
	 * @return registration status
	 */
	public String registrationProcess(final LoginDTO lgnDTO) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					String status = "";
					// registrationDTO responseDTO1 = new registrationDTO();
					responseDTO1 = ApplicationUtil.getInstance().getSyncServer(context).getRegistration(lgnDTO);
					System.out.println("Response for registration---->"+ responseDTO1);

					if (responseDTO1 != null && !responseDTO1.equals("")&& responseDTO1.getStatus() != null	&& !responseDTO1.getStatus().trim().equals("")) {
						responseDTO1.setFirstName(lgnDTO.getfName());
						responseDTO1.setLastName(lgnDTO.getlName());
						responseDTO1.setMiddleName(lgnDTO.getMiddleName());
						responseDTO1.setMobileNo(lgnDTO.getPhoneNo());
						responseDTO1.setEmail(lgnDTO.getEmail());
						if (responseDTO1.getStatus().trim().equalsIgnoreCase(getResources().getString(R.string.success))) {
							ApplicationUtil.savePreference(ApplicationConstant.REG_NAME,responseDTO1.getFirstName(),RegistrationActivity.this);
							ApplicationUtil.savePreference(ApplicationConstant.xtoken,responseDTO1.getxToken(),	RegistrationActivity.this);
							ApplicationUtil.savePreference(ApplicationConstant.regMobNo,responseDTO1.getMobileNo(),RegistrationActivity.this);
							ApplicationUtil.savePreference(ApplicationConstant.EMAIL_ID,responseDTO1.getEmail(),RegistrationActivity.this);
							ApplicationUtil.savePreference(ApplicationConstant.REG_STATUS,"PARTIAL",RegistrationActivity.this);
							ApplicationConstant.XToken = responseDTO1.getxToken();
							ApplicationConstant.MobileNo = responseDTO1.getMobileNo();
							status = responseDTO1.getStatus();
							regStatus = status;
							if (regStatus != null&& regStatus.trim().equalsIgnoreCase(getResources().getString(R.string.success))) {
													sendSMS();
								progressHandler.sendEmptyMessage(3);

							}

							// syncObj = null;
						} else {
							// registrationHandler.sendEmptyMessage(0);
							progressHandler.sendEmptyMessage(2);

						}
					} else {
						progressHandler.sendEmptyMessage(1);
						// progressHandler2.sendEmptyMessage(0);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
		return regStatus;

	}

	/**
	 * method to receive OTP
	 * 
	 * @return responseMsg
	 */
	/*
	 * public String recieveOTP() { Thread thread = new Thread() {
	 * 
	 * 
	 * @Override public void run() {
	 * 
	 * try { responseMsg =
	 * ApplicationUtil.getInstance().getSyncServer().receiveOTP
	 * (ApplicationConstant.pref.getString("regmbNo", null)); } catch (Exception
	 * e) { e.printStackTrace(); } }}; thread.start(); return responseMsg; }
	 *//**
	 * method to validate received OTP
	 * 
	 * @return status
	 */
	/*
	 * public String validateOTP() { Thread thread = new Thread() {
	 * 
	 * @Override public void run() { OTPValidateDTO responseDTO = new
	 * OTPValidateDTO(); try { responseDTO =
	 * ApplicationUtil.getInstance().getSyncServer().getOTPValidate();
	 * if(responseDTO
	 * .getOTP_status().trim().equalsIgnoreCase(context.getResources
	 * ().getString(R.string.success))) { SyncModule syncObj = new
	 * SyncModule(context); syncObj.saveValidateOTPResponse(responseDTO);
	 * validationStatus = responseDTO.getOTP_status(); }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } }};
	 * 
	 * thread.start(); return validationStatus;
	 * 
	 * 
	 * 
	 * }
	 *//**
	 * method to send message not in use
	 * 
	 * @param number
	 * @return
	 */
	/*
	 * public String sendLongSMS(String number) { String phoneNumber = number;
	 * SmsManager smsManager = SmsManager.getDefault(); int OTP = (int)
	 * (Math.random()*1000000); System.out.println(OTP);
	 * smsManager.sendTextMessage(phoneNumber, null, OTP+"", null, null);
	 * progressHandler.sendEmptyMessage(0); return OTP+"";
	 * 
	 * }
	 *//**
	 * method to validate OTP from server not in use
	 */
	/*
	 * public void ValidationOTP() { SyncModule syncObj = new SyncModule();
	 * OTPValidateDTO responseDTO = new OTPValidateDTO(); try { responseDTO =
	 * ApplicationUtil.getInstance().getSyncServer().getOTPValidate();
	 * if(responseDTO
	 * .getOTP_status().trim().equalsIgnoreCase(context.getResources
	 * ().getString(R.string.success))) {
	 * syncObj.saveValidateOTPResponse(responseDTO); validationStatus =
	 * responseDTO.getOTP_status();
	 * System.out.println("Validation Status is--->"+validationStatus);
	 * if(validationStatus
	 * .trim().equalsIgnoreCase(getResources().getString(R.string.success))) {
	 * Intent intent = new Intent(context, BGSyncService.class);
	 * startService(intent); startActivity(new Intent(RegistrationActivity.this,
	 * AllTaskActivity.class)); //
	 * overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right); }
	 * else { syncObj.resendOTP(); } }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } syncObj = null; }
	 *//**
	 * focus change listener for edit text field
	 */
	OnFocusChangeListener focusListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v.getId() == R.id.editTextName) {

				if (checkPhoneNo(mobileNo.getText().toString().trim())) {
					if (!contactRetreived) {
						retrieveContactRecord(mobileNo.getText().toString()
								.trim());
						if (first.trim().equalsIgnoreCase("me")) {
							first = "";
						}
						if (mobileNo != null
								&& !mobileNo.getText().toString().trim()
								.equals("") && hasFocus) {

							firstName.setText(first);
						}
					}
				}

			} else if (v.getId() == R.id.editTextPhoneNo) {
				if (hasFocus) {
					contactRetreived = false;
				}
			}
		}
	};

	/**
	 * click change listener for different clicks on screen
	 */
	OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.Done) {

				String mobileNumber=mobileNo.getText().toString();
				String fname=firstName.getText().toString();


				if(mobileNumber.trim().isEmpty()||fname.trim().isEmpty())	{
					if(mobileNumber.trim().isEmpty()&&fname.trim().isEmpty()){
						CommonUtilsUi.getCustomeDialog(context, "Please specify phone number and name ", new DialogCallback() {
							@Override
							public Object execuet(Dialog dialog) {
								mobileNo.requestFocus();
								dialog.dismiss();

								return null;
							}
						});
					}
					else if(mobileNumber.trim().isEmpty()){
						CommonUtilsUi.getCustomeDialog(context, "Please specify phone number", new DialogCallback() {

							@Override
							public Object execuet(Dialog dialog) {
								mobileNo.requestFocus();
								dialog.dismiss();

								return null;
							}
						});//checkPhoneNo
					}else if(fname.trim().isEmpty()){
						CommonUtilsUi.getCustomeDialog(context, "Please specify name", new DialogCallback() {

							@Override
							public Object execuet(Dialog dialog) {
								firstName.requestFocus();
								dialog.dismiss();

								return null;
							}
						});
					}
				}else{

					if (checkPhoneNo(mobileNo.getText().toString().trim())) {
						progressDialog = ProgressDialog.show(RegistrationActivity.this, "", getResources().getString(R.string.progress));
						/*	if (txtWhatsAppNo != null
								&& mobileNo != null
								&& txtWhatsAppNo.trim().equals(
										mobileNo.getText().toString().trim())) {
							flagCheckIsEdit = false;
						} else {
							flagCheckIsEdit = true;
						}*/

						//invalidName.setVisibility(View.INVISIBLE);
						LoginDTO lgnDTO = new LoginDTO();
						String number = countryCallingCode.getText().toString()
								+ mobileNo.getText().toString();

						lgnDTO.setPhoneNo(number);
						lgnDTO.setCountryName(country.getText().toString());
						if (firstName.getText().toString().trim().contains(" ")) {
							String[] name = firstName.getText().toString()
									.trim().split(" ");
							if (name.length == 2) {
								lgnDTO.setfName(name[0]);
								lgnDTO.setlName(name[1]);
							} else {
								lgnDTO.setfName(name[0]);
								lgnDTO.setMiddleName(name[1]);
								lgnDTO.setlName(name[2]);

							}
						} else {
							lgnDTO.setfName(firstName.getText().toString());
						}
						if (ApplicationUtil.checkInternetConn(context)) {
							
							lgnDTO.setEmail(emailIdTxt.getText().toString());
							registrationProcess(lgnDTO);

						} else {
							progressHandler.sendEmptyMessage(0);
							displayValidationDialog(getResources().getString(R.string.offline_alert));
						}

						// ApplicationConstant.ApplicantName=firstName.getText().toString();

					} else{

						CommonUtilsUi.getCustomeDialog(context, "Please specify valid phone number", new DialogCallback() {

							@Override
							public Object execuet(Dialog dialog) {
								//	mobileNo.setText("");
								mobileNo.requestFocus();
								dialog.dismiss();

								return null;
							}
						});
					}


				}
			}

			/*
			 * if (v.getId() == R.id.btnEdit) { mobileNo.setEnabled(true);
			 * firstName.setEnabled(true);
			 * btnEdit.setVisibility(View.INVISIBLE); flagCheckIsEdit= true; }
			 */

			if (v.getId() == R.id.textViewTerm) {
				Intent i = new Intent(RegistrationActivity.this,
						TermAndConditionActivity.class);

				startActivity(i);

			}

		}
	};

	public void sendSMS() {
		String phoneNumber = "+919230002323";
		String message = "REGMTASSIGNER";
		try {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(phoneNumber, null, message, null, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("sms sended");
	}

	private void registerDeviceIdOnServer(final Context context){

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					for (int j = 1; j <= 2; j++) {
						if (regId.equals("")) {
							regId = GCMRegistrar.getRegistrationId(context);
							GCMRegistrar.register(context,CommonUtil.SENDER_ID);
						} else {
							if (GCMRegistrar.isRegisteredOnServer(context)) {

								System.out
								.println("already register with system");
								try {
									ApplicationUtil.savePreference(ApplicationConstant.regId,responseDTO1.getMobileNo(),RegistrationActivity.this);
									if(regId!=null&&regId.length()>0)
									{
										ApplicationUtil.registerOnDevice(context,responseDTO1.getMobileNo(),regId);
										break;
									}
								} catch (Exception e) {
									// TODO Auto-generated catch
									// block
									progressHandler.sendEmptyMessage(0);
									e.printStackTrace();
								}
							}

							// CommonUtilities.displayMessage(context,
							// message);

						}


					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}).start();
	}

	private void Get_ValidateOTP(final Handler handler)
	{
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					forcursorbreak:
						for (int j = 0; j <= 4; j++) {

							Uri uriSMSURI = Uri.parse("content://sms/inbox");
							Cursor cur = getContentResolver().query(uriSMSURI, null, null, null,null);
							String otp_get_sms = "";

							whileCursorLoop:
								while (cur.moveToNext()) 
								{
									//if(j==3)
									{
										if(cur.getString(cur.getColumnIndex("address")).equalsIgnoreCase("LM-assign"))
										{
											Pattern pattern = Pattern.compile("\\w+([0-9]+)");
											Matcher matcher = pattern.matcher(cur.getString(cur.getColumnIndex("body")).toString());
											for(int i = 0 ; i < matcher.groupCount(); i++)
											{
												matcher.find();
												System.out.println(matcher.group());
												otp_get_sms = matcher.group() ;
												System.out.println("Here is the OTP ..... " + otp_get_sms);
											}
											
											/*String[] str = cur.getString(cur.getColumnIndex("body")).toString().split("successful confirmation"); 	
											if(str[0].length()>0)
											{
												if (progressDialog != null) {
													progressDialog.dismiss();
												}
											}*/
										}
										if(otp_get_sms.length()==4)
										{
											break whileCursorLoop;
										}
									}
								}

							if(otp_get_sms.length()==4)
							{
								ApplicationConstant.OTP = otp_get_sms;

								if(ValidationOTP()){
									progressHandler.sendEmptyMessage(4);
									return;
								}
								//break forcursorbreak;
							}
							if(j==3){
								progressHandler.sendEmptyMessage(5);
								return;
							}
							Thread.sleep(5000);

						}
				}catch (InterruptedException e)
				{
					
					e.printStackTrace();
					
				}
			}
		}).start();
	}

	public boolean ValidationOTP() { 
		SyncModule syncObj = new SyncModule(context);
		OTPValidateDTO responseDTO = new OTPValidateDTO(); 
		try { 
			responseDTO = ApplicationUtil.getInstance().getSyncServer(context).getOTPValidate();
			if(responseDTO.getOTP_status().trim().equalsIgnoreCase(context.getResources().getString(R.string.success))) 
			{
				syncObj.saveValidateOTPResponse(responseDTO); validationStatus = responseDTO.getOTP_status();
				System.out.println(" Here is Validation Status is--->"+validationStatus);
				if(validationStatus.trim().equalsIgnoreCase(getResources().getString(R.string.success)))
				{
					if (progressDialog != null) {
						progressDialog.dismiss();
					}
//					startActivity(new Intent(RegistrationActivity.this, GoogleSyncActivity.class));
					startActivity(new Intent(RegistrationActivity.this, AllTaskActivity.class));
					AppUtility.hideKeyboard(RegistrationActivity.this);
					finish();
					return true;
				}
				else {
					return false;
					//Get_ValidateOTP();
					
					/*if (progressDialog != null) {
						progressDialog.dismiss();
					}
					Toast.makeText(getApplicationContext(), getString(R.string.registration_fail_toast), Toast.LENGTH_LONG).show();*/
				}
				
			}

		} catch (Exception e) { e.printStackTrace(); } syncObj = null;
		return false;	
	}

}
