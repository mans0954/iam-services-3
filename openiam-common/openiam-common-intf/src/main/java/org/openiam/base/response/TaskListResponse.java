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
