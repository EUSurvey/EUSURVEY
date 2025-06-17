package com.ec.survey.service;

import com.ec.survey.exception.ForbiddenException;
import com.ec.survey.exception.ForbiddenURLException;
import com.ec.survey.exception.InternalServerErrorException;
import com.ec.survey.exception.InvalidURLException;
import com.ec.survey.exception.NoFormLoadedException;
import com.ec.survey.model.*;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.LocalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.Element;
import com.ec.survey.model.survey.Question;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.NotAgreedToPsException;
import com.ec.survey.tools.NotAgreedToTosException;
import com.ec.survey.tools.Tools;
import com.ec.survey.tools.WeakAuthenticationException;

import com.ec.survey.tools.activity.ActivityRegistry;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.net.*;
import java.util.*;

@Service("sessionService")
public class SessionService extends BasicService {

	@Resource(name = "ldapService")
	private LdapService ldapService;

	private @Value("${proxy.host}") String proxyHost;
	private @Value("${proxy.port}") String proxyPort;
	private @Value("${proxy.user}") String proxyUser;
	private @Value("${proxy.password}") String proxyPassword;
	private @Value("${pdfserver.prefix}") String pdfServerPrefix;
	private @Value("${proxy.nonProxyHosts}") String proxyNonProxyHosts;


	public void setCaptchaText(HttpServletRequest request, String text) {
		request.getSession().setAttribute("captcha", text);
	}

	public String getCaptchaText(HttpServletRequest request) {
		return (String) request.getSession().getAttribute("captcha");
	}
	
	public User getCurrentUser(HttpServletRequest request)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException {
		return getCurrentUser(request, true);
	}

	public User getCurrentUser(HttpServletRequest request, boolean strictChecks)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException {
		return getCurrentUser(request, strictChecks, strictChecks);
	}

	public User getCurrentUser(HttpServletRequest request, boolean checkTOS, boolean checkWeakAuthentication)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException {
		if (request == null)
			return null;

		User user = (User) request.getSession().getAttribute("USER");
		Boolean weakAuthentication = (Boolean) request.getSession().getAttribute("WEAKAUTHENTICATION");

		if (user != null) {
			Session session = sessionFactory.getCurrentSession();
			user = (User) session.merge(user);

			String weakAuthenticationDisabled = settingsService.get(Setting.WeakAuthenticationDisabled);

			if (weakAuthenticationDisabled.equalsIgnoreCase("true") && checkWeakAuthentication
					&& user.getType().equalsIgnoreCase(User.ECAS) && user.isExternal() && weakAuthentication) {
				throw new WeakAuthenticationException();
			}

			String disabled = settingsService.get(Setting.CreateSurveysForExternalsDisabled);
			if (disabled.equalsIgnoreCase("true") && user.isExternal()) {
				user.setCanCreateSurveys(false);
			}
		}

		if (checkTOS && user != null && !user.isAgreedToPS()) {
			throw new NotAgreedToPsException();
		}

		if (checkTOS && user != null && !user.isAgreedToToS()) {
			throw new NotAgreedToTosException();
		}

		return user;
	}

	public void setCurrentUser(HttpServletRequest request, User user) {
		request.getSession().setAttribute("USER", user);
	}

	public boolean checkUser(int userid, HttpServletRequest request) {
		User sessionUser = (User) request.getSession().getAttribute("USER");
		return sessionUser.getId() == userid;
	}

	public Form getFormOrNull(HttpServletRequest request, String shortname, boolean loadReplies) {
		try {
			return getForm(request, shortname, loadReplies, false);
		} catch (Exception ex) {
			logger.debug(ex);
		}
		return null;
	}

