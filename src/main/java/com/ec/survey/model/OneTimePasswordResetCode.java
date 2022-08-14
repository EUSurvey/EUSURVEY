package com.ec.survey.model;

import java.util.Date;
import java.util.UUID;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import com.ec.survey.model.administration.User;

@Entity
@Table(name = "PASWWORDRESET")
public class OneTimePasswordResetCode {
	
	private int id;
	private String login;
	private String email;	
	private String code;
	private int userId;
	private Date created;
	
	public OneTimePasswordResetCode(){}
		
	public OneTimePasswordResetCode(User user) {
		created = new Date();
		login = user.getLogin();
		email = user.getEmail();
		userId = user.getId();
		code = UUID.randomUUID().toString();
	}
	
	@Id
	@Column(name = "PR_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "PR_LOGIN")
	public String getLogin() {
		return login;
	}	
	public void setLogin(String login) {
		this.login = login;
	}
	
	@Column(name = "PR_EMAIL")
	public String getEmail() {
		return email;
	}	
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Column(name = "PR_CODE")
	public String getCode() {
		return code;
	}	
	public void setCode(String code) {
		this.code = code;
	}
	
	@Column(name = "PR_USER")
	public Integer getUserId() {
		return userId;
	}	
	public void setUserId(Integer userId) {
		this.userId = userId != null ? userId : 0;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PR_CREATED", nullable = false)
	@DateTimeFormat(pattern="dd/MM/yyyy")
	public Date getCreated() {
		return created;
	}	
	public void setCreated(Date created) {
		this.created = created;
	}


}
