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
import com.ec.survey.tools.Tools;
import com.ec.survey.tools.Ucs2Utf8;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.Map.Entry;

@Controller
@RequestMapping("/{shortname}/management")
public class ParticipantsController extends BasicController {

	private @Value("${server.prefix}") String host;
	private @Value("${smtpserver}") String smtpServer;
	private @Value("${smtp.port}") String smtpPort;
	private @Value("${participant.default.domain:@null}") String defaultDomain;

	@Resource(name = "mailService")
	private MailService mailService;

	@RequestMapping(value = "/participants", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView participants(@PathVariable String shortname, HttpServletRequest request, Locale locale)
			throws Exception {
		User u = sessionService.getCurrentUser(request);

		Form form;
		Survey survey = surveyService.getSurveyByShortname(shortname, true, u, request, false, true, true, false);
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

		if (accessPrivilege < 1) {
			throw new ForbiddenURLException();
		}

		int owner = u.getId();

		ModelAndView result = new ModelAndView("management/participants", "form", form);

		String name = request.getParameter("name");
		String email = request.getParameter("email");
		HashMap<String, String> filter = new HashMap<>();
		if (name != null)
			filter.put("name", name);
		if (email != null)
			filter.put("email", email);

		if (u.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) == 2) {
			owner = 0;
		}
		int numberOfAttendees = attendeeService.getNumberOfAttendees(owner, filter);
		result.addObject("numberOfAttendees", numberOfAttendees);
		result.addObject("attributeNames", u.getSelectedAttributes());
		result.addObject("filter", filter);
		result.addObject("allAttributeNames", attendeeService.getAllAttributes(owner));
		if (request.getParameter("action") != null) {
			result.addObject("action", request.getParameter("action"));
		}

		if (request.getParameter("error") != null) {
			result.addObject("error", request.getParameter("error"));
		}

		List<KeyValue> domains = ldapDBService.getDomains(false, false, resources, locale);
		result.addObject("domains", domains);

		return result;
	}

