package org.openiam.idm.srvc.role.dto;

import org.openiam.base.BaseObject;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.role.domain.RolePolicyEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RolePolicy", propOrder = {
        "rolePolicyId",
        "roleId",
        "name",
        "value1",
        "value2",
        "action",
        "executionOrder",
        "policyScript",
        "actionQualifier"
})
@DozerDTOCorrespondence(RolePolicyEntity.class)
public class RolePolicy extends BaseObject {

    protected String rolePolicyId;
    protected String roleId;
    protected String name;
    protected String value1;
    protected String value2;
    protected String action;
    protected Integer executionOrder;
    protected String actionQualifier;
    protected String policyScript;


    public RolePolicy() {
    }

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
		result = prime * result + ((rolePolicyId == null) ? 0 : rolePolicyId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		RolePolicy other = (RolePolicy) obj;
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

    
}
