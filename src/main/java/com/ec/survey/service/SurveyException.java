package com.ec.survey.service;

public class SurveyException extends Exception {

	private static final long serialVersionUID = 1L;

	private int surveyID;

	public SurveyException(Integer surveyID) {
		this.surveyID = surveyID;
	}

	public int getSurveyID() {
		return surveyID;
	}
}
