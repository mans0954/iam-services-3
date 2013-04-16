package org.openiam.idm.srvc.meta.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SaveTemplateProfileResponse", propOrder = {
	"currentValue",
	"elementName"
})
public class SaveTemplateProfileResponse extends Response {
	
	private String currentValue;
	private String elementName;

	public SaveTemplateProfileResponse() {
		
	}
	
	public SaveTemplateProfileResponse(final ResponseStatus code) {
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
