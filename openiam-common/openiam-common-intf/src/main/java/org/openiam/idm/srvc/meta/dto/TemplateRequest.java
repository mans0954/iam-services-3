package org.openiam.idm.srvc.meta.dto;

import org.openiam.base.request.BaseServiceRequest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TemplateRequest", 
	propOrder = { 
		"targetObjectId",
		"patternId",
		"templateId",
		"languageId",
		"isAdminRequest",
		"requestURI",
		"requesterId"
})
public class TemplateRequest extends BaseServiceRequest {

	private String targetObjectId;
	private String languageId;
	private String patternId;
	private String templateId;
	private String requestURI;
	private boolean isAdminRequest;
	private String requesterId;
	public String getTargetObjectId() {
		return targetObjectId;
	}
	public void setTargetObjectId(String targetObjectId) {
		this.targetObjectId = targetObjectId;
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
	public String getRequestURI() {
		return requestURI;
	}
	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}
	public String getRequesterId() {
		return requesterId;
	}

	public void setRequesterId(String requesterId) {
		this.requesterId = requesterId;
	}
}
