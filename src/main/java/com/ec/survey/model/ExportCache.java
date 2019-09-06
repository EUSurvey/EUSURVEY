package com.ec.survey.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
	@GeneratedValue
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
