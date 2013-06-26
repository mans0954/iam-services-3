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
public class PolicyAttributeEntity implements java.io.Serializable, Comparable<PolicyAttributeEntity> {

    private static final long serialVersionUID = -291717117636794761L;

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "POLICY_ATTR_ID", length = 32)
	private String policyAttrId;
	@Column(name = "POLICY_ID")
	private String policyId;
	@Column(name = "DEF_PARAM_ID")
	private String defParamId;
	@Column(name = "NAME", length = 60)
	private String name;
	@Column(name = "OPERATION", length = 20)
	private String operation;
	@Column(name = "VALUE1", length = 255)
	private String value1;
	@Column(name = "VALUE2", length = 255)
	private String value2;
	
	@Column(name = "REQUIRED")
    @Type(type = "yes_no")
    private boolean required = true;

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	@Column(name = "RULE_TEXT")
	private String rule;

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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOperation() {
        return this.operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
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

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getDefParamId() {
        return defParamId;
    }

    public void setDefParamId(String defParamId) {
        this.defParamId = defParamId;
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
		int result = 1;
		result = prime * result
				+ ((defParamId == null) ? 0 : defParamId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((operation == null) ? 0 : operation.hashCode());
		result = prime * result
				+ ((policyAttrId == null) ? 0 : policyAttrId.hashCode());
		result = prime * result
				+ ((policyId == null) ? 0 : policyId.hashCode());
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PolicyAttributeEntity other = (PolicyAttributeEntity) obj;
		if (defParamId == null) {
			if (other.defParamId != null)
				return false;
		} else if (!defParamId.equals(other.defParamId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (operation == null) {
			if (other.operation != null)
				return false;
		} else if (!operation.equals(other.operation))
			return false;
		if (policyAttrId == null) {
			if (other.policyAttrId != null)
				return false;
		} else if (!policyAttrId.equals(other.policyAttrId))
			return false;
		if (policyId == null) {
			if (other.policyId != null)
				return false;
		} else if (!policyId.equals(other.policyId))
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

	
}
