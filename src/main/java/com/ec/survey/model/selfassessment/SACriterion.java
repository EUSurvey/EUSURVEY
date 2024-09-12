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
@Table(name = "SACRITERIA", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "SACRITERIA_NAME", "SACRITERIA_SURVEY" }, name = "NAME_SURVEY") })
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SACriterion implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String surveyUID;
	private String name;
	private String acronym;
	private String type;
	
	@Id
	@Column(name = "SACRITERIA_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "SACRITERIA_SURVEY")
	public String getSurveyUID() {
		return surveyUID;
	}

	public void setSurveyUID(String surveyUID) {
		this.surveyUID = surveyUID;
	}
	
	@Column(name = "SACRITERIA_NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "SACRITERIA_ACRONYM")
	public String getAcronym() {
		return acronym;
	}

	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	@Column(name = "SACRITERIA_TYPE")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


}
