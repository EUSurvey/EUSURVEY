package com.ec.survey.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "CHANGEOWNER", uniqueConstraints = {@UniqueConstraint(columnNames={"CHANGEOWNER_CODE"},name="CHANGEOWNER_CODE"),@UniqueConstraint(columnNames={"CHANGEOWNER_SURVEYUID"},name="CHANGEOWNER_SURVEYUID")})
public class ChangeOwnerRequest {

	private Integer id;
	private String code;
	private String surveyUid;
	private Date created;
	private String login;
	private String email;
	private boolean addAsFormManager;

	@Id
	@Column(name = "CHANGEOWNER_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "CHANGEOWNER_CODE")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	@Column(name = "CHANGEOWNER_SURVEYUID")
	public String getSurveyUid() {
		return surveyUid;
	}
	public void setSurveyUid(String surveyUid) {
		this.surveyUid = surveyUid;
	}
	
	@Column(name = "CHANGEOWNER_DATE")
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}

	@Column(name = "CHANGEOWNER_LOGIN")
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

	@Column(name = "CHANGEOWNER_EMAIL")
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

	@Column(name = "CHANGEOWNER_FORMMANAGER")
    public boolean isAddAsFormManager() { return addAsFormManager; }
    public void setAddAsFormManager(boolean addAsFormManager) { this.addAsFormManager = addAsFormManager; }
}
