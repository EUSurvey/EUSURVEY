package com.ec.survey.model;

import com.ec.survey.model.survey.Survey;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.Tools;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "EXPORTS")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Export implements java.io.Serializable {

	public enum ExportState
	{
		Pending, Failed, Finished
	}

	public enum ExportType {
		Content, Statistics, Charts, AddressBook, Activity, Tokens, Files, VoterFiles, Survey, StatisticsQuiz, ECFGlobalResults, ECFProfileResults, ECFOrganizationResults, PDFReport
	}
	
	public enum ExportFormat
	{
		xls, xlsx, odt, ods, pdf, doc, csv, xml, zip, eus, docx
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
	private Boolean forArchiving;
	
	// The profile to which we want to compare the ecf results
	private String ecfProfileUid;

	private String displayUsername;
	private String charts;
	private Boolean splitMCQ = false;
	
	@Id
	@Column(name = "EXPORT_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
		this.allAnswers = allAnswers != null && allAnswers;
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
		return type == ExportType.Statistics || type == ExportType.PDFReport;
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
	public boolean isTypeVoterFiles() { return type == ExportType.VoterFiles; }
	
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
		return Tools.formatDate(date, ConversionTools.DateTimeFormat);
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
	
	@Column(name = "EXPORT_FORARCHIVING")
	public Boolean isForArchiving() {
		return forArchiving;
	}
	public void setForArchiving(Boolean forArchiving) {
		this.forArchiving = forArchiving;
	}
	
	@Lob
	@Column(name = "EXPORT_CHARTS")
	public String getCharts() {
		return charts;
	}
	public void setCharts(String charts) {
		this.charts = charts;
	}
	@Column(name = "EXPORT_SPLIT_MCQ")
	public Boolean getSplitMCQ() {
		return splitMCQ;
	}
	public void setSplitMCQ(Boolean splitMCQ) {
		this.splitMCQ = splitMCQ != null && splitMCQ;
	}

	@JsonIgnore
	@Transient
	public Map<String, String> getChartsByQuestionUID() {
		Map<String, String> chartsByQuestionUid = new HashMap<>();
		
		if (charts != null) {		
			String[] entries = charts.split(",");
			for (String pair : entries) {
				String[] keyValue = pair.split(":");
				if (keyValue.length > 1) {
					chartsByQuestionUid.put(keyValue[0], keyValue[1]);
				}
			}		
		}
		
		return chartsByQuestionUid;
	}

	@Transient
	public String getDisplayUsername() {
		return displayUsername;
	}

	@Transient
	public void setDisplayUsername(String displayUsername) {
		this.displayUsername = displayUsername;
	}	
}

