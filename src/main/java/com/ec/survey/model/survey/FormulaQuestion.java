package com.ec.survey.model.survey;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.owasp.esapi.errors.ValidationException;

import javax.persistence.*;

/**
 * Represents a formula question in a survey
 */
@Entity
@DiscriminatorValue("FORMULA")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class FormulaQuestion extends Question {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FormulaQuestion() {}
	
	public FormulaQuestion(String title, String shortname, String uid) {
		super(title, shortname, uid);
	}

	private String formula;
	private int decimalPlaces;
	private Double minD;
	private Double maxD;
	
	@Column(name = "FORMULA")
	public String getFormula() {
		return formula;
	}	
	public void setFormula(String formula) {
		this.formula = formula;
	}
	
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
	
	public FormulaQuestion copy(String fileDir) throws ValidationException
	{
		FormulaQuestion copy = new FormulaQuestion();
		baseCopy(copy);
		copy.setFormula(formula);
		copy.setDecimalPlaces(decimalPlaces);
		copy.setMax(maxD);
		copy.setMin(minD);
				
		return copy;
	}
	
	@Transient
	@Override
	public String getCss()
	{
		String css = super.getCss();
		css += " formula";	
		return css;
	}
	
	@Override
	public boolean differsFrom(Element element) {
		if (basicDiffersFrom(element)) return true;
		
		if (!(element instanceof FormulaQuestion)) return true;
		
		FormulaQuestion formula = (FormulaQuestion)element;		
	
		if (this.formula != null && !this.formula.equals(formula.formula)) return true;
		if (getDecimalPlaces() != null && !getDecimalPlaces().equals(formula.getDecimalPlaces())) return true;
		
		if (maxD == null && formula.maxD != null) return true;
		if (minD == null && formula.minD != null) return true;
		if (maxD != null && !maxD.equals(formula.maxD)) return true;
		if (minD != null && !minD.equals(formula.minD)) return true;
		
		return false;
	}
}
