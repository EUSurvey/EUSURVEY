package com.ec.survey.controller;

import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.Role;
import com.ec.survey.service.AdministrationService;
import com.ec.survey.service.SurveyService;

@Controller
@RequestMapping("/administration")
public class RoleController extends BasicController {
	
	@Resource(name="administrationService")
	private AdministrationService administrationService;
	
	@Resource(name="surveyService")
	private SurveyService surveyService;
	
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
	
	@RequestMapping(value = "/roles", method = RequestMethod.POST)
	public ModelAndView rolesPost(@RequestParam("id") String id, @RequestParam("privilege") String privilege, @RequestParam("value") String value, Model model) {	
				
		Role role = administrationService.getRole(Integer.parseInt(id));
		role.getGlobalPrivileges().put(GlobalPrivilege.valueOf(privilege), Integer.parseInt(value));
		administrationService.updateRole(role);
		
		List<Role> roles = administrationService.getAllRoles();   	
    	ModelAndView m =  new ModelAndView("administration/roles", "roles", roles);
    	if (isShowEcas()) m.addObject("showecas", true);
    	if (isCasOss()) m.addObject("casoss", true);
  	return m;
	}
	
	@RequestMapping(value = "/createRole", method = RequestMethod.POST)
	public ModelAndView createRole(@RequestParam("name") String name, Model model, Locale locale) {	
				
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
			model.addAttribute("error", resources.getMessage("validation.required", null, "This field is required", locale));
			
		} else {
			if (valid)
			{
				Role role = new Role();
				role.setName(name);
				administrationService.createRole(role);
			} else {
				model.addAttribute("error", resources.getMessage("error.UniqueName", null, "This name already exists. Please choose a unique name.", locale));
			}
		}
		
		roles = administrationService.getAllRoles();   	
    	ModelAndView m =  new ModelAndView("administration/roles", "roles", roles);
    	if (isShowEcas()) m.addObject("showecas", true);
    	if (isCasOss()) m.addObject("casoss", true);
 		return m;
	}
	
	@RequestMapping(value = "/deleteRole", method = RequestMethod.POST)
	public ModelAndView deleteRole(@RequestParam("id") String id, Model model, Locale locale) {	
		try {
			administrationService.deleteRole(Integer.parseInt(id));
		} catch (DataIntegrityViolationException e)
		{
			model.addAttribute("error", resources.getMessage("error.CannotDeleteRole", null, "You cannot delete a role when there are users with that role.", locale));
		}
		
		List<Role> roles = administrationService.getAllRoles();   	
    	ModelAndView m =  new ModelAndView("administration/roles", "roles", roles);
    	if (isShowEcas()) m.addObject("showecas", true);
    	if (isCasOss()) m.addObject("casoss", true);
 		return m;
	}
		
}
