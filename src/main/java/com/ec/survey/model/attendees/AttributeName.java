package com.ec.survey.model.attendees;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "ATTRIBUTENAME")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AttributeName implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;	
	private Integer ownerId;
	private String name;	
	
	public AttributeName(){}
	
	public AttributeName(Integer ownerId, String name){
		this.name = name;
		this.ownerId = ownerId;
	}
		
	@Id
	@Column(name = "AN_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "OWNER_ID")
	public Integer getOwnerId() {
		return ownerId;
	}	
	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}
	
	@Column(name = "AN_NAME")
	public String getName() {
		return name;
	}	
	public void setName(String name) {
		this.name = name;
	}
	
}
