package org.openiam.idm.srvc.pswd.rule;

import java.util.LinkedList;
import java.util.List;

import org.openiam.base.ws.ResponseCode;

public class PasswordRuleException extends Exception {
	
	public PasswordRuleException(final ResponseCode code) {
		this.code = code;
	}

	private ResponseCode code;
	private List<Object> responseValues;
	
	public List<Object> getResponseValues() {
		return responseValues;
	}

	public void setResponseValues(List<Object> responseValues) {
		this.responseValues = responseValues;
	}

	public ResponseCode getCode() {
		return code;
	}

	public void setCode(ResponseCode code) {
		this.code = code;
	}
	
	public void addResponseValue(final Object obj) {
		if(obj != null) {
			if(responseValues == null) {
				responseValues = new LinkedList<Object>();
			}
			responseValues.add(obj);
		}
	}
	
	public void addResponseValues(final Object... args) {
		if(args != null) {
			for(final Object obj : args) {
				addResponseValue(obj);
			}
		}
	}
}
