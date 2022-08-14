package com.ec.survey.model;

import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "ATTEMPTS")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class WrongAttempts {

	private Integer id;
	private String ip;
	private int counter;
	private Date lockDate;

	public static final int LIMIT = 10;
	
	public WrongAttempts() {}
	
	public WrongAttempts(String ip) {
		this.ip = ip;
	}
	
	@Id
	@Column(name = "ATTEMPTS_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "ATTEMPTS_IP")
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}

	@Column(name = "ATTEMPTS_COUNT")
	public Integer getCounter() {
		return counter;
	}
	public void setCounter(Integer counter) {
		this.counter = counter != null ? counter : 0;
	}
	
	@Column(name = "ATTEMPTS_DATE")
	public Date getLockDate() {
		return lockDate;
	}
	public void setlockDate(Date lockDate) {
		this.lockDate = lockDate;
	}
	
	@Transient
	public void increaseCounter() {
		counter++;
		if (counter == LIMIT) lockDate = new Date();		
	}
	
}
