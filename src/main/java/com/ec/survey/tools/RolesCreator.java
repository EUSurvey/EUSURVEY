package com.ec.survey.tools;

import com.ec.survey.model.administration.Role;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.service.AdministrationService;

public class RolesCreator {

	public static void createBasicRoles(AdministrationService administrationService, boolean showecas) {		
		
		//create roles
		Role role = new Role();
		role.setName("Administrator");
		role.getGlobalPrivileges().put(GlobalPrivilege.RightManagement, 2);
		role.getGlobalPrivileges().put(GlobalPrivilege.UserManagement, 2);
		role.getGlobalPrivileges().put(GlobalPrivilege.FormManagement, 2);
		role.getGlobalPrivileges().put(GlobalPrivilege.ContactManagement, 2);
		role.getGlobalPrivileges().put(GlobalPrivilege.SystemManagement, 2);
		if (showecas)
		{
			role.getGlobalPrivileges().put(GlobalPrivilege.ECAccess, 1);
		}
		administrationService.createRole(role);	
		
		role = new Role();
		role.setName("Form Manager");
		role.getGlobalPrivileges().put(GlobalPrivilege.RightManagement, 0);
		role.getGlobalPrivileges().put(GlobalPrivilege.UserManagement, 1);
		role.getGlobalPrivileges().put(GlobalPrivilege.FormManagement, 1);
		role.getGlobalPrivileges().put(GlobalPrivilege.ContactManagement, 1);
		role.getGlobalPrivileges().put(GlobalPrivilege.ECAccess, 0);
		administrationService.createRole(role);
		
		if (showecas)
		{
			role = new Role();
			role.setName("Form Manager (EC)");
			role.getGlobalPrivileges().put(GlobalPrivilege.RightManagement, 0);
			role.getGlobalPrivileges().put(GlobalPrivilege.UserManagement, 1);
			role.getGlobalPrivileges().put(GlobalPrivilege.FormManagement, 1);
			role.getGlobalPrivileges().put(GlobalPrivilege.ContactManagement, 1);
			role.getGlobalPrivileges().put(GlobalPrivilege.ECAccess, 1);
			administrationService.createRole(role);
		}
				
	}		

}
