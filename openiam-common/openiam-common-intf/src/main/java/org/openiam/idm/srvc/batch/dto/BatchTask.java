package org.openiam.idm.srvc.batch.dto;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * BatchConfig represents configuration information for a batch task.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BatchConfig", propOrder = {
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
public class BatchTask extends KeyNameDTO {

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

}
