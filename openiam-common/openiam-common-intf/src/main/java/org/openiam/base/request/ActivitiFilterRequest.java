package org.openiam.base.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;

/**
 * Created by alexander on 25/08/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActivitiFilterRequest", propOrder = {
        "taskId",
        "executionId",
        "userId",
        "description",
        "fromDate",
        "toDate",
        "from",
        "size"
})
public class ActivitiFilterRequest extends BaseServiceRequest {
    String executionId;
    String taskId;
    String userId;
    String description;
    Date fromDate;
    Date toDate;
    int from;
    int size;

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ActivitiFilterRequest{");
        sb.append("executionId='").append(executionId).append('\'');
        sb.append(", taskId='").append(taskId).append('\'');
        sb.append(", userId='").append(userId).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", fromDate=").append(fromDate);
        sb.append(", toDate=").append(toDate);
        sb.append(", from=").append(from);
        sb.append(", size=").append(size);
        sb.append('}');
        return sb.toString();
    }
}
