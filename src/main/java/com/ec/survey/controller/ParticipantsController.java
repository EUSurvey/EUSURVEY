package com.ec.survey.controller;

import com.ec.survey.exception.*;
import com.ec.survey.model.*;
import com.ec.survey.model.administration.EcasUser;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.LocalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.administration.Voter;
import com.ec.survey.model.attendees.*;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.*;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.Tools;
import com.ec.survey.tools.Ucs2Utf8;

import com.ec.survey.tools.activity.ActivityRegistry;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/{shortname}/management")
public class ParticipantsController extends BasicController {

	private @Value("${server.prefix}") String host;
	private @Value("${smtpserver}") String smtpServer;
	private @Value("${smtp.port}") String smtpPort;
	private @Value("${participant.default.domain:@null}") String defaultDomain;
	private @Value("${monitoring.recipient}") String monitoringEmail;

	private @Value("${participants.warning.size:500}") int warningSizeGuestList;
	private @Value("${participants.warning.count:10}") int warningCountGuestLists;

	@Resource(name = "ecService")
	private ECService ecService;
	
	@Resource(name = "mailService")
	private MailService mailService;
	
	@Resource(name = "eVoteService")
	private EVoteService eVoteService;

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
		String enableEUGuestList = settingsService.get(Setting.EnableEUGuestList);
		result.addObject("enableEUGuestList", enableEUGuestList.equalsIgnoreCase("true"));
		result.addObject("useUILanguage", true);

		String name = request.getParameter("name");
		String email = request.getParameter(Constants.EMAIL);
		HashMap<String, String> filter = new HashMap<>();
		if (name != null)
			filter.put("name", name);
		if (email != null)
			filter.put(Constants.EMAIL, email);

