package com.ec.survey.controller;

import com.ec.survey.exception.InvalidURLException;
import com.ec.survey.model.*;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.*;
import com.ec.survey.service.mapping.PaginationMapper;
import com.ec.survey.tools.AnswerExecutor;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.QuizExecutor;
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
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Controller("homeController")
public class HomeController extends BasicController {
	
	public @Value("${stresstests.createdata}") String createStressData;	
	private @Value("${smtpserver}") String smtpServer;
	private @Value("${smtp.port}") String smtpPort;
	private @Value("${server.prefix}") String host;
	
	private @Value("${support.recipient}") String supportEmail;
	private @Value("${support.recipientinternal}") String supportEmailInternal;

	@Autowired
	protected PaginationMapper paginationMapper;    
		
	@Resource(name="machineTranslationService")
	MachineTranslationService machineTranslationService;
	
	@Resource(name = "taskExecutor")
	private TaskExecutor taskExecutor;
	
	@Resource(name="mailService")
	private MailService mailService;
	
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
	
	private String getBrowserInformation(HttpServletRequest request, Locale locale)
	{
		StringBuilder result = new StringBuilder();
		
		if (request.getParameter("error") != null)
		{
			String url = (String) request.getSession().getAttribute("lastErrorURL");
			if (url != null)
			{
				result.append("URL: ").append(url).append("\n");
			}
			Integer code = (Integer) request.getSession().getAttribute("lastErrorCode");
			if (code != null)
			{
				result.append("HTTP Code: ").append(code).append("\n");
			}
			Date time = (Date) request.getSession().getAttribute("lastErrorTime");
			if (time != null)
			{
				result.append("Error time: ").append(time).append("\n");
			}
		}
		result.append("Bowser: ").append(request.getHeader("User-Agent")).append("\n");
		result.append("Language: ").append(locale.getLanguage()).append("\n");
		return result.toString();
	}
	
