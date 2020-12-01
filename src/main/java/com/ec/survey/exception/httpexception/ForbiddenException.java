package com.ec.survey.exception.httpexception;

/**
 * Represents a 403 exception that will be sent back to the browser
 * This should be sent when a user is logged but doesn't have the right to perform an AJAX call
 */
public class ForbiddenException extends Exception {

	private static final long serialVersionUID = 1L;

}
