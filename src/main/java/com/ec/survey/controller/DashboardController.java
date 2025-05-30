package com.ec.survey.controller;

import com.ec.survey.exception.ForbiddenURLException;
import com.ec.survey.model.*;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.LocalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.attendees.Invitation;
import com.ec.survey.model.evote.QuorumContributions;
import com.ec.survey.model.survey.*;
import com.ec.survey.model.survey.dashboard.Contributions;
import com.ec.survey.model.survey.dashboard.EndDates;
import com.ec.survey.tools.*;

import com.ec.survey.tools.activity.ActivityRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

@Controller
public class DashboardController extends BasicController {

	private @Value("${server.prefix}") String host;

	@RequestMapping(value = {"/dashboard","/dashboard/runner"}, method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView dashboard(HttpServletRequest request, Locale locale, Model model)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException, ForbiddenURLException,
			IOException {

		if (request.getParameter("deletearchive") != null) {
			String archiveid = request.getParameter("deletearchive");
			Archive archive = archiveService.get(Integer.parseInt(archiveid));
			Survey survey = surveyService.getSurveyByUniqueId(archive.getSurveyUID(), false, true);

			User u = sessionService.getCurrentUser(request);

			if (survey != null) {
				sessionService.upgradePrivileges(survey, u, request);
				if (!u.getId().equals(survey.getOwner().getId())
						&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
						&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2) {
					throw new ForbiddenURLException();
				}
			} else {
				if (!archive.getOwner().equals(u.getName())) {
					throw new ForbiddenURLException();
				}
			}

			archiveService.delete(archive);
		}

		// check user (e.g. weak authentication)
		sessionService.getCurrentUser(request);
		ModelAndView result = new ModelAndView("dashboard");

		if (request.getParameter("archived") != null) {
			String shortname = request.getParameter("archived");
			result.addObject("archived", shortname);
		}

		if (request.getParameter(Constants.DELETED) != null) {
			String shortname = request.getParameter(Constants.DELETED);
			result.addObject(Constants.DELETED, shortname);
		}

		if (request.getParameter("frozen") != null) {
			String shortname = request.getParameter("frozen");
			result.addObject("frozen", shortname);
		}

		result.addObject(Constants.FILTER, new ArchiveFilter());

		return result;
	}

	@RequestMapping(value = "/dashboard/contributions", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody Contributions contributions(HttpServletRequest request, HttpServletResponse response,
			Locale locale, Model model) {
		try {

			User u = sessionService.getCurrentUser(request);
			Contributions contributions = new Contributions();

			String sort = request.getParameter("sort");
			String span = request.getParameter("span");
			String type = request.getParameter("type");

			Map<Integer, String> surveys = surveyService.getAllPublishedSurveysForUser(u, sort, type);
			contributions.setSurveys(surveys);

			int index = 0;
			if (request.getParameter(Constants.SURVEY) != null) {
				index = Integer.parseInt(request.getParameter(Constants.SURVEY));
			}

			if (request.getParameter("surveyid") != null) {
				int surveyid = Integer.parseInt(request.getParameter("surveyid"));
				if (surveyid > 0) {
					Integer[] surveyids = new Integer[0];
					surveyids = surveys.keySet().toArray(surveyids);
					for (int i = 0; i < surveyids.length; i++) {
						if (surveyids[i] == surveyid) {
							index = i;
							break;
						}
					}
				}
			}

			if (surveys.size() > 0) {
				contributions.setSurveyId((int) surveys.keySet().toArray()[index]);
				contributions.setSurveyTitle((String) surveys.values().toArray()[index]);

				Map<Date, Integer> answersPerDay = answerService.getAnswersPerDay(contributions.getSurveyId(), span);

				contributions.setAnswersPerDay(answersPerDay);

				int[] cs = answerService.getAnswerStatistics(contributions.getSurveyId());

				contributions.setContributionStates(cs);
				contributions.setSurveyIndex(index);
			}

			return contributions;
		} catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error(ex.getMessage(), ex);
		}

		return null;
	}

	@RequestMapping(value = "/dashboard/quorumContributions", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody QuorumContributions quorumContributions(HttpServletRequest request, HttpServletResponse response,
													 Locale locale, Model model) {
		try {
			int surveyid = Integer.parseInt(request.getParameter("surveyid"));
			
			Form form = sessionService.getForm(request, null, false, false);
			User u = sessionService.getCurrentUser(request);
			sessionService.upgradePrivileges(form.getSurvey(), u, request);

			if (!u.getId().equals(form.getSurvey().getOwner().getId())
					&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.AccessResults) < 1) {
				throw new ForbiddenURLException();
			}
			
			QuorumContributions contributions = new QuorumContributions();
			contributions.setNumberOfContributions(answerService.getNumberOfAnswerSetsPublished(form.getSurvey().getUniqueId()));

			String span = request.getParameter("span");
			
			contributions.setSurveyId(surveyid);

			int[] cs = answerService.getAnswerStatistics(surveyid);
			contributions.setContributionStates(cs);

			Map<Date, Integer> answersPerDay = answerService.getQuorumAnswers(surveyid, span);
			contributions.setAnswersPerTimeUnit(answersPerDay, span);

			String uid = form.getSurvey().getUniqueId();

			contributions.setVoters(eVoteService.getVoterCount(uid, null));

			activityService.log(ActivityRegistry.ID_OPEN_QUORUM, null, null, u.getId(), uid);

			return contributions;
		} catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error(ex.getMessage(), ex);
		}

		return null;
	}

