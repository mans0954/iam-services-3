package org.openiam.idm.srvc.meta.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.bpm.dto.AbstractWorkflowResponse;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SaveTemplateProfileResponse", propOrder = {
	"currentValue",
	"elementName",
	"plaintextPassword",
	"login",
	"userId"
})
public class SaveTemplateProfileResponse extends AbstractWorkflowResponse {
	
	private String currentValue;
	private String elementName;
	private String plaintextPassword;
	private String login;
	private String userId;
	
	public SaveTemplateProfileResponse() {
		super();
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

	public String getPlaintextPassword() {
		return plaintextPassword;
	}

	public void setPlaintextPassword(String plaintextPassword) {
		this.plaintextPassword = plaintextPassword;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	
	
	
}
