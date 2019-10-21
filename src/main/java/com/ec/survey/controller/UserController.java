package com.ec.survey.controller;

import com.ec.survey.model.Paging;
import com.ec.survey.model.Setting;
import com.ec.survey.model.SqlPagination;
import com.ec.survey.model.UserFilter;
import com.ec.survey.model.UsersConfiguration;
import com.ec.survey.model.administration.Role;
import com.ec.survey.model.administration.User;
import com.ec.survey.service.AdministrationService;
import com.ec.survey.service.SessionService;
import com.ec.survey.service.SurveyService;
import com.ec.survey.service.mapping.PaginationMapper;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.Tools;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

@Controller
@RequestMapping("/administration/users")
public class UserController extends BasicController {
	
	@Resource(name="administrationService")
	private AdministrationService administrationService;
	
	@Resource(name="surveyService")
	private SurveyService surveyService;
	
	@Resource(name="sessionService")
	private SessionService sessionService;
    
    @Autowired
	protected PaginationMapper paginationMapper;    
	
	@RequestMapping
	public ModelAndView users(HttpServletRequest request, Model model) throws Exception {	
		
		Paging<User> paging = new Paging<>();
		UserFilter filter = new UserFilter();
		
		if (request.getMethod().equals("POST"))
		{
			if (!"true".equals(request.getParameter("clearFilter"))) {
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
	
	@RequestMapping(value = "/banuser", method = RequestMethod.POST)
	public ModelAndView banuser(@RequestParam("userId") String userId, @RequestParam("emailText") String emailText, HttpServletRequest request, Model model) throws Exception {	
		
		if (userId == null || userId.length() == 0 || emailText == null || emailText.length() == 0)
		{
			throw new Exception("invalid input data");
		}
			
		administrationService.banUser(userId, emailText);
		
		return new ModelAndView("redirect:/administration/users?frozen=1");	
	}
	
	@RequestMapping(value = "/unbanuser", method = RequestMethod.POST)
	public ModelAndView unbanuser(@RequestParam("userId") String userId, HttpServletRequest request, Model model) throws Exception {	
		
		if (userId == null || userId.length() == 0)
		{
			throw new Exception("invalid input data");
		}
			
		administrationService.unbanUser(userId);
		
		return new ModelAndView("redirect:/administration/users?unfrozen=1");	
	}
		
	@RequestMapping(value = "/createUser", method = RequestMethod.POST)
	public ModelAndView createUser(@RequestParam("add-login") String login, @RequestParam("add-email") String email, @RequestParam("add-other-email") String otherEmail, @RequestParam("add-firstname") String firstname, @RequestParam("add-lastname") String lastname, @RequestParam("add-comment") String comment, @RequestParam("add-password") String password, @RequestParam("add-language") String language, @RequestParam("add-roles") String roles, HttpServletRequest request, Model model, Locale locale) throws Exception {	
				
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
	
	@RequestMapping(value = "/updateUser", method = RequestMethod.POST)
	public ModelAndView updateUser(@RequestParam("update-id") String id, @RequestParam("update-email") String email, @RequestParam("update-other-email") String otherEmail, @RequestParam("update-firstname") String firstname, @RequestParam("update-lastname") String lastname, @RequestParam("update-comment") String comment, @RequestParam("update-password") String password, @RequestParam("update-language") String language, @RequestParam("update-roles") String roles, HttpServletRequest request, Model model, Locale locale) throws Exception {	
		
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
	
	@RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
	public ModelAndView deleteUser(@RequestParam("id") String id, HttpServletRequest request,  Model model, Locale locale) throws Exception {	
				
		try {
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
			
}