	@RequestMapping(value = "/dashboard/metadata", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody String[] metadata(HttpServletRequest request, HttpServletResponse response, Locale locale,
			Model model) {
		try {

			User u = sessionService.getCurrentUser(request);
			String type = request.getParameter("type");
			return surveyService.getMetaDataForUser(u, type);
		} catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error(ex.getMessage(), ex);
		}

		return null;
	}

	@RequestMapping(value = "/dashboard/surveystates", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody int[] surveystates(HttpServletRequest request, HttpServletResponse response, Locale locale,
			Model model) {
		try {
			User u = sessionService.getCurrentUser(request);
			String type = request.getParameter("type");

			return surveyService.getSurveyStatisticsForUser(u, type);
		} catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error(ex.getMessage(), ex);
		}

		return null;
	}

	@RequestMapping(value = "/dashboard/enddates", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody EndDates enddates(HttpServletRequest request, HttpServletResponse response, Locale locale,
			Model model) {
		try {
			User u = sessionService.getCurrentUser(request);
			String type = request.getParameter("type");
			Map<Date, List<String>> surveyswithenddates = surveyService.getSurveysWithEndDatesForUser(u, type);

			EndDates result = new EndDates();
			result.setEndDates(surveyswithenddates);

			return result;
		} catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error(ex.getMessage());
		}

		return null;
	}

