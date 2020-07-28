package com.ec.survey.controller;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.Role;

@Controller
@RequestMapping("/administration")
public class RoleController extends BasicController {
	
	@RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
	public String root(Locale locale, Model model) {	
    	return "administration/users";
	}	
	
	@RequestMapping(value = "/roles", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView roles(Locale locale, Model model) {	
		List<Role> roles = administrationService.getAllRoles();   	
    	ModelAndView m =  new ModelAndView("administration/roles", "roles", roles);
    	if (isShowEcas()) m.addObject("showecas", true);
    	if (isCasOss()) m.addObject("casoss", true);
    	return m;
	}
	
	@PostMapping(value = "/roles")
	public ModelAndView rolesPost(HttpServletRequest request,  Locale locale) {	
		
		String target = request.getParameter("target");
		if (target != null) {
			if (target.equals("createRole"))
			{
				return createRole(request.getParameter("name"), locale);
			} else if (target.equals("deleteRole"))
			{
				return deleteRole(request.getParameter("id"), locale);
			}
		}
		
		String id = request.getParameter("id");
		String privilege= request.getParameter("privilege");
		String value= request.getParameter("value");
					
		Role role = administrationService.getRole(Integer.parseInt(id));
		role.getGlobalPrivileges().put(GlobalPrivilege.valueOf(privilege), Integer.parseInt(value));
		administrationService.updateRole(role);
		
		List<Role> roles = administrationService.getAllRoles();   	
    	ModelAndView m =  new ModelAndView("administration/roles", "roles", roles);
    	if (isShowEcas()) m.addObject("showecas", true);
    	if (isCasOss()) m.addObject("casoss", true);
  	return m;
	}
	
	public ModelAndView createRole(@RequestParam("name") String name, Locale locale) {	
		ModelAndView m =  new ModelAndView("administration/roles");    	
			
		List<Role> roles = administrationService.getAllRoles();
		boolean valid = true;
		for (Role role : roles) {
			if (role.getName().equalsIgnoreCase(name))
			{
				valid = false;
				break;
			}
		}
		
		if (name.trim().length() == 0)
		{
			m.addObject("error", resources.getMessage("validation.required", null, "This field is required", locale));					
		} else {
			if (valid)
			{
				Role role = new Role();
				role.setName(name);
				administrationService.createRole(role);
			} else {
				m.addObject("error", resources.getMessage("error.UniqueName", null, "This name already exists. Please choose a unique name.", locale));
			}
		}
		
		roles = administrationService.getAllRoles();   	
		m.addObject("roles", roles);
    	if (isShowEcas()) m.addObject("showecas", true);
    	if (isCasOss()) m.addObject("casoss", true);
 		return m;
	}
	
	public ModelAndView deleteRole(@RequestParam("id") String id, Locale locale) {	
    	ModelAndView m =  new ModelAndView("administration/roles");
   		try {
			administrationService.deleteRole(Integer.parseInt(id));
		} catch (DataIntegrityViolationException e)
		{
			m.addObject("error", resources.getMessage("error.CannotDeleteRole", null, "You cannot delete a role when there are users with that role.", locale));
		}
		
		List<Role> roles = administrationService.getAllRoles();   	
		m.addObject("roles", roles);
    	if (isShowEcas()) m.addObject("showecas", true);
    	if (isCasOss()) m.addObject("casoss", true);
 		return m;
	}
		
}
