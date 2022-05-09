package com.ec.survey.tools;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.ec.survey.model.survey.PossibleAnswer;

public class ElementHelper {
	private static List<PossibleAnswer> sortByColumn(List<PossibleAnswer> answers, int numColumns)
	{
		if (numColumns <= 1) return answers;
		
		int rows = (int)Math.ceil(((double)answers.size()) / numColumns);
		
		List<List<PossibleAnswer>> answersByColumn = new ArrayList<>();
		for (int i = 0; i < numColumns; i++)
		{
			answersByColumn.add(new ArrayList<>());
		}
		int currentcolumn = 0;
		int currentcolumnsize = rows;
		PossibleAnswer[] answersvalues = answers.toArray(new PossibleAnswer[0]);
		for (int i = 0; i < answers.size(); i++)
		{
			if (answersByColumn.get(currentcolumn).size() == currentcolumnsize)
			{
				currentcolumn++;
			}
			answersByColumn.get(currentcolumn).add(answersvalues[i]);
		}
		
		int counter = answers.size();
		
		for (int i = 0; i < answersByColumn.size(); i++)
		{
			while (answersByColumn.get(i).size() < currentcolumnsize)
			{
				answersByColumn.get(i).add(null);
				counter++;
			}
		}
		
		List<PossibleAnswer> result = new ArrayList<>();
		for (int i = 0; i < counter; i++)
		{
			result.add(answersByColumn.get(i % numColumns).get(0));
			answersByColumn.get(i % numColumns).remove(0);
		}
		
		return result;
	}
	
	public static Collection<PossibleAnswer> getOrderedPossibleAnswers(List<PossibleAnswer> possibleAnswers, Integer order, int numColumns) {
		if (order != null && order == 1)
		{
		    final Collator instance = Collator.getInstance();
			
			Map<String, PossibleAnswer> answers = new TreeMap<>(instance);
			for (PossibleAnswer answer: possibleAnswers)
			{
				answers.put(answer.getStrippedTitleNoEscape(), answer);
			}
			return sortByColumn(new ArrayList<>(Arrays.asList(answers.values().toArray(new PossibleAnswer[0]))), numColumns);
		} else if (order != null && order == 2)
		{
			List<PossibleAnswer> answers = possibleAnswers;
			Collections.shuffle(answers);
			return sortByColumn(new ArrayList<>(Arrays.asList(answers.toArray(new PossibleAnswer[0]))), numColumns);
		} else {
			return sortByColumn(possibleAnswers, numColumns);	
		}
	}
}
