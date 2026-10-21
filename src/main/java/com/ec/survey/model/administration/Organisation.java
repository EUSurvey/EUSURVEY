package com.ec.survey.model.administration;

import com.ec.survey.model.WhitelistedUser;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "ORGANISATIONS", uniqueConstraints = { @UniqueConstraint(columnNames = { "ORGANISATION_ORGANISATION" }, name = "ORGANISATION_ORGANISATION") })
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Organisation implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String organisation;
	private List<User> managers = new ArrayList<>();
	private List<WhitelistedUser> whitelist = new ArrayList<>();

	@Id
	@Column(name = "ORGANISATION_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "ORGANISATION_ORGANISATION")
	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	@ManyToMany(targetEntity = User.class)
	@JoinTable(name = "ORGANISATION_MANAGERS", foreignKey = @ForeignKey(javax.persistence.ConstraintMode.NO_CONSTRAINT),
			joinColumns = @JoinColumn(name = "ORGANISATION_ID"),
			inverseJoinColumns = @JoinColumn(name = "USER_ID"), uniqueConstraints = @UniqueConstraint(columnNames = {"ORGANISATION_ID", "USER_ID"}))
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<User> getManagers() {
		return managers;
	}

	public void setManagers(List<User> managers) {
		this.managers = managers;
	}

	@ManyToMany(targetEntity = WhitelistedUser.class)
	@JoinTable(name = "ORGANISATION_WHITELIST", foreignKey = @ForeignKey(javax.persistence.ConstraintMode.NO_CONSTRAINT),
			joinColumns = @JoinColumn(name = "ORGANISATION_ID"),
			inverseJoinColumns = @JoinColumn(name = "WHITELISTEDUSER_ID"), uniqueConstraints = @UniqueConstraint(columnNames = {"ORGANISATION_ID", "WHITELISTEDUSER_ID"}))
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<WhitelistedUser> getWhitelist() {
		return whitelist;
	}

	public void setWhitelist(List<WhitelistedUser> whitelist) {
		this.whitelist = whitelist;
	}

}
