package org.openiam.idm.searchbeans;

import org.openiam.idm.srvc.mngsys.dto.AttributeMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganizationSearchBean", propOrder = {
        "resourceId",
        "synchConfigId"
})
public class AttributeMapSearchBean extends AbstractSearchBean<AttributeMap, String> {
    private static final long serialVersionUID = 1L;

    private String resourceId;
    private String synchConfigId;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getSynchConfigId() {
        return synchConfigId;
    }

    public void setSynchConfigId(String synchConfigId) {
        this.synchConfigId = synchConfigId;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((resourceId == null) ? 0 : resourceId.hashCode());
		result = prime * result
				+ ((synchConfigId == null) ? 0 : synchConfigId.hashCode());
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
		AttributeMapSearchBean other = (AttributeMapSearchBean) obj;
		if (resourceId == null) {
			if (other.resourceId != null)
				return false;
		} else if (!resourceId.equals(other.resourceId))
			return false;
		if (synchConfigId == null) {
			if (other.synchConfigId != null)
				return false;
		} else if (!synchConfigId.equals(other.synchConfigId))
			return false;
		return true;
	}

    
}
