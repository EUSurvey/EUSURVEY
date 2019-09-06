package com.ec.survey.model.attendees;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "INVITATIONS")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Invitation {
	
	private int id;	
	private int participationGroupId;
	private int attendeeId;	
	private String uniqueId;
	private Date invited;
	private Date reminded;
	private int answers;
	private Boolean deactivated;
	
	public Invitation() {}
	
	public Invitation(int participationGroupId, int attendeeId)
	{
		this.participationGroupId = participationGroupId;
		this.attendeeId = attendeeId;
		this.uniqueId = UUID.randomUUID().toString();
		this.invited = new Date();
	}
	
	public Invitation(int participationGroupId, String token) {
		this.participationGroupId = participationGroupId;
		this.uniqueId = token;
		this.deactivated = false;
		this.invited = new Date();
	}

	@Id
	@Column(name = "INVITATION_ID")
	@GeneratedValue
	public Integer getId() {
		return id;
	}	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "ATTENDEE_ID")
	public Integer getAttendeeId() {
		return attendeeId;
	}	
	public void setAttendeeId(int attendeeId) {
		this.attendeeId = attendeeId;
	}
	
	@Column(name = "PARTICIPATIONGROUP_ID")
	public Integer getParticipationGroupId() {
		return participationGroupId;
	}	
	public void setParticipationGroupId(int participationGroupId) {
		this.participationGroupId = participationGroupId;
	}
	
	@Column(name = "ATTENDEE_ANSWERS")
	public Integer getAnswers() {
		return answers;
	}	
	public void setAnswers(int answers) {
		this.answers = answers;
	}
	
	@Column(name = "UNIQUE_ID")
	public String getUniqueId() {
		return uniqueId;
	}	
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	
	@Column(name = "ATTENDEE_INVITED")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern="dd/MM/yyyy")
	public Date getInvited()
	{
		return invited;
	}
	public void setInvited(Date invited)
	{
		this.invited = invited;
	}
	
	@Column(name = "ATTENDEE_REMINDED")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern="dd/MM/yyyy")
	public Date getReminded()
	{
		return reminded;
	}
	public void setReminded(Date reminded)
	{
		this.reminded = reminded;
	}

	@Column(name = "INV_DEACTIVATED")
	public Boolean getDeactivated() {
		return deactivated;
	}
	public void setDeactivated(Boolean deactivated) {
		this.deactivated = deactivated;
	}
	
}
