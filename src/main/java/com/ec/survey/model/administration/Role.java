package com.ec.survey.model.administration;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

@Entity
@Table(name = "GLOBALROLES")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Role implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;

	private Integer id;	
	private String name;	
	private Map<GlobalPrivilege, Integer> globalPrivileges;
	
	public Role()
	{
		globalPrivileges = new HashMap<>();
		globalPrivileges.put(GlobalPrivilege.UserManagement, 0);
		globalPrivileges.put(GlobalPrivilege.FormManagement, 0);
		globalPrivileges.put(GlobalPrivilege.ContactManagement, 0);
		globalPrivileges.put(GlobalPrivilege.RightManagement, 0);	
		globalPrivileges.put(GlobalPrivilege.ECAccess, 0);	
		globalPrivileges.put(GlobalPrivilege.SystemManagement, 0);	
	}

	@Id
	@Column(name = "ROLE_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "ROLE_NAME")
	public String getName() {
		return name;
	}	
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "ROLE_PRIVILEGES")
	public String getPrivileges() {
		StringBuilder result = new StringBuilder();
		
		TreeSet<GlobalPrivilege> privileges = new TreeSet<>(globalPrivileges.keySet());
		
		for (GlobalPrivilege privilege : privileges) {
			result.append(privilege.toString()).append(":").append(globalPrivileges.get(privilege)).append(";");
		}
		return result.toString();
	}
	public void setPrivileges(String privilegesString) {
		globalPrivileges = new HashMap<>();
		String[] privileges = privilegesString.split(";");
		for (String privilegeString : privileges) {
			if (privilegeString.length() > 0)
			{
				String[] privilege = privilegeString.split(":");
				globalPrivileges.put(GlobalPrivilege.valueOf(privilege[0]), Integer.parseInt(privilege[1]));
			}
		}
	}	
	
	@Transient
	public Map<GlobalPrivilege, Integer> getGlobalPrivileges() {
		return globalPrivileges;
	}
	public void setGlobalPrivileges(Map<GlobalPrivilege, Integer> globalPrivileges) {
		this.globalPrivileges = globalPrivileges;
	}
	
	@Transient
	public int getPrivilegeValue(String key)
	{
		GlobalPrivilege priv = GlobalPrivilege.valueOf(key);
		if (globalPrivileges.containsKey(priv))
		{
			return globalPrivileges.get(priv);
		}
		return 0;
	}

}
