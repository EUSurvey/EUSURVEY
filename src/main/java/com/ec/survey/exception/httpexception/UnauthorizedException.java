package com.ec.survey.exception.httpexception;

/**
 * Represents a 401 exception that will be sent back to the browser
 * This should be sent when a user is not yet logged - therefore doesn't have the right to perform an AJAX call
 */
public class UnauthorizedException extends Exception {

	private static final long serialVersionUID = 1L;

}
