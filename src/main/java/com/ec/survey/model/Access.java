package com.ec.survey.model;

import com.ec.survey.model.administration.LocalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.Survey;
import org.apache.log4j.Logger;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents a privilege that a user (not the owner) has for a survey
 */
@Entity
@Table(name = "SURACCESS", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "ACCESS_USER", "SURVEY" }, name = "ACCESS_USER") })
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Access {

	private int id;
	private User user;
	private String department;
	private Survey survey;
	private Map<LocalPrivilege, Integer> localPrivileges;
	private boolean readonly;

	protected static final Logger logger = Logger.getLogger(Access.class);

	public Access() {
		localPrivileges = new HashMap<>();
		localPrivileges.put(LocalPrivilege.AccessDraft, 0);
		localPrivileges.put(LocalPrivilege.FormManagement, 0);
		localPrivileges.put(LocalPrivilege.AccessResults, 0);
		localPrivileges.put(LocalPrivilege.ManageInvitations, 0);
	}

	@Id
	@Column(name = "ACCESS_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "ACCESS_USER", nullable = true)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(name = "ACCESS_DEPARTMENT", nullable = true)
	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	@ManyToOne
	@JoinColumn(name = "SURVEY")
	public Survey getSurvey() {
		return survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	@Column(name = "ACCESS_PRIVILEGES")
	public String getPrivileges() {
		StringBuilder result = new StringBuilder();
		for (Entry<LocalPrivilege, Integer> entry : localPrivileges.entrySet()) {
			result.append(entry.getKey().toString()).append(":").append(entry.getValue()).append(";");
		}
		return result.toString();
	}

	public void setPrivileges(String privilegesString) {
		try {
			if (privilegesString != null && privilegesString.length() > 0) {
				localPrivileges = new HashMap<>();
				String[] privileges = privilegesString.split(";");
				for (String privilegeString : privileges) {
					if (privilegeString.length() > 0) {
						String[] privilege = privilegeString.split(":");
						localPrivileges.put(LocalPrivilege.valueOf(privilege[0]), Integer.parseInt(privilege[1]));
					}
				}
				if (privileges.length == 3) {
					localPrivileges.put(LocalPrivilege.ManageInvitations,
							localPrivileges.get(LocalPrivilege.FormManagement));
				}
			} else {
				logger.info("empty privileges string for access " + id + " found");
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	@Transient
	public Map<LocalPrivilege, Integer> getLocalPrivileges() {
		return localPrivileges;
	}

	public void setLocalPrivileges(Map<LocalPrivilege, Integer> localPrivileges) {
		this.localPrivileges = localPrivileges;
	}

	@Transient
	public int getLocalPrivilegeValue(String key) {
		return localPrivileges.get(LocalPrivilege.valueOf(key));
	}

	@Transient
	public boolean hasAnyPrivileges() {
		return localPrivileges.get(LocalPrivilege.AccessDraft) > 0
				|| localPrivileges.get(LocalPrivilege.AccessResults) > 0
				|| localPrivileges.get(LocalPrivilege.FormManagement) > 0
				|| localPrivileges.get(LocalPrivilege.ManageInvitations) > 0;
	}

	@Transient
	public String getInfo() {
		String result = "";
		if (user != null) {
			result = user.getName();
		} else if (department != null) {
			result = department;
		}
		result += " - " + getPrivileges();

		return result;
	}

	@Transient
	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

}
