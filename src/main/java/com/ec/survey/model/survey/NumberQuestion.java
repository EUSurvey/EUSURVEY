package com.ec.survey.model.survey;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.owasp.esapi.errors.ValidationException;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

/**
 * Represents a number question in a survey
 */
@Entity
@DiscriminatorValue("NUMBER")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class NumberQuestion extends Question {
	public static final String MAXLABEL = "MAXLABEL";
	public static final String MINLABEL = "MINLABEL";
	public static final String UNIT = "UNIT";
	private static final long serialVersionUID = 1L;

	public NumberQuestion() {}
	
	public NumberQuestion(String title, String shortname, String uid) {
		super(title, shortname, uid);
	}
	private int decimalPlaces;
	private String unit;
	private Double minD;
	private Double maxD;
	private String minLabel;
	private String maxLabel;
	private String display;
	private String initialSliderPosition;
	private Boolean displayGraduationScale;
	private Double maxDistance = -1.0;
	
	//this is for backward compatibility (serializer), do not remove!
	private double min;
	private double max;
	
	@Column(name = "DECIMALPLACES")
	public Integer getDecimalPlaces() {
		return decimalPlaces;
	}	
	public void setDecimalPlaces(Integer decimalPlaces) {
		this.decimalPlaces = decimalPlaces;
	}
	
	@Column(name = "MINNUMBER")
	public Double getMin() {
		return minD;
	}	
	public void setMin(Double minD) {
		this.minD = minD;
	}
		
	@Transient
	public String getMinString()
	{
		if (minD == null) return "";
		String s = String.valueOf(minD);
		if (s.endsWith(".0")) s = s.replace(".0", "");
		return s;
	}
	
	@Column(name = "MAXNUMBER")
	public Double getMax() {
		return maxD;
	}	
	public void setMax(Double maxD) {
		this.maxD = maxD;
	}
	
	@Transient
	public String getMaxString()
	{
		if (maxD == null) return "";
		String s = String.valueOf(maxD);
		if (s.endsWith(".0")) s = s.replace(".0", "");
		return s;
	}
	
	@Column(name = "NUMBERUNIT")
	public String getUnit() {
		return unit;
	}	
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	@Column(name = "MINLABEL")
	public String getMinLabel() {
		return minLabel != null ? minLabel : "";
	}
	public void setMinLabel(String minLabel) {
		this.minLabel = minLabel != null ? minLabel : "";
	}

	@Column(name = "MAXLABEL")
	public String getMaxLabel() {
		return maxLabel != null ? maxLabel : "";
	}
	public void setMaxLabel(String maxLabel) {
		this.maxLabel = maxLabel != null ? maxLabel : "";
	}
	
	@Column(name = "DISPLAY")
	public String getDisplay() {
		return (display != null && display.length() > 0) ? display: "Number";
	}
	public void setDisplay(String display) {
		this.display =  (display != null && display.length() > 0) ? display: "Number";
	}
	
	@Column(name = "INITSLIDER")
	public String getInitialSliderPosition() {
		return (initialSliderPosition != null && initialSliderPosition.length() > 0) ? initialSliderPosition: "Left";
	}
	public void setInitialSliderPosition(String initialSliderPosition) {
		this.initialSliderPosition = (initialSliderPosition != null && initialSliderPosition.length() > 0) ? initialSliderPosition: "Left";
	}
	
	@Column(name = "GRADSCALE")
	public Boolean getDisplayGraduationScale() {
		return displayGraduationScale == null ? false : displayGraduationScale;
	}
	public void setDisplayGraduationScale(Boolean displayGraduationScale) {
		this.displayGraduationScale = displayGraduationScale == null ? false : displayGraduationScale;
	}
	
	@Column(name = "MAXDISTANCEDOUBLE")
	public Double getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(Double maxDistance) {
		this.maxDistance = maxDistance != null ? maxDistance : -1.0;
	}
	
	public NumberQuestion copy(String fileDir) throws ValidationException
	{
		NumberQuestion copy = new NumberQuestion();
		baseCopy(copy);
		copy.setDecimalPlaces(decimalPlaces);
		copy.maxD = maxD;
		copy.minD = minD;
		copy.unit = unit;
		copy.display = display;
		copy.minLabel = minLabel;
		copy.maxLabel = maxLabel;
		copy.initialSliderPosition = initialSliderPosition;
		copy.displayGraduationScale = displayGraduationScale;
		copy.maxDistance = maxDistance;
		
		return copy;
	}
	
	@Transient
	@Override
	public String getCss()
	{
		String css = super.getCss();
		
		css += " number";
		
		if (minD != null)
		{
			css += " min" + minD;
		}
		
		if (maxD != null)
		{
			css += " max" + maxD;
		}
		
		//decimal places are always to be set
		
		css += " prec" + decimalPlaces;
		
		return css;
	}
	
	@Override
	public boolean differsFrom(Element element) {
		if (basicDiffersFrom(element)) return true;
		
		if (!(element instanceof NumberQuestion)) return true;
		
		NumberQuestion number = (NumberQuestion)element;
		
		if (getDecimalPlaces() != null && !getDecimalPlaces().equals(number.getDecimalPlaces())) return true;
		
		if (maxD == null && number.maxD != null) return true;
		if (minD == null && number.minD != null) return true;
		if (unit == null && number.unit != null) return true;
		if (minLabel == null && number.minLabel != null) return true;
		if (maxLabel == null && number.maxLabel != null) return true;
		
		if (maxD != null && !maxD.equals(number.maxD)) return true;
		if (minD != null && !minD.equals(number.minD)) return true;
		
		if (display != null && !display.equals(number.display)) return true;
		if (displayGraduationScale != null && !displayGraduationScale.equals(number.displayGraduationScale)) return true;
		if (initialSliderPosition != null && !initialSliderPosition.equals(number.initialSliderPosition)) return true;			

		if (!maxDistance.equals(number.maxDistance)) return true;
		
		return (unit != null && !unit.equals(number.unit));
	}

	//used during import process to upgrade older version of the class
	public void upgrade() {
		if (min > 0)
		{
			this.minD = min;
		}
		if (max > 0)
		{
			this.maxD = max;
		}
		if (minD != null && minD.equals(0.0))
		{
			this.minD = null;
		}
		if (maxD != null && maxD.equals(0.0))
		{
			this.maxD = null;
		}
	}
	
	@Transient
	public boolean isSlider() {
		return getDisplay().equals("Slider");
	}
	
	@Transient
	public boolean showStatisticsForNumberQuestion() {
		if (decimalPlaces > 0 || minD == null || maxD == null) {
			return false;
		}
		
		return (maxD - minD) <= 10;
	}
	
	@Transient
	public List<String> getAllPossibleAnswers() {
		List<String> answers = new ArrayList<>();
		
		if (!showStatisticsForNumberQuestion()) {
			return answers;
		}
		
		NumberFormat nf = DecimalFormat.getInstance();
		nf.setMaximumFractionDigits(0);
		
		double v = minD;
		while (v <= maxD) {
			answers.add(nf.format(v));
			v++;
		}
				
		return answers;
	}
	
	@Transient
	public String getAnswerWithPrefix(String answer) {
		return getId() + answer;
	}
}
