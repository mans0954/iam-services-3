package org.openiam.bpm.response;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.mule.util.concurrent.DaemonThreadFactory;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.provision.dto.ProvisionUser;
import org.springframework.validation.beanvalidation.CustomValidatorBean;

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
    "memberAssociationType"
})
public class TaskWrapper implements Serializable {
	
	private static Logger LOG = Logger.getLogger(TaskWrapper.class);

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

	public TaskWrapper() {
		
	}
	
	public TaskWrapper(final Task task, final RuntimeService runtimeService) {
		id = task.getId();
		name = task.getName();
		owner = task.getOwner();
		priority = task.getPriority();
		processDefinitionId = task.getProcessDefinitionId();
		processInstanceId = task.getProcessInstanceId();
		taskDefinitionKey = task.getTaskDefinitionKey();
		parentTaskId = task.getParentTaskId();
		assignee = task.getAssignee();
		createdTime = task.getCreateTime();
		description = task.getDescription();
		dueDate = task.getDueDate();
		executionId = task.getExecutionId();
		setCustomVariables(runtimeService);
	}
	
	public TaskWrapper(final HistoricTaskInstance historyInstance) {
		id = historyInstance.getId();
		name = historyInstance.getName();
		owner = historyInstance.getOwner();
		priority = historyInstance.getPriority();
		processDefinitionId = historyInstance.getProcessDefinitionId();
		processInstanceId = historyInstance.getProcessInstanceId();
		taskDefinitionKey = historyInstance.getTaskDefinitionKey();
		parentTaskId = historyInstance.getParentTaskId();
		assignee = historyInstance.getAssignee();
		createdTime = historyInstance.getStartTime();
		description = historyInstance.getDescription();
		dueDate = historyInstance.getDueDate();
		endDate = historyInstance.getEndTime();
		executionId = historyInstance.getExecutionId();
		//setCustomVariables(runtimeService);
	}
	
	/**
	 * sets the custom variable objects that were given to Activiti throughout this task
	 * @param runtimeService
	 */
	private void setCustomVariables(final RuntimeService runtimeService) {
		if(StringUtils.isNotEmpty(executionId)) {
			try {
				final Map<String, Object> customVariables = runtimeService.getVariables(executionId);
				if(customVariables != null) {
					if(customVariables.containsKey(ActivitiConstants.REQUEST_METADATA_MAP.getName())) {
						requestMetadataMap = (LinkedHashMap<String, String>)customVariables.get(ActivitiConstants.REQUEST_METADATA_MAP.getName());
					}
					
					if(customVariables.containsKey(ActivitiConstants.EMPLOYEE_ID.getName())) {
						employeeId = (String)customVariables.get(ActivitiConstants.EMPLOYEE_ID.getName());
					}
					
					if(customVariables.containsKey(ActivitiConstants.ATTESTATION_URL.getName())) {
						customObjectURI = (String)customVariables.get(ActivitiConstants.ATTESTATION_URL.getName());
						customObjectURI = new StringBuilder(customObjectURI).append(String.format("?id=%s&taskId=%s", employeeId, id)).toString();
					}
					
					if(customVariables.containsKey(ActivitiConstants.DELETABLE.getName())) {
						deletable = ((Boolean)customVariables.get(ActivitiConstants.DELETABLE.getName())).booleanValue();
					}

                    if(customVariables.containsKey(ActivitiConstants.WORKFLOW_NAME.getName())) {
                        workflowName = (String)customVariables.get(ActivitiConstants.WORKFLOW_NAME.getName());
                    }

                    if(customVariables.containsKey(ActivitiConstants.ASSOCIATION_TYPE.getName())) {
                        associationType = (String)customVariables.get(ActivitiConstants.ASSOCIATION_TYPE.getName());
                    }

                    if(customVariables.containsKey(ActivitiConstants.ASSOCIATION_ID.getName())) {
                        associationId = (String)customVariables.get(ActivitiConstants.ASSOCIATION_ID.getName());
                    }

                    if(customVariables.containsKey(ActivitiConstants.MEMBER_ASSOCIATION_TYPE.getName())) {
                        memberAssociationType = (String)customVariables.get(ActivitiConstants.MEMBER_ASSOCIATION_TYPE.getName());
                    }

                    if(customVariables.containsKey(ActivitiConstants.MEMBER_ASSOCIATION_ID.getName())) {
                        memberAssociationId = (String)customVariables.get(ActivitiConstants.MEMBER_ASSOCIATION_ID.getName());
                    }
				}
			} catch(ActivitiException e) {
				LOG.warn(String.format("Could not fetch variables for Execution ID: %s.  Changes are that the task is completed.", executionId));
			} catch(Throwable e) {
			}
		}
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
