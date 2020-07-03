package com.ec.survey.tools;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.BadCredentialsException;



public class BadSurveyCredentialsException extends BadCredentialsException {
	
	protected static final Logger logger = Logger.getLogger(BadCredentialsException.class);
	
	public BadSurveyCredentialsException(String message) {		
		super(message);
		logger.error("BadSurveyCredentialsException".toUpperCase() + " HAS BEEN CALLED WITH MESSAGE " + message);
	}

	private static final long serialVersionUID = 1L;
}
