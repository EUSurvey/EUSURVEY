package com.ec.survey.model;

import javax.persistence.*;

@Entity
@Table(name = "PROPERTIES")
public class Property {

	private Integer id;
	private String key;
	private String value;

    public static final String DEPARTMENTS = "DEPARTMENTS";

	@Id
	@Column(name = "PROPERTIES_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "PROPERTIES_KEY", unique = true)
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	@Column(name = "PROPERTIES_VALUE", length = 500000)
	@Lob
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

}
