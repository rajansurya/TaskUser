package com.taskmanager.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.taskmanager.app.MyApp;
import com.taskmanager.app.R;
import com.taskmanager.app.common.CustomTypeFace;
import com.taskmanager.app.common.DateTimeUtil;
import com.taskmanager.app.common.DownloadImageTask;
import com.taskmanager.app.common.HashTagMatcher;
import com.taskmanager.app.common.NonProfilePhotoPlaceHolder;
import com.taskmanager.app.listener.SwipeDismissListViewTouchListener;
import com.taskmanager.app.listener.SwipeDismissListViewTouchListener.Swipe;
import com.taskmanager.background.SyncModule;
import com.taskmanager.database.DBAdapter;
import com.taskmanager.dto.TaskInfoEntity;
import com.taskmanager.entites.TaskEntity;
import com.taskmanager.ui.AllTaskActivity;
import com.taskmanager.ui.CommentActivity;
import com.taskmanager.ui.CreateTaskActivity;
import com.taskmanager.ui.SnoozActivity;
import com.taskmanager.util.ApplicationConstant;
import com.taskmanager.util.ApplicationUtil;
import com.taskmanager.util.CommonUtil;
import com.taskmanager.webservice.BGConnector;
import com.taskmanager.webservice.BGConnectorImpl;
import com.taskmanager.widgets.CustomButton;
import com.taskmanager.widgets.DialogMenu;
import com.taskmanager.widgets.MyToast;

public class FilterAllTaskListAdapter extends ArrayAdapter {
	private LayoutInflater mInflater;
	private CutomFilter mCutomFilter;
	private String mFilterTextString;
	
	private AllTaskActivity mActivity;
	private ArrayList mFilteredList;
	private ArrayList mOriginalList;
	
	private TaskInfoEntity mTaskInfoEntitySelected;

	public FilterAllTaskListAdapter(AllTaskActivity activity, ArrayList list, ListView listView) {
		super(activity, R.layout.task_row, list);
		this.mActivity = activity;
		this.mFilteredList = this.mOriginalList = list;
		
		mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
//		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(listView, mSwipeDismissCallbacks);
//		listView.setOnTouchListener(touchListener);
//		listView.setOnScrollListener(touchListener.makeScrollListener());
	}

	private SwipeDismissListViewTouchListener.OnDismissCallback mSwipeDismissCallbacks = new SwipeDismissListViewTouchListener.OnDismissCallback() {
		@Override
		public void onDismiss(ListView listView, int[] reverseSortedPositions, Swipe swipe) {
			try{
				int position = reverseSortedPositions[0]-1;
				final TaskInfoEntity entity=(TaskInfoEntity) mFilteredList.get(position);
				ViewHolder holder = (ViewHolder)listView.getChildAt(position).getTag();

				if(swipe == Swipe.RIGHT) {
					clickOnCloseButton(holder, entity);
				}
				else if(swipe == Swipe.LEFT){
					clickOnLaterButton(holder, entity);
				}
			}catch(NullPointerException e){
			}
		}
	};
	
		
	@Override
	public int getCount() {
//		if(mFilteredList==null)
//			return 0;
		return mFilteredList.size();
	}

	@Override
	public Object getItem(int position) {
		return mFilteredList.get(position);
	}

