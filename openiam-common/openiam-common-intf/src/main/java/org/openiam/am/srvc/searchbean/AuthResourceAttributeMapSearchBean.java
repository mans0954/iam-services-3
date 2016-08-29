package org.openiam.am.srvc.searchbean;

import org.openiam.am.srvc.dto.AuthResourceAttributeMap;
import org.openiam.idm.searchbeans.AbstractKeyNameSearchBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthResourceAttributeMapSearchBean", propOrder = {
        "providerId",
        "amAttributeId"
})
public class AuthResourceAttributeMapSearchBean extends AbstractKeyNameSearchBean<AuthResourceAttributeMap, String> {
    private String providerId;
    private String amAttributeId;

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getAmAttributeId() {
        return amAttributeId;
    }

    public void setAmAttributeId(String amAttributeId) {
        this.amAttributeId = amAttributeId;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((amAttributeId == null) ? 0 : amAttributeId.hashCode());
		result = prime * result
				+ ((providerId == null) ? 0 : providerId.hashCode());
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
		AuthResourceAttributeMapSearchBean other = (AuthResourceAttributeMapSearchBean) obj;
		if (amAttributeId == null) {
			if (other.amAttributeId != null)
				return false;
		} else if (!amAttributeId.equals(other.amAttributeId))
			return false;
		if (providerId == null) {
			if (other.providerId != null)
				return false;
		} else if (!providerId.equals(other.providerId))
			return false;
		return true;
	}

    
}
