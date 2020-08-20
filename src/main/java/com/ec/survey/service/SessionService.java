package com.ec.survey.service;

import com.ec.survey.exception.ForbiddenURLException;
import com.ec.survey.exception.InvalidURLException;
import com.ec.survey.exception.NoFormLoadedException;
import com.ec.survey.model.*;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.LocalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.Element;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.NotAgreedToPsException;
import com.ec.survey.tools.NotAgreedToTosException;
import com.ec.survey.tools.WeakAuthenticationException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.net.*;
import java.util.List;

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

	public User getCurrentUser(HttpServletRequest request)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException {
		return getCurrentUser(request, true, true);
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
				if (!allowed) {
					throw new ForbiddenURLException();
				}

				setCurrentUser(request, user);
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
			activityService.log(201, null, null, user.getId(), survey.getUniqueId());
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

			if (filterOwn && filterShared) {
				filter.setSelector("all");
			} else if (filterOwn) {
				filter.setSelector("my");
			} else if (filterShared) {
				filter.setSelector("shared");
			} else {
				filter.setSelector("all");
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
			filter = getResultFilter(userid, surveyid);
		} else {
			filter = (ResultFilter) request.getSession().getAttribute("ResultFilter");
		}

		if (filter != null && filter.getDefaultQuestions() != null && filter.getDefaultQuestions()) {
			filter.getVisibleQuestions().clear();
			filter.getExportedQuestions().clear();

			Survey survey = surveyService.getSurvey(filter.getSurveyId());

			for (Element question : survey.getQuestions()) {
				if (question.isUsedInResults()) {
					if (filter.getVisibleQuestions().size() < 20) {
						filter.getVisibleQuestions().add(question.getId().toString());
					}
					filter.getExportedQuestions().add(question.getId().toString());
				}
			}
		}

		return filter;
	}

	public ResultFilter getResultFilter(int userid, int surveyid) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session
				.createQuery(
						"FROM ResultFilter r WHERE r.userId = :userid and r.surveyId = :surveyid ORDER BY r.id DESC")
				.setInteger("userid", userid).setInteger("surveyid", surveyid);
		@SuppressWarnings("unchecked")
		List<ResultFilter> result = query.list();

		if (!result.isEmpty()) {
			Hibernate.initialize(result.get(0).getLanguages());
			return result.get(0);
		}

		return null;
	}

	public List<ResultFilter> getAllResultFilter(int surveyid) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM ResultFilter r WHERE r.surveyId = :surveyid ORDER BY r.id DESC")
				.setInteger("surveyid", surveyid);
		@SuppressWarnings("unchecked")
		List<ResultFilter> result = query.list();

		return result;
	}

	@Transactional
	public void setLastResultFilter(HttpServletRequest request, ResultFilter filter, int user, int survey)
			throws InterruptedException {
		request.getSession().setAttribute("ResultFilter", filter);

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
	}

	@Transactional
	public void internalSetLastResultFilter(ResultFilter filter, int user, int survey) {
		Session session = sessionFactory.getCurrentSession();

		filter.setUserId(user);
		filter.setSurveyId(survey);

		ResultFilter existing = getResultFilter(user, survey);
		if (existing == null) {
			existing = new ResultFilter();
		}
		filter.merge(existing);

		session.evict(filter);
		session.saveOrUpdate(existing);
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

		System.setProperty("nonProxyHosts", "localhost");

		if (StringUtils.isEmpty(proxyNonProxyHosts)) {
			proxyNonProxyHosts = "localhost";
		}

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
}
