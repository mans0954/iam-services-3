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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BatchTask batchTask = (BatchTask) o;

        if (id != null ? !id.equals(batchTask.id) : batchTask.id != null) return false;
        if (name != null ? !name.equals(batchTask.name) : batchTask.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("BatchTask");
        sb.append("{id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", enabled=").append(enabled);
        sb.append(", lastExecTime=").append(lastExecTime);
        sb.append(", lastModifiedDate=").append(lastModifiedDate);
        sb.append(", runOn=").append(runOn);
        sb.append(", taskUrl='").append(taskUrl).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append(", param1='").append(param1).append('\'');
        sb.append(", param2='").append(param2).append('\'');
        sb.append(", param3='").append(param3).append('\'');
        sb.append(", param4='").append(param4).append('\'');
        sb.append(", executionOrder=").append(executionOrder);
        sb.append(", cronExpression='").append(cronExpression).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
