package com.ec.survey.controller;

import com.ec.survey.exception.ForbiddenURLException;
import com.ec.survey.exception.MessageException;
import com.ec.survey.model.KeyValue;
import com.ec.survey.model.WhitelistLabel;
import com.ec.survey.model.WhitelistedUser;
import com.ec.survey.model.administration.Organisation;
import com.ec.survey.model.administration.Role;
import com.ec.survey.model.administration.User;
import com.ec.survey.service.OrganisationService;
import com.ec.survey.service.UtilsService;
import com.ec.survey.tools.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/organisationmanagement")
public class OrganisationManagementController extends BasicController {

	@Resource(name = "organisationService")
	private OrganisationService organisationService;

	@Resource(name = "utilsService")
	private UtilsService utilsService;

	@GetMapping(value = "/whitelist")
	public ModelAndView whitelist(HttpServletRequest request, Locale locale) throws NotAgreedToPsException, NotAgreedToTosException, WeakAuthenticationException, ForbiddenURLException {
		User user = sessionService.getCurrentUser(request);

		if (user.getOrganisationPrivilege() < 1) {
			throw new ForbiddenURLException();
		}

		Map<String, String> allOrganisations =  utilsService.getAllOrganisations(locale);
		List<Organisation> lstOrganisations = organisationService.getOrganisationsForManager(user.getId());
		Map<String, String> organisations = new HashMap<>();
		for (Organisation organisation : lstOrganisations) {
			if (allOrganisations.containsKey(organisation.getOrganisation())) {
				organisations.put(organisation.getOrganisation(), allOrganisations.get(organisation.getOrganisation()));
			} else {
				organisations.put(organisation.getOrganisation(), organisation.getOrganisation());
			}
		}

		var result = new ModelAndView("organisationmanagement/whitelist", "organisations", organisations);

		List<KeyValue> domains = ldapDBService.getDomains(true, true, resources, locale);
		result.addObject("domains", domains);

		return result;
	}

	@PostMapping(value = "/whitelist/addlabel")
	public @ResponseBody String addlabel(HttpServletRequest request) throws NotAgreedToPsException, NotAgreedToTosException, WeakAuthenticationException, ForbiddenURLException {
		User user = sessionService.getCurrentUser(request);

		if (user.getOrganisationPrivilege() < 1) {
			throw new ForbiddenURLException();
		}

		int userid = Integer.parseInt(request.getParameter("user"));
		String label = request.getParameter("label");

		var organisation = organisationService.getOrganisationForWhitelistedUser(userid);
		if (organisation.getManagers().stream().noneMatch(m -> Objects.equals(m.getId(), user.getId()))) {
			throw new ForbiddenURLException();
		}

		var whitelistedUser = organisation.getWhitelist().stream().filter(u -> Objects.equals(u.getId(), userid)).findFirst().get();
		if (whitelistedUser.getLabels().stream().anyMatch(l -> l.getName().equals(label))) {
			return "exists";
		}

		organisationService.addLabel(whitelistedUser, label);

		return "success";
	}

	@PostMapping(value = "/whitelist/removelabel")
	public @ResponseBody String removelabel(HttpServletRequest request) throws NotAgreedToPsException, NotAgreedToTosException, WeakAuthenticationException, ForbiddenURLException {
		User user = sessionService.getCurrentUser(request);

		if (user.getOrganisationPrivilege() < 1) {
			throw new ForbiddenURLException();
		}

		int userid = Integer.parseInt(request.getParameter("user"));
		int labelid = Integer.parseInt(request.getParameter("label"));

		var organisation = organisationService.getOrganisationForWhitelistedUser(userid);
		if (organisation.getManagers().stream().noneMatch(m -> Objects.equals(m.getId(), user.getId()))) {
			throw new ForbiddenURLException();
		}

		var whitelistedUser = organisation.getWhitelist().stream().filter(u -> Objects.equals(u.getId(), userid)).findFirst().get();
		if (whitelistedUser.getLabels().stream().noneMatch(l -> l.getId().equals(labelid))) {
			return "error";
		}

		organisationService.removeLabel(whitelistedUser, labelid);

		return "success";
	}

