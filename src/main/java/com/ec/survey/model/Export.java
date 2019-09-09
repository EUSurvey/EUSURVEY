package com.ec.survey.model;

import com.ec.survey.model.survey.Survey;
import com.ec.survey.tools.ConversionTools;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "EXPORTS")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Export implements java.io.Serializable {

	public enum ExportState
	{
		Pending, Failed, Finished
	}
	
	public enum ExportType
	{
		Content, Statistics, Charts, AddressBook, Activity, Tokens, Files, Survey, StatisticsQuiz
	}
	
	public enum ExportFormat
	{
		xls, xlsx, odt, ods, pdf, doc, csv, xml, zip, eus
	}
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Survey survey;
	private ExportState state;
	private ExportType type;
	private ExportFormat format;
	private String name;
	private Integer userId;
	private Date date;
	private boolean valid;
	private boolean notified;
	private boolean allAnswers;
	private ResultFilter resultFilter;
	private ActivityFilter activityFilter;
	private Boolean zipped = false;
	private Boolean showShortnames = false;
	private Boolean addMeta = false;
	private Integer participationGroup;
	private String email;
	
	@Id
	@Column(name = "EXPORT_ID")
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@com.fasterxml.jackson.annotation.JsonIgnore
	@ManyToOne  
	@JoinColumn(name="SURVEY_ID")
	public Survey getSurvey() {
		return survey;
	}
	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	@Column(name = "EXPORT_STATE")
	public ExportState getState() {
		return state;
	}
	public void setState(ExportState state) {
		this.state = state;
	}
	
	@Column(name = "EXPORT_FORMAT")
	public ExportFormat getFormat() {
		return format;
	}
	public void setFormat(ExportFormat format) {
		this.format = format;
	}
	
	@Column(name = "EXPORT_NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "EXPORT_EMAIL")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Column(name = "USER_ID")
	public int getUserId() {
		return userId != null ? userId : 0;
	}
	public void setUserId(Integer userId) {
		this.userId = userId != null ? userId : 0;
	}
	
	@Column(name = "EXPORT_DATE")
	@DateTimeFormat(pattern=ConversionTools.DateTimeFormat)
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	@Column(name = "EXPORT_TYPE")
	public ExportType getType() {
		return type;
	}
	public void setType(ExportType type) {
		this.type = type;
	}
	
	@Column(name = "EXPORT_ZIPPED")
	public Boolean getZipped() {
		return zipped;
	}
	public void setZipped(Boolean zipped) {
		this.zipped = zipped;
	}
	
	@Column(name = "EXPORT_NOT")
	public boolean isNotified() {
		return notified;
	}
	public void setNotified(boolean notified) {
		this.notified = notified;
	}
	
	@Column(name = "EXPORT_ALLANSWERS")
	public boolean isAllAnswers() {
		return allAnswers;
	}
	public void setAllAnswers(Boolean allAnswers) {
		this.allAnswers = allAnswers != null ? allAnswers : false;
	}
	
	@Column(name = "EXPORT_VALID")
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	@Transient
	public boolean isFinished() {
		return state == ExportState.Finished;
	}
	
	@Transient
	public boolean isTypeContent() {
		return type == ExportType.Content;
	}
	
	@Transient
	public boolean isTypeStatistics() {
		return type == ExportType.Statistics;
	}
	
	@Transient
	public boolean isTypeStatisticsQuiz() {
		return type == ExportType.StatisticsQuiz;
	}
	
	@Transient
	public boolean isTypeAddressBook() {
		return type == ExportType.AddressBook;
	}
	
	@Transient
	public boolean isTypeActivity() {
		return type == ExportType.Activity;
	}
	
	@Transient
	public boolean isTypeTokens() {
		return type == ExportType.Tokens;
	}
	
	@Transient
	public boolean isTypeFiles() {
		return type == ExportType.Files;
	}
	
	@Transient
	public boolean isTypeSurvey() {
		return type == ExportType.Survey;
	}
	
	@Transient
	public String getSurveyTitle() {
		return this.survey == null ? "" : this.survey.getTitle();
	}
	
	@Transient
	public String getSurveyShortname() {
		return this.survey == null ? "" : this.survey.getShortname();
	}
	
	@Transient
	public String getFormattedDate() {
		return new SimpleDateFormat(ConversionTools.DateTimeFormat).format(date);
	}
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="id_resflt")
	public ResultFilter getResultFilter() {
		return resultFilter;
	}	
	public void setResultFilter(ResultFilter resultFilter) {
		this.resultFilter = resultFilter;
	}
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="id_acflt")
	public ActivityFilter getActivityFilter() {
		return activityFilter;
	}	
	public void setActivityFilter(ActivityFilter activityFilter) {
		this.activityFilter = activityFilter;
	}
	
	@Column(name = "EXPORT_SHORTNAMES")
	public Boolean getShowShortnames() {
		return showShortnames;
	}
	public void setShowShortnames(Boolean showShortnames) {
		this.showShortnames = showShortnames;
	}
	
	@Column(name = "EXPORT_META")
	public Boolean getAddMeta() {
		return addMeta;
	}
	public void setAddMeta(Boolean addMeta) {
		this.addMeta = addMeta;
	}
	
	@Column(name = "EXPORT_PARTGROUP")
	public Integer getParticipationGroup() {
		return participationGroup;
	}
	public void setParticipationGroup(Integer participationGroup) {
		this.participationGroup = participationGroup;
	}
	
}

