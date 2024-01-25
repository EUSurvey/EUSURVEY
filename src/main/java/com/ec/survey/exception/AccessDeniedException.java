package com.ec.survey.exception;

import org.apache.log4j.Logger;

public class AccessDeniedException extends RuntimeException {
	
	protected static Logger logger = Logger.getLogger(AccessDeniedException.class);
	
	public AccessDeniedException(String message) {		
		super(message);
	}

	private static final long serialVersionUID = 1L;
}
