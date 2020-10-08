package com.ec.survey.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.ec.survey.tools.ConversionTools;

/**
 * Represents one entry in the activity log of a survey.
 */
@Entity
@Table(name = "ACTIVITY")
public class Activity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Date date;
	private int logID;
	private String oldValue;
	private String newValue;
	private String surveyUID;
	private int userId;
	private String userName;
	private String type;
	
	private static final String ADDED = "Added";
	private static final String CREATED = "Created";
	private static final String DELETED = "Deleted";
	private static final String MODIFIED = "Modified";
	private static final String REMOVED = "Removed";
	private static final String SENT = "Sent";
	private static final String STARTED = "Started";
	private static final String SUBMITTED = "Submitted";
	private static final String TRANSLATION = "Translation";
			
	@Id
	@Column(name = "ACTIVITY_ID")
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern=ConversionTools.DateTimeFormat)
	@Column(name = "ACTIVITY_DATE")
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	@Column(name = "ACTIVITY_LOGID")
	public int getLogID() {
		return logID;
	}
	public void setLogID(int logID) {
		this.logID = logID;
	}
	
	@Column(name = "ACTIVITY_OLD")
	@Lob
	public String getOldValue() {
		return oldValue;
	}
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	@Column(name = "ACTIVITY_NEW")
	@Lob
	public String getNewValue() {
		return newValue;
	}
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	
	@Column(name = "ACTIVITY_SUID")
	public String getSurveyUID() {
		return surveyUID;
	}
	public void setSurveyUID(String surveyUID) {
		this.surveyUID = surveyUID;
	}
	
	@Column(name = "ACTIVITY_USER")
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	@Column(name = "ACTIVITY_TYPE")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Transient
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@Transient
	public String getObject(int logID)
	{
		if (logID < 200) return "Survey";
		if (logID < 300) return "DraftSurvey";
		if (logID == 312) return "Activities";
		if (logID < 400) return "Results";
		if (logID < 404) return "Contribution";
		if (logID < 500) return "TestContribution";
		if (logID < 600) return "GuestList";
		if (logID < 700) return "Privileges";
		if (logID < 800) return "Messages";		
		return "";
	}
	
	@Transient
	public String getObject()
	{
		return getObject(logID);
	}
	
	@Transient
	public String getProperty()
	{
		if (logID < 105) return "n/a";
		if (logID < 107) return "State";
		if (logID < 109) return "PendingChanges";
		if (logID == 109) return "Alias";
		if (logID == 110) return "EndNotificationState";
		if (logID == 111) return "EndNotificationValue";
		if (logID == 112) return "EndNotificationReach";
		if (logID == 113) return "ContactCreation";
		if (logID == 114) return "Security";
		if (logID == 115) return "Password";
		if (logID == 116) return "Anonymity";
		if (logID == 117) return "Privacy";
		if (logID == 118) return "Captcha";
		if (logID == 119) return "EditContribution";
		if (logID == 120) return "MultiPaging";
		if (logID == 121) return "PageWiseValidation";
		if (logID == 122) return "WCAGCompliance";
		if (logID == 123) return "Owner";
		if (logID == 201) return "n/a";
		if (logID == 202) return "Properties";
		if (logID == 203) return "UsefulLink";
		if (logID == 204) return "UsefulLink";
		if (logID == 205) return "BackgroundDocument";
		if (logID == 206) return "BackgroundDocument";
		if (logID == 207) return "Title";
		if (logID == 208) return "PivotLanguage";
		if (logID == 209) return "Contact";
		if (logID == 210) return "Autopublish";
		if (logID == 211) return "StartDate";
		if (logID == 212) return "EndDate";
		if (logID == 213) return "Logo";
		if (logID == 214) return "Skin";
		if (logID == 215) return "AutoNumberingSections";
		if (logID == 216) return "AutoNumberingQuestions";
		if (logID == 217) return "ElementOrder";
		if (logID < 221) return "SurveyElement";
		if (logID < 225) return TRANSLATION;
		if (logID == 225) return "ConfirmationPage";
		if (logID == 226) return "EscapePage";
		if (logID == 227) return TRANSLATION;
		if (logID == 228) return TRANSLATION;
		if (logID == 301) return "PublishIndividual";
		if (logID == 302) return "PublishCharts";
		if (logID == 303) return "PublishStatistics";
		if (logID == 304) return "PublicSearch";
		if (logID == 305) return "PublishQuestionSelection";
		if (logID == 306) return "PublishAnswerSelection";
		if (logID == 307) return "ExportStatistics";
		if (logID == 308) return "ExportContent";
		if (logID == 309) return "ExportCharts";
		if (logID == 312) return "ExportActivities";
		if (logID == 313) return "PublishUploadedElements";
		if (logID == 314) return "ExportUploadedElements";
		if (logID == 315) return "DeleteColumn";
		if (logID < 312) return "Export";
		if (logID < 407) return "n/a";
		if (logID < 506) return type != null ? type : "Token/Contacts/Department";
		if (logID == 506) return "Invitations";
		if (logID == 507) return "Invitations";
		if (logID < 604) return "n/a";
		if (logID == 701) return "EndNotificationMessage";
		return "n/a";
	}

	@Transient
	public String getEvent()
	{
		if (logID < 104) return CREATED;
		if (logID == 104) return DELETED;
		if (logID < 107) return MODIFIED;
		if (logID == 107) return "Applied";
		if (logID == 108) return "Discarded";
		if (logID < 200) return MODIFIED;
		if (logID == 201) return "Opened";
		if (logID == 202) return "Saved";
		if (logID == 203) return ADDED;
		if (logID == 204) return REMOVED;
		if (logID == 205) return ADDED;
		if (logID == 206) return REMOVED;
		if (logID < 218) return MODIFIED;
		if (logID == 218) return ADDED;
		if (logID == 219) return DELETED;
		if (logID == 220) return MODIFIED;
		if (logID == 221) return ADDED;
		if (logID == 222) return DELETED;
		if (logID == 223) return "Enabled";
		if (logID == 224) return "Disabled";
		if (logID == 228) return "Requested";
		if (logID < 307) return MODIFIED;
		if (logID < 310) return STARTED;
		if (logID == 310) return "Returned";
		if (logID == 311) return DELETED;
		if (logID == 312) return STARTED;
		if (logID == 313) return MODIFIED;
		if (logID == 314) return STARTED;
		if (logID == 401) return SUBMITTED;
		if (logID == 402) return DELETED;
		if (logID == 403) return MODIFIED;
		if (logID == 404) return SUBMITTED;
		if (logID == 405) return DELETED;
		if (logID == 406) return MODIFIED;
		if (logID == 501) return CREATED;
		if (logID == 502) return DELETED;
		if (logID == 503) return "Paused";
		if (logID == 504) return STARTED;
		if (logID == 505) return MODIFIED;
		if (logID == 506) return SENT;
		if (logID == 507) return MODIFIED;
		if (logID == 601) return ADDED;
		if (logID == 602) return MODIFIED;
		if (logID == 603) return REMOVED;
		if (logID == 701) return SENT;
		return "";
	}
	
	@Transient
	public String getDescription()
	{		
		return "";
	}
	
}