	@RequestMapping(value = "/dashboard/surveys", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody List<Survey> surveys(HttpServletRequest request, HttpServletResponse response, Locale locale,
			Model model) {
		try {
			User u = sessionService.getCurrentUser(request);
			SurveyFilter filter = new SurveyFilter();
			filter.setUser(u);

			int page = 1;
			if (request.getParameter("page") != null) {
				page = Integer.parseInt(request.getParameter("page"));
			}

			if (request.getParameter(Constants.SHORTNAME) != null) {
				String shortname = request.getParameter(Constants.SHORTNAME);
				if (shortname.trim().length() > 0) {
					filter.setShortname(shortname);
				}
			}
			if (request.getParameter("title") != null) {
				String title = request.getParameter("title");
				if (title.trim().length() > 0) {
					filter.setTitle(title);
				}
			}
			if (request.getParameter("owner") != null) {
				String owner = request.getParameter("owner");
				if (owner.trim().length() > 0) {
					filter.setOwner(owner);
				}
			}
			if (request.getParameter("slanguage") != null) {
				String language = request.getParameter("slanguage");
				if (language.trim().length() > 0) {
					String[] langs = new String[1];
					langs[0] = language;
					filter.setLanguages(langs);
				}
			}
			if (request.getParameter("status") != null) {
				String status = request.getParameter("status");
				if (status.trim().length() > 0) {
					filter.setStatus(status);
				}
			}
			if (request.getParameter("security") != null) {
				String security = request.getParameter("security");
				if (security.trim().length() > 0) {
					filter.setAccess(security);
				}
			}
			if (request.getParameter("startfrom") != null) {
				String startfrom = request.getParameter("startfrom");
				if (startfrom.trim().length() > 0) {
					filter.setStartFrom(ConversionTools.getDate(startfrom));
				}
			}
			if (request.getParameter("startto") != null) {
				String startto = request.getParameter("startto");
				if (startto.trim().length() > 0) {
					filter.setStartTo(ConversionTools.getDate(startto));
				}
			}
			if (request.getParameter("endfrom") != null) {
				String endfrom = request.getParameter("endfrom");
				if (endfrom.trim().length() > 0) {
					filter.setEndFrom(ConversionTools.getDate(endfrom));
				}
			}
			if (request.getParameter("endto") != null) {
				String endto = request.getParameter("endto");
				if (endto.trim().length() > 0) {
					filter.setEndTo(ConversionTools.getDate(endto));
				}
			}

			if (request.getParameter("sort") != null) {
				filter.setSortKey(request.getParameter("sort").toUpperCase());
				filter.setSortOrder(
						request.getParameter("asc") != null && request.getParameter("asc").equalsIgnoreCase("true")
								? "ASC"
								: "DESC");
			}

			if (request.getParameter("reported") != null) {
				filter.setSurveys("REPORTED");
			}

			if (request.getParameter("frozen") != null) {
				filter.setSurveys("FROZEN");
			}

			if (request.getParameter("type") != null) {
				filter.setSelector(request.getParameter("type"));
			}

			SqlPagination paging = new SqlPagination(page, 10);
			return surveyService.getSurveysForDashboard(filter, paging, u);
		} catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error(ex.getMessage(), ex);
		}

		return null;
	}

	@RequestMapping(value = "/dashboard/surveysadvanced", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody List<List<Integer>> surveysadvanced(HttpServletRequest request, HttpServletResponse response,
			Locale locale, Model model) {
		try {
			List<List<Integer>> listOfList = new ArrayList<>();
			String surveysUIDS = request.getParameter("ids");
			if (surveysUIDS != null && surveysUIDS.length() > 0) {
				String[] uids = surveysUIDS.split(";");
				for (String uid : uids) {
					List<Integer> list = new ArrayList<>();
					// 1: The number of invitations
					list.add(participationService.getNumberOfOpenInvitations(uid));
					// 2: The number of drafts
					list.add(answerService.getNumberOfDrafts(uid));
					listOfList.add(list);
				}

				return listOfList;
			}
		} catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error(ex.getMessage(), ex);
		}

		return null;
	}

	@RequestMapping(value = "/dashboard/archives", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody List<Archive> archives(HttpServletRequest request, HttpServletResponse response, Locale locale,
			Model model) {
		try {
			if (request.getParameter("type") != null) {
				if (request.getParameter("type").equalsIgnoreCase("shared")) {
					return new ArrayList<Archive>();
				}
			}

			User u = sessionService.getCurrentUser(request);

			int page = 1;
			if (request.getParameter("page") != null) {
				page = Integer.parseInt(request.getParameter("page"));
			}

			ArchiveFilter filter = new ArchiveFilter();
			filter.setUserId(u.getId());

			if (request.getParameter("title") != null) {
				String title = request.getParameter("title");
				if (title.trim().length() > 0) {
					filter.setTitle(title);
				}
			}
			if (request.getParameter("shortname") != null) {
				String shortname = request.getParameter("shortname");
				if (shortname.trim().length() > 0) {
					filter.setShortname(shortname);
				}
			}
			if (request.getParameter("createdfrom") != null) {
				String createdfrom = request.getParameter("createdfrom");
				if (createdfrom.trim().length() > 0) {
					filter.setCreatedFrom(ConversionTools.getDate(createdfrom));
				}
			}
			if (request.getParameter("createdto") != null) {
				String createdto = request.getParameter("createdto");
				if (createdto.trim().length() > 0) {
					filter.setCreatedTo(ConversionTools.getDate(createdto));
				}
			}
			if (request.getParameter("archivedfrom") != null) {
				String archivedfrom = request.getParameter("archivedfrom");
				if (archivedfrom.trim().length() > 0) {
					filter.setArchivedFrom(ConversionTools.getDate(archivedfrom));
				}
			}
			if (request.getParameter("archivedto") != null) {
				String archivedto = request.getParameter("archivedto");
				if (archivedto.trim().length() > 0) {
					filter.setArchivedTo(ConversionTools.getDate(archivedto));
				}
			}

			if (request.getParameter("sort") != null) {
				filter.setSortKey(request.getParameter("sort"));
				filter.setSortOrder(
						request.getParameter("asc") != null && request.getParameter("asc").equalsIgnoreCase("true")
								? "ASC"
								: "DESC");
			}

			return archiveService.getAllArchives(filter, page, 10, true);
		} catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error(ex.getMessage(), ex);
		}

		return null;
	}

