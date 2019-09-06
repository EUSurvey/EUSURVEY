package com.ec.survey.model;

import com.ec.survey.tools.Tools;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "RESULTFILTER")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ResultFilter implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private Integer userId;
	private String invitation;
	private String caseId;
	private String draftId;
	private String user;
	private int surveyId;
	private String surveyUid;
	private String surveyShortname;
	private String surveyTitle;
	private String surveyStatus;
	private String status;
	private String surveyPublishedResults;
	private Date surveyEndDateFrom;
	private Date surveyEndDateTo;
	private Date generatedFrom;
	private Date generatedTo;
	private Date updatedFrom;
	private Date updatedTo;
	private Set<String> languages;
	private String sortKey = "created";
	private String sortOrder = "DESC";
	private Map<String, String> filterValues = new HashMap<>();
	private Set<String> visibleQuestions = new HashSet<>();
	private Set<String> exportedQuestions = new HashSet<>();
	private Boolean createdOrUpdated = false;
	private Boolean onlyReallyUpdated = false;
	private Boolean noTestAnswers = false;
	
	public void clearResultFilter() {
		invitation = null;
		caseId = null;
		draftId = null;
		user = null;
		generatedFrom = null;
		generatedTo = null;
		updatedFrom = null;
		updatedTo = null;
		if (languages != null) languages.clear();
		sortKey = "created";
		sortOrder = "DESC";
		if (filterValues != null) filterValues.clear();
		createdOrUpdated = false;
		onlyReallyUpdated = false;
		noTestAnswers = false;
	}	

	public void clearSelectedQuestions() {
		visibleQuestions.clear();
		exportedQuestions.clear();		
	}

	@Id
	@Column(name = "RESFILTER_ID")
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "RESFILTER_DATEFROM")
	@DateTimeFormat(pattern="dd/MM/yyyy")
	public Date getGeneratedFrom() {
		return generatedFrom;
	}
	public void setGeneratedFrom(Date generatedFrom) {
		this.generatedFrom = generatedFrom;
	}
	
	@Column(name = "RESFILTER_DATETO")
	@DateTimeFormat(pattern="dd/MM/yyyy")
	public Date getGeneratedTo() {
		return generatedTo;
	}
	public void setGeneratedTo(Date generatedTo) {
		this.generatedTo = generatedTo;
	}
	
	@Column(name = "RESFILTER_UPDATEFROM")
	@DateTimeFormat(pattern="dd/MM/yyyy")
	public Date getUpdatedFrom() {
		return updatedFrom;
	}
	public void setUpdatedFrom(Date updatedFrom) {
		this.updatedFrom = updatedFrom;
	}
	
	@Column(name = "RESFILTER_UPDATETO")
	@DateTimeFormat(pattern="dd/MM/yyyy")
	public Date getUpdatedTo() {
		return updatedTo;
	}
	public void setUpdatedTo(Date updatedTo) {
		this.updatedTo = updatedTo;
	}
	
	@ElementCollection
	public Set<String> getLanguages() {
		return languages;
	}
	public void setLanguages(Set<String> languages) {
		this.languages = languages;
	}
	
	@Transient
	public boolean containsLanguage(String code)
	{
		if (languages != null)
		for (String c : languages)
		{
			if (c.equalsIgnoreCase(code)) return true;
		}
		return false;
	}
	
	@Column(name = "RESFILTER_SORTKEY")
	public String getSortKey() {
		return sortKey;
	}
	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}
	
	@Column(name = "RESFILTER_SORTORDER")
	public String getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	@Column(name = "RESFILTER_INV")
	public String getInvitation() {
		return invitation;
	}
	public void setInvitation(String invitation) {
		this.invitation = invitation;
	}
	
	@Column(name = "RESFILTER_CASE")
	public String getCaseId() {
		return caseId;
	}
	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}
	
	@Column(name = "RESFILTER_CRORUPD")
	public Boolean getCreatedOrUpdated() {
		return createdOrUpdated;
	}
	public void setCreatedOrUpdated(Boolean createdOrUpdated) {
		this.createdOrUpdated = createdOrUpdated;
	}
	
	@Column(name = "RESFILTER_REALUPD")
	public Boolean getOnlyReallyUpdated() {
		return onlyReallyUpdated;
	}
	public void setOnlyReallyUpdated(Boolean onlyReallyUpdated) {
		this.onlyReallyUpdated = onlyReallyUpdated;
	}
	
	@Column(name = "RESFILTER_NOTESTANS")
	public Boolean getNoTestAnswers() {
		return noTestAnswers;
	}
	public void setNoTestAnswers(Boolean noTestAnswers) {
		this.noTestAnswers = noTestAnswers;
	}
		
	@Column(name = "RESFILTER_USER")
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	
	@Column(name = "RESFILTER_OWNER")
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	@ElementCollection
	@Cascade(value={CascadeType.ALL})
	public Map<String, String> getFilterValues() {
		return filterValues;
	}
	public void setFilterValues(Map<String, String> filterValues) {
		this.filterValues = filterValues;
	}
	
	@ElementCollection
	@Cascade(value={CascadeType.ALL})
	public Set<String> getVisibleQuestions() {
		return visibleQuestions;
	}
	public void setVisibleQuestions(Set<String> visibleQuestions) {
		this.visibleQuestions = visibleQuestions;
	}
	
	@ElementCollection
	@Cascade(value={CascadeType.ALL})
	public Set<String> getExportedQuestions() {
		return exportedQuestions;
	}
	public void setExportedQuestions(Set<String> exportedQuestions) {
		this.exportedQuestions = exportedQuestions;
	}
	
	@Transient
	public boolean contains(String questionId, String value)
	{
		return (filterValues.containsKey(questionId) && filterValues.get(questionId).contains(value));
	}
	
	@Transient
	public boolean contains(String questionId, String questionUid, String value)
	{
		String combined = questionId + "|" + questionUid;
		return (filterValues.containsKey(combined) && filterValues.get(combined).contains(value));
	}
	
	@Transient
	public boolean containsQuestion(String questionId, String questionUid)
	{
		String combined = questionId + "|" + questionUid;
		return (filterValues.containsKey(combined));
	}
	
	@Transient
	public boolean contains(String questionId, String questionUid, String value, String paUid)
	{
		String combined = questionId + "|" + questionUid;
		return (filterValues.containsKey(combined) && (filterValues.get(combined).contains(value + "|") || filterValues.get(combined).contains(paUid)));
	}
	
	@Transient
	public boolean visible(String questionId)
	{
		return visibleQuestions.contains(questionId);
	}
	
	@Transient
	public boolean exported(String questionId)
	{
		//Fallback for old filter that have no exported questions
		if (exportedQuestions == null || exportedQuestions.size() == 0) return visible(questionId);
		
		return exportedQuestions.contains(questionId);
	}
	
	@Transient
	public String getValue(String questionId)
	{
		if (filterValues.containsKey(questionId)) return filterValues.get(questionId);
		return "";
	}
	
	@Transient
	public String getValue(String questionId, String questionUid)
	{
		String combined = questionId + "|" + questionUid;
		if (filterValues.containsKey(combined)) return filterValues.get(combined);
		return "";
	}
	
	public ResultFilter copy() {
		ResultFilter copy = new ResultFilter();
		merge(copy);
		return copy;
	}
	
	public int getSurveyId() {
		return surveyId;
	}
	public void setSurveyId(int surveyId) {
		this.surveyId = surveyId;
	}
	
	public String getSurveyUid() {
		return surveyUid;
	}
	public void setSurveyUid(String surveyUid) {
		this.surveyUid = surveyUid;
	}	
	
	@Transient 
	public String getHash(boolean allAnswers)
	{
		StringBuilder result = new StringBuilder();
		result.append(this.caseId);
		result.append(this.invitation);
		result.append(this.surveyId);
		result.append(this.user);
		result.append(this.generatedFrom);
		result.append(this.generatedTo);
		result.append(this.updatedFrom);
		result.append(this.updatedTo);
		result.append(this.createdOrUpdated);
		result.append(this.onlyReallyUpdated);
	
		result.append(this.languages == null ? "" : StringUtils.join(this.languages, ""));
		result.append(StringUtils.join(this.filterValues.keySet(), ""));
		result.append(StringUtils.join(this.filterValues.values(), ""));
		
		//visibleQuestions
		if (visibleQuestions != null && visibleQuestions.size() > 0)
		{
			SortedSet<String> sortedVisibleQuestions = new TreeSet<>(visibleQuestions);
			for (String id : sortedVisibleQuestions)
			{
				result.append(id);
			}
		}
		if (exportedQuestions != null && exportedQuestions.size() > 0)
		{
			SortedSet<String> sortedExportedQuestions = new TreeSet<>(exportedQuestions);
			for (String id : sortedExportedQuestions)
			{
				result.append(id);
			}
		}
		
		if (allAnswers)
		{
			result.append("aa");
		}
		
		return Tools.md5hash(result.toString());
	}
	
	@Transient
	public boolean isEmpty() {
		if (caseId != null && caseId.length() > 0) return false;
		if (invitation != null && invitation.length() > 0) return false;
		if (user != null && user.length() > 0) return false;
		if (generatedFrom != null) return false;
		if (generatedTo != null) return false;
		if (updatedFrom != null) return false;
		if (updatedTo != null) return false;
		if (languages != null && languages.size() > 0) return false;
		if (filterValues != null && filterValues.size() > 0) return false;
		
		return true;
	}
	public ResultFilter merge(ResultFilter copy) {
		copy.caseId = caseId;

		if (filterValues != null)
		{
			Map<String, String> newFilterValues = new HashMap<>();
			for (String key : filterValues.keySet()) {
				newFilterValues.put(key, filterValues.get(key));
			}

			copy.filterValues = newFilterValues;
		}

		copy.generatedFrom = generatedFrom;
		copy.generatedTo = generatedTo;
		copy.invitation = invitation;

		if (languages != null)
		{
			Set<String> newLanguages = new HashSet<>();
            newLanguages.addAll(languages);

			copy.languages = newLanguages;
		}
		copy.sortKey = sortKey;
		copy.sortOrder = sortOrder;
		copy.updatedFrom = updatedFrom;
		copy.updatedTo = updatedTo;
		copy.user = user;

		Set<String> newVisibleQuestions = new HashSet<>();
        newVisibleQuestions.addAll(visibleQuestions);
		copy.visibleQuestions = newVisibleQuestions;

		Set<String> newExportedQuestions = new HashSet<>();
        newExportedQuestions.addAll(exportedQuestions);
		copy.exportedQuestions = newExportedQuestions;

		copy.surveyId = surveyId;
		copy.userId = userId;

		copy.createdOrUpdated = createdOrUpdated;
		copy.onlyReallyUpdated = onlyReallyUpdated;
		
		copy.surveyStatus = surveyStatus;
		copy.status = status;
		copy.surveyEndDateFrom = surveyEndDateFrom;
		copy.surveyEndDateTo = surveyEndDateTo;

		return copy;
	}
	
	public String getSurveyShortname() {
		return surveyShortname;
	}
	public void setSurveyShortname(String surveyShortname) {
		this.surveyShortname = surveyShortname;
	}
	
	public String getSurveyTitle() {
		return surveyTitle;
	}
	public void setSurveyTitle(String surveyTitle) {
		this.surveyTitle = surveyTitle;
	}
	
	@Column(name = "RESFILTER_STITLE")
	public String getSurveyStatus() {
		return surveyStatus;
	}
	public void setSurveyStatus(String surveyStatus) {
		this.surveyStatus = surveyStatus;
	}

	@Column(name = "RESFILTER_SENDFROM")
	@DateTimeFormat(pattern="dd/MM/yyyy")
	public Date getSurveyEndDateFrom() {
		return surveyEndDateFrom;
	}
	public void setSurveyEndDateFrom(Date surveyEndDateFrom) {
		this.surveyEndDateFrom = surveyEndDateFrom;
	}

	@Column(name = "RESFILTER_SENDTO")
	@DateTimeFormat(pattern="dd/MM/yyyy")
	public Date getSurveyEndDateTo() {
		return surveyEndDateTo;
	}
	public void setSurveyEndDateTo(Date surveyEndDateTo) {
		this.surveyEndDateTo = surveyEndDateTo;
	}

	@Column(name = "RESFILTER_STATUS")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "RESFILTER_SURPUBRES")
	public String getSurveyPublishedResults() {
		return surveyPublishedResults;
	}
	public void setSurveyPublishedResults(String surveyPublishedResults) {
		this.surveyPublishedResults = surveyPublishedResults;
	}

	public String getDraftId() {
		return draftId;
	}
	public void setDraftId(String draftId) {
		this.draftId = draftId;
	}

}
