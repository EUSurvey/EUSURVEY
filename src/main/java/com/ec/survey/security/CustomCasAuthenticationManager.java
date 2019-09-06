package com.ec.survey.security;

import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.Role;
import com.ec.survey.model.administration.User;
import com.ec.survey.service.AdministrationService;
import org.apache.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class CustomCasAuthenticationManager implements UserDetailsService {

	protected static Logger logger = Logger.getLogger("com.ec.survey");
	
	@Resource(name="administrationService")
	private AdministrationService administrationService;

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		
		
		User usr= getUserFromDb(userName);
		List<GrantedAuthority> auths = getAuthorities(usr);

		return buildUserForAuthentication(usr, auths);
	}
	
	// Converts EUSurvey User to 
	// org.springframework.security.core.userdetails.User
	private  org.springframework.security.core.userdetails.User buildUserForAuthentication(User user, 
		List<GrantedAuthority> authorities) {
		logger.error("in CustomCasAuthenticationManager buildUserForAuthentication");

		for (GrantedAuthority authority : authorities) {
			logger.error("in MyUserDetailsService buildUserForAuthentication AUTH SHOW " + authority.getAuthority());
		}
		
		org.springframework.security.core.userdetails.User value;
		try {
			logger.error("in MyUserDetailsService buildUserForAuthentication try to create the Spring User");
			value = new org.springframework.security.core.userdetails.User(user.getLogin(),null,true,true,true,true,authorities);	
		} catch (Exception e) {
			
			throw new UsernameNotFoundException("Error when trying to convert EUSuevry user to spring user");
		}
		
			
		return value;
	}

	
	private User getUserFromDb(String userName){
		logger.error("Start getUserFromDb for user coming from ldap authentication " + userName );
		try {
			User user = null;
			try {					
				user = administrationService.getUserForLogin(userName, true);
			} catch (Exception e)
			{
				logger.error("getUserFromDb Error whentrying to get user from Db " + e.getLocalizedMessage() );
			}
			
			List<Role> Roles = administrationService.getAllRoles();
			Role intRole = null;
			for (Role role : Roles) {
				if (role.getName().equalsIgnoreCase("Form Manager")) intRole = role;
			}	
			
			if (user == null)
			{	
				user = new User();
				user.setLogin(userName);
				user.setType(User.ECAS);	
				user.setLanguage("EN");
				
				user.getRoles().add(intRole);
				
				administrationService.createUser(user);
			}

			return user;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}				
		
	}
	
	
	private List<GrantedAuthority> getAuthorities(User user ) {
		List<GrantedAuthority> authList = new ArrayList<>();

		authList.add(new SimpleGrantedAuthority("ROLE_USER"));
		

		if (user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2)
		{
			authList.add(new SimpleGrantedAuthority("ROLE_FORM_ADMIN"));
		}
		
		if (user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) >= 1)
		{
			authList.add(new SimpleGrantedAuthority("ROLE_FORM_MANAGER"));
		}
		
		if (user.getGlobalPrivileges().get(GlobalPrivilege.RightManagement) == 2)
		{
			authList.add(new SimpleGrantedAuthority("ROLE_RIGHT_ADMIN"));
		}
		
		if (user.getGlobalPrivileges().get(GlobalPrivilege.RightManagement) >= 1)
		{
			authList.add(new SimpleGrantedAuthority("ROLE_RIGHT_MANAGER"));
		}
		
		if (user.getGlobalPrivileges().get(GlobalPrivilege.UserManagement) == 2)
		{
			authList.add(new SimpleGrantedAuthority("ROLE_USER_ADMIN"));
		}
		
		if (user.getGlobalPrivileges().get(GlobalPrivilege.UserManagement) >= 1)
		{
			authList.add(new SimpleGrantedAuthority("ROLE_USER_MANAGER"));
		}
		
		if (user.getGlobalPrivileges().get(GlobalPrivilege.SystemManagement) == 2)
		{
			authList.add(new SimpleGrantedAuthority("ROLE_SYSTEM_ADMIN"));
		}
		
		if (user.getGlobalPrivileges().get(GlobalPrivilege.SystemManagement) >= 1)
		{
			authList.add(new SimpleGrantedAuthority("ROLE_SYSTEM_MANAGER"));
		}

		if (user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) == 2)
		{
			authList.add(new SimpleGrantedAuthority("ROLE_CONTACT_ADMIN"));
		}
		
		if (user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) >= 1)
		{
			authList.add(new SimpleGrantedAuthority("ROLE_CONTACT_MANAGER"));
		}
		
		if (user.getGlobalPrivileges().get(GlobalPrivilege.ECAccess) >= 1)
		{
			authList.add(new SimpleGrantedAuthority("ROLE_EC_ACCESS"));
		}
		
		// Return list of granted authorities
		return authList;
	}


}
