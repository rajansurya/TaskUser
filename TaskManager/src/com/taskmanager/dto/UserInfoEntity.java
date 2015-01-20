/**
 * 
 */
package com.taskmanager.dto;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

/**
 * @author Maksood
 *
 */
public class UserInfoEntity implements Serializable {
	private Long id;
	private String firstName;
	private String lastName;
	private String middleName;
	private String email_id;
	private String operating_system;
	private String reg_status;
	private Time last_login_date;
	private Date reg_date;
	private String user_token;
	private String mobile_number;
	
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmail_id() {
		return email_id;
	}
	public void setEmail_id(String email_id) {
		this.email_id = email_id;
	}
	public String getOperating_system() {
		return operating_system;
	}
	public void setOperating_system(String operating_system) {
		this.operating_system = operating_system;
	}
	public String getReg_status() {
		return reg_status;
	}
	public void setReg_status(String reg_status) {
		this.reg_status = reg_status;
	}
	public Time getLast_login_date() {
		return last_login_date;
	}
	public void setLast_login_date(Time last_login_date) {
		this.last_login_date = last_login_date;
	}
	public Date getReg_date() {
		return reg_date;
	}
	public void setReg_date(Date reg_date) {
		this.reg_date = reg_date;
	}
	public String getUser_token() {
		return user_token;
	}
	public void setUser_token(String user_token) {
		this.user_token = user_token;
	}
	public String getMobile_number() {
		return mobile_number;
	}
	public void setMobile_number(String mobile_number) {
		this.mobile_number = mobile_number;
	}
	
	

	

}