	@RequestMapping(value = "/participantsDelete", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody String participantsDelete(HttpServletRequest request, Locale locale) {
		return participantsOperation(request, "delete", locale);
	}

	@RequestMapping(value = "/participantsActivate", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody String participantsActivate(HttpServletRequest request, Locale locale) {
		return participantsOperation(request, "activate", locale);
	}

	@RequestMapping(value = "/participantsDeactivate", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody String participantsDeactive(HttpServletRequest request, Locale locale) {
		return participantsOperation(request, "deactivate", locale);
	}

	private String participantsOperation(HttpServletRequest request, String operation, Locale locale) {
		try {
			String id = request.getParameter("id");
			ParticipationGroup g = participationService.get(Integer.parseInt(id), true);

			Survey survey = surveyService.getSurveyByUniqueId(g.getSurveyUid(), false, true);
			if (survey == null) {
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

			if (accessPrivilege < 2) {
				throw new ForbiddenURLException();
			}

			switch (operation) {
			case "activate":
				g.setActive(true);
				participationService.update(g);
				activityService.log(504, null, g.getId().toString(), u.getId(), survey.getUniqueId(), g.getNiceType());
				break;
			case "deactivate":
				g.setActive(false);
				participationService.update(g);
				activityService.log(503, null, g.getId().toString(), u.getId(), survey.getUniqueId(), g.getNiceType());
				break;
			case "delete":
				participationService.delete(g);
				activityService.log(502, g.getId().toString(), null, u.getId(), survey.getUniqueId(), g.getNiceType());
				break;
			default:
				throw new InvalidURLException();
			}

			return "ok";
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return resources.getMessage("error.OperationFailed", null,
					"There was a problem during execution of the operation. The error was logged. Please contact support if the problem occurs again.",
					locale);
		}
	}

	@SuppressWarnings({ "rawtypes" })
	@RequestMapping(value = "/children", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody List children(@PathVariable String shortname, HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");

		ParticipationGroup g = participationService.get(Integer.parseInt(id));

		User u = sessionService.getCurrentUser(request);

		Survey survey = surveyService.getSurveyByUniqueId(g.getSurveyUid(), false, true);
		if (survey == null) {
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

		if (accessPrivilege < 1) {
			throw new ForbiddenURLException();
		}

		Integer newPage = ConversionTools.getInt(request.getParameter("newPage"), 1);
		Integer itemsPerPage = ConversionTools.getInt(request.getParameter("itemsPerPage"), 10);
		boolean all = request.getParameter("all") != null && request.getParameter("all").equals("true");

		if (all) {
			itemsPerPage = Integer.MAX_VALUE;
			newPage = 1;
		}

		Map<Integer, Invitation> invitationsByAttendee = attendeeService
				.getInvitationsByAttendeeForParticipationGroup(g.getId());

		int first = (newPage - 1) * itemsPerPage;
		int counter = 0;
		if (g.getType() == ParticipationGroupType.Static) {
			List<Attendee> result = new ArrayList<>();

			for (Attendee attendee : g.getAttendees()) {
				if (counter < first) {
					counter++;
				} else {
					if (invitationsByAttendee.containsKey(attendee.getId())) {
						Invitation invitation = invitationsByAttendee.get(attendee.getId());
						attendee.setInvited(invitation.getInvited());
						attendee.setReminded(invitation.getReminded());
						attendee.setAnswers(invitation.getAnswers());
					}
					result.add(attendee);
					if (result.size() == itemsPerPage)
						break;
				}
			}

			return result;
		} else if (g.getType() == ParticipationGroupType.ECMembers) {
			List<EcasUser> result = new ArrayList<>();

			for (EcasUser ecasUser : g.getEcasUsers()) {
				if (counter < first) {
					counter++;
				} else {
					if (invitationsByAttendee.containsKey(ecasUser.getId())) {
						Invitation invitation = invitationsByAttendee.get(ecasUser.getId());
						ecasUser.setInvited(invitation.getInvited());
						ecasUser.setReminded(invitation.getReminded());
						ecasUser.setAnswers(invitation.getAnswers());
					}
					result.add(ecasUser);
					if (result.size() == itemsPerPage)
						break;
				}
			}

			return g.getEcasUsers();
		} else {
			List<Invitation> result = new ArrayList<>();
			for (Invitation i : attendeeService.getInvitationsForParticipationGroup(g.getId())) {
				if (counter < first) {
					counter++;
				} else {
					result.add(i);
					if (result.size() == itemsPerPage)
						break;
				}
			}

			return result;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PostMapping(value = "/saveguestlist")
	public @ResponseBody String saveguestlist(@RequestBody LinkedHashMap json, HttpServletRequest request,
			Locale locale) {
		try {
			User user = sessionService.getCurrentUser(request);
			Form form = sessionService.getFormFromSessionInfo(request);
			ParticipationGroup g = new ParticipationGroup(form.getSurvey().getUniqueId());

			sessionService.upgradePrivileges(form.getSurvey(), user, request);
			int accessPrivilege = 0;
			if (form.getSurvey().getOwner().getId().equals(user.getId())) {
				accessPrivilege = 2;
			} else if (user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2) {
				accessPrivilege = 2;
			} else {
				accessPrivilege = user.getLocalPrivileges().get(LocalPrivilege.ManageInvitations);
			}

			if (accessPrivilege < 2) {
				throw new ForbiddenURLException();
			}

			int id = json.get("id") != null && json.get("id").toString().length() > 0 ? (int) json.get("id") : 0;

			if (id > 0) {
				g = participationService.get(id);
			} else {
				g.setActive(true);
				g.setSurveyId(form.getSurvey().getId());
			}

			g.setName(json.get("name").toString());
			g.setType(ParticipationGroupType.valueOf(json.get("type").toString()));

			List<Integer> attendeeIDs = null;
			List<Integer> userIDs = null;
			List<String> tokens = null;
			if (g.getType() == ParticipationGroupType.Static) {
				ArrayList<LinkedHashMap> attendees = (ArrayList) json.get("attendees");
				attendeeIDs = new ArrayList<>();

				for (int i = 0; i < attendees.size(); i++) {
					LinkedHashMap attendee = attendees.get(i);
					attendeeIDs.add((int) attendee.get("id"));
				}

			} else if (g.getType() == ParticipationGroupType.Token) {
				ArrayList<LinkedHashMap> invitations = (ArrayList) json.get("tokens");
				tokens = new ArrayList<>();
				for (int i = 0; i < invitations.size(); i++) {
					LinkedHashMap token = invitations.get(i);
					tokens.add(token.get("uniqueId").toString());
				}
			} else if (g.getType() == ParticipationGroupType.ECMembers) {
				ArrayList<LinkedHashMap> users = (ArrayList) json.get("users");
				userIDs = new ArrayList<>();
				for (int i = 0; i < users.size(); i++) {
					LinkedHashMap ecasuser = users.get(i);
					userIDs.add((int) ecasuser.get("id"));
				}
			}
			g.setOwnerId(user.getId());
			g.setInCreation(true);

			participationService.save(g);
			if (g.getType() == ParticipationGroupType.Static) {
				participationService.addParticipantsToGuestListAsync(g.getId(), attendeeIDs);
			} else if (g.getType() == ParticipationGroupType.Token) {
				participationService.addTokensToGuestListAsync(g.getId(), tokens);
			} else if (g.getType() == ParticipationGroupType.ECMembers) {
				participationService.addUsersToGuestListAsync(g.getId(), userIDs);
			}

			if (id > 0) {
				activityService.log(505, null, g.getId().toString(), user.getId(), form.getSurvey().getUniqueId(),
						g.getNiceType());
			} else {
				activityService.log(501, null, g.getId().toString(), user.getId(), form.getSurvey().getUniqueId(),
						g.getNiceType());
			}

			return "success";
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return resources.getMessage("error.OperationFailed", null,
				"There was a problem during execution of the operation. The error was logged. Please contact support if the problem occurs again.",
				locale);
	}

	@RequestMapping(value = "/participants/finishedguestlists", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody List<String> finishedguestlists(HttpServletRequest request) {
		List<String> groups = new ArrayList<>();

		String ids = request.getParameter("ids");
		if (ids != null && ids.length() > 0 && ids.contains(";")) {
			String[] arrIDs = ids.split("\\;");
			for (String id : arrIDs) {
				if (id != null && id.length() > 0) {
					ParticipationGroup g = participationService.get(Integer.parseInt(id), true);
					if (g != null && !g.isInCreation()) {
						String participants;

						if (g.getError() != null && g.getError().length() > 0) {
							participants = "error" + g.getError();
						} else if (g.getType() == ParticipationGroupType.ECMembers) {
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

	@RequestMapping(value = "/participants/finishedguestlistsmail", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody List<KeyValue> finishedguestlistsmail(HttpServletRequest request) {

		List<KeyValue> groups = new ArrayList<>();

		String ids = request.getParameter("ids");
		String surveyUid = request.getParameter("surveyUid");
		if (ids != null && ids.length() > 0 && ids.contains(";")) {
			List<Integer> running = mailService.getParticipationGroupsWithRunningMail(surveyUid);

			String[] arrIDs = ids.split("\\;");
			for (String id : arrIDs) {
				if (id != null && id.length() > 0 && !running.contains(Integer.parseInt(id))) {
					Integer count = participationService.getInvitationCount(Integer.parseInt(id));
					groups.add(new KeyValue(id, count.toString()));
				}
			}
		}

		return groups;
	}

	@RequestMapping(value = "/tokensjson", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody List<String> tokensjson(HttpServletRequest request) throws Exception {
		String rows = request.getParameter("rows");
		int itemsPerPage = Integer.parseInt(rows);

		String page = request.getParameter("page");
		int newPage = Integer.parseInt(page);

		String group = request.getParameter("group");

		if (group == null || !StringUtils.isNumeric(group)) {
			return new ArrayList<>();
		}

		int id = Integer.parseInt(group);

		String token = request.getParameter("token");

		ParticipationGroup g = participationService.get(id);
		Survey survey = surveyService.getSurveyByUniqueId(g.getSurveyUid(), false, true);
		if (survey == null) {
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

		if (accessPrivilege < 1) {
			throw new ForbiddenURLException();
		}

		if (token != null && token.length() > 0) {
			List<String> tokens = new ArrayList<>();

			try {
				List<Invitation> invitations = attendeeService.getInvitationsByUniqueId(token);

				for (Invitation invitation : invitations) {
					if (invitation != null && invitation.getParticipationGroupId().equals(id)) {
						if (invitation.getDeactivated() != null && invitation.getDeactivated()) {
							tokens.add("0" + invitation.getUniqueId());
						} else {
							tokens.add("1" + invitation.getUniqueId());
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

	@RequestMapping(value = "/sendInvitations/{id}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView sendInvitations(@PathVariable String shortname, @PathVariable String id,
			HttpServletRequest request, Locale locale) throws Exception {
		Form form;
		try {
			form = sessionService.getForm(request, shortname, false, false);
		} catch (NoFormLoadedException ne) {
			logger.error(ne.getLocalizedMessage(), ne);
			ModelAndView model = new ModelAndView("error/generic");
			String message = resources.getMessage("error.NoFormLoadedNew", null,
					"You have to load a survey before using this page!", locale);
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

		if (accessPrivilege < 2) {
			throw new ForbiddenURLException();
		}

		ModelAndView result = new ModelAndView("management/send-invitations", "form", form);

		result.addObject("serverprefix", host);

		ParticipationGroup participationGroup = participationService.get(Integer.parseInt(id));

		result.addObject("senderSubject",
				participationGroup.getTemplateSubject() != null ? participationGroup.getTemplateSubject()
						: "Invitation");

		Map<Integer, Invitation> invitationsByAttendee = attendeeService
				.getInvitationsByAttendeeForParticipationGroup(participationGroup.getId());

		for (Attendee attendee : participationGroup.getAttendees()) {
			if (invitationsByAttendee.containsKey(attendee.getId())) {
				Invitation invitation = invitationsByAttendee.get(attendee.getId());
				attendee.setInvited(invitation.getInvited());
				attendee.setReminded(invitation.getReminded());
				attendee.setAnswers(invitation.getAnswers());
			}
		}

		for (EcasUser ecasUser : participationGroup.getEcasUsers()) {
			if (invitationsByAttendee.containsKey(ecasUser.getId())) {
				Invitation invitation = invitationsByAttendee.get(ecasUser.getId());
				ecasUser.setInvited(invitation.getInvited());
				ecasUser.setReminded(invitation.getReminded());
				ecasUser.setAnswers(invitation.getAnswers());
			}
		}

		result.addObject("participationGroup", participationGroup);
		result.addObject("usertexts", participationService.getTemplates(user.getId()));

		if (user.getECPrivilege() > 0) {
			result.addObject("allowSenderAddress", true);
		}

		return result;
	}

	private List<EcasUser> getInvalidEcasUsers(int participationGroupId, String selectedAttendee,
			HttpServletRequest request) {
		List<EcasUser> result = new ArrayList<>();

		ParticipationGroup participationGroup = participationService.get(participationGroupId);
		if (participationGroup.getType() == ParticipationGroupType.ECMembers) {
			if (selectedAttendee != null && selectedAttendee.trim().length() > 0) {
				// one participant
				EcasUser ecasUser = participationGroup.getEcasUser(Integer.parseInt(selectedAttendee));

				if (!MailService.isNotEmptyAndValidEmailAddress(ecasUser.getEmail())) {
					// first try to get it from LDAP
					ecasUser.setEmail(ldapService.getEmail(ecasUser.getName()));

					// if not found, send error message to the user in order to remove the
					// participant
					if (!MailService.isNotEmptyAndValidEmailAddress(ecasUser.getEmail())) {
						result.add(ecasUser);
					}
				}
			} else {
				// several participants
				for (String key : Ucs2Utf8.requestToHashMap(request).keySet()) {
					if (key.startsWith("attendee")) {
						int intKey = Integer.parseInt(key.substring(8));
						EcasUser ecasUser = participationGroup.getEcasUser(intKey);

						if (!MailService.isNotEmptyAndValidEmailAddress(ecasUser.getEmail())) {
							// first try to get it from LDAP
							ecasUser.setEmail(ldapService.getEmail(ecasUser.getName()));

							// if not found, send error message to the user in order to remove the
							// participant
							if (!MailService.isNotEmptyAndValidEmailAddress(ecasUser.getEmail())) {
								result.add(ecasUser);
							}
						}
					}
				}
			}
		}

		return result;
	}

	@PostMapping(value = "/sendInvitations")
	public ModelAndView sendInvitationsPOST(@PathVariable String shortname, HttpServletRequest request, Locale locale)
			throws Exception {
		Form form;
		try {
			form = sessionService.getForm(request, shortname, false, false);
		} catch (NoFormLoadedException ne) {
			logger.error(ne);
			ModelAndView model = new ModelAndView("error/generic");
			String message = resources.getMessage("error.NoFormLoadedNew", null,
					"You have to load a survey before using this page!", locale);
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

		if (accessPrivilege < 2) {
			throw new ForbiddenURLException();
		}

		String participationGroupIdString = request.getParameter("participationGroup");
		String mailtemplate = request.getParameter("mailtemplate");
		String mailtext = request.getParameter("mailtext");

		String selectedAttendee = request.getParameter("selectedAttendee");
		String senderAddress = Tools.escapeHTML(request.getParameter("senderAddress"));
		String senderSubject = Tools.escapeHTML(request.getParameter("senderSubject"));

		if (user.getECPrivilege() == 0) {
			senderAddress = user.getEmail();
		}

		if (senderSubject == null || senderSubject.trim().length() == 0) {
			senderSubject = "Invitation";
		}

		String text1 = Tools.filterHTML(request.getParameter("text1"));
		String text2 = Tools.filterHTML(request.getParameter("text2"));

		if (senderAddress == null || senderAddress.trim().length() == 0)
			senderAddress = user.getEmail();
		if (senderAddress == null || senderAddress.trim().length() == 0)
			senderAddress = sender;

		// in case of ec guest lists, first check validity of all email addresses
		int participationGroupId = Integer.parseInt(participationGroupIdString);
		List<EcasUser> invalidEcasUsers = getInvalidEcasUsers(participationGroupId, selectedAttendee, request);
		if (!invalidEcasUsers.isEmpty()) {
			ModelAndView model = new ModelAndView("error/generic");
			String[] args = { invalidEcasUsers.get(0).getName() };
			String message = resources.getMessage("error.InvalidEmailForParticipant", args,
					"The email address of participant {0} is invalid. Please remove that participant before sending the invitations.",
					locale);
			model.addObject("message", message);
			return model;
		}

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
		task.setParticipationGroupId(participationGroupId);
		task.setParameters(Ucs2Utf8.requestToHashMap(request));

		mailService.start(task);

		if (mailtext != null && mailtext.length() > 0) {
			int templateid = Integer.parseInt(mailtext);
			ParticipationGroup group = participationService.get(task.getParticipationGroupId());
			group.setLastUsedTemplateID(templateid);
			participationService.update(group);
		}

		return new ModelAndView(
				"redirect:/" + form.getSurvey().getShortname() + "/management/participants?action=mailsstarted");
	}

	@RequestMapping(value = "/participantsjson", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody List<ParticipationGroup> participantsjson(@PathVariable String shortname,
			HttpServletRequest request) throws Exception {

		User user = sessionService.getCurrentUser(request);

		Form form = sessionService.getFormFromSessionInfo(request);

		sessionService.upgradePrivileges(form.getSurvey(), user, request);
		int accessPrivilege = 0;
		if (form.getSurvey().getOwner().getId().equals(user.getId())) {
			accessPrivilege = 2;
		} else if (user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2) {
			accessPrivilege = 2;
		} else {
			accessPrivilege = user.getLocalPrivileges().get(LocalPrivilege.ManageInvitations);
		}

		if (accessPrivilege < 1) {
			throw new ForbiddenURLException();
		}

		List<ParticipationGroup> participationGroups = participationService.getAll(form.getSurvey().getUniqueId(), true,
				0, Integer.MAX_VALUE);
		for (ParticipationGroup group : participationGroups) {
			if (group.getType() == ParticipationGroupType.ECMembers) {
				group.setChildren(group.getEcasUsers().size());
				group.setEcasUsers(null);
			} else if (group.getType() == ParticipationGroupType.Static) {
				group.setChildren(group.getAttendees().size());
				group.setAttendees(null);
			} else {
				group.setChildren(group.getAll());
			}
		}

		return participationGroups;
	}

	@GetMapping(value = "/participantsJSONAll", headers = "Accept=*/*")
	public @ResponseBody Paging<Attendee> participantsSearchAll(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return participantsSearch(request, response, true);
	}

	@GetMapping(value = "/participantsJSON", headers = "Accept=*/*")
	public @ResponseBody Paging<Attendee> participantsSearch(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return participantsSearch(request, response, false);
	}

	@GetMapping(value = "/usersJSON", headers = "Accept=*/*")
	public @ResponseBody Paging<EcasUser> usersJSON(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		User user = sessionService.getCurrentUser(request);
		Form form = sessionService.getFormFromSessionInfo(request);

		sessionService.upgradePrivileges(form.getSurvey(), user, request);
		int accessPrivilege = 0;
		if (form.getSurvey().getOwner().getId().equals(user.getId())) {
			accessPrivilege = 2;
		} else if (user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2) {
			accessPrivilege = 2;
		} else {
			accessPrivilege = user.getLocalPrivileges().get(LocalPrivilege.ManageInvitations);
		}

		if (accessPrivilege < 1) {
			throw new ForbiddenURLException();
		}

		Map<String, String[]> parameters = Ucs2Utf8.requestToHashMap(request);
		String name = "";
		if (parameters.containsKey("name"))
			name = parameters.get("name")[0];
		String email = "";
		if (parameters.containsKey("email"))
			email = parameters.get("email")[0];
		String department = "";
		if (parameters.containsKey("department"))
			department = parameters.get("department")[0];
		String domain = "";
		if (parameters.containsKey("domain"))
			domain = parameters.get("domain")[0];

		String newPage = request.getParameter("newPage");
		newPage = newPage == null ? "1" : newPage;
		Integer itemsPerPage = ConversionTools.getInt(request.getParameter("itemsPerPage"), 100);

		if (domain.length() == 0)
			return null;

		Paging<EcasUser> paging = new Paging<>();
		paging.setItemsPerPage(itemsPerPage);
		paging.setCurrentPage(Integer.parseInt(newPage));

		List<EcasUser> users = ldapDBService.getECASUsers(name, department, email, domain, Integer.parseInt(newPage),
				itemsPerPage);
		paging.setItems(users);

		return paging;
	}
	
	@GetMapping(value = "/topDepartmentsJSON", headers="Accept=*/*")
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

	@GetMapping(value = "/departmentsJSON", headers = "Accept=*/*")
	public @ResponseBody List<KeyValue> departments(HttpServletRequest request, HttpServletResponse response) {
		return ldapService.getDepartments(request.getParameter("term"));
  }

	@PostMapping(value = "/saveTemplateJSON", headers = "Accept=*/*")
	public @ResponseBody String saveTemplateJSON(HttpServletRequest request, HttpServletResponse response) {
		try {
			String text1 = Tools.filterHTML(Ucs2Utf8.unconvert(request.getParameter("text1")));
			String text2 = Tools.filterHTML(Ucs2Utf8.unconvert(request.getParameter("text2")));
			String subject = Tools.filterHTML(Ucs2Utf8.unconvert(request.getParameter("subject")));
			String template = request.getParameter("template");
			String texttemplate = request.getParameter("texttemplate");
			String name = request.getParameter("name");
			String replyto = request.getParameter("replyto");

			User user = sessionService.getCurrentUser(request);
			if (user == null)
				return "ERROR";

			InvitationTemplate existing = participationService.getTemplateByName(name, user.getId());
			if (existing != null)
				return "EXISTS";

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
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return "ERROR";
	}

	@GetMapping(value = "/loadTemplateJSON", headers = "Accept=*/*")
	public @ResponseBody InvitationTemplate loadTemplateJSON(HttpServletRequest request, HttpServletResponse response) {
		try {

			String id = request.getParameter("id");

			User user = sessionService.getCurrentUser(request);
			if (user == null)
				return null;

			InvitationTemplate existing = participationService.getTemplate(Integer.parseInt(id));
			if (existing == null)
				return null;

			if (!existing.getOwner().getId().equals(user.getId())) {
				return null;
			}

			return existing;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	@GetMapping(value = "/deleteTemplateJSON", headers = "Accept=*/*")
	public @ResponseBody String deleteTemplateJSON(HttpServletRequest request, HttpServletResponse response) {
		try {

			String id = request.getParameter("id");

			User user = sessionService.getCurrentUser(request);
			if (user == null)
				return null;

			InvitationTemplate existing = participationService.getTemplate(Integer.parseInt(id));
			if (existing == null)
				return null;

			if (!existing.getOwner().getId().equals(user.getId())) {
				return null;
			}

			participationService.delete(existing);

			return "OK";
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return "ERROR";
	}

	private @ResponseBody Paging<Attendee> participantsSearch(HttpServletRequest request, HttpServletResponse response,
			boolean all) throws Exception {

		User user = sessionService.getCurrentUser(request);
		int owner = user.getId();

		Map<String, String[]> parameters = Ucs2Utf8.requestToHashMap(request);
		String name = "";
		if (parameters.containsKey("name"))
			name = parameters.get("name")[0];
		String email = "";
		if (parameters.containsKey("email"))
			email = parameters.get("email")[0];
		HashMap<String, String> attributeFilter = new HashMap<>();
		if (name.length() > 0)
			attributeFilter.put("name", name);
		if (email.length() > 0)
			attributeFilter.put("email", email);

		if (user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) == 2) {
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

		for (Entry<String, String[]> entry : parameters.entrySet()) {
			try {
				if (entry.getKey().equals("owner") && entry.getValue() != null && entry.getValue().length > 0) {
					String[] value = entry.getValue();
					if (value[0] != null && value[0].length() > 0) {
						attributeFilter.put(entry.getKey(), entry.getValue()[0]);
					}
				} else {
					int intKey = Integer.parseInt(entry.getKey());

					if (intKey > 0 && entry.getValue() != null && entry.getValue().length > 0
							&& !entry.getKey().equalsIgnoreCase("name") && !entry.getKey().equalsIgnoreCase("email")
							&& !entry.getKey().equalsIgnoreCase("newPage")) {
						String[] value = entry.getValue();
						if (value[0] != null && value[0].length() > 0) {
							attributeFilter.put(entry.getKey(), entry.getValue()[0]);
						}
					}
				}
			} catch (Exception e) {
				// ignore
			}
		}

		List<Attendee> attendees = attendeeService.getAttendees(owner, attributeFilter, paging.getCurrentPage(),
				paging.getItemsPerPage());

		paging.setItems(attendees);

		return paging;
	}

	@PostMapping(value = "/participants")
	public ModelAndView participantsPOST(@PathVariable String shortname, HttpServletRequest request, Locale locale)
			throws Exception {
		Form form;
		try {
			form = sessionService.getForm(request, shortname, false, false);
		} catch (NoFormLoadedException ne) {
			logger.error(ne);
			ModelAndView model = new ModelAndView("error/generic");
			String message = resources.getMessage("error.NoFormLoadedNew", null,
					"You have to load a survey before using this page!", locale);
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

		if (accessPrivilege < 2) {
			throw new ForbiddenURLException();
		}

		int owner = user.getId();

		Map<String, String[]> parameters = Ucs2Utf8.requestToHashMap(request);
		String name = "";
		if (parameters.containsKey("name"))
			name = parameters.get("name")[0];
		String email = "";
		if (parameters.containsKey("email"))
			email = parameters.get("email")[0];
		HashMap<String, String> attributeFilter = new HashMap<>();
		AttendeeFilter filter = new AttendeeFilter();

		if (user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) < 2) {
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

		String groupName = parameters.get("groupName")[0];
		String groupOwner = parameters.get("groupOwner")[0];
		String groupType = parameters.get("groupType")[0];

		parameters.remove("groupName");
		parameters.remove("groupOwner");
		parameters.remove("groupType");
		parameters.remove("name");
		parameters.remove("email");
		parameters.remove("newPage");

		if (groupType.equalsIgnoreCase("dynamic")) {

			for (Entry<String, String[]> entry : parameters.entrySet()) {
				if (!entry.getKey().equalsIgnoreCase("id")) {
					int intKey = Integer.parseInt(entry.getKey());

					if (intKey > 0 && entry.getValue() != null && entry.getValue().length > 0) {
						String[] value = entry.getValue();
						if (value[0] != null && value[0].length() > 0)
							attributeFilter.put(entry.getKey(), entry.getValue()[0]);
						Attribute attribute = new Attribute();
						AttributeName attributeName = attendeeService.getAttributeName(intKey);
						attribute.setAttributeName(attributeName);
						attribute.setValue(entry.getValue()[0]);
						filter.getAttributes().add(attribute);
					}
				}
			}

			List<Attendee> attendees = attendeeService.getAttendees(owner, attributeFilter, 1, Integer.MAX_VALUE);
			ParticipationGroup g = new ParticipationGroup(form.getSurvey().getUniqueId());

			if (parameters.containsKey("id")) {
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

			if (groupOwner != null && groupOwner.length() > 0 && !groupOwner.equalsIgnoreCase(user.getLogin())) {
				// this feature is not used yet
			} else {
				g.setOwnerId(user.getId());
			}

			participationService.save(g);

		}

		return participants(shortname, request, locale);
	}

	@RequestMapping(value = "/participants/createTokens", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody List<Invitation> createTokens(@PathVariable String shortname, HttpServletRequest request,
			Locale locale) {
		String strtokens = request.getParameter("tokens");
		int tokens = Integer.parseInt(strtokens);

		List<Invitation> newtokens = new ArrayList<>();

		for (int i = 0; i < tokens; i++) {
			String token = UUID.randomUUID().toString();
			Invitation inv = new Invitation();
			inv.setUniqueId(token);

			newtokens.add(inv);
		}

		return newtokens;
	}
}
