package com.ec.survey.controller;

import com.ec.survey.exception.ForbiddenURLException;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.attendees.Attendee;
import com.ec.survey.model.attendees.Share;
import com.ec.survey.tools.NotAgreedToPsException;
import com.ec.survey.tools.NotAgreedToTosException;
import com.ec.survey.tools.Tools;
import com.ec.survey.tools.Ucs2Utf8;
import com.ec.survey.tools.WeakAuthenticationException;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping("/settings")
public class SettingsController extends BasicController {

	@Autowired
	private LocaleResolver localeResolver;

	@RequestMapping(method = { RequestMethod.GET, RequestMethod.HEAD })
	public String root(HttpServletRequest request, Locale locale, Model model)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException {
		// check user (e.g. weak authentication)
		sessionService.getCurrentUser(request);
		model.addAttribute("languages", surveyService.getLanguages());
		return "settings/skin";
	}

	@RequestMapping(value = "/myAccount", method = { RequestMethod.GET, RequestMethod.HEAD })
	public String myAccount(HttpServletRequest request, ModelMap model, Locale locale)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException {
		model.addAttribute("languages", surveyService.getLanguages());

		String message = request.getParameter("message");
		if (message != null) {
			switch (message) {
			case "password":
				model.addAttribute("message",
						resources.getMessage("info.PasswordChanged", null, "The password has been changed", locale));
				break;
			case "email":
				model.addAttribute("message", resources.getMessage("message.NewEmailAddressSend", null,
						"The email address will be changed after confirmation", locale));
				break;
			case "language":
				User user = sessionService.getCurrentUser(request);
				model.addAttribute("message", resources.getMessage("message.LanguageChanged", null,
						"The language has been changed", new Locale(user.getLanguage())));
				break;
			case "pivot":
				model.addAttribute("message",
						resources.getMessage("message.LanguageChanged", null, "The language has been changed", locale));
				break;
			}
		}

		return "settings/myAccount";
	}

	@PostMapping(value = "/myAccount")
	public String myAccountPOST(HttpServletRequest request, HttpServletResponse response, ModelMap model, Locale locale)
			throws Exception {

		String target = request.getParameter("target");
		if (target != null) {
			if (target.equals("changePassword")) {
				return changePassword(request, model, locale);
			} else if (target.equals("changeEmail")) {
				return changeEmail(request, model, locale);
			} else if (target.equals("changeLanguage")) {
				return changeLanguage(request, response);
			} else if (target.equals("changePivotLanguage")) {
				return changePivotLanguage(request);
			} else if (target.equals("deleteAccount")) {
				User u = sessionService.getCurrentUser(request);
				administrationService.setUserDeleteRequested(u.getId());
				request.getSession().invalidate();
				sessionService.setCurrentUser(request, null);

				model.addAttribute("message", resources.getMessage("message.validateDelete", null,
						"The deletion of your account has been started. You will receive a confirmation mail containing a link. Click on the link to confirm the deletion of your account.",
						locale));
				return "error/info";
			}
		}

		model.addAttribute("languages", surveyService.getLanguages());
		return "settings/myAccount";
	}

	public String changePassword(HttpServletRequest request, ModelMap model, Locale locale)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException {

		String oldPassword = request.getParameter("oldpassword");
		String newPassword = request.getParameter("newpassword");
		String newPassword2 = request.getParameter("newpassword2");

		model.addAttribute("languages", surveyService.getLanguages());

		if (newPassword == null || newPassword2 == null || newPassword.trim().length() == 0) {
			model.addAttribute("error",
					resources.getMessage("message.ValidPassword", null, "Please provide a valid password", locale));
			model.addAttribute("operation", "changePassword");
			return "settings/myAccount";
		}

		if (!newPassword.equals(newPassword2)) {
			model.addAttribute("error", resources.getMessage("validation.NewPasswordsDontMatch", null,
					"The new passwords do not match", locale));
			model.addAttribute("operation", "changePassword");
			return "settings/myAccount";
		}

		User user = sessionService.getCurrentUser(request);

		if (oldPassword == null || !Tools.isPasswordValid(user.getPassword(), oldPassword + user.getPasswordSalt())) {
			model.addAttribute("error",
					resources.getMessage("validation.OldPasswordWrong", null, "The old password is wrong", locale));
			model.addAttribute("operation", "changePassword");
			return "settings/myAccount";
		}

		if (Tools.isPasswordWeak(newPassword)) {
			model.addAttribute("error", resources.getMessage("error.PasswordWeak", null,
					"This password does not fit our password policy. Please choose a password between 8 and 16 characters with at least one digit and one non-alphanumeric characters (e.g. !?$&%...).",
					locale));
			model.addAttribute("operation", "changePassword");
			return "settings/myAccount";
		}

		user.setPassword(Tools.hash(newPassword + user.getPasswordSalt()));

		administrationService.updateUser(user);
		sessionService.setCurrentUser(request, user);

		return "redirect:/settings/myAccount?message=password";
	}

