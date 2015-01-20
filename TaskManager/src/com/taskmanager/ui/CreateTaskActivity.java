package com.taskmanager.ui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.appsflyer.AppsFlyerLib;
import com.taskmanager.adapter.ContactListDropDownAdapter;
import com.taskmanager.adapter.HashTaskDropDownAdapter;
import com.taskmanager.app.R;
import com.taskmanager.app.common.AppUtility;
import com.taskmanager.app.common.AssigneeTagView;
import com.taskmanager.app.common.CustomTypeFace;
import com.taskmanager.app.common.DateTimeUtil;
import com.taskmanager.autocom.CustomAutoCompleteView;
import com.taskmanager.background.DialogCallback;
import com.taskmanager.background.SyncModule;
import com.taskmanager.bean.ResponseDto;
import com.taskmanager.bean.UpdateTask;
import com.taskmanager.bean.taskListDTO;
import com.taskmanager.database.DBAdapter;
import com.taskmanager.dto.Contact;
import com.taskmanager.dto.TaskInfoEntity;
import com.taskmanager.util.ApplicationConstant;
import com.taskmanager.util.ApplicationUtil;
import com.taskmanager.util.CommonUtil;
import com.taskmanager.wheel.widget.OnWheelChangedListener;
import com.taskmanager.wheel.widget.WheelView;
import com.taskmanager.wheel.widget.adapters.DayArrayAdapter;
import com.taskmanager.wheel.widget.adapters.NumericWheelAdapter;

/**
 * Activity for rendering create new task and edit task screen
 * 
 * @author mayankb
 * 
 */
@SuppressLint("ResourceAsColor")
public class CreateTaskActivity extends FragmentActivity {

	private Context context;
	private String[] reminderValues = {"Off","5 min before","10 min before","30 min before","1 hour before","3 hour before","1 day before"};
	private ArrayAdapter reminderArrayAdapter;
	public  CustomAutoCompleteView mAutoCompleteView;
	
	private Button contatcs_add;

	private TextView mDeadlineDate;
	private TextView mDeadlineTime;
	
	private LinearLayout assigneeName;//Adding assignee on UI runtime
	private ImageView assigneeIcon;
	
	private Spinner reminderSpinner;
	private ToggleButton deadlineCheck;
	private ImageView reminderIcon;

	private RadioGroup priorityGrp;
	private RadioButton lowPriority;
	private RadioButton mediumPriority;
	private RadioButton highPriority;
	private RadioButton firePriority;
	private TextView alarTxtView;
	private ImageView AddTaskBtnLinear;                       // new Done button added by Ankit

	public String deadlineDateTxt = "";
	public String deadlineTimeTxt = "18:00";
	
	public String reminderDateTime = "";
	private ArrayList<String> assigneeNames = new ArrayList<String>();
	private ArrayList<String> assigneeNumbers = new ArrayList<String>();
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

	private ImageView cancelBtn;                          // new cancel button added by Ankit
	Uri uriContact;
	private String contactID;
	String[] numbers;
	private String name;
	private RelativeLayout relativeSelectAssignee;
	String updatedDesc = null;
	private TextView textViewAssigner;
	private CheckBox checkBoxSendSms = null;
	private ImageView addtaskediticon;
	private RelativeLayout reativeAssigner;
	private LinearLayout linearPriority;
	private LinearLayout linearDate;
	private LinearLayout linearTop;
	private LinearLayout linearTopSecnd;
	private RelativeLayout relativeEdit;
	private LinearLayout  linearEditView;
	private LinearLayout  linearHeader;
	private TaskInfoEntity entity;
	private int selectCount=0;
	private TextView selftoDoMsg;
	public String[] autoCompletiTem;

	private ViewGroup mDeadLineLayout;
	private ViewGroup mAlarmLayout;
	private ViewGroup mFireLayout;
	
	private TextView mDeadLineTextView;
	private TextView mAlarmTextView;
	private TextView mFireTextView;
	
	private String mAlarmReminderTime;
	
	private ArrayAdapter<? extends Object> mLastUsedDropDownAdapter;
	private HashTaskDropDownAdapter mHashTaskDropDownAdapter;
	private ContactListDropDownAdapter mContactListDropDownAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vishesh_create_task2);
		
		DBAdapter adapter=DBAdapter.getInstance(context);
		adapter.openReadDataBase();
		
		mHashTaskDropDownAdapter = new HashTaskDropDownAdapter(CreateTaskActivity.this, Arrays.asList(adapter.getTaskByHash("#")));
		mContactListDropDownAdapter = new ContactListDropDownAdapter(CreateTaskActivity.this, adapter.getContacList());
		
		findViewById(R.id.DeadLineCrossButton).setOnClickListener(mButtonsClickListener);
		findViewById(R.id.AlarmCrossButton).setOnClickListener(mButtonsClickListener);
		findViewById(R.id.FireCrossButton).setOnClickListener(mButtonsClickListener);
		
		findViewById(R.id.CalendarButton).setOnClickListener(mClickListener);
		findViewById(R.id.AlarmButton).setOnClickListener(mClickListener);
		findViewById(R.id.AssignButton).setOnClickListener(mClickListener);
		findViewById(R.id.FireButton).setOnClickListener(mButtonsClickListener);
		
		mDeadLineLayout = (ViewGroup)findViewById(R.id.DeadLineLayout);
		mAlarmLayout = (ViewGroup)findViewById(R.id.AlarmLineLayout);
		mFireLayout = (ViewGroup)findViewById(R.id.FireLineLayout);
		
		mDeadLineTextView = (TextView)findViewById(R.id.DeadLineTextView);
		mAlarmTextView = (TextView)findViewById(R.id.AlarmTextView);
		mFireTextView = (TextView)findViewById(R.id.FireTextView);
		mDeadLineTextView.setTypeface(CustomTypeFace.getRobotoLight(CreateTaskActivity.this));
		mAlarmTextView.setTypeface(CustomTypeFace.getRobotoLight(CreateTaskActivity.this));
		mFireTextView.setTypeface(CustomTypeFace.getRobotoLight(CreateTaskActivity.this));
		
		getWindow().setBackgroundDrawable(new ColorDrawable(0));	
		LinearLayout linearEditView=(LinearLayout)findViewById(R.id.linearEditView);

		context = this;
		mAutoCompleteView = (CustomAutoCompleteView) findViewById(R.id.AutoCompleteView);
		mAutoCompleteView.setTypeface(CustomTypeFace.getRobotoLight(CreateTaskActivity.this));
		mAutoCompleteView.setDropDownBackgroundResource(R.drawable.bg_drop_down);
		
		// add the listener so it will tries to suggest while the user types
		mAutoCompleteView.addTextChangedListener(mAutoCompleteViewTextWatcher);
