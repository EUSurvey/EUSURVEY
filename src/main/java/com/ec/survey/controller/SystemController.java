package com.ec.survey.controller;

import com.ec.survey.model.Activity;
import com.ec.survey.model.Message;
import com.ec.survey.model.Setting;
import com.ec.survey.model.administration.ComplexityParameters;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.service.MailService;
import com.ec.survey.service.SessionService;
import com.ec.survey.service.SettingsService;
import com.ec.survey.service.SystemService;
import com.ec.survey.tools.NotAgreedToTosException;
import com.ec.survey.tools.Tools;
import com.ec.survey.tools.WeakAuthenticationException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

@Controller
@RequestMapping("/administration/system")
public class SystemController extends BasicController {
	
	@Resource(name="systemService")
	private SystemService systemService;	
	
	@Resource(name="sessionService")
	private SessionService sessionService;	
	
	@Resource(name="settingsService")
	private SettingsService settingsService;	
	
	@RequestMapping(value = "/message", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody Message getSystemMessage(HttpServletRequest request) throws NotAgreedToTosException, WeakAuthenticationException {
		User user = sessionService.getCurrentUser(request); 
		Message message;
		
		if (user != null && user.getGlobalPrivileges().get(GlobalPrivilege.SystemManagement) > 0)
		{
			message = systemService.getAdminMessage();
			if (message != null) return message;
		}
		
		message = systemService.getMessage();
		
		boolean runnermode = request.getParameter("runnermode") != null && request.getParameter("runnermode").equalsIgnoreCase("true");
		
		if (message.isActive() && message.getText().length() > 0)
		{
			if (message.getType() == 0)
			{
				//form managers only
				if (user == null) return null;
			} else if (message.getType() == 1)
			{
				//participants only
				if (!runnermode) return null;
			} else if (message.getType() == 2)
			{
				//participants and form managers
				if (!runnermode && user == null) return null;
			}
			
			request.getSession().setAttribute("lastmessageversion", String.valueOf(message.getVersion()));
			
			return message;
		}
		
		return user == null ? null : systemService.getUserMessage(user.getId());
	}
	
	@RequestMapping(value = "/deletemessage", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody String deleteMessage(HttpServletRequest request) throws NotAgreedToTosException, WeakAuthenticationException
	{
		User user = sessionService.getCurrentUser(request); 
		String sid = request.getParameter("id");
		
		if (sid != null)
		{
			int id = Integer.parseInt(sid);
			systemService.deleteMessage(id, user.getId(), user.getGlobalPrivileges().get(GlobalPrivilege.SystemManagement) > 0);			
		}
		
		return "OK";
	}
	
	@RequestMapping(value = "/messages/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView getSystemMessagesRunner(HttpServletRequest request) throws NotAgreedToTosException, WeakAuthenticationException {
		return getSystemMessages(request, true);
	}
	
	@RequestMapping(value = "/messages", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView getSystemMessages(HttpServletRequest request) throws NotAgreedToTosException, WeakAuthenticationException {
		return getSystemMessages(request, false);
	}
	
	private ModelAndView getSystemMessages(HttpServletRequest request, boolean runnermode) throws NotAgreedToTosException, WeakAuthenticationException {
		Message message = systemService.getMessage();
		
		User user = sessionService.getCurrentUser(request);
		
		if (!message.isActive()) {
			message = new Message();
		} else if (message.getType() == 0)
		{
			//form managers only
			if (user == null) message = new Message();
		} else if (message.getType() == 1)
		{
			//participants only
			if (!runnermode) message = new Message();
		}		
		
		ModelAndView m =  new ModelAndView("administration/messages", "message", message);
		
		if (runnermode)
		{
			m.addObject("runnermode", true);
		}
		
    	return m;
	}
	
	@RequestMapping
	public ModelAndView system(HttpServletRequest request, Model model) {	
    	Message message = systemService.getMessage();
		ModelAndView m =  new ModelAndView("administration/system", "message", message);
		String loggingenabled = settingsService.get(Setting.ActivityLoggingEnabled);
		m.addObject("logging", loggingenabled);
		m.addObject("activity", new Activity());
		m.addObject("allActivityIds", Setting.ActivityLoggingIds());
		m.addObject("enabledActivityIds", settingsService.getEnabledActivityLoggingIds());
		
		Map<String, String> complexityParameterList = new LinkedHashMap<>();
		for(ComplexityParameters cp : ComplexityParameters.values())
		{
			complexityParameterList.put(cp.getKey(), settingsService.get(cp.getKey()));
		}
		m.addObject("complexityParameters", complexityParameterList);

		m.addObject("reportMaxNumber", settingsService.get(Setting.MaxReports));
		m.addObject("reportMessageText", settingsService.get(Setting.ReportText));
		m.addObject("reportRecipients", settingsService.get(Setting.ReportRecipients));
		
		return m;
	}
	
	@RequestMapping(value ="/configureReports", method = RequestMethod.POST)
	public ModelAndView configureReports( HttpServletRequest request, Locale locale) throws Exception {	
		String number = request.getParameter("maxNumber");
		
		if (number == null || !Tools.isInteger(number))
		{
			throw new Exception("Invalid number");
		}
		
		String text = request.getParameter("messageText"); 
		
		if (text == null || text.length() == 0)
		{
			throw new Exception("text must not be empty");
		}
		
		String[] emails = request.getParameterValues("messageEmail");
		String recipients = "";
		if (emails != null)
		{
			for (String email : emails) {
				if (email.trim().length() > 0)
				{
					if (!MailService.isValidEmailAddress(email))
					{
						throw new Exception("invalid email address:" + email);
					}					
					
					if (recipients.length() > 0)
					{
						recipients += ";";
					}
					recipients += email;
				}
			}
		}
		
		settingsService.update(Setting.MaxReports, number);
		settingsService.update(Setting.ReportText, text);
		settingsService.update(Setting.ReportRecipients, recipients);
		
		return new ModelAndView("redirect:/administration/system");
	}
	
	@RequestMapping(value ="/configureLogging", method = RequestMethod.POST)
	public ModelAndView configureLogging(@RequestParam("enabled") String enabled, HttpServletRequest request, Locale locale) {	
		settingsService.update(Setting.ActivityLoggingEnabled, enabled);
		for (int i : Setting.ActivityLoggingIds())
		{
			String key = "activity" + i;
			String param = request.getParameter(key);
			if (param != null && param.equalsIgnoreCase(Integer.toString(i)))
			{
				settingsService.update(i + "ActivityEnabled", "true");
			} else {
				settingsService.update(i + "ActivityEnabled", "false");
			}
			
		}
		return new ModelAndView("redirect:/administration/system");
	}
	
	@RequestMapping(value ="/disableMessage", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView disableMessage(HttpServletRequest request, Model model) {	
    	Message message = systemService.getMessage();
    	message.setActive(false);
    	systemService.save(message);
    	return new ModelAndView("redirect:/administration/system");
	}	
	
	@RequestMapping(value ="/configureMessage", method = RequestMethod.POST)
	public ModelAndView configureMessage(@RequestParam("active") String active, @RequestParam("autodeactivate") String autodeactivate, @RequestParam("autodeactivatetime") String autodeactivatetime, @RequestParam("time") String time, @RequestParam("type") String type, @RequestParam("text") String text, @RequestParam("criticality") String criticality, HttpServletRequest request, Model model, Locale locale) {	
    	Message message = systemService.getMessage();
		
		try {
			message.setCriticality(Integer.parseInt(criticality));
			message.setText(text);
			message.setTime(Integer.parseInt(time));
			message.setType(Integer.parseInt(type));
			message.setActive(Boolean.valueOf(active));
			message.setVersion(message.getVersion()+1);
			
			if (autodeactivate != null && autodeactivate.length() > 0)
			{
				 DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
				 Date result =  df.parse(autodeactivate); 
				 if (autodeactivatetime != null && autodeactivatetime.length() > 0)
				 {
					 int hours = Integer.parseInt(autodeactivatetime);
					 Calendar cal = Calendar.getInstance();
					 cal.setTime(result);
					 cal.add(Calendar.HOUR_OF_DAY, hours);
					 result = cal.getTime();
				 }
				 message.setAutoDeactivate(result);
			} else {
				message.setAutoDeactivate(null);
			}
			
			systemService.save(message);
			return new ModelAndView("redirect:/administration/system");
		} catch (NumberFormatException e)
		{
			ModelAndView m =  new ModelAndView("administration/system", "message", message);
			m.addObject("error", resources.getMessage("validation.invalidNumber", null, "This value is not a valid number", locale));
			return m;
		} catch (ParseException e) {
			ModelAndView m =  new ModelAndView("administration/system", "message", message);
			m.addObject("error", resources.getMessage("validation.invalidDate", null, "This value is not a valid date", locale));
			return m;
		}		
	}
	
	@RequestMapping(value ="/configureComplexity", method = RequestMethod.POST)
	public ModelAndView configureComplexity(@RequestParam Map<String,String> allRequestParams, HttpServletRequest request, Model model, Locale locale) 
	{
		ModelAndView m = null;
		
		try
		{
			for(Entry<String, String> e: allRequestParams.entrySet())
			{
				if(!e.getKey().equals("_csrf"))
				{
					Integer.parseInt(e.getValue());
					settingsService.update(e.getKey(), e.getValue());
				}
			}
			
			m =  new ModelAndView("redirect:/administration/system");
		}
		catch (NumberFormatException e)
		{
			m =  new ModelAndView("administration/system");
			m.addObject("error", resources.getMessage("validation.invalidNumber", null, "This value is not a valid number", locale));
		}

		return m;	
	}
	
	@RequestMapping(value = "/complexity", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody Map<String, Integer> getComplexityDefinition(HttpServletRequest request) {
		
		Map<String, Integer> complexityParameterList = new LinkedHashMap<>();
		for(ComplexityParameters cp : ComplexityParameters.values())
		{
			complexityParameterList.put(cp.getKey(), Integer.parseInt(settingsService.get(cp.getKey())));
		}
		
		return complexityParameterList;
	}
}
