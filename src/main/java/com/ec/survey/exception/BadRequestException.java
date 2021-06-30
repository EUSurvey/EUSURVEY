package com.ec.survey.exception;

/**
 * Class used only for returning 400 error in webservice scenarios
 */
public class BadRequestException extends Exception {

	private static final long serialVersionUID = 1L;

	public BadRequestException() {
		super();
	}
	public BadRequestException(Throwable cause) {
        super(cause);
    }
	
}
