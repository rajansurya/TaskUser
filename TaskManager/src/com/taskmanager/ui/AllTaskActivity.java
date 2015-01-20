package com.taskmanager.ui;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.Profile;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.google.android.gcm.GCMRegistrar;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.taskmanager.adapter.DisplayCloseTaskAdapter;
import com.taskmanager.adapter.DisplayTaskListAdapter;
import com.taskmanager.adapter.FilterAllTaskListAdapter;
import com.taskmanager.adapter.HashTaskDropDownAdapter;
import com.taskmanager.adapter.edit.EditCustomAutoCompleteView;
import com.taskmanager.adapter.edit.EditTextChangedListener;
import com.taskmanager.app.MyApp;
import com.taskmanager.app.R;
import com.taskmanager.app.common.AppUtility;
import com.taskmanager.app.common.CustomTypeFace;
import com.taskmanager.app.common.DownloadImageTask;
import com.taskmanager.app.common.NonProfilePhotoPlaceHolder;
import com.taskmanager.background.SyncModule;
import com.taskmanager.background.TaskSyncUtils;
import com.taskmanager.bean.ChangeAssigneeDto;
import com.taskmanager.bean.ChangeAssigneeResponseDto;
import com.taskmanager.bean.NotationDto;
import com.taskmanager.database.DBAdapter;
import com.taskmanager.dto.ContactWiseDto;
import com.taskmanager.dto.TaskInfoEntity;
import com.taskmanager.entites.TaskEntity;
import com.taskmanager.util.ApplicationConstant;
import com.taskmanager.util.ApplicationUtil;
import com.taskmanager.util.CommonUtil;
import com.taskmanager.widgets.CustomImageButton;
import com.taskmanager.widgets.DialogMenu;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirections;

/**
 * Activity to render task list screen and all their actions such as
 * filtering,sorting etc.
 * 
 * @author mayankb
 * 
 */
public class AllTaskActivity extends FragmentActivity {
	private TextView mCountTextVeiw;
	public String headerTitle;
	private ViewGroup mAllTabLayout;
	private ViewGroup mSelfTabLayout;
	private ViewGroup mInTabLayout;
	private ViewGroup mOutTabLayout;
	private ViewGroup mFavroiteTabLayout;
	private ViewGroup mFireTabLayout;
	private static int titleCount;
	private boolean isSelfTabSelected;
	private boolean isInTabSelected;
	private boolean isOutTabSelected;
	private boolean isFavoriteTabSelected;
	private boolean isFireTabSelected;
	
	private ViewGroup mHeaderLayout;
	
	public EditCustomAutoCompleteView messageEdit;
	private TextView mAllTabTextView;
	private TextView mSelfTabTextView;
	private TextView mInTabTextView;
	private TextView mOutTabTextView;

	public TextView mFavoriteTabTextView;
	public TextView mFireTabTextView;

	private ImageView mFavoriteTabImageView;
	private ImageView mFireTabImageView;

	private Timer timer;
	private String pulldownDataState = "OPEN";
	public static boolean isTaskOrMsgCreationInitated;
	public static boolean isSyncTreadRunning;
	boolean newMsgOrtask;
	public AllTaskActivity context;
	private ArrayList<TaskInfoEntity> openTaskList = new ArrayList<TaskInfoEntity>();

	public String sortVal = "OPEN";
	public boolean isLater = true;
	public ArrayAdapter<String> editAdapter;
	public String[] editAutoCompletiTem;
	
	private CustomImageButton mCreateTaskButton;
	
	private boolean isTaskOpen = false;
	private boolean istaskOpenClose = false;
	// private Dialog dialog;
	private TextView moreBtn;
	private CheckBox mFocusNowHeaderCheckBox;
	
	private ArrayList<ArrayList<String>> listDBElement;
	private boolean junkFlag = false;
	private int totalUnreadCount = 0;
	private ArrayList<String> unreadTaskID = new ArrayList<String>();
	private TextView totalUnread;
	private static boolean isPullRefreshInProgress;
	private static String noOfFavourite = "0";
	private static String noOfFire = "0";
	private int favCount = 0;
	private int fireCount = 0;
	private int scrollTask = 0;
	private int rowHeight = 0;
	private LinearLayout openTaskInfoLinear;
	private LinearLayout openTaskDescBtnsLinear;
	private LinearLayout openTaskIcon;
	private ImageView openTaskDivider;
	private ImageView openTaskArrow;
	private LinearLayout openTaskcheck;

	private String taskType = "All";
	private ProgressDialog progressDialog;

	private ViewGroup mMainLayout;
	private ViewGroup noTaskMsgLayout;
	private ImageView mNoTaskImageView;
	private TextView mNoTaskTitleTextView;
	private TextView mNoTaskDescriptionTextView;
	
	private Handler progressHandler;
	private ScrollView TaskList;
	private String sortBy = "UPDATION_DATE_TIME";
	private String contactSummery = "not";

	public String mSideMenuSelectedLabels = "";

	private boolean favViewFlag = false;
	private boolean fireViewFlag = false;
	private String filterType;

	private String changedAssigneeName = "";
	private String changedAssigneeNum = "";
	public String globalTaskId;
	public String globalAssigneeNum = "";
	public String globalAssigneeName = "";
	private boolean flagcompleteCheckBox = false;
	private ProgressDialog progressUnread;
	private boolean favouriteFlagSelf = false;
	private boolean favouriteFlagReceived = false;
	private boolean favouriteFlagAssigned = false;
	public HashMap<String, String> taskCheckBoxMap = null;
	private String[] changeAssigneeNumbers;
	private String changeAssigneeNum;
	private boolean flag_Close_Message = false;
	private ProgressDialog progressDialogReceived = null;
	private ProgressBar progressBarMessage = null;
	private LinearLayout lineartotalUnreadButton;

	private LinearLayout linearUnreadTotal = null;
	private int favCountGlobal;
	private int firecountGlobal;
	public TextView txtAssigorName = null;
	
	private PullToRefreshListView listview = null;
//	private RefreshableListView list_view = null;
	
	private FilterAllTaskListAdapter mFilterAllTaskListAdapter;
	private ViewGroup mHeaderActionLayout;
	private ViewGroup mHeaderSearchLayout;
	private ImageView mHeaderSearchButton;
	private TextView mHeaderSearchBackButton;
	private EditText mHeaderSearchEditText;
	
	// private WeakHashMap viewMap=new WeakHashMap() ;
	private static TaskSyncUtils syncUtils;
//	private ListView actualListView;

	private ViewGroup mTabsLayout;
	private Stack<View> mBackNavigationStack;
	private Stack<String> mNavigationHeaderTitleStack;

	private enum BackNavigationStackAction {
		ADD, REMOVE, CLEAR
	}

	private DisplayTaskListAdapter mDisplayTaskListAdapter;
	private DisplayCloseTaskAdapter mDisplayCloseTaskAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_task_screen);
		
		mBackNavigationStack = new Stack<View>();
		mNavigationHeaderTitleStack = new Stack<String>();

		context = this;
		isContactWise = false;
		if (syncUtils == null) {
			System.out.println("create New syncUtils");
			syncUtils = TaskSyncUtils.getInstance(context);
		}
		// System.out.println("On create called");
		pulldownDataState = "OPEN";
		Runtime rt = Runtime.getRuntime();
		long maxMemory = rt.maxMemory();
		// System.out.println("onCreate maxMemory:" + maxMemory);
		taskCheckBoxMap = new HashMap<String, String>();
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		int memoryClass = am.getMemoryClass();
		// System.out.println("onCreate memoryClass:" + memoryClass);

		progressHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				// System.out.println("progress handler called AllTaskActivity");
				if (progressDialog != null)
					progressDialog.dismiss();
			}
		};
		isLater=false;
		mMainLayout = (ViewGroup)findViewById(R.id.MainLayout);
		
		mTabsLayout = (ViewGroup) findViewById(R.id.TabsLayout);
		mCountTextVeiw = (TextView) findViewById(R.id.CountTextVeiw);
		mCountTextVeiw.setTypeface(CustomTypeFace
				.getRobotoMedium(AllTaskActivity.this));
		mAllTabLayout = (ViewGroup) findViewById(R.id.allTabLayout);
		mSelfTabLayout = (ViewGroup) findViewById(R.id.selfTabLayout);
		mInTabLayout = (ViewGroup) findViewById(R.id.inTabLayout);
		mOutTabLayout = (ViewGroup) findViewById(R.id.outTabLayout);
		mFavroiteTabLayout = (ViewGroup) findViewById(R.id.faviconTabLayout);
		mFireTabLayout = (ViewGroup) findViewById(R.id.fireTabLayout);
		
		mHeaderLayout = (ViewGroup)findViewById(R.id.HeaderLayout);
		mAllTabTextView = (TextView) findViewById(R.id.AllTabTextView);
		mSelfTabTextView = (TextView) findViewById(R.id.SelfTabTextView);
		mInTabTextView = (TextView) findViewById(R.id.InTabTextView);
		mOutTabTextView = (TextView) findViewById(R.id.OutTabTextView);
		mAllTabTextView.setTypeface(CustomTypeFace
				.getRobotoMedium(AllTaskActivity.this));
		mSelfTabTextView.setTypeface(CustomTypeFace
				.getRobotoMedium(AllTaskActivity.this));
		mInTabTextView.setTypeface(CustomTypeFace
				.getRobotoMedium(AllTaskActivity.this));
		mOutTabTextView.setTypeface(CustomTypeFace
				.getRobotoMedium(AllTaskActivity.this));

		mFavoriteTabImageView = (ImageView) findViewById(R.id.favoriteTabImageView);
		mFavoriteTabTextView = (TextView) findViewById(R.id.favoriteTabTextView);
		mFavoriteTabTextView.setTypeface(CustomTypeFace
				.getRobotoLight(AllTaskActivity.this));
		mFireTabImageView = (ImageView) findViewById(R.id.fireTabImageView);
		mFireTabTextView = (TextView) findViewById(R.id.fireTabTextView);
		mFireTabTextView.setTypeface(CustomTypeFace
				.getRobotoLight(AllTaskActivity.this));

		totalUnread = (TextView) findViewById(R.id.totalUnread);

		lineartotalUnreadButton = (LinearLayout) findViewById(R.id.totalUnreadLinear);
		linearUnreadTotal = (LinearLayout) findViewById(R.id.totalUnreadLinear);
		lineartotalUnreadButton.setVisibility(View.INVISIBLE);
		mCreateTaskButton = (CustomImageButton) findViewById(R.id.CreateTaskButton);
		moreBtn = (TextView) findViewById(R.id.moreiconlinear);
		mFocusNowHeaderCheckBox = (CheckBox)findViewById(R.id.FocusNowCheckBox);
		noTaskMsgLayout = (ViewGroup) findViewById(R.id.notaskMsgLayout);
		mNoTaskImageView = (ImageView)findViewById(R.id.NoTaskImgeView);
		mNoTaskTitleTextView = (TextView)findViewById(R.id.NoTastTitleTextView);
		mNoTaskDescriptionTextView = (TextView)findViewById(R.id.NoTastDescriptionTextView);
		
		TaskList = (ScrollView) findViewById(R.id.tasklistscroll);
		listview = (PullToRefreshListView) findViewById(R.id.tasklistviewscroll);
//		list_view = (RefreshableListView) findViewById(R.id.tasklistviewscroll);
		// list_view.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