	@RequestMapping(value = "/dashboard/personalcontributions", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody List<Object[]> personalcontributions(HttpServletRequest request, HttpServletResponse response,
			Locale locale, Model model) {
		try {

			List<Object[]> contributions = new ArrayList<>();

			ResultFilter filter = new ResultFilter();

			User user = sessionService.getCurrentUser(request);

			if (user.getOtherEmail() != null && user.getOtherEmail().length() > 0) {
				List<String> allemails = user.getAllEmailAddresses();
				filter.setUser(String.join(";", allemails));
			} else {
				filter.setUser(user.getEmail());
			}

			filter.setSortKey("date");
			filter.setSortOrder("DESC");
			filter.setStatus("Submitted");

			int page = 1;
			if (request.getParameter("page") != null) {
				page = Integer.parseInt(request.getParameter("page"));
			}

			if (request.getParameter("lasteditfrom") != null) {
				String lasteditfrom = request.getParameter("lasteditfrom");
				if (lasteditfrom.trim().length() > 0) {
					filter.setUpdatedFrom(ConversionTools.getDate(lasteditfrom));
				}
			}
			if (request.getParameter("lasteditto") != null) {
				String lasteditto = request.getParameter("lasteditto");
				if (lasteditto.trim().length() > 0) {
					filter.setUpdatedTo(ConversionTools.getDate(lasteditto));
				}
			}
			if (request.getParameter(Constants.SURVEY) != null) {
				String survey = request.getParameter(Constants.SURVEY);
				if (survey.trim().length() > 0) {
					filter.setSurveyTitle(survey);
				}
			}
			if (request.getParameter("surveystatus") != null) {
				String surveystatus = request.getParameter("surveystatus");
				if (surveystatus.trim().length() > 0) {
					filter.setSurveyStatus(surveystatus);
				}
			}
			if (request.getParameter("endfrom") != null) {
				String endfrom = request.getParameter("endfrom");
				if (endfrom.trim().length() > 0) {
					filter.setSurveyEndDateFrom(ConversionTools.getDate(endfrom));
				}
			}
			if (request.getParameter("endto") != null) {
				String endto = request.getParameter("endto");
				if (endto.trim().length() > 0) {
					filter.setSurveyEndDateTo(ConversionTools.getDate(endto));
				}
			}

			SqlPagination paging = new SqlPagination(page, 10);

			List<AnswerSet> answerSets = answerService.getAnswers(null, filter, paging, false, false, false);

			for (AnswerSet answerSet : answerSets) {
				Object[] answer = new Object[9];
				answer[0] = answerSet.getId();
				answer[1] = answerSet.getUniqueCode();
				answer[2] = answerSet.getNiceUpdateDate();
				answer[3] = ConversionTools.removeHTML(answerSet.getSurvey().getTitle());
				answer[4] = answerSet.getSurvey().getIsActive();
				answer[5] = answerSet.getSurvey().getAutomaticPublishing() ? answerSet.getSurvey().getEndString() : "";
				answer[6] = answerSet.getSurvey().getChangeContribution() && answerSet.getSurvey().getIsActive();
				answer[7] = answerSet.getSurvey().getPublication().isShowContent()
						|| answerSet.getSurvey().getPublication().isShowStatistics();
				answer[8] = answerSet.getSurvey().getDownloadContribution();

				contributions.add(answer);
			}

			return contributions;
		} catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error(ex.getMessage(), ex);
		}

		return null;
	}

