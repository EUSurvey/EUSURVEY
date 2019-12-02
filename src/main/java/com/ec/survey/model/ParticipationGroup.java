package com.ec.survey.model;

import com.ec.survey.model.administration.EcasUser;
import com.ec.survey.model.attendees.Attendee;
import com.ec.survey.model.attendees.AttendeeFilter;
import com.ec.survey.tools.ConversionTools;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "PARTICIPANTS")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ParticipationGroup {
	
	private int id;
	private int surveyId;
	private String surveyUid;
	private int ownerId;
	private int invited;
	private int all;
	private String name;
	private String ecas;
	private String template1;
	private String template2;
	private String templateSubject;
	private List<Attendee> attendees;
	private List<EcasUser> ecasUsers;
	private ParticipationGroupType type;
	private AttendeeFilter attendeeFilter;
	private boolean active;
	//private Set<String> departments;
	private boolean inCreation;
	private boolean runningMails;
	private String error;
	private String domainCode;
	private Date created;
	private int children;
	private Integer lastUsedTemplateID;

	//needed for Hibernate
	public ParticipationGroup() {}
	
	public ParticipationGroup(String uniqueId) {
		this.surveyUid = uniqueId;
		this.created = new Date();
	}
	
	@Id
	@Column(name = "PARTICIPATION_ID")
	@GeneratedValue
	public Integer getId() {
		return id;
	}	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "PARTICIPATION_TYPE")
	public ParticipationGroupType getType() {
		return type;
	}	
	public void setType(ParticipationGroupType type) {
		this.type = type;
	}
	
	@Transient
	public String getNiceType()
	{
		switch(type)
		{
		case Dynamic:
			return "Contacts";
		case ECMembers:
			return "Department";
		case Static:
			return "Contacts";
		case Token:
			return "Token";
		}
		
		return "unknown";
	}
	
	@Column(name = "PARTICIPATION_SURVEY_ID")
	public Integer getSurveyId() {
		return surveyId;
	}	
	public void setSurveyId(Integer surveyId) {
		this.surveyId = surveyId;
	}
	
	@Column(name = "PARTICIPATION_SURVEY_UID")
	public String getSurveyUid() {
		return surveyUid;
	}	
	public void setSurveyUid(String surveyUid) {
		this.surveyUid = surveyUid;
	}
	
	@Column(name = "PARTICIPATION_OWNER_ID")
	public Integer getOwnerId() {
		return ownerId;
	}	
	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}
	
	@Column(name = "PARTICIPANTS_NAME")
	public String getName() {
		return name;
	}	
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "ECAS_NAME")
	public String getECAS() {
		return ecas;
	}	
	public void setECAS(String ecas) {
		this.ecas = ecas;
	}
	
	@Column(name = "PARTICIPATION_ACTIVE")
	public Boolean getActive() {
		return active;
	}	
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	@OneToOne(cascade = CascadeType.ALL)
	public AttendeeFilter getAttendeeFilter() {
		return attendeeFilter;
	}	
	public void setAttendeeFilter(AttendeeFilter attendeeFilter) {
		this.attendeeFilter = attendeeFilter;
	}
	
	@ManyToMany()
	@Fetch(value = FetchMode.SELECT)
	@LazyCollection(LazyCollectionOption.EXTRA)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<Attendee> getAttendees() {
		return attendees;
	}	
	public void setAttendees(List<Attendee> attendees) {
		this.attendees = attendees;
	}
	
	@ManyToMany()
	@Fetch(value = FetchMode.SELECT)
	@LazyCollection(LazyCollectionOption.EXTRA)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<EcasUser> getEcasUsers() {
		return ecasUsers;
	}	
	public void setEcasUsers(List<EcasUser> ecasUsers) {
		this.ecasUsers = ecasUsers;
	}

	//@Column(name = "PARTICIPATION_INVITED")
	@Transient
	public int getInvited() {
		return invited;
	}
	public void setInvited(int invited) {
		this.invited = invited;
	}
	
	@Transient
	public int getAll() {
		return all;
	}
	public void setAll(int all) {
		this.all = all;
	}
	
	@Transient
	public Attendee getAttendee(int attid) {
		for (Attendee attendee: attendees)
		{
			if (attendee.getId().equals(attid)) return attendee;
		}
		return null;
	}
	
	@Transient
	public EcasUser getEcasUser(int attid) {
		for (EcasUser user: ecasUsers)
		{
			if (user.getId().equals(attid)) return user;
		}
		return null;
	}

	@Column(name = "TEMPL1")
	@Lob
	public String getTemplate1() {
		return template1;
	}
	public void setTemplate1(String template1) {
		this.template1 = template1;
	}
	
	@Column(name = "TEMPL2")
	@Lob
	public String getTemplate2() {
		return template2;
	}
	public void setTemplate2(String template2) {
		this.template2 = template2;
	}
	
	@Column(name = "TEMPLSUBJ")
	public String getTemplateSubject() {
		return templateSubject;
	}
	public void setTemplateSubject(String templateSubject) {
		this.templateSubject = templateSubject;
	}
	
//	@ElementCollection
//	@CollectionTable(name="GROUPDEPARTMENTS", joinColumns= @JoinColumn(name="gd_id"))
//	@Column(name = "DEPS")
//	public Set<String> getDepartments() {
//		return departments;
//	}
//	public void setDepartments(Set<String> departments) {
//		this.departments = departments;
//	}

	@Column(name = "INCREATION", columnDefinition = "boolean default false", nullable = false)
	public boolean isInCreation() {
		return inCreation;
	}
	public void setInCreation(boolean inCreation) {
		this.inCreation = inCreation;
	}

	@Transient
	public boolean isRunningMails() {
		return runningMails;
	}
	public void setRunningMails(boolean runningMails) {
		this.runningMails = runningMails;
	}

	@Column(name = "ERROR")
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}

	@Column(name = "DOMAN_CODE")
	public String getDomainCode() {
		return domainCode;
	}
	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SURVEY_CREATED")
	@DateTimeFormat(pattern=ConversionTools.DateFormat)
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	
	@Transient
	public String getFormattedDate() {
		if (created == null) return "";
		return new SimpleDateFormat(ConversionTools.DateTimeFormat).format(created);
	}

	@Transient
	public int getChildren() {
		return children;
	}
	public void setChildren(int children) {
		this.children = children;
	}

	@Column(name = "TEMPLID")
	public Integer getLastUsedTemplateID() {
		return lastUsedTemplateID;
	}
	public void setLastUsedTemplateID(Integer lastUsedTemplateID) {
		this.lastUsedTemplateID = lastUsedTemplateID;
	} 
}
