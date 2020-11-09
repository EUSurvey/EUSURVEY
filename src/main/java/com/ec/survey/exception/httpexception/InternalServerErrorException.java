package com.ec.survey.exception.httpexception;

/**
 * Represents a 500 exception that will be sent back to the browser
 */
public class InternalServerErrorException extends Exception {

    private static final long serialVersionUID = 1L;
    
    public InternalServerErrorException(Throwable cause) {
        super(cause);
    }

}
