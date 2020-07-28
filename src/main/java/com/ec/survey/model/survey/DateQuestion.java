package com.ec.survey.model.survey;


import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.Tools;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.owasp.esapi.errors.ValidationException;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Represents a date question in a survey
 */
@Entity
@DiscriminatorValue("DATEQUESTION")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DateQuestion extends Question {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DateQuestion() {}
	
	public DateQuestion(Survey survey, String title, String shortname, String uid) {
		super(title, shortname, uid);
	}
	
	private Date min;
	private Date max;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MIN")
	@DateTimeFormat(pattern=ConversionTools.DateFormat)
	public Date getMin() {
		return min;
	}	
	public void setMin(Date min) {
		this.min = min;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MAX")
	@DateTimeFormat(pattern=ConversionTools.DateFormat)
	public Date getMax() {
		return max;
	}	
	public void setMax(Date max) {
		this.max = max;
	}
	
	public DateQuestion copy(String fileDir) throws ValidationException
	{
		DateQuestion copy = new DateQuestion();		
		baseCopy(copy);
		copy.max = max;
		copy.min = min;
						
		return copy;
	}
	
	@Transient
	@Override
	public String getCss()
	{
		String css = super.getCss();
		
		css += " date";
		
		if (min != null)
		{
			css += " min" + Tools.formatDate(min, ConversionTools.DateFormat).replace("/", "");
		}
		
		if (max != null)
		{
			css += " max" + Tools.formatDate(max, ConversionTools.DateFormat).replace("/", "");
		}
		
		return css;
	}

	@Override
	public boolean differsFrom(Element element) {
		if (basicDiffersFrom(element)) return true;
		
		if (!(element instanceof DateQuestion)) return true;
		
		DateQuestion date = (DateQuestion)element;

		return ((min != null && !min.equals(date.min)) || (max != null && !max.equals(date.max)));
	}
	
	@Transient 
	public String getMinString()
	{
		return Tools.formatDate(min, ConversionTools.DateFormat);
	}
	
	@Transient 
	public String getMaxString()
	{
		return Tools.formatDate(max, ConversionTools.DateFormat);
	}
	
}
