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
import java.util.Map.Entry;

/**
 * Defines the results we want to extract from the database. Hence, gives options to filter but also to order by.
 */
@Entity
@Table(name = "RESULTFILTER")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ResultFilter implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public enum ResultFilterOrderBy {
		NAME_ASC("nameAsc"),
		NAME_DESC("nameDesc"),
		SCORE_ASC("scoreAsc"),
		SCORE_DESC("scoreDesc"),
		DATE_ASC("dateAsc"),
		DATE_DESC("dateDesc"),
		REPLIES_ASC("repliesAsc"),
		REPLIES_DESC("repliesDesc"),
		CREATED_ASC("createdAsc"),
		CREATED_DESC("createdDesc"),
		ECFSCORE_ASC("ecfScoreAsc"),
		ECFSCORE_DESC("ecfScoreDesc"),
		ECFGAP_ASC("ecfGapAsc"),
		ECFGAP_DESC("ecfGapDesc"),
		UNKNOWN(null);
		
		String value;
		
		ResultFilterOrderBy(String value) {
			this.value = value;
		}
		
		public static ResultFilterOrderBy parse(String value) {
			if (value.equalsIgnoreCase(NAME_ASC.value())) {
				return NAME_ASC;
			}
			if (value.equalsIgnoreCase(SCORE_ASC.value())) {
				return SCORE_ASC;
			}
			if (value.equalsIgnoreCase(DATE_ASC.value())) {
				return DATE_ASC;
			}
			if (value.equalsIgnoreCase(REPLIES_ASC.value())) {
				return REPLIES_ASC;
			}
			if (value.equalsIgnoreCase(CREATED_ASC.value())) {
				return CREATED_ASC;
			}
			if (value.equalsIgnoreCase(ECFSCORE_ASC.value())) {
				return ECFSCORE_ASC;
			}
			if (value.equalsIgnoreCase(ECFGAP_ASC.value())) {
				return ECFGAP_ASC;
			}
			if (value.equalsIgnoreCase(NAME_DESC.value())) {
				return NAME_DESC;
			}
			if (value.equalsIgnoreCase(SCORE_DESC.value())) {
				return SCORE_DESC;
			}
			if (value.equalsIgnoreCase(DATE_DESC.value())) {
				return DATE_DESC;
			}
			if (value.equalsIgnoreCase(REPLIES_DESC.value())) {
				return REPLIES_DESC;
			}
			if (value.equalsIgnoreCase(CREATED_DESC.value())) {
				return CREATED_DESC;
			}
			if (value.equalsIgnoreCase(ECFSCORE_DESC.value())) {
				return ECFSCORE_DESC;
			}
			if (value.equalsIgnoreCase(ECFGAP_DESC.value())) {
				return ECFGAP_DESC;
			}
			return UNKNOWN;
		}
		
		public String value() {
			return this.value;
		}
		
		public ResultFilterSortKey toResultFilterSortKey() {
			switch(this) {
			case NAME_ASC: return ResultFilterSortKey.NAME;
			case NAME_DESC: return ResultFilterSortKey.NAME;		
			case SCORE_ASC: return ResultFilterSortKey.SCORE;
			case SCORE_DESC: return ResultFilterSortKey.SCORE;
			case DATE_ASC: return ResultFilterSortKey.DATE;
			case DATE_DESC: return ResultFilterSortKey.DATE;
			case REPLIES_ASC: return ResultFilterSortKey.REPLIES;
			case REPLIES_DESC: return ResultFilterSortKey.REPLIES;
			case CREATED_ASC: return ResultFilterSortKey.CREATED;
			case CREATED_DESC: return ResultFilterSortKey.CREATED;
			case ECFSCORE_ASC: return ResultFilterSortKey.ECFSCORE;
			case ECFSCORE_DESC: return ResultFilterSortKey.ECFSCORE;
			case ECFGAP_ASC: return ResultFilterSortKey.ECFGAP;
			case ECFGAP_DESC: return ResultFilterSortKey.ECFGAP;
			default: return ResultFilterSortKey.UNKNOWN;
			}
		}
		
		public String toAscOrDesc() {
			switch(this) {
			case NAME_ASC: return "ASC";
			case NAME_DESC: return "DESC";
			case SCORE_ASC: return "ASC";
			case SCORE_DESC: return "DESC";
			case DATE_ASC: return "ASC";
			case DATE_DESC: return "DESC";
			case REPLIES_ASC: return "ASC";
			case REPLIES_DESC: return "DESC";
			case CREATED_ASC: return "ASC";
			case CREATED_DESC: return "DESC";
			case ECFSCORE_ASC: return "ASC";
			case ECFSCORE_DESC: return "DESC";
			case ECFGAP_ASC: return "ASC";
			case ECFGAP_DESC: return "DESC";
			default: return "";
			}
		}
	}
	
	public enum ResultFilterSortKey {
		NAME("name"),
		SCORE("score"),
		DATE("date"),
		REPLIES("replies"),
		CREATED("created"),
		ECFSCORE("ecfScore"),
		ECFGAP("ecfGap"),
		UNKNOWN(null);
		
		String value;
		
		ResultFilterSortKey(String value) {
			this.value = value;
		}
		
		public static ResultFilterSortKey parse(String value) {
			if (value.equalsIgnoreCase(NAME.value())) {
				return NAME;
			}
			if (value.equalsIgnoreCase(SCORE.value())) {
				return SCORE;
			}
			if (value.equalsIgnoreCase(DATE.value())) {
				return DATE;
			}
			if (value.equalsIgnoreCase(REPLIES.value())) {
				return REPLIES;
			}
			if (value.equalsIgnoreCase(CREATED.value())) {
				return CREATED;
			}
			if (value.equalsIgnoreCase(ECFSCORE.value())) {
				return ECFSCORE;
			}
			if (value.equalsIgnoreCase(ECFGAP.value())) {
				return ECFGAP;
			}
			return UNKNOWN;
		}
		
		public String value() {
			return this.value;
		}
	}
	
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
	private Boolean defaultQuestions = true;
	
	// Only ECF answers with the following ecf Profile
	private String answeredECFProfileUID;
	private String compareToECFProfileUID;
	
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
		answeredECFProfileUID = null;
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
		{
			for (String c : languages)
			{
				if (c.equalsIgnoreCase(code)) return true;
			}
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
	public void addExportedQuestion(String question)
	{
		if (!this.exportedQuestions.contains(question))
		{
			this.exportedQuestions.add(question);
		}
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
		if (exportedQuestions == null || exportedQuestions.isEmpty()) return visible(questionId);
		
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
	
	@Transient
	public String getFromValue(String questionId, String questionUid)
	{
		String combined = questionId + "|" + questionUid + "from";
		if (filterValues.containsKey(combined)) return filterValues.get(combined);
		return "";
	}
	
	@Transient
	public String getToValue(String questionId, String questionUid)
	{
		String combined = questionId + "|" + questionUid + "to";
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
		result.append(this.answeredECFProfileUID == null ? "" : this.answeredECFProfileUID);
	
		result.append(this.languages == null ? "" : StringUtils.join(this.languages, ""));
		result.append(StringUtils.join(this.filterValues.keySet(), ""));
		result.append(StringUtils.join(this.filterValues.values(), ""));
		
		//visibleQuestions
		if (visibleQuestions != null && !visibleQuestions.isEmpty())
		{
			SortedSet<String> sortedVisibleQuestions = new TreeSet<>(visibleQuestions);
			for (String id : sortedVisibleQuestions)
			{
				result.append(id);
			}
		}
		if (exportedQuestions != null && !exportedQuestions.isEmpty())
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
		if (languages != null && !languages.isEmpty()) return false;
		if (filterValues != null && !filterValues.isEmpty()) return false;
		if (answeredECFProfileUID != null && !answeredECFProfileUID.isEmpty()) return false;
		
		return true;
	}
	public ResultFilter merge(ResultFilter copy) {
		copy.caseId = caseId;

		if (filterValues != null)
		{
			Map<String, String> newFilterValues = new HashMap<>();
			for (Entry<String, String> entry : filterValues.entrySet()) {
				newFilterValues.put(entry.getKey(), entry.getValue());
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
		
		copy.defaultQuestions = defaultQuestions;
		copy.answeredECFProfileUID = answeredECFProfileUID;

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

	@Column(name = "RESFILTER_DEFAULT")
	public Boolean getDefaultQuestions() {
		return defaultQuestions;
	}
	public void setDefaultQuestions(Boolean defaultQuestions) {
		this.defaultQuestions = defaultQuestions;
	}

	@Column(name = "RESFILTER_ANS_ECF_PROFILE_UID")
	public String getAnsweredECFProfileUID() {
		return answeredECFProfileUID;
	}
	public void setAnsweredECFProfileUID(String ecfProfileUid) {
		this.answeredECFProfileUID = ecfProfileUid;
	}

	@Column(name = "RESFILTER_COMP_ECF_PROFILE_UID")
	public String getCompareToECFProfileUID() {
		return compareToECFProfileUID;
	}
	public void setCompareToECFProfileUID(String compareToECFProfileUID) {
		this.compareToECFProfileUID = compareToECFProfileUID;
	}

}
