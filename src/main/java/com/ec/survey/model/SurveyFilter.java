package com.ec.survey.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.ec.survey.model.administration.User;

public class SurveyFilter implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private User user;
	private String shortname;
	private String title;
	private String uid;
	private String owner;
	private Date generatedFrom;
	private Date generatedTo;
	private Date publishedFrom;
	private Date publishedTo;
	private Date firstPublishedFrom;
	private Date firstPublishedTo;
	private Date startFrom;
	private Date startTo;
	private Date endFrom;	
	private Date endTo;
	private String access;
	private String status;
	private String selector;
	private String keywords;
	private String[] languages;
	private String sortKey = "survey_created";
	private String sortOrder = "DESC";
	private String surveys = "existing";
	private String surveyType = "all";
	private String userDepartment;
	private Boolean deleted = null;
	private Date deletedFrom;
	private Date deletedTo;
	private Integer minReported = null;
	private Integer minContributions = null;
	private Boolean frozen = null;
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	public String getShortname() {
		return shortname;
	}
	public void setShortname(String name) {
		this.shortname = name;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	@DateTimeFormat(pattern="dd/MM/yyyy")
	public Date getGeneratedFrom() {
		return generatedFrom;
	}
	public void setGeneratedFrom(Date generatedFrom) {
		this.generatedFrom = generatedFrom;
	}
	
	@DateTimeFormat(pattern="dd/MM/yyyy")
	public Date getGeneratedTo() {
		return generatedTo;
	}
	public void setGeneratedTo(Date generatedTo) {
		this.generatedTo = generatedTo;
	}
	
	@DateTimeFormat(pattern="dd/MM/yyyy")
	public Date getStartFrom() {
		return startFrom;
	}
	public void setStartFrom(Date startFrom) {
		this.startFrom = startFrom;
	}
	
	@DateTimeFormat(pattern="dd/MM/yyyy")
	public Date getStartTo() {
		return startTo;
	}
	public void setStartTo(Date startTo) {
		this.startTo = startTo;
	}
	
	@DateTimeFormat(pattern="dd/MM/yyyy")
	public Date getEndFrom() {
		return endFrom;
	}
	public void setEndFrom(Date endFrom) {
		this.endFrom = endFrom;
	}
	
	@DateTimeFormat(pattern="dd/MM/yyyy")
	public Date getEndTo() {
		return endTo;
	}
	public void setEndTo(Date endTo) {
		this.endTo = endTo;
	}	
	
	public String getAccess() {
		return access;
	}
	public void setAccess(String access) {
		this.access = access;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getSelector() {
		return selector;
	}
	public void setSelector(String selector) {
		this.selector = selector;
	}
	
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	
	public String[] getLanguages() {
		return languages;
	}
	public void setLanguages(String[] languages) {
		this.languages = languages;
	}
	
	public boolean containsLanguage(String code)
	{
		if (languages != null)
		for (String c : languages)
		{
			if (c.equalsIgnoreCase(code)) return true;
		}
		return false;
	}
	
	public String getSortKey() {
		return sortKey;
	}
	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}
	
	public String getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	@DateTimeFormat(pattern="dd/MM/yyyy")
	public Date getPublishedFrom() {
		return publishedFrom;
	}
	public void setPublishedFrom(Date publishedFrom) {
		this.publishedFrom = publishedFrom;
	}
	
	@DateTimeFormat(pattern="dd/MM/yyyy")
	public Date getPublishedTo() {
		return publishedTo;
	}
	public void setPublishedTo(Date publishedTo) {
		this.publishedTo = publishedTo;
	}
	
	@DateTimeFormat(pattern="dd/MM/yyyy")
	public Date getFirstPublishedFrom() {
		return firstPublishedFrom;
	}
	public void setFirstPublishedFrom(Date firstPublishedFrom) {
		this.firstPublishedFrom = firstPublishedFrom;
	}
	
	@DateTimeFormat(pattern="dd/MM/yyyy")
	public Date getFirstPublishedTo() {
		return firstPublishedTo;
	}
	public void setFirstPublishedTo(Date firstPublishedTo) {
		this.firstPublishedTo = firstPublishedTo;
	}
	
	public String getSurveys() {
		return surveys;
	}
	public void setSurveys(String surveys) {
		this.surveys = surveys;
	}
	
	public String getType() {
		return surveyType;
	}
	public void setType(String surveyType) {
		this.surveyType = surveyType;
	}
	
	public String getUserDepartment() {
		return userDepartment;
	}
	public void setUserDepartment(String userDepartment) {
		this.userDepartment = userDepartment;
	}
	
	public Boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	
	@DateTimeFormat(pattern="dd/MM/yyyy")
	public Date getDeletedFrom() {
		return deletedFrom;
	}
	public void setDeletedFrom(Date deletedFrom) {
		this.deletedFrom = deletedFrom;
	}
	
	@DateTimeFormat(pattern="dd/MM/yyyy")
	public Date getDeletedTo() {
		return deletedTo;
	}
	public void setDeletedTo(Date deletedTo) {
		this.deletedTo = deletedTo;
	}
	
	public Integer getMinReported() {
		return minReported;
	}
	public void setMinReported(Integer minReported) {
		this.minReported = minReported;
	}
	
	public Integer getMinContributions() {
		return minContributions;
	}
	public void setMinContributions(Integer minContributions) {
		this.minContributions = minContributions;
	}
	
	public Boolean getFrozen() {
		return frozen;
	}
	public void setFrozen(Boolean frozen) {
		this.frozen = frozen;
	}
}