//		mAutoCompleteView.addTextChangedListener(new CustomAutoCompleteTextChangedListener(this));
		autoCompletiTem=new String[]{};
		
		reminderSpinner = (Spinner) findViewById(R.id.reminderspinner);
		reminderArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,reminderValues);
		reminderArrayAdapter.setDropDownViewResource(R.drawable.spinner_style);
		reminderSpinner.setAdapter(reminderArrayAdapter);
		reminderSpinner.setSelection(4);
		reminderSpinner.setEnabled(false);
		reminderSpinner.setClickable(false);


		/**
		 * Appflyer Methods
		 */
		try{
			AppsFlyerLib.sendTracking(getApplicationContext());
		}catch (Exception e) {
		}

		reminderSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if("Off".equalsIgnoreCase(arg0.getItemAtPosition(arg2).toString())){
				}
				if(selectCount==0){
				}
				selectCount++;

				if(deadlineCheck.isChecked()){
					((TextView)reminderSpinner.getSelectedView()).setTextColor(getResources().getColor(R.color.grey));
				}else{
					((TextView)reminderSpinner.getSelectedView()).setTextColor(getResources().getColor(R.color.light_gray_date));
				}
			}
		}); 

		contatcs_add = (Button) findViewById(R.id.contacts_add);

		mDeadlineDate = (TextView) findViewById(R.id.deadlinedate);
		mDeadlineTime = (TextView) findViewById(R.id.deadlinetime);
		
		assigneeName = (LinearLayout) findViewById(R.id.asigneeLyout);
		deadlineCheck = (ToggleButton ) findViewById(R.id.deadlinecheck);
		reminderIcon = (ImageView) findViewById(R.id.remindericons);
		priorityGrp = (RadioGroup) findViewById(R.id.priorityradio);
		lowPriority = (RadioButton) findViewById(R.id.lowpriorityradio);
		mediumPriority = (RadioButton) findViewById(R.id.mediumpriorityradio);
		highPriority = (RadioButton) findViewById(R.id.highpriorityradio);
		firePriority = (RadioButton) findViewById(R.id.firepriorityradio);
		mediumPriority.setChecked(true);
		AddTaskBtnLinear = (ImageView) findViewById(R.id.addtaskbtnlinear);
		AddTaskBtnLinear.setEnabled(true);
		cancelBtn = (ImageView) findViewById(R.id.BackButton);
		textViewAssigner = (TextView)findViewById(R.id.textViewAssigner);
		selftoDoMsg = (TextView)findViewById(R.id.textViewAssigner1);
		checkBoxSendSms = (CheckBox)findViewById(R.id.checkBoxSms);
		addtaskediticon= (ImageView)findViewById(R.id.addtaskediticon);
		alarTxtView= (TextView)findViewById(R.id.alarTxtView);

		String text = "<font color=#515151>Send SMS if assignee not having  </font> <font color=#DC143C>mAssigner </font><font color=#515151>App. Carrier SMS charges will apply.</font>";
		textViewAssigner.setText(Html.fromHtml(text));
		reminderIcon.setEnabled(false);
		reminderIcon.setClickable(false);
		selftoDoMsg.setText(Html.fromHtml("For self To Do maintain it empty."));
		reminderIcon.setEnabled(false);
		reminderIcon.setClickable(false);

		reminderIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		mAutoCompleteView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mAutoCompleteView.setCursorVisible(true);
			}
		});

		mAutoCompleteView.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					return true;
				}
				return false;
			}});

		relativeSelectAssignee= (RelativeLayout)findViewById(R.id.relativeSelectAssignee);
		reativeAssigner=(RelativeLayout)findViewById(R.id.reativeAssigner);
		linearPriority=(LinearLayout)findViewById(R.id.linearPriority);
		linearDate= (LinearLayout)findViewById(R.id.linearDate);
		linearTop= (LinearLayout)findViewById(R.id.linearTop);
		linearTopSecnd= (LinearLayout)findViewById(R.id.linearTopSecnd);
		relativeEdit = (RelativeLayout)findViewById(R.id.relativeEdit);
		linearEditView=(LinearLayout)findViewById(R.id.linearEditView);
		linearHeader=(LinearLayout)findViewById(R.id.linearEditView);

		mDeadlineDate.setOnClickListener(null);
		mDeadlineTime.setOnClickListener(null);
		Calendar calendar=Calendar.getInstance();
		calendar.add(Calendar.HOUR, 12);
		String  currentTargate=new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
		deadlineDateTxt = currentTargate;

		if (getIntent().getExtras() != null) {
			action = getIntent().getExtras().getString("action");
			if (action.trim().equals("createTask")) {
				createTaskView();
			} else {
				contatcs_add.setOnClickListener(mClickListener);
				
				assigneeName.setOnClickListener(mClickListener);
				AddTaskBtnLinear.setOnClickListener(mClickListener);
				deadlineCheck.setOnCheckedChangeListener(mCheckedChangeListener);
				deadlineCheck.setClickable(true);
				lowPriority.setClickable(true);
				mediumPriority.setClickable(true);
				highPriority.setClickable(true);
				firePriority.setClickable(true);
				taskSummaryReceived = getIntent().getExtras().getString("desc");
				mAutoCompleteView.setText(taskSummaryReceived);
				priorityReceived = getIntent().getExtras()
						.getString("priority");
				taskId = getIntent().getExtras().getString("taskId");
				targetDate = getIntent().getExtras().getString("targateDate");

				if(targetDate!=null){
					long targetTimeStamp = ApplicationUtil.getTimeStamp(targetDate);
					if(targetTimeStamp>new Date().getTime()){
						String[] targetDateVals = ApplicationUtil.changeDateTime2( targetTimeStamp).split("\\|\\|");
						String deadlineDateTxt = targetDateVals[0];
						String time = targetDateVals[1];
						mDeadlineDate.setText(deadlineDateTxt);
						mDeadlineTime.setText("" + time);
						deadlineCheck.setChecked(true);
					}
				}else{
					deadlineCheck.setChecked(false);
				}
				if("4".equals(priorityReceived)){
					priorityGrp.check(R.id.firepriorityradio);
					mFireLayout.setVisibility(View.VISIBLE);
				}
			}
		}

		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(0, R.anim.slide_out_to_bottom);
			}
		});
		
		mAutoCompleteView.setOnItemClickListener(mAutoCompleteDropDownItemClickListener);
	}
	
	private AdapterView.OnItemClickListener mAutoCompleteDropDownItemClickListener = new AdapterView.OnItemClickListener(){
		public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
//			if(adapterView.getAdapter() instanceof ContactListDropDownAdapter){
			if(mLastUsedDropDownAdapter instanceof ContactListDropDownAdapter){
//				ContactListDropDownAdapter adapter = (ContactListDropDownAdapter)adapterView.getAdapter();
//				Contact contact = adapter.getItem(arg2);
				
				Contact contact = mContactListDropDownAdapter.getItem(arg2);
				
//				if(mSelectedAssigneeList.contains(contact) == false){
//					mSelectedAssigneeList.add(contact);
//				}
				
				if(assigneeNames.contains(contact.getName()) == false){
					assigneeNames.add(contact.getName());
					assigneeNumbers.add(contact.getNumber());
				}
			}
			updateAutoCompleteViewText();
			mAutoCompleteView.setAdapter(null);
		}
	};
	
    private void updateAutoCompleteViewText(){
    	boolean isAssigneeAvailable = false;
    	SpannableStringBuilder builder = new SpannableStringBuilder();
    	String text = mAutoCompleteView.getText().toString();

    	for(int index = 0; index < assigneeNames.size(); index++){
    		isAssigneeAvailable = true;
//    		if(text.contains(assigneeNames.get(index))){
    			text = text.replaceAll(assigneeNames.get(index), "");
    			SpannableStringBuilder assignee = AssigneeTagView.getAssigneeView(CreateTaskActivity.this, assigneeNames.get(index), assigneeNumbers.get(index));
    			builder.append(assignee).append(" ");
//    		}
    	}
    	if(isAssigneeAvailable){
    		builder.append("\n");
    	}
    	builder.append(text.trim());
    	mAutoCompleteView.setText(builder);
    	mAutoCompleteView.setSelection(mAutoCompleteView.getText().length());
    }
    
    private void updateAssigneeInTempList(){
    	String text = mAutoCompleteView.getText().toString();
    	for(int index = 0; index < assigneeNames.size(); index++){
    		if(text.contains(assigneeNames.get(index)) == false){
    			assigneeNames.remove(index);
    			assigneeNumbers.remove(index);
    		}
    	}
    }
    
	private TextWatcher mAutoCompleteViewTextWatcher = new TextWatcher() {
	     
	    @Override
	    public void afterTextChanged(Editable s) {
	    	System.out.println(s);
	    }
	 
	    @Override
	    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	    	System.out.println(s);
	    }
	    
	    @Override
	    public void onTextChanged(CharSequence userInput, int start, int before, int count) {
	    	int cursorIndex = mAutoCompleteView.getSelectionStart() -1;
	    	String text = userInput.toString();
	    	
	    	if(text.isEmpty()/* || cursorIndex == -1*/){
	    		mAutoCompleteView.dismissDropDown();
	    		mAutoCompleteView.setAdapter(null);
	    		return;
	    	}
	    	
	    	int hashTaskLastIndex = -1;
	    	int contactLastIndex = -1;
	    	int blankLastIndex = -1;
	    	
	    	if(text.indexOf(" ")!=-1){
	    		blankLastIndex = text.lastIndexOf(" ", cursorIndex);
	    	}
	    	if(text.indexOf("#")!=-1){
	    		hashTaskLastIndex = text.lastIndexOf("#", cursorIndex);
	    	}
	    	if(text.indexOf("@")!=-1){
	    		contactLastIndex = text.lastIndexOf("@", cursorIndex);
	    	}
	    	
	    	if(hashTaskLastIndex > contactLastIndex && hashTaskLastIndex > blankLastIndex){
	    		mLastUsedDropDownAdapter = mHashTaskDropDownAdapter;
	    		mAutoCompleteView.setAdapter(mHashTaskDropDownAdapter);
	    	}
	    	else if(contactLastIndex > hashTaskLastIndex/* || (blankLastIndex > hashTaskLastIndex && text.contains("#"))*/){
	    		if(assigneeNames.size() > 4){
	    			mAutoCompleteView.setAdapter(null);
	    		}else {
	    			mLastUsedDropDownAdapter = mContactListDropDownAdapter;
	    			mAutoCompleteView.setAdapter(mContactListDropDownAdapter);
	    		}
	    	}
	    	else if(contactLastIndex == -1 && (hashTaskLastIndex == -1 || blankLastIndex > hashTaskLastIndex)){
	    		mAutoCompleteView.dismissDropDown();
	    		mAutoCompleteView.setAdapter(null);
	    	}
	    	
	    	updateAssigneeInTempList();
	    }
	};
	
	private OnClickListener mButtonsClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			int viewId = view.getId();

			if(viewId == R.id.DeadLineCrossButton){
				mDeadLineLayout.setVisibility(View.GONE);
			}
			else if(viewId == R.id.AlarmCrossButton){
				mAlarmLayout.setVisibility(View.GONE);
			}
			else if(viewId == R.id.FireCrossButton){
				mFireLayout.setVisibility(View.GONE);
			}
			else if(viewId == R.id.FireButton){
				mFireLayout.setVisibility(View.VISIBLE);
			}
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
			finish();
			return true;
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onResume() {
		super.onResume(); {
			if (targetDate != null && targetDate.trim().length() > 0) {
				try {
					long targetTimeStamp = ApplicationUtil .getUpdatedTimestampFromDate(targetDate);
					String[] targetDateVals = ApplicationUtil.changeDateTime2( targetTimeStamp).split("\\|\\|");
					deadlineDateTxt = targetDateVals[0];
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		mDeadlineDate.setText(deadlineDateTxt);
		if (action.trim().equals("createTask")||action.trim().equals("copyTask"))
			createAssigneeListView();
	}

	/**
	 * method for to generate view of screen for creating task
	 */
	private void createTaskView() {
		contatcs_add.setOnClickListener(mClickListener);
		
		assigneeName.setOnClickListener(mClickListener);

		AddTaskBtnLinear.setOnClickListener(mClickListener);
		deadlineCheck.setOnCheckedChangeListener(mCheckedChangeListener);
		lowPriority.setClickable(true);
		mediumPriority.setClickable(true);
		highPriority.setClickable(true);
		firePriority.setClickable(true);
	}

	/**
	 * method to create task
	 */
	@SuppressLint("SimpleDateFormat")
	private ResponseDto  addTask() {
		boolean isSelf=false;
		String assigneeList = "";
		String assigneeName = "";
		for (int i = 0; i < assigneeNumbers.size(); i++) {
			if (i == 0) {
				String number = assigneeNumbers.get(i);
				String regex = "[\\D]";
				number = number.replaceAll(regex, "");
				if(CommonUtil.getValidMsisdn(number).equals(CommonUtil.getRegNum(context))){
					isSelf=true;
				}
				assigneeList = "\""+number+"\"";
				assigneeName = assigneeNames.get(i);

			} else {
				String number = assigneeNumbers.get(i);
				String regex = "[\\D]";
				number = number.replaceAll(regex, "");
				assigneeList = assigneeList + ", "+"\""+number+"\""; 
				assigneeName = assigneeNames.get(i);
			}
		}

		String taskDesc = mAutoCompleteView.getText().toString().trim();
		for(String assignee : assigneeNames){
			taskDesc = taskDesc.replaceAll(assignee, "");
		}
		taskDesc = taskDesc.trim();
		
		if(entity!=null){
			reminderDateTime=entity.getAlarm_Date_Time();
		}
		String priorityVal = "";
		String targetDate = "";
		String closerDate = "";
		String reminderTime = "";
		if(mDeadLineLayout.getVisibility() == View.VISIBLE){
			targetDate = deadlineDateTxt + " " + deadlineTimeTxt + ":00";
		}
		
//		if (deadlineCheck.isChecked()) {
//			if (reminderDateTime != null && reminderDateTime.trim().length() > 0)
//				reminderTime = reminderDateTime + ":00";
//			if(targetDate!=null){
//				SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm");
//				Calendar calendar=Calendar.getInstance();
//				try {
//					int time=0;
//					String remiderBefore=reminderSpinner.getSelectedItem().toString();
//					if(!"Off".equalsIgnoreCase(remiderBefore)){
//						calendar.setTime(dateFormat.parse(targetDate));
//
//						if("5 min before".equalsIgnoreCase(remiderBefore)){
//							time=5;
//						}else if("10 min before".equalsIgnoreCase(remiderBefore)){
//							time=10;
//						}else if("30 min before".equalsIgnoreCase(remiderBefore)){
//							time=30;
//						}else if("1 hour before".equalsIgnoreCase(remiderBefore)){
//							time=60;
//						}else if("3 hour before".equalsIgnoreCase(remiderBefore)){
//							time=180;
//						}else if("1 day before".equalsIgnoreCase(remiderBefore)){
//							time=24*60;
//						}
//						calendar.add(Calendar.MINUTE, -time);
//						reminderTime=dateFormat.format(calendar.getTime());
//						reminderTime = reminderTime + ":00";
//					}
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
//			}
//		}	
			//This code written by vishesh
			if(mAlarmReminderTime != null && mAlarmReminderTime.isEmpty() == false && mAlarmLayout.getVisibility() == View.VISIBLE){
				try {
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
					Date parsed = dateFormat.parse(mAlarmReminderTime);
					dateFormat.applyPattern("dd/MM/yyyy HH:mm:ss");
					reminderTime = dateFormat.format(parsed);
					System.out.println(reminderTime);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		
		String isFav = "";
//		if (priorityGrp.getCheckedRadioButtonId() == R.id.lowpriorityradio) {
//			priorityVal = getResources().getString(R.string.low_priority_val);
//		}
//		else if (priorityGrp.getCheckedRadioButtonId() == R.id.highpriorityradio) {
//			priorityVal = getResources().getString(R.string.high_priority_val);
//		}
//		if (priorityGrp.getCheckedRadioButtonId() == R.id.firepriorityradio) {
//			priorityVal = getResources().getString(R.string.fire_priority_val);
//		}
//		else {
//			priorityVal = getResources().getString(R.string.med_priority_val);
//		}
		
		if (mFireLayout.getVisibility() == View.VISIBLE) {
			priorityVal = getResources().getString(R.string.fire_priority_val);
		}
		else {
			priorityVal = getResources().getString(R.string.med_priority_val);
		}
		
		taskListDTO task = new taskListDTO();

		task.setAssignFrom((ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context)));
		if (assigneeList!=null&&!assigneeList.trim().equalsIgnoreCase(""))
			task.setAssignTo(assigneeList);
		else{
			task.setAssignTo("\""+ (ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context))+"\"" );
			isSelf=true;
		}
		task.setTaskDesc(taskDesc);
		task.setAssignToName(assigneeName);
		task.setTargetDateTime(targetDate);
		task.setPriority(priorityVal);
		task.setCreationDate(ApplicationUtil.getCurrentDateOfSystem());
		task.setClosureDateTime(closerDate);
		task.setReminderTime(reminderTime);
		task.setFavouraite(isFav);
		task.setStatus("OPEN");
		task.setCreationDateime(System.currentTimeMillis());
		task.setClosureDate(System.currentTimeMillis());
		task.setIsMsgSendChecked(checkBoxSendSms.isChecked()?"Y":"N");
		task.setUpdatedDateTime(System.currentTimeMillis());
		task.setUpdationDate(ApplicationUtil.getCurrentDateOfSystem());
		if(isSelf){
			SyncModule syncObj = new SyncModule(context);
			ResponseDto responseDto=new ResponseDto();
			syncObj.createselfTask(task);
			responseDto.setStatus("00");
			return responseDto;
		}
		return saveTask(task);
	}

	/**
	 * method to save task in DB and sync if network available
	 * @param task
	 */
	private ResponseDto  saveTask(taskListDTO task) {
		SyncModule syncObj = new SyncModule(context);
		ResponseDto responseDto=	syncObj.createTask(task);
		syncObj = null;
		return responseDto;
	}

	/**
	 * click listener for different clicks on screen
	 */
	private OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			int viewId = view.getId();
			if (R.id.deadlinedate == viewId || R.id.deadlinetime == viewId || R.id.CalendarButton == viewId) {
				if (action.trim().equals("createTask")) {
					DateTimeUtil dateTimeUtil = new DateTimeUtil(CreateTaskActivity.this, mOnDateSetListener);
					dateTimeUtil.showDatePicker();
					
//					Intent intent = new Intent(CreateTaskActivity.this, CalenderActivity.class);
//					Bundle b = new Bundle();
//					b.putString("action", "createTask");
//					b.putString("targateDate", targetDate);
//					b.putString("reminderTime", reminderTime);
//					intent.putExtras(b);
//					startActivityForResult(intent, 2);
				} else {
					DateTimeUtil dateTimeUtil = new DateTimeUtil(CreateTaskActivity.this, mOnDateSetListener);
					dateTimeUtil.showDatePicker();
					
//					Intent intent = new Intent(CreateTaskActivity.this, CalenderActivity.class);
//					Bundle b = new Bundle();
//					b.putString("action", "reminder");
//					b.putString("targateDate", targetDate);
//					b.putString("reminderTime", reminderTime);
//					b.putString("TaskId", taskId);
//					intent.putExtras(b);
//					startActivityForResult(intent, 2);
				}
			}
			else if(viewId == R.id.AlarmButton){
				Intent intent = new Intent(CreateTaskActivity.this, SnoozActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("alarm_date_Time", getAlarmDateTimeForPickers());
				bundle.putString("schedule_task_date_Time", getScheduleTaskDateTimeForPickers());
				intent.putExtras(bundle);
				startActivityForResult(intent, 3);
				overridePendingTransition(R.anim.slide_in_from_bottom, 0);
			}
			if (R.id.contacts_add == viewId || viewId == R.id.AssignButton) {
				Intent intent = new Intent(CreateTaskActivity.this,CustomeContactMultiActivity.class);
				startActivityForResult(intent, 1);
				overridePendingTransition(R.anim.slide_in_from_bottom, 0);
			}
			if (R.id.addtaskbtnlinear == viewId) {
				String taskDescription = mAutoCompleteView.getText().toString();
				
				for(String assignee : assigneeNames){
					taskDescription = taskDescription.replaceAll(assignee, "");
				}
				taskDescription = taskDescription.trim();
				
				
				if (taskDescription != null && taskDescription.trim().length() > 0) {
					AddTaskBtnLinear.setClickable(false);
					AddTaskBtnLinear.setEnabled(false);
					ApplicationConstant.createTask_Flag_run_handle = 1;
					new AsyncCreateTask().execute();
				}
				else {
					Toast.makeText( context, getResources().getString( R.string.blank_task_desc_toast), Toast.LENGTH_LONG).show();
				}
			} 
		}
	};
	
	private String getScheduleTaskDateTimeForPickers(){
		String scheduleDate = null;
		if(mDeadLineLayout.getVisibility() == View.VISIBLE){
			scheduleDate = deadlineDateTxt + " 00:00:00";
		}
		return scheduleDate;
	}
	
	private String getAlarmDateTimeForPickers(){
		String alarmDateTime = null;
		if(mAlarmLayout.getVisibility() == View.VISIBLE){
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				Date parsed = dateFormat.parse(mAlarmTextView.getText().toString());
				dateFormat.applyPattern("dd/MM/yyyy HH:mm");
				alarmDateTime = dateFormat.format(parsed);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return alarmDateTime;
	}

	private DateTimeUtil.OnDateSetListener mOnDateSetListener = new DateTimeUtil.OnDateSetListener() {
		@Override
		public void onDateSet(int day, int month, int year) {
			deadlineDateTxt = day + "/" + month + "/" + year;
			
			String deadLineDate = ApplicationUtil.getDateTime11(deadlineDateTxt);
			mDeadLineTextView.setText(deadLineDate);
			mDeadLineLayout.setVisibility(View.VISIBLE);
			
			AppUtility.showKeyboard(CreateTaskActivity.this);
		}
	};
	
	/**
	 * checked change listener for deadline check
	 */

	private OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			int viewId = buttonView.getId();
			if (R.id.deadlinecheck == viewId) {
				if (isChecked) {
					reminderIcon.setEnabled(true);
					reminderIcon.setClickable(true);
					mDeadlineDate.setTextColor(getResources().getColor( R.color.grey));
					mDeadlineDate.setOnClickListener(mClickListener);
					mDeadlineTime.setTextColor(getResources().getColor( R.color.grey));
					mDeadlineTime.setOnClickListener(mClickListener);
					reminderIcon.setEnabled(true);
					reminderIcon.setClickable(true);
					reminderSpinner.setEnabled(true);
					reminderSpinner.setClickable(true);
					reminderIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.alarm_blue));

					try {
						if(reminderSpinner.getSelectedView()!=null){
							((TextView)reminderSpinner.getSelectedView()).setTextColor(getResources().getColor(R.color.grey));
						}
					} catch (NotFoundException e) {
					}
				} else {
					reminderSpinner.setEnabled(false);
					reminderSpinner.setClickable(false);
					mDeadlineDate.setTextColor(getResources().getColor(
							R.color.light_gray_date));
					mDeadlineDate.setOnClickListener(null);
					mDeadlineTime.setTextColor(getResources().getColor(R.color.light_gray_date));
					mDeadlineTime.setOnClickListener(null);
					reminderIcon.setEnabled(false);
					reminderIcon.setClickable(false);
					reminderIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.alarm));
					try {
						if(reminderSpinner.getSelectedView()!=null){
							((TextView)reminderSpinner.getSelectedView()).setTextColor(getResources().getColor(R.color.light_gray_date));
						}
					} catch (NotFoundException e) {
					}
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

		switch (reqCode) {
		case 1:
			if (resultCode == Activity.RESULT_OK) {
				String data_number = data.getStringExtra("changedAssigneeNum");
				String[] numbers = data_number.split(",");
				String data_name = data.getStringExtra("changedAssigneeName");
				String[] names = data_name.split(",");
				ArrayList<String> tempList1 = new ArrayList<String>();

				for (int j = 0; j < numbers.length; j++) {
					String number_print = numbers[j];
					if(assigneeNumbers.contains(number_print)) {
					}
					else {
						if(assigneeNumbers.size()>4) {
							Toast.makeText(getApplicationContext(), getString(R.string.contact_exceed_toast), Toast.LENGTH_LONG).show();
						}
						else {
							assigneeNumbers.add(number_print);
							String name_str = names[j];
							assigneeNames.add(name_str);
						}
					}
				}
				//Display assignee on AutoCompleteTextView
				updateAutoCompleteViewText();
			}

		case 2:
			if (resultCode == 123) {
				if (data != null && data.getExtras() != null) {
					deadlineDateTxt = data.getStringExtra("deadlineDateVal");
					System.out.println("deadlineDateTxt" + deadlineDateTxt);
					if ((ApplicationUtil.parseDate(ApplicationUtil .getCurrentDate())).before(ApplicationUtil .parseDate(deadlineDateTxt)) == true) {
						deadlineTimeTxt = data .getStringExtra("deadlineTimeVal");
						String	reminderLabelValue = data .getStringExtra("reminderValueSelected");
						mDeadlineTime.setText("" + deadlineTimeTxt);
						reminderIcon.setEnabled(true);
						reminderIcon.setClickable(true);
						alarTxtView.setText(reminderLabelValue);
					} else {
						String	reminderLabelValue = data .getStringExtra("reminderValueSelected");
						alarTxtView.setText(reminderLabelValue);
						DateFormat dateFormatter = new SimpleDateFormat("HH:mm");
						dateFormatter.setLenient(false);
						Date today = new Date();
						String s = dateFormatter.format(today);

						System.out.println(Integer.parseInt(data .getStringExtra("deadlineTimeVal").replace(":", "")) <= Integer.parseInt(s.replace(":", "")));
						if (Integer.parseInt(data.getStringExtra( "deadlineTimeVal").replace(":", "")) <= Integer .parseInt(s.replace(":", ""))) {
							Toast.makeText(context, "Time is incorrect", Toast.LENGTH_SHORT).show();
							deadlineTimeTxt = s;
							mDeadlineTime.setText("" + s);
						} else {
							deadlineTimeTxt = data .getStringExtra("deadlineTimeVal");
							mDeadlineTime.setText("" + deadlineTimeTxt);
						}
					}

					reminderTime = reminderDateTime = data .getStringExtra("reminderDateVal");
					targetDate = data.getStringExtra("targetDateVal");
					
					//Visibility of alarm if set.//Added by vishesh
					String reminderTime = data.getStringExtra("reminderAlarmTime");
					mAlarmReminderTime = reminderTime;
					if(reminderTime != null && reminderTime.isEmpty() == false){
						mAlarmTextView.setText(reminderTime);
						mAlarmLayout.setVisibility(View.VISIBLE);
					}
					
					//If user will select ok then date will be selected so setting DealLine layout visibility on.
					if(deadlineDateTxt != null && deadlineDateTxt.isEmpty() == false){
						mDeadLineTextView.setText(deadlineDateTxt);
						mDeadLineLayout.setVisibility(View.VISIBLE);
					}
				}
			}
			break;
		case 3://SnoozActivity
			if(resultCode == Activity.RESULT_OK && data != null){
				//Visibility of alarm if set.//Added by vishesh
				mAlarmReminderTime = data.getStringExtra("reminderAlarmTime");
				if(mAlarmReminderTime != null && mAlarmReminderTime.isEmpty() == false){
					
					if(mDeadLineLayout.getVisibility() == View.VISIBLE){
						String deadLineDate = ApplicationUtil.getDateTime11(deadlineDateTxt);
						String alarmDate = ApplicationUtil.getDateTime12(mAlarmReminderTime);
						
						if(deadLineDate.equals(alarmDate)){
							mAlarmTextView.setText(ApplicationUtil.getDateTime13(mAlarmReminderTime));
						}
					} else {
						mAlarmTextView.setText(ApplicationUtil.getDateTime14(mAlarmReminderTime));
					}
					
					mAlarmLayout.setVisibility(View.VISIBLE);
				}
			}
			break;
		}
	}

	@Override
	public void onBackPressed() {
		Intent data = new Intent();
		data.putExtra("taskcreated", false);
		setResult(RESULT_OK, data);
		finish();
		overridePendingTransition(0, R.anim.slide_out_to_bottom);
//		super.onBackPressed();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int nameSize = savedInstanceState.getInt("sizeNameList");
		for (int i = 0; i < nameSize; i++) {
			assigneeNames .addAll(savedInstanceState.getStringArrayList(i + "Name"));
		}
		int numberSize = savedInstanceState.getInt("sizeNumberList");
		for (int i = 0; i < numberSize; i++) {
			assigneeNumbers.addAll(savedInstanceState.getStringArrayList(i + "Number"));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("sizeNameList", assigneeNames.size());
		for (int i = 0; i < assigneeNames.size(); i++) {
			outState.putString(i + "Name", assigneeNames.get(i));
		}
		outState.putInt("sizeNumberList", assigneeNumbers.size());
		for (int i = 0; i < assigneeNumbers.size(); i++) {
			outState.putString(i + "Number", assigneeNumbers.get(i));
		}
	}

	/**
	 * method to create assignee list view after selecting contact from phone
	 * book
	 * 
	 * @param name
	 * @param tag
	 */
	private void createAssigneeListView() {
		row = new LinearLayout(context);
		spaceInRow = 2;
		assigneeName.removeAllViews();

		ImageView assign_icon = (ImageView) findViewById(R.id.asigneeicon);
		TextView assign_text = (TextView) findViewById(R.id.assign_text);

		if (assigneeNames.size() == 0) {
			assign_icon.setVisibility(View.VISIBLE);
			assign_text.setVisibility(View.VISIBLE);
		}

		for (int i = 0; i < assigneeNames.size(); i++) {
			System.out.println("Here is size and values ...  " + assigneeNames.size());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(3, 3, 3, 3);
			final TextView newAssignee = new TextView(context);
			newAssignee.setGravity(Gravity.CENTER);
			newAssignee.setLayoutParams(params);
			newAssignee.setPadding(10, 10, 10, 10);
			newAssignee.setText(assigneeNames.get(i));
			newAssignee.setTextColor(getResources().getColor(R.color.black));
			newAssignee.setBackgroundDrawable(getResources().getDrawable( R.drawable.asignee_name));

			assign_icon.setVisibility(View.GONE);
			assign_text.setVisibility(View.GONE);

			newAssignee.setTag((assigneeNumbers.get(i)));

			if (spaceInRow == 2) {
				row = new LinearLayout(context);
				row.setOrientation(LinearLayout.HORIZONTAL);
				LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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
					String msg = "Do you want to delete \"" + newAssignee.getText() + "\" From List?";
					long temp_val = 0;
					for (int j = 0; j < assigneeNames.size(); j++) {
						System.out.println("Total number is .... " + assigneeNames.size() + " ... " + assigneeNames.get(j) + ".... " + newAssignee.getText().toString() );
						if(assigneeNames.get(j).equalsIgnoreCase(newAssignee.getText().toString())) {
							String numberString = assigneeNumbers.get(j).trim();
							temp_val = numberString.length() > 0 ? Long.parseLong(numberString) : 0;
						}
					}
					displayConfirmationDialog(msg, temp_val);
					return false;
				}
			});
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	/**
	 * This method display Confirmation pop up
	 * 
	 * @param msg
	 * @param view
	 */
	private void displayConfirmationDialog(String msg, final long tag) {
		final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage(msg)
		.setCancelable(false)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				for (int j = 0; j < assigneeNumbers.size(); j++) {
					if (assigneeNumbers.get(j).trim().equals(""+tag)) {
						assigneeNumbers.remove(j);
						assigneeNames.remove(j);
					}
				}
				createAssigneeListView();
				dialog.dismiss();
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private String retrieveContactName() {
		String contactName = null;

		// querying contact data store
		Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);
		if (cursor.moveToFirst()) {
			// DISPLAY_NAME = The display name for the contact.
			// HAS_PHONE_NUMBER = An indicator of whether this contact has at
			// least one phone number.

			contactName = cursor.getString(cursor .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		}

		cursor.close();
		Log.d("Contact", "Contact Name: " + contactName);
		return contactName;
	}



	public class AsyncCreateTask extends AsyncTask<Void, Void, ResponseDto> {
		private ProgressDialog pDialog = null;

		@SuppressWarnings("static-access")
		@Override
		protected void onPreExecute() {
			pDialog = new ProgressDialog(CreateTaskActivity.this).show(context, "", "Please Wait...");
		}

		@Override
		protected ResponseDto doInBackground(Void... params) {
			return addTask();
		}

		@Override
		protected void onPostExecute(ResponseDto result) {
			if (pDialog != null) {
				pDialog.dismiss();
				if("401".equalsIgnoreCase(result.getStatus())){
					final DialogCallback callback=new DialogCallback() {
						@Override
						public Object execuet(Dialog dialog) {
							finish();
							return null;
						}
					};
					CommonUtilsUi.getCustomeDialog(CreateTaskActivity.this, result.getMessageDescription(),callback);
					return ;
				}
			}
			ApplicationConstant.createTask_Flag_run_handle = 0;

			Intent data = new Intent();
			data.putExtra("taskcreated", true);
			setResult(RESULT_OK, data);
			finish();
			overridePendingTransition(0, R.anim.slide_out_to_bottom);
		}
	};

	public class AsyncUpdateTask extends AsyncTask<UpdateTask, Void, Void> {
		private ProgressDialog pDialog = null;

		@Override
		protected Void doInBackground(UpdateTask... params) {
			UpdateTask task_update = params[0];

			SyncModule syncObj = new SyncModule(context);
			syncObj.UpdateTask(task_update);
			syncObj = null;
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			ApplicationConstant.createTask_Flag_run_handle = 0;
			if (pDialog != null) {
				pDialog.dismiss();
			}
			Intent data = new Intent();
			data.putExtra("taskcreated", true);
			setResult(RESULT_OK, data);
			finish();
			Toast.makeText( context, getResources() .getString(R.string.task_updated_toast_synced), Toast.LENGTH_LONG).show();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			pDialog = new ProgressDialog(CreateTaskActivity.this).show(context, "", "Please Wait...");
			super.onPreExecute();
		}
	};

	private void show(){
		final Dialog dialog = new Dialog(this,android.R.style.Theme_Translucent_NoTitleBar);
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.gravity = (Gravity.CENTER);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setGravity(Gravity.BOTTOM);
		dialog.setContentView(R.layout.calender_screen_new);
		dialog.getWindow().setAttributes(lp);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		LinearLayout linearOk=(LinearLayout) dialog.findViewById(R.id.linearOk);
		LinearLayout linearCancel=(LinearLayout) dialog.findViewById(R.id.linearCancel);
		wlp.gravity = Gravity.BOTTOM;
		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(wlp);
		final WheelView hours = (WheelView) dialog.findViewById(R.id.hour);
		NumericWheelAdapter hourAdapter = new NumericWheelAdapter(this, 0, 23);
		hourAdapter.setItemResource(R.layout.wheel_text_item);
		hourAdapter.setItemTextResource(R.id.text);
		hours.setViewAdapter(hourAdapter);
		hours.setCyclic(true);
		final WheelView mins = (WheelView) dialog.findViewById(R.id.mins);
		NumericWheelAdapter minAdapter = new NumericWheelAdapter(this, 0, 59);
		minAdapter.setItemResource(R.layout.wheel_text_item);
		minAdapter.setItemTextResource(R.id.text);
		mins.setViewAdapter(minAdapter);
		mins.setCyclic(true);
		int curIndex=0;

		Calendar calendar = Calendar.getInstance(Locale.US);
		calendar.set(Calendar.YEAR, 2014);
		calendar.set(2014, 0,1);
		hours.setCurrentItem(calendar.get(Calendar.HOUR));
		mins.setCurrentItem(calendar.get(Calendar.MINUTE));
		int daysCount=0;
		final WheelView day = (WheelView) dialog.findViewById(R.id.day);
		ArrayList<String> dates=new ArrayList<String>();
		for(int i=0;i<=364;i++){
			int day1 = -daysCount / 2 + i;
			Calendar newCalendar = (Calendar) calendar.clone();
			newCalendar.roll(Calendar.DAY_OF_YEAR, day1);
			dates.add(getDate(newCalendar.getTimeInMillis()));
			if(getDate(new Date().getTime()).equalsIgnoreCase(getDate(newCalendar.getTimeInMillis()))){
				curIndex=i;
			}
		}

		DayArrayAdapter arrayAdapter=	new DayArrayAdapter(this, dates);
		day.setViewAdapter(arrayAdapter);
		day.setCyclic(true);
		day.setCurrentItem(curIndex);
		String	currentItem=	day.getItemVale();
		System.out.println(currentItem);
		SimpleDateFormat dateFormat=new SimpleDateFormat("dd,MMM,yyyy");
		try {
			finalCalendar.setTime(dateFormat.parse(currentItem));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		finalh=calendar.get(Calendar.HOUR);
		finalM=calendar.get(Calendar.MINUTE);
		dialog.show();

		linearOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finalCalendar.add(Calendar.HOUR, finalh);
				finalCalendar.add(Calendar.MINUTE, finalM);
				Date date=finalCalendar.getTime();
				System.out.println(date);
				dialog.dismiss();
			}
		});

		linearCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		day.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				try {
					String	currentItem=	wheel.getItemVale();
					System.out.println(currentItem);
					SimpleDateFormat dateFormat=new SimpleDateFormat("dd,MMM,yyyy");
					finalCalendar.setTime(dateFormat.parse(currentItem));
					System.out.println(finalCalendar.toString());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		});

		hours.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				finalh=newValue;
			}
		});

		mins.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				finalM=newValue;
			}
		});
	}
	
	Calendar finalCalendar=Calendar.getInstance();
	String finalDate="";
	int finalh=0;
	int finalM=0;

	private String getDate(long time){
		SimpleDateFormat dateFormat=new SimpleDateFormat("dd,MMM,yyyy");
		return dateFormat.format(time);
	}
}
