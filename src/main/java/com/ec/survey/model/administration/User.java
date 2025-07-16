package com.ec.survey.model.administration;

import com.ec.survey.model.ResultAccess;
import com.ec.survey.model.attendees.AttributeName;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.apache.commons.lang3.text.WordUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.*;
import java.util.Map.Entry;

@Entity
@Table(name = "USERS", uniqueConstraints = { @UniqueConstraint(columnNames = { "USER_LOGIN" }, name = "USER_LOGIN") })
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String login;
	private String password;
	private String passwordSalt;
	private String email;
	private String emailToValidate;
	private String otherEmail;
	private String comment;
	private String language = "EN";
	private String defaultPivotLanguage = "EN";
	private String type;
	private String displayName;
	private String givenName;
	private String surName;
	private boolean validated = false;
	private List<Role> roles;
	private Map<GlobalPrivilege, Integer> globalPrivileges;
	private Map<LocalPrivilege, Integer> localPrivileges;
	private boolean privilegesLoaded = false;
	private List<AttributeName> selectedAttributes;
	private String selectedAttributesOrder;
	private String validationCode;
	private Date validationCodeGeneration;
	private List<String> departments = new ArrayList<>();
	private int badLoginAttempts = 0;
	private int userExistsAttempts = 0;
	private Date userExistsAttemptDate;
	private boolean agreedToToS;
	private Date agreedToToSDate;
	private String agreedToToSVersion;
	private boolean agreedToPS;
	private Date agreedToPSDate;
	private String agreedToPSVersion;
	private Integer lastEditedSurvey;
	private boolean canCreateSurveys = true;
	private boolean isFrozen = false;
	private boolean deleted;
	private boolean deleteRequested;
	private String deleteCode;
	private Date deleteDate;
	private ResultAccess resultAccess;
	private String organisation;
	
	public static final String ECAS = "ECAS";
	public static final String SYSTEM = "SYSTEM";

	public User() {
		globalPrivileges = new HashMap<>();
		globalPrivileges.put(GlobalPrivilege.UserManagement, 0);
		globalPrivileges.put(GlobalPrivilege.FormManagement, 0);
		globalPrivileges.put(GlobalPrivilege.ContactManagement, 0);
		globalPrivileges.put(GlobalPrivilege.RightManagement, 0);
		globalPrivileges.put(GlobalPrivilege.ECAccess, 0);
		globalPrivileges.put(GlobalPrivilege.SystemManagement, 0);

		localPrivileges = new HashMap<>();
		localPrivileges.put(LocalPrivilege.AccessDraft, 0);
		localPrivileges.put(LocalPrivilege.FormManagement, 0);
		localPrivileges.put(LocalPrivilege.AccessResults, 0);
		localPrivileges.put(LocalPrivilege.ManageInvitations, 0);

		roles = new ArrayList<>();
		selectedAttributes = new ArrayList<>();
	}

	@Id
	@Column(name = "USER_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "USER_LOGIN")
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@Column(name = "USER_PASSWORD")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "USER_PWSALT")
	public String getPasswordSalt() {
		return passwordSalt;
	}

	public void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}

	@Column(name = "USER_EMAIL", nullable = false)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "USER_EMAIL_TO_VALIDATE")
	public String getEmailToValidate() {
		return emailToValidate;
	}

	public void setEmailToValidate(String emailToValidate) {
		this.emailToValidate = emailToValidate;
	}

	@Column(name = "USER_COMMENT")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Column(name = "USER_OTHEREMAIL")
	public String getOtherEmail() {
		return otherEmail;
	}

	public void setOtherEmail(String otherEmail) {
		if (otherEmail != null) {
			otherEmail = otherEmail.replace("\n", "").replace("\r", "");
		}
		this.otherEmail = otherEmail;
	}

	@Column(name = "USER_LANGUAGE")
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		if (language != null) {
			this.language = language;
		}
	}

	@Column(name = "USER_PIVOTLANGUAGE")
	public String getDefaultPivotLanguage() {
		return defaultPivotLanguage != null ? defaultPivotLanguage : "EN";
	}

	public void setDefaultPivotLanguage(String defaultPivotLanguage) {
		if (defaultPivotLanguage != null) {
			this.defaultPivotLanguage = defaultPivotLanguage;
		}
	}

	@Column(name = "USER_TYPE")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "USER_GIVENNAME")
	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	@Transient
	public String getGivenNameOrLogin() {
		String result = getName();
		if (givenName != null && givenName.length() > 0)
			result = givenName;
		return WordUtils.capitalizeFully(result);
	}

	@Column(name = "USER_SURNAME")
	public String getSurName() {
		return surName;
	}

	public void setSurName(String surName) {
		this.surName = surName;
	}

	@ManyToMany(targetEntity = Role.class)
	@JoinTable(foreignKey = @ForeignKey(javax.persistence.ConstraintMode.NO_CONSTRAINT),
			joinColumns = @JoinColumn(name = "USERS_USER_ID"),
			inverseJoinColumns = @JoinColumn(name = "roles_ROLE_ID"))
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	@Transient
	public String getRolesAsString() {
		StringBuilder s = new StringBuilder();
		if (roles != null) {
			for (Role role : roles) {
				s.append(role.getId()).append(";");
			}
		}
		return s.toString();
	}

	@ManyToMany(targetEntity = AttributeName.class)
	@JoinTable(foreignKey = @ForeignKey(javax.persistence.ConstraintMode.NO_CONSTRAINT),
			joinColumns = @JoinColumn(name = "USERS_USER_ID"),
			inverseJoinColumns = @JoinColumn(name = "selectedAttributes_AN_ID"))
	@Fetch(value = FetchMode.SELECT)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<AttributeName> getSelectedAttributes() {
		return selectedAttributes;
	}

	public void setSelectedAttributes(List<AttributeName> selectedAttributes) {
		this.selectedAttributes = selectedAttributes;
	}

	@Column(name = "USER_ATTORDER")
	public String getSelectedAttributesOrder() {
		return selectedAttributesOrder;
	}

	public void setSelectedAttributesOrder(String selectedAttributesOrder) {
		this.selectedAttributesOrder = selectedAttributesOrder;
	}

	@Column(name = "VALIDATED")
	public boolean getValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	@Column(name = "VALIDCODE")
	public String getValidationCode() {
		return validationCode;
	}

	public void setValidationCode(String validationCode) {
		this.validationCode = validationCode;
	}

	@Column(name = "VALIDDATE")
	public Date getValidationCodeGeneration() {
		return validationCodeGeneration;
	}

	public void setValidationCodeGeneration(Date validationCodeGeneration) {
		this.validationCodeGeneration = validationCodeGeneration;
	}

	@Column(name = "USER_DISPLAYNAME")
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Column(name = "ATTEMPTS")
	public int getBadLoginAttempts() {
		return badLoginAttempts;
	}

	public void setBadLoginAttempts(Integer badLoginAttempts) {
		this.badLoginAttempts = badLoginAttempts != null ? badLoginAttempts : 0;
	}

	@Column(name = "USER_EXISTS_ATTEMPS")
	public int getUserExistsAttempts() {
		return this.userExistsAttempts;
	}

	public void setUserExistsAttempts(Integer userExistsAttempts) {
		this.userExistsAttempts = userExistsAttempts != null ? userExistsAttempts : 0;
	}

	@Column(name = "USER_EXISTS_ATTEMPT_DATE")
	public Date getUserExistsAttemptDate() {
		return userExistsAttemptDate;
	}

	public void setUserExistsAttemptDate(Date userExistsAttemptDate) {
		this.userExistsAttemptDate = userExistsAttemptDate;
	}

	@Column(name = "USER_TOS")
	public boolean isAgreedToToS() {
		return agreedToToS;
	}

	public void setAgreedToToS(Boolean agreedToToS) {
		this.agreedToToS = agreedToToS != null && agreedToToS;
	}

	public void setAgreedToToS(boolean agreedToToS) {
		this.agreedToToS = agreedToToS;
	}

	@Column(name = "USER_TOSDATE")
	public Date getAgreedToToSDate() {
		return agreedToToSDate;
	}

	public void setAgreedToToSDate(Date agreedToToSDate) {
		this.agreedToToSDate = agreedToToSDate;
	}

	@Column(name = "USER_TOSVERSION")
	public String getAgreedToToSVersion() {
		return agreedToToSVersion;
	}

	public void setAgreedToToSVersion(String agreedToToSVersion) {
		this.agreedToToSVersion = agreedToToSVersion;
	}

	@Column(name = "USER_PS")
	public boolean isAgreedToPS() {
		return agreedToPS;
	}

	public void setAgreedToPS(Boolean agreedToPS) {
		this.agreedToPS = agreedToPS != null && agreedToPS;
	}

	@Column(name = "USER_PSDATE")
	public Date getAgreedToPSDate() {
		return agreedToPSDate;
	}

	public void setAgreedToPSDate(Date agreedToPSDate) {
		this.agreedToPSDate = agreedToPSDate;
	}

	@Column(name = "USER_PSVERSION")
	public String getAgreedToPSVersion() {
		return agreedToPSVersion;
	}

	public void setAgreedToPSVersion(String agreedToPSVersion) {
		this.agreedToPSVersion = agreedToPSVersion;
	}

	@Column(name = "USER_DELETED")
	public Boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted != null && deleted;
	}

	@Column(name = "USER_DELREQ")
	public Boolean isDeleteRequested() {
		return deleteRequested;
	}

	public void setDeleteRequested(Boolean deleteRequested) {
		this.deleteRequested = deleteRequested != null && deleteRequested;
	}

	@Column(name = "USER_DELDATE")
	public Date getDeleteDate() {
		return deleteDate;
	}

	public void setDeleteDate(Date deleteDate) {
		this.deleteDate = deleteDate;
	}

	@Column(name = "USER_DELCODE")
	public String getDeleteCode() {
		return deleteCode;
	}

	public void setDeleteCode(String deleteCode) {
		this.deleteCode = deleteCode;
	}

	@Column(name = "USER_LAST_SURVEY")
	public Integer getLastEditedSurvey() {
		return lastEditedSurvey;
	}

	public void setLastEditedSurvey(Integer lastEditedSurvey) {
		this.lastEditedSurvey = lastEditedSurvey;
	}

	@Column(name = "USER_FROZEN")
	public boolean isFrozen() {
		return isFrozen;
	}

	public void setFrozen(Boolean isFrozen) {
		this.isFrozen = isFrozen != null && isFrozen;
	}
	
	@Column(name = "USER_ORGANISATION")
	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	@Transient
	public String getName() {
		if (displayName != null && displayName.length() > 0) {
			return displayName;
		} else {
			return login;
		}
	}

	@Transient
	public String getFirstLastName() {
		if (surName != null && surName.length() > 0) {
			return givenName + " " + surName;
		} else {
			return getName();
		}
	}

	@Transient
	public String getDepartment() {
		if (departments != null && !departments.isEmpty()) {
			return "(EC-" + departments.get(departments.size() - 1) + ")";
		}

		if (ECAS.equals(type) && departments != null && departments.size() == 1
				&& departments.get(0).equalsIgnoreCase("external")) {
			return "(EXT)";
		}

		return "";
	}

	@Transient
	public boolean isSelected(AttributeName pattributeName) {
		for (AttributeName attributeName : selectedAttributes) {
			if (attributeName.getId().equals(pattributeName.getId())) {
				return true;
			}
		}
		return false;
	}

	@Transient
	private void loadPrivileges() {
		for (Role role : roles) {
			for (GlobalPrivilege privilege : role.getGlobalPrivileges().keySet()) {
				if (role.getGlobalPrivileges().get(privilege) > globalPrivileges.get(privilege)) {
					globalPrivileges.put(privilege, role.getGlobalPrivileges().get(privilege));
				}
			}
		}
		privilegesLoaded = true;
	}
	

	@Transient
	@JsonIgnore
	public ResultAccess getResultAccess() {
		return resultAccess;
	}

	@Transient
	@JsonIgnore
	public Boolean getResultAccessWrite() {
		return resultAccess != null && !resultAccess.isReadonly();
	}

	public void setResultAccess(ResultAccess resultAccess) {
		this.resultAccess = resultAccess;
	}

	@Transient
	public Map<LocalPrivilege, Integer> getLocalPrivileges() {
		return localPrivileges;
	}

	public void setLocalPrivileges(Map<LocalPrivilege, Integer> localPrivileges) {
		this.localPrivileges = localPrivileges;
	}

	@Transient
	public Map<GlobalPrivilege, Integer> getGlobalPrivileges() {
		if (!privilegesLoaded)
			loadPrivileges();
		return globalPrivileges;
	}

	public void setGlobalPrivileges(Map<GlobalPrivilege, Integer> globalPrivileges) {
		this.globalPrivileges = globalPrivileges;
	}

	@Transient
	public boolean getShowAdmin() {
		return getGlobalPrivileges().get(GlobalPrivilege.UserManagement) > 1
				|| getGlobalPrivileges().get(GlobalPrivilege.RightManagement) > 0
				|| getGlobalPrivileges().get(GlobalPrivilege.SystemManagement) > 0;
	}

	@Transient
	public int getContactPrivilege() {
		return getGlobalPrivileges().get(GlobalPrivilege.ContactManagement);
	}

	@Transient
	public int getFormPrivilege() {
		return getGlobalPrivileges().get(GlobalPrivilege.FormManagement);
	}

	@Transient
	public int getECPrivilege() {
		return getGlobalPrivileges().get(GlobalPrivilege.ECAccess);
	}

	@Transient
	public int getLocalPrivilegeValue(String key) {
		return localPrivileges.get(LocalPrivilege.valueOf(key));
	}

	@Transient
	public int getGlobalPrivilegeValue(String key) {
		return getGlobalPrivileges().get(GlobalPrivilege.valueOf(key));
	}

	@Transient
	public List<String> getDepartments() {
		return departments;
	}
	
	@Transient
	@JsonIgnore
	public boolean isECUser() {
		return this.getEmail() != null && this.getEmail().toLowerCase().endsWith("ec.europa.eu");
	}

	public void setDepartments(List<String> departments) {
		this.departments = departments;
	}

	public void upgradePrivileges(Map<LocalPrivilege, Integer> localPrivileges) {
		for (Entry<LocalPrivilege, Integer> entry : localPrivileges.entrySet()) {
			if (entry.getValue() > this.localPrivileges.get(entry.getKey())) {
				this.localPrivileges.put(entry.getKey(), entry.getValue());
			}
		}
	}

	@Transient
	public boolean isExternal() {
		return getECPrivilege() == 0;
	}

	@Transient
	public boolean isCanCreateSurveys() {
		return canCreateSurveys;
	}

	public void setCanCreateSurveys(boolean canCreateSurveys) {
		this.canCreateSurveys = canCreateSurveys;
	}

	@Transient
	public List<String> getAllEmailAddresses() {
		List<String> result = new ArrayList<>();
		result.add(email);

		if (otherEmail != null && otherEmail.length() > 0) {
			String[] emails = otherEmail.split(";");
			for (int i = 0; i < emails.length; i++) {
				if (emails[i].length() > 0) {
					result.add(emails[i]);
				}
			}
		}

		return result;
	}

}
