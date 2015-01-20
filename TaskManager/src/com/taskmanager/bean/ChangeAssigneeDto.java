package com.taskmanager.bean;

public class ChangeAssigneeDto {
	
	String taskId;
	String Assign_To_No;
	String Assign_To_Name;
	String oldAssignee;
	String isAssigneeSync;
	String mobileNumber;
	String taskType;
	
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getIsAssigneeSync() {
		return isAssigneeSync;
	}
	public void setIsAssigneeSync(String isAssigneeSync) {
		this.isAssigneeSync = isAssigneeSync;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	public String getAssign_To_No() {
		return Assign_To_No;
	}
	public void setAssign_To_No(String assign_To_No) {
		Assign_To_No = assign_To_No;
	}
	public String getAssign_To_Name() {
		return Assign_To_Name;
	}
	public void setAssign_To_Name(String assign_To_Name) {
		Assign_To_Name = assign_To_Name;
	}
	public String getOldAssignee() {
		return oldAssignee;
	}
	public void setOldAssignee(String oldAssignee) {
		this.oldAssignee = oldAssignee;
	}

}
