package com.ec.survey.tools;

public class InvalidXHTMLException extends Exception {
	private static final long serialVersionUID = 1L;
	private Object element;
	
	public static final String REQUIRED = "This field is required";
	public static final String TOOSHORT = "This text is not long enough";
	public static final String TOOLONG = "This text is too long";
	public static final String TOOSMALL = "This value is too small";
	public static final String TOOBIG = "This value is too big";
	public static final String NOTENOUGH = "Not enough answers selected";
	public static final String TOOMANY = "Too many answers selected";
	public static final String NOTUNIQUE = "This value already exists";
	public static final String NOTANUMBER = "This value is not a valid number";
	
	public InvalidXHTMLException(Object element, String message)
	{
		super(message);
		this.element = element;
	}

	public Object getElement() {
		return element;
	}

	public void setElement(Object element) {
		this.element = element;
	}
}