	public Form getForm(HttpServletRequest request, String shortname, boolean loadReplies, boolean synchronize)
			throws Exception {
		// first check if a survey id was specified as url parameter
		if (request.getParameter(Constants.SURVEY) != null) {
			String id = request.getParameter(Constants.SURVEY);
			User user = getCurrentUser(request);
			Survey survey = surveyService.getSurvey(Integer.parseInt(id), false, true);
			if (survey != null)
				return checkSurvey(survey, user, request);
		}

		// then check if a shortname was specified as url parameter
		if (shortname != null && !shortname.equalsIgnoreCase("noform")) {
			User user = getCurrentUser(request);
			Survey survey = surveyService.getSurvey(shortname, true, false, loadReplies, false, null, true,
					synchronize);
			if (survey != null)
				return checkSurvey(survey, user, request);
			throw new InvalidURLException();
		}

		Form form = getFormFromSessionInfo(request);
		if (form != null)
			return form;

		if (request.getParameter(Constants.SHORTNAME) != null) {
			String alias = request.getParameter(Constants.SHORTNAME);
			User user = getCurrentUser(request);
			Survey survey = surveyService.getSurvey(alias, true, false, loadReplies, false, null, true, synchronize);
			if (survey != null)
				return checkSurvey(survey, user, request);
			throw new InvalidURLException();
		}

		throw new NoFormLoadedException();

	}

	public Form getFormFromSessionInfo(HttpServletRequest request) throws Exception {
		SessionInfo info = (SessionInfo) request.getSession().getAttribute("sessioninfo");

		if (info != null) {
			Survey survey = surveyService.getSurvey(info.getSurvey(), info.getLanguage());

			Form form = new Form(survey, translationService.getTranslationsForSurvey(survey.getId(), true),
					survey.getLanguage(), resources, contextpath);
			form.setNumberOfAnswerSets(answerService.getNumberOfAnswerSets(survey, new ResultFilter()));

			String languageCode = info.getLanguage();
			if (request.getParameter("slang") != null && request.getParameter("slang").trim().length() > 0) {
				languageCode = request.getParameter("slang").trim();
				form.setLanguage(surveyService.getLanguage(languageCode));
			} else if (form.getLanguage() != null) {
				languageCode = form.getLanguage().getCode();
			}

			if (info.getLanguage() != null && !survey.getLanguage().getCode().equalsIgnoreCase(languageCode)) {
				survey = surveyService.getSurvey(survey.getId(), languageCode);
				form.setSurvey(survey);
				info.setLanguage(languageCode);
				request.getSession().setAttribute("sessioninfo", info);
			}

			return form;
		}

		return null;
	}

