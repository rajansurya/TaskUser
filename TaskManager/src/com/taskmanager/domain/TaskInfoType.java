//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.11.04 at 04:56:55 PM IST 
//


package com.taskmanager.domain;





public class TaskInfoType {

   private String laterTask;
    private String regStatus;
	private  String oldTaskId;
    private String mobileNumber;
   
    private String lastUpdateTime;
    
    private String modifiedBy;
   private String transactionId;
    private String taskId;
   
    private String groupId;
   
    private String assignFrom;
   
    private String assignFromName;
   
    private String assignTo;
   
    private String assignToName;
   
    private String taskDescription;
   
    private String priority;
   
    private String fire;
   
    private String priorityDescription;
   
    private String targetDate;
   
    private String closerDate;
   
    private String closedBy;
   
    private String reminderTime;
   
    private String message;
   
    private String status;
   
    private String favouraite;
   
    private String creationDate;
   
    private String totalMessage;
   
    private String unreadMessage;
   
    private String isAssiner;
   
    private String oldAssignee;
   
    private String newAssignee;
    private String taskUrl;
    private String modifyDate;
    private String modyfiedDate;
   private  String isMessageRead;
   private String isTaskRead;
   
   private String updateState;
    
   
    
   
   public String getIsMessageRead() {
	return isMessageRead;
}

public void setIsMessageRead(String isMessageRead) {
	this.isMessageRead = isMessageRead;
}

public String getIsTaskRead() {
	return isTaskRead;
}

public void setIsTaskRead(String isTaskRead) {
	this.isTaskRead = isTaskRead;
}

public String getUpdateState() {
	return updateState;
}

public void setUpdateState(String updateState) {
	this.updateState = updateState;
}

public String getModyfiedDate() {
		return modyfiedDate;
	}

	public void setModyfiedDate(String modyfiedDate) {
		this.modyfiedDate = modyfiedDate;
	}

public  String getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(String modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String getTaskUrl() {
		return taskUrl;
	}

	public void setTaskUrl(String taskUrl) {
		this.taskUrl = taskUrl;
	}

	private MessageListType messages;

	

	public String getOldTaskId() {
		return oldTaskId;
	}

	public void setOldTaskId(String oldTaskId) {
		this.oldTaskId = oldTaskId;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getAssignFrom() {
		return assignFrom;
	}

	public void setAssignFrom(String assignFrom) {
		this.assignFrom = assignFrom;
	}

	public String getAssignFromName() {
		return assignFromName;
	}

	public void setAssignFromName(String assignFromName) {
		this.assignFromName = assignFromName;
	}

	public String getAssignTo() {
		return assignTo;
	}

	public void setAssignTo(String assignTo) {
		this.assignTo = assignTo;
	}

	public String getAssignToName() {
		return assignToName;
	}

	public void setAssignToName(String assignToName) {
		this.assignToName = assignToName;
	}

	public String getTaskDescription() {
		return taskDescription;
	}

	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getFire() {
		return fire;
	}

	public void setFire(String fire) {
		this.fire = fire;
	}

	public String getPriorityDescription() {
		return priorityDescription;
	}

	public void setPriorityDescription(String priorityDescription) {
		this.priorityDescription = priorityDescription;
	}

	public String getTargetDate() {
		return targetDate;
	}

	public void setTargetDate(String targetDate) {
		this.targetDate = targetDate;
	}

	public String getCloserDate() {
		return closerDate;
	}

	public void setCloserDate(String closerDate) {
		this.closerDate = closerDate;
	}

	public String getClosedBy() {
		return closedBy;
	}

	public void setClosedBy(String closedBy) {
		this.closedBy = closedBy;
	}

	public String getReminderTime() {
		return reminderTime;
	}

	public void setReminderTime(String reminderTime) {
		this.reminderTime = reminderTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFavouraite() {
		return favouraite;
	}

	public void setFavouraite(String favouraite) {
		this.favouraite = favouraite;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getTotalMessage() {
		return totalMessage;
	}

	public void setTotalMessage(String totalMessage) {
		this.totalMessage = totalMessage;
	}

	public String getUnreadMessage() {
		return unreadMessage;
	}

	public void setUnreadMessage(String unreadMessage) {
		this.unreadMessage = unreadMessage;
	}

	public String getIsAssiner() {
		return isAssiner;
	}

	public void setIsAssiner(String isAssiner) {
		this.isAssiner = isAssiner;
	}

	public String getOldAssignee() {
		return oldAssignee;
	}

	public void setOldAssignee(String oldAssignee) {
		this.oldAssignee = oldAssignee;
	}

	public String getNewAssignee() {
		return newAssignee;
	}

	public void setNewAssignee(String newAssignee) {
		this.newAssignee = newAssignee;
	}

	
	public MessageListType getMessages() {
		return messages;
	}

	public void setMessages(MessageListType messages) {
		this.messages = messages;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getRegStatus() {
		return regStatus;
	}

	public void setRegStatus(String regStatus) {
		this.regStatus = regStatus;
	}

	public String getLaterTask() {
		return laterTask;
	}

	public void setLaterTask(String laterTask) {
		this.laterTask = laterTask;
	}
	

    }