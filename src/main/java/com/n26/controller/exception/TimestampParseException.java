package com.n26.controller.exception;

public class TimestampParseException extends RuntimeException {
	private static final long serialVersionUID = -7495485184807972564L;

	public TimestampParseException() {
		super();
	}
	
	public TimestampParseException(String message) {
		super(message);
	}
}
