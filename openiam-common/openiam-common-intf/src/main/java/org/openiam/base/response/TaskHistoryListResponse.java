package org.openiam.base.response;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by alexander on 26/08/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskHistoryListResponse", propOrder = {
})
public class TaskHistoryListResponse extends BaseListResponse<TaskHistoryWrapper> {

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TaskHistoryListResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