	@Override
	public Filter getFilter() {
		if(mCutomFilter == null){
			mCutomFilter = new CutomFilter();
		}
		return mCutomFilter;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final TaskInfoEntity entity=(TaskInfoEntity) mFilteredList.get(position);
		final String AssignFromNum = entity.getUserInfoFrom().getMobile_number();
		final String AssignFrom = entity.getUserInfoFrom().getFirstName();
		final String AssignTo = entity.getUserInfoTo().getFirstName();
		final String AssignToNum = entity.getUserInfoTo().getMobile_number();
		final String targetDate = entity.getTarget_date();
		final ViewHolder holder;
		
		final String Taskid = entity.getId();
		if(convertView == null){
			holder=new ViewHolder();
			convertView = mInflater.inflate(R.layout.task_row, parent, false);
		
			holder.contentLayout = (ViewGroup)convertView.findViewById(R.id.ContentLayout);
			holder.contactPhotoImageView = (ImageView)convertView.findViewById(R.id.ContactPhotoView);
			holder.editButton = (ImageView)convertView.findViewById(R.id.EditButton);
			holder.taskSyncStatusImageView = (ImageView)convertView.findViewById(R.id.TaskSyncStatusImageView);
			holder.taskInOutImageView = (ImageView)convertView.findViewById(R.id.TaskInOutImageView);
			holder.taskInOutImageView.setVisibility(View.GONE);
			holder.moreButtonImageView = (ImageView)convertView.findViewById(R.id.MoreButtonImageView);
			
			holder.nameTextView = (TextView)convertView.findViewById(R.id.NameTextView);
			holder.nameTextView.setTypeface(CustomTypeFace.getRobotoMedium(mActivity));
			holder.updatedTimeTextView = (TextView)convertView.findViewById(R.id.UpdatedTimeTextView);
			holder.updatedTimeTextView.setTypeface(CustomTypeFace.getRobotoRegular(mActivity));
			holder.descriptionTextView = (TextView)convertView.findViewById(R.id.DescriptionTextView);
			holder.descriptionTextView.setTypeface(CustomTypeFace.getRobotoRegular(mActivity));
			holder.deadLineTextView = (TextView)convertView.findViewById(R.id.DeadLineTextView);
			holder.deadLineTextView.setTypeface(CustomTypeFace.getRobotoMedium(mActivity));
			
			holder.commentButtonLayout = (LinearLayout)convertView.findViewById(R.id.CommentButtonLayout);
			holder.commentCountTextView = (TextView)convertView.findViewById(R.id.CommentCountTextView);
			holder.commentCountTextView.setTypeface(CustomTypeFace.getRobotoMedium(mActivity));
			holder.focusNowButton = (CustomButton)convertView.findViewById(R.id.FocusNowButton);
			holder.focusNowButton.setTypeface(CustomTypeFace.getRobotoMedium(mActivity));
			holder.priorityButton = (CustomButton)convertView.findViewById(R.id.PriorityButton);
			holder.priorityButton.setTypeface(CustomTypeFace.getRobotoMedium(mActivity));
			
			holder.closedButton = (TextView)convertView.findViewById(R.id.ClosedButton);
			holder.closedButton.setTypeface(CustomTypeFace.getRobotoLight(mActivity));
			holder.assignButton = (TextView)convertView.findViewById(R.id.AssignButton);
			holder.assignButton.setTypeface(CustomTypeFace.getRobotoLight(mActivity));
			holder.scheduleButton = (TextView)convertView.findViewById(R.id.ScheduleButton);
			holder.scheduleButton.setTypeface(CustomTypeFace.getRobotoLight(mActivity));
			holder.alarmButton = (CustomButton)convertView.findViewById(R.id.AlarmButton);
			holder.alarmButton.setTypeface(CustomTypeFace.getRobotoLight(mActivity));
			holder.bluePannelLayout = (ViewGroup)convertView.findViewById(R.id.BluePannelLayout);
			
			convertView.setTag(holder);
	} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
			
//			holder.contactPhotoImageView.setBackgroundResource(NonProfilePhotoPlaceHolder.getPlaceHolder(entity.getTaskAssigneeName()));
			holder.contactPhotoImageView.setBackgroundDrawable(mActivity.getResources().getDrawable(NonProfilePhotoPlaceHolder.getPhotoPlaceHolder(entity.getTaskAssigneeName())));
			
			holder.priorityButton.setEnableIcon(entity.isFireCheck());
			
			//MoreButton grid icon
			if(entity.getTaskType().trim().equalsIgnoreCase("Inbox")){
				holder.moreButtonImageView.setImageResource(R.drawable.more_4grid);
			} else {
				holder.moreButtonImageView.setImageResource(R.drawable.more_9grid);
			}

			//Description
			if(entity.getTask_description().contains("#")){
				holder.descriptionTextView.setMovementMethod(LinkMovementMethod.getInstance());
				holder.descriptionTextView.setText(HashTagMatcher.getHashTagsSpannable(mActivity, mHashWordClickedListener, entity.getTask_description()));
				holder.descriptionTextView.setHighlightColor(Color.TRANSPARENT);
			} else {
				holder.descriptionTextView.setText(entity.getTask_description());
			}
			
		if(entity.getTaskType().equalsIgnoreCase("sent")){
			DownloadImageTask.setImage(mActivity, AssignToNum, holder.contactPhotoImageView, true);
		}
		else if(entity.getTaskType().equalsIgnoreCase("inbox")){
			DownloadImageTask.setImage(mActivity, AssignFromNum, holder.contactPhotoImageView, true);
		}
		else if(entity.getTaskType().equalsIgnoreCase("self")){
			DownloadImageTask.setImage(mActivity, AssignFromNum, holder.contactPhotoImageView, true);
		}
		
//		holder.contentLayout.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				showMoreDialog(holder, entity);
//			}
//		});
		
		holder.alarmButton.setEnableIcon(entity.getIsAlarmSet().equalsIgnoreCase("y"));
		if(entity.getTaskType() != null && entity.getTaskType().trim().equalsIgnoreCase("inbox")){
			holder.scheduleButton.setVisibility(View.GONE);
			holder.editButton.setVisibility(View.GONE);
			holder.assignButton.setVisibility(View.GONE);
		}else{
			holder.scheduleButton.setVisibility(View.VISIBLE);
			holder.editButton.setVisibility(View.VISIBLE);
			holder.assignButton.setVisibility(View.VISIBLE);
		}

		holder.taskSyncStatusImageView.setImageResource(entity.getTaskSyncStatus());			
		
		//New logic and format
		StringBuilder updatedDateStringBuilder = new StringBuilder();;
		if(entity.getCreation_date()!=null&&entity.getUpdatedDate()!=null&&!entity.getCreation_date().isEmpty()&&ApplicationUtil.comparetwoDate(entity.getCreation_date(),entity.getUpdatedDate())) {
			if(ApplicationUtil.getDisplayDate(entity.getCreation_date()).equalsIgnoreCase(ApplicationUtil.getDisplayDate(entity.getUpdatedDate()))) {
				updatedDateStringBuilder.append(ApplicationUtil.getDisplayDate(entity.getCreation_date()));
			}
			else {
				if(ApplicationUtil.CheckDateFormat(ApplicationUtil.getDisplayDate(entity.getUpdatedDate())) == null) {
					updatedDateStringBuilder.append("updated "+ApplicationUtil.getDisplayDate(entity.getUpdatedDate()));
				}
				else {
					updatedDateStringBuilder.append("updated on "+ApplicationUtil.getDisplayDate(entity.getUpdatedDate()));
				}
			}
		}
		else {
			updatedDateStringBuilder.append(ApplicationUtil.getDisplayDate(entity.getCreation_date()));
		}
		holder.updatedTimeTextView.setText(updatedDateStringBuilder.toString());
		
		//Old logic and format
//		if(entity.getCreation_date()!=null&&entity.getUpdatedDate()!=null&&!entity.getCreation_date().isEmpty()&&ApplicationUtil.comparetwoDate(entity.getCreation_date(),entity.getUpdatedDate())) {
//			if(ApplicationUtil.getDisplayDate(entity.getCreation_date()).equalsIgnoreCase(ApplicationUtil.getDisplayDate(entity.getUpdatedDate()))) {
//				holder.updatedTimeTextView.setText("");
//			} else {
//				if(ApplicationUtil.CheckDateFormat(ApplicationUtil.getDisplayDate(entity.getUpdatedDate())) == null) {
//					holder.updatedTimeTextView.setText(""+" [ updated "+ApplicationUtil.getDisplayDate(entity.getUpdatedDate())+" ago ]");
//				} else {
//					holder.updatedTimeTextView.setText(""+" [ updated on "+ApplicationUtil.getDisplayDate(entity.getUpdatedDate())+" ]");
//				}
//			}
//		} else {
//			holder.updatedTimeTextView.setText("");
//		}
		
		holder.taskInOutImageView.setImageResource(entity.getTaskStatusArrow());
		
		holder.nameTextView.setText(entity.getTaskAssigneeName().equalsIgnoreCase("self") ? "me" : entity.getTaskAssigneeName());
		if(entity.getTaskAssigneeName()!=null&&entity.getTaskAssigneeName().trim().equalsIgnoreCase("Assigner"))
		{
			holder.nameTextView.setTextColor(mActivity.getResources().getColor(R.color.red));
			holder.nameTextView.setTypeface(CustomTypeFace.getRobotoMedium(mActivity));
		}
		//We don't require gray text color.
		/*else if(entity.getTaskType().equalsIgnoreCase("sent")&&!entity.isUserRegistered()){
			holder.nameTextView.setTextColor(mActivity.getResources().getColor(R.color.grey));
			holder.nameTextView.setTypeface(CustomTypeFace.getRobotoMedium(mActivity));
		}*/
		else
		{
			holder.nameTextView.setTypeface(CustomTypeFace.getRobotoMedium(mActivity));
			holder.nameTextView.setTextColor(mActivity.getResources().getColor(R.color.black));
		}
		
		
		//Code is below, changed typeface 14 Oct 2014
		/*if("N".equalsIgnoreCase(entity.getIsTaskRead())&&!"Self".equalsIgnoreCase(entity.getTaskType())){
			holder.descriptionTextView.setTypeface(CustomTypeFace.getRobotoMedium(mActivity));
		}else{
			holder.descriptionTextView.setTypeface(CustomTypeFace.getRobotoLight(mActivity));
		}*/
		
		if("N".equalsIgnoreCase(entity.getIsTaskRead())&&!"Self".equalsIgnoreCase(entity.getTaskType())){
			holder.descriptionTextView.setTypeface(CustomTypeFace.getRobotoRegular(mActivity));
		}else{
			holder.descriptionTextView.setTypeface(CustomTypeFace.getRobotoRegular(mActivity));
		}

		//10 Oct 2014 Vishesh- Commented because Puneet don't require red background. 
//		if(entity.getTaskUrl()!=null&&!entity.getTaskUrl().equals("")) {
//			holder.descriptionTextView.setBackgroundColor(mActivity.getResources().getColor(R.color.reddish));
//		}
//		else {
//			holder.descriptionTextView.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
//		}
//		if(entity.getTaskType().equalsIgnoreCase("sent")&&!entity.isUserRegistered()){
//			holder.descriptionTextView.setBackgroundColor(mActivity.getResources().getColor(R.color.reddish));
//		}
		
		//DeadLine TextView, commented on 28 October 2014
//		if (entity.getTarget_date() != null && entity.getTarget_date().trim().length() > 0) {
//			long targetTimestamp = ApplicationUtil.getUpdatedTimestampFromDate3(entity.getTarget_date());
//			String targetTimeTemp = ApplicationUtil	.changeDateTime4(targetTimestamp);
//			holder.deadLineTextView.setText("Due date is " + targetTimeTemp);
//			holder.deadLineTextView.setVisibility(View.VISIBLE);
//		} else {
//			holder.deadLineTextView.setVisibility(View.GONE);
//		}
		
		//DealLine new concept 28 October 2014
		String targetDateString = null;
		if (entity.getTarget_date() != null && entity.getTarget_date().trim().length() > 0) {
			targetDateString = ApplicationUtil.getDateTime6(entity.getTarget_date());
		}
		String alarmDateString = null;
		if (entity.getAlarm_Date_Time() != null && entity.getAlarm_Date_Time().trim().length() > 0) {
			alarmDateString = ApplicationUtil.getDateTime6(entity.getAlarm_Date_Time());
		}
		
		if(targetDateString == null && alarmDateString == null){
			holder.deadLineTextView.setVisibility(View.GONE);
//			holder.deadLineTextView.setVisibility(View.VISIBLE);
//			holder.deadLineTextView.setText("Due date-");
//			holder.deadLineTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		}
		else if(targetDateString != null && alarmDateString != null && targetDateString.equals(alarmDateString)){
			holder.deadLineTextView.setVisibility(View.VISIBLE);
			holder.deadLineTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_alarm_small, 0);
//			String text = String.format("Due date- %s, %s", targetDateString, ApplicationUtil.getDateTime8(entity.getAlarm_Date_Time()));
			String text = String.format("Due date- %s", ApplicationUtil.getDateTime10(entity.getAlarm_Date_Time()));
			holder.deadLineTextView.setText(text);
		}
		else if(targetDateString != null && alarmDateString != null){
			holder.deadLineTextView.setVisibility(View.VISIBLE);
			holder.deadLineTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_alarm_small, 0);
			String text = String.format("Due date- %s, Alarm- %s", targetDateString, ApplicationUtil.getDateTime8(entity.getAlarm_Date_Time()));
			holder.deadLineTextView.setText(text);
		}
		else if(targetDateString != null) {
			holder.deadLineTextView.setVisibility(View.VISIBLE);
			holder.deadLineTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			String text = String.format("Due date- %s", targetDateString);
			holder.deadLineTextView.setText(text);
		}
		else if(alarmDateString != null) {
			holder.deadLineTextView.setVisibility(View.VISIBLE);
			holder.deadLineTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_alarm_small, 0);
			String text = String.format("Alarm- %s", ApplicationUtil.getDateTime8(entity.getAlarm_Date_Time()));
			holder.deadLineTextView.setText(text);
		}
		
		
		//Comment
		/*if(((entity.getTaskType() != null && entity.getTaskType().trim().equalsIgnoreCase("self"))||((entity.getTaskUrl()!=null)&&((!entity.getTaskUrl().isEmpty()))))) {
			holder.commentButtonLayout.setVisibility(View.GONE);
		} 
		else {*/
			if(entity.getMsgCount()==0){
//				holder.commentCountTextView.setTextColor(entity.getMsgCountColor());
//				holder.commentCountTextView.setText(""+entity.getMsgCount());
				holder.commentCountTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.notefaction_check, 0, 0, 0);
			}else{
//				holder.commentCountTextView.setTextColor(entity.getMsgCountColor());
				holder.commentCountTextView.setText(""+entity.getMsgCount());
				holder.commentCountTextView.setCompoundDrawablesWithIntrinsicBounds(entity.getMsgSyncStatusBackground(), 0, 0, 0);
			}
