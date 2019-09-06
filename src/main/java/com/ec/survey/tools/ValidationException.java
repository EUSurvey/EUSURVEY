package com.ec.survey.tools;

import com.ec.survey.model.survey.Element;

public class ValidationException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private Element element;
	
	public ValidationException(Element element, String message)
	{
		super(message);
		this.element = element;
	}

	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}
	
}
