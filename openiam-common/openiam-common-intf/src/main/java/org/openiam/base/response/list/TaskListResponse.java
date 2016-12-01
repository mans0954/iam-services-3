package org.openiam.base.response.list;

import org.openiam.base.response.TaskWrapper;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by alexander on 26/08/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskListResponse", propOrder = {
})
public class TaskListResponse extends BaseListResponse<TaskWrapper> {

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TaskListResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
