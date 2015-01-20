package com.taskmanager.dto;

import java.util.ArrayList;
import java.util.List;

public class Contact {
	private String name;
	private String number;
	private boolean isSelected;
	
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	private String regStatus;
	
	public String getRegStatus() {
		return regStatus;
	}
	public void setRegStatus(String regStatus) {
		this.regStatus = regStatus;
	}
	private List<String> numbers;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public List<String> getNumbers() {
		if(numbers==null){
			numbers=new ArrayList<String>();
		}
		return numbers;
	}
	public void setNumbers(List<String> numbers) {
		this.numbers = numbers;
	}
	@Override
	public boolean equals(Object o) {
		Contact contact=(Contact)o;
		
		return contact.getName().equalsIgnoreCase(name);
	}
	@Override
	public int hashCode() {
		
		return name.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}
}
