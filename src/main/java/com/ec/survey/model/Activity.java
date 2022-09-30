package com.ec.survey.model;

import java.util.Date;
import javax.persistence.*;

import com.ec.survey.tools.activity.ActivityRegistry;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	public String getObject(int logID){
		return ActivityRegistry.getObjectFromId(logID);
	}
	
	@Transient
	public String getObject(){
		return getObject(logID);
	}
	
	@Transient
	public String getProperty(){
		return ActivityRegistry.getPropertyFromId(logID, type);
	}

	@Transient
	public String getEvent(){
		return ActivityRegistry.getEventFromId(logID);
	}
	
	@Transient
	public String getDescription()
	{		
		return "";
	}
	
}