	@RequestMapping(value = "/dashboard/personaldrafts", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody List<Object[]> personaldrafts(HttpServletRequest request, HttpServletResponse response,
			Locale locale, Model model) {
		try {

			List<Object[]> drafts = new ArrayList<>();

			ResultFilter filter = new ResultFilter();

			User user = sessionService.getCurrentUser(request);

			if (user.getOtherEmail() != null && user.getOtherEmail().length() > 0) {
				List<String> allemails = user.getAllEmailAddresses();
				filter.setUser(String.join(";", allemails));
			} else {
				filter.setUser(user.getEmail());
			}
			filter.setSortKey("date");
			filter.setSortOrder("DESC");

			int page = 1;
			if (request.getParameter("page") != null) {
				page = Integer.parseInt(request.getParameter("page"));
			}

			if (request.getParameter("lasteditfrom") != null) {
				String lasteditfrom = request.getParameter("lasteditfrom");
				if (lasteditfrom.trim().length() > 0) {
					filter.setUpdatedFrom(ConversionTools.getDate(lasteditfrom));
				}
			}
			if (request.getParameter("lasteditto") != null) {
				String lasteditto = request.getParameter("lasteditto");
				if (lasteditto.trim().length() > 0) {
					filter.setUpdatedTo(ConversionTools.getDate(lasteditto));
				}
			}
			if (request.getParameter(Constants.SURVEY) != null) {
				String survey = request.getParameter(Constants.SURVEY);
				if (survey.trim().length() > 0) {
					filter.setSurveyTitle(survey);
				}
			}
			if (request.getParameter("surveystatus") != null) {
				String surveystatus = request.getParameter("surveystatus");
				if (surveystatus.trim().length() > 0) {
					filter.setSurveyStatus(surveystatus);
				}
			}

			filter.setStatus("draft");

			if (request.getParameter("endfrom") != null) {
				String endfrom = request.getParameter("endfrom");
				if (endfrom.trim().length() > 0) {
					filter.setSurveyEndDateFrom(ConversionTools.getDate(endfrom));
				}
			}
			if (request.getParameter("endto") != null) {
				String endto = request.getParameter("endto");
				if (endto.trim().length() > 0) {
					filter.setSurveyEndDateTo(ConversionTools.getDate(endto));
				}
			}

			SqlPagination paging = new SqlPagination(page, 10);

			List<AnswerSet> answerSets = answerService.getDraftAnswers(-1, filter, paging, false, false);

			for (AnswerSet answerSet : answerSets) {
				Object[] answer = new Object[8];
				answer[0] = answerSet.getId();
				answer[1] = answerSet.getUniqueCode();
				answer[2] = answerSet.getNiceUpdateDate();
				answer[3] = ConversionTools.removeHTML(answerSet.getSurvey().getTitle());
				answer[4] = answerSet.getSurvey().getIsActive();
				answer[5] = answerSet.getSurvey().getAutomaticPublishing() ? answerSet.getSurvey().getEndString() : "";

				Invitation invitation = attendeeService.getInvitationByUniqueId(answerSet.getInvitationId());
				
				if (invitation != null) {
					answer[6] = host + "runner/invited/" + invitation.getParticipationGroupId() + Constants.PATH_DELIMITER
							+ invitation.getUniqueId();
				} else {
					Draft draft = answerService.getDraftByAnswerUID(answerSet.getUniqueCode());
					
					if (answerSet.getSurvey().getIsDraft()) {
						answer[6] = draft == null ? "" : host + answerSet.getSurvey().getUniqueId() + "/management/test?draftid=" + draft.getUniqueId();
					} else {
						answer[6] = draft == null ? "" : host + "runner/" + answerSet.getSurvey().getUniqueId();
					}
				}

				drafts.add(answer);
			}

			return drafts;
		} catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error(ex.getMessage(), ex);
		}

		return null;
	}

