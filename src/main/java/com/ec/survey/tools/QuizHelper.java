package com.ec.survey.tools;

import com.ec.survey.model.Answer;
import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.survey.*;
import com.ec.survey.model.survey.quiz.QuizResult;

import java.util.*;

public class QuizHelper {

	public static QuizResult getQuizResult(AnswerSet answerSet)
	{
		return getQuizResult(answerSet, answerSet.getSurvey());
	}

	public static QuizResult getQuizResult(AnswerSet answerSet, Survey survey)
	{
		return getQuizResult(answerSet, survey, null);
	}

	public static QuizResult getQuizResult(AnswerSet answerSet, final Set<String> invisibleElements)
	{
		return getQuizResult(answerSet, answerSet.getSurvey(), invisibleElements);
	}

	public static QuizResult getQuizResult(AnswerSet answerSet, Survey survey, final Set<String> invisibleElements)
	{
		QuizResult result = new QuizResult();

		int score = 0;
		int maximumScore = 0;

		String currentSectionUid = null; 
		int currentSectionScore = 0;
		int currentSectionMaxScore = 0;
		
		for (Element element : survey.getQuestionsAndSections()) {
			if (invisibleElements != null && invisibleElements.contains(element.getUniqueId())) {
				continue;
			}
			if (element instanceof Section)
			{
				if (currentSectionUid != null)
				{
					result.getSectionScores().put(currentSectionUid, currentSectionScore + Constants.PATH_DELIMITER + currentSectionMaxScore);
				}
				currentSectionUid = element.getUniqueId();
				currentSectionScore = 0;
				currentSectionMaxScore = 0;
			} else if (element instanceof Question)
			{
				Question question = (Question)element;
				
				if (question instanceof Matrix)
				{
					Matrix matrix = (Matrix)question;
									
					List<Element> matrixAnswers = matrix.getAnswers();
					for (int i = 0; i < matrixAnswers.size(); i++) {
						result.getPositionForAnswerUID().put(matrixAnswers.get(i).getUniqueId(), i);
						result.getAnswerUIDForPosition().put(i, matrixAnswers.get(i).getUniqueId());
					}
					
					for (Element matrixQuestionElement : matrix.getQuestions()) {
						if (invisibleElements != null && invisibleElements.contains(matrixQuestionElement.getUniqueId())) {
							continue;
						}

						Question matrixQuestion = (Question)matrixQuestionElement;
						List<Answer> answers = answerSet.getAnswers(matrixQuestion.getUniqueId());
						
						if (matrixQuestion.getScoring() == 1) {	
							result.getQuestionMaximumScores().put(matrixQuestion.getUniqueId(), matrixQuestion.getQuizPoints());
							maximumScore += matrixQuestion.getQuizPoints();
							currentSectionMaxScore += matrixQuestion.getQuizPoints();
							
							//TODO: is that correct?
							if (answers.isEmpty() && !matrix.getIsSingleChoice())
							{
								result.getPartiallyAnswersMultipleChoiceQuestions().add(matrixQuestion.getUniqueId());
							}
							
							if (answers.isEmpty()) {
								continue;
							}
							
							if (matrix.getIsSingleChoice()) {
								// get points if answer is correct						
								Answer answer = answers.get(0);
								if (result.getPositionForAnswerUID() != null && result.getPositionForAnswerUID().containsKey(answer.getPossibleAnswerUniqueId())) {
									int pos = result.getPositionForAnswerUID().get(answer.getPossibleAnswerUniqueId());
									ScoringItem scoringItem = matrixQuestion.getScoringItems().get(pos);
									if (scoringItem != null && scoringItem.isCorrect()) {
										score += matrixQuestion.getQuizPoints();
										currentSectionScore += matrixQuestion.getQuizPoints();
										result.getQuestionScores().put(matrixQuestion.getUniqueId(), matrixQuestion.getQuizPoints());
									}
								}
							} else {
								// get points if exactly all correct answers are selected						
								Set<String> correctUIDs = new HashSet<>();
								for (int i = 0; i < matrixQuestion.getScoringItems().size(); i++) {
									if (matrixQuestion.getScoringItems().get(i).isCorrect())
									{
										String uid = result.getAnswerUIDForPosition().get(i);
										correctUIDs.add(uid);
									}
								}						
								if (correctUIDs.size() == answers.size())
								{
									boolean allfound = true;
									for (Answer answer : answers)
									{
										if (!correctUIDs.contains(answer.getPossibleAnswerUniqueId()))
										{
											allfound = false;
											break;
										}
									}							
									if (allfound)
									{
										score += matrixQuestion.getQuizPoints();
										currentSectionScore += matrixQuestion.getQuizPoints();
										result.getQuestionScores().put(matrixQuestion.getUniqueId(), matrixQuestion.getQuizPoints());
									} else {
										result.getPartiallyAnswersMultipleChoiceQuestions().add(matrixQuestion.getUniqueId());
									}
								} else {
									result.getPartiallyAnswersMultipleChoiceQuestions().add(matrixQuestion.getUniqueId());
								}
							}
						} else {
							if (matrix.getIsSingleChoice())
							{
								int max = 0;
								for (ScoringItem scoringItem: matrixQuestion.getScoringItems()) {
									if (scoringItem.getPoints() > max)
									{
										max = scoringItem.getPoints();
									}
								}
								maximumScore += max;
								currentSectionMaxScore += max;
								result.getQuestionMaximumScores().put(matrixQuestion.getUniqueId(), max);
								
								if (!answers.isEmpty())
								{
									Answer answer = answers.get(0);
									int pos = result.getPositionForAnswerUID().get(answer.getPossibleAnswerUniqueId());
									ScoringItem scoringItem = matrixQuestion.getScoringItems().get(pos);
								
									if (scoringItem != null)
									{
										score += scoringItem.getPoints();
										currentSectionScore += scoringItem.getPoints();
										result.getQuestionScores().put(matrixQuestion.getUniqueId(), scoringItem.getPoints());
									}
								}
							} else {
								for (ScoringItem scoringItem : matrixQuestion.getScoringItems()) {
									if (scoringItem.getPoints() > 0)
									{
										maximumScore += scoringItem.getPoints();
										currentSectionMaxScore += scoringItem.getPoints();
										if (!result.getQuestionMaximumScores().containsKey(matrixQuestion.getUniqueId()))
										{
											result.getQuestionMaximumScores().put(matrixQuestion.getUniqueId(), scoringItem.getPoints());
										} else {
											result.getQuestionMaximumScores().put(matrixQuestion.getUniqueId(), result.getQuestionMaximumScores().get(matrixQuestion.getUniqueId()) + scoringItem.getPoints());
										}
									}
								}
					
								int qscore = 0;
										
								for (Answer answer : answers)
								{
									int pos = result.getPositionForAnswerUID().get(answer.getPossibleAnswerUniqueId());
									ScoringItem scoringItem = matrixQuestion.getScoringItems().get(pos);
									if (scoringItem != null)
									{
										qscore += scoringItem.getPoints();
									}
								}
								
								if (matrixQuestion.getNoNegativeScore() != null && matrixQuestion.getNoNegativeScore() && qscore < 0)
								{
									qscore = 0;
								}
								
								score += qscore;
								currentSectionScore += qscore;
								result.getQuestionScores().put(matrixQuestion.getUniqueId(), qscore);	
								
								if (!result.getQuestionScores().containsKey(matrixQuestion.getUniqueId()) || (result.getQuestionMaximumScores().containsKey(matrixQuestion.getUniqueId()) && (result.getQuestionMaximumScores().get(matrixQuestion.getUniqueId()) > result.getQuestionScores().get(matrixQuestion.getUniqueId()))))
								{
									result.getPartiallyAnswersMultipleChoiceQuestions().add(matrixQuestion.getUniqueId());
								}
							}
						}
					}
				}
				
				if (question.getScoring() == 0) continue;
				
				List<Answer> answers = answerSet.getAnswers(question.getUniqueId());
				
				if (question.getScoring() == 1) //global points
				{
					result.getQuestionMaximumScores().put(question.getUniqueId(), question.getQuizPoints());
					maximumScore += question.getQuizPoints();
					currentSectionMaxScore += question.getQuizPoints();
					
					if (answers.isEmpty() && question instanceof MultipleChoiceQuestion)
					{
						result.getPartiallyAnswersMultipleChoiceQuestions().add(question.getUniqueId());
					}					
					
					if (question instanceof SingleChoiceQuestion)
					{
						if (answers.isEmpty()) {
							continue;
						}
						// get points if answer is correct						
						Answer answer = answers.get(0);
						ChoiceQuestion choice = (ChoiceQuestion) question;
						PossibleAnswer pa = choice.getPossibleAnswerByUniqueId(answer.getPossibleAnswerUniqueId());
						if (pa != null && pa.getScoring() != null && pa.getScoring().isCorrect())
						{
							score += question.getQuizPoints();
							currentSectionScore += question.getQuizPoints();
							result.getQuestionScores().put(question.getUniqueId(), question.getQuizPoints());
						}										
					} else if (question instanceof MultipleChoiceQuestion)
					{
						if (answers.isEmpty()) {
							continue;
						}
						// get points if exactly all correct answers are selected						
						ChoiceQuestion choice = (ChoiceQuestion) question;
						Set<String> correctUIDs = new HashSet<>();
						for (PossibleAnswer pa : choice.getAllPossibleAnswers()) {
							if (pa != null && pa.getScoring() != null && pa.getScoring().isCorrect())
							{
								correctUIDs.add(pa.getUniqueId());
							}
						}						
						if (correctUIDs.size() == answers.size())
						{
							boolean allfound = true;
							for (Answer answer : answers)
							{
								if (!correctUIDs.contains(answer.getPossibleAnswerUniqueId()))
								{
									allfound = false;
									break;
								}
							}							
							if (allfound)
							{
								score += question.getQuizPoints();
								currentSectionScore += question.getQuizPoints();
								result.getQuestionScores().put(question.getUniqueId(), question.getQuizPoints());
							} else {
								result.getPartiallyAnswersMultipleChoiceQuestions().add(question.getUniqueId());
							}
						} else {
							result.getPartiallyAnswersMultipleChoiceQuestions().add(question.getUniqueId());
						}
					} else if (question instanceof NumberQuestion)
					{
						//get points if at least one correct rule is fulfilled
						boolean ok = false;
						boolean wrong = false;
						Double value = !answers.isEmpty() ? Double.parseDouble(answers.get(0).getValue()) : null;
						ScoringItem defaultItem = null;
						for (ScoringItem scoringItem : question.getScoringItems())
						{
							if (ruleMatches(scoringItem, value))
							{
								if (scoringItem.isCorrect())
								{
									ok = true;
								} else {
									wrong = true;
								}
								result.getQuestionScoringItems().put(element.getUniqueId(), scoringItem);
							}
							
							if (scoringItem.getType() == -1)
							{
								defaultItem = scoringItem;
							}
						}
						if (ok)
						{
							score += question.getQuizPoints();
							currentSectionScore += question.getQuizPoints();
							result.getQuestionScores().put(question.getUniqueId(), question.getQuizPoints());
						} else if (wrong)
						{
							result.getQuestionScores().put(question.getUniqueId(), 0);
						} else if (defaultItem != null && defaultItem.isCorrect())
						{
							score += question.getQuizPoints();
							currentSectionScore += question.getQuizPoints();
							result.getQuestionScores().put(question.getUniqueId(), question.getQuizPoints());
							result.getQuestionScoringItems().put(element.getUniqueId(), defaultItem);
						}
					} else if (question instanceof DateQuestion)
					{
						//get points if at least one correct rule is fulfilled
						boolean ok = false;
						boolean wrong = false;
						Date value = !answers.isEmpty() ? ConversionTools.getDate(answers.get(0).getValue()) : null;
						ScoringItem defaultItem = null;
						for (ScoringItem scoringItem : question.getScoringItems())
						{							
							if (ruleMatches(scoringItem, value))
							{
								if (scoringItem.isCorrect())
								{
									ok = true;
								}else {
									wrong = true;
								}
								result.getQuestionScoringItems().put(element.getUniqueId(), scoringItem);
							}
							
							if (scoringItem.getType() == -1)
							{
								defaultItem = scoringItem;
							}
						}
						if (ok)
						{
							score += question.getQuizPoints();
							currentSectionScore += question.getQuizPoints();
							result.getQuestionScores().put(question.getUniqueId(), question.getQuizPoints());
						} else if (wrong)
						{
							result.getQuestionScores().put(question.getUniqueId(), 0);
						} else if (defaultItem != null && defaultItem.isCorrect())
						{
							score += question.getQuizPoints();
							currentSectionScore += question.getQuizPoints();
							result.getQuestionScores().put(question.getUniqueId(), question.getQuizPoints());
							result.getQuestionScoringItems().put(element.getUniqueId(), defaultItem);
						}
					} else if (question instanceof FreeTextQuestion)
					{
						//get points if at least one correct rule is fulfilled
						boolean ok = false;
						boolean wrong = false;
						String value = !answers.isEmpty() ? answers.get(0).getValue() : null;
						ScoringItem defaultItem = null;
						for (ScoringItem scoringItem : question.getScoringItems())
						{
							if (ruleMatches(scoringItem, value))
							{
								if (scoringItem.isCorrect())
								{
									ok = true;
								}else {
									wrong = true;
								}
								result.getQuestionScoringItems().put(element.getUniqueId(), scoringItem);
							}
							
							if (scoringItem.getType() == -1)
							{
								defaultItem = scoringItem;
							}
						}
						if (ok)
						{
							score += question.getQuizPoints();
							currentSectionScore += question.getQuizPoints();
							result.getQuestionScores().put(question.getUniqueId(), question.getQuizPoints());
						} else if (wrong)
						{
							result.getQuestionScores().put(question.getUniqueId(), 0);
						} else if (defaultItem != null && defaultItem.isCorrect())
						{
							score += question.getQuizPoints();
							currentSectionScore += question.getQuizPoints();
							result.getQuestionScores().put(question.getUniqueId(), question.getQuizPoints());
							result.getQuestionScoringItems().put(element.getUniqueId(), defaultItem);
						}
					}
				} else if (question.getScoring() == 2) //individual points
				{
					if (question instanceof SingleChoiceQuestion)
					{
						ChoiceQuestion choice = (ChoiceQuestion) question;
						int max = 0;
						for (PossibleAnswer pa : choice.getAllPossibleAnswers()) {
							if (pa.getScoring() != null && pa.getScoring().getPoints() > max)
							{
								max = pa.getScoring().getPoints();
							}
						}
						maximumScore += max;
						currentSectionMaxScore += max;
						result.getQuestionMaximumScores().put(question.getUniqueId(), max);
						
						if (!answers.isEmpty())
						{
							Answer answer = answers.get(0);
							PossibleAnswer pa = choice.getPossibleAnswerByUniqueId(answer.getPossibleAnswerUniqueId());
							if (pa != null && pa.getScoring() != null)
							{
								score += pa.getScoring().getPoints();
								currentSectionScore += pa.getScoring().getPoints();
								result.getQuestionScores().put(question.getUniqueId(), pa.getScoring().getPoints());
							}
						}
					} else if (question instanceof MultipleChoiceQuestion)
					{
						MultipleChoiceQuestion choice = (MultipleChoiceQuestion) question;
						for (PossibleAnswer pa : choice.getAllPossibleAnswers()) {
							if (pa.getScoring() != null && pa.getScoring().getPoints() > 0)
							{
								maximumScore += pa.getScoring().getPoints();
								currentSectionMaxScore += pa.getScoring().getPoints();
								if (!result.getQuestionMaximumScores().containsKey(question.getUniqueId()))
								{
									result.getQuestionMaximumScores().put(question.getUniqueId(), pa.getScoring().getPoints());
								} else {
									result.getQuestionMaximumScores().put(question.getUniqueId(), result.getQuestionMaximumScores().get(question.getUniqueId()) + pa.getScoring().getPoints());
								}
							}
						}
			
						int qscore = 0;
								
						for (Answer answer : answers)
						{
							PossibleAnswer pa = choice.getPossibleAnswerByUniqueId(answer.getPossibleAnswerUniqueId());
							if (pa != null && pa.getScoring() != null)
							{
								qscore += pa.getScoring().getPoints();
								}
							}
						
						if (choice.getNoNegativeScore() != null && choice.getNoNegativeScore() && qscore < 0)
						{
							qscore = 0;
						}
						
						score += qscore;
						currentSectionScore += qscore;
						result.getQuestionScores().put(question.getUniqueId(), qscore);	
						
						if (!result.getQuestionScores().containsKey(question.getUniqueId()) || (result.getQuestionMaximumScores().containsKey(question.getUniqueId()) && (result.getQuestionMaximumScores().get(question.getUniqueId()) > result.getQuestionScores().get(question.getUniqueId()))))
						{
							result.getPartiallyAnswersMultipleChoiceQuestions().add(question.getUniqueId());
						}
					} else if (question instanceof NumberQuestion)
					{
						Double value = answers.isEmpty() ? null : Double.parseDouble(answers.get(0).getValue());
						List<ScoringItem> matches = new ArrayList<>();
						ScoringItem defaultItem = null;
						int max = 0;
						for (ScoringItem scoringItem : question.getScoringItems())
						{							
							if (scoringItem.getPoints() > max)
							{
								max = scoringItem.getPoints();
							}
							
							if (ruleMatches(scoringItem, value))
							{
								matches.add(scoringItem);
							}
							
							if (scoringItem.getType() == -1)
							{
								defaultItem = scoringItem;
							}
						}
						result.getQuestionMaximumScores().put(question.getUniqueId(), max);
						maximumScore += max;
						currentSectionMaxScore += max;
						if (!matches.isEmpty())
						{
							ScoringItem bestMatch = getBestMatch(matches);
							score += bestMatch.getPoints();
							currentSectionScore += bestMatch.getPoints();
							result.getQuestionScores().put(question.getUniqueId(), bestMatch.getPoints());
							result.getQuestionScoringItems().put(element.getUniqueId(), bestMatch);
						} else {
							if (defaultItem != null && defaultItem.isCorrect())
							{
								score += defaultItem.getPoints();
								currentSectionScore += defaultItem.getPoints();
								result.getQuestionScores().put(question.getUniqueId(), defaultItem.getPoints());
								result.getQuestionScoringItems().put(element.getUniqueId(), defaultItem);
							}
						}
					}  else if (question instanceof DateQuestion)
					{
						Date value = answers.isEmpty() ? null : ConversionTools.getDate(answers.get(0).getValue());
						List<ScoringItem> matches = new ArrayList<>();
						ScoringItem defaultItem = null;
						int max = 0;
						for (ScoringItem scoringItem : question.getScoringItems())
						{		
							if (scoringItem.getPoints() > max)
							{
								max = scoringItem.getPoints();
							}
							
							if (ruleMatches(scoringItem, value))
							{
								matches.add(scoringItem);
							}
							
							if (scoringItem.getType() == -1)
							{
								defaultItem = scoringItem;
							}
						}
						result.getQuestionMaximumScores().put(question.getUniqueId(), max);
						maximumScore += max;
						currentSectionMaxScore += max;
						if (!matches.isEmpty())
						{
							ScoringItem bestMatch = getBestMatch(matches);
							score += bestMatch.getPoints();
							currentSectionScore += bestMatch.getPoints();
							result.getQuestionScores().put(question.getUniqueId(), bestMatch.getPoints());
							result.getQuestionScoringItems().put(element.getUniqueId(), bestMatch);
						} else {
							if (defaultItem != null && defaultItem.isCorrect())
							{
								score += defaultItem.getPoints();
								currentSectionScore += defaultItem.getPoints();
								result.getQuestionScores().put(question.getUniqueId(), defaultItem.getPoints());
								result.getQuestionScoringItems().put(element.getUniqueId(), defaultItem);
							}
						}
					} else if (question instanceof FreeTextQuestion)
					{
						String value = answers.isEmpty() ? null : answers.get(0).getValue();
						List<ScoringItem> matches = new ArrayList<>();
						ScoringItem defaultItem = null;
						int max = 0;
						for (ScoringItem scoringItem : question.getScoringItems())
						{	
							if (scoringItem.getPoints() > max)
							{
								max = scoringItem.getPoints();
							}
							
							if (ruleMatches(scoringItem, value))
							{
								matches.add(scoringItem);
							}
							
							if (scoringItem.getType() == -1)
							{
								defaultItem = scoringItem;
							}
						}
						result.getQuestionMaximumScores().put(question.getUniqueId(), max);
						maximumScore += max;
						currentSectionMaxScore += max;
						if (!matches.isEmpty())
						{
							ScoringItem bestMatch = matches.get(0);
							score += bestMatch.getPoints();
							currentSectionScore += bestMatch.getPoints();
							result.getQuestionScores().put(question.getUniqueId(), bestMatch.getPoints());
							result.getQuestionScoringItems().put(element.getUniqueId(), bestMatch);
						} else {
							if (defaultItem != null && defaultItem.isCorrect())
							{
								score += defaultItem.getPoints();
								currentSectionScore += defaultItem.getPoints();
								result.getQuestionScores().put(question.getUniqueId(), defaultItem.getPoints());
								result.getQuestionScoringItems().put(element.getUniqueId(), defaultItem);
							}
						}
					}
				}
			}
		}
		
		if (currentSectionUid != null)
		{
			result.getSectionScores().put(currentSectionUid, currentSectionScore + Constants.PATH_DELIMITER + currentSectionMaxScore);
		}
		
		result.setScore(score);
		result.setMaximumScore(maximumScore);
		
		return result;
	}
	
