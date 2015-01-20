package com.taskmanager.bean;

public class TaskInfoDto {

	private String taskId;
	private String groupId;
	private String assignFrom;
	String assignTo;
	String assignToName;;
	String taskDescription;
	String priority;
	String priorityDescription;
	String creationDate;
	String closureBy;
	String transactionId;
	
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getClosureBy() {
		return closureBy;
	}
	public void setClosureBy(String closureBy) {
		this.closureBy = closureBy;
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
	public String getPriorityDescription() {
		return priorityDescription;
	}
	public void setPriorityDescription(String priorityDescription) {
		this.priorityDescription = priorityDescription;
	}
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	
}
