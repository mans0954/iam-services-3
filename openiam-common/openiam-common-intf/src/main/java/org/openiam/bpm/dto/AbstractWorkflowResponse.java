package org.openiam.bpm.dto;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractWorkflowResponse", propOrder = {
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
	"processDefinitionVersion",
	"protectingResourceId",
	"processOwners"
})
public abstract class AbstractWorkflowResponse extends Response {

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
	private String protectingResourceId;
	private List<String> processOwners;
	
	protected AbstractWorkflowResponse() {}
	
	protected AbstractWorkflowResponse(final ResponseStatus code) {
		super(code);
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

	public String getProtectingResourceId() {
		return protectingResourceId;
	}

	public void setProtectingResourceId(String protectingResourceId) {
		this.protectingResourceId = protectingResourceId;
	}

	public List<String> getProcessOwners() {
		return processOwners;
	}
	
	public void addProcessOwner(final String owner) {
		if(owner != null) {
			if(this.processOwners == null) {
				this.processOwners = new LinkedList<String>();
			}
			this.processOwners.add(owner);
		}
	}

	public void setProcessOwners(List<String> processOwners) {
		this.processOwners = processOwners;
	}
	
	
}