		if (u.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) == 2) {
			owner = 0;
		}
		int numberOfAttendees = attendeeService.getNumberOfAttendees(owner, filter);
		result.addObject("numberOfAttendees", numberOfAttendees);
		result.addObject("attributeNames", u.getSelectedAttributes());
		result.addObject(Constants.FILTER, filter);
		result.addObject("allAttributeNames", attendeeService.getAllAttributes(owner));
		if (request.getParameter("action") != null) {
			result.addObject("action", request.getParameter("action"));
		}

		if (request.getParameter(Constants.ERROR) != null) {
			result.addObject(Constants.ERROR, request.getParameter(Constants.ERROR));
		}

		List<KeyValue> domains = ldapDBService.getDomains(false, false, resources, locale);

		if (survey.getIsEVote()){
			//Remove all domains that don't start with ec.europa
			domains = domains.stream().filter(kV -> kV.getKey().startsWith("ec.europa")).collect(Collectors.toList());
		}

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
				activityService.log(ActivityRegistry.ID_GUEST_LIST_STARTED, null, g.getId().toString(), u.getId(), survey.getUniqueId(), g.getNiceType());
				break;
			case "deactivate":
				g.setActive(false);
				participationService.update(g);
				activityService.log(ActivityRegistry.ID_GUEST_LIST_PAUSED, null, g.getId().toString(), u.getId(), survey.getUniqueId(), g.getNiceType());
				break;
			case "delete":
				if (g.getType() == ParticipationGroupType.VoterFile) {
					eVoteService.deleteAllVoters(survey.getUniqueId());
				}
				
				participationService.delete(g);
				activityService.log(ActivityRegistry.ID_GUEST_LIST_REMOVED, g.getId().toString(), null, u.getId(), survey.getUniqueId(), g.getNiceType());
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

		int first = (newPage > 1 ? newPage - 1 : 0) * itemsPerPage;
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

			return result;
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
	public @ResponseBody String saveguestlist(@RequestBody Map json, HttpServletRequest request,
			Locale locale) {
		try {
			User user = sessionService.getCurrentUser(request);
			Form form = sessionService.getFormFromSessionInfo(request);
			ParticipationGroup participationGroup = new ParticipationGroup(form.getSurvey().getUniqueId());

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
				participationGroup = participationService.get(id);
			} else {
				participationGroup.setActive(true);
				participationGroup.setSurveyId(form.getSurvey().getId());
			}

			participationGroup.setName(json.get("name").toString());
			participationGroup.setType(ParticipationGroupType.valueOf(json.get("type").toString()));

			List<Integer> attendeeIDs = null;
			List<Integer> userIDs = null;
			List<String> tokens = null;
			List<String> deactivatedTokens = null;
			int size = 0;

			if (participationGroup.getType() == ParticipationGroupType.Static) {
				ArrayList<LinkedHashMap> attendees = (ArrayList) json.get("attendees");
				attendeeIDs = new ArrayList<>();

				if (user.isExternal()) {

					if (form.getSurvey().getValidator() != null && form.getSurvey().getValidated() && form.getSurvey().getOrganisation() != null) {
						logger.info("limits of external users skipped as the survey was created on behalf of an institution");
					} else {

						String limit = settingsService.get(Setting.ContactGuestlistLimitForExternals);
						if (limit != null && Tools.isInteger(limit)) {
							if (id == 0 && participationService.getContactGuestlistCount(form.getSurvey().getUniqueId()) >= Integer.parseInt(limit)) {
								return "External users can only create a maximum of " + limit + " contact guest lists.";
							}
						}

						limit = settingsService.get(Setting.ContactGuestlistSizeLimitForExternals);
						if (limit != null && Tools.isInteger(limit)) {
							if (attendees.size() > Integer.parseInt(limit)) {
								return "External users can only create contact guest list with a maximum of " + limit + " contacts.";
							}
						}

					}
				}

				for (int i = 0; i < attendees.size(); i++) {
					LinkedHashMap attendee = attendees.get(i);
					attendeeIDs.add((int) attendee.get("id"));
				}

				size = attendeeIDs.size();

			} else if (participationGroup.getType() == ParticipationGroupType.Token) {
				ArrayList<LinkedHashMap> invitations = (ArrayList) json.get("tokens");
				tokens = new ArrayList<>();
				deactivatedTokens = new ArrayList<>();
				for (int i = 0; i < invitations.size(); i++) {
					LinkedHashMap token = invitations.get(i);
					tokens.add(token.get("uniqueId").toString());
					
					if (token.get("deactivated").toString().equalsIgnoreCase("true")) {
						deactivatedTokens.add(token.get("uniqueId").toString());
					}
				}
				size = tokens.size();
			} else if (participationGroup.getType() == ParticipationGroupType.ECMembers) {
				ArrayList<LinkedHashMap> users = (ArrayList) json.get("users");
				userIDs = new ArrayList<>();
				for (int i = 0; i < users.size(); i++) {
					LinkedHashMap ecasuser = users.get(i);
					userIDs.add((int) ecasuser.get("id"));
				}
				size = userIDs.size();
			}
			participationGroup.setOwnerId(user.getId());
			participationGroup.setInCreation(true);

			participationService.save(participationGroup);
			if (participationGroup.getType() == ParticipationGroupType.Static) {
				participationService.addParticipantsToGuestListAsync(participationGroup.getId(), attendeeIDs);
			} else if (participationGroup.getType() == ParticipationGroupType.Token) {
				participationService.addTokensToGuestListAsync(participationGroup.getId(), tokens, deactivatedTokens);
			} else if (participationGroup.getType() == ParticipationGroupType.ECMembers) {
				participationService.addUsersToGuestListAsync(participationGroup.getId(), userIDs);
			}

			CheckForSuspiciousGuestList(size, form.getSurvey(), user);

			if (id > 0) {
				activityService.log(ActivityRegistry.ID_GUEST_LIST_MODIFIED, null, participationGroup.getId().toString(), user.getId(), form.getSurvey().getUniqueId(),
						participationGroup.getNiceType());
				return "successsaved";
			} else {
				activityService.log(ActivityRegistry.ID_GUEST_LIST_CREATED, null, participationGroup.getId().toString(), user.getId(), form.getSurvey().getUniqueId(),
						participationGroup.getNiceType());
				return "successcreated";
			}

			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return resources.getMessage("error.OperationFailed", null,
				"There was a problem during execution of the operation. The error was logged. Please contact support if the problem occurs again.",
				locale);
	}

	private void CheckForSuspiciousGuestList(int listSize, Survey survey, User user) {
		if (listSize > warningSizeGuestList) {
			SendGuestListWarningMail("A user has created a very large guest list for their survey: " + listSize + " entries", "Very large guest list", survey, user);
		}

		var listCount = participationService.getParticipationGroupCount(survey.getUniqueId());
		if (listCount > warningCountGuestLists) {
			SendGuestListWarningMail("A user has created a large amount of guest lists for their survey: " + listCount + " lists", "Many guest lists", survey, user);
		}
	}

	private void SendGuestListWarningMail(String title, String subject, Survey survey, User user) {
		var mailBody = "<b>SECURITY ALERT</b><br />" +
				"<br />" +
				title + "<br />" +
				"<br />" +
				"Runner: <a href=\"" + host	+ "runner/" + survey.getShortname() + "\">" + host + "runner/" + survey.getShortname() + "</a><br />" +
				"<br />" +
				"<table>" +
				"<tr><td><b>Username:</b></td>" + "<td style='padding-left: 10px;'>" + user.getLogin() + "</td></tr>" +
				"<tr><td><b>First name:</b></td>" + "<td style='padding-left: 10px;'>" + user.getGivenName() + "</td></tr>" +
				"<tr><td><b>Surname:</b></td>" + "<td style='padding-left: 10px;'>" + user.getSurName() + "</td></tr>" +
				"<tr><td><b>Email address:</b></td>" + "<td style='padding-left: 10px;'>" + user.getEmail() + "</td></tr>" +
				"<tr><td><b>Survey Title:</b></td>" + "<td style='padding-left: 10px;'>" + survey.cleanTitle() + "</td></tr>" +
				"<tr><td><b>Survey Alias:</b></td>" + "<td style='padding-left: 10px;'>" + survey.getShortname() + "</td></tr>" +
				"<tr><td><b>Survey UID:</b></td>" + "<td style='padding-left: 10px;'>" + survey.getUniqueId() + "</td></tr>" +
				"</table><br />" +
				"Administration: <a href=\"" + host	+ survey.getShortname() + "/management/overview\">" + host + survey.getShortname() + "/management/overview</a><br />";

		try {
			var mail = mailService.getEUSurveyMailTemplate(mailBody);
			mailService.SendHtmlMail(monitoringEmail, sender, sender, subject, mail, null);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
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
							participants = Constants.ERROR + g.getError();
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
			ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
			String message = resources.getMessage("error.NoFormLoadedNew", null,
					"You have to load a survey before using this page!", locale);
			model.addObject(Constants.MESSAGE, message);
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

		var canSendLinks = !user.isExternal();
		if (!canSendLinks) {
			result.addObject("noInviteLinks", true);
		}

		if (user.getECPrivilege() > 0) {
			result.addObject("allowSenderAddress", true);
		}

		return result;
	}

	private List<EcasUser> getInvalidEcasUsers(int participationGroupId, String selectedAttendee,
			HttpServletRequest request) throws NamingException {
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
			ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
			String message = resources.getMessage("error.NoFormLoadedNew", null,
					"You have to load a survey before using this page!", locale);
			model.addObject(Constants.MESSAGE, message);
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

		var canSendLinks = !user.isExternal();

		String text1 = Tools.filterHTML(request.getParameter("text1"), canSendLinks);
		String text2 = Tools.filterHTML(request.getParameter("text2"), canSendLinks);

		if (!canSendLinks) {
			var linksA = "([^\\s<>]+://)[^\\s<>]+"; //Matches links like https://localhost or http://google.com
			var linksB = "([^.<>\\s]+\\.)+\\w\\w+"; //Matches links like search.google.de
			text1 = text1.replaceAll(linksA, "");
			text1 = text1.replaceAll(linksB, "");

			text2 = text2.replaceAll(linksA, "");
			text2 = text2.replaceAll(linksB, "");

			senderSubject = senderSubject.replaceAll(linksA, "");
			senderSubject = senderSubject.replaceAll(linksB, "");
		}

		if (senderAddress == null || senderAddress.trim().length() == 0)
			senderAddress = user.getEmail();
		if (senderAddress == null || senderAddress.trim().length() == 0)
			senderAddress = sender;

		// in case of ec guest lists, first check validity of all email addresses
		int participationGroupId = Integer.parseInt(participationGroupIdString);
		List<EcasUser> invalidEcasUsers = getInvalidEcasUsers(participationGroupId, selectedAttendee, request);
		if (!invalidEcasUsers.isEmpty()) {
			ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
			String[] args = { invalidEcasUsers.get(0).getName() };
			String message = resources.getMessage("error.InvalidEmailForParticipant", args,
					"The email address of participant {0} is invalid. Please remove that participant before sending the invitations.",
					locale);
			model.addObject(Constants.MESSAGE, message);
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
			} else if (group.getType() == ParticipationGroupType.VoterFile) {
				group.setChildren((int) eVoteService.getVoterCount(form.getSurvey().getUniqueId(), null));
				group.setInvited((int) eVoteService.getVoterCount(form.getSurvey().getUniqueId(), true));
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
		if (parameters.containsKey(Constants.EMAIL))
			email = parameters.get(Constants.EMAIL)[0];
		String department = "";
		if (parameters.containsKey("department"))
			department = parameters.get("department")[0];
		String domain = "";
		if (parameters.containsKey("domain"))
			domain = parameters.get("domain")[0];
		String login = "";
		if (parameters.containsKey("login"))
			login = parameters.get("login")[0];

		String newPage = request.getParameter("newPage");
		newPage = newPage == null ? "1" : newPage;
		Integer itemsPerPage = ConversionTools.getInt(request.getParameter("itemsPerPage"), 100);

		if (domain.length() == 0)
			return null;
		if (form.getSurvey().getIsEVote() && !domain.startsWith("ec.europa")){
			return null;
		}

		Paging<EcasUser> paging = new Paging<>();
		paging.setItemsPerPage(itemsPerPage);
		paging.setCurrentPage(Integer.parseInt(newPage));

//		List<EcasUser> users = ldapDBService.getECASUsers(name, login, department, email, domain, Integer.parseInt(newPage),
//				itemsPerPage);
//		paging.setItems(users);

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
		String term = request.getParameter("term");
		Boolean isDGs = request.getParameter("isdgs").equalsIgnoreCase("true");
		return ecService.GetEntities(term, isDGs);
  }

	@PostMapping(value = "/saveTemplateJSON", headers = "Accept=*/*")
	public @ResponseBody String saveTemplateJSON(HttpServletRequest request, HttpServletResponse response) {
		try {
			String text1 = Tools.filterHTML(Ucs2Utf8.unconvert(request.getParameter("text1"), request.getCharacterEncoding()));
			String text2 = Tools.filterHTML(Ucs2Utf8.unconvert(request.getParameter("text2"), request.getCharacterEncoding()));
			String subject = Tools.filterHTML(Ucs2Utf8.unconvert(request.getParameter("subject"), request.getCharacterEncoding()));
			String template = request.getParameter("template");
			String texttemplate = request.getParameter("texttemplate");
			String name = request.getParameter("name");
			String replyto = request.getParameter("replyto");

			User user = sessionService.getCurrentUser(request);
			if (user == null)
				return Constants.ERROR;

			InvitationTemplate existing = participationService.getTemplateByName(name, user.getId());
			if (existing != null)
				return "exists";

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
		return Constants.ERROR;
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

			return "ok";
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return Constants.ERROR;
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
		if (parameters.containsKey(Constants.EMAIL))
			email = parameters.get(Constants.EMAIL)[0];
		HashMap<String, String> attributeFilter = new HashMap<>();
		if (name.length() > 0)
			attributeFilter.put("name", name);
		if (email.length() > 0)
			attributeFilter.put(Constants.EMAIL, email);

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
							&& !entry.getKey().equalsIgnoreCase("name") && !entry.getKey().equalsIgnoreCase(Constants.EMAIL)
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
			ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
			String message = resources.getMessage("error.NoFormLoadedNew", null,
					"You have to load a survey before using this page!", locale);
			model.addObject(Constants.MESSAGE, message);
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
		if (parameters.containsKey(Constants.EMAIL))
			email = parameters.get(Constants.EMAIL)[0];
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
			attributeFilter.put(Constants.EMAIL, email);
			filter.setEmail(email);
		}

		String groupName = parameters.get("groupName")[0];
		String groupOwner = parameters.get("groupOwner")[0];
		String groupType = parameters.get("groupType")[0];

		parameters.remove("groupName");
		parameters.remove("groupOwner");
		parameters.remove("groupType");
		parameters.remove("name");
		parameters.remove(Constants.EMAIL);
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
	
	@RequestMapping(value = "/votersJSON", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody List<Voter> votersJSON(@PathVariable String shortname, HttpServletRequest request) {

		try {
			String page = request.getParameter("page");

			if (page == null) {
				return null;
			}
			
			String user = request.getParameter("user");
			String first = request.getParameter("first");
			String last = request.getParameter("last");
			String svoted = request.getParameter("voted");
			Boolean voted = svoted == null ? null : Boolean.parseBoolean(svoted);
			
			request.getSession().setAttribute("VotersUserFilter", user);
			request.getSession().setAttribute("VotersFirstFilter", first);
			request.getSession().setAttribute("VotersLastFilter", last);
			request.getSession().setAttribute("VotersVotedFilter", voted);
			
			Form form = sessionService.getForm(request, null, false, false);
			User u = sessionService.getCurrentUser(request);
			sessionService.upgradePrivileges(form.getSurvey(), u, request);

			if (!u.getId().equals(form.getSurvey().getOwner().getId())
					&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.ManageInvitations) < 1) {
				throw new ForbiddenURLException();
			}

			int itemsPerPage = 20;
			
			List<Voter> result = eVoteService.getVoters(form.getSurvey().getUniqueId(), Integer.parseInt(page), itemsPerPage, user, first, last, voted);
			
			return result;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;
	}
	
	@RequestMapping(value = "/totalVotersJSON", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody int totalVotersJSON(@PathVariable String shortname, HttpServletRequest request) {
		try {
			Form form = sessionService.getForm(request, null, false, false);
			User u = sessionService.getCurrentUser(request);
			sessionService.upgradePrivileges(form.getSurvey(), u, request);

			if (!u.getId().equals(form.getSurvey().getOwner().getId())
					&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.ManageInvitations) < 1) {
				throw new ForbiddenURLException();
			}
			
			String user = request.getParameter("user");
			String first = request.getParameter("first");
			String last = request.getParameter("last");
			String svoted = request.getParameter("voted");
			Boolean voted = svoted == null ? null : Boolean.parseBoolean(svoted);
			
			int result = (int) eVoteService.getVoterCount(form.getSurvey().getUniqueId(), user, first, last, voted);
			
			return result;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return 0;
	}
	
	@PostMapping(value = "/uploadvoterfile")
	public @ResponseBody List<Voter> uploadvoterfile(@PathVariable String shortname, HttpServletRequest request, HttpServletResponse response) {
		InputStream is = null;
		
		try {

			if (request instanceof DefaultMultipartHttpServletRequest) {
				DefaultMultipartHttpServletRequest r = (DefaultMultipartHttpServletRequest) request;
				is = r.getFile("qqfile").getInputStream();
			} else {
				is = request.getInputStream();
			}

			Form form = sessionService.getForm(request, null, false, false);
			User u = sessionService.getCurrentUser(request);
			sessionService.upgradePrivileges(form.getSurvey(), u, request);

			if (!u.getId().equals(form.getSurvey().getOwner().getId())
					&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.ManageInvitations) < 2) {
				throw new ForbiddenURLException();
			}
			
			ArrayList<Voter> voters = eVoteService.importVoterFile(form.getSurvey().getUniqueId(), is);
			if (voters.size() > 0) {
				eVoteService.addVoters(voters, u);
				List<Voter> firstVoterPage = eVoteService.getVoters(form.getSurvey().getUniqueId(), 1, 20, null, null, null, null);
				response.setStatus(HttpServletResponse.SC_OK);
				return firstVoterPage;
			}
			
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);			
		
		} catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error(ex.getMessage(), ex);
		} finally {
			try {
				is.close();
			} catch (IOException ignored) {
				// ignore
			}
		}

		return null;
	}

	@RequestMapping(value = "/emptyvoterfile", method = { RequestMethod.GET, RequestMethod.HEAD })
	@ResponseBody
	public ResponseEntity<byte[]> emptyvoterfile(HttpServletRequest request, HttpServletResponse response) {

		final HttpHeaders headers = new HttpHeaders();

		try {

			byte[] file = eVoteService.exportVoterFile(new LinkedList<>());

			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment;filename=voterfile.xlsx");
			response.setContentLength(file.length);
			response.getOutputStream().write(file);
			response.getOutputStream().flush();
			response.getOutputStream().close();

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			headers.setContentType(MediaType.TEXT_PLAIN);
			return new ResponseEntity<>(e.getLocalizedMessage().getBytes(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return null;
	}
	
	@PostMapping(value = "/deleteVoter")
	public @ResponseBody String deleteVoter(@PathVariable String shortname, @RequestParam  String id, HttpServletRequest request, HttpServletResponse response) {
		try {
			Form form = sessionService.getForm(request, null, false, false);
			User u = sessionService.getCurrentUser(request);
			sessionService.upgradePrivileges(form.getSurvey(), u, request);

			if (!u.getId().equals(form.getSurvey().getOwner().getId())
					&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.ManageInvitations) < 2) {
				throw new ForbiddenURLException();
			}			
			
			if (eVoteService.deleteVoter(Integer.parseInt(id), form.getSurvey().getUniqueId(), u)) {
				response.setStatus(HttpServletResponse.SC_OK);
				return "success";
			}
		} catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error(ex.getMessage(), ex);
		}
			
		return "error";
	}

	@PostMapping(value = "/addVoters")
	public @ResponseBody List<Voter> addVoters(@PathVariable String shortname, @RequestParam(name = "voters[]") List<Integer> voters, HttpServletRequest request, HttpServletResponse response) {
		try {
			Form form = sessionService.getForm(request, null, false, false);
			User u = sessionService.getCurrentUser(request);
			sessionService.upgradePrivileges(form.getSurvey(), u, request);

			if (!u.getId().equals(form.getSurvey().getOwner().getId())
					&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.ManageInvitations) < 2) {
				throw new ForbiddenURLException();
			}

			if (voters.size() > 0) {
				String uid = form.getSurvey().getUniqueId();

				List<EcasUser> users = ldapDBService.getExclusiveECASVoteUsersWithIds(uid, voters);
				//The query already ignores duplicate voters
				LinkedList<Voter> votersList = new LinkedList<>();

				for (EcasUser user : users){
					if (user.getOrganisation().startsWith("ec.europa")){
						Voter voter = new Voter();
						voter.setEcMoniker(user.getEcMoniker());
						voter.setGivenName(user.getGivenName());
						voter.setSurname(user.getSurname());
						voter.setSurveyUid(uid);
						voter.setCreated(new Date());
						votersList.add(voter);
					}
				}

				if (votersList.size() > 0) {
					eVoteService.addMoreVoters(votersList, u);
				}
				List<Voter> firstVoterPage = eVoteService.getVoters(form.getSurvey().getUniqueId(), 1, 20, null, null, null, null);
				response.setStatus(HttpServletResponse.SC_OK);
				return firstVoterPage;
			}

			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}

		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		return null;
	}
	
}
