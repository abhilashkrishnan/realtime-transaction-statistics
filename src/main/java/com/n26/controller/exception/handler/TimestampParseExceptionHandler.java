package com.n26.controller.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.n26.controller.exception.TimestampParseException;

@ControllerAdvice
@RestController
public class TimestampParseExceptionHandler {
	
	/**
	 * Exception handler for timestamp parsing errors
	 * @return HTTP Status 422
	 */
	@ExceptionHandler(TimestampParseException.class)
	public ResponseEntity<Object> handleParseError() {
		return new ResponseEntity<Object>(HttpStatus.UNPROCESSABLE_ENTITY);
	}
}