	@PostMapping(value = "/organisation/{code}")
	public @ResponseBody List<WhitelistedUser> organisation(HttpServletRequest request, @PathVariable String code) throws ForbiddenURLException, NotAgreedToPsException, NotAgreedToTosException, WeakAuthenticationException {
		User user = sessionService.getCurrentUser(request);

		if (user.getOrganisationPrivilege() < 1) {
			throw new ForbiddenURLException();
		}

		List<Organisation> lstOrganisations = organisationService.getOrganisationsForManager(user.getId());
		var organisation = organisationService.getOrganisation(code);

		if (organisation == null || !lstOrganisations.contains(organisation)) throw new ForbiddenURLException();

		var result = new ArrayList<WhitelistedUser>();

		var username = request.getParameter("username");
		var email = request.getParameter("email");
		var label = request.getParameter("label");
		var from = request.getParameter("from");
		var to = request.getParameter("to");
		Date fromDate = null;
		Date toDate = null;
		if (from != null && !from.isEmpty()) {
			fromDate = Tools.parseDateString(from, "dd/MM/yyyy");
		}
		if (to != null && !to.isEmpty()) {
			toDate = Tools.parseDateString(to, "dd/MM/yyyy");
		}

		var sort = request.getParameter("sort");
		if (sort != null || sort.isEmpty()) sort = "username";
		var asc = request.getParameter("asc") != null && request.getParameter("asc").equals("true");

		if (!organisation.getWhitelist().isEmpty()) {
			for (WhitelistedUser u : organisation.getWhitelist()) {
				if (username != null && !username.isEmpty() && !u.getName().toLowerCase().contains(username.toLowerCase())) {
					continue;
				}
				if (email != null && !email.isEmpty() && !u.getEmail().toLowerCase().contains(email.toLowerCase())) {
					continue;
				}
				if (label != null && !label.isEmpty()) {
					if (u.getLabels().stream().noneMatch(l -> l.getName().toLowerCase().contains(label.toLowerCase()))) {
						continue;
					}
				}
				if (fromDate != null && fromDate.after(u.getAdded())) {
					continue;
				}
				if (toDate != null && toDate.before(u.getAdded())) {
					continue;
				}

				u.setSurveys(surveyService.getNumberOfSurveysByOwner(u.getUser().getId()));
				result.add(u);
			}
		}

		if ("username".equals(sort)) {
			if (asc) {
				result.sort(Comparator.comparing(WhitelistedUser::getName, String::compareToIgnoreCase));
			} else {
				result.sort(Comparator.comparing(WhitelistedUser::getName, String::compareToIgnoreCase).reversed());
			}
		} else if ("email".equals(sort)) {
			if (asc) {
				result.sort(Comparator.comparing(WhitelistedUser::getEmail, String::compareToIgnoreCase));
			} else {
				result.sort(Comparator.comparing(WhitelistedUser::getEmail, String::compareToIgnoreCase).reversed());
			}
		} else if ("surveys".equals(sort)) {
			if (asc) {
				result.sort(Comparator.comparing(WhitelistedUser::getSurveys));
			} else {
				result.sort(Comparator.comparing(WhitelistedUser::getSurveys).reversed());
			}
		} else if ("added".equals(sort)) {
			if (asc) {
				result.sort(Comparator.comparing(WhitelistedUser::getAdded));
			} else {
				result.sort(Comparator.comparing(WhitelistedUser::getAdded).reversed());
			}
		}

		return result;
	}

	@PostMapping(value = "/addAuthorisedUser")
	public @ResponseBody boolean addAuthorisedUser(HttpServletRequest request) throws NamingException, LoginAlreadyExistsException, NotAgreedToPsException, NotAgreedToTosException, WeakAuthenticationException, ForbiddenURLException {
		var code = request.getParameter("organisation");
		var login = request.getParameter("login");
		var ecas = request.getParameter("ecas");
		boolean isEcasUser = ecas != null && ecas.equals("true");

		User user = sessionService.getCurrentUser(request);

		if (user.getOrganisationPrivilege() < 1) {
			throw new ForbiddenURLException();
		}

		List<Organisation> lstOrganisations = organisationService.getOrganisationsForManager(user.getId());
		var organisation = organisationService.getOrganisation(code);

		if (organisation == null || !lstOrganisations.contains(organisation)) throw new ForbiddenURLException();

		User u = null;

		try {
			u = administrationService.getUserForLogin(login, isEcasUser);
		} catch (Exception e) {
			// ignore
		}

		u = organisationService.prepareAuthorizedUser(u, isEcasUser, login);
		return organisationService.addAuthorisedUser(code, u.getId());
	}

	@PostMapping(value = "/deleteAuthorisedUser")
	public @ResponseBody boolean deleteAuthorisedUser(HttpServletRequest request) throws MessageException, NotAgreedToPsException, NotAgreedToTosException, WeakAuthenticationException, ForbiddenURLException {
		String code = request.getParameter("organisation");
		int id = Integer.parseInt(request.getParameter("id"));

		User user = sessionService.getCurrentUser(request);

		if (user.getOrganisationPrivilege() < 1) {
			throw new ForbiddenURLException();
		}

		List<Organisation> lstOrganisations = organisationService.getOrganisationsForManager(user.getId());
		var organisation = organisationService.getOrganisation(code);

		if (organisation == null || !lstOrganisations.contains(organisation)) throw new ForbiddenURLException();

		return organisationService.deleteAuthorisedUser(code, id);
	}

}
