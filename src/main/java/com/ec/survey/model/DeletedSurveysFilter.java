package com.ec.survey.model;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.ec.survey.tools.ConversionTools;

public class DeletedSurveysFilter {
	
	private String id;
	private String uniqueId;
	private String title;
	private String alias;
	private String owner;
	private Date createdFrom;
	private Date createdTo;
	private Date deletedFrom;
	private Date deletedTo;	
	private Boolean finished;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern=ConversionTools.DateFormat)
	public Date getCreatedFrom() {
		return createdFrom;
	}
	public void setCreatedFrom(Date createdFrom) {
		this.createdFrom = createdFrom;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern=ConversionTools.DateFormat)
	public Date getCreatedTo() {
		return createdTo;
	}
	public void setCreatedTo(Date createdTo) {
		this.createdTo = createdTo;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern=ConversionTools.DateFormat)
	public Date getDeletedFrom() {
		return deletedFrom;
	}
	public void setDeletedFrom(Date deletedFrom) {
		this.deletedFrom = deletedFrom;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern=ConversionTools.DateFormat)
	public Date getDeletedTo() {
		return deletedTo;
	}
	public void setDeletedTo(Date deletedTo) {
		this.deletedTo = deletedTo;
	}
	
	public String getShortname() {
		return alias;
	}
	public void setShortname(String alias) {
		this.alias = alias;
	}
	
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public Boolean getFinished() {
		return finished;
	}
	public void setFinished(Boolean finished) {
		this.finished = finished;
	}
	
}
