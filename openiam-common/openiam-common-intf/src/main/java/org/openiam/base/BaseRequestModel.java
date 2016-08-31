package org.openiam.base;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.bpm.util.ActivitiRequestType;
import org.openiam.idm.srvc.meta.dto.PageTempate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by alexander on 28/12/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BaseRequestModel",
        propOrder = {
                "activitiRequestType",
                "pageTemplate",
        })
public abstract class BaseRequestModel<TargetObject extends KeyDTO> extends BaseServiceRequest {
    private ActivitiRequestType activitiRequestType;
    private PageTempate pageTemplate;

    public ActivitiRequestType getActivitiRequestType() {
        return activitiRequestType;
    }

    public void setActivitiRequestType(ActivitiRequestType activitiRequestType) {
        this.activitiRequestType = activitiRequestType;
    }

    public PageTempate getPageTemplate() {
        return pageTemplate;
    }

    public void setPageTemplate(PageTempate pageTemplate) {
        this.pageTemplate = pageTemplate;
    }

    public abstract TargetObject getTargetObject();
    public abstract void setTargetObject(TargetObject obj);

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        BaseRequestModel that = (BaseRequestModel) o;

        if (activitiRequestType != that.activitiRequestType) return false;
        if (pageTemplate != null ? !pageTemplate.equals(that.pageTemplate) : that.pageTemplate != null) return false;
        if (requesterId != null ? !requesterId.equals(that.requesterId) : that.requesterId != null) return false;
        return getLanguageId() != null ? getLanguageId().equals(that.getLanguageId()) : that.getLanguageId() == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (activitiRequestType != null ? activitiRequestType.hashCode() : 0);
        result = 31 * result + (pageTemplate != null ? pageTemplate.hashCode() : 0);
        result = 31 * result + (this.getLanguageId() != null ? getLanguageId().hashCode() : 0);
        result = 31 * result + (requesterId != null ? requesterId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BaseRequestModel{" +
                "activitiRequestType=" + activitiRequestType +
                ", pageTemplate=" + pageTemplate +
                ", languageId='" + getLanguageId() + '\'' +
                ", requesterId='" + requesterId + '\'' +
                '}';
    }
}
