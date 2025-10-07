package com.ec.survey.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ec.survey.model.survey.*;
import com.ec.survey.tools.activity.ActivityRegistry;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.exception.DataException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.ec.survey.exception.ForbiddenURLException;
import com.ec.survey.exception.FrozenSurveyException;
import com.ec.survey.exception.InvalidURLException;
import com.ec.survey.exception.MessageException;
import com.ec.survey.exception.NoFormLoadedException;
import com.ec.survey.exception.TooManyFiltersException;
import com.ec.survey.exception.AccessDeniedException;
import com.ec.survey.exception.httpexception.ForbiddenException;
import com.ec.survey.exception.httpexception.InternalServerErrorException;
import com.ec.survey.exception.httpexception.NotFoundException;
import com.ec.survey.exception.httpexception.UnauthorizedException;
import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.Archive;
import com.ec.survey.model.Draft;
import com.ec.survey.model.Setting;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.chargeback.SubmittedContribution;
import com.ec.survey.service.ActivityService;
import com.ec.survey.service.AdministrationService;
import com.ec.survey.service.AnswerExplanationService;
import com.ec.survey.service.AnswerService;
import com.ec.survey.service.ArchiveService;
import com.ec.survey.service.AttendeeService;
import com.ec.survey.service.ECFService;
import com.ec.survey.service.EVoteService;
import com.ec.survey.service.ExportService;
import com.ec.survey.service.FileService;
import com.ec.survey.service.LdapDBService;
import com.ec.survey.service.LdapService;
import com.ec.survey.service.ParticipationService;
import com.ec.survey.service.ReportingServiceProxy;
import com.ec.survey.service.SelfAssessmentService;
import com.ec.survey.service.SessionService;
import com.ec.survey.service.SettingsService;
import com.ec.survey.service.SkinService;
import com.ec.survey.service.SurveyService;
import com.ec.survey.service.SystemService;
import com.ec.survey.service.TranslationService;
import com.ec.survey.tools.ArchiveExecutor;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.InvalidXHTMLException;
import com.ec.survey.tools.NotAgreedToTosException;
import com.ec.survey.tools.NotAgreedToPsException;
import com.ec.survey.tools.WeakAuthenticationException;

@Controller
public class BasicController implements BeanFactoryAware {

	protected static final Logger logger = Logger.getLogger(BasicController.class);

	@Autowired
	protected MessageSource resources;

	@Resource(name = "answerService")
	protected AnswerService answerService;
	
	@Resource(name = "answerExplanationService")
	protected AnswerExplanationService answerExplanationService;

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

	@Resource(name = "settingsService")
	protected SettingsService settingsService;

	@Resource(name = "reportingServiceProxy")
	protected ReportingServiceProxy reportingService;

	@Resource(name = "ecfService")
	protected ECFService ecfService;
	
	@Resource(name = "eVoteService")
	protected EVoteService eVoteService;
	
	@Resource(name = "selfassessmentService")
	protected SelfAssessmentService selfassessmentService;
	
	public @Value("${captcha.secret}") String captchasecret;
	public @Value("${captcha.serverprefix}") String captchaserverprefix;
	public @Value("${captcha.serverprefixtarget}") String captchaserverprefixtarget;
	public @Value("${captcha.token:#{null}}") String captchatoken;
	public @Value("${captcha.x-bypass-secret:#{null}}") String captchabypasssecret;
	public @Value("${ui.enableresponsive}") String enableresponsive;
	private @Value("${ecaslogout}") String ecaslogout;
	public @Value("${showecas}") String showecas;
	public @Value("${ecashost}") String ecashost;
	public @Value("${sender}") String sender;
	public @Value("${captcha.bypass:@null}") String bypassCaptcha;
	public @Value("${ui.enablepublicsurveys}") String enablepublicsurveys;
	public @Value("${enablereportingdatabase}") String enablereportingdatabase;

	public @Value("${ecas.require2fa:#{false}}") boolean require2fa;

	// OCAS
	public @Value("${casoss}") String cassOss;
	protected @Value("${contextpath}") String contextpath;

