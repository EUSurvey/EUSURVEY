package com.ec.survey.model;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "VALIDCODE", uniqueConstraints = {@UniqueConstraint(columnNames={"VALIDCODE_CODE"},name="VALIDCODE_CODE")})
public class ValidCode {

	private Integer id;
	private String code;
	private String surveyUid;
	private Date created;
	
	public ValidCode() {}
	
	public ValidCode(String uniqueCode, String surveyUid) {
		code = uniqueCode;
		this.surveyUid = surveyUid;
		created = new Date();
	}
	
	@Id
	@Column(name = "VALIDCODE_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "VALIDCODE_CODE")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	@Column(name = "VALIDCODE_SURVEYUID")
	public String getSurveyUid() {
		return surveyUid;
	}
	public void setSurveyUid(String surveyUid) {
		this.surveyUid = surveyUid;
	}
	
	@Column(name = "VALIDCODE_DATE")
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	
}
