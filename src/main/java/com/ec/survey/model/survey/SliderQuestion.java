package com.ec.survey.model.survey;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.owasp.esapi.errors.ValidationException;

import javax.persistence.*;

/**
 * Represents a slider question in a survey
 */
@Entity
@DiscriminatorValue("SLIDER")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SliderQuestion extends Question {
	
	private static final long serialVersionUID = 1L;

	public SliderQuestion() {}
	
	public SliderQuestion(String title, String shortname, String uid) {
		super(title, shortname, uid);
	}
	private int decimalPlaces;
	private Double minD;
	private Double maxD;
	private String minLabel;
	private String maxLabel;
	
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
	
	@Column(name = "MINLABEL")
	public String getMinLabel() {
		return minLabel;
	}
	public void setMinLabel(String minLabel) {
		this.minLabel = minLabel;
	}

	@Column(name = "MAXLABEL")
	public String getMaxLabel() {
		return maxLabel;
	}
	public void setMaxLabel(String maxLabel) {
		this.maxLabel = maxLabel;
	}
	
	public SliderQuestion copy(String fileDir) throws ValidationException
	{
		SliderQuestion copy = new SliderQuestion();
		baseCopy(copy);
		copy.setDecimalPlaces(decimalPlaces);
		copy.maxD = maxD;
		copy.minD = minD;
		copy.minLabel = minLabel;
		copy.maxLabel = maxLabel;
		
		return copy;
	}
	
	@Transient
	@Override
	public String getCss()
	{
		String css = super.getCss();
		
		css += " slider";
		
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
		
		if (!(element instanceof SliderQuestion)) return true;
		
		SliderQuestion slider = (SliderQuestion)element;
		
		if (getDecimalPlaces() != null && !getDecimalPlaces().equals(slider.getDecimalPlaces())) return true;
		
		if (maxD == null && slider.maxD != null) return true;
		if (minD == null && slider.minD != null) return true;
		if (minLabel == null && slider.minLabel != null) return true;
		if (maxLabel == null && slider.maxLabel != null) return true;
		
		if (maxD != null && !maxD.equals(slider.maxD)) return true;
		if (minD != null && !minD.equals(slider.minD)) return true;
		if (minLabel != null && !minLabel.equals(slider.minLabel)) return true;
		if (maxLabel != null && !maxLabel.equals(slider.maxLabel)) return true;
		
		return false;
	}
	
}
