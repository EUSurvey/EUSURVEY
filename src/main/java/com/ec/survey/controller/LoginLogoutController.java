package com.ec.survey.controller;

import com.ec.survey.model.OneTimePasswordResetCode;
import com.ec.survey.model.administration.User;
import com.ec.survey.security.CustomAuthenticationManager;
import com.ec.survey.security.CustomAuthenticationSuccessHandler;
import com.ec.survey.service.AdministrationService;
import com.ec.survey.service.MailService;
import com.ec.survey.service.SessionService;
import com.ec.survey.tools.NotAgreedToTosException;
import com.ec.survey.tools.Tools;
import com.ec.survey.tools.WeakAuthenticationException;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Handles and retrieves the login or denied page depending on the URI template
 */
@Controller
@EnableWebSecurity
public class LoginLogoutController extends BasicController {

	@Resource(name="administrationService")
	private AdministrationService administrationService;
	
	@Resource(name="sessionService")
	private SessionService sessionService;
	
	@Resource(name="mailService")
	private MailService mailService;
		
	@Autowired
	@Qualifier("customAuthenticationManager")
	protected CustomAuthenticationManager customAuthenticationManager;
	
	@Autowired
	@Qualifier("customAuthenticationSuccessHandler")
	protected CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
	
	private @Value("${smtpserver}") String smtpServer;
	private @Value("${smtp.port}") String smtpPort;
	private @Value("${server.prefix}") String host;
	private @Value("${ecashost}") String ecashost;
	
	@RequestMapping(value = "/auth/login/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String getLoginPageRunnerMode(@RequestParam(value="error", required=false) boolean error, HttpServletRequest request, ModelMap model, Locale locale) {
		return "redirect:/auth/login";
	}
	
	@RequestMapping(value = "/auth/login", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String getLoginPage(@RequestParam(value="error", required=false) boolean error, HttpServletRequest request, ModelMap model, Locale locale) throws NotAgreedToTosException, WeakAuthenticationException {
		if (isShowEcas()) model.put("showecas", true);
		if (isCasOss()) model.put("casoss", true);
		
		//if user is already logged in, redirect to the welcome page instead
		User user = sessionService.getCurrentUser(request);
		
		if (user != null) return "redirect:/dashboard";

		if (error) {
			// Assign an error message
			model.put("error", resources.getMessage("error.CredentialsInvalid", null, "You have entered an invalid login or password!", locale));		
		}
		
		model.put("ecasurl", ecashost);
		model.put("serviceurl", host + "auth/ecaslogin");

		if (request.getSession().getAttribute("ticket") != null) model.put("ticket", request.getSession().getAttribute("ticket"));
		
		request.getSession().removeAttribute("ticket");
		
		if (request.getParameter("passwordsaved") != null)
		{
			model.put("info", resources.getMessage("message.PasswordSaved", null, "Your password has been saved! You can log in now.", locale));
		}
		
		return "auth/login";
	}
	
	@RequestMapping(value = "/auth/login", method = {RequestMethod.POST})
	public String postLoginPage(ModelMap model, HttpServletRequest request, Locale locale) {
		String target = request.getParameter("target");
		
		if (target != null && target.equals("forgotPassword"))
		{
			return forgotPassword(request.getParameter("email"), request.getParameter("login"), model, locale, request);
		}	
		return "auth/login";
	}
	
	@RequestMapping(value = "/auth/ecaslogin", method = {RequestMethod.GET, RequestMethod.HEAD})
	public void ecaslogin(@RequestParam(value="error", required=false) boolean error, HttpServletRequest request, HttpServletResponse response, ModelMap model) throws ServletException, IOException {
		if (isShowEcas()) model.put("showecas", true);
		if (isCasOss()) model.put("casoss", true);
		String ticket = request.getParameter("ticket");
		
		String oldUser = "";
		if (request.getSession().getAttribute("USER") != null)
		{
			User user = (com.ec.survey.model.administration.User)request.getSession().getAttribute("USER");
			oldUser = "oldLogin:" + user.getLogin();
		}
		
		logger.debug("LoginLogoutController".toUpperCase() + " ticket authentification is "+ticket);
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(oldUser, ticket);
		Authentication authenticatedUser = customAuthenticationManager.authenticate(token);
		SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
		
		customAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authenticatedUser);
	}
	
