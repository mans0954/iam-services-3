package org.openiam.idm.srvc.mngsys.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.domain.DefaultReconciliationAttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ReconciliationResourceAttributeMapEntity;
import org.openiam.idm.srvc.policy.dto.Policy;

/**
 * @author zaporozhec
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReconciliationResourceAttributeMap", propOrder = {
        "attributePolicy",
        "defaultAttributePolicy" })
@DozerDTOCorrespondence(ReconciliationResourceAttributeMapEntity.class)
public class ReconciliationResourceAttributeMap extends KeyDTO {

    private static final long serialVersionUID = -4584242607384442243L;
    private Policy attributePolicy;
    private DefaultReconciliationAttributeMap defaultAttributePolicy;

    public Policy getAttributePolicy() {
        return attributePolicy;
    }

    public void setAttributePolicy(Policy attributePolicy) {
        this.attributePolicy = attributePolicy;
    }

    public DefaultReconciliationAttributeMap getDefaultAttributePolicy() {
        return defaultAttributePolicy;
    }

    public void setDefaultAttributePolicy(
            DefaultReconciliationAttributeMap defaultAttributePolicy) {
        this.defaultAttributePolicy = defaultAttributePolicy;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((attributePolicy == null) ? 0 : attributePolicy.hashCode());
		result = prime
				* result
				+ ((defaultAttributePolicy == null) ? 0
						: defaultAttributePolicy.hashCode());
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
		ReconciliationResourceAttributeMap other = (ReconciliationResourceAttributeMap) obj;
		if (attributePolicy == null) {
			if (other.attributePolicy != null)
				return false;
		} else if (!attributePolicy.equals(other.attributePolicy))
			return false;
		if (defaultAttributePolicy == null) {
			if (other.defaultAttributePolicy != null)
				return false;
		} else if (!defaultAttributePolicy.equals(other.defaultAttributePolicy))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ReconciliationResourceAttributeMap [attributePolicy="
				+ attributePolicy + ", defaultAttributePolicy="
				+ defaultAttributePolicy + ", id=" + id + ", objectState="
				+ objectState + ", requestorSessionID=" + requestorSessionID
				+ ", requestorUserId=" + requestorUserId + ", requestorLogin="
				+ requestorLogin + ", requestClientIP=" + requestClientIP + "]";
	}

    
}
