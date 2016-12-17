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
@XmlType(name = "BaseTemplateRequestModel",
        propOrder = {
                "activitiRequestType",
                "pageTemplate",
                "languageId"
        })
public abstract class BaseTemplateRequestModel<TargetObject extends KeyDTO> extends KeyDTO  {
    private ActivitiRequestType activitiRequestType;
    private PageTempate pageTemplate;
    private String languageId;

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

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        BaseTemplateRequestModel that = (BaseTemplateRequestModel) o;

        if (activitiRequestType != that.activitiRequestType) return false;
        if (pageTemplate != null ? !pageTemplate.equals(that.pageTemplate) : that.pageTemplate != null) return false;
        return languageId != null ? languageId.equals(that.languageId) : that.languageId == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (activitiRequestType != null ? activitiRequestType.hashCode() : 0);
        result = 31 * result + (pageTemplate != null ? pageTemplate.hashCode() : 0);
        result = 31 * result + (languageId != null ? languageId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BaseRequestModel{" +
                "activitiRequestType=" + activitiRequestType +
                ", pageTemplate=" + pageTemplate +
                ", languageId='" + languageId + '\'' +
                '}';
    }
}
