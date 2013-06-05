package org.openiam.idm.srvc.meta.exception;

import org.openiam.base.ws.ResponseCode;

public class PageTemplateException extends Exception {

	private ResponseCode code;
	private String currentValue;
	private String elementName;
	
	public PageTemplateException(final ResponseCode code) {
		this.code = code;
	}
	
	public ResponseCode getCode() {
		return code;
	}

	public String getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}

	public void setCode(ResponseCode code) {
		this.code = code;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}
	
	
}
