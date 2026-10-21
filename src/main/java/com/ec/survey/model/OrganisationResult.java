package com.ec.survey.model;

import java.util.Map;

public class OrganisationResult {

	private int allSurveys;
	private int allContributions;
	private int surveysThisYear;
	private int surveysLastYear;
	private int contributionsThisYear;
	private int contributionsLastYear;
	private Map<Integer, String> managers;
	private Map<Integer, String> whitelist;

	public int getAllSurveys() {
		return allSurveys;
	}

	public void setAllSurveys(int allSurveys) {
		this.allSurveys = allSurveys;
	}

	public int getAllContributions() {
		return allContributions;
	}

	public void setAllContributions(int allContributions) {
		this.allContributions = allContributions;
	}

	public int getSurveysThisYear() {
		return surveysThisYear;
	}

	public void setSurveysThisYear(int surveysThisYear) {
		this.surveysThisYear = surveysThisYear;
	}

	public int getSurveysLastYear() {
		return surveysLastYear;
	}

	public void setSurveysLastYear(int surveysLastYear) {
		this.surveysLastYear = surveysLastYear;
	}

	public int getContributionsThisYear() {
		return contributionsThisYear;
	}

	public void setContributionsThisYear(int contributionsThisYear) {
		this.contributionsThisYear = contributionsThisYear;
	}

	public int getContributionsLastYear() {
		return contributionsLastYear;
	}

	public void setContributionsLastYear(int contributionsLastYear) {
		this.contributionsLastYear = contributionsLastYear;
	}

    public Map<Integer, String> getManagers() { return managers; }

    public void setManagers(Map<Integer, String> managers) { this.managers = managers; }

    public Map<Integer, String> getWhitelist() { return whitelist;  }

    public void setWhitelist(Map<Integer, String> whitelist) { this.whitelist = whitelist; }
}
