//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.11.04 at 04:56:55 PM IST 
//


package com.taskmanager.domain;



/**
 * <p>Java class for AssigneeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AssigneeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="assignFrom" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="assignFromName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="assignTo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="assignToName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */

public class AssigneeType {

    
    protected String assignFrom;
    
    protected String assignFromName;
    
    protected String assignTo;
    
    protected String assignToName;

	public String getAssignFrom() {
		return assignFrom;
	}

	public void setAssignFrom(String assignFrom) {
		this.assignFrom = assignFrom;
	}

	public String getAssignFromName() {
		return assignFromName;
	}

	public void setAssignFromName(String assignFromName) {
		this.assignFromName = assignFromName;
	}

	public String getAssignTo() {
		return assignTo;
	}

	public void setAssignTo(String assignTo) {
		this.assignTo = assignTo;
	}

	public String getAssignToName() {
		return assignToName;
	}

	public void setAssignToName(String assignToName) {
		this.assignToName = assignToName;
	}

   
}
