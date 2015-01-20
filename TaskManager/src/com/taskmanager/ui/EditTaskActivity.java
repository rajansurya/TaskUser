package com.taskmanager.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.taskmanager.app.R;
import com.taskmanager.background.SyncModule;
import com.taskmanager.bean.UpdateTask;
import com.taskmanager.bean.taskListDTO;
import com.taskmanager.util.ApplicationConstant;
import com.taskmanager.util.ApplicationUtil;

/**
 * Activity for rendering create new task and edit task screen
 * 
 * @author mayankb
 * 
 */
public class EditTaskActivity extends Activity {

	private Context context;

	private EditText addTask;

	
	private Button AddTaskBtn;
	private LinearLayout AddTaskBtnLinear;

	public String deadlineDateTxt = "";
	public String reminderDateTime = "";
	public String deadlineTimeTxt = "18:00";
	private ArrayList<ArrayList<String>> assigneeNames = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<String>> assigneeNumbers = new ArrayList<ArrayList<String>>();
	private int spaceInRow = 2;
	private LinearLayout row;

	private String action;

	private String taskSummaryReceived;
	private String priorityReceived;

	private String taskId;
	public String targetDate;
	public String reminderTime;
	public String group_id;

	private String taskAssignee;

	private LinearLayout cancelBtn;
	
	private ImageButton imgOk;
	private ImageButton imgCancel;
		String updatedDesc = null;
		private ImageView addtaskediticon;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		System.out.println("on create called");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

	
		setContentView(R.layout.edittaskview);
		getWindow().setBackgroundDrawable(new ColorDrawable(0));
		