	private static ScoringItem getBestMatch(List<ScoringItem> matches)
	{
		if (matches.size() == 1) return matches.get(0);
		
		//first equals
		for (ScoringItem scoringItem : matches) {
			if (scoringItem.getType() == 0)
			{
				return scoringItem;
			}
		}
		
		return matches.get(0);
	}
	
	private static boolean ruleMatches(ScoringItem scoringItem, String value)
	{
		if (scoringItem.getType() == 8) return value == null || value.length() == 0;		
		
		if (value == null || value.trim().length() == 0) return false;

		String ruleValue = scoringItem.getValue().trim();
		return ruleValue.equalsIgnoreCase(value.trim());
	}
	
	private static boolean ruleMatches(ScoringItem scoringItem, Double value)
	{
		if (scoringItem.getType() == 8) return value == null;		
		
		if (value == null) return false;
		
		if (scoringItem.getType() == -1)
		{
			//all other values
			return false;
		}
		
		if (scoringItem.getValue() == null || scoringItem.getValue().trim().length() == 0)
		{
			return false;
		}
		
		double ruleValue = Double.parseDouble(scoringItem.getValue());
		switch (scoringItem.getType())
		{
			case 0: //equals
				if (!Tools.isDoubleEqual(value, ruleValue))
				{
					return false;
				}
				break;
			case 1: //<
				if (value >= ruleValue)
				{
					return false;
				}
				break;
			case 2: //<=
				if (value > ruleValue)
				{
					return false;
				}
				break;
			case 3: //>
				if (value <= ruleValue)
				{
					return false;
				}
				break;
			case 4: //>=
				if (value < ruleValue)
				{
					return false;
				}
				break;
			case 5: //between
				if (scoringItem.getValue2() == null || scoringItem.getValue2().length() == 0) return false;
				double ruleValue2 = Double.parseDouble(scoringItem.getValue2());
				if (value < ruleValue || value > ruleValue2)
				{
					return false;
				}
				break;
			default:
				break;
		}
		
		return true;
	}
	
	private static boolean ruleMatches(ScoringItem scoringItem, Date value)
	{
		if (scoringItem.getType() == 8) return value == null;	
		
		if (value == null) return false;
		
		if (scoringItem.getType() == -1)
		{
			//all other values
			return false;
		}
		
		Date ruleValue = ConversionTools.getDate(scoringItem.getValue());
		switch (scoringItem.getType())
		{
			case 0: //equals
				if (!value.equals(ruleValue))
				{
					return false;
				}
				break;
			case 1: //<
				if (!value.before(ruleValue))
				{
					return false;
				}
				break;
			case 2: //<=
				if (value.after(ruleValue))
				{
					return false;
				}
				break;
			case 3: //>
				if (!value.after(ruleValue))
				{
					return false;
				}
				break;
			case 4: //>=
				if (value.before(ruleValue))
				{
					return false;
				}
				break;
			case 5: //between
				if (scoringItem.getValue2() == null || scoringItem.getValue2().length() == 0) return false;	
				Date ruleValue2 = ConversionTools.getDate(scoringItem.getValue2());
				if (value.before(ruleValue) || value.after(ruleValue2))
				{
					return false;
				}
				break;
			default:
				break;
		}
		
		return true;
	}

}
