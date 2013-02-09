package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.domain.AuthResourceAMAttributeEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthResourceAMAttribute", propOrder = {
        "amAttributeId",
        "attributeName"
})
@DozerDTOCorrespondence(AuthResourceAMAttributeEntity.class)
public class AuthResourceAMAttribute implements Serializable {
    private String amAttributeId;
    private String attributeName;

    public String getAmAttributeId() {
        return amAttributeId;
    }

    public void setAmAttributeId(String amAttributeId) {
        this.amAttributeId = amAttributeId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((amAttributeId == null) ? 0 : amAttributeId.hashCode());
		result = prime * result
				+ ((attributeName == null) ? 0 : attributeName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuthResourceAMAttribute other = (AuthResourceAMAttribute) obj;
		if (amAttributeId == null) {
			if (other.amAttributeId != null)
				return false;
		} else if (!amAttributeId.equals(other.amAttributeId))
			return false;
		if (attributeName == null) {
			if (other.attributeName != null)
				return false;
		} else if (!attributeName.equals(other.attributeName))
			return false;
		return true;
	}
    
    
}
