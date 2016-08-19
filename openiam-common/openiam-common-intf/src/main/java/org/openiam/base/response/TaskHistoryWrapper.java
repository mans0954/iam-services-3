package org.openiam.base.response;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.user.domain.UserEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskHistoryWrapper", propOrder = {
	"id",
	"taskId",
	"activityId",
	"activityName",
	"activityType",
	"assigneeId",
	"assigneeName",
	"duration",
	"endTime",
	"executionId",
	"processDefinitionId",
	"processInstanceId",
	"startTime",
	"task",
	"nextIds",
	"calledProcessInstanceId",
	"tenantId",
	"variableDetails"
})
public class TaskHistoryWrapper {
	
	private String id;
	private String activityId;
	private String activityName;
	private String activityType;
	private String assigneeId;
	private String assigneeName;
	private Long duration;
	private Date endTime;
	private String executionId;
	private String processDefinitionId;
	private String processInstanceId;
	private Date startTime;
	private TaskWrapper task;
	private Set<String> nextIds;
	private String calledProcessInstanceId;
	private String taskId;
	private String tenantId;
	private ActivitiHistoricDetail variableDetails;
	
	public TaskHistoryWrapper() {}
	
	public void setUserInfo(final UserEntity user) {
		if(user != null) {
			this.assigneeName = user.getDisplayName();
		}
	}
	
	public void setTask(final TaskWrapper task) {
		this.task = task;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public String getAssigneeId() {
		return assigneeId;
	}

	public void setAssigneeId(String assigneeId) {
		this.assigneeId = assigneeId;
	}

	public String getAssigneeName() {
		return assigneeName;
	}

	public void setAssigneeName(String assigneeName) {
		this.assigneeName = assigneeName;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getExecutionId() {
		return executionId;
	}

	public void setExecutionId(String executionId) {
		this.executionId = executionId;
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

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public TaskWrapper getTask() {
		return task;
	}

	public Set<String> getNextIds() {
		return nextIds;
	}

	public void setNextIds(Set<String> nextIds) {
		this.nextIds = nextIds;
	}
	
	public void addNextTask(final String id) {
		if(id != null) {
			if(this.nextIds == null) {
				this.nextIds = new HashSet<String>();
			}
			this.nextIds.add(id);
		}
	}

	public String getCalledProcessInstanceId() {
		return calledProcessInstanceId;
	}

	public void setCalledProcessInstanceId(String calledProcessInstanceId) {
		this.calledProcessInstanceId = calledProcessInstanceId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public ActivitiHistoricDetail getVariableDetails() {
		return variableDetails;
	}

	public void setVariableDetails(ActivitiHistoricDetail variableDetails) {
		this.variableDetails = variableDetails;
	}
	
	
}
