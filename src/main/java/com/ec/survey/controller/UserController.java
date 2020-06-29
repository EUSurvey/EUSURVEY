package com.ec.survey.controller;

import com.ec.survey.model.Paging;
import com.ec.survey.model.Setting;
import com.ec.survey.model.SqlPagination;
import com.ec.survey.model.UserFilter;
import com.ec.survey.model.UsersConfiguration;
import com.ec.survey.model.administration.Role;
import com.ec.survey.model.administration.User;
import com.ec.survey.service.mapping.PaginationMapper;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.Tools;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

@Controller
@RequestMapping("/administration/users")
public class UserController extends BasicController {
    
    @Autowired
	protected PaginationMapper paginationMapper;    
	
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView users(HttpServletRequest request, Model model) throws Exception {	
		
		Paging<User> paging = new Paging<>();
		UserFilter filter = new UserFilter();
		
		if (request.getMethod().equals("POST") && !"true".equals(request.getParameter("clearFilter")))
		{
			filter = sessionService.getUserFilter(request);		
			
			String newPage = request.getParameter("newPage");
			newPage = newPage == null ? "1" : newPage;
			Integer itemsPerPage = ConversionTools.getInt(request.getParameter("itemsPerPage"), 10);
	    		    	
			paging.setItemsPerPage(itemsPerPage);
			int numberOfSurveys = administrationService.getNumberOfUsers(filter);
			paging.setNumberOfItems(numberOfSurveys);
			paging.moveTo(newPage);
			
            SqlPagination sqlPagination = paginationMapper.toSqlPagination(paging);
			List<User> users = administrationService.getUsers(filter, sqlPagination);
			paging.setItems(users);	
		}
    	
    	ModelAndView m =  new ModelAndView("administration/users", "paging", paging);
    	
    	List<Role> roles = administrationService.getAllRoles();
    	m.addObject("ExistingRoles", roles);
    	m.addObject("filter", filter);
    	
    	UsersConfiguration usersConfiguration = administrationService.getUsersConfiguration(sessionService.getCurrentUser(request).getId());
    	if (usersConfiguration == null) usersConfiguration = new UsersConfiguration();
    	m.addObject("usersConfiguration", usersConfiguration);
    	
    	m.addObject("freezeusertext", settingsService.get(Setting.FreezeUserTextBan));
    	m.addObject("unfreezeusertext", settingsService.get(Setting.FreezeUserTextUnban));

    	return m;
	}
	
	@PostMapping(value = "/banuser")
	public ModelAndView banuser(@RequestParam("userId") String userId, @RequestParam("emailText") String emailText, HttpServletRequest request, Model model) throws Exception {	
		
		if (userId == null || userId.length() == 0 || emailText == null || emailText.length() == 0)
		{
			throw new Exception("invalid input data");
		}
			
		administrationService.banUser(userId, emailText);
		
		return new ModelAndView("redirect:/administration/users?frozen=1");	
	}
	
	@PostMapping(value = "/unbanuser")
	public ModelAndView unbanuser(@RequestParam("userId") String userId, HttpServletRequest request, Model model) throws Exception {	
		
		if (userId == null || userId.length() == 0)
		{
			throw new Exception("invalid input data");
		}
			
		administrationService.unbanUser(userId);
		
		return new ModelAndView("redirect:/administration/users?unfrozen=1");	
	}
		
	@PostMapping
	public ModelAndView usersPOST(HttpServletRequest request, Model model, Locale locale) throws Exception {	
    	
    	String target = request.getParameter("target");
    	if (target != null)
    	{
    		if (target.equals("createUser"))
    		{
    			return createUser(request, model, locale);
    		} else if (target.equals("updateUser"))
    		{
    			return updateUser(request, model, locale);
    		} else if (target.equals("deleteUser"))
    		{
    			return deleteUser(request, model, locale);
    		}
    	}
    	
    	return users(request, model);
    }

