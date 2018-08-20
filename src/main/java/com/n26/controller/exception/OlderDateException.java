package com.n26.controller.exception;

public class OlderDateException extends RuntimeException {
	private static final long serialVersionUID = -3108787691755037653L;

	public OlderDateException() {
		super();
	}
	
	public OlderDateException(String message) {
		super(message);
	}
}
