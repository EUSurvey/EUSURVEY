package com.ec.survey.controller;

import com.ec.survey.exception.ForbiddenURLException;
import com.ec.survey.exception.InvalidURLException;
import com.ec.survey.model.*;
import com.ec.survey.model.selfassessment.SATargetDataset;
import com.ec.survey.model.survey.*;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.service.mapping.PaginationMapper;
import com.ec.survey.tools.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;

@Controller
@RequestMapping("/publication")
public class PublicationController extends BasicController {

	@Resource(name = "taskExecutor")
	private TaskExecutor taskExecutor;

	@Autowired
	protected PaginationMapper paginationMapper;

	private @Value("${server.prefix}") String host;
	private @Value("${smtpserver}") String smtpServer;
	private @Value("${smtp.port}") String smtpPort;
	private @Value("${stresstests.createdata}") String createStressData;

	@RequestMapping(value = "/{shortname}")
	public ModelAndView publication(@PathVariable String shortname, HttpServletRequest request, Locale locale) {

		try {

			Survey survey = surveyService.getSurveyLight(shortname, false, true); //ByShortname(shortname, false, null, request, false, true, true, false);
			String lang = request.getParameter("surveylanguage");

			ResultFilter userFilter = new ResultFilter();
			int itemsPerPage = 10;
			ResultFilter publicationFilter = null;
			boolean active = true;
			boolean filtered = false;

			String orderByScore = request.getParameter("sort");
			if (orderByScore != null) {
				if (orderByScore.equalsIgnoreCase("scoreDesc")) {
					userFilter.setSortKey("score");
					userFilter.setSortOrder("desc");
				} else if (orderByScore.equalsIgnoreCase("scoreAsc")) {
					userFilter.setSortKey("score");
					userFilter.setSortOrder("asc");
				}
			}

			if (survey != null) {
				if (survey.getPublication().getPassword() != null
						&& survey.getPublication().getPassword().length() > 0) {
					String publicationpassword = (String) request.getSession().getAttribute("publicationpassword");
					if (publicationpassword == null
							|| !publicationpassword.equals(survey.getPublication().getPassword())) {
						return new ModelAndView("redirect:/publication/auth/" + shortname);
					}
				}

				if (request != null)
					itemsPerPage = ConversionTools.getInt(request.getParameter("itemsPerPage"), 50);
				publicationFilter = survey.getPublication().getFilter();
				if (publicationFilter.getVisibleQuestions().isEmpty()) {
					// initially add all questions
					for (Element question : survey.getQuestionsAndSections()) {
						publicationFilter.getVisibleQuestions().add(question.getId().toString());
					}
				}

				Map<String, String[]> parameters = Ucs2Utf8.requestToHashMap(request);

				if (survey.getPublication().isAllQuestions()) {
					for (Element question : survey.getQuestionsAndSections()) {
						userFilter.getVisibleQuestions().add(question.getId().toString());
					}
				} else {
					for (String questionId : publicationFilter.getVisibleQuestions()) {
						userFilter.getVisibleQuestions().add(questionId);
					}
				}

				if (!survey.getPublication().isAllContributions()) {
					for (String key : publicationFilter.getFilterValues().keySet()) {
						userFilter.getFilterValues().put(key, publicationFilter.getFilterValues().get(key));
						String uid = key.substring(key.indexOf('|') + 1);
						userFilter.getReadOnlyFilterQuestions().add(uid);
					}
				}

				userFilter.setSurveyId(survey.getId());

				filtered = putParameterFilters(parameters, userFilter.getFilterValues());
			}

			if (survey != null && survey.getPublication() != null) {

				Publication publication = survey.getPublication();

				if (publication.isShowContent() || publication.isShowStatistics() || publication.isShowCharts()) {
					Paging<AnswerSet> paging = new Paging<>();
					paging.setItemsPerPage(-1);

					String newPage = request.getParameter("newPage");
					paging.moveTo(newPage == null ? "first" : newPage);

					SqlPagination sqlPagination = new SqlPagination(1, itemsPerPage);
					List<AnswerSet> answerSets = answerService.getAnswers(survey, null, sqlPagination, false, true, active && false);
					paging.setNumberOfItems(answerSets.size());

					if (lang != null) {
						survey = SurveyHelper.createTranslatedSurvey(survey.getId(), lang, surveyService,
								translationService, true);
						survey.setLanguage(surveyService.getLanguage(lang));
					}

					Form form = new Form(survey, translationService.getTranslationsForSurvey(survey.getId(), false),
							survey.getLanguage(), resources, contextpath);
					form.setAnswerSets(new ArrayList<>());
					paging.setItems(new ArrayList<>());
					form.setPublicationMode(true);

					ModelAndView result = new ModelAndView("publication/publication", "form", form);
					result.addObject("publication", publication);
					result.addObject("paging", paging);
					result.addObject("active", active);
					result.addObject(Constants.FILTER, userFilter);
					result.addObject("publicationFilter", publicationFilter);
					result.addObject("filtered", filtered);
					result.addObject("useUILanguage", true);

					String resultType = request.getParameter("resultType");
					resultType = resultType == null ? "content" : resultType;
					result.addObject("resultType", resultType);
					result.addObject("submit", false);
					result.addObject("runnermode", true);

					result.addObject("questionswithuploadedfiles", answerService.getQuestionsWithUploadedFiles(survey));

					request.getSession().setAttribute("lastPublishedFilter" + shortname, userFilter);

					String selectedtab = request.getParameter("selectedtab");
					result.addObject("selectedtab", selectedtab == null ? 1 : Integer.parseInt(selectedtab));

					if (survey.getIsSelfAssessment()) {
						List<SATargetDataset> datasets = selfassessmentService.getTargetDatasets(survey.getUniqueId());
						result.addObject("targetdatasets", datasets);
					}

					return result;
				}
			}
		} catch (InvalidURLException iue) {
			// ignore
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		ModelAndView result = new ModelAndView(Constants.VIEW_ERROR_GENERIC, Constants.MESSAGE,
				resources.getMessage("error.NoPublishedResults", null, "This survey has no published results", locale));
		result.addObject("noMenu", true);
		return result;
	}

	private boolean putParameterFilters(Map<String, String[]> parameters, Map<String, String> filterValues){
		boolean filtered = false;

		var presetKeys = new HashSet<>(filterValues.keySet());

		for (Entry<String, String[]> entry : parameters.entrySet()) {
			if (entry.getKey().startsWith(Constants.FILTER)) {
				String questionId = entry.getKey().substring(6);
				String[] values = entry.getValue();
				String value = StringUtils.arrayToDelimitedString(values, ";");

				if (value.replace(";", "").trim().length() > 0) {
					String uid = questionId.substring(questionId.indexOf('|') + 1);

					boolean found = false;
					for (var key : presetKeys) {
						if (key.endsWith(uid)) {
							found = true;
							break;
						}
					}
					if (!found) {
						filterValues.put(questionId, value);
					}

					filtered = true;
				}
			}
		}
		return filtered;
	}

	@GetMapping(value = "/auth/{shortname}")
	public ModelAndView authenticate(@PathVariable String shortname, HttpServletRequest request, Locale locale) {
		return new ModelAndView("publication/auth", Constants.SHORTNAME, shortname);
	}

	@PostMapping(value = "/auth/{shortname}")
	public ModelAndView authenticatePOST(@PathVariable String shortname, HttpServletRequest request, Locale locale)
			throws InvalidURLException {

		Map<String, String[]> parameters = Ucs2Utf8.requestToHashMap(request);

		String publicationpassword = parameters.get("publicationpassword")[0];
		Survey survey = surveyService.getSurveyByShortname(shortname, false, null, request, false, true, true, false);

		if (survey != null) {
			if (survey.getPublication().getPassword() != null && survey.getPublication().getPassword().length() > 0
					&& publicationpassword != null
					&& publicationpassword.equals(survey.getPublication().getPassword())) {
				request.getSession().setAttribute("publicationpassword", publicationpassword);
				return new ModelAndView("redirect:/publication/" + shortname);
			}
		} else {
			throw new InvalidURLException();
		}

		ModelAndView result = new ModelAndView("publication/auth", Constants.SHORTNAME, shortname);
		result.addObject(Constants.ERROR, resources.getMessage("error.WrongPassword", null, "the password is wrong", locale));
		return result;
	}

	@GetMapping(value = "/individualJSON", headers = "Accept=*/*")
	public @ResponseBody Map<String, String> individualJSON(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String[]> parameters = Ucs2Utf8.requestToHashMap(request);
		String surveyid = "";
		if (parameters.containsKey(Constants.SURVEY))
			surveyid = parameters.get(Constants.SURVEY)[0];
		String counter = "";
		if (parameters.containsKey("counter"))
			counter = parameters.get("counter")[0];

		try {
			if (surveyid.length() > 0 && counter.length() > 0) {
				Survey survey = surveyService.getSurvey(Integer.parseInt(surveyid), false, true);

				if (survey != null && survey.getPublication().isShowContent()) {
					if (survey.getPublication().getPassword() != null
							&& survey.getPublication().getPassword().length() > 0) {
						String publicationpassword = (String) request.getSession().getAttribute("publicationpassword");
						if (publicationpassword == null
								|| !publicationpassword.equals(survey.getPublication().getPassword())) {
							return null;
						}
					}

					ResultFilter filter = survey.getPublication().getFilter();

					ResultFilter userFilter = (ResultFilter) request.getSession()
							.getAttribute("lastPublishedFilter" + survey.getShortname());
					if (userFilter != null)
						filter = userFilter;

					SqlPagination sqlPagination = new SqlPagination(Integer.parseInt(counter) + 1, 1);
					List<AnswerSet> answerSets = answerService.getAnswers(survey, filter, sqlPagination, false, false,
							true);

					Map<String, String> result = new HashMap<>();

					if (answerSets.isEmpty()) {
						result.put("noresults", "noresults");
						return result;
					}

					AnswerSet answerSet = answerSets.get(0);

					Map<String, Question> questionMapByUid = survey.getQuestionMapByUniqueId();
					Map<String, Element> matrixMapByUid = survey.getMatrixMapByUid();

					for (Answer answer : answerSet.getAnswers()) {
						Question question = (Question) questionMapByUid.get(answer.getQuestionUniqueId());
						
						if (question instanceof Text
								&& matrixMapByUid.containsKey(answer.getPossibleAnswerUniqueId())) {
							if (result.containsKey(answer.getQuestionUniqueId())) {
								result.put(answer.getQuestionUniqueId(), result.get(answer.getQuestionUniqueId())
										+ "<br />" + matrixMapByUid.get(answer.getPossibleAnswerUniqueId()).getStrippedTitle());
							} else {
								result.put(answer.getQuestionUniqueId(),
										matrixMapByUid.get(answer.getPossibleAnswerUniqueId()).getStrippedTitle());
							}
						} else if (question == null && matrixMapByUid.containsKey(answer.getPossibleAnswerUniqueId())) {
							if (result.containsKey(answer.getQuestionUniqueId())) {
								result.put(answer.getQuestionUniqueId(), result.get(answer.getQuestionUniqueId())
										+ "<br />" + matrixMapByUid.get(answer.getPossibleAnswerUniqueId()).getTitle());
							} else {
								result.put(answer.getQuestionUniqueId(),
										matrixMapByUid.get(answer.getPossibleAnswerUniqueId()).getTitle());
							}
						} else if (question instanceof ChoiceQuestion) {							
							
							if (survey.getIsSelfAssessment() && question instanceof SingleChoiceQuestion) {
								SingleChoiceQuestion scq = (SingleChoiceQuestion)question;
								
								if (scq.getIsTargetDatasetQuestion()) {
									
									SATargetDataset dataset = selfassessmentService.getTargetDataset(Integer.parseInt(answer.getValue()));									
									
									if (result.containsKey(answer.getQuestionUniqueId())) {
										result.put(answer.getQuestionUniqueId(),
												result.get(answer.getQuestionUniqueId()) + "<br />" + dataset.getName());
									} else {
										result.put(answer.getQuestionUniqueId(), dataset.getName());
									}
									
									continue;
								}
							}
							
							String title = ((ChoiceQuestion) question)
									.getPossibleAnswerByUniqueId(answer.getPossibleAnswerUniqueId()) != null
											? ((ChoiceQuestion) question)
													.getPossibleAnswerByUniqueId(answer.getPossibleAnswerUniqueId())
													.getTitle()
											: "";
								title = ConversionTools.removeHTML(title);

							if (result.containsKey(answer.getQuestionUniqueId())) {
								result.put(answer.getQuestionUniqueId(),
										result.get(answer.getQuestionUniqueId()) + "<br />" + title);
							} else {
								result.put(answer.getQuestionUniqueId(), title);
							}
						} else if (question instanceof Upload) {
							if (answer.getFiles() != null && !answer.getFiles().isEmpty()) {
								for (File file : answer.getFiles()) {
									String name = "<a target='blank' href='" + contextpath + "/files/" + survey.getUniqueId() + "/" + file.getUid()
											+ "'>" + file.getName() + "</a><br />";

									if (result.containsKey(answer.getQuestionUniqueId())) {
										result.put(answer.getQuestionUniqueId(),
												result.get(answer.getQuestionUniqueId()) + "<br />" + name);
									} else {
										result.put(answer.getQuestionUniqueId(), name);
									}
								}
							}
						}  else if (question instanceof RankingQuestion) {

							String[] answerSplit = answer.getValue().split(";");

							Map<String, RankingItem> childs = ((RankingQuestion) question).getChildElementsByUniqueId();
							for (int i = 0; i < answerSplit.length; i++){
								answerSplit[i] = childs.get(answerSplit[i]).getStrippedTitle();
							}

							String answerReadable = String.join("; ", answerSplit);

							if (result.containsKey(answer.getQuestionUniqueId())) {
								result.put(answer.getQuestionUniqueId(),
										result.get(answer.getQuestionUniqueId()) + "<br />" + answerReadable);
							} else {
								result.put(answer.getQuestionUniqueId(), answerReadable);
							}

						} else if (question instanceof GalleryQuestion) {
							GalleryQuestion gallery = (GalleryQuestion) question;
							for (int i = 0; i < gallery.getFiles().size(); i++) {
								if (answer.getValue().equals(Integer.toString(i))) {
									String name = gallery.getFiles().get(i).getName() + "<br />";

									if (result.containsKey(answer.getQuestionUniqueId())) {
										result.put(answer.getQuestionUniqueId(),
												result.get(answer.getQuestionUniqueId()) + "<br />" + name);
									} else {
										result.put(answer.getQuestionUniqueId(), name);
									}
								}
							}
						} else if (question instanceof Table) {
							Integer row = answer.getRow();
							Integer column = answer.getColumn();

							String uidString = row.toString() + answer.getQuestionUniqueId() + column.toString();
							result.put(uidString, ConversionTools.escape(answer.getValue()));
						} else if (question instanceof ComplexTableItem) {
							ComplexTableItem item = (ComplexTableItem) question;
							
							if (item.getCellType() == ComplexTableItem.CellType.SingleChoice || item.getCellType() == ComplexTableItem.CellType.MultipleChoice) {
								String title = item.getPossibleAnswerByUniqueId(answer.getPossibleAnswerUniqueId()) != null
												? item.getPossibleAnswerByUniqueId(answer.getPossibleAnswerUniqueId())
												.getStrippedTitle()
												: "";

								if (result.containsKey(answer.getQuestionUniqueId())) {
									result.put(answer.getQuestionUniqueId(),
											result.get(answer.getQuestionUniqueId()) + "<br />" + title);
								} else {
									result.put(answer.getQuestionUniqueId(), title);
								}
							} else {
								if (result.containsKey(answer.getQuestionUniqueId())) {
									result.put(answer.getQuestionUniqueId(),
											result.get(answer.getQuestionUniqueId()) + "<br />"
													+ ConversionTools.escape(answer.getValue()));
								} else {
									result.put(answer.getQuestionUniqueId(),
											ConversionTools.escape(answer.getValue()));
								}
							}
							
						} else {

							if (answer.getPossibleAnswerUniqueId() != null && answer.getPossibleAnswerUniqueId().length() > 0) {
								// probably a deleted question / answer
							} else {

								if (question instanceof FreeTextQuestion
										&& ((FreeTextQuestion) question).getIsPassword()
										&& answer.getValue().length() > 0) {
									result.put(answer.getQuestionUniqueId(), "********");
								} else {
									if (result.containsKey(answer.getQuestionUniqueId())) {
										result.put(answer.getQuestionUniqueId(),
												result.get(answer.getQuestionUniqueId()) + "<br />"
														+ ConversionTools.escape(answer.getValue()));
									} else {
										result.put(answer.getQuestionUniqueId(),
												ConversionTools.escape(answer.getValue()));
									}
								}
							}
						}

					}

					return result;
				}

			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;
	}

	@GetMapping(value = "/export/{type}/{format}/{id}", headers = "Accept=*/*")
	public @ResponseBody String export(@PathVariable String type, @PathVariable String format, @PathVariable String id,
			HttpServletRequest request, HttpServletResponse response, Locale locale) {
		try {

			if (!checkCaptcha(request)) {
				return "errorcaptcha";
			}

			Survey survey = surveyService.getSurvey(Integer.parseInt(id), false, true);

			if (survey != null && ((type.startsWith("Statistics") && survey.getPublication().isShowStatistics())
					|| (!type.startsWith("Statistics") && survey.getPublication().isShowContent()))) {
				if (survey.getPublication().getPassword() != null
						&& survey.getPublication().getPassword().length() > 0) {
					String publicationpassword = (String) request.getSession().getAttribute("publicationpassword");
					if (publicationpassword == null
							|| !publicationpassword.equals(survey.getPublication().getPassword())) {
						return "not authenticated";
					}
				}


				Map<String, String[]> parameters = Ucs2Utf8.requestToHashMap(request);

				String email = parameters.get(Constants.EMAIL)[0];

				ResultFilter filter = survey.getPublication().getFilter().copy();
				putParameterFilters(parameters, filter.getFilterValues());

				var hash = filter.getHash(false);
				if (format.equalsIgnoreCase("pdf")){
					hash += locale.getLanguage();
				}

				StatisticsExecutor export = (StatisticsExecutor) context.getBean("statisticsExecutor");
				export.init(survey, type, format, hash, email, sender, host, locale, filter);
				taskExecutor.execute(export);
				return "success";
			} else {
				logger.error("try to export published results: " + type + Constants.PATH_DELIMITER + format + Constants.PATH_DELIMITER + id);
			}

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return Constants.ERROR;
	}

	@GetMapping(value = "/exportresultsxls/{id}", headers = "Accept=*/*")
	public @ResponseBody String exportresultsxls(@PathVariable String id, HttpServletRequest request,
												 HttpServletResponse response, Locale locale) {
		return exportExcelResults("xls", id, request, locale);
	}

	@GetMapping(value = "/exportresultsxlsx/{id}", headers = "Accept=*/*")
	public @ResponseBody String exportresultsxlsx(@PathVariable String id, HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		return exportExcelResults("xlsx", id, request, locale);
	}

	@GetMapping(value = "/exportresultsods/{id}", headers = "Accept=*/*")
	public @ResponseBody String exportresultsods(@PathVariable String id, HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		return exportExcelResults("ods", id, request, locale);
	}

	private String exportExcelResults(String type, String id, HttpServletRequest request, Locale locale) {
		try {
			Survey survey = surveyService.getSurvey(Integer.parseInt(id), false, true);
			if (survey != null && survey.getPublication().isShowContent()) {
				if (survey.getPublication().getPassword() != null
						&& survey.getPublication().getPassword().length() > 0) {
					String publicationpassword = (String) request.getSession().getAttribute("publicationpassword");
					if (publicationpassword == null
							|| !publicationpassword.equals(survey.getPublication().getPassword())) {
						throw new ForbiddenURLException();
					}
				}

				if (!checkCaptcha(request)) {
					return "errorcaptcha";
				}

				Map<String, String[]> parameters = Ucs2Utf8.requestToHashMap(request);
				String email = parameters.get(Constants.EMAIL)[0];
				ResultFilter filter = survey.getPublication().getFilter().copy();
				putParameterFilters(parameters, filter.getFilterValues());

				ResultsExecutor resultsExecutor = (ResultsExecutor) context.getBean("resultsExecutor");
				resultsExecutor.init(survey, filter, email, sender, host, fileDir, type,
						resources, locale, null);
				taskExecutor.execute(resultsExecutor);
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return Constants.ERROR;
		}
		return "success";
	}

	@GetMapping(value = "/exportfiles/{id}", headers = "Accept=*/*")
	public @ResponseBody String exportfiles(@PathVariable String id, HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		try {
			Survey survey = surveyService.getSurvey(Integer.parseInt(id), false, true);
			if (survey != null && survey.getPublication().isShowContent()
					&& survey.getPublication().getShowUploadedDocuments()) {
				if (survey.getPublication().getPassword() != null
						&& survey.getPublication().getPassword().length() > 0) {
					String publicationpassword = (String) request.getSession().getAttribute("publicationpassword");
					if (publicationpassword == null
							|| !publicationpassword.equals(survey.getPublication().getPassword())) {
						throw new ForbiddenURLException();
					}
				}

				if (!checkCaptcha(request)) {
					return "errorcaptcha";
				}

				Map<String, String[]> parameters = Ucs2Utf8.requestToHashMap(request);

				String question = parameters.get("question")[0];
				String email = parameters.get(Constants.EMAIL)[0];
				ResultFilter filter = survey.getPublication().getFilter();

				ResultsExecutor resultsExecutor = (ResultsExecutor) context.getBean("resultsExecutor");
				resultsExecutor.init(survey, filter, email, sender, host, fileDir, "files",
						resources, locale, question);
				taskExecutor.execute(resultsExecutor);
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return Constants.ERROR;
		}
		return "success";
	}
}
