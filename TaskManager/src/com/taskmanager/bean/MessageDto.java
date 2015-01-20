package com.taskmanager.bean;

public class MessageDto {
private String Message_ID;
private String M_Desc;
private String M_From;
private String M_To;
private String IsMessageSync="N";
private String IsRead="N";
private String Task_ID;
private String M_From_Name;
private String Creation_Date;
private String transactionId;

public String getTransactionId() {
	return transactionId;
}
public void setTransactionId(String transactionId) {
	this.transactionId = transactionId;
}
private boolean IsMessage;
private String msg_Type;
public String getMsg_Type() {
	return msg_Type;
}
public void setMsg_Type(String msg_Type) {
	this.msg_Type = msg_Type;
}
public boolean getIsMessage() {
	return IsMessage;
}
public void setIsMessage(boolean isMessage) {
	IsMessage = isMessage;
}
public String getMessage_ID() {
	return Message_ID;
}
public void setMessage_ID(String message_ID) {
	Message_ID = message_ID;
}
public String getM_Desc() {
	return M_Desc;
}
public void setM_Desc(String m_Desc) {
	M_Desc = m_Desc;
}
public String getM_From() {
	return M_From;
}
public void setM_From(String m_From) {
	M_From = m_From;
}
public String getM_To() {
	return M_To;
}
public void setM_To(String m_To) {
	M_To = m_To;
}
public String getIsMessageSync() {
	return IsMessageSync;
}
public void setIsMessageSync(String isMessageSync) {
	IsMessageSync = isMessageSync;
}
public String getIsRead() {
	return IsRead;
}
public void setIsRead(String isRead) {
	IsRead = isRead;
}
public String getTask_ID() {
	return Task_ID;
}
public void setTask_ID(String task_ID) {
	Task_ID = task_ID;
}
public String getM_From_Name() {
	return M_From_Name;
}
public void setM_From_Name(String m_From_Name) {
	M_From_Name = m_From_Name;
}
public String getCreation_Date() {
	return Creation_Date;
}
public void setCreation_Date(String creation_Date) {
	Creation_Date = creation_Date;
}


}
