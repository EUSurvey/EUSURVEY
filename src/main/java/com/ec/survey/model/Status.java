package com.ec.survey.model;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

import com.ec.survey.tools.ConversionTools;

@Entity
@Table(name = "STATUS")
public class Status {

	public enum FSCheckStates
	{
		Unset, FirstThresholdEmailSent, SecondThresholdEmailSent
	}

	private Integer id;
	private int dbversion;
	private Date updateDate;
	private Date lastAnswerSetAnonymDate;
	private FSCheckStates fsCheckState = FSCheckStates.Unset;
	
	@Id
	@Column(name = "STATUS_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "DBVERSION")
	public int getDbversion() {
		return dbversion;
	}
	public void setDbversion(int dbversion) {
		this.dbversion = dbversion;
	}	
	
	@Column(name = "DBUPDATE")
	public Date getUpdateDate() {
		return updateDate;
	}	
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}	

	/**
	 * Gets the time of the last answer set that was anonymised
	 */
	@Column(name = "ANSWERANONYMDATE")
	@DateTimeFormat(pattern=ConversionTools.DateTimeFormat)
	public Date getLastAnswerSetAnonymDate() {
		return lastAnswerSetAnonymDate;
	}	

	/**
	 * Sets the time of the last answer set that was anonymised
	 */
	public void setLastAnswerSetAnonymDate(Date lastAnswerSetAnonymDate) {
		this.lastAnswerSetAnonymDate = lastAnswerSetAnonymDate;
	}

	@Column(name = "FSCHECKSTATE")
	public FSCheckStates getFsCheckState() {
		return fsCheckState;
	}

	public void setFsCheckState(FSCheckStates fsCheckState) {
		this.fsCheckState = fsCheckState == null ? FSCheckStates.Unset : fsCheckState;
	}
}
