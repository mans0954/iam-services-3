package org.openiam.bpm.request;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HistorySearchBean", propOrder = {
    "assigneeId",
    "completed",
    "dueBefore",
    "dueAfter",
    "taskOwnerId",
    "taskName",
    "taskDescription"
})
public class HistorySearchBean implements Serializable {
	private String assigneeId;
	private Boolean completed;
	
	@XmlSchemaType(name = "dateTime")
	private Date dueBefore;
	
	@XmlSchemaType(name = "dateTime")
	private Date dueAfter;
	private String taskOwnerId;
	private String taskName;
	private String taskDescription;
	
	public String getAssigneeId() {
		return assigneeId;
	}
	public void setAssigneeId(String assigneeId) {
		this.assigneeId = assigneeId;
	}
	public Boolean isCompleted() {
		return completed;
	}
	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}
	public Date getDueBefore() {
		return dueBefore;
	}
	public void setDueBefore(Date dueBefore) {
		this.dueBefore = dueBefore;
	}
	public Date getDueAfter() {
		return dueAfter;
	}
	public void setDueAfter(Date dueAfter) {
		this.dueAfter = dueAfter;
	}
	public String getTaskOwnerId() {
		return taskOwnerId;
	}
	public void setTaskOwnerId(String taskOwnerId) {
		this.taskOwnerId = taskOwnerId;
	}
	public Boolean getCompleted() {
		return completed;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getTaskDescription() {
		return taskDescription;
	}
	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}
	
	
	
}
