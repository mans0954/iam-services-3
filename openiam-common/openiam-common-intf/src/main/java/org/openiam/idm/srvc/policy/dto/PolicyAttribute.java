package org.openiam.idm.srvc.policy.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.policy.domain.PolicyAttributeEntity;

// Generated Mar 7, 2009 11:47:12 AM by Hibernate Tools 3.2.2.GA

/**
 * PolicyAttribute is used to add additional attributes to a policy object.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicyAttribute", propOrder = {
	"policyId",
	"defParamId",
	"operation", 
	"value1", 
	"value2", 
	"rule",
	"required" 
})
@DozerDTOCorrespondence(PolicyAttributeEntity.class)
public class PolicyAttribute extends KeyNameDTO {

	private static final long serialVersionUID = -291717117636794761L;
	protected String policyId;
	protected String defParamId;
	protected String operation;
	protected String value1;
	protected String value2;
	protected String rule;
	protected boolean required = true;

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public PolicyAttribute() {
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
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((defParamId == null) ? 0 : defParamId.hashCode());
		result = prime * result
				+ ((operation == null) ? 0 : operation.hashCode());
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
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PolicyAttribute other = (PolicyAttribute) obj;
		if (defParamId == null) {
			if (other.defParamId != null)
				return false;
		} else if (!defParamId.equals(other.defParamId))
			return false;
		if (operation == null) {
			if (other.operation != null)
				return false;
		} else if (!operation.equals(other.operation))
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

	@Override
	public String toString() {
		return String
				.format("PolicyAttribute [policyId=%s, defParamId=%s, operation=%s, value1=%s, value2=%s, rule=%s, required=%s, toString()=%s]",
						policyId, defParamId, operation, value1, value2, rule,
						required, super.toString());
	}

	
}
