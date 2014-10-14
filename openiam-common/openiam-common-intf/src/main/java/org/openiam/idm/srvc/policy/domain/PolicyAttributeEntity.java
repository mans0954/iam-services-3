package org.openiam.idm.srvc.policy.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;

/**
 * @author zaporozhec
 */

/**
 * PolicyAttribute is used to add additional attributes to a policy object.
 */
@Entity
@Table(name = "POLICY_ATTRIBUTE")
@DozerDTOCorrespondence(PolicyAttribute.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "POLICY_ATTR_ID")),
	@AttributeOverride(name = "name", column = @Column(name = "NAME", length = 100))
})
public class PolicyAttributeEntity extends AbstractKeyNameEntity {

    private static final long serialVersionUID = -291717117636794761L;
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "POLICY_ID", referencedColumnName = "POLICY_ID", insertable = true, updatable = false)
    private PolicyEntity policy;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "DEF_PARAM_ID", insertable = true, updatable = true, nullable = true)
    private PolicyDefParamEntity defaultParametr;


    @Column(name = "VALUE1", length = 2048)
    private String value1;
    @Column(name = "VALUE2", length = 2048)
    private String value2;

    @Column(name = "REQUIRED")
    @Type(type = "yes_no")
    private boolean required = true;

	@Column(name = "RULE_TEXT")
	private String rule;

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}



	public PolicyEntity getPolicy() {
		return policy;
	}

	public void setPolicy(PolicyEntity policy) {
		this.policy = policy;
	}

    public PolicyAttributeEntity() {
    }

    public PolicyAttributeEntity(String policyAttrId) {
        this.policyAttrId = policyAttrId;
    }  

    public String getPolicyAttrId() {
        return this.policyAttrId;
    }

    public void setPolicyAttrId(String policyAttrId) {
        this.policyAttrId = policyAttrId;
    }


    public String getValue1() {
        return this.value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return this.value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public PolicyDefParamEntity getDefParam() {
		return defParam;
	}

	public void setDefParam(PolicyDefParamEntity defParam) {
		this.defParam = defParam;
	}

	public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    @Override
	public String toString() {
		return "PolicyAttributeEntity [policyAttrId=" + policyAttrId
				+ ", policyId=" + policyId + ", defParamId=" + defParamId
				+ ", name=" + name + ", operation=" + operation + ", value1="
				+ value1 + ", value2=" + value2 + ", required="
				+ required + ", rule=" + rule + "]";
	}

    public int compareTo(PolicyAttributeEntity o) {
        if (getName() == null || o == null) {
            // Not recommended, but compareTo() is only used for display purposes in this case
            return Integer.MIN_VALUE;
        }
        return getName().compareTo(o.getName());
    }

@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((defParam == null) ? 0 : defParam.hashCode());
		result = prime * result
				+ ((operation == null) ? 0 : operation.hashCode());
		result = prime * result + ((policy == null) ? 0 : policy.hashCode());
		result = prime * result + (required ? 1231 : 1237);
		result = prime * result + ((rule == null) ? 0 : rule.hashCode());
		result = prime * result + ((value1 == null) ? 0 : value1.hashCode());
		result = prime * result + ((value2 == null) ? 0 : value2.hashCode());
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
		PolicyAttributeEntity other = (PolicyAttributeEntity) obj;
		if (defParam == null) {
			if (other.defParam != null)
				return false;
		} else if (!defParam.equals(other.defParam))
			return false;
		if (operation == null) {
			if (other.operation != null)
				return false;
		} else if (!operation.equals(other.operation))
			return false;
		if (policy == null) {
			if (other.policy != null)
				return false;
		} else if (!policy.equals(other.policy))
			return false;
		if (required != other.required)
			return false;
		if (rule == null) {
			if (other.rule != null)
				return false;
		} else if (!rule.equals(other.rule))
			return false;
		if (value1 == null) {
			if (other.value1 != null)
				return false;
		} else if (!value1.equals(other.value1))
			return false;
		if (value2 == null) {
			if (other.value2 != null)
				return false;
		} else if (!value2.equals(other.value2))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("PolicyAttributeEntity [policy=%s, defParam=%s, operation=%s, value1=%s, value2=%s, required=%s, rule=%s]",
						policy, defParam, operation, value1, value2,
						required, rule);
	}

    
}
