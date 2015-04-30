package org.openiam.activiti.model.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskSearchBean", propOrder = {
	"assigneeId",
	"candidateId",
	"processDefinitionId",
	"memberAssociationId",
	"ownerId"
})
public class TaskSearchBean implements Serializable {
	
	public TaskSearchBean() {}

	private String assigneeId;
	private String candidateId;
	private String processDefinitionId;
	private String memberAssociationId;
	private String ownerId;
	
	public String getAssigneeId() {
		return assigneeId;
	}
	public TaskSearchBean setAssigneeId(String assigneeId) {
		this.assigneeId = assigneeId;
		return this;
	}
	public String getCandidateId() {
		return candidateId;
	}
	public TaskSearchBean setCandidateId(String candidateId) {
		this.candidateId = candidateId;
		return this;
	}
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}
	public TaskSearchBean setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
		return this;
	}
	public String getMemberAssociationId() {
		return memberAssociationId;
	}
	public TaskSearchBean setMemberAssociationId(String memberAssociationId) {
		this.memberAssociationId = memberAssociationId;
		return this;
	}
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	
	
	
}
