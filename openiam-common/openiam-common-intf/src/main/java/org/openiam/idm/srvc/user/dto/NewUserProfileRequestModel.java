package org.openiam.idm.srvc.user.dto;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.meta.dto.PageTempate;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NewUserProfileRequestModel", 
	propOrder = { 
        "roleIds",
        "groupIds",
        "loginList",
        "organizationIds",
        "supervisorIds",
        "customApproverIds"
})
public class NewUserProfileRequestModel extends UserProfileRequestModel implements Serializable {

	private List<Login> loginList;
	private List<String> roleIds;
	private List<String> groupIds;
	private List<String> supervisorIds;
	private List<String> organizationIds;
	private List<String> customApproverIds;

	public List<String> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<String> roleIds) {
		this.roleIds = roleIds;
	}

	public List<Login> getLoginList() {
		return loginList;
	}

	public void setLoginList(List<Login> loginList) {
		this.loginList = loginList;
	}
	
	public void addLogin(final Login login) {
		if(login != null) {
			if(this.loginList == null) {
				this.loginList = new LinkedList<Login>();
			}
			this.loginList.add(login);
		}
	}

	public List<String> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(List<String> groupIds) {
		this.groupIds = groupIds;
	}

	public List<String> getSupervisorIds() {
		return supervisorIds;
	}

	public void setSupervisorIds(List<String> supervisorIds) {
		this.supervisorIds = supervisorIds;
	}

	public List<String> getOrganizationIds() {
		return organizationIds;
	}

	public void setOrganizationIds(List<String> organizationIds) {
		this.organizationIds = organizationIds;
	}
	
	public void addCustomApproverId(final String arg) {
		if(arg != null) {
			if(this.customApproverIds == null) {
				this.customApproverIds = new LinkedList<String>();
			}
			this.customApproverIds.add(arg);
		}
	}

	public List<String> getCustomApproverIds() {
		return customApproverIds;
	}

	public void setCustomApproverIds(List<String> customApproverIds) {
		this.customApproverIds = customApproverIds;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((groupIds == null) ? 0 : groupIds.hashCode());
		result = prime * result
				+ ((loginList == null) ? 0 : loginList.hashCode());
		result = prime * result
				+ ((organizationIds == null) ? 0 : organizationIds.hashCode());
		result = prime * result + ((roleIds == null) ? 0 : roleIds.hashCode());
		result = prime * result
				+ ((supervisorIds == null) ? 0 : supervisorIds.hashCode());
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
		NewUserProfileRequestModel other = (NewUserProfileRequestModel) obj;
		if (groupIds == null) {
			if (other.groupIds != null)
				return false;
		} else if (!groupIds.equals(other.groupIds))
			return false;
		if (loginList == null) {
			if (other.loginList != null)
				return false;
		} else if (!loginList.equals(other.loginList))
			return false;
		if (organizationIds == null) {
			if (other.organizationIds != null)
				return false;
		} else if (!organizationIds.equals(other.organizationIds))
			return false;
		if (roleIds == null) {
			if (other.roleIds != null)
				return false;
		} else if (!roleIds.equals(other.roleIds))
			return false;
		if (supervisorIds == null) {
			if (other.supervisorIds != null)
				return false;
		} else if (!supervisorIds.equals(other.supervisorIds))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("NewUserProfileRequestModel [loginList=%s, roleIds=%s, groupIds=%s, supervisorIds=%s, organizationIds=%s]",
						loginList, roleIds, groupIds, supervisorIds,
						organizationIds);
	}

	
}
