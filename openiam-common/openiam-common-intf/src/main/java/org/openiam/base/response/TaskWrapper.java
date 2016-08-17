package org.openiam.base.response;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskWrapper", propOrder = {
	"id",
	"name",
	"owner",
	"priority",
	"processDefinitionId",
	"processInstanceId",
	"taskDefinitionKey",
	"parentTaskId",
	"assignee",
	"createdTime",
	"description",
	"dueDate",
	"executionId",
	"endDate",
	"requestMetadataMap",
	"customObjectURI",
	"employeeId",
	"deletable",
    "workflowName",
    "associationType",
    "associationId",
    "memberAssociationId",
    "memberAssociationType",
	"startMembershipDate",
	"endMembershipDate",
	"userNotes",
	"attestationManagedSysFilter",
	"resourceId"
})
public class TaskWrapper implements Serializable {
	
	private static final Log LOG = LogFactory.getLog(TaskWrapper.class);

	private String id;
	private String name;
	private String owner;
	private Integer priority;
	private String processDefinitionId;
	private String processInstanceId;
	private String taskDefinitionKey;
	private String parentTaskId;
	private String assignee;
	private String customObjectURI;
	private String employeeId;
	private boolean deletable = true;
	
	private String resourceId;
	
	@XmlSchemaType(name = "dateTime")
	private Date createdTime;
	private String description;
	
	@XmlSchemaType(name = "dateTime")
	private Date dueDate;
	private String executionId;
	
	@XmlSchemaType(name = "dateTime")
	private Date endDate;
	
	private LinkedHashMap<String, String> requestMetadataMap;

    private String workflowName;
    private String associationType;
    private String associationId;
    private String memberAssociationType;
    private String memberAssociationId;

	@XmlSchemaType(name = "dateTime")
	private Date startMembershipDate;
	@XmlSchemaType(name = "dateTime")
	private Date endMembershipDate;
	private String userNotes;
	private List<String> attestationManagedSysFilter;


	public TaskWrapper() {
		
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

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getTaskDefinitionKey() {
		return taskDefinitionKey;
	}

	public void setTaskDefinitionKey(String taskDefinitionKey) {
		this.taskDefinitionKey = taskDefinitionKey;
	}

	public String getParentTaskId() {
		return parentTaskId;
	}

	public void setParentTaskId(String parentTaskId) {
		this.parentTaskId = parentTaskId;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getExecutionId() {
		return executionId;
	}

	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getStartMembershipDate() {
		return startMembershipDate;
	}

	public void setStartMembershipDate(Date startMembershipDate) {
		this.startMembershipDate = startMembershipDate;
	}

	public Date getEndMembershipDate() {
		return endMembershipDate;
	}

	public void setEndMembershipDate(Date endMembershipDate) {
		this.endMembershipDate = endMembershipDate;
	}

	public String getUserNotes() {
		return userNotes;
	}

	public void setUserNotes(String userNotes) {
		this.userNotes = userNotes;
	}

	public Map<String, String> getRequestMetadataMap() {
		return requestMetadataMap;
	}

	public void setRequestMetadataMap(LinkedHashMap<String, String> requestMetadataMap) {
		this.requestMetadataMap = requestMetadataMap;
	}

	public String getCustomObjectURI() {
		return customObjectURI;
	}

	public void setCustomObjectURI(String customObjectURI) {
		this.customObjectURI = customObjectURI;
	}
	
	public String getEmployeeId() {
		return employeeId;
	}

	public boolean isDeletable() {
		return deletable;
	}

	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	
	

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaskWrapper other = (TaskWrapper) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

    public String getWorkflowName() {
        return workflowName;
    }

    public String getAssociationType() {
        return associationType;
    }

    public String getAssociationId() {
        return associationId;
    }

    public String getMemberAssociationType() {
        return memberAssociationType;
    }

    public String getMemberAssociationId() {
        return memberAssociationId;
    }

	public List<String> getAttestationManagedSysFilter() {
		return attestationManagedSysFilter;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public void setAssociationType(String associationType) {
		this.associationType = associationType;
	}

	public void setAssociationId(String associationId) {
		this.associationId = associationId;
	}

	public void setMemberAssociationType(String memberAssociationType) {
		this.memberAssociationType = memberAssociationType;
	}

	public void setMemberAssociationId(String memberAssociationId) {
		this.memberAssociationId = memberAssociationId;
	}

	public void setAttestationManagedSysFilter(List<String> attestationManagedSysFilter) {
		this.attestationManagedSysFilter = attestationManagedSysFilter;
	}

	@Override
	public String toString() {
		return String
				.format("TaskWrapper [id=%s, name=%s, owner=%s, priority=%s, processDefinitionId=%s, processInstanceId=%s, taskDefinitionKey=%s, parentTaskId=%s, assignee=%s, createdTime=%s, description=%s, dueDate=%s, executionId=%s]",
						id, name, owner, priority, processDefinitionId,
						processInstanceId, taskDefinitionKey, parentTaskId,
						assignee, createdTime, description, dueDate,
						executionId);
	}
	
	
}
