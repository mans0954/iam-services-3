package org.openiam.authmanager.common.model;

import java.util.HashSet;
import java.util.Set;

public class InternalAuthroizationUser {

	private String userId;
	private Set<AuthorizationManagerLoginId> loginIds;
	private Set<String> resourceIds;
	private Set<String> roleIds;
	private Set<String> groupIds;
	
	public InternalAuthroizationUser() {
		
	}
	
	public void setUserId(final String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public Set<AuthorizationManagerLoginId> getLoginIds() {
		return loginIds;
	}

	public Set<String> getResourceIds() {
		return resourceIds;
	}

	public Set<String> getRoleIds() {
		return roleIds;
	}

	public Set<String> getGroupIds() {
		return groupIds;
	}
	
	public void addLoginId(final AuthorizationManagerLoginId loginId) {
		if(loginIds == null) {
			loginIds = new HashSet<AuthorizationManagerLoginId>();
		}
		loginIds.add(loginId);
	}
	
	public void addResourceId(final String resourceId) {
		if(resourceIds == null) {
			resourceIds = new HashSet<String>();
		}
		resourceIds.add(resourceId);
	}
	
	public void addRoleId(final String roleId) {
		if(roleIds == null) {
			roleIds = new HashSet<String>();
		}
		roleIds.add(roleId);
	}
	
	public void addGroupId(final String groupId) {
		if(groupIds == null) {
			groupIds = new HashSet<String>();
		}
		groupIds.add(groupId);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		InternalAuthroizationUser other = (InternalAuthroizationUser) obj;
		if (groupIds == null) {
			if (other.groupIds != null)
				return false;
		} else if (!groupIds.equals(other.groupIds))
			return false;
		if (loginIds == null) {
			if (other.loginIds != null)
				return false;
		} else if (!loginIds.equals(other.loginIds))
			return false;
		if (resourceIds == null) {
			if (other.resourceIds != null)
				return false;
		} else if (!resourceIds.equals(other.resourceIds))
			return false;
		if (roleIds == null) {
			if (other.roleIds != null)
				return false;
		} else if (!roleIds.equals(other.roleIds))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("InternalAuthroizationUser [userId=%s, loginIds=%s, resourceIds=%s, roleIds=%s, groupIds=%s]",
						userId, loginIds, resourceIds, roleIds, groupIds);
	}
	
	
}
