package com.ec.survey.model;

import com.ec.survey.tools.ConversionTools;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "DELETEDCONTRIBUTIONS")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DeletedContribution implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String contributionCode;
	private String surveyUid;
	private Date creationDate;
	private Date deletionDate;
	
	@Id
	@Column(name = "DELETEDCONTRIBUTIONS_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "DELETEDCONTRIBUTIONS_CODE")
	public String getContributionCode() {
		return contributionCode;
	}
	public void setContributionCode(String contributionCode) {
		this.contributionCode = contributionCode;
	}
	
	@Column(name = "DELETEDCONTRIBUTIONS_SURVEY")
	public String getSurveyUid() {
		return surveyUid;
	}
	public void setSurveyUid(String surveyUid) {
		this.surveyUid = surveyUid;
	}	
	
	@Column(name = "DELETEDCONTRIBUTIONS_CREATED")
	@DateTimeFormat(pattern=ConversionTools.DateTimeFormat)
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	@Column(name = "DELETEDCONTRIBUTIONS_DELETED")
	@DateTimeFormat(pattern=ConversionTools.DateTimeFormat)
	public Date getDeletionDate() {
		return deletionDate;
	}
	public void setDeletionDate(Date deletionDate) {
		this.deletionDate = deletionDate;
	}	
}

