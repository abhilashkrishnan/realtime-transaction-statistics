package com.n26.controller.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.n26.controller.exception.FutureDateException;

@ControllerAdvice
@RestController
public class FutureDateExceptionHandler {

	@ExceptionHandler(FutureDateException.class)
	protected ResponseEntity<Object> handleRequestInvalidFormat() {
		return new ResponseEntity<Object>(HttpStatus.UNPROCESSABLE_ENTITY);
	}
}
