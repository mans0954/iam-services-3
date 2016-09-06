package org.openiam.base.request;

import org.openiam.model.AccessViewFilterBean;

import java.util.Date;

/**
 * Created by alexander on 02/09/16.
 */
public class AccessReviewRequest extends BaseServiceRequest {
    private AccessViewFilterBean filterBean;
    private String viewType;
    private Date date;
    private String parentId;
    private String parentBeanType;
    private boolean isRootOnly;

    public AccessViewFilterBean getFilterBean() {
        return filterBean;
    }

    public void setFilterBean(AccessViewFilterBean filterBean) {
        this.filterBean = filterBean;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentBeanType() {
        return parentBeanType;
    }

    public void setParentBeanType(String parentBeanType) {
        this.parentBeanType = parentBeanType;
    }

    public boolean isRootOnly() {
        return isRootOnly;
    }

    public void setRootOnly(boolean rootOnly) {
        isRootOnly = rootOnly;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AccessReviewRequest{");
        sb.append(super.toString());
        sb.append("isRootOnly=").append(isRootOnly);
        sb.append(", parentBeanType='").append(parentBeanType).append('\'');
        sb.append(", parentId='").append(parentId).append('\'');
        sb.append(", date=").append(date);
        sb.append(", viewType='").append(viewType).append('\'');
        sb.append(", filterBean=").append(filterBean);
        sb.append('}');
        return sb.toString();
    }
}
