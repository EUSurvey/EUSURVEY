package com.ec.survey.controller;

import com.ec.survey.exception.TooManyFiltersException;
import com.ec.survey.model.*;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.LocalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.attendees.Invitation;
import com.ec.survey.model.delphi.*;
import com.ec.survey.model.survey.*;
import com.ec.survey.service.*;
import com.ec.survey.tools.*;
import com.ec.survey.tools.export.StatisticsCreator;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.normalize.CharacterStrippingNormalizer;
import com.kennycason.kumo.nlp.normalize.LowerCaseNormalizer;
import com.kennycason.kumo.nlp.normalize.TrimToEmptyNormalizer;

import javafx.util.Pair;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/runner")
public class DelphiController extends BasicController {

	@Resource(name = "mailService")
	private MailService mailService;
	
	private final Map<String, Map<String, String>> uniqueCodeToUser = new HashMap<>();	
	private static List<String> stopWords = null;	

	@GetMapping("/delphiGet")
	public ResponseEntity<DelphiExplanation> delphiGetExplanation(HttpServletRequest request, Locale locale) {
		try {
			final String answerSetId = request.getParameter("answerSetId");
			final int answerSetIdParsed = Integer.parseInt(answerSetId);
			final AnswerSet answerSet = answerService.get(answerSetIdParsed);
			if (answerSet == null) {
				return new ResponseEntity<>(new DelphiExplanation(resources.getMessage("error.DelphiGet", null, locale), ""), HttpStatus.BAD_REQUEST);
			}
			
			final String questionUid = request.getParameter("questionUid");
			
			Element element = answerSet.getSurvey().getQuestionMapByUniqueId().get(questionUid);

			if (!(element instanceof Question)) {
				return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
			}

			Question question = (Question) element;
			if (!answerSetContainsAnswerForQuestion(answerSet, question)) {
				return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
			}			
			
			final AnswerExplanation explanation = answerExplanationService.getExplanation(answerSetIdParsed, questionUid);
			DelphiExplanation delphiExplanation = new DelphiExplanation();
			delphiExplanation.setText(explanation.getText());
			delphiExplanation.setFileInfoFromFiles(explanation.getFiles());
			
			if ((element instanceof SingleChoiceQuestion || element instanceof NumberQuestion)) {
			
				DelphiMedian median = null;
				
				if (element instanceof SingleChoiceQuestion) {
					SingleChoiceQuestion singleChoiceQuestion = (SingleChoiceQuestion)element;
					if (singleChoiceQuestion.getUseLikert() && singleChoiceQuestion.getMaxDistance() >= 0) {						
						List<Answer> answers = answerSet.getAnswers(singleChoiceQuestion.getId(), singleChoiceQuestion.getUniqueId());
						if (!answers.isEmpty())
						{
							median = answerService.getMedian(answerSet.getSurvey(), singleChoiceQuestion, answers.get(0), null);							
						}
					}
				}
				
				if (element instanceof NumberQuestion) {
					NumberQuestion numberQuestion = (NumberQuestion)element;
					if (numberQuestion.isSlider() && numberQuestion.getMaxDistance() >= 0) {						
						List<Answer> answers = answerSet.getAnswers(numberQuestion.getId(), numberQuestion.getUniqueId());
						if (!answers.isEmpty())
						{
							median = answerService.getMedian(answerSet.getSurvey(), numberQuestion, answers.get(0), null);
						}
					}
				}
				
				if (median != null && median.isMaxDistanceExceeded() && explanation.getText().trim().length() > 0 && (explanation.getChangedForMedian() == null || !explanation.getChangedForMedian())) {
					String text = resources.getMessage("label.NewExplanation", null, locale) + ":<br /><br/><br />" + resources.getMessage("label.OldExplanation", null, locale) + ":<br /><br /><span style='color: #999;'>" + explanation.getText() + "</span>";
					delphiExplanation.setText(text);
				}			
				
				delphiExplanation.setChangedForMedian(explanation.getChangedForMedian() != null && explanation.getChangedForMedian());
			}			
			
			return new ResponseEntity<>(delphiExplanation, HttpStatus.OK);
		} catch (NoSuchElementException ex) {
			return new ResponseEntity<>(new DelphiExplanation("", ""), HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(new DelphiExplanation(resources.getMessage("error.DelphiGet", null, locale), ""), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/delphiUpdate")
	public ResponseEntity<DelphiUpdateResult> delphiCreateOrUpdateAnswerAndExplanation(HttpServletRequest request, Locale locale) {
		try {
					
			final String surveyId = request.getParameter("surveyId");
			final int surveyIdParsed = Integer.parseInt(surveyId);

			final String questionUid = request.getParameter("questionUid");
			final Survey survey = surveyService.getSurvey(surveyIdParsed);
			
			if (survey == null || !survey.getIsDelphi()) {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
			
			final String languageCode = request.getParameter("languageCode");
			final String answerSetUniqueCode = request.getParameter("ansSetUniqueCode");
			final String invitationId = request.getParameter("invitation");
			final User user = sessionService.getCurrentUser(request, false, false);
			
			Element element = survey.getElementsByUniqueId().get(questionUid);

			AnswerSet answerSet;
			final AnswerSet existingAnswerSet = answerService.get(answerSetUniqueCode);
			if (existingAnswerSet == null) {
				//save
				answerSet = SurveyHelper.parseAnswerSet(request, survey, answerSetUniqueCode, false, languageCode, user, fileService);
			} else {
				//update
				answerSet = SurveyHelper.parseAndMergeDelphiAnswerSet(request, survey, answerSetUniqueCode, existingAnswerSet, languageCode, user, fileService, element);
			}
			
			if (invitationId != null && invitationId.length() > 0) {
				Invitation invitation = attendeeService.getInvitation(Integer.parseInt(invitationId));

				answerSet.setInvitationId(invitation.getUniqueId());
				if (invitation.getAnswers() == 0)
				{
					invitation.setAnswers(1);
				}
			}
			
			if (survey.getEcasSecurity() && user != null) {
				answerSet.setResponderEmail(user.getEmail());
			}
			
			Set<String> invisibleElements = new HashSet<>();
			
			List<Element> elements = new ArrayList<>();
			if (element != null) {
				elements.add(element);
			}
			if (element instanceof ChoiceQuestion) {
				//use case: a choice question with a dependent "other" text box question
				for (Answer answer : answerSet.getAnswers()) {
					if (!answer.getQuestionUniqueId().equals(element.getUniqueId())) {
						Element candidate = survey.getElementsByUniqueId().get(answer.getQuestionUniqueId());
						if (candidate instanceof FreeTextQuestion) {
							elements.add(candidate);
						}
					}
				}
			} else if (element == null || element instanceof Section) {
				//use case: save all data during page change in a multi-page Delphi survey
				for (Answer answer : answerSet.getAnswers()) {
					elements.add(survey.getElementsByUniqueId().get(answer.getQuestionUniqueId()));						
				}
			}

			final Map<Element, String> validation = SurveyHelper.validateAnswerSet(answerSet, answerService,
					invisibleElements, resources, locale, null, request, true, user, fileService, elements);
			
			if (!validation.isEmpty()) {
				return new ResponseEntity<>(new DelphiUpdateResult(resources.getMessage("error.CheckValidation", null, locale)), HttpStatus.BAD_REQUEST);
			}

			saveAnswerSet(answerSet, fileDir, null, -1);
			
			DelphiUpdateResult updateResult = new DelphiUpdateResult(resources.getMessage("message.ChangesSaved", null, locale));
			updateResult.setLink(serverPrefix + "editcontribution/" + answerSet.getUniqueCode());
			if (survey.getSecurity().startsWith("open")) {
				updateResult.setOpen(true);
			}
			
			if (answerSet.getChangedForMedian()) {
				updateResult.setChangedForMedian(true);
			} else if (answerSet.getChangeExplanationText()) {
				updateResult.setChangeExplanationText(true);
			}
			
			return new ResponseEntity<>(updateResult, HttpStatus.OK);

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(new DelphiUpdateResult(resources.getMessage("error.DelphiCreateOrUpdate", null, locale)), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "delphiGraph")
	public ResponseEntity<AbstractDelphiGraphData> delphiGraph(HttpServletRequest request) {
		try {
			String surveyid = request.getParameter("surveyid");
			int sid = Integer.parseInt(surveyid);

			String languageCode = request.getParameter("languagecode");
			Survey survey = surveyService.getSurvey(sid, languageCode);

			if (survey == null || !survey.getIsDelphi()) {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}

			User user = sessionService.getCurrentUser(request);
			
			boolean resultsview = request.getParameter("resultsview") != null && request.getParameter("resultsview").equalsIgnoreCase("true"); 
			
			boolean privileged = resultsview && (survey.getOwner().getId().equals(user.getId()) ||
					(user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2) ||
					(user.getLocalPrivileges().get(LocalPrivilege.AccessResults) > 0));

			AnswerSet answerSet = answerService.get(request.getParameter("uniquecode"));
			
			if (!privileged && !survey.getIsDelphiShowAnswersAndStatisticsInstantly() && answerSet == null) {
				// participant may only see answers if he answered before
				return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
			}

			String questionuid = request.getParameter("questionuid");
			Element element = survey.getQuestionMapByUniqueId().get(questionuid);

			if (!(element instanceof Question)) {
				return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
			}

			Question question = (Question) element;
			if (!question.getIsDelphiQuestion() || (!privileged && !survey.getIsDelphiShowAnswersAndStatisticsInstantly()
					&& !answerSetContainsAnswerForQuestion(answerSet, question))) {
				return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
			}

			Statistics statistics = new Statistics();
			statistics.setSurveyId(survey.getId());

			StatisticsCreator creator = (StatisticsCreator) context.getBean("statisticsCreator");
			creator.init(survey, null, false);

			if (question instanceof NumberQuestion) {
				NumberQuestion numq = (NumberQuestion) question;
				NumberQuestionStatistics numberQuestionStats = creator.getAnswers4NumberQuestionStatistics(survey, numq);
				return handleDelphiNumberQuestion(survey, numq, numberQuestionStats);				
			}
			
			if (question instanceof FreeTextQuestion) {
				
				if (question.getDelphiChartType() == DelphiChartType.None) {
					return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
				}
				
				return handleDelphiFreetextQuestion(survey, question, creator);
			}

			Map<Integer, Integer> numberOfAnswersMap = new HashMap<>();
			Map<Integer, Map<Integer, Integer>> numberOfAnswersMapMatrix = new HashMap<>();
			Map<Integer, Map<Integer, Integer>> numberOfAnswersMapRatingQuestion = new HashMap<>();
			Map<Integer, Map<Integer, Integer>> numberOfAnswersMapGallery = new HashMap<>();
			Map<Integer, Map<String, Set<String>>> multipleChoiceSelectionsByAnswerset = new HashMap<>();
			Map<String, Integer> numberOfAnswersMapNumberQuestion = new HashMap<>();

			creator.getAnswers4Statistics(
					survey,
					question,
					numberOfAnswersMap,
					numberOfAnswersMapMatrix,
					numberOfAnswersMapGallery,
					multipleChoiceSelectionsByAnswerset,
					numberOfAnswersMapRatingQuestion,
					numberOfAnswersMapNumberQuestion);

			if (question instanceof ChoiceQuestion) {
				return handleDelphiGraphChoiceQuestion(survey, (ChoiceQuestion) question, statistics, creator, numberOfAnswersMap, multipleChoiceSelectionsByAnswerset);
			}

			if (question instanceof Matrix) {
				return handleDelphiGraphMatrix(survey, (Matrix) question, statistics, creator, numberOfAnswersMapMatrix);
			}

			if (question instanceof RatingQuestion) {
				return handleDelphiGraphRatingQuestion(survey, (RatingQuestion) question, statistics, creator, numberOfAnswersMapRatingQuestion);
			}

			return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ResponseEntity<AbstractDelphiGraphData> handleDelphiGraphRatingQuestion(Survey survey, RatingQuestion question, Statistics statistics, StatisticsCreator creator, Map<Integer, Map<Integer, Integer>> numberOfAnswersMapRatingQuestion) {
		DelphiGraphDataMulti result = new DelphiGraphDataMulti();
		result.setQuestionType(DelphiQuestionType.Rating);
		result.setChartType(question.getDelphiChartType());

		Collection<String> uids = question.getQuestions().stream().map(Element::getUniqueId).collect(Collectors.toList());
		int contributions = answerExplanationService.getTotalDelphiContributions(uids, survey.getIsDraft());

		if (contributions < survey.getMinNumberDelphiStatistics()) {
			return ResponseEntity.noContent().build();
		}

		for (Element subQuestion : question.getQuestions()) {
			DelphiGraphDataSingle questionResults = new DelphiGraphDataSingle();
			questionResults.setLabel(subQuestion.getStrippedTitleNoEscape());

			for (int i = 1; i <= question.getNumIcons(); i++) {
				creator.addStatistics4RatingQuestion(survey, i, subQuestion, statistics, numberOfAnswersMapRatingQuestion);

				DelphiGraphEntry entry = new DelphiGraphEntry();
				entry.setLabel(Integer.toString(i));
				entry.setValue(statistics.getRequestedRecordsForRatingQuestion(subQuestion, i));
				questionResults.addEntry(entry);
			}

			result.addQuestion(questionResults);
		}

		// only show statistics if applicable for some subquestion
		if (result.getQuestions().isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(result);
	}

	private ResponseEntity<AbstractDelphiGraphData> handleDelphiGraphMatrix(Survey survey, Matrix question, Statistics statistics, StatisticsCreator creator, Map<Integer, Map<Integer, Integer>> numberOfAnswersMapMatrix) {
		DelphiGraphDataMulti result = new DelphiGraphDataMulti();
		result.setQuestionType(DelphiQuestionType.Matrix);
		result.setChartType(question.getDelphiChartType());

		Collection<String> uids = question.getQuestions().stream().map(Element::getUniqueId).collect(Collectors.toList());
		int contributions = answerExplanationService.getTotalDelphiContributions(uids, survey.getIsDraft());

		if (contributions < survey.getMinNumberDelphiStatistics()) {
			return ResponseEntity.noContent().build();
		}

		for (Element matrixQuestion : question.getQuestions()) {
			DelphiGraphDataSingle questionResults = new DelphiGraphDataSingle();
			questionResults.setLabel(matrixQuestion.getStrippedTitleNoEscape());

			for (Element matrixAnswer : question.getAnswers()) {
				creator.addStatistics4Matrix(survey, matrixAnswer, matrixQuestion, statistics, numberOfAnswersMapMatrix);

				DelphiGraphEntry entry = new DelphiGraphEntry();
				entry.setLabel(matrixAnswer.getStrippedTitleNoEscape());
				entry.setValue(statistics.getRequestedRecordsForMatrix(matrixQuestion, matrixAnswer));
				questionResults.addEntry(entry);
			}

			result.addQuestion(questionResults);
		}

		if (result.getQuestions().isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		result.setLabel(question.getStrippedTitle());
		return ResponseEntity.ok(result);
	}

	private ResponseEntity<AbstractDelphiGraphData> handleDelphiFreetextQuestion(Survey survey, Question question, StatisticsCreator creator) throws TooManyFiltersException {
		final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
		frequencyAnalyzer.setWordFrequenciesToReturn(200);
		frequencyAnalyzer.addNormalizer(new LowerCaseNormalizer());
		frequencyAnalyzer.addNormalizer(new TrimToEmptyNormalizer());
		frequencyAnalyzer.addNormalizer(new CharacterStrippingNormalizer());
		frequencyAnalyzer.setMinWordLength(3);
				
		if (stopWords == null) {
			InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/StopWords/EN.txt");
			String text;
			try {
				text = IOUtils.toString(inputStream, "UTF-8");
				String[] stopWordsArray = text.split("\\R");
				stopWords = Arrays.asList(stopWordsArray);
			} catch (IOException e) {
				logger.error(e.getLocalizedMessage(), e);
				stopWords = Arrays.asList("and", "or");
			}
		}
		
		frequencyAnalyzer.setStopWords(stopWords);
		
		List<String> texts = creator.getAnswers4FreeTextStatistics(survey, question);
		
		if (texts.size() < survey.getMinNumberDelphiStatistics()) {
			// only show statistics for this question if the total number of answers exceeds the threshold
			return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
		}		
		
		final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(texts);
		
		DelphiGraphDataSingle result = new DelphiGraphDataSingle();
		result.setChartType(question.getDelphiChartType());
		result.setQuestionType(DelphiQuestionType.FreeText);
		result.setLabel(question.getStrippedTitle());
	
		for (WordFrequency frequency : wordFrequencies) {
			DelphiGraphEntry delphiGraphEntry = new DelphiGraphEntry();
			delphiGraphEntry.setLabel(frequency.getWord());
			delphiGraphEntry.setValue(frequency.getFrequency());
			result.addEntry(delphiGraphEntry);
		}

		return ResponseEntity.ok(result);
	}

	private ResponseEntity<AbstractDelphiGraphData> handleDelphiNumberQuestion(Survey survey, NumberQuestion question, NumberQuestionStatistics numberQuestionStatistics) {
		if (numberQuestionStatistics.getNumberVotes() < survey.getMinNumberDelphiStatistics() || (!question.showStatisticsForNumberQuestion() && !question.isSlider())) {
			// only show statistics for this question if the total number of answers exceeds the threshold
			return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
		}
		
		DelphiGraphDataSingle result = new DelphiGraphDataSingle();
		result.setChartType(question.showStatisticsForNumberQuestion() ? question.getDelphiChartType() : DelphiChartType.None);

		result.setQuestionType(DelphiQuestionType.Number);
		result.setLabel(question.getStrippedTitle());

		Map<String, Integer> valuesMagnitude = numberQuestionStatistics.getValuesMagnitude();
		for (Map.Entry<String, Integer> mapEntry : valuesMagnitude.entrySet()) {
			String value = mapEntry.getKey();
			Integer rate = mapEntry.getValue();
			DelphiGraphEntry delphiGraphEntry = new DelphiGraphEntry();
			delphiGraphEntry.setLabel(value);
			delphiGraphEntry.setValue(rate);
			result.addEntry(delphiGraphEntry);
		}

		return ResponseEntity.ok(result);
	}

	private ResponseEntity<AbstractDelphiGraphData> handleDelphiGraphChoiceQuestion(Survey survey, ChoiceQuestion question, Statistics statistics, StatisticsCreator creator, Map<Integer, Integer> numberOfAnswersMap, Map<Integer, Map<String, Set<String>>> multipleChoiceSelectionsByAnswerset) throws Exception {
		if (numberOfAnswersMap.get(question.getId()) < survey.getMinNumberDelphiStatistics()) {
			// only show statistics for this question if the total number of answers exceeds the threshold
			return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
		}

		creator.addStatistics(survey, question, statistics, numberOfAnswersMap, multipleChoiceSelectionsByAnswerset);

		ResultFilter resultFilter = new ResultFilter();
		resultFilter.setVisibleQuestions(new HashSet<>(question.getId()));
		answerService.getNumberOfAnswerSets(survey, resultFilter);

		DelphiGraphDataSingle result = new DelphiGraphDataSingle();
		result.setChartType(question.getDelphiChartType());

		if (question instanceof SingleChoiceQuestion) {
			result.setQuestionType(DelphiQuestionType.SingleChoice);
		} else if (question instanceof MultipleChoiceQuestion) {
			result.setQuestionType(DelphiQuestionType.MultipleChoice);
		} else {
			return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
		}

		for (PossibleAnswer answer : question.getAllPossibleAnswers()) {
			DelphiGraphEntry entry = new DelphiGraphEntry();
			entry.setLabel(answer.getStrippedTitleNoEscape());
			entry.setValue(statistics.getRequestedRecords().get(answer.getId().toString()));
			result.addEntry(entry);
		}

		if (question instanceof SingleChoiceQuestion) {
			result.setLabel(question.getStrippedTitle());
		}
		return ResponseEntity.ok(result);
	}

	@GetMapping(value = "delphiStructure")
	public ResponseEntity<DelphiStructure> delphiStructure(HttpServletRequest request, Locale locale) {
		try {
			String surveyid = request.getParameter("surveyid");
			int sid = Integer.parseInt(surveyid);

			String languageCode = request.getParameter("languagecode");
			Survey survey = surveyService.getSurvey(sid, languageCode, true);

			if (survey == null || !survey.getIsDelphi()) {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
			
			String uniqueCode = request.getParameter("uniquecode");
			AnswerSet answerSet = null;
			if (uniqueCode != null && uniqueCode.length() > 0)
			{
				answerSet = answerService.get(uniqueCode);
			}
			
			DelphiStructure structure = new DelphiStructure();
			DelphiSection currentDelphiSection = new DelphiSection();
		
			DelphiSection lastMainSection = null;
			for (Element element : survey.getQuestionsAndSections()) {
				if (element instanceof Section) {
					Section section = (Section) element;
					
					currentDelphiSection = new DelphiSection();
					currentDelphiSection.setTitle(element.getTitle());
					currentDelphiSection.setLevel(section.getLevel());
					structure.getSections().add(currentDelphiSection);
					
					if (section.getLevel() == 1) {
						lastMainSection = currentDelphiSection;
					}
					
				} else if (element instanceof Question) {
					Question question = (Question)element;
					if (question.getIsDelphiQuestion()) {
						
						DelphiQuestion delphiQuestion = new DelphiQuestion();
						delphiQuestion.setTitle(question.getTitle());
						delphiQuestion.setUid(question.getUniqueId());
						delphiQuestion.setId(question.getId());
												
						if (answerSet != null)
						{
							String result = "";
							
							if (question instanceof Matrix)
							{								
								Matrix matrix = (Matrix)question;
						
								for (Element matrixQuestions : matrix.getQuestions()) {
									List<Answer> answers = answerSet.getAnswers(matrixQuestions.getId(), matrixQuestions.getUniqueId());
									
									for (Answer answer: answers) {
										result += SurveyHelper.getAnswerTitle(survey, answer, false) + " ";
									}
								}
								
								delphiQuestion.setAnswer(result);
							} else if (question instanceof RatingQuestion)
							{								
								RatingQuestion rating = (RatingQuestion)question;

								for (Element ratingQuestions : rating.getQuestions()) {
									List<Answer> answers = answerSet.getAnswers(ratingQuestions.getId(), ratingQuestions.getUniqueId());
									
									for (Answer answer: answers) {
										result += answer.getValue() + " ";
									}
								}

								delphiQuestion.setAnswer(result);
							} else if (question instanceof RankingQuestion)
							{
								List<Answer> answers = answerSet.getAnswers(question.getId(), question.getUniqueId());

								if (!answers.isEmpty()) {
									for (Answer answer : answers) {
										result += SurveyHelper.getAnswerTitle(survey, answer, false) + " ";
									}
								}
							} else {
								List<Answer> answers = answerSet.getAnswers(question.getId(), question.getUniqueId());

								if (!answers.isEmpty()) {
									for (Answer answer : answers) {
										result += SurveyHelper.getAnswerTitle(survey, answer, false) + " ";
										
										DelphiMedian median = null;										
										
										if (question instanceof SingleChoiceQuestion) {
											SingleChoiceQuestion singleChoiceQuestion = (SingleChoiceQuestion)question;
											if (singleChoiceQuestion.getUseLikert() && singleChoiceQuestion.getMaxDistance() > -1) {
												median = answerService.getMedian(survey, singleChoiceQuestion, answer, null);												
											}
										}
										
										if (question instanceof NumberQuestion) {
											NumberQuestion numberQuestion = (NumberQuestion)question;
											if (numberQuestion.isSlider() && numberQuestion.getMaxDistance() > -1) {
												median = answerService.getMedian(survey, numberQuestion, answer, null);												
											}
										}
										
										if (median != null) {
											delphiQuestion.setMaxDistanceExceeded(median.isMaxDistanceExceeded());
											
											try {
												AnswerExplanation explanation = answerExplanationService.getExplanation(answerSet.getId(), question.getUniqueId());
												if (explanation.getChangedForMedian() != null && explanation.getChangedForMedian()) {
													delphiQuestion.setChangedForMedian(true);
												}
											} catch (NoSuchElementException e) {
												//ignore
											}
										}
									}
								}
							}

							delphiQuestion.setAnswer(result);
							delphiQuestion.setHasUnreadComments(answerExplanationService.hasUnreadComments(answerSet.getUniqueCode(), question.getUniqueId()));
						}

						currentDelphiSection.getQuestions().add(delphiQuestion);
						currentDelphiSection.setHasDirectDelphiQuestions(true);
						
						//if the survey does not start with a section we create one
						if (structure.getSections().isEmpty()) {
							currentDelphiSection.setLevel(1);
							structure.getSections().add(currentDelphiSection);
						}
												
						if (lastMainSection != null) {
							lastMainSection.setHasDelphiQuestions(true);
						}
					} else {
						//non-delphi question
						if (!structure.isUnansweredMandatoryQuestions() && !question.getOptional() && !question.getIsDependent()) 
						{
							if (answerSet == null) {
								structure.setUnansweredMandatoryQuestions(true);
							} else if (question instanceof MatrixOrTable)
							{
								boolean found = false;
								MatrixOrTable matrix = (MatrixOrTable)question;
						
								for (Element matrixQuestions : matrix.getQuestions()) {
									List<Answer> answers = answerSet.getAnswers(matrixQuestions.getId(), matrixQuestions.getUniqueId());
									if (!answers.isEmpty()) {
										found = true;
										break;
									}
								}
								
								if (!found) {
									structure.setUnansweredMandatoryQuestions(true);
								}
							} else if (question instanceof RatingQuestion)
							{								
								RatingQuestion rating = (RatingQuestion)question;
								boolean found = false;
								for (Element ratingQuestions : rating.getQuestions()) {
									List<Answer> answers = answerSet.getAnswers(ratingQuestions.getId(), ratingQuestions.getUniqueId());
									if (!answers.isEmpty()) {
										found = true;
										break;
									}
								}

								if (!found) {
									structure.setUnansweredMandatoryQuestions(true);
								}
							} else {
								List<Answer> answers = answerSet.getAnswers(question.getId(), question.getUniqueId());
								if (answers.isEmpty()) {
									structure.setUnansweredMandatoryQuestions(true);
								}
							}
						}
					}
				}
			}

			return ResponseEntity.ok(structure);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void loadComments(DelphiTableEntry tableEntry, int originalAnswerSetId, Integer currentAnswerSetId, String questionUid, String surveyUid) {
		List<AnswerComment> comments = answerExplanationService.loadComments(originalAnswerSetId, questionUid);

		boolean isOriginalAnswerer = currentAnswerSetId != null && originalAnswerSetId == currentAnswerSetId;

		Map<Integer, DelphiComment> rootComments = new HashMap<>();
		for (AnswerComment comment : comments) {

			if (!uniqueCodeToUser.containsKey(surveyUid)) {
				uniqueCodeToUser.put(surveyUid, new HashMap<>());
			}

			Map<String, String> map = uniqueCodeToUser.get(surveyUid);

			if (!map.containsKey(comment.getUniqueCode())) {
				map.put(comment.getUniqueCode(), "User " + (map.size() + 1));
			}

			String user = "";
			Date date = null;
			if (!comment.getText().equals(AnswerExplanationService.DELETED_DELPHI_COMMENT_WITH_REPLIES_TEXT)) {
				// Only put the user and the date when the comment with replies has not been deleted.
				user = map.get(comment.getUniqueCode());
				date = comment.getDate();
			}

			boolean updateFlags = false;
			boolean isUnread = false;

			if (isOriginalAnswerer && !comment.getReadByParticipant()) {
				comment.setReadByParticipant(true);
				isUnread = true;
				updateFlags = true;
			}

			AnswerComment parent = comment.getParent();

			if (parent != null && currentAnswerSetId != null && !comment.getReadByParent()) {
				AnswerSet currentAnswerSet = answerService.get(currentAnswerSetId);

				if (currentAnswerSet != null && parent.getUniqueCode() != null && parent.getUniqueCode().equals(currentAnswerSet.getUniqueCode())) {
					comment.setReadByParent(true);
					isUnread = true;
					updateFlags = true;
				}
			}

			DelphiComment delphiComment = new DelphiComment(user, comment.getText(), date, comment.getId(), comment.getUniqueCode(), isUnread);

			if (parent == null) {
				tableEntry.getComments().add(delphiComment);
				rootComments.put(comment.getId(), delphiComment);
			} else {
				rootComments.get(parent.getId()).getReplies().add(delphiComment);
			}

			if (updateFlags) {
				answerExplanationService.saveOrUpdateComment(comment);
			}
		}
	}

	private void loadFiles(DelphiTableEntry tableEntry, int answerSetId, String questionUid) {
		try {
			final AnswerExplanation explanation = answerExplanationService.getExplanation(answerSetId, questionUid);
			final List<com.ec.survey.model.survey.base.File> explanationFiles = explanation.getFiles();
			for (com.ec.survey.model.survey.base.File explanationFile : explanationFiles) {
				DelphiTableFile tableFile = new DelphiTableFile(explanationFile.getName(), explanationFile.getUid());
				tableEntry.getFiles().add(tableFile);
			}
		} catch (NoSuchElementException ex) {
		}
	}
	
	@GetMapping(value = "delphiMedian")
	public ResponseEntity<DelphiMedian> delphiMedian(HttpServletRequest request) throws Exception {
		String surveyid = request.getParameter("surveyid");
		int sid = Integer.parseInt(surveyid);

		Survey survey = surveyService.getSurvey(sid);

		if (survey == null || !survey.getIsDelphi()) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

		AnswerSet answerSet = answerService.get(request.getParameter("uniquecode"));
		
		String questionuid = request.getParameter("questionuid");
		Element element = survey.getQuestionMapByUniqueId().get(questionuid);
		
		if (answerSet == null || element == null || !(element instanceof SingleChoiceQuestion || element instanceof NumberQuestion)) {
			return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
		}
		
		List<Answer> answerList = answerSet.getAnswers(-1, questionuid);
		if (answerList.isEmpty()) {
			return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
		}
		Answer answer = answerList.get(0);
		
		DelphiMedian median;
		
		if (element instanceof SingleChoiceQuestion) {		
			SingleChoiceQuestion singleChoiceQuestion = (SingleChoiceQuestion) element;
			if (!singleChoiceQuestion.getUseLikert() || singleChoiceQuestion.getMaxDistance() == -1) {
				return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
			}
			median = answerService.getMedian(survey, singleChoiceQuestion, answer, null);
		} else {
			NumberQuestion numberQuestion = (NumberQuestion) element;
			if (!numberQuestion.isSlider() || numberQuestion.getMaxDistance() == -1) {
				return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
			}
			median = answerService.getMedian(survey, numberQuestion, answer, null);
		}
	
		if (null == median) {
			return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
		}
		
		return ResponseEntity.ok(median);
	}


	@GetMapping(value = "delphiTable")
	public ResponseEntity<DelphiTable> delphiTable(HttpServletRequest request) {
		try {
			String surveyid = request.getParameter("surveyid");
			int sid = Integer.parseInt(surveyid);

			String languageCode = request.getParameter("languagecode");
			Survey survey = surveyService.getSurvey(sid, languageCode);

			if (survey == null || !survey.getIsDelphi()) {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}

			if (!survey.getIsDelphiShowAnswers()) {
				return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
			}

			AnswerSet answerSet = answerService.get(request.getParameter("uniquecode"));

			if (!survey.getIsDelphiShowAnswersAndStatisticsInstantly() && answerSet == null) {
				// participant may only see answers if he answered before
				return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
			}

			String questionuid = request.getParameter("questionuid");
			Element element = survey.getQuestionMapByUniqueId().get(questionuid);

			if (!(element instanceof Question)) {
				return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
			}

			Question question = (Question) element;
			if (!question.getIsDelphiQuestion() || (!survey.getIsDelphiShowAnswersAndStatisticsInstantly()
					&& !answerSetContainsAnswerForQuestion(answerSet, question))) {
				return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
			}

			DelphiTableOrderBy orderBy = DelphiTableOrderBy.UpdateDesc;
			try {
				orderBy = DelphiTableOrderBy.valueOf(request.getParameter("orderby"));
			} catch (Exception ignored) {
			}

			int limit = 20;
			try {
				limit = Integer.parseInt(request.getParameter("limit"));
				limit = Math.max(limit, 0);
			} catch (Exception ignored) {
			}

			int offset = 0;
			try {
				offset = Integer.parseInt(request.getParameter("offset"));
				offset = Math.max(offset, 0);
			} catch (Exception ignored) {
			}

			Integer answerSetId = answerSet != null ? answerSet.getId() : null;

			DelphiTable result;

			if (question instanceof ChoiceQuestion) {
				result = handleDelphiTableChoiceQuestion((ChoiceQuestion) question, orderBy, limit, offset, answerSetId);
			} else if (question instanceof Matrix) {
				result = handleDelphiTableMatrix((Matrix) question, orderBy, limit, offset, answerSetId);
			} else if (question instanceof RatingQuestion) {
				result = handleDelphiTableRatingQuestion((RatingQuestion) question, orderBy, limit, offset, answerSetId);
			} else if (question instanceof Table) {
				result = handleDelphiTableTable((Table) question, orderBy, limit, offset, answerSetId);
			} else if (question instanceof RankingQuestion) {
				result = handleDelphiTableRankingQuestion((RankingQuestion) question, orderBy, limit, offset, answerSetId);
			} else {
				result = handleDelphiTableRawValueQuestion(question, orderBy, limit, offset, answerSetId);
			}

			result.setQuestionType(DelphiQuestionType.from(question));

			for (DelphiTableEntry entry : result.getEntries()) {
				boolean stopIteration = false;

				for (DelphiComment comment : entry.getComments()) {
					if (comment.isUnread()) {
						result.setHasNewComments(true);
						stopIteration = true;
						break;
					}

					for (DelphiComment reply : comment.getReplies()) {
						if (reply.isUnread()) {
							result.setHasNewComments(true);
							stopIteration = true;
							break;
						}
					}

					if (stopIteration) {
						break;
					}
				}

				if (stopIteration) {
					break;
				}
			}

			result.setShowExplanationBox(question.getShowExplanationBox());

			return ResponseEntity.ok(result);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Groups DelphiContributions into subsequent lists and maintains the original order (requires values to be sorted upfront)
	 */
	private List<List<DelphiContribution>> groupDelphiContributions(DelphiContributions contributions) {
		List<List<DelphiContribution>> result = new ArrayList<>();

		int currentAnswerSetId = -1;
		List<DelphiContribution> currentList = new ArrayList<>();

		for (DelphiContribution contrib : contributions.getContributions()) {
			int answerSetId = contrib.getAnswerSetId();

			if (answerSetId != currentAnswerSetId) {
				currentAnswerSetId = answerSetId;

				if (!currentList.isEmpty()) {
					result.add(currentList);
					currentList = new ArrayList<>();
				}
			}

			currentList.add(contrib);
		}

		if (!currentList.isEmpty()) {
			result.add(currentList);
		}

		return result;
	}

	private DelphiTable handleDelphiTableChoiceQuestion(ChoiceQuestion question, DelphiTableOrderBy orderBy, int limit, int offset, Integer answerSetId) {
		DelphiTable result = new DelphiTable();
		result.setOffset(offset);

		Map<String, String> answerUidToTitle = new HashMap<>();
		Map<String, String> dependentElements = new HashMap<>();
		for (PossibleAnswer answer : question.getAllPossibleAnswers()) {
			String id = answer.getUniqueId();
			String title = answer.getTitle();
			answerUidToTitle.put(id, title);
			
			for (Element dependentElement : answer.getDependentElements().getDependentElements()) {
				if (dependentElement instanceof FreeTextQuestion && !dependentElement.isDelphiElement() && !dependentElements.containsKey(question.getUniqueId()))
				{
					dependentElements.put(question.getUniqueId(), dependentElement.getUniqueId());
				}
			}
		}

		DelphiContributions contributions = answerExplanationService.getDelphiContributions(question, orderBy, limit, offset);
		result.setTotal(contributions.getTotal());

		Survey survey = question.getSurvey();

		for (List<DelphiContribution> entry : groupDelphiContributions(contributions)) {
			List<String> values = entry.stream()
					.map(DelphiContribution::getAnswerUid)
					.collect(Collectors.toList());

			DelphiTableEntry tableEntry = new DelphiTableEntry();
			DelphiContribution firstValue = entry.get(0);
			tableEntry.setAnswerSetId(firstValue.getAnswerSetId());
			tableEntry.setAnswerSetUniqueCode(firstValue.getAnswerSetUniqueCode());
			tableEntry.setExplanation(firstValue.getExplanation());
			tableEntry.setUpdateDate(firstValue.getUpdate());
			loadComments(tableEntry, firstValue.getAnswerSetId(), answerSetId, question.getUniqueId(), survey.getUniqueId());
			loadFiles(tableEntry, firstValue.getAnswerSetId(), question.getUniqueId());

			for (String value : values) {
				String title = answerUidToTitle.getOrDefault(value, "n/a");
				DelphiTableAnswer answer = new DelphiTableAnswer(null, title);
				tableEntry.getAnswers().add(answer);
			}
			
			if (dependentElements.containsKey(question.getUniqueId())) {
				String dependentElementUid = dependentElements.get(question.getUniqueId());
				List<String> additionalValues = answerExplanationService.getDelphiDependentAnswers(dependentElementUid, firstValue.getAnswerSetId());
				
				for (String value : additionalValues) {
					DelphiTableAnswer answer = new DelphiTableAnswer(null, value);
					tableEntry.getAnswers().add(answer);
				}
			}

			result.getEntries().add(tableEntry);
		}

		return result;
	}

	private DelphiTable handleDelphiTableMatrix(Matrix question, DelphiTableOrderBy orderBy, int limit, int offset, Integer answerSetId) {
		DelphiTable result = new DelphiTable();
		result.setOffset(offset);

		Map<String, String> answerTitles = new HashMap<>();
		for (Element matrixAnswer : question.getAnswers()) {
			answerTitles.put(matrixAnswer.getUniqueId(), matrixAnswer.getTitle());
		}

		Map<String, Integer> questionPositions = new HashMap<>();
		Map<String, String> questionTitles = new HashMap<>();
		for (Element matrixQuestion : question.getQuestions()) {
			questionPositions.put(matrixQuestion.getUniqueId(), matrixQuestion.getPosition());
			questionTitles.put(matrixQuestion.getUniqueId(), matrixQuestion.getTitle());
		}

		DelphiContributions contributions = answerExplanationService.getDelphiContributions(question, orderBy, limit, offset);
		result.setTotal(contributions.getTotal());

		Survey survey = question.getSurvey();

		for (List<DelphiContribution> entry : groupDelphiContributions(contributions)) {
			// maps position to element
			Collection<Pair<Integer, DelphiTableAnswer>> answers = new ArrayList<>(entry.size());

			for (DelphiContribution contrib : entry) {
				// find labels for question and answer
				String label = questionTitles.getOrDefault(contrib.getQuestionUid(), "Unknown");
				String value = answerTitles.getOrDefault(contrib.getAnswerUid(), "n/a");

				DelphiTableAnswer answer = new DelphiTableAnswer(label, value);
				int position = questionPositions.get(contrib.getQuestionUid());
				answers.add(new Pair<>(position, answer));
			}

			// sort answers by position
			List<DelphiTableAnswer> sortedAnswers = answers.stream()
					.sorted(Comparator.comparingInt(Pair::getKey))
					.map(Pair::getValue)
					.collect(Collectors.toList());

			DelphiContribution firstValue = entry.get(0);

			DelphiTableEntry tableEntry = new DelphiTableEntry();
			tableEntry.setAnswerSetId(firstValue.getAnswerSetId());
			tableEntry.setAnswerSetUniqueCode(firstValue.getAnswerSetUniqueCode());
			tableEntry.getAnswers().addAll(sortedAnswers);
			tableEntry.setExplanation(firstValue.getExplanation());
			tableEntry.setUpdateDate(firstValue.getUpdate());
			loadComments(tableEntry, firstValue.getAnswerSetId(), answerSetId, question.getUniqueId(), survey.getUniqueId());
			loadFiles(tableEntry, firstValue.getAnswerSetId(), question.getUniqueId());

			result.getEntries().add(tableEntry);
		}

		return result;
	}

	private DelphiTable handleDelphiTableRatingQuestion(RatingQuestion question, DelphiTableOrderBy orderBy, int limit, int offset, Integer answerSetId) {
		DelphiTable result = new DelphiTable();
		result.setOffset(offset);

		Map<String, Integer> questionPositions = new HashMap<>();
		Map<String, String> questionTitles = new HashMap<>();
		for (Element subQuestion : question.getQuestions()) {
			questionPositions.put(subQuestion.getUniqueId(), subQuestion.getPosition());
			questionTitles.put(subQuestion.getUniqueId(), subQuestion.getTitle());
		}

		DelphiContributions contributions = answerExplanationService.getDelphiContributions(question, orderBy, limit, offset);
		result.setTotal(contributions.getTotal());

		Survey survey = question.getSurvey();

		for (List<DelphiContribution> entry : groupDelphiContributions(contributions)) {
			// maps position to element
			Collection<Pair<Integer, DelphiTableAnswer>> answers = new ArrayList<>(entry.size());

			for (DelphiContribution contrib : entry) {
				// find label for question ID
				String label = questionTitles.getOrDefault(contrib.getQuestionUid(), "Unknown");

				DelphiTableAnswer answer = new DelphiTableAnswer(label, contrib.getValue());
				int position = questionPositions.get(contrib.getQuestionUid());
				answers.add(new Pair<>(position, answer));
			}

			// sort answers by position
			List<DelphiTableAnswer> sortedAnswers = answers.stream()
					.sorted(Comparator.comparingInt(Pair::getKey))
					.map(Pair::getValue)
					.collect(Collectors.toList());

			DelphiContribution firstValue = entry.get(0);

			DelphiTableEntry tableEntry = new DelphiTableEntry();
			tableEntry.setAnswerSetId(firstValue.getAnswerSetId());
			tableEntry.setAnswerSetUniqueCode(firstValue.getAnswerSetUniqueCode());
			tableEntry.getAnswers().addAll(sortedAnswers);
			tableEntry.setExplanation(firstValue.getExplanation());
			tableEntry.setUpdateDate(firstValue.getUpdate());
			loadComments(tableEntry, firstValue.getAnswerSetId(), answerSetId, question.getUniqueId(), survey.getUniqueId());
			loadFiles(tableEntry, firstValue.getAnswerSetId(), question.getUniqueId());

			result.getEntries().add(tableEntry);
		}

		return result;
	}
	
	private DelphiTable handleDelphiTableRankingQuestion(RankingQuestion question, DelphiTableOrderBy orderBy, int limit, int offset, Integer answerSetId) {
		DelphiTable result = new DelphiTable();
		result.setOffset(offset);

		// get survey
		Survey survey = question.getSurvey();

		// get all contributions
		DelphiContributions contributions = answerExplanationService.getDelphiContributions(Collections.singletonList(question.getUniqueId()), question.getUniqueId(), survey.getIsDraft(), orderBy, limit, offset);
		result.setTotal(contributions.getTotal());
		
		// no need to group by answer set because there can only be one answer per answer set
		for (DelphiContribution contrib : contributions.getContributions()) {			
			List<String> rankingAnswerList = question.getAnswer(contrib.getValue());
			String value = String.join("; ", rankingAnswerList);
			DelphiTableEntry tableEntry = new DelphiTableEntry();
			tableEntry.setAnswerSetId(contrib.getAnswerSetId());
			tableEntry.setAnswerSetUniqueCode(contrib.getAnswerSetUniqueCode());
			tableEntry.setExplanation(contrib.getExplanation());
			tableEntry.setUpdateDate(contrib.getUpdate());
			tableEntry.getAnswers().add(new DelphiTableAnswer(null, value));
			loadComments(tableEntry, contrib.getAnswerSetId(), answerSetId, question.getUniqueId(), survey.getUniqueId());
			loadFiles(tableEntry, contrib.getAnswerSetId(), question.getUniqueId());

			result.getEntries().add(tableEntry);
		}

		return result;
	}	
	
	@PostMapping(value = "/delphiAddComment")
	public ResponseEntity<String> delphiAddComment(HttpServletRequest request) {
		try {
			final String surveyId = request.getParameter("surveyid");
			final int surveyIdParsed = Integer.parseInt(surveyId);
			final Survey survey = surveyService.getSurvey(surveyIdParsed);

			final String answerSetId = request.getParameter("answersetid");
			final int answerSetIdParsed = Integer.parseInt(answerSetId);
			
			final String text = request.getParameter("text");
			final String uniqueCode = request.getParameter("uniquecode");
			
			final String questionUid = request.getParameter("questionuid");
			final String parent = request.getParameter("parent");
			
			Element element = survey.getQuestionMapByUniqueId().get(questionUid);

			if (!(element instanceof Question) || !element.isDelphiElement()) {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
			
			AnswerSet answerSet = answerService.get(uniqueCode);
			
			if (!survey.getIsDelphiShowAnswersAndStatisticsInstantly()) {			
				if (answerSet == null) {
					// participant may only see answers if he answered before
					return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
				}
	
				Question question = (Question) element;
				if (!answerSetContainsAnswerForQuestion(answerSet, question)) {
					return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
				}			
			}
			
			AnswerComment comment = new AnswerComment();
			comment.setAnswerSetId(answerSetIdParsed);
			comment.setQuestionUid(questionUid);
			comment.setUniqueCode(uniqueCode);
			comment.setText(text);
			comment.setDate(new Date());

			// check if commentator is participant who gave the original answer
			comment.setReadByParticipant(answerSet != null && answerSetIdParsed == answerSet.getId());
			
			if (parent != null) {
				int parentCommentId = Integer.parseInt(parent);
				AnswerComment parentComment = answerExplanationService.getComment(parentCommentId);
				if (parentComment == null || !parentComment.getQuestionUid().equals(questionUid))
				{
					return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
				}
				comment.setParent(parentComment);

				// check if commentator is also creator of parent comment
				comment.setReadByParent(parentComment.getUniqueCode() != null && parentComment.getUniqueCode().equals(uniqueCode));
			}

			answerExplanationService.saveOrUpdateComment(comment);

			return new ResponseEntity<>(null, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/editDelphiComment/{id}")
	public ResponseEntity<String> delphiEditComment(@PathVariable String id, HttpServletRequest request) {

		try {
			final int idParsed = Integer.parseInt(id);

			final String text = request.getParameter("text");
			final String uniqueCode = request.getParameter("uniqueCode");
			final boolean formManager = request.getParameter("formManager") != null;

			if (uniqueCode == null || uniqueCode.isEmpty()) {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}

			final AnswerComment comment = answerExplanationService.getComment(idParsed);
			if (comment == null
					|| !comment.getUniqueCode().equals(uniqueCode)
					|| comment.getText().equals(AnswerExplanationService.DELETED_DELPHI_COMMENT_WITH_REPLIES_TEXT)
					|| text.equals(AnswerExplanationService.DELETED_DELPHI_COMMENT_WITH_REPLIES_TEXT)) {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
			if (formManager) {
				final int userid = sessionService.getCurrentUser(request).getId();
				AnswerSet answerset = answerService.get(uniqueCode);
				if (answerset == null) {
					answerset = answerService.getDraft(uniqueCode).getAnswerSet();
				}
				final String surveyID = answerset.getSurvey().getUniqueId();
				activityService.log(801, "id: " + comment.getId() + " ;text:" + comment.getText(),
						comment.getId() + ":" + text, userid,
						surveyID);
			}
			comment.setText(text);
			comment.setDate(new Date());
			answerExplanationService.saveOrUpdateComment(comment);

			return new ResponseEntity<>(null, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/deleteDelphiComment/{id}")
	public ResponseEntity<String> delphiDeleteComment(@PathVariable String id, HttpServletRequest request) {

		try {
			final int idParsed = Integer.parseInt(id);

			final String uniqueCode = request.getParameter("uniqueCode");
			final boolean formManager = request.getParameter("formManager") != null;

			if (uniqueCode == null || uniqueCode.isEmpty()) {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}

			final AnswerComment comment = answerExplanationService.getComment(idParsed);
			if (comment == null || !comment.getUniqueCode().equals(uniqueCode)) {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
			if (formManager) {
				final int userid = sessionService.getCurrentUser(request).getId();
				AnswerSet answerset = answerService.get(uniqueCode);
				if (answerset == null) {
					answerset = answerService.getDraft(uniqueCode).getAnswerSet();
				}
				final String surveyID = answerset.getSurvey().getUniqueId();
				activityService.log(802, "id: " + comment.getId() + " ;text: " + comment.getText(),
						null, userid,
						surveyID);
			}
			if (!answerExplanationService.hasCommentChildren(idParsed)) {
				answerExplanationService.deleteComment(comment);
			} else {
				comment.setText(AnswerExplanationService.DELETED_DELPHI_COMMENT_WITH_REPLIES_TEXT);
				comment.setDate(new Date());
				answerExplanationService.saveOrUpdateComment(comment);
			}

			return new ResponseEntity<>(null, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@PostMapping(value = "/sendDelphiLink")
	public ResponseEntity<String> sendDelphiLink(HttpServletRequest request, Locale locale) {
		try {
			final String uniqueCode = request.getParameter("uniqueCode");
			if (uniqueCode == null || uniqueCode.isEmpty()) {
				return new ResponseEntity<>("Invalid Code", HttpStatus.BAD_REQUEST);
			}
			
			Map<String, String[]> parameters = Ucs2Utf8.requestToHashMap(request);
			String email = parameters.get(Constants.EMAIL)[0];
			String link = serverPrefix + "editcontribution/" + uniqueCode;
			
			AnswerSet answerSet = answerService.get(uniqueCode);
		
			if (email == null || answerSet == null) {
				return new ResponseEntity<>("Invalid Code", HttpStatus.BAD_REQUEST);
			}

			String body = "Dear EUSurvey user,<br /><br />Your contribution to the survey '<b>" + answerSet.getSurvey().cleanTitle()
					+ "</b>' has been saved. To open the contribution again, please follow this link:<br /><br />";
			body += "<a href=\"" + link + "\">" + link + "</a><br /><br />Your EUSurvey team";

			try {
				InputStream inputStream = servletContext
						.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
				String text = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", body).replace("[HOST]",
						serverPrefix);

				mailService.SendHtmlMail(email, sender, sender,
						resources.getMessage("message.mail.linkSubject", null, new Locale("EN")), text, null);
			} catch (Exception e) {
				logger.error("Problem during sending the draft link. To:" + email + " Link:" + link, e);
				return new ResponseEntity<>(resources.getMessage("message.mail.failMailLinkDraft", null, locale), HttpStatus.INTERNAL_SERVER_ERROR);
			}		

			return new ResponseEntity<>(resources.getMessage("message.mail.successMailLinkDraft", null, locale), HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(resources.getMessage("message.mail.failMailLinkDraft", null, locale), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private DelphiTable handleDelphiTableRawValueQuestion(Question question, DelphiTableOrderBy orderBy, int limit, int offset, Integer answerSetId) {
		DelphiTable result = new DelphiTable();
		result.setOffset(offset);

		// get survey
		Survey survey = question.getSurvey();

		// get all contributions
		DelphiContributions contributions = answerExplanationService.getDelphiContributions(Collections.singletonList(question.getUniqueId()), question.getUniqueId(), survey.getIsDraft(), orderBy, limit, offset);
		result.setTotal(contributions.getTotal());

		// no need to group by answer set because there can only be one answer per answer set
		for (DelphiContribution contrib : contributions.getContributions()) {
			DelphiTableEntry tableEntry = new DelphiTableEntry();
			tableEntry.setAnswerSetId(contrib.getAnswerSetId());
			tableEntry.setAnswerSetUniqueCode(contrib.getAnswerSetUniqueCode());
			tableEntry.setExplanation(contrib.getExplanation());
			tableEntry.setUpdateDate(contrib.getUpdate());
			tableEntry.getAnswers().add(new DelphiTableAnswer(null, contrib.getValue()));
			loadComments(tableEntry, contrib.getAnswerSetId(), answerSetId, question.getUniqueId(), survey.getUniqueId());
			loadFiles(tableEntry, contrib.getAnswerSetId(), question.getUniqueId());

			result.getEntries().add(tableEntry);
		}

		return result;
	}

	private DelphiTable handleDelphiTableTable(Table question, DelphiTableOrderBy orderBy, int limit, int offset, Integer answerSetId) {
		DelphiTable result = new DelphiTable();
		result.setOffset(offset);

		DelphiContributions contributions = answerExplanationService.getDelphiContributions(question, orderBy, limit, offset);
		result.setTotal(contributions.getTotal());

		Survey survey = question.getSurvey();

		String[] colLabels = new String[question.getColumns()];
		String[] rowLabels = new String[question.getRows()];

		for (Element child : question.getChildElements()) {
			Integer position = child.getPosition();

			if (position < 1) {
				continue;
			}

			if (position < question.getColumns()) {
				colLabels[position] = child.getTitle();
			} else {
				rowLabels[position - question.getColumns() + 1] = child.getTitle();
			}
		}

		for (List<DelphiContribution> entry : groupDelphiContributions(contributions)) {
			DelphiContribution firstValue = entry.get(0);
			DelphiTableEntry tableEntry = new DelphiTableEntry();
			tableEntry.setAnswerSetId(firstValue.getAnswerSetId());
			tableEntry.setAnswerSetUniqueCode(firstValue.getAnswerSetUniqueCode());
			tableEntry.setExplanation(firstValue.getExplanation());
			tableEntry.setUpdateDate(firstValue.getUpdate());

			for (DelphiContribution contrib : entry) {
				String label = "<span>" + colLabels[contrib.getColumn()] + " - " + rowLabels[contrib.getRow()] + "</span>";
				DelphiTableAnswer answer = new DelphiTableAnswer(label, contrib.getValue());
				tableEntry.getAnswers().add(answer);
			}

			result.getEntries().add(tableEntry);
			loadComments(tableEntry, firstValue.getAnswerSetId(), answerSetId, question.getUniqueId(), survey.getUniqueId());
			loadFiles(tableEntry, firstValue.getAnswerSetId(), question.getUniqueId());
		}

		return result;
	}
}
