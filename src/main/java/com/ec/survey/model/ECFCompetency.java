package com.ec.survey.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.ec.survey.model.survey.Question;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * Represents a competence
 */
@Entity
@Table(name = "ECF_COMPETENCY")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ECFCompetency implements Serializable, Comparable<ECFCompetency>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String competenceUid;
	private String name;
	private String description;
	private List<Question> questions;
	private List<ECFExpectedScore> expectedScores = new ArrayList<>();
	private ECFCluster ecfCluster;
	private Integer orderNumber;

	protected static final Logger logger = Logger.getLogger(ECFCompetency.class);

	private ECFCompetency() {

	}

	public ECFCompetency(String competenceUid, String name, String description) {
		this.competenceUid = competenceUid;
		this.description = description;
		this.name = name;
	}

	public ECFCompetency(String competenceUid, String name, String description, ECFCluster ecfCluster) {
		this.competenceUid = competenceUid;
		this.name = name;
		this.description = description;
		this.ecfCluster = ecfCluster;
	}
	
	public ECFCompetency(String competenceUid, String name, String description, ECFCluster ecfCluster, Integer orderNumber) {
		this.competenceUid = competenceUid;
		this.name = name;
		this.description = description;
		this.ecfCluster = ecfCluster;
		this.orderNumber = orderNumber;
	}

	@Id
	@Column(name = "COMPETENCY_ID")
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "COMPETENCY_UID")
	public String getCompetenceUid() {
		return competenceUid;
	}

	public void setCompetenceUid(String competenceUid) {
		this.competenceUid = competenceUid;
	}

	@Column(name = "COMPETENCY_NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "COMPETENCY_DESC")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JsonIgnore
	@OneToMany(mappedBy = "ecfCompetency", cascade = CascadeType.ALL, orphanRemoval = true)
	@Fetch(value = FetchMode.SELECT)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	@JsonIgnore
	@OneToMany(mappedBy = "id.competency", cascade = CascadeType.ALL, orphanRemoval = true)
	@Fetch(value = FetchMode.SELECT)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<ECFExpectedScore> getECFExpectedScores() {
		return expectedScores;
	}

	public void setECFExpectedScores(List<ECFExpectedScore> expectedScores) {
		this.expectedScores = expectedScores;
	}

	public void addECFExpectedScore(ECFExpectedScore expectedScore) {
		List<ECFExpectedScore> newScores = new ArrayList<>();
		newScores.addAll(this.expectedScores);
		this.expectedScores = newScores;
	}

	@Column(name = "ORDER_NUMBER")
	public Integer getOrderNumber() {
		return this.orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "ECF_CLUSTER", referencedColumnName = "ECF_CLUSTER_ID")
	public ECFCluster getEcfCluster() {
		return ecfCluster;
	}

	public void setEcfCluster(ECFCluster ecfCluster) {
		this.ecfCluster = ecfCluster;
	}

	public ECFCompetency replaceScore(ECFProfile previousScoreId, ECFExpectedScore copiedScore) {
		this.setECFExpectedScores(this.expectedScores.stream().map(expectedScore -> {
			if (expectedScore.getECFExpectedScoreToProfileEid().getECFProfile().equals(previousScoreId)) {
				return copiedScore;
			} else {
				return expectedScore;
			}
		}).collect(Collectors.toList()));
		return this;
	}

	public ECFCompetency copy() {
		ECFCompetency competencyCopy = new ECFCompetency(UUID.randomUUID().toString(), this.getName(),
				this.getDescription(), this.getEcfCluster(), this.getOrderNumber());

		return competencyCopy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((competenceUid == null) ? 0 : competenceUid.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ECFCompetency other = (ECFCompetency) obj;
		if (competenceUid == null) {
			if (other.competenceUid != null)
				return false;
		} else if (!competenceUid.equals(other.competenceUid))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public int compareTo(ECFCompetency otherObject) {
		return this.getOrderNumber().compareTo(otherObject.getOrderNumber());
	}

}
