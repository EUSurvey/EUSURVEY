package com.ec.survey.model;

import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.*;

@Entity
@Table(name = "PENDING_CHANGES", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "SURVEY_UID" }, name = "SURVEY_UID") })
public class PendingChanges implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String surveyUid;
	private Set<String> newElements = new HashSet<>();
	private Set<String> changedElements = new HashSet<>();
	private Set<String> deletedElements = new HashSet<>();
	
	@Id
	@Column(name = "PENDING_CHANGES_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "SURVEY_UID", length = 36)
	public String getSurveyUid() {
		return surveyUid;
	}
	public void setSurveyUid(String surveyUid) {
		this.surveyUid = surveyUid;
	}

	@ElementCollection
	@Cascade(value={CascadeType.ALL})
	public Set<String> getNewElements() {
		return newElements;
	}
	public void setNewElements(Set<String> newElements) {
		this.newElements = newElements;
	}

	@ElementCollection
	@Cascade(value={CascadeType.ALL})
	public Set<String> getChangedElements() {
		return changedElements;
	}
	public void setChangedElements(Set<String> changedElements) {
		this.changedElements = changedElements;
	}

	@ElementCollection
	@Cascade(value={CascadeType.ALL})
	public Set<String> getDeletedElements() {
		return deletedElements;
	}
	public void setDeletedElements(Set<String> deletedElements) {
		this.deletedElements = deletedElements;
	}

	@Transient
	public boolean hasPendingChanges() {
		return !newElements.isEmpty() || !changedElements.isEmpty() || !deletedElements.isEmpty();
	}
}
