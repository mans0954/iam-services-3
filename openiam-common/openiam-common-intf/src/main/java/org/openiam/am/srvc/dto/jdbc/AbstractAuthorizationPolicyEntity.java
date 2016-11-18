package org.openiam.am.srvc.dto.jdbc;

import java.util.BitSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.policy.dto.Policy;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractAuthorizationPolicyEntity", propOrder = {
})
public abstract class AbstractAuthorizationPolicyEntity extends AbstractAuthorizationEntity {

	@XmlTransient
	private String passwordPolicyId;
	
	protected AbstractAuthorizationPolicyEntity(){}
	
	protected AbstractAuthorizationPolicyEntity(final AbstractAuthorizationPolicyEntity dto) {
		super(dto);
		this.passwordPolicyId = dto.getPasswordPolicyId();
	}

	public String getPasswordPolicyId() {
		return passwordPolicyId;
	}

	public void setPasswordPolicyId(String passwordPolicyId) {
		this.passwordPolicyId = passwordPolicyId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((passwordPolicyId == null) ? 0 : passwordPolicyId.hashCode());
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
		AbstractAuthorizationPolicyEntity other = (AbstractAuthorizationPolicyEntity) obj;
		if (passwordPolicyId == null) {
			if (other.passwordPolicyId != null)
				return false;
		} else if (!passwordPolicyId.equals(other.passwordPolicyId))
			return false;
		return true;
	}
	
	
}
