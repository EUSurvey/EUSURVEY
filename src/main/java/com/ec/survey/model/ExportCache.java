package com.ec.survey.model;

import javax.persistence.*;

@Entity
@Table(name="EXPORTCACHE")
public class ExportCache {

	private int id;
	private int surveyId;	
	private String filterHash;
	private String type;
	private String uid;
	
	@Id
	@Column(name = "EXPCA_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="SURVEYID")
	public int getSurveyId() {
		return surveyId;
	}
	public void setSurveyId(int surveyId) {
		this.surveyId = surveyId;
	}
	
	@Column(name="FILTER")
	public String getFilterHash() {
		return filterHash;
	}
	public void setFilterHash(String filterHash) {
		this.filterHash = filterHash;
	}
	
	@Column(name="EXTYPE")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Column(name="UID")
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}

}
