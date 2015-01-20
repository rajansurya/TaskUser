package com.taskmanager.adapter;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.taskmanager.adapter.DisplayTaskListAdapter.ViewHolder;
import com.taskmanager.app.R;
import com.taskmanager.app.common.CustomTypeFace;
import com.taskmanager.app.common.DownloadImageTask;
import com.taskmanager.app.common.NonProfilePhotoPlaceHolder;
import com.taskmanager.app.listener.SwipeDismissListViewTouchListener;
import com.taskmanager.app.listener.SwipeDismissListViewTouchListener.Swipe;
import com.taskmanager.background.SyncModule;
import com.taskmanager.database.DBAdapter;
import com.taskmanager.dto.TaskInfoEntity;
import com.taskmanager.entites.TaskEntity;
import com.taskmanager.ui.AllTaskActivity;
import com.taskmanager.ui.CommentActivity;
import com.taskmanager.util.ApplicationConstant;
import com.taskmanager.util.ApplicationUtil;
import com.taskmanager.util.CommonUtil;
import com.taskmanager.webservice.BGConnector;
import com.taskmanager.webservice.BGConnectorImpl;
import com.taskmanager.widgets.MyToast;

public class DisplayCloseTaskAdapter extends ArrayAdapter<TaskInfoEntity> {
	private LayoutInflater mInflater;
	
	private AllTaskActivity mActivity;
	final private ArrayList list_entity;

	public DisplayCloseTaskAdapter(AllTaskActivity context, ArrayList list, ListView listView) {
		super(context, R.layout.task_row_closed, list);
		this.mActivity = context;
		this.list_entity = list;
		
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
//		if (android.os.Build.VERSION.SDK_INT >= 11) {
//			SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(listView, mSwipeDismissCallbacks);
//			listView.setOnTouchListener(touchListener);
//			listView.setOnScrollListener(touchListener.makeScrollListener());
//		}
	}

	private SwipeDismissListViewTouchListener.OnDismissCallback mSwipeDismissCallbacks = new SwipeDismissListViewTouchListener.OnDismissCallback() {
		@Override
		public void onDismiss(ListView listView, int[] reverseSortedPositions, Swipe swipe) {
			int position = reverseSortedPositions[0]-1;
			try{
				final TaskInfoEntity entity=(TaskInfoEntity) list_entity.get(position);
				ViewHolder holder = (ViewHolder)listView.getChildAt(position).getTag();

				if(swipe == Swipe.RIGHT) {
					clickOnUndoButton(holder, entity);
				}
				else if(swipe == Swipe.LEFT){
					clickOnDeleteButton(holder, entity);
				}
			}catch(Exception e){}
		}
	};
	
