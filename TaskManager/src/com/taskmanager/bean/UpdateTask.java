package com.taskmanager.bean;


public class UpdateTask {
	private String Task_Desc;
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
	private String  IsTaskUpdateSync="N";
	private String groupId = "";
	private String task_id;
	private String priority;
	private String target_date;
	private String reminderTime;
	//
	private String creationDate;
	private String updateDate;
	private long updateDateTime;
	private String assignFrom;
	private String assignTo;

	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	public long getUpdateDateTime() {
		return updateDateTime;
	}
	public void setUpdateDateTime(long updateDateTime) {
		this.updateDateTime = updateDateTime;
	}
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
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
	private long creationDateime;
	private long closureDate;
	
	//
	
	
	
	public String getReminderTime() {
		return reminderTime;
	}
	public void setReminderTime(String reminderTime) {
		this.reminderTime = reminderTime;
	}
	public String getTarget_date() {
		return target_date;
	}
	public void setTarget_date(String target_date) {
		this.target_date = target_date;
	}
	public String getTask_id() {
		return task_id;
	}
	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getTask_Desc() {
		return Task_Desc;
	}
	public void setTask_Desc(String task_Desc) {
		Task_Desc = task_Desc;
	}
	public String getIsTaskUpdateSync() {
		return IsTaskUpdateSync;
	}
	public void setIsTaskUpdateSync(String isTaskUpdateSync) {
		IsTaskUpdateSync = isTaskUpdateSync;
	}
	public String getTASK_SYNC_TYPE() {
		return TASK_SYNC_TYPE;
	}
	public void setTASK_SYNC_TYPE(String tASK_SYNC_TYPE) {
		TASK_SYNC_TYPE = tASK_SYNC_TYPE;
	}
	private String  TASK_SYNC_TYPE;
}
