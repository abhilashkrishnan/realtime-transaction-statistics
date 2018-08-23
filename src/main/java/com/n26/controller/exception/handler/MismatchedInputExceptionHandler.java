package com.n26.controller.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;

@ControllerAdvice
@RestController
public class MismatchedInputExceptionHandler {
	
	/**
	 * Exception handler for JSON type mismatch
	 * @return HTTP Status 400
	 */
	@ExceptionHandler(MismatchedInputException.class)
	protected ResponseEntity<Object> handleTypeMismatch() {
		return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
	}
}
