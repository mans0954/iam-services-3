package org.openiam.idm.srvc.pswd.dto;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.ws.ResponseCode;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PasswordRule", propOrder = { 
	"minBound", 
	"maxBound", 
	"code",
	"responseValues"})
public class PasswordRule implements Serializable {
	private Object minBound;
	private Object maxBound;
	private ResponseCode code;
	private List<Object> responseValues;
	

	public PasswordRule() {}
	
	public PasswordRule(final ResponseCode code) {
		this.code = code;
	}
	
	public PasswordRule(final ResponseCode code, final Object[] args) {
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
	
	public Object[] getResponseValueAsArray() {
		Object[] retVal = null;
		if(responseValues != null) {
			retVal = new Object[responseValues.size()];
			for(int i = 0; i < responseValues.size(); i++) {
				retVal[i] = responseValues.get(i);
			}
		}
		return retVal;
	}
	

	public boolean hasMinBound() {
		return (minBound != null);
	}
	
	public boolean hasMaxBound() {
		return (maxBound != null);
	}
}
