package com.ec.survey.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "USERSCONFIGURATION")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class UsersConfiguration implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private int userId;
	private boolean showName = true;
	private boolean showEmail = true;
	private boolean showLanguage = true;
	private boolean showRoles = false;
	private boolean showComment = false;
	
	@Id
	@Column(name = "UC_ID")
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "UC_USER")
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Column(name = "UC_NAME")
	public boolean getShowName() {
		return showName;
	}
	public void setShowName(boolean showName) {
		this.showName = showName;
	}
	
	@Column(name = "UC_EMAIL")
	public boolean getShowEmail() {
		return showEmail;
	}
	public void setShowEmail(boolean showEmail) {
		this.showEmail = showEmail;
	}
	
	@Column(name = "UC_LANG")
	public boolean getShowLanguage() {
		return showLanguage;
	}
	public void setShowLanguage(boolean showLanguage) {
		this.showLanguage = showLanguage;
	}
	
	@Column(name = "UC_ROLES")
	public boolean getShowRoles() {
		return showRoles;
	}
	public void setShowRoles(boolean showRoles) {
		this.showRoles = showRoles;
	}
	
	@Column(name = "UC_COMM")
	public boolean getShowComment() {
		return showComment;
	}
	public void setShowComment(boolean showComment) {
		this.showComment = showComment;
	}
	
}