	@RequestMapping(value = "/home/support", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String support(HttpServletRequest request, Locale locale, ModelMap model) {
		model.put("continueWithoutJavascript", true);
		model.put("additionalinfo", getBrowserInformation(request, locale));
		
		if (request.getParameter("error") != null)
		{
			model.put("fromerrorpage", true);
		}
		
		model.addAttribute("oss",super.isOss());
		return "home/support";
	}
	
	@RequestMapping(value = "/home/support/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String supportRunner(HttpServletRequest request, Locale locale, Model model) {
		model.addAttribute("continueWithoutJavascript", true);
		model.addAttribute("runnermode", true);
		model.addAttribute("additionalinfo", getBrowserInformation(request, locale));
		model.addAttribute("oss",super.isOss());
		return "home/support";
	}
	
	@RequestMapping(value = "/home/support", method = {RequestMethod.POST})
	public String supportPOST(HttpServletRequest request, Locale locale, ModelMap model) throws NumberFormatException, Exception {
		
		if (!checkCaptcha(request))
		{
			model.put("wrongcaptcha", true);
			return "home/support";
		}		
		
		String reason = ConversionTools.removeHTML(request.getParameter("contactreason"), true);
		String name = ConversionTools.removeHTML(request.getParameter("name"), true);
		String email = ConversionTools.removeHTML(request.getParameter("email"), true);
		String subject = ConversionTools.removeHTML(request.getParameter("subject"), true);
		String message = ConversionTools.removeHTML(request.getParameter("message"), true);
		String additionalinfo  = request.getParameter("additionalinfo");
		String additionalsurveyinfotitle = request.getParameter("additionalsurveyinfotitle");
		String additionalsurveyinfoalias = request.getParameter("additionalsurveyinfoalias");
		String[] uploadedfiles = request.getParameterValues("uploadedfile");				
		
		StringBuilder body = new StringBuilder();
		body.append("Dear helpdesk team,<br />please open a ticket and assign it to DIGIT EUSURVEY SUPPORT.<br />Thank you in advance<br/><hr /><br />");
		
		body.append("<table>");
		
		body.append("<tr><td>Affected user:</td><td>").append(name).append("</td></tr>");
		body.append("<tr><td>Email address:</td><td>").append(email).append("</td></tr>");
		
		body.append("<tr><td>&nbsp;</td><td>&nbsp;</td></tr>");
		
		boolean additioninfo = false;
		if (additionalsurveyinfotitle != null && additionalsurveyinfotitle.length() > 0) {
			body.append("<tr><td>Survey Title:</td><td>").append(additionalsurveyinfotitle).append("</td></tr>");
			additioninfo = true;
		}		
		if (additionalsurveyinfoalias != null && additionalsurveyinfoalias.length() > 0) {
			
			String link = host + "runner/" + additionalsurveyinfoalias;			
			
			body.append("<tr><td>Survey Alias:</td><td><a href='").append(link).append("'>").append(additionalsurveyinfoalias).append("</a></td></tr>");
			additioninfo = true;
		}
		if (additioninfo)
		{
			body.append("<tr><td>&nbsp;</td><td>&nbsp;</td></tr>");
		}
		
		body.append("<tr><td>Reason:</td><td>").append(reason).append("</td></tr>");
		body.append("<tr><td>Subject:</td><td>").append(subject).append("</td></tr>");
		
		body.append("</table>");		
		
		body.append("<br />Message text:<br />").append(message).append("<br /><br />");
		if (additionalinfo != null)
		{
			body.append(ConversionTools.escape(additionalinfo).replace("\n", "<br />"));
		}
		
		InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
		String text = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", body).replace("[HOST]", host);		
		
		java.io.File attachment1 = null;
		java.io.File attachment2 = null;
		if (uploadedfiles != null)
		{
			if (uploadedfiles.length > 0)
			{
				attachment1 = fileService.getTemporaryFile(uploadedfiles[0]);
				if (uploadedfiles.length > 1)
				{
					attachment2 = fileService.getTemporaryFile(uploadedfiles[1]);
				}
			}
		}
		
		if (email.toLowerCase().endsWith("ec.europa.eu"))
		{
			mailService.SendHtmlMail(supportEmailInternal, sender, sender, subject, text, smtpServer, Integer.parseInt(smtpPort), attachment1, attachment2, null, true);
		} else {
			mailService.SendHtmlMail(supportEmail, sender, sender, subject, text, smtpServer, Integer.parseInt(smtpPort), attachment1, attachment2, null, true);
		}
		
		model.put("messagesent", true);
		model.put("additionalinfo", getBrowserInformation(request, locale));
		return "home/support";
	}
	
	@RequestMapping(value = "/home/support/deletefile", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody String deletefile(HttpServletRequest request, HttpServletResponse response) {
		
		try {
			String uid = request.getParameter("uid");	
			
			java.io.File file = fileService.getTemporaryFile(uid);
			
			if (file.exists() && file.delete())
			{
				return "{\"success\": true}";
			}			
					
		} catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logger.error(ex.getMessage(), ex);
        }

		return "{\"success\": false}";
	}	
	
	@RequestMapping(value = "/home/support/uploadfile", method = RequestMethod.POST)
	public void uploadFile(HttpServletRequest request, HttpServletResponse response) {

		PrintWriter writer = null;
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            writer = response.getWriter();
        } catch (IOException ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        }

        String filename;
        boolean error = false;
        
        try {
        
	        if (request instanceof DefaultMultipartHttpServletRequest)
	        {
	        	DefaultMultipartHttpServletRequest r = (DefaultMultipartHttpServletRequest)request;        	
	        	filename = com.ec.survey.tools.FileUtils.cleanFilename(java.net.URLDecoder.decode(r.getFile("qqfile").getOriginalFilename(), "UTF-8"));        	
	        	is = r.getFile("qqfile").getInputStream();        	
	        } else {
	        	filename = com.ec.survey.tools.FileUtils.cleanFilename(java.net.URLDecoder.decode(request.getHeader("X-File-Name"), "UTF-8"));
	        	is = request.getInputStream();
	        }
        	
	        if (!error)
	        {
	        	java.io.File file = fileService.getTemporaryFile();          
	        	String uid = file.getName();
	        			
	            fos = new FileOutputStream(file);
	            IOUtils.copy(is, fos);
	                                  
            	response.setStatus(HttpServletResponse.SC_OK);
            	writer.print("{\"success\": true, \"id\": '" + uid + "', \"uid\": '" + uid + "', \"longdesc\": '', \"comment\": '', \"width\": '', \"name\": '" + filename + "'}");
	        }
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.print("{\"success\": false}");
            logger.error(ex.getMessage(), ex);
        } finally {
            try {
                fos.close();
                is.close();
            } catch (IOException ignored) {
            }
        }

        writer.flush();
        writer.close();
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
		return privacystatementinternal(request, locale, model, 1);
	}
	
	@RequestMapping(value = "/home/privacystatement/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String privacystatementrunner(HttpServletRequest request, Locale locale, Model model) {
		model.addAttribute("runnermode", true);		
		return privacystatementinternal(request, locale, model, 1);
	}
	