//		}
			
			 if(entity.getTaskType().equalsIgnoreCase("self")&&entity.getMsgSyncStatusBackground()!=R.drawable.notefaction_cross){
					holder.commentCountTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.notefaction_check_read, 0, 0, 0);
				}
		
		if("CLOSE".equalsIgnoreCase(entity.getStatus())&&!entity.isLocalThread()) {
			holder.closedButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checkedbox, 0, 0, 0);
			holder.descriptionTextView.setTextColor(Color.GRAY);
//			holder.descriptionTextView.setPaintFlags(holder.descriptionTextView	.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		} else if("CLOSE".equalsIgnoreCase(entity.getStatus())&&entity.isLocalThread()){
			holder.closedButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checkedbox, 0, 0, 0);
			holder.descriptionTextView.setTextColor(Color.GRAY);
//			holder.descriptionTextView.setPaintFlags(holder.descriptionTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

		}else{
			holder.closedButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.uncheckedbox, 0, 0, 0);
			holder.descriptionTextView.setTextColor(Color.BLACK);
//			holder.descriptionTextView.setPaintFlags(holder.descriptionTextView.getPaintFlags()	& (~Paint.STRIKE_THRU_TEXT_FLAG));
		}
		
		//Click on Name
		holder.nameTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(entity.getTaskUrl()!=null&& !entity.getTaskUrl().trim().equals("")) {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(entity.getTaskUrl()));
					mActivity.startActivity(browserIntent);
					return;
				}
			}
		});

		holder.commentButtonLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if("N".equalsIgnoreCase(entity.getIsSendMessageUnread())){
					holder.commentCountTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.notefaction_check_read, 0, 0, 0);
					
					entity.setMsgSyncStatusBackground(R.drawable.notefaction_check_read);
					entity.setMsgCountColor(mActivity.getResources().getColor(R.color.black));
				}
				
				MyApp.getInstance().commentButtonTextView = holder.commentCountTextView;
				Intent intent = new Intent(mActivity, CommentActivity.class);
				intent.putExtra("entity", entity);
				intent.putExtra("isReadOnly", true);
				mActivity.startActivityForResult(intent, 4);
				mActivity.overridePendingTransition(R.anim.slide_in_from_bottom, 0);
				