	public ModelAndView createUser(HttpServletRequest request, Model model, Locale locale) throws Exception {	
		
		String login = request.getParameter("add-login");
		String email = request.getParameter("add-email");
		String otherEmail = request.getParameter("add-other-email");
		String firstname = request.getParameter("add-firstname");
		String lastname = request.getParameter("add-lastname");
		String comment = request.getParameter("add-comment");
		String password = request.getParameter("add-password");
		String language = request.getParameter("add-language");
		String roles = request.getParameter("add-roles"); 
						
		List<User> users = administrationService.getAllUsers();
		boolean valid = true;
		for (User user : users) {
			if (user.getLogin().equalsIgnoreCase(login))
			{
				valid = false;
				break;
			}
		}
		
		if (valid)
		{
			if (Tools.isPasswordWeak(password))
			{
				model.addAttribute("error", resources.getMessage("error.PasswordWeak", null, "This password does not fit our password policy. Please choose a password between 8 and 16 characters with at least one digit and one non-alphanumeric characters (e.g. !?$&%...).", locale));
			} else {
				User user = new User();
				user.setValidated(true);
				user.setLogin(login);
				user.setGivenName(firstname);
				user.setSurName(lastname);
				user.setPasswordSalt(Tools.newSalt());
				user.setPassword(Tools.hash(password + user.getPasswordSalt())); 
				user.setComment(comment);
				user.setEmail(email);
				user.setOtherEmail(otherEmail);
				user.setLanguage(language);
				user.setType(User.SYSTEM);
				
				if (!administrationService.checkEmailsNotBanned(user.getAllEmailAddresses()))
				{
					model.addAttribute("error", resources.getMessage("error.EmailBanned", null, "This email adress belongs to a banned user.", locale));
				} else {
					if (roles != null && roles.length() > 0)
					{
						String[] ids = roles.split(";");
						Map<Integer, Role> rolesById = administrationService.getAllRolesAsMap();
						for (String id : ids) {
							if (rolesById.containsKey(Integer.parseInt(id)))
							{
								user.getRoles().add(rolesById.get(Integer.parseInt(id)));
							}
						}				
					}
					
					administrationService.createUser(user);
				}
			}
		} else {
			model.addAttribute("error", resources.getMessage("error.LoginExists", null, "This login already exists. Please choose a unique login.", locale));
		}
		
		return users(request, model);
	}
	
	public ModelAndView updateUser(HttpServletRequest request, Model model, Locale locale) throws Exception {	
		
		String id = request.getParameter("update-id");
		String email = request.getParameter("update-email");
		String otherEmail = request.getParameter("update-other-email");
		String firstname = request.getParameter("update-firstname");
		String lastname = request.getParameter("update-lastname");
		String comment = request.getParameter("update-comment");
		String password = request.getParameter("update-password");
		String language = request.getParameter("update-language");
		String roles = request.getParameter("update-roles"); 
		
		User user = administrationService.getUser(Integer.parseInt(id));
		
		if (user != null)
		{
			if (user.getType().equalsIgnoreCase("SYSTEM"))
			{
				if (password != null && password.length() > 0 && !password.equals("#######"))
				{
					if (Tools.isPasswordWeak(password))
					{
						model.addAttribute("error", resources.getMessage("error.PasswordWeak", null, "This password does not fit our password policy. Please choose a password between 8 and 16 characters with at least one digit and one non-alphanumeric characters (e.g. !?$&%...).", locale));
						return users(request, model);
					}
									
					user.setPassword(Tools.hash(password + user.getPasswordSalt())); 
				}
				user.setGivenName(firstname);
				user.setSurName(lastname);
				user.setComment(comment);
				user.setEmail(email);
				user.setLanguage(language);
							
				user.getRoles().clear();
				
				if (roles != null && roles.length() > 0)
				{
					String[] ids = roles.split(";");
					Map<Integer, Role> rolesById = administrationService.getAllRolesAsMap();
					for (String rid : ids) {
						if (rolesById.containsKey(Integer.parseInt(rid)))
						{
							user.getRoles().add(rolesById.get(Integer.parseInt(rid)));
						}
					}				
				}
			}
			
			user.setOtherEmail(otherEmail);
			
			administrationService.updateUser(user);
		} else {
			model.addAttribute("error", resources.getMessage("error.UserNotFound", null, "User not found", locale));
		}
		
		return users(request, model);
	}
	
	public ModelAndView deleteUser(HttpServletRequest request,  Model model, Locale locale) throws Exception {	
			
		try {
			String id = request.getParameter("id");
			String login = administrationService.deleteUser(Integer.parseInt(id));	
			model.addAttribute("info", resources.getMessage("info.UserDeleted", new Object[] {login}, "Deletion failed", locale));
			
		} catch (DataIntegrityViolationException de) {
			model.addAttribute("userreferenceserror",true);
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
			model.addAttribute("error", resources.getMessage("error.DeletionFailed", null, "Deletion failed", locale));
		}
		return users(request, model);
	}
	
	@PostMapping(value = "/undoDelete")
	public @ResponseBody String undoDelete(HttpServletRequest request) {
		String sid = request.getParameter("id");
		if (sid == null || sid.length() == 0) return "invalid id";
				
		try {
			int id = Integer.parseInt(sid);
			User user = administrationService.getUser(id);
			if (user == null) return "invalid id";
			
			user.setDeleted(false);
			administrationService.updateUser(user);
			return "OK";
			
		} catch (NumberFormatException e) {
			return "invalid id";
		}
	}
			
}
