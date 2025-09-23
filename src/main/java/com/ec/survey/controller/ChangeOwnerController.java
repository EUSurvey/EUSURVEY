package com.ec.survey.controller;

import com.ec.survey.exception.ForbiddenException;
import com.ec.survey.model.administration.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Controller
@RequestMapping("/ownership")
public class ChangeOwnerController extends BasicController {

	@GetMapping(value = "/accept/{code}")
	public ModelAndView acceptOwnership(@PathVariable String code, HttpServletRequest request, Locale locale) throws Exception {
		User u = sessionService.getCurrentUser(request);

		if (u == null) throw new ForbiddenException();

		var surveyUid = surveyService.acceptOwnershipRequest(code, u);

		if (surveyUid != null) {

			var survey = surveyService.getSurveyByUniqueId(surveyUid, false, true);

			return new ModelAndView("redirect:/" + survey.getShortname() + "/management/overview?isNewOwner=true");
		}

		var model = new ModelAndView("error/generic");

		model.addObject("message", resources.getMessage("message.OwnerChangeRequestInvalid", null, "This request to change survey ownership is not valid anymore.", locale));

		return model;
	}

	@GetMapping(value = "/reject/{code}")
	public ModelAndView rejectOwnership(@PathVariable String code, HttpServletRequest request, Locale locale) throws Exception {
		User u = sessionService.getCurrentUser(request);

		if (u == null) throw new ForbiddenException();

		var surveyUid = surveyService.rejectOwnershipRequest(code, u);

		if (surveyUid != null) {
			var model = new ModelAndView("error/info");

			var survey = surveyService.getSurveyByUniqueId(surveyUid, false, true);

			model.addObject("message", resources.getMessage("message.OwnerRejected", new String[]{ survey.cleanTitle() }, "The request for the transfer of ownership of the following survey: \"{0}\" has been refused.", locale));

			return model;
		}

		var model = new ModelAndView("error/generic");

		model.addObject("message", resources.getMessage("message.OwnerChangeRequestInvalid", null, "This request to change survey ownership is not valid anymore.", locale));

		return model;
	}
}