	@Autowired
	public ServletContext servletContext;

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
	public void setBeanFactory(BeanFactory beanFactory) {
		context = beanFactory;
	}

	public boolean isShowPrivacy() {
		return (!StringUtils.isEmpty(showPrivacy) && showPrivacy.equalsIgnoreCase("true"));
	}

	public boolean isOss() {
		return (!StringUtils.isEmpty(oss) && oss.equalsIgnoreCase("true"));
	}

	public boolean isShowEcas() {
		return showecas != null && showecas.equalsIgnoreCase("true");
	}

	public boolean isCasOss() {
		return cassOss != null && cassOss.equalsIgnoreCase("true");
	}

	public boolean isReportingDatabaseEnabled() {
		return enablereportingdatabase != null && enablereportingdatabase.equalsIgnoreCase("true");
	}

	public boolean isChargeBackeEnabled() {
		String enabled = settingsService.get(Setting.EnableChargeback);
		return enabled != null && enabled.equalsIgnoreCase("true");
	}
	
	public static boolean isAjax(HttpServletRequest request) {
		return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
	}

	public boolean isByPassCaptcha() {
		return bypassCaptcha != null && bypassCaptcha.equalsIgnoreCase("true");
	}
	
	@ExceptionHandler(com.ec.survey.tools.SurveyCreationLimitExceededException.class)
	public ModelAndView handleSurveyCreationLimitExceededException(Exception e, HttpServletRequest request) {
		logger.info(e.getLocalizedMessage(), e);
		ModelAndView model = new ModelAndView("redirect:/errors/surveylimit.html");
		model.addObject("contextpath", contextpath);		
		return model;
	}

	@ExceptionHandler(com.ec.survey.tools.Bad2faCredentialsException.class)
	public ModelAndView handleBad2faCredentialsException(Exception e, HttpServletRequest request) {
		logger.info(e.getLocalizedMessage(), e);
		ModelAndView model = new ModelAndView("redirect:/errors/2fa.html");
		model.addObject("contextpath", contextpath);
		return model;
	}

	@ExceptionHandler(com.ec.survey.tools.FrozenCredentialsException.class)
	public ModelAndView handleFrozenCredentialsException(Exception e, HttpServletRequest request) {
		logger.info(e.getLocalizedMessage(), e);
		ModelAndView model = new ModelAndView("redirect:/errors/frozen.html");
		model.addObject("contextpath", contextpath);
		return model;
	}

	@ExceptionHandler(InvalidURLException.class)
	public ModelAndView handleInvalidURLException(Exception e, HttpServletRequest request) {
		logger.info(e.getLocalizedMessage(), e);
		ModelAndView model = new ModelAndView("redirect:/errors/404.html");
		model.addObject("is404", true);
		model.addObject("contextpath", contextpath);
		return model;
	}

	@ExceptionHandler(FrozenSurveyException.class)
	public ModelAndView handleFrozenSurveyException(Exception e, HttpServletRequest request, Locale locale) {
		logger.error(e.getLocalizedMessage(), e);
		ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
		String message = resources.getMessage("error.FrozenSurvey", null,
				"This survey has been blocked due to an infringement to our policy. We are sorry for the inconvenience this may cause. Please try again later.",
				locale);
		model.addObject(Constants.MESSAGE, message);
		model.addObject("contextpath", contextpath);

		String uisessiontimeout = settingsService.get("uisessiontimeout");
		model.getModelMap().addAttribute("uisessiontimeout", uisessiontimeout);

		return model;
	}

