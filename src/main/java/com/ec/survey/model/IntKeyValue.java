package com.ec.survey.model;

public class IntKeyValue {

	private Integer key;
	private Integer value;
	
	public IntKeyValue()
	{}

	public IntKeyValue(Integer key, Integer value) {
		this.key = key;
		this.value = value;
	}
	
	public Integer getKey() {
		return key;
	}
	public void setKey(Integer key) {
		this.key = key;
	}
	
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	
}
