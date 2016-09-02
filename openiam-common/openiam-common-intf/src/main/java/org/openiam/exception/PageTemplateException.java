package org.openiam.exception;

import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;

public class PageTemplateException extends BasicDataServiceException {

	private String currentValue;
	private String elementName;
	
	public PageTemplateException(final ResponseCode code) {
		super(code);
	}
	

	public String getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}
	
	
}
