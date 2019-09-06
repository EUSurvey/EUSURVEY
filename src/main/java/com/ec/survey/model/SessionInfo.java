package com.ec.survey.model;

import java.io.Serializable;

public class SessionInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int survey;	
	private int user;
	private int owner;
	private String language;
	private String shortname;
	
	public SessionInfo() {}
	
	public SessionInfo(int survey, int user, int owner, String language, String shortname) {
		this.survey = survey;
		this.user = user;
		this.language = language;
		this.shortname = shortname;
		this.owner = owner;
	}
	
	public String getShortnameForMenu()
	{
		if (shortname != null)
		{
			if (shortname.length() > 10)
			{
				return shortname.substring(0, 9) + "...";
			} else {
				return shortname;
			}
		}
		return "";
	}

	public int getSurvey() {
		return survey;
	}

	public void setSurvey(int survey) {
		this.survey = survey;
	}

	public int getUser() {
		return user;
	}

	public void setUser(int user) {
		this.user = user;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}
			
}
