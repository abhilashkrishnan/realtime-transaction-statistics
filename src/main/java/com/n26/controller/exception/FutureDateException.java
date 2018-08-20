package com.n26.controller.exception;

public class FutureDateException extends RuntimeException {
	private static final long serialVersionUID = -7254329147303013661L;

	public FutureDateException() {
		super();
	}
	
	public FutureDateException(String message) {
		super(message);
	}
}
