package com.ec.survey.model.survey;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.owasp.esapi.errors.ValidationException;
import com.ec.survey.tools.Tools;

import javax.persistence.*;
import java.util.Objects;

/**
 * Represents a multiple choice question in a survey
 */
@Entity
@DiscriminatorValue("MULTIPLECHOICE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MultipleChoiceQuestion extends ChoiceQuestion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MultipleChoiceQuestion() {
	}

	public MultipleChoiceQuestion(String title, String shortname, String uid) {
		super(title, shortname, uid);
	}

	private boolean useCheckboxes;
	private int numColumns = 1;
	private int minChoices;
	private int maxChoices;
	private boolean noNegativeScore;

	@Column(name = "CHECKBOXES")
	public boolean getUseCheckboxes() {
		return useCheckboxes;
	}

	public void setUseCheckboxes(boolean useCheckboxes) {
		this.useCheckboxes = useCheckboxes;
	}

	@Column(name = "NUMCOLUMNS")
	public int getNumColumns() {
		return numColumns;
	}

	public void setNumColumns(int numColumns) {
		this.numColumns = numColumns;
	}

	@Column(name = "MIN_CHOICES")
	public int getMinChoices() {
		return minChoices;
	}

	public void setMinChoices(int minChoices) {
		this.minChoices = minChoices;
	}

	@Column(name = "MAX_CHOICES")
	public int getMaxChoices() {
		return maxChoices;
	}

	public void setMaxChoices(int maxChoices) {
		this.maxChoices = maxChoices;
	}

	@Column(name = "NONEGATIVE")
	public Boolean getNoNegativeScore() {
		return noNegativeScore;
	}

	public void setNoNegativeScore(Boolean noNegativeScore) {
		this.noNegativeScore = noNegativeScore != null && noNegativeScore;
	}

	public MultipleChoiceQuestion copy(String fileDir) throws ValidationException {
		MultipleChoiceQuestion copy = new MultipleChoiceQuestion();
		baseCopy(copy);
		copy.setOrder(getOrder());
		copy.numColumns = numColumns;
		copy.maxChoices = maxChoices;
		copy.minChoices = minChoices;
		copy.useCheckboxes = useCheckboxes;

		for (PossibleAnswer possibleAnswer : getPossibleAnswers()) {
			PossibleAnswer answerCopy = possibleAnswer.copy(fileDir);
			copy.getPossibleAnswers().add(answerCopy);
		}

		return copy;
	}

	@Transient
	@Override
	public String getCss() {
		String css = super.getCss();

		css += " multiplechoice";

		if (useCheckboxes) {
			css += " checkboxes";
		} else {
			css += " listbox";
		}

		if (minChoices > 0) {
			css += " min" + minChoices;
		}

		if (maxChoices > 0) {
			css += " max" + maxChoices;
		}

		if (getReadonly()) {
			css += " readonly";
		}

		return css;
	}

	@Override
	public boolean differsFrom(Element element) {
		if (basicDiffersFrom(element))
			return true;

		if (!(element instanceof MultipleChoiceQuestion))
			return true;

		MultipleChoiceQuestion multi = (MultipleChoiceQuestion) element;

		if (numColumns != multi.numColumns || maxChoices != multi.maxChoices || minChoices != multi.minChoices
				|| useCheckboxes != multi.useCheckboxes) {
			return true;
		}

		if (!Objects.equals(getOrder(), multi.getOrder()))
			return true;

		if (getPossibleAnswers().size() != multi.getPossibleAnswers().size())
			return true;

		for (int i = 0; i < getPossibleAnswers().size(); i++) {
			if (!getPossibleAnswers().get(i).getTitle().equals(multi.getPossibleAnswers().get(i).getTitle()))
				return true;
			if (!getPossibleAnswers().get(i).getDependentElementsUIDString()
					.equals(multi.getPossibleAnswers().get(i).getDependentElementsUIDString()))
				return true;
			if (!Tools.isEqual(getPossibleAnswers().get(i).getShortname(),
					multi.getPossibleAnswers().get(i).getShortname()))
				return true;
			if (getPossibleAnswers().get(i).getScoring() != null && getPossibleAnswers().get(i).getScoring()
					.differsFrom(multi.getPossibleAnswers().get(i).getScoring())) {
					return true;
			}
		}

		return false;
	}
}
