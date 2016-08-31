package org.openiam.base.response;

import org.openiam.base.ws.Response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by alexander on 25/08/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskWrapperResponse", propOrder = {
        "task"
})
public class TaskWrapperResponse extends Response {
    private TaskWrapper task;

    public TaskWrapper getTask() {
        return task;
    }

    public void setTask(TaskWrapper task) {
        this.task = task;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TaskWrapperResponse{");
        sb.append(super.toString());
        sb.append("task=").append(task);
        sb.append('}');
        return sb.toString();
    }
}
