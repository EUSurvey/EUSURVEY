package com.ec.survey.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ec.survey.exception.ForbiddenException;
import com.ec.survey.model.administration.User;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.NotAgreedToPsException;
import com.ec.survey.tools.NotAgreedToTosException;
import com.ec.survey.tools.WeakAuthenticationException;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Controller
@RequestMapping("/logins")
public class JSONController extends BasicController {
	
	@RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody String[] getLoginsJSON(HttpServletRequest request) {
 
		String query = request.getParameter("term");
		return administrationService.getLoginsForPrefix(query, null, false);
	}
	
	@RequestMapping(value = "/ECAS", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody String[] getECASLoginsJSON(HttpServletRequest request) {
 
		String query = request.getParameter("term");
		return ldapDBService.getECASLoginsForPrefix(query);
	}
	
	@RequestMapping(value = "/departments", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody String[] getDepartmentsJSON(HttpServletRequest request) {
 
		String query = request.getParameter("term");
		return ldapDBService.getDepartments(null,query, true, false);
	}
	
	@GetMapping(value = "/usersJSON", headers="Accept=*/*")
	public @ResponseBody String[] participantsSearch(HttpServletRequest request, HttpServletResponse response ) throws NamingException, NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException, ForbiddenException {
		
		User u = sessionService.getCurrentUser(request);
		
		String name = request.getParameter("name");
		if (name != null) name = name.trim();
		
		String type = request.getParameter("type");
		String first = request.getParameter("first");
		if (first != null) first = first.trim();
		String last = request.getParameter("last");
		if (last != null) last = last.trim();
		String email = request.getParameter(Constants.EMAIL);
		if (email != null) email = email.trim();
		String department = request.getParameter("department");
		if (department != null) department = department.trim();
		String order = request.getParameter("order");
		
		if (u.isExternal()) {
			if (type != "external") {
				throw new ForbiddenException();
			}
		}
		
		if (!type.equalsIgnoreCase("system"))
		{
			return ldapService.getECASLogins(name, department, type, first, last, email, order, u.isExternal() ? 1 : 100);
		} else {
			return administrationService.getLoginsForPrefix(name, email, true);			
		}
	}

	@GetMapping(value = "/usersEmailJSON", headers="Accept=*/*")
	public @ResponseBody String[] participantsEmailSearch(HttpServletRequest request, HttpServletResponse response ) throws NamingException {
		String email = request.getParameter("emails");
		String[] splittedEmails = Arrays.stream(email.split(";")).map(String::trim).toArray(String[]::new);

		return administrationService.checkLoginsForEmails(Arrays.asList(splittedEmails));
	}
}
