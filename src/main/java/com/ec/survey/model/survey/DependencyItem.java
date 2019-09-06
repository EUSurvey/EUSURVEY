package com.ec.survey.model.survey;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a list of dependent items that are triggered
 * by another survey element
 */
@Entity
@Table(name = "DEPITEMS")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DependencyItem implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer position;
	private List<Element> dependentElements = new ArrayList<>();
		
	@Id
	@Column(name = "ID")
	@GeneratedValue
	public Integer getId() {
		return id;
	}	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "POS")
	public Integer getPosition() {
		return position;
	}	
	public void setPosition(Integer position) {
		this.position = position;
	}
	
	@ManyToMany(targetEntity=Element.class)
	@JoinTable(name = "POSSIBLEANSWER_ELEMENT")
	@Fetch(value = FetchMode.SELECT)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<Element> getDependentElements() {
		return dependentElements;
	}
	public void setDependentElements(List<Element> dependentElements) {
		this.dependentElements = dependentElements;
	}
	
	@Transient
	public Set<String> getDependentElementUniqueIds() {
		Set<String> results = new HashSet<>();
		for (Element e : dependentElements)
		{
			results.add(e.getUniqueId());
		}
		return results;
	}	
	
}
