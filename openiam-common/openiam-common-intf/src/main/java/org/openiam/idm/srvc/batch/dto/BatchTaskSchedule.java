package org.openiam.idm.srvc.batch.dto;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.Type;
import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.idm.srvc.batch.domain.BatchTaskScheduleEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BatchTaskSchedule", propOrder = {
	"taskId",
	"nextScheduledRun",
	"completed",
	"running"
})
@DozerDTOCorrespondence(BatchTaskScheduleEntity.class)
public class BatchTaskSchedule extends KeyDTO {
	
	
	public BatchTaskSchedule() {}

	private String taskId;
	private Date nextScheduledRun;
	private boolean completed;
	private boolean running;
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public Date getNextScheduledRun() {
		return nextScheduledRun;
	}
	public void setNextScheduledRun(Date nextScheduledRun) {
		this.nextScheduledRun = nextScheduledRun;
	}
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	public boolean isRunning() {
		return running;
	}
	public void setRunning(boolean running) {
		this.running = running;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (completed ? 1231 : 1237);
		result = prime
				* result
				+ ((nextScheduledRun == null) ? 0 : nextScheduledRun.hashCode());
		result = prime * result + (running ? 1231 : 1237);
		result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BatchTaskSchedule other = (BatchTaskSchedule) obj;
		if (completed != other.completed)
			return false;
		if (nextScheduledRun == null) {
			if (other.nextScheduledRun != null)
				return false;
		} else if (!nextScheduledRun.equals(other.nextScheduledRun))
			return false;
		if (running != other.running)
			return false;
		if (taskId == null) {
			if (other.taskId != null)
				return false;
		} else if (!taskId.equals(other.taskId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "BatchTaskSchedule [taskId=" + taskId + ", nextScheduledRun="
				+ nextScheduledRun + ", completed=" + completed + ", running="
				+ running + ", id=" + id + ", objectState=" + objectState
				+ ", requestorSessionID=" + requestorSessionID
				+ ", requestorUserId=" + requestorUserId + ", requestorLogin="
				+ requestorLogin + ", requestClientIP=" + requestClientIP + "]";
	}
	
	
}
