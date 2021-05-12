package com.ec.survey.exception;

/**
 * Class used only for returning 403 error in webservice scenarios
 */
public class ForbiddenException extends Exception {

	private static final long serialVersionUID = 1L;

	public ForbiddenException() {
		super();
	}
	public ForbiddenException(Throwable cause) {
        super(cause);
    }
	
}
