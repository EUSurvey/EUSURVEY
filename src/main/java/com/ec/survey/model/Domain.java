package com.ec.survey.model;

import javax.persistence.*;

@Entity
@Table(name = "DOMAINS",  uniqueConstraints = {@UniqueConstraint(columnNames={"CODE"},name="CODE")})
public class Domain implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String code;
	private String description;
	
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	
	public Domain()
	{
		
	}
	
	public Domain(String code, String description) {
		super();
		this.code = code;
		this.description = description;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "CODE")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	

}
