package com.ec.survey.model;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.ec.survey.model.administration.User;

@Entity
@Table(name = "INVTEMPL", uniqueConstraints = {@UniqueConstraint(columnNames={"INVTEMPL_NAME", "OWNER"},name="INVTEMPL_NAME")})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class InvitationTemplate implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private String template1;
	private String template2;
	private String templateSubject;
	private String templateMail;
	private int templateText;
	private User owner;
	private String replyto;
	
	@Id
	@Column(name = "INVTEMPL_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "INVTEMPL_NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "INVTEMPL1")
	@Lob
	public String getTemplate1() {
		return template1;
	}
	public void setTemplate1(String template1) {
		this.template1 = template1;
	}
	
	@Column(name = "INVTEMPL2")
	@Lob
	public String getTemplate2() {
		return template2;
	}
	public void setTemplate2(String template2) {
		this.template2 = template2;
	}
	
	@Column(name = "INVTEMPLSUBJ")
	public String getTemplateSubject() {
		return templateSubject;
	}
	public void setTemplateSubject(String templateSubject) {
		this.templateSubject = templateSubject;
	}
	
	@Column(name = "INVTEMPLMAIL")
	public String getTemplateMail() {
		return 	templateMail;
	}
	public void setTemplateMail(String 	templateMail) {
		this.templateMail = templateMail;
	}
	
	@Column(name = "INVTEMPLTEXT")
	public Integer getTemplateText() {
		return templateText;
	}
	public void setTemplateText(Integer templateText) {
		this.templateText = templateText;
	}

	@ManyToOne  
	@JoinColumn(name="OWNER", nullable = false)    
	public User getOwner() {return owner;}  
	public void setOwner(User owner) {this.owner = owner;}
	
	@Column(name = "INVTEMPLREPLY")
	public String getReplyto() {
		return replyto;
	}
	public void setReplyto(String replyto) {
		this.replyto = replyto;
	}
}
