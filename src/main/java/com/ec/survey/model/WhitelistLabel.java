package com.ec.survey.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
@Table(name = "WHITELISTLABEL", uniqueConstraints = {@UniqueConstraint(columnNames={"LABEL_NAME"},name="LABEL_NAME")})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class WhitelistLabel implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;

	public WhitelistLabel()
	{}
	
	@Id
	@Column(name = "LABEL_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "LABEL_NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