	@ExceptionHandler(ForbiddenURLException.class)
	public ModelAndView handleForbiddenURLException(Exception e, HttpServletRequest request) {
		logger.info(e.getLocalizedMessage(), e);
		ModelAndView model = new ModelAndView("redirect:/errors/403.html");
		model.addObject("contextpath", contextpath);
		return model;
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ModelAndView handleAccessDeniedException(Exception e, HttpServletRequest request) {
		logger.info(e.getLocalizedMessage(), e);
		ModelAndView model = new ModelAndView("redirect:/errors/accessdenied.html");
		model.addObject("contextpath", contextpath);
		return model;
	}
	

	@ExceptionHandler(NotAgreedToTosException.class)
	public ModelAndView handleNotAgreedToTosException(Exception e, HttpServletRequest request) {
		ModelAndView model = new ModelAndView("redirect:/auth/tos");
		model.addObject("contextpath", contextpath);
		var sessionCache = new HttpSessionRequestCache();
		sessionCache.saveRequest(request, null);
		return model;
	}

	@ExceptionHandler(NotAgreedToPsException.class)
	public ModelAndView handleNotAgreedToPsException(Exception e, HttpServletRequest request) {
		ModelAndView model = new ModelAndView("redirect:/auth/ps");
		model.addObject("contextpath", contextpath);
		var sessionCache = new HttpSessionRequestCache();
		sessionCache.saveRequest(request, null);
		return model;
	}

	@ExceptionHandler(WeakAuthenticationException.class)
	public ModelAndView handleWeakAuthenticationException(Exception e, HttpServletRequest request, Locale locale) {
		logger.error(e.getLocalizedMessage(), e);
		ModelAndView model = new ModelAndView("redirect:/errors/weak.html");
		model.addObject("contextpath", contextpath);
		return model;
	}

	@ExceptionHandler(TooManyFiltersException.class)
	public ModelAndView handleTooManyFiltersException(Exception e, HttpServletRequest request, Locale locale) {
		logger.error(e.getLocalizedMessage(), e);
		ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
		String message = resources.getMessage("error.TooManyFilters", null,
				"You used too many search filters. Please use at most 3 filters at the same time", locale);
		model.addObject(Constants.MESSAGE, message);
		model.addObject("contextpath", contextpath);
		return model;
	}

	@ExceptionHandler(InvalidXHTMLException.class)
	public ModelAndView handleInvalidXHTMLException(InvalidXHTMLException e, Locale locale,
			HttpServletRequest request) {
		logger.error(e.getLocalizedMessage(), e);
		ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
		String message = resources.getMessage("label.InvalidXHTMLPost", null,
				"The data you submitted contains invalid XHTML content. Please remove it before saving.", locale);
		message += ": " + e.getMessage();
		model.addObject(Constants.MESSAGE, message);
		model.addObject("contextpath", contextpath);
		return model;
	}

	@ExceptionHandler(NoFormLoadedException.class)
	public ModelAndView handleNoFormLoadedException(Exception e, Locale locale, HttpServletRequest request) {
		logger.error(e.getLocalizedMessage(), e);
		ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
		String message = resources.getMessage("error.NoFormLoadedNew", null,
				"You have to load a survey before using this page!", locale);
		model.addObject(Constants.MESSAGE, message);
		model.addObject("contextpath", contextpath);
		return model;
	}

	@ExceptionHandler(MessageException.class)
	public ModelAndView handleMessageException(Exception e, Locale locale, HttpServletRequest request) {
		logger.error(e.getMessage(), e);
		ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
		model.addObject(Constants.MESSAGE, e.getMessage());
		model.addObject("contextpath", contextpath);
		return model;
	}

	@ExceptionHandler({ java.net.SocketException.class, ClientAbortException.class })
	public void handleClientAbortException(Exception e, Locale locale, HttpServletRequest request) {
		logger.info(e.getLocalizedMessage(), e);
	}
	
	@ExceptionHandler({ DataException.class })
	public void handleHibernateException(Exception e, Locale locale, HttpServletRequest request) {
		logger.error(e.getLocalizedMessage(), e);
		if (e.getCause() != null) {
			logger.error(e.getCause().getLocalizedMessage(), e.getCause());
		}
	}

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(UnauthorizedException.class)
	public void handleUnauthorizedException(UnauthorizedException e) {
		// nothing else to do
	}

	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(ForbiddenException.class)
	public void handleForbiddenException(ForbiddenException e) {
		// nothing else to do
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NotFoundException.class)
	public void handleNotFoundException(NotFoundException e) {
		// nothing else to do
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(InternalServerErrorException.class)
	public void handleInternalServerErrorException(InternalServerErrorException e) {
		logger.error(e.getLocalizedMessage(), e);
		// nothing else to do
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public ModelAndView handleBadCredentialsException(BadCredentialsException e, Locale locale, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if ("VOTERREJECTED".equals(e.getMessage())) {
			logger.error(e.getMessage(), e);
			ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
			String message = resources.getMessage("error.InvalidVoter", null,
					"Your account is either not allowed to participate in this vote or you have already voted.", locale);
			model.addObject(Constants.MESSAGE, message);
			model.addObject("contextpath", contextpath);
			return model;
		};		
		
		return handleException((Exception)e, locale, request, response);
	}

	@ExceptionHandler(Exception.class)
	public ModelAndView handleException(Exception e, Locale locale, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		logger.error(e.getLocalizedMessage(), e);
		if (e instanceof IllegalArgumentException) {
			logger.error("caused by URL: " + request.getRequestURL().toString() + "?" + request.getQueryString());
		}

		if (!response.getOutputStream().isReady()) {
			logger.error("Exception thrown after outputstream was closed, caused by URL: "
					+ request.getRequestURL().toString() + "?" + request.getQueryString());
			// return null;
		}

		ModelAndView model;
		model = new ModelAndView("redirect:/errors/500.html");
		model.addObject("contextpath", contextpath);
		return model;
	}

	public void saveAnswerSet(AnswerSet answerSet, String fileDir, String draftid, int userid, HttpServletRequest request) throws Exception {
		boolean saved = false;

		int counter = 1;

		boolean existingAnswerSet = answerSet.getId() != null && answerSet.getId() > 0;

		String oldvalues = "";
		if (existingAnswerSet && answerSet.getSurvey().getIsDraft() && activityService.isLogEnabled(ActivityRegistry.ID_TEST_EDIT)) {
			oldvalues = answerService.serializeOriginal(answerSet.getId());
		}
		
		if (answerSet.getSurvey().getIsEVote()) {
			String ecmoniker = (String) request.getSession().getAttribute("ECMONIKER");
			if (ecmoniker == null || !eVoteService.checkVoter(answerSet.getSurvey().getUniqueId(), ecmoniker)) {
				throw new MessageException("Invalid voter");
			}
			
			eVoteService.setVoted(answerSet.getSurvey().getUniqueId(), ecmoniker);
		}

		while (!saved) {
			try {
				
				answerService.internalSaveAnswerSet(answerSet, fileDir, draftid, true, true);
				sessionService.ClearUniqueCodeForForm(request, answerSet.getSurvey().getId());
				if (answerSet.getId() != null) {
					if (existingAnswerSet) {
						String newvalues = answerSet.serialize();
						if (answerSet.getSurvey().getIsDraft()) {
							activityService.log(ActivityRegistry.ID_TEST_EDIT, answerSet.getUniqueCode() + ":" + oldvalues,
									answerSet.getUniqueCode() + ":" + newvalues, userid,
									answerSet.getSurvey().getUniqueId());
						} else {
							activityService.log(ActivityRegistry.ID_CONTRIBUTION_EDIT, null, answerSet.getUniqueCode(), userid,
									answerSet.getSurvey().getUniqueId());
						}
					} else {
						if (answerSet.getSurvey().getIsDraft()) {
							activityService.log(ActivityRegistry.ID_TEST_SUBMIT, null, answerSet.getUniqueCode(), -1,
									answerSet.getSurvey().getUniqueId());
						} else {
							activityService.log(ActivityRegistry.ID_CONTRIBUTION_SUBMIT, null, answerSet.getUniqueCode(), -1,
									answerSet.getSurvey().getUniqueId());
						}
					}
				}
				
				if (!answerSet.getIsDraft() && !answerSet.getSurvey().getIsDraft() && !existingAnswerSet) {
					answerService.chargeSubmission(answerSet);				
				}
				
				if (answerSet.getSurvey().getWebhook() != null && answerSet.getSurvey().getWebhook().length() > 0) {
					answerService.callHook(answerSet.getSurvey().getWebhook());
				}
				
				saved = true;
			} catch (org.hibernate.exception.LockAcquisitionException
					| org.springframework.dao.CannotAcquireLockException ex) {
				logger.info("lock on answerSet table catched; retry counter: " + counter);
				counter++;

				if (counter > 60) {
					logger.error(ex.getLocalizedMessage(), ex);
					throw ex;
				}

				Thread.sleep(1000);
			}
		}
	}

	public Survey editSave(Survey survey, HttpServletRequest request)
			throws InvalidXHTMLException, NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException,
			IOException, InterruptedException {
		int counter = 1;

		while (true) {
			try {
				survey = surveyService.editSave(survey, request);
				return survey;
			} catch (org.hibernate.exception.LockAcquisitionException
					| org.springframework.dao.CannotAcquireLockException ex) {
				logger.info("lock on survey table catched; retry counter: " + counter);
				counter++;

				if (counter > 60) {
					logger.error(ex.getLocalizedMessage(), ex);
					throw ex;
				}

				Thread.sleep(1000);
			}
		}
	}

	public boolean parseEVoteSurvey(Survey survey) {
		boolean passedSC = false;
		PossibleAnswer linkedSCAnswer = null;
		boolean passedMC = true;

		for(Question q : survey.getQuestions()) {
			if (q instanceof SingleChoiceQuestion) {
				List<PossibleAnswer> pa = ((SingleChoiceQuestion) q).getPossibleAnswers();
				for (PossibleAnswer answer : pa) {
					if (answer.getTitle().equals("I want to vote")) {
						passedSC = true;
						linkedSCAnswer = answer;
						break;
					}
				}
			} else if (q instanceof MultipleChoiceQuestion) {
				MultipleChoiceQuestion mq = ((MultipleChoiceQuestion) q);
				if (passedSC && !(mq.getTriggers()).equals(linkedSCAnswer.getId() + ";")) {
					passedMC = false;
				}
			}
		}

		if (passedSC && passedMC) {
			return false;
		} else {
			return true;
		}
	}

	public ModelAndView basicwelcome(HttpServletRequest request) {

		Boolean weakAuthentication = request.getSession().getAttribute("WEAKAUTHENTICATION") == null ? false : (Boolean) request.getSession().getAttribute("WEAKAUTHENTICATION");
		if (weakAuthentication) {
			//probably coming from the form runner: log the user out
			request.getSession().invalidate();
		}

		ModelAndView model = new ModelAndView("home/welcome");
		model.addObject("page", "welcome");
		model.addObject("ecasurl", ecashost);
		model.addObject("require2fa", require2fa);
		model.addObject("serviceurl", serverPrefix + "auth/ecaslogin");
		model.addObject("continueWithoutJavascript", true);
		if (isShowEcas())
			model.addObject("showecas", true);
		// CASOSS
		if (isCasOss())
			model.addObject("casoss", true);

		if (request.getParameter("ecaslogout") != null) {
			model.addObject("ECASLOGOUT", ecaslogout);
		}
		
		model.addObject("allowIndex", true);

		return model;
	}

	protected ModelAndView testDraftAlreadySubmittedByUniqueCode(String uniqueAnswerSet, Locale locale) {
		if (surveyService.answerSetExists(uniqueAnswerSet, false, true)) {
			ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
			model.addObject(Constants.MESSAGE, resources.getMessage("error.AnswerAlreadySubmitted", null,
					"This answer was already submitted.", locale));
			return model;
		}
		return null;
	}

	protected ModelAndView testDraftAlreadySubmitted(Draft draft, Locale locale) {
		if (draft != null) {
			String uniqueAnswerSet = draft.getAnswerSet().getUniqueCode();
			ModelAndView err = testDraftAlreadySubmittedByUniqueCode(uniqueAnswerSet, locale);
			if (err != null)
				return err;
		}
		return null;
	}

	protected boolean checkCaptcha(HttpServletRequest request) {
		URLConnection connection = null;
		try {
			if (!isByPassCaptcha()) {
				String captcha = settingsService.get("captcha");
				if (captcha.equalsIgnoreCase("recaptcha")) {
					sessionService.initializeProxy();

					String str = request.getParameter("g-recaptcha-response");
					URL url = new URL("https://www.google.com/recaptcha/api/siteverify?secret=" + captchasecret
							+ "&response=" + str);
					connection = url.openConnection();
					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String inputLine;
					while ((inputLine = in.readLine()) != null)
						if (inputLine.contains("success")) {
							in.close();
							return inputLine.contains("true");
						}
					in.close();
				}
				if (captcha.equalsIgnoreCase("internal")) {
					String str = request.getParameter("internal_captcha_response");
					if (str == null)
					{
						str = request.getParameter("g-recaptcha-response");
					}
					
					return sessionService.getCaptchaText(request).equals(str);
				} 
				if (captcha.equalsIgnoreCase("eucaptcha")) {
					String str = request.getParameter("internal_captcha_response");
					if (str == null)
					{
						str = request.getParameter("g-recaptcha-response");
					}
					
					String id = request.getParameter("captcha_id");
					String useaudio = request.getParameter("captcha_useaudio");
					String originalcookies = request.getParameter("captcha_original_cookies");
										
					if (str == null || id == null) {
						return false;
					}
					
					sessionService.initializeProxy();
					URL url = new URL(captchaserverprefixtarget + "validateCaptcha/" + id);
					HttpURLConnection conn = (HttpURLConnection)url.openConnection();
					conn.setRequestMethod("POST");
					conn.setRequestProperty("xJwtString", captchatoken);
					conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

					if (captchabypasssecret != null) {
						conn.setRequestProperty("x-bypass-secret", captchabypasssecret);
					}
					
					String[] cookies = originalcookies.split("#");			
					for (String cookie : cookies) {
						conn.addRequestProperty("Cookie", cookie);
					}	
										
					String postData = "captchaAnswer="  + str + "&useAudio=" + ("true".equalsIgnoreCase(useaudio));
					byte[] postDataBytes = postData.getBytes("UTF-8");
					
					conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
				    conn.setDoOutput(true);
				    conn.getOutputStream().write(postDataBytes);
				    
				    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				    StringBuilder sb = new StringBuilder();
			        for (int c; (c = in.read()) >= 0;) {
			            sb.append((char)c);
			        }
			        String response = sb.toString();
					in.close();
					
					return response.equalsIgnoreCase("{\"responseCaptcha\":\"success\"}");
				}
				return false;
			} else {
				return true;
			}
		} catch (NullPointerException npe) {
			// this happens when the captcha was not displayed. We can ignore it here as
			// that is handled on the page itself
		} catch (IOException ioe) {
			// this happens when the eucaptcha returns "unsuccessful"			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return false;
	}
	
	protected boolean answerSetContainsAnswerForQuestion(AnswerSet answerSet, Element question) {
		if (question instanceof Matrix) {
			return !answerSet.getMatrixAnswers((Matrix) question).isEmpty();
		}

		if (question instanceof RatingQuestion) {
			for (Element childQuestion : ((RatingQuestion) question).getChildElements()) {
				if (!answerSet.getAnswers(childQuestion.getUniqueId()).isEmpty()) {
					return true;
				}
			}
			return false;
		}
		
		return !answerSet.getAnswers(question.getUniqueId()).isEmpty();
	}

	protected Map<String, String> getMapForRequestParameters(HttpServletRequest request) {
		var values = new HashMap<String, String>();
		for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
			if (entry.getKey().equalsIgnoreCase("token")){
				continue;
			}
			if (!values.containsKey(entry.getKey())){
				values.put(entry.getKey(), entry.getValue()[0]);
			}
		}

		if (request.getSession().getAttribute("ECASSURVEYPARAMS") != null) {
			String params = request.getSession().getAttribute("ECASSURVEYPARAMS").toString();
			//if (params.contains("&")) {
				var p = params.split("&");
				for (String entry : p) {
					if (entry.contains("=")) {
						var l = entry.split("=");
						var name = URLDecoder.decode(l[0], StandardCharsets.UTF_8);
						var value = URLDecoder.decode(l[1], StandardCharsets.UTF_8);
						if (!name.isEmpty() && !value.isEmpty()) {
							if (!values.containsKey(name)){
								values.put(name, value);
							}
						}
					}
				}
			//}
			request.getSession().removeAttribute("ECASSURVEYPARAMS"); // use only directly after login
		}

		return values;
	}
}