	public boolean userIsResultReadAuthorized(Survey survey, HttpServletRequest request) throws
	NotAgreedToTosException,
	WeakAuthenticationException,
	NotAgreedToPsException,
	InternalServerErrorException,
	ForbiddenException {
		User user = this.getCurrentUser(request);
		try {
			this.sessionService.upgradePrivileges(survey, user, request);
		} catch (Exception e1) {
			throw new InternalServerErrorException(e1);
		}
		if (!survey.getOwner().getId().equals(user.getId())) {
			if (!(user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) >= 2)) {
				if (!(user.getLocalPrivileges().get(LocalPrivilege.FormManagement) >= 1)) {
					if (!(user.getLocalPrivileges().get(LocalPrivilege.AccessResults) >= 1)) {
						throw new ForbiddenException();
					}
				}
			}
		}
		return true;
	}

	public boolean userIsResultOrDraftReadAuthorized(Survey survey, HttpServletRequest request) throws
			NotAgreedToTosException,
			WeakAuthenticationException,
			NotAgreedToPsException,
			InternalServerErrorException,
			ForbiddenException {
		User user = this.getCurrentUser(request);
		try {
			this.sessionService.upgradePrivileges(survey, user, request);
		} catch (Exception e1) {
			throw new InternalServerErrorException(e1);
		}
		if (!survey.getOwner().getId().equals(user.getId())) {
			if (!(user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) >= 2)) {
				if (!(user.getLocalPrivileges().get(LocalPrivilege.FormManagement) >= 1)) {
					if (!(user.getLocalPrivileges().get(LocalPrivilege.AccessResults) >= 1)) {
						if (!(user.getLocalPrivileges().get(LocalPrivilege.AccessDraft) >= 1)) {
							if (!(survey.getDedicatedResultPrivileges() && surveyService.getResultAccess(survey.getUniqueId(), user.getId()) != null)) {
								throw new ForbiddenException();
							}
						}
					}
				}
			}
		}
		return true;
	}
	
	public boolean userCanEditResults(Survey survey, User user, HttpServletRequest request) {
		if (survey.getOwner().getId().equals(user.getId()))
			return true;
		if (user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2)
			return true;

		try {
			upgradePrivileges(survey, user, request);
			if (user.getLocalPrivileges().get(LocalPrivilege.FormManagement) == 2)
				return true;
			
			if (user.getLocalPrivileges().get(LocalPrivilege.AccessResults) == 2)
				return true;

			boolean isDraft = request != null && request.getParameter("results-source") != null
					&& request.getParameter("results-source").equalsIgnoreCase("draft");
			if (isDraft && user.getLocalPrivileges().get(LocalPrivilege.AccessDraft) == 2)
				return true;
			
			if (user.getResultAccess() != null && !user.getResultAccess().isReadonly()) {
				return true;
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return false;
	}

	public boolean userIsFormReaderOrAdmin(Survey survey, User user, HttpServletRequest request) {
		if (survey.getOwner().getId().equals(user.getId()))
			return true;
		if (user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2)
			return true;

		try {
			upgradePrivileges(survey, user, request);
			if (user.getLocalPrivileges().get(LocalPrivilege.FormManagement) >= 1)
				return true;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return false;
	}

	public boolean userIsFormAdmin(Survey survey, User user, HttpServletRequest request) {
		if (survey.getOwner().getId().equals(user.getId()))
			return true;
		if (user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2)
			return true;

		try {
			upgradePrivileges(survey, user, request);
			if (user.getLocalPrivileges().get(LocalPrivilege.FormManagement) == 2)
				return true;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return false;
	}

	public boolean userIsAnswerer(AnswerSet answerSet, User user) {
		if (answerSet != null && answerSet.getResponderEmail() != null && user.getEmail() != null
				&& (answerSet.getResponderEmail().equalsIgnoreCase(user.getEmail())
						|| answerSet.getResponderEmail().equalsIgnoreCase(Tools.md5hash(user.getEmail())))) {
			// User's email is the same
			return true;
		}
		if (answerSet != null && answerSet.getSurvey() != null && answerSet.getSurvey().getOwner() != null
				&& answerSet.getSurvey().getOwner().getId().equals(user.getId())) {
			// User is same ID
			return true;
		}
		return false;
	}

	public boolean userIsFormManager(Survey survey, User user, HttpServletRequest request) {
		if (survey.getOwner().getId().equals(user.getId()))
			return true;
		if (user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2)
			return true;

		try {
			upgradePrivileges(survey, user, request);
			if (user.getLocalPrivileges().get(LocalPrivilege.FormManagement) > 0)
				return true;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return false;
	}

	public void upgradePrivileges(Survey survey, User user, HttpServletRequest request) throws ForbiddenURLException {
		if (user == null)
			throw new ForbiddenURLException();
		
		user.setResultAccess(null);	

		Access access = null;
		if (!survey.getOwner().getId().equals(user.getId())) {
			if (user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2) {
				boolean allowed = false;

				access = surveyService.getAccess(survey.getId(), user.getId());
				if (access != null && access.hasAnyPrivileges()) {
					user.setLocalPrivileges(access.getLocalPrivileges());
					allowed = true;
				}

				// check access by ecas group
				if (user.getType().equalsIgnoreCase("ecas")) {
					List<String> groups = ldapService.getUserLDAPGroups(user.getLogin());
					for (String group : groups) {
						access = surveyService.getGroupAccess(survey.getId(), group);
						if (access != null && access.hasAnyPrivileges()) {
							user.upgradePrivileges(access.getLocalPrivileges());
							allowed = true;
						}
					}
				}
				// check access for system user
				if (user.getType().equalsIgnoreCase("system")) {
					access = surveyService.getGroupAccess(survey.getId(), "system");
					if (access != null && access.hasAnyPrivileges()) {
						user.upgradePrivileges(access.getLocalPrivileges());
						allowed = true;
					}
				}
				
				// check result access
				if (survey.getDedicatedResultPrivileges()) {
					ResultAccess resultAccess = surveyService.getResultAccess(survey.getUniqueId(), user.getId());
					if (resultAccess != null) {
						user.setResultAccess(resultAccess);					
						allowed = true;
					}
				}
				
				if (!allowed) {
					throw new ForbiddenURLException();
				}

				this.setCurrentUser(request, user);
			}
		} else {
			// owner has full local privileges
			user.getLocalPrivileges().put(LocalPrivilege.FormManagement, 2);
			user.getLocalPrivileges().put(LocalPrivilege.AccessResults, 2);
			user.getLocalPrivileges().put(LocalPrivilege.AccessDraft, 2);
		}

		updateSessionInfo(survey, user, request);
	}

	private Form checkSurvey(Survey survey, User user, HttpServletRequest request) throws Exception {
		if (survey == null)
			return null;

		// check if user is allowed
		upgradePrivileges(survey, user, request);

		Form form = new Form(survey, translationService.getTranslationsForSurvey(survey.getId(), true),
				survey.getLanguage(), resources, contextpath);
		form.setNumberOfAnswerSets(answerService.getNumberOfAnswerSets(survey, new ResultFilter()));

		if (survey.getArchived() || survey.getIsDeleted()) {
			throw new InvalidURLException();
		}

		return form;
	}

	public void updateSessionInfo(Survey survey, User user, HttpServletRequest request) {
		SessionInfo info = (SessionInfo) request.getSession().getAttribute("sessioninfo");

		if (info != null && info.getSurvey() != survey.getId()) {
			activityService.log(ActivityRegistry.ID_SURVEY_LOADED, null, null, user.getId(), survey.getUniqueId());
		}
		request.getSession().setAttribute("sessioninfo", new SessionInfo(survey.getId(), user.getId(),
				survey.getOwner().getId(), survey.getLanguage().getCode(), survey.getShortname()));
	}

	public SurveyFilter getSurveyFilter(HttpServletRequest request, boolean forms)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException {
		SurveyFilter filter = new SurveyFilter();

		filter.setUser(getCurrentUser(request));

		if (request.getParameter("clearFilter") != null && "true".equals(request.getParameter("clearFilter"))) {
			if (forms)
				request.getSession().removeAttribute("SURVEYFILTER");
			return filter;
		}

		if (request.getParameter("itemsPerPage") != null) {
			filter.setShortname(request.getParameter("name"));
			filter.setTitle(request.getParameter("title"));
			filter.setGeneratedFrom(ConversionTools.getDate(request.getParameter("generatedFrom")));
			filter.setGeneratedTo(ConversionTools.getDate(request.getParameter("generatedTo")));
			filter.setStartFrom(ConversionTools.getDate(request.getParameter("startFrom")));
			filter.setStartTo(ConversionTools.getDate(request.getParameter("startTo")));
			filter.setEndFrom(ConversionTools.getDate(request.getParameter("endFrom")));
			filter.setEndTo(ConversionTools.getDate(request.getParameter("endTo")));
			filter.setAccess(request.getParameter("access"));

			boolean filterDraft = request.getParameter("statusDraft") != null
					&& request.getParameter("statusDraft").equalsIgnoreCase("Draft");
			boolean filterUnpublished = request.getParameter("statusUnpublished") != null
					&& request.getParameter("statusUnpublished").equalsIgnoreCase("Unpublished");
			boolean filterPublished = request.getParameter("statusPublished") != null
					&& request.getParameter("statusPublished").equalsIgnoreCase("Published");

			boolean filterOwn = request.getParameter("surveysOwn") != null
					&& request.getParameter("surveysOwn").equalsIgnoreCase("own");
			boolean filterShared = request.getParameter("surveysShared") != null
					&& request.getParameter("surveysShared").equalsIgnoreCase("shared");
			boolean filterAny = request.getParameter("surveysShared") != null
					&& request.getParameter("surveysShared").equalsIgnoreCase("any");

			boolean filterStandardSurvey = request.getParameter("surveyStandard") != null
					&& request.getParameter("surveyStandard").equalsIgnoreCase("standard");
			boolean filterOPC = request.getParameter("surveyOPC") != null
					&& request.getParameter("surveyOPC").equalsIgnoreCase("opc");
			boolean filterQuiz = request.getParameter("surveyQuiz") != null
					&& request.getParameter("surveyQuiz").equalsIgnoreCase("quiz");
			boolean filterDelphi = request.getParameter("surveyDelphi") != null
					&& request.getParameter("surveyDelphi").equalsIgnoreCase("delphi");
			boolean filterEVote = request.getParameter("surveyEVote") != null
					&& request.getParameter("surveyEVote").equalsIgnoreCase("evote");
			boolean filterECF = request.getParameter("surveyECF") != null
					&& request.getParameter("surveyECF").equalsIgnoreCase("ecf");
			boolean filterSelfAssessment = request.getParameter("surveySelfAssessment") != null
					&& request.getParameter("surveySelfAssessment").equalsIgnoreCase("selfassessment");

			String status = "";
			if (filterDraft)
				status += "Draft;";
			if (filterUnpublished)
				status += "Unpublished;";
			if (filterPublished)
				status += "Published;";
			filter.setStatus(status);
			filter.setKeywords(request.getParameter("keywords"));
			filter.setLanguages(request.getParameterValues("languages"));
			filter.setOwner(request.getParameter("owner"));

			if (filterAny) {
				filter.setSelector("any");
			} else if (filterOwn && filterShared) {
				filter.setSelector("all");
			} else if (filterOwn) {
				filter.setSelector("my");
			} else if (filterShared) {
				filter.setSelector("shared");
			} else {
				filter.setSelector("all");
			}

			if (filterStandardSurvey) {
				filter.addSurveyType("standard");
			} else {
				filter.removeSurveyType("standard");
			}
			if (filterOPC) {
				filter.addSurveyType("opc");
			} else {
				filter.removeSurveyType("opc");
			}
			if (filterQuiz) {
				filter.addSurveyType("quiz");
			} else {
				filter.removeSurveyType("quiz");
			}
			if (filterDelphi) {
				filter.addSurveyType("delphi");
			} else {
				filter.removeSurveyType("delphi");
			}
			if (filterEVote) {
				filter.addSurveyType("evote");
			} else {
				filter.removeSurveyType("evote");
			}
			if (filterECF) {
				filter.addSurveyType("ecf");
			} else {
				filter.removeSurveyType("ecf");
			}
			if (filterSelfAssessment) {
				filter.addSurveyType("selfassessment");
			} else {
				filter.removeSurveyType("selfassessment");
			}

			if (request.getParameter("tags") != null && request.getParameter("tags") != "") {
				filter.setTags(List.of(request.getParameter("tags").split(";")));
			}

			if (request.getParameter("sortkey") == null) {
				filter.setSortKey("survey_created");
				filter.setSortOrder("DESC");
			} else {

				// check for validity
				String sortkey = request.getParameter("sortkey");
				if (sortkey.equalsIgnoreCase("surveyname") || sortkey.equalsIgnoreCase("survey_created")
						|| sortkey.equalsIgnoreCase("survey_end_date") || sortkey.equalsIgnoreCase("replies")) {
					filter.setSortKey(sortkey);
					if (request.getParameter("sortorder") != null
							&& request.getParameter("sortorder").equalsIgnoreCase("asc")) {
						filter.setSortOrder("ASC");
					} else {
						filter.setSortOrder("DESC");
					}
				}
			}

			if (forms)
				request.getSession().setAttribute("SURVEYFILTER", filter);

		} else {

			if (forms && request.getSession().getAttribute("SURVEYFILTER") != null) {
				filter = (SurveyFilter) request.getSession().getAttribute("SURVEYFILTER");
			}

		}

		return filter;
	}

	public ResultFilter getLastResultFilter(HttpServletRequest request) {
		return getLastResultFilter(request, 0, 0);
	}

	public ResultFilter getLastResultFilter(HttpServletRequest request, int userid, int surveyid) {
		if (request == null)
			return null;

		ResultFilter filter = null;

		if (userid > 0 && surveyid > 0) {
			filter = getResultFilter(userid, surveyid, true);
		} else {
			filter = (ResultFilter) request.getSession().getAttribute("ResultFilter");
		}

		if (filter != null && filter.getDefaultQuestions() != null && filter.getDefaultQuestions()) {
			Survey survey = surveyService.getSurvey(filter.getSurveyId());
			
			List<String> ids = new ArrayList<>();
			List<String> allids = new ArrayList<>();

			for (Element question : survey.getQuestions()) {
				if (question.isUsedInResults()) {
					if (ids.size() < 20) {
						ids.add(question.getId().toString());
					}
					allids.add(question.getId().toString());
				}
			}
			
			List<String> idsToRemove = new ArrayList<>();
			for (String id : filter.getVisibleQuestions()) {
				if (!ids.contains(id)) {
					idsToRemove.add(id);
				}
			}
			for (String id : idsToRemove) {
				filter.getVisibleQuestions().remove(id);
			}			
			for (String id : ids) {
				if (!filter.getVisibleQuestions().contains(id)) {
					filter.getVisibleQuestions().add(id);
				}
			}
			
			idsToRemove = new ArrayList<>();
			for (String id : filter.getExportedQuestions()) {
				if (!allids.contains(id)) {
					idsToRemove.add(id);
				}
			}
			for (String id : idsToRemove) {
				filter.getExportedQuestions().remove(id);
			}
			for (String id : allids) {
				if (!filter.getExportedQuestions().contains(id)) {
					filter.getExportedQuestions().add(id);
				}
			}

			if (!idsToRemove.isEmpty()) {
				updateFilterValueIds(filter);
			}
		}

		return filter;
	}

	@Transactional
	public ResultFilter getResultFilter(int userid, int surveyid, boolean evict) {
		Session session = sessionFactory.getCurrentSession();
		Query<ResultFilter> query = session
				.createQuery(
						"FROM ResultFilter r WHERE r.userId = :userid and r.surveyId = :surveyid ORDER BY r.id DESC", ResultFilter.class)
				.setParameter("userid", userid).setParameter("surveyid", surveyid);

		List<ResultFilter> result = query.list();

		if (!result.isEmpty()) {
			ResultFilter filter = result.get(0);
			filter = answerService.initialize(filter);
			
			if (evict) {
				session.evict(filter);
			}			
			
			return filter;
		}

		return null;
	}
	
	@Transactional
	public List<ResultFilter> getResultFilterForApplyChanges(int surveyid) {
		List<ResultFilter> result = getAllUserResultFilter(surveyid);
		
		Survey survey = surveyService.getSurvey(surveyid);
		result.add(survey.getPublication().getFilter());
		
		return result;
	}

	public List<ResultFilter> getAllUserResultFilter(int surveyid) {
		Session session = sessionFactory.getCurrentSession();
		Query<ResultFilter> query = session.createQuery("FROM ResultFilter r WHERE r.surveyId = :surveyid and r.userId is not null", ResultFilter.class)
				.setParameter("surveyid", surveyid);

		return query.list();
	}

	@Transactional
	public void setLastResultFilter(HttpServletRequest request, ResultFilter filter, int user, int survey)
			throws InterruptedException {
		// there can only be changes during a POST
		if (request.getMethod().equalsIgnoreCase("POST")) {
			boolean saved = false;
			int counter = 1;

			if (request.getParameter("resultsFormMode") != null
					&& request.getParameter("resultsFormMode").equalsIgnoreCase("configure")) {
				filter.setDefaultQuestions(false);
			}

			while (!saved) {
				try {
					internalSetLastResultFilter(filter, user, survey);
					saved = true;
				} catch (org.hibernate.exception.LockAcquisitionException ex) {
					logger.info("lock on RESULTFILTER table catched; retry counter: " + counter);
					counter++;

					if (counter > 60) {
						logger.error(ex.getLocalizedMessage(), ex);
						throw ex;
					}

					Thread.sleep(1000);
				}
			}
		}
		
		answerService.initialize(filter);
		request.getSession().setAttribute("ResultFilter", filter.copy());
	}

	@Transactional
	public void internalSetLastResultFilter(ResultFilter filter, int user, int survey) {
		Session session = sessionFactory.getCurrentSession();

		filter.setUserId(user);
		filter.setSurveyId(survey);

		ResultFilter existing = getResultFilter(user, survey, false);
		if (existing == null) {
			existing = new ResultFilter();
		}
		filter.merge(existing);

		session.evict(filter);
		session.saveOrUpdate(existing);
	}

	public void updateFilterValueIds(ResultFilter filter){
		Map<String, String> filterValues = filter.getFilterValues();
		Map<String, String> updatedValues = new HashMap<>();

		Survey survey = surveyService.getSurvey(filter.getSurveyId());

		Map<Integer, Question> qIdMap = survey.getQuestionMap();
		Map<String, Question> qUidMap = survey.getQuestionMapByUniqueId();

		for (Map.Entry<String, String> entry : filterValues.entrySet()){
			String[] split = entry.getKey().split("\\|");
			String uid = split[1];
			String id = split[0].split("-")[0];

			if (qIdMap.containsKey(Integer.parseInt(id)) || !qUidMap.containsKey(uid)) {
				updatedValues.put(entry.getKey(), entry.getValue());
			} else {
				Element elem = qUidMap.get(uid);
				String newKey = entry.getKey().replace(id, elem.getId().toString());
				updatedValues.put(newKey, entry.getValue());
			}
		}

		filter.setFilterValues(updatedValues);
	}

	public ActivityFilter getLastActivityFilter(HttpServletRequest request) {
		if (request == null)
			return null;
		return (ActivityFilter) request.getSession().getAttribute("ActivityFilter");
	}

	public void setLastActivityFilter(HttpServletRequest request, ActivityFilter filter) {
		request.getSession().setAttribute("ActivityFilter", filter);
	}

	public UserFilter getUserFilter(HttpServletRequest request) {
		UserFilter filter = new UserFilter();

		if ("true".equals(request.getParameter("clearFilter"))) {
			return filter;
		}

		filter.setLogin(request.getParameter("login"));
		filter.setEmail(request.getParameter(Constants.EMAIL));
		filter.setComment(request.getParameter("comment"));

		filter.setECaccess(
				request.getParameter("ECaccess") != null && request.getParameter("ECaccess").equalsIgnoreCase("true"));
		filter.setNoECaccess(request.getParameter("NoECaccess") != null
				&& request.getParameter("NoECaccess").equalsIgnoreCase("true"));
		filter.setECASaccess(request.getParameter("ECASaccess") != null
				&& request.getParameter("ECASaccess").equalsIgnoreCase("true"));
		filter.setNoECASaccess(request.getParameter("NoECASaccess") != null
				&& request.getParameter("NoECASaccess").equalsIgnoreCase("true"));

		filter.setECAS(request.getParameter("ECAS") != null);
		filter.setSystem(request.getParameter("system") != null);

		filter.setLanguages(request.getParameterValues("languages"));

		filter.setBanned(
				request.getParameter("banned") != null && request.getParameter("banned").equalsIgnoreCase("true"));
		filter.setUnbanned(
				request.getParameter("unbanned") != null && request.getParameter("unbanned").equalsIgnoreCase("true"));

		String roles[] = request.getParameterValues("roles");
		if (roles != null && roles.length > 0) {
			filter.setRoles(roles);
		}

		String sortKey = request.getParameter("sortkey");
		if (sortKey != null && (sortKey.equalsIgnoreCase("login") || sortKey.equalsIgnoreCase(Constants.EMAIL))) {
			filter.setSortKey(request.getParameter("sortkey"));
			filter.setSortOrder(request.getParameter("sortorder"));
		}

		return filter;
	}

	public void initializeProxy() {
		System.getProperties().setProperty("http.proxyHost", proxyHost);
		System.getProperties().setProperty("http.proxyPort", proxyPort);
		System.getProperties().setProperty("http.proxyUser", proxyUser);
		System.getProperties().setProperty("http.proxyPassword", proxyPassword);

		System.getProperties().setProperty("https.proxyHost", proxyHost);
		System.getProperties().setProperty("https.proxyPort", proxyPort);
		System.getProperties().setProperty("https.proxyUser", proxyUser);
		System.getProperties().setProperty("https.proxyPassword", proxyPassword);

		if (StringUtils.isEmpty(proxyNonProxyHosts)) {
			proxyNonProxyHosts = "localhost";
		}

		System.setProperty("nonProxyHosts", proxyNonProxyHosts);
		
		logger.info("SessionService set Non proxy Host " + proxyNonProxyHosts);
		System.getProperties().setProperty("http.nonProxyHosts", proxyNonProxyHosts);

		Authenticator.setDefault(new Authenticator() {
			@Override
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(proxyUser, proxyPassword.toCharArray());
			}
		});

		CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
	}

	public String getPdfServerPrefix() {
		return pdfServerPrefix;
	}

	/**
	 * get session attribute CHECK_EXPORT
	 * 
	 * @param request HTTP request
	 * @return session attribute if user need to call ajax function to check for new
	 *         exports
	 */
	public String getCheckExport(HttpServletRequest request) {
		if (request == null)
			return null;
		if (request.getSession() == null)
			return null;
		return (String) request.getSession().getAttribute("CHECK_EXPORT");
	}

	/**
	 * set session attribute CHECK_EXPORT to "true" or "false"
	 * 
	 * @param request HTTP request
	 * @param value
	 */
	public void setCheckExport(HttpServletRequest request, String value) {
		request.getSession().setAttribute("CHECK_EXPORT", value);
	}

	public String getContextPath() {
		return servletContext.getContextPath();
	}
	
	public void setFormStartDate(HttpServletRequest request, Form form, String uniqueCode) {
		Date startDate = (Date)request.getSession().getAttribute(Constants.START + uniqueCode);
		if (startDate != null) {
			form.setStartDate(startDate);
		}
		
		request.getSession().setAttribute(Constants.START + uniqueCode, form.getStartDate());
	}
	
	public void SetUniqueCodeForForm(HttpServletRequest request, int surveyId, String uniqueCode) {
		request.getSession().setAttribute(Constants.UNIQUECODE + surveyId, uniqueCode);
	}

	public void ClearUniqueCodeForForm(HttpServletRequest request, int surveyId)  {
		request.getSession().removeAttribute(Constants.UNIQUECODE + surveyId);		
	}	
}
