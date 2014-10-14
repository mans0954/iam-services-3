package org.openiam.idm.srvc.policy.domain;


import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.policy.dto.PolicyDefParam;

/**
 * @author zaporozhec
 */
@Entity
@Table(name = "POLICY_DEF_PARAM")
@DozerDTOCorrespondence(PolicyDefParam.class)
public class PolicyDefParamEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="POLICY_DEF_ID", referencedColumnName = "POLICY_DEF_ID", insertable = true, updatable = true, nullable=true)
	private PolicyDefEntity policyDef;

	@Column(name = "DESCRIPTION", length = 255)
    private String description;

	@Column(name = "OPERATION", length = 20)
    private String operation;

	@Column(name = "VALUE1", length = 3076)
    private String value1;

	@Column(name = "VALUE2", length = 3076)
    private String value2;

	@Column(name = "REPEATS")
    private Integer repeats;

	@Column(name = "POLICY_PARAM_HANDLER", length = 255)
    private String policyParamHandler;

	@Column(name = "HANDLER_LANGUAGE", length = 20)
    private String handlerLanguage;

	@Column(name = "PARAM_GROUP", length = 20)
    private String paramGroup;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy="defParam", orphanRemoval=true)
	//@JoinColumn(name = "POLICY_DEF_ID", insertable = false, updatable = false)
    private Set<PolicyAttributeEntity> attributes;


	public PolicyDefParamEntity() {
	}

    public PolicyDefParamEntity(String defParamId) {
        this.defParamId = defParamId;
    }

    ng getDescription() {
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

    public PolicyDefEntity getPolicyDef() {
		return policyDef;
	}

	public void setPolicyDef(PolicyDefEntity policyDef) {
		this.policyDef = policyDef;
	}

	public String getHandlerLanguage() {
        return handlerLanguage;
    }

    public void setHandlerLanguage(String handlerLanguage) {
        this.handlerLanguage = handlerLanguage;
    }

	public Set<PolicyAttributeEntity> getAttributes() {
		return attributes;
	}

	public void setAttributes(Set<PolicyAttributeEntity> attributes) {
		this.attributes = attributes;
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
				+ ((policyDef == null) ? 0 : policyDef.hashCode());
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
		PolicyDefParamEntity other = (PolicyDefParamEntity) obj;
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
		if (policyDef == null) {
			if (other.policyDef != null)
				return false;
		} else if (!policyDef.equals(other.policyDef))
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
		return "PolicyDefParamEntity [policyDef=" + policyDef
				+ ", description=" + description + ", operation=" + operation
				+ ", value1=" + value1 + ", value2=" + value2 + ", repeats="
				+ repeats + ", policyParamHandler=" + policyParamHandler
				+ ", handlerLanguage=" + handlerLanguage + ", paramGroup="
				+ paramGroup + ", toString()=" + super.toString() + "]";
	}

	
}
