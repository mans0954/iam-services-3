package org.openiam.exception;

import org.openiam.base.ws.ResponseCode;

public class CustomActivitiException extends RuntimeException {

	private String message;
	private ResponseCode code;
	
	public CustomActivitiException(final ResponseCode code, final String message) {
		super();
		this.message = message;
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public ResponseCode getCode() {
		return code;
	}
	
	
}
