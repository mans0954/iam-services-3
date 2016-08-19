package org.openiam.exception;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.ws.ResponseCode;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PasswordRuleException", propOrder = { 
	"minBound", 
	"maxBound", 
	"code",
	"responseValues"
	})
public class PasswordRuleException extends Exception {
	private Object minBound;
	private Object maxBound;
	private ResponseCode code;
	private List<Object> responseValues;
	
	public PasswordRuleException() {
		super();
	}
	
	public PasswordRuleException(final ResponseCode code) {
		this.code = code;
	}
	
	public PasswordRuleException(final ResponseCode code, final Object[] args) {
		this.code = code;
		addResponseValues(args);
	}
	
	public Object getMinBound() {
		return minBound;
	}

	public void setMinBound(Object minBound) {
		this.minBound = minBound;
	}

	public Object getMaxBound() {
		return maxBound;
	}

	public void setMaxBound(Object maxBound) {
		this.maxBound = maxBound;
	}

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
