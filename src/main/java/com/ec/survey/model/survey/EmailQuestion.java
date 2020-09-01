package com.ec.survey.model.survey;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.owasp.esapi.errors.ValidationException;

import javax.persistence.Cacheable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Represents an email question in a survey
 */
@Entity
@DiscriminatorValue("EMAIL")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EmailQuestion extends Question {
	
	private static final long serialVersionUID = 1L;

	public EmailQuestion() {}
	
	public EmailQuestion(String title, String shortname, String uid) {
		super(title, shortname, uid);
	}
	private String answer;
	
	@Transient
	public String getStringAnswer() {
		return answer;
	}	
	public void setStringAnswer(String answer) {
		this.answer = answer;
	}
	
	public EmailQuestion copy(String fileDir) throws ValidationException
	{
		EmailQuestion copy = new EmailQuestion();
		baseCopy(copy);
		copy.answer = answer;
				
		return copy;
	}
	
	@Transient
	@Override
	public String getCss()
	{
		String css = super.getCss();		
		css += " email";			
		return css;
	}
	
	@Override
	public boolean differsFrom(Element element) {
		return basicDiffersFrom(element);
	}

}
