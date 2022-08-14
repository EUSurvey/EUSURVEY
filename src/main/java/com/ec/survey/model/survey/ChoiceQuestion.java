package com.ec.survey.model.survey;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.ec.survey.tools.ElementHelper;

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

	public ChoiceQuestion(String title, String shortname, String uid) {
		super(title, shortname, uid);
	}
	
	@Column(name = "CHOICEORDER")
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}	
	
	@OneToMany(targetEntity=PossibleAnswer.class, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(foreignKey = @ForeignKey(javax.persistence.ConstraintMode.NO_CONSTRAINT),
			name = "ELEMENTS_ELEMENTS",
			joinColumns = @JoinColumn(name = "ELEMENTS_ID"),
			inverseJoinColumns = @JoinColumn(name = "possibleAnswers_ID"))
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
		
		int columns = 1;
		if (this instanceof SingleChoiceQuestion)
		{
			columns = ((SingleChoiceQuestion)this).getNumColumns();
		} else if (this instanceof MultipleChoiceQuestion)
		{
			columns = ((MultipleChoiceQuestion)this).getNumColumns();
		}
		
		orderedPossibleAnswers = ElementHelper.getOrderedPossibleAnswers(possibleAnswers, order, columns);
		
		return orderedPossibleAnswers;
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
		
		if (!missingPossibleAnswers.isEmpty())
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
