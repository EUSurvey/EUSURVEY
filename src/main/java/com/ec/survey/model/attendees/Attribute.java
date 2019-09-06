package com.ec.survey.model.attendees;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "ATTRIBUTE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Attribute {
	
	private Integer id;	
	private Integer attendeeId;
	private AttributeName attributeName;
	private String value;	
	
	public Attribute(){}
	
	public Attribute(Integer ownerId, AttributeName attributeName, String value){
		this.attributeName = attributeName;
		this.value = value;
	}
		
	@Id
	@Column(name = "ATTRIBUTE_ID")
	@GeneratedValue
	public Integer getId() {
		return id;
	}	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "ATTE_ID")
	public Integer getAttendeeId() {
		return attendeeId;
	}	
	public void setAttendeeId(Integer attendeeId) {
		this.attendeeId = attendeeId;
	}
	
	@ManyToOne(optional = false )
	public AttributeName getAttributeName() {
		return attributeName;
	}	
	public void setAttributeName(AttributeName attributeName) {
		this.attributeName = attributeName;
	}
	
	@Lob
	@Column(name = "ATTRIBUTE_VALUE")
	public String getValue() {
		return value;
	}	
	public void setValue(String value) {
		this.value = value;
	}
	
}