	@RequestMapping(value = "/dashboard/personalinvitations", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody List<Object[]> personalinvitations(HttpServletRequest request, HttpServletResponse response,
			Locale locale, Model model) {
		try {
			List<Object[]> invitations = new ArrayList<>();

			int page = 1;
			if (request.getParameter("page") != null) {
				page = Integer.parseInt(request.getParameter("page"));
			}

			String survey = request.getParameter(Constants.SURVEY);
			String surveystatus = request.getParameter("surveystatus");
			String expirystart = request.getParameter("endfrom");
			String expiryend = request.getParameter("endto");
			String datestart = request.getParameter("datefrom");
			String dateend = request.getParameter("dateto");

			Date start = null;
			if (expirystart != null && expirystart.trim().length() > 0) {
				start = ConversionTools.getDate(expirystart);
			}

			Date end = null;
			if (expiryend != null && expiryend.trim().length() > 0) {
				end = ConversionTools.getDate(expiryend);
			}

			Date startInv = null;
			if (datestart != null && datestart.trim().length() > 0) {
				startInv = ConversionTools.getDate(datestart);
			}

			Date endInv = null;
			if (dateend != null && dateend.trim().length() > 0) {
				endInv = ConversionTools.getDate(dateend);
			}

			SqlPagination paging = new SqlPagination(page, 10);

			List<Invitation> lstInvitations = participationService.getInvitations(
					sessionService.getCurrentUser(request), paging, survey, surveystatus, start, end, startInv, endInv);

			for (Invitation invitation : lstInvitations) {
				ParticipationGroup group = participationService.get(invitation.getParticipationGroupId());
				Survey s = surveyService.getSurvey(group.getSurveyUid(), true, false, false, false, null, true, false);
				
				if (s != null) {

					Object[] answer = new Object[8];
					answer[0] = ConversionTools.getFullStringSmall(invitation.getInvited());
					answer[1] = ConversionTools.removeHTML(s.getTitle());
					answer[2] = s.getIsActive();
				answer[3] = s.getAutomaticPublishing() ?  s.getEndString() : "";
					answer[4] = host + "runner/invited/" + invitation.getParticipationGroupId() + Constants.PATH_DELIMITER
							+ invitation.getUniqueId();
	
					invitations.add(answer);
				
				}
			}

			return invitations;
		} catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error(ex.getMessage(), ex);
		}

		return null;
	}

	@RequestMapping(value = "/dashboard/contributionstates", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody int[] contributionstates(HttpServletRequest request, HttpServletResponse response,
			Locale locale, Model model) {
		try {

			User u = sessionService.getCurrentUser(request);
			return answerService.getContributionStatisticsForUser(u.getId());
		} catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error(ex.getMessage(), ex);
		}

		return null;
	}
	
	@RequestMapping(value = "/dashboard/opendelphisurveys", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody List<Object[]> delphisurveys(HttpServletRequest request, HttpServletResponse response,
			Locale locale, Model model) {
		try {

			int page = 1;
			if (request.getParameter("page") != null) {
				page = Integer.parseInt(request.getParameter("page"));
			}
			
			User u = sessionService.getCurrentUser(request);
			SurveyFilter filter = new SurveyFilter();
			filter.setUser(u);
			
			filter.setType("delphi");
			filter.setAccess("open");
			
			if (request.getParameter("title") != null) {
				String title = request.getParameter("title");
				if (title.trim().length() > 0) {
					filter.setTitle(title);
				}
			}
			
			if (request.getParameter("status") != null) {
				String status = request.getParameter("status");
				if (status.trim().length() > 0) {
					filter.setStatus(status);
				}
			}
			
			if (request.getParameter("endfrom") != null) {
				String endfrom = request.getParameter("endfrom");
				if (endfrom.trim().length() > 0) {
					filter.setEndFrom(ConversionTools.getDate(endfrom));
				}
			}
			if (request.getParameter("endto") != null) {
				String endto = request.getParameter("endto");
				if (endto.trim().length() > 0) {
					filter.setEndTo(ConversionTools.getDate(endto));
				}
			}
			
			SqlPagination paging = new SqlPagination(page, 10);
			List<Survey> surveyList = surveyService.getSurveysIncludingTranslationLanguages(filter, paging, false, false);		
			
			List<Object[]> result = new ArrayList<Object[]>();
		
			for (Survey survey : surveyList) {

				Object[] answer = new Object[4];
				answer[0] = ConversionTools.removeHTML(survey.getTitle());
				answer[1] = survey.getIsActive();
				answer[2] = survey.getEndString();
				answer[3] = host + "runner/" + survey.getShortname();

				result.add(answer);
			}

			return result;
		} catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error(ex.getMessage(), ex);
		}

		return null;
	}

}
