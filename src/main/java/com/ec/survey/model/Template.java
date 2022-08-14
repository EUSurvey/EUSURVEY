package com.ec.survey.model;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.Element;

@Entity
@Table(name = "TEMPL")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Template implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private Element element;
	private User owner;
	
	@Id
	@Column(name = "TEMPL_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "TEMPL_NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@ManyToOne  
	@JoinColumn(name="OWNER", nullable = false)    
	public User getOwner() {return owner;}  
	public void setOwner(User owner) {this.owner = owner;}  
	
	@OneToOne(cascade = CascadeType.ALL)
	public Element getElement() {
		return element;
	}
	public void setElement(Element element) {
		this.element = element;
	}

	

}
