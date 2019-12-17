package com.ec.survey.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Resolves to 404 error */
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class InvalidURLException extends Exception {

	private static final long serialVersionUID = 1L;

}
