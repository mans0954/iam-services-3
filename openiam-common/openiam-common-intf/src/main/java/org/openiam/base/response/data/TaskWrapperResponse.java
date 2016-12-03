package org.openiam.base.response.data;


import org.openiam.base.response.TaskWrapper;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by alexander on 25/08/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskWrapperResponse", propOrder = {
})
public final class TaskWrapperResponse extends BaseDataResponse<TaskWrapper> {

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TaskWrapperResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
