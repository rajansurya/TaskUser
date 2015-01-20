package com.taskmanager.bean;

public class NotationDto {
	
	private String notaionName;
	
	private int count;

	public String getNotaionName() {
		return notaionName;
	}

	public void setNotaionName(String notaionName) {
		this.notaionName = notaionName;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	@Override
	public boolean equals(Object object) {
		NotationDto dto = (NotationDto)object;
		return notaionName.equalsIgnoreCase(dto.getNotaionName());
	}
	

}
