package com.ec.survey.security;

import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.Role;
import com.ec.survey.model.administration.User;
import com.ec.survey.service.AdministrationService;
import com.ec.survey.service.LdapService;
import com.ec.survey.service.SessionService;
import com.ec.survey.tools.Bad2faCredentialsException;
import com.ec.survey.tools.BadSurveyCredentialsException;
import com.ec.survey.tools.EcasHelper;
import com.ec.survey.tools.Tools;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class CustomAuthenticationManager implements AuthenticationManager {

	protected static Logger logger = Logger.getLogger("com.ec.survey");

	@Resource(name="administrationService")
	private AdministrationService administrationService;
	
	@Resource(name="ldapService")
	private LdapService ldapService;
	
	@Resource(name="sessionService")
	private SessionService sessionService;
	
	private @Value("${ecasvalidationhost}") String ecasvalidationhost;
	private @Value("${server.prefix}") String host;
	
	public Authentication authenticate(Authentication auth)
			throws AuthenticationException {

		User user = null;
		logger.debug("CustomAuthenticationManager start authenticate name " + auth.getName() );
		boolean surveyLoginMode = auth.getName() != null && auth.getName().startsWith("surveyloginmode");
		
		if (surveyLoginMode || (auth.getName() == null || auth.getName().length() == 0 || auth.getName().startsWith("oldLogin:")) && (((String)auth.getCredentials()).startsWith("ECAS_ST") || ((String)auth.getCredentials()).startsWith("ST")))
		{
			String ticket = (String)auth.getCredentials();
			String ValidationURL="";
			
			String service = "auth/ecaslogin";
			String survey = "";
			if (surveyLoginMode)
			{
				survey = auth.getName().replace("surveyloginmode", "");
				service = "auth/surveylogin?survey=" + survey;
			}
					
			// check if we are on open Cas solution then use the validateservice  url to avoid exception
			// other (ecas) we have to use the laxValidate to allow login with external user JIRA ESURVEY-2759
			if(ldapService.isCasOss()){
				ValidationURL = ecasvalidationhost + "/serviceValidate?userDetails=true&ticket=" + ticket + "&service=" + host + service;
			}else{
				ValidationURL = ecasvalidationhost + "/laxValidate?userDetails=true&ticket=" + ticket + "&service=" + host + service;
			}
			
			logger.debug("authenticate".toUpperCase() +" GET THE TICKET TO CHECK VALUE " + ValidationURL +" THE TICKET IS " + ticket);
			
			boolean weakAuthentication = false;
    		sessionService.initializeProxy();
    		logger.debug("authenticate".toUpperCase() +" PROXY INITIALZED");
    		String xmlValidationAnswer = EcasHelper.getSourceContents(ValidationURL);
    		logger.info("authenticate".toUpperCase() +" GET THE SOURCE CONTENT " + xmlValidationAnswer);
    		if (xmlValidationAnswer.contains("<cas:authenticationSuccess>")) {
    			String username = EcasHelper.getXmlTagValue(xmlValidationAnswer, "cas:user");
    			String type = EcasHelper.getXmlTagValue(xmlValidationAnswer, "cas:employeeType");	    				    			
				String strength = EcasHelper.getXmlTagValue(xmlValidationAnswer, "cas:strength");    				
				
				if (auth.getName() != null && auth.getName().startsWith("oldLogin:"))
				{
					String oldlogin = auth.getName().substring(9);
					if (!oldlogin.equals(username))
					{
						logger.warn("replacing user " + oldlogin + " by user " + username);
					}
				}
				
				try {					
					logger.debug("authenticate".toUpperCase() +" START TO GET USER INFORMATION FROM DB FOR USERNAME " + username);
					user = administrationService.getUserForLogin(username, true);
				} catch (Exception e)
		    	{
					//if an ecas user logs in for the first time there is no db entry for him yes
		    	}
				
				logger.debug("authenticate".toUpperCase() +" Get All Roles From AdminService");
				List<Role> Roles = administrationService.getAllRoles();
				Role ecRole = null;
				Role intRole = null;
				for (Role role : Roles) {
					if (role.getName().equalsIgnoreCase("Form Manager (EC)")) ecRole = role;
					if (role.getName().equalsIgnoreCase("Form Manager")) intRole = role;
				}	
				
				if (user == null)
				{	
					user = new User();
					user.setLogin(username);
					user.setType(User.ECAS);	
					user.setLanguage("EN");
					user.setEmail(EcasHelper.getXmlTagValue(xmlValidationAnswer, "cas:email"));
					user.setGivenName(EcasHelper.getXmlTagValue(xmlValidationAnswer, "cas:firstName"));
					user.setSurName(EcasHelper.getXmlTagValue(xmlValidationAnswer, "cas:lastName"));
					
					if (type.equalsIgnoreCase("f") || type.equalsIgnoreCase("x") || type.equalsIgnoreCase("i") || type.equalsIgnoreCase("c")) 
					{
						user.getRoles().add(ecRole);
					} else {
						if (strength.equalsIgnoreCase("PASSWORD") || strength.equalsIgnoreCase("STRONG"))
	    				{
	    					weakAuthentication = true;
	    					if (!surveyLoginMode)
	    					{
		    					throw new Bad2faCredentialsException("Ecas user does not use two factor authentication!");
		    				}
						}						
						
						user.getRoles().add(intRole);							
					}
					
					try {
						administrationService.createUser(user);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						throw new BadCredentialsException("Ecas user cannot be created!");
					}
				} else {
					String oldEmail = user.getEmail();
					
					user.setEmail(EcasHelper.getXmlTagValue(xmlValidationAnswer, "cas:email"));
					user.setGivenName(EcasHelper.getXmlTagValue(xmlValidationAnswer, "cas:firstName"));
					user.setSurName(EcasHelper.getXmlTagValue(xmlValidationAnswer, "cas:lastName"));
									
					if (type.equalsIgnoreCase("f") || type.equalsIgnoreCase("x") || type.equalsIgnoreCase("i") || type.equalsIgnoreCase("c")) 
					{
						if (ecRole != null)
						{
							if (user.getRoles().size() != 1 || !Objects.equals(user.getRoles().get(0).getId(), ecRole.getId()))
							{
								user.getRoles().clear();
								user.getRoles().add(ecRole);
								administrationService.updateUser(user);
							}
						}
					} else {
						if (intRole != null)
						{
							if (strength.equalsIgnoreCase("PASSWORD") || strength.equalsIgnoreCase("STRONG"))
		    				{
		    					weakAuthentication = true;
		    					if (!surveyLoginMode)
		    					{
			    					throw new Bad2faCredentialsException("Ecas user does not use two factor authentication!");
			    				}
							}
							
							if (user.getRoles().size() != 1 || !Objects.equals(user.getRoles().get(0).getId(), intRole.getId()))
							{
								user.getRoles().clear();
								user.getRoles().add(intRole);
								administrationService.updateUser(user);
							}
						}
					}
					
					if (!oldEmail.equalsIgnoreCase(user.getEmail()))
					{
						if (user.getOtherEmail() == null)
						{
							user.setOtherEmail(oldEmail);
						} else {
							if (!user.getOtherEmail().endsWith(";"))
							{
								user.setOtherEmail(user.getOtherEmail() + ";" + oldEmail);
							} else {
								user.setOtherEmail(user.getOtherEmail() + oldEmail);
							}
						}
						administrationService.updateUser(user);
					}
				}
				
				Collection<GrantedAuthority> authorities = getAuthorities(user, true, weakAuthentication);
				
				if (surveyLoginMode)
				{
					authorities.add(new SimpleGrantedAuthority("ROLE_ECAS_SURVEY_" + survey));
				}
				
				return new UsernamePasswordAuthenticationToken(
						username, 
						"", 
						authorities);
				
				} else{
					logger.error("cas:authenticationSuccess NOT FOUND IN XMLVALIDATION");
				} 
	    	
	    	
	    	logger.error("Ecas user cannot be validated!");
	    	
	    	if (surveyLoginMode)
	    	{
	    		throw new BadSurveyCredentialsException("Ecas user cannot be validated!");
	    	}
	    	
			throw new BadCredentialsException("Ecas user cannot be validated!");
		}

		try {
			// Retrieve user details from database
			user = administrationService.getUserForLogin(auth.getName(), false);
		} catch (Exception e) {
			throw new BadCredentialsException("User does not exists!");
		}

		// Compare passwords
		// Make sure to encode the password first before comparing
		//first check if old password hash (MD5)		
		if (administrationService.checkUserPassword(user, (String) auth.getCredentials())) {
			//replaced md5 hash by salted SHA-512 hash			
		} else	if (!Tools.isPasswordValid(user.getPassword(), auth.getCredentials() + user.getPasswordSalt())) {
			
			if (user.getBadLoginAttempts() >= 2)
			{
				throw new LockedException("More than two bad login attempts");
			} else {
				user.setBadLoginAttempts(user.getBadLoginAttempts()+1);
				administrationService.updateUser(user);
			}
			throw new BadCredentialsException("Wrong password!");
		}
		
		if (!user.getValidated())
		{
			logger.error("User not validated!");
			throw new BadCredentialsException("User not validated!");
		}
		
		user.setBadLoginAttempts(0);
		administrationService.updateUser(user);
			
		return new UsernamePasswordAuthenticationToken(
				auth.getName(), 
				auth.getCredentials(), 
				getAuthorities(user, false, false));
	}

	/**
	 * Retrieves the correct ROLE type depending on the access level, where access level is an Integer.
	 * Basically, this interprets the access value whether it's for a regular user or admin.
	 * @param user
	 * @param ecas
     * @return
     */
	public Collection<GrantedAuthority> getAuthorities(User user, boolean ecas, boolean weakAuthentication) {
		List<GrantedAuthority> authList = new ArrayList<>();
		
		if (weakAuthentication)
		{
			authList.add(new SimpleGrantedAuthority("ROLE_WEAK_AUTHENTICATION"));
		}

		authList.add(new SimpleGrantedAuthority("ROLE_USER"));
		
		if (ecas) authList.add(new SimpleGrantedAuthority("ROLE_ECAS_USER"));
			
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

