package org.openiam.idm.srvc.policy.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.policy.dto.PolicyObjectAssoc;

/**
 * 
 * @author zaporozhec
 *
 */
@Entity
@Table(name = "POLICY_OBJECT_ASSOC")
@DozerDTOCorrespondence(PolicyObjectAssoc.class)
public class PolicyObjectAssocEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "POLICY_OBJECT_ID", length = 32)
    private String policyObjectId;
    @Column(name = "POLICY_ID", length = 32)
    private String policyId;
    @Column(name = "ASSOCIATION_LEVEL", length = 20)
    private String associationLevel;
    @Column(name = "ASSOCIATION_VALUE", length = 255)
    private String associationValue;
    @Column(name = "OBJECT_TYPE", length = 100)
    private String objectType;
    @Column(name = "OBJECT_ID", length = 32)
    private String objectId;
    @Column(name = "PARENT_ASSOC_ID", length = 32)
    private String parentAssocId;


    public PolicyObjectAssocEntity() {
    }

    public String getPolicyObjectId() {
        return policyObjectId;
    }

    public void setPolicyObjectId(String policyObjectId) {
        this.policyObjectId = policyObjectId;
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
		int result = 1;
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
		result = prime * result
				+ ((policyObjectId == null) ? 0 : policyObjectId.hashCode());
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
		PolicyObjectAssocEntity other = (PolicyObjectAssocEntity) obj;
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
		if (policyObjectId == null) {
			if (other.policyObjectId != null)
				return false;
		} else if (!policyObjectId.equals(other.policyObjectId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("PolicyObjectAssocEntity [policyObjectId=%s, policyId=%s, associationLevel=%s, associationValue=%s, objectType=%s, objectId=%s, parentAssocId=%s]",
						policyObjectId, policyId, associationLevel,
						associationValue, objectType, objectId, parentAssocId);
	}


}
