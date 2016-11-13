package org.openiam.base.domain;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.openiam.idm.srvc.policy.domain.PolicyEntity;

@MappedSuperclass
public abstract class AbstractEntitlementPolicyEntity extends AbstractMetdataTypeEntity {

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "POLICY_ID", referencedColumnName = "POLICY_ID", insertable = true, updatable = true)
    private PolicyEntity policy;

	public PolicyEntity getPolicy() {
		return policy;
	}

	public void setPolicy(PolicyEntity policy) {
		this.policy = policy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((policy == null) ? 0 : policy.hashCode());
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
		AbstractEntitlementPolicyEntity other = (AbstractEntitlementPolicyEntity) obj;
		if (policy == null) {
			if (other.policy != null)
				return false;
		} else if (!policy.equals(other.policy))
			return false;
		return true;
	}

    
}
