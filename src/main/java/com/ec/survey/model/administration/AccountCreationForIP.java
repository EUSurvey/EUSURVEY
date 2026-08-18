package com.ec.survey.model.administration;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "ACCOUNTCREATION", indexes = { @Index(name = "AC_IDX", columnList = "ACCOUNTCREATION_IP, ACCOUNTCREATION_DOMAIN, ACCOUNTCREATION_DATE") })
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AccountCreationForIP implements java.io.Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String ip;
	private String domain;
	private Date date;

	@Id
	@Column(name = "ACCOUNTCREATION_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "ACCOUNTCREATION_IP")
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Column(name = "ACCOUNTCREATION_DATE")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "ACCOUNTCREATION_DOMAIN")
    public String getDomain() {
        return domain;
    }
    public void setDomain(String domain) {
        this.domain = domain;
    }
}
