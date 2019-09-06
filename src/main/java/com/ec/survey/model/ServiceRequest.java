package com.ec.survey.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.format.annotation.DateTimeFormat;
import com.ec.survey.tools.ConversionTools;

@Entity
@Table(name = "SERVICEREQUESTS", uniqueConstraints = {@UniqueConstraint(columnNames={"REQUESTS_USERID"},name="REQUESTS_USERID")})
public class ServiceRequest implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer userId;
	private Integer counter;
	private Date date;
	
	@Id
	@Column(name = "REQUESTS_ID")
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "REQUESTS_USERID")
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	@Column(name = "REQUESTS_DATE")
	@DateTimeFormat(pattern=ConversionTools.DateTimeFormat)
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	@Column(name = "REQUESTS_COUNTER")
	public Integer getCounter() {
		return counter;
	}
	public void setCounter(Integer counter) {
		this.counter = counter;
	}
	
}

