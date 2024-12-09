package com.ec.survey.model.chargeback;

import com.ec.survey.tools.ConversionTools;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "SUBMITTEDCONTRIBUTION", indexes = {@Index(name = "SC_D_SUID", columnList = "SUBMITTEDCONTRIBUTION_SUBMITTED, SUBMITTEDCONTRIBUTION_SURVEY_UID")}, uniqueConstraints = {@UniqueConstraint(columnNames={"SUBMITTEDCONTRIBUTION_SURVEY_UID", "SUBMITTEDCONTRIBUTION_AS_ID"}, name="SC_SUID_ASID")})
public class SubmittedContribution implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer answerSetID;
	private String surveyUID;
	private String organisation;
	private Date submitted;
		
	@Id
	@Column(name = "SUBMITTEDCONTRIBUTION_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "SUBMITTEDCONTRIBUTION_SURVEY_UID")
	public String getSurveyUID() {
		return surveyUID;
	}
	public void setSurveyUID(String surveyUID) {
		this.surveyUID = surveyUID;
	}
	
	@Column(name = "SUBMITTEDCONTRIBUTION_AS_ID")
	public Integer getAnswerSetID() {
		return answerSetID;
	}
	public void setAnswerSetID(Integer answerSetID) {
		this.answerSetID = answerSetID;
	}

	@Column(name = "SUBMITTEDCONTRIBUTION_SUBMITTED")
	@DateTimeFormat(pattern = ConversionTools.DateTimeFormat)
	public Date getSubmitted() {
		return submitted;
	}
	public void setSubmitted(Date submitted) {
		this.submitted = submitted;
	}
	
	@Column(name = "SUBMITTEDCONTRIBUTION_ORG")
	public String getOrganisation() {
		return organisation;
	}
	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}
}
