package org.openiam.bpm.response;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.activiti.engine.history.HistoricActivityInstance;
import org.openiam.idm.srvc.user.domain.UserEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskHistoryWrapper", propOrder = {
	"id",
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
	"nextIds"
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

	public TaskHistoryWrapper() {}
	
	public TaskHistoryWrapper(final HistoricActivityInstance instance) {
		this.id = instance.getId();
		this.activityId = instance.getActivityId();
		this.activityName = instance.getActivityName();
		this.activityType = instance.getActivityType();
		this.assigneeId = instance.getAssignee();
		this.duration = instance.getDurationInMillis();
		this.endTime = instance.getEndTime();
		this.executionId = instance.getExecutionId();
		this.processDefinitionId = instance.getProcessDefinitionId();
		this.processInstanceId = instance.getProcessInstanceId();
		this.startTime = instance.getStartTime();
	}
	
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
}
