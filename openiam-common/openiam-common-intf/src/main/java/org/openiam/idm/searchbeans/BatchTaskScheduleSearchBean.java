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
public class BatchTaskScheduleSearchBean extends AbstractSearchBean<BatchTaskSchedule, String> implements SearchBean {
	
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
	public String getCacheUniqueBeanKey() {
		return new StringBuilder()
				.append(getKey() != null ? getKey() : "")
				.toString();
	}
}