	public String changeEmail(HttpServletRequest request, ModelMap model, Locale locale)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException {

		String password = request.getParameter("password");
		String email = request.getParameter("newemail");
		String email2 = request.getParameter("newemail2");

		model.addAttribute("languages", surveyService.getLanguages());

		if (email == null || email2 == null || email.trim().length() == 0) {
			model.addAttribute("error",
					resources.getMessage("error.ValidEmail", null, "Please provide a valid email address", locale));
			model.addAttribute("operation", "changeEmail");
			return "settings/myAccount";
		}

		if (!email.equals(email2)) {
			model.addAttribute("error",
					resources.getMessage("error.EmailsDontMatch", null, "The email addresses do not match", locale));
			model.addAttribute("operation", "changeEmail");
			return "settings/myAccount";
		}

		User user = sessionService.getCurrentUser(request);

		if (password == null || !Tools.isPasswordValid(user.getPassword(), password + user.getPasswordSalt())) {
			model.addAttribute("error",
					resources.getMessage("error.WrongPassword", null, "The password is wrong", locale));
			model.addAttribute("operation", "changeEmail");
			return "settings/myAccount";
		}

		if (!EmailValidator.getInstance().isValid(email)) {
			model.addAttribute("error",
					resources.getMessage("error.InvalidEmail", null, "The email address is not valid", locale));
			model.addAttribute("operation", "changeEmail");
			return "settings/myAccount";
		}

		user.setEmailToValidate(email);
		administrationService.updateUser(user);

		if (!administrationService.sendNewEmailAdressValidationEmail(user)) {
			model.addAttribute("error", resources.getMessage("error.InvalidEmail", null,
					"The confirmation email could not be sent", locale));
			model.addAttribute("operation", "changeEmail");
			return "settings/myAccount";
		}

		return "redirect:/settings/myAccount?message=email";
	}

	public String changeLanguage(HttpServletRequest request, HttpServletResponse response)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException {
		String lang = request.getParameter("change-lang");

		User user = sessionService.getCurrentUser(request);

		user.setLanguage(lang);
		administrationService.updateUser(user);

		sessionService.setCurrentUser(request, user);

		localeResolver.setLocale(request, response, new Locale(user.getLanguage()));

		return "redirect:/settings/myAccount?message=language";
	}

	public String changePivotLanguage(HttpServletRequest request)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException {
		String lang = request.getParameter("change-lang");

		User user = sessionService.getCurrentUser(request);

		user.setDefaultPivotLanguage(lang);
		administrationService.updateUser(user);

		sessionService.setCurrentUser(request, user);
		return "redirect:/settings/myAccount?message=pivot";
	}

	@GetMapping(value = "/shares")
	public ModelAndView shares(HttpServletRequest request, Locale locale)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException {
		User user = sessionService.getCurrentUser(request);

		String delete = request.getParameter("delete");

		if (delete != null && delete.trim().length() > 0) {
			Share share = attendeeService.getShare(Integer.parseInt(delete));
			if (user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) == 2
					|| share.getOwner().getId().equals(user.getId())) {
				attendeeService.deleteShare(Integer.parseInt(delete));
			}
		}

