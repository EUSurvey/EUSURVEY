package com.ec.survey.model.attendees;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ATTENDEEFILTER")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AttendeeFilter {
	
	private int id;
	private int ownerId;
	private String name;
	private String email;
	private List<Attribute> attributes = new ArrayList<>();
	
	@Id
	@Column(name = "AFILTER_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "ATTENDEEFILTER_NAME")
	public String getName() {
		return name;
	}	
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "ATTENDEEFILTER_EMAIL")
	public String getEmail() {
		return email;
	}	
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Column(name = "ATTENDEEFILTER_OWNER_ID")
	public Integer getOwnerId() {
		return ownerId;
	}	
	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}
	
	@ManyToMany(cascade={CascadeType.ALL})
    @JoinTable(name = "ATTENDEEFILTER_ATTRIBUTES", joinColumns = { @JoinColumn(name = "ATTENDEEFILTER_ID") }, inverseJoinColumns = { @JoinColumn(name = "ATTRIBUTE_ID") })
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<Attribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
}
