package com.ec.survey.controller;

import com.ec.survey.exception.ForbiddenURLException;
import com.ec.survey.exception.InvalidURLException;
import com.ec.survey.exception.NoFormLoadedException;
import com.ec.survey.model.*;
import com.ec.survey.model.administration.EcasUser;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.LocalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.attendees.*;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.*;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.NotAgreedToTosException;
import com.ec.survey.tools.Tools;
import com.ec.survey.tools.Ucs2Utf8;
import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.errors.IntrusionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/{shortname}/management")
public class ParticipantsController extends BasicController {

	private @Value("${server.prefix}") String host;
	private @Value("${sender}") String sender;
	private @Value("${smtpserver}") String smtpServer;
	private @Value("${smtp.port}") String smtpPort;
	private @Value("${participant.default.domain:@null}") String defaultDomain;
		
	@Resource(name = "sessionService")
	private SessionService sessionService;
	
	@Resource(name = "surveyService")
	private SurveyService surveyService;
	
	@Resource(name = "participationService")
	private ParticipationService participationService;
	
	@Resource(name="attendeeService")
	private AttendeeService attendeeService;
	
	@Resource(name="mailService")
	private MailService mailService;
	
	@Resource(name="ldapService")
	private LdapService ldapService;
	
	@Resource(name = "ldapDBService")
	private LdapDBService ldapDBService;
	
	
	@RequestMapping(value = "/participantsDelete", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String participantsDelete(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		ParticipationGroup g = participationService.get(Integer.parseInt(id));
		
		Survey survey = surveyService.getSurveyByUniqueId(g.getSurveyUid(), false, true);		
		if (survey == null)
		{
			survey = surveyService.getSurvey(g.getSurveyId(), false, true);
		}
		
		User u = sessionService.getCurrentUser(request);
		sessionService.upgradePrivileges(survey, u, request);
		
		int accessPrivilege = 0;
		if (survey.getOwner().getId().equals(u.getId())) {
			accessPrivilege = 2;
		} else if (u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2) {
			accessPrivilege = 2;
		} else {
			accessPrivilege = u.getLocalPrivileges().get(LocalPrivilege.ManageInvitations);
		}
		
		if (accessPrivilege < 2)
		{		
			return null;		
		}
		
		if (g.getActive()) return "redirect:/" +  survey.getShortname() + "/management/participants";
		
		participationService.delete(g);
		activityService.log(502, g.getId().toString(), null, u.getId(), survey.getUniqueId(), g.getNiceType());
				
		return "redirect:/" + survey.getShortname() + "/management/participants?action=deleted";
	}
		
	@RequestMapping(value = "/participantsActivate", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String participantsActive(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		
		ParticipationGroup g = participationService.get(Integer.parseInt(id));
				
		Survey survey = surveyService.getSurveyByUniqueId(g.getSurveyUid(), false, true);		
		if (survey == null)
		{
			survey = surveyService.getSurvey(g.getSurveyId(), false, true);
		}
		
		User u = sessionService.getCurrentUser(request);
		sessionService.upgradePrivileges(survey, u, request);
		int accessPrivilege = 0;
		if (survey.getOwner().getId().equals(u.getId())) {
			accessPrivilege = 2;
		} else if (u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2) {
			accessPrivilege = 2;
		} else {
			accessPrivilege = u.getLocalPrivileges().get(LocalPrivilege.ManageInvitations);
		}
		
		if (accessPrivilege < 2)
		{		
			return null;		
		}
		
		g.setActive(true);
		participationService.update(g);
		activityService.log(504, null, g.getId().toString(), u.getId(), survey.getUniqueId(), g.getNiceType());
		return "redirect:/" + survey.getShortname() + "/management/participants?action=activated";
	}
	
	@RequestMapping(value = "/participantsEdit", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView participantsEdit(@PathVariable String shortname, HttpServletRequest request, Locale locale) throws Exception {
		String id = request.getParameter("id");
		String error = request.getParameter("error");
				
		ParticipationGroup g = participationService.get(Integer.parseInt(id));
		
		User u = sessionService.getCurrentUser(request);
		
		Survey survey = surveyService.getSurveyByUniqueId(g.getSurveyUid(), false, true);		
		if (survey == null)
		{
			survey = surveyService.getSurvey(g.getSurveyId(), false, true);
		}
		
		sessionService.upgradePrivileges(survey, u, request);
		int accessPrivilege = 0;
		if (survey.getOwner().getId().equals(u.getId())) {
			accessPrivilege = 2;
		} else if (u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2) {
			accessPrivilege = 2;
		} else {
			accessPrivilege = u.getLocalPrivileges().get(LocalPrivilege.ManageInvitations);
		}
		
		if (accessPrivilege < 1)
		{		
			throw new ForbiddenURLException();		
		}
		
		boolean readonly = accessPrivilege < 2;
		
		ModelAndView result = participants(shortname, request, locale);
		
		if (g.getDepartments() != null && g.getDepartments().size() > 1)
		{
			g.setDepartments(new TreeSet<>(g.getDepartments()));
		}
		
		result.addObject("selectedParticipationGroup", g);
		result.addObject("grouperror", error);
		if (readonly)
		{
			result.addObject("readonly", readonly);
		}
		
		return result;
	}
	
	@RequestMapping(value = "/participants/finishedguestlists", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody List<String> finishedguestlists(HttpServletRequest request) {
		List<String> groups = new ArrayList<>();
		
		String ids = request.getParameter("ids");		
		if (ids != null && ids.length() > 0 && ids.contains("|"))
		{
			String[] arrIDs = ids.split("\\|");
			for (String id: arrIDs)
			{
				if (id != null && id.length() > 0)
				{
					ParticipationGroup g = participationService.get(Integer.parseInt(id), true);
					if (g != null && !g.isInCreation())
					{
						String participants = "0";
						
						if (g.getError() != null && g.getError().length() > 0)
						{
							participants = "error" + g.getError();
						} else if (g.getType() == ParticipationGroupType.ECMembers)
						{
							participants = Integer.toString(g.getEcasUsers().size());
						} else if (g.getType() == ParticipationGroupType.Token) {
							participants = Integer.toString(g.getInvited());
						} else {
							participants = Integer.toString(g.getAttendees().size());
						}
						groups.add(id + "|" + participants);
					}
				}
			}
		}
		
		return groups;
	}
	
	@RequestMapping(value = "/participants/finishedguestlistsmail", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody List<KeyValue> finishedguestlistsmail(HttpServletRequest request) {
				
		List<KeyValue> groups = new ArrayList<>();
		
		String ids = request.getParameter("ids");
		String surveyUid = request.getParameter("surveyUid");	
		if (ids != null && ids.length() > 0 && ids.contains("|"))
		{
			List<Integer> running = mailService.getParticipationGroupsWithRunningMail(surveyUid);
						
			String[] arrIDs = ids.split("\\|");
			for (String id: arrIDs)
			{
				if (id != null && id.length() > 0)
				{					
					if (!running.contains(Integer.parseInt(id)))
					{
						Integer count = participationService.getInvitationCount(Integer.parseInt(id));
						groups.add(new KeyValue(id,count.toString()));
					}					
				}
			}
		}
		
		return groups;
	}
	
	@RequestMapping(value = "/tokensjson", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody List<String> tokensjson(HttpServletRequest request) throws Exception {
		String rows = request.getParameter("rows");		
		int itemsPerPage = Integer.parseInt(rows);
		
		String page = request.getParameter("page");		
		int newPage = Integer.parseInt(page);
		
		String group = request.getParameter("group");	
		
		if (group == null || !StringUtils.isNumeric(group))
		{
			return new ArrayList<>();
		}
		
		int id = Integer.parseInt(group);
		
		String token = request.getParameter("token");	
		
		ParticipationGroup g = participationService.get(id);
		Survey survey = surveyService.getSurveyByUniqueId(g.getSurveyUid(), false, true);		
		if (survey == null)
		{
			survey = surveyService.getSurvey(g.getSurveyId(), false, true);
		}
		User u = sessionService.getCurrentUser(request);
		sessionService.upgradePrivileges(survey, u, request);
		int accessPrivilege = 0;
		if (survey.getOwner().getId().equals(u.getId())) {
			accessPrivilege = 2;
		} else if (u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2) {
			accessPrivilege = 2;
		} else {
			accessPrivilege = u.getLocalPrivileges().get(LocalPrivilege.ManageInvitations);
		}
		
		if (accessPrivilege < 1)
		{		
			throw new ForbiddenURLException();		
		}
		
		if (token != null && token.length() > 0)
		{
			List<String> tokens = new ArrayList<>();
			
			try {
				List<Invitation> invitations = attendeeService.getInvitationsByUniqueId(token);
				
				for (Invitation invitation : invitations) {
					if (invitation != null)
					{
						if (invitation.getParticipationGroupId().equals(id))
						{
							if (invitation.getDeactivated() != null && invitation.getDeactivated())
							{
								tokens.add("0" + invitation.getUniqueId());
							} else {
								tokens.add("1" + invitation.getUniqueId());
							}						
						}						
					} 
				}				
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}
			
			return tokens;			
		}

		return participationService.getTokens(newPage, itemsPerPage, id, true);
	}
	
	@RequestMapping(value = "/participantsDeactivate", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String participantsDeactive(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		
		ParticipationGroup g = participationService.get(Integer.parseInt(id), true);
		
		Survey survey = surveyService.getSurveyByUniqueId(g.getSurveyUid(), false, true);		
		if (survey == null)
		{
			survey = surveyService.getSurvey(g.getSurveyId(), false, true);
		}
		User u = sessionService.getCurrentUser(request);
		
		sessionService.upgradePrivileges(survey, u, request);
		int accessPrivilege = 0;
		if (survey.getOwner().getId().equals(u.getId())) {
			accessPrivilege = 2;
		} else if (u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2) {
			accessPrivilege = 2;
		} else {
			accessPrivilege = u.getLocalPrivileges().get(LocalPrivilege.ManageInvitations);
		}
		
		if (accessPrivilege < 2)
		{		
			throw new ForbiddenURLException();		
		}
		
		g.setActive(false);
		participationService.update(g);
		activityService.log(503, null, g.getId().toString(), u.getId(), survey.getUniqueId(), g.getNiceType());
		return "redirect:/" + survey.getShortname() + "/management/participants?action=deactivated";
	}
	
	@RequestMapping(value = "/sendInvitations/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView sendInvitations(@PathVariable String shortname, @PathVariable String id, HttpServletRequest request, Locale locale) throws Exception {
		Form form;
		try {
			form = sessionService.getForm(request, shortname, false);
		} catch (NoFormLoadedException ne)
		{
			logger.error(ne.getLocalizedMessage(), ne);
			ModelAndView model = new ModelAndView("error/generic");
			String message = resources.getMessage("error.NoFormLoaded", null, "You have to load a survey before you can use this page!", locale);
			model.addObject("message", message);
			return model;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new ForbiddenURLException();
		}
		
		User user = sessionService.getCurrentUser(request);
		sessionService.upgradePrivileges(form.getSurvey(), user, request);
		int accessPrivilege = 0;
		if (form.getSurvey().getOwner().getId().equals(user.getId())) {
			accessPrivilege = 2;
		} else if (user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2) {
			accessPrivilege = 2;
		} else {
			accessPrivilege = user.getLocalPrivileges().get(LocalPrivilege.ManageInvitations);
		}
		
		if (accessPrivilege < 2)
		{		
			throw new ForbiddenURLException();		
		}
		
		ModelAndView result = new ModelAndView("management/send-invitations", "form", form);
		
		result.addObject("serverprefix", host);
		
		ParticipationGroup participationGroup = participationService.get(Integer.parseInt(id));
		
		result.addObject("senderSubject", participationGroup.getTemplateSubject() != null ? participationGroup.getTemplateSubject() : "Invitation");
		
		Map<Integer, Invitation> invitationsByAttendee = attendeeService.getInvitationsByAttendeeForParticipationGroup(participationGroup.getId());
		
		for (Attendee attendee : participationGroup.getAttendees()) {
			if (invitationsByAttendee.containsKey(attendee.getId()))
			{
				Invitation invitation = invitationsByAttendee.get(attendee.getId());
				attendee.setInvited(invitation.getInvited());
				attendee.setReminded(invitation.getReminded());
				attendee.setAnswers(invitation.getAnswers());
			}
		}
		
		for (EcasUser ecasUser : participationGroup.getEcasUsers()) {
			if (invitationsByAttendee.containsKey(ecasUser.getId()))
			{
				Invitation invitation = invitationsByAttendee.get(ecasUser.getId());
				ecasUser.setInvited(invitation.getInvited());
				ecasUser.setReminded(invitation.getReminded());
				ecasUser.setAnswers(invitation.getAnswers());
			}
		}
		
		result.addObject("participationGroup", participationGroup);
		result.addObject("usertexts", participationService.getTemplates(user.getId()));		
		
		if (user.getECPrivilege() > 0)
		{
			result.addObject("allowSenderAddress", true);
		}
		
		return result;
	}
	
	@RequestMapping(value = "/sendInvitations", method = RequestMethod.POST)
	public ModelAndView sendInvitationsPOST(@PathVariable String shortname, HttpServletRequest request, Locale locale) throws Exception {
		Form form;
		try {
			form = sessionService.getForm(request, shortname, false);
		} catch (NoFormLoadedException ne)
		{
			logger.error(ne);
			ModelAndView model = new ModelAndView("error/generic");
			String message = resources.getMessage("error.NoFormLoaded", null, "You have to load a survey before you can use this page!", locale);
			model.addObject("message", message);
			return model;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new ForbiddenURLException();
		}
		
		User user = sessionService.getCurrentUser(request);
		sessionService.upgradePrivileges(form.getSurvey(), user, request);
		int accessPrivilege = 0;
		if (form.getSurvey().getOwner().getId().equals(user.getId())) {
			accessPrivilege = 2;
		} else if (user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2) {
			accessPrivilege = 2;
		} else {
			accessPrivilege = user.getLocalPrivileges().get(LocalPrivilege.ManageInvitations);
		}
		
		if (accessPrivilege < 2)
		{		
			throw new ForbiddenURLException();		
		}
		
		String participationGroupIdString = request.getParameter("participationGroup");
		String mailtemplate = request.getParameter("mailtemplate");
		String mailtext = request.getParameter("mailtext");
		
		String selectedAttendee = request.getParameter("selectedAttendee");
		String senderAddress = Tools.escapeHTML(request.getParameter("senderAddress"));
		String senderSubject = Tools.escapeHTML(request.getParameter("senderSubject"));
		
		if (user.getECPrivilege() == 0)
		{
			senderAddress = user.getEmail();
		}
		
		if (senderSubject == null || senderSubject.trim().length() == 0)
		{
			senderSubject = "Invitation";
		}
		
		String text1 = Tools.filterHTML(request.getParameter("text1"));
		String text2 = Tools.filterHTML(request.getParameter("text2"));	
		
		if (senderAddress == null || senderAddress.trim().length() == 0) senderAddress = user.getEmail();		
		if (senderAddress == null || senderAddress.trim().length() == 0) senderAddress = sender;
		
		MailTask task = new MailTask();
		task.setLocale(locale.getLanguage());
		task.setSelectedAttendee(selectedAttendee);
		task.setSenderAddress(senderAddress);
		task.setSenderSubject(senderSubject);
		task.setState("WAITING");
		task.setSurveyUid(form.getSurvey().getUniqueId());
		task.setText1(text1);
		task.setText2(text2);
		task.setMailtemplate(mailtemplate);
		task.setUserId(user.getId());
		task.setParticipationGroupId(Integer.parseInt(participationGroupIdString));
		task.setParameters(Ucs2Utf8.requestToHashMap(request));
		
		mailService.start(task);
		
		if (mailtext != null && mailtext.length() > 0)
		{
			int templateid = Integer.parseInt(mailtext);
			ParticipationGroup group = participationService.get(task.getParticipationGroupId());
			group.setLastUsedTemplateID(templateid);
			participationService.update(group);
		}		
		
		return new ModelAndView("redirect:/" + form.getSurvey().getShortname() + "/management/participants?action=mailsstarted");
	}
	
	@RequestMapping(value = "/participantsjson", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody List<ParticipationGroup> participantsjson(@PathVariable String shortname, HttpServletRequest request) throws Exception {	
		
		int itemsPerPage = 10;
		int page = 1;
		
		if(request.getParameter("rows") != null && request.getParameter("page") != null)
		{
			String itemsPerPageValue = request.getParameter("rows");		
			itemsPerPage = Integer.parseInt(itemsPerPageValue);
			
			String pageValue = request.getParameter("page");		
			page = Integer.parseInt(pageValue);
		}
		
		User user = sessionService.getCurrentUser(request);
		
		Form form;
		Survey survey = surveyService.getSurveyByShortname(shortname, true, user, request, false, true, true);
		form = new Form(resources);
		form.setSurvey(survey);
		
		sessionService.upgradePrivileges(form.getSurvey(), user, request);
		int accessPrivilege = 0;
		if (form.getSurvey().getOwner().getId().equals(user.getId())) {
			accessPrivilege = 2;
		} else if (user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2) {
			accessPrivilege = 2;
		} else {
			accessPrivilege = user.getLocalPrivileges().get(LocalPrivilege.ManageInvitations);
		}
		
		if (accessPrivilege < 1)
		{		
			throw new ForbiddenURLException();		
		}
	
		List<ParticipationGroup> participationGroups = participationService.getAll(form.getSurvey().getUniqueId(), true, page, itemsPerPage);
		for (ParticipationGroup group : participationGroups)
		{
			if (group.getType() == ParticipationGroupType.ECMembers)
			{
				group.setChildren(group.getEcasUsers().size());
				group.setEcasUsers(null);
			} else if (group.getType() == ParticipationGroupType.Static)
			{
				group.setChildren(group.getAttendees().size());
				group.setAttendees(null);
			}
		}
		
		return participationGroups;
	}
	

	@RequestMapping(value = "/participants", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView participants(@PathVariable String shortname, HttpServletRequest request, Locale locale) throws Exception {
		User u = sessionService.getCurrentUser(request);
		
		Form form;
		Survey survey = surveyService.getSurveyByShortname(shortname, true, u, request, false, true, true);
		form = new Form(resources);
		form.setSurvey(survey);
				
		sessionService.upgradePrivileges(form.getSurvey(), u, request);
		int accessPrivilege = 0;
		if (form.getSurvey().getOwner().getId().equals(u.getId())) {
			accessPrivilege = 2;
		} else if (u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2) {
			accessPrivilege = 2;
		} else {
			accessPrivilege = u.getLocalPrivileges().get(LocalPrivilege.ManageInvitations);
		}
		
		if (accessPrivilege < 1)
		{		
			throw new ForbiddenURLException();		
		}

		int owner = u.getId();
		
		ModelAndView result = new ModelAndView("management/participants", "form", form);
		
		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String newPage = request.getParameter("newPage");		
		newPage = newPage == null ? "1" : newPage;
		HashMap<String, String> filter = new HashMap<>();
		if (name != null) filter.put("name", name);
		if (email != null) filter.put("email", email);
		
		if (u.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) == 2)
		{
			owner = 0;
		}
		
		Integer itemsPerPage = ConversionTools.getInt(request.getParameter("itemsPerPage"), 10);	
		Paging<Attendee> paging = new Paging<>();
		paging.setItemsPerPage(itemsPerPage);
		int numberOfAttendees = attendeeService.getNumberOfAttendees(owner, filter);
		paging.setNumberOfItems(numberOfAttendees);
		paging.moveTo(newPage);
		
		List<Attendee> attendees = attendeeService.getAttendees(owner, new HashMap<>(), paging.getCurrentPage(), paging.getItemsPerPage());
		paging.setItems(attendees);

    	result.addObject("attributeNames", u.getSelectedAttributes());
    	result.addObject("filter", filter);
    	result.addObject("paging", paging);
    	
    	result.addObject("allAttributeNames", attendeeService.getAllAttributes(owner));
    	
    	if (request.getParameter("action") != null)
    	{
    		result.addObject("action", request.getParameter("action"));
    	}
    	
    	if (request.getParameter("error") != null)
    	{
    		result.addObject("error", request.getParameter("error"));
    	}
		
		List<KeyValue> domains = ldapDBService.getDomains(false, false, resources, locale);
		result.addObject("domains", domains);
		
		boolean ownerSelected = false;
		if (u.getSelectedAttributes() != null)
		{
	    	for (AttributeName aname : u.getSelectedAttributes())
	    	{
	    		if (aname.getName().equals("Owner"))
	    		{
	    			ownerSelected = true;
	    			break;
	    		}
	    	}
		}
    	result.addObject("ownerSelected",ownerSelected);
		
    	
    	// add the default domian selection
    	if(StringUtils.isEmpty(defaultDomain))
    		defaultDomain="eu.europa.ec";
    	result.addObject("defaultDomain",defaultDomain);
		
		return result;
	}
	
	
	@RequestMapping(value = "/participantsJSONAll", headers="Accept=*/*", method=RequestMethod.GET)
	public @ResponseBody Paging<Attendee> participantsSearchAll(HttpServletRequest request, HttpServletResponse response ) throws NotAgreedToTosException {
		return participantsSearch(request, response, true);
	}
	
	@RequestMapping(value = "/participantsJSON", headers="Accept=*/*", method=RequestMethod.GET)
	public @ResponseBody Paging<Attendee> participantsSearch(HttpServletRequest request, HttpServletResponse response ) throws NotAgreedToTosException {
		return participantsSearch(request, response, false);
	}
	
	@RequestMapping(value = "/topDepartmentsJSON", headers="Accept=*/*", method=RequestMethod.GET)
	public @ResponseBody List<KeyValue> topDepartments(HttpServletRequest request, HttpServletResponse response ) throws InvalidURLException {
		
		if (!isAjax(request))
		{
			throw new InvalidURLException();
		}
		
		String domain = request.getParameter("domain");
		if (domain == null) {
			domain = "eu.europa.ec";
		}
		return ldapService.getTopDepartments(domain);		
	}
	
	@RequestMapping(value = "/departmentsJSON", headers="Accept=*/*", method=RequestMethod.GET)
	public @ResponseBody List<KeyValue> departments(HttpServletRequest request, HttpServletResponse response ) {
		return ldapService.getDepartments(request.getParameter("term"));		
	}
	
	@RequestMapping(value = "/saveTemplateJSON", headers="Accept=*/*", method=RequestMethod.POST)
	public @ResponseBody String saveTemplateJSON(HttpServletRequest request, HttpServletResponse response ) throws IntrusionException {
		try {		
			String text1 = Tools.filterHTML(Ucs2Utf8.unconvert(request.getParameter("text1")));
			String text2 = Tools.filterHTML(Ucs2Utf8.unconvert(request.getParameter("text2")));
			String subject = Tools.filterHTML(Ucs2Utf8.unconvert(request.getParameter("subject")));
			String template = request.getParameter("template");
			String texttemplate = request.getParameter("texttemplate");
			String name = request.getParameter("name");
			String replyto = request.getParameter("replyto");
			
			User user = sessionService.getCurrentUser(request);
			if (user == null) return "ERROR";
			
			InvitationTemplate existing = participationService.getTemplateByName(name, user.getId());
			if (existing != null) return "EXISTS";
			
			InvitationTemplate it = new InvitationTemplate();
			it.setName(name);
			it.setOwner(user);
			it.setTemplate1(text1);
			it.setTemplate2(text2);
			it.setTemplateMail(template);
			it.setTemplateSubject(subject);
			it.setTemplateText(Integer.parseInt(texttemplate));
			it.setReplyto(replyto);
			
			participationService.save(it);
			return it.getId().toString();		
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
		}
		return "ERROR";
	}
	
	@RequestMapping(value = "/loadTemplateJSON", headers="Accept=*/*", method=RequestMethod.GET)
	public @ResponseBody InvitationTemplate loadTemplateJSON(HttpServletRequest request, HttpServletResponse response ) throws IntrusionException {
		try {		
			
			String id = request.getParameter("id");
			
			User user = sessionService.getCurrentUser(request);
			if (user == null) return null;
			
			InvitationTemplate existing = participationService.getTemplate(Integer.parseInt(id));
			if (existing == null) return null;
			
			if (!existing.getOwner().getId().equals(user.getId()))
			{
				return null;
			}
			
			return existing;		
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
	
	@RequestMapping(value = "/deleteTemplateJSON", headers="Accept=*/*", method=RequestMethod.GET)
	public @ResponseBody String deleteTemplateJSON(HttpServletRequest request, HttpServletResponse response ) throws IntrusionException {
		try {		
			
			String id = request.getParameter("id");
			
			User user = sessionService.getCurrentUser(request);
			if (user == null) return null;
			
			InvitationTemplate existing = participationService.getTemplate(Integer.parseInt(id));
			if (existing == null) return null;
			
			if (!existing.getOwner().getId().equals(user.getId()))
			{
				return null;
			}
			
			participationService.delete(existing);
			
			return "OK";
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
		}
		return "ERROR";
	}
		
	private @ResponseBody Paging<Attendee> participantsSearch(HttpServletRequest request, HttpServletResponse response, boolean all ) throws NotAgreedToTosException {

		User user = sessionService.getCurrentUser(request);
		int owner = user.getId();
		
		HashMap<String,String[]> parameters = Ucs2Utf8.requestToHashMap(request);
		String name = "";
		if (parameters.containsKey("name")) name = parameters.get("name")[0];
		String email = "";
		if (parameters.containsKey("email")) email = parameters.get("email")[0];
		HashMap<String, String> attributeFilter = new HashMap<>();
		if (name.length() > 0) attributeFilter.put("name", name);
		if (email.length() > 0) attributeFilter.put("email", email);
		
		if (user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) == 2)
		{
			owner = 0;
		}
		
		String newPage = request.getParameter("newPage");		
		newPage = newPage == null ? "1" : newPage;
		Integer itemsPerPage = ConversionTools.getInt(request.getParameter("itemsPerPage"), 10);	
		
		if (all) {
			itemsPerPage = Integer.MAX_VALUE;
			newPage = "1";
		}
		
		Paging<Attendee> paging = new Paging<>();
		paging.setItemsPerPage(itemsPerPage);
		int numberOfAttendees = attendeeService.getNumberOfAttendees(owner, attributeFilter);
		paging.setNumberOfItems(numberOfAttendees);
		paging.setCurrentPage(Integer.parseInt(newPage));
		
		for (String key : parameters.keySet())
		{
			try {
				if (key.equals("owner") && parameters.get(key) != null && parameters.get(key).length > 0)
				{
					String[] value = parameters.get(key);
					if (value[0] != null && value[0].length() > 0)
					attributeFilter.put(key, parameters.get(key)[0]);
				} else {
					int intKey = Integer.parseInt(key);
					
					if (intKey > 0 && parameters.get(key) != null && parameters.get(key).length > 0 && !key.equalsIgnoreCase("name") && !key.equalsIgnoreCase("email")  && !key.equalsIgnoreCase("newPage"))
					{
						String[] value = parameters.get(key);
						if (value[0] != null && value[0].length() > 0)
						attributeFilter.put(key, parameters.get(key)[0]);
					}
				}
			} catch (Exception e)
			{}
		}
		
		List<Attendee> attendees = attendeeService.getAttendees(owner, attributeFilter, paging.getCurrentPage(), paging.getItemsPerPage());
		
		paging.setItems(attendees);
		
		return paging;
	}
	
	@RequestMapping(value = "/participants", method = RequestMethod.POST)
	public ModelAndView participantsPOST(@PathVariable String shortname, HttpServletRequest request, Locale locale) throws Exception {
		Form form;
		try {
			form = sessionService.getForm(request, shortname, false);
		} catch (NoFormLoadedException ne)
		{
			logger.error(ne);
			ModelAndView model = new ModelAndView("error/generic");
			String message = resources.getMessage("error.NoFormLoaded", null, "You have to load a survey before you can use this page!", locale);
			model.addObject("message", message);
			return model;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new ForbiddenURLException();
		}
		User user = sessionService.getCurrentUser(request);
		
		sessionService.upgradePrivileges(form.getSurvey(), user, request);
		int accessPrivilege = 0;
		if (form.getSurvey().getOwner().getId().equals(user.getId())) {
			accessPrivilege = 2;
		} else if (user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2) {
			accessPrivilege = 2;
		} else {
			accessPrivilege = user.getLocalPrivileges().get(LocalPrivilege.ManageInvitations);
		}
		
		if (accessPrivilege < 2)
		{		
			throw new ForbiddenURLException();		
		}
		
		
		int owner = user.getId();
		
		HashMap<String,String[]> parameters = Ucs2Utf8.requestToHashMap(request);
		String name = "";
		if (parameters.containsKey("name")) name = parameters.get("name")[0];
		String email = "";
		if (parameters.containsKey("email")) email = parameters.get("email")[0];
		HashMap<String, String> attributeFilter = new HashMap<>();
		AttendeeFilter filter = new AttendeeFilter();
		
		if (user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) < 2)
		{
			attributeFilter.put("owner", user.getId().toString());
			filter.setOwnerId(user.getId());
		} else {
			owner = 0;
		}
		
		if (name.length() > 0) {
			attributeFilter.put("name", name);
			filter.setName(name);
		}
		if (email.length() > 0) {
			attributeFilter.put("email", email);
			filter.setEmail(email);
		}
		
		String groupName =  parameters.get("groupName")[0];
		String groupOwner =  parameters.get("groupOwner")[0];
		String groupType =  parameters.get("groupType")[0];
		
		parameters.remove("groupName");
		parameters.remove("groupOwner");
		parameters.remove("groupType");
		parameters.remove("name");
		parameters.remove("email");
		parameters.remove("newPage");		
		
		if (groupType.equalsIgnoreCase("dynamic"))
		{
				
			for (String key : parameters.keySet())
			{				
				if (!key.equalsIgnoreCase("id"))
				{
					int intKey = Integer.parseInt(key);				
					
					if (intKey > 0 && parameters.get(key) != null && parameters.get(key).length > 0)
					{
						String[] value = parameters.get(key);
						if (value[0] != null && value[0].length() > 0)
						attributeFilter.put(key, parameters.get(key)[0]);
						Attribute attribute = new Attribute();
						AttributeName attributeName = attendeeService.getAttributeName(intKey);
						attribute.setAttributeName(attributeName);
						attribute.setValue(parameters.get(key)[0]);
						filter.getAttributes().add(attribute);
					}
				}
			}
		
			List<Attendee> attendees = attendeeService.getAttendees(owner, attributeFilter, 1, Integer.MAX_VALUE);
			ParticipationGroup g = new ParticipationGroup(form.getSurvey().getUniqueId());
			
			if (parameters.containsKey("id"))
			{
				int id = Integer.parseInt(parameters.get("id")[0]);
				g = participationService.get(id);
			} else {
				g.setActive(true);			
				g.setSurveyId(form.getSurvey().getId());
			}
			
			g.setAttendeeFilter(filter);
			g.setAttendees(attendees);			
			g.setName(groupName);
			g.setType(ParticipationGroupType.Dynamic);
			
			if (groupOwner != null && groupOwner.length() > 0 && !groupOwner.equalsIgnoreCase(user.getLogin()))
			{
				//this feature is not used yet
			} else {			
				g.setOwnerId(user.getId());
			}
			
			participationService.save(g);
			
		}
		
		return participants(shortname, request, locale);
	}
	
	@RequestMapping(value = "/participants/createTokens", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody List<String> participantsCreateTokensPOST(@PathVariable String shortname, HttpServletRequest request, Locale locale) {
		String strtokens = request.getParameter("tokens");		
		int tokens = Integer.parseInt(strtokens);
		
		List<String> newtokens = new ArrayList<>();
		
		for (int i = 0; i < tokens; i++) {
        	String token = UUID.randomUUID().toString();
        	newtokens.add(token);
		}
		
		return newtokens;
	}
	
	@RequestMapping(value = "/participants/saveTokens", method = RequestMethod.POST)
	public ModelAndView saveTokensPOST(@PathVariable String shortname, HttpServletRequest request, Locale locale) throws Exception {
		Form form;
		try {
			
			String idSurvey = request.getParameter("survey");
			logger.info("saveTokensPOST GET THE SURVEY ID " + idSurvey +" SHORTNAME " + shortname);
			
			//Survey survey4Token= surveyService.getSurveyForParticipant(shortname);
			
			StopWatch st= new StopWatch();
			st.setKeepTaskList(true);
			
			st.start();
			form = sessionService.getForm(request, shortname, false);			
			st.stop();
			logger.info("SaveToken with FORM TOOK " + st.getTotalTimeMillis() );
			
		} catch (NoFormLoadedException ne)
		{
			logger.error(ne);
			ModelAndView model = new ModelAndView("error/generic");
			String message = resources.getMessage("error.NoFormLoaded", null, "You have to load a survey before you can use this page!", locale);
			model.addObject("message", message);
			return model;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new ForbiddenURLException();
		}
		User user = sessionService.getCurrentUser(request);
		
		sessionService.upgradePrivileges(form.getSurvey(), user, request);
		int accessPrivilege = 0;
		if (form.getSurvey().getOwner().getId().equals(user.getId())) {
			accessPrivilege = 2;
		} else if (user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2) {
			accessPrivilege = 2;
		} else {
			accessPrivilege = user.getLocalPrivileges().get(LocalPrivilege.ManageInvitations);
		}
		
		if (accessPrivilege < 2)
		{		
			throw new ForbiddenURLException();		
		}
			
		HashMap<String,String[]> parameters = Ucs2Utf8.requestToHashMap(request);
		
		String groupid = "";
		if (parameters.containsKey("groupid")) groupid = parameters.get("groupid")[0];	
		String groupname = "";
		if (parameters.containsKey("groupname")) groupname = Tools.escapeHTML(parameters.get("groupname")[0]);	
		
		ParticipationGroup g = new ParticipationGroup(form.getSurvey().getUniqueId());
		
		if (groupid.length() > 0)
		{
			int id = Integer.parseInt(groupid);
			g = participationService.get(id);
		} else {
			g.setActive(true);			
			g.setSurveyId(form.getSurvey().getId());
			g.setOwnerId(user.getId());
		}
		
		g.setName(groupname);
		g.setType(ParticipationGroupType.Token);
		
		Map<String, String> operations = new HashMap<>();
		List<String> tokens = new ArrayList<>();
		for (String key : parameters.keySet())
		{
			String v = parameters.get(key)[0];
			
			if (v != null && v.trim().length() > 0)
			{				
				if (key.startsWith("newtoken"))
				{
					String token = parameters.get(key)[0];
					tokens.add(token);
				} else if (key.startsWith("token"))
				{
					String token = key.substring(5);
					String value = parameters.get(key)[0];
					
					if (value != null && value.length() > 0)
					{
						operations.put(token, value);
					}
				}
			}
		}	
		
		if (operations.size() == 0)
		{
			g.setInCreation(true);
		}
		
		participationService.save(g);
	
		if (operations.size() > 0)
		{
			attendeeService.executeOperations(operations, g.getId());
			return new ModelAndView("redirect:/" +  shortname + "/management/participants?action=operations");
		}
		
		participationService.addTokensToGuestListAsync(g.getId(), tokens);
		
		ModelAndView result = new ModelAndView("redirect:/" + shortname + "/management/participants");
		result.addObject("action", "guestlistcreated");
		
		if (groupid.length() > 0)
		{
			activityService.log(505, null, g.getId().toString(), user.getId(), form.getSurvey().getUniqueId(), g.getNiceType());
		} else {
			activityService.log(501, null, g.getId().toString(), user.getId(), form.getSurvey().getUniqueId(), g.getNiceType());
		}
		
		return result;
	}
	
	@RequestMapping(value = "/participantsStatic", method = RequestMethod.POST)
	public ModelAndView participantsStaticPOST(@PathVariable String shortname, HttpServletRequest request, Locale locale) throws Exception {
		Form form;
		try {
			form = sessionService.getForm(request, shortname, false);
		} catch (NoFormLoadedException ne)
		{
			logger.error(ne);
			ModelAndView model = new ModelAndView("error/generic");
			String message = resources.getMessage("error.NoFormLoaded", null, "You have to load a survey before you can use this page!", locale);
			model.addObject("message", message);
			return model;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new ForbiddenURLException();
		}
		
		User user = sessionService.getCurrentUser(request);
		
		sessionService.upgradePrivileges(form.getSurvey(), user, request);
		int accessPrivilege = 0;
		if (form.getSurvey().getOwner().getId().equals(user.getId())) {
			accessPrivilege = 2;
		} else if (user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2) {
			accessPrivilege = 2;
		} else {
			accessPrivilege = user.getLocalPrivileges().get(LocalPrivilege.ManageInvitations);
		}
		
		if (accessPrivilege < 2)
		{		
			throw new ForbiddenURLException();		
		}
		
		HashMap<String,String[]> parameters = Ucs2Utf8.requestToHashMap(request);
		
		if (!parameters.containsKey("groupName"))
		{
			ModelAndView result = new ModelAndView("redirect:/" + shortname + "/management/participants?error=namemissing");
			return result;
		}
	
		String groupName =  parameters.get("groupName")[0];
		
		parameters.remove("groupName");
		parameters.remove("groupOwner");
		parameters.remove("groupType");
			
		List<Integer> attendeeIDs = new ArrayList<>();
		
		for (String key : parameters.keySet())
		{
			if (key.startsWith("att"))
			{
				int intKey = Integer.parseInt(key.substring(3));				
				attendeeIDs.add(intKey);
			}
		}
	
		ParticipationGroup g = new ParticipationGroup(form.getSurvey().getUniqueId());
		
		if (parameters.containsKey("id"))
		{
			int id = Integer.parseInt(parameters.get("id")[0]);
			g = participationService.get(id);
		} else {
			g.setActive(true);			
			g.setSurveyId(form.getSurvey().getId());
		}
	
		g.setName(groupName);		
		g.setType(ParticipationGroupType.Static);
		
		g.setOwnerId(user.getId());
		
		g.setInCreation(true);
		
		participationService.save(g);
			
		participationService.addParticipantsToGuestListAsync(g.getId(), attendeeIDs);
		
		ModelAndView result = new ModelAndView("redirect:/" + shortname + "/management/participants");
		
		result.addObject("action", "guestlistcreated");
		
		if (parameters.containsKey("id"))
		{
			activityService.log(505, null, g.getId().toString(), user.getId(), form.getSurvey().getUniqueId(), g.getNiceType());
		} else{
			activityService.log(501, null, g.getId().toString(), user.getId(), form.getSurvey().getUniqueId(), g.getNiceType());
		}
		
		return result;
	}
	
	@RequestMapping(value = "/participantsDepartments", method = RequestMethod.POST)
	public ModelAndView participantsDepartmentsPOST(@PathVariable String shortname, HttpServletRequest request, Locale locale) throws Exception {
		Form form;
		try {
			form = sessionService.getForm(request, shortname, false);
		} catch (NoFormLoadedException ne)
		{
			logger.error(ne);
			ModelAndView model = new ModelAndView("error/generic");
			String message = resources.getMessage("error.NoFormLoaded", null, "You have to load a survey before you can use this page!", locale);
			model.addObject("message", message);
			return model;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new ForbiddenURLException();
		}
		User user = sessionService.getCurrentUser(request);
		sessionService.upgradePrivileges(form.getSurvey(), user, request);
		int accessPrivilege = 0;
		if (form.getSurvey().getOwner().getId().equals(user.getId())) {
			accessPrivilege = 2;
		} else if (user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2) {
			accessPrivilege = 2;
		} else {
			accessPrivilege = user.getLocalPrivileges().get(LocalPrivilege.ManageInvitations);
		}
		
		if (accessPrivilege < 2)
		{		
			throw new ForbiddenURLException();		
		}
		
		HashMap<String,String[]> parameters = Ucs2Utf8.requestToHashMap(request);
				
		String groupName =  Tools.escapeHTML(parameters.get("groupName")[0]);
		logger.debug("START CREATE GUEST LIST ROM LDAP- GROUP NAME " + groupName);
		
		parameters.remove("groupName");
		parameters.remove("groupOwner");
		parameters.remove("groupType");
			
		List<String> departments = new ArrayList<>();
		
		for (String key : parameters.keySet())
		{
			if (key.startsWith("node"))
			{
				String department = key.substring(4);
				departments.add(department);
			} else if (key.startsWith("rootnode"))
			{
				departments.add(key);
			}
		}
	
		logger.debug("START CREATE GUEST LIST ROM LDAP- GROUP NAME GET DEPARTMENTS " + departments.size());
		ParticipationGroup g = new ParticipationGroup(form.getSurvey().getUniqueId());
		logger.debug("START CREATE GUEST LIST ROM LDAP- GROUP NAME PARTICPANTGROUP TRY TO CREATE " );
		if (parameters.containsKey("id"))
		{
			int id = Integer.parseInt(parameters.get("id")[0]);
			g = participationService.get(id);
		} else {
			g.setActive(true);			
			g.setSurveyId(form.getSurvey().getId());
		}
		
		if (parameters.containsKey("domain"))
		{
			g.setDomainCode( parameters.get("domain")[0]);
		}
		
		g.setInCreation(true);
		
		g.setName(groupName);		
		g.setType(ParticipationGroupType.ECMembers);
		
		g.setOwnerId(user.getId());
		
		participationService.save(g);
		logger.debug("START CREATE GUEST LIST ROM LDAP- PARTICIPANTGROUP CREATED ");			
		
		participationService.addParticipantsToGuestListAsync(g.getId(), departments);
		
		ModelAndView result = new ModelAndView("redirect:/" + shortname + "/management/participants");
		
		result.addObject("action", "guestlistcreated");
		
		if (parameters.containsKey("id"))
		{
			activityService.log(505, null, g.getId().toString(), user.getId(), form.getSurvey().getUniqueId(), g.getNiceType());
		} else {
			activityService.log(501, null, g.getId().toString(), user.getId(), form.getSurvey().getUniqueId(), g.getNiceType());
		}
		
		return result;
	}

}
