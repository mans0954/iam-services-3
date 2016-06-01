package org.openiam.idm.searchbeans;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.batch.dto.BatchTask;
import org.openiam.idm.srvc.batch.dto.BatchTaskSchedule;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BatchTaskScheduleSearchBean", propOrder = {
	"completed",
	"running",
	"taskId",
	"nextScheduledRun",
	"nextScheduledRunFrom",
	"nextScheduledRunTo"
})
public class BatchTaskScheduleSearchBean extends AbstractSearchBean<BatchTaskSchedule, String> {
	
	public BatchTaskScheduleSearchBean() {}

	private Date nextScheduledRunFrom;
	private Date nextScheduledRunTo;
	
	private Date nextScheduledRun;
	private String taskId;
	private Boolean completed;
	private Boolean running;
	public Boolean getCompleted() {
		return completed;
	}
	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}
	public Boolean getRunning() {
		return running;
	}
	public void setRunning(Boolean running) {
		this.running = running;
	}
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
	public Date getNextScheduledRunFrom() {
		return nextScheduledRunFrom;
	}
	public void setNextScheduledRunFrom(Date nextScheduledRunFrom) {
		this.nextScheduledRunFrom = nextScheduledRunFrom;
	}
	public Date getNextScheduledRunTo() {
		return nextScheduledRunTo;
	}
	public void setNextScheduledRunTo(Date nextScheduledRunTo) {
		this.nextScheduledRunTo = nextScheduledRunTo;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((completed == null) ? 0 : completed.hashCode());
		result = prime
				* result
				+ ((nextScheduledRun == null) ? 0 : nextScheduledRun.hashCode());
		result = prime
				* result
				+ ((nextScheduledRunFrom == null) ? 0 : nextScheduledRunFrom
						.hashCode());
		result = prime
				* result
				+ ((nextScheduledRunTo == null) ? 0 : nextScheduledRunTo
						.hashCode());
		result = prime * result + ((running == null) ? 0 : running.hashCode());
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
		BatchTaskScheduleSearchBean other = (BatchTaskScheduleSearchBean) obj;
		if (completed == null) {
			if (other.completed != null)
				return false;
		} else if (!completed.equals(other.completed))
			return false;
		if (nextScheduledRun == null) {
			if (other.nextScheduledRun != null)
				return false;
		} else if (!nextScheduledRun.equals(other.nextScheduledRun))
			return false;
		if (nextScheduledRunFrom == null) {
			if (other.nextScheduledRunFrom != null)
				return false;
		} else if (!nextScheduledRunFrom.equals(other.nextScheduledRunFrom))
			return false;
		if (nextScheduledRunTo == null) {
			if (other.nextScheduledRunTo != null)
				return false;
		} else if (!nextScheduledRunTo.equals(other.nextScheduledRunTo))
			return false;
		if (running == null) {
			if (other.running != null)
				return false;
		} else if (!running.equals(other.running))
			return false;
		if (taskId == null) {
			if (other.taskId != null)
				return false;
		} else if (!taskId.equals(other.taskId))
			return false;
		return true;
	}

	
}
