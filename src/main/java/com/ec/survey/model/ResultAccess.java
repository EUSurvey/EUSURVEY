package com.ec.survey.model;

import com.ec.survey.tools.ConversionTools;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import javax.persistence.*;

/**
 * Represents a privilege that a user has to access the results of a survey
 */
@Entity
@Table(name = "RESULTACCESS", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "RESACC_USER", "SURVEY" }, name = "RESACC_USER") })
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ResultAccess {

	private int id;
	private int user;
	private int owner;
	private String surveyUID;
	private boolean readonly;
	private Date date;
	private String userName;
	private ResultFilter resultFilter;
	private String filter;
	private String readonlyFilterQuestions;
	
	protected static final Logger logger = Logger.getLogger(ResultAccess.class);

	@Id
	@Column(name = "RESACC_ID")
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "RESACC_USER")
	public int getUser() {
		return user;
	}

	public void setUser(int user) {
		this.user = user;
	}
	
	@Column(name = "RESACC_OWNER")
	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	@Column(name = "RESACC_READONLY")
	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	@Column(name = "RESACC_DATE")
	@DateTimeFormat(pattern = ConversionTools.DateTimeFormat)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "SURVEY")
	public String getSurveyUID() {
		return surveyUID;
	}

	public void setSurveyUID(String surveyUID) {
		this.surveyUID = surveyUID;
	}
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="id_resflt")
	public ResultFilter getResultFilter() {
		return resultFilter;
	}	
	public void setResultFilter(ResultFilter resultFilter) {
		this.resultFilter = resultFilter;
	}

	@Transient
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Transient
	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	@Column(name = "RESACC_ROQUESTIONS")
	public String getReadonlyFilterQuestions() {
		return readonlyFilterQuestions;
	}

	public void setReadonlyFilterQuestions(String readonlyFilterQuestions) {
		this.readonlyFilterQuestions = readonlyFilterQuestions;
	}
}
