package org.openiam.idm.srvc.batch.dto;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * BatchConfig represents configuration information for a batch task.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BatchConfig", propOrder = {
        "id",
        "name",
        "enabled",
        "lastExecTime",
        "taskUrl",
        "status",
        "param1",
        "param2",
        "param3",
        "param4",
        "executionOrder",
        "lastModifiedDate",
        "runOn",
        "springBean",
        "springBeanMethod",
        "cronExpression"
})
@DozerDTOCorrespondence(BatchTaskEntity.class)
public class BatchTask implements Serializable {

    private String id;
    private String name;
    private boolean enabled;
    @XmlSchemaType(name = "dateTime")
    private Date lastExecTime;
    @XmlSchemaType(name = "dateTime")
    private Date lastModifiedDate;
    @XmlSchemaType(name = "dateTime")
    private Date runOn;
    private String taskUrl;
    private String status;
    private String param1;
    private String param2;
    private String param3;
    private String param4;
    private Integer executionOrder = new Integer(1);
    private String springBean;
    private String springBeanMethod;
    private String cronExpression;

    public BatchTask() {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cronExpression == null) ? 0 : cronExpression.hashCode());
		result = prime * result + (enabled ? 1231 : 1237);
		result = prime * result
				+ ((executionOrder == null) ? 0 : executionOrder.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((lastExecTime == null) ? 0 : lastExecTime.hashCode());
		result = prime
				* result
				+ ((lastModifiedDate == null) ? 0 : lastModifiedDate.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BatchTask other = (BatchTask) obj;
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
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

	@Override
	public String toString() {
		return "BatchTask [id=" + id + ", name=" + name + ", enabled="
				+ enabled + ", lastExecTime=" + lastExecTime
				+ ", lastModifiedDate=" + lastModifiedDate + ", runOn=" + runOn
				+ ", taskUrl=" + taskUrl + ", status=" + status + ", param1="
				+ param1 + ", param2=" + param2 + ", param3=" + param3
				+ ", param4=" + param4 + ", executionOrder=" + executionOrder
				+ ", springBean=" + springBean + ", springBeanMethod="
				+ springBeanMethod + ", cronExpression=" + cronExpression + "]";
	}
	
	
}
