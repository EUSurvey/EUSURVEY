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
	
	public static QuizResult getQuizResult(AnswerSet answerSet, Survey survey) {	
		QuizResult result = new QuizResult();
		
		int score = 0;
		int maximumScore = 0;
		
		String currentSectionUid = null; 
		int currentSectionScore = 0;
		int currentSectionMaxScore = 0;
		
		for (Element element : survey.getElements()) {
			if (element instanceof Section)
			{
				if (currentSectionUid != null)
				{
					result.getSectionScores().put(currentSectionUid, currentSectionScore + "/" + currentSectionMaxScore);
				}
				currentSectionUid = element.getUniqueId();
				currentSectionScore = 0;
				currentSectionMaxScore = 0;
			} else if (element instanceof Question)
			{
				Question question = (Question)element;
				if (question.getScoring() == 0) continue;
				
				List<Answer> answers = answerSet.getAnswers(question.getId(), question.getUniqueId());
				
				if (question.getScoring() == 1) //global points
				{
					result.getQuestionMaximumScores().put(question.getUniqueId(), question.getPoints());
					maximumScore += question.getPoints();
					currentSectionMaxScore += question.getPoints();
					
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
							score += question.getPoints();
							currentSectionScore += question.getPoints();
							result.getQuestionScores().put(question.getUniqueId(), question.getPoints());
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
								score += question.getPoints();
								currentSectionScore += question.getPoints();
								result.getQuestionScores().put(question.getUniqueId(), question.getPoints());
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
							score += question.getPoints();
							currentSectionScore += question.getPoints();
							result.getQuestionScores().put(question.getUniqueId(), question.getPoints());
						} else if (wrong)
						{
							result.getQuestionScores().put(question.getUniqueId(), 0);
						} else if (defaultItem != null)
						{
							score += defaultItem.getPoints();
							currentSectionScore += defaultItem.getPoints();
							result.getQuestionScores().put(question.getUniqueId(), defaultItem.getPoints());
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
							score += question.getPoints();
							currentSectionScore += question.getPoints();
							result.getQuestionScores().put(question.getUniqueId(), question.getPoints());
						} else if (wrong)
						{
							result.getQuestionScores().put(question.getUniqueId(), 0);
						} else if (defaultItem != null)
						{
							score += defaultItem.getPoints();
							currentSectionScore += defaultItem.getPoints();
							result.getQuestionScores().put(question.getUniqueId(), defaultItem.getPoints());
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
							score += question.getPoints();
							currentSectionScore += question.getPoints();
							result.getQuestionScores().put(question.getUniqueId(), question.getPoints());
						} else if (wrong)
						{
							result.getQuestionScores().put(question.getUniqueId(), 0);
						} else if (defaultItem != null)
						{
							score += defaultItem.getPoints();
							currentSectionScore += defaultItem.getPoints();
							result.getQuestionScores().put(question.getUniqueId(), defaultItem.getPoints());
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
							if (defaultItem != null)
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
							if (defaultItem != null)
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
							if (defaultItem != null)
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
			result.getSectionScores().put(currentSectionUid, currentSectionScore + "/" + currentSectionMaxScore);
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
