package org.openiam.idm.srvc.batch.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.batch.dto.BatchTask;
import org.openiam.internationalization.Internationalized;

@Entity
@Table(name = "BATCH_CONFIG")
@DozerDTOCorrespondence(BatchTask.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "TASK_ID", length = 32)),
	@AttributeOverride(name = "name", column = @Column(name = "TASK_NAME", length = 50))
})
public class BatchTaskEntity extends AbstractKeyNameEntity {
	
	@Column(name = "ENABLED")
	@Type(type = "yes_no")
    private boolean enabled;
	
	@Column(name = "RUN_ON", length=19)
    private Date runOn;
	
	@Column(name = "LAST_EXEC_TIME", length=19)
    private Date lastExecTime;
	
	@Column(name = "LAST_MODIFIED_DATETIME", length=19)
    private Date lastModifiedDate;
	
	@Column(name = "TASK_URL",length=255)
	private String taskUrl;
	
	@Column(name = "CRON_EXPRESSION",length=100)
	private String cronExpression;
	
	@Column(name = "STATUS", length=20)
    private String status;
    
	@Column(name = "PARAM1", length=255)
	private String param1;
    
	@Column(name = "PARAM2", length=255)
	private String param2;
	
	@Column(name = "PARAM3", length=255)
    private String param3;
	
	@Column(name = "PARAM4", length=255)
    private String param4;
	
	@Column(name = "EXECUTION_ORDER")
    private Integer executionOrder = new Integer(1);
    
	@Column(name = "SPRING_BEAN", length=100)
	private String springBean;
	
	@Column(name = "SPRING_BEAN_METHOD", length=100)
	private String springBeanMethod;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="task", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<BatchTaskScheduleEntity> scheduledTasks;
	
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public Date getLastExecTime() {
		return lastExecTime;
	}
	public void setLastExecTime(Date lastExecTime) {
		this.lastExecTime = lastExecTime;
	}
	public String getTaskUrl() {
		return taskUrl;
	}
	public void setTaskUrl(String taskUrl) {
		this.taskUrl = taskUrl;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getParam1() {
		return param1;
	}
	public void setParam1(String param1) {
		this.param1 = param1;
	}
	public String getParam2() {
		return param2;
	}
	public void setParam2(String param2) {
		this.param2 = param2;
	}
	public String getParam3() {
		return param3;
	}
	public void setParam3(String param3) {
		this.param3 = param3;
	}
	public String getParam4() {
		return param4;
	}
	public void setParam4(String param4) {
		this.param4 = param4;
	}
	public Integer getExecutionOrder() {
		return executionOrder;
	}
	public void setExecutionOrder(Integer executionOrder) {
		this.executionOrder = executionOrder;
	}
	
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public Date getRunOn() {
		return runOn;
	}
	public void setRunOn(Date runOn) {
		this.runOn = runOn;
	}
	public String getSpringBean() {
		return springBean;
	}
	public void setSpringBean(String springBean) {
		this.springBean = springBean;
	}
	public String getSpringBeanMethod() {
		return springBeanMethod;
	}
	public void setSpringBeanMethod(String springBeanMethod) {
		this.springBeanMethod = springBeanMethod;
	}
	public String getCronExpression() {
		return cronExpression;
	}
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public Set<BatchTaskScheduleEntity> getScheduledTasks() {
		return scheduledTasks;
	}
	public void setScheduledTasks(Set<BatchTaskScheduleEntity> scheduledTasks) {
		this.scheduledTasks = scheduledTasks;
	}
	
	public void addScheduledTask(final BatchTaskScheduleEntity schedule) {
		if(this.scheduledTasks == null) {
			this.scheduledTasks = new HashSet<BatchTaskScheduleEntity>();
		}
		this.scheduledTasks.add(schedule);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((cronExpression == null) ? 0 : cronExpression.hashCode());
		result = prime * result + (enabled ? 1231 : 1237);
		result = prime * result
				+ ((executionOrder == null) ? 0 : executionOrder.hashCode());
		result = prime * result
				+ ((lastExecTime == null) ? 0 : lastExecTime.hashCode());
		result = prime
				* result
				+ ((lastModifiedDate == null) ? 0 : lastModifiedDate.hashCode());
		result = prime * result + ((param1 == null) ? 0 : param1.hashCode());
		result = prime * result + ((param2 == null) ? 0 : param2.hashCode());
		result = prime * result + ((param3 == null) ? 0 : param3.hashCode());
		result = prime * result + ((param4 == null) ? 0 : param4.hashCode());
		result = prime * result + ((runOn == null) ? 0 : runOn.hashCode());
		result = prime * result
				+ ((springBean == null) ? 0 : springBean.hashCode());
		result = prime
				* result
				+ ((springBeanMethod == null) ? 0 : springBeanMethod.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((taskUrl == null) ? 0 : taskUrl.hashCode());
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
		BatchTaskEntity other = (BatchTaskEntity) obj;
		if (cronExpression == null) {
			if (other.cronExpression != null)
				return false;
		} else if (!cronExpression.equals(other.cronExpression))
			return false;
		if (enabled != other.enabled)
			return false;
		if (executionOrder == null) {
			if (other.executionOrder != null)
				return false;
		} else if (!executionOrder.equals(other.executionOrder))
			return false;
		if (lastExecTime == null) {
			if (other.lastExecTime != null)
				return false;
		} else if (!lastExecTime.equals(other.lastExecTime))
			return false;
		if (lastModifiedDate == null) {
			if (other.lastModifiedDate != null)
				return false;
		} else if (!lastModifiedDate.equals(other.lastModifiedDate))
			return false;
		if (param1 == null) {
			if (other.param1 != null)
				return false;
		} else if (!param1.equals(other.param1))
			return false;
		if (param2 == null) {
			if (other.param2 != null)
				return false;
		} else if (!param2.equals(other.param2))
			return false;
		if (param3 == null) {
			if (other.param3 != null)
				return false;
		} else if (!param3.equals(other.param3))
			return false;
		if (param4 == null) {
			if (other.param4 != null)
				return false;
		} else if (!param4.equals(other.param4))
			return false;
		if (runOn == null) {
			if (other.runOn != null)
				return false;
		} else if (!runOn.equals(other.runOn))
			return false;
		if (springBean == null) {
			if (other.springBean != null)
				return false;
		} else if (!springBean.equals(other.springBean))
			return false;
		if (springBeanMethod == null) {
			if (other.springBeanMethod != null)
				return false;
		} else if (!springBeanMethod.equals(other.springBeanMethod))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (taskUrl == null) {
			if (other.taskUrl != null)
				return false;
		} else if (!taskUrl.equals(other.taskUrl))
			return false;
		return true;
	}
	
	
}
