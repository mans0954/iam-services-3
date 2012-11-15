package org.openiam.base.ws.exception;

import org.openiam.base.ws.ResponseCode;

public class BasicDataServiceException extends Exception {

	private ResponseCode code;
	
	public BasicDataServiceException(final ResponseCode code) {
		this.code = code;
	}

	public ResponseCode getCode() {
		return code;
	}

	public void setCode(ResponseCode code) {
		this.code = code;
	}
	
	
}
