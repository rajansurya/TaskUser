package com.taskmanager.bean;

public class OTPValidateDTO {
	
//	{"regId":"123456"
//    "status":"Complete",
//    "errorStatus":"0",
//    "errorMessage":"Sucess",
//    "displayMessage":"Registration Complete"}
//	Registration_ID	Mobile_Number	OTP	OTP_Status

	private String regID;
	private String mobileNo;
	private String OTP;
	private String OTP_status;
	private String DisplayMessage;
	private String ErrorMessage;
	public String getRegID() {
		return regID;
	}
	public void setRegID(String regID) {
		this.regID = regID;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getOTP() {
		return OTP;
	}
	public void setOTP(String oTP) {
		OTP = oTP;
	}
	public String getOTP_status() {
		return OTP_status;
	}
	public void setOTP_status(String oTP_status) {
		OTP_status = oTP_status;
	}
	public String getDisplayMessage() {
		return DisplayMessage;
	}
	public void setDisplayMessage(String displayMessage) {
		DisplayMessage = displayMessage;
	}
	public String getErrorMessage() {
		return ErrorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		ErrorMessage = errorMessage;
	}
	

}
