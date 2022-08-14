package com.ec.survey.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
@Table(name = "LANGUAGES")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Language implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String code;
	private String name;
	private String englishName;
	private boolean official;
	
	public Language()
	{}

	public Language(String code, String name, String englishName, boolean official) {
		this.code = code;
		this.name = name;
		this.englishName = englishName;
		this.official = official;
	}
	
	@Id
	@Column(name = "LANGUAGE_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "LANGUAGE_NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "LANGUAGE_ENNAME")
	public String getEnglishName() {
		return englishName;
	}
	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}
	
	@Column(name = "LANGUAGE_CODE")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "LANGUAGE_OFFI")
	public boolean isOfficial() {
		return official;
	}
	public void setOfficial(boolean official) {
		this.official = official;
	}
	
}
