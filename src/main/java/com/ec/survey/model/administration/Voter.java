package com.ec.survey.model.administration;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "VOTERS", uniqueConstraints = {@UniqueConstraint(columnNames={"VOTER_ECMONIKER", "VOTER_SURVEY"}, name="VOTER_ECMONIKER_SURVEY")}, indexes = { @Index(name = "SURVEY_UID_IDX", columnList = "VOTER_SURVEY") })
public class Voter {
	
	private Integer id;	
	private String givenName;
	private String surname;
	private String ecMoniker;
	private boolean voted;
	private String surveyUid;
	private Date created;

	@Id
	@Column(name = "VOTER_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Transient 
	public String getDisplayName()
	{
		if (givenName != null && givenName.length() > 0)
		{
			return givenName + " " + surname;
		}

		return ecMoniker;
	}
		
	@Column(name = "VOTER_GN")
	public String getGivenName() {
		return givenName;
	}	
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	
	@Column(name = "VOTER_SN")
	public String getSurname() {
		return surname;
	}	
	public void setSurname(String surname) {
		this.surname = surname;
	}
		
	@Column(name = "VOTER_ECMONIKER")
	public String getEcMoniker() {
		return ecMoniker;
	}
	public void setEcMoniker(String ecMoniker) {
		this.ecMoniker = ecMoniker;
	}

	@Column(name = "USER_VOTED")
	public boolean getVoted() {
		return voted;
	}
	public void setVoted(boolean voted) {
		this.voted = voted;
	}
	
	@Column(name = "VOTER_SURVEY")
	public String getSurveyUid() {
		return surveyUid;
	}
	public void setSurveyUid(String surveyUid) {
		this.surveyUid = surveyUid;
	}
	
	@Column(name = "VOTER_CREATED")
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
}