//		list_view.setShowIndicator(false);

		// Default settings for tab content load.
		onTabChanged(null);

		/**
		 * Appflyer Methods
		 */

		try {
			AppsFlyerLib.sendTracking(getApplicationContext());
		} catch (Exception e) {
		}

		if (ApplicationUtil.getPreference(
				ApplicationConstant.LAST_UPDATED_DATE, context) == null) {
			new AsyncTask<Void, Void, Void>() {
				private ProgressDialog dialog;

				@Override
				protected Void doInBackground(Void... params) {
					try {
						TaskSyncUtils.getInstance(context).fullSync(
								ApplicationUtil.getPreference(
										ApplicationConstant.regMobNo, context),
								context, null);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					dialog.dismiss();
					callAsynchronousTask();
					try {
						NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
						nMgr.cancelAll();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					new Asyntask().execute();
					super.onPostExecute(result);
				}

				@Override
				protected void onPreExecute() {
					dialog = new ProgressDialog(AllTaskActivity.this);
					dialog.setMessage("Fetching your tasks");
					dialog.setCancelable(false);
					dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					dialog.show();

				}
			}.execute();
		} else {
			String status = ApplicationUtil.getPreference("status", this);
			callAsynchronousTask();
			try {
				NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				nMgr.cancelAll();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			if ("CLOSE".equalsIgnoreCase(status)) {
				mTabsLayout.setVisibility(View.GONE);
				mFocusNowHeaderCheckBox.setVisibility(View.GONE);
				mHeaderSearchBackButton.setVisibility(View.GONE);
				mHeaderLayout.setBackgroundResource(R.drawable.navigation_bar_done);
				updatedBackNavigativeStack(null, null, BackNavigationStackAction.CLEAR);
				
				AsyntaskClose asyntaskClose = new AsyntaskClose("CLOSED");
				asyntaskClose.setTitle(TaskCategory.TASK_DONE);
				asyntaskClose.execute();

			} else if ("Junk".equalsIgnoreCase(status)) {
				junkFlag = true;
				LoadJunkList loadJunkList = new LoadJunkList("JUNK");
				loadJunkList.setTitle(TaskCategory.TASK_SPAM);
				loadJunkList.execute();

			} else {
				new Asyntask().execute();
			}
		}
		ApplicationUtil.savePreference("status", null, context);

		linearUnreadTotal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isPullRefreshInProgress) {
					return;
				}
				isLater=false;
				sortVal = getResources().getString(R.string.open_task_status);
				junkFlag = false;
				ApplicationConstant.message_Task_Flag_run_handle = 1;
				ApplicationConstant.createTask_Flag_run_handle = 1;
				new AsyncTotalOpen().execute();
			}

		});

		mAllTabLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isLater=false;
				try {
					NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					nMgr.cancelAll();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				clickOnAllTabButton(v);
				mSideMenuSelectedLabels =  "AllTab";
				sortVal="Open";
				
//				startActivity(new Intent(AllTaskActivity.this, TasksTabActivity.class));
			}
		});

		mSelfTabLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onTabChanged(v);

				if (sortVal.equalsIgnoreCase("CLOSE") && !junkFlag) {
					AsyntaskClose asyntaskClose = new AsyntaskClose();
					asyntaskClose.setTitle(TaskCategory.TASK_SELF);
					asyntaskClose.execute();
				} else {
					if (sortVal.equalsIgnoreCase("OPEN") && !junkFlag) {
						Asyntask asyntask = new Asyntask();
						asyntask.setTitle(TaskCategory.TASK_SELF);
						asyntask.execute();
					} else if (junkFlag) {
						LoadJunkList loadJunkList = new LoadJunkList();
						loadJunkList.setTitle(TaskCategory.TASK_SELF);
						loadJunkList.execute();
					}
				}
				isLater=false;
				mSideMenuSelectedLabels =  "SelfTab";
			}
		});

		mInTabLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onTabChanged(v);
				isLater=false;
				if (sortVal.equalsIgnoreCase("CLOSE") && !junkFlag) {
					AsyntaskClose asyntaskClose = new AsyntaskClose();
					asyntaskClose.setTitle(TaskCategory.TASK_RECEIVED);
					asyntaskClose.execute();
				} else {
					if (sortVal.equalsIgnoreCase("OPEN") && !junkFlag) {
						Asyntask asyntask = new Asyntask();
						asyntask.setTitle(TaskCategory.TASK_RECEIVED);
						asyntask.execute();
					} else if (junkFlag) {
						LoadJunkList loadJunkList = new LoadJunkList();
						loadJunkList.setTitle(TaskCategory.TASK_RECEIVED);
						loadJunkList.execute();
					}
				}
			
				mSideMenuSelectedLabels =  "InTab";
			}
		});

		mOutTabLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isLater=false;
				onTabChanged(v);
				if (sortVal.equalsIgnoreCase("CLOSE") && !junkFlag) {
					AsyntaskClose asyntaskClose = new AsyntaskClose();
					asyntaskClose.setTitle(TaskCategory.TASK_ASSIGNED);
					asyntaskClose.execute();
				} else {
					if (sortVal.equalsIgnoreCase("OPEN") && !junkFlag) {
						Asyntask asyntask = new Asyntask();
						asyntask.setTitle(TaskCategory.TASK_ASSIGNED);
						asyntask.execute();
					} else if (junkFlag) {
						LoadJunkList loadJunkList = new LoadJunkList();
						loadJunkList.setTitle(TaskCategory.TASK_ASSIGNED);
						loadJunkList.execute();
					}
				}
				
				mSideMenuSelectedLabels =  "OutTab";
			}
		});

		// new task click
		mCreateTaskButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(AllTaskActivity.this, CreateTaskActivity.class);
				Bundle b = new Bundle();
				b.putString("sortingVal", sortVal);
				b.putString("action", "createTask");
				intent.putExtras(b);
				startActivityForResult(intent, 2);
				overridePendingTransition(R.anim.slide_in_from_bottom, 0);
			}
		});

		//Header Focus now button
		mFocusNowHeaderCheckBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isPullRefreshInProgress) {
					return;
				}
				isLater=true;
				if(mFocusNowHeaderCheckBox.isChecked()){
					mHeaderLayout.setBackgroundResource(R.drawable.navigation_bar);
					updatedBackNavigativeStack(null, null, BackNavigationStackAction.CLEAR);
					mTabsLayout.setVisibility(View.GONE);
					mFocusNowHeaderCheckBox.setChecked(true);
					
					isSelfTabSelected = true;
					isInTabSelected = true;
					isOutTabSelected = true;
					isFavoriteTabSelected = true;
					isFireTabSelected = false;
					
					Asyntask asyntask = new Asyntask();
					asyntask.setTitle(TaskCategory.TASK_FOCUS_NOW);
					asyntask.execute();
					mSideMenuSelectedLabels =  "FocusTab";
				} else {
					mAllTabLayout.performClick();
				}
			}
		});
		
		// side menu click
		moreBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mBackNavigationStack.size() > STACK_MIN_COUNT) {
					updatedBackNavigativeStack(null, null, BackNavigationStackAction.REMOVE);
					return;
				}
				showCustomDialog();
			}
		});

		mFavroiteTabLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onTabChanged(v);
				isLater=true;
				Asyntask asyntask = new Asyntask();
				asyntask.setTitle(TaskCategory.TASK_FOCUS_NOW);
				asyntask.execute();
				mSideMenuSelectedLabels =  "FocusTab";
			}
		});

		mFireTabLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onTabChanged(v);
				isLater=true;
				Asyntask asyntask = new Asyntask();
				asyntask.setTitle(TaskCategory.TASK_URGENT);
				asyntask.execute();
				mSideMenuSelectedLabels =  "FireTab";
			}
		});

		ArrayList<ArrayList<String>> userTable = ApplicationUtil
				.getInstance()
				.uploadListFromDB(
						"User",
						new String[] { "Status,XToken,Mobile_Number,Number_Validation,First_Name" },
						null, context);
		if (userTable != null && !userTable.isEmpty()) {
			ApplicationConstant.ApplicantName = userTable.get(0).get(4);
		}

		getLoaderManager().initLoader(100, null, mLoaderCallbacks);
		
		//Headers functionality
		mHeaderActionLayout = (ViewGroup)findViewById(R.id.HeaderActionLayout);
		mHeaderSearchLayout = (ViewGroup)findViewById(R.id.HeaderSearchLayout);
		mHeaderSearchButton = (ImageView)findViewById(R.id.HeaderSearchButton);
		mHeaderSearchBackButton = (TextView)findViewById(R.id.HeaderSearchBackButton);
		mHeaderSearchEditText = (EditText)findViewById(R.id.HeaderSearchEditText);
		mHeaderSearchEditText.addTextChangedListener(mTaskFilterTextWatcher);
		
		mHeaderSearchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mHeaderActionLayout.setVisibility(View.GONE);
				mTabsLayout.setVisibility(View.GONE);
				mHeaderSearchLayout.setVisibility(View.VISIBLE);
				clickOnHeaderSearchButton();
			}
		});
		
		mHeaderSearchBackButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AppUtility.hideKeyboard(AllTaskActivity.this);
				mFilterAllTaskListAdapter = null;
				mHeaderSearchEditText.setText("");
				mHeaderActionLayout.setVisibility(View.VISIBLE);
				mHeaderSearchLayout.setVisibility(View.GONE);
				mAllTabLayout.performClick();
			}
		});
	}
	
	private void clickOnHeaderSearchButton(){
		if (isPullRefreshInProgress) {
			return;
		}
		
		mHeaderLayout.setBackgroundResource(R.drawable.navigation_bar_search);
		updatedBackNavigativeStack(null, null, BackNavigationStackAction.CLEAR);
		
		onTabChanged(null);//It will enable the fields for all tasks.
		isContactWise = false;
		sortVal = getResources().getString(R.string.open_task_status);
		junkFlag = false;
		sortBy = "UPDATION_DATE_TIME";
		ApplicationConstant.message_Task_Flag_run_handle = 1;
		ApplicationConstant.createTask_Flag_run_handle = 1;

		TaskForFilteringAsyncTask asyncTotalOpen = new TaskForFilteringAsyncTask();
		asyncTotalOpen.execute();
	}
	
	public void updateUIonSearch(boolean isEmpty){
		if (isEmpty) {
			noTaskMsgLayout.setVisibility(View.VISIBLE);
			listview.setVisibility(View.GONE);

			mMainLayout.setBackgroundColor(Color.parseColor("#646464"));
			mNoTaskImageView.setImageResource(R.drawable.bg_no_task_search);
			mNoTaskTitleTextView.setText("No search records found.");
			mNoTaskDescriptionTextView.setText("Please refine your search if you don't see what you need.");
		} else {
			noTaskMsgLayout.setVisibility(View.GONE);
			listview.setVisibility(View.VISIBLE);
			mMainLayout.setBackgroundColor(Color.parseColor("#00000000"));
		}
	}
	
	private TextWatcher mTaskFilterTextWatcher = new TextWatcher() {
		@Override
		public void afterTextChanged(Editable arg0) {
		}
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}
		@Override
		public void onTextChanged(CharSequence toSearch, int start, int before, int count) {
			if(mFilterAllTaskListAdapter != null){
				mFilterAllTaskListAdapter.getFilter().filter(toSearch);
			}
		}
	};
	
	private OnRefreshListener<ListView> mOnRefreshListener = new OnRefreshListener<ListView>() {
		@Override
		public void onRefresh(PullToRefreshBase<ListView> refreshView) {
			String label = DateUtils.formatDateTime(
					getApplicationContext(), System.currentTimeMillis(),
					DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);

			// Update the LastUpdatedLabel
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
			
			callToRefreshTheListView();
		}
	};
	
	private void callToRefreshTheListView(){
		// Do work to refresh the list here.
		if ("CLOSE".equalsIgnoreCase(pulldownDataState)) {
			new PullDownAsyntaskClose().execute();
		} else if (junkFlag) {
			new PullDownLoadJunkList().execute();
		} else {
			new PullDownAsyntask().execute();
		}
	}
	
	private final int STACK_MIN_COUNT = 1;

	/**
	 * @param view
	 * @param isAdding
	 *            , true for adding the view in backstack, and false to remove
	 *            the last view from stack.
	 */
	private void updatedBackNavigativeStack(View view, String title, BackNavigationStackAction action) {
		if (action == BackNavigationStackAction.ADD) {
			if (mBackNavigationStack.size() > STACK_MIN_COUNT) {
				mBackNavigationStack.peek().setVisibility(View.GONE);
			}
			mNavigationHeaderTitleStack.add(title);
			mBackNavigationStack.add(view);
			// Update the icon
			if (mBackNavigationStack.size() > STACK_MIN_COUNT) {
				moreBtn.setBackgroundResource(R.drawable.summery_back_btn);
			} else {
				moreBtn.setBackgroundResource(R.drawable.icon_menu);
			}
		} else if (action == BackNavigationStackAction.REMOVE) {
			if (mBackNavigationStack.size() > STACK_MIN_COUNT) {
				mBackNavigationStack.pop().setVisibility(View.GONE);
				mBackNavigationStack.peek().setVisibility(View.VISIBLE);

				// Title
				mNavigationHeaderTitleStack.pop();
				setHeaderTitleCount(mNavigationHeaderTitleStack.peek());
				noTaskMsgLayout.setVisibility(View.GONE);
				mMainLayout.setBackgroundColor(Color.parseColor("#00000000"));
			}
			// Update the icon
			if (mBackNavigationStack.size() <= STACK_MIN_COUNT) {
				moreBtn.setBackgroundResource(R.drawable.icon_menu);
			}
		} else if (action == BackNavigationStackAction.CLEAR) {
			mBackNavigationStack.clear();
			moreBtn.setBackgroundResource(R.drawable.icon_menu);
			listview.setVisibility(View.VISIBLE);

			// Title
			mNavigationHeaderTitleStack.clear();
		}
	}

	private LoaderCallbacks<Cursor> mLoaderCallbacks = new LoaderCallbacks<Cursor>() {
		@Override
		public void onLoaderReset(Loader<Cursor> arg0) {

		}

		@Override
		public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
			if (cursor != null && cursor.moveToFirst()) {
				// String url =
				// cursor.getString(cursor.getColumnIndex("photo_thumb_uri"));
				byte[] data = cursor.getBlob(0);
				if (data != null) {
					Bitmap bitmap = BitmapFactory
							.decodeStream(new ByteArrayInputStream(data));
					// moreBtn.setBackgroundDrawable(new
					// BitmapDrawable(getResources(), bitmap));
				}
			}
		}

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			Uri uri = Uri.withAppendedPath(
					ContactsContract.Profile.CONTENT_URI,
					ContactsContract.Contacts.Data.CONTENT_DIRECTORY);
			return new CursorLoader(AllTaskActivity.this, uri,
					new String[] { Profile.PHOTO_THUMBNAIL_URI }, null, null,
					null);
		}
	};

	private void onTabChanged(View view) {
		isSelfTabSelected = false;
		isInTabSelected = false;
		isOutTabSelected = false;
		isFavoriteTabSelected = false;
		isFireTabSelected = false;

		mAllTabLayout.setBackgroundResource(R.drawable.tab_unselected);
		mSelfTabLayout.setBackgroundResource(R.drawable.tab_unselected);
		mInTabLayout.setBackgroundResource(R.drawable.tab_unselected);
		mOutTabLayout.setBackgroundResource(R.drawable.tab_unselected);
		mFavroiteTabLayout.setBackgroundResource(R.drawable.tab_unselected);
		mFireTabLayout.setBackgroundResource(R.drawable.tab_unselected);

		mFavoriteTabImageView
				.setImageResource(R.drawable.star_tabbar_unselected);
		mFireTabImageView.setImageResource(R.drawable.fire_tabbar_unselected);

		mAllTabTextView.setTextColor(getResources().getColor(
				R.color.unselected_tab_text_color));
		mSelfTabTextView.setTextColor(getResources().getColor(
				R.color.unselected_tab_text_color));
		mInTabTextView.setTextColor(getResources().getColor(
				R.color.unselected_tab_text_color));
		mOutTabTextView.setTextColor(getResources().getColor(
				R.color.unselected_tab_text_color));

		if (view != null) {
			view.setBackgroundResource(R.drawable.tab_selected);
		} else {
			isSelfTabSelected = true;
			isInTabSelected = true;
			isOutTabSelected = true;
			mAllTabLayout.setBackgroundResource(R.drawable.tab_selected);
			mAllTabTextView.setTextColor(getResources().getColor(
					R.color.selected_tab_text_color));
		}

		if (view == null) {
			return;
		}
		if (view.getId() == mAllTabLayout.getId()) {
			isSelfTabSelected = true;
			isInTabSelected = true;
			isOutTabSelected = true;
			mAllTabTextView.setTextColor(getResources().getColor(
					R.color.selected_tab_text_color));
		} else if (view.getId() == mSelfTabLayout.getId()) {
			isSelfTabSelected = true;
			mSelfTabTextView.setTextColor(getResources().getColor(
					R.color.selected_tab_text_color));
		} else if (view.getId() == mInTabLayout.getId()) {
			isInTabSelected = true;
			mInTabTextView.setTextColor(getResources().getColor(
					R.color.selected_tab_text_color));
		} else if (view.getId() == mOutTabLayout.getId()) {
			isOutTabSelected = true;
			mOutTabTextView.setTextColor(getResources().getColor(
					R.color.selected_tab_text_color));
		} else if (view.getId() == mFavroiteTabLayout.getId()) {
			isSelfTabSelected = true;
			isInTabSelected = true;
			isOutTabSelected = true;
			isFavoriteTabSelected = true;
			mFavoriteTabImageView
					.setImageResource(R.drawable.star_tabbar_selected);
		} else if (view.getId() == mFireTabLayout.getId()) {
			isSelfTabSelected = true;
			isInTabSelected = true;
			isOutTabSelected = true;
			isFireTabSelected = true;
			mFireTabImageView.setImageResource(R.drawable.fire_tabbar_selected);
		}
	}

	public void setHeaderTitleCount(String title, int count) {
		// TODO Fix the title issue.
		title = title == null ? TaskCategory.TASK_BOX : title;
		mCountTextVeiw.setText(String.format("%s (%d)", title, count));
		this.headerTitle = title;
	}

	public void setHeaderTitleCount(String title) {
		title = title == null ? TaskCategory.TASK_BOX : title;
		mCountTextVeiw.setText(title);
		this.headerTitle = title;
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				CommonUtil.DISPLAY_MESSAGE_ACTION));
		registerReceiver(broadcastReceiver, new IntentFilter(
				"com.taskmaganger.ui.AlltaskActivity.relaodList.broadcast"));
	}

	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (broadcastReceiver != null)
			unregisterReceiver(broadcastReceiver);
	}

	/**
	 * 
	 * To Display Junk Task
	 */

	/**
	 * method to render contact wise task list
	 */
	private String contactWiseType;
	private String contactWiseTypeValue;
	boolean isContactWise;

	private void createContactWiseList(ArrayList<TaskInfoEntity> entities) {
		LinearLayout parent = (LinearLayout) findViewById(R.id.tasklist);
		String valueType = null;
		String value = null;
		parent.removeAllViews();
		for (int i = 0; i < entities.size(); i++) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final View child = inflater
					.inflate(R.layout.contact_wise_row, null);

			String dbName = entities.get(i).getAssignFromName();
			String dbNum = entities.get(i).getAssign_from();
			String dbCount = entities.get(i).getCount();

			TextView name = (TextView) child.findViewById(R.id.name);
			name.setTypeface(CustomTypeFace
					.getRobotoMedium(AllTaskActivity.this));
			TextView count = (TextView) child.findViewById(R.id.taskcount);
			count.setTypeface(CustomTypeFace
					.getRobotoRegular(AllTaskActivity.this));

			ImageView imageView = (ImageView) child
					.findViewById(R.id.ContactPhotoView);
			imageView.setBackgroundResource(NonProfilePhotoPlaceHolder
					.getPhotoPlaceHolder(""));
			DownloadImageTask.setImage(AllTaskActivity.this, dbNum, imageView,
					true);

			if (dbNum != null && !dbNum.trim().equals("")) {
				// DownloadImageTask.setImage(AllTaskActivity.this, dbNum,
				// imageView, true);
				valueType = "number";
				value = dbNum;
				name.setText(ApplicationUtil.getContactNameFromPhoneNo(
						AllTaskActivity.this, dbNum));
			}
			final String ValType = valueType;
			final String Val = value;
			count.setText(dbCount);

			child.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					isContactWise = true;
					loadContactWiseTask(Val, ValType);
				}
			});
			parent.addView(child);
		}
		progressHandler.sendEmptyMessage(0);
	}

	private void createHashWiseList(ArrayList<NotationDto> entities) {
		String WORK = "#Work";
		String TRAVEL = "#Travel";
		String SHOPPING = "#Shopping";
		String PARTY = "#Party";
		
		ArrayList<NotationDto> defaultList = new ArrayList<NotationDto>();
		for (int i = 0; i < DBAdapter.notationName.length; i++) {
			NotationDto dto = new NotationDto();
			dto.setNotaionName(DBAdapter.notationName[i]);
	dto.setCount(0);
	
			if(!entities.contains(dto)){
				entities.add(dto);
			}
		}
		
		for(int index = 0; index < entities.size(); index++){
			NotationDto dto = entities.get(index);
			String notationName = dto.getNotaionName();
//			if(notationName.contains(WORK) || notationName.contains(TRAVEL) || notationName.contains(SHOPPING) || notationName.contains(PARTY)){
			if(notationName.equalsIgnoreCase(WORK) || notationName.equalsIgnoreCase(TRAVEL) || notationName.equalsIgnoreCase(SHOPPING) || notationName.equalsIgnoreCase(PARTY)){
				defaultList.add(0, dto);
			}else{
				defaultList.add(dto);
			}
		}
		
		entities = defaultList;
		
		setHeaderTitleCount(TaskCategory.TASK_CATEGORIES, entities.size());
		
		LinearLayout parent = (LinearLayout) findViewById(R.id.tasklist);
		String valueType = null;
		parent.removeAllViews();
		for (int i = 0; i < entities.size(); i++) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final View child = inflater.inflate(R.layout.contact_wise_row, null);

			String notationName = entities.get(i).getNotaionName();

			int dbCount = entities.get(i).getCount();

			TextView name = (TextView) child.findViewById(R.id.name);
			name.setTypeface(CustomTypeFace
					.getRobotoMedium(AllTaskActivity.this));
			TextView count = (TextView) child.findViewById(R.id.taskcount);
			count.setTypeface(CustomTypeFace
					.getRobotoRegular(AllTaskActivity.this));

			name.setText(notationName.replace("#", ""));

			ImageView imageView = (ImageView) child	.findViewById(R.id.ContactPhotoView);

			String categoryNameForIcon = notationName.replace("#", "");
			if (categoryNameForIcon.equalsIgnoreCase("work")) {
				imageView.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.hash_work));
			} else if (categoryNameForIcon.equalsIgnoreCase("travel")) {
				imageView.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.hash_travel));
			} else if (categoryNameForIcon.equalsIgnoreCase("shopping")) {
				imageView.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.hash_shopping));
			} else if (categoryNameForIcon.equalsIgnoreCase("party")) {
				imageView.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.hash_party));
			} else {
				imageView.setBackgroundDrawable(getResources().getDrawable(
						NonProfilePhotoPlaceHolder
								.getHashPlaceHolder(categoryNameForIcon)));
			}

			// name.setText(notationName);
			final String ValType = valueType;
			final String Val = notationName;
			count.setText(dbCount + "");

			child.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					isContactWise = true;
					loadContactWiseTask(Val, ValType, true);
				}
			});
			parent.addView(child);
		}
		progressHandler.sendEmptyMessage(0);
	}

	/**
	 * Side menu slider.
	 */
	private void showCustomDialog() {
		final DialogMenu dialog = new DialogMenu(AllTaskActivity.this, R.style.SliderMenuDialogAnimation, R.layout.slider_left_menu_dialog);
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		lp.gravity = Gravity.LEFT;
		dialog.getWindow().setAttributes(lp);
		dialog.show();

		// User Details
		ViewGroup homeLayout = (ViewGroup)dialog.findViewById(R.id.HomeLayout);
		homeLayout.setBackgroundResource(AppUtility.getSideMenuHeaderBackground());
		ImageView userImageView = (ImageView) dialog .findViewById(R.id.ContactPhotoView);
		TextView nameTextView = (TextView) dialog .findViewById(R.id.NameTextView);
		TextView mobileNumberTextView = (TextView) dialog .findViewById(R.id.MobileNumberTextView);
		TextView quoteTextView = (TextView)dialog.findViewById(R.id.QuoteTextView);
		quoteTextView.setText(String.format("\"%s.\"", AppUtility.getSideMenuHeaderQuote()));

		// Assigners
		final ViewGroup contactWiseReceived = (ViewGroup) dialog
				.findViewById(R.id.contactReceive);
		TextView contact_recieved_txt = (TextView) dialog
				.findViewById(R.id.recievedTaskId);

		// Assignees
		final ViewGroup contactWiseAssigned = (ViewGroup) dialog
				.findViewById(R.id.contactAssigned);
		TextView contact_assigned_txt = (TextView) dialog
				.findViewById(R.id.assignedTaskId);

		// Project/Category
		final ViewGroup contactWiseHasTask = (ViewGroup) dialog
				.findViewById(R.id.contactAssignedHash);
		TextView contact_assigned_txtHash = (TextView) dialog
				.findViewById(R.id.assignedTaskIdHash);

		// Open Task
		final ViewGroup openTaskFilter = (ViewGroup) dialog
				.findViewById(R.id.openfilterlinear);
		TextView contact_open_txt = (TextView) dialog
				.findViewById(R.id.openTaskId);
		TextView openIcon = (TextView) dialog.findViewById(R.id.openicon);

		// Closed Task
		final ViewGroup closedTaskFilter = (ViewGroup) dialog
				.findViewById(R.id.closefilterlinear);
		TextView contact_close_txt = (TextView) dialog
				.findViewById(R.id.closeTaskId);
		TextView closedIcon = (TextView) dialog.findViewById(R.id.closedicon);

		// Unknown Task
		final ViewGroup junkTaskFilter = (ViewGroup) dialog
				.findViewById(R.id.junkfilterlinear);
		TextView contact_junk_txt = (TextView) dialog
				.findViewById(R.id.junkTaskId);
		TextView junkIcon = (TextView) dialog.findViewById(R.id.junkicon);

		final ViewGroup todayFilterlinear = (ViewGroup) dialog
				.findViewById(R.id.todayFilterlinear);
		TextView todayicon = (TextView) dialog.findViewById(R.id.todayicon);
		TextView todayTitle = (TextView) dialog.findViewById(R.id.todayTaskId);

		final ViewGroup tomorrowFilterlinear = (ViewGroup) dialog
				.findViewById(R.id.tomorrowFilterlinear);
		TextView tomorrowicon = (TextView) dialog
				.findViewById(R.id.tomorrowicon);
		TextView tomorrowTitle = (TextView) dialog
				.findViewById(R.id.tomorrowTaskId);

		final ViewGroup next7daysFilterlinear = (ViewGroup) dialog
				.findViewById(R.id.Next7daysFilterlinear);
		TextView next7daysicon = (TextView) dialog
				.findViewById(R.id.Next7daysicon);
		TextView next7daysTitle = (TextView) dialog
				.findViewById(R.id.Next7daysTaskId);

		final ViewGroup delayedFilterlinear = (ViewGroup) dialog
				.findViewById(R.id.DelayedFilterlinear);
		TextView delayedicon = (TextView) dialog.findViewById(R.id.Delayedicon);
		TextView delayedTitle = (TextView) dialog
				.findViewById(R.id.DelayedTaskId);

		
		final ViewGroup snoozFilterlinear = (ViewGroup) dialog.findViewById(R.id.snoozilterlinear);
		TextView snoozicon = (TextView) dialog.findViewById(R.id.snoozicon);
		TextView snoozTitle = (TextView) dialog.findViewById(R.id.snoozTaskId);
		
		final ViewGroup fireFilterLinear = (ViewGroup) dialog.findViewById(R.id.FireFilterlinear);
		TextView fireTitle = (TextView) dialog.findViewById(R.id.FireTaskId);
		TextView fireIcon = (TextView) dialog.findViewById(R.id.FireIcon);
		
		
		
		final ViewGroup sync = (ViewGroup) dialog.findViewById(R.id.synclinear);
		final ViewGroup support = (ViewGroup) dialog
				.findViewById(R.id.relativeFaq);

		// Initialize user details
		userImageView.setBackgroundResource(NonProfilePhotoPlaceHolder .getPhotoPlaceHolder(ApplicationUtil.getPreference( ApplicationConstant.REG_NAME, context)));
		DownloadImageTask.setImage(AllTaskActivity.this, ApplicationUtil.getPreference( ApplicationConstant.regMobNo, context), userImageView, true);
		nameTextView.setText(ApplicationUtil.getPreference( ApplicationConstant.REG_NAME, context));
		mobileNumberTextView.setText(ApplicationUtil.getPreference( ApplicationConstant.regMobNo, context));

		// Initialize the count of tasks
		TaskEntity entity = new TaskEntity();
		openIcon.setText("" + entity.getTotalOpen(context));
		closedIcon.setText("" + entity.getTotalClose(context));
		junkIcon.setText("" + entity.getTotalJunk(context));
		delayedicon.setText("" + entity.delayedTask(context));
		todayicon.setText("" + entity.totalTodayTasks(context));
		tomorrowicon.setText("" + entity.tomorrowTasks(context));
		next7daysicon.setText("" + entity.next7dayTasks(context));
		snoozicon.setText("" + entity.snoozCount(context));
		fireIcon.setText("" + entity.urgencyCount(context));//Not implemented yet

		// Restore selected labels
		//Group 1
		if (mSideMenuSelectedLabels.equals("Received")) {
			contactWiseReceived.setBackgroundColor(getResources().getColor( R.color.slider_item_selected_bg_color));
			contact_recieved_txt.setTextColor(getResources().getColor( R.color.slider_item_selected_text_color));
//			contact_recieved_txt.setCompoundDrawablesWithIntrinsicBounds( R.drawable.slider_assigners_selected, 0, 0, 0);
		} else if (mSideMenuSelectedLabels.equals("Assigned")) {
			contactWiseAssigned.setBackgroundColor(getResources().getColor( R.color.slider_item_selected_bg_color)); 
			contact_assigned_txt.setTextColor(getResources().getColor( R.color.slider_item_selected_text_color));
//			contact_assigned_txt.setCompoundDrawablesWithIntrinsicBounds( R.drawable.slider_assigners_selected, 0, 0, 0);
		} else if (mSideMenuSelectedLabels.equals("Category")) {
			contactWiseHasTask.setBackgroundColor(getResources().getColor( R.color.slider_item_selected_bg_color));
			contact_assigned_txtHash.setTextColor(getResources().getColor( R.color.slider_item_selected_text_color));
//			contact_assigned_txtHash.setCompoundDrawablesWithIntrinsicBounds( R.drawable.slider_category_selected, 0, 0, 0);
		}
		
		//Group 2
		else if (mSideMenuSelectedLabels.equals(getString(R.string.openTask))) {
			openTaskFilter.setBackgroundColor(getResources().getColor(
					R.color.slider_item_selected_bg_color));
			contact_open_txt.setTextColor(Color.parseColor("#32b1fa"));
//			contact_open_txt.setCompoundDrawablesWithIntrinsicBounds( R.drawable.slider_open_selected, 0, 0, 0);
			openIcon.setTextColor(Color.parseColor("#32b1fa"));
		} else if (mSideMenuSelectedLabels
				.equals(getString(R.string.closedTask))) {
			closedTaskFilter.setBackgroundColor(getResources().getColor( R.color.slider_item_selected_bg_color));
			contact_close_txt.setTextColor(Color.parseColor("#0f9d58")); 
//			contact_close_txt.setCompoundDrawablesWithIntrinsicBounds( R.drawable.slider_closed_selected, 0, 0, 0);
			closedIcon.setTextColor(Color.parseColor("#0f9d58"));
		} else if (mSideMenuSelectedLabels .equals(getString(R.string.unknownTask))) {
			junkTaskFilter.setBackgroundColor(getResources().getColor( R.color.slider_item_selected_bg_color));
			contact_junk_txt.setTextColor(Color.parseColor("#9e9e9e"));
//			contact_junk_txt.setCompoundDrawablesWithIntrinsicBounds( R.drawable.slider_spam_selected, 0, 0, 0);
			junkIcon.setTextColor(Color.parseColor("#9e9e9e"));
		} 
		
		//Group 3
		else if (mSideMenuSelectedLabels .equals(getString(R.string.fireTask))) { 
			fireFilterLinear.setBackgroundColor(getResources().getColor( R.color.slider_item_selected_bg_color));
			fireTitle.setTextColor(Color.parseColor("#e4302f"));
			fireIcon.setTextColor(Color.parseColor("#e4302f"));
		} else if (mSideMenuSelectedLabels
				.equals(getString(R.string.delayedTask))) {
			delayedFilterlinear.setBackgroundColor(getResources().getColor(
					R.color.slider_item_selected_bg_color));
			delayedTitle.setTextColor(Color.parseColor("#da4336"));
//			delayedTitle.setCompoundDrawablesWithIntrinsicBounds( R.drawable.slider_delay_selected, 0, 0, 0);
			delayedicon.setTextColor(Color.parseColor("#da4336"));
		} else if (mSideMenuSelectedLabels
				.equals(getString(R.string.todaysTask))) {
			todayFilterlinear.setBackgroundColor(getResources().getColor(
					R.color.slider_item_selected_bg_color));
			todayTitle.setTextColor(getResources().getColor(R.color.slider_item_selected_text_color));
//			todayTitle.setCompoundDrawablesWithIntrinsicBounds( R.drawable.slider_calendar_selected, 0, 0, 0);
			todayicon.setTextColor(getResources().getColor(R.color.slider_item_selected_text_color));
		} else if (mSideMenuSelectedLabels
				.equals(getString(R.string.tomorrowsTask))) {
			tomorrowFilterlinear.setBackgroundColor(getResources().getColor(
					R.color.slider_item_selected_bg_color));
			tomorrowTitle.setTextColor(getResources().getColor(R.color.slider_item_selected_text_color));
//			tomorrowTitle.setCompoundDrawablesWithIntrinsicBounds( R.drawable.slider_calendar_selected, 0, 0, 0);
			tomorrowicon.setTextColor(getResources().getColor(R.color.slider_item_selected_text_color));
		} else if (mSideMenuSelectedLabels
				.equals(getString(R.string.next7DaysTask))) {
			next7daysFilterlinear.setBackgroundColor(getResources().getColor(
					R.color.slider_item_selected_bg_color));
			next7daysTitle.setTextColor(getResources().getColor(R.color.slider_item_selected_text_color));
//			next7daysTitle.setCompoundDrawablesWithIntrinsicBounds( R.drawable.slider_calendar_selected, 0, 0, 0);
			next7daysicon.setTextColor(getResources().getColor(R.color.slider_item_selected_text_color));
		} else if (mSideMenuSelectedLabels
				.equals(getString(R.string.snoozTask))) {
			snoozFilterlinear.setBackgroundColor(getResources().getColor(
					R.color.slider_item_selected_bg_color));
			snoozTitle.setTextColor(Color.parseColor("#eca403"));
//			snoozTitle.setCompoundDrawablesWithIntrinsicBounds( R.drawable.slider_later_selected, 0, 0, 0);
			snoozicon.setTextColor(Color.parseColor("#eca403"));
		} else {// Default
			openTaskFilter.setBackgroundColor(getResources().getColor(
					R.color.slider_item_selected_bg_color));
			contact_open_txt.setTextColor(Color.parseColor("#32b1fa"));
//			contact_open_txt.setCompoundDrawablesWithIntrinsicBounds( R.drawable.slider_open_selected, 0, 0, 0);
			openIcon.setTextColor(Color.parseColor("#32b1fa"));
		}
		
		//User detail for home screen
		homeLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openTaskFilter.performClick();			
			}
		});

		// Assigners
		contactWiseReceived.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isPullRefreshInProgress) {
					return;
				}
				mHeaderLayout.setBackgroundResource(R.drawable.navigation_bar);
				updatedBackNavigativeStack(null, null, BackNavigationStackAction.CLEAR);
				mTabsLayout.setVisibility(View.GONE);
				mFocusNowHeaderCheckBox.setVisibility(View.GONE);

				sortBy = "";
				contactWiseReceived.setBackgroundColor(getResources().getColor(
						R.color.slider_item_selected_bg_color));
				mFireTabTextView.setText("0");
				mFavoriteTabTextView.setText("0");
				contactSummery = "Receive";
				listview.setVisibility(View.GONE);
				sortVal = getResources().getString(R.string.open_task_status);
				taskType = "Inbox";
				junkFlag = false;

				CreateContactWiseSummery contactWiseSummery = new CreateContactWiseSummery();
				contactWiseSummery.setTitle(TaskCategory.TASK_RECEIVED);
				contactWiseSummery.execute();

				mSideMenuSelectedLabels = "Received";
				dialog.dismiss();
			}
		});

		// Assignees
		contactWiseAssigned.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isPullRefreshInProgress) {
					return;
				}
				mHeaderLayout.setBackgroundResource(R.drawable.navigation_bar);
				updatedBackNavigativeStack(null, null, BackNavigationStackAction.CLEAR);
				mTabsLayout.setVisibility(View.GONE);
				mFocusNowHeaderCheckBox.setVisibility(View.GONE);

				sortBy = "";
				contactWiseAssigned.setBackgroundColor(getResources().getColor(
						R.color.slider_item_selected_bg_color));
				contactSummery = "Assign";
				mFireTabTextView.setText("0");
				mFavoriteTabTextView.setText("0");

				listview.setVisibility(View.GONE);
				sortVal = getResources().getString(R.string.open_task_status);

				taskType = "sent";
				junkFlag = false;
				isContactWise = true;

				CreateContactWiseSummery contactWiseSummery = new CreateContactWiseSummery();
				contactWiseSummery.setTitle(TaskCategory.TASK_ASSIGNED);
				contactWiseSummery.execute();

				mSideMenuSelectedLabels = "Assigned";
				dialog.dismiss();
			}
		});

		// Project/Category
		contactWiseHasTask.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isPullRefreshInProgress) {
					return;
				}
				mHeaderLayout.setBackgroundResource(R.drawable.navigation_bar);
				updatedBackNavigativeStack(null, null, BackNavigationStackAction.CLEAR);
				mTabsLayout.setVisibility(View.GONE);
				mFocusNowHeaderCheckBox.setVisibility(View.GONE);

				sortBy = "";
				contactWiseHasTask.setBackgroundColor(getResources().getColor(
						R.color.slider_item_selected_bg_color));
				contactSummery = "hasTask";
				mFireTabTextView.setText("0");
				mFavoriteTabTextView.setText("0");

				listview.setVisibility(View.GONE);
				sortVal = getResources().getString(R.string.open_task_status);

				taskType = "hashTask";
				junkFlag = false;
				isContactWise = true;

				CreateHasWiseSummery createHasWiseSummery = new CreateHasWiseSummery();
				createHasWiseSummery.setTitle(TaskCategory.TASK_CATEGORIES);
				createHasWiseSummery.execute();

				mSideMenuSelectedLabels = "Category";
				dialog.dismiss();
			}
		});

		// Open Tasks
		openTaskFilter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isPullRefreshInProgress) {
					return;
				}
				mHeaderLayout.setBackgroundResource(R.drawable.navigation_bar);
				updatedBackNavigativeStack(null, null, BackNavigationStackAction.CLEAR);
				mTabsLayout.setVisibility(View.VISIBLE);
				
				mFocusNowHeaderCheckBox.setVisibility(View.VISIBLE);
				mFocusNowHeaderCheckBox.setChecked(false);
				
				openTaskFilter.setBackgroundColor(getResources().getColor( R.color.slider_item_selected_bg_color));
				closedTaskFilter.setBackgroundColor(getResources().getColor( R.color.trans));
				junkTaskFilter.setBackgroundColor(getResources().getColor( R.color.trans));

				contactWiseAssigned.setBackgroundColor(getResources().getColor( R.color.trans));
				contactWiseReceived.setBackgroundColor(getResources().getColor( R.color.trans));

				clickOnAllTabButton(mAllTabLayout);
				isLater=false;
