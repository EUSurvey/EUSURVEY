package com.ec.survey.tools;

public class InvalidEmailException extends Exception {
	private static final long serialVersionUID = 1L;
	private final Object element;

	public InvalidEmailException(Object element, String message)
	{
		super(message);
		this.element = element;
	}

	public Object getElement() {
		return element;
	}
}
