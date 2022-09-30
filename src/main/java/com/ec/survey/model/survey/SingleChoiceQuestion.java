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
 * Represents a single choice question in a survey
 */
@Entity
@DiscriminatorValue("SINGLECHOICE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SingleChoiceQuestion extends ChoiceQuestion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SingleChoiceQuestion() {
	}

	public SingleChoiceQuestion(String title, String shortname, String uid) {
		super(title, shortname, uid);
	}

	//These two are only used by the Serializer
	private boolean useRadioButtons;
	private Boolean useLikert;

	private int numColumns = 1;
	private Integer maxDistance = -1;
	//Defaults to Selectbox because of the old RADIO and LIKERT columns that defined SELECT with both false
	private SingleChoiceStyle singleChoiceStyle = SingleChoiceStyle.SELECT;

	@Column(name = "RADIO")
	@JsonIgnore
	@Deprecated
	public boolean getUseRadioButtons() {
		return singleChoiceStyle == SingleChoiceStyle.RADIO;
	}

	@Deprecated
	public void setUseRadioButtons(boolean useRadioButtons) {
		if (useRadioButtons){
			singleChoiceStyle = SingleChoiceStyle.RADIO;
		}
	}
	
	@Column(name = "LIKERT")
	@JsonIgnore
	@Deprecated
	public Boolean getUseLikert() {
		return singleChoiceStyle == SingleChoiceStyle.LIKERT;
	}

	@Deprecated
	public void setUseLikert(Boolean useLikert) {
		if (useLikert != null && useLikert){
			singleChoiceStyle = SingleChoiceStyle.LIKERT;
		}
	}

	@Column(name = "SINGLE_CHOICE_STYLE")
	@JsonIgnore
	public SingleChoiceStyle getSingleChoiceStyle(){
		return singleChoiceStyle;
	}

	public void setSingleChoiceStyle(SingleChoiceStyle singleChoiceStyle){
		if (singleChoiceStyle != null){
			this.singleChoiceStyle = singleChoiceStyle;
		} //Otherwise defaults to SELECT or is set via setUseLikert or setUseRadioButtons
	}

	@Transient
	public String getChoiceType(){
		return singleChoiceStyle.getText();
	}

	public void setChoiceType(String choiceType){
		singleChoiceStyle = SingleChoiceStyle.getFromText(choiceType);
		if (singleChoiceStyle == null){
			singleChoiceStyle = SingleChoiceStyle.RADIO;
		}
	}

	@Column(name = "NUMCOLUMNS")
	public int getNumColumns() {
		return numColumns;
	}

	public void setNumColumns(int numColumns) {
		this.numColumns = numColumns;
	}
	
	@Column(name = "MAXDISTANCE")
	public Integer getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(Integer maxDistance) {
		this.maxDistance = maxDistance != null ? maxDistance : -1;
	}

	public SingleChoiceQuestion copy(String fileDir) throws ValidationException {
		SingleChoiceQuestion copy = new SingleChoiceQuestion();
		baseCopy(copy);
		copy.numColumns = numColumns;
		copy.singleChoiceStyle = singleChoiceStyle;
		copy.setOrder(getOrder());
		copy.maxDistance = maxDistance;
		
		for (PossibleAnswer possibleAnswer : getPossibleAnswers()) {
			PossibleAnswer answerCopy = possibleAnswer.copy(fileDir);
			copy.getPossibleAnswers().add(answerCopy);
		}
		
		return copy;
	}

	@Override
	public boolean differsFrom(Element element) {
		if (basicDiffersFrom(element))
			return true;

		if (!(element instanceof SingleChoiceQuestion))
			return true;

		SingleChoiceQuestion single = (SingleChoiceQuestion) element;

		if (singleChoiceStyle != single.singleChoiceStyle)
			return true;
		if (numColumns != single.numColumns)
			return true;
		if (getPossibleAnswers().size() != single.getPossibleAnswers().size())
			return true;
		
		if (!maxDistance.equals(single.maxDistance)) //Integer objects don't compare with != just like Strings
			return true;

		if (!Objects.equals(getOrder(), single.getOrder()))
			return true;

		for (int i = 0; i < getPossibleAnswers().size(); i++) {
			if (getPossibleAnswers().get(i).differsFrom(single.getPossibleAnswers().get(i))) {
				return true;
			}
		}

		return false;
	}

	//This method is called by Javas Serializer API
	//https://docs.oracle.com/javase/7/docs/api/java/io/ObjectInputStream.html#readObject()
	private void readObject(ObjectInputStream inStream) throws ClassNotFoundException, IOException {
		inStream.defaultReadObject(); //Read all properties

		//Use the setters to patch the deprecated properties
		setUseRadioButtons(useRadioButtons);
		setUseLikert(useLikert);
	}

}
