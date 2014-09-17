package org.openiam.idm.srvc.policy.dto;

// Generated Mar 7, 2009 11:47:12 AM by Hibernate Tools 3.2.2.GA

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.policy.domain.PolicyDefEntity;

/**
 * PolicyDef represent a policy definition
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicyDef", propOrder = {
        "description",
        "policyType",
        "locationType",
        "policyRule",
        "policyHandler",
        "policyAdviseHandler",
        "policyDefParams",
        "policies"
})
@DozerDTOCorrespondence(PolicyDefEntity.class)
public class PolicyDef extends KeyNameDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private String description;
    private String policyType;
    private String locationType;
    private String policyRule;
    private String policyHandler;
    private String policyAdviseHandler;
    private Set<PolicyDefParam> policyDefParams = new HashSet<PolicyDefParam>(0);
    private Set<Policy> policies = new HashSet<Policy>(0);

    public PolicyDef() {
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPolicyType() {
        return this.policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public String getLocationType() {
        return this.locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getPolicyRule() {
        return this.policyRule;
    }

    public void setPolicyRule(String policyRule) {
        this.policyRule = policyRule;
    }

    public String getPolicyHandler() {
        return this.policyHandler;
    }

    public void setPolicyHandler(String policyHandler) {
        this.policyHandler = policyHandler;
    }

    public String getPolicyAdviseHandler() {
        return this.policyAdviseHandler;
    }

    public void setPolicyAdviseHandler(String policyAdviseHandler) {
        this.policyAdviseHandler = policyAdviseHandler;
    }

    public Set<PolicyDefParam> getPolicyDefParams() {
        return this.policyDefParams;
    }

    public void setPolicyDefParams(Set<PolicyDefParam> policyDefParams) {
        this.policyDefParams = policyDefParams;
    }

    public Set<Policy> getPolicies() {
        return this.policies;
    }

    public void setPolicies(Set<Policy> policies) {
        this.policies = policies;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((locationType == null) ? 0 : locationType.hashCode());
		result = prime
				* result
				+ ((policyAdviseHandler == null) ? 0 : policyAdviseHandler
						.hashCode());
		result = prime * result
				+ ((policyHandler == null) ? 0 : policyHandler.hashCode());
		result = prime * result
				+ ((policyRule == null) ? 0 : policyRule.hashCode());
		result = prime * result
				+ ((policyType == null) ? 0 : policyType.hashCode());
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
		PolicyDef other = (PolicyDef) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (locationType == null) {
			if (other.locationType != null)
				return false;
		} else if (!locationType.equals(other.locationType))
			return false;
		if (policyAdviseHandler == null) {
			if (other.policyAdviseHandler != null)
				return false;
		} else if (!policyAdviseHandler.equals(other.policyAdviseHandler))
			return false;
		if (policyHandler == null) {
			if (other.policyHandler != null)
				return false;
		} else if (!policyHandler.equals(other.policyHandler))
			return false;
		if (policyRule == null) {
			if (other.policyRule != null)
				return false;
		} else if (!policyRule.equals(other.policyRule))
			return false;
		if (policyType == null) {
			if (other.policyType != null)
				return false;
		} else if (!policyType.equals(other.policyType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PolicyDef [description=" + description + ", policyType="
				+ policyType + ", locationType=" + locationType
				+ ", policyRule=" + policyRule + ", policyHandler="
				+ policyHandler + ", policyAdviseHandler="
				+ policyAdviseHandler + ", toString()=" + super.toString()
				+ "]";
	}

    
}
