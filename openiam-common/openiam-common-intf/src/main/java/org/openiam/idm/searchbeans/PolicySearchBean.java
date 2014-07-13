package org.openiam.idm.searchbeans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.role.dto.Role;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicySearchBean", propOrder = {
        "policyDefId"
})
public class PolicySearchBean extends AbstractKeyNameSearchBean<Policy, String> {

	private String policyDefId;

    public String getPolicyDefId() {
		return policyDefId;
	}

	public void setPolicyDefId(String policyDefId) {
		this.policyDefId = policyDefId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((policyDefId == null) ? 0 : policyDefId.hashCode());
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
		PolicySearchBean other = (PolicySearchBean) obj;
		if (policyDefId == null) {
			if (other.policyDefId != null)
				return false;
		} else if (!policyDefId.equals(other.policyDefId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format(
				"PolicySearchBean [policyDefId=%s, toString()=%s]",
				policyDefId, super.toString());
	}
	
	
}
