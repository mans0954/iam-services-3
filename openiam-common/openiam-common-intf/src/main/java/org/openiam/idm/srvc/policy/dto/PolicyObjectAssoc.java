package org.openiam.idm.srvc.policy.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.policy.domain.PolicyObjectAssocEntity;

// Generated Dec 1, 2009 12:48:52 AM by Hibernate Tools 3.2.2.GA

/**
 * represents the level at which a policy is associated with other
 * objects. Policy can be associated at the following levels: USER_CLASSIFICATION, USER_TYPE, RESOURCE,
 * ORGANIZATION, SECURITY_DOMAIN, GLOBAL
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicyObjectAssoc", propOrder = {
        "policyId",
        "associationLevel",
        "associationValue",
        "objectType",
        "objectId",
        "parentAssocId"
})
@DozerDTOCorrespondence(PolicyObjectAssocEntity.class)
public class PolicyObjectAssoc extends KeyDTO {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String policyId;
    private String associationLevel;
    private String associationValue;
    private String objectType;
    private String objectId;
    private String parentAssocId;

    public PolicyObjectAssoc() {
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getAssociationLevel() {
        return associationLevel;
    }

    public void setAssociationLevel(String associationLevel) {
        this.associationLevel = associationLevel;
    }

    public String getAssociationValue() {
        return associationValue;
    }

    public void setAssociationValue(String associationValue) {
        this.associationValue = associationValue;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getParentAssocId() {
        return parentAssocId;
    }

    public void setParentAssocId(String parentAssocId) {
        this.parentAssocId = parentAssocId;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((associationLevel == null) ? 0 : associationLevel.hashCode());
		result = prime
				* result
				+ ((associationValue == null) ? 0 : associationValue.hashCode());
		result = prime * result
				+ ((objectId == null) ? 0 : objectId.hashCode());
		result = prime * result
				+ ((objectType == null) ? 0 : objectType.hashCode());
		result = prime * result
				+ ((parentAssocId == null) ? 0 : parentAssocId.hashCode());
		result = prime * result
				+ ((policyId == null) ? 0 : policyId.hashCode());
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
		PolicyObjectAssoc other = (PolicyObjectAssoc) obj;
		if (associationLevel == null) {
			if (other.associationLevel != null)
				return false;
		} else if (!associationLevel.equals(other.associationLevel))
			return false;
		if (associationValue == null) {
			if (other.associationValue != null)
				return false;
		} else if (!associationValue.equals(other.associationValue))
			return false;
		if (objectId == null) {
			if (other.objectId != null)
				return false;
		} else if (!objectId.equals(other.objectId))
			return false;
		if (objectType == null) {
			if (other.objectType != null)
				return false;
		} else if (!objectType.equals(other.objectType))
			return false;
		if (parentAssocId == null) {
			if (other.parentAssocId != null)
				return false;
		} else if (!parentAssocId.equals(other.parentAssocId))
			return false;
		if (policyId == null) {
			if (other.policyId != null)
				return false;
		} else if (!policyId.equals(other.policyId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PolicyObjectAssoc [policyId=" + policyId
				+ ", associationLevel=" + associationLevel
				+ ", associationValue=" + associationValue + ", objectType="
				+ objectType + ", objectId=" + objectId + ", parentAssocId="
				+ parentAssocId + ", getId()=" + getId() + "]";
	}

	
}
