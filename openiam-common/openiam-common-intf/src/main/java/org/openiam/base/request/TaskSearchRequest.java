package org.openiam.base.request;

import org.openiam.activiti.model.dto.TaskSearchBean;

/**
 * Created by alexander on 26/08/16.
 */
public class TaskSearchRequest extends BaseServiceRequest{
    private TaskSearchBean searchBean;
    private int from;
    private int size;

    public TaskSearchBean getSearchBean() {
        return searchBean;
    }

    public void setSearchBean(TaskSearchBean searchBean) {
        this.searchBean = searchBean;
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
        final StringBuffer sb = new StringBuffer("TaskSearchRequest{");
        sb.append(super.toString());
        sb.append("searchBean=").append(searchBean);
        sb.append(", from=").append(from);
        sb.append(", size=").append(size);
        sb.append('}');
        return sb.toString();
    }
}
