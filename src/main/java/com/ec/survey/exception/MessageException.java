package com.ec.survey.exception;

public class MessageException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MessageException(String message) {		
		super(message);
	}

	public MessageException(String message, Exception e) {
		super(message, e);
	}

}
