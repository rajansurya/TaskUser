package com.taskmanager.bean;

public class MessageListDTO {

	private String msgId;
	private String oldMessageId;
	private String taskId;
	private String mFrom;
	private String mFromName;
	private String mTo;
	private String mToName;
	private String mDesc;
	private String creationDate;
	private String transactioId;
	
	public String getTransactioId() {
		return transactioId;
	}
	public void setTransactioId(String transactioId) {
		this.transactioId = transactioId;
	}
	public String getOldMessageId() {
		return oldMessageId;
	}
	public void setOldMessageId(String oldMessageId) {
		this.oldMessageId = oldMessageId;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getmFrom() {
		return mFrom;
	}
	public void setmFrom(String mFrom) {
		this.mFrom = mFrom;
	}
	public String getmTo() {
		return mTo;
	}
	public void setmTo(String mTo) {
		this.mTo = mTo;
	}
	public String getmDesc() {
		return mDesc;
	}
	public void setmDesc(String mDesc) {
		this.mDesc = mDesc;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public String getmFromName() {
		return mFromName;
	}
	public void setmFromName(String mFromName) {
		this.mFromName = mFromName;
	}
	public String getmToName() {
		return mToName;
	}
	public void setmToName(String mToName) {
		this.mToName = mToName;
	}
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

}

