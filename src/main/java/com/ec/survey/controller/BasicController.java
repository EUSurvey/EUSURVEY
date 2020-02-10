package com.ec.survey.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.ec.survey.exception.ForbiddenURLException;
import com.ec.survey.exception.FrozenSurveyException;
import com.ec.survey.exception.InvalidURLException;
import com.ec.survey.exception.MessageException;
import com.ec.survey.exception.NoFormLoadedException;
import com.ec.survey.exception.TooManyFiltersException;
import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.Archive;
import com.ec.survey.model.Draft;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.ActivityService;
import com.ec.survey.service.AdministrationService;
import com.ec.survey.service.AnswerService;
import com.ec.survey.service.ArchiveService;
import com.ec.survey.service.AttendeeService;
import com.ec.survey.service.ExportService;
import com.ec.survey.service.FileService;
import com.ec.survey.service.LdapDBService;
import com.ec.survey.service.LdapService;
import com.ec.survey.service.ParticipationService;
import com.ec.survey.service.ReportingService;
import com.ec.survey.service.SessionService;
import com.ec.survey.service.SettingsService;
import com.ec.survey.service.SkinService;
import com.ec.survey.service.SurveyService;
import com.ec.survey.service.SystemService;
import com.ec.survey.service.TranslationService;
import com.ec.survey.tools.ArchiveExecutor;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.InvalidXHTMLException;
import com.ec.survey.tools.NotAgreedToTosException;
import com.ec.survey.tools.NotAgreedToPsException;
import com.ec.survey.tools.WeakAuthenticationException;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.multitype.MultiTypeCaptchaService;

@Controller
public class BasicController implements BeanFactoryAware {
	
	protected static final Logger logger = Logger.getLogger(BasicController.class);
	
	@Autowired
	protected MessageSource resources;	

	@Resource(name = "answerService")
	protected AnswerService answerService;
	
	@Resource(name = "surveyService")
	protected SurveyService surveyService;
	
	@Resource(name = "systemService")
	protected SystemService systemService;
	
	@Resource(name = "attendeeService")
	protected AttendeeService attendeeService;
	
	@Resource(name = "participationService")
	protected ParticipationService participationService;
	
	@Resource(name = "activityService")
	protected ActivityService activityService;
	
	@Resource(name = "exportService")
	protected ExportService exportService;

	@Resource(name = "administrationService")
	protected AdministrationService administrationService;

	@Resource(name = "sessionService")
	protected SessionService sessionService;
	
	@Resource(name = "fileService")
	protected FileService fileService;
	
	@Resource(name = "skinService")
	protected SkinService skinService;
	
	@Resource(name = "translationService")
	protected TranslationService translationService;
	
	@Resource(name = "ldapService")
	protected LdapService ldapService;
	
	@Resource(name = "ldapDBService")
	protected LdapDBService ldapDBService;
	
	@Resource(name = "archiveService")
	protected ArchiveService archiveService;
	
	@Resource(name = "taskExecutorLong")
	protected TaskExecutor taskExecutorLong;
		
	@Resource(name = "taskExecutorLongRestore")
	protected TaskExecutor taskExecutorLongRestore;
	
	@Resource(name="settingsService")
	protected SettingsService settingsService;	
	
	@Resource(name = "reportingService")
	protected ReportingService reportingService;
	
	public @Value("${captcha.secret}") String captchasecret;
	public @Value("${ui.enableresponsive}") String enableresponsive;	
	private @Value("${ecaslogout}") String ecaslogout;
	public @Value("${showecas}") String showecas;	
	public @Value("${ecashost}") String ecashost;	
	public @Value("${sender}") String sender;
	public @Value("${captcha.bypass:@null}") String bypassCaptcha;
	public @Value("${ui.enablepublicsurveys}") String enablepublicsurveys;
	
	//OCAS
	public @Value("${casoss}") String cassOss;
	protected @Value("${contextpath}") String contextpath;	

