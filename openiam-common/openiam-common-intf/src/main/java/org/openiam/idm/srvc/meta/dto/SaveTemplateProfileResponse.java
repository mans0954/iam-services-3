package org.openiam.idm.srvc.meta.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SaveTemplateProfileResponse", propOrder = {
	"currentValue",
	"elementName",
	"plaintextPassword",
	"login",
	"userId",
	"approverAssociationIds",
	"approverUserIds",
	"activityId",
	"businessKey",
	"deploymentId",
	"processInstanceId",
	"id",
	"name",
	"tenantId",
	"parentId",
	"processDefinitionId",
	"processDefinitionKey",
	"processDefinitionName",
	"processDefinitionVersion"
})
public class SaveTemplateProfileResponse extends Response {
	
	private String currentValue;
	private String elementName;
	private String plaintextPassword;
	private String login;
	private String userId;
	private List<String> approverAssociationIds;
	private List<String> approverUserIds;
	
	private String activityId;
	private String businessKey;
	private String deploymentId;
	private String processInstanceId;
	private String id;
	private String name;
	private String tenantId;
	private String parentId;
	private String processDefinitionId;
	private String processDefinitionKey;
	private String processDefinitionName;
	private Integer processDefinitionVersion;

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

	public List<String> getApproverAssociationIds() {
		return approverAssociationIds;
	}

	public void setApproverAssociationIds(List<String> approverAssociationIds) {
		this.approverAssociationIds = approverAssociationIds;
	}

	public List<String> getApproverUserIds() {
		return approverUserIds;
	}

	public void setApproverUserIds(List<String> approverUserIds) {
		this.approverUserIds = approverUserIds;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getBusinessKey() {
		return businessKey;
	}

	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	public String getDeploymentId() {
		return deploymentId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public String getProcessDefinitionKey() {
		return processDefinitionKey;
	}

	public void setProcessDefinitionKey(String processDefinitionKey) {
		this.processDefinitionKey = processDefinitionKey;
	}

	public String getProcessDefinitionName() {
		return processDefinitionName;
	}

	public void setProcessDefinitionName(String processDefinitionName) {
		this.processDefinitionName = processDefinitionName;
	}

	public Integer getProcessDefinitionVersion() {
		return processDefinitionVersion;
	}

	public void setProcessDefinitionVersion(Integer processDefinitionVersion) {
		this.processDefinitionVersion = processDefinitionVersion;
	}
	
	
}
