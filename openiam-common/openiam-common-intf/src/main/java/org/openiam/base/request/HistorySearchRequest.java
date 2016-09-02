package org.openiam.base.request;

import org.openiam.activiti.model.dto.HistorySearchBean;

/**
 * Created by alexander on 26/08/16.
 */
public class HistorySearchRequest extends BaseServiceRequest {
    private HistorySearchBean searchBean;
    private int from;
    private int size;

    public HistorySearchBean getSearchBean() {
        return searchBean;
    }

    public void setSearchBean(HistorySearchBean searchBean) {
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
        final StringBuffer sb = new StringBuffer("HistorySearchRequest{");
        sb.append(super.toString());
        sb.append("searchBean=").append(searchBean);
        sb.append(", from=").append(from);
        sb.append(", size=").append(size);
        sb.append('}');
        return sb.toString();
    }
}
