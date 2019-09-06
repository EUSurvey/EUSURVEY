package com.ec.survey.controller;

import com.ec.survey.service.AdministrationService;
import com.ec.survey.service.LdapDBService;
import com.ec.survey.service.LdapService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/logins")
public class JSONController extends BasicController {
	
	@Resource(name="administrationService")
	private AdministrationService administrationService;
	
	@Resource(name="ldapDBService")
	private LdapDBService ldapDBService;

	@Resource(name="ldapService")
	private LdapService ldapService;
	
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
	
	@RequestMapping(value = "/usersJSON", headers="Accept=*/*", method=RequestMethod.GET)
	public @ResponseBody String[] participantsSearch(HttpServletRequest request, HttpServletResponse response ) {
		String name = request.getParameter("name");
		if (name != null) name = name.trim();
		
		String type = request.getParameter("type");
		String first = request.getParameter("first");
		if (first != null) first = first.trim();
		String last = request.getParameter("last");
		if (last != null) last = last.trim();
		String email = request.getParameter("email");
		if (email != null) email = email.trim();
		String department = request.getParameter("department");
		if (department != null) department = department.trim();
		String order = request.getParameter("order");
		
		if (!type.equalsIgnoreCase("system"))
		{
			return ldapService.getECASLogins(name, department, type, first, last, email, order);
		}else {
			return administrationService.getLoginsForPrefix(name, email, true);
		}

	}
}
