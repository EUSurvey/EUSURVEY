package com.ec.survey.tools;

import com.ec.survey.exception.FrozenSurveyException;
import com.ec.survey.model.*;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.*;
import com.ec.survey.model.survey.ComplexTableItem.CellType;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.service.*;
import com.ec.survey.tools.activity.ActivityRegistry;
import com.lowagie.text.pdf.BaseFont;
import com.mysql.cj.util.StringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class SurveyHelper {

	private static final Logger logger = Logger.getLogger(SurveyHelper.class);

	private static void gatherExplanationUploadsForQuestion(Survey survey, Question question, String uniqueCode, AnswerSet answerSet, AnswerSet.ExplanationData explanationData, FileService fileService) {
		// find temporary directory
		java.io.File rootfolder = fileService.getSurveyExplanationUploadsFolder(survey.getUniqueId(), false);
		java.io.File directory = new java.io.File(
				rootfolder.getPath() + Constants.PATH_DELIMITER + uniqueCode + Constants.PATH_DELIMITER + question.getUniqueId());

		if (directory.exists()) {
			checkFiles(directory, fileService.getSurveyFilesFolder(survey.getUniqueId()), explanationData.files);
		}
	}

	public static AnswerSet parseAnswerSet(HttpServletRequest request, Survey survey, String uniqueCode, boolean update, String languageCode, User user, FileService fileService) {
		Map<Integer, Question> questions = survey.getQuestionMap();
		Map<Integer, Element> matrixQuestions = survey.getMatrixMap();

		Map<String, String[]> parameterMap = Ucs2Utf8.requestToHashMap(request);

		Iterator<String> iterator = parameterMap.keySet().iterator();

		AnswerSet answerSet = new AnswerSet();
		answerSet.setUpdateDate(new Date());

		answerSet.setLanguageCode(languageCode);

		if (!survey.isAnonymous()) {
			String ip = request.getRemoteAddr();
			if (request.getHeader("X-Forwarded-For") != null
					&& !request.getHeader("X-Forwarded-For").equalsIgnoreCase("0.0.0.0"))
				ip += " (" + request.getHeader("X-Forwarded-For") + ")";
			answerSet.setIP(ip);
		}

		if (!update) {
			answerSet.setDate(answerSet.getUpdateDate());
		}

		answerSet.setSurvey(survey);
		answerSet.setSurveyId(survey.getId());
		answerSet.setUniqueCode(uniqueCode);		
		
		if (survey.getTimeLimit().length() > 0) {
			Date startDate = (Date) request.getSession().getAttribute(Constants.START + uniqueCode);
			answerSet.setStartDate(startDate);
		}

		if (request.getParameter("disclaimerMinimized") != null) {
			answerSet.setDisclaimerMinimized(request.getParameter("disclaimerMinimized").equalsIgnoreCase("true"));
		}

		if (request.getParameter("wcagMode") != null) {
			answerSet.setWcagMode(request.getParameter("wcagMode").equalsIgnoreCase("true"));
		}
		
		if (request.getParameter("medianWarningVisible") != null) {
			answerSet.setMedianWarningVisible(request.getParameter("medianWarningVisible").equalsIgnoreCase("true"));
		}

		while (iterator.hasNext()) {
			String key = iterator.next(); // key is the question id
			String[] values = parameterMap.get(key);
			if (key.startsWith(Constants.ANSWER)) {
				key = key.substring(6);

				if (key.contains("|")) {
					if (values[0].isEmpty()) {
						continue;
					}
					String suffix = key.substring(key.indexOf('|') + 1);
					int row = Integer.parseInt(suffix.substring(0, suffix.indexOf('|')));
					int col = Integer.parseInt(suffix.substring(suffix.indexOf('|') + 1));
					key = key.substring(0, key.indexOf('|'));

					Table table = (Table) questions.get(Integer.parseInt(key));

					Answer answer = new Answer();
					answer.setAnswerSet(answerSet);
					answer.setQuestionUniqueId(table.getUniqueId());

					Element tablequestion = table.getQuestions().get(row - 1);
					Element tableanswer = table.getAnswers().get(col - 1);

					answer.setPossibleAnswerUniqueId(tablequestion.getUniqueId() + "#" + tableanswer.getUniqueId());

					answer.setValue(values[0]);
					answer.setRow(row);
					answer.setColumn(col);
					answerSet.addAnswer(answer);
				} else {

					Question question = questions.get(Integer.parseInt(key));

					if (question != null) {

						if (question instanceof Upload) {
							// find temporary directory
							java.io.File rootfolder = fileService.getSurveyUploadsFolder(survey.getUniqueId(), false);
							java.io.File directory = new java.io.File(
									rootfolder.getPath() + Constants.PATH_DELIMITER + uniqueCode + Constants.PATH_DELIMITER + question.getId());

							if (directory.exists()) {
								// create File items
								Answer answer = new Answer();
								answer.setAnswerSet(answerSet);

								checkFiles(directory, fileService.getSurveyFilesFolder(survey.getUniqueId()), answer.getFiles());

								if (!answer.getFiles().isEmpty()) {
									answer.setQuestionUniqueId(question.getUniqueId());
									answer.setValue("files");
									answerSet.addAnswer(answer);
								}
							}
						} else {
							for (String value : values) {
								if (value.isEmpty() || value.equalsIgnoreCase("false")
										|| (question instanceof DateQuestion && value.equalsIgnoreCase("DD/MM/YYYY"))
										|| (question instanceof TimeQuestion && value.equalsIgnoreCase("HH:mm:ss"))) {
									continue;
								}
								if (question instanceof RankingQuestion) {
									RankingQuestion rankingQuestion = (RankingQuestion) question;
									if (!rankingQuestion.isValidAnswer(value)) {
										continue;
									}
								}

								Answer answer = new Answer();
								answer.setAnswerSet(answerSet);
								answer.setQuestionUniqueId(question.getUniqueId());
								answer.setValue(value);

								if (question instanceof ChoiceQuestion) {
									if (!"EVOTE-ALL".equals(value)){
										int paid = Integer.parseInt(value);

										ChoiceQuestion cq = (ChoiceQuestion) question;
										answer.setPossibleAnswerUniqueId(cq.getPossibleAnswer(paid).getUniqueId());
									} else {
										answer.setPossibleAnswerUniqueId("EVOTE-ALL");
									}
								} else if (question instanceof ComplexTableItem) {
									ComplexTableItem item = (ComplexTableItem) question;
									if (item.getCellType() == ComplexTableItem.CellType.SingleChoice || item.getCellType() == ComplexTableItem.CellType.MultipleChoice) {
										int paid = Integer.parseInt(value);
										answer.setPossibleAnswerUniqueId(item.getPossibleAnswer(paid).getUniqueId());
									}
								} else if (question instanceof GalleryQuestion) {
									GalleryQuestion gallery = (GalleryQuestion)question;
									int index = Integer.parseInt(value);
									File file = gallery.getFiles().get(index);
									answer.setPossibleAnswerUniqueId(file.getUid());
								}
																					
								answerSet.addAnswer(answer);
							}
						}	
					} else {
						// try matrix

						Element element = matrixQuestions.get(Integer.parseInt(key));
						if (element != null) {
							for (String value : values) {
								if (value.isEmpty()) {
									continue;
								}
								Answer answer = new Answer();
								answer.setAnswerSet(answerSet);
								answer.setQuestionUniqueId(element.getUniqueId());
								answer.setValue(value);

								int paid = Integer.parseInt(value);

								Element pa = matrixQuestions.get(paid);
								if (pa != null) {
									answer.setPossibleAnswerUniqueId(pa.getUniqueId());
								}

								answerSet.addAnswer(answer);
							}
						}
					}
				}
			} else if (key.startsWith("explanation")) {
				key = key.substring(11);
				Question question = questions.get(Integer.parseInt(key));
				if (question != null && question.getIsDelphiQuestion()) {
					String[] explanationText = parameterMap.get("explanation" + question.getId());
					AnswerSet.ExplanationData explanationData = new AnswerSet.ExplanationData();
					boolean doUpdateAnswerSet = false;
					if (explanationText != null && explanationText.length > 0) {
						explanationData.text = explanationText[0];
						doUpdateAnswerSet = true;
					}
					gatherExplanationUploadsForQuestion(survey, question, uniqueCode, answerSet, explanationData, fileService);
					if (!explanationData.files.isEmpty()) {
						doUpdateAnswerSet = true;
					}
					if (doUpdateAnswerSet) {
						answerSet.getExplanations().put(question.getUniqueId(), explanationData);
					}
				}
			}
		}

		if (survey.getIsOPC() && user != null) {
			for (Question q : survey.getQuestions()) {
				if (q instanceof FreeTextQuestion) {
					if (q.getShortname().equalsIgnoreCase("firstName")) {
						Answer answer = new Answer();
						answer.setAnswerSet(answerSet);
						answer.setQuestionUniqueId(q.getUniqueId());
						answer.setValue(user.getGivenName());

						answerSet.clearAnswers(q);
						answerSet.addAnswer(answer);
					} else if (q.getShortname().equalsIgnoreCase("surname")) {
						Answer answer = new Answer();
						answer.setAnswerSet(answerSet);
						answer.setQuestionUniqueId(q.getUniqueId());
						answer.setValue(user.getSurName());

						answerSet.clearAnswers(q);
						answerSet.addAnswer(answer);
					} else if (q.getShortname().equalsIgnoreCase(Constants.EMAIL)) {
						Answer answer = new Answer();
						answer.setAnswerSet(answerSet);
						answer.setQuestionUniqueId(q.getUniqueId());
						answer.setValue(user.getEmail());

						answerSet.clearAnswers(q);
						answerSet.addAnswer(answer);
					}
				}
			}
		}

		if (survey.getIsQuiz()) {
			answerSet.setScore(QuizHelper.getQuizResult(answerSet).getScore());
		}

		return answerSet;
	}

	//answerSet.getSurvey().getElements()
	public static Map<Element, String>  validateAnswerSet(AnswerSet answerSet, AnswerService answerService,
			Set<String> invisibleElements, MessageSource resources, Locale locale, String draftid,
			HttpServletRequest request, boolean skipDraftCreation, User user, FileService fileService)
			throws InterruptedException, IOException {
		return validateAnswerSet(answerSet, answerService,
						invisibleElements, resources, locale, draftid,
						request, skipDraftCreation, user, fileService, answerSet.getSurvey().getElements());
	}
	
	public static Map<Element, String> validateAnswerSet(AnswerSet answerSet, AnswerService answerService,
			Set<String> invisibleElements, MessageSource resources, Locale locale, String draftid,
			HttpServletRequest request, boolean skipDraftCreation, User user, FileService fileService, List<Element> elements)
			throws InterruptedException, IOException {
		Map<Element, List<Element>> dependencies = answerSet.getSurvey().getTriggersByDependantElement();
		HashMap<Element, String> result = new HashMap<>();

		Draft draft = null;
		if (draftid != null && draftid.length() > 0) {
			draft = answerService.getDraft(draftid);
		}
		if (draft == null) {
			draft = answerService.getDraftByAnswerUID(answerSet.getUniqueCode());
		}

		boolean lastSectionInvisible = false;

		for (Element element : elements) {
			element.setSurvey(answerSet.getSurvey());

			if (!lastSectionInvisible || ((element instanceof Section) && ((Section) element).getLevel() == 1)) {
				validateElement(element, answerSet, dependencies, result, answerService, invisibleElements, resources,
						locale, null, request, draft);
				
				if (element instanceof Matrix) {
					Matrix m = (Matrix) element;					

					for (Element matrixquestion : m.getQuestions()) {
						matrixquestion.setSurvey(m.getSurvey());
						validateElement(matrixquestion, answerSet, dependencies, result, answerService,
								invisibleElements, resources, locale, m, request, draft);
					}
				}
			} else if (lastSectionInvisible && answerSet.getSurvey().getMultiPaging()) {
				// special case: elements in an invisible page
				invisibleElements.add(element.getUniqueId());

				List<Answer> answers = new ArrayList<>();
				if (element instanceof Matrix) {
					answers = answerSet.getMatrixAnswers((Matrix) element);
				} else if (element instanceof RatingQuestion) {
					RatingQuestion rating = (RatingQuestion) element;
					for (Element child : rating.getChildElements()) {
						answers.addAll(answerSet.getAnswers(child.getUniqueId()));
					}
				} else {
					answers = answerSet.getAnswers(element.getUniqueId());
				}

				for (Answer answer : answers) {
					answerSet.getAnswers().remove(answer);
				}
			}

			if (element instanceof Section && ((Section) element).getLevel() == 1) {
				if (answerSet.getSurvey().getMultiPaging() && invisibleElements.contains(element.getUniqueId())) {
					lastSectionInvisible = true;
				} else {
					lastSectionInvisible = false;
				}
			} else if (element instanceof RatingQuestion) {
				RatingQuestion r = (RatingQuestion) element;
				boolean atLeastOneQuestionVisible = false;
				for (Element childquestion : r.getQuestions()) {
					childquestion.setSurvey(r.getSurvey());
					validateElement(childquestion, answerSet, dependencies, result, answerService, invisibleElements,
							resources, locale, r, request, draft);
					if (!invisibleElements.contains(childquestion.getUniqueId())) {
						atLeastOneQuestionVisible = true;
					}
				}
				if (!atLeastOneQuestionVisible && !invisibleElements.contains(r.getUniqueId())) {
					invisibleElements.add(r.getUniqueId());
				}
			} else if (element instanceof ComplexTable) {
				ComplexTable c = (ComplexTable) element;
				for (Element childquestion : c.getQuestionChildElements()) {
					childquestion.setSurvey(c.getSurvey());
					validateElement(childquestion, answerSet, dependencies, result, answerService, invisibleElements,
							resources, locale, c, request, draft);
				}
			}
		}

		if (!skipDraftCreation && result.size() > 0) {
			// save draft and remove password values (security)
			String uid = UUID.randomUUID().toString();

			if (draft != null) {
				SurveyHelper.parseAndMergeAnswerSet(request, answerSet.getSurvey(),
						answerSet.getUniqueCode(), draft.getAnswerSet(), answerSet.getLanguageCode(), user,
						fileService);
				draft.getAnswerSet().setIsDraft(true); // this also sets the ISDRAFT flag of the answers inside the
														// answerset
				uid = draft.getUniqueId();
			}

			if (draft == null) {
				draft = new Draft();
				draft.setUniqueId(uid);
				answerSet.setIsDraft(true);
				draft.setAnswerSet(answerSet);
			}

			try {
				answerService.saveDraft(draft, false);
			} catch (Exception e) {
				//ignore
			}

			result.put(new DraftIDElement(), uid);
		}

		return result;
	}

	public static boolean validateElement(Element element, AnswerSet answerSet,
			Map<Element, List<Element>> dependencies, Map<Element, String> result, AnswerService answerService,
			Set<String> invisibleElements, MessageSource resources, Locale locale, Element parentElement,
			HttpServletRequest request, Draft draft) {
		try {

			List<Answer> answers;

			Matrix parent = null;
			if (parentElement instanceof Matrix)
				parent = (Matrix) parentElement;

			if (element instanceof Question) {
				Question question = (Question) element;

				if (question instanceof Matrix) {
					answers = answerSet.getMatrixAnswers((Matrix) question);
				} else {
					answers = answerSet.getAnswers(question.getUniqueId());
				}

				// check dependency
				if (parent == null ? question.getIsDependent() : question.getIsDependentMatrixQuestion()) {
					List<Element> deps = dependencies.get(question);
					List<Element> questiondependencies = null;
					if (deps != null)
						questiondependencies = new ArrayList<>(deps);

					if (questiondependencies == null && parent != null) {
						// a question without dependencies inside a dependent matrix
						deps = dependencies.get(parent);
						if (deps != null)
							questiondependencies = new ArrayList<>(deps);
					} else if (parent != null) {
						// a question with dependencies inside (possibly) a dependent matrix
						deps = dependencies.get(element);
						if (deps != null)
							questiondependencies.addAll(deps);
					}

					boolean found = checkDependencies(questiondependencies, element, question, parent, answerSet, invisibleElements);

					if (found && question instanceof Matrix) {
						boolean answerPossibilityVisible = false;
						boolean subquestionFound = false;
						for (Element matrixquestion : ((Matrix) question).getQuestions()) {
							List<Element> matrixquestiondependencies = dependencies.get(matrixquestion);
							if (matrixquestiondependencies != null) {
								matrixquestiondependencies.remove(questiondependencies);
								subquestionFound = checkDependencies(matrixquestiondependencies, matrixquestion, (Question) matrixquestion, parent, answerSet, invisibleElements);
								if (!subquestionFound) {
									invisibleElements.add(matrixquestion.getUniqueId());
								}
								answerPossibilityVisible = answerPossibilityVisible || subquestionFound;
							} else {
								// matrix question has no own dependency
								answerPossibilityVisible = true;
							}
							
						}
						if (!answerPossibilityVisible) {
							found = false;
						}
					} else if (found && parent != null & parent instanceof Matrix) {
						if (invisibleElements.contains(parent.getUniqueId())) {
							found = false;
							invisibleElements.add(question.getUniqueId());
						}
					}
					
					if (!found) {
						invisibleElements.add(question.getUniqueId());
					}
					
					if (!(element instanceof Matrix) && !question.getOptional() && answers.isEmpty() && found) {
						result.put(element,
								resources.getMessage("validation.required", null, "This field is required.", locale));
					} else if (!(element instanceof Matrix) && !found && !answers.isEmpty()) {
						// this answer must be ignored because the dependent question was not triggered
						for (Answer answer : answers) {
							answerSet.getAnswers().remove(answer);
						}

						answers.clear();
					} else if (element instanceof RatingQuestion && !found) {
						RatingQuestion rating = (RatingQuestion) element;
						for (Element child : rating.getChildElements()) {
							List<Answer> childanswers = answerSet.getAnswers(child.getUniqueId());
							for (Answer answer : childanswers) {
								answerSet.getAnswers().remove(answer);
							}
						}
					}

					//matrix must be ignored because the dependent question was not triggered
					if (element instanceof  Matrix && !found && !answers.isEmpty()) {
						for(Answer answer : answers) {
							answerSet.getAnswers().remove(answer);
						}
					}

				} else {

					if (!(element instanceof Matrix) && !(element instanceof RatingQuestion) && !question.getOptional()
							&& answers.isEmpty()
							&& (parentElement == null || !invisibleElements.contains(parentElement.getUniqueId()))) {
						result.put(element,
								resources.getMessage("validation.required", null, "This field is required.", locale));
					}
				}

				if (element instanceof FreeTextQuestion) {
					FreeTextQuestion freeTextQuestion = (FreeTextQuestion) element;
					String answer = "";
					if (!answers.isEmpty())
						answer = answers.get(0).getValue();

					if (!(freeTextQuestion.getIsPassword() && answer.equals("********"))) {
						if (freeTextQuestion.getMinCharacters() > 0 && answer.length() > 0
								&& answer.length() < freeTextQuestion.getMinCharacters()) {
							result.put(element, resources.getMessage("validation.textNotLongEnough", null,
									"This text is not long enough", locale));
						}

						if (freeTextQuestion.getMaxCharacters() > 0 && answer.length() > 0
								&& answer.length() > freeTextQuestion.getMaxCharacters()) {
							result.put(element, resources.getMessage("validation.textTooLong", null,
									"This text is too long", locale));
						}
					}

					if (freeTextQuestion.getIsPassword() && freeTextQuestion.getIsComparable()
							&& answer.equals("********")) {
						String second = request.getParameter("secondanswer" + freeTextQuestion.getId());

						if (draft != null) {
							List<Answer> originalAnswers = draft.getAnswerSet().getAnswers(freeTextQuestion.getUniqueId());
							if (!originalAnswers.isEmpty()) {
								String first = originalAnswers.get(0).getValue();
								List<Answer> currentPasswordAnswers = answerSet.getAnswers(freeTextQuestion.getUniqueId());
								if (!currentPasswordAnswers.isEmpty()) {
									String currentAnswer = currentPasswordAnswers.get(0).getValue();
									if (currentAnswer != null && currentAnswer.equalsIgnoreCase("********")
											&& !first.equals(second)) {
										result.put(element, resources.getMessage("validation.nomatch", null,
												"The two values do not match", locale));
									}
								}
							}
						}
					}

					if (answerSet.getSurvey().getRegistrationForm()
							&& question.getAttributeName().equalsIgnoreCase(Constants.EMAIL)
							&& !MailService.isValidEmailAddress(answer)) {
						result.put(element, resources.getMessage("validation.invalidEmail", null,
								"This is not a valid email address", locale));
					}

					if (freeTextQuestion.getIsUnique()) {
						int existing = answerService.getNumberAnswersForValue(answer, freeTextQuestion.getId(),
								freeTextQuestion.getUniqueId(), answerSet.getSurvey().getIsDraft(),
								answerSet.getUniqueCode());
						if (existing > 0)
							result.put(element, resources.getMessage("validation.notUnique", null,
									"This input already exists. Please try another entry.", locale));
					}

					if (freeTextQuestion.getIsPassword()
							&& freeTextQuestion.getSurvey().getShortname().equalsIgnoreCase("NewSelfRegistrationSurvey")
							&& answer.length() > 0 && answer != null && !answer.equals("********")
							&& Tools.isPasswordWeak(answer)) {
						result.put(element, resources.getMessage("error.PasswordWeak", null,
								"This password does not fit our password policy. Please choose a password between 8 and 16 characters with at least one digit and one non-alphanumeric characters (e.g. !?$%...).",
								locale));
					}
				}

				if (element instanceof RegExQuestion) {
					RegExQuestion regExQuestion = (RegExQuestion) element;
					String answer = "";
					if (!answers.isEmpty())
						answer = answers.get(0).getValue();
					if (regExQuestion.getIsUnique()) {
						int existing = answerService.getNumberAnswersForValue(answer, regExQuestion.getId(),
								regExQuestion.getUniqueId(), answerSet.getSurvey().getIsDraft(),
								answerSet.getUniqueCode());
						if (existing > 0)
							result.put(element, resources.getMessage("validation.notUnique", null,
									"This input already exists. Please try another entry.", locale));
					}
				}

				if (element instanceof NumberQuestion || element instanceof FormulaQuestion) {
					checkNumber(element, answerSet, answers, result, resources, locale, answerService);
				}

				if (element instanceof DateQuestion) {
					DateQuestion dateQuestion = (DateQuestion) element;

					Date answer = null;
					if (!answers.isEmpty() && answers.get(0).getValue().length() > 0)
						answer = ConversionTools.getDate(answers.get(0).getValue());

					if (dateQuestion.getMin() != null && answer != null
							&& dateQuestion.getMin().compareTo(answer) > 0) {
						result.put(element, resources.getMessage("validation.valueTooSmall", null,
								"This value is too small", locale));
					}

					if (dateQuestion.getMax() != null && answer != null
							&& dateQuestion.getMax().compareTo(answer) < 0) {
						result.put(element,
								resources.getMessage("validation.valueTooBig", null, "This value is too big", locale));
					}
				}

				if (element instanceof TimeQuestion) {
					TimeQuestion timeQuestion = (TimeQuestion) element;

					String answer = null;
					if (!answers.isEmpty() && answers.get(0).getValue().length() > 0)
						answer = answers.get(0).getValue();

					if (timeQuestion.getMin() != null && timeQuestion.getMin().length() > 0 && answer != null) {
						Date timeMin = new SimpleDateFormat(ConversionTools.TimeFormat).parse(timeQuestion.getMin());
						Calendar calendarMin = Calendar.getInstance();
						calendarMin.setTime(timeMin);
						calendarMin.add(Calendar.DATE, 1);

						Date timeAnswer = new SimpleDateFormat(ConversionTools.TimeFormat).parse(answer);
						Calendar calendarAnswer = Calendar.getInstance();
						calendarAnswer.setTime(timeAnswer);
						calendarAnswer.add(Calendar.DATE, 1);

						if (calendarMin.getTime().after(calendarAnswer.getTime())) {
							result.put(element, resources.getMessage("validation.valueTooSmall", null,
									"This value is too small", locale));
						}
					}

					if (timeQuestion.getMax() != null && timeQuestion.getMax().length() > 0 && answer != null) {
						Date timeMax = new SimpleDateFormat(ConversionTools.TimeFormat).parse(timeQuestion.getMax());
						Calendar calendarMax = Calendar.getInstance();
						calendarMax.setTime(timeMax);
						calendarMax.add(Calendar.DATE, 1);

						Date timeAnswer = new SimpleDateFormat(ConversionTools.TimeFormat).parse(answer);
						Calendar calendarAnswer = Calendar.getInstance();
						calendarAnswer.setTime(timeAnswer);
						calendarAnswer.add(Calendar.DATE, 1);

						if (calendarMax.getTime().before(calendarAnswer.getTime())) {
							result.put(element, resources.getMessage("validation.valueTooBig", null,
									"This value is too big", locale));
						}
					}
				}

				if (element instanceof MultipleChoiceQuestion) {
					MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) element;

					if (multipleChoiceQuestion.getMinChoices() > answers.size() && !answers.isEmpty()) {
						result.put(element, resources.getMessage("validation.notEnoughAnswers", null,
								"Not enough answers selected", locale));
					}

					if (multipleChoiceQuestion.getMaxChoices() < answers.size()
							&& multipleChoiceQuestion.getMaxChoices() > 0 && !answers.isEmpty()) {
						result.put(element, resources.getMessage("validation.tooManyAnswers", null,
								"Too many answers selected", locale));
					}
				}

				if (element instanceof Table) {
					boolean found = false;
					for (Answer answerelem : answers) {
						String answer = answerelem.getValue().replaceAll("[^\\u0000-\\uFFFF]", "\uFFFD");

						if (!answers.isEmpty() && answer.indexOf('\uFFFD') > -1) {
							answerelem.setValue(answer);
							found = true;
						}
					}
					if (found) {
						result.put(element, resources.getMessage("validation.invalidCharacter", null,
								"Invalid (non UTF-8) characters detected. Please check your input.", locale));
					}
				}
				
				if (element instanceof ComplexTableItem) {
					ComplexTableItem complexTableItem = (ComplexTableItem) element;
					
					if (invisibleElements.contains(parentElement.getUniqueId())) {
						invisibleElements.add(complexTableItem.getUniqueId());
						
						// this answer must be ignored because the dependent question was not triggered
						for (Answer answer : answers) {
							answerSet.getAnswers().remove(answer);
						}
						
						answers.clear();
					} else {					
						switch (complexTableItem.getCellType()) {
							case FreeText:
								String answer = "";
								if (!answers.isEmpty())
									answer = answers.get(0).getValue();
	
								if (complexTableItem.getMinCharacters() > 0 && answer.length() > 0
										&& answer.length() < complexTableItem.getMinCharacters()) {
									result.put(element, resources.getMessage("validation.textNotLongEnough", null,
											"This text is not long enough", locale));
								}
	
								if (complexTableItem.getMaxCharacters() > 0 && answer.length() > 0
										&& answer.length() > complexTableItem.getMaxCharacters()) {
									result.put(element, resources.getMessage("validation.textTooLong", null,
											"This text is too long", locale));
								}					
								break;
							case MultipleChoice:
								if (complexTableItem.getMinChoices() > answers.size() && !answers.isEmpty()) {
									result.put(element, resources.getMessage("validation.notEnoughAnswers", null,
											"Not enough answers selected", locale));
								}
	
								if (complexTableItem.getMaxChoices() < answers.size()
										&& complexTableItem.getMaxChoices() > 0 && !answers.isEmpty()) {
									result.put(element, resources.getMessage("validation.tooManyAnswers", null,
											"Too many answers selected", locale));
								}
								break;
							case Number:
							case Formula:
								checkNumber(complexTableItem, answerSet, answers, result, resources, locale, answerService);
								break;
						}
					}
				}

			} else if (element instanceof Section) {
				Section section = (Section) element;

				if (section.getIsDependent()) {
					boolean found = false;
					for (Element trigger : dependencies.get(section)) {
						if (trigger instanceof Matrix) {
							Matrix m = (Matrix) trigger;
							for (DependencyItem candidate : m.getDependentElements()) {
								for (Element dependent : candidate.getDependentElements()) {
									if (dependent.getId().equals(section.getId())
											|| dependent.getUniqueId().equals(section.getUniqueId())) {
										Element matrixQuestion = m.getQuestions()
												.get(candidate.getPosition() / (m.getColumns() - 1));
										Element matrixAnswer = m.getAnswers()
												.get(candidate.getPosition() % (m.getColumns() - 1));
										if (answerSet.getMatrixAnswer(matrixQuestion.getUniqueId(),
														matrixAnswer.getUniqueId()) != null) {
											found = true;
										}
									}
								}
							}
						} else {
							if (trigger instanceof PossibleAnswer) {
								PossibleAnswer possibleAnswer = (PossibleAnswer) trigger;
								for (Answer answer : answerSet.getAnswers(
										answerSet.getSurvey().getElementsById().get(possibleAnswer.getQuestionId())
												.getUniqueId())) {
									if (answer.getPossibleAnswerUniqueId().equals(trigger.getUniqueId())) {
										found = true;
									}
								}
							}
						}
					}

					if (!found) {
						invisibleElements.add(section.getUniqueId());
					}
				}
			}

			return true;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return false;
		}
	}
	
	public static Question getFirstUnansweredMandatoryReadonlyQuestion(AnswerSet answerSet) {
		//check that all readonly mandatory questions are answered
		for (Question question : answerSet.getSurvey().getQuestions()) {
			if (question instanceof MatrixOrTable && question.getReadonly()) {
				MatrixOrTable m = (MatrixOrTable) question;
				for (Element sub : m.getQuestions()) {
					if (sub instanceof Text) {
						Text t = (Text)sub;
						if (!t.getOptional()) {
							if (answerSet.getAnswers(t.getUniqueId()).isEmpty()) {
								return t;								
							}
						}
					}
				}
			} else if (!question.getOptional() && question.getReadonly()) {
				if (answerSet.getAnswers(question.getUniqueId()).isEmpty()) {
					return question;
				}
			}				
		}
		
		return null;
	}

	public static boolean checkDependencies(List<Element> questiondependencies, Element element, Question question, Matrix parent, AnswerSet answerSet, Set<String> invisibleElements) {
		boolean found = false;
		boolean missing = false;
		if (questiondependencies != null)
			for (Element trigger : questiondependencies) {
				if (trigger instanceof Matrix) {
					Matrix m = (Matrix) trigger;
					for (DependencyItem candidate : m.getDependentElements()) {
						Set<String> dependentElementUniqueIds = candidate.getDependentElementUniqueIds();
						if (dependentElementUniqueIds.contains(question.getUniqueId()) || (parent != null
								&& dependentElementUniqueIds.contains(parent.getUniqueId()))) {
							Element matrixQuestion = m.getQuestions()
									.get(candidate.getPosition() / (m.getColumns() - 1));
							Element matrixAnswer = m.getAnswers()
									.get(candidate.getPosition() % (m.getColumns() - 1));
							if ((answerSet.getMatrixAnswer(matrixQuestion.getUniqueId(), matrixAnswer.getUniqueId()) != null
									|| answerSet.getMatrixAnswer(matrixQuestion.getUniqueId(), matrixAnswer.getUniqueId()) != null) && !invisibleElements.contains(matrixQuestion.getUniqueId()) && !invisibleElements.contains(m.getUniqueId())) {
								found = true;
							} else {
								missing = true;
							}
						}
					}
				} else {
					if (trigger instanceof PossibleAnswer) {
						PossibleAnswer possibleAnswer = (PossibleAnswer) trigger;
						boolean paFound = false;
						for (Answer answer : answerSet.getAnswers(
								answerSet.getSurvey().getElementsById().get(possibleAnswer.getQuestionId())
										.getUniqueId())) {
							if (((answer.getPossibleAnswerUniqueId() != null && answer
									.getPossibleAnswerUniqueId().equals(trigger.getUniqueId())))
									&& !invisibleElements.contains(answer.getQuestionUniqueId())) {
								found = true;
								paFound = true;
							}
						}
						if (!paFound) {
							missing = true;
						}
					}
				}
			}

		if (element.getIsDependent() && element.getUseAndLogic() && missing) {
			found = false;
		}

		return found;
	}

	private static void checkNumber(Element element, AnswerSet answerSet, List<Answer> answers, Map<Element, String> result, MessageSource resources, Locale locale, AnswerService answerService) {
		double answer = 0;
		Double min = null;
		Double max = null;
		boolean unique = false;
		if (element instanceof NumberQuestion) {
			NumberQuestion n = (NumberQuestion) element;
			min = n.getMin();
			max = n.getMax();
			unique = n.getIsUnique();
		} else if (element instanceof FormulaQuestion) {
			FormulaQuestion f = (FormulaQuestion) element;
			min = f.getMin();
			max = f.getMax();
		} else {
			ComplexTableItem item = (ComplexTableItem) element;
			min = item.getMin();
			max = item.getMax();
		}
		try {
			if (!answers.isEmpty() && answers.get(0).getValue().length() > 0) {
				answer = Double.parseDouble(answers.get(0).getValue());

				if (min != null && answer != 0 && answer < min) {
					result.put(element, resources.getMessage("validation.valueTooSmall", null,
							"This value is too small", locale));
				}

				if (max != null && answer != 0 && answer > max) {
					result.put(element, resources.getMessage("validation.valueTooBig", null,
							"This value is too big", locale));
				}

				if (unique) {
					int existing = answerService.getNumberAnswersForValue(answers.get(0).getValue().trim(),
							element.getId(), element.getUniqueId(),
							answerSet.getSurvey().getIsDraft(), answerSet.getUniqueCode());
					if (existing > 0)
						result.put(element, resources.getMessage("validation.notUnique", null,
								"This input already exists. Please try another entry.", locale));
				}
			}
		} catch (Exception e) {
			result.put(element, resources.getMessage("validation.invalidNumber", null,
					"This value is not a valid number", locale));
		}
		
	}

	public static AnswerSet parseAndMergeAnswerSet(HttpServletRequest request, Survey survey, String uniqueCode, AnswerSet answerSet, String languageCode, User user, FileService fileService) throws IOException {
		if (user == null && survey.getIsOPC()) {
			// edit contribution
			user = new User();
		}

		// keep passwords
		Map<String, String> passwordValues = new HashMap<>();
		Set<String> uploadedFiles = new HashSet<>();
		for (Element element : survey.getElements()) {
			if (element instanceof FreeTextQuestion) {
				FreeTextQuestion q = (FreeTextQuestion) element;
				if (q.getIsPassword()) {
					List<Answer> answers = answerSet.getAnswers(q.getUniqueId());
					if (!answers.isEmpty()) {
						passwordValues.put(q.getUniqueId(), answers.get(0).getValue());
					}
				} else if (survey.getIsOPC() && q.getShortname().equalsIgnoreCase("firstName")) {
					List<Answer> answers = answerSet.getAnswers(q.getUniqueId());
					if (!answers.isEmpty()) {
						user.setGivenName(answers.get(0).getValue());
					}
				} else if (survey.getIsOPC() && q.getShortname().equalsIgnoreCase("surname")) {
					List<Answer> answers = answerSet.getAnswers(q.getUniqueId());
					if (!answers.isEmpty()) {
						user.setSurName(answers.get(0).getValue());
					}
				} else if (survey.getIsOPC() && q.getShortname().equalsIgnoreCase(Constants.EMAIL)) {
					List<Answer> answers = answerSet.getAnswers(q.getUniqueId());
					if (!answers.isEmpty()) {
						user.setEmail(answers.get(0).getValue());
					}
				}
			} else if (element instanceof Upload) {
				Upload d = (Upload) element;
				List<Answer> answers = answerSet.getAnswers(d.getUniqueId());
				for (Answer answer : answers) {
					if (answer.getFiles() != null) {
						for (File f : answer.getFiles()) {
							uploadedFiles.add(f.getUid());
						}
					}
				}
			}
		}

		answerSet.getAnswers().clear();

		AnswerSet parsedAnswerSet = parseAnswerSet(request, survey, uniqueCode, true, languageCode, user,
				fileService);

		for (Answer answer : parsedAnswerSet.getAnswers()) {
			answer.setAnswerSet(answerSet);
			try {
				if (answer.getValue() != null && answer.getValue().equalsIgnoreCase("********")
						&& passwordValues.containsKey(answer.getQuestionUniqueId())) {
					answer.setValue(passwordValues.get(answer.getQuestionUniqueId()));
				}
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}
			answerSet.getAnswers().add(answer);

			if (answer.getFiles() != null) {
				for (File f : answer.getFiles()) {
					if (uploadedFiles.contains(f.getUid())) {
						uploadedFiles.remove(f.getUid());
					}
				}
			}
		}

		answerSet.setLanguageCode(parsedAnswerSet.getLanguageCode());
		answerSet.setSurvey(survey);
		answerSet.setWcagMode(parsedAnswerSet.getWcagMode());
		answerSet.setDisclaimerMinimized(parsedAnswerSet.getDisclaimerMinimized());
		answerSet.getExplanations().putAll(parsedAnswerSet.getExplanations());

		// remove deleted uploaded files from the file system
		for (String uid : uploadedFiles) {
			java.io.File file = fileService.getSurveyUploadFile(survey.getUniqueId(), uid);
			Files.deleteIfExists(file.toPath());
		}

		return answerSet;
	}

	public static AnswerSet parseAndMergeDelphiAnswerSet(HttpServletRequest request, Survey survey, String uniqueCode, AnswerSet answerSet, String languageCode, User user, FileService fileService, Element question) throws IOException {
		AnswerSet parsedAnswerSet = parseAnswerSet(request, survey, uniqueCode, true, languageCode, user,
				fileService);

		//remove all answers for questions that were answered here
		for (Answer answer : parsedAnswerSet.getAnswers()) {
			List<Answer> oldAnswers = answerSet.getAnswers(answer.getQuestionUniqueId());
			for (Answer oldAnswer: oldAnswers) {
				answerSet.getAnswers().remove(oldAnswer);
			}
		}

		if (question != null) {
			if (question instanceof Matrix) {
				Matrix parent = (Matrix) question;
				for (Element childQuestion : parent.getQuestions()) {
					List<Answer> oldAnswers = answerSet.getAnswers(childQuestion.getUniqueId());
					for (Answer oldAnswer : oldAnswers) {
						answerSet.getAnswers().remove(oldAnswer);
					}
				}
			} else if (question instanceof RatingQuestion) {
				RatingQuestion parent = (RatingQuestion) question;
				for (Element childQuestion : parent.getQuestions())
				{
					List<Answer> oldAnswers = answerSet.getAnswers(childQuestion.getUniqueId());
					for (Answer oldAnswer: oldAnswers) {
						answerSet.getAnswers().remove(oldAnswer);
					}
				}
			} else {
				List<Answer> oldAnswers = answerSet.getAnswers(question.getUniqueId());
				for (Answer oldAnswer: oldAnswers) {
					answerSet.getAnswers().remove(oldAnswer);
				}
			}
		}
		
		for (Answer answer : parsedAnswerSet.getAnswers()) {
			answer.setAnswerSet(answerSet);
			
			//add new answers for the question
			answerSet.getAnswers().add(answer);
		}
		
		answerSet.getExplanations().putAll(parsedAnswerSet.getExplanations());
		
		answerSet.setMedianWarningVisible(parsedAnswerSet.getMedianWarningVisible());
	
		return answerSet;
	}

	public static void recreateUploadedFiles(AnswerSet answerSet, Survey survey, FileService fileService,
			AnswerExplanationService answerExplanationService) {

		recreateDelphiExplanationUploadedFiles(answerSet, survey, fileService, answerExplanationService);
		recreateUploadedAnswerFiles(answerSet, survey, fileService);
	}

	private static void recreateDelphiExplanationUploadedFiles(AnswerSet answerSet, Survey survey,
			FileService fileService, AnswerExplanationService answerExplanationService) {

		if (answerSet.getId() == null) {
			return;
		}
		
		int answerSetId = answerSet.getId();
		String surveyUniqueId = survey.getUniqueId();

		List<AnswerExplanation> explanations = answerExplanationService.getExplanations(answerSetId);
		for (AnswerExplanation explanation : explanations) {
			String questionUid = explanation.getQuestionUid();
			for (File file : explanation.getFiles()) {
				String filename = file.getName();
				if (null != filename) {
					FileInputStream in = null;
					FileOutputStream out = null;

					try {
						java.io.File folder = fileService.getSurveyFilesFolder(surveyUniqueId);
						in = new FileInputStream(folder.getPath() + Constants.PATH_DELIMITER + file.getUid());

						java.io.File basePath = fileService.getSurveyExplanationUploadsFolder(surveyUniqueId, false);

						java.io.File directory = new java.io.File(basePath + Constants.PATH_DELIMITER + answerSet.getUniqueCode() + Constants.PATH_DELIMITER + questionUid);
						directory.mkdirs();
						java.io.File fileOut = new java.io.File(directory.getPath() + Constants.PATH_DELIMITER + file.getName());
						out = new FileOutputStream(fileOut);

						IOUtils.copy(in, out);

					} catch (Exception e) {
						logger.error(e.getLocalizedMessage(), e);
					} finally {
						try {
							in.close();
							out.close();
						} catch (Exception e) {
							// ignore
						}
					}
				}
			}
		}
	}

	private static void recreateUploadedAnswerFiles(AnswerSet answerSet, Survey survey, FileService fileService) {

		Map<String, Element> elementsByUniqueId = survey.getElementsByUniqueId();

		for (Answer answer : answerSet.getAnswers()) {
			for (File file : answer.getFiles()) {
				FileInputStream in = null;
				FileOutputStream out = null;

				try {
					java.io.File folder = fileService.getSurveyFilesFolder(answerSet.getSurvey().getUniqueId());
					in = new FileInputStream(folder.getPath() + Constants.PATH_DELIMITER + file.getUid());

					int questionId = 0;

					if (answer.getQuestionUniqueId() != null && answer.getQuestionUniqueId().length() > 0
							&& elementsByUniqueId.containsKey(answer.getQuestionUniqueId())) {
						questionId = elementsByUniqueId.get(answer.getQuestionUniqueId()).getId();
					}

					java.io.File basePath = fileService.getSurveyUploadsFolder(answerSet.getSurvey().getUniqueId(), false);
					java.io.File directory = new java.io.File(basePath + Constants.PATH_DELIMITER + answerSet.getUniqueCode() + Constants.PATH_DELIMITER + questionId);
					directory.mkdirs();
					java.io.File fileOut = new java.io.File(directory.getPath() + Constants.PATH_DELIMITER + file.getName());
					out = new FileOutputStream(fileOut);

					IOUtils.copy(in, out);

				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				} finally {
					try {
						in.close();
						out.close();
					} catch (Exception e) {
						// ignore
					}
				}
			}
		}
	}

	private static void checkFiles(java.io.File directory, java.io.File folder, List<File> files) {
		java.io.File[] listFiles = directory.listFiles();
		if (listFiles != null) {
			for (java.io.File file : listFiles) {
				String uid = UUID.randomUUID().toString();

				FileInputStream in = null;
				FileOutputStream out = null;

				try {
					in = new FileInputStream(file);

					out = new FileOutputStream(new java.io.File(folder.getPath() + Constants.PATH_DELIMITER + uid));

					IOUtils.copy(in, out);

					File f = new File();
					f.setUid(uid);
					f.setName(file.getName());

					files.add(f);

				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				} finally {
					try {
						in.close();
						out.close();
					} catch (IOException e) {
						// ignore
					}
				}
			}
		}
	}

	private static Section getSection(Map<String, String[]> parameterMap, Element currentElement, Survey survey,
			String id, ServletContext servletContext, boolean log220) throws InvalidXHTMLException {
		Section section;
		if (currentElement == null) {
			section = new Section(getString(parameterMap, "text", id, servletContext),
					getString(parameterMap, Constants.SHORTNAME, id, servletContext),
					getString(parameterMap, "uid", id, servletContext));
		} else {
			section = (Section) currentElement;
		}

		String oldValues = "";
		String newValues = "";

		String title = getString(parameterMap, "text", id, servletContext);
		if (log220 && !section.getTitle().equals(title)) {
			oldValues += " title: " + section.getTitle();
			newValues += " title: " + title;
		}
		section.setTitle(title);

		String shortname = getString(parameterMap, Constants.SHORTNAME, id, servletContext);
		if (log220 && section.getShortname() != null && !section.getShortname().equals(shortname)) {
			oldValues += " shortname: " + section.getShortname();
			newValues += " shortname: " + shortname;
		}
		section.setShortname(shortname);
		
		boolean useAndLogic = getBoolean(parameterMap, "useAndLogic", id);
		if (log220 && !section.getUseAndLogic().equals(useAndLogic)) {
			oldValues += " useAndLogic: " + section.getUseAndLogic();
			newValues += " useAndLogic: " + useAndLogic;
		}
		section.setUseAndLogic(useAndLogic);

		int level = getInteger(parameterMap, "level", id);
		if (log220 && !section.getLevel().equals(level)) {
			oldValues += " level: " + section.getLevel();
			newValues += " level: " + level;
		}
		section.setLevel(level);
		
		Integer order = getInteger(parameterMap, "order", id);
		if (log220 && !order.equals(section.getOrder())) {
			oldValues += " order: " + section.getOrder();
			newValues += " order: " + order;
		}
		section.setOrder(order);

		String tabtitle = getString(parameterMap, "tabtitle", id, servletContext);
		if (log220 && section.getTabTitle() != null && !section.getTabTitle().equals(tabtitle)) {
			oldValues += " tabtitle: " + section.getTabTitle();
			newValues += " tabtitle: " + tabtitle;
		}
		section.setTabTitle(tabtitle);

		if (log220 && oldValues.length() > 0) {
			String[] oldnew = { oldValues, newValues };
			section.getActivitiesToLog().put(ActivityRegistry.ID_ELEMENT_UPDATED, oldnew);
		}

		return section;
	}

	private static Text getText(Map<String, String[]> parameterMap, Element currentElement, String id,
			ServletContext servletContext, boolean log220) throws InvalidXHTMLException {
		String oldValues = "";
		String newValues = "";

		Text text;
		if (currentElement == null) {
			text = new Text(getString(parameterMap, "text", id, servletContext),
					getString(parameterMap, "uid", id, servletContext));
		} else {

			if (currentElement instanceof EmptyElement) {
				text = new Text(getString(parameterMap, "text", id, servletContext),
						getString(parameterMap, "uid", id, servletContext));
			} else {
				text = (Text) currentElement;
			}
		}

		String title = getString(parameterMap, "text", id, servletContext);
		if (log220 && !text.getTitle().equals(title)) {
			oldValues += " title: " + text.getTitle();
			newValues += " title: " + title;
		}
		text.setTitle(title);

		String shortname = getString(parameterMap, Constants.SHORTNAME, id, servletContext);
		if (log220 && text.getShortname() != null && !text.getShortname().equals(shortname)) {
			oldValues += " shortname: " + text.getShortname();
			newValues += " shortname: " + shortname;
		}
		text.setShortname(shortname);
		
		boolean useAndLogic = getBoolean(parameterMap, "useAndLogic", id);
		if (log220 && !text.getUseAndLogic().equals(useAndLogic)) {
			oldValues += " useAndLogic: " + text.getUseAndLogic();
			newValues += " useAndLogic: " + useAndLogic;
		}
		text.setUseAndLogic(useAndLogic);

		Boolean isOptional = getBoolean(parameterMap, "optional", id);
		if (log220 && !isOptional.equals(text.getOptional())) {
			oldValues += " optional: " + text.getOptional();
			newValues += " optional: " + isOptional;
		}
		text.setOptional(isOptional);

		Boolean isReadonly = getBoolean(parameterMap, "readonly", id);
		if (log220 && !isReadonly.equals(text.getReadonly())) {
			oldValues += " readonly: " + text.getReadonly();
			newValues += " readonly: " + isReadonly;
		}
		text.setReadonly(isReadonly);

		if (log220 && oldValues.length() > 0) {
			String[] oldnew = { oldValues, newValues };
			text.getActivitiesToLog().put(ActivityRegistry.ID_ELEMENT_UPDATED, oldnew);
		}

		return text;
	}

	private static Image getImage(Map<String, String[]> parameterMap, Element currentElement, String id,
			ServletContext servletContext, boolean log220) throws InvalidXHTMLException {
		Image image;
		String oldValues = "";
		String newValues = "";

		if (currentElement == null) {
			image = new Image(getString(parameterMap, "text", id, servletContext),
					getString(parameterMap, "uid", id, servletContext));
		} else {
			image = (Image) currentElement;
		}

		String title = getString(parameterMap, "text", id, servletContext);
		if (log220 && !image.getTitle().equals(title)) {
			oldValues += " title: " + image.getTitle();
			newValues += " title: " + title;
		}
		image.setTitle(title);

		String shortname = getString(parameterMap, Constants.SHORTNAME, id, servletContext);
		if (log220 && image.getShortname() != null && !image.getShortname().equals(shortname)) {
			oldValues += " shortname: " + image.getShortname();
			newValues += " shortname: " + shortname;
		}
		image.setShortname(shortname);
		
		boolean useAndLogic = getBoolean(parameterMap, "useAndLogic", id);
		if (log220 && !image.getUseAndLogic().equals(useAndLogic)) {
			oldValues += " useAndLogic: " + image.getUseAndLogic();
			newValues += " useAndLogic: " + useAndLogic;
		}
		image.setUseAndLogic(useAndLogic);

		Boolean isOptional = getBoolean(parameterMap, "optional", id);
		if (log220 && !isOptional.equals(image.getOptional())) {
			oldValues += " optional: " + image.getOptional();
			newValues += " optional: " + isOptional;
		}
		image.setOptional(isOptional);

		String align = getString(parameterMap, "align", id, servletContext);
		if (log220 && !image.getAlign().equals(align)) {
			oldValues += " align: " + image.getAlign();
			newValues += " align: " + align;
		}
		image.setAlign(align);

		int scale = getInteger(parameterMap, "scale", id);
		if (log220 && !image.getScale().equals(scale)) {
			oldValues += " scale: " + image.getScale();
			newValues += " scale: " + scale;
		}
		image.setScale(scale);

		String longdesc = getString(parameterMap, "longdesc", id, servletContext);
		if (log220 && !Tools.isEqual(image.getLongdesc(), longdesc)) {
			oldValues += " longdesc: " + image.getLongdesc();
			newValues += " longdesc: " + longdesc;
		}
		image.setLongdesc(longdesc);

		int width = getInteger(parameterMap, "width", id);
		if (log220 && !image.getWidth().equals(width)) {
			oldValues += " width: " + image.getWidth();
			newValues += " width: " + width;
		}
		image.setWidth(width);

		String url = getString(parameterMap, "url", id, servletContext);
		if (!url.contains(Constants.PATH_DELIMITER))
			url = servletContext.getContextPath() + "/files/" + url;
		if (log220 && !url.equals(image.getUrl())) {
			oldValues += " url: " + image.getUrl();
			newValues += " url: " + url;
		}
		image.setUrl(url);

		if (log220 && oldValues.length() > 0) {
			String[] oldnew = { oldValues, newValues };
			image.getActivitiesToLog().put(ActivityRegistry.ID_ELEMENT_UPDATED, oldnew);
		}

		return image;
	}

	private static Ruler getRuler(Map<String, String[]> parameterMap, Element currentElement, String id,
			ServletContext servletContext, boolean log220) throws InvalidXHTMLException {
		Ruler ruler;
		String oldValues = "";
		String newValues = "";

		if (currentElement == null) {
			ruler = new Ruler(getString(parameterMap, "text", id, servletContext),
					getString(parameterMap, "uid", id, servletContext));
		} else {
			ruler = (Ruler) currentElement;
		}

		String title = getString(parameterMap, "text", id, servletContext);
		if (log220 && !ruler.getTitle().equals(title)) {
			oldValues += " title: " + ruler.getTitle();
			newValues += " title: " + title;
		}
		ruler.setTitle(title);

		String shortname = getString(parameterMap, Constants.SHORTNAME, id, servletContext);
		if (log220 && ruler.getShortname() != null && !ruler.getShortname().equals(shortname)) {
			oldValues += " shortname: " + ruler.getShortname();
			newValues += " shortname: " + shortname;
		}
		ruler.setShortname(shortname);
		
		boolean useAndLogic = getBoolean(parameterMap, "useAndLogic", id);
		if (log220 && !ruler.getUseAndLogic().equals(useAndLogic)) {
			oldValues += " useAndLogic: " + ruler.getUseAndLogic();
			newValues += " useAndLogic: " + useAndLogic;
		}
		ruler.setUseAndLogic(useAndLogic);

		ruler.setOptional(true);

		String color = getString(parameterMap, "color", id, servletContext);
		if (log220 && !ruler.getColor().equals(color)) {
			oldValues += " color: " + ruler.getColor();
			newValues += " color: " + color;
		}
		ruler.setColor(color);

		int height = getInteger(parameterMap, "height", id);
		if (log220 && !ruler.getHeight().equals(height)) {
			oldValues += " height: " + ruler.getHeight();
			newValues += " height: " + height;
		}
		ruler.setHeight(height);

		String style = getString(parameterMap, "style", id, servletContext);
		if (log220 && !Tools.isEqual(ruler.getStyle(), style)) {
			oldValues += " style: " + ruler.getStyle();
			newValues += " style: " + style;
		}
		ruler.setStyle(style);

		if (log220 && oldValues.length() > 0) {
			String[] oldnew = { oldValues, newValues };
			ruler.getActivitiesToLog().put(ActivityRegistry.ID_ELEMENT_UPDATED, oldnew);
		}

		return ruler;
	}

	private static GalleryQuestion getGallery(Map<String, String[]> parameterMap, Element currentElement,
			String id, FileService fileService, ServletContext servletContext, boolean log220,
			Map<String, Integer> fileIDsByUID) throws InvalidXHTMLException {
		GalleryQuestion gallery;
		String oldValues = "";
		String newValues = "";

		if (currentElement == null) {
			gallery = new GalleryQuestion(getString(parameterMap, "text", id, servletContext),
					getString(parameterMap, Constants.SHORTNAME, id, servletContext),
					getString(parameterMap, "uid", id, servletContext));
		} else {
			gallery = (GalleryQuestion) currentElement;
		}

		String title = getString(parameterMap, "text", id, servletContext);
		if (log220 && !gallery.getTitle().equals(title)) {
			oldValues += " title: " + gallery.getTitle();
			newValues += " title: " + title;
		}
		gallery.setTitle(title);

		String shortname = getString(parameterMap, Constants.SHORTNAME, id, servletContext);
		if (log220 && gallery.getShortname() != null && !gallery.getShortname().equals(shortname)) {
			oldValues += " shortname: " + gallery.getShortname();
			newValues += " shortname: " + shortname;
		}
		gallery.setShortname(shortname);
		
		boolean useAndLogic = getBoolean(parameterMap, "useAndLogic", id);
		if (log220 && !gallery.getUseAndLogic().equals(useAndLogic)) {
			oldValues += " useAndLogic: " + gallery.getUseAndLogic();
			newValues += " useAndLogic: " + useAndLogic;
		}
		gallery.setUseAndLogic(useAndLogic);

		Boolean isOptional = getBoolean(parameterMap, "optional", id);
		if (log220 && !isOptional.equals(gallery.getOptional())) {
			oldValues += " optional: " + gallery.getOptional();
			newValues += " optional: " + isOptional;
		}
		gallery.setOptional(isOptional);

		Boolean isReadonly = getBoolean(parameterMap, "readonly", id);
		if (log220 && !isReadonly.equals(gallery.getReadonly())) {
			oldValues += " readonly: " + gallery.getReadonly();
			newValues += " readonly: " + isReadonly;
		}
		gallery.setReadonly(isReadonly);

		Integer cols = getInteger(parameterMap, "columns", id);
		if (log220 && !cols.equals(gallery.getColumns())) {
			oldValues += " columns: " + gallery.getColumns();
			newValues += " columns: " + cols;
		}
		gallery.setColumns(cols);

		Integer limit = getInteger(parameterMap, "limit", id);
		if (log220 && !limit.equals(gallery.getLimit())) {
			oldValues += " limit: " + gallery.getLimit();
			newValues += " limit: " + limit;
		}
		gallery.setLimit(limit);

		Boolean selectable = getBoolean(parameterMap, "selectable", id);
		if (log220 && !selectable.equals(gallery.getSelection())) {
			oldValues += " selectable: " + gallery.getSelection();
			newValues += " selectable: " + selectable;
		}
		gallery.setSelection(selectable);

		Boolean numbering = getBoolean(parameterMap, "numbering", id);
		if (log220 && !numbering.equals(gallery.getNumbering())) {
			oldValues += " numbering: " + gallery.getNumbering();
			newValues += " numbering: " + numbering;
		}
		gallery.setNumbering(numbering);

		String help = getString(parameterMap, "help", id, servletContext);
		if (log220 && gallery.getHelp() != null && !gallery.getHelp().equals(help)) {
			oldValues += " help: " + gallery.getHelp();
			newValues += " help: " + help;
		}
		gallery.setHelp(help);

		StringBuilder oldFiles = new StringBuilder();
		StringBuilder newFiles = new StringBuilder();

		gallery.getFiles().clear();

		int count = getInteger(parameterMap, "count", id);
		for (int i = 1; i <= count; i++) {
			String uid = getString(parameterMap, "image" + i, id, servletContext);
			String comment = getString(parameterMap, "comment" + i, id, servletContext);
			String longdesc = getString(parameterMap, "longdesc" + i, id, servletContext);
			String desc = getString(parameterMap, "desc" + i, id, servletContext);
			String name = getString(parameterMap, "name" + i, id, servletContext);

			try {
				File file = fileService.get(uid, fileIDsByUID.get(uid));
				file.setName(name);
				file.setComment(comment);
				file.setPosition(i);
				file.setLongdesc(longdesc);
				file.setDescription(desc);
				gallery.getFiles().add(file);
				if (log220) {
					newFiles.append(file.getName()).append("(").append(file.getComment()).append("),");
				}
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}

		if (log220 && !oldFiles.toString().equals(newFiles.toString())) {
			oldValues += " files: " + oldFiles;
			newValues += " files: " + newFiles;
		}

		if (log220 && oldValues.length() > 0) {
			String[] oldnew = { oldValues, newValues };
			gallery.getActivitiesToLog().put(ActivityRegistry.ID_ELEMENT_UPDATED, oldnew);
		}

		return gallery;
	}

	private static Upload getUpload(Map<String, String[]> parameterMap, Element currentElement,
			String id, ServletContext servletContext, boolean log220) throws InvalidXHTMLException {
		Upload upload;
		String oldValues = "";
		String newValues = "";

		if (currentElement == null) {
			upload = new Upload(getString(parameterMap, "text", id, servletContext),
					getString(parameterMap, Constants.SHORTNAME, id, servletContext),
					getString(parameterMap, "uid", id, servletContext));
		} else {
			upload = (Upload) currentElement;
		}

		String title = getString(parameterMap, "text", id, servletContext);
		if (log220 && !upload.getTitle().equals(title)) {
			oldValues += " title: " + upload.getTitle();
			newValues += " title: " + title;
		}
		upload.setTitle(title);

		String shortname = getString(parameterMap, Constants.SHORTNAME, id, servletContext);
		if (log220 && upload.getShortname() != null && !upload.getShortname().equals(shortname)) {
			oldValues += " shortname: " + upload.getShortname();
			newValues += " shortname: " + shortname;
		}
		upload.setShortname(shortname);
		
		boolean useAndLogic = getBoolean(parameterMap, "useAndLogic", id);
		if (log220 && !upload.getUseAndLogic().equals(useAndLogic)) {
			oldValues += " useAndLogic: " + upload.getUseAndLogic();
			newValues += " useAndLogic: " + useAndLogic;
		}
		upload.setUseAndLogic(useAndLogic);

		Boolean isOptional = getBoolean(parameterMap, "optional", id);
		if (log220 && !isOptional.equals(upload.getOptional())) {
			oldValues += " optional: " + upload.getOptional();
			newValues += " optional: " + isOptional;
		}
		upload.setOptional(isOptional);

		String help = getString(parameterMap, "help", id, servletContext);
		if (log220 && upload.getHelp() != null && !upload.getHelp().equals(help)) {
			oldValues += " help: " + upload.getHelp();
			newValues += " help: " + help;
		}
		upload.setHelp(help);

		String extensions = getString(parameterMap, "extensions", id, servletContext);
		if (log220 && upload.getExtensions() != null && !upload.getExtensions().equals(extensions)) {
			oldValues += " extensions: " + upload.getExtensions();
			newValues += " extensions: " + extensions;
		}
		upload.setExtensions(extensions);
		
		Integer maxFileSize = getInteger(parameterMap, "maxFileSize", id);
		if (log220 && upload.getMaxFileSize() != null && !upload.getMaxFileSize().equals(maxFileSize)) {
			oldValues += " maxFileSize: " + upload.getMaxFileSize();
			newValues += " maxFileSize: " + maxFileSize;
		}
		upload.setMaxFileSize(maxFileSize);		
		
		if (log220 && oldValues.length() > 0) {
			String[] oldnew = { oldValues, newValues };
			upload.getActivitiesToLog().put(ActivityRegistry.ID_ELEMENT_UPDATED, oldnew);
		}

		return upload;
	}

	private static Download getDownload(Map<String, String[]> parameterMap, Element currentElement,
			String id, FileService fileService, ServletContext servletContext, boolean log220,
			Map<String, Integer> fileIDsByUID) throws InvalidXHTMLException {
		Download download;
		String oldValues = "";
		String newValues = "";

		if (currentElement == null) {
			download = new Download(getString(parameterMap, "text", id, servletContext),
					getString(parameterMap, Constants.SHORTNAME, id, servletContext),
					getString(parameterMap, "uid", id, servletContext));
		} else {
			download = (Download) currentElement;
		}

		String title = getString(parameterMap, "text", id, servletContext);
		if (log220 && !download.getTitle().equals(title)) {
			oldValues += " title: " + download.getTitle();
			newValues += " title: " + title;
		}
		download.setTitle(title);

		String shortname = getString(parameterMap, Constants.SHORTNAME, id, servletContext);
		if (log220 && download.getShortname() != null && !download.getShortname().equals(shortname)) {
			oldValues += " shortname: " + download.getShortname();
			newValues += " shortname: " + shortname;
		}
		download.setShortname(shortname);
		
		boolean useAndLogic = getBoolean(parameterMap, "useAndLogic", id);
		if (log220 && !download.getUseAndLogic().equals(useAndLogic)) {
			oldValues += " useAndLogic: " + download.getUseAndLogic();
			newValues += " useAndLogic: " + useAndLogic;
		}
		download.setUseAndLogic(useAndLogic);

		Boolean isOptional = getBoolean(parameterMap, "optional", id);
		if (log220 && !isOptional.equals(download.getOptional())) {
			oldValues += " optional: " + download.getOptional();
			newValues += " optional: " + isOptional;
		}
		download.setOptional(isOptional);

		StringBuilder oldFiles = new StringBuilder();
		StringBuilder newFiles = new StringBuilder();
		if (log220) {
			for (File file : download.getFiles()) {
				oldFiles.append(file.getName()).append(",");
			}
		}

		String[] files = getStrings(parameterMap, "files", id, servletContext);

		int counter = 1;

		if (files != null)
			for (String uid : files) {
				try {
					File file = fileService.get(uid, fileIDsByUID.get(uid));
					file.setPosition(counter++);
					download.getFiles().add(file);
					if (log220) {
						newFiles.append(file.getName()).append(",");
					}
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}
			}

		if (log220 && !oldFiles.toString().equals(newFiles.toString())) {
			oldValues += " files: " + oldFiles;
			newValues += " files: " + newFiles;
		}

		String help = getString(parameterMap, "help", id, servletContext);
		if (log220 && download.getHelp() != null && !download.getHelp().equals(help)) {
			oldValues += " help: " + download.getHelp();
			newValues += " help: " + help;
		}
		download.setHelp(help);

		if (log220 && oldValues.length() > 0) {
			String[] oldnew = { oldValues, newValues };
			download.getActivitiesToLog().put(ActivityRegistry.ID_ELEMENT_UPDATED, oldnew);
		}

		return download;
	}

	private static Confirmation getConfirmation(Map<String, String[]> parameterMap, Element currentElement,
			Survey survey, String id, FileService fileService, ServletContext servletContext, boolean log220)
			throws InvalidXHTMLException {
		Confirmation confirmation;
		String oldValues = "";
		String newValues = "";

		if (currentElement == null) {
			confirmation = new Confirmation(getString(parameterMap, "text", id, servletContext),
					getString(parameterMap, Constants.SHORTNAME, id, servletContext),
					getString(parameterMap, "uid", id, servletContext));
		} else {
			confirmation = (Confirmation) currentElement;
		}

		String title = getString(parameterMap, "text", id, servletContext);
		if (log220 && !confirmation.getTitle().equals(title)) {
			oldValues += " title: " + confirmation.getTitle();
			newValues += " title: " + title;
		}
		confirmation.setTitle(title);

		String confirmationtext = getString(parameterMap, "confirmationtext", id, servletContext);
		if (log220 && !confirmation.getConfirmationtext().equals(confirmationtext)) {
			oldValues += " confirmationtext: " + confirmation.getConfirmationtext();
			newValues += " confirmationtext: " + confirmationtext;
		}
		confirmation.setConfirmationtext(confirmationtext);

		String confirmationlabel = getString(parameterMap, "confirmationlabel", id, servletContext);
		if (log220 && !confirmation.getConfirmationlabel().equals(confirmationlabel)) {
			oldValues += " confirmationlabel: " + confirmation.getConfirmationlabel();
			newValues += " confirmationlabel: " + confirmationlabel;
		}
		confirmation.setConfirmationlabel(confirmationlabel);

		String shortname = getString(parameterMap, Constants.SHORTNAME, id, servletContext);
		if (log220 && confirmation.getShortname() != null && !confirmation.getShortname().equals(shortname)) {
			oldValues += " shortname: " + confirmation.getShortname();
			newValues += " shortname: " + shortname;
		}
		confirmation.setShortname(shortname);

		boolean useAndLogic = getBoolean(parameterMap, "useAndLogic", id);
		if (log220 && !confirmation.getUseAndLogic().equals(useAndLogic)) {
			oldValues += " useAndLogic: " + confirmation.getUseAndLogic();
			newValues += " useAndLogic: " + useAndLogic;
		}
		confirmation.setUseAndLogic(useAndLogic);
		
		confirmation.setOptional(false);

		Boolean isUsetext = getBoolean(parameterMap, "usetext", id);
		if (log220 && !isUsetext.equals(confirmation.isUsetext())) {
			oldValues += " isUsetext: " + confirmation.isUsetext();
			newValues += " isUsetext: " + isUsetext;
		}
		confirmation.setUsetext(isUsetext);

		Boolean isUseupload = getBoolean(parameterMap, "useupload", id);
		if (log220 && !isUseupload.equals(confirmation.isUseupload())) {
			oldValues += " isUseupload: " + confirmation.isUseupload();
			newValues += " isUseupload: " + isUseupload;
		}
		confirmation.setUseupload(isUseupload);

		StringBuilder oldFiles = new StringBuilder();
		StringBuilder newFiles = new StringBuilder();
		if (log220) {
			for (File file : confirmation.getFiles()) {
				oldFiles.append(file.getName()).append(",");
			}
		}

		String[] files = getStrings(parameterMap, "files", id, servletContext);

		int counter = 1;

		if (files != null)
			for (String uid : files) {
				try {
					File file = fileService.get(uid);
					file.setPosition(counter++);
					confirmation.getFiles().add(file);
					if (log220) {
						newFiles.append(file.getName()).append(",");
					}
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}
			}

		if (log220 && !oldFiles.toString().equals(newFiles.toString())) {
			oldValues += " files: " + oldFiles;
			newValues += " files: " + newFiles;
		}

		if (log220 && oldValues.length() > 0) {
			String[] oldnew = { oldValues, newValues };
			confirmation.getActivitiesToLog().put(ActivityRegistry.ID_ELEMENT_UPDATED, oldnew);
		}

		return confirmation;
	}

	private static FreeTextQuestion getFreeText(Map<String, String[]> parameterMap, Element currentElement,
			String id, ServletContext servletContext, boolean log220) throws InvalidXHTMLException {
		String oldValues = "";
		String newValues = "";
		FreeTextQuestion freetext;
		if (currentElement == null) {
			freetext = new FreeTextQuestion(getString(parameterMap, "text", id, servletContext),
					getString(parameterMap, Constants.SHORTNAME, id, servletContext),
					getString(parameterMap, "uid", id, servletContext));
		} else {
			freetext = (FreeTextQuestion) currentElement;
		}

		String title = getString(parameterMap, "text", id, servletContext);
		if (log220 && !freetext.getTitle().equals(title)) {
			oldValues += " title: " + freetext.getTitle();
			newValues += " title: " + title;
		}
		freetext.setTitle(title);

		String shortname = getString(parameterMap, Constants.SHORTNAME, id, servletContext);
		if (log220 && freetext.getShortname() != null && !freetext.getShortname().equals(shortname)) {
			oldValues += " shortname: " + freetext.getShortname();
			newValues += " shortname: " + shortname;
		}
		freetext.setShortname(shortname);
		
		Boolean isDelphiQuestion = getBoolean(parameterMap, "delphiquestion", id);
		if (log220 && !isDelphiQuestion.equals(freetext.getIsDelphiQuestion())) {
			oldValues += " isDelphiQuestion: " + freetext.getIsDelphiQuestion();
			newValues += " isDelphiQuestion: " + isDelphiQuestion;
		}
		freetext.setIsDelphiQuestion(isDelphiQuestion);
		
		boolean useAndLogic = getBoolean(parameterMap, "useAndLogic", id);
		if (log220 && !freetext.getUseAndLogic().equals(useAndLogic)) {
			oldValues += " useAndLogic: " + freetext.getUseAndLogic();
			newValues += " useAndLogic: " + useAndLogic;
		}
		freetext.setUseAndLogic(useAndLogic);

		Boolean showExplanationBox = getBoolean(parameterMap, "explanationbox", id);
		if (log220 && !showExplanationBox.equals(freetext.getShowExplanationBox())) {
			oldValues += " showExplanationBox: " + freetext.getShowExplanationBox();
			newValues += " showExplanationBox: " + showExplanationBox;
		}
		freetext.setShowExplanationBox(showExplanationBox);
		
		String delphiChartTypeString = getString(parameterMap, "delphicharttype", id, servletContext);
		DelphiChartType delphiChartType = StringUtils.isNullOrEmpty(delphiChartTypeString) ? DelphiChartType.None : DelphiChartType.valueOf(delphiChartTypeString);
		if (log220 && delphiChartType != freetext.getDelphiChartType()) {
			oldValues += " delphiChartType: " + freetext.getDelphiChartType();
			newValues += " delphiChartType: " + delphiChartType;
		}
		freetext.setDelphiChartType(delphiChartType);

		Boolean isOptional = getBoolean(parameterMap, "optional", id);
		if (isDelphiQuestion && !isOptional) {
			// Enforce optionality of Delphi questions.
			isOptional = true;
		}
		if (log220 && !isOptional.equals(freetext.getOptional())) {
			oldValues += " optional: " + freetext.getOptional();
			newValues += " optional: " + isOptional;
		}
		freetext.setOptional(isOptional);

		Boolean isReadonly = getBoolean(parameterMap, "readonly", id);
		if (log220 && !isReadonly.equals(freetext.getReadonly())) {
			oldValues += " readonly: " + freetext.getReadonly();
			newValues += " readonly: " + isReadonly;
		}
		freetext.setReadonly(isReadonly);

		String help = getString(parameterMap, "help", id, servletContext);
		if (log220 && freetext.getHelp() != null && !freetext.getHelp().equals(help)) {
			oldValues += " help: " + freetext.getHelp();
			newValues += " help: " + help;
		}
		freetext.setHelp(help);

		Integer min = getInteger(parameterMap, "min", id);
		if (log220 && !freetext.getMinCharacters().equals(min)) {
			oldValues += " minCharacters: " + freetext.getMinCharacters();
			newValues += " minCharacters: " + min;
		}
		freetext.setMinCharacters(min);

		Integer max = getInteger(parameterMap, "max", id);
		if (log220 && !freetext.getMaxCharacters().equals(max)) {
			oldValues += " maxCharacters: " + freetext.getMaxCharacters();
			newValues += " maxCharacters: " + max;
		}
		freetext.setMaxCharacters(max);

		Integer rows = getInteger(parameterMap, "rows", id);
		if (log220 && !freetext.getNumRows().equals(rows)) {
			oldValues += " rows: " + freetext.getNumRows();
			newValues += " rows: " + rows;
		}
		freetext.setNumRows(rows);

		Boolean attribute = getBoolean(parameterMap, "attribute", id);
		if (log220 && !attribute.equals(freetext.getIsAttribute())) {
			oldValues += " attribute: " + freetext.getIsAttribute();
			newValues += " attribute: " + attribute;
		}
		freetext.setIsAttribute(attribute);

		String nameattribute = getString(parameterMap, "nameattribute", id, servletContext);
		if (log220 && !freetext.getAttributeName().equals(nameattribute)) {
			oldValues += " attributename: " + freetext.getAttributeName();
			newValues += " attributename: " + nameattribute;
		}
		freetext.setAttributeName(nameattribute);

		Boolean password = getBoolean(parameterMap, "password", id);
		if (log220 && !password.equals(freetext.getIsPassword())) {
			oldValues += " password: " + freetext.getIsPassword();
			newValues += " password: " + password;
		}
		freetext.setIsPassword(password);

		Boolean unique = getBoolean(parameterMap, "unique", id);
		if (log220 && !unique.equals(freetext.getIsUnique())) {
			oldValues += " unique: " + freetext.getIsUnique();
			newValues += " unique: " + unique;
		}
		freetext.setIsUnique(unique);

		Boolean comparable = getBoolean(parameterMap, "comparable", id);
		if (log220 && !comparable.equals(freetext.getIsComparable())) {
			oldValues += " comparable: " + freetext.getIsComparable();
			newValues += " comparable: " + comparable;
		}
		freetext.setIsComparable(comparable);

		Integer scoring = getInteger(parameterMap, "scoring", id);
		if (log220 && !scoring.equals(freetext.getScoring())) {
			oldValues += " scoring: " + freetext.getScoring();
			newValues += " scoring: " + scoring;
		}
		freetext.setScoring(scoring);

		Integer points = getInteger(parameterMap, "points", id, 1);
		if (log220 && !points.equals(freetext.getQuizPoints())) {
			oldValues += " points: " + freetext.getQuizPoints();
			newValues += " points: " + points;
		}
		freetext.setQuizPoints(points);

		if (log220 && oldValues.length() > 0) {
			String[] oldnew = { oldValues, newValues };
			freetext.getActivitiesToLog().put(ActivityRegistry.ID_ELEMENT_UPDATED, oldnew);
		}

		return freetext;
	}

	private static RegExQuestion getRegEx(Map<String, String[]> parameterMap, Element currentElement,
			String id, ServletContext servletContext, boolean log220) throws InvalidXHTMLException {
		String oldValues = "";
		String newValues = "";
		RegExQuestion regex;
		if (currentElement == null) {
			regex = new RegExQuestion(getString(parameterMap, "text", id, servletContext),
					getString(parameterMap, Constants.SHORTNAME, id, servletContext),
					getString(parameterMap, "uid", id, servletContext));
		} else {
			regex = (RegExQuestion) currentElement;
		}

		String title = getString(parameterMap, "text", id, servletContext);
		if (log220 && !regex.getTitle().equals(title)) {
			oldValues += " title: " + regex.getTitle();
			newValues += " title: " + title;
		}
		regex.setTitle(title);

		String shortname = getString(parameterMap, Constants.SHORTNAME, id, servletContext);
		if (log220 && regex.getShortname() != null && !regex.getShortname().equals(shortname)) {
			oldValues += " shortname: " + regex.getShortname();
			newValues += " shortname: " + shortname;
		}
		regex.setShortname(shortname);
		
		Boolean isDelphiQuestion = getBoolean(parameterMap, "delphiquestion", id);
		if (log220 && !isDelphiQuestion.equals(regex.getIsDelphiQuestion())) {
			oldValues += " isDelphiQuestion: " + regex.getIsDelphiQuestion();
			newValues += " isDelphiQuestion: " + isDelphiQuestion;
		}
		regex.setIsDelphiQuestion(isDelphiQuestion);
		
		boolean useAndLogic = getBoolean(parameterMap, "useAndLogic", id);
		if (log220 && !regex.getUseAndLogic().equals(useAndLogic)) {
			oldValues += " useAndLogic: " + regex.getUseAndLogic();
			newValues += " useAndLogic: " + useAndLogic;
		}
		regex.setUseAndLogic(useAndLogic);

		Boolean showExplanationBox = getBoolean(parameterMap, "explanationbox", id);
		if (log220 && !showExplanationBox.equals(regex.getShowExplanationBox())) {
			oldValues += " showExplanationBox: " + regex.getShowExplanationBox();
			newValues += " showExplanationBox: " + showExplanationBox;
		}
		regex.setShowExplanationBox(showExplanationBox);

		Boolean isOptional = getBoolean(parameterMap, "optional", id);
		if (isDelphiQuestion && !isOptional) {
			// Enforce optionality of Delphi questions.
			isOptional = true;
		}
		if (log220 && !isOptional.equals(regex.getOptional())) {
			oldValues += " optional: " + regex.getOptional();
			newValues += " optional: " + isOptional;
		}
		regex.setOptional(isOptional);

		Boolean isReadonly = getBoolean(parameterMap, "readonly", id);
		if (log220 && !isReadonly.equals(regex.getReadonly())) {
			oldValues += " readonly: " + regex.getReadonly();
			newValues += " readonly: " + isReadonly;
		}
		regex.setReadonly(isReadonly);

		String help = getString(parameterMap, "help", id, servletContext);
		if (log220 && regex.getHelp() != null && !regex.getHelp().equals(help)) {
			oldValues += " help: " + regex.getHelp();
			newValues += " help: " + help;
		}
		regex.setHelp(help);

		Integer rows = getInteger(parameterMap, "rows", id);
		if (log220 && !regex.getNumRows().equals(rows)) {
			oldValues += " rows: " + regex.getNumRows();
			newValues += " rows: " + rows;
		}
		regex.setNumRows(rows);

		Boolean attribute = getBoolean(parameterMap, "attribute", id);
		if (log220 && !attribute.equals(regex.getIsAttribute())) {
			oldValues += " attribute: " + regex.getIsAttribute();
			newValues += " attribute: " + attribute;
		}
		regex.setIsAttribute(attribute);

		String nameattribute = getString(parameterMap, "nameattribute", id, servletContext);
		if (log220 && !regex.getAttributeName().equals(nameattribute)) {
			oldValues += " attributename: " + regex.getAttributeName();
			newValues += " attributename: " + nameattribute;
		}
		regex.setAttributeName(nameattribute);

		Boolean password = getBoolean(parameterMap, "password", id);
		if (log220 && !password.equals(regex.getIsPassword())) {
			oldValues += " password: " + regex.getIsPassword();
			newValues += " password: " + password;
		}
		regex.setIsPassword(password);

		Boolean unique = getBoolean(parameterMap, "unique", id);
		if (log220 && !unique.equals(regex.getIsUnique())) {
			oldValues += " unique: " + regex.getIsUnique();
			newValues += " unique: " + unique;
		}
		regex.setIsUnique(unique);

		Boolean comparable = getBoolean(parameterMap, "comparable", id);
		if (log220 && !comparable.equals(regex.getIsComparable())) {
			oldValues += " comparable: " + regex.getIsComparable();
			newValues += " comparable: " + comparable;
		}
		regex.setIsComparable(comparable);

		String sregex = getString(parameterMap, "regex", id, servletContext);
		if (log220 && !regex.getRegex().equals(sregex)) {
			oldValues += " regex: " + regex.getRegex();
			newValues += " regex: " + sregex;
		}
		regex.setRegex(sregex);

		if (log220 && oldValues.length() > 0) {
			String[] oldnew = { oldValues, newValues };
			regex.getActivitiesToLog().put(ActivityRegistry.ID_ELEMENT_UPDATED, oldnew);
		}

		return regex;
	}

	private static EmailQuestion getEmail(Map<String, String[]> parameterMap, Element currentElement,
			String id, ServletContext servletContext, boolean log220) throws InvalidXHTMLException {
		EmailQuestion email;
		String oldValues = "";
		String newValues = "";
		if (currentElement == null) {
			email = new EmailQuestion(getString(parameterMap, "text", id, servletContext),
					getString(parameterMap, Constants.SHORTNAME, id, servletContext),
					getString(parameterMap, "uid", id, servletContext));
		} else {
			email = (EmailQuestion) currentElement;
		}

		String title = getString(parameterMap, "text", id, servletContext);
		if (log220 && !email.getTitle().equals(title)) {
			oldValues += " title: " + email.getTitle();
			newValues += " title: " + title;
		}
		email.setTitle(title);

		String shortname = getString(parameterMap, Constants.SHORTNAME, id, servletContext);
		if (log220 && email.getShortname() != null && !email.getShortname().equals(shortname)) {
			oldValues += " shortname: " + email.getShortname();
			newValues += " shortname: " + shortname;
		}
		email.setShortname(shortname);
		
		boolean useAndLogic = getBoolean(parameterMap, "useAndLogic", id);
		if (log220 && !email.getUseAndLogic().equals(useAndLogic)) {
			oldValues += " useAndLogic: " + email.getUseAndLogic();
			newValues += " useAndLogic: " + useAndLogic;
		}
		email.setUseAndLogic(useAndLogic);

		Boolean isOptional = getBoolean(parameterMap, "optional", id);
		if (log220 && !isOptional.equals(email.getOptional())) {
			oldValues += " optional: " + email.getOptional();
			newValues += " optional: " + isOptional;
		}
		email.setOptional(isOptional);

		Boolean isReadonly = getBoolean(parameterMap, "readonly", id);
		if (log220 && !isReadonly.equals(email.getReadonly())) {
			oldValues += " readonly: " + email.getReadonly();
			newValues += " readonly: " + isReadonly;
		}
		email.setReadonly(isReadonly);

		String help = getString(parameterMap, "help", id, servletContext);
		if (log220 && email.getHelp() != null && !email.getHelp().equals(help)) {
			oldValues += " help: " + email.getHelp();
			newValues += " help: " + help;
		}
		email.setHelp(help);

		Boolean attribute = getBoolean(parameterMap, "attribute", id);
		if (log220 && !attribute.equals(email.getIsAttribute())) {
			oldValues += " attribute: " + email.getIsAttribute();
			newValues += " attribute: " + attribute;
		}
		email.setIsAttribute(attribute);
		
		String nameattribute = getString(parameterMap, "nameattribute", id, servletContext);
		if (log220 && !email.getAttributeName().equals(nameattribute)) {
			oldValues += " attributename: " + email.getAttributeName();
			newValues += " attributename: " + nameattribute;
		}
		email.setAttributeName(nameattribute);

		if (log220 && oldValues.length() > 0) {
			String[] oldnew = { oldValues, newValues };
			email.getActivitiesToLog().put(ActivityRegistry.ID_ELEMENT_UPDATED, oldnew);
		}

		return email;
	}

	private static NumberQuestion getNumber(Map<String, String[]> parameterMap, Element currentElement,
			String id, ServletContext servletContext, boolean log220) throws InvalidXHTMLException {
		NumberQuestion number;
		String oldValues = "";
		String newValues = "";
		if (currentElement == null) {
			number = new NumberQuestion(getString(parameterMap, "text", id, servletContext),
					getString(parameterMap, Constants.SHORTNAME, id, servletContext),
					getString(parameterMap, "uid", id, servletContext));
		} else {
			number = (NumberQuestion) currentElement;
		}

		String title = getString(parameterMap, "text", id, servletContext);
		if (log220 && !number.getTitle().equals(title)) {
			oldValues += " title: " + number.getTitle();
			newValues += " title: " + title;
		}
		number.setTitle(title);

		String shortname = getString(parameterMap, Constants.SHORTNAME, id, servletContext);
		if (log220 && number.getShortname() != null && !number.getShortname().equals(shortname)) {
			oldValues += " shortname: " + number.getShortname();
			newValues += " shortname: " + shortname;
		}
		number.setShortname(shortname);

		Boolean isDelphiQuestion = getBoolean(parameterMap, "delphiquestion", id);
		if (log220 && !isDelphiQuestion.equals(number.getIsDelphiQuestion())) {
			oldValues += " isDelphiQuestion: " + number.getIsDelphiQuestion();
			newValues += " isDelphiQuestion: " + isDelphiQuestion;
		}
		number.setIsDelphiQuestion(isDelphiQuestion);

		boolean useAndLogic = getBoolean(parameterMap, "useAndLogic", id);
		if (log220 && !number.getUseAndLogic().equals(useAndLogic)) {
			oldValues += " useAndLogic: " + number.getUseAndLogic();
			newValues += " useAndLogic: " + useAndLogic;
		}
		number.setUseAndLogic(useAndLogic);
		
		String delphiChartTypeString = getString(parameterMap, "delphicharttype", id, servletContext);
		DelphiChartType delphiChartType = StringUtils.isNullOrEmpty(delphiChartTypeString) ? DelphiChartType.None : DelphiChartType.valueOf(delphiChartTypeString);
		if (log220 && delphiChartType != number.getDelphiChartType()) {
			oldValues += " delphiChartType: " + number.getDelphiChartType();
			newValues += " delphiChartType: " + delphiChartType;
		}
		number.setDelphiChartType(delphiChartType);
		
		Boolean showExplanationBox = getBoolean(parameterMap, "explanationbox", id);
		if (log220 && !showExplanationBox.equals(number.getShowExplanationBox())) {
			oldValues += " showExplanationBox: " + number.getShowExplanationBox();
			newValues += " showExplanationBox: " + showExplanationBox;
		}
		number.setShowExplanationBox(showExplanationBox);

		Boolean isOptional = getBoolean(parameterMap, "optional", id);
		if (isDelphiQuestion && !isOptional) {
			// Enforce optionality of Delphi questions.
			isOptional = true;
		}
		if (log220 && !isOptional.equals(number.getOptional())) {
			oldValues += " optional: " + number.getOptional();
			newValues += " optional: " + isOptional;
		}
		number.setOptional(isOptional);

		Boolean isReadonly = getBoolean(parameterMap, "readonly", id);
		if (log220 && !isReadonly.equals(number.getReadonly())) {
			oldValues += " readonly: " + number.getReadonly();
			newValues += " readonly: " + isReadonly;
		}
		number.setReadonly(isReadonly);

		String help = getString(parameterMap, "help", id, servletContext);
		if (log220 && number.getHelp() != null && !number.getHelp().equals(help)) {
			oldValues += " help: " + number.getHelp();
			newValues += " help: " + help;
		}
		number.setHelp(help);

		Boolean attribute = getBoolean(parameterMap, "attribute", id);
		if (log220 && !attribute.equals(number.getIsAttribute())) {
			oldValues += " attribute: " + number.getIsAttribute();
			newValues += " attribute: " + attribute;
		}
		number.setIsAttribute(attribute);

		String nameattribute = getString(parameterMap, "nameattribute", id, servletContext);
		if (log220 && !number.getAttributeName().equals(nameattribute)) {
			oldValues += " attributename: " + number.getAttributeName();
			newValues += " attributename: " + nameattribute;
		}
		number.setAttributeName(nameattribute);

		String unit = getString(parameterMap, "unit", id, servletContext);
		if (log220 && !number.getUnit().equals(unit)) {
			oldValues += " unit: " + number.getUnit();
			newValues += " unit: " + unit;
		}
		number.setUnit(unit);

		Boolean unique = getBoolean(parameterMap, "unique", id);
		if (log220 && !unique.equals(number.getIsUnique())) {
			oldValues += " unique: " + number.getIsUnique();
			newValues += " unique: " + unique;
		}
		number.setIsUnique(unique);

		Double min = getDouble(parameterMap, "min", id);
		if (log220 && number.getMin() != null && !number.getMin().equals(min)) {
			oldValues += " min: " + number.getMin();
			newValues += " min: " + min;
		}
		number.setMin(min);

		Double max = getDouble(parameterMap, "max", id);
		if (log220 && number.getMax() != null && !number.getMax().equals(max)) {
			oldValues += " max: " + number.getMax();
			newValues += " max: " + max;
		}
		number.setMax(max);

		Integer decimalplaces = getInteger(parameterMap, "decimalplaces", id);
		if (log220 && !number.getDecimalPlaces().equals(decimalplaces)) {
			oldValues += " decimalplaces: " + number.getDecimalPlaces();
			newValues += " decimalplaces: " + decimalplaces;
		}
		number.setDecimalPlaces(decimalplaces);

		Integer scoring = getInteger(parameterMap, "scoring", id);
		if (log220 && !scoring.equals(number.getScoring())) {
			oldValues += " scoring: " + number.getScoring();
			newValues += " scoring: " + scoring;
		}
		number.setScoring(scoring);

		Integer points = getInteger(parameterMap, "points", id, 1);
		if (log220 && !points.equals(number.getQuizPoints())) {
			oldValues += " points: " + number.getQuizPoints();
			newValues += " points: " + points;
		}
		number.setQuizPoints(points);
		
		String display = getString(parameterMap, "display", id, servletContext);
		if (log220 && !number.getDisplay().equals(display)) {
			oldValues += " display: " + number.getDisplay();
			newValues += " display: " + display;
		}
		number.setDisplay(display);
		
		String minLabel = getString(parameterMap, "minLabel", id, servletContext);
		if (log220 && !number.getMinLabel().equals(minLabel)) {
			oldValues += " minLabel: " + number.getMinLabel();
			newValues += " minLabel: " + minLabel;
		}
		number.setMinLabel(minLabel);
		
		String maxLabel = getString(parameterMap, "maxLabel", id, servletContext);
		if (log220 && !number.getMaxLabel().equals(maxLabel)) {
			oldValues += " maxLabel: " + number.getMaxLabel();
			newValues += " maxLabel: " + maxLabel;
		}
		number.setMaxLabel(maxLabel);
		
		String initialSliderPosition = getString(parameterMap, "initialSliderPosition", id, servletContext);
		if (log220 && !number.getInitialSliderPosition().equals(initialSliderPosition)) {
			oldValues += " initialSliderPosition: " + number.getInitialSliderPosition();
			newValues += " initialSliderPosition: " + initialSliderPosition;
		}
		number.setInitialSliderPosition(initialSliderPosition);

		Boolean displayGraduationScale = getBoolean(parameterMap, "displayGraduationScale", id);
		if (log220 && !attribute.equals(number.getDisplayGraduationScale())) {
			oldValues += " displayGraduationScale: " + number.getDisplayGraduationScale();
			newValues += " displayGraduationScale: " + displayGraduationScale;
		}
		number.setDisplayGraduationScale(displayGraduationScale);
		
		Double maxDistance = getDouble(parameterMap, "maxDistance", id);
		if (log220 && !maxDistance.equals(number.getMaxDistance())) {
			oldValues += " maxDistance: " + number.getMaxDistance();
			newValues += " maxDistance: " + maxDistance;
		}
		number.setMaxDistance(maxDistance);
		
		if (log220 && oldValues.length() > 0) {
			String[] oldnew = { oldValues, newValues };
			number.getActivitiesToLog().put(ActivityRegistry.ID_ELEMENT_UPDATED, oldnew);
		}

		return number;
	}

	private static FormulaQuestion getFormula(Map<String, String[]> parameterMap, Element currentElement,
			String id, ServletContext servletContext, boolean log220) throws InvalidXHTMLException {
		FormulaQuestion formula;
		String oldValues = "";
		String newValues = "";
		if (currentElement == null) {
			formula = new FormulaQuestion(getString(parameterMap, "text", id, servletContext),
					getString(parameterMap, Constants.SHORTNAME, id, servletContext),
					getString(parameterMap, "uid", id, servletContext));
		} else {
			formula = (FormulaQuestion) currentElement;
		}
		formula.setOptional(true);

		String title = getString(parameterMap, "text", id, servletContext);
		if (log220 && !formula.getTitle().equals(title)) {
			oldValues += " title: " + formula.getTitle();
			newValues += " title: " + title;
		}
		formula.setTitle(title);

		String shortname = getString(parameterMap, Constants.SHORTNAME, id, servletContext);
		if (log220 && formula.getShortname() != null && !formula.getShortname().equals(shortname)) {
			oldValues += " shortname: " + formula.getShortname();
			newValues += " shortname: " + shortname;
		}
		formula.setShortname(shortname);
		
		boolean useAndLogic = getBoolean(parameterMap, "useAndLogic", id);
		if (log220 && !formula.getUseAndLogic().equals(useAndLogic)) {
			oldValues += " useAndLogic: " + formula.getUseAndLogic();
			newValues += " useAndLogic: " + useAndLogic;
		}
		formula.setUseAndLogic(useAndLogic);
		
		Boolean isReadonly = getBoolean(parameterMap, "readonly", id);
		if (log220 && !isReadonly.equals(formula.getReadonly())) {
			oldValues += " readonly: " + formula.getReadonly();
			newValues += " readonly: " + isReadonly;
		}
		formula.setReadonly(isReadonly);

		String help = getString(parameterMap, "help", id, servletContext);
		if (log220 && formula.getHelp() != null && !formula.getHelp().equals(help)) {
			oldValues += " help: " + formula.getHelp();
			newValues += " help: " + help;
		}
		formula.setHelp(help);

		Boolean attribute = getBoolean(parameterMap, "attribute", id);
		if (log220 && !attribute.equals(formula.getIsAttribute())) {
			oldValues += " attribute: " + formula.getIsAttribute();
			newValues += " attribute: " + attribute;
		}
		formula.setIsAttribute(attribute);

		String nameattribute = getString(parameterMap, "nameattribute", id, servletContext);
		if (log220 && !formula.getAttributeName().equals(nameattribute)) {
			oldValues += " attributename: " + formula.getAttributeName();
			newValues += " attributename: " + nameattribute;
		}
		formula.setAttributeName(nameattribute);

		String sformula = getString(parameterMap, "formula", id, servletContext);
		if (log220 && !formula.getFormula().equals(sformula)) {
			oldValues += " formula: " + formula.getFormula();
			newValues += " formula: " + sformula;
		}
		formula.setFormula(sformula);

		Double min = getDouble(parameterMap, "min", id);
		if (log220 && formula.getMin() != null && !formula.getMin().equals(min)) {
			oldValues += " min: " + formula.getMin();
			newValues += " min: " + min;
		}
		formula.setMin(min);

		Double max = getDouble(parameterMap, "max", id);
		if (log220 && formula.getMax() != null && !formula.getMax().equals(max)) {
			oldValues += " max: " + formula.getMax();
			newValues += " max: " + max;
		}
		formula.setMax(max);

		Integer decimalplaces = getInteger(parameterMap, "decimalplaces", id);
		if (log220 && !formula.getDecimalPlaces().equals(decimalplaces)) {
			oldValues += " decimalplaces: " + formula.getDecimalPlaces();
			newValues += " decimalplaces: " + decimalplaces;
		}
		formula.setDecimalPlaces(decimalplaces);

		return formula;
	}
	
	private static DateQuestion getDate(Map<String, String[]> parameterMap, Element currentElement,
			String id, ServletContext servletContext, boolean log220) throws InvalidXHTMLException {
		DateQuestion date;
		String oldValues = "";
		String newValues = "";
		if (currentElement == null) {
			date = new DateQuestion(getString(parameterMap, "text", id, servletContext),
					getString(parameterMap, Constants.SHORTNAME, id, servletContext),
					getString(parameterMap, "uid", id, servletContext));
		} else {
			date = (DateQuestion) currentElement;
		}

		String title = getString(parameterMap, "text", id, servletContext);
		if (log220 && !date.getTitle().equals(title)) {
			oldValues += " title: " + date.getTitle();
			newValues += " title: " + title;
		}
		date.setTitle(title);

		String shortname = getString(parameterMap, Constants.SHORTNAME, id, servletContext);
		if (log220 && date.getShortname() != null && !date.getShortname().equals(shortname)) {
			oldValues += " shortname: " + date.getShortname();
			newValues += " shortname: " + shortname;
		}
		date.setShortname(shortname);
		
		Boolean isDelphiQuestion = getBoolean(parameterMap, "delphiquestion", id);
		if (log220 && !isDelphiQuestion.equals(date.getIsDelphiQuestion())) {
			oldValues += " isDelphiQuestion: " + date.getIsDelphiQuestion();
			newValues += " isDelphiQuestion: " + isDelphiQuestion;
		}
		date.setIsDelphiQuestion(isDelphiQuestion);

		boolean useAndLogic = getBoolean(parameterMap, "useAndLogic", id);
		if (log220 && !date.getUseAndLogic().equals(useAndLogic)) {
			oldValues += " useAndLogic: " + date.getUseAndLogic();
			newValues += " useAndLogic: " + useAndLogic;
		}
		date.setUseAndLogic(useAndLogic);

		Boolean showExplanationBox = getBoolean(parameterMap, "explanationbox", id);
		if (log220 && !showExplanationBox.equals(date.getShowExplanationBox())) {
			oldValues += " showExplanationBox: " + date.getShowExplanationBox();
			newValues += " showExplanationBox: " + showExplanationBox;
		}
		date.setShowExplanationBox(showExplanationBox);

		Boolean isOptional = getBoolean(parameterMap, "optional", id);
		if (isDelphiQuestion && !isOptional) {
			// Enforce optionality of Delphi questions.
			isOptional = true;
		}
		if (log220 && !isOptional.equals(date.getOptional())) {
			oldValues += " optional: " + date.getOptional();
			newValues += " optional: " + isOptional;
		}
		date.setOptional(isOptional);

		Boolean isReadonly = getBoolean(parameterMap, "readonly", id);
		if (log220 && !isReadonly.equals(date.getReadonly())) {
			oldValues += " readonly: " + date.getReadonly();
			newValues += " readonly: " + isReadonly;
		}
		date.setReadonly(isReadonly);

		String help = getString(parameterMap, "help", id, servletContext);
		if (log220 && date.getHelp() != null && !date.getHelp().equals(help)) {
			oldValues += " help: " + date.getHelp();
			newValues += " help: " + help;
		}
		date.setHelp(help);

		Boolean attribute = getBoolean(parameterMap, "attribute", id);
		if (log220 && !attribute.equals(date.getIsAttribute())) {
			oldValues += " attribute: " + date.getIsAttribute();
			newValues += " attribute: " + attribute;
		}
		date.setIsAttribute(attribute);

		String nameattribute = getString(parameterMap, "nameattribute", id, servletContext);
		if (log220 && !date.getAttributeName().equals(nameattribute)) {
			oldValues += " attributename: " + date.getAttributeName();
			newValues += " attributename: " + nameattribute;
		}
		date.setAttributeName(nameattribute);

		Date min = getDate(parameterMap, "min", id);
		if (log220 && min != null && !min.equals(date.getMin())) {
			oldValues += " min: " + date.getMin();
			newValues += " min: " + min;
		}
		date.setMin(min);

		Date max = getDate(parameterMap, "max", id);
		if (log220 && max != null && !max.equals(date.getMax())) {
			oldValues += " max: " + date.getMax();
			newValues += " max: " + max;
		}
		date.setMax(max);

		Integer scoring = getInteger(parameterMap, "scoring", id);
		if (log220 && !scoring.equals(date.getScoring())) {
			oldValues += " scoring: " + date.getScoring();
			newValues += " scoring: " + scoring;
		}
		date.setScoring(scoring);

		Integer points = getInteger(parameterMap, "points", id, 1);
		if (log220 && !points.equals(date.getQuizPoints())) {
			oldValues += " points: " + date.getQuizPoints();
			newValues += " points: " + points;
		}
		date.setQuizPoints(points);

		if (log220 && oldValues.length() > 0) {
			String[] oldnew = { oldValues, newValues };
			date.getActivitiesToLog().put(ActivityRegistry.ID_ELEMENT_UPDATED, oldnew);
		}

		return date;
	}

	private static TimeQuestion getTime(Map<String, String[]> parameterMap, Element currentElement,
			String id, ServletContext servletContext, boolean log220) throws InvalidXHTMLException {
		TimeQuestion time;
		String oldValues = "";
		String newValues = "";
		if (currentElement == null) {
			time = new TimeQuestion(getString(parameterMap, "text", id, servletContext),
					getString(parameterMap, Constants.SHORTNAME, id, servletContext),
					getString(parameterMap, "uid", id, servletContext));
		} else {
			time = (TimeQuestion) currentElement;
		}

		String title = getString(parameterMap, "text", id, servletContext);
		if (log220 && !time.getTitle().equals(title)) {
			oldValues += " title: " + time.getTitle();
			newValues += " title: " + title;
		}
		time.setTitle(title);

		String shortname = getString(parameterMap, Constants.SHORTNAME, id, servletContext);
		if (log220 && time.getShortname() != null && !time.getShortname().equals(shortname)) {
			oldValues += " shortname: " + time.getShortname();
			newValues += " shortname: " + shortname;
		}
		time.setShortname(shortname);
		
		Boolean isDelphiQuestion = getBoolean(parameterMap, "delphiquestion", id);
		if (log220 && !isDelphiQuestion.equals(time.getIsDelphiQuestion())) {
			oldValues += " isDelphiQuestion: " + time.getIsDelphiQuestion();
			newValues += " isDelphiQuestion: " + isDelphiQuestion;
		}
		time.setIsDelphiQuestion(isDelphiQuestion);
		boolean useAndLogic = getBoolean(parameterMap, "useAndLogic", id);
		if (log220 && !time.getUseAndLogic().equals(useAndLogic)) {
			oldValues += " useAndLogic: " + time.getUseAndLogic();
			newValues += " useAndLogic: " + useAndLogic;
		}		
		time.setUseAndLogic(useAndLogic);

		Boolean showExplanationBox = getBoolean(parameterMap, "explanationbox", id);
		if (log220 && !showExplanationBox.equals(time.getShowExplanationBox())) {
			oldValues += " showExplanationBox: " + time.getShowExplanationBox();
			newValues += " showExplanationBox: " + showExplanationBox;
		}
		time.setShowExplanationBox(showExplanationBox);

		Boolean isOptional = getBoolean(parameterMap, "optional", id);
		if (isDelphiQuestion && !isOptional) {
			// Enforce optionality of Delphi questions.
			isOptional = true;
		}
		if (log220 && !isOptional.equals(time.getOptional())) {
			oldValues += " optional: " + time.getOptional();
			newValues += " optional: " + isOptional;
		}
		time.setOptional(isOptional);

		Boolean isReadonly = getBoolean(parameterMap, "readonly", id);
		if (log220 && !isReadonly.equals(time.getReadonly())) {
			oldValues += " readonly: " + time.getReadonly();
			newValues += " readonly: " + isReadonly;
		}
		time.setReadonly(isReadonly);

		String help = getString(parameterMap, "help", id, servletContext);
		if (log220 && time.getHelp() != null && !time.getHelp().equals(help)) {
			oldValues += " help: " + time.getHelp();
			newValues += " help: " + help;
		}
		time.setHelp(help);

		Boolean attribute = getBoolean(parameterMap, "attribute", id);
		if (log220 && !attribute.equals(time.getIsAttribute())) {
			oldValues += " attribute: " + time.getIsAttribute();
			newValues += " attribute: " + attribute;
		}
		time.setIsAttribute(attribute);

		String nameattribute = getString(parameterMap, "nameattribute", id, servletContext);
		if (log220 && !time.getAttributeName().equals(nameattribute)) {
			oldValues += " attributename: " + time.getAttributeName();
			newValues += " attributename: " + nameattribute;
		}
		time.setAttributeName(nameattribute);

		String min = getString(parameterMap, "min", id, servletContext);
		if (log220 && min != null && !min.equals(time.getMin())) {
			oldValues += " min: " + time.getMin();
			newValues += " min: " + min;
		}
		time.setMin(min);

		String max = getString(parameterMap, "max", id, servletContext);
		if (log220 && max != null && !max.equals(time.getMax())) {
			oldValues += " max: " + time.getMax();
			newValues += " max: " + max;
		}
		time.setMax(max);

		if (log220 && oldValues.length() > 0) {
			String[] oldnew = { oldValues, newValues };
			time.getActivitiesToLog().put(ActivityRegistry.ID_ELEMENT_UPDATED, oldnew);
		}

		return time;
	}

	private static RatingQuestion getRating(Map<String, String[]> parameterMap, Element currentElement, String id,
			ServletContext servletContext, boolean log220, String[] questions, String[] shortnamesForQuestions,
			String[] uidsForQuestions, String[] optionalsForQuestions) throws InvalidXHTMLException {
		RatingQuestion rating;
		String oldValues = "";
		String newValues = "";
		StringBuilder oldQuestions = new StringBuilder();
		StringBuilder newQuestions = new StringBuilder();

		if (currentElement == null) {
			rating = new RatingQuestion(getString(parameterMap, "text", id, servletContext),
					getString(parameterMap, Constants.SHORTNAME, id, servletContext),
					getString(parameterMap, "uid", id, servletContext));
		} else {
			rating = (RatingQuestion) currentElement;

			if (log220) {
				for (Element child : rating.getChildElements()) {
					oldQuestions.append(child.getTitle()).append("(").append(child.getShortname()).append("),");
				}
			}

			List<Element> toDelete = new ArrayList<>();
			List<String> palist = new ArrayList<>();
			for (Element pa : rating.getChildElements()) {
				boolean found = false;
				for (String question : questions) {
					if (pa.getTitle().equals(question)) {
						found = true;
						break;
					}
				}
				if (!found || palist.contains(pa.getTitle())) {
					toDelete.add(pa);
				} else {
					palist.add(pa.getTitle());
				}
			}

			for (Element pa : toDelete) {
				rating.getChildElements().remove(pa);
			}
		}

		String title = getString(parameterMap, "text", id, servletContext);
		if (log220 && !rating.getTitle().equals(title)) {
			oldValues += " title: " + rating.getTitle();
			newValues += " title: " + title;
		}
		rating.setTitle(title);

		String shortname = getString(parameterMap, Constants.SHORTNAME, id, servletContext);
		if (log220 && rating.getShortname() != null && !rating.getShortname().equals(shortname)) {
			oldValues += " shortname: " + rating.getShortname();
			newValues += " shortname: " + shortname;
		}
		rating.setShortname(shortname);
		
		Boolean isDelphiQuestion = getBoolean(parameterMap, "delphiquestion", id);
		if (log220 && !isDelphiQuestion.equals(rating.getIsDelphiQuestion())) {
			oldValues += " isDelphiQuestion: " + rating.getIsDelphiQuestion();
			newValues += " isDelphiQuestion: " + isDelphiQuestion;
		}
		rating.setIsDelphiQuestion(isDelphiQuestion);

		boolean useAndLogic = getBoolean(parameterMap, "useAndLogic", id);
		if (log220 && !rating.getUseAndLogic().equals(useAndLogic)) {
			oldValues += " useAndLogic: " + rating.getUseAndLogic();
			newValues += " useAndLogic: " + useAndLogic;
		}	
		rating.setUseAndLogic(useAndLogic);

		Boolean showExplanationBox = getBoolean(parameterMap, "explanationbox", id);
		if (log220 && !showExplanationBox.equals(rating.getShowExplanationBox())) {
			oldValues += " showExplanationBox: " + rating.getShowExplanationBox();
			newValues += " showExplanationBox: " + showExplanationBox;
		}
		rating.setShowExplanationBox(showExplanationBox);

		Boolean isOptional = getBoolean(parameterMap, "optional", id);
		if (isDelphiQuestion && !isOptional) {
			// Enforce optionality of Delphi questions.
			isOptional = true;
		}
		if (log220 && !isOptional.equals(rating.getOptional())) {
			oldValues += " optional: " + rating.getOptional();
			newValues += " optional: " + isOptional;
		}
		rating.setOptional(isOptional);

		String help = getString(parameterMap, "help", id, servletContext);
		if (log220 && rating.getHelp() != null && !rating.getHelp().equals(help)) {
			oldValues += " help: " + rating.getHelp();
			newValues += " help: " + help;
		}
		rating.setHelp(help);

		Integer numIcons = getInteger(parameterMap, "numIcons", id);
		if (log220 && !numIcons.equals(rating.getNumIcons())) {
			oldValues += " numIcons: " + rating.getNumIcons();
			newValues += " numIcons: " + numIcons;
		}
		rating.setNumIcons(numIcons);

		Integer iconType = getInteger(parameterMap, "iconType", id);
		if (log220 && !iconType.equals(rating.getIconType())) {
			oldValues += " iconType: " + rating.getIconType();
			newValues += " iconType: " + iconType;
		}
		rating.setIconType(iconType);

		String delphiChartTypeString = getString(parameterMap, "delphicharttype", id, servletContext);
		DelphiChartType delphiChartType = StringUtils.isNullOrEmpty(delphiChartTypeString) ? rating.getDefaultDelphiChartType() : DelphiChartType.valueOf(delphiChartTypeString);
		if (log220 && delphiChartType != rating.getDelphiChartType()) {
			oldValues += " delphiChartType: " + rating.getDelphiChartType();
			newValues += " delphiChartType: " + delphiChartType;
		}
		rating.setDelphiChartType(delphiChartType);
		
		Element q;
		int j = 0;
		for (int k = 0; k < questions.length; k++) {

			String question = questions[k];
			shortname = "";
			if (shortnamesForQuestions != null && shortnamesForQuestions.length > k)
				shortname = shortnamesForQuestions[k];

			q = null;
			boolean found = false;

			for (Element pa : rating.getChildElements()) {
				if (pa.getTitle().equals(question)) {
					q = pa;
					found = true;
					break;
				}
			}

			if (q == null) {
				q = new Text();
				q.setUniqueId(UUID.randomUUID().toString());
			}

			q.setTitle(question);
			q.setShortname(shortname);
			q.setPosition(j);

			isOptional = true;
			if (optionalsForQuestions != null && optionalsForQuestions.length > k)
				isOptional = isDelphiQuestion || !optionalsForQuestions[k].equalsIgnoreCase("false");

			((Question) q).setOptional(isOptional);

			j++;

			if (log220) {
				newQuestions.append(q.getTitle()).append("(").append(q.getShortname()).append("),");
			}

			if (!found)
				rating.getChildElements().add(q);
		}

		if (log220 && !oldQuestions.toString().equals(newQuestions.toString())) {
			oldValues += " answers: " + oldQuestions;
			newValues += " answers: " + newQuestions;
		}

		if (log220 && oldValues.length() > 0) {
			String[] oldnew = { oldValues, newValues };
			rating.getActivitiesToLog().put(ActivityRegistry.ID_ELEMENT_UPDATED, oldnew);
		}

		return rating;
	}
	
	private static ComplexTable getComplexTable(Map<String, String[]> parameterMap, Element currentElement, 
			Survey survey, String id, ServletContext servletContext, boolean log220, Map<Integer, Element> elementsById) throws InvalidXHTMLException {
		String oldValues = "";
		String newValues = "";
		ComplexTable table;
		
		if (currentElement == null) {
			table = new ComplexTable(getString(parameterMap, "text", id, servletContext),
					getString(parameterMap, Constants.SHORTNAME, id, servletContext),
					getString(parameterMap, "uid", id, servletContext));
		} else {
			table = (ComplexTable) currentElement;
		}

		String title = getString(parameterMap, "text", id, servletContext);
		if (log220 && table.getTitle() != null && !table.getTitle().equals(title)) {
			oldValues += " title: " + table.getTitle();
			newValues += " title: " + title;
		}
		table.setTitle(title);

		String shortname = getString(parameterMap, Constants.SHORTNAME, id, servletContext);
		if (log220 && table.getShortname() != null && !table.getShortname().equals(shortname)) {
			oldValues += " shortname: " + table.getShortname();
			newValues += " shortname: " + shortname;
		}
		table.setShortname(shortname);

		int rows = getInteger(parameterMap, "rows", id);
		if (log220 && table.getRows() != rows) {
			oldValues += " rows: " + table.getRows();
			newValues += " rows: " + rows;
		}
		table.setRows(rows);

		boolean useAndLogic = getBoolean(parameterMap, "useAndLogic", id);
		if (log220 && table.getUseAndLogic() != null && !table.getUseAndLogic().equals(useAndLogic)) {
			oldValues += " useAndLogic: " + table.getUseAndLogic();
			newValues += " useAndLogic: " + useAndLogic;
		}
		table.setUseAndLogic(useAndLogic);

		int columns = getInteger(parameterMap, "columns", id);
		if (log220 && table.getColumns() != columns) {
			oldValues += " columns: " + table.getColumns();
			newValues += " columns: " + columns;
		}
		table.setColumns(columns);

		boolean showHeadersAndBorders = getBoolean(parameterMap, "showHeadersAndBorders", id);
		if (log220 && table.isShowHeadersAndBorders() != showHeadersAndBorders) {
			oldValues += " showHeadersAndBorders: " + table.isShowHeadersAndBorders();
			newValues += " showHeadersAndBorders: " + showHeadersAndBorders;
		}
		table.setShowHeadersAndBorders(showHeadersAndBorders);

		ComplexTable.SizeType size = ComplexTable.SizeType.values()[getInteger(parameterMap, "size", id)];
		if (log220 && table.getSize() != null && !table.getSize().equals(size)) {
			oldValues += " size: " + table.getSize();
			newValues += " size: " + size;
		}
		table.setSize(size);

		boolean optional = getBoolean(parameterMap, "optional", id);
		if (log220 && table.getOptional() != null && !table.getOptional().equals(optional)) {
			oldValues += " optional: " + table.getOptional();
			newValues += " optional: " + optional;
		}
		table.setOptional(optional);

		String help = getString(parameterMap, "help", id, servletContext);
		if (log220 && table.getHelp() != null && !table.getHelp().equals(help)) {
			oldValues += " help: " + table.getHelp();
			newValues += " help: " + help;
		}
		table.setHelp(help);
		
		String childIdsAsString = parameterMap.get("childelements" + id)[0];
		String[] childIds = childIdsAsString.split(";");

		ArrayList<ComplexTableItem> newChildren = new ArrayList<>();

		for (String childid : childIds) {
			Element currentChild = null;
			if (!childid.contains("-") && elementsById.containsKey(Integer.parseInt(childid))) {
				currentChild = elementsById.get(Integer.parseInt(childid));
			}

			ComplexTableItem child = getComplexTableItem(childid, currentChild, parameterMap, servletContext, log220, survey);
			if (child.getCellType() != CellType.Empty) {
				newChildren.add(child);
			}
		}

		table.setChildElements(newChildren);

		if (log220 && oldValues.length() > 0) {
			String[] oldnew = { oldValues, newValues };
			table.getActivitiesToLog().put(ActivityRegistry.ID_ELEMENT_UPDATED, oldnew);
		}
		
		return table;
	}
	
	private static ComplexTableItem getComplexTableItem(String id, Element currentElement, Map<String, String[]> parameterMap, ServletContext servletContext, boolean log220, Survey survey) throws InvalidXHTMLException {
		String oldValues = "";
		String newValues = "";
		ComplexTableItem item;
		boolean newElement = false;

		if (currentElement == null) {
			item = new ComplexTableItem(getString(parameterMap, "text", id, servletContext),
					getString(parameterMap, "originalTitle", id, servletContext),
					getString(parameterMap, Constants.SHORTNAME, id, servletContext),
					getString(parameterMap, "uid", id, servletContext));
			newElement = true;
		} else {
			item = (ComplexTableItem) currentElement;
		}

		String title = getString(parameterMap, "text", id, servletContext);
		if (log220 && item.getTitle() != null && !item.getTitle().equals(title)) {
			oldValues += " title: " + item.getTitle();
			newValues += " title: " + title;
		}
		item.setTitle(title);

		String shortname = getString(parameterMap, Constants.SHORTNAME, id, servletContext);
		if (log220 && item.getShortname() != null && !item.getShortname().equals(shortname)) {
			oldValues += " shortname: " + item.getShortname();
			newValues += " shortname: " + shortname;
		}
		item.setShortname(shortname);

		boolean optional = getBoolean(parameterMap, "optional", id);
		if (log220 && item.getOptional() != null && !item.getOptional().equals(optional)) {
			oldValues += " optional: " + item.getOptional();
			newValues += " optional: " + optional;
		}
		item.setOptional(optional);

		ComplexTableItem.CellType cellType = ComplexTableItem.CellType.values()[getInteger(parameterMap, "cellType", id)];
		if (log220 && item.getCellType() != null && !item.getCellType().equals(cellType)) {
			oldValues += " cellType: " + item.getCellType();
			newValues += " cellType: " + cellType;
		}
		item.setCellType(cellType);

		int row = getInteger(parameterMap, "row", id);
		if (log220 && item.getRow() != row) {
			oldValues += " row: " + item.getRow();
			newValues += " row: " + row;
		}
		item.setRow(row);

		int column = getInteger(parameterMap, "column", id);
		if (log220 && item.getColumn() != column) {
			oldValues += " column: " + item.getColumn();
			newValues += " column: " + column;
		}
		item.setColumn(column);

		int columnSpan = getInteger(parameterMap, "columnSpan", id);
		if (log220 && item.getColumn() != columnSpan) {
			oldValues += " columnSpan: " + item.getColumnSpan();
			newValues += " columnSpan: " + columnSpan;
		}
		item.setColumnSpan(columnSpan);

		String help = getString(parameterMap, "help", id, servletContext);
		if (log220 && item.getHelp() != null && !item.getHelp().equals(help)) {
			oldValues += " help: " + item.getHelp();
			newValues += " help: " + help;
		}
		item.setHelp(help);

		int minCharacters = getInteger(parameterMap, "minCharacters", id);
		if (log220 && item.getMinCharacters() != null && !item.getMinCharacters().equals(minCharacters)) {
			oldValues += " minCharacters: " + item.getMinCharacters();
			newValues += " minCharacters: " + minCharacters;
		}
		item.setMinCharacters(minCharacters);

		int maxCharacters = getInteger(parameterMap, "maxCharacters", id);
		if (log220 && item.getMaxCharacters() != null && !item.getMaxCharacters().equals(maxCharacters)) {
			oldValues += " maxCharacters: " + item.getMaxCharacters();
			newValues += " maxCharacters: " + maxCharacters;
		}
		item.setMaxCharacters(maxCharacters);

		int minChoices = getInteger(parameterMap, "minChoices", id);
		if (log220 && item.getMinChoices() != minChoices) {
			oldValues += " minChoices: " + item.getMinChoices();
			newValues += " minChoices: " + minChoices;
		}
		item.setMinChoices(minChoices);

		int maxChoices = getInteger(parameterMap, "maxChoices", id);
		if (log220 && item.getMaxChoices() != maxChoices) {
			oldValues += " maxChoices: " + item.getMaxChoices();
			newValues += " maxChoices: " + maxChoices;
		}
		item.setMaxChoices(maxChoices);

		int numRows = getInteger(parameterMap, "numRows", id);
		if (log220 && item.getNumRows() != null && !item.getNumRows().equals(numRows)) {
			oldValues += " numRows: " + item.getNumRows();
			newValues += " numRows: " + numRows;
		}
		item.setNumRows(numRows);

		boolean useRadioButtons = getBoolean(parameterMap, "useRadioButtons", id);
		if (log220 && item.getUseRadioButtons() != useRadioButtons) {
			oldValues += " useRadioButtons: " + item.getUseRadioButtons();
			newValues += " useRadioButtons: " + useRadioButtons;
		}
		item.setUseRadioButtons(useRadioButtons);

		boolean useCheckBoxes = getBoolean(parameterMap, "useCheckboxes", id);
		if (log220 && item.getUseCheckboxes() != useCheckBoxes) {
			oldValues += " useCheckBoxes: " + item.getUseCheckboxes();
			newValues += " useCheckBoxes: " + useCheckBoxes;
		}
		item.setUseCheckboxes(useCheckBoxes);

		int numColumns = getInteger(parameterMap, "numColumns", id);
		if (log220 && item.getNumColumns() != numColumns) {
			oldValues += " numColumns: " + item.getNumRows();
			newValues += " numColumns: " + numColumns;
		}
		item.setNumColumns(numColumns);

		int order = getInteger(parameterMap, "order", id);
		if (log220 && item.getOrder() != null && !item.getOrder().equals(order)) {
			oldValues += " order: " + item.getOrder();
			newValues += " order: " + order;
		}
		item.setOrder(order);

		String resultText = getString(parameterMap, "resultText", id, servletContext);
		if (log220 && item.getResultText() != null && !item.getResultText().equals(resultText)) {
			oldValues += " resultText: " + item.getResultText();
			newValues += " resultText: " + resultText;
		}
		item.setResultText(resultText);

		int decimalPlaces = getInteger(parameterMap, "decimalPlaces", id);
		if (log220 && item.getDecimalPlaces() != null && !item.getDecimalPlaces().equals(decimalPlaces)) {
			oldValues += " decimalPlaces: " + item.getDecimalPlaces();
			newValues += " decimalPlaces: " + decimalPlaces;
		}
		item.setDecimalPlaces(decimalPlaces);

		String unit = getString(parameterMap, "unit", id, servletContext);
		if (log220 && item.getUnit() != null && !item.getUnit().equals(unit)) {
			oldValues += " unit: " + item.getUnit();
			newValues += " unit: " + unit;
		}
		item.setUnit(unit);

		Double min = getDouble(parameterMap, "min", id);
		if (log220 && item.getMin() != null && !item.getMin().equals(min)) {
			oldValues += " min: " + item.getMin();
			newValues += " min: " + min;
		}
		item.setMin(min);

		Double max = getDouble(parameterMap, "max", id);
		if (log220 && item.getMax() != null && !item.getMax().equals(max)) {
			oldValues += " max: " + item.getMax();
			newValues += " max: " + max;
		}
		item.setMax(max);

		String formula = getString(parameterMap, "formula", id, servletContext);
		if (log220 && item.getFormula() != null && !item.getFormula().equals(formula)) {
			oldValues += " formula: " + item.getFormula();
			newValues += " formula: " + formula;
		}
		item.setFormula(formula);
		
		Boolean isReadonly = getBoolean(parameterMap, "readonly", id);
		if (log220 && !isReadonly.equals(item.getReadonly())) {
			oldValues += " readonly: " + item.getReadonly();
			newValues += " readonly: " + isReadonly;
		}
		item.setReadonly(isReadonly);
		
		if (item.getCellType() == CellType.SingleChoice || item.getCellType() == CellType.MultipleChoice) {
			String[] answers = getAnswers(parameterMap, id);
			String[] originalAnswers = getOriginalAnswers(parameterMap, id);
			String[] uniqueIDs = getUniqueIDs(parameterMap, id);
			String[] shortnamesForAnswers = parameterMap.get("pashortname" + id);
			if (!newElement) {
				DeletePossibleAnswers(answers, originalAnswers, item.getPossibleAnswers());
			}
			getPossibleAnswers(answers, originalAnswers, uniqueIDs, null, shortnamesForAnswers, null, null, null, null, item.getPossibleAnswers(), survey, log220, new StringBuilder(), null);
		} else if (item.getPossibleAnswers() != null && !item.getPossibleAnswers().isEmpty()) {
			// this means the type was SingleChoice/MultipleChoice before -> we need to delete the PossibleAnswers
			DeletePossibleAnswers(new String[0], new String[0], item.getPossibleAnswers());		
		}

		if (log220 && oldValues.length() > 0) {
			String[] oldnew = { oldValues, newValues };
			item.getActivitiesToLog().put(ActivityRegistry.ID_ELEMENT_UPDATED, oldnew);
		}

		return item;
	}
	
	private static void DeletePossibleAnswers(String[] answers, String[] originalAnswers, List<PossibleAnswer> possibleAnswers) {
		List<PossibleAnswer> toDelete = new ArrayList<>();
		List<String> palist = new ArrayList<>();
		for (PossibleAnswer pa : possibleAnswers) {
			boolean found = false;
			for (String answer : answers) {
				if (pa.getTitle().equals(answer)) {
					found = true;
					break;
				}
			}
			if (!found)
	        {
	    	  for (String answer : originalAnswers) {
	             if (pa.getTitle().equals(answer)) {
	               found = true;
	               break;
	             }
	          }	
	        }
			if (!found || palist.contains(pa.getTitle())) {
				toDelete.add(pa);
			} else {
				palist.add(pa.getTitle());
			}
		}

		for (PossibleAnswer pa : toDelete) {
			possibleAnswers.remove(pa);
		}
	}
	
	private static void getPossibleAnswers(String[] answers, String[] originalAnswers, String[] uniqueIDs, String[] dependenciesForAnswers, String[] shortnamesForAnswers, String[] correctForAnswers, String[] pointsForAnswers, String[] feedbackForAnswers, String[] exclusiveForAnswers, List<PossibleAnswer> possibleAnswers, Survey survey, boolean log220, StringBuilder newAnswers, HashMap<PossibleAnswer, String> dependencies) {
		PossibleAnswer p;
		int j = 0;
		String shortname;
		for (int k = 0; k < answers.length; k++) {

			String answer = answers[k];
			String originalAnswer = originalAnswers[k];
			String uniqueID = "";
			if (uniqueIDs != null && uniqueIDs.length > k)
				uniqueID = uniqueIDs[k];
			String answerDependencies = "";
			if (dependenciesForAnswers != null && dependenciesForAnswers.length > k)
				answerDependencies = dependenciesForAnswers[k];
			shortname = "";
			if (shortnamesForAnswers != null && shortnamesForAnswers.length > k)
				shortname = shortnamesForAnswers[k];

			p = null;
			boolean found = false;

			for (PossibleAnswer pa : possibleAnswers) {
				if ((uniqueIDs != null && pa.getUniqueId().equals(uniqueID)) ||
					(uniqueIDs == null && (pa.getTitle().equals(answer) || pa.getTitle().equals(originalAnswer)))) {
					p = pa;
					p.getDependentElements().getDependentElements().clear();
					found = true;
					break;
				}
			}

			if (p == null) {
				p = new PossibleAnswer();
				p.setUniqueId(UUID.randomUUID().toString());
			}

			p.setTitle(answer);
			p.setShortname(shortname);
			p.setPosition(j);
			
			if (exclusiveForAnswers != null) {
				p.setExclusive(exclusiveForAnswers[k].equalsIgnoreCase("true"));
			}

			if (survey.getIsQuiz()) {
				boolean correct = false;
				if (correctForAnswers != null && correctForAnswers.length > k)
					correct = correctForAnswers[k].equalsIgnoreCase("true");

				int points = 0;
				if (pointsForAnswers != null && pointsForAnswers.length > k && pointsForAnswers[k].length() > 0)
				{
					try {
						points = Integer.parseInt(pointsForAnswers[k]);
					} catch (NumberFormatException nfe) {
						logger.error(nfe.getLocalizedMessage(), nfe);
					}
				}
				
				String feedback = "";
				if (feedbackForAnswers != null && feedbackForAnswers.length > k)
					feedback = feedbackForAnswers[k];

				if (p.getScoring() == null) {
					p.setScoring(new ScoringItem());
				}

				p.getScoring().setCorrect(correct);
				p.getScoring().setPoints(points);
				p.getScoring().setFeedback(feedback);
			}

			j++;

			if (log220) {
				newAnswers.append(p.getTitle()).append("(").append(p.getShortname()).append("),");
			}

			if (!found)
				possibleAnswers.add(p);

			if (answerDependencies != null && answerDependencies.trim().length() > 0) {
				dependencies.put(p, answerDependencies);
			}
		}
	}

	private static SingleChoiceQuestion getSingleChoice(Map<String, String[]> parameterMap, Element currentElement,
			Survey survey, String id, String[] answers, String[] originalAnswers, String[] uniqueIDs, String[] dependenciesForAnswers,
			HashMap<PossibleAnswer, String> dependencies, String[] shortnamesForAnswers, String[] correctForAnswers,
			String[] pointsForAnswers, String[] feedbackForAnswers, ServletContext servletContext, boolean log220)
			throws InvalidXHTMLException {
		String oldValues = "";
		String newValues = "";

		SingleChoiceQuestion singlechoice;
		String choicetype = parameterMap.get("choicetype" + id)[0];

		if (!(currentElement instanceof SingleChoiceQuestion)) {
			singlechoice = new SingleChoiceQuestion(getString(parameterMap, "text", id, servletContext),
					getString(parameterMap, Constants.SHORTNAME, id, servletContext),
					getString(parameterMap, "uid", id, servletContext));
		} else {
			singlechoice = (SingleChoiceQuestion) currentElement;
			DeletePossibleAnswers(answers, originalAnswers, singlechoice.getPossibleAnswers());
		}

		String title = getString(parameterMap, "text", id, servletContext);
		if (log220 && !singlechoice.getTitle().equals(title)) {
			oldValues += " title: " + singlechoice.getTitle();
			newValues += " title: " + title;
		}
		singlechoice.setTitle(title);

		String shortname = getString(parameterMap, Constants.SHORTNAME, id, servletContext);
		if (log220 && singlechoice.getShortname() != null && !singlechoice.getShortname().equals(shortname)) {
			oldValues += " shortname: " + singlechoice.getShortname();
			newValues += " shortname: " + shortname;
		}
		singlechoice.setShortname(shortname);
		
		Boolean isDelphiQuestion = getBoolean(parameterMap, "delphiquestion", id);
		if (log220 && !isDelphiQuestion.equals(singlechoice.getIsDelphiQuestion())) {
			oldValues += " isDelphiQuestion: " + singlechoice.getIsDelphiQuestion();
			newValues += " isDelphiQuestion: " + isDelphiQuestion;
		}
		singlechoice.setIsDelphiQuestion(isDelphiQuestion);

		boolean useAndLogic = getBoolean(parameterMap, "useAndLogic", id);
		if (log220 && !singlechoice.getUseAndLogic().equals(useAndLogic)) {
			oldValues += " useAndLogic: " + singlechoice.getUseAndLogic();
			newValues += " useAndLogic: " + useAndLogic;
		}		
		singlechoice.setUseAndLogic(useAndLogic);

		Boolean showExplanationBox = getBoolean(parameterMap, "explanationbox", id);
		if (log220 && !showExplanationBox.equals(singlechoice.getShowExplanationBox())) {
			oldValues += " showExplanationBox: " + singlechoice.getShowExplanationBox();
			newValues += " showExplanationBox: " + showExplanationBox;
		}
		singlechoice.setShowExplanationBox(showExplanationBox);

		Boolean isOptional = getBoolean(parameterMap, "optional", id);
		if (isDelphiQuestion && !isOptional) {
			// Enforce optionality of Delphi questions.
			isOptional = true;
		}
		if (log220 && !isOptional.equals(singlechoice.getOptional())) {
			oldValues += " optional: " + singlechoice.getOptional();
			newValues += " optional: " + isOptional;
		}
		singlechoice.setOptional(isOptional);

		Boolean isReadonly = getBoolean(parameterMap, "readonly", id);
		if (log220 && !isReadonly.equals(singlechoice.getReadonly())) {
			oldValues += " readonly: " + singlechoice.getReadonly();
			newValues += " readonly: " + isReadonly;
		}
		singlechoice.setReadonly(isReadonly);

		String help = getString(parameterMap, "help", id, servletContext);
		if (log220 && singlechoice.getHelp() != null && !singlechoice.getHelp().equals(help)) {
			oldValues += " help: " + singlechoice.getHelp();
			newValues += " help: " + help;
		}
		singlechoice.setHelp(help);

		Boolean attribute = getBoolean(parameterMap, "attribute", id);
		if (log220 && !attribute.equals(singlechoice.getIsAttribute())) {
			oldValues += " attribute: " + singlechoice.getIsAttribute();
			newValues += " attribute: " + attribute;
		}
		singlechoice.setIsAttribute(attribute);

		String delphiChartTypeString = getString(parameterMap, "delphicharttype", id, servletContext);
		DelphiChartType delphiChartType = StringUtils.isNullOrEmpty(delphiChartTypeString) ? singlechoice.getDefaultDelphiChartType() : DelphiChartType.valueOf(delphiChartTypeString);
		if (log220 && delphiChartType != singlechoice.getDelphiChartType()) {
			oldValues += " delphiChartType: " + singlechoice.getDelphiChartType();
			newValues += " delphiChartType: " + delphiChartType;
		}
		singlechoice.setDelphiChartType(delphiChartType);

		String nameattribute = getString(parameterMap, "nameattribute", id, servletContext);
		if (log220 && !singlechoice.getAttributeName().equals(nameattribute)) {
			oldValues += " attributename: " + singlechoice.getAttributeName();
			newValues += " attributename: " + nameattribute;
		}
		singlechoice.setAttributeName(nameattribute);

		if (!singlechoice.getChoiceType().equals(choicetype)){
			if (log220) {
				oldValues += " choiceType: " + singlechoice.getChoiceType();
				newValues += " choiceType: " + choicetype;
			}
			singlechoice.setChoiceType(choicetype);
		}

		Integer columns = getInteger(parameterMap, "columns", id);
		if (log220 && !columns.equals(singlechoice.getNumColumns())) {
			oldValues += " columns: " + singlechoice.getNumColumns();
			newValues += " columns: " + columns;
		}
		singlechoice.setNumColumns(columns);

		Integer order = getInteger(parameterMap, "order", id);
		if (log220 && !order.equals(singlechoice.getOrder())) {
			oldValues += " order: " + singlechoice.getOrder();
			newValues += " order: " + order;
		}
		singlechoice.setOrder(order);

		Integer scoring = getInteger(parameterMap, "scoring", id);
		if (log220 && !scoring.equals(singlechoice.getScoring())) {
			oldValues += " scoring: " + singlechoice.getScoring();
			newValues += " scoring: " + scoring;
		}
		singlechoice.setScoring(scoring);

		Integer points = getInteger(parameterMap, "points", id, 1);
		if (log220 && !points.equals(singlechoice.getQuizPoints())) {
			oldValues += " points: " + singlechoice.getQuizPoints();
			newValues += " points: " + points;
		}
		singlechoice.setQuizPoints(points);

		String subType = getString(parameterMap, "subType", id, servletContext);
		if (log220 && !singlechoice.getSubType().equals(subType)) {
			oldValues += " subType: " + singlechoice.getSubType();
			newValues += " subType: " + subType;
		}
		singlechoice.setSubType(subType);

		Integer displayMode = getInteger(parameterMap, "displayMode", id, 1);
		if (log220 && !displayMode.equals(singlechoice.getDisplayMode())) {
			oldValues += " displayMode: " + singlechoice.getDisplayMode();
			newValues += " displayMode: " + displayMode;
		}
		singlechoice.setDisplayMode(displayMode);

		Integer maxDistance = getInteger(parameterMap, "maxDistance", id);
		if (log220 && !maxDistance.equals(singlechoice.getMaxDistance())) {
			oldValues += " maxDistance: " + singlechoice.getMaxDistance();
			newValues += " maxDistance: " + maxDistance;
		}
		singlechoice.setMaxDistance(maxDistance);
		
		StringBuilder oldAnswers = new StringBuilder();
		StringBuilder newAnswers = new StringBuilder();
		if (log220) {
			for (PossibleAnswer pa : singlechoice.getPossibleAnswers()) {
				oldAnswers.append(pa.getTitle()).append("(").append(pa.getShortname()).append("),");
			}
		}

		getPossibleAnswers(answers, originalAnswers, uniqueIDs, dependenciesForAnswers, shortnamesForAnswers, correctForAnswers, pointsForAnswers, feedbackForAnswers, null, singlechoice.getPossibleAnswers(), survey, log220, newAnswers, dependencies);

		if (log220 && !oldAnswers.toString().equals(newAnswers.toString())) {
			oldValues += " answers: " + oldAnswers;
			newValues += " answers: " + newAnswers;
		}

		if (log220 && oldValues.length() > 0) {
			String[] oldnew = { oldValues, newValues };
			singlechoice.getActivitiesToLog().put(ActivityRegistry.ID_ELEMENT_UPDATED, oldnew);
		}

		return singlechoice;
	}

	private static MultipleChoiceQuestion getMultipleChoice(Map<String, String[]> parameterMap, Element currentElement,
			Survey survey, String id, String[] answers, String[] originalAnswers, String[] uniqueIDs, String[] dependenciesForAnswers,
			HashMap<PossibleAnswer, String> dependencies, String[] shortnamesForAnswers, String[] correctForAnswers,
			String[] pointsForAnswers, String[] feedbackForAnswers, String[] exclusiveForAnswers, ServletContext servletContext, boolean log220)
			throws InvalidXHTMLException {
		String oldValues = "";
		String newValues = "";

		String choicetype = parameterMap.get("choicetype" + id)[0];

		MultipleChoiceQuestion multiplechoice;

		if (!(currentElement instanceof MultipleChoiceQuestion)) {
			multiplechoice = new MultipleChoiceQuestion(getString(parameterMap, "text", id, servletContext),
					getString(parameterMap, Constants.SHORTNAME, id, servletContext),
					getString(parameterMap, "uid", id, servletContext));
		} else {
			multiplechoice = (MultipleChoiceQuestion) currentElement;
			DeletePossibleAnswers(answers, originalAnswers, multiplechoice.getPossibleAnswers());
		}

		String title = getString(parameterMap, "text", id, servletContext);
		if (log220 && !multiplechoice.getTitle().equals(title)) {
			oldValues += " title: " + multiplechoice.getTitle();
			newValues += " title: " + title;
		}
		multiplechoice.setTitle(title);

		String shortname = getString(parameterMap, Constants.SHORTNAME, id, servletContext);
		if (log220 && multiplechoice.getShortname() != null && !multiplechoice.getShortname().equals(shortname)) {
			oldValues += " shortname: " + multiplechoice.getShortname();
			newValues += " shortname: " + shortname;
		}
		multiplechoice.setShortname(shortname);

		Boolean isDelphiQuestion = getBoolean(parameterMap, "delphiquestion", id);
		if (log220 && !isDelphiQuestion.equals(multiplechoice.getIsDelphiQuestion())) {
			oldValues += " isDelphiQuestion: " + multiplechoice.getIsDelphiQuestion();
			newValues += " isDelphiQuestion: " + isDelphiQuestion;
		}
		multiplechoice.setIsDelphiQuestion(isDelphiQuestion);

		boolean useAndLogic = getBoolean(parameterMap, "useAndLogic", id);
		if (log220 && !multiplechoice.getUseAndLogic().equals(useAndLogic)) {
			oldValues += " useAndLogic: " + multiplechoice.getUseAndLogic();
			newValues += " useAndLogic: " + useAndLogic;
		}
		multiplechoice.setUseAndLogic(useAndLogic);
		
		Boolean showExplanationBox = getBoolean(parameterMap, "explanationbox", id);
		if (log220 && !showExplanationBox.equals(multiplechoice.getShowExplanationBox())) {
			oldValues += " showExplanationBox: " + multiplechoice.getShowExplanationBox();
			newValues += " showExplanationBox: " + showExplanationBox;
		}
		multiplechoice.setShowExplanationBox(showExplanationBox);

		Boolean isOptional = getBoolean(parameterMap, "optional", id);
		if (isDelphiQuestion && !isOptional) {
			// Enforce optionality of Delphi questions.
			isOptional = true;
		}
		if (log220 && !isOptional.equals(multiplechoice.getOptional())) {
			oldValues += " optional: " + multiplechoice.getOptional();
			newValues += " optional: " + isOptional;
		}
		multiplechoice.setOptional(isOptional);

		Boolean isReadonly = getBoolean(parameterMap, "readonly", id);
		if (log220 && !isReadonly.equals(multiplechoice.getReadonly())) {
			oldValues += " readonly: " + multiplechoice.getReadonly();
			newValues += " readonly: " + isReadonly;
		}
		multiplechoice.setReadonly(isReadonly);

		String help = getString(parameterMap, "help", id, servletContext);
		if (log220 && multiplechoice.getHelp() != null && !multiplechoice.getHelp().equals(help)) {
			oldValues += " help: " + multiplechoice.getHelp();
			newValues += " help: " + help;
		}
		multiplechoice.setHelp(help);

		Boolean attribute = getBoolean(parameterMap, "attribute", id);
		if (log220 && !attribute.equals(multiplechoice.getIsAttribute())) {
			oldValues += " attribute: " + multiplechoice.getIsAttribute();
			newValues += " attribute: " + attribute;
		}
		multiplechoice.setIsAttribute(attribute);

		String delphiChartTypeString = getString(parameterMap, "delphicharttype", id, servletContext);
		DelphiChartType delphiChartType = StringUtils.isNullOrEmpty(delphiChartTypeString) ? multiplechoice.getDefaultDelphiChartType() : DelphiChartType.valueOf(delphiChartTypeString);
		if (log220 && delphiChartType != multiplechoice.getDelphiChartType()) {
			oldValues += " delphiChartType: " + multiplechoice.getDelphiChartType();
			newValues += " delphiChartType: " + delphiChartType;
		}
		multiplechoice.setDelphiChartType(delphiChartType);

		String nameattribute = getString(parameterMap, "nameattribute", id, servletContext);
		if (log220 && !multiplechoice.getAttributeName().equals(nameattribute)) {
			oldValues += " attributename: " + multiplechoice.getAttributeName();
			newValues += " attributename: " + nameattribute;
		}
		multiplechoice.setAttributeName(nameattribute);

		if (!multiplechoice.getChoiceType().equals(choicetype)){
			if (log220) {
				oldValues += " choiceType: " + multiplechoice.getChoiceType();
				newValues += " choiceType: " + choicetype;
			}
			multiplechoice.setChoiceType(choicetype);
		}

		Integer columns = getInteger(parameterMap, "columns", id);
		if (log220 && !columns.equals(multiplechoice.getNumColumns())) {
			oldValues += " columns: " + multiplechoice.getNumColumns();
			newValues += " columns: " + columns;
		}
		multiplechoice.setNumColumns(columns);

		Integer minChoices = getInteger(parameterMap, "choicemin", id);
		if (log220 && !minChoices.equals(multiplechoice.getMinChoices())) {
			oldValues += " minChoices: " + multiplechoice.getMinChoices();
			newValues += " minChoices: " + minChoices;
		}
		multiplechoice.setMinChoices(minChoices);

		Integer maxChoices = getInteger(parameterMap, "choicemax", id);
		if (log220 && !maxChoices.equals(multiplechoice.getMaxChoices())) {
			oldValues += " maxChoices: " + multiplechoice.getMaxChoices();
			newValues += " maxChoices: " + maxChoices;
		}
		multiplechoice.setMaxChoices(maxChoices);

		Integer order = getInteger(parameterMap, "order", id);
		if (log220 && !order.equals(multiplechoice.getOrder())) {
			oldValues += " order: " + multiplechoice.getOrder();
			newValues += " order: " + order;
		}
		multiplechoice.setOrder(order);

		Integer scoring = getInteger(parameterMap, "scoring", id);
		if (log220 && !scoring.equals(multiplechoice.getScoring())) {
			oldValues += " scoring: " + multiplechoice.getScoring();
			newValues += " scoring: " + scoring;
		}
		multiplechoice.setScoring(scoring);

		Integer points = getInteger(parameterMap, "points", id, 1);
		if (log220 && !points.equals(multiplechoice.getQuizPoints())) {
			oldValues += " points: " + multiplechoice.getQuizPoints();
			newValues += " points: " + points;
		}
		multiplechoice.setQuizPoints(points);

		Boolean noNegativeScore = getBoolean(parameterMap, "noNegativeScore", id);
		if (log220 && !noNegativeScore.equals(multiplechoice.getNoNegativeScore())) {
			oldValues += " noNegativeScore: " + multiplechoice.getNoNegativeScore();
			newValues += " noNegativeScore: " + noNegativeScore;
		}
		multiplechoice.setNoNegativeScore(noNegativeScore);

		String subType = getString(parameterMap, "subType", id, servletContext);
		if (log220 && !multiplechoice.getSubType().equals(subType)) {
			oldValues += " subType: " + multiplechoice.getSubType();
			newValues += " subType: " + subType;
		}
		multiplechoice.setSubType(subType);

		Integer displayMode = getInteger(parameterMap, "displayMode", id, 1);
		if (log220 && !displayMode.equals(multiplechoice.getDisplayMode())) {
			oldValues += " displayMode: " + multiplechoice.getDisplayMode();
			newValues += " displayMode: " + displayMode;
		}
		multiplechoice.setDisplayMode(displayMode);

		StringBuilder oldAnswers = new StringBuilder();
		StringBuilder newAnswers = new StringBuilder();
		if (log220) {
			for (PossibleAnswer pa : multiplechoice.getPossibleAnswers()) {
				oldAnswers.append(pa.getTitle()).append("(").append(pa.getShortname()).append("),");
			}
		}

		getPossibleAnswers(answers, originalAnswers, uniqueIDs, dependenciesForAnswers, shortnamesForAnswers, correctForAnswers, pointsForAnswers, feedbackForAnswers, exclusiveForAnswers, multiplechoice.getPossibleAnswers(), survey, log220, newAnswers, dependencies);

		if (log220 && !oldAnswers.toString().equals(newAnswers.toString())) {
			oldValues += " answers: " + oldAnswers;
			newValues += " answers: " + newAnswers;
		}

		if (log220 && oldValues.length() > 0) {
			String[] oldnew = { oldValues, newValues };
			multiplechoice.getActivitiesToLog().put(ActivityRegistry.ID_ELEMENT_UPDATED, oldnew);
		}

		return multiplechoice;
	}

	private static void retrieveFromParameterMap(Map<String, String[]> parameterMap, String key, List<String> destinationList, boolean doFilter) {
		String[] items = parameterMap.getOrDefault(key, new String[0]);
		List<String> list = new ArrayList<>(Arrays.asList(items));
		if (doFilter) {
			list.removeAll(Arrays.asList("", null));
			for (String entry : list) {
				if (null == entry) {
					continue;
				}
				destinationList.add(Tools.filterHTML(entry));
			}
		} else {
			destinationList.addAll(list);		
		}
	}

	private static void parseParameterMapForRankingitems(Map<String, String[]> parameterMap, String id, List<String> rankingItemTitles, List<String> rankingItemOriginalTitles, List<String> rankingItemShortNames, List<String> rankingItemUids) {
		retrieveFromParameterMap(parameterMap, "rankingitemtitle" + id, rankingItemTitles, true);
		retrieveFromParameterMap(parameterMap, "rankingitemoriginaltitle" + id, rankingItemOriginalTitles, true);
		retrieveFromParameterMap(parameterMap, "rankingitemshortname" + id, rankingItemShortNames, false);
		retrieveFromParameterMap(parameterMap, "rankingitemuid" + id, rankingItemUids, false);
	}

	private static RankingQuestion getRankingQuestion(Map<String, String[]> parameterMap, Element currentElement, String id, List<String> rankingItemTitles, List<String> rankingItemOriginalTitles, List<String> rankingItemShortNames, List<String> rankingItemUids, ServletContext servletContext, boolean log220) throws InvalidXHTMLException {
		String oldValues = "";
		String newValues = "";

		RankingQuestion rankingQuestion;
		if (currentElement instanceof RankingQuestion) {
			rankingQuestion = (RankingQuestion) currentElement;

			// make sure the childElements match the items given and that all are unique
			List<RankingItem> itemsToDelete = new ArrayList<>();
			List<String> newUniqueItems = new ArrayList<>();
			for (RankingItem thatItem : rankingQuestion.getChildElements()) {
				String uid = thatItem.getUniqueId();
				boolean found = rankingItemUids.contains(uid);

				if (!found || newUniqueItems.contains(uid)) {
					itemsToDelete.add(thatItem);
				} else {
					newUniqueItems.add(uid);
				}
			}

			rankingQuestion.getChildElements().removeAll(itemsToDelete);
		} else {
			rankingQuestion = new RankingQuestion();
			String uid = getString(parameterMap, "uid", id, servletContext);
			rankingQuestion.setUniqueId(uid);
		}

		String title = getString(parameterMap, "text", id, servletContext);
		if (log220 && !rankingQuestion.getTitle().equals(title)) {
			oldValues += " title: " + rankingQuestion.getTitle();
			newValues += " title: " + title;
		}
		rankingQuestion.setTitle(title);

		Boolean isDelphiQuestion = getBoolean(parameterMap, "delphiquestion", id);
		if (log220 && !isDelphiQuestion.equals(rankingQuestion.getIsDelphiQuestion())) {
			oldValues += " isDelphiQuestion: " + rankingQuestion.getIsDelphiQuestion();
			newValues += " isDelphiQuestion: " + isDelphiQuestion;
		}
		rankingQuestion.setIsDelphiQuestion(isDelphiQuestion);

		// Enforce optionality of Delphi questions.
		Boolean isOptional = isDelphiQuestion || getBoolean(parameterMap, "optional", id);
		if (log220 && !isOptional.equals(rankingQuestion.getOptional())) {
			oldValues += " optional: " + rankingQuestion.getOptional();
			newValues += " optional: " + isOptional;
		}
		rankingQuestion.setOptional(isOptional);
		
		String delphiChartTypeString = getString(parameterMap, "delphicharttype", id, servletContext);
		DelphiChartType delphiChartType = StringUtils.isNullOrEmpty(delphiChartTypeString) ? DelphiChartType.None : DelphiChartType.valueOf(delphiChartTypeString);
		if (log220 && delphiChartType != rankingQuestion.getDelphiChartType()) {
			oldValues += " delphiChartType: " + rankingQuestion.getDelphiChartType();
			newValues += " delphiChartType: " + delphiChartType;
		}
		rankingQuestion.setDelphiChartType(delphiChartType);

		Boolean showExplanationBox = getBoolean(parameterMap, "explanationbox", id);
		if (log220 && !showExplanationBox.equals(rankingQuestion.getShowExplanationBox())) {
			oldValues += " showExplanationBox: " + rankingQuestion.getShowExplanationBox();
			newValues += " showExplanationBox: " + showExplanationBox;
		}
		rankingQuestion.setShowExplanationBox(showExplanationBox);

		String help = getString(parameterMap, "help", id, servletContext);
		if (log220 && rankingQuestion.getHelp() != null && !rankingQuestion.getHelp().equals(help)) {
			oldValues += " help: " + rankingQuestion.getHelp();
			newValues += " help: " + help;
		}
		rankingQuestion.setHelp(help);

		String shortname = getString(parameterMap, Constants.SHORTNAME, id, servletContext);
		if (log220 && rankingQuestion.getShortname() != null && !rankingQuestion.getShortname().equals(shortname)) {
			oldValues += " shortname: " + rankingQuestion.getShortname();
			newValues += " shortname: " + shortname;
		}
		rankingQuestion.setShortname(shortname);
		
		boolean useAndLogic = getBoolean(parameterMap, "useAndLogic", id);
		if (log220 && !rankingQuestion.getUseAndLogic().equals(useAndLogic)) {
			oldValues += " useAndLogic: " + rankingQuestion.getUseAndLogic();
			newValues += " useAndLogic: " + useAndLogic;
		}
		rankingQuestion.setUseAndLogic(useAndLogic);

		Integer order = getInteger(parameterMap, "order", id);
		if (log220 && !order.equals(rankingQuestion.getOrder())) {
			oldValues += " order: " + rankingQuestion.getOrder();
			newValues += " order: " + order;
		}
		rankingQuestion.setOrder(order);

		StringBuilder oldRankingItemTitles = new StringBuilder();
		StringBuilder newRankingItemTitles = new StringBuilder();
		if (log220) {
			for (RankingItem thatItem : rankingQuestion.getChildElements()) {
				oldRankingItemTitles.append(thatItem.getTitle()).append("(").append(thatItem.getShortname()).append("),");
			}
		}

		if (!rankingItemTitles.isEmpty()) {
			List<RankingItem> childElements = rankingQuestion.getChildElements();
			for (int k = 0; k < rankingItemTitles.size(); k++) {
				String thatTitle = Tools.getFromListOrDefault(k, rankingItemTitles, "");
				String thatOriginalTitle = Tools.getFromListOrDefault(k, rankingItemOriginalTitles, "");
				String thatShortName = Tools.getFromListOrDefault(k, rankingItemShortNames, "");
				String thatUid = Tools.getFromListOrDefault(k, rankingItemUids, "");
				RankingItem thatItem = null;
				for (RankingItem existingItem : childElements) {
					if (thatUid.equals(existingItem.getUniqueId())) {
						thatItem = existingItem;
						break;
					}
				}
				boolean isNewItem = false;
				if (null == thatItem) {
					thatItem = new RankingItem();
					isNewItem = true;
				} // else we update the existing Element
				thatItem.setTitle(thatTitle);
				thatItem.setOriginalTitle(thatOriginalTitle);
				thatItem.setShortname(thatShortName);
				thatItem.setUniqueId(thatUid);
				
				if (isNewItem) {
					childElements.add(thatItem);
				}
			}
			rankingQuestion.setChildElements(childElements);
		} else {
			rankingQuestion.setChildElements(new ArrayList<>());
		}

		if (log220) {
			for (RankingItem thatItem : rankingQuestion.getChildElements()) {
				newRankingItemTitles.append(thatItem.getTitle()).append("(").append(thatItem.getShortname()).append("),");
			}
		}
		if (log220 && !oldRankingItemTitles.toString().equals(newRankingItemTitles.toString())) {
			oldValues += " rankingitems: " + oldRankingItemTitles;
			newValues += " rankingitems: " + newRankingItemTitles;
		}

		if (log220 && oldValues.length() > 0) {
			String[] oldNew = { oldValues, newValues };
			rankingQuestion.getActivitiesToLog().put(ActivityRegistry.ID_ELEMENT_UPDATED, oldNew);
		}

		return rankingQuestion;
	}

	private static Matrix getMatrix(Map<String, String[]> parameterMap, Element currentElement,
			String id, Map<Integer, Element> elementsById, String[] dependenciesForAnswers,
			HashMap<Matrix, HashMap<Integer, String>> matrixDependencies, ServletContext servletContext, boolean log220)
			throws InvalidXHTMLException {
		String oldValues = "";
		String newValues = "";
		StringBuilder oldLabels = new StringBuilder();
		StringBuilder newLabels = new StringBuilder();
		Matrix matrix;
		if (currentElement == null) {
			matrix = new Matrix(getString(parameterMap, "text", id, servletContext),
					getString(parameterMap, Constants.SHORTNAME, id, servletContext),
					getString(parameterMap, "uid", id, servletContext));
		} else {
			matrix = (Matrix) currentElement;

			if (log220) {
				for (Element child : matrix.getChildElements()) {
					oldLabels.append(child.getTitle()).append("(").append(child.getShortname()).append("),");
				}
			}

			matrix.getChildElements().clear();
			matrix.getDependentElements().clear();
		}

		String title = getString(parameterMap, "text", id, servletContext);
		if (log220 && !matrix.getTitle().equals(title)) {
			oldValues += " title: " + matrix.getTitle();
			newValues += " title: " + title;
		}
		matrix.setTitle(title);

		String firstCellText = getString(parameterMap, "firstCellText", id, servletContext);
		if (log220 && !matrix.getFirstCellText().equals(firstCellText)) {
			oldValues += " title: " + matrix.getFirstCellText();
			newValues += " title: " + firstCellText;
		}
		matrix.setFirstCellText(firstCellText);

		String help = getString(parameterMap, "help", id, servletContext);
		if (log220 && matrix.getHelp() != null && !matrix.getHelp().equals(help)) {
			oldValues += " help: " + matrix.getHelp();
			newValues += " help: " + help;
		}
		matrix.setHelp(help);

		String shortname = getString(parameterMap, Constants.SHORTNAME, id, servletContext);
		if (log220 && matrix.getShortname() != null && !matrix.getShortname().equals(shortname)) {
			oldValues += " shortname: " + matrix.getShortname();
			newValues += " shortname: " + shortname;
		}
		matrix.setShortname(shortname);
		
		boolean useAndLogic = getBoolean(parameterMap, "useAndLogic", id);
		if (log220 && !matrix.getUseAndLogic().equals(useAndLogic)) {
			oldValues += " useAndLogic: " + matrix.getUseAndLogic();
			newValues += " useAndLogic: " + useAndLogic;
		}
		matrix.setUseAndLogic(useAndLogic);

		Integer rows = getInteger(parameterMap, "matrixrows", id);
		if (log220 && !rows.equals(matrix.getRows())) {
			oldValues += " rows: " + matrix.getRows();
			newValues += " rows: " + rows;
		}
		matrix.setRows(rows);

		Integer cols = getInteger(parameterMap, "matrixcols", id);
		if (log220 && !cols.equals(matrix.getColumns())) {
			oldValues += " columns: " + matrix.getColumns();
			newValues += " columns: " + cols;
		}
		matrix.setColumns(cols);

		Integer tabletype = getInteger(parameterMap, "tabletype", id);
		if (log220 && !tabletype.equals(matrix.getTableType())) {
			oldValues += " tabletype: " + matrix.getTableType();
			newValues += " tabletype: " + tabletype;
		}
		matrix.setTableType(tabletype);

		Boolean isSingleChoice = getBoolean(parameterMap, "single", id);
		if (log220 && !isSingleChoice.equals(matrix.getIsSingleChoice())) {
			oldValues += " isSingleChoice: " + matrix.getIsSingleChoice();
			newValues += " isSingleChoice: " + isSingleChoice;
		}
		matrix.setIsSingleChoice(isSingleChoice);

		Boolean isInterdependent = getBoolean(parameterMap, "interdependent", id);
		if (log220 && !isInterdependent.equals(matrix.getIsInterdependent())) {
			oldValues += " isInterdependent: " + matrix.getIsInterdependent();
			newValues += " isInterdependent: " + isInterdependent;
		}
		matrix.setIsInterdependent(isInterdependent);

		Boolean isDelphiQuestion = getBoolean(parameterMap, "delphiquestion", id);
		if (log220 && !isDelphiQuestion.equals(matrix.getIsDelphiQuestion())) {
			oldValues += " isDelphiQuestion: " + matrix.getIsDelphiQuestion();
			newValues += " isDelphiQuestion: " + isDelphiQuestion;
		}
		matrix.setIsDelphiQuestion(isDelphiQuestion);
		
		Boolean showExplanationBox = getBoolean(parameterMap, "explanationbox", id);
		if (log220 && !showExplanationBox.equals(matrix.getShowExplanationBox())) {
			oldValues += " showExplanationBox: " + matrix.getShowExplanationBox();
			newValues += " showExplanationBox: " + showExplanationBox;
		}
		matrix.setShowExplanationBox(showExplanationBox);

		// Enforce optionality of Delphi questions.
		Boolean isOptional = isDelphiQuestion || getBoolean(parameterMap, "optional", id);
		if (log220 && !isOptional.equals(matrix.getOptional())) {
			oldValues += " optional: " + matrix.getOptional();
			newValues += " optional: " + isOptional;
		}
		matrix.setOptional(isOptional);

		Boolean isReadonly = getBoolean(parameterMap, "readonly", id);
		if (log220 && !isReadonly.equals(matrix.getReadonly())) {
			oldValues += " readonly: " + matrix.getReadonly();
			newValues += " readonly: " + isReadonly;
		}
		matrix.setReadonly(isReadonly);

		Integer order = getInteger(parameterMap, "order", id);
		if (log220 && !order.equals(matrix.getOrder())) {
			oldValues += " order: " + matrix.getOrder();
			newValues += " order: " + order;
		}
		matrix.setOrder(order);

		String type = getString(parameterMap, "choicetype", id, servletContext);
		Boolean useRadioButtons = type != null && (type.equalsIgnoreCase("radio") || type.equalsIgnoreCase("checkbox"));
		if (log220 && !useRadioButtons.equals(matrix.isUseRadioButtons())) {
			oldValues += " useRadioButtons: " + matrix.isUseRadioButtons();
			newValues += " useRadioButtons: " + useRadioButtons;
		}
		matrix.setUseRadioButtons(useRadioButtons);

		Integer minRows = getInteger(parameterMap, "rowsmin", id);
		if (log220 && !minRows.equals(matrix.getMinRows())) {
			oldValues += " minRows: " + matrix.getMinRows();
			newValues += " minRows: " + minRows;
		}
		matrix.setMinRows(minRows);

		Integer maxRows = getInteger(parameterMap, "rowsmax", id);
		if (log220 && !maxRows.equals(matrix.getMaxRows())) {
			oldValues += " maxRows: " + matrix.getMaxRows();
			newValues += " maxRows: " + maxRows;
		}
		matrix.setMaxRows(maxRows);

		String widths = getString(parameterMap, "widths", id, servletContext);
		if (log220 && matrix.getWidths() != null && !matrix.getWidths().equals(widths)) {
			oldValues += " widths: " + matrix.getWidths();
			newValues += " widths: " + widths;
		}
		matrix.setWidths(widths);

		String delphiChartTypeString = getString(parameterMap, "delphicharttype", id, servletContext);
		DelphiChartType delphiChartType = StringUtils.isNullOrEmpty(delphiChartTypeString) ? matrix.getDefaultDelphiChartType() : DelphiChartType.valueOf(delphiChartTypeString);
		if (log220 && delphiChartType != matrix.getDelphiChartType()) {
			oldValues += " delphiChartType: " + matrix.getDelphiChartType();
			newValues += " delphiChartType: " + delphiChartType;
		}
		matrix.setDelphiChartType(delphiChartType);

		// now get the elements inside the matrix
		String matrixIdsAsString = parameterMap.get("matrixelements" + id)[0];
		String[] matrixIds = matrixIdsAsString.split(";");

		if (matrixIdsAsString.trim().length() > 0)
			for (int j = 0; j < matrixIds.length; j++) {
				String elementid = matrixIds[j];

				if (elementid.equalsIgnoreCase("null")) {
					Element child = new EmptyElement();
					child.setPosition(j);
					matrix.getChildElements().add(j, child);
					if (isDelphiQuestion && child instanceof Question) {
						((Question) child).setOptional(true);
					}
				} else {
					String elementtype = parameterMap.get("type" + elementid)[0].toLowerCase();
					Element currentChild;

					if (!elementid.contains("-") && elementsById.containsKey(Integer.parseInt(elementid))) {
						currentChild = elementsById.get(Integer.parseInt(elementid));
					} else {
						currentChild = null;
					}

					Element child = null;
					if (elementtype.equalsIgnoreCase("text")) {
						child = getText(parameterMap, currentChild, elementid, servletContext, log220);
					} else if (elementtype.equalsIgnoreCase("image")) {
						child = getImage(parameterMap, currentChild, elementid, servletContext, log220);
					} else {
						child = new EmptyElement();
					}

					if (child != null) {
						child.setPosition(j);
						child.setOldId(elementid);
						matrix.getChildElements().add(j, child);
						if (log220) {
							newLabels.append(child.getTitle()).append("(").append(child.getShortname()).append("),");
						}
						if (isDelphiQuestion && child instanceof Question) {
							((Question) child).setOptional(true);
						}
					}

				}

			}

		// dependencies
		for (int k = 0; k < (matrix.getRows() - 1) * (matrix.getColumns() - 1); k++) {
			String answerDependencies = "";
			if (dependenciesForAnswers != null && dependenciesForAnswers.length > k)
				answerDependencies = dependenciesForAnswers[k];

			if (answerDependencies != null && answerDependencies.trim().length() > 0) {
				HashMap<Integer, String> map = null;
				if (matrixDependencies.containsKey(matrix)) {
					map = matrixDependencies.get(matrix);
				} else {
					map = new HashMap<>();
					matrixDependencies.put(matrix, map);
				}

				map.put(k, answerDependencies);
			}
		}

		if (log220 && !oldLabels.toString().equals(newLabels.toString())) {
			oldValues += " answers: " + oldLabels;
			newValues += " answers: " + newLabels;
		}

		if (log220 && oldValues.length() > 0) {
			String[] oldnew = { oldValues, newValues };
			matrix.getActivitiesToLog().put(ActivityRegistry.ID_ELEMENT_UPDATED, oldnew);
		}

		return matrix;
	}

	private static Table getTable(Map<String, String[]> parameterMap, Element currentElement, String id,
			Map<Integer, Element> elementsById, ServletContext servletContext, boolean log220)
			throws InvalidXHTMLException {
		String oldValues = "";
		String newValues = "";
		StringBuilder oldLabels = new StringBuilder();
		StringBuilder newLabels = new StringBuilder();

		Table table;
		if (currentElement == null) {
			table = new Table(getString(parameterMap, "text", id, servletContext),
					getString(parameterMap, Constants.SHORTNAME, id, servletContext),
					getString(parameterMap, "uid", id, servletContext));
		} else {
			table = (Table) currentElement;

			if (log220) {
				for (Element child : table.getChildElements()) {
					oldLabels.append(child.getTitle()).append("(").append(child.getShortname()).append("),");
				}
			}

			table.getChildElements().clear();
		}

		String title = getString(parameterMap, "text", id, servletContext);
		if (log220 && !table.getTitle().equals(title)) {
			oldValues += " title: " + table.getTitle();
			newValues += " title: " + title;
		}
		table.setTitle(title);

		String firstCellText = getString(parameterMap, "firstCellText", id, servletContext);
		if (log220 && !table.getFirstCellText().equals(firstCellText)) {
			oldValues += " title: " + table.getFirstCellText();
			newValues += " title: " + firstCellText;
		}
		table.setFirstCellText(firstCellText);

		String help = getString(parameterMap, "help", id, servletContext);
		if (log220 && table.getHelp() != null && !table.getHelp().equals(help)) {
			oldValues += " help: " + table.getHelp();
			newValues += " help: " + help;
		}
		table.setHelp(help);

		String shortname = getString(parameterMap, Constants.SHORTNAME, id, servletContext);
		if (log220 && table.getShortname() != null && !table.getShortname().equals(shortname)) {
			oldValues += " shortname: " + table.getShortname();
			newValues += " shortname: " + shortname;
		}
		table.setShortname(shortname);
		
		boolean useAndLogic = getBoolean(parameterMap, "useAndLogic", id);
		if (log220 && !table.getUseAndLogic().equals(useAndLogic)) {
			oldValues += " useAndLogic: " + table.getUseAndLogic();
			newValues += " useAndLogic: " + useAndLogic;
		}
		table.setUseAndLogic(useAndLogic);

		Integer type = getInteger(parameterMap, "tabletype", id);
		if (log220 && !type.equals(table.getTableType())) {
			oldValues += " type: " + table.getTableType();
			newValues += " type: " + type;
		}
		table.setTableType(type);

		Integer rows = getInteger(parameterMap, "rows", id);
		if (log220 && !rows.equals(table.getRows())) {
			oldValues += " rows: " + table.getRows();
			newValues += " rows: " + rows;
		}
		table.setRows(rows);

		Integer cols = getInteger(parameterMap, "columns", id);
		if (log220 && !cols.equals(table.getColumns())) {
			oldValues += " columns: " + table.getColumns();
			newValues += " columns: " + cols;
		}
		table.setColumns(cols);

		Boolean isDelphiQuestion = getBoolean(parameterMap, "delphiquestion", id);
		if (log220 && !isDelphiQuestion.equals(table.getIsDelphiQuestion())) {
			oldValues += " isDelphiQuestion: " + table.getIsDelphiQuestion();
			newValues += " isDelphiQuestion: " + isDelphiQuestion;
		}
		table.setIsDelphiQuestion(isDelphiQuestion);
		
		Boolean showExplanationBox = getBoolean(parameterMap, "explanationbox", id);
		if (log220 && !showExplanationBox.equals(table.getShowExplanationBox())) {
			oldValues += " showExplanationBox: " + table.getShowExplanationBox();
			newValues += " showExplanationBox: " + showExplanationBox;
		}
		table.setShowExplanationBox(showExplanationBox);

		// Enforce optionality of Delphi questions.
		Boolean isOptional = isDelphiQuestion || getBoolean(parameterMap, "optional", id);
		if (log220 && !isOptional.equals(table.getOptional())) {
			oldValues += " optional: " + table.getOptional();
			newValues += " optional: " + isOptional;
		}
		table.setOptional(isOptional);

		Boolean isReadonly = getBoolean(parameterMap, "readonly", id);
		if (log220 && !isReadonly.equals(table.getReadonly())) {
			oldValues += " readonly: " + table.getReadonly();
			newValues += " readonly: " + isReadonly;
		}
		table.setReadonly(isReadonly);

		String widths = getString(parameterMap, "widths", id, servletContext);
		if (log220 && table.getWidths() != null && !table.getWidths().equals(widths)) {
			oldValues += " widths: " + table.getWidths();
			newValues += " widths: " + widths;
		}
		table.setWidths(widths);

		// now get the elements inside the table
		String tableIdsAsString = parameterMap.get("tableelements" + id)[0];
		String[] tableIds = tableIdsAsString.split(";");

		if (tableIdsAsString.trim().length() > 0)
			for (int j = 0; j < tableIds.length; j++) {
				String elementid = tableIds[j];

				if (elementid.equalsIgnoreCase("null")) {
					Element child = new EmptyElement();
					child.setPosition(j);
					table.getChildElements().add(j, child);
					if (isDelphiQuestion && child instanceof Question) {
						((Question) child).setOptional(true);
					}
				} else {

					Element currentChild;

					if (!elementid.contains("-") && elementsById.containsKey(Integer.parseInt(elementid))) {
						currentChild = elementsById.get(Integer.parseInt(elementid));
					} else {
						currentChild = null;
					}

					Element child = getText(parameterMap, currentChild, elementid, servletContext, log220);

					if (child != null) {
						child.setPosition(j);
						table.getChildElements().add(j, child);
						if (isDelphiQuestion && child instanceof Question) {
							((Question) child).setOptional(true);
						}
					}

					if (log220) {
						newLabels.append(child.getTitle()).append("(").append(child.getShortname()).append("),");
					}
				}
			}

		if (log220 && !oldLabels.toString().equals(newLabels.toString())) {
			oldValues += " answers: " + oldLabels;
			newValues += " answers: " + newLabels;
		}

		if (log220 && oldValues.length() > 0) {
			String[] oldnew = { oldValues, newValues };
			table.getActivitiesToLog().put(ActivityRegistry.ID_ELEMENT_UPDATED, oldnew);
		}

		return table;
	}

	public static Element parseElement(HttpServletRequest request, FileService fileService, String id, Survey survey,
			ServletContext servletContext, boolean log220) throws InvalidXHTMLException {
		return parseElement(request, fileService, id, survey, new HashMap<>(), new HashMap<>(), new HashMap<>(),
				servletContext, log220, new HashMap<>());
	}

	private static Element parseElement(HttpServletRequest request, FileService fileService, String id, Survey survey,
			Map<Integer, Element> elementsById,
			HashMap<PossibleAnswer, String> dependencies, HashMap<Matrix, HashMap<Integer, String>> matrixDependencies,
			ServletContext servletContext, boolean log220, Map<String, Integer> fileIDsByUID)
			throws InvalidXHTMLException {
		Map<String, String[]> parameterMap = Ucs2Utf8.requestToHashMap(request);

		if (!parameterMap.containsKey("type" + id)) {
			logger.error("type for following id not found: " + id);
			return null;
		}

		String type = parameterMap.get("type" + id)[0].toLowerCase();

		Element element = null;

		Element currentElement;
		if (!id.contains("-") && elementsById.containsKey(Integer.parseInt(id))) {
			currentElement = elementsById.get(Integer.parseInt(id));
		} else {
			currentElement = null;
		}

		if (type.equalsIgnoreCase("section")) {
			element = getSection(parameterMap, currentElement, survey, id, servletContext,
					log220 && currentElement != null);
		} else if (type.equalsIgnoreCase("matrix")) {
			String[] dependenciesForAnswers = parameterMap.get("dependencies" + id);
			element = getMatrix(parameterMap, currentElement, id, elementsById, dependenciesForAnswers,
					matrixDependencies, servletContext, log220 && currentElement != null);
		} else if (type.equalsIgnoreCase("table")) {
			element = getTable(parameterMap, currentElement, id, elementsById, servletContext,
					log220 && currentElement != null);
		} else if (type.equalsIgnoreCase("text")) {
			element = getText(parameterMap, currentElement, id, servletContext, log220 && currentElement != null);
		} else if (type.equalsIgnoreCase("image")) {
			element = getImage(parameterMap, currentElement, id, servletContext, log220 && currentElement != null);
		} else if (type.equalsIgnoreCase("ruler")) {
			element = getRuler(parameterMap, currentElement, id, servletContext,
					log220 && currentElement != null);
		} else if (type.equalsIgnoreCase("gallery")) {
			element = getGallery(parameterMap, currentElement, id, fileService, servletContext,
					log220 && currentElement != null, fileIDsByUID);
		} else if (type.equalsIgnoreCase("upload")) {
			element = getUpload(parameterMap, currentElement, id, servletContext,
					log220 && currentElement != null);
		} else if (type.equalsIgnoreCase("download")) {
			element = getDownload(parameterMap, currentElement, id, fileService, servletContext,
					log220 && currentElement != null, fileIDsByUID);
		} else if (type.equalsIgnoreCase("confirmation")) {
			element = getConfirmation(parameterMap, currentElement, survey, id, fileService, servletContext,
					log220 && currentElement != null);
		} else if (type.equalsIgnoreCase("freetext")) {
			element = getFreeText(parameterMap, currentElement, id, servletContext,
					log220 && currentElement != null);
		} else if (type.equalsIgnoreCase("regex")) {
			element = getRegEx(parameterMap, currentElement, id, servletContext,
					log220 && currentElement != null);
		} else if (type.equalsIgnoreCase("formula")) {
			element = getFormula(parameterMap, currentElement, id, servletContext,
					log220 && currentElement != null);
		} else if (type.equalsIgnoreCase(Constants.EMAIL)) {
			element = getEmail(parameterMap, currentElement, id, servletContext,
					log220 && currentElement != null);
		} else if (type.equalsIgnoreCase("number")) {
			element = getNumber(parameterMap, currentElement, id, servletContext,
					log220 && currentElement != null);
		} else if (type.equalsIgnoreCase("date")) {
			element = getDate(parameterMap, currentElement, id, servletContext,
					log220 && currentElement != null);
		} else if (type.equalsIgnoreCase("time")) {
			element = getTime(parameterMap, currentElement, id, servletContext,
					log220 && currentElement != null);
		} else if (type.equalsIgnoreCase("rating")) {
			String[] questions = parameterMap.get("question" + id);
			if (questions == null) {
				questions = new String[0];
			}

			List<String> list = new ArrayList<>(Arrays.asList(questions));
			list.removeAll(Arrays.asList("", null));
			questions = list.toArray(new String[0]);

			for (int i = 0; i < questions.length; i++) {
				if (questions[i] != null)
					questions[i] = Tools.filterHTML(questions[i]);
			}

			String[] shortnamesForQuestions = parameterMap.get("questionshortname" + id);
			String[] uidsForQuestions = parameterMap.get("questionuid" + id);
			String[] optionalsForQuestions = parameterMap.get("questionoptional" + id);

			element = getRating(parameterMap, currentElement, id, servletContext, log220 && currentElement != null,
					questions, shortnamesForQuestions, uidsForQuestions, optionalsForQuestions);
		} else if (type.equalsIgnoreCase("choice")) {
			String[] answers = getAnswers(parameterMap, id);
			String[] originalAnswers = getOriginalAnswers(parameterMap, id);
			String[] uniqueIDs = getUniqueIDs(parameterMap, id);
			String[] dependenciesForAnswers = parameterMap.get("dependencies" + id);
			String[] shortnamesForAnswers = parameterMap.get("pashortname" + id);
			String[] correctForAnswers = parameterMap.get("correct" + id);
			String[] pointsForAnswers = parameterMap.get("answerpoints" + id);
			String[] feedbackForAnswers = parameterMap.get("feedback" + id);
			String[] exclusiveForAnswers = parameterMap.get("exclusive" + id);

			boolean single = getBoolean(parameterMap, "single", id);

			if (single) {
				element = getSingleChoice(parameterMap, currentElement, survey, id, answers, originalAnswers, uniqueIDs, dependenciesForAnswers,
						dependencies, shortnamesForAnswers, correctForAnswers, pointsForAnswers, feedbackForAnswers,
						servletContext, log220);
			} else {
				element = getMultipleChoice(parameterMap, currentElement, survey, id, answers, originalAnswers, uniqueIDs, dependenciesForAnswers,
						dependencies, shortnamesForAnswers, correctForAnswers, pointsForAnswers, feedbackForAnswers, exclusiveForAnswers,
						servletContext, log220);
			}
		} else if (type.equalsIgnoreCase("rankingquestion")) {
			List<String> rankingItemTitles = new ArrayList<>();
			List<String> rankinItemOriginalTitles = new ArrayList<>();
			List<String> rankingItemShortNames = new ArrayList<>();
			List<String> rankingItemUids = new ArrayList<>();
			parseParameterMapForRankingitems(parameterMap, id, rankingItemTitles, rankinItemOriginalTitles, rankingItemShortNames, rankingItemUids);
			element = getRankingQuestion(parameterMap, currentElement, id, rankingItemTitles, rankinItemOriginalTitles, rankingItemShortNames, rankingItemUids, servletContext, log220 && currentElement != null);
		} else if (type.equalsIgnoreCase("complextable")) {
			element = getComplexTable(parameterMap, currentElement, survey, id, servletContext, log220, elementsById);			
		}

		if (element instanceof Question){
			String[] rowsLocked = parameterMap.getOrDefault("editorRowsLocked" + id, new String[]{"false"});
			String[] columnsLocked = parameterMap.getOrDefault("editorColumnsLocked" + id, new String[]{"false"});
			element.setEditorRowsLocked(BooleanUtils.toBooleanObject(rowsLocked[0]));
			element.setEditorColumnsLocked(BooleanUtils.toBooleanObject(columnsLocked[0]));
		}

		if (survey.getIsQuiz() && element instanceof Question) {
			String[] scoringitems = parameterMap.get("scoringitem" + id);
			if (scoringitems == null) {
				scoringitems = new String[0];
			}

			List<ScoringItem> deletedScoringItems = new ArrayList<>();
			List<String> scoringitemslist = Arrays.asList(scoringitems);

			Question question = (Question) element;

			if (question.getScoringItems() != null)
				for (ScoringItem item : question.getScoringItems()) {
					if (!scoringitemslist.contains(item.getId().toString())) {
						deletedScoringItems.add(item);
					}
				}

			for (ScoringItem itemToDelete : deletedScoringItems) {
				((Question) element).getScoringItems().remove(itemToDelete);
			}

			for (int i = 0; i < scoringitems.length; i++) {
				ScoringItem item = getScoringItem(parameterMap, question, scoringitems[i], servletContext);
				item.setPosition(i);
			}
		}

		return element;
	}
	
	private static String[] getAnswers(Map<String, String[]> parameterMap, String id) {
		String[] answers = parameterMap.get(Constants.ANSWER + id);
		if (answers == null) {
			answers = new String[0];
		}

		List<String> list = new ArrayList<>(Arrays.asList(answers));
		list.removeAll(Arrays.asList("", null));
		answers = list.toArray(new String[0]);

		for (int i = 0; i < answers.length; i++) {
			if (answers[i] != null)
				answers[i] = Tools.filterHTML(answers[i]);
		}
		
		return answers;
	}
	
	private static String[] getOriginalAnswers(Map<String, String[]> parameterMap, String id) {
		String[] originalAnswers = parameterMap.get("originalAnswer" + id);
	
		if (originalAnswers == null) {
			originalAnswers = new String[0];
		}
		
		List<String> list = new ArrayList<>(Arrays.asList(originalAnswers));
		list.removeAll(Arrays.asList("", null));
		originalAnswers = list.toArray(new String[0]);
		
		for (int i = 0; i < originalAnswers.length; i++) {
			if (originalAnswers[i] != null) {
				originalAnswers[i] = Tools.filterHTML(originalAnswers[i]);
			}
		}

		return originalAnswers;
	}
	
	private static ScoringItem getScoringItem(Map<String, String[]> parameterMap, Question element, String id,
			ServletContext servletContext) throws InvalidXHTMLException {
		ScoringItem scoringItem = null;
		if (element.getScoringItems() != null)
			for (ScoringItem item : element.getScoringItems()) {
				if (item.getId() != null && item.getId().toString().equals(id)) {
					scoringItem = item;
					break;
				}
			}

		if (scoringItem == null) {
			if (element.getScoringItems() == null)
				element.setScoringItems(new ArrayList<>());
			scoringItem = new ScoringItem();
			element.getScoringItems().add(scoringItem);
		}

		boolean correct = getBoolean(parameterMap, "correct", id);
		scoringItem.setCorrect(correct);

		int type = getInteger(parameterMap, "type", id);
		scoringItem.setType(type);

		String value = getString(parameterMap, "value", id, servletContext);
		scoringItem.setValue(value);

		String value2 = getString(parameterMap, "value2", id, servletContext);
		scoringItem.setValue2(value2);

		String feedback = getString(parameterMap, "feedback", id, servletContext);
		scoringItem.setFeedback(feedback);

		Double min = getDouble(parameterMap, "min", id);
		scoringItem.setMin(min);

		Double max = getDouble(parameterMap, "max", id);
		scoringItem.setMax(max);

		Date minDate = getDate(parameterMap, "minDate", id);
		scoringItem.setMinDate(minDate);

		Date maxDate = getDate(parameterMap, "maxDate", id);
		scoringItem.setMaxDate(maxDate);

		int points = getInteger(parameterMap, "points", id);
		scoringItem.setPoints(points);

		return scoringItem;
	}

	private static String[] getUniqueIDs(Map<String, String[]> parameterMap, String id) {
		String[] uniqueIDs = parameterMap.get("pauid" + id);

		if (uniqueIDs == null) {
			uniqueIDs = new String[0];
		}

		List<String> list = new ArrayList<>(Arrays.asList(uniqueIDs));
		list.removeAll(Arrays.asList("", null));
		uniqueIDs = list.toArray(new String[0]);

		for (int i = 0; i < uniqueIDs.length; i++) {
			if (uniqueIDs[i] != null) {
				uniqueIDs[i] = Tools.filterHTML(uniqueIDs[i]);
			}
		}

		return uniqueIDs;
	}

	public static Survey parseSurvey(HttpServletRequest request, SurveyService surveyService,
			FileService fileService, ServletContext servletContext, boolean log217, boolean log220,
			Map<String, Integer> fileIDsByUID) throws InvalidXHTMLException {
		Map<Integer, String[]> activitiesToLog = new HashMap<>();
		Map<String, String[]> parameterMap = Ucs2Utf8.requestToHashMap(request);

		String surveyId = parameterMap.get("survey.id")[0];

		Survey survey = surveyService.getSurvey(Integer.parseInt(surveyId), false, false);

		String oldOrder = "";
		if (log217) {
			oldOrder = survey.serialize(true);
		}

		Map<Integer, Element> elementsById = new HashMap<>();
		List<Element> oldElements = new ArrayList<>();
		for (Element element : survey.getElementsRecursive(true)) {
			elementsById.put(element.getId(), element);
			oldElements.add(element);
		}

		String idsAsString = parameterMap.get("elements")[0];
		String[] ids = idsAsString.split(";");

		Element currentElement = null;

		HashMap<PossibleAnswer, String> dependencies = new HashMap<>();
		HashMap<Matrix, HashMap<Integer, String>> matrixDependencies = new HashMap<>();

		for (Element element : oldElements) {
			if (!idsAsString.contains(element.getId().toString())) {
				survey.getElements().remove(element);
				if (!(element instanceof EmptyElement || element instanceof PossibleAnswer)) {
					surveyService.removeDependencies(element);
				}
				if (!(element instanceof PossibleAnswer)) {
					String[] oldnew = { element.getUniqueId(), null };
					activitiesToLog.put(ActivityRegistry.ID_ELEMENT_REMOVED, oldnew);
				}
			}
		}

		if (idsAsString.trim().length() > 0)
		{
			for (int i = 0; i < ids.length; i++) {
				String id = ids[i];
				if (!id.equalsIgnoreCase("undefined")) {
					Element element = parseElement(request, fileService, id, survey, elementsById,
							dependencies, matrixDependencies, servletContext, log220, fileIDsByUID);
					if (element != null) {
						if (element.getId() == null) {
							// this is a new element
							String[] oldnew = { null, element.getUniqueId() };
							activitiesToLog.put(ActivityRegistry.ID_ELEMENT_ADDED, oldnew);
						} else if (log220 && element.getActivitiesToLog().size() > 0) {
							activitiesToLog.putAll(element.getActivitiesToLog());
						}

						if (!id.contains("-") && elementsById.containsKey(Integer.parseInt(id))) {
							currentElement = elementsById.get(Integer.parseInt(id));

							if (!currentElement.getType().equalsIgnoreCase(element.getType())) {
								// this is for example a MultipleChoiceQuestion that was turned into a
								// SingleChoiceQuestion
								survey.getElements().remove(currentElement);
								surveyService.removeDependencies(currentElement);
								currentElement = null;
							}

						} else {
							currentElement = null;
						}

						element.setOldId(id);
						element.setPosition(i);
						if (currentElement == null) {
							if (i <= survey.getElements().size()) {
								survey.getElements().add(i, element);
							} else {
								survey.getElements().add(element);
							}
						}
					}
				}
			}
			
			survey.reorderElementsByPosition();
		}
		
		survey.resetElementsRecursive();

		// post processing for dependencies
		for (Entry<PossibleAnswer, String> entry : dependencies.entrySet()) {
			String[] questionIDs = entry.getValue().split(";");

			for (String questionID : questionIDs) {
				for (Element element : survey.getElementsRecursive()) {
					if ((element.getId() != null && element.getId().toString().equalsIgnoreCase(questionID))
							|| (element.getOldId() != null && element.getOldId().equalsIgnoreCase(questionID))) {
						// add the question to the answer's dependent elements
						entry.getKey().getDependentElements().getDependentElements().add(element);
						break;
					}

				}
			}
		}
		for (Matrix m : matrixDependencies.keySet()) {

			m.getDependentElements().clear();

			for (Integer k : matrixDependencies.get(m).keySet()) {
				String questionIDsString = matrixDependencies.get(m).get(k);

				if (questionIDsString != null && questionIDsString.length() > 0) {
					DependencyItem dep = new DependencyItem();
					dep.setPosition(k);

					String[] questionIDs = questionIDsString.split(";");

					for (String questionID : questionIDs) {
						for (Element element : survey.getElementsRecursive()) {
							if ((element.getId() != null && element.getId().toString().equalsIgnoreCase(questionID))
									|| (element.getOldId() != null
											&& element.getOldId().equalsIgnoreCase(questionID))) {
								// add the question to the answer's dependent elements
								dep.getDependentElements().add(element);
								break;
							}

						}
					}

					m.getDependentElements().add(dep);
				}
			}
		}

		// fix for old surveys having elements without uids
		for (Element elem : survey.getElementsRecursive(true)) {
			if (elem.getUniqueId() == null || elem.getUniqueId().trim().length() == 0) {
				String newUniqueId = UUID.randomUUID().toString();
				elem.setUniqueId(newUniqueId);
			}
		}
		
		if (request.getParameter("criticalComplexity") != null) {
			survey.setCriticalComplexity(request.getParameter("criticalComplexity").toString().equalsIgnoreCase("true"));
		}

		if (log217) {
			String newOrder = survey.serialize(true);
			if (!oldOrder.equalsIgnoreCase(newOrder)) {
				String[] oldnew = { oldOrder, newOrder };
				activitiesToLog.put(ActivityRegistry.ID_ELEMENT_ORDER, oldnew);
			}
		}

		survey.setActivitiesToLog(activitiesToLog);

		return survey;
	}

	public static void replace(Survey survey, String search, String replace) {

		survey.setTitle(survey.getTitle().replace(search, replace));

		if (survey.getIntroduction() != null)
			survey.setIntroduction(survey.getIntroduction().replace(search, replace));

		// TODO: what about shortname?

		for (Element element : survey.getElements()) {

			if (element.getLocked())
				continue;

			element.setTitle(element.getTitle().replace(search, replace));

			if (element instanceof Question) {
				Question question = (Question) element;
				if (question.getHelp() != null && question.getHelp().trim().length() > 0) {
					question.setHelp(question.getHelp().replace(search, replace));
				}

			}

			if (element instanceof ChoiceQuestion) {
				ChoiceQuestion choice = (ChoiceQuestion) element;
				for (PossibleAnswer answer : choice.getPossibleAnswers()) {
					answer.setTitle(answer.getTitle().replace(search, replace));
				}
			}

			if (element instanceof Matrix) {
				Matrix matrix = (Matrix) element;

				for (Element child : matrix.getChildElements()) {
					if (child instanceof Text) {
						child.setTitle(child.getTitle().replace(search, replace));
					}
				}
			}

			if (element instanceof Table) {
				Table table = (Table) element;
				for (Element child : table.getChildElements()) {
					if (child instanceof Text) {
						child.setTitle(child.getTitle().replace(search, replace));
					}
				}
			}
		}

	}

	private static String getString(Map<String, String[]> parameterMap, String name, String id,
			ServletContext servletContext) throws InvalidXHTMLException {
		return getString(parameterMap, name, id, servletContext, false);
	}

	private static String getString(Map<String, String[]> parameterMap, String name, String id,
			ServletContext servletContext, boolean escape) throws InvalidXHTMLException {
		String result = null;

		try {
			if (parameterMap.get(name + id) != null) {
				result = parameterMap.get(name + id)[0];

				if (escape) {
					result = result.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
				}

				result = Tools.filterHTML(result);
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return null;
		}

		if (!name.equalsIgnoreCase("unit") && !name.equalsIgnoreCase("regex")) {
			if (result != null && !XHTMLValidator.validate(result, servletContext, null)) {
				throw new InvalidXHTMLException(result, result);
			}
		}

		return result;
	}

	private static String[] getStrings(Map<String, String[]> parameterMap, String name, String id,
			ServletContext servletContext) throws InvalidXHTMLException {
		String[] result = null;
		try {
			if (parameterMap.get(name + id) != null) {
				result = parameterMap.get(name + id);
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return null;
		}

		if (result != null) {
			for (String string : result) {
				if (!XHTMLValidator.validate(string, servletContext, null)) {
					throw new InvalidXHTMLException(result, "Invalid XHTML found: " + Arrays.toString(result));
				}
			}
		}

		return result;
	}

	private static Double getDouble(Map<String, String[]> parameterMap, String name, String id) {
		try {
			if (parameterMap.get(name + id) != null) {
				String value = parameterMap.get(name + id)[0];
				if (value.length() > 0)
					return Double.parseDouble(value);
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;
	}

	private static Date getDate(Map<String, String[]> parameterMap, String name, String id) {
		try {
			if (parameterMap.get(name + id) != null) {
				String value = parameterMap.get(name + id)[0];
				if (value.length() > 0)
					return ConversionTools.getDate(value);
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;
	}

	private static int getInteger(Map<String, String[]> parameterMap, String name, String id) {
		return getInteger(parameterMap, name, id, 0);
	}

	private static int getInteger(Map<String, String[]> parameterMap, String name, String id, int defaultvalue) {
		try {
			if (parameterMap.get(name + id) != null) {
				String value = parameterMap.get(name + id)[0];
				if (value.length() > 0)
					return Integer.parseInt(value);
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return defaultvalue;
	}

	private static boolean getBoolean(Map<String, String[]> parameterMap, String name, String id) {
		try {
			if (parameterMap.get(name + id) != null) {
				String value = parameterMap.get(name + id)[0];
				if (value.length() > 0)
					return Boolean.parseBoolean(value);
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return false;
	}

	public static void synchronizeSurvey(Survey survey, String languageCode, TranslationService translationService,
			Language language, boolean setSurvey) {
		if (language != null) {
			survey.setLanguage(language);
		}

		for (Element element : survey.getElementsRecursive()) {
			element.setSurvey(setSurvey ? survey : null);
		}

		Translations translationsItem = translationService.getTranslations(survey.getId(), languageCode);
		if (translationsItem != null) {
			Map<String, Translation> translationsByKey = translationsItem.getTranslationsByKey();

			if (translationsByKey.get(Survey.TITLE) != null)
				survey.setTitle(translationsByKey.get(Survey.TITLE).getLabel());
			if (translationsByKey.get(Survey.LOGOTEXT) != null)
				survey.setLogoText(translationsByKey.get(Survey.LOGOTEXT).getLabel());
			if (translationsByKey.get(Survey.INTRODUCTION) != null)
				survey.setIntroduction(translationsByKey.get(Survey.INTRODUCTION).getLabel());
			if (translationsByKey.get(Survey.ESCAPEPAGE) != null)
				survey.setEscapePage(translationsByKey.get(Survey.ESCAPEPAGE).getLabel());
			if (translationsByKey.get(Survey.ESCAPELINK) != null)
				survey.setEscapeLink(translationsByKey.get(Survey.ESCAPELINK).getLabel());
			if (translationsByKey.get(Survey.CONFIRMATIONPAGE) != null)
				survey.setConfirmationPage(translationsByKey.get(Survey.CONFIRMATIONPAGE).getLabel());
			if (translationsByKey.get(Survey.CONFIRMATIONLINK) != null)
				survey.setConfirmationLink(translationsByKey.get(Survey.CONFIRMATIONLINK).getLabel());
			if (translationsByKey.get(Survey.QUIZWELCOMEMESSAGE) != null)
				survey.setQuizWelcomeMessage(translationsByKey.get(Survey.QUIZWELCOMEMESSAGE).getLabel());
			if (translationsByKey.get(Survey.QUIZRESULTSMESSAGE) != null)
				survey.setQuizResultsMessage(translationsByKey.get(Survey.QUIZRESULTSMESSAGE).getLabel());

			Set<String> linkstodelete = new HashSet<>();
			Map<String, String> changes = new HashMap<>();
			for (String key : survey.getUsefulLinks().keySet()) {
				String[] data = key.split("#");
				String newkey = data[0] + "#usefullink";

				if (translationsByKey.get(newkey) != null && translationsByKey.get(newkey).getLabel().length() > 0
						&& !(data[0] + "#" + translationsByKey.get(newkey).getLabel()).equals(key)) {
					changes.put(data[0] + "#" + translationsByKey.get(newkey).getLabel(),
							survey.getUsefulLinks().get(key));
					linkstodelete.add(key);
				}
			}
			for (Entry<String, String> entry : changes.entrySet()) {
				survey.getUsefulLinks().put(entry.getKey(), entry.getValue());
			}
			for (String key : linkstodelete) {
				survey.getUsefulLinks().remove(key);
			}

			Set<String> backdocstodelete = new HashSet<>();
			changes = new HashMap<>();
			for (String filename : survey.getBackgroundDocuments().keySet()) {
				String url = survey.getBackgroundDocuments().get(filename);
				String uid = BasicService.getFileUIDFromUrl(url);
				String newkey = uid + "#backgrounddocument";

				if (translationsByKey.get(newkey) != null && translationsByKey.get(newkey).getLabel().length() > 0
						&& !translationsByKey.get(newkey).getLabel().equals(filename)) {
					changes.put(translationsByKey.get(newkey).getLabel(), url);
					backdocstodelete.add(filename);
				}
			}
			for (Entry<String, String> entry : changes.entrySet()) {
				survey.getBackgroundDocuments().put(entry.getKey(), entry.getValue());
			}
			for (String key : backdocstodelete) {
				survey.getBackgroundDocuments().remove(key);
			}

			for (Element element : survey.getElementsRecursive()) {
				if (translationsByKey.get(element.getUniqueId()) != null) {
					element.setTitle(translationsByKey.get(element.getUniqueId()).getLabel());
				} else if (translationsByKey.get(element.getId().toString()) != null) {
					element.setTitle(translationsByKey.get(element.getId().toString()).getLabel());
				}

				if (element instanceof Section) {
					Section section = (Section) element;
					if (translationsByKey.get(element.getUniqueId() + Section.TABTITLE) != null) {
						section.setTabTitle(translationsByKey.get(element.getUniqueId() + Section.TABTITLE).getLabel());
					} else if (translationsByKey.get(element.getId().toString() + Section.TABTITLE) != null) {
						section.setTabTitle(
								translationsByKey.get(element.getId().toString() + Section.TABTITLE).getLabel());
					}
				}

				if (element instanceof NumberQuestion) {
					NumberQuestion number = (NumberQuestion) element;
					if (translationsByKey.get(number.getUniqueId() + NumberQuestion.UNIT) != null) {
						number.setUnit(translationsByKey.get(number.getUniqueId() + NumberQuestion.UNIT).getLabel());
					} else if (translationsByKey.get(number.getId().toString() + NumberQuestion.UNIT) != null) {
						number.setUnit(
								translationsByKey.get(number.getId().toString() + NumberQuestion.UNIT).getLabel());
					} else {
						number.setUnit("");
					}

					if (number.isSlider()) {
						if (translationsByKey.get(number.getUniqueId() + NumberQuestion.MINLABEL) != null) {
							number.setMinLabel(translationsByKey.get(number.getUniqueId() + NumberQuestion.MINLABEL).getLabel());
						} else if (translationsByKey.get(number.getId().toString() + NumberQuestion.MINLABEL) != null) {
							number.setMinLabel(translationsByKey.get(number.getId().toString() + NumberQuestion.MINLABEL).getLabel());
						} else {
							number.setMinLabel("");
						}

						if (translationsByKey.get(number.getUniqueId() + NumberQuestion.MAXLABEL) != null) {
							number.setMaxLabel(translationsByKey.get(number.getUniqueId() + NumberQuestion.MAXLABEL).getLabel());
						} else if (translationsByKey.get(number.getId().toString() + NumberQuestion.MAXLABEL) != null) {
							number.setMaxLabel(translationsByKey.get(number.getId().toString() + NumberQuestion.MAXLABEL).getLabel());
						} else {
							number.setMaxLabel("");
						}
					}
				}

				if (element instanceof Confirmation) {
					Confirmation confirmation = (Confirmation) element;
					if (translationsByKey.get(confirmation.getUniqueId() + Confirmation.LABEL) != null) {
						confirmation.setConfirmationlabel(
								translationsByKey.get(confirmation.getUniqueId() + Confirmation.LABEL).getLabel());
					}
					if (translationsByKey.get(confirmation.getUniqueId() + Confirmation.TEXT) != null) {
						confirmation.setConfirmationtext(
								translationsByKey.get(confirmation.getUniqueId() + Confirmation.TEXT).getLabel());
					}
				}

				if (element instanceof ChoiceQuestion) {
					ChoiceQuestion choice = (ChoiceQuestion) element;
					for (PossibleAnswer answer : choice.getPossibleAnswers()) {
						if (translationsByKey.get(answer.getUniqueId()) != null) {
							answer.setTitle(translationsByKey.get(answer.getUniqueId()).getLabel());
						} else if (translationsByKey.get(answer.getId().toString()) != null) {
							answer.setTitle(translationsByKey.get(answer.getId().toString()).getLabel());
						}

						if (translationsByKey.get(answer.getUniqueId() + Question.FEEDBACK) != null) {
							answer.getScoring().setFeedback(
									translationsByKey.get(answer.getUniqueId() + Question.FEEDBACK).getLabel());
						}
					}
				}

				if (element instanceof Question) {
					Question question = (Question) element;

					if (translationsByKey.get(question.getUniqueId() + "help") != null) {
						question.setHelp(translationsByKey.get(question.getUniqueId() + "help").getLabel());
					} else if (translationsByKey.get(question.getId().toString() + "help") != null) {
						question.setHelp(translationsByKey.get(question.getId().toString() + "help").getLabel());
					} else {
						question.setHelp("");
					}

					if (survey.getIsQuiz() && question.getScoring() != 0 && question.getScoringItems() != null) {
						for (ScoringItem scoringItem : question.getScoringItems()) {
							if (translationsByKey.get(scoringItem.getUniqueId() + Question.FEEDBACK) != null) {
								scoringItem.setFeedback(translationsByKey
										.get(scoringItem.getUniqueId() + Question.FEEDBACK).getLabel());
							}
						}
					}
				}

				if (element instanceof MatrixOrTable) {
					MatrixOrTable matrix = (MatrixOrTable) element;

					if (translationsByKey.get(matrix.getUniqueId() + MatrixOrTable.FIRSTCELL) != null) {
						matrix.setFirstCellText(
								translationsByKey.get(matrix.getUniqueId() + MatrixOrTable.FIRSTCELL).getLabel());
					}

					for (Element child : matrix.getChildElements()) {
						if (translationsByKey.get(child.getUniqueId()) != null) {
							child.setTitle(translationsByKey.get(child.getUniqueId()).getLabel());
						} else if (translationsByKey.get(child.getId().toString()) != null) {
							child.setTitle(translationsByKey.get(child.getId().toString()).getLabel());
						}
					}
				}

				if (element instanceof RatingQuestion) {
					RatingQuestion rating = (RatingQuestion) element;
					for (Element child : rating.getChildElements()) {
						if (translationsByKey.get(child.getUniqueId()) != null) {
							child.setTitle(translationsByKey.get(child.getUniqueId()).getLabel());
						} else if (translationsByKey.get(child.getId().toString()) != null) {
							child.setTitle(translationsByKey.get(child.getId().toString()).getLabel());
						}
					}
				}

				if (element instanceof RankingQuestion) {
					RankingQuestion ranking = (RankingQuestion) element;
					for (Element child : ranking.getChildElements()) {
						if (translationsByKey.get(child.getUniqueId()) != null) {
							child.setTitle(translationsByKey.get(child.getUniqueId()).getLabel());
						} else if (translationsByKey.get(child.getId().toString()) != null) {
							child.setTitle(translationsByKey.get(child.getId().toString()).getLabel());
						}
					}
				}

				if (element instanceof GalleryQuestion) {
					GalleryQuestion gallery = (GalleryQuestion) element;
					for (com.ec.survey.model.survey.base.File child : gallery.getFiles()) {
						if (translationsByKey.get(child.getUid() + GalleryQuestion.TEXT) != null) {
							child.setComment(translationsByKey.get(child.getUid() + GalleryQuestion.TEXT).getLabel());
						}
						if (translationsByKey.get(child.getUid()) != null) {
							child.setDescription(translationsByKey.get(child.getUid()).getLabel());
						}
						if (translationsByKey.get(child.getUid() + GalleryQuestion.TITLE) != null) {
							child.setName(translationsByKey.get(child.getUid() + GalleryQuestion.TITLE).getLabel());
						}
					}
				}

				if (element instanceof ComplexTable) {
					ComplexTable table = (ComplexTable) element;

					for (ComplexTableItem child : table.getChildElements()) {

						if (translationsByKey.get(child.getUniqueId()) != null)
							child.setTitle(translationsByKey.get(child.getUniqueId()).getLabel());

						if (translationsByKey.get(child.getUniqueId() + "RESULTTEXT") != null)
							child.setResultText(translationsByKey.get(child.getUniqueId() + "RESULTTEXT").getLabel());

						if (translationsByKey.get(child.getUniqueId() + "UNIT") != null)
							child.setUnit(translationsByKey.get(child.getUniqueId() + "UNIT").getLabel());

						if (child.isChoice()){
							for (PossibleAnswer possibleAnswer : child.getPossibleAnswers()){
								if (translationsByKey.get(possibleAnswer.getUniqueId()) != null)
									possibleAnswer.setTitle(translationsByKey.get(possibleAnswer.getUniqueId()).getLabel());
							}
						}
					}

				}
			}
		}
	}

	public static Survey createTranslatedSurvey(int surveyId, String lang, SurveyService surveyService,
			TranslationService translationService, boolean setSurvey) {

		Survey survey = surveyService.getSurveyReadOnly(surveyId, false, setSurvey);

		if (survey != null) {
			if (lang.equalsIgnoreCase(survey.getLanguage().getCode()))
				return survey;

			Translations translations = translationService.getTranslations(survey.getId(), lang);

			if (translations == null)
				return survey;

			synchronizeSurvey(survey, lang, translationService, null, setSurvey);
		}

		return survey;
	}

	public static void calcTableWidths(Survey survey, Form f) {

		List<String> ids = new ArrayList<>();
		List<String> widths = new ArrayList<>();

		BaseFont bf = null;
		try {
			bf = BaseFont.createFont("/Fonts/FreeSans.ttf", BaseFont.IDENTITY_H, true);
		} catch (Exception e) {
			// ignore
		}

		for (Element element : survey.getElementsRecursive()) {
			if (bf != null && (element instanceof MatrixOrTable || element instanceof GalleryQuestion || element instanceof ComplexTable)) {

				boolean isTable = element instanceof Table;

				float w = 0.0f;
				boolean done = false;
				double wtext;
				double wimage;

				if (element instanceof ComplexTable) {
					w = (((ComplexTable) element).getColumns()) * 200;
					done = true;
				}
				
				if (!done && element instanceof GalleryQuestion) {
					GalleryQuestion gallery = (GalleryQuestion) element;
					double imageWidth = (600 - 20 - (gallery.getColumns() * 30)) / gallery.getColumns();

					int col = 0;

					double[] galWidths = new double[gallery.getColumns()];
					for (int i = 0; i < galWidths.length; i++) {
						galWidths[i] = imageWidth;
					}

					for (File file : gallery.getFiles()) {
						String name = getLongestWord(file.getName());
						String comment = getLongestWord(file.getComment());
						if (name != null) {
							double wcand = bf.getWidthPoint(name, 10) + 2 * 9.0 + 1.0;
							if (wcand > galWidths[col]) {
								galWidths[col] = wcand;
							}
						}
						if (comment != null) {
							double wcand = bf.getWidthPoint(comment, 10) + 2 * 9.0 + 1.0;
							if (wcand > galWidths[col]) {
								galWidths[col] = wcand;
							}
						}

						col++;
						if (col >= gallery.getColumns()) {
							col = 0;
						}
					}

					for (double galWidth : galWidths) {
						w += galWidth;
					}

					done = true;
				}

				if (!done && isTable) {
					try {
						String width = ((Table) element).getCompleteWidth();
						if (!width.equalsIgnoreCase("auto") && width.endsWith("px")) {
							w = Integer.parseInt(width.replace("px", ""));
							done = true;
						}
					} catch (Exception e) {
						// ignore
					}
				}

				if (!done) {

					MatrixOrTable matrix = (MatrixOrTable) element;
					List<Element> lst = matrix.getChildElements();
					int i;
					for (i = 0; i < matrix.getColumns(); i++) {
						Element child = lst.get(i);
						if (child.getUniqueId() != null || child.getId().toString() != null) {
							String str = child.getStrippedTitleNoEscape2();
							str = getLongestWord(str);
							wtext = 0;
							if (str != null) {
								wtext += bf.getWidthPoint(str, 10) + 2 * 9.0/*
																			 * matrixtable.td.padding-left +
																			 * matrixtable.td.padding-right
																			 */
										+ 1.0 /* border *//* cf. [runner.jsp] */; // FIXME: Remove hardcoding
																					// constants from runner.jsp
							}
							if (!isTable) {
								wimage = getBiggestImageWidth(child.getTitle());
								if (wimage > wtext) {
									wtext = wimage;
								}
							}

							w += wtext;

						}
					}

					double w2 = 0;
					for (i = matrix.getColumns(); i < matrix.getColumns() + matrix.getRows() - 1; i++) {
						Element child = lst.get(i);
						if (child.getUniqueId() != null || child.getId().toString() != null) {
							String str = child.getStrippedTitleNoEscape2();
							str = getLongestWord(str);
							double cw = 0;
							if (str != null) {
								cw = bf.getWidthPoint(str, 10) + (float) (2 * 9.0)/*
																					 * matrixtable.td.padding-left +
																					 * matrixtable.td.padding-right
																					 */
										+ 1.0f /* border *//* cf. [runner.jsp] */; // FIXME: Remove hardcoding
																					// constants from runner.jsp
							}
							if (!isTable) {
								wimage = getBiggestImageWidth(child.getTitle());
								if (wimage > cw) {
									cw = wimage;
								}
							}

							if (cw > w2)
								w2 = cw;

						}
					}
					w += w2;
				}

				// px to pt
				w = (float) (w * 1.333333);

				element.setPDFWidth(w);
				if (w > 595.0) { // NOTE: A4 = 595 pt x 842 pt ; Letter = 612 pt

					if (w <= 842) {
						w = 842;
						element.setPDFWidth(w);
					}

					element.setHasPDFWidth(true);
					ids.add(element.getId().toString());
					widths.add(Float.toString(w));
				}
			}

		}

		if (ids.size() > 0) {
			f.setVarPageElts(ids, widths);
		}
	}

	private static double getBiggestImageWidth(String html) {

		if (html == null)
			return 0;

		double w = 0;

		try {

			while (html.contains("<img")) {
				// strip anything before the image
				html = html.substring(html.indexOf("<img"));
				String imagetag = html.substring(0, html.indexOf('>'));

				if (imagetag.contains("width=")) {
					String width = imagetag.substring(imagetag.indexOf("width=") + 7);
					width = width.substring(0, width.indexOf('\"'));
					double dwidth = Double.parseDouble(width);
					if (dwidth > w) {
						w = dwidth;
					}
				}

				html = html.substring(html.indexOf('>') + 1);
			}

		} catch (Exception e) {
			// ignore
		}

		return w;
	}

	public static String getLongestWord(final String str) {

		if (str == null)
			return str;

		String stringArray[] = str.replace('\u00A0', ' ').split("\\s");
		String word = "";
		for (int i = 0; i < stringArray.length; i++) {
			if (i == 0) {
				word = stringArray[0];
			}
			word = compare(word, stringArray[i]);
		}
		return word;
	}

	public static String compare(String st1, String st2) {
		if (st1.length() > st2.length()) {
			return st1;
		} else {
			return st2;
		}
	}

	public static boolean isDeactivatedOrEndDateExceeded(Survey survey, SurveyService surveyService)
			throws FrozenSurveyException {
		boolean bln = false;

		if (!survey.getIsDraft()) {
			Survey draft = surveyService.getSurveyByUniqueId(survey.getUniqueId(), false, true);
			if (!draft.getIsActive())
				return true;

			if (draft.getIsFrozen()) {
				throw new FrozenSurveyException();
			}
		}

		if (survey.getEnd() != null && survey.getAutomaticPublishing()) {
			Calendar now = Calendar.getInstance();
			now.setTime(new Date());
			Calendar endDate = Calendar.getInstance();
			endDate.setTime(survey.getEnd());
			bln = now.after(endDate);
		}
		return bln;
	}

	public static boolean isMaxContributionReached(Survey survey, AnswerService answerService) {
		if (survey.getIsUseMaxNumberContribution()) {
			return survey.getMaxNumberContribution() <= answerService.getNumberOfAnswerSetsPublished(null,
					survey.getUniqueId());
		}
		return false;
	}

	public static String getAnswerTitle(Survey survey, Answer answer, boolean publicationMode) {
		String answerValue = answer.getValue();

		try {

			if (survey != null) {

				Element question = null;

				Map<String, Question> questionMap = survey.getQuestionMapByUniqueId();

				if (questionMap != null) {
					question = questionMap.get(answer.getQuestionUniqueId());
				}

				if (question == null) {
					question = survey.getMatrixMapByUid().get(answer.getQuestionUniqueId());
				}

				if (question == null) {
					question = survey.getMissingElementsByUniqueId().get(answer.getQuestionUniqueId());
				}

				if (question == null && answer.getQuestionUniqueId() != null
						&& answer.getQuestionUniqueId().length() > 0) {
					question = survey.getMissingElementsByUniqueId().get(answer.getQuestionUniqueId());
				}

				if (question == null && answer.getPossibleAnswerUniqueId() != null && answer.getPossibleAnswerUniqueId().length() > 0) {
					int possibleAnswerId = Integer.parseInt(answerValue);
					if (survey.getMissingElementsById().containsKey(possibleAnswerId)) {
						return survey.getMissingElementsById().get(possibleAnswerId).getStrippedTitleNoEscape2();
					}

					if (answer.getPossibleAnswerUniqueId() != null && answer.getPossibleAnswerUniqueId().length() > 0
							&& survey.getElementsByUniqueId().containsKey(answer.getPossibleAnswerUniqueId())) {
						return survey.getElementsByUniqueId().get(answer.getPossibleAnswerUniqueId())
								.getStrippedTitleNoEscape2();
					}

					if (answer.getPossibleAnswerUniqueId() != null && answer.getPossibleAnswerUniqueId().length() > 0
							&& survey.getMissingElementsByUniqueId().containsKey(answer.getPossibleAnswerUniqueId())) {
						return survey.getMissingElementsByUniqueId().get(answer.getPossibleAnswerUniqueId())
								.getStrippedTitleNoEscape2();
					}
				}

				if (question instanceof FreeTextQuestion) {
					if (publicationMode && answerValue != null && answerValue.length() > 0) {
						FreeTextQuestion q = (FreeTextQuestion) question;
						if (q.getIsPassword()) {
							return "********";
						}
					}
					return answerValue;
				} else if (question instanceof RankingQuestion) {
					RankingQuestion rankingQuestion = (RankingQuestion) question;
					List<String> answerTitles = rankingQuestion.getAnswerWithStrippedTitleNoEscape(answerValue);
					return String.join("; ", answerTitles);
				} else if (question instanceof ChoiceQuestion) {
					int possibleAnswerId = Integer.parseInt(answerValue);
					ChoiceQuestion choicequestion = (ChoiceQuestion) question;
					if (choicequestion.getPossibleAnswer(possibleAnswerId) != null) {
						return choicequestion.getPossibleAnswer(possibleAnswerId).getStrippedTitleNoEscape2();
					} else {
						if (survey.getMissingElementsById().containsKey(possibleAnswerId)) {
							return survey.getMissingElementsById().get(possibleAnswerId).getStrippedTitleNoEscape2();
						}
					}

					if (choicequestion.getPossibleAnswerByUniqueId(answer.getPossibleAnswerUniqueId()) != null) {
						return choicequestion.getPossibleAnswerByUniqueId(answer.getPossibleAnswerUniqueId())
								.getStrippedTitleNoEscape2();
					}

					return "";
				} else if (question instanceof ComplexTableItem) {
					ComplexTableItem item = (ComplexTableItem) question;
					if (item.getCellType() == ComplexTableItem.CellType.SingleChoice || item.getCellType() == ComplexTableItem.CellType.MultipleChoice) {
						for (PossibleAnswer a : item.getPossibleAnswers()) {
							if (a.getUniqueId().equals(answer.getPossibleAnswerUniqueId())) {
								return a.getStrippedTitleNoEscape2();
							}
						}
					}
					
					return answerValue;
				} else if (question instanceof Text || question instanceof Image || question instanceof EmptyElement) {
					// could be inside a matrix
					int possibleAnswerId = Integer.parseInt(answerValue);
					for (Element element : survey.getElements()) {
						if (element instanceof Matrix) {
							for (Element child : ((Matrix) element).getChildElements()) {
								if (child.getId().equals(possibleAnswerId) || (child.getUniqueId() != null
										&& child.getUniqueId().equalsIgnoreCase(answer.getPossibleAnswerUniqueId()))) {
									return child.getStrippedTitleNoEscape2();
								}
							}
							for (Element child : ((Matrix) element).getMissingAnswers()) {
								if (child.getId().equals(possibleAnswerId)) {
									return child.getStrippedTitleNoEscape2();
								}
							}
						}
					}
					for (Element element : survey.getMissingElements()) {
						if (element instanceof Matrix) {
							for (Element child : ((Matrix) element).getChildElements()) {
								if (child.getId().equals(possibleAnswerId)) {
									return child.getStrippedTitleNoEscape2();
								}
							}
							for (Element child : ((Matrix) element).getMissingAnswers()) {
								if (child.getId().equals(possibleAnswerId)) {
									return child.getStrippedTitleNoEscape2();
								}
							}
						} else if (element.getId().equals(possibleAnswerId)) {
							return element.getStrippedTitleNoEscape2();
						}
					}

					return "";
				}
			}

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return "";
		}

		return answerValue;
	}
}
