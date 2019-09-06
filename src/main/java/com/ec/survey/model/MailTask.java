package com.ec.survey.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.*;
import java.io.IOException;
import java.util.HashMap;

@Entity
@Table(name = "MAILTASKS", indexes = {@Index(name = "IDX_MAILTASKS", columnList = "MAILTASK_SURVEY, MAILTASK_NOT, MAILTASK_STATE")})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MailTask implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String selectedAttendee;
	private int participationGroupId;
	private int userId;
	private String surveyUid;
	private String senderAddress;
	private String senderSubject;
	private String text1;
	private String text2;
	private String parametersSerialized;
	private String locale;
	private String state;
	private String message;
	private int mailsSent;
	private boolean notified;
	private String mailtemplate;
	
	public final static String WAITING = "WAITING";
	public final static String FINISHED = "FINISHED";
	public final static String ERROR = "ERROR";
	
	@Id
	@Column(name = "MAILTASK_ID")
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "MAILTASK_ATT")
	public String getSelectedAttendee() {
		return selectedAttendee;
	}
	public void setSelectedAttendee(String selectedAttendee) {
		this.selectedAttendee = selectedAttendee;
	}
	
	@Column(name = "MAILTASK_PG")
	public int getParticipationGroupId() {
		return participationGroupId;
	}
	public void setParticipationGroupId(int participationGroupId) {
		this.participationGroupId = participationGroupId;
	}
	
	@Column(name = "MAILTASK_USER")
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	@Column(name = "MAILTASK_NOT")
	public boolean isNotified() {
		return notified;
	}
	public void setNotified(boolean notified) {
		this.notified = notified;
	}
	
	@Column(name = "MAILTASK_SURVEY")
	public String getSurveyUid() {
		return surveyUid;
	}
	public void setSurveyUid(String surveyUid) {
		this.surveyUid = surveyUid;
	}
	
	@Column(name = "MAILTASK_S")
	public String getSenderAddress() {
		return senderAddress;
	}
	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}
	
	@Column(name = "MAILTASK_SUBJECT")
	public String getSenderSubject() {
		return senderSubject;
	}
	public void setSenderSubject(String senderSubject) {
		this.senderSubject = senderSubject;
	}
	
	@Column(name = "MAILTASK_T1")
	@Lob
	public String getText1() {
		return text1;
	}
	public void setText1(String text1) {
		this.text1 = text1;
	}
	
	@Column(name = "MAILTASK_T2")
	@Lob
	public String getText2() {
		return text2;
	}
	public void setText2(String text2) {
		this.text2 = text2;
	}
	
	@Column(name = "MAILTASK_LOCALE")
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	@SuppressWarnings("unchecked")
	@Transient
	public HashMap<String,String[]> getParameters() throws IOException {
		return parametersSerialized != null ? new ObjectMapper().readValue(parametersSerialized, HashMap.class) : null;
	}
	public void setParameters(HashMap<String,String[]> parameters) throws IOException {
		this.parametersSerialized = new ObjectMapper().writeValueAsString(parameters);
	}
	
	@Column(name = "MAILTASK_PARAMS")
	@Lob
	public String getParametersSerialized() {
		return parametersSerialized;
	}
	public void setParametersSerialized(String parametersSerialized) {
		this.parametersSerialized = parametersSerialized;
	}
	
	@Column(name = "MAILTASK_STATE")
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	@Column(name = "MAILTASK_RESULT")
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		if (message != null)
		{
			this.message = message.length() > 250 ? message.substring(255) : message;
		}		
	}
	
	@Column(name = "MAILTASK_SENT")
	public int getMailsSent() {
		return mailsSent;
	}
	public void setMailsSent(int mailsSent) {
		this.mailsSent = mailsSent;
	}
	
	@Column(name = "MAILTASK_TEMPLATE")
	public String getMailtemplate() {
		return mailtemplate;
	}
	public void setMailtemplate(String mailtemplate) {
		this.mailtemplate = mailtemplate;
	}	
	
}

