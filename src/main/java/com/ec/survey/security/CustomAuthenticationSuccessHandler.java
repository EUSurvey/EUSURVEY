package com.ec.survey.security;


import java.io.IOException;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.LocaleResolver;

import com.ec.survey.model.Setting;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.service.AdministrationService;
import com.ec.survey.service.LdapService;
import com.ec.survey.service.SettingsService;
import com.ec.survey.tools.EcasHelper;

public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandlerExtended {
	
	@Resource(name="ldapService")
	private LdapService ldapService;
	
	@Resource(name="administrationService")
	private AdministrationService administrationService;	
	
	@Resource(name="settingsService")
	protected SettingsService settingsService;	
	
	@Autowired private LocaleResolver localeResolver;
	
	private @Value("${server.prefix}") String host;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
	
		request.getSession().setAttribute("serverprefix", host);		
			
		SecurityContext securityContext = SecurityContextHolder.getContext();
		
		User user = null;
		try {
			boolean ecas = false;
			boolean weakAuthentication = false;
			for (GrantedAuthority auth: securityContext.getAuthentication().getAuthorities())
			{
				if (auth.getAuthority().equalsIgnoreCase("ROLE_ECAS_USER"))
				{
					ecas = true;
				}
				
				if (auth.getAuthority().equalsIgnoreCase("ROLE_WEAK_AUTHENTICATION"))
				{
					weakAuthentication = true;
				}				
				
				if (auth.getAuthority().startsWith("ROLE_ECAS_SURVEY_"))
				{
					request.getSession().setAttribute("ECASSURVEY", auth.getAuthority().substring(17));
				}
			}
			
			if (ecas)
			{
				try {					
					user = administrationService.getUserForLogin(securityContext.getAuthentication().getName(), true);
				} catch (Exception e)
		    	{
					logger.error(e.getLocalizedMessage(), e);
		    	}
				
				if (user == null)
				{
					if (securityContext.getAuthentication().getDetails() != null && securityContext.getAuthentication().getDetails() instanceof User)
					{
						user = (User)securityContext.getAuthentication().getDetails();
					} else {
						user = new User();
					}
					
					user.setLogin(securityContext.getAuthentication().getName());
					user.setLanguage("EN");
					user.setType(User.ECAS);				
				}
				
				String disabled = settingsService.get(Setting.CreateSurveysForExternalsDisabled);
				if (disabled.equalsIgnoreCase("true") && user.getGlobalPrivileges().get(GlobalPrivilege.ECAccess) == 0)
	    		{
	    			user.setCanCreateSurveys(false);
	    		}
									
				//read data from ldap
				EcasHelper.readData(user, ldapService);				
			} else {
				user = administrationService.getUserForLogin(securityContext.getAuthentication().getName(), ecas);
			}
			
			request.getSession().setAttribute("USER", user);
			request.getSession().setAttribute("WEAKAUTHENTICATION", weakAuthentication);
		    localeResolver.setLocale(request, response, new Locale(user.getLanguage()));		        
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
				
		super.onAuthenticationSuccess(request, response, authentication);
	}

}
