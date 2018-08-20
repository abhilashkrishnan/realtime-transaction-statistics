package com.n26.controller.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@ControllerAdvice
@RestController
public class TypeMismatchExceptionHandler {

	@ExceptionHandler(InvalidFormatException.class)
	protected ResponseEntity<Object> handleTypeMismatch() {
		return new ResponseEntity<Object>(HttpStatus.UNPROCESSABLE_ENTITY);
	}
}
