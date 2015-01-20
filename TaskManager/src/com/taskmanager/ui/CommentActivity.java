package com.taskmanager.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taskmanager.app.R;
import com.taskmanager.app.common.CustomTypeFace;
import com.taskmanager.app.common.DownloadImageTask;
import com.taskmanager.app.common.NonProfilePhotoPlaceHolder;
import com.taskmanager.background.SyncModule;
import com.taskmanager.bean.MessageDialogDto;
import com.taskmanager.bean.MessageDto;
import com.taskmanager.bean.ResponseDto;
import com.taskmanager.database.DBAdapter;
import com.taskmanager.dto.MessageInfoEntity;
import com.taskmanager.dto.TaskInfoEntity;
import com.taskmanager.util.ApplicationConstant;
import com.taskmanager.util.ApplicationUtil;
import com.taskmanager.util.CommonUtil;
import com.taskmanager.webservice.BGConnector;
import com.taskmanager.webservice.BGConnectorImpl;

public class CommentActivity extends FragmentActivity {
	private CommentActivity mActivity;
	private TaskInfoEntity mTaskInfoEntity;
	
	private ImageView mSendButton;
	private EditText mCommentEditText;
	private ViewGroup mLinearMessgeLayout;
	private View mDividerView;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_comment);
		mActivity = this;
		
		mTaskInfoEntity = (TaskInfoEntity)getIntent().getSerializableExtra("entity");
		boolean isReadOnly = getIntent().getBooleanExtra("isReadOnly", false);
		
		mSendButton = (ImageView)findViewById(R.id.send);
		mCommentEditText = (EditText)findViewById(R.id.message);
		mLinearMessgeLayout = (ViewGroup)findViewById(R.id.linearMessge);
		mDividerView = findViewById(R.id.dividerView);
		
		if(isReadOnly == false){
			mLinearMessgeLayout.setVisibility(View.GONE);
			mDividerView.setVisibility(View.GONE);
		}
		
		new InitCommentsAsyncTask().execute();
	}
	
	private class InitCommentsAsyncTask extends AsyncTask<Void, Void, ArrayList<MessageInfoEntity>> {
		private ProgressDialog progressDialogReceived=null;
		
		@Override
		protected void onPreExecute() {
			progressDialogReceived = new ProgressDialog(mActivity);
			progressDialogReceived.setMessage(mActivity.getResources().getString(R.string.task_progress));
			progressDialogReceived.setCancelable(false);
			progressDialogReceived.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialogReceived.show();
		}

		@Override
		protected ArrayList<MessageInfoEntity> doInBackground(Void... params) {
			DBAdapter adapter=DBAdapter.getInstance(mActivity);
			List<MessageInfoEntity> messageInfoEntities=adapter.getMessageByTaskId(mTaskInfoEntity.getId());
			return (ArrayList<MessageInfoEntity>)messageInfoEntities;
		}
		@Override
		protected void onPostExecute(ArrayList<MessageInfoEntity> list) {
			super.onPostExecute(list);
			final String AssignFromNum = mTaskInfoEntity.getUserInfoFrom().getMobile_number();
			final String AssignToNum = mTaskInfoEntity.getUserInfoTo().getMobile_number();
			
			displayMessageDialogNew(list, mTaskInfoEntity.getTaskType() , mTaskInfoEntity.getId(),	AssignFromNum, AssignToNum,mTaskInfoEntity);
			
			if(progressDialogReceived!=null) {
				progressDialogReceived.dismiss();
			}
		}
	}
	
	

	public void displayMessageDialogNew(ArrayList<MessageInfoEntity> messageList,	String TaskType, final String taskId,
			final String taskFrom,final String taskTo,final TaskInfoEntity entity) {
		
		String msgTo = "";

		if (TaskType!=null&& TaskType.trim().equalsIgnoreCase("Inbox")) {
			//messageEdit.setText(getResources().getString(R.string.msg_from_receiver));
			msgTo = taskFrom;
		} else if (TaskType!=null&&TaskType.trim().equalsIgnoreCase("Sent")) {
			//messageEdit.setText(getResources().getString(R.string.msg_from_sender));
			msgTo = taskTo;
		}
		final String msgToNum = msgTo;
		MessageDialogDto dialogDto = new MessageDialogDto();
		dialogDto.setAssignFromNum(taskFrom);
		dialogDto.setAssignToNum(msgTo);
		dialogDto.setDialogMessgaeList(messageList);
		dialogDto.setTaskid(taskId);
		dialogDto.setTaskTypeDB(TaskType);
//		dialogDto.setDialogView(dialog);
		dialogDto.setUnReadStatus(entity.getIsSendMessageUnread());
		new AsyntaskShowMessageDialog().execute(dialogDto);
		
		mSendButton.setEnabled(true);
		mSendButton.setClickable(true);
		mSendButton.setOnClickListener(mClickListener);
	}
	
	private OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String TaskType = mTaskInfoEntity.getTaskType();
			mTaskInfoEntity.getId();	
			final String AssignFromNum = mTaskInfoEntity.getUserInfoFrom().getMobile_number();
			final String AssignToNum = mTaskInfoEntity.getUserInfoTo().getMobile_number();
			String taskFrom = AssignFromNum;
			String taskTo = AssignToNum;
			String taskId = mTaskInfoEntity.getId();
			
			String msgTo = "";

			if (TaskType!=null&& TaskType.trim().equalsIgnoreCase("Inbox")) {
				//messageEdit.setText(getResources().getString(R.string.msg_from_receiver));
				msgTo = taskFrom;
			} else if (TaskType!=null&&TaskType.trim().equalsIgnoreCase("Sent")) {
				//messageEdit.setText(getResources().getString(R.string.msg_from_sender));
				msgTo = taskTo;
			}
			final String msgToNum = msgTo;

			final String message = mCommentEditText.getText().toString();
			if (message.trim().equals("")) {
				Toast.makeText(mActivity,	getResources().getString(R.string.blank_msg_toast),Toast.LENGTH_LONG).show();
				//msgSyncStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.notefaction_crosswithredoutline));
				return;
			}else{
				//Disable the button to avoid double click.
				mSendButton.setEnabled(true);
				mSendButton.setClickable(true);
				mSendButton.setOnClickListener(mClickListener);
				
				//-----
				int randomNo = (int) (Math.random() * 1000000);
				String tempId = randomNo + "";
				ArrayList<ArrayList<String>> userTable = ApplicationUtil.getInstance().uploadListFromDB("User", new String[] {"Status,XToken,Mobile_Number,Number_Validation"}, null, mActivity);
				MessageDto mDto = new MessageDto();
				mDto.setMessage_ID(tempId);
				mDto.setIsMessageSync("N");
				mDto.setIsRead("Y");
				mDto.setM_Desc(message);
				if(userTable != null && !userTable.isEmpty())
				{
					mDto.setM_From(userTable.get(0).get(2));
				}
				String transactionId=CommonUtil.getTransactionId(mActivity);
				mDto.setM_From_Name(ApplicationConstant.ApplicantName);
				mDto.setM_To(msgToNum);
				mDto.setTask_ID(taskId);
				mDto.setCreation_Date(ApplicationUtil.getGMTDate());
				mDto.setIsMessage(true);
				mDto.setTransactionId(transactionId);

				ContentValues initialValues = new ContentValues();
				initialValues.put("Message_ID", mDto.getMessage_ID());
				initialValues.put("transactionId", transactionId);
				initialValues.put("M_Desc", mDto.getM_Desc());
				initialValues.put("M_From", mDto.getM_From());
				initialValues.put("M_From_Name",ApplicationUtil.getContactNameFromPhoneNo(mActivity,mDto.getM_From()));
				//initialValues.put("M_To_Name",ApplicationUtil.getContactNameFromPhoneNo(context,mDto.getM_To(),mDto.getM_To()));
				initialValues.put("M_To", mDto.getM_To());
				initialValues.put("IsMessageSync", "N");
				initialValues.put("IsRead", "Y");//
				initialValues.put("Task_ID", mDto.getTask_ID());
				initialValues.put("Creation_Date", mDto.getCreation_Date());
				initialValues.put("CreationDateTime", ApplicationUtil.getGMTLong());
				ApplicationUtil.getInstance().saveDataInDB("Messaging", null,initialValues, mActivity);
				
				int count=(mTaskInfoEntity.getMsgCount()+1);// update icon
				
				//Doing this to avoid NumberFormatException, Because now number is not updating.
			//	int count=0;
//				commentCount.setText(count+"");
				mTaskInfoEntity.setMsgCount(count);
				mTaskInfoEntity.setMsgSyncStatusBackground(R.drawable.notefaction_cross);
//				commentCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.notefaction_cross, 0, 0, 0);
				Map map=new HashMap();
				map.put("dto", mDto);
//				map.put("msgSyncStatus", commentCount);
				map.put("entity", mTaskInfoEntity);
				new AsyntaskCreateMessage().execute(map);
			}
		}
	};

	private class AsyntaskShowMessageDialog extends
	AsyncTask<MessageDialogDto, Void, ArrayList<MessageInfoEntity>> {
		private MessageDialogDto dialogDto;

		@Override
		protected ArrayList<MessageInfoEntity> doInBackground(MessageDialogDto... params) {
			dialogDto = params[0];
			ArrayList<MessageInfoEntity>	dialogMessgaeList = dialogDto.getDialogMessgaeList();
			return dialogMessgaeList;
		}

		@Override
		protected void onPostExecute(ArrayList<MessageInfoEntity> result) {
			LinearLayout messages = (LinearLayout) mActivity.findViewById(R.id.messageList);
			for (MessageInfoEntity messageInfoEntity : result) {

				String msgId = messageInfoEntity.getMessageId();
				String msgFromName = messageInfoEntity.getUserInfoFrom().getFirstName();
				String msgDesc = messageInfoEntity.getMessage_description();
				String msgCreationDate =messageInfoEntity.getCreatedDate();
				String msgIsRead = messageInfoEntity.getMessageReadStatus();
				String msgFromNo = messageInfoEntity.getUserInfoFrom().getMobile_number();
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				final View child = inflater.inflate(R.layout.message_row, null);

				TextView sender = (TextView) child.findViewById(R.id.sender);
				sender.setTypeface(CustomTypeFace.getRobotoMedium(mActivity));

				if(msgFromNo!=null&&!ApplicationConstant.ApplicantName.equalsIgnoreCase(""))
				{
					//sender.setText(ApplicationConstant.ApplicantName);
					try {
						sender.setText(msgFromName!=null?msgFromName:msgFromNo);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				// TextView date = (TextView) child.findViewById(R.id.msgdate);

				TextView message = (TextView) child.findViewById(R.id.messagetxt);
				message.setTypeface(CustomTypeFace.getRobotoRegular(mActivity));
				message.setText(msgDesc);
				
				TextView messageDate = (TextView) child.findViewById(R.id.msgdate);
				messageDate.setTypeface(CustomTypeFace.getRobotoLight(mActivity));
				messageDate.setText(ApplicationUtil.getDisplayDate(msgCreationDate));
				
				ImageView imageView = (ImageView)child.findViewById(R.id.PhotoView);
				imageView.setBackgroundResource(NonProfilePhotoPlaceHolder.getPhotoPlaceHolder(msgFromName));
				DownloadImageTask.setImage(mActivity, msgFromNo, imageView, true);
				
				if (msgIsRead != null && !msgIsRead.trim().equalsIgnoreCase("Y")|| msgIsRead.trim().equalsIgnoreCase("")) {
					
					//Vishesh 17 Oct 2014, Don't require bold as per new design.
//					message.setTypeface(null, Typeface.BOLD);
				}
				messages.addView(child);
			}

			if ("N".equalsIgnoreCase(dialogDto.getUnReadStatus())) {
				ContentValues initialValues = new ContentValues();
				initialValues.put("IsRead", "y");
				initialValues.put("MSG_TYPE", "READ");
				ApplicationUtil.getInstance().updateDataInDB("Messaging",initialValues, mActivity, "Task_ID = '" + dialogDto.getTaskid() + "'",null);
				new Thread(new Runnable() {

					@Override
					public void run() {
						BGConnector connector=new BGConnectorImpl();
						try {
							boolean flag=connector.markMessageAsRead(dialogDto.getTaskid(), ApplicationUtil.getPreference(ApplicationConstant.regMobNo,mActivity),dialogDto.getAssignToNum(), ApplicationUtil.getPreference(ApplicationConstant.xtoken,mActivity));
							if(!flag){
								ContentValues initialValues = new ContentValues();
								initialValues.put("IsRead", "y");
								initialValues.put("MSG_TYPE", "READNONSYNC");
								ApplicationUtil.getInstance().updateDataInDB("Messaging",initialValues, mActivity, "Task_ID = '" + dialogDto.getTaskid() + "'",null);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
			
			TextView noCommentsTextView = (TextView)mActivity.findViewById(R.id.NoMsgTextView);
			if(messages.getChildCount() == 0){
				noCommentsTextView.setVisibility(View.VISIBLE);
			} else {
				noCommentsTextView.setVisibility(View.GONE);
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
	}
	
	private class AsyntaskCreateMessage extends	AsyncTask<Map, Void, Map> {
		private ProgressDialog progressDialogReceived=null;
		
		@Override
		protected void onPreExecute() {
			progressDialogReceived = new ProgressDialog(mActivity);
			progressDialogReceived.setMessage(mActivity.getResources().getString(R.string.task_progress));
			progressDialogReceived.setCancelable(false);
			progressDialogReceived.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialogReceived.show();
		}
		
		@Override
		protected Map doInBackground(Map... params) {
			Map map=params[0];

			final MessageDto dto =(MessageDto) map.get("dto");
			SyncModule syncObj = new SyncModule(mActivity);
			ResponseDto responseDto=syncObj.createMessage(dto);
			map.put("status", responseDto.getStatus());
			map.put("message", responseDto.getMessageDescription());
			map.put("messageId", responseDto.getMessageId());
			return map;
		}

		@Override
		protected void onPostExecute(Map map) {
			int activityResult = Activity.RESULT_CANCELED;
			try {
				String status=(String)map.get("status");
				final MessageDto dto =(MessageDto) map.get("dto");
				final TaskInfoEntity entity=(TaskInfoEntity)map.get("entity");
				String messageId=dto.getMessage_ID();
				if("401".equalsIgnoreCase(status)){
					String message=(String)map.get("message");
					CommonUtilsUi.getCustomeDialog(mActivity, message);
					activityResult = Activity.RESULT_CANCELED;
//					return ;
				}
				
				else if("00".equalsIgnoreCase(status)){
//					View msgSyncStatus=(View)map.get("msgSyncStatus");
//					((TextView)msgSyncStatus).setCompoundDrawablesWithIntrinsicBounds(R.drawable.notefaction_check, 0, 0, 0);
					entity.setMsgSyncStatusBackground(R.drawable.notefaction_check);
					entity.setIsSendMessageUnread("Y");
					activityResult = Activity.RESULT_OK;
				}
				
				if(progressDialogReceived!=null) {
					progressDialogReceived.dismiss();
				}
				
				entity.setMsgSyncStatusBackground(R.drawable.notefaction_check);
				activityResult = Activity.RESULT_OK;
				
				Intent intent = new Intent();
				intent.putExtra("entity", entity);
				setResult(activityResult, intent);
				mActivity.finish();
				mActivity.overridePendingTransition(0, R.anim.slide_out_to_bottom);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		mActivity.overridePendingTransition(0, R.anim.slide_out_to_bottom);
	}
}
