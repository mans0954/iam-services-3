package org.openiam.idm.srvc.meta.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TemplateRequest", 
	propOrder = { 
		"userId",
		"localeName",
		"patternId",
		"templateId",
		"languageId",
		"languageCode",
		"isAdminRequest",
		"requestURI"
})
public class TemplateRequest {

	private String userId;
	private String localeName;
	private String languageId;
	private String patternId;
	private String templateId;
	private String languageCode;
	private String requestURI;
	private boolean isAdminRequest;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getLocaleName() {
		return localeName;
	}
	public void setLocaleName(String localeName) {
		this.localeName = localeName;
	}
	public String getPatternId() {
		return patternId;
	}
	public void setPatternId(String patternId) {
		this.patternId = patternId;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	
	public boolean isAdminRequest() {
		return isAdminRequest;
	}
	public void setAdminRequest(boolean isAdminRequest) {
		this.isAdminRequest = isAdminRequest;
	}
	public String getLanguageId() {
		return languageId;
	}
	public void setLanguageId(String languageId) {
		this.languageId = languageId;
	}
	public String getLanguageCode() {
		return languageCode;
	}
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
	public String getRequestURI() {
		return requestURI;
	}
	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}
	
}
