package com.ec.survey.model.chargeback;

import com.ec.survey.tools.ConversionTools;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "PUBLISHEDSURVEY", indexes = {@Index(name = "PS_D_SUID", columnList = "PUBLISHEDSURVEY_PUBLISHED, PUBLISHEDSURVEY_SURVEY_UID")}, uniqueConstraints = {@UniqueConstraint(columnNames={"PUBLISHEDSURVEY_SURVEY_UID"}, name="PS_SUID")})
public class PublishedSurvey implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String surveyUID;
	private String organisation;
	private Date published;
		
	@Id
	@Column(name = "PUBLISHEDSURVEY_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "PUBLISHEDSURVEY_SURVEY_UID")
	public String getSurveyUID() {
		return surveyUID;
	}
	public void setSurveyUID(String surveyUID) {
		this.surveyUID = surveyUID;
	}

	@Column(name = "PUBLISHEDSURVEY_PUBLISHED")
	@DateTimeFormat(pattern = ConversionTools.DateTimeFormat)
	public Date getPublished() {
		return published;
	}

	public void setPublished(Date date) {
		this.published = date;
	}
	
	@Column(name = "PUBLISHEDSURVEY_ORG")
	public String getOrganisation() {
		return organisation;
	}
	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

}