//				sortBy = "UPDATION_DATE_TIME";
//				junkFlag = false;
//				contactSummery = "";
//				sortVal = getResources().getString(R.string.open_task_status);
//
//				AsyncTotalOpen asyncTotalOpen = new AsyncTotalOpen();
//				asyncTotalOpen.setTitle("Task Box");
//				asyncTotalOpen.execute();
				sortVal="Open";
				mSideMenuSelectedLabels = getString(R.string.openTask);
				dialog.dismiss();
			}
		});

		// Closed Tasks
		closedTaskFilter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isPullRefreshInProgress) {
					return;
				}
				mHeaderLayout.setBackgroundResource(R.drawable.navigation_bar_done);
				updatedBackNavigativeStack(null, null, BackNavigationStackAction.CLEAR);
				mTabsLayout.setVisibility(View.GONE);
				mFocusNowHeaderCheckBox.setVisibility(View.GONE);
				mHeaderSearchButton.setVisibility(View.GONE);

				junkFlag = false;
				contactSummery = "";
				openTaskFilter.setBackgroundColor(getResources().getColor(
						R.color.trans));
				closedTaskFilter.setBackgroundColor(getResources().getColor(
						R.color.slider_item_selected_bg_color));
				junkTaskFilter.setBackgroundColor(getResources().getColor(
						R.color.trans));
				contactWiseAssigned.setBackgroundColor(getResources().getColor(
						R.color.trans));
				contactWiseReceived.setBackgroundColor(getResources().getColor(
						R.color.trans));

				//
				sortVal = getResources().getString(R.string.closed_task_status);
				if (sortBy == null || sortBy.isEmpty()) {
					sortBy = "UPDATION_DATE_TIME";
				}

				AsyntaskClose asyntaskClose = new AsyntaskClose(true);
				asyntaskClose.setTitle(TaskCategory.TASK_DONE);
				asyntaskClose.execute();

				mSideMenuSelectedLabels = getString(R.string.closedTask);
				dialog.dismiss();
			}
		});

		// //Unknown Tasks
		junkTaskFilter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isPullRefreshInProgress) {
					return;
				}
				mHeaderLayout.setBackgroundResource(R.drawable.navigation_bar_spam);
				updatedBackNavigativeStack(null, null, BackNavigationStackAction.CLEAR);
				mTabsLayout.setVisibility(View.GONE);
				mFocusNowHeaderCheckBox.setVisibility(View.GONE);

				if (sortBy == null || sortBy.isEmpty()) {
					sortBy = "UPDATION_DATE_TIME";
				}
				contactSummery = "";
				openTaskFilter.setBackgroundColor(getResources().getColor(
						R.color.trans));
				closedTaskFilter.setBackgroundColor(getResources().getColor(
						R.color.trans));
				junkTaskFilter.setBackgroundColor(getResources().getColor(
						R.color.slider_item_selected_bg_color));
				contactWiseAssigned.setBackgroundColor(getResources().getColor(
						R.color.trans));
				contactWiseReceived.setBackgroundColor(getResources().getColor(
						R.color.trans));
				isContactWise = false;
				sortVal = getResources().getString(R.string.open_task_status);
				junkFlag = false;
				junkFlag = true;

				LoadJunkList loadJunkList = new LoadJunkList(true);
				loadJunkList.setTitle(TaskCategory.TASK_SPAM);
				loadJunkList.execute();

				mSideMenuSelectedLabels = getString(R.string.unknownTask);
				dialog.dismiss();
			}
		});

		// today Tasks
		todayFilterlinear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isPullRefreshInProgress) {
					return;
				}
				isLater=true;
				mHeaderLayout.setBackgroundResource(R.drawable.navigation_bar);
				updatedBackNavigativeStack(null, null, BackNavigationStackAction.CLEAR);
				mTabsLayout.setVisibility(View.GONE);
				mFocusNowHeaderCheckBox.setVisibility(View.GONE);

				if (sortBy == null || sortBy.isEmpty()) {
					sortBy = "UPDATION_DATE_TIME";
				}

				junkFlag = false;
				openTaskFilter.setBackgroundColor(getResources().getColor(
						R.color.slider_item_selected_bg_color));
				closedTaskFilter.setBackgroundColor(getResources().getColor(
						R.color.trans));
				junkTaskFilter.setBackgroundColor(getResources().getColor(
						R.color.trans));

				contactSummery = "";
				contactWiseAssigned.setBackgroundColor(getResources().getColor(
						R.color.trans));
				contactWiseReceived.setBackgroundColor(getResources().getColor(
						R.color.trans));
				sortBy = "today";
				onTabChanged(null);
				// isSelfTabSelected, isInTabSelected, isOutTabSelected,
				// isFavoriteTabSelected, isFireTabSelected
				AsyncTotalOpen asyncTotalOpen = new AsyncTotalOpen();
				asyncTotalOpen.setTitle(TaskCategory.TASK_TODAY);
				asyncTotalOpen.execute();

				mSideMenuSelectedLabels = getString(R.string.todaysTask);
				dialog.dismiss();
			}
		});

		// tomorrow Tasks
		tomorrowFilterlinear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isPullRefreshInProgress) {
					return;
				}
				isLater=true;
				mHeaderLayout.setBackgroundResource(R.drawable.navigation_bar);
				updatedBackNavigativeStack(null, null, BackNavigationStackAction.CLEAR);
				mTabsLayout.setVisibility(View.GONE);
				mFocusNowHeaderCheckBox.setVisibility(View.GONE);

				if (sortBy == null || sortBy.isEmpty()) {
					sortBy = "UPDATION_DATE_TIME";
				}

				junkFlag = false;
				openTaskFilter.setBackgroundColor(getResources().getColor(
						R.color.slider_item_selected_bg_color));
				closedTaskFilter.setBackgroundColor(getResources().getColor(
						R.color.trans));
				junkTaskFilter.setBackgroundColor(getResources().getColor(
						R.color.trans));

				contactSummery = "";
				contactWiseAssigned.setBackgroundColor(getResources().getColor(
						R.color.trans));
				contactWiseReceived.setBackgroundColor(getResources().getColor(
						R.color.trans));
				sortBy = "tomorrow";
				onTabChanged(null);
				AsyncTotalOpen asyncTotalOpen = new AsyncTotalOpen();
				asyncTotalOpen.setTitle(TaskCategory.TASK_TOMORROW);
				asyncTotalOpen.execute();

				mSideMenuSelectedLabels = getString(R.string.tomorrowsTask);
				dialog.dismiss();
			}
		});

		// next 7 days Tasks
		next7daysFilterlinear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isPullRefreshInProgress) {
					return;
				}
				isLater=true;
				mHeaderLayout.setBackgroundResource(R.drawable.navigation_bar);
				updatedBackNavigativeStack(null, null, BackNavigationStackAction.CLEAR);
				mTabsLayout.setVisibility(View.GONE);
				mFocusNowHeaderCheckBox.setVisibility(View.GONE);

				if (sortBy == null || sortBy.isEmpty()) {
					sortBy = "UPDATION_DATE_TIME";
				}

				junkFlag = false;
				openTaskFilter.setBackgroundColor(getResources().getColor(
						R.color.slider_item_selected_bg_color));
				closedTaskFilter.setBackgroundColor(getResources().getColor(
						R.color.trans));
				junkTaskFilter.setBackgroundColor(getResources().getColor(
						R.color.trans));

				contactSummery = "";
				contactWiseAssigned.setBackgroundColor(getResources().getColor(
						R.color.trans));
				contactWiseReceived.setBackgroundColor(getResources().getColor(
						R.color.trans));
				sortBy = "next7Days";
				onTabChanged(null);
				AsyncTotalOpen asyncTotalOpen = new AsyncTotalOpen();
				asyncTotalOpen.setTitle(TaskCategory.TASK_NEXT_7_DAY);
				asyncTotalOpen.execute();

				mSideMenuSelectedLabels = getString(R.string.next7DaysTask);
				dialog.dismiss();
			}
		});

		//Fire Tasks
		fireFilterLinear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isPullRefreshInProgress) {
					return;
				}
				sortBy = "fire";
				isLater=true;
				//Navigation header will change later.
				mHeaderLayout.setBackgroundResource(R.drawable.navigation_bar_urgency);
				updatedBackNavigativeStack(null, null, BackNavigationStackAction.CLEAR);
				mTabsLayout.setVisibility(View.GONE);
				mFocusNowHeaderCheckBox.setVisibility(View.GONE);
				
				//To make the query for all.
				isSelfTabSelected = true;
				isInTabSelected = true;
				isOutTabSelected = true;
				isFireTabSelected = true;
				
				Asyntask asyntask = new Asyntask();
				asyntask.setTitle(TaskCategory.TASK_URGENT);
				asyntask.execute();
				mSideMenuSelectedLabels = getString(R.string.fireTask);	
				dialog.dismiss();
			}
		});
		
		// Delay Tasks
		delayedFilterlinear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isPullRefreshInProgress) {
					return;
				}
				isLater=true;
				mHeaderLayout.setBackgroundResource(R.drawable.navigation_bar_delayed);
				updatedBackNavigativeStack(null, null, BackNavigationStackAction.CLEAR);
				mTabsLayout.setVisibility(View.GONE);
				mFocusNowHeaderCheckBox.setVisibility(View.GONE);

				if (sortBy == null || sortBy.isEmpty()) {
					sortBy = "UPDATION_DATE_TIME";
				}

				junkFlag = false;
				openTaskFilter.setBackgroundColor(getResources().getColor(
						R.color.slider_item_selected_bg_color));
				closedTaskFilter.setBackgroundColor(getResources().getColor(
						R.color.trans));
				junkTaskFilter.setBackgroundColor(getResources().getColor(
						R.color.trans));

				contactSummery = "";
				contactWiseAssigned.setBackgroundColor(getResources().getColor(
						R.color.trans));
				contactWiseReceived.setBackgroundColor(getResources().getColor(
						R.color.trans));
				sortBy = "delayed";
				onTabChanged(null);
				AsyncTotalOpen asyncTotalOpen = new AsyncTotalOpen();
				asyncTotalOpen.setTitle(TaskCategory.TASK_DELAYED);
				asyncTotalOpen.execute();

				mSideMenuSelectedLabels = getString(R.string.delayedTask);
				dialog.dismiss();
			}
		});
		
		// Delay Tasks
		snoozFilterlinear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isPullRefreshInProgress) {
					return;
				}
				mHeaderLayout.setBackgroundResource(R.drawable.navigation_bar_later);
				updatedBackNavigativeStack(null, null, BackNavigationStackAction.CLEAR);
				mTabsLayout.setVisibility(View.GONE);
				mFocusNowHeaderCheckBox.setVisibility(View.GONE);
				onTabChanged(null);

				if (sortBy == null || sortBy.isEmpty()) {
					sortBy = "UPDATION_DATE_TIME";
				}

				junkFlag = false;
				openTaskFilter.setBackgroundColor(getResources().getColor(
						R.color.slider_item_selected_bg_color));
				closedTaskFilter.setBackgroundColor(getResources().getColor(
						R.color.trans));
				junkTaskFilter.setBackgroundColor(getResources().getColor(
						R.color.trans));

				contactSummery = "";
				contactWiseAssigned.setBackgroundColor(getResources().getColor(
						R.color.trans));
				contactWiseReceived.setBackgroundColor(getResources().getColor(
						R.color.trans));
				sortBy = "snooz";
				isLater=true;
				onTabChanged(null);
				AsyncTotalOpen asyncTotalOpen = new AsyncTotalOpen();
				asyncTotalOpen.setTitle(TaskCategory.TASK_LATER);
				asyncTotalOpen.execute();

				mSideMenuSelectedLabels = getString(R.string.snoozTask);
				dialog.dismiss();
			}
		});

		// Sync
		sync.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isPullRefreshInProgress) {
					return;
				}
				mHeaderLayout.setBackgroundResource(R.drawable.navigation_bar);
				try {
					if (sortBy == null || sortBy.isEmpty()) {
						sortBy = "UPDATION_DATE_TIME";
					}
					junkFlag = false;
					sync.setEnabled(false);
					new AsyncTask<Void, Void, Void>() {
						private ProgressDialog dialog;

						protected void onPreExecute() {
							dialog = new ProgressDialog(AllTaskActivity.this);
							dialog.setMessage("loading...");
							dialog.setCancelable(false);
							dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
							dialog.show();
						};

						protected void onPostExecute(Void result) {
							dialog.dismiss();
							new AsyncTotalOpen().execute();
						};

						@Override
						protected Void doInBackground(Void... params) {
							String curentMsisdn = ApplicationUtil
									.getPreference(
											ApplicationConstant.regMobNo,
											context);
							try {
								Thread thread = new Thread(syncUtils);
								thread.start();
								thread.join();
							} catch (Exception e) {
								e.printStackTrace();
							}
							return null;
						}
					}.execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog.dismiss();
			}
		});

		// Support
		support.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
						.parse(ApplicationConstant.SUPPORT_URL));
				startActivity(browserIntent);
			}
		});
	}
	
	private void clickOnAllTabButton(View view){
		if (isPullRefreshInProgress) {
			return;
		}
		
		mHeaderLayout.setBackgroundResource(R.drawable.navigation_bar);
		updatedBackNavigativeStack(null, null, BackNavigationStackAction.CLEAR);
		mTabsLayout.setVisibility(View.VISIBLE);
		mFocusNowHeaderCheckBox.setVisibility(View.VISIBLE);
		mHeaderSearchButton.setVisibility(View.VISIBLE);
		mFocusNowHeaderCheckBox.setChecked(false);
		
		onTabChanged(view);
		isContactWise = false;
		sortVal = getResources().getString(R.string.open_task_status);
		junkFlag = false;
		sortBy = "UPDATION_DATE_TIME";
		ApplicationConstant.message_Task_Flag_run_handle = 1;
		ApplicationConstant.createTask_Flag_run_handle = 1;

		AsyncTotalOpen asyncTotalOpen = new AsyncTotalOpen();
		asyncTotalOpen.setTitle(TaskCategory.TASK_BOX);
		asyncTotalOpen.execute();
	}
	
	/**
	 * method to display pop up while changing task assignee
	 * 
	 * @param taskid
	 * @param assignTo
	 * @param assignToNum
	 */
	public void displayChangeAssignee(final String taskid,
			final String assignTo, final String assignToNum,
			final TextView txtAssinorTask) {

		final DialogMenu dialog = new DialogMenu(this,
				R.layout.change_assignee_popup);
		dialog.show();

		
		final LinearLayout asigneeName = (LinearLayout) dialog
				.findViewById(R.id.asigneename);
		ImageView asigneeIcon = (ImageView) dialog
				.findViewById(R.id.asigneeicon);
		LinearLayout prevAssigneeList = (LinearLayout) dialog
				.findViewById(R.id.prevasigneeslist);
		Button doneBtn = (Button) dialog.findViewById(R.id.donebtn);
		// dialog.setCancelable(true);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(3, 3, 3, 3);
		final LinearLayout prevAssignee = new LinearLayout(context);
		prevAssignee.setGravity(Gravity.CENTER);
		prevAssignee.setLayoutParams(params);
		prevAssignee.setOrientation(LinearLayout.HORIZONTAL);
		prevAssignee.setPadding(10, 10, 10, 10);
		prevAssignee.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.asignee_name));

		final LinearLayout changedAssignee = new LinearLayout(context);
		changedAssignee.removeAllViews();

		changedAssignee.setGravity(Gravity.CENTER);
		changedAssignee.setLayoutParams(params);
		changedAssignee.setOrientation(LinearLayout.HORIZONTAL);
		changedAssignee.setPadding(10, 10, 10, 10);
		changedAssignee.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.asignee_name));
		// System.out.println("globalAssigneeName"+globalAssigneeName);

		TextView txtchangeAssignee = new TextView(context);

		if (globalAssigneeName != null) {
			if ((ApplicationUtil.getPreference(ApplicationConstant.regMobNo,
					context)) != null
					&& globalAssigneeNum != null
					&& (ApplicationUtil.getPreference(
							ApplicationConstant.regMobNo, context)).trim()
							.contains(globalAssigneeNum.trim()))
				txtchangeAssignee.setText(TaskCategory.TASK_SELF);
			else
				txtchangeAssignee.setText(globalAssigneeName);
			txtchangeAssignee.setTextColor(getResources().getColor(
					R.color.black));
			txtchangeAssignee.setLayoutParams(params);
			txtchangeAssignee.setPadding(0, 0, 5, 0);
			prevAssignee.addView(txtchangeAssignee);
			prevAssigneeList.addView(prevAssignee);
		}

		TextView assignee = new TextView(context);

		if (assignTo != null && !assignTo.equalsIgnoreCase("")
				&& !assignToNum.equalsIgnoreCase("")) {
			if ((ApplicationUtil.getPreference(ApplicationConstant.regMobNo,
					context) != null && assignToNum != null && ((ApplicationUtil
						.getPreference(ApplicationConstant.regMobNo, context))
					.trim().contains(assignToNum.trim()))))
				assignee.setText(TaskCategory.TASK_SELF);
			else
				assignee.setText(assignTo);
			assignee.setTextColor(getResources().getColor(R.color.black));
			assignee.setLayoutParams(params);
			assignee.setPadding(0, 0, 5, 0);
			changedAssignee.addView(assignee);

			asigneeName.addView(changedAssignee);
		}

		Button btnClose = (Button) dialog.findViewById(R.id.btnClose);

		btnClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		asigneeName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				asigneeName.removeAllViews();

				Intent intent = new Intent(AllTaskActivity.this,
						CustomeContactActivity.class);

				startActivityForResult(intent, 1);
				dialog.dismiss();
			}
		});
		asigneeIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				asigneeName.removeAllViews();
				Intent intent = new Intent(AllTaskActivity.this,
						CustomeContactActivity.class);

				startActivityForResult(intent, 1);
				dialog.dismiss();
			}
		});

		doneBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (changedAssigneeNum != null
						&& changedAssigneeNum.trim().length() > 0) {

					ChangeAssigneeDto changeAssigneeDto = new ChangeAssigneeDto();
					changeAssigneeDto.setTaskId(taskid);
					changeAssigneeDto.setAssign_To_No(CommonUtil
							.getValidMsisdn(changedAssigneeNum));
					changeAssigneeDto.setAssign_To_Name(changedAssigneeName);
					changeAssigneeDto.setIsAssigneeSync("N");
					changeAssigneeDto.setMobileNumber(ApplicationUtil
							.getPreference(ApplicationConstant.regMobNo,
									context));
					changeAssigneeDto.setOldAssignee(CommonUtil
							.getValidMsisdn(globalAssigneeNum));
					Map ch_map = new HashMap();
					ch_map.put("chdto", changeAssigneeDto);
					ch_map.put("changeAssigneeview", txtAssinorTask);

					new AsyncChangeAssignee().execute(ch_map);

					changedAssigneeName = "";
					changedAssigneeNum = "";
					// loadTaskList();
					dialog.dismiss();
				}
			}
		});
		dialog.show();
	}

	/**
	 * to open android phone book
	 */
	@SuppressLint("SimpleDateFormat")
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		switch (reqCode) {
		case 1:
			if (resultCode == Activity.RESULT_OK) {
				changedAssigneeNum = data.getStringExtra("changedAssigneeNum");
				changedAssigneeName = data
						.getStringExtra("changedAssigneeName");

				if (changedAssigneeNum != null
						&& changedAssigneeNum.length() > 0) {
					if (changedAssigneeNum.length() > 1) {
						long temp_changedAssigneeNum = Long
								.parseLong(changedAssigneeNum);
						AlertDialog.Builder ad = new AlertDialog.Builder(this);
						ad.setTitle(changedAssigneeName);

						ad.setItems(changeAssigneeNumbers,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										displayChangeAssignee(
												globalTaskId,
												changedAssigneeName,
												(ApplicationUtil
														.getValidNumber(changedAssigneeNum)),
												txtAssigorName);
									}
								});
						displayChangeAssignee(globalTaskId,
								changedAssigneeName,
								(ApplicationUtil
										.getValidNumber(changedAssigneeNum)),
								txtAssigorName);
					} else {
						displayChangeAssignee(globalTaskId,
								changedAssigneeName,
								(ApplicationUtil
										.getValidNumber(changedAssigneeNum)),
								txtAssigorName);
					}
				} else {
					displayChangeAssignee(globalTaskId, "", "", txtAssigorName);
					Toast.makeText(
							context,
							"Sorry!! Selected Contact does not contain any Number",
							Toast.LENGTH_LONG).show();
				}
				break;
			}
		case 2:
			if (resultCode==Activity.RESULT_OK||resultCode==123) {
				mHeaderLayout.setBackgroundResource(R.drawable.navigation_bar);
				boolean receiveAssignedFlag = data.getBooleanExtra( "taskcreated", false);
				sortVal =getResources().getString(R.string.open_task_status);

				if (receiveAssignedFlag) {
					mTabsLayout.setVisibility(View.VISIBLE);
					mAllTabLayout.performClick();
				}
			}
			break;
		case 3:
			if (resultCode == Activity.RESULT_OK || resultCode == 123) {
				new Asyntask().execute();
			}
			break;
		case 4:// CommentActivity
			if (resultCode == Activity.RESULT_OK && data != null) {
				MyApp.getInstance().commentButtonTextView
						.setCompoundDrawablesWithIntrinsicBounds(
								R.drawable.notefaction_check, 0, 0, 0);
				
				//TO refresh the UI task
				new Asyntask().execute();
			}
			break;
		case 5:// SnoozActivity
			if (resultCode == Activity.RESULT_OK && data != null) {
				String selectedAlarmDateTime = data
						.getStringExtra("reminderAlarmTime");
				final String taskId = data.getStringExtra("TaskId");
				final String taskDesc = data.getStringExtra("TaskDesc");

				if (selectedAlarmDateTime == null
						|| selectedAlarmDateTime.trim().isEmpty()) {
					return;
				}

				try {
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd/MM/yyyy hh:mm a");
					Date parsed = dateFormat.parse(selectedAlarmDateTime);
					dateFormat.applyPattern("dd/MM/yyyy HH:mm:ss");
					selectedAlarmDateTime = dateFormat.format(parsed);
				} catch (ParseException e) {
					e.printStackTrace();
				}

				final String alarmDateTime = selectedAlarmDateTime;

				ContentValues initialValues = new ContentValues();
				initialValues.put("isAlarmSet", "Y");
				initialValues.put("Alarm_Date_Time", alarmDateTime);
				initialValues.put("UPDATED_DATE", ApplicationUtil.getGMTDate());
				initialValues.put("UPDATION_DATE_TIME",
						ApplicationUtil.getGMTLong());

				ApplicationUtil.getInstance().updateDataInDB("Task", initialValues, context, "Task_ID ='" + taskId + "'", null);

				Thread thread = new Thread() {
					public void run() {
						if (alarmDateTime != null)
							AppUtility.setAlarmManager(AllTaskActivity.this, alarmDateTime, taskId, taskDesc);
					};
				};
				thread.start();
				
				//TO refresh the UI task
				new Asyntask().execute();
			}
			break;
		}
	}

	/**
	 * method to load task list on particular contact
	 * 
	 * @param name
	 */
	private void loadContactWiseTask(String Value, String ValueType) {
		junkFlag = false;
		totalUnreadCount = 0;
		ContactWiseDto c_dto = new ContactWiseDto();
		contactWiseType = ValueType;
		contactWiseTypeValue = Value;
		c_dto.setValue(Value);
		c_dto.setValueType(ValueType);

		String contactName = ApplicationUtil.getContactNameFromPhoneNo(
				AllTaskActivity.this, Value);
		createContactTaskList contactTaskList = new createContactTaskList();
		contactTaskList.setTitle(contactName);
		contactTaskList.execute(c_dto);
	}

	public void loadTaskByName(String name) {
		junkFlag = false;
		totalUnreadCount = 0;
		taskType = "byName";
		ContactWiseDto c_dto = new ContactWiseDto();
		contactWiseType = "byName";
		contactWiseTypeValue = name;
		c_dto.setValue(name);
		c_dto.setValueType(name);

		// String contactName =
		// ApplicationUtil.getContactNameFromPhoneNo(AllTaskActivity.this,Value);
		TaskByNameAsync contactTaskList = new TaskByNameAsync();
		contactTaskList.setTitle(name);
		contactTaskList.execute(c_dto);
	}
	
	public void hashTagListViewfilter(String hashTag){
		if(isPullRefreshInProgress){
			return;
		}
		updatedBackNavigativeStack(null, null, BackNavigationStackAction.CLEAR);
		mTabsLayout.setVisibility(View.GONE);
		mFocusNowHeaderCheckBox.setVisibility(View.GONE);
		
		sortBy="";
		contactSummery = "hasTask";
		mFireTabTextView.setText("0");
		mFavoriteTabTextView.setText("0");

		listview.setVisibility(View.GONE);
		sortVal = getResources().getString(R.string.open_task_status);

		taskType = "hashTask";
		junkFlag = false;
		isContactWise=true;

		mSideMenuSelectedLabels = "";
		
		loadContactWiseTask(hashTag, null, true);
	}
	
	private void loadContactWiseTask(String Value, String ValueType, boolean isHashTag) {
		junkFlag = false;
		totalUnreadCount = 0;
		ContactWiseDto c_dto = new ContactWiseDto();
		contactWiseType = ValueType;
		contactWiseTypeValue = Value;
		c_dto.setValue(Value);
		c_dto.setValueType(ValueType);

		createContactTaskList contactTaskList = new createContactTaskList();
		contactTaskList
				.setTitle(Value == null ? "" : Value.replaceAll("#", ""));
		contactTaskList.execute(c_dto);
	}

	/**
	 * method to expand task
	 * 
	 * @param iconList
	 * @param dividerLine
	 * @param taskSummaryExpanded
	 * @param arrow
	 * @param checkBox1
	 * @param checkBox2
	 */
	private void expandTask(final View iconList, final View dividerLine,
			final View arrow, final View checkBox1) {
		expand(iconList);
	}

	/**
	 * method to close expanded task
	 * 
	 * @param iconList
	 * @param dividerLine
	 * @param taskSummaryExpanded
	 * @param arrow
	 * @param checkBox1
	 * @param checkBox2
	 */
	protected void collapseTask(final View iconList, final View dividerLine,
			final View arrow, final View checkBox1) {
		collapse(iconList);

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
			}
		}, 750);
	}

	/**
	 * broadcast receiver to receive reloading of screen after any change
	 */
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				if (intent
						.getAction()
						.equals("com.taskmaganger.ui.AlltaskActivity.relaodList.broadcast")) {
					// System.out.println("Reload screen through broadcast called");
					totalUnread.setVisibility(View.VISIBLE);
					lineartotalUnreadButton.setVisibility(View.VISIBLE);
					totalUnread.setText("!");

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * method for expand animation
	 * 
	 * @param v
	 */
	public static void expand(final View v) {
		v.measure(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		final int targtetHeight = v.getMeasuredHeight();

		v.getLayoutParams().height = 0;
		v.setVisibility(View.VISIBLE);
		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				v.getLayoutParams().height = interpolatedTime == 1 ? LayoutParams.WRAP_CONTENT
						: (int) (targtetHeight * interpolatedTime);
				v.requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration(750);
		v.startAnimation(a);
	}

	/**
	 * method for collapse animation
	 * 
	 * @param v
	 */
	public static void collapse(final View v) {
		final int initialHeight = v.getMeasuredHeight();

		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				if (interpolatedTime == 1) {
					v.setVisibility(View.GONE);
				} else {
					v.getLayoutParams().height = initialHeight
							- (int) (initialHeight * interpolatedTime);
					v.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration(750);
		v.startAnimation(a);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("globalname", globalAssigneeName);
		outState.putString("globalnum", globalAssigneeNum);
		outState.putString("globalTaskId", globalTaskId);
		outState.putString("contactSummery", contactSummery);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		globalAssigneeName = savedInstanceState.getString("globalname");
		globalAssigneeNum = savedInstanceState.getString("globalnum");
		globalTaskId = savedInstanceState.getString("globalTaskId");
		contactSummery = savedInstanceState.getString("contactSummery");
	}

	/**
	 * Function used to fetch data from the phone contactlist
	 * 
	 * @param mContext
	 * @param assigntoNum
	 * @param assigntoName
	 * @return
	 */

	public String getContactNameFromPhoneNo(Context mContext,
			String assigntoNum, String assigntoName)//
	{
		System.gc();
		String contactName = null;
		if (assigntoNum != null && !assigntoNum.isEmpty()) {
			try {
				String assigntoNumWithoutReg = ApplicationUtil
						.getValidNumber(assigntoNum);
				if (assigntoNumWithoutReg != null) {

					ContentResolver localContentResolver = mContext
							.getContentResolver();
					Cursor contactLookupCursor = localContentResolver.query(Uri
							.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
									Uri.encode(assigntoNumWithoutReg)),
							new String[] { PhoneLookup.DISPLAY_NAME,
									PhoneLookup._ID }, null, null, null);
					try {
						while (contactLookupCursor.moveToNext()) {
							contactName = contactLookupCursor
									.getString(contactLookupCursor
											.getColumnIndexOrThrow(PhoneLookup.DISPLAY_NAME));
							String contactId = contactLookupCursor
									.getString(contactLookupCursor
											.getColumnIndexOrThrow(PhoneLookup._ID));
							if (contactName == null
									|| contactName.equalsIgnoreCase("")) {
								contactName = assigntoNumWithoutReg;
							}
						}
					} finally {
						contactLookupCursor.close();
					}
				}
				if (contactName == null && assigntoName != null
						&& !assigntoName.equalsIgnoreCase("")) {
					contactName = assigntoName;
				} else if (contactName == null && assigntoName != null
						&& assigntoName.equalsIgnoreCase("")) {
					contactName = assigntoNum;
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}

		} else {
			if (assigntoName != null && !assigntoName.isEmpty()) {
				contactName = assigntoName;
			} else if (contactName == null) {
				contactName = assigntoNum;
			}
		}
		{

		}

		return contactName;
	}

	private class Asyntask extends AsyncTask<Void, Void, Map> {
		private ProgressDialog dialog;
		private String title;

		public void setTitle(String title) {
			this.title = title;
		}

		public Asyntask() {
			dialog = new ProgressDialog(AllTaskActivity.this);
			dialog.setMessage(getResources().getString(R.string.task_progress));
			dialog.setCancelable(false);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.show();
			TaskList.setVisibility(View.GONE);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(Map result) {
			ArrayList<TaskInfoEntity> entities = new ArrayList<TaskInfoEntity>();
			entities = (ArrayList<TaskInfoEntity>) result.get("list");
			openTaskList = entities;
//			DisplayTaskListAdapter adpter = new DisplayTaskListAdapter(context, entities, list_view);
//			list_view.setAdapter(adpter);
			mDisplayTaskListAdapter = new DisplayTaskListAdapter(context, entities, null);
			updateListViewAdapter(mDisplayTaskListAdapter);

			setHeaderTitleCount(this.title, entities.size());
			mFireTabTextView.setText("" + result.get("fireCount"));
			mFavoriteTabTextView.setText("" + result.get("favCount"));
			if (entities != null && entities.isEmpty()) {
				noTaskMsgLayout.setVisibility(View.VISIBLE);
				listview.setVisibility(View.GONE);
				updateNoTaskIconAndText(true);
			} else {
				noTaskMsgLayout.setVisibility(View.GONE);
				listview.setVisibility(View.VISIBLE);
				updateNoTaskIconAndText(false);
			}
//			updatedBackNavigativeStack(list_view, mCountTextVeiw.getText() .toString(), BackNavigationStackAction.ADD);

			if (dialog != null) {
				dialog.dismiss();
			}
			pulldownDataState = "OPEN";
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mSideMenuSelectedLabels = getString(R.string.openTask);
			setHeaderTitleCount(this.title, 0);
		}

		@Override
		protected Map doInBackground(Void... params) {
			HashMap map = null;

			try {
				TaskEntity taskEntityObj = new TaskEntity();
				map = (HashMap) (taskEntityObj.getTaskListNew(
						isSelfTabSelected, isInTabSelected, isOutTabSelected, isFavoriteTabSelected, isFireTabSelected, sortVal,
						sortBy, context,isLater));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return map;
		}
	}

	private class PullDownLoadJunkList extends AsyncTask<Void, Void, Map> {

		public PullDownLoadJunkList() {
			isPullRefreshInProgress = true;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pulldownDataState = "JUNK";
		}

		@Override
		protected Map doInBackground(Void... params) {
			Map entities = new HashMap();
			TaskEntity taskEntityObj = new TaskEntity();
			totalUnreadCount = 0;
			try {
				entities = (HashMap) taskEntityObj.getJunkTaskList(
						isSelfTabSelected, isInTabSelected, isOutTabSelected,
						isFavoriteTabSelected, isFireTabSelected, sortBy,
						context);
				taskEntityObj = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return entities;
		}

		@Override
		protected void onPostExecute(Map result) {
			if (result != null) {
				ArrayList<TaskInfoEntity> entities = new ArrayList<TaskInfoEntity>();
				entities = (ArrayList<TaskInfoEntity>) result.get("list");
//				DisplayTaskListAdapter adpter = new DisplayTaskListAdapter( context, entities, list_view);
//				list_view.setAdapter(adpter);
				
				mDisplayTaskListAdapter = new DisplayTaskListAdapter( context, entities, null);
				updateListViewAdapter(mDisplayTaskListAdapter);
				
				mFireTabTextView.setText("" + result.get("fireCount"));
				mFavoriteTabTextView.setText("" + result.get("favCount"));
				if (entities != null && entities.isEmpty()) {
					noTaskMsgLayout.setVisibility(View.VISIBLE);
					listview.setVisibility(View.GONE);
					updateNoTaskIconAndText(true);
				} else {
					noTaskMsgLayout.setVisibility(View.GONE);
					listview.setVisibility(View.VISIBLE);
					updateNoTaskIconAndText(false);
				}

			}

			listview.onRefreshComplete();
//			list_view.completeRefreshing();
			isPullRefreshInProgress = false;
		}
	}

	private class PullDownAsyntask extends AsyncTask<Void, Void, Map> {
		public PullDownAsyntask() {
			isPullRefreshInProgress = true;
		}

		@Override
		protected void onPostExecute(Map result) {
			ArrayList<TaskInfoEntity> entities = new ArrayList<TaskInfoEntity>();
			entities = (ArrayList<TaskInfoEntity>) result.get("list");
			openTaskList = entities;
//			DisplayTaskListAdapter adpter = new DisplayTaskListAdapter(context, entities, list_view);
//			list_view.setAdapter(adpter);
			
			mDisplayTaskListAdapter = new DisplayTaskListAdapter(context, entities, null);
			updateListViewAdapter(mDisplayTaskListAdapter);
			
			mFireTabTextView.setText("" + result.get("fireCount"));
			mFavoriteTabTextView.setText("" + result.get("favCount"));
			if (entities != null && entities.isEmpty()) {
				noTaskMsgLayout.setVisibility(View.VISIBLE);
				listview.setVisibility(View.GONE);
				updateNoTaskIconAndText(true);
			} else {
				noTaskMsgLayout.setVisibility(View.GONE);
				listview.setVisibility(View.VISIBLE);
				updateNoTaskIconAndText(false);
			}

			listview.onRefreshComplete();
//			list_view.completeRefreshing();
			isPullRefreshInProgress = false;
		}

		@Override
		protected Map doInBackground(Void... params) {
			HashMap map = null;

			try {
				TaskEntity taskEntityObj = new TaskEntity();
				map = (HashMap) (taskEntityObj.getTaskListNew(
						isSelfTabSelected, isInTabSelected, isOutTabSelected,
						isFavoriteTabSelected, isFireTabSelected, sortVal,
						sortBy, context,isLater));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return map;
		}
	}

	public class PullDownAsyntaskClose extends AsyncTask<Void, Void, Map> {
		public PullDownAsyntaskClose() {
			isPullRefreshInProgress = true;
		}

		private ProgressDialog dialog;

		@Override
		protected void onPostExecute(Map result) {
			ArrayList<TaskInfoEntity> entities = new ArrayList<TaskInfoEntity>();
			if (result != null) {
				entities = (ArrayList<TaskInfoEntity>) result.get("list");
//				DisplayCloseTaskAdapter adapter = new DisplayCloseTaskAdapter( context, entities, list_view);
//				list_view.setAdapter(adapter);
				
				mDisplayCloseTaskAdapter = new DisplayCloseTaskAdapter( context, entities, null);
				updateListViewAdapter(mDisplayCloseTaskAdapter);
				
				mFireTabTextView.setText("" + result.get("fireCount"));
				mFavoriteTabTextView.setText("" + result.get("favCount"));
				if (entities != null && entities.isEmpty()) {
					noTaskMsgLayout.setVisibility(View.VISIBLE);
					listview.setVisibility(View.GONE);
					updateNoTaskIconAndText(true);
				} else {
					noTaskMsgLayout.setVisibility(View.GONE);
					listview.setVisibility(View.VISIBLE);
					updateNoTaskIconAndText(false);
				}
			}

			listview.onRefreshComplete();
//			list_view.completeRefreshing();
			isPullRefreshInProgress = false;
		}

		@Override
		protected Map doInBackground(Void... params) {
			HashMap map = null;
			try {
				TaskEntity taskEntityObj = new TaskEntity();
				map = (HashMap) (taskEntityObj.getCloseTaskList(
						isSelfTabSelected, isInTabSelected, isOutTabSelected,
						isFavoriteTabSelected, isFireTabSelected, sortBy,
						context));
			} catch (Exception e) {
				dialog.dismiss();
				e.printStackTrace();
			}
			return map;
		}
	}

	private class CreateContactWiseSummery extends
			AsyncTask<Void, Void, ArrayList<TaskInfoEntity>> {
		private ProgressDialog dialog;
		private String title;

		public void setTitle(String title) {
			this.title = title;
		}

		public CreateContactWiseSummery() {

			// loadTaskList();
			dialog = new ProgressDialog(AllTaskActivity.this);
			// progressUnread.setTitle("Please Wait!!");
			dialog.setMessage(getResources().getString(R.string.task_progress));
			dialog.setCancelable(false);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.show();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setHeaderTitleCount(title);
		}

		@Override
		protected ArrayList<TaskInfoEntity> doInBackground(Void... params) {
			ArrayList<TaskInfoEntity> entities = new ArrayList<TaskInfoEntity>();
			try {
				totalUnreadCount = 0;
				TaskEntity taskEntityObj = new TaskEntity();
				try {
					entities = (ArrayList<TaskInfoEntity>) taskEntityObj
							.getContactWiseList(taskType, sortVal, context);
					favCount = 0;
					fireCount = 0;
					noOfFavourite = favCount + "";
					noOfFire = fireCount + "";
				} catch (Exception e) {
					e.printStackTrace();
				}
				taskEntityObj = null; // System.out.println("task LIST----->" +
										// entities);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return entities;
		}

		@Override
		protected void onPostExecute(ArrayList<TaskInfoEntity> result) {
			if (dialog != null) {
				dialog.dismiss();
			}

			if (result != null && result.size() > 0) {

				TaskList.setVisibility(View.VISIBLE);
				noTaskMsgLayout.setVisibility(View.GONE);
				updateNoTaskIconAndText(false);

				setHeaderTitleCount(title, result.size());
				updatedBackNavigativeStack(TaskList, mCountTextVeiw.getText()
						.toString(), BackNavigationStackAction.ADD);
				createContactWiseList(result);
			} else {
				noTaskMsgLayout.setVisibility(View.VISIBLE);
				TaskList.setVisibility(View.GONE);
				updateNoTaskIconAndText(true);
			}
		}
	}
	// Hash wise search
	private class CreateHasWiseSummery extends
			AsyncTask<Void, Void, ArrayList<NotationDto>> {
		private ProgressDialog dialog;
		private String title;

		public void setTitle(String title) {
			this.title = title;
		}

		public CreateHasWiseSummery() {
			// loadTaskList();
			dialog = new ProgressDialog(AllTaskActivity.this);
			// progressUnread.setTitle("Please Wait!!");
			dialog.setMessage(getResources().getString(R.string.task_progress));
			dialog.setCancelable(false);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.show();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setHeaderTitleCount(this.title, 0);
		}

		@Override
		protected ArrayList<NotationDto> doInBackground(Void... params) {
			ArrayList<NotationDto> entities = new ArrayList<NotationDto>();
			try {
				totalUnreadCount = 0;
				TaskEntity taskEntityObj = new TaskEntity();
				try {

					entities = (ArrayList<NotationDto>) taskEntityObj
							.getHasWiseGroup(context);
					favCount = 0;
					fireCount = 0;
					noOfFavourite = favCount + "";
					noOfFire = fireCount + "";

				} catch (Exception e) {
					e.printStackTrace();
				}
				taskEntityObj = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return entities;
		}

		@Override
		protected void onPostExecute(ArrayList<NotationDto> result) {
			if (dialog != null) {
				dialog.dismiss();
			}

			if (result != null && result.size() > 0) {
				TaskList.setVisibility(View.VISIBLE);
				noTaskMsgLayout.setVisibility(View.GONE);
				updateNoTaskIconAndText(false);

				setHeaderTitleCount(title, result.size());
				updatedBackNavigativeStack(TaskList, mCountTextVeiw.getText() .toString(), BackNavigationStackAction.ADD);
				createHashWiseList(result);
			} else {
				noTaskMsgLayout.setVisibility(View.VISIBLE);
				TaskList.setVisibility(View.GONE);
				setHeaderTitleCount(this.title, 0);
				updateNoTaskIconAndText(true);
//				CommonUtilsUi .getCustomeDialog( AllTaskActivity.this, "\n Now you categorize  tasks by using #(keyword) in the task message.\nFor example use #personal to categorize personal tasks and #projectName to categorize tasks related to a particular project.");
			}
		}
	}

	private class LoadJunkList extends AsyncTask<Void, Void, Map> {
		private String status;
		private ProgressDialog dialog;
		private String title;

		public void setTitle(String title) {
			this.title = title;
		}

		public LoadJunkList(String status) {
			this.status = status;
			dialog = new ProgressDialog(AllTaskActivity.this);
			dialog.setMessage(getResources().getString(R.string.task_progress));
			dialog.setCancelable(false);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.show();
			TaskList.setVisibility(View.GONE);
		}

		public LoadJunkList(boolean isTotal) {
			dialog = new ProgressDialog(AllTaskActivity.this);
			// progressUnread.setTitle("Please Wait!!");
			dialog.setMessage(getResources().getString(R.string.task_progress));
			dialog.setCancelable(false);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.show();
			TaskList.setVisibility(View.GONE);
		}

		public LoadJunkList() {
			dialog = new ProgressDialog(AllTaskActivity.this);
			// progressUnread.setTitle("Please Wait!!");
			dialog.setMessage(getResources().getString(R.string.task_progress));
			dialog.setCancelable(false);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.show();
			TaskList.setVisibility(View.GONE);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pulldownDataState = "JUNK";
			setHeaderTitleCount(this.title, 0);
		}

		@Override
		protected Map doInBackground(Void... params) {
			Map entities = new HashMap();
			TaskEntity taskEntityObj = new TaskEntity();
			totalUnreadCount = 0;
			try {

				entities = (HashMap) taskEntityObj.getJunkTaskList(
						isSelfTabSelected, isInTabSelected, isOutTabSelected,
						isFavoriteTabSelected, isFireTabSelected, sortBy,
						context);
				taskEntityObj = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return entities;
		}

		@Override
		protected void onPostExecute(Map result) {
			if (result != null) {
				ArrayList<TaskInfoEntity> entities = new ArrayList<TaskInfoEntity>();
				entities = (ArrayList<TaskInfoEntity>) result.get("list");
//				DisplayTaskListAdapter adpter = new DisplayTaskListAdapter( context, entities, list_view);
//				list_view.setAdapter(adpter);
				
				mDisplayTaskListAdapter = new DisplayTaskListAdapter( context, entities, null);
				updateListViewAdapter(mDisplayTaskListAdapter);

				setHeaderTitleCount(this.title, entities.size());
				mFireTabTextView.setText("" + result.get("fireCount"));
				mFavoriteTabTextView.setText("" + result.get("favCount"));
				if (entities != null && entities.isEmpty()) {
					noTaskMsgLayout.setVisibility(View.VISIBLE);
					listview.setVisibility(View.GONE);
					updateNoTaskIconAndText(true);
				} else {
					noTaskMsgLayout.setVisibility(View.GONE);
					listview.setVisibility(View.VISIBLE);
					updateNoTaskIconAndText(false);
				}

			}
			if (dialog != null) {
				dialog.dismiss();
			}
		}
	}

	private class createContactTaskList extends
			AsyncTask<ContactWiseDto, Void, Map> {
		private ProgressDialog dialog;
		private String title;

		public void setTitle(String title) {
			this.title = title;
		}

		public createContactTaskList() {
			dialog = new ProgressDialog(AllTaskActivity.this);
			// progressUnread.setTitle("Please Wait!!");
			dialog.setMessage(getResources().getString(R.string.task_progress));
			dialog.setCancelable(false);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.show();
			TaskList.setVisibility(View.GONE);
		}

		@Override
		protected void onPostExecute(Map result) {
			if (result != null) {
				ArrayList<TaskInfoEntity> entities = new ArrayList<TaskInfoEntity>();
				entities = (ArrayList<TaskInfoEntity>) result.get("list");
//				DisplayTaskListAdapter adpter = new DisplayTaskListAdapter( context, entities, list_view);
//				list_view.setAdapter(adpter);
				
				mDisplayTaskListAdapter = new DisplayTaskListAdapter(context, entities, null);
				updateListViewAdapter(mDisplayTaskListAdapter);
				
				mFireTabTextView.setText("" + result.get("fireCount"));
				mFavoriteTabTextView.setText("" + result.get("favCount"));

				if (entities != null && entities.isEmpty()) {
					noTaskMsgLayout.setVisibility(View.VISIBLE);
					listview.setVisibility(View.GONE);
					setHeaderTitleCount(title, 0);
					updateNoTaskIconAndText(true);
				} else {
					noTaskMsgLayout.setVisibility(View.GONE);
					listview.setVisibility(View.VISIBLE);
					setHeaderTitleCount(title, entities.size());
					updateNoTaskIconAndText(false);
				}
				updatedBackNavigativeStack(listview, mCountTextVeiw .getText().toString(), BackNavigationStackAction.ADD);

			}
			if (dialog != null) {
				dialog.dismiss();
			}
		}

		@Override
		protected void onPreExecute() {
			try {
				// totalUnread.setText(""+totalOpen() );
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			super.onPreExecute();
		}

		@Override
		protected Map doInBackground(ContactWiseDto... params) {
			ContactWiseDto contact = params[0];
			TaskEntity taskEntityObj = new TaskEntity();
			HashMap entities = new HashMap();
			try {
				entities = (HashMap) taskEntityObj.getContactWiseTasks(
						taskType, sortVal, contactWiseTypeValue,
						contactWiseType, isFavoriteTabSelected,
						isFireTabSelected, context);
				favCount = 0;
				fireCount = 0;
				// alarmCount = 0;
				// noOfAlarm = alarmCount+"";
				noOfFavourite = favCount + "";
				noOfFire = fireCount + "";
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return entities;
		}
	}

	private class TaskByNameAsync extends AsyncTask<ContactWiseDto, Void, Map> {
		private ProgressDialog dialog;
		private String title;

		public void setTitle(String title) {
			this.title = title;
		}

		public TaskByNameAsync() {
			dialog = new ProgressDialog(AllTaskActivity.this);
			// progressUnread.setTitle("Please Wait!!");
			dialog.setMessage(getResources().getString(R.string.task_progress));
			dialog.setCancelable(false);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.show();
			TaskList.setVisibility(View.GONE);
		}

		@Override
		protected void onPostExecute(Map result) {
			if (result != null) {

				noTaskMsgLayout.setVisibility(View.GONE);
				listview.setVisibility(View.VISIBLE);
				ArrayList<TaskInfoEntity> entities = new ArrayList<TaskInfoEntity>();
				entities = (ArrayList<TaskInfoEntity>) result.get("list");
//				DisplayTaskListAdapter adpter = new DisplayTaskListAdapter( context, entities, list_view);
//				list_view.setAdapter(adpter);
				
				mDisplayTaskListAdapter = new DisplayTaskListAdapter( context, entities, null);
				updateListViewAdapter(mDisplayTaskListAdapter);
				
				mFireTabTextView.setText("" + result.get("fireCount"));
				mFavoriteTabTextView.setText("" + result.get("favCount"));

				setHeaderTitleCount(title, entities.size());
				updatedBackNavigativeStack(listview, mCountTextVeiw.getText()
						.toString(), BackNavigationStackAction.ADD);

			}
			if (dialog != null) {
				dialog.dismiss();
			}
		}

		@Override
		protected Map doInBackground(ContactWiseDto... params) {
			// TODO Auto-generated method stub
			ContactWiseDto contact = params[0];
			TaskEntity taskEntityObj = new TaskEntity();
			HashMap entities = new HashMap();
			try {
				entities = (HashMap) taskEntityObj.getContactWiseTasks(
						taskType, sortVal, contactWiseTypeValue,
						contactWiseType, isFavoriteTabSelected,
						isFireTabSelected, context);
				favCount = 0;
				fireCount = 0;
				// alarmCount = 0;
				// noOfAlarm = alarmCount+"";
				noOfFavourite = favCount + "";
				noOfFire = fireCount + "";
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return entities;
		}
	}

	private class AsyncChangeAssignee extends AsyncTask<Map, Void, Map> {
		private ProgressDialog dialog;

		@Override
		protected Map doInBackground(Map... params) {
			Map map = params[0];
			ChangeAssigneeDto dtoChangeAssignee = (ChangeAssigneeDto) map
					.get("chdto");
			ContentValues initialValues = new ContentValues();
			initialValues.put("Assign_To", dtoChangeAssignee.getAssign_To_No());
			initialValues.put("Assign_To_Name",
					dtoChangeAssignee.getAssign_To_Name());
			initialValues.put("IsTaskRead", "N");
			initialValues.put("UPDATED_DATE", ApplicationUtil.getGMTDate());
			if (TaskCategory.TASK_SELF.equalsIgnoreCase(dtoChangeAssignee.getTaskType())) {
				initialValues.put("TaskType", "self");
			} else {
				initialValues.put("TaskType", "sent");

			}

			initialValues.put("UPDATED_DATE", ApplicationUtil.getGMTDate());
			initialValues.put("UPDATION_DATE_TIME",
					ApplicationUtil.getGMTLong());
			initialValues
					.put("oldAssignee", dtoChangeAssignee.getOldAssignee());
			ApplicationUtil.getInstance().updateDataInDB("Task", initialValues,
					context,
					"Task_ID ='" + dtoChangeAssignee.getTaskId() + "'", null);

			SyncModule sync = new SyncModule(context);
			ChangeAssigneeResponseDto responseDto;
			try {

				if (ApplicationUtil.checkInternetConn(context)) {
					responseDto = sync.changeAssignee(dtoChangeAssignee);
					map.put("status", responseDto.getStatus());
					// map.put("messageId", responseDto.getMessageId());
				} else {
					initialValues.put("IsAssigneeSync", "N");
					initialValues.put("IsTaskSync", "N");
					ApplicationUtil.getInstance().updateDataInDB("Task",
							initialValues, context,
							"Task_ID ='" + dtoChangeAssignee.getTaskId() + "'",
							null);
					initialValues.put("IsTaskRead", "N");
					map.put("status", "05");
				}
			} catch (Exception e) {
				dialog.dismiss();
				e.printStackTrace();
			}

			return map;
		}

		@Override
		protected void onPostExecute(Map result) {
			String status = (String) result.get("status");
			final ChangeAssigneeDto dtoChangeAssignee = (ChangeAssigneeDto) result
					.get("chdto");
			TextView txtAssigor = (TextView) result.get("changeAssigneeview");
			if ("00".equalsIgnoreCase(status)) {

				// msgSyncStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.notefaction_check));
				if (txtAssigor != null)
					txtAssigor.setText(dtoChangeAssignee.getAssign_To_Name());
			} else {
				if (txtAssigor != null)
					txtAssigor.setText(dtoChangeAssignee.getAssign_To_Name());
			}

			if (dialog != null) {
				dialog.dismiss();
			}

			new Asyntask().execute();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(AllTaskActivity.this);
			// progressUnread.setTitle("Please Wait!!");
			dialog.setMessage(getResources().getString(R.string.task_progress));
			dialog.setCancelable(false);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.show();

			super.onPreExecute();
		}
	}

	public void callAsynchronousTask() {
		// final Handler handler = new Handler();
		timer = new Timer();
		TimerTask doAsynchronousTask = new TimerTask() {
			@Override
			public void run() {
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							Thread thread = new Thread(syncUtils);
							thread.start();
						} catch (Exception e) {
						}

					}
				}).start();

			}
		};
		timer.schedule(doAsynchronousTask, 0, 60000);

	}

	public class AsyntaskClose extends AsyncTask<Void, Void, Map> {
		private ProgressDialog dialog;
		private String status;
		private String title;

		public void setTitle(String title) {
			this.title = title;
		}

		public AsyntaskClose() {
			dialog = new ProgressDialog(AllTaskActivity.this);
			dialog.setMessage(getResources().getString(R.string.task_progress));
			dialog.setCancelable(false);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.show();
			TaskList.setVisibility(View.GONE);

		}

		public AsyntaskClose(boolean isToatl) {
			dialog = new ProgressDialog(AllTaskActivity.this);
			dialog.setMessage(getResources().getString(R.string.task_progress));
			dialog.setCancelable(false);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.show();
			TaskList.setVisibility(View.GONE);
		}

		public AsyntaskClose(String status) {
			this.status = status;
			dialog = new ProgressDialog(AllTaskActivity.this);
			dialog.setMessage(getResources().getString(R.string.task_progress));
			dialog.setCancelable(false);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.show();
			TaskList.setVisibility(View.GONE);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setHeaderTitleCount(this.title, 0);
		}

		@Override
		protected void onPostExecute(Map result) {
			ArrayList<TaskInfoEntity> entities = new ArrayList<TaskInfoEntity>();
			if (result != null) {
				entities = (ArrayList<TaskInfoEntity>) result.get("list");
//				DisplayCloseTaskAdapter adapter = new DisplayCloseTaskAdapter( context, entities, list_view);
//				list_view.setAdapter(adapter);
				
				mDisplayCloseTaskAdapter = new DisplayCloseTaskAdapter( context, entities, null);
				updateListViewAdapter(mDisplayCloseTaskAdapter);

				setHeaderTitleCount(this.title, entities.size());
				mFireTabTextView.setText("" + result.get("fireCount"));
				mFavoriteTabTextView.setText("" + result.get("favCount"));
				if (entities != null && entities.isEmpty()) {
					noTaskMsgLayout.setVisibility(View.VISIBLE);
					listview.setVisibility(View.GONE);
					updateNoTaskIconAndText(true);
				} else {
					noTaskMsgLayout.setVisibility(View.GONE);
					listview.setVisibility(View.VISIBLE);
					updateNoTaskIconAndText(false);
				}
			}

			if (dialog != null) {
				dialog.dismiss();
			}
			pulldownDataState = "CLOSE";
			super.onPostExecute(result);
		}

		@Override
		protected Map doInBackground(Void... params) {
			HashMap map = null;
			try {
				TaskEntity taskEntityObj = new TaskEntity();
				map = (HashMap) (taskEntityObj.getCloseTaskList(
						isSelfTabSelected, isInTabSelected, isOutTabSelected,
						isFavoriteTabSelected, isFireTabSelected, sortBy,
						context));
			} catch (Exception e) {
				dialog.dismiss();
				e.printStackTrace();
			}
			return map;
		}
	}

	public class AsyncTotalOpen extends AsyncTask<String, String, Map> {
		private ProgressDialog progressUnread_new = null;
		private String title;

		public void setTitle(String title) {
			this.title = title;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setHeaderTitleCount(this.title, 0);

			progressUnread_new = new ProgressDialog(AllTaskActivity.this);
			progressUnread_new.setMessage(getResources().getString(
					R.string.task_progress));
			progressUnread_new.setCancelable(false);
			progressUnread_new.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressUnread_new.show();
		}

		@Override
		protected Map doInBackground(String... params) {
			String curentMsisdn = ApplicationUtil.getPreference(
					ApplicationConstant.regMobNo, context);
			HashMap map = null;

			TaskEntity taskEntityObj = new TaskEntity();

			try {
				map = ((HashMap) taskEntityObj.getTaskListNew(
						isSelfTabSelected, isInTabSelected, isOutTabSelected,
						isFavoriteTabSelected, isFireTabSelected, sortVal,
						sortBy, context,isLater));
			} catch (Exception e) {
				e.printStackTrace();
			}

			return map;
		}

		@Override
		protected void onPostExecute(Map result) {
			ArrayList<TaskInfoEntity> entities = new ArrayList<TaskInfoEntity>();
			entities = (ArrayList<TaskInfoEntity>) result.get("list");
			openTaskList = entities;
//			actualListView.setAdapter(adpter);
//			DisplayTaskListAdapter adpter = new DisplayTaskListAdapter(context, entities, list_view);
//			list_view.setAdapter(adpter);
			
			mDisplayTaskListAdapter = new DisplayTaskListAdapter(context, entities, null);
			updateListViewAdapter(mDisplayTaskListAdapter);
			
			setHeaderTitleCount(this.title, entities.size());
			mFireTabTextView.setText("" + result.get("fireCount"));
			mFavoriteTabTextView.setText("" + result.get("favCount"));

			TaskList.setVisibility(View.GONE);
			contactSummery = "not";
			sortVal = "OPEN";
			if (entities != null && entities.isEmpty()) {
				noTaskMsgLayout.setVisibility(View.VISIBLE);
				listview.setVisibility(View.GONE);
				updateNoTaskIconAndText(true);
			} else {
				noTaskMsgLayout.setVisibility(View.GONE);
				listview.setVisibility(View.VISIBLE);
				updateNoTaskIconAndText(false);
			}

			if (ApplicationUtil.checkInternetConn(context)) {
				ApplicationConstant.message_Task_Flag_run_handle = 0;
				ApplicationConstant.createTask_Flag_run_handle = 0;
			}

			noOfFavourite = favCount + "";
			noOfFire = fireCount + "";

			lineartotalUnreadButton.setVisibility(View.INVISIBLE);
			if (progressUnread_new != null) {
				progressUnread_new.dismiss();
			}
			pulldownDataState = "OPEN";
			// list_view.onRefreshComplete();
		}

	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(
					CommonUtil.EXTRA_MESSAGE);

			/**
			 * Take appropriate action on this message depending upon your app
			 * requirement For now i am just displaying it on the screen
			 * */

			// Showing received message
			// lblMessage.append(newMessage + "\n");
			Toast.makeText(getApplicationContext(),
					"New Message: " + newMessage, Toast.LENGTH_LONG).show();

			// Releasing wake lock
			// WakeLocker.release();
		}
	};

	@Override
	protected void onDestroy() {
		try {
			if (mHandleMessageReceiver != null)
				unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		if(mHeaderSearchLayout.getVisibility() == View.VISIBLE){
			mHeaderSearchBackButton.performClick();
			return;
		}
		
		if (mBackNavigationStack.size() > STACK_MIN_COUNT) {
			updatedBackNavigativeStack(null, null, BackNavigationStackAction.REMOVE);
			return;
		}
		else if(mSideMenuSelectedLabels != getString(R.string.openTask)){
			mSideMenuSelectedLabels = getString(R.string.openTask);//"Next back step to close the app.";
			clickOnAllTabButton(mAllTabLayout);
			return;
		}
		finish();
	}

	private void showAppExitDialog() {
		final DialogMenu dialog = new DialogMenu(context,
				R.layout.custome_confirm_alert_view);
		dialog.show();

		Button okButton = (Button) dialog.findViewById(R.id.okButton);
		Button cancel = (Button) dialog.findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();

			}
		});
		TextView alertText = (TextView) dialog.findViewById(R.id.alertText);
		alertText.setText("Are you sure you want to close App?");
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AllTaskActivity.this.finish();
			}
		});
	}

	public void editContentPopup(final String taskId,
			final String taskDescription, final String targetDate,
			final TextView taskSummaryTxtView, final TaskInfoEntity entity) {
		final DialogMenu dialog = new DialogMenu(this, R.layout.edittaskview);
		dialog.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE); dialog.show();
		
		String msgTo = "";
		 messageEdit = (EditCustomAutoCompleteView) dialog.findViewById(R.id.addtaskedit);
		 messageEdit.setDropDownBackgroundResource(R.drawable.bg_drop_down);

		editAutoCompletiTem = new String[] {};
		// set our adapter
		editAdapter = new HashTaskDropDownAdapter(this, Arrays.asList(editAutoCompletiTem));

		messageEdit.setAdapter(editAdapter);
		messageEdit.addTextChangedListener(new EditTextChangedListener(AllTaskActivity.this));
		messageEdit.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					// Perform action on key press
					return true;
				}
				return false;
			}
		});
		messageEdit.setText(taskDescription);
		messageEdit.setSelection(messageEdit.length());

		TextView updateButton = (TextView) dialog
				.findViewById(R.id.UpdateButton);
		dialog.show();

		updateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (messageEdit.getText().toString().trim().length() == 0) {
					Toast.makeText(
							context,
							getResources().getString(
									R.string.blank_task_desc_toast),
							Toast.LENGTH_LONG).show();
					return;
				}

				SyncModule syncObj = new SyncModule(context);
				syncObj.updateTaskContent(taskId, messageEdit.getText()
						.toString());
				
				taskSummaryTxtView.setText(messageEdit.getText().toString());

				entity.setTask_description(messageEdit.getText().toString());
				dialog.dismiss();
			}
		});
	}

	public void deleteCalendarEvent(ContentResolver resolver, Uri eventsUri,
			String title) {
		Cursor cursor;

		String selection = "title = ?";
		String[] selectionArgs = new String[] { title };

		if (android.os.Build.VERSION.SDK_INT <= 7) {
			cursor = resolver.query(eventsUri, null, selection, selectionArgs,
					null);
		} else {
			cursor = resolver.query(eventsUri, null, selection, selectionArgs,
					null);
			// cursor = resolver.query(eventsUri, new String[]{ "_id" },
			// "calendar_id=" + calendarId, null, null);
		}
		while (cursor.moveToNext()) {
			long eventId = cursor.getLong(cursor.getColumnIndex("_id"));
			resolver.delete(ContentUris.withAppendedId(eventsUri, eventId),
					null, null);
		}
		cursor.close();
	}

	public void hideTabBarPanel() {
		updatedBackNavigativeStack(null, null, BackNavigationStackAction.CLEAR);
		mTabsLayout.setVisibility(View.GONE);
		mFocusNowHeaderCheckBox.setVisibility(View.GONE);
	}
	
	private void updateNoTaskIconAndText(boolean isEmpty){
		if(isEmpty){
			int icon = getNoTaskIcon();
			String[] text = getNoTaskText();

			mNoTaskImageView.setImageResource(icon);
			mNoTaskTitleTextView.setText(text[0]);
			mNoTaskDescriptionTextView.setText(text[1]);
		}else {
			mMainLayout.setBackgroundColor(Color.parseColor("#00000000"));
		}
	}
	
	private int getNoTaskIcon(){
		int icon = 0;

		if(headerTitle == null || headerTitle.equalsIgnoreCase(TaskCategory.TASK_BOX)){
			icon = R.drawable.bg_no_task_box;
			mMainLayout.setBackgroundColor(Color.parseColor("#646464"));
		}
		else if(headerTitle.equalsIgnoreCase(TaskCategory.TASK_DONE)){
			icon = R.drawable.bg_no_task_done;
//			mMainLayout.setBackgroundColor(Color.parseColor("#33ac71"));
			mMainLayout.setBackgroundColor(Color.parseColor("#646464"));
		}
		else if(headerTitle.equalsIgnoreCase(TaskCategory.TASK_LATER)){
			icon = R.drawable.bg_no_task_snooz;
//			mMainLayout.setBackgroundColor(Color.parseColor("#fbc02d"));
			mMainLayout.setBackgroundColor(Color.parseColor("#646464"));
		}
		else if(headerTitle.equalsIgnoreCase(TaskCategory.TASK_SPAM)){
			icon = R.drawable.bg_no_task_spam;
			mMainLayout.setBackgroundColor(Color.parseColor("#646464"));
		}
		else if(headerTitle.equalsIgnoreCase(TaskCategory.TASK_DELAYED)){
			icon = R.drawable.bg_no_task_deleyed;
//			mMainLayout.setBackgroundColor(Color.parseColor("#ea928b"));
			mMainLayout.setBackgroundColor(Color.parseColor("#646464"));
		}
		else if(headerTitle.equalsIgnoreCase(TaskCategory.TASK_TODAY) || headerTitle.equalsIgnoreCase(TaskCategory.TASK_TOMORROW)
				|| headerTitle.equalsIgnoreCase(TaskCategory.TASK_NEXT_7_DAY)){
			icon = R.drawable.bg_no_task_calender;
//			mMainLayout.setBackgroundColor(Color.parseColor("#99d6f1"));
			mMainLayout.setBackgroundColor(Color.parseColor("#646464"));
		}
		else if(headerTitle.equalsIgnoreCase(TaskCategory.TASK_RECEIVED) || headerTitle.equalsIgnoreCase(TaskCategory.TASK_ASSIGNED)){
			if(mTabsLayout.getVisibility() == View.VISIBLE){
				icon = R.drawable.bg_no_task_box;
//				mMainLayout.setBackgroundColor(Color.parseColor("#646464"));
				mMainLayout.setBackgroundColor(Color.parseColor("#646464"));
				
			} else {
				icon = R.drawable.bg_no_task_assigny;
//				mMainLayout.setBackgroundColor(Color.parseColor("#99d6f1"));
				mMainLayout.setBackgroundColor(Color.parseColor("#646464"));
			}
		}
		else if(headerTitle.equalsIgnoreCase(TaskCategory.TASK_CATEGORIES)){
			icon = R.drawable.bg_no_task_category;
//			mMainLayout.setBackgroundColor(Color.parseColor("#99d6f1"));
			mMainLayout.setBackgroundColor(Color.parseColor("#646464"));
		}
		else if(headerTitle.equalsIgnoreCase(TaskCategory.TASK_URGENT)){
			icon = R.drawable.bg_no_task_urgency;
			mMainLayout.setBackgroundColor(Color.parseColor("#646464"));
		}
		else {//TaskBox
			icon = R.drawable.bg_no_task_box;
			mMainLayout.setBackgroundColor(Color.parseColor("#646464"));
		}
		return icon;
	}
	
	private String[] getNoTaskText(){
		String text[] = new String[2];;

		if(headerTitle == null || headerTitle.equalsIgnoreCase(TaskCategory.TASK_BOX)){
			text[0] = "Nothing in Task box";
			text[1] = "There is no task in your task box.";
		}
		else if(headerTitle.equalsIgnoreCase(TaskCategory.TASK_LATER)){
			text[0] = "Nothing in Later box";
			text[1] = "Any task moved to Later box will appear here. Low priority tasks can be moved to Later box by left swipe or via action grid.";
		}
		else if(headerTitle.equalsIgnoreCase(TaskCategory.TASK_CATEGORIES)){
			text[0] = "No categorized task";
			text[1] = "List of categories appear here. Tasks can be categorized by using # while creating/editing a task.";
		}
		else if(headerTitle.equalsIgnoreCase(TaskCategory.TASK_ASSIGNED)){
			if(mTabsLayout.getVisibility() == View.VISIBLE){
				text[0] = "No Assigned task";
				text[1] = "No task has been assigned to any one. While creating a task use @ to select the contact for task assignment.";
			} else {
				text[0] = "No assigned task";
				text[1] = "List of contacts to whom you assigned task appear here.";
			}
		}
		else if(headerTitle.equalsIgnoreCase(TaskCategory.TASK_RECEIVED)){
			if(mTabsLayout.getVisibility() == View.VISIBLE){
				text[0] = "No Received task";
				text[1] = "No task has been received from anyone.";
			} else {
				text[0] = "No received task";
				text[1] = "List of contacts who assigned task appear here.";
			}
		}
		else if(headerTitle.equalsIgnoreCase(TaskCategory.TASK_NEXT_7_DAY)){
			text[0] = "No task in next 7 days";
			text[1] = "Tasks with Due Date with in next 7 days appear here.";
		}
		else if(headerTitle.equalsIgnoreCase(TaskCategory.TASK_TOMORROW)){
			text[0] = "No task for tomorrow";
			text[1] = "Tasks with Due Date as tomorrow appear here.";
		}
		else if(headerTitle.equalsIgnoreCase(TaskCategory.TASK_TODAY)){
			text[0] = "No task for today";
			text[1] = "Tasks with Due Date as today appear here.";
		}
		else if(headerTitle.equalsIgnoreCase(TaskCategory.TASK_DELAYED)){
			text[0] = "No Delayed task";
			text[1] = "Tasks with lapsed Due Date appear here.";
		}
		else if(headerTitle.equalsIgnoreCase(TaskCategory.TASK_SPAM)){
			text[0] = "Nothing in Spam box";
			text[1] = "Tasks received from mobile numbers not in your contact book appears here.";
		}
		else if(headerTitle.equalsIgnoreCase(TaskCategory.TASK_DONE)){
			text[0] = "Nothing in Done box";
			text[1] = "Any task marked as Done will appear here. Task can be marked Done by right swipe or via action grid.";
		}
		else if(headerTitle.equalsIgnoreCase(TaskCategory.TASK_SELF)){
			text[0] = "No Self task";
			text[1] = "No self task created by you.";
		}
		else if(headerTitle.equalsIgnoreCase(TaskCategory.TASK_FOCUS_NOW)){
			text[0] = "No Focus now task";
			text[1] = "No task has been pinned to immediately focus upon. Task can be pinned by clicking pin icon.";
		}
		else if(headerTitle.equalsIgnoreCase(TaskCategory.TASK_URGENT)){
			text[0] = "No urgent task";
			text[1] = "No task has been marked as urgent one. Task can be marked urgent by clicking fire icon.";
		}
		else {//It will work sub category
			text[0] = "No task available";
			text[1] = "Tasks can be categorized by using # while creating/editing a task.";
		}
		return text;
	}
	
	public interface TaskCategory {
		String TASK_BOX = "Task Box";
		String TASK_DONE = "Done";
		String TASK_LATER = "Later";
		String TASK_SPAM = "Spam";
		String TASK_DELAYED = "Delayed";
		String TASK_TODAY = "Today";
		String TASK_TOMORROW = "Tomorrow";
		String TASK_NEXT_7_DAY = "Next 7 Days";
		String TASK_RECEIVED = "Received";
		String TASK_ASSIGNED = "Assigned";
		String TASK_CATEGORIES = "Categories";
		String TASK_SELF = "Self";
		String TASK_FOCUS_NOW = "Focus now";
		String TASK_URGENT = "Urgent";
	}
	
	public class TaskForFilteringAsyncTask extends AsyncTask<String, String, Map> {
		private ProgressDialog progressUnread_new = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressUnread_new = new ProgressDialog(AllTaskActivity.this);
			progressUnread_new.setMessage(getResources().getString( R.string.task_progress));
			progressUnread_new.setCancelable(false);
			progressUnread_new.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressUnread_new.show();
		}

		@Override
		protected Map doInBackground(String... params) {
			String curentMsisdn = ApplicationUtil.getPreference(ApplicationConstant.regMobNo, context);
			HashMap map = null;

			TaskEntity taskEntityObj = new TaskEntity();

			try {
				map = ((HashMap) taskEntityObj.getTaskListNew(
						isSelfTabSelected, isInTabSelected, isOutTabSelected,
						isFavoriteTabSelected, isFireTabSelected, sortVal,
						sortBy, context,isLater));
			} catch (Exception e) {
				e.printStackTrace();
			}

			return map;
		}

		@Override
		protected void onPostExecute(Map result) {
			ArrayList<TaskInfoEntity> entities = new ArrayList<TaskInfoEntity>();
			entities = (ArrayList<TaskInfoEntity>) result.get("list");
			openTaskList = entities;
//			mFilterAllTaskListAdapter = new FilterAllTaskListAdapter(context, entities, list_view);
//			list_view.setAdapter(mFilterAllTaskListAdapter);
			
			mFilterAllTaskListAdapter = new FilterAllTaskListAdapter(context, entities, null);
			updateListViewAdapter(mFilterAllTaskListAdapter);

			TaskList.setVisibility(View.GONE);
			contactSummery = "not";
			sortVal = "OPEN";
			
			if (entities != null && entities.isEmpty()) {
				noTaskMsgLayout.setVisibility(View.VISIBLE);
				listview.setVisibility(View.GONE);

				mMainLayout.setBackgroundColor(Color.parseColor("#646464"));
				mNoTaskImageView.setImageResource(R.drawable.bg_no_task_search);
				mNoTaskTitleTextView.setText("No search records found.");
				mNoTaskDescriptionTextView.setText("No records available for search.");
			} else {
				noTaskMsgLayout.setVisibility(View.GONE);
				listview.setVisibility(View.VISIBLE);
				mMainLayout.setBackgroundColor(Color.parseColor("#00000000"));
			}

			if (ApplicationUtil.checkInternetConn(context)) {
				ApplicationConstant.message_Task_Flag_run_handle = 0;
				ApplicationConstant.createTask_Flag_run_handle = 0;
			}

			noOfFavourite = favCount + "";
			noOfFire = fireCount + "";

			lineartotalUnreadButton.setVisibility(View.INVISIBLE);
			if (progressUnread_new != null) {
				progressUnread_new.dismiss();
			}
			pulldownDataState = "OPEN";
		}
	}
	
	private void updateListViewAdapter(BaseAdapter adapter){
		mSwipeActionAdapter = new SwipeActionAdapter(adapter);
		mSwipeActionAdapter.setListView( listview.getRefreshableView());
		listview.setAdapter(mSwipeActionAdapter);
		
		if(adapter instanceof DisplayTaskListAdapter){
			mSwipeActionAdapter.setSwipeActionListener(mAllTaskSwipeActionListener);
			if(headerTitle.equalsIgnoreCase(TaskCategory.TASK_LATER)){
				mSwipeActionAdapter.addBackground(SwipeDirections.DIRECTION_FAR_LEFT, R.layout.row_swipe_all_task)
				.addBackground(SwipeDirections.DIRECTION_NORMAL_LEFT, R.layout.row_swipe_all_task)
				.addBackground(SwipeDirections.DIRECTION_FAR_RIGHT, R.layout.row_swipe_done_task)
				.addBackground(SwipeDirections.DIRECTION_NORMAL_RIGHT, R.layout.row_swipe_done_task);
			}
			else {
				mSwipeActionAdapter.addBackground(SwipeDirections.DIRECTION_FAR_LEFT, R.layout.row_swipe_later_task)
				.addBackground(SwipeDirections.DIRECTION_NORMAL_LEFT, R.layout.row_swipe_later_task)
				.addBackground(SwipeDirections.DIRECTION_FAR_RIGHT, R.layout.row_swipe_done_task)
				.addBackground(SwipeDirections.DIRECTION_NORMAL_RIGHT, R.layout.row_swipe_done_task);
			}
		} else {
			mSwipeActionAdapter.setSwipeActionListener(null);
		}
		listview.setOnRefreshListener(mOnRefreshListener);
	}
	
	protected SwipeActionAdapter mSwipeActionAdapter;
	
	private SwipeActionAdapter.SwipeActionListener mAllTaskSwipeActionListener = new SwipeActionAdapter.SwipeActionListener() {
		@Override
		public boolean hasActions(int arg0) {
			return true;
		}

		@Override
		public void onSwipe(int[] positionList, int[] directionList) {
		}

		@Override
		public boolean shouldDismiss(int position, int direction) {
			if(mDisplayTaskListAdapter == null){
				return false;
			}

			switch (direction) {
			case SwipeDirections.DIRECTION_FAR_LEFT:
			case SwipeDirections.DIRECTION_NORMAL_LEFT:
//				DisplayTaskListAdapter.ViewHolder holder = (DisplayTaskListAdapter.ViewHolder)mDisplayTaskListAdapter.getViewAt(position -1).getTag();
//				mDisplayTaskListAdapter.showMoreDialog(holder, (TaskInfoEntity)mDisplayTaskListAdapter.getList().get(position -1));
				
				mDisplayTaskListAdapter.clickOnLaterButton(null, (TaskInfoEntity)mDisplayTaskListAdapter.getList().get(position -1));
				break;
			case SwipeDirections.DIRECTION_FAR_RIGHT:
			case SwipeDirections.DIRECTION_NORMAL_RIGHT:
				mDisplayTaskListAdapter.clickOnCloseButton(null, (TaskInfoEntity)mDisplayTaskListAdapter.getList().get(position -1));
				break;
			}
			
//			mDisplayTaskListAdapter.remove(mDisplayTaskListAdapter.getItem(position -1));
//			mSwipeActionAdapter = new SwipeActionAdapter(mDisplayTaskListAdapter);
//			mSwipeActionAdapter.notifyDataSetChanged();
			callToRefreshTheListView();
			return true;
		}
	};
}