	@RequestMapping(value = "/home/tos", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String tos(HttpServletRequest request, Locale locale, Model model) {
		return privacystatementinternal(request, locale, model, 2);
	}
	
	@RequestMapping(value = "/home/tos/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String tosrunner(HttpServletRequest request, Locale locale, Model model) {
		model.addAttribute("runnermode", true);		
		return privacystatementinternal(request, locale, model, 2);
	}
	
	private String privacystatementinternal(HttpServletRequest request, Locale locale, Model model, int page) {
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
		model.addAttribute("page", page);
		return "auth/tos";
	}
	
	@RequestMapping(value = "/home/welcome", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView welcome(HttpServletRequest request, Locale locale) {	
		return basicwelcome(request, locale);
	}
	
	@RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView home(HttpServletRequest request, Locale locale) {
		request.getSession().setAttribute("serverprefix",serverPrefix);
		return basicwelcome(request, locale);
	}
	
	@RequestMapping(value = "/home/welcome/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView welcomerunner(HttpServletRequest request, Locale locale) {	
		return basicwelcome(request, locale);
	}
	
	@RequestMapping(value = "/home/editcontribution")
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
	
	@RequestMapping(value = "/home/editcontribution", method = RequestMethod.POST)
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
	
	@RequestMapping(value = "/home/publicsurveys/runner")
	public ModelAndView publicsurveysrunner(HttpServletRequest request) throws Exception {	
		ModelAndView result = publicsurveys(request);
			result.addObject("runnermode", true);
		return result;
	}
	
	@RequestMapping(value = "/home/publicsurveys")

	public ModelAndView publicsurveys(HttpServletRequest request) throws Exception {	
		
		if (!enablepublicsurveys.equalsIgnoreCase("true"))
		{
			throw new InvalidURLException();
		}
		
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
	public @ResponseBody List<Survey> publicsurveysjson(HttpServletRequest request) throws Exception {	
		
		if (!enablepublicsurveys.equalsIgnoreCase("true"))
		{
			throw new InvalidURLException();
		}
		
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
		return surveyService.getSurveysIncludingTranslationLanguages(filter, sqlPagination, false, false);
	}
	
	@RequestMapping(value = "/validate/{id}/{code}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String validate(@PathVariable String id, @PathVariable String code, Locale locale, Model model) {
		
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
	
	@RequestMapping(value = "/deleteaccount/{id}/{code}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String deleteaccount(HttpServletRequest request, @PathVariable String id, @PathVariable String code, Locale locale, Model model)  {	
		
		try {
			administrationService.confirmUserDeleteRequest(Integer.parseInt(id), code);
			model.addAttribute("message", resources.getMessage("message.AccountDeleted", null, "Your account has been deleted!", locale));
			return "error/info";
		} catch (NumberFormatException nfe) {
			model.addAttribute("message", resources.getMessage("error.UserNotFound", null, "User not found.", locale));
		} catch (Exception e) {
			switch (e.getMessage()) {
			case "User unknown":
				model.addAttribute("message", resources.getMessage("error.UserNotFound", null, "User not found.", locale));
				break;
			case "Wrong code":
				model.addAttribute("message", resources.getMessage("error.WrongCode", null, "The code is wrong.", locale));
				break;
			case "Request too old":
				model.addAttribute("message", resources.getMessage("error.DeleteCodeOutdated", null, "You did not confirm the account deletion during the corresponding time span.", locale));
				break;
			default:
				logger.error(e.getLocalizedMessage(), e);
				break;
			}
		}
	
		return "error/generic";
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
	
	@RequestMapping(value = "/home/reportAbuse", method = RequestMethod.GET)
	public String reportAbuse (HttpServletRequest request, Locale locale, Model model) throws InvalidURLException {	
		model.addAttribute("lang", locale.getLanguage());
		model.addAttribute("runnermode", true);
		
		String surveyid = request.getParameter("survey");
		if (surveyid == null || surveyid.trim().length() == 0)
		{
			throw new InvalidURLException();
		}
		
		try {
			int id = Integer.parseInt(surveyid);
			
			Survey survey = surveyService.getSurvey(id);
			
			if (survey == null)
			{
				throw new InvalidURLException();
			}
			
			model.addAttribute("AbuseSurvey", survey.getUniqueId());
			model.addAttribute("AbuseType", "");
			model.addAttribute("AbuseText", "");
			model.addAttribute("AbuseEmail", "");
			
		} catch (NumberFormatException e)
		{
			throw new InvalidURLException();
		}
		
		return "home/reportabuse";
	}
	
	@RequestMapping(value = "home/reportAbuse", method = RequestMethod.POST)
	public ModelAndView reportAbusePOST(HttpServletRequest request, Locale locale, HttpServletResponse response) throws NumberFormatException, Exception {
		ModelAndView model = new ModelAndView("home/reportabuse");
		
		String uid = request.getParameter("abuseSurvey");
		String type = request.getParameter("abuseType");
		String text = request.getParameter("abuseText");
		String email = request.getParameter("abuseEmail");
		
		Survey survey = surveyService.getSurveyByUniqueId(uid, false, true);
		
		if (survey == null)
		{
			throw new InvalidURLException();
		}
		
		if (!checkCaptcha(request)) {			
			model.addObject("wrongcaptcha", true);
			model.addObject("contextpath", contextpath);
			
			model.addObject("AbuseSurvey", uid);
			model.addObject("AbuseType", type);
			model.addObject("AbuseText", text);
			model.addObject("AbuseEmail", email);
			
			return model;
		}		
	
		logger.info("HomeController.reportAbuse called with abuseType " + type);
		
		surveyService.reportAbuse(survey, type, text, email);
		
		model = new ModelAndView("error/info");
		String message = resources.getMessage("info.ReportAbuseSent", null, "The abuse has been reported to the team in charge of the service.", locale);
		
		model.addObject("message", message);
		model.addObject("contextpath", contextpath);
		
		String link = serverPrefix + "runner/" + survey.getShortname();
		model.addObject("SurveyLink", link);
		
		return model;
	}
	
}
