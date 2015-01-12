package org.openiam.idm.srvc.batch.domain;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.batch.dto.BatchTask;
import org.openiam.idm.srvc.batch.dto.BatchTaskSchedule;
import org.openiam.idm.srvc.res.domain.ResourceEntity;

@Entity
@Table(name = "BATCH_SCHEDULE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "BATCH_SCHEDLE_ID", length = 32))
})
@DozerDTOCorrespondence(BatchTaskSchedule.class)
public class BatchTaskScheduleEntity extends KeyEntity {

	@ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = true, updatable = false)
    private BatchTaskEntity task;
	
	@Column(name = "NEXT_SCHEDULED_RUN", length=19)
    private Date nextScheduledRun;
	
	@Column(name = "COMPLETED")
	@Type(type = "yes_no")
    private boolean completed;
	
	@Column(name = "IS_RUNNING")
	@Type(type = "yes_no")
    private boolean running;

	public BatchTaskEntity getTask() {
		return task;
	}

	public void setTask(BatchTaskEntity task) {
		this.task = task;
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
		result = prime * result + ((task == null) ? 0 : task.hashCode());
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
		BatchTaskScheduleEntity other = (BatchTaskScheduleEntity) obj;
		if (completed != other.completed)
			return false;
		if (nextScheduledRun == null) {
			if (other.nextScheduledRun != null)
				return false;
		} else if (!nextScheduledRun.equals(other.nextScheduledRun))
			return false;
		if (running != other.running)
			return false;
		if (task == null) {
			if (other.task != null)
				return false;
		} else if (!task.equals(other.task))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BatchTaskScheduleEntity [task=" + task + ", nextScheduledRun="
				+ nextScheduledRun + ", completed=" + completed + ", running="
				+ running + ", id=" + id + "]";
	}

	
}
