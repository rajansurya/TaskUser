package com.taskmanager.dto;

import java.io.Serializable;



public class TaskInfoEntity implements Serializable {
	public static int expandedIndex = -1;

	private String isTaskSnooz;
	private String id;
	private String isSendMessageUnread;
	private String assign_from;
	private String assign_to;
	private String task_description;
	private String creation_date;
	private String target_date;
	private String closer_date;
	private boolean userRegistered;
	private String fire_flag;
	private String IsFavouraite;
	private String isReminder;
	private String reminder_time;
	private String transactionId;
	private String newAssigne;
	private String isMessage;
	private String status;
	private String priority;
	private UserInfoEntity userInfoFrom;
	private UserInfoEntity userInfoTo;
	private String groupId;
	private String task_closed_by;
	private String closerDateTime;
	private String Alarm_Date_Time;
	private String taskSyncType;
	private int alarmBackground;
	private int textTypeface;
	private int msgCount=0;
	private int taskStatusArrow =0;
	private String taskAssigneeName = "";
	private int msgSyncStatusBackground ;
	private int msgCountColor ;
	private int calenderImage;
	private int editIcon;
	private int userIcon;
	private int fireBackground;
	private boolean fireEnable=true;
	private boolean fireCheck = false;
	private String updatedDate;
	private String isMessageSend;
	private int textIconTextColor;
	private String targetDateTime;
	private String displayName;


	public String getTargetDateTime() {
		return targetDateTime;
	}

	public void setTargetDateTime(String targetDateTime) {
		this.targetDateTime = targetDateTime;
	}

	public String getNewAssigne() {
		return newAssigne;
	}