		int ownerId;
		if (user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) == 2) {
			ownerId = -1;
		} else {
			ownerId = user.getId();
		}

		List<Share> shares = attendeeService.getShares(ownerId);
		List<Share> passiveShares = attendeeService.getPassiveShares(user.getId());

		ModelAndView result = new ModelAndView("settings/shares");

		result.addObject("shares", shares);
		result.addObject("passiveShares", passiveShares);
		result.addObject("attributeNames", user.getSelectedAttributes());
		result.addObject("allAttributeNames", attendeeService.getAllAttributes(ownerId));

		return result;
	}

	@PostMapping(value = "/shares")
	public ModelAndView sharesPOST(HttpServletRequest request, Locale locale)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException {

		String target = request.getParameter("target");
		if (target != null && target.equals("createStaticShare")) {
			return createStaticShare(request, locale);
		}

		return shares(request, locale);
	}

	@RequestMapping(value = "/shareEdit/{pid}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView shareEdit(@PathVariable String pid, HttpServletRequest request, Locale locale)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException {
		int id = Integer.parseInt(pid);
		User user = sessionService.getCurrentUser(request);
		Share share = attendeeService.getShare(id);

		ModelAndView result = shares(request, locale);

		if (share == null) {
			result.addObject("message", resources.getMessage("message.ShareNotFound", null, "Share not found", locale));
		} else {

			if (!share.getOwner().getId().equals(user.getId())
					&& user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) != 2
					&& !(share.getReadonly() || !share.getRecipient().getId().equals(user.getId()))) {
				if (share.getReadonly() || !share.getRecipient().getId().equals(user.getId())) {
					result.addObject("message", resources.getMessage("error.ShareUnauthorized", null,
							"You are not authorized to edit this share.", locale));
					return result;
				}
			}

			result.addObject("shareToEdit", share);
			result.addObject("readonly", !share.getOwner().getId().equals(user.getId())
					&& user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) != 2 && share.getReadonly());

		}

		return result;
	}

	@GetMapping(value = "/userExists", headers = "Accept=*/*")
	public @ResponseBody boolean userExists(HttpServletRequest request, HttpServletResponse response)
			throws NotAgreedToTosException, WeakAuthenticationException, ForbiddenURLException, NotAgreedToPsException {
		Map<String, String[]> parameters = Ucs2Utf8.requestToHashMap(request);
		User userInRequest = sessionService.getCurrentUser(request);
		User user = administrationService.getUser(userInRequest.getId());
		Date now = new Date();
		Date lastAttemptMoment = new Date();
		if (user.getUserExistsAttemptDate() != null) {
			lastAttemptMoment = user.getUserExistsAttemptDate();
		} else {
			user.setUserExistsAttemptDate(now);
		}

		long lastAttemptMomentPlusOneHourLong = lastAttemptMoment.getTime() + 1000L * 60L * 60L;
		Date lastAttemptMomentPlusOneHour = new Date(lastAttemptMomentPlusOneHourLong);
		int numberOfAttemptsInOneHour = user.getUserExistsAttempts();
		if (now.after(lastAttemptMomentPlusOneHour)) {
			// reinit
			numberOfAttemptsInOneHour = 1;
			user.setUserExistsAttempts(numberOfAttemptsInOneHour);
			user.setUserExistsAttemptDate(now);
		} else {
			// adding
			numberOfAttemptsInOneHour += 1;
			user.setUserExistsAttempts(numberOfAttemptsInOneHour);
			if (numberOfAttemptsInOneHour > 30) {
				throw new ForbiddenURLException();
				// not saving the user since this would overload the Users table
			}
		}
		administrationService.updateUser(user);
		sessionService.setCurrentUser(request, user);

		String login = parameters.get("login")[0];
		User searchedUser = administrationService.getUserForLogin(login);
		return searchedUser != null;
	}

	public ModelAndView createStaticShare(HttpServletRequest request, Locale locale)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException {

		User user = sessionService.getCurrentUser(request);

		Map<String, String[]> parameters = Ucs2Utf8.requestToHashMap(request);

		String shareName = parameters.get("shareName")[0];
		String shareMode = parameters.get("shareMode")[0];
		String recipientname = parameters.get("recipientName")[0];

		String shareToEdit = request.getParameter("shareToEdit");

		User recipient = null;
		try {
			recipient = administrationService.getUserForLogin(recipientname);
		} catch (Exception e) {
			ModelAndView result = shares(request, locale);
			result.addObject("message",
					resources.getMessage("error.UnknownRecipient", null, "The recipient does not exist", locale));
			return result;
		}

		parameters.remove("shareName");
		parameters.remove("shareMode");

		List<Attendee> attendees = new ArrayList<>();

		for (String key : parameters.keySet()) {
			if (key.startsWith("att")) {
				int intKey = Integer.parseInt(key.substring(3));

				Attendee attendee = attendeeService.get(intKey);
				attendees.add(attendee);
			}
		}

		Share share = null;

		if (shareToEdit == null) {
			share = new Share();
			share.setOwner(user);
		} else {
			share = attendeeService.getShare(Integer.parseInt(shareToEdit));

			if (share == null) {
				ModelAndView result = shares(request, locale);
				result.addObject("message",
						resources.getMessage("error.ShareNotFound", null, "Share not found", locale));
				return result;
			}

			if (!share.getOwner().getId().equals(user.getId())
					&& user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) != 2
					&& !(share.getReadonly() || !share.getRecipient().getId().equals(user.getId()))
					&& (share.getReadonly() || !share.getRecipient().getId().equals(user.getId()))) {

				ModelAndView result = shares(request, locale);
				result.addObject("message", resources.getMessage("error.ShareUnauthorized", null,
						"You are not authorized to edit this share.", locale));
				return result;
			}
		}

		share.setName(shareName);
		share.setReadonly(!shareMode.equalsIgnoreCase("readwrite"));

		share.setAttendees(attendees);
		share.setRecipient(recipient);
		attendeeService.save(share);

		return

		shares(request, locale);
	}

}
