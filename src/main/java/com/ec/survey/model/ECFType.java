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
 * Represents a set of competency clusters
 */
@Entity
@Table(name = "ECF_TYPE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ECFType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String uid;
	private String name;

	private List<ECFCluster> clusters = new ArrayList<>();

	public ECFType() {

	}

	public ECFType(String uid, String name) {
		this.uid = uid;
		this.name = name;
	}

	@Id
	@Column(name = "ECF_TYPE_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "ECF_TYPE_UID")
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

	@JsonIgnore
	@OneToMany(mappedBy = "ecfType", cascade = CascadeType.ALL, orphanRemoval = true)
	@Fetch(value = FetchMode.SELECT)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<ECFCluster> getClusters() {
		return clusters;
	}

	public void setClusters(List<ECFCluster> clusters) {
		this.clusters = clusters;
	}

	public void addCluster(ECFCluster cluster) {
		this.clusters.add(cluster);
	}

	public ECFType copy() {
		ECFType typeCopy = new ECFType(UUID.randomUUID().toString(), this.getName());
		return typeCopy;
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
		ECFType other = (ECFType) obj;
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
