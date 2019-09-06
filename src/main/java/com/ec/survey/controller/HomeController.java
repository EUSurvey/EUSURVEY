package com.ec.survey.controller;

import com.ec.survey.model.*;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.*;
 import com.ec.survey.service.mapping.PaginationMapper;
import com.ec.survey.tools.AnswerExecutor;
import com.ec.survey.tools.NotAgreedToTosException;
import com.ec.survey.tools.QuizExecutor;
import com.ec.survey.tools.SurveyHelper;
import com.ec.survey.tools.Ucs2Utf8;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;

@Controller("homeController")
public class HomeController extends BasicController {
	
	public @Value("${export.fileDir}") String fileDir;	
	public @Value("${stresstests.createdata}") String createStressData;	
	private @Value("${smtpserver}") String smtpServer;
	private @Value("${smtp.port}") String smtpPort;

	@Autowired
	protected PaginationMapper paginationMapper;    
		
	@Resource(name="machineTranslationService")
	MachineTranslationService machineTranslationService;
	
	@Resource(name = "taskExecutor")
	private TaskExecutor taskExecutor;
	
	@RequestMapping(value = "/home/about", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String about(Locale locale, ModelMap model) {
		model.put("continueWithoutJavascript", true);
		model.put("oss", super.isOss());		
		return "home/about";
	}
	
	@RequestMapping(value = "/home/about/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String aboutRunner(Locale locale, Model model) {
		model.addAttribute("continueWithoutJavascript", true);
		model.addAttribute("runnermode", true);
		model.addAttribute("oss", super.isOss());
		return "home/about";
	}
	
	@RequestMapping(value = "/home/download", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String download(Locale locale, Model model) {
		model.addAttribute("continueWithoutJavascript", true);
		model.addAttribute("oss",super.isOss());
		return "home/download";
	}
	
	@RequestMapping(value = "/home/download/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String downloadRunner(Locale locale, Model model) {
		model.addAttribute("continueWithoutJavascript", true);
		model.addAttribute("runnermode", true);
		model.addAttribute("oss",super.isOss());
		return "home/download";
	}
	
	@RequestMapping(value = "/home/documentation", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String documentation(Locale locale, ModelMap model) {
		if (isShowEcas()) model.addAttribute("showecas", true);
		// CASOSS
		if (isCasOss()) model.addAttribute("casoss", true);
		model.put("continueWithoutJavascript", true);
		return "home/documentation";
	}
	
	@RequestMapping(value = "/home/documentation/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String documentationRunner(Locale locale, Model model) {
		model.addAttribute("continueWithoutJavascript", true);
		model.addAttribute("runnermode", true);
		return "home/documentation";
	}
	
	@RequestMapping(value = "/home/support", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String support(Locale locale, ModelMap model) {
		model.put("continueWithoutJavascript", true);
		model.addAttribute("oss",super.isOss());
		return "home/support";
	}
	
	@RequestMapping(value = "/home/support/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String supportRunner(Locale locale, Model model) {
		model.addAttribute("continueWithoutJavascript", true);
		model.addAttribute("runnermode", true);
		model.addAttribute("oss",super.isOss());
		return "home/support";
	}
	
	@RequestMapping(value = "/home/helpparticipants", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String helpparticipants(HttpServletRequest request, Locale locale, Model model) {
		return helpparticipantsinternal(request, locale, model);
	}
	
	@RequestMapping(value = "/home/helpparticipants/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String helpparticipantsrunner(HttpServletRequest request, Locale locale, Model model) {
		model.addAttribute("runnermode", true);		
		return helpparticipantsinternal(request, locale, model);
	}
	
	private String helpparticipantsinternal(HttpServletRequest request, Locale locale, Model model) {
		
		model.addAttribute("runnermode", true);
		model.addAttribute("page", "helpparticipants");
		
		if (request.getParameter("faqlanguage") != null)
		{
			String lang = request.getParameter("faqlanguage");
			if (lang.equalsIgnoreCase("de"))
			{
				model.addAttribute("faqlanguage","de");
				return "home/helpparticipantsde";
			} else if (lang.equalsIgnoreCase("fr"))
			{
				model.addAttribute("faqlanguage","fr");
				return "home/helpparticipantsfr";
			} else if (lang.equalsIgnoreCase("en"))
			{
				model.addAttribute("faqlanguage","en");
				return "home/helpparticipants";
			}
		}
		
		if (locale.getLanguage().equals(new Locale("de").getLanguage()))
		{
			model.addAttribute("faqlanguage","de");
			return "home/helpparticipantsde";
		} else if (locale.getLanguage().equals(new Locale("fr").getLanguage()))
		{
			model.addAttribute("faqlanguage","fr");
			return "home/helpparticipantsfr";
		}
		
		model.addAttribute("faqlanguage","en");
		return "home/helpparticipants";
	}
	
	@RequestMapping(value = "/home/helpauthors/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String helpauthorsrunner(HttpServletRequest request, Locale locale, Model model) {
		model.addAttribute("runnermode", true);		
		return helpauthorsinternal(request, locale, model);
	}
	
	@RequestMapping(value = "/home/helpauthors", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String helpauthors(HttpServletRequest request, Locale locale, Model model) {
		return helpauthorsinternal(request, locale, model);
	}
	
	private String helpauthorsinternal(HttpServletRequest request, Locale locale, Model model) {
		
		model.addAttribute("runnermode", true);
		
		if (request.getParameter("faqlanguage") != null)
		{
			String lang = request.getParameter("faqlanguage");
			if (lang.equalsIgnoreCase("de"))
			{
				model.addAttribute("faqlanguage","de");
				return "home/helpauthorsde";
			} else if (lang.equalsIgnoreCase("fr"))
			{
				model.addAttribute("faqlanguage","fr");
				return "home/helpauthorsfr";
			} else if (lang.equalsIgnoreCase("en"))
			{
				model.addAttribute("faqlanguage","en");
				return "home/helpauthors";
			}
		}
		
		if (locale.getLanguage().equals(new Locale("de").getLanguage()))
		{
			model.addAttribute("faqlanguage","de");
			return "home/helpauthorsde";
		} else if (locale.getLanguage().equals(new Locale("fr").getLanguage()))
		{
			model.addAttribute("faqlanguage","fr");
			return "home/helpauthorsfr";
		}
		
		model.addAttribute("faqlanguage","en");
		return "home/helpauthors";
	}
	
	@RequestMapping(value = "/home/privacystatement", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String privacystatement(HttpServletRequest request, Locale locale, Model model) {
		return privacystatementinternal(request, locale, model);
	}
	
	@RequestMapping(value = "/home/privacystatement/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String privacystatementrunner(HttpServletRequest request, Locale locale, Model model) {
		model.addAttribute("runnermode", true);		
		return privacystatementinternal(request, locale, model);
	}
	
	private String privacystatementinternal(HttpServletRequest request, Locale locale, Model model) {
		if (request.getParameter("language") != null)
		{
			String lang = request.getParameter("language");
			if (lang.equalsIgnoreCase("de"))
			{
				model.addAttribute("language","de");
			} else if (lang.equalsIgnoreCase("fr"))
			{
				model.addAttribute("language","fr");
			} else if (lang.equalsIgnoreCase("en"))
			{
				model.addAttribute("language","en");
			}
		} else {		
			if (locale.getLanguage().equals(new Locale("de").getLanguage()))
			{
				model.addAttribute("language","de");
			} else if (locale.getLanguage().equals(new Locale("fr").getLanguage()))
			{
				model.addAttribute("language","fr");
			}
		}
		
		model.addAttribute("readonly", true);
		model.addAttribute("user",request.getSession().getAttribute("USER"));
		model.addAttribute("oss",super.isOss());
		return "auth/tos";
	}
	
	@RequestMapping(value = "/home/faw/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String faqrunner(Locale locale, Model model) {
		model.addAttribute("runnermode", true);
		return "home/faq";
	}
	
	@RequestMapping(value = "/home/faq", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String faq(Locale locale, Model model) {
		return "home/faq";
	}
	
	@RequestMapping(value = "/home/welcome", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView welcome(Locale locale) {	
		return basicwelcome(locale);
	}
	
	@RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView home(HttpServletRequest request, Locale locale) {
		request.getSession().setAttribute("serverprefix",serverPrefix);
		return basicwelcome(locale);
	}
	
	@RequestMapping(value = "/home/welcome/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView welcomerunner(Locale locale) {	
		return basicwelcome(locale);
	}
	
	@RequestMapping(value = "/home/editcontribution ")
	public String editcontribution (HttpServletRequest request, Locale locale, Model model) {	
		model.addAttribute("lang", locale.getLanguage());
		model.addAttribute("runnermode", true);
		return "home/accesscontribution";
	}
	
	private boolean checkValid(WrongAttempts attempts)
	{
		if (attempts == null || attempts.getCounter() < 10 || attempts.getLockDate() == null) return true;
		
		Calendar cal = Calendar.getInstance();  
			
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(attempts.getLockDate());
		cal2.add(Calendar.DAY_OF_MONTH, +1);  
		
		return cal.after(cal2);
	}	
	
	@RequestMapping(value = "/home/editcontribution ", method = RequestMethod.POST)
	public ModelAndView editcontributionPost(HttpServletRequest request, Locale locale) {		
		try {
			
			String remoteAddr = request.getRemoteAddr();
			if (request.getHeader("X-Forwarded-For") != null && !request.getHeader("X-Forwarded-For").equalsIgnoreCase("0.0.0.0")) remoteAddr += " (" + request.getHeader("X-Forwarded-For") + ")";
						
			WrongAttempts attempts = answerService.getWrongAttempts(remoteAddr);
			
			ModelAndView result = new ModelAndView("home/accesscontribution");
		
			if (!checkValid(attempts))
			{
				result.addObject("message", resources.getMessage("message.IPblocked", null, "Your IP is blocked after 10 wrong attempts!", locale));
				result.addObject("runnermode", true);
				return result;
			}
			
			//check captcha
			if (!checkCaptcha(request))
			{
	        	//after 10 bad requests the IP is locked for 24 hours
	        	if (attempts == null) attempts = new WrongAttempts(remoteAddr);
	        	attempts.increaseCounter();
	        	answerService.save(attempts);
	        	result.addObject("captchaerror", resources.getMessage("message.captchawrongnew", null, "The CAPTCHA code is not correct!", locale));
				result.addObject("runnermode", true);
				return result;
	        }
		
			//get answerset
	        String uniqueCode = request.getParameter("uniqueCode");	
	        String token = request.getParameter("token");	
	        
	        if (uniqueCode != null) uniqueCode = uniqueCode.trim();
	        
	        if (token != null) token = token.trim();
	        
			AnswerSet answerSet = null;
			
			if (uniqueCode != null && uniqueCode.length() > 0)
			{			
				answerSet = answerService.get(uniqueCode);
				
				if (answerSet == null)
				{
					answerSet = answerService.getByInvitationCode(uniqueCode);
				}				
			} 
			
			if (answerSet == null) {
				//after 10 bad requests the IP is locked for 24 hours
	        	if (attempts == null) attempts = new WrongAttempts(remoteAddr);
	        	attempts.increaseCounter();
	        	answerService.save(attempts);
				result.addObject("message", resources.getMessage("message.contributionidwrong", null, "The case code is not correct!", locale));
				result.addObject("runnermode", true);
				return result;
	        }

			return new ModelAndView("redirect:/editcontribution/" + answerSet.getUniqueCode());
		
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
			ModelAndView result = new ModelAndView("home/accesscontribution", "message", resources.getMessage("message.contributionerror", null, "Loading of the contribution not possible!", locale));
			result.addObject("runnermode", true);
			return result;
		}
	}
	
	@RequestMapping(value = "/home/downloadcontribution", headers = "Accept=*/*", method = {RequestMethod.POST})
	public @ResponseBody String downloadcontribution(HttpServletRequest request, HttpServletResponse response) {
		try {
			
			if (!checkCaptcha(request))
			{
				return "errorcaptcha";
			}
			
			HashMap<String, String[]> parameters = Ucs2Utf8.requestToHashMap(request);
			String code = parameters.get("caseid")[0];
			
			AnswerSet answerSet = answerService.get(code);
			if (answerSet != null) {			
				
				if (!answerSet.getSurvey().getDownloadContribution())
				{
					return "errorcaseidforbidden";
				}
				
				if (answerSet.getInvitationId() != null && answerSet.getInvitationId().length() > 0)
				{
					return "errorcaseidinvitation";
				}
				
				String email = parameters.get("email")[0];
				
				logger.info("starting creation of answer pdf for contribution " + code + " to be sent to " + email);
								
				if (answerSet.getSurvey().getIsQuiz())
				{
					QuizExecutor export = (QuizExecutor) context.getBean("quizExecutor");
					export.init(answerSet, email, sender, smtpServer, smtpPort, serverPrefix);
					taskExecutor.execute(export);
				} else {				
					AnswerExecutor export = (AnswerExecutor) context.getBean("answerExecutor");
					export.init(answerSet, email, sender, smtpServer, smtpPort, serverPrefix);
					taskExecutor.execute(export);
				}
			} else {
				return "errorcaseid";
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return "error";
		}

		return "success";
	}
	
	@RequestMapping(value = "home/runner", method = RequestMethod.POST)
	public ModelAndView processSubmit(HttpServletRequest request, Locale locale) {

		try {
			
			//get answerset
			String answerSetIdString = request.getParameter("IdAnswerSet");
			String uniqueCode = request.getParameter("uniqueCode");
			
			AnswerSet oldAnswerSet = answerService.get(Integer.parseInt(answerSetIdString));
			
			if (oldAnswerSet == null || !oldAnswerSet.getUniqueCode().equals(uniqueCode))
			{
				return new ModelAndView("error/generic", "message", resources.getMessage("message.dataProblem", null, "There was a problem with the submitted data.", locale));
			}
		
			Survey survey = surveyService.getSurvey(Integer.parseInt(request.getParameter("survey.id")), false, true);
				
			User user = sessionService.getCurrentUser(request, false);
			AnswerSet answerSet = SurveyHelper.parseAndMergeAnswerSet(request, survey, fileDir, uniqueCode, oldAnswerSet, oldAnswerSet.getLanguageCode(), user, fileService);
			
			saveAnswerSet(answerSet, fileDir, null, -1);
					
			ModelAndView result = new ModelAndView("thanks", "uniqueCode", oldAnswerSet.getUniqueCode());
			
			if (survey.getIsOPC())
			{
				result.addObject("opcredirection", survey.getFinalConfirmationLink(opcredirect, oldAnswerSet.getLanguageCode()));
			}
			
			if (!survey.isAnonymous() && answerSet.getResponderEmail() != null)
			{
				result.addObject("participantsemail", answerSet.getResponderEmail());
			}
			
			result.addObject("isthankspage",true);
			result.addObject("runnermode", true);
			result.addObject(new Form(resources, surveyService.getLanguage(oldAnswerSet.getLanguageCode()),contextpath));
			result.addObject("text", survey.getConfirmationPage());			
			if (survey.getConfirmationPageLink() != null && survey.getConfirmationPageLink() && survey.getConfirmationLink() != null && survey.getConfirmationLink().length() > 0) {
				result.addObject("redirect", survey.getFinalConfirmationLink(oldAnswerSet.getLanguageCode()));
			}
			result.addObject("surveyprefix", survey.getId() + ".");
			return result;
		
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ModelAndView("error/basic");
		}
	}
	
	@RequestMapping(value = "/home/publicsurveys/runner")
	public ModelAndView publicsurveysrunner(HttpServletRequest request) throws NotAgreedToTosException {	
		ModelAndView result = publicsurveys(request);
			result.addObject("runnermode", true);
		return result;
	}
	
	@RequestMapping(value = "/home/publicsurveys")

	public ModelAndView publicsurveys(HttpServletRequest request) throws NotAgreedToTosException {	
		
		SurveyFilter filter = sessionService.getSurveyFilter(request, false);
		filter.setUser(null);		
		String newPage = request.getParameter("newPage");				
		newPage = newPage == null ? "1" : newPage;
			
		int rowsPerPage = 10;
           	
		Paging<Survey> paging = new Paging<>();
		paging.setItemsPerPage(rowsPerPage);
		int numberOfSurveys = 0; 
		paging.setNumberOfItems(numberOfSurveys);
		paging.moveTo(newPage);
		
		String sortKey = request.getParameter("sort");
		if (sortKey != null && sortKey.trim().length() > 1)
		{
			sortKey = sortKey.trim();
			
			if (sortKey.equalsIgnoreCase("publication") || sortKey.equalsIgnoreCase("created") )
				filter.setSortKey("survey_created");
			else if (sortKey.equalsIgnoreCase("expiration"))
			{
				filter.setSortKey("survey_end_date");
				filter.setSortOrder("DESC");
			}
			else if (sortKey.equalsIgnoreCase("popularity"))
				filter.setSortKey("replies");
		} else {
			filter.setSortKey("survey_created");
		}

		SqlPagination sqlPagination = paginationMapper.toSqlPagination(paging);
		List<Survey> surveys = surveyService.getSurveys(filter, sqlPagination);
		paging.setItems(surveys);
		
		request.getSession().setAttribute("lastPublicSurveyFilter", filter);
		
		ModelAndView result = new ModelAndView("home/publicsurveys", "paging", paging);    	
    	result.addObject("isPublic", true);
    	result.addObject("filter", filter);
    	
    	//get most popular surveys
    	SurveyFilter popfilter = new SurveyFilter();
    	popfilter.setUser(null);	
    	List<Survey> popularSurveys = surveyService.getPopularSurveys(popfilter);
    	result.addObject("popularSurveys", popularSurveys);
    	
    	return result;
	}
	
	@RequestMapping(value = "/home/publicsurveysjson", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody List<Survey> publicsurveysjson(HttpServletRequest request) {	
		
		int itemsPerPage = 10;
		int newPage = 1;
		
		String rows = request.getParameter("rows");	
		try
		{
			if (rows != null)
			{
				itemsPerPage = Integer.parseInt(rows);
			}
		} catch (NumberFormatException e)
		{
			itemsPerPage = 10;
		}		
		
		String page = request.getParameter("page");		
		try
		{
			if (page != null)
			{
				newPage = Integer.parseInt(page);
			}
		} catch (NumberFormatException e)
		{
			newPage = 1;
		}
		
		SurveyFilter filter = (SurveyFilter) request.getSession().getAttribute("lastPublicSurveyFilter");

		SqlPagination sqlPagination = new SqlPagination(newPage, itemsPerPage);
		return surveyService.getSurveysIncludingTranslationLanguages(filter, sqlPagination, false);
	}
	
	@RequestMapping(value = "/validate/{id}/{code}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String dashboard(@PathVariable String id, @PathVariable String code, Locale locale, Model model) {
		
		try {
			if (administrationService.validateUser(Integer.parseInt(id), code))
			{
				return "home/validated";			
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		
		return "error/validation";
	}
	
	@RequestMapping(value = "/validateNewEmail/{id}/{code}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String validateNewEmail(HttpServletRequest request, @PathVariable String id, @PathVariable String code, Locale locale, Model model) {	
		try {
			if (administrationService.validateNewEmail(request, Integer.parseInt(id), code))
			{
				return "home/emailchangevalidated";
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		
		return "error/validation";
	}
	 	
	@RequestMapping(value = "home/notifySuccess", method = RequestMethod.POST)
	public void notifySuccess(HttpServletRequest request, Locale locale, HttpServletResponse response) {
		String requestId = request.getParameter("requestId");
		if (requestId == null) {
			requestId = request.getParameter("request-id");
		}
		String targetLanguage = request.getParameter("target-language");
		String translatedText = request.getParameter("translated-text");
		logger.info("HomeController.notifySuccess called with for request " + requestId);
		machineTranslationService.saveSuccessResponse(requestId,targetLanguage,translatedText);
	}

	@RequestMapping(value = "home/notifyError", method = RequestMethod.POST)
	public void notifyError(HttpServletRequest request, Locale locale, HttpServletResponse response) {
		String requestId = request.getParameter("requestId");
		if (requestId == null) {
			requestId = request.getParameter("request-id");
		  }
		String targetLanguage = request.getParameter("target-languages");
		String errorCode = request.getParameter("error-code");
		String errorMessage = request.getParameter("error-message");

		logger.error("HomeController.notifyError called for the translation with request ID " + requestId);
		machineTranslationService.saveErrorResponse(requestId,targetLanguage,errorCode,errorMessage);
	}
	
}