	@Override
	public int getCount() {
		if(list_entity==null)
			return 0;
		return list_entity.size();
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final TaskInfoEntity entity=(TaskInfoEntity) list_entity.get(position);
		final String AssignFromNum = entity.getUserInfoFrom().getMobile_number();
		final String AssignFrom = entity.getUserInfoFrom().getFirstName();
		final String AssignTo = entity.getUserInfoTo().getFirstName();
		final String AssignToNum = entity.getUserInfoTo().getMobile_number();
		final String targetDate = entity.getTarget_date();
		final ViewHolder holder;
		
		final String Taskid = entity.getId();
		if(convertView == null){
			holder=new ViewHolder();
			convertView = mInflater.inflate(R.layout.task_row_closed, parent, false);
		
			holder.contactPhotoImageView = (ImageView)convertView.findViewById(R.id.ContactPhotoView);
			holder.taskSyncStatusImageView = (ImageView)convertView.findViewById(R.id.TaskSyncStatusImageView);
			holder.taskInOutImageView = (ImageView)convertView.findViewById(R.id.TaskInOutImageView);
			holder.taskInOutImageView.setVisibility(View.GONE);
			
			holder.nameTextView = (TextView)convertView.findViewById(R.id.NameTextView);
			holder.nameTextView.setTypeface(CustomTypeFace.getRobotoMedium(mActivity));
			holder.updatedTimeTextView = (TextView)convertView.findViewById(R.id.UpdatedTimeTextView);
			holder.updatedTimeTextView.setTypeface(CustomTypeFace.getRobotoRegular(mActivity));
			holder.descriptionTextView = (TextView)convertView.findViewById(R.id.DescriptionTextView);
			holder.descriptionTextView.setTypeface(CustomTypeFace.getRobotoRegularItalic(mActivity));
			holder.deadLineTextView = (TextView)convertView.findViewById(R.id.DeadLineTextView);
			holder.deadLineTextView.setTypeface(CustomTypeFace.getRobotoMedium(mActivity));
			
			holder.commentButtonLayout = (LinearLayout)convertView.findViewById(R.id.CommentButtonLayout);
			holder.commentCountTextView = (TextView)convertView.findViewById(R.id.CommentCountTextView);
			holder.undoLayout = (ViewGroup)convertView.findViewById(R.id.UndoLayout);
			holder.deleteLayout = (ViewGroup)convertView.findViewById(R.id.DeleteLayout);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.descriptionTextView.setText(entity.getTask_description());
		
		holder.undoLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				clickOnUndoButton(holder, entity);
			}
		});
		
		holder.deleteLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				clickOnDeleteButton(holder, entity);
			}
		});
		
		holder.contactPhotoImageView.setBackgroundDrawable(mActivity.getResources().getDrawable(NonProfilePhotoPlaceHolder.getPhotoPlaceHolder(entity.getTaskAssigneeName())));
		if(entity.getTaskType().equalsIgnoreCase("sent")){
			DownloadImageTask.setImage(mActivity, AssignToNum, holder.contactPhotoImageView, true);
		}
		else if(entity.getTaskType().equalsIgnoreCase("inbox")){
			DownloadImageTask.setImage(mActivity, AssignFromNum, holder.contactPhotoImageView, true);
		}
		else if(entity.getTaskType().equalsIgnoreCase("self")){
			DownloadImageTask.setImage(mActivity, AssignFromNum, holder.contactPhotoImageView, true);
		}

		holder.taskSyncStatusImageView.setImageResource(entity.getTaskSyncStatus());			
		
		StringBuilder updatedDateStringBuilder = new StringBuilder();;
		if(entity.getCreation_date()!=null&&entity.getUpdatedDate()!=null&&!entity.getCreation_date().isEmpty()&&ApplicationUtil.comparetwoDate(entity.getCreation_date(),entity.getUpdatedDate())) {
			if(ApplicationUtil.getDisplayDate(entity.getCreation_date()).equalsIgnoreCase(ApplicationUtil.getDisplayDate(entity.getUpdatedDate()))) {
				updatedDateStringBuilder.append(ApplicationUtil.getDisplayDate(entity.getCreation_date()));
			}
			else {
//				updatedDateStringBuilder.append(ApplicationUtil.getDisplayDate(entity.getCreation_date()));
//				if(ApplicationUtil.CheckDateFormat(ApplicationUtil.getDisplayDate(entity.getUpdatedDate())) == null) {
//					updatedDateStringBuilder.append(" [ updated "+ApplicationUtil.getDisplayDate(entity.getUpdatedDate())+" ago ]");
//				}
//				else {
//					updatedDateStringBuilder.append(" [ updated on "+ApplicationUtil.getDisplayDate(entity.getUpdatedDate())+" ]");
//				}
				
				if(ApplicationUtil.CheckDateFormat(ApplicationUtil.getDisplayDate(entity.getUpdatedDate())) == null) {
					updatedDateStringBuilder.append("updated "+ApplicationUtil.getDisplayDate(entity.getUpdatedDate()));
				}
				else {
					updatedDateStringBuilder.append("updated on "+ApplicationUtil.getDisplayDate(entity.getUpdatedDate()));
				}
			}
		}
		else
		{
			updatedDateStringBuilder.append(ApplicationUtil.getDisplayDate(entity.getCreation_date()));
		}
		holder.updatedTimeTextView.setText(updatedDateStringBuilder.toString());
		holder.taskInOutImageView.setImageResource(entity.getTaskStatusArrow());
