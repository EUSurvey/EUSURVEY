package com.ec.survey.model.attendees;

import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.ec.survey.model.administration.User;

@Entity
@Table(name = "SHARES")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Share {
	
	private Integer id;	
	private String name;	
	private Boolean readonly;
	private List<Attendee> attendees;
	private User owner;
	private User recipient;
	
	@Id
	@Column(name = "SHARE_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne  
	@JoinColumn(name="OWNER", nullable = false)
	public User getOwner() {
		return owner;
	}	
	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	@Column(name = "NAME")
	public String getName() {
		return name;
	}	
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "READONLY")
	public Boolean getReadonly() {
		return readonly;
	}	
	public void setReadonly(Boolean readonly) {
		this.readonly = readonly;
	}
	
	@ManyToMany()
	@JoinTable(foreignKey = @ForeignKey(javax.persistence.ConstraintMode.NO_CONSTRAINT),
			inverseJoinColumns = @JoinColumn(name = "attendees_ATTENDEE_ID"),
			joinColumns = @JoinColumn(name = "SHARES_SHARE_ID"))
	@Fetch(value = FetchMode.SELECT)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<Attendee> getAttendees() {
		return attendees;
	}	
	public void setAttendees(List<Attendee> attendees) {
		this.attendees = attendees;
	}
	
	@ManyToOne  
	@JoinColumn(name="RECIPIENT", nullable = false)
	public User getRecipient() {
		return recipient;
	}
	public void setRecipient(User recipient) {
		this.recipient = recipient;
	}
	
	@Transient
	public boolean containsAttendee(Integer id) {
		for (Attendee attendee: attendees)
		{
			if (attendee.getId().equals(id)) return true;
		}
		return false;
	}
	
}
