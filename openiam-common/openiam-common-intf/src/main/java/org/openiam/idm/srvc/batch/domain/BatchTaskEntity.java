package org.openiam.idm.srvc.batch.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.batch.dto.BatchTask;

@Entity
@Table(name = "BATCH_CONFIG")
@DozerDTOCorrespondence(BatchTask.class)
public class BatchTaskEntity implements Serializable {

	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "TASK_ID", length = 32)
	private String id;
	
	@Column(name = "TASK_NAME", length = 50)
    private String name;
	
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
    
	@Column(name = "PARAM1", length=4000)
	private String param1;
    
	@Column(name = "PARAM2", length=4000)
	private String param2;
	
	@Column(name = "PARAM3", length=4000)
    private String param3;
	
	@Column(name = "PARAM4", length=4000)
    private String param4;
	
	@Column(name = "EXECUTION_ORDER")
    private Integer executionOrder = new Integer(1);
    
	@Column(name = "SPRING_BEAN", length=100)
	private String springBean;
	
	@Column(name = "SPRING_BEAN_METHOD", length=100)
	private String springBeanMethod;
	
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

        BatchTaskEntity that = (BatchTaskEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

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
        sb.append("BatchTaskEntity");
        sb.append("{id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", enabled=").append(enabled);
        sb.append(", runOn=").append(runOn);
        sb.append(", lastExecTime=").append(lastExecTime);
        sb.append(", lastModifiedDate=").append(lastModifiedDate);
        sb.append(", taskUrl='").append(taskUrl).append('\'');
        sb.append(", cronExpression='").append(cronExpression).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append(", param1='").append(param1).append('\'');
        sb.append(", param2='").append(param2).append('\'');
        sb.append(", param3='").append(param3).append('\'');
        sb.append(", param4='").append(param4).append('\'');
        sb.append(", executionOrder=").append(executionOrder);
        sb.append('}');
        return sb.toString();
    }
}
