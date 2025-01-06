package com.ec.survey.security;

import com.ec.survey.exception.AccessDeniedException;
import com.ec.survey.exception.ForbiddenURLException;
import com.ec.survey.model.Setting;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.Role;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.AdministrationService;
import com.ec.survey.service.EVoteService;
import com.ec.survey.service.LdapService;
import com.ec.survey.service.SessionService;
import com.ec.survey.service.SettingsService;
import com.ec.survey.service.SurveyService;
import com.ec.survey.tools.Bad2faCredentialsException;
import com.ec.survey.tools.BadSurveyCredentialsException;
import com.ec.survey.tools.EcasHelper;
import com.ec.survey.tools.FrozenCredentialsException;
import com.ec.survey.tools.Tools;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
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
	
	@Resource(name="surveyService")
	private SurveyService surveyService;
	
	@Resource(name="eVoteService")
	private EVoteService eVoteService;
	
	@Resource(name="settingsService")
	private SettingsService settingsService;
	
	private @Value("${ecasvalidationhost}") String ecasvalidationhost;
	private @Value("${server.prefix}") String host;
	private @Value("${ecas.require2fa:#{false}}") boolean require2fa;
	
	public Authentication authenticate(Authentication auth)
	{
		User user = null;
		boolean surveyLoginMode = auth.getName() != null && auth.getName().startsWith("surveyloginmode");
		boolean organisationSet = false;
		
		if (surveyLoginMode || (auth.getName() == null || auth.getName().length() == 0 || auth.getName().startsWith("oldLogin:")) && (((String)auth.getCredentials()).startsWith("ECAS_ST") || ((String)auth.getCredentials()).startsWith("ST")))
		{
			String ticket = (String)auth.getCredentials();
			String validationUrl="";
			
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
				validationUrl = ecasvalidationhost + "/serviceValidate?userDetails=true&ticket=" + ticket + "&service=" + host + service;
			} else {
				validationUrl = ecasvalidationhost + "/laxValidate?userDetails=true&ticket=" + ticket + "&service=" + host + service;
			}
			
			boolean weakAuthentication = false;
    		sessionService.initializeProxy();
    		String xmlValidationAnswer = EcasHelper.getSourceContents(validationUrl);
    		logger.info("authenticate".toUpperCase() +" GET THE SOURCE CONTENT " + xmlValidationAnswer);
    		if (xmlValidationAnswer.contains("<cas:authenticationSuccess>")) {
    			String username = EcasHelper.getXmlTagValue(xmlValidationAnswer, "cas:user");
    			String type = EcasHelper.getXmlTagValue(xmlValidationAnswer, "cas:employeeType");	    				    			
				String strength = EcasHelper.getXmlTagValue(xmlValidationAnswer, "cas:strength");
				String domain = EcasHelper.getXmlTagValue(xmlValidationAnswer, "cas:domain");
				String organisation = domain;
				if (domain.equalsIgnoreCase("eu.europa.ec")) {
					String departmentNumber = EcasHelper.getXmlTagValue(xmlValidationAnswer, "cas:departmentNumber");
					if (departmentNumber.contains(".")) {
						organisation = departmentNumber.substring(0, departmentNumber.indexOf('.'));
					} else {
						organisation = departmentNumber;
					}
				}
				
				String whiteList = settingsService.get(Setting.EULoginWhitelist);
				if (!surveyLoginMode && whiteList.trim().length() > 0) {
					List<String> allowedUsers = Arrays.asList(whiteList.split(";"));
					if (!allowedUsers.contains(username)) {
						throw new AccessDeniedException("Ecas user not in whitelist");
					}
				}

				boolean no2fa = strength.equalsIgnoreCase("PASSWORD") || strength.equalsIgnoreCase("STRONG");

				if (no2fa && require2fa){
					throw new Bad2faCredentialsException();
				}
				
				if (auth.getName() != null && auth.getName().startsWith("oldLogin:"))
				{
					String oldlogin = auth.getName().substring(9);
					if (!oldlogin.equals(username))
					{
						logger.warn("replacing user " + oldlogin + " by user " + username);
					}
				}
				
				try {					
					user = administrationService.getUserForLogin(username, true);
				} catch (Exception e)
		    	{
					//if an ecas user logs in for the first time there is no db entry for him yes
		    	}
				
				List<Role> roles = administrationService.getAllRoles();
				Role ecRole = null;
				Role intRole = null;
				for (Role role : roles) {
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
					organisationSet = true;
					user.setOrganisation(organisation);
					
					if (type.equalsIgnoreCase("f") || type.equalsIgnoreCase("x") || type.equalsIgnoreCase("i") || type.equalsIgnoreCase("c") || type.equalsIgnoreCase("xf") || type.equalsIgnoreCase("q")) 
					{
						user.getRoles().add(ecRole);
					} else {
						if (no2fa){
	    					weakAuthentication = true;
	    					if (!surveyLoginMode){
		    					throw new Bad2faCredentialsException();
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
					
					try {
						organisationSet = !Tools.isEqual(user.getOrganisation(),organisation);
						user.setOrganisation(organisation);
						if (organisationSet) {
							administrationService.updateUser(user);
						}
						
						user.setEmail(EcasHelper.getXmlTagValue(xmlValidationAnswer, "cas:email"));
						user.setGivenName(EcasHelper.getXmlTagValue(xmlValidationAnswer, "cas:firstName"));
						user.setSurName(EcasHelper.getXmlTagValue(xmlValidationAnswer, "cas:lastName"));
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
									
					if (type.equalsIgnoreCase("f") || type.equalsIgnoreCase("x") || type.equalsIgnoreCase("i") || type.equalsIgnoreCase("c") || type.equalsIgnoreCase("xf") || type.equalsIgnoreCase("q")) 
					{
						if (ecRole != null && (user.getRoles().size() != 1 || !Objects.equals(user.getRoles().get(0).getId(), ecRole.getId())))
						{
							user.getRoles().clear();
							user.getRoles().add(ecRole);
							administrationService.updateUser(user);
						}
					} else {
						if (intRole != null)
						{
							if (no2fa){
		    					weakAuthentication = true;
		    					if (!surveyLoginMode){
			    					throw new Bad2faCredentialsException();
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
				
				Collection<GrantedAuthority> authorities = getAuthorities(user, true, weakAuthentication, surveyLoginMode, organisationSet);
				
				checkUserNotBanned(user);
				
				if (surveyLoginMode)
				{
					if (survey.contains("?")) {
						survey = survey.substring(0, survey.indexOf("?"));
					}
					Survey draft = surveyService.getSurvey(survey, true, false, false, false, null, true, false, false, false);
					if (draft.getIsEVote()) {
						String ecmoniker = EcasHelper.getXmlTagValue(xmlValidationAnswer, "cas:user");
						if (!eVoteService.checkVoter(draft.getUniqueId(), ecmoniker)) {
							throw new BadSurveyCredentialsException("VOTERREJECTED");
						}
						authorities.add(new SimpleGrantedAuthority("ROLE_ECUSER_" + ecmoniker));
					}
					
					authorities.add(new SimpleGrantedAuthority("ROLE_ECAS_SURVEY_" + survey));
				}
				
				UsernamePasswordAuthenticationToken t = new UsernamePasswordAuthenticationToken(
					username,
					"", 
					authorities);
				
				t.setDetails(user);
				
				return t;
				
			} else {
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
		
		checkUserNotBanned(user);
		
		user.setBadLoginAttempts(0);
		administrationService.updateUser(user);
			
		return new UsernamePasswordAuthenticationToken(
				auth.getName(), 
				auth.getCredentials(), 
				getAuthorities(user, false, false, surveyLoginMode, organisationSet));
	}
	
	private void checkUserNotBanned(User user)
	{
		if (user.isFrozen()) {
			throw new FrozenCredentialsException("User is banned!");
		}
		
		if (!administrationService.checkEmailsNotBanned(user.getAllEmailAddresses()))
		{
			throw new FrozenCredentialsException("User is banned!");
		}
	}

	/**
	 * Retrieves the correct ROLE type depending on the access level, where access level is an Integer.
	 * Basically, this interprets the access value whether it's for a regular user or admin.
	 * @param user
	 * @param ecas
     * @return
     */
	public Collection<GrantedAuthority> getAuthorities(User user, boolean ecas, boolean weakAuthentication, boolean surveyLogin, boolean organisationSet) {
		List<GrantedAuthority> authList = new ArrayList<>();
		
		if (weakAuthentication)
		{
			authList.add(new SimpleGrantedAuthority("ROLE_WEAK_AUTHENTICATION"));
		}
		
		if (organisationSet)
		{
			authList.add(new SimpleGrantedAuthority("ROLE_ORGANISATION_SET"));
		}
		
		if (ecas) authList.add(new SimpleGrantedAuthority("ROLE_ECAS_USER"));

		if (!surveyLogin) {
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
		}
		
		// Return list of granted authorities
		return authList;
	}

}