		context = this;
		addTask = (EditText) findViewById(R.id.addtaskedit);

		
		AddTaskBtnLinear = (LinearLayout) findViewById(R.id.addtaskbtnlinear);
		AddTaskBtn = (Button) findViewById(R.id.addtaskbtn);
		AddTaskBtn.setEnabled(true);
		cancelBtn = (LinearLayout) findViewById(R.id.cancelbtnlinear);
		imgOk = (ImageButton)findViewById(R.id.imgOk);
		imgCancel = (ImageButton)findViewById(R.id.imgCancel);
		addtaskediticon= (ImageView)findViewById(R.id.addtaskediticon);
		addTask.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addTask.setCursorVisible(true);
			}
		});
		
		
		addTask.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(s.length()>0)
				{
					addtaskediticon.setBackgroundResource(R.drawable.createtask_blue);
					//enterTask.setVisibility(View.GONE);
				}
				else
				{
					addtaskediticon.setBackgroundResource(R.drawable.createtask);
					//enterTask.setVisibility(View.VISIBLE);
					addTask.setHint("Enter Task");
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				//.setVisibility(View.GONE);
				addTask.setCursorVisible(true);
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				//addTask.setCursorVisible(false);
			}
		});
		
		

		Date mDate = new Date(System.currentTimeMillis());
		SimpleDateFormat mDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		//deadlineDateTxt = mDateFormat.format(mDate);

		if (getIntent().getExtras() != null) {
			action = getIntent().getExtras().getString("action");
			if (action.trim().equals("createTask")) {
				
				
				createTaskView();
			} else {
				
				
				
				
				taskSummaryReceived = getIntent().getExtras().getString("desc");
				priorityReceived = getIntent().getExtras()
						.getString("priority");
				taskId = getIntent().getExtras().getString("taskId");
				targetDate = getIntent().getExtras().getString("targateDate");
				reminderTime = getIntent().getExtras()
					.getString("reminderTime");
				taskAssignee = getIntent().getExtras().getString("assignedTo");
				group_id = getIntent().getExtras().getString("group_id");
				editTaskView(taskId);
			}
		}

		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		imgCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
	}
	
	
	@Override
	  public boolean onTouchEvent(MotionEvent event) {
	    // If we've received a touch notification that the user has touched
	    // outside the app, finish the activity.
	    if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
	      finish();
	      return true;
	    }

	    // Delegate everything else to Activity.
	    return super.onTouchEvent(event);
	  }

	@Override
	protected void onResume() {
		System.out.println("on resume called");
		System.out.println("Called onResume");
		// TODO Auto-generated method stub
		super.onResume();
	  if (action.trim().equals("createTask"))
		 createAssigneeListView();
		

	}

	/**
	 * method for to generate view of screen for editing task
	 * 
	 * @param taskId
	 */
	private void editTaskView(final String taskId) {
		// TODO Auto-generated method stub
		addTask.setText(taskSummaryReceived);
		
		 AddTaskBtnLinear.setEnabled(true);
		AddTaskBtnLinear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AddTaskBtnLinear.setEnabled(false);
				if (!action.trim().equals("createTask")) {
					// if(addTask.getText().t)

					updatedDesc = addTask.getText().toString();
					if (updatedDesc != null && updatedDesc.trim().length() > 0) {
						
						ApplicationConstant.createTask_Flag_run_handle = 1;
						UpdateTask task = new UpdateTask();
						task.setIsTaskUpdateSync("N");
						task.setTask_Desc(updatedDesc);
						task.setTASK_SYNC_TYPE("GROUP");
						task.setGroupId(group_id);
						task.setTask_id(taskId);
						task.setAssignFrom(ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context));
						task.setAssignTo(taskAssignee);
						
						task.setUpdateDate(ApplicationUtil.getGMTDate());
						task.setUpdateDateTime(ApplicationUtil.getGMTLong());
					//	task.setCreationDate(ApplicationUtil.getCurrentDateOfSystem());
						//task.setCreationDateime(System.currentTimeMillis());
						task.setClosureDate(System.currentTimeMillis());
						task.setPriority(priorityReceived);
						task.setTarget_date(targetDate);
						task.setReminderTime(reminderTime);
						new AsyncUpdateTask().execute(task);
					} else {
						AddTaskBtnLinear.setEnabled(true);
						Toast.makeText(
								context,
								getResources().getString(
										R.string.blank_task_desc_toast),
								Toast.LENGTH_LONG).show();
					}

					}
			}
		});



	}

	/**
	 * method for to generate view of screen for creating task
	 */
	private void createTaskView() {
		addTask.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					System.out.println("has focus---" + hasFocus);
					addTask.setHint("");
				}

			}
		});

	}

	/**
	 * method to create task
	 */
	private void addTask() {

		

		String taskDesc = addTask.getText().toString();

		taskListDTO task = new taskListDTO();

		task.setAssignFrom((ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context)));
		task.setTaskDesc(taskDesc);
		task.setCreationDate(ApplicationUtil.getCurrentDateOfSystem());
		task.setStatus("OPEN");
		task.setCreationDateime(System.currentTimeMillis());
		task.setClosureDate(System.currentTimeMillis());
		saveTask(task);

	}

	/**
	 * method to save task in DB and sync if network available
	 * 
	 * @param task
	 */
	private void saveTask(taskListDTO task) {
		SyncModule syncObj = new SyncModule(context);
	//	syncObj.createTask(task);
		syncObj = null;

	}

	/**
	 * click listener for different clicks on screen
	 */
	private OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			int viewId = view.getId();
			if (R.id.deadlinedate == viewId || R.id.deadlinetime == viewId) {
				if (action.trim().equals("createTask")) {
					Intent intent = new Intent(EditTaskActivity.this,
							CalenderActivity.class);
					Bundle b = new Bundle();
					b.putString("action", "createTask");
					//b.putString("targateDate", targetDate);
					//b.putString("reminderTime", reminderTime);
					intent.putExtras(b);
					startActivityForResult(intent, 2);

				} else {
					Intent intent = new Intent(EditTaskActivity.this,
							CalenderActivity.class);
					Bundle b = new Bundle();
					b.putString("action", "reminder");
					//b.putString("targateDate", targetDate);
					//b.putString("reminderTime", reminderTime);
					b.putString("TaskId", taskId);
					intent.putExtras(b);
					startActivityForResult(intent, 2);
				}
			}
			if (R.id.asigneename == viewId || R.id.asigneeicon == viewId) {

				// Intent intent = new Intent(Intent.ACTION_PICK,
				// Contacts.CONTENT_URI);
				Intent intent = new Intent(Intent.ACTION_PICK,
						ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, 1);
			}

			if (R.id.addtaskbtnlinear == viewId||R.id.imgOk==viewId) {
				String taskDescription = addTask.getText().toString();
				if (taskDescription != null
						&& taskDescription.trim().length() > 0) {
										
					AddTaskBtnLinear.setClickable(false);
					AddTaskBtnLinear.setEnabled(false);
					/*
					 * addTask(); Toast.makeText( context,
					 * getResources().getString(
					 * R.string.task_created_toast_synced),
					 * Toast.LENGTH_LONG).show();
					 */
					// ApplicationConstant.createTask_Flag_run_handle=1;
					ApplicationConstant.createTask_Flag_run_handle = 1;
					//new AsyncCreateTask().execute();
					}
				else
					Toast.makeText(
							context,
							getResources().getString(
									R.string.blank_task_desc_toast),
							Toast.LENGTH_LONG).show();

				} 
			}
		

	};

	/**
	 * checked change listener for deadline check
	 */
	private OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			int viewId = buttonView.getId();
			if (R.id.deadlinecheck == viewId) {
				if (isChecked) {
					//mDeadlineDate.setTextColor(getResources().getColor(
					//		R.color.grey));
					//mDeadlineDate.setOnClickListener(mClickListener);
					//mDeadlineTime.setTextColor(getResources().getColor(
					//		R.color.grey));
					//mDeadlineTime.setOnClickListener(mClickListener);

				} else {
					//mDeadlineDate.setTextColor(getResources().getColor(
					//		R.color.light_gray_date));
					//mDeadlineDate.setOnClickListener(null);
					//mDeadlineTime.setTextColor(getResources().getColor(
					//		R.color.light_gray_date));
					//mDeadlineTime.setOnClickListener(null);
				}
			}

		}
	};
	
	
	
	

	/**
	 * to open android phone book
	 */
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);
		System.out.println("Called Onactivity");
		System.out.println("on activity Called----" + reqCode + "-resCode--"
				+ resultCode);

		switch (reqCode) {
		case 1:
			if (resultCode == Activity.RESULT_OK) {
				/*
				 * Uri contactData = data.getData(); Cursor c =
				 * managedQuery(contactData, null, null, null, null); if
				 * (c.moveToFirst()) { String name = c .getString(c
				 * .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				 * String id = c.getString(c
				 * .getColumnIndex(ContactsContract.Contacts._ID)); String num =
				 * ""; if (Integer .parseInt(c.getString(c
				 * .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)))
				 * > 0) { Cursor pCur = getContentResolver()
				 * .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				 * null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +
				 * " = ?", new String[] { id }, null); while (pCur.moveToNext())
				 * { name = c.getString(c
				 * .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)); num
				 * = pCur .getString(pCur
				 * .getColumnIndex(ContactsContract.CommonDataKinds
				 * .Phone.NUMBER));
				 * 
				 * } }
				 */

				//uriContact = data.getData();
				//retrieveContactNumber();
				//relativeSelectAssignee.setBackgroundResource(R.drawable.createassign_blue);
				
			}

		case 2:
			if (resultCode == 123) {/*
				if (data != null && data.getExtras() != null) {

					//deadlineDateTxt = data.getStringExtra("deadlineDateVal");
					//System.out.println("deadlineDateTxt" + deadlineDateTxt);
					// deadlineTimeTxt = data.getStringExtra("deadlineTimeVal");
					// parseDate(String date)
					// ApplicationUtil.compareTwoDates(ApplicationUtil.getCurrentDate(),
					// deadlineDateTxt)==-1
					if ((ApplicationUtil.parseDate(ApplicationUtil
							.getCurrentDate())).before(ApplicationUtil
							.parseDate(deadlineDateTxt)) == true) {
						deadlineTimeTxt = data
								.getStringExtra("deadlineTimeVal");
						mDeadlineTime.setText("" + deadlineTimeTxt);
					} else {

						DateFormat dateFormatter = new SimpleDateFormat("HH:mm");
						dateFormatter.setLenient(false);
						Date today = new Date();
						String s = dateFormatter.format(today);

						System.out.println(Integer.parseInt(data
								.getStringExtra("deadlineTimeVal").replace(":",
										"")) <= Integer.parseInt(s.replace(":",
								"")));
						if (Integer.parseInt(data.getStringExtra(
								"deadlineTimeVal").replace(":", "")) <= Integer
								.parseInt(s.replace(":", ""))) {
							Toast.makeText(context, "Time is incorrect",
									Toast.LENGTH_SHORT).show();
							deadlineTimeTxt = s;
							mDeadlineTime.setText("" + s);
						} else {
							deadlineTimeTxt = data
									.getStringExtra("deadlineTimeVal");
							mDeadlineTime.setText("" + deadlineTimeTxt);
						}
					}
					// System.out.println(s);

					System.out.println("deadlineDateTxt" + deadlineTimeTxt);
					reminderTime = reminderDateTime = data
							.getStringExtra("reminderDateVal");
					System.out.println("deadlineDateTxt" + reminderDateTime);
					targetDate = data.getStringExtra("targetDateVal");
					System.out.println("deadlineDateTxt" + targetDate);

				}
				System.out.println("Result code case 2");
			*/}
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent data = new Intent();
		data.putExtra("taskcreated", false);
		setResult(RESULT_OK, data);
		super.onBackPressed();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		System.out.println("Called onRestoreInstanceState");
		super.onRestoreInstanceState(savedInstanceState);
		int nameSize = savedInstanceState.getInt("sizeNameList");
		/*for (int i = 0; i < nameSize; i++) {
			assigneeNames
					.add(savedInstanceState.getStringArrayList(i + "Name"));
		}
		int numberSize = savedInstanceState.getInt("sizeNumberList");
		for (int i = 0; i < numberSize; i++) {
			assigneeNumbers.add(savedInstanceState.getStringArrayList(i
					+ "Number"));
		}
		System.out.println("assignees Numbers...." + assigneeNumbers);
		System.out.println("assignees Names...." + assigneeNames);
		System.out.println("actvity killed");*/
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {/*
		// TODO Auto-generated method stub
		System.out.println("Called onSaveInstanceState");

		super.onSaveInstanceState(outState);
		outState.putInt("sizeNameList", assigneeNames.size());
		for (int i = 0; i < assigneeNames.size(); i++) {
			outState.putStringArrayList(i + "Name", assigneeNames.get(i));
		}
		outState.putInt("sizeNumberList", assigneeNumbers.size());
		for (int i = 0; i < assigneeNumbers.size(); i++) {
			outState.putStringArrayList(i + "Number", assigneeNumbers.get(i));
		}

	*/}

	/**
	 * method to create assignee list view after selecting contact from phone
	 * book
	 * 
	 * @param name
	 * @param tag
	 */
	private void createAssigneeListView() {/*
		row = new LinearLayout(context);
		spaceInRow = 2;
		System.out.println("Data to render----Assignee Names---->"
				+ assigneeNames + "----assigneeNums---->" + assigneeNumbers);
		assigneeName.removeAllViews();
		if (assigneeNames.size() == 0) {
			TextView hintText = new TextView(context);
			LinearLayout.LayoutParams parameters = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			hintText.setGravity(Gravity.CENTER_VERTICAL);
			hintText.setHint("Assign To");
			hintText.setHintTextColor(getResources()
					.getColor(R.color.hint_gray));
			hintText.setPadding(0, 10, 0, 0);
			hintText.setTextSize(15);
			assigneeName.addView(hintText);
			relativeSelectAssignee.setBackgroundResource(R.drawable.createassign_grey);
		}
		//if(assigneeNumbers!=null&&assigneeNumbers.size()==0)

		for (int i = 0; i < assigneeNames.size(); i++) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(3, 3, 3, 3);
			final TextView newAssignee = new TextView(context);
			newAssignee.setGravity(Gravity.CENTER);
			newAssignee.setLayoutParams(params);
			newAssignee.setPadding(10, 10, 10, 10);
			newAssignee.setText(assigneeNames.get(i).get(0));
			newAssignee.setTextColor(getResources().getColor(R.color.black));
			newAssignee.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.asignee_name));

			newAssignee.setTag(Integer.parseInt(assigneeNames.get(i).get(1)));

			if (spaceInRow == 2) {
				row = new LinearLayout(context);
				row.setOrientation(LinearLayout.HORIZONTAL);
				LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				row.setLayoutParams(params);
				row.addView(newAssignee);
				assigneeName.addView(row);
				spaceInRow--;
			} else {
				assigneeName.removeView(row);
				row.addView(newAssignee);
				assigneeName.addView(row);
				spaceInRow = 2;

			}

			newAssignee.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					String msg = "Do you want to delete \""
							+ newAssignee.getText() + "\" From List?";
					displayConfirmationDialog(msg,
							(Integer) newAssignee.getTag());
					return false;
				}
			});
		}

	*/}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	/**
	 * This method display Confirmation pop up
	 * 
	 * @param msg
	 * @param view
	 */
	private void displayConfirmationDialog(String msg, final int tag) {/*
		final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		
		dialog.setMessage(msg)
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						for (int j = 0; j < assigneeNumbers.size(); j++) {
							if (assigneeNumbers.get(j).get(1).trim()
									.equals(tag + "")) {
								assigneeNumbers.remove(j);
								assigneeNames.remove(j);
							}
						}
						createAssigneeListView();
						dialog.dismiss();
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						});

		dialog.show();

	*/}

	// =========================== changes to get Contact number
	// ==================================================

	/*private String retrieveContactNumber() {

		String contactNumber = null;

		// =================================== Select more than one contact
		// =====================================

		// String [] numbers = null;
		int i = 0;
		boolean flagCheckContact = false;

		String phoneNumber = null;
		String id = null;
		// String name = null;

		ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(uriContact, null, null, null, null);

		if (cur.getCount() > 0) {

			while (cur.moveToNext()) {
				id = cur.getString(cur
						.getColumnIndex(ContactsContract.Contacts._ID));

				name = cur
						.getString(cur
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

				if (Integer
						.parseInt(cur.getString(cur
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					Cursor pCur = cr.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = ?", new String[] { id }, null);
					numbers = new String[pCur.getCount()];
					while (pCur.moveToNext()) {
						phoneNumber = pCur
								.getString(pCur
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						numbers[i] = phoneNumber;
						i++;
						flagCheckContact = true;
					}
					pCur.close();
				} else {
					flagCheckContact = false;
				}
			}

		}
		cur.close();
		if (numbers != null && numbers.length > 0 && flagCheckContact) {
			if (numbers.length > 1) {
				// String name =retrieveContactName();
				AlertDialog.Builder ad = new AlertDialog.Builder(this);
				ad.setTitle(name);
				ad.setItems(numbers, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// contactNumber = numbers[which];
						System.out.println("Numbers are ====== "
								+ numbers[which]);

						int tag = assigneeNumbers.size();

						System.out.println("Phonebook tag added---->" + tag);

						ArrayList<String> tempList = new ArrayList<String>();
						tempList.add(name);
						tempList.add(String.valueOf(tag));
						assigneeNames.add(tempList);
						System.out.println("Phonebook assigneeNames---->"
								+ assigneeNames);
						ArrayList<String> tempList2 = new ArrayList<String>();
						tempList2.add(ApplicationUtil
								.getValidNumber(numbers[which]));
						tempList2.add(String.valueOf(tag));
						assigneeNumbers.add(tempList2);
						System.out.println("Name is" + name);
						System.out.println("Number is"
								+ ApplicationUtil
										.getValidNumber(numbers[which]));
						if (action.trim().equals("createTask"))
							createAssigneeListView();

					}
				});
				AlertDialog adb = ad.create();
				adb.show();
			} else {
				int tag = assigneeNumbers.size();

				System.out.println("Phonebook tag added---->" + tag);

				ArrayList<String> tempList = new ArrayList<String>();
				tempList.add(name);
				tempList.add(String.valueOf(tag));
				assigneeNames.add(tempList);
				System.out.println("Phonebook assigneeNames---->"
						+ assigneeNames);
				ArrayList<String> tempList2 = new ArrayList<String>();
				tempList2.add(ApplicationUtil.getValidNumber(numbers[0]));
				tempList2.add(String.valueOf(tag));
				assigneeNumbers.add(tempList2);
				System.out.println("Name is" + name);
				System.out.println("Number is"
						+ ApplicationUtil.getValidNumber(numbers[0]));
				if (action.trim().equals("createTask"))
					createAssigneeListView();
			}
		} else {
			Toast.makeText(context,
					"Sorry!! Selected Contact does not contain any Number",
					Toast.LENGTH_LONG).show();
		}

		// ==================================================================

		Log.d("Contact", "Contact Phone Number: " + contactNumber);
		return contactNumber;
	}*/

	/*private String retrieveContactName() {

		String contactName = null;

		// querying contact data store
		Cursor cursor = getContentResolver().query(uriContact, null, null,
				null, null);

		if (cursor.moveToFirst()) {

			// DISPLAY_NAME = The display name for the contact.
			// HAS_PHONE_NUMBER = An indicator of whether this contact has at
			// least one phone number.

			contactName = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		}

		cursor.close();

		Log.d("Contact", "Contact Name: " + contactName);
		
		return contactName;
	}*/

	

	/*public class AsyncCreateTask extends AsyncTask<Void, Void, Void>

	{
		private ProgressDialog pDialog = null;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pDialog = new ProgressDialog(EditTaskActivity.this).show(context,
					"", "Please Wait...");

		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			addTask();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			if (pDialog != null) {
				pDialog.dismiss();
			}
//			if(checkBoxSendSms.isChecked())
//			{
//				sendSMS(AssigneeNos, taskText);
//			}
			
			
			ApplicationConstant.createTask_Flag_run_handle = 0;

			
			//  Toast.makeText( context, getResources().getString(
			// R.string.task_created_toast_synced), Toast.LENGTH_LONG).show();
			 
			Intent data = new Intent();
			data.putExtra("taskcreated", true);
			setResult(RESULT_OK, data);
			finish();
			// super.onPostExecute(result);
		}

	};*/

	public class AsyncUpdateTask extends AsyncTask<UpdateTask, Void, Void>

	{
		private ProgressDialog pDialog = null;

		@Override
		protected Void doInBackground(UpdateTask... params) {
			UpdateTask task_update = params[0];
			// TODO Auto-generated method stub
			// addTask();
			// contentValues.put("groupId", list.get(0).getGroupId());

			SyncModule syncObj = new SyncModule(context);
			syncObj.UpdateTask(task_update);
			syncObj = null;

			/*
			 * if (ApplicationUtil.checkInternetConn(context)) {
			 * 
			 * SyncModule syncObj = new SyncModule(context);
			 * syncObj.UpdateTask(); syncObj = null;
			 * ApplicationConstant.createTask_Flag_run_handle=0;
			 * //ApplicationConstant.flag_Ui_Refresh = true;
			 * 
			 * 
			 * 
			 * 
			 * }
			 */

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			ApplicationConstant.createTask_Flag_run_handle = 0;
			if (pDialog != null) {
				pDialog.dismiss();
			}
//			if(checkBoxSendSms.isChecked())
//			{
//				sendSMS(AssigneeNos, taskText);
//			}
			Intent data = new Intent();
			data.putExtra("taskcreated", true);
			setResult(RESULT_OK, data);
			finish();
			Toast.makeText(
					context,
					getResources()
							.getString(R.string.task_updated_toast_synced),
					Toast.LENGTH_LONG).show();
			

			// Intent data = new Intent();
			// data.putExtra("taskcreated", true);
			// setResult(RESULT_OK, data);
			// finish();
			// ApplicationConstant.createTask_Flag_run_handle = 0;

			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pDialog = new ProgressDialog(EditTaskActivity.this).show(context,
					"", "Please Wait...");
			super.onPreExecute();
		}

	};
	
	

}
