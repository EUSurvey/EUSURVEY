package com.ec.survey.model.survey;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.owasp.esapi.errors.ValidationException;
import javax.persistence.*;
import java.io.IOException;
import java.io.ObjectInputStream;
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

	private boolean useCheckboxes; //Used only by the Serializer

	private int numColumns = 1;
	private int minChoices;
	private int maxChoices;
	
	//Defaults to Listbox because of the old CHECKBOXES column that was Listbox with false
	private MultipleChoiceStyle multipleChoiceStyle = MultipleChoiceStyle.LIST;

	@Column(name = "CHECKBOXES")
	@JsonIgnore
	@Deprecated
	public boolean getUseCheckboxes() {
		return multipleChoiceStyle == MultipleChoiceStyle.CHECKBOX;
	}

	@Deprecated
	public void setUseCheckboxes(boolean useCheckboxes) {
		if (useCheckboxes){
			multipleChoiceStyle = MultipleChoiceStyle.CHECKBOX;
		}
	}

	@Column(name = "MULTIPLE_CHOICE_STYLE")
	@JsonIgnore
	public MultipleChoiceStyle getMultipleChoiceStyle(){
		return multipleChoiceStyle;
	}

	public void setMultipleChoiceStyle(MultipleChoiceStyle multipleChoiceStyle){
		if (multipleChoiceStyle != null){
			this.multipleChoiceStyle = multipleChoiceStyle;
		} //Otherwise defaults to LIST or is set via setUseCheckboxes
	}

	@Transient
	public String getChoiceType(){
		return multipleChoiceStyle.getText();
	}

	public void setChoiceType(String choiceType){
		multipleChoiceStyle = MultipleChoiceStyle.getFromText(choiceType);
		if (multipleChoiceStyle == null){
			multipleChoiceStyle = MultipleChoiceStyle.CHECKBOX;
		}
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

	public MultipleChoiceQuestion copy(String fileDir) throws ValidationException {
		MultipleChoiceQuestion copy = new MultipleChoiceQuestion();
		baseCopy(copy);
		copy.setOrder(getOrder());
		copy.numColumns = numColumns;
		copy.maxChoices = maxChoices;
		copy.minChoices = minChoices;
		copy.multipleChoiceStyle = multipleChoiceStyle;

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

		switch (multipleChoiceStyle){
			case CHECKBOX:
				css += " checkboxes";
				break;
			case LIST:
				css += " listbox";
				break;
			case EVOTE:
				css += " checkboxes";
				break;
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
				|| multipleChoiceStyle != multi.multipleChoiceStyle) {
			return true;
		}

		if (!Objects.equals(getOrder(), multi.getOrder()))
			return true;

		if (getPossibleAnswers().size() != multi.getPossibleAnswers().size())
			return true;

		for (int i = 0; i < getPossibleAnswers().size(); i++) {
			if (getPossibleAnswers().get(i).differsFrom(multi.getPossibleAnswers().get(i))) {
				return true;
			}
		}

		return false;
	}

	//This method is called by Javas Serializer API
	//https://docs.oracle.com/javase/7/docs/api/java/io/ObjectInputStream.html#readObject()
	private void readObject(ObjectInputStream inStream) throws ClassNotFoundException, IOException {
		inStream.defaultReadObject();  //Read all properties

		//Use the setters to patch the deprecated properties
		setUseCheckboxes(useCheckboxes);
	}
}
