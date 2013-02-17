package org.openiam.am.srvc.domain;

import org.openiam.am.srvc.dto.AuthResourceAMAttribute;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "AUTH_RESOURCE_AM_ATTRIBUTE")
@DozerDTOCorrespondence(AuthResourceAMAttribute.class)
public class AuthResourceAMAttributeEntity implements Serializable {
    @Id
    @Column(name="AM_ATTRIBUTE_ID", length=100, nullable = false)
    private String amAttributeId;
    @Column(name="ATTRIBUTE_NAME", length=100, nullable = false)
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
		AuthResourceAMAttributeEntity other = (AuthResourceAMAttributeEntity) obj;
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

	@Override
	public String toString() {
		return String
				.format("AuthResourceAMAttributeEntity [amAttributeId=%s, attributeName=%s]",
						amAttributeId, attributeName);
	}
    
    
}
