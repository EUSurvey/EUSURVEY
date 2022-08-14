package com.ec.survey.model;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "DRAFTS")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Draft {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	private Integer id;
	private AnswerSet answerSet;
	private String uniqueId;
	
	@Id
	@Column(name = "DRAFT_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@OneToOne(cascade = CascadeType.ALL)      
	public AnswerSet getAnswerSet() {return answerSet;}  
	public void setAnswerSet(AnswerSet s) {this.answerSet = s;}  
	
	@Column(name = "DRAFT_UID")
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
}
