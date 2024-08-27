package com.ec.survey.model.survey;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.owasp.esapi.errors.ValidationException;

import com.ec.survey.model.selfassessment.SACriterion;
import com.ec.survey.model.selfassessment.SATargetDataset;

import javax.persistence.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
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
	private Boolean isTargetDatasetQuestion;
	private Boolean displayAllQuestions;
	private Boolean isSAQuestion;
	private SACriterion evaluationCriterion;
	private List<Integer> hiddenTargetDatasetIds = new ArrayList<Integer>();
	private List<SATargetDataset> targetDatasets = new ArrayList<SATargetDataset>();

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
	
	@Column(name = "TARGETDATASET")
	public Boolean getIsTargetDatasetQuestion() {
		return isTargetDatasetQuestion == null ? false : isTargetDatasetQuestion;
	}

	public void setIsTargetDatasetQuestion(Boolean isTargetDatasetQuestion) {
		this.isTargetDatasetQuestion = isTargetDatasetQuestion == null ? false : isTargetDatasetQuestion;
	}
	
	@Column(name = "DISPLAYALLQUESTIONS")
	public Boolean getDisplayAllQuestions() {
		return displayAllQuestions == null ? false : displayAllQuestions;
	}

	public void setDisplayAllQuestions(Boolean displayAllQuestions) {
		this.displayAllQuestions = displayAllQuestions == null ? false : displayAllQuestions;
	}
	
	@Column(name = "SAQUESTION")
	public Boolean getIsSAQuestion() {
		return isSAQuestion == null ? false : isSAQuestion;
	}

	public void setIsSAQuestion(Boolean isSAQuestion) {
		this.isSAQuestion = isSAQuestion == null ? false : isSAQuestion;
	}
	
	@ManyToOne
	@JoinColumn(name = "SACRITERION")
	public SACriterion getEvaluationCriterion() {
		return evaluationCriterion;
	}

	public void setEvaluationCriterion(SACriterion evaluationCriterion) {
		this.evaluationCriterion = evaluationCriterion;
	}


	public SingleChoiceQuestion copy(String fileDir) throws ValidationException {
		SingleChoiceQuestion copy = new SingleChoiceQuestion();
		baseCopy(copy);
		copy.numColumns = numColumns;
		copy.singleChoiceStyle = singleChoiceStyle;
		copy.setOrder(getOrder());
		copy.maxDistance = maxDistance;
		copy.isTargetDatasetQuestion = isTargetDatasetQuestion;
		copy.displayAllQuestions = displayAllQuestions;
		copy.isSAQuestion = isSAQuestion;
		copy.evaluationCriterion = evaluationCriterion;
		
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
		
		if (isTargetDatasetQuestion != single.isTargetDatasetQuestion)
			return true;
		
		if (isSAQuestion != single.isSAQuestion)
			return true;
		
		if (displayAllQuestions != single.displayAllQuestions)
			return true;
		
		if (!Objects.equals(evaluationCriterion, single.evaluationCriterion))
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

	@Transient
	public List<Integer> getHiddenTargetDatasetIds() {
		return hiddenTargetDatasetIds;
	}

	public void setHiddenTargetDatasetIds(List<Integer> hiddenTargetDatasetIds) {
		this.hiddenTargetDatasetIds = hiddenTargetDatasetIds;
	}

	@Transient
	public List<SATargetDataset> getTargetDatasets() {
		return targetDatasets;
	}

	public void setTargetDatasets(List<SATargetDataset> targetDatasets) {
		this.targetDatasets = targetDatasets;
	}

}
