package com.ec.survey.model.survey;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.*;

/**
 * The base class for single and multiple choice questions.
 * It contains a list of PossibleAnswer instances that
 * represent the answer options inside the question.
 */
@Entity
@DiscriminatorValue("CHOICE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public abstract class ChoiceQuestion extends Question {

	private static final long serialVersionUID = 1L;
	private List<PossibleAnswer> possibleAnswers = new ArrayList<>();
	private List<PossibleAnswer> missingPossibleAnswers = new ArrayList<>();
	private Integer order;
	private boolean foreditor;
	
	public ChoiceQuestion() {}

	public ChoiceQuestion(Survey survey, String title, String shortname, String uid) {
		super(survey, title, shortname, uid);
	}
	
	@Column(name = "CHOICEORDER")
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}	
	
	@OneToMany(targetEntity=PossibleAnswer.class, cascade = CascadeType.ALL)
	@Fetch(value = FetchMode.SELECT)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@OrderBy(value = "position asc")
	public List<PossibleAnswer> getPossibleAnswers() {
		return possibleAnswers;
	}	
	public void setPossibleAnswers(List<PossibleAnswer> answers) {
		this.possibleAnswers = answers;
	}
	
	private Collection<PossibleAnswer> orderedPossibleAnswers = null;
	
	@Transient
	public Collection<PossibleAnswer> getOrderedPossibleAnswers() {
		if (foreditor) return possibleAnswers;
		
		if (orderedPossibleAnswers != null) return orderedPossibleAnswers;
		
		if (order != null && order == 1)
		{
			Map<String, PossibleAnswer> answers = new TreeMap<>();
			for (PossibleAnswer answer: possibleAnswers)
			{
				answers.put(answer.getStrippedTitle(), answer);
			}
			orderedPossibleAnswers = sortByColumn(new ArrayList<>(Arrays.asList(answers.values().toArray(new PossibleAnswer[0]))));
		} else if (order != null && order == 2)
		{
			List<PossibleAnswer> answers = possibleAnswers;
			Collections.shuffle(answers);
			orderedPossibleAnswers = sortByColumn(new ArrayList<>(Arrays.asList(answers.toArray(new PossibleAnswer[0]))));
		} else {
			orderedPossibleAnswers = sortByColumn(possibleAnswers);	
		}
		
		return orderedPossibleAnswers;
	}
	
	private List<PossibleAnswer> sortByColumn(List<PossibleAnswer> answers)
	{
		int columns = 1;
		if (this instanceof SingleChoiceQuestion)
		{
			columns = ((SingleChoiceQuestion)this).getNumColumns();
		} else if (this instanceof MultipleChoiceQuestion)
		{
			columns = ((MultipleChoiceQuestion)this).getNumColumns();
		}
		
		if (columns == 1) return answers;
		
		int rows = (int)Math.ceil(((double)answers.size()) / columns);
		
		List<List<PossibleAnswer>> answersByColumn = new ArrayList<>();
		for (int i = 0; i < columns; i++)
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
			result.add(answersByColumn.get(i % columns).get(0));
			answersByColumn.get(i % columns).remove(0);
		}
		
		return result;
	}
	
	@Transient
	public Collection<Collection<PossibleAnswer>> getOrderedPossibleAnswersByRows() {
		Collection<PossibleAnswer> answers = getOrderedPossibleAnswers();
		
		Collection<Collection<PossibleAnswer>> result = new ArrayList<>();
		Collection<PossibleAnswer> currentRow = new ArrayList<>();
		result.add(currentRow);
		
		int columns = 1;
		if (this instanceof SingleChoiceQuestion)
		{
			columns = ((SingleChoiceQuestion)this).getNumColumns();
		} else if (this instanceof MultipleChoiceQuestion)
		{
			columns = ((MultipleChoiceQuestion)this).getNumColumns();
		}
		
		for (PossibleAnswer answer : answers)
		{
			if (currentRow.size() >= columns)
			{
				currentRow = new ArrayList<>();
				result.add(currentRow);
			}
			currentRow.add(answer);
		}		
		
		return result;
	}
	
	@Transient
	public List<PossibleAnswer> getMissingPossibleAnswers() {
		return missingPossibleAnswers;
	}	
	public void setMissingPossibleAnswers(List<PossibleAnswer> missingPossibleAnswers) {
		this.missingPossibleAnswers = missingPossibleAnswers;
	}
	
	@Transient
	public List<PossibleAnswer> getAllPossibleAnswers() {
		
		if (missingPossibleAnswers.size() > 0)
		{
			List<PossibleAnswer> result = new ArrayList<>();
			for (PossibleAnswer pa : missingPossibleAnswers)
			{
				if (!result.contains(pa))
				{
					result.add(pa);
				}
			}
			for (PossibleAnswer pa : possibleAnswers)
			{
				if (!result.contains(pa))
				{
					result.add(pa);
				}
			}
			
			result.sort(Survey.newElementByPositionComparator());
			
			return result;
		} else {
			return possibleAnswers;
		}		
		
	}	
	
	@Transient
	public PossibleAnswer getPossibleAnswer(int id) {
		for (PossibleAnswer possibleAnswer : getAllPossibleAnswers()) {
			if (possibleAnswer.getId() == id) {
				return possibleAnswer;
			}
		}
		return null;
	}	
	
	@Transient
	public PossibleAnswer getPossibleAnswerByUniqueId(String uid) {
		for (PossibleAnswer possibleAnswer : getAllPossibleAnswers()) {
			if (possibleAnswer.getUniqueId() != null && possibleAnswer.getUniqueId().length() > 0 && possibleAnswer.getUniqueId().equals(uid)) {
				return possibleAnswer;
			}
		}
		return null;
	}

	@Transient
	public boolean isForeditor() {
		return foreditor;
	}
	public void setForeditor(boolean foreditor) {
		this.foreditor = foreditor;
	}
	
}
