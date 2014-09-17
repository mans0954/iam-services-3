package org.openiam.idm.srvc.policy.dto;


import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.policy.domain.PolicyDefParamEntity;

/**
 * PolicyDefParam represent the parameters of a policy definition.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicyDefParam", propOrder = {
        "policyDefId",
        "description",
        "operation",
        "value1",
        "value2",
        "repeats",
        "policyParamHandler",
        "handlerLanguage",
        "paramGroup"
})
@DozerDTOCorrespondence(PolicyDefParamEntity.class)
public class PolicyDefParam extends KeyNameDTO {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private String policyDefId;
    private String description;
    private String operation;
    private String value1;
    private String value2;
    private Integer repeats;
    private String policyParamHandler;
    private String handlerLanguage;
    private String paramGroup;


    public PolicyDefParam() {
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getRepeats() {
        return this.repeats;
    }

    public void setRepeats(Integer repeats) {
        this.repeats = repeats;
    }

    public String getPolicyParamHandler() {
        return this.policyParamHandler;
    }

    public void setPolicyParamHandler(String policyParamHandler) {
        this.policyParamHandler = policyParamHandler;
    }


    public String getParamGroup() {
        return paramGroup;
    }

    public void setParamGroup(String paramGroup) {
        this.paramGroup = paramGroup;
    }

    public String getPolicyDefId() {
        return policyDefId;
    }

    public void setPolicyDefId(String policyDefId) {
        this.policyDefId = policyDefId;
    }

    public String getHandlerLanguage() {
        return handlerLanguage;
    }

    public void setHandlerLanguage(String handlerLanguage) {
        this.handlerLanguage = handlerLanguage;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((handlerLanguage == null) ? 0 : handlerLanguage.hashCode());
		result = prime * result
				+ ((operation == null) ? 0 : operation.hashCode());
		result = prime * result
				+ ((paramGroup == null) ? 0 : paramGroup.hashCode());
		result = prime * result
				+ ((policyDefId == null) ? 0 : policyDefId.hashCode());
		result = prime
				* result
				+ ((policyParamHandler == null) ? 0 : policyParamHandler
						.hashCode());
		result = prime * result + ((repeats == null) ? 0 : repeats.hashCode());
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
		PolicyDefParam other = (PolicyDefParam) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (handlerLanguage == null) {
			if (other.handlerLanguage != null)
				return false;
		} else if (!handlerLanguage.equals(other.handlerLanguage))
			return false;
		if (operation == null) {
			if (other.operation != null)
				return false;
		} else if (!operation.equals(other.operation))
			return false;
		if (paramGroup == null) {
			if (other.paramGroup != null)
				return false;
		} else if (!paramGroup.equals(other.paramGroup))
			return false;
		if (policyDefId == null) {
			if (other.policyDefId != null)
				return false;
		} else if (!policyDefId.equals(other.policyDefId))
			return false;
		if (policyParamHandler == null) {
			if (other.policyParamHandler != null)
				return false;
		} else if (!policyParamHandler.equals(other.policyParamHandler))
			return false;
		if (repeats == null) {
			if (other.repeats != null)
				return false;
		} else if (!repeats.equals(other.repeats))
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
		return "PolicyDefParam [policyDefId=" + policyDefId + ", description="
				+ description + ", operation=" + operation + ", value1="
				+ value1 + ", value2=" + value2 + ", repeats=" + repeats
				+ ", policyParamHandler=" + policyParamHandler
				+ ", handlerLanguage=" + handlerLanguage + ", paramGroup="
				+ paramGroup + ", toString()=" + super.toString() + "]";
	}

    
}
