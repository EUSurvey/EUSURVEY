package com.ec.survey.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents a competency cluster
 */
@Entity
@Table(name = "ECF_CLUSTER")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ECFCluster implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String uid;
	private String name;
	private ECFType ecfType;
	private List<ECFCompetency> competencies = new ArrayList<>();

	public ECFCluster() {

	}

	public ECFCluster(String uid, String name) {
		this.uid = uid;
		this.name = name;
	}

	public ECFCluster(String uid, String name, ECFType ecfType) {
		this.uid = uid;
		this.name = name;
		this.ecfType = ecfType;
	}

	@Id
	@Column(name = "ECF_CLUSTER_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "ECF_CLUSTER_UID")
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	@Column(name = "ECF_CLUSTER_NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne
	@JoinColumn(name = "ECF_TYPE", referencedColumnName = "ECF_TYPE_ID")
	public ECFType getEcfType() {
		return ecfType;
	}

	public void setEcfType(ECFType ecfType) {
		this.ecfType = ecfType;
	}

	@JsonIgnore
	@OneToMany(mappedBy = "ecfCluster", cascade = CascadeType.ALL, orphanRemoval = true)
	@Fetch(value = FetchMode.SELECT)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<ECFCompetency> getCompetencies() {
		return competencies;
	}

	public void setCompetencies(List<ECFCompetency> competencies) {
		this.competencies = competencies;
	}

	public void addCompetency(ECFCompetency competency) {
		List<ECFCompetency> newCompetencies = new ArrayList<>();
		newCompetencies.addAll(this.competencies);
		this.competencies = newCompetencies;
	}

	public ECFCluster copy() {
		ECFCluster clusterCopy = new ECFCluster(UUID.randomUUID().toString(), this.getName(), this.getEcfType());
		return clusterCopy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
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
		ECFCluster other = (ECFCluster) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (uid == null) {
			if (other.uid != null)
				return false;
		} else if (!uid.equals(other.uid))
			return false;
		return true;
	}

}
