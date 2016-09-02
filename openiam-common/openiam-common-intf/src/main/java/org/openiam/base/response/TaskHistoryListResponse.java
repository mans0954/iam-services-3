package org.openiam.base.response;

import org.openiam.base.ws.Response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by alexander on 26/08/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskHistoryListResponse", propOrder = {
        "taskHistoryList"
})
public class TaskHistoryListResponse extends Response {
    private List<TaskHistoryWrapper> taskHistoryList;

    public List<TaskHistoryWrapper> getTaskHistoryList() {
        return taskHistoryList;
    }

    public void setTaskHistoryList(List<TaskHistoryWrapper> taskList) {
        this.taskHistoryList = taskHistoryList;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TaskHistoryListResponse{");
        sb.append(super.toString());
        sb.append(", taskHistoryList=").append(taskHistoryList);
        sb.append('}');
        return sb.toString();
    }
}