	public void setNewAssigne(String newAssigne) {
		this.newAssigne = newAssigne;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getIsSendMessageUnread() {
		return isSendMessageUnread;
	}

	public void setIsSendMessageUnread(String isSendMessageUnread) {
		this.isSendMessageUnread = isSendMessageUnread;
	}

	public int getTextIconTextColor() {
		return textIconTextColor;
	}

	public void setTextIconTextColor(int textIconTextColor) {
		this.textIconTextColor = textIconTextColor;
	}

	public String getIsMessageSend() {
		return isMessageSend;
	}




	public void setIsMessageSend(String isMessageSend) {
		this.isMessageSend = isMessageSend;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public boolean isCompleteCheckBoxImgEnable() {
		return completeCheckBoxImgEnable;
	}

	public void setCompleteCheckBoxImgEnable(boolean completeCheckBoxImgEnable) {
		this.completeCheckBoxImgEnable = completeCheckBoxImgEnable;
	}

	public boolean isCompleteCheckBoxChecked() {
		return completeCheckBoxChecked;
	}

	public void setCompleteCheckBoxChecked(boolean completeCheckBoxChecked) {
		this.completeCheckBoxChecked = completeCheckBoxChecked;
	}

	private int favCount;
	private boolean isLocalThread;
	private String taskUrl;
	private int urlTaskBackground;
	private boolean completeCheckBoxImgEnable= true;
	private boolean completeCheckBoxChecked = false;

	public boolean isCompleteCheckBoxImg() {
		return completeCheckBoxImgEnable;
	}

	public void setCompleteCheckBoxImg(boolean completeCheckBoxImg) {
		this.completeCheckBoxImgEnable = completeCheckBoxImg;
	}



	public int getUrlTaskBackground() {
		return urlTaskBackground;
	}

	public void setUrlTaskBackground(int urlTaskBackground) {
		this.urlTaskBackground = urlTaskBackground;
	}

	public String getTaskUrl() {
		return taskUrl;
	}

	public void setTaskUrl(String taskUrl) {
		this.taskUrl = taskUrl;
	}

	public boolean isLocalThread() {
		return isLocalThread;
	}

	public void setLocalThread(boolean isLocalThread) {
		this.isLocalThread = isLocalThread;
	}

	public int getFavCount() {
		return favCount;
	}

	public void setFavCount(int favCount) {
		this.favCount = favCount;
	}

	public int getFireCount() {
		return fireCount;
	}

	public void setFireCount(int fireCount) {
		this.fireCount = fireCount;
	}

	private int TaskSyncStatus;
	private int fireCount ;





	public int getTaskSyncStatus() {
		return TaskSyncStatus;
	}

	public void setTaskSyncStatus(int taskSyncStatus) {
		TaskSyncStatus = taskSyncStatus;
	}

	public boolean isFireCheck() {
		return fireCheck;
	}

	public void setFireCheck(boolean fireCheck) {
		this.fireCheck = fireCheck;
	}

	public int getFireBackground() {
		return fireBackground;
	}

	public void setFireBackground(int fireBackground) {
		this.fireBackground = fireBackground;
	}

	public boolean isFireEnable() {
		return fireEnable;
	}

	public void setFireEnable(boolean fireEnable) {
		this.fireEnable = fireEnable;
	}

	public int getCalenderImage() {
		return calenderImage;
	}

	public void setCalenderImage(int calenderImage) {
		this.calenderImage = calenderImage;
	}

	public int getEditIcon() {
		return editIcon;
	}

	public void setEditIcon(int editIcon) {
		this.editIcon = editIcon;
	}

	public int getUserIcon() {
		return userIcon;
	}

	public void setUserIcon(int userIcon) {
		this.userIcon = userIcon;
	}

	public int getMsgSyncStatusBackground() {
		return msgSyncStatusBackground;
	}

	public void setMsgSyncStatusBackground(int msgSyncStatusBackground) {
		this.msgSyncStatusBackground = msgSyncStatusBackground;
	}

	public int getMsgCountColor() {
		return msgCountColor;
	}

	public void setMsgCountColor(int msgCountColor) {
		this.msgCountColor = msgCountColor;
	}

	public int getTaskStatusArrow() {
		return taskStatusArrow;
	}

	public void setTaskStatusArrow(int taskStatusArrow) {
		this.taskStatusArrow = taskStatusArrow;
	}

	public String getTaskAssigneeName() {
		return taskAssigneeName;
	}

	public void setTaskAssigneeName(String taskAssigneeName) {
		this.taskAssigneeName = taskAssigneeName;
	}



	public int getMsgCount() {
		return msgCount;
	}

	public void setMsgCount(int msgCount) {
		this.msgCount = msgCount;
	}

	public int getTextTypeface() {
		return textTypeface;
	}

	public void setTextTypeface(int textTypeface) {
		this.textTypeface = textTypeface;
	}

	public int getAlarmBackground() {
		return alarmBackground;
	}

	public void setAlarmBackground(int alarmBackground) {
		this.alarmBackground = alarmBackground;
	}

	public String getTaskSyncType() {
		return taskSyncType;
	}

	public void setTaskSyncType(String taskSyncType) {
		this.taskSyncType = taskSyncType;
	}

	public String getAlarm_Date_Time() {
		return Alarm_Date_Time;
	}

	public void setAlarm_Date_Time(String alarm_Date_Time) {
		Alarm_Date_Time = alarm_Date_Time;
	}

	public String getIsAlarmSet() {
		return isAlarmSet;
	}

	public void setIsAlarmSet(String isAlarmSet) {
		this.isAlarmSet = isAlarmSet;
	}

	private String isAlarmSet = "N";

	public String getCloserDateTime() {
		return closerDateTime;
	}

	public void setCloserDateTime(String closerDateTime) {
		this.closerDateTime = closerDateTime;
	}

	private String taskType;
	private String isFAvSync;
	private String isStatusSync;
	private String isPrioritySync;
	private String isReminderSync;
	private String isTaskUpdateSync;
	private String isTaskSync;
	private String isActive;
	private String IsJunk;
	private String count;
	private String assignToName;
	private String assignFromName;
	public String getAssignFromName() {
		return assignFromName;
	}

	public void setAssignFromName(String assignFromName) {
		this.assignFromName = assignFromName;
	}

	public String getAssignToName() {
		return assignToName;
	}

	public void setAssignToName(String assignToName) {
		this.assignToName = assignToName;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	private String alarmTime;



	private String isAssigneeSync;

	private String old_assignee;

	private String IsTargetSync;

	private String TotalMessage;


	private String UnreadMessage;

	private String IsTaskRead;

	private String ClosedBy;

	private String oldAssignee;
	private String CreationDateTime;

	private String ClosureDate;



	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getIsFAvSync() {
		return isFAvSync;
	}

	public void setIsFAvSync(String isFAvSync) {
		this.isFAvSync = isFAvSync;
	}

	public String getIsStatusSync() {
		return isStatusSync;
	}

	public void setIsStatusSync(String isStatusSync) {
		this.isStatusSync = isStatusSync;
	}

	public String getIsPrioritySync() {
		return isPrioritySync;
	}

	public void setIsPrioritySync(String isPrioritySync) {
		this.isPrioritySync = isPrioritySync;
	}

	public String getIsReminderSync() {
		return isReminderSync;
	}

	public void setIsReminderSync(String isReminderSync) {
		this.isReminderSync = isReminderSync;
	}

	public String getIsTaskUpdateSync() {
		return isTaskUpdateSync;
	}

	public void setIsTaskUpdateSync(String isTaskUpdateSync) {
		this.isTaskUpdateSync = isTaskUpdateSync;
	}

	public String getIsTaskSync() {
		return isTaskSync;
	}

	public void setIsTaskSync(String isTaskSync) {
		this.isTaskSync = isTaskSync;
	}

	public String getIsJunk() {
		return IsJunk;
	}

	public void setIsJunk(String isJunk) {
		IsJunk = isJunk;
	}

	public String getAlarmTime() {
		return alarmTime;
	}

	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}


	public String getIsAssigneeSync() {
		return isAssigneeSync;
	}

	public void setIsAssigneeSync(String isAssigneeSync) {
		this.isAssigneeSync = isAssigneeSync;
	}

	public String getIsTargetSync() {
		return IsTargetSync;
	}

	public void setIsTargetSync(String isTargetSync) {
		IsTargetSync = isTargetSync;
	}

	public String getTotalMessage() {
		return TotalMessage;
	}

	public void setTotalMessage(String totalMessage) {
		TotalMessage = totalMessage;
	}

	public String getUnreadMessage() {
		return UnreadMessage;
	}

	public void setUnreadMessage(String unreadMessage) {
		UnreadMessage = unreadMessage;
	}

	public String getIsTaskRead() {
		return IsTaskRead;
	}

	public void setIsTaskRead(String isTaskRead) {
		IsTaskRead = isTaskRead;
	}

	public String getClosedBy() {
		return ClosedBy;
	}

	public void setClosedBy(String closedBy) {
		ClosedBy = closedBy;
	}

	public String getOldAssignee() {
		return oldAssignee;
	}

	public void setOldAssignee(String oldAssignee) {
		this.oldAssignee = oldAssignee;
	}

	public String getCreationDateTime() {
		return CreationDateTime;
	}

	public void setCreationDateTime(String creationDateTime) {
		CreationDateTime = creationDateTime;
	}

	public String getClosureDate() {
		return ClosureDate;
	}

	public void setClosureDate(String closureDate) {
		ClosureDate = closureDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAssign_from() {
		return assign_from;
	}

	public void setAssign_from(String assign_from) {
		this.assign_from = assign_from;
	}

	public String getAssign_to() {
		return assign_to;
	}

	public void setAssign_to(String assign_to) {
		this.assign_to = assign_to;
	}

	public String getTask_description() {
		return task_description;
	}

	public void setTask_description(String task_description) {
		this.task_description = task_description;
	}

	public String getCreation_date() {
		return creation_date;
	}

	public void setCreation_date(String creation_date) {
		this.creation_date = creation_date;
	}

	public String getTarget_date() {
		return target_date;
	}

	public void setTarget_date(String target_date) {
		this.target_date = target_date;
	}

	public String getCloser_date() {
		return closer_date;
	}

	public void setCloser_date(String closer_date) {
		this.closer_date = closer_date;
	}

	public String getFire_flag() {
		return fire_flag;
	}

	public void setFire_flag(String fire_flag) {
		this.fire_flag = fire_flag;
	}

	public String getIsFavouraite() {
		return IsFavouraite;
	}

	public void setIsFavouraite(String isFavouraite) {
		IsFavouraite = isFavouraite;
	}

	public String getIsReminder() {
		return isReminder;
	}

	public void setIsReminder(String isReminder) {
		this.isReminder = isReminder;
	}

	public String getReminder_time() {
		return reminder_time;
	}

	public void setReminder_time(String reminder_time) {
		this.reminder_time = reminder_time;
	}

	public String getIsMessage() {
		return isMessage;
	}

	public void setIsMessage(String isMessage) {
		this.isMessage = isMessage;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public UserInfoEntity getUserInfoFrom() {
		return userInfoFrom;
	}

	public void setUserInfoFrom(UserInfoEntity userInfoFrom) {
		this.userInfoFrom = userInfoFrom;
	}

	public UserInfoEntity getUserInfoTo() {
		return userInfoTo;
	}

	public void setUserInfoTo(UserInfoEntity userInfoTo) {
		this.userInfoTo = userInfoTo;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getTask_closed_by() {
		return task_closed_by;
	}

	public void setTask_closed_by(String task_closed_by) {
		this.task_closed_by = task_closed_by;
	}

	public String getOld_assignee() {
		return old_assignee;
	}

	public void setOld_assignee(String old_assignee) {
		this.old_assignee = old_assignee;
	}

	public boolean isUserRegistered() {
		return userRegistered;
	}

	public void setUserRegistered(boolean userRegistered) {
		this.userRegistered = userRegistered;
	}

	public String getIsTaskSnooz() {
		return isTaskSnooz;
	}

	public void setIsTaskSnooz(String isTaskSnooz) {
		this.isTaskSnooz = isTaskSnooz;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}