//		holder.updatedTimeTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, entity.getTaskStatusArrow(), 0);
		
		holder.nameTextView.setText(entity.getTaskAssigneeName().equalsIgnoreCase("self") ? "me" : entity.getTaskAssigneeName());
		
		//DeadLine TextView, commented on 28 October 2014
//		if (entity.getTarget_date() != null && entity.getTarget_date().trim().length() > 0) {
//			long targetTimestamp = ApplicationUtil.getUpdatedTimestampFromDate3(entity.getTarget_date());
//			String targetTimeTemp = ApplicationUtil	.changeDateTime4(targetTimestamp);
//			holder.deadLineTextView.setText(targetTimeTemp);
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
//					holder.deadLineTextView.setVisibility(View.VISIBLE);
//					holder.deadLineTextView.setText("Due date-");
//					holder.deadLineTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				}
				else if(targetDateString != null && alarmDateString != null && targetDateString.equals(alarmDateString)){
					holder.deadLineTextView.setVisibility(View.VISIBLE);
					holder.deadLineTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_alarm_small, 0);
//					String text = String.format("Due date- %s, %s", targetDateString, ApplicationUtil.getDateTime8(entity.getAlarm_Date_Time()));
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
		if(((entity.getTaskType() != null && entity.getTaskType().trim().equalsIgnoreCase("self"))||((entity.getTaskUrl()!=null)&&((!entity.getTaskUrl().isEmpty()))))) {
//			holder.commentButtonLayout.setVisibility(View.GONE);
			holder.commentCountTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.notefaction_check, 0, 0, 0);

		} else {
			if(entity.getMsgCount()==0){
//				holder.commentCountTextView.setTextColor(entity.getMsgCountColor());
//				holder.commentCountTextView.setText(""+entity.getMsgCount());
//				holder.commentCountTextView.setBackgroundResource(R.drawable.notefaction_check);
				holder.commentCountTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.notefaction_check, 0, 0, 0);
			}else{
//				holder.commentCountTextView.setTextColor(entity.getMsgCountColor());
				holder.commentCountTextView.setText(""+entity.getMsgCount());
//				holder.commentCountTextView.setBackgroundResource(entity.getMsgSyncStatusBackground());	
				holder.commentCountTextView.setCompoundDrawablesWithIntrinsicBounds(entity.getMsgSyncStatusBackground(), 0, 0, 0);
			}
		}
		
		if("CLOSE".equalsIgnoreCase(entity.getStatus())&&!entity.isLocalThread()) {
			holder.descriptionTextView.setTextColor(Color.GRAY);
//			holder.descriptionTextView.setPaintFlags(holder.descriptionTextView	.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		} else if("CLOSE".equalsIgnoreCase(entity.getStatus())&&entity.isLocalThread()){
			holder.descriptionTextView.setTextColor(Color.GRAY);
//			holder.descriptionTextView.setPaintFlags(holder.descriptionTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

		}else{
			holder.descriptionTextView.setTextColor(Color.BLACK);
//			holder.descriptionTextView.setPaintFlags(holder.descriptionTextView.getPaintFlags()	& (~Paint.STRIKE_THRU_TEXT_FLAG));
		}
		
		holder.commentButtonLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if("N".equalsIgnoreCase(entity.getIsSendMessageUnread())){
					holder.commentCountTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.notefaction_check_read, 0, 0, 0);
					entity.setMsgSyncStatusBackground(R.drawable.notefaction_check_read);
