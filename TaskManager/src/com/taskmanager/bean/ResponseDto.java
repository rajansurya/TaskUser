package com.taskmanager.bean;

public class ResponseDto {
private String status;
private String messageId;
private String messageDescription;
private String redirectUrl;

public String getMessageDescription() {
	return messageDescription;
}
public void setMessageDescription(String messageDescription) {
	this.messageDescription = messageDescription;
}
public String getRedirectUrl() {
	return redirectUrl;
}
public void setRedirectUrl(String redirectUrl) {
	this.redirectUrl = redirectUrl;
}
public String getStatus() {
	return status;
}
public void setStatus(String status) {
	this.status = status;
}
public String getMessageId() {
	return messageId;
}
public void setMessageId(String messageId) {
	this.messageId = messageId;
}

}
