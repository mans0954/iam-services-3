package org.openiam.idm.srvc.policy.domain;

// Generated Mar 7, 2009 11:47:12 AM by Hibernate Tools 3.2.2.GA

import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.policy.dto.PolicyDef;

/**
 * PolicyDef represent a policy definition
 */
@Entity
@Table(name = "POLICY_DEF")
@DozerDTOCorrespondence(PolicyDef.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides(value={
		@AttributeOverride(name = "id", column = @Column(name = "POLICY_DEF_ID", length = 32)),
		@AttributeOverride(name = "name", column = @Column(name = "NAME", length = 60))
	})
public class PolicyDefEntity extends AbstractKeyNameEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "DESCRIPTION", length = 255)
    private String description;
	@Column(name = "POLICY_TYPE", length = 20)
    private String policyType;
	@Column(name = "LOCATION_TYPE", length = 20)
    private String locationType;
	@Column(name = "POLICY_RULE", length = 500)
    private String policyRule;
	@Column(name = "POLICY_HANDLER", length = 255)
    private String policyHandler;
	@Column(name = "POLICY_ADVISE_HANDLER", length = 255)
    private String policyAdviseHandler;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy="policyDef", orphanRemoval=true)
	//@JoinColumn(name = "POLICY_DEF_ID", insertable = false, updatable = false)
    private Set<PolicyDefParamEntity> policyDefParams = new HashSet<PolicyDefParamEntity>(0);


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy="policyDef", orphanRemoval=true)
    //@JoinColumn(name = "POLICY_DEF_ID", insertable = false, updatable = false)
    private Set<PolicyEntity> policies = new HashSet<PolicyEntity>(0);

    public PolicyDefEntity() {
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

    public Set<PolicyDefParamEntity> getPolicyDefParams() {
        return this.policyDefParams;
    }

    public void setPolicyDefParams(Set<PolicyDefParamEntity> policyDefParams) {
        this.policyDefParams = policyDefParams;
    }

    public Set<PolicyEntity> getPolicies() {
        return this.policies;
    }

    public void setPolicies(Set<PolicyEntity> policies) {
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
		PolicyDefEntity other = (PolicyDefEntity) obj;
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
		return "PolicyDefEntity [description=" + description + ", policyType="
				+ policyType + ", locationType=" + locationType
				+ ", policyRule=" + policyRule + ", policyHandler="
				+ policyHandler + ", policyAdviseHandler="
				+ policyAdviseHandler + ", toString()=" + super.toString()
				+ "]";
	}

    
}