	@Autowired
	public ServletContext servletContext;
	 
	@Autowired
	private MultiTypeCaptchaService captchaService;
	
	@Value("${server.prefix}")
	public String serverPrefix;	
	
	protected @Value("${export.tempFileDir}") String tempFileDir;
	public @Value("${export.fileDir}") String fileDir;
	protected @Value("${isworkerserver}") String isworkerserver;
	protected @Value("${useworkerserver}") String useworkerserver;
	protected @Value("${workerserverurl}") String workerserverurl;	
	protected @Value("${archive.fileDir}") String archiveFileDir;

	protected @Value("${oss}") String oss;
	protected @Value("${show.privacy}") String showPrivacy;	
	
	protected @Value("${opc.redirect}") String opcredirect;

	protected BeanFactory context;
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		context = beanFactory;		
	}

	public boolean isShowPrivacy(){
		return (!StringUtils.isEmpty(showPrivacy) && showPrivacy.equalsIgnoreCase("true")); 
	}

	public boolean isOss(){
		return (!StringUtils.isEmpty(oss) && oss.equalsIgnoreCase("true")); 
	}
	
	public boolean isShowEcas()
	{
		return showecas != null && showecas.equalsIgnoreCase("true");
	}

	public boolean isCasOss()
	{
		return cassOss != null && cassOss.equalsIgnoreCase("true");
	}

	public static boolean isAjax(HttpServletRequest request) {
	   return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
	}
	
	public boolean isByPassCaptcha(){
		return bypassCaptcha !=null && bypassCaptcha.equalsIgnoreCase("true");
	}
	
	@ExceptionHandler(com.ec.survey.tools.Bad2faCredentialsException.class) 
    public ModelAndView handleBad2faCredentialsException(Exception e, HttpServletRequest request) {
		logger.info(e.getLocalizedMessage(), e);
		ModelAndView model =  new ModelAndView("redirect:/errors/2fa.html");
		model.addObject("contextpath", contextpath);
		return model;
    }
	
	@ExceptionHandler(com.ec.survey.tools.FrozenCredentialsException.class) 
    public ModelAndView handleFrozenCredentialsException(Exception e, HttpServletRequest request) {
		logger.info(e.getLocalizedMessage(), e);
		ModelAndView model =  new ModelAndView("redirect:/errors/frozen.html");
		model.addObject("contextpath", contextpath);
		return model;
    }
	
	@ExceptionHandler(InvalidURLException.class) 
    public ModelAndView handleInvalidURLException(Exception e, HttpServletRequest request) {
		logger.info(e.getLocalizedMessage(), e);
		ModelAndView model =  new ModelAndView("redirect:/errors/404.html");
		model.addObject("is404", true);
		model.addObject("contextpath", contextpath);
		return model;
    }
	
	@ExceptionHandler(FrozenSurveyException.class) 
    public ModelAndView handleFrozenSurveyException(Exception e, HttpServletRequest request, Locale locale) {
		logger.error(e.getLocalizedMessage(), e);
		ModelAndView model = new ModelAndView("error/generic");
		String message = resources.getMessage("error.FrozenSurvey", null, "This survey has been blocked due to an infringement to our policy. We are sorry for the inconvenience this may cause. Please try again later.", locale);
		model.addObject("message", message);
		model.addObject("contextpath", contextpath);
		return model;
    }
	
	@ExceptionHandler(ForbiddenURLException.class) 
    public ModelAndView handleForbiddenURLException(Exception e, HttpServletRequest request) {
		logger.info(e.getLocalizedMessage(), e);
        ModelAndView model = new ModelAndView("redirect:/errors/403.html");
		model.addObject("contextpath", contextpath);
		return model;
    }
	
	@ExceptionHandler(NotAgreedToTosException.class) 
    public ModelAndView handleNotAgreedToTosException(Exception e, HttpServletRequest request) {
		ModelAndView model = new ModelAndView("redirect:/auth/tos");
		model.addObject("contextpath", contextpath);
		return model;
    }
	
	@ExceptionHandler(NotAgreedToPsException.class) 
    public ModelAndView handleNotAgreedToPsException(Exception e, HttpServletRequest request) {
		ModelAndView model = new ModelAndView("redirect:/auth/ps");
		model.addObject("contextpath", contextpath);
		return model;
    }
	
	@ExceptionHandler(WeakAuthenticationException.class) 
    public ModelAndView handleWeakAuthenticationException(Exception e, HttpServletRequest request, Locale locale) {
		logger.error(e.getLocalizedMessage(), e);
		ModelAndView model = new ModelAndView("error/generic");
		String message = resources.getMessage("error.WeakAuthentication", null, "Please log in using two factor authentication in order to access the system.", locale);
		model.addObject("message", message);
		model.addObject("contextpath", contextpath);
		return model;
    }
	
	@ExceptionHandler(TooManyFiltersException.class) 
    public ModelAndView handleTooManyFiltersException(Exception e, HttpServletRequest request, Locale locale) {
		logger.error(e.getLocalizedMessage(), e);
		ModelAndView model = new ModelAndView("error/generic");
		String message = resources.getMessage("error.TooManyFilters", null, "You used too many search filters. Please use at most 3 filters at the same time", locale);
		model.addObject("message", message);
		model.addObject("contextpath", contextpath);
		return model;
    }
	
	@ExceptionHandler(InvalidXHTMLException.class) 
    public ModelAndView handleInvalidXHTMLException(InvalidXHTMLException e, Locale locale, HttpServletRequest request) {
		logger.error(e.getLocalizedMessage(), e);
		ModelAndView model = new ModelAndView("error/generic");
		String message = resources.getMessage("label.InvalidXHTMLPost", null, "The data you submitted contains invalid XHTML content. Please remove it before saving.", locale);
		message += ": " + e.getMessage();
		model.addObject("message", message);
		model.addObject("contextpath", contextpath);
		return model;
    }
	
	@ExceptionHandler(NoFormLoadedException.class) 
    public ModelAndView handleNoFormLoadedException(Exception e, Locale locale, HttpServletRequest request) {
		logger.error(e.getLocalizedMessage(), e);
		ModelAndView model = new ModelAndView("error/generic");
		String message = resources.getMessage("error.NoFormLoaded", null, "You have to load a survey before you can use this page!", locale);
		model.addObject("message", message);
		model.addObject("contextpath", contextpath);
		return model;
    }
	
	@ExceptionHandler(MessageException.class) 
    public ModelAndView handleMessageException(Exception e, Locale locale, HttpServletRequest request) {
		logger.error(e.getMessage(), e);
		ModelAndView model = new ModelAndView("error/generic");
		model.addObject("message", e.getMessage());
		model.addObject("contextpath", contextpath);
		return model;
    }
	
	@ExceptionHandler({java.net.SocketException.class, ClientAbortException.class}) 
    public void handleClientAbortException(Exception e, Locale locale, HttpServletRequest request) {
		logger.info(e.getLocalizedMessage(), e);
    }
	
	@ExceptionHandler(Exception.class) 
    public ModelAndView handleException(Exception e, Locale locale, HttpServletRequest request) {
		logger.error(e.getLocalizedMessage(), e);
		if (e instanceof IllegalArgumentException)
		{
			logger.error("caused by URL: " + request.getRequestURL().toString() + "?" + request.getQueryString());
		}		
		ModelAndView model;		
		model = new ModelAndView("redirect:/errors/500.html");		
		model.addObject("contextpath", contextpath);
		return model;
    }

	public void saveAnswerSet(AnswerSet answerSet, String fileDir, String draftid, int userid) throws Exception {
		boolean saved = false;
		
		int counter = 1;
		
		boolean existingAnswerSet = answerSet.getId() != null && answerSet.getId() > 0;
		
		String oldvalues = "";
		if (existingAnswerSet && answerSet.getSurvey().getIsDraft() && activityService.isLogEnabled(406))
		{
			oldvalues = answerService.serializeOriginal(answerSet.getId());
		}
		
		while(!saved)
		{
			try {
				answerService.internalSaveAnswerSet(answerSet, fileDir, draftid, true, true);
				if (answerSet.getId() != null)
				{
					if (existingAnswerSet)
					{
						String newvalues = answerSet.serialize();
						if (answerSet.getSurvey().getIsDraft())
						{
							activityService.log(406, answerSet.getUniqueCode() + ":" + oldvalues, answerSet.getUniqueCode() + ":" + newvalues, userid, answerSet.getSurvey().getUniqueId());
						} else {
							activityService.log(403, null, answerSet.getUniqueCode(), userid, answerSet.getSurvey().getUniqueId());
						}
					} else {
						if (answerSet.getSurvey().getIsDraft())
						{
							activityService.log(404, null, answerSet.getUniqueCode(), -1, answerSet.getSurvey().getUniqueId());
						} else {
							activityService.log(401, null, answerSet.getUniqueCode(), -1, answerSet.getSurvey().getUniqueId());
						}
					}
				}
				saved = true;
			} catch (org.hibernate.exception.LockAcquisitionException | org.springframework.dao.CannotAcquireLockException ex)
			{
				logger.info("lock on answerSet table catched; retry counter: " + counter);
				counter++;
								
				if (counter > 60)
				{
					logger.error(ex.getLocalizedMessage(), ex);
					throw ex;
				}
				
				Thread.sleep(1000);
				}
			}
		}
	
	public Survey editSave(Survey survey, HttpServletRequest request) throws Exception {
		boolean saved = false;
		int counter = 1;
				
		while(!saved)
		{
			try {
				survey = surveyService.editSave(survey, request);
				return survey;
			} catch (org.hibernate.exception.LockAcquisitionException | org.springframework.dao.CannotAcquireLockException ex)
			{
				logger.info("lock on survey table catched; retry counter: " + counter);
				counter++;
								
				if (counter > 60)
				{
					logger.error(ex.getLocalizedMessage(), ex);
					throw ex;
				}
				
				Thread.sleep(1000);
			}
		}
		return null;
	}	
	
	public ModelAndView basicwelcome(HttpServletRequest request, Locale locale) {	
		ModelAndView model = new ModelAndView("home/welcome");
		model.addObject("page", "welcome");
		model.addObject("ecasurl", ecashost);
		model.addObject("serviceurl", serverPrefix + "auth/ecaslogin");
		model.addObject("continueWithoutJavascript", true);
		if (isShowEcas()) model.addObject("showecas", true);
		// CASOSS
		if (isCasOss()) model.addObject("casoss", true);
		
		if (request.getParameter("ecaslogout") != null)
		{
			model.addObject("ECASLOGOUT", ecaslogout);
		}
		
		return model;
	}
	
	protected ModelAndView testDraftAlreadySubmittedByUniqueCode(Survey survey, String uniqueAnswerSet, Locale locale) throws ForbiddenURLException {
		if (surveyService.answerSetExists(uniqueAnswerSet, false))
		{		
			ModelAndView model = new ModelAndView("error/generic");
			model.addObject("message", resources.getMessage("error.AnswerAlreadySubmitted", null, "This answer was already submitted.", locale));
			return model;
		}
		return null;		
	}

	protected ModelAndView testDraftAlreadySubmitted(Survey survey, Draft draft, Locale locale) throws ForbiddenURLException {
		if (draft!=null)
		{
			String uniqueAnswerSet = draft.getAnswerSet().getUniqueCode();
			ModelAndView err = testDraftAlreadySubmittedByUniqueCode(survey, uniqueAnswerSet, locale);
			if (err!=null) return err;
		}
		return null;
	}

	protected boolean checkCaptcha(HttpServletRequest request)
	{
		URLConnection connection = null;  
		try {
			if(!isByPassCaptcha()){
				
				String captcha = settingsService.get("captcha");
				if (captcha.equalsIgnoreCase("recaptcha"))
				{				
					sessionService.initializeProxy();
					
					String str = request.getParameter("g-recaptcha-response");
					URL url = new URL("https://www.google.com/recaptcha/api/siteverify?secret=" + captchasecret + "&response=" + str);
					connection = url.openConnection();
					BufferedReader in = new BufferedReader(
	                         new InputStreamReader(
	                         connection.getInputStream()));
					String inputLine;
					while ((inputLine = in.readLine()) != null) 
			            if (inputLine.contains("success"))
			            {
			            	in.close();
			            	return inputLine.contains("true");
			            }
			        in.close();
				} else if (captcha.equalsIgnoreCase("internal")) {
					String str = request.getParameter("j_captcha_response");
					if (str == null) str = request.getParameter("g-recaptcha-response");
					
					boolean validCaptcha = false;
					try {
						validCaptcha = captchaService.validateResponseForID(request.getSession().getId(), str);
					} catch (CaptchaServiceException e) {
						//should not happen, may be thrown if the id is not valid
						return false; 
					}
					return validCaptcha;
				} else {
					return true;
				}
		        return false;        
			}else{
				logger.error("checkCaptcha Has been bypassing");
				return true;
			}
		} catch (NullPointerException npe)
		{
			//this happens when the captcha was not displayed. We can ignore it here as that is handled on the page itself
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
		}	
		return false;
	}
	
	protected boolean archiveSurvey(Survey survey, User u) throws IOException {
		Archive archive = new Archive();
		archive.setArchived(new Date());
		archive.setCreated(survey.getCreated());
		
		String title = ConversionTools.removeHTML(survey.getTitle(), true).replace("\"", "'");
		if (title.length() > 250) title = title.substring(0, 250) + "...";
		
		archive.setSurveyTitle(title);
		archive.setSurveyUID(survey.getUniqueId());
		archive.setReplies(answerService.getNumberOfAnswerSetsPublished(survey.getShortname(), survey.getUniqueId()));
		
		archive.setSurveyHasUploadedFiles(survey.getHasUploadElement());
		
		archive.setSurveyShortname(survey.getShortname());
		archive.setOwner(survey.getOwner().getName());
		archive.setUserId(u.getId());
		StringBuilder langs = new StringBuilder();
		if (survey.getTranslations() != null)
		for (String s : survey.getTranslations())
		{
			langs.append(s);
		}
		archive.setLanguages(langs.toString());
		archiveService.add(archive);
		
		if (useworkerserver.equalsIgnoreCase("true") && isworkerserver.equalsIgnoreCase("false"))
		{
			logger.info("calling worker server for archiving survey " + survey.getId());
			
			URL workerurl = new URL(workerserverurl + "worker/startArchive/" + archive.getId());
			
			try {			
				URLConnection wc = workerurl.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(wc.getInputStream()));
				String inputLine;
				StringBuilder result = new StringBuilder();
				while ((inputLine = in.readLine()) != null) result.append(inputLine);
				in.close();
				
				if (!result.toString().equals("OK"))
				{
					logger.error("calling worker server for archiving survey " + survey.getId() + " returned " + result);
					return false;
				}
				
				surveyService.removeFromSessionCache(survey);
				return true;
			} catch (ConnectException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		} 
		
		ArchiveExecutor export = (ArchiveExecutor) context.getBean("archiveExecutor"); 
		export.init(archive, survey, u);
		export.prepare();
		taskExecutorLong.execute(export);		
		
		return true;
	}	
}
