package com.ec.survey.model;

import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.*;
@Configurable
@Entity
@Table(name="STATISTICSREQUEST")
public class StatisticsRequest {
	
	private int id;
	private int surveyId;	
	private ResultFilter filter;
	private boolean allanswers;
	
	@Id
	@Column(name = "REQID")
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
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="id_resflt")
	public ResultFilter getFilter() {
		return filter;
	}
	public void setFilter(ResultFilter filter) {
		this.filter = filter;
	}
	
	@Column(name="ALLANSWERS")
	public boolean isAllanswers() {
		return allanswers;
	}
	public void setAllanswers(boolean allanswers) {
		this.allanswers = allanswers;
	}
		
}
