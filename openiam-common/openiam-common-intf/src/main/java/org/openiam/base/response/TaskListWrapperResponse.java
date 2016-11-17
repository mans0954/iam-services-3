package org.openiam.base.response;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by alexander on 26/08/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskListWrapperResponse", propOrder = {
})
public final class TaskListWrapperResponse extends BaseDataResponse<TaskListWrapper> {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TaskListWrapperResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
