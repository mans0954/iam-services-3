package org.openiam.base.ws.exception;

import org.openiam.base.ws.ResponseCode;

public class BasicDataServiceException extends Exception {

	private ResponseCode code;
	private String responseValue;
	
	public BasicDataServiceException(final ResponseCode code) {
		this.code = code;
	}
	
	public BasicDataServiceException(final ResponseCode code, final String responseValue) {
		this.code = code;
		this.responseValue = responseValue;
	}

	public ResponseCode getCode() {
		return code;
	}

	public void setCode(ResponseCode code) {
		this.code = code;
	}
	
	public String getResponseValue() {
		return responseValue;
	}
}
