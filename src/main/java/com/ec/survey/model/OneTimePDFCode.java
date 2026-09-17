package com.ec.survey.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "PDFCODE")
public class OneTimePDFCode {

	private int id;
	private String surveyUid;
	private int answerSetId;
	private String code;
	private Date created;

	public OneTimePDFCode(){}

	public OneTimePDFCode(String survey, int answerSet) {
		created = new Date();
		surveyUid = survey;
		answerSetId = answerSet;
		code = UUID.randomUUID().toString();
	}
	
	@Id
	@Column(name = "PDFCODE_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "PDFCODE_SURVEY")
	public String getSurveyUid() {
		return surveyUid;
	}	
	public void setSurveyUid(String surveyUid) {
		this.surveyUid = surveyUid;
	}
	
	@Column(name = "PDFCODE_ANSWERSET")
	public int getAnswerSetId() {
		return answerSetId;
	}	
	public void setAnswerSetId(int answerSetId) {
		this.answerSetId = answerSetId;
	}
	
	@Column(name = "PDFCODE_CODE", unique = true, nullable = false)
	public String getCode() {
		return code;
	}	
	public void setCode(String code) {
		this.code = code;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PR_CREATED", nullable = false)
	@DateTimeFormat(pattern="dd/MM/yyyy")
	public Date getCreated() {
		return created;
	}	
	public void setCreated(Date created) {
		this.created = created;
	}
}
