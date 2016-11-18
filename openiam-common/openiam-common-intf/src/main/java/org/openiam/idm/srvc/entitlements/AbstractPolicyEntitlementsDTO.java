package org.openiam.idm.srvc.entitlements;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractPolicyEntitlementsDTO", propOrder = {
        "policyId"
})
public abstract class AbstractPolicyEntitlementsDTO extends AbstractEntitlementsDTO {

	private String policyId;

	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
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
		AbstractPolicyEntitlementsDTO other = (AbstractPolicyEntitlementsDTO) obj;
		if (policyId == null) {
			if (other.policyId != null)
				return false;
		} else if (!policyId.equals(other.policyId))
			return false;
		return true;
	}
	
	
}
