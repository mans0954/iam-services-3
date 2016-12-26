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
                "pageTemplate"
        })
public abstract class BaseTemplateRequestModel<TargetObject extends KeyDTO> extends KeyDTO  {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((activitiRequestType == null) ? 0 : activitiRequestType.hashCode());
		result = prime * result + ((pageTemplate == null) ? 0 : pageTemplate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseTemplateRequestModel other = (BaseTemplateRequestModel) obj;
		if (activitiRequestType != other.activitiRequestType)
			return false;
		if (pageTemplate == null) {
			if (other.pageTemplate != null)
				return false;
		} else if (!pageTemplate.equals(other.pageTemplate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BaseTemplateRequestModel [activitiRequestType=" + activitiRequestType + ", pageTemplate=" + pageTemplate
				+ "]";
	}

    
}
