package com.ec.survey.model.selfassessment;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "SATARGETDATASETS", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "SATARGETDATASETS_NAME", "SATARGETDATASETS_SURVEY" }, name = "NAME_SURVEY") })
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SATargetDataset implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String surveyUID;
	private String name;
	
	@Id
	@Column(name = "SATARGETDATASETS_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "SATARGETDATASETS_NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "SATARGETDATASETS_SURVEY")
	public String getSurveyUID() {
		return surveyUID;
	}

	public void setSurveyUID(String surveyUID) {
		this.surveyUID = surveyUID;
	}
}
