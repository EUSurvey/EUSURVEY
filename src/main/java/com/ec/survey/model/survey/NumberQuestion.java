package com.ec.survey.model.survey;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.owasp.esapi.errors.ValidationException;

import javax.persistence.*;

/**
 * Represents a number question in a survey
 */
@Entity
@DiscriminatorValue("NUMBER")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class NumberQuestion extends Question {
	
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
	
	public NumberQuestion copy(String fileDir) throws ValidationException
	{
		NumberQuestion copy = new NumberQuestion();
		baseCopy(copy);
		copy.setDecimalPlaces(decimalPlaces);
		copy.maxD = maxD;
		copy.minD = minD;
		copy.unit = unit;
		
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
		
		if (maxD != null && !maxD.equals(number.maxD)) return true;
		if (minD != null && !minD.equals(number.minD)) return true;
		if (unit != null && !unit.equals(number.unit)) return true;
		
		return false;
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
	
}
