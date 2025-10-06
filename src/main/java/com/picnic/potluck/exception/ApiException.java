package com.picnic.potluck.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
	private final String code;
	private final HttpStatus status;

	public ApiException(String code, String message, HttpStatus status) {
		super(message);
		this.code = code;
		this.status = status;
	}
	public String code() { return code; }
	public HttpStatus status() { return status; }
}
