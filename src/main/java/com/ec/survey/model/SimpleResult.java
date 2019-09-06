package com.ec.survey.model;

public class SimpleResult {

	private boolean success;
	private String result;
	
	public SimpleResult()
	{}

	public SimpleResult(String result, boolean success) {
		this.result = result;
		this.success = success;
	}
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	public boolean getSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
}
