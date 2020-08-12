package com.ec.survey.model.survey;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.owasp.esapi.errors.ValidationException;
import javax.persistence.*;

/**
 * Represents a time question in a survey
 */
@Entity
@DiscriminatorValue("TIMEQUESTION")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TimeQuestion extends Question {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TimeQuestion() {}
	
	public TimeQuestion(String title, String shortname, String uid) {
		super(title, shortname, uid);
	}
	
	private String min;
	private String max;
	
	@Column(name = "MINTIME")
	public String getMin() {
		return min;
	}	
	public void setMin(String min) {
		this.min = min;
	}
	
	@Column(name = "MAXTIME")
	public String getMax() {
		return max;
	}	
	public void setMax(String max) {
		this.max = max;
	}
	
	public TimeQuestion copy(String fileDir) throws ValidationException
	{
		TimeQuestion copy = new TimeQuestion();		
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
		
		css += " time";
		
		if (min != null && min.length() > 0)
		{
			css += " min" + min.replace(":", "");
		}
		
		if (max != null && max.length() > 0)
		{
			css += " max" + max.replace(":", "");
		}
		
		return css;
	}

	@Override
	public boolean differsFrom(Element element) {
		if (basicDiffersFrom(element)) return true;
		
		if (!(element instanceof TimeQuestion)) return true;
		
		TimeQuestion date = (TimeQuestion)element;

		return ((min != null && !min.equals(date.min)) || (max != null && !max.equals(date.max)));
	}
	
}
