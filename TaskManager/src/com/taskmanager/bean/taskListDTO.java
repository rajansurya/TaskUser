package com.taskmanager.bean;

public class taskListDTO {

	private String taskId;
	private String oldTaskId;
	private String assignFrom;
	private String assignFromName;
	private String assignTo;
	private String assignToName;
	private String taskDesc;
	private String priority;
	private String priorityDesc;
	private String closureDateTime;
	private String targetDateTime;
	private String fireFlag;
	private String reminderTime;
	private String isReminder;
	private String isMessage;
	private String status;
	private String favouraite;
	private String taskType;
	private String creationDate;
	private String transactioId;
	private String IsJunk="N";
	private String groupId;
	private String TotalMessage;
	private String UnreadMessage;
	private String closedBy;
	private long creationDateime;
	private long closureDate;
	private String isMsgSendChecked = "N";
	private String taskUrl="";
	private String updationDate;
	private long updatedDateTime;
	

	public String getTransactioId() {
		return transactioId;
	}

	public void setTransactioId(String transactioId) {
		this.transactioId = transactioId;
	}

	public String getOldTaskId() {
		return oldTaskId;
	}

	public void setOldTaskId(String oldTaskId) {
		this.oldTaskId = oldTaskId;
	}

	public String getUpdationDate() {
		return updationDate;
	}

	public void setUpdationDate(String updationDate) {
		this.updationDate = updationDate;
	}

	public long getUpdatedDateTime() {
		return updatedDateTime;
	}

	public void setUpdatedDateTime(long updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}

	public String getTaskUrl() {
		return taskUrl;
	}

	public void setTaskUrl(String taskUrl) {
		this.taskUrl = taskUrl;
	}

	public String getIsMsgSendChecked() {
		return isMsgSendChecked;
	}

	public void setIsMsgSendChecked(String isMsgSendChecked) {
		this.isMsgSendChecked = isMsgSendChecked;
	}

	public long getCreationDateime() {
		return creationDateime;
	}

	public void setCreationDateime(long creationDateime) {
		this.creationDateime = creationDateime;
	}

	public long getClosureDate() {
		return closureDate;
	}

	public void setClosureDate(long closureDate) {
		this.closureDate = closureDate;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getAssignFrom() {
		return assignFrom;
	}

	public void setAssignFrom(String assignFrom) {
		this.assignFrom = assignFrom;
	}

	public String getAssignTo() {
		return assignTo;
	}

	public void setAssignTo(String assignTo) {
		this.assignTo = assignTo;
	}

	public String getClosureDateTime() {
		return closureDateTime;
	}

	public void setClosureDateTime(String closureDateTime) {
		this.closureDateTime = closureDateTime;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getFireFlag() {
		return fireFlag;
	}

	public void setFireFlag(String fireFlag) {
		this.fireFlag = fireFlag;
	}

	public String getFavouraite() {
		return favouraite;
	}

	public void setFavouraite(String favouraite) {
		this.favouraite = favouraite;
	}

	public String getIsReminder() {
		return isReminder;
	}

	public void setIsReminder(String isReminder) {
		this.isReminder = isReminder;
	}

	public String getReminderTime() {
		return reminderTime;
	}

	public void setReminderTime(String reminderTime) {
		this.reminderTime = reminderTime;
	}

	public String getIsMessage() {
		return isMessage;
	}

	public void setIsMessage(String isMessage) {
		this.isMessage = isMessage;
	}

	public String getTaskDesc() {
		return taskDesc;
	}

	public void setTaskDesc(String taskDesc) {
		this.taskDesc = taskDesc;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

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

	public String getPriorityDesc() {
		return priorityDesc;
	}

	public void setPriorityDesc(String priorityDesc) {
		this.priorityDesc = priorityDesc;
	}

	public String getTargetDateTime() {
		return targetDateTime;
	}

	public void setTargetDateTime(String targetDateTime) {
		this.targetDateTime = targetDateTime;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getIsJunk() {
		return IsJunk;
	}

	public void setIsJunk(String isJunk) {
		IsJunk = isJunk;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
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

	public String getClosedBy() {
		return closedBy;
	}

	public void setClosedBy(String closedBy) {
		this.closedBy = closedBy;
	}

}
