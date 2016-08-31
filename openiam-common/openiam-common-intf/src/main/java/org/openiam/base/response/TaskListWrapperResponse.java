package org.openiam.base.response;

import org.openiam.base.ws.Response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by alexander on 26/08/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskListWrapperResponse", propOrder = {
        "taskListWrapper"
})
public class TaskListWrapperResponse extends Response {
    private TaskListWrapper taskListWrapper;

    public TaskListWrapper getTaskListWrapper() {
        return taskListWrapper;
    }

    public void setTaskListWrapper(TaskListWrapper taskListWrapper) {
        this.taskListWrapper = taskListWrapper;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TaskListWrapperResponse{");
        sb.append(super.toString());
        sb.append(", taskListWrapper=").append(taskListWrapper);
        sb.append('}');
        return sb.toString();
    }
}
