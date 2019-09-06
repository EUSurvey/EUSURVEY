package com.ec.survey.model;

public class ImportTranslationResult {

	private boolean success;
	private String message;
	private String language;
	private String uid;
	private int surveyId;
	private boolean exists;
	private boolean ignored;
	private String[] pivotLabels;
	private String[] keys;
	private String[] labels;
	private String[] invalidKeys = new String[0];
	
	
	public ImportTranslationResult()
	{}


	public boolean isSuccess() {
		return success;
	}


	public void setSuccess(boolean success) {
		this.success = success;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public boolean isExists() {
		return exists;
	}


	public void setExists(boolean exists) {
		this.exists = exists;
	}


	public String[] getPivotLabels() {
		return pivotLabels;
	}


	public void setPivotLabels(String[] pivotLabels) {
		this.pivotLabels = pivotLabels;
	}


	public String[] getLabels() {
		return labels;
	}


	public void setLabels(String[] labels) {
		this.labels = labels;
	}


	public boolean isIgnored() {
		return ignored;
	}


	public void setIgnored(boolean ignored) {
		this.ignored = ignored;
	}


	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}


	public int getSurveyId() {
		return surveyId;
	}


	public void setSurveyId(int surveyId) {
		this.surveyId = surveyId;
	}


	public String getUid() {
		return uid;
	}


	public void setUid(String uid) {
		this.uid = uid;
	}


	public String[] getKeys() {
		return keys;
	}


	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	public String[] getInvalidKeys() {
		return invalidKeys;
	}

	public void setInvalidKeys(String[] invalidKeys) {
		this.invalidKeys = invalidKeys;		
	}	
	
}