	@RequestMapping(value = "/auth/surveylogin", method = {RequestMethod.GET, RequestMethod.HEAD})
	public void surveylogin(@RequestParam(value="error", required=false) boolean error, HttpServletRequest request, HttpServletResponse response, ModelMap model) throws ServletException, IOException {
		if (isShowEcas()) model.put("showecas", true);
		if (isCasOss()) model.put("casoss", true);
		String ticket = request.getParameter("ticket");
		String shortname = request.getParameter("survey");
		String draftid = request.getParameter("draftid");
		String surveylanguage = request.getParameter("surveylanguage");
		
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("surveyloginmode" + shortname, ticket + "&draftid=" + draftid + "&surveylanguage=" + surveylanguage);
		Authentication authenticatedUser = customAuthenticationManager.authenticate(token);
		SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
		
		customAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authenticatedUser);
	}
	
	@RequestMapping(value = "/auth/logout", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView getLogoutPage(HttpServletRequest request) throws WeakAuthenticationException {
		
		User user = null;
		try {
			user = sessionService.getCurrentUser(request);
		} catch (NotAgreedToTosException e) {
			//ignore
		} catch (WeakAuthenticationException e) {
			//ignore
		}
		
		request.getSession().invalidate();
		
		if (user != null && user.getType().equalsIgnoreCase(User.ECAS))
		{
			return new ModelAndView("redirect:/home/welcome?ecaslogout");	
		}
		
		return new ModelAndView("redirect:/home/welcome");
	}

	@RequestMapping(value = "/auth/denied")
	public String getDeniedPage() {
		return "auth/deniedpage";
	}
	
	@RequestMapping(value = "/auth/tos")
	public ModelAndView getToSPage(HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("USER");
		if (super.isShowPrivacy()) {
			Map<String, Object> model = new HashMap<String,Object>();
			model.put("user", user);
			model.put("oss", super.isOss());
			return new ModelAndView("auth/tos", model);
		} else {
			user.setAgreedToToS(true);
			administrationService.updateUser(user);
			sessionService.setCurrentUser(request, user);
			return new ModelAndView("redirect:/dashboard");
		}
	}
	
	@RequestMapping(value = "/auth/tos", method = RequestMethod.POST)
	public String getToSPagePost(HttpServletRequest request, HttpServletResponse response) {
		
		User user = (User) request.getSession().getAttribute("USER");
		
		String userid = request.getParameter("user");
		String accepted = request.getParameter("accepted");

		if (userid != null && userid.equals(user.getId().toString()) && accepted != null && accepted.equalsIgnoreCase("true"))
		{
			user.setAgreedToToS(true);
			user.setAgreedToToSDate(new Date());
			user.setAgreedToToSVersion(Tools.getCurrentToSVersion());
			administrationService.updateUser(user);
			sessionService.setCurrentUser(request, user);
			
			RequestCache requestCache = new HttpSessionRequestCache();
			SavedRequest savedRequest = requestCache.getRequest(request, response);
			String targetUrl = "/dashboard";
			if (savedRequest != null) {
				 targetUrl = savedRequest.getRedirectUrl();
			}
			return "redirect:" + targetUrl;
		}
		
		return "redirect:/auth/tos";
	}
	
	@RequestMapping(value = "/auth/deleted")
	public ModelAndView deleted(HttpServletRequest request) {
		request.getSession().invalidate();
		return new ModelAndView("auth/deleted");
	}
	
	public String forgotPassword(String email, String login, ModelMap model, Locale locale, HttpServletRequest request) {
		if (isShowEcas()) model.put("showecas", true);
		if (isCasOss()) model.put("casoss", true);
		String errorMessage = resources.getMessage("error.LoginEmailInvalid", null, "You did not provide a valid login and a valid email address!", locale);

		if (email != null && email.length() > 0 && login != null && login.length() > 0) {
						
			try {
				//check if the email address belongs to a user
				// if it's for oss release then check if the OCAS is used if such the case then search for user of type ECAS
				boolean searchForEcasUser =(super.isOss() && isCasOss());
				logger.debug("forgotPassword search for user ECAS " + searchForEcasUser);
				User user = administrationService.getUserForLogin(login, searchForEcasUser);
				
				if (!user.getEmail().equalsIgnoreCase(email))
				{
					model.put("error", errorMessage);
					model.put("mode", "forgotPassword");
					return "auth/login";
				}
				
				//check captcha
				if (!checkCaptcha(request)){
		        	model.put("captchaerror", resources.getMessage("message.captchawrongnew", null, "The CAPTCHA code is not correct!", locale));
		        	model.put("mode", "forgotPassword");
					return "auth/login";
		        }
								
				//generate a new code
				OneTimePasswordResetCode code = administrationService.createOneTimePasswordResetCode(user);
				
				//send the code by email
				String body = resources.getMessage("mail.Dear", null, "Dear", locale) + " " + user.getLogin() + "<br />" + resources.getMessage("mail.PasswordRequest", null, "A password reset request has been made for this email address. Please click the link below to change your password. The link will be valid for 24 hours.", locale) + "<br /><br />" ;
				body += "<a href='" + host + "auth/reset/" + code.getCode() + "'>" + host + "auth/reset/" + code.getCode() + "</a>";
				
				InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
				String text = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", body).replace("[HOST]", host);		
								
				mailService.SendHtmlMail(code.getEmail(), sender, sender, resources.getMessage("mail.PasswordResetRequest", null, "Password Reset Request", locale), text, smtpServer, Integer.parseInt(smtpPort), null);
			} catch (Exception e) {
				model.put("error", errorMessage);
				return "auth/login";
			}
			
			model.put("info", resources.getMessage("message.EmailResetSend", null, "An email containing a link to reset your password has been sent to you.", locale));
			return "auth/login";
		}
		
		model.put("error", errorMessage);
		return "auth/login";
	}
	
	@RequestMapping(value = "/auth/reset/{code}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String resetPassword(@PathVariable String code, ModelMap model, Locale locale) {
		logger.debug("Received request for sending forgot password mail");
		if (isShowEcas()) model.put("showecas", true);
		if (isCasOss()) model.put("casoss", true);
		if (code == null || code.length() == 0)
		{
			model.put("error", resources.getMessage("error.ResetCodeInvalid", null, "You did not provide a valid password reset code!", locale));
			return "auth/login";
		}
		
		try {
			OneTimePasswordResetCode codeItem = administrationService.getOneTimePasswordResetCode(code);
			
			if (!checkValid(codeItem))
			{
				model.put("error", resources.getMessage("error.ResetCodeOutdated", null, "This password reset code is not valid anymore! Please request a new one.", locale));
				return "auth/login";
			}
			
			model.put("code", codeItem.getCode());
			return "auth/reset";
			
		} catch (Exception e) {
			model.put("error", resources.getMessage("error.ResetCodeInvalid", null, "You did not provide a valid password reset code!", locale));
			logger.error(e.getLocalizedMessage(), e);
		}
		
		return "redirect:/auth/login";
	}
	
	private boolean checkValid(OneTimePasswordResetCode codeItem)
	{
		Calendar cal = Calendar.getInstance();  
		cal.add(Calendar.DAY_OF_MONTH, -1);  
		
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(codeItem.getCreated());
		
		return !cal2.before(cal);
	}
	
	@RequestMapping(value = "/auth/resetPost", method = RequestMethod.POST)
	public String resetPost (@RequestParam(value="password", required=true) String password, @RequestParam(value="password2", required=true) String password2, @RequestParam(value="code", required=true) String code, ModelMap model, Locale locale) {
		
		model.put("code", code);
		if (isShowEcas()) model.put("showecas", true);
		if (isCasOss()) model.put("casoss", true);
		if (password == null || password.length() == 0 || password2 == null)
		{
			model.put("error", resources.getMessage("error.PasswordInvalid", null, "You did not provide a valid password!", locale));	
			return "auth/reset";
		}
		
		if (!password.equals(password2))
		{
			model.put("error", resources.getMessage("error.PasswordsDontMatch", null, "The two passwords do not match!", locale));	
			return "auth/reset";
		}
		
		try {
			OneTimePasswordResetCode codeItem = administrationService.getOneTimePasswordResetCode(code);
			
			if (!checkValid(codeItem))
			{
				model.put("error", resources.getMessage("error.ResetCodeOutdated", null, "This password reset code is not valid anymore! Please request a new one.", locale));
				return "auth/reset";
			}
			
			if (Tools.isPasswordWeak(password))
			{
				model.put("error", resources.getMessage("error.PasswordWeak", null, "This password does not fit our password policy. Please choose a password between 8 and 16 characters with at least one digit and one non-alphanumeric characters (e.g. !?$&%...).", locale));
				return "auth/reset";
			}
			
			User user = administrationService.getUser(codeItem.getUserId());
			user.setPasswordSalt(Tools.newSalt());
			user.setPassword(Tools.hash(password + user.getPasswordSalt()));
			
			administrationService.updateUser(user);
			return "redirect:/auth/login?passwordsaved";

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			model.put("error", resources.getMessage("error.ResetNotPossible", null, "Reset not possible!", locale));
			return "auth/reset";
		}
		
	}

}