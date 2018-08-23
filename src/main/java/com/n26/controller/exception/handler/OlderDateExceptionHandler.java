package com.n26.controller.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.n26.controller.exception.OlderDateException;

@ControllerAdvice
@RestController
public class OlderDateExceptionHandler {
	
	/**
	 * Exception handler for transaction in future date 
	 * @return HTTP Status 204
	 */
	@ExceptionHandler(OlderDateException.class)
	protected ResponseEntity<Object> handleFutureDate() {
		System.out.println(">>>>>>>>>>>>>>>>>>> Older...");
		return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
	}
}
