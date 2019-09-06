package com.ec.survey.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MESSAGE_TYPES")
public class MessageType{
	
	private int id;
	private int criticality;
	private int defaultTime;
	private String label;
	private String icon;
	private String css;
	
	@Id
	@Column(name = "MT_ID")
	@GeneratedValue
	public Integer getId() {
		return id;
	}	
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "MT_LABEL")
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	@Column(name = "MT_CRITICALITY")
	public int getCriticality() {
		return criticality;
	}
	public void setCriticality(int criticality) {
		this.criticality = criticality;
	}

	@Column(name = "MT_TIME")
	public int getDefaultTime() {
		return defaultTime;
	}
	public void setDefaultTime(int defaultTime) {
		this.defaultTime = defaultTime;
	}

	@Column(name = "MT_ICON")
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	@Column(name = "MT_CSS")
	public String getCss() {
		return css;
	}
	public void setCss(String css) {
		this.css = css;
	}  
	
	

}

