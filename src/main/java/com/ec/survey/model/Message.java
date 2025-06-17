package com.ec.survey.model;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.Tools;

@Entity
@Table(name = "MESSAGES", indexes = { @Index(name = "MESSAGE_USER", columnList = "M_USER")})
public class Message{
	
	private int id;
	private int criticality;
	private int time;
	private String text;
	private boolean active;
	
	// 1: form managers only
	// 2: participants only
	// 3: participants and form managers
	// 4: everyone
	// 5: admins only
	private int type;
	private int version;
	private List<MessageType> types;
	private boolean alreadyShown;
	private Date autoDeactivate;
	private Integer userId;
	
	@Id
	@Column(name = "M_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}	
	public void setId(Integer id) {
		this.id = id;
	}

	@Lob
	@Column(name = "M_TEXT")
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	@Column(name = "M_CRITICALITY")
	public int getCriticality() {
		return criticality;
	}
	public void setCriticality(int criticality) {
		this.criticality = criticality;
	}

	@Column(name = "M_STATE")
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Column(name = "M_TYPE")
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	@Column(name = "M_TIME")
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}

	@Column(name = "M_VERSION")
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
	@Column(name = "M_USER")
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "M_AUTODEAC")
	@DateTimeFormat(pattern=ConversionTools.DateTimeFormat)
	public Date getAutoDeactivate() {
		return autoDeactivate;
	}
	public void setAutoDeactivate(Date autoDeactivate) {
		this.autoDeactivate = autoDeactivate;
	}
	
	@Transient
	public String getAutoDeactivateDate()
	{
		if (autoDeactivate != null)
		{
			return Tools.formatDate(autoDeactivate, ConversionTools.DateFormat);
		}
		return "";
	}
	
	@Transient
	public String getAutoDeactivateTime()
	{
		if (autoDeactivate != null)
		{
			String result = Tools.formatDate(autoDeactivate, "HH:mm").substring(0,2);
			if (result.startsWith("0")) result = result.substring(1,2);
			return result;
		}
		return "0";
	}
	
	@Transient
	public List<MessageType> getTypes() {
		return types;
	}
	public void setTypes(List<MessageType> types) {
		this.types = types;
	}  	
	
	@Transient
	public String getIcon()
	{
		if (types != null)
		{
			for (MessageType type : types) {
				if (type.getCriticality() == criticality)
				{
					return type.getIcon();
				}
			}
		}
		
		return null;
	}
	
	@Transient
	public String getCss()
	{
		if (types != null)
		{
			for (MessageType type : types) {
				if (type.getCriticality() == criticality)
				{
					return type.getCss();
				}
			}
		}
		
		return null;
	}
	
	@Transient
	public boolean isAlreadyShown() {
		return alreadyShown;
	}
	public void setAlreadyShown(boolean alreadyShown) {
		this.alreadyShown = alreadyShown;
	}	
}

