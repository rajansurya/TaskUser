/**
 * 
 */
package com.taskmanager.dto;

import java.util.Date;

/**
 * @author Maksood
 *
 */
public class MessageInfoEntity {
	private Long id;
	private Long task_id;
	private String message_from;
	private String message_to;
	private String message_description;
	private Date created_date;
	private Boolean ismessage_read;
	private String messageId;
	private UserInfoEntity userInfoFrom;
	private UserInfoEntity userInfoTo;
	private String messageSysnStatus;
	private String messageReadStatus;
	private String createdDate;
	private String transactionId;
	
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getMessageReadStatus() {
		return messageReadStatus;
	}
	public void setMessageReadStatus(String messageReadStatus) {
		this.messageReadStatus = messageReadStatus;
	}
	public String getMessageSysnStatus() {
		return messageSysnStatus;
	}
	public void setMessageSysnStatus(String messageSysnStatus) {
		this.messageSysnStatus = messageSysnStatus;
	}
	public UserInfoEntity getUserInfoFrom() {
		return userInfoFrom;
	}
	public void setUserInfoFrom(UserInfoEntity userInfoFrom) {
		this.userInfoFrom = userInfoFrom;
	}
	public UserInfoEntity getUserInfoTo() {
		return userInfoTo;
	}
	public void setUserInfoTo(UserInfoEntity userInfoTo) {
		this.userInfoTo = userInfoTo;
	}
	public Boolean getIsmessage_read() {
		return ismessage_read;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getTask_id() {
		return task_id;
	}
	public void setTask_id(Long task_id) {
		this.task_id = task_id;
	}
	public String getMessage_from() {
		return message_from;
	}
	public void setMessage_from(String message_from) {
		this.message_from = message_from;
	}
	public String getMessage_to() {
		return message_to;
	}
	public void setMessage_to(String message_to) {
		this.message_to = message_to;
	}
	public String getMessage_description() {
		return message_description;
	}
	public void setMessage_description(String message_description) {
		this.message_description = message_description;
	}
	public Date getCreated_date() {
		return created_date;
	}
	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}
	public Boolean isIsmessage_read() {
		return ismessage_read;
	}
	public void setIsmessage_read(Boolean ismessage_read) {
		this.ismessage_read = ismessage_read;
	}
	
	
	
}
