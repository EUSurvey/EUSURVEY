package com.ec.survey.model;

import com.ec.survey.model.administration.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "WEBSERVICETASK")
public class WebserviceTask {
	
	private int id;
	private User user;
	private WebserviceTaskType type;
	private int groupId;
	private int surveyId;
	private String surveyUid;
	private int number;
	private Date start;
	private Date end;
	private boolean done;
	private String result;
	private String error;
	private String token;
	private String uniqueId;
	private boolean showIDs;
	private boolean addMeta;
	private Integer exportType;
	private String contributionType;
	private String fileTypes;
	private String hook;
	
	private Date created;
	private Date started;
	private int counter;
	
	private boolean empty;
	private boolean xmlOnly;
	
	public WebserviceTask(WebserviceTaskType type)
	{
		this.type = type;
		this.done = false;
	}
	
	public WebserviceTask()
	{}
	
	@Id
	@Column(name = "WST_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne  
	@JoinColumn(name="WST_USER")    
	public User getUser() {return user;}  
	public void setUser(User user) {this.user = user;}
	
	public WebserviceTaskType getType() {
		return type;
	}
	public void setType(WebserviceTaskType type) {
		this.type = type;
	}

	@Column(name = "WST_GROUP")
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	@Column(name = "WST_NUM")
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}

	@Column(name = "WST_DONE")
	public boolean isDone() {
		return done;
	}
	public void setDone(boolean done) {
		this.done = done;
	}

	@Column(name = "WST_RESULT")
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}  
	
	@Column(name = "WST_ERROR")
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}

	@Column(name = "WST_SURVEYID")
	public int getSurveyId() {
		return surveyId;
	}
	public void setSurveyId(int surveyId) {
		this.surveyId = surveyId;
	}
	
	@Column(name = "WST_SURVEYUID")
	public String getSurveyUid() {
		return surveyUid;
	}
	public void setSurveyUid(String surveyUid) {
		this.surveyUid = surveyUid;
	}  

	@Column(name = "WST_START")
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}

	@Column(name = "WST_END")
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}

	@Column(name = "WST_TOKEN")
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}

	@Column(name = "WST_UNIQUEID")
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	@Column(name = "WST_SHOWIDS")
	public boolean isShowIDs() {
		return showIDs;
	}
	public void setShowIDs(boolean showIDs) {
		this.showIDs = showIDs;
	}

	@Column(name = "WST_ADDMETA")
	public boolean isAddMeta() {
		return addMeta;
	}
	public void setAddMeta(boolean addMeta) {
		this.addMeta = addMeta;
	}

	@Column(name = "WST_CREATED")
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}

	@Column(name = "WST_STARTED")
	public Date getStarted() {
		return started;
	}
	public void setStarted(Date started) {
		this.started = started;
	}

	@Column(name = "WST_COUNTER")
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}

	@Column(name = "WST_EXPORTTYPE")
	public Integer getExportType() {
		return exportType;
	}
	public void setExportType(Integer exportType) {
		this.exportType = exportType;
	}
	
	@Column(name = "WST_FILETYPES")
	public String getFileTypes() {
		return fileTypes;
	}
	public void setFileTypes(String fileTypes) {
		this.fileTypes = fileTypes;
	}
	
	@Column(name = "WST_CONTRIBTYPE")
	public String getContributionType() {
		return contributionType;
	}
	public void setContributionType(String contributionType) {
		this.contributionType = contributionType;
	}

	@Column(name = "WST_EMPTYRESULT")
	public Boolean isEmpty() {
		return empty;
	}
	public void setEmpty(Boolean empty) {
		this.empty = empty != null && empty;
	}

	@Column(name = "WST_XMLONLY")
	public boolean isXmlOnly() {
		return xmlOnly;
	}
	public void setXmlOnly(boolean xmlOnly) {
		this.xmlOnly = xmlOnly;
	}

	@Column(name = "WST_HOOK")
	public String getHook() {
		return hook;
	}
	public void setHook(String hook) {
		this.hook = hook;
	}
}

