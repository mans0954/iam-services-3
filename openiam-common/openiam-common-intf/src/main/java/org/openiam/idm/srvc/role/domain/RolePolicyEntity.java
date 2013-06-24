package org.openiam.idm.srvc.role.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.role.dto.RolePolicy;

@Entity
@Table(name="ROLE_POLICY")
@DozerDTOCorrespondence(RolePolicy.class)
public class RolePolicyEntity implements Serializable {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="ROLE_POLICY_ID", length=32)
    protected String rolePolicyId;
    
    @Column(name="ROLE_ID", length=32)
    protected String roleId;
    
    @Column(name="NAME", length=40)
    protected String name;
    
    @Column(name="VALUE1", length=40)
    protected String value1;
    
    @Column(name="VALUE2", length=40)
    protected String value2;
    
    @Column(name="ACTION", length=20)
    protected String action;
    
    @Column(name="EXECUTION_ORDER")
    protected Integer executionOrder;
    
    @Column(name="ACTION_QUALIFIER")
    protected String actionQualifier;
    
    @Column(name="POLICY_SCRIPT",length=100)
    protected String policyScript;

	public String getRolePolicyId() {
		return rolePolicyId;
	}

	public void setRolePolicyId(String rolePolicyId) {
		this.rolePolicyId = rolePolicyId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue1() {
		return value1;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Integer getExecutionOrder() {
		return executionOrder;
	}

	public void setExecutionOrder(Integer executionOrder) {
		this.executionOrder = executionOrder;
	}

	public String getActionQualifier() {
		return actionQualifier;
	}

	public void setActionQualifier(String actionQualifier) {
		this.actionQualifier = actionQualifier;
	}

	public String getPolicyScript() {
		return policyScript;
	}

	public void setPolicyScript(String policyScript) {
		this.policyScript = policyScript;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result
				+ ((actionQualifier == null) ? 0 : actionQualifier.hashCode());
		result = prime * result
				+ ((executionOrder == null) ? 0 : executionOrder.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((policyScript == null) ? 0 : policyScript.hashCode());
		result = prime * result + ((roleId == null) ? 0 : roleId.hashCode());
		result = prime * result
				+ ((rolePolicyId == null) ? 0 : rolePolicyId.hashCode());
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
		RolePolicyEntity other = (RolePolicyEntity) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (actionQualifier == null) {
			if (other.actionQualifier != null)
				return false;
		} else if (!actionQualifier.equals(other.actionQualifier))
			return false;
		if (executionOrder == null) {
			if (other.executionOrder != null)
				return false;
		} else if (!executionOrder.equals(other.executionOrder))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (policyScript == null) {
			if (other.policyScript != null)
				return false;
		} else if (!policyScript.equals(other.policyScript))
			return false;
		if (roleId == null) {
			if (other.roleId != null)
				return false;
		} else if (!roleId.equals(other.roleId))
			return false;
		if (rolePolicyId == null) {
			if (other.rolePolicyId != null)
				return false;
		} else if (!rolePolicyId.equals(other.rolePolicyId))
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
				.format("RolePolicyEntity [rolePolicyId=%s, roleId=%s, name=%s, value1=%s, value2=%s, action=%s, executionOrder=%s, actionQualifier=%s, policyScript=%s]",
						rolePolicyId, roleId, name, value1, value2, action,
						executionOrder, actionQualifier, policyScript);
	}
    
    
}
