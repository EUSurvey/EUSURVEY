package com.ec.survey.exception;

import org.apache.log4j.Logger;

public class TooManyFiltersException extends Exception {
	
	protected static Logger logger = Logger.getLogger(TooManyFiltersException.class);
	
	public TooManyFiltersException(String message) {		
		super(message);
	}

	private static final long serialVersionUID = 1L;
}