//				new AsyncTask<Void, Void, Void>() {
//					private ProgressDialog progressDialogReceived=null;
//					@Override
//					protected Void doInBackground(Void... params) {
//						return null;
//					}
//					@Override
//					protected void onPostExecute(Void result) {
//						DBAdapter adapter=DBAdapter.getInstance(mActivity);
//						List<MessageInfoEntity> messageInfoEntities=adapter.getMessageByTaskId(Taskid);
//						mActivity.displayMessageDialogNew((ArrayList<MessageInfoEntity>)messageInfoEntities, entity.getTaskType() , Taskid,	AssignFromNum, AssignToNum,holder.commentCountTextView, holder.commentButtonLayout,entity);
//
//						if(progressDialogReceived!=null) {
//							progressDialogReceived.dismiss();
//						}
//					}
//					@Override
//					protected void onPreExecute() {
//						progressDialogReceived = new ProgressDialog(mActivity);
//						progressDialogReceived.setMessage(mActivity.getResources().getString(R.string.task_progress));
//						progressDialogReceived.setCancelable(false);
//						progressDialogReceived.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//						progressDialogReceived.show();
//					}
//				}.execute();
			}
		});

		//Favourite
		
		holder.focusNowButton.setEnableIcon(Boolean.parseBoolean(entity.getIsFavouraite() == null ? "false" : entity.getIsFavouraite()));
		
		holder.focusNowButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int count=0;
				final ContentValues initialValues = new ContentValues();
				boolean isFavorite = Boolean.parseBoolean(entity.getIsFavouraite() == null ? "false" : entity.getIsFavouraite());
				if (isFavorite == false) {
					 count=Integer.parseInt(mActivity.mFavoriteTabTextView.getText().toString())+1;
					mActivity.mFavoriteTabTextView.setText(count+"");
					initialValues.put("IsFavouraite", "true");
					entity.setIsFavouraite("true");
					holder.focusNowButton.setEnableIcon(true);
				} else {
					 count=Integer.parseInt(mActivity.mFavoriteTabTextView.getText().toString());
					if(count!=0){
						count=count-1;
						entity.setIsFavouraite("false");
					}
					mActivity.mFavoriteTabTextView.setText(count+"");
					initialValues.put("IsFavouraite", "false");
					holder.focusNowButton.setEnableIcon(false);
				}
				ApplicationUtil.getInstance().updateDataInDB("Task", initialValues, mActivity,"Task_ID = '" + Taskid + "'", null);
				if("Favorite".equalsIgnoreCase(mActivity.headerTitle)){
					mActivity.setHeaderTitleCount("Favorite", count);
					mOriginalList.remove(entity);
					FilterAllTaskListAdapter.this.notifyDataSetChanged();
					
				}
				new Thread(){
					@Override
					public void run() {
					String status="";
					BGConnector bgConnector=new BGConnectorImpl(mActivity);

					String msisdn=ApplicationUtil.getPreference(ApplicationConstant.regMobNo, mActivity);
					if(CommonUtil.getValidMsisdn(msisdn).equalsIgnoreCase(AssignFromNum)){
						status=bgConnector.updateFav(Taskid, holder.focusNowButton.isEnableIcon()+"",msisdn);
					}else{
						status=bgConnector.updateFav(Taskid, holder.focusNowButton.isEnableIcon()+"",AssignToNum);
					}
					if("00".equalsIgnoreCase(status)){
						initialValues.put("IsFAvSync", "Y");
					}else{
						initialValues.put("IsFAvSync", "N");
					}
					ApplicationUtil.getInstance().updateDataInDB("Task", initialValues, mActivity,"Task_ID = '" + Taskid + "'", null);
				}}.start();

				try {
					//favBadge.setText(""+totalFavourite());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		//Fire/PriorityButton
		holder.priorityButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int count=0;
				if (!entity.getTaskType().trim().equalsIgnoreCase("Inbox")) {
					if (holder.priorityButton.isEnableIcon() == false) {
						 count=Integer.parseInt(mActivity.mFireTabTextView.getText().toString())+1;
						mActivity.mFireTabTextView.setText(count+"");
						entity.setFireCheck(true);
						holder.priorityButton.setEnableIcon(true);
					}else{
						 count=Integer.parseInt(mActivity.mFireTabTextView.getText().toString());
						if(count!=0){
							count=count-1;
							entity.setFireCheck(false);
							holder.priorityButton.setEnableIcon(false);
						}
						mActivity.mFireTabTextView.setText(count+"");
					}
					
				
					
					ContentValues initialValues = new ContentValues();
					if (holder.priorityButton.isEnableIcon()) {
						initialValues.put("Fire_Flag", "true");
						initialValues.put("Priority", mActivity.getResources().getString(R.string.fire_priority_val));
					} else {
						initialValues.put("Fire_Flag", "false");
						initialValues.put("Priority", mActivity.getResources().getString(R.string.med_priority_val));
					}
					ApplicationUtil.getInstance().updateDataInDB("Task", initialValues, mActivity,"Task_ID = '" + Taskid + "'", null);
					if("Urgency".equalsIgnoreCase(mActivity.headerTitle)){
						ApplicationUtil.getInstance().updateDataInDB("Task", initialValues, mActivity,"Task_ID = '" + Taskid + "'", null);
						mActivity.setHeaderTitleCount("Urgency", count);
						mOriginalList.remove(entity);
						FilterAllTaskListAdapter.this.notifyDataSetChanged();
					}
					new Thread(){
						@Override
						public void run() {
							String priority="1";
							final ContentValues initialValues1 = new ContentValues();
							if (holder.priorityButton.isEnableIcon()) {
								priority=mActivity.getResources().getString(R.string.fire_priority_val);
							} else {
								priority=mActivity.getResources().getString(R.string.med_priority_val);
							}
							BGConnector bgConnector=new BGConnectorImpl(mActivity);
							String status="";
							try {
								status = bgConnector.updatePriority(Taskid, priority,ApplicationUtil.getPreference(ApplicationConstant.regMobNo,mActivity));
							} catch (Exception e) {
								e.printStackTrace();
							}
							if("00".equalsIgnoreCase(status)){
								initialValues1.put("IsTaskSync", "Y");
								initialValues1.put("IsPrioritySync", "Y");
							}else{
								initialValues1.put("IsTaskSync", "N");
								initialValues1.put("IsPrioritySync", "N");
							}
							ApplicationUtil.getInstance().updateDataInDB("Task", initialValues1, mActivity,"Task_ID = '" + Taskid + "'", null);
						}
					}.start();
				}
				else {
					Toast.makeText(mActivity, "Assigned task can't be updated.", Toast.LENGTH_SHORT).show();
				}
			}
		});

		if(entity.getTaskUrl()!=null&&!entity.getTaskUrl().equals("")) {
			holder. focusNowButton.setEnabled(false);
			holder.commentButtonLayout.setEnabled(false);
		}
		else {
			holder. focusNowButton.setEnabled(true);
			holder.commentButtonLayout.setEnabled(true);
		}
		
		holder.nameTextView.setOnClickListener(new  OnClickListener() {
			@Override
			public void onClick(View v) {
				TaskEntity taskEntityObj = new TaskEntity();
				HashMap entities=new HashMap();
				try {
					String name=holder.nameTextView.getText().toString();
					if("Me".equalsIgnoreCase(name)){
						name="self";
					}
					entities = (HashMap) taskEntityObj.getTaskByName("OPEN",name,mActivity.context);
					mOriginalList.clear();
					mOriginalList.addAll((ArrayList<TaskInfoEntity>) entities.get("list"));
					mActivity.setHeaderTitleCount(name, mOriginalList.size());
					notifyDataSetChanged();
					
					mActivity.mSideMenuSelectedLabels = "Category by name";
					mActivity.hideTabBarPanel();
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
				//mActivity.loadTaskByName(holder.nameTextView.getText().toString());
				//System.out.println("name");
			}
		});
		convertView.setEnabled(true);
		holder.closedButton.setEnabled(true);
		holder.commentButtonLayout.setEnabled(true);
		
		//Click on RowView
//		convertView.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				if(entity.getTaskUrl()!=null&& !entity.getTaskUrl().trim().equals("")) {
//					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(entity.getTaskUrl()));
//					mActivity.startActivity(browserIntent);
//					return;
//				} 
//			}
//		});
		
		if(TaskInfoEntity.expandedIndex == position){
			holder.bluePannelLayout.setVisibility(View.VISIBLE);
		} else {
			holder.bluePannelLayout.setVisibility(View.GONE);
		}
		
		//Gesture
//		SimpleGestureFilter.SimpleGestureListener simpleGestureListener = new SimpleGestureFilter.SimpleGestureListener() {
//			@Override
//			public void onSwipe(int direction) {
//				if(direction == SimpleGestureFilter.SWIPE_LEFT || direction == SimpleGestureFilter.SWIPE_RIGHT){
////					clickOnCloseButton(holder, entity);
//				}
//			}
//			@Override
//			public void onSingleTap() {
//				showMoreDialog(holder, entity);
//			}
//			@Override
//			public void onDoubleTap() {
//			}
//		};
//		
//		final SimpleGestureFilter gestureFilter = new SimpleGestureFilter(mActivity, simpleGestureListener);
//		convertView.setOnTouchListener(new View.OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				gestureFilter.onTouchEvent(event);
//				return false;
//			}
//		});
		
		holder.moreButtonImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showMoreDialog(holder, entity);
			}
		});
		
		return convertView;
	}

	private HashTagMatcher.WordClickedListener mHashWordClickedListener = new HashTagMatcher.WordClickedListener() {
		@Override
		public void wordClicked(CharSequence word) {
			mActivity.hashTagListViewfilter(word.toString());
//			Toast.makeText(mActivity, word, Toast.LENGTH_SHORT).show();			
		}
	};
	
	
	public void collapse(final View v) {
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
	
	public void expand(final View v) {
		v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
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
	
	private class ViewHolder {
		ViewGroup contentLayout;
		ImageView contactPhotoImageView;
		ImageView editButton;
		ImageView taskSyncStatusImageView;
		ImageView taskInOutImageView;
		ImageView moreButtonImageView;
		
		TextView nameTextView;
		TextView updatedTimeTextView;
		TextView descriptionTextView;
		TextView deadLineTextView;
		
		LinearLayout commentButtonLayout;
		TextView commentCountTextView;
		CustomButton focusNowButton;
		CustomButton priorityButton;
		TextView closedButton;
		TextView assignButton;
		TextView scheduleButton;
		CustomButton alarmButton;
		ViewGroup bluePannelLayout;
	}
	
	private void showMoreDialog(final ViewHolder holder, final TaskInfoEntity entity) {
		final DialogMenu dialogMenu = new DialogMenu(mActivity, R.layout.popup_summery);
		dialogMenu.show();
		
		OnClickListener mDailogClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				int viewId = view.getId();
				if(viewId == R.id.CloseButton){
					clickOnCloseButton(holder, entity);
				}
				else if(viewId == R.id.CopyButton){
					clickOnCopyButton(holder, entity);
				}
				else if(viewId == R.id.AlarmButton){
					clickOnAlarmButton(holder, entity);
				}
				else if(viewId == R.id.ShareButton){
					clickOnShareButton(holder, entity);
				}
				else if(viewId == R.id.EditButton){
					clickOnEditButton(holder, entity);
				}
				else if(viewId == R.id.DeleteButton){
					clickOnDeleteButton(holder, entity);
				}
				else if(viewId == R.id.DueDateButton){
					clickOnScheduleButton(holder, entity);
				}
				else if(viewId == R.id.AssigneeButton){
					clickOnAssignButton(holder, entity);
				}
				else if(viewId == R.id.SMSButton){
					clickOnLaterButton(holder, entity);
				}
				dialogMenu.dismiss();
			}
		};

		dialogMenu.findViewById(R.id.CloseButton).setOnClickListener(mDailogClickListener);
		dialogMenu.findViewById(R.id.CopyButton).setOnClickListener(mDailogClickListener);
		dialogMenu.findViewById(R.id.AlarmButton).setOnClickListener(mDailogClickListener);
		dialogMenu.findViewById(R.id.ShareButton).setOnClickListener(mDailogClickListener);
		dialogMenu.findViewById(R.id.EditButton).setOnClickListener(mDailogClickListener);
		dialogMenu.findViewById(R.id.DeleteButton).setOnClickListener(mDailogClickListener);
		dialogMenu.findViewById(R.id.DueDateButton).setOnClickListener(mDailogClickListener);
		dialogMenu.findViewById(R.id.AssigneeButton).setOnClickListener(mDailogClickListener);
		dialogMenu.findViewById(R.id.SMSButton).setOnClickListener(mDailogClickListener);
		
		TextView SMSButton= (TextView)dialogMenu.findViewById(R.id.SMSButton);
//		SMSButton.setText("Snooz");
		if("Y".equalsIgnoreCase(entity.getIsTaskSnooz())){
//			SMSButton.setText("Open");
			SMSButton.setBackgroundResource(R.drawable.grid_open);
		} else {
			SMSButton.setBackgroundResource(R.drawable.grid_later);
		}
		
		if(entity.getTaskType().trim().equalsIgnoreCase("Inbox")){
			dialogMenu.findViewById(R.id.BottomLayout).setVisibility(View.GONE);
			dialogMenu.findViewById(R.id.RightLayout).setVisibility(View.GONE);
		}
	}
	
	private void clickOnEditButton(final ViewHolder holder, final TaskInfoEntity entity){
		if (!entity.getTaskType().trim().equalsIgnoreCase("Inbox")){
			mActivity.editContentPopup(entity.getId(), entity.getTask_description(),entity.getTarget_date(), holder.descriptionTextView,entity);
		}
	}
	
	private String STATUS;
	private void clickOnCloseButton(final ViewHolder holder, final TaskInfoEntity entity){
		STATUS = "";
		final String targetDate = entity.getTarget_date();
		final String AssignFromNum = entity.getUserInfoFrom().getMobile_number();
		final boolean isAssigner;
		final String msisdn=CommonUtil.getValidMsisdn(ApplicationUtil.getPreference(ApplicationConstant.regMobNo, mActivity));
		if(CommonUtil.getValidMsisdn(msisdn).equals(CommonUtil.getValidMsisdn(AssignFromNum))){
			isAssigner=true;
		}else{
			isAssigner=false;
		}
		STATUS="CLOSE";
		ContentValues initialValues = new ContentValues();
		initialValues.put("Status", STATUS);
		initialValues.put("UPDATED_DATE", ApplicationUtil.getGMTDate());
		initialValues.put("UPDATION_DATE_TIME",ApplicationUtil.getGMTLong());
		if("CLOSE".equalsIgnoreCase(STATUS)){
			DBAdapter adapter=DBAdapter.getInstance(mActivity);
			adapter.openDataBase();
			adapter.deleteRecordInDB("NOTAION_TASK_MAPPING", "Task_ID = '" + entity.getId()+ "'", null);
			adapter.close();
		}
		ApplicationUtil.getInstance().updateDataInDB("Task",initialValues, mActivity,"Task_ID = '" + entity.getId() + "'", null);
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				SyncModule module=new SyncModule(mActivity);
				module.changeTaskStatus(entity.getId(), STATUS, isAssigner, msisdn);
			}
		}, 1);
			
		mOriginalList.remove(entity);
		FilterAllTaskListAdapter.this.notifyDataSetChanged();
		mActivity.setHeaderTitleCount(mActivity.headerTitle, mOriginalList.size());
		MyToast.makeText(mActivity, "Task has been completed.", Color.parseColor("#259b24"));
	}

	private void clickOnAssignButton(final ViewHolder holder, final TaskInfoEntity entity){
		final String AssignTo = entity.getUserInfoTo().getFirstName();
		final String AssignToNum = entity.getUserInfoTo().getMobile_number();
		final String Taskid = entity.getId();
		
		if (!entity.getTaskType().trim().equalsIgnoreCase("Inbox"))	{
			mActivity.txtAssigorName = holder.nameTextView;
			mActivity.globalAssigneeNum = AssignToNum;
			mActivity.globalAssigneeName = mActivity.getContactNameFromPhoneNo(mActivity,AssignToNum, AssignTo);
			mActivity.globalTaskId = Taskid;
			if (mActivity.globalAssigneeName != null && mActivity.globalAssigneeNum != null ) {
				mActivity.txtAssigorName = holder.nameTextView;
				mActivity.displayChangeAssignee(Taskid, "", "",mActivity.txtAssigorName);
			}
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	private void clickOnScheduleButton(final ViewHolder holder, final TaskInfoEntity entity){
		mTaskInfoEntitySelected = entity;
//		final String Taskid = entity.getId();
//		String contact_name_cal = ""; 
//		String contact_description_cal = "";
//		try{
//			if(entity.getTaskType().trim().equalsIgnoreCase("Self")) {
//				contact_name_cal = "self";
//				contact_description_cal = entity.getTask_description(); 
//			}
//			if(entity.getTaskType().trim().equalsIgnoreCase("Sent")) {
//				contact_name_cal = entity.getTaskAssigneeName();
//				contact_description_cal = entity.getTask_description();
//			}
//		}catch (Exception e) { }
//
//		if (!entity.getTaskType().trim().equalsIgnoreCase("Inbox")) {
//			Intent intent = new Intent(mActivity, CalenderActivity.class);
//			Bundle b = new Bundle();
//			b.putString("action", "schedule");
//			b.putString("contact_to", contact_name_cal);
//			b.putString("contact_desc", contact_description_cal);
//			b.putString("action", "reminder");
//			b.putString("targateDate", entity.getTarget_date());
//			b.putString("reminderTime", entity.getReminder_time());
//			b.putString("TaskId", Taskid);
//			intent.putExtras(b);
//			mActivity.startActivityForResult(intent, 3);
//		}
		
		DateTimeUtil dateTimeUtil = new DateTimeUtil(mActivity, mScheduleDateSetListener);
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			Date date = dateFormat.parse(entity.getTarget_date());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			
			dateTimeUtil.setDate(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) +1, calendar.get(Calendar.YEAR));
		}catch(Exception e){
			System.out.println(e.toString());
		}
		dateTimeUtil.showDatePicker();
	}
	
	private void clickOnAlarmButton(final ViewHolder holder, final TaskInfoEntity entity){
		Intent intent = new Intent(mActivity, SnoozActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("alarm_date_Time", entity.getAlarm_Date_Time());
		bundle.putString("schedule_task_date_Time", entity.getTarget_date());
		bundle.putString("TaskId", entity.getId());
		bundle.putString("TaskDesc", entity.getTask_description());
		intent.putExtras(bundle);
		mActivity.startActivityForResult(intent, 5);
		mActivity.overridePendingTransition(R.anim.slide_in_from_bottom, 0);
		
//		final String Taskid = entity.getId();
//		Intent intent = new Intent(mActivity, CalenderActivity.class);
//		Bundle b = new Bundle();
//		b.putString("action", "shedulealarm");
//		b.putString("alarm_date_Time", entity.getAlarm_Date_Time());
//		b.putString("isAlarmSet", entity.getIsAlarmSet());
//		//b.putString("reminderTime", reminderTime);
//		b.putString("TaskId", Taskid);
//		b.putString("TaskDesc", entity.getTask_description());
//		intent.putExtras(b);
//		mActivity.startActivityForResult(intent, 3);
	}
	
	private void clickOnCopyButton(final ViewHolder holder, final TaskInfoEntity entity){
		final String Taskid = entity.getId();
		
		Intent intent = new Intent(mActivity,CreateTaskActivity.class);
		Bundle b = new Bundle();
		b.putString("action", "copyTask");
		b.putString("priority",  entity.getPriority());
		b.putString("desc", entity.getTask_description());
		b.putString("targateDate",  entity.getTarget_date());
		b.putString("reminderTime", entity.getReminder_time());
		b.putString("taskId", Taskid);
		b.putString("sortingVal", mActivity.sortVal);
		intent.putExtras(b);
		mActivity.startActivityForResult(intent, 2);
		mActivity.overridePendingTransition(R.anim.slide_in_from_bottom, 0);
	}
	
	private void clickOnDeleteButton(final ViewHolder holder, final TaskInfoEntity entity){
		final String taskId=entity.getId();
		TaskEntity taskEntity = new TaskEntity();
		taskEntity.deleteChangeAssignee(taskId,mActivity);

		String contact_name_cal = ""; 
		String contact_description_cal = "";

		try{

			if(entity.getTaskType().trim().equalsIgnoreCase("Self"))
			{
				contact_name_cal = "self";
				contact_description_cal = entity.getTask_description(); 
			}

			if(entity.getTaskType().trim().equalsIgnoreCase("Sent"))
			{
				contact_name_cal = entity.getTaskAssigneeName();
				contact_description_cal = entity.getTask_description();
			}
			// Deleting event from Calendar
			ContentResolver cr = mActivity.getContentResolver();
			Uri EVENTS_URI =    Uri.parse("content://com.android.calendar/" + "events");
			String title_event = "To "+contact_name_cal+ " : " + contact_description_cal;
			mActivity.deleteCalendarEvent(cr, EVENTS_URI, title_event);

		}catch (Exception e)
		{
		}

		mOriginalList.remove(entity);
		notifyDataSetChanged();
		mActivity.setHeaderTitleCount(mActivity.headerTitle, mOriginalList.size());

		MyToast.makeText(mActivity, "Task has been deleted.", Color.parseColor("#da4336"));

		new Thread(new  Runnable() {
			public void run() {
				try{
					BGConnector connector=new BGConnectorImpl();

					boolean flag=connector.deleteTask(taskId, ApplicationUtil.getPreference(ApplicationConstant.regMobNo,mActivity),mActivity, ApplicationUtil.getPreference(ApplicationConstant.xtoken,mActivity));
				}catch(Exception exception){}
			}
		}).start();
	}
	
	private void clickOnShareButton(final ViewHolder holder, final TaskInfoEntity entity){
		String taskDescription = getShareDescriptionMsg(entity);		
		Intent sendIntent = new Intent();
	    sendIntent.setAction(Intent.ACTION_SEND);
	    sendIntent.putExtra(Intent.EXTRA_SUBJECT, "");
	    sendIntent.putExtra(Intent.EXTRA_TEXT, taskDescription);
//	    sendIntent.setType("message/rfc822");
	    sendIntent.setType("text/plain");

	    if (Build.VERSION.SDK_INT >= 19) {//Build.VERSION_CODES.KITKAT = 19
	     // This will open the "Complete action with" dialog if the user doesn't have a default app set.
	     mActivity.startActivity(sendIntent);
	    } else {
	    	mActivity.startActivity(Intent.createChooser(sendIntent, "Share Via"));
	    }
	}
	
	private void clickOnLaterButton(final ViewHolder holder, final TaskInfoEntity entity){
		try{
			ContentValues initialValues = new ContentValues();
			if("Y".equalsIgnoreCase(entity.getIsTaskSnooz())){
				initialValues.put("isTaskSnooz", "N");
				
				
				MyToast.makeText(mActivity, "Moved to Task Box.", Color.parseColor("#32b1fa"));
			}else{
				initialValues.put("isTaskSnooz", "Y");
				MyToast.makeText(mActivity, "Task moved to Later Box.", Color.parseColor("#fd9800"));
			}
			ApplicationUtil.getInstance().updateDataInDB("Task",initialValues, mActivity,"Task_ID ='" + entity.getId() + "'", null);
			
			new Thread(){
				@Override
				public void run() {
				BGConnector bgConnector=new BGConnectorImpl(mActivity);
				String msisdn=ApplicationUtil.getPreference(ApplicationConstant.regMobNo, mActivity);
				if("Y".equalsIgnoreCase(entity.getIsTaskSnooz())){
					bgConnector.removeLaterTask(entity.getId(), msisdn);
				}else{
					bgConnector.addLaterTask(entity.getId(), msisdn);
					
				}
				
				
			}}.start();
			
			mOriginalList.remove(entity);
			mActivity.setHeaderTitleCount(mActivity.headerTitle, mOriginalList.size());
			FilterAllTaskListAdapter.this.notifyDataSetChanged();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
		/*
		String address = null;
		if(entity.getTaskType().equalsIgnoreCase("self") == false){
			address = entity.getUserInfoTo().getMobile_number();
		}
		
		String taskDescription = getShareDescriptionMsg(entity);
		Intent i = new Intent(android.content.Intent.ACTION_VIEW);
		if(address !=null){
			i.putExtra("address", address);//"5556; 5558; 5560"
		}
		i.putExtra("sms_body", taskDescription);
		i.setType("vnd.android-dir/mms-sms");
		mActivity.startActivity(i);
	*/}
	
	private DateTimeUtil.OnDateSetListener mScheduleDateSetListener = new DateTimeUtil.OnDateSetListener() {
		@SuppressLint("DefaultLocale")
		@Override
		public void onDateSet(int day, int month, int year) {
			String changedTarget = String.format("%d/%d/%d 05:30:00", day, month, year);
			updateScheduleDate(changedTarget);
		}
	};
	
	@SuppressLint("SimpleDateFormat")
	private void updateScheduleDate(String changedTarget) {
		ContentValues initialValues = new ContentValues();
		String taskId = mTaskInfoEntitySelected.getId();
		try {
			SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			initialValues.put("Target_Date_TIME",dateFormat.parse(changedTarget).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		initialValues.put("Target_Date", changedTarget);
		initialValues.put("IsTargetSync", "N");
		initialValues.put("UPDATED_DATE", ApplicationUtil.getGMTDate());
		initialValues.put("UPDATION_DATE_TIME",ApplicationUtil.getGMTLong());
		ApplicationUtil.getInstance().updateDataInDB("Task", initialValues, mActivity, "Task_ID = '"+taskId+"'", null);
		SyncModule syncObj = new SyncModule(mActivity);
		syncObj.UpdateTargetDate(taskId,changedTarget);
		syncObj = null;
		
		mActivity.onActivityResult(3, Activity.RESULT_OK, null);
	}
	
	private String getShareDescriptionMsg(final TaskInfoEntity entity){
		String taskDescriptionText = entity.getTask_description();
		boolean isFavorite = entity.isFireCheck();
		String targetDate = entity.getTarget_date();
		
		String descriptionDetails = null;
		if(targetDate != null && targetDate.trim().isEmpty() == false){
			descriptionDetails = "due date- " + ApplicationUtil.getDateTime6(entity.getTarget_date());;
		}
		if(descriptionDetails != null && isFavorite){
			descriptionDetails = "urgent, " + descriptionDetails;
		} else if(isFavorite){
			descriptionDetails = "urgent";
		}
		
		if(descriptionDetails != null){
			descriptionDetails = String.format(" (%s)", descriptionDetails);
		} else {
			descriptionDetails = "";
		}
		
		String taskDescription = String.format("Please take care: %s%s", taskDescriptionText, descriptionDetails);	
		return taskDescription;
	}
	
	private class CutomFilter extends Filter {
		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			mFilterTextString = constraint.toString().toLowerCase();
			mFilteredList = (ArrayList)results.values;
			notifyDataSetChanged();
			
			mActivity.updateUIonSearch(results.count == 0);
		}

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			constraint=constraint.toString().trim();
			ArrayList list = new ArrayList();
			for(Object object : mOriginalList){
				TaskInfoEntity entity = (TaskInfoEntity)object;
				String words[]=constraint.toString().split(" ");
					
					for (int i = 0; i < words.length; i++) {
						
						if(entity.getDisplayName().toLowerCase().contains(words[i].trim().toLowerCase())||entity.getTask_description().toLowerCase().contains(constraint.toString().toLowerCase())){
							
							list.add(entity);
							break;
						
					}
						
						
						/*if(entity.getTask_description().toLowerCase().contains(constraint.toString().toLowerCase())){
							if(entity.getDisplayName().toLowerCase().contains(words[i].trim().toLowerCase())){
								list.add(entity);
								break;
							}
							
						}else {
							
					}*/
					
				}
			}
			FilterResults results = new FilterResults();
			results.values = list;
			results.count = list.size();
			return results;
		}
	}
}