//					holder.commentCountTextView.setTextColor(mActivity.getResources().getColor(R.color.black));
					entity.setMsgCountColor(mActivity.getResources().getColor(R.color.black));
				}
				
				Intent intent = new Intent(mActivity, CommentActivity.class);
				intent.putExtra("entity", entity);
				intent.putExtra("isReadOnly", false);
				mActivity.startActivity(intent);
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
//						mActivity.displayCloseMessageDialog((ArrayList<MessageInfoEntity>)messageInfoEntities, entity.getTaskType() , Taskid,	AssignFromNum, AssignToNum,holder.commentCountTextView,holder.commentButtonLayout,entity);
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
		
		return convertView;
	}

	private class ViewHolder {
		ImageView contactPhotoImageView;
		ImageView taskSyncStatusImageView;
		ImageView taskInOutImageView;
		
		TextView nameTextView;
		TextView updatedTimeTextView;
		TextView descriptionTextView;
		TextView deadLineTextView;
		
		LinearLayout commentButtonLayout;
		TextView commentCountTextView;
		ViewGroup undoLayout;
		ViewGroup deleteLayout;
		
	}
	
	private String STATUS;
	private void clickOnUndoButton(final ViewHolder holder, final TaskInfoEntity entity){
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
		if (!entity.isLocalThread()) {
			entity.setStatus("OPEN");
			entity.setLocalThread(true);
			//
			if(targetDate!=null&& targetDate.trim().length()>0)
			{
				long targetTimestamp = ApplicationUtil.getUpdatedTimestampFromDate3(entity.getTarget_date());
				String targetTimeTemp = ApplicationUtil	.changeDateTime4(targetTimestamp);
				Spannable WordToSpan = new SpannableString(entity.getTask_description());
				WordToSpan.setSpan(new ForegroundColorSpan(Color.BLACK), 0,	WordToSpan.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				
				if(holder != null){
					holder.descriptionTextView.setText(WordToSpan);
				}
			}

			STATUS="OPEN";
			if(holder != null){
				holder.descriptionTextView.setTextColor(Color.BLACK);
			}
			
//			holder.descriptionTextView.setPaintFlags(holder.descriptionTextView.getPaintFlags()	& (~Paint.STRIKE_THRU_TEXT_FLAG));
			SyncModule module=new SyncModule(mActivity);
			module.createNotaiont(entity.getTask_description(), entity.getId());
		} else {
			entity.setLocalThread(true);
			entity.setStatus("CLOSE");
			STATUS="CLOSE";
			if(targetDate!=null&& targetDate.trim().length()>0)
			{
				long targetTimestamp = ApplicationUtil.getUpdatedTimestampFromDate3(entity.getTarget_date());
				String targetTimeTemp = ApplicationUtil	.changeDateTime4(targetTimestamp);
				Spannable WordToSpan = new SpannableString(entity.getTask_description());
				WordToSpan.setSpan(new ForegroundColorSpan(Color.GRAY), 0,	WordToSpan.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				if(holder != null){
					holder.descriptionTextView.setText(WordToSpan);
				}
			}
			if(holder != null){
				holder.descriptionTextView.setTextColor(Color.GRAY);
			}
//			holder.descriptionTextView.setPaintFlags(holder.descriptionTextView	.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

		}
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
		
		list_entity.remove(entity);
		notifyDataSetChanged();
		mActivity.setHeaderTitleCount(mActivity.headerTitle,list_entity.size());
		
		MyToast.makeText(mActivity, "Task has been reopened.", Color.parseColor("#32b1fa"));
	}
	
	private void clickOnDeleteButton(final ViewHolder holder, final TaskInfoEntity entity){
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setMessage("Are you sure to delete this task?");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				clickOnConfirmToDeleteButton(holder, entity);
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		builder.create().show();
	}
	
	private void clickOnConfirmToDeleteButton(final ViewHolder holder, final TaskInfoEntity entity){
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

		list_entity.remove(entity);
		notifyDataSetChanged();
		mActivity.setHeaderTitleCount(mActivity.headerTitle,list_entity.size());


		new Thread(new  Runnable() {
			public void run() {
				try{
					BGConnector connector=new BGConnectorImpl();

					boolean flag=connector.deleteTask(taskId, ApplicationUtil.getPreference(ApplicationConstant.regMobNo,mActivity),mActivity, ApplicationUtil.getPreference(ApplicationConstant.xtoken,mActivity));
				}catch(Exception exception){}
			}
		}).start();
		
		MyToast.makeText(mActivity, "Task has been deleted.", Color.parseColor("#da4336"));
	}
}
