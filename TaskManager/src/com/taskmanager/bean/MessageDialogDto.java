package com.taskmanager.bean;

import java.util.ArrayList;

import android.app.Dialog;

import com.taskmanager.dto.MessageInfoEntity;

public class MessageDialogDto {
	private  ArrayList<MessageInfoEntity> dialogMessgaeList;
	private String taskTypeDB;
	private String Taskid;
	private String AssignFromNum;
	private Dialog dialogView;
	private String unReadStatus;
	
	public String getUnReadStatus() {
		return unReadStatus;
	}
	public void setUnReadStatus(String unReadStatus) {
		this.unReadStatus = unReadStatus;
	}
	public Dialog getDialogView() {
		return dialogView;
	}
	public void setDialogView(Dialog dialogView) {
		this.dialogView = dialogView;
	}
	public ArrayList<MessageInfoEntity> getDialogMessgaeList() {
		return dialogMessgaeList;
	}
	public void setDialogMessgaeList( ArrayList<MessageInfoEntity> dialogMessgaeList) {
		this.dialogMessgaeList = dialogMessgaeList;
	}
	public String getTaskTypeDB() {
		return taskTypeDB;
	}
	public void setTaskTypeDB(String taskTypeDB) {
		this.taskTypeDB = taskTypeDB;
	}
	public String getTaskid() {
		return Taskid;
	}
	public void setTaskid(String taskid) {
		Taskid = taskid;
	}
	public String getAssignFromNum() {
		return AssignFromNum;
	}
	public void setAssignFromNum(String assignFromNum) {
		AssignFromNum = assignFromNum;
	}
	public String getAssignToNum() {
		return AssignToNum;
	}
	public void setAssignToNum(String assignToNum) {
		AssignToNum = assignToNum;
	}
	private String AssignToNum;

}
