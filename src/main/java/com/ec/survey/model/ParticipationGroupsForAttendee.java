package com.ec.survey.model;

public class ParticipationGroupsForAttendee {
	
	private String attendeeName;
	private String attendeeEmail;
	private String participationGroupName;
	private int participationGroupId;
	private String surveyAlias;
	
	public String getAttendeeName() {
		return attendeeName;
	}
	public void setAttendeeName(String attendeeName) {
		this.attendeeName = attendeeName;
	}
	public String getAttendeeEmail() {
		return attendeeEmail;
	}
	public void setAttendeeEmail(String attendeeEmail) {
		this.attendeeEmail = attendeeEmail;
	}
	public String getParticipationGroupName() {
		return participationGroupName;
	}
	public void setParticipationGroupName(String participationGroupName) {
		this.participationGroupName = participationGroupName;
	}
	public String getSurveyAlias() {
		return surveyAlias;
	}
	public void setSurveyAlias(String surveyAlias) {
		this.surveyAlias = surveyAlias;
	}
	public int getParticipationGroupId() {
		return participationGroupId;
	}
	public void setParticipationGroupId(int participationGroupId) {
		this.participationGroupId = participationGroupId;
	}

}
