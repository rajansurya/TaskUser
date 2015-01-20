package com.taskmanager.bean;

import java.util.List;

public class CreateTaskDTO {
	
	private String ID;
	private String status;
	private String DisplayMessage;
	private List<TaskInfoDto> taskList;
	private List<String > notRegisteredNo;
	
	
	public List<String> getNotRegisteredNo() {
		return notRegisteredNo;
	}
	public void setNotRegisteredNo(List<String> notRegisteredNo) {
		this.notRegisteredNo = notRegisteredNo;
	}
	
	
	public List<TaskInfoDto> getTaskList() {
		return taskList;
	}
	public void setTaskList(List<TaskInfoDto> taskList) {
		this.taskList = taskList;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDisplayMessage() {
		return DisplayMessage;
	}
	public void setDisplayMessage(String displayMessage) {
		DisplayMessage = displayMessage;
	}
	
	

}
