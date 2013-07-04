package org.openiam.authmanager.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.authmanager.ws.request.AuthorizationMatrixAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EntitlementsMatrix", propOrder = {
	"userId",
	"resourceMap",
	"groupMap",
	"roleMap",
	"roleIds",
	"groupIds",
	"resourceIds",
	"resourceToResourceMap",
	"groupToGroupMap",
	"roleToRoleMap",
	"groupToRoleMap",
	"groupToResourceMap",
	"roleToResourceMap",
	"publicResourceIds"
})
public class UserEntitlementsMatrix implements Serializable {
	private String userId;
	private Map<String, AuthorizationResource> resourceMap;
	private Map<String, AuthorizationGroup> groupMap;
	private Map<String, AuthorizationRole> roleMap;
	
	private Set<String> roleIds;
	private Set<String> groupIds;
	private Set<String> resourceIds;
	private Set<String> publicResourceIds;
	
	@XmlJavaTypeAdapter(AuthorizationMatrixAdapter.class)
	private Map<String, Set<String>> groupToGroupMap;
	
	@XmlJavaTypeAdapter(AuthorizationMatrixAdapter.class)
	private Map<String, Set<String>> roleToRoleMap;
	
	@XmlJavaTypeAdapter(AuthorizationMatrixAdapter.class)
	private Map<String, Set<String>> groupToRoleMap;
	
	@XmlJavaTypeAdapter(AuthorizationMatrixAdapter.class)
	private Map<String, Set<String>> resourceToResourceMap;
	
	@XmlJavaTypeAdapter(AuthorizationMatrixAdapter.class)
	private Map<String, Set<String>> groupToResourceMap;
	
	@XmlJavaTypeAdapter(AuthorizationMatrixAdapter.class)
	private Map<String, Set<String>> roleToResourceMap;
	
	public void setGroupToGroupMap(final Map<String, Set<AuthorizationGroup>> groupToGroupMap) {
		if(groupToGroupMap != null) {
			if(this.groupToGroupMap == null) {
				this.groupToGroupMap = new HashMap<String, Set<String>>();
			}
			for(final String groupId : groupToGroupMap.keySet()) {
				final Set<AuthorizationGroup> groups = groupToGroupMap.get(groupId);
				if(CollectionUtils.isNotEmpty(groups)) {
					if(!this.groupToGroupMap.containsKey(groupId)) {
						this.groupToGroupMap.put(groupId, new HashSet<String>());
					}
					for(final AuthorizationGroup role : groups) {
						this.groupToGroupMap.get(groupId).add(role.getId());
					}
				}
			}
		}
	}
	
	public void setRoleToRoleMap(final Map<String, Set<AuthorizationRole>> roleToRoleMap) {
		if(roleToRoleMap != null) {
			if(this.roleToRoleMap == null) {
				this.roleToRoleMap = new HashMap<String, Set<String>>();
			}
			for(final String roleId : roleToRoleMap.keySet()) {
				final Set<AuthorizationRole> roles = roleToRoleMap.get(roleId);
				if(CollectionUtils.isNotEmpty(roles)) {
					if(!this.roleToRoleMap.containsKey(roleId)) {
						this.roleToRoleMap.put(roleId, new HashSet<String>());
					}
					for(final AuthorizationRole role : roles) {
						this.roleToRoleMap.get(roleId).add(role.getId());
					}
				}
			}
		}
	}
	
	public void setGroupToRoleMap(final Map<String, Set<AuthorizationRole>> groupToRoleMap) {
		if(groupToRoleMap != null) {
			if(this.groupToRoleMap == null) {
				this.groupToRoleMap = new HashMap<String, Set<String>>();
			}
			for(final String groupId : groupToRoleMap.keySet()) {
				final Set<AuthorizationRole> roles = groupToRoleMap.get(groupId);
				if(CollectionUtils.isNotEmpty(roles)) {
					if(!this.groupToRoleMap.containsKey(groupId)) {
						this.groupToRoleMap.put(groupId, new HashSet<String>());
					}
					for(final AuthorizationRole role : roles) {
						this.groupToRoleMap.get(groupId).add(role.getId());
					}
				}
			}
		}
	}
	
	public void setRoleToResourceMap(Map<String, Set<AuthorizationResource>> roleToResourceMap) {
		if(roleToResourceMap != null) {
			if(this.roleToResourceMap == null) {
				this.roleToResourceMap = new HashMap<String, Set<String>>();
			}
			for(final String roleId : roleToResourceMap.keySet()) {
				final Set<AuthorizationResource> resources = roleToResourceMap.get(roleId);
				if(CollectionUtils.isNotEmpty(resources)) {
					if(!this.roleToResourceMap.containsKey(roleId)) {
						this.roleToResourceMap.put(roleId, new HashSet<String>());
					}
					for(final AuthorizationResource resource : resources) {
						this.roleToResourceMap.get(roleId).add(resource.getId());
					}
				}
			}
		}
	}
	
	public void setGroupToResourceMap(Map<String, Set<AuthorizationResource>> groupToResourceMap) {
		if(groupToResourceMap != null) {
			if(this.groupToResourceMap == null) {
				this.groupToResourceMap = new HashMap<String, Set<String>>();
			}
			for(final String groupId : groupToResourceMap.keySet()) {
				final Set<AuthorizationResource> resources = groupToResourceMap.get(groupId);
				if(CollectionUtils.isNotEmpty(resources)) {
					if(!this.groupToResourceMap.containsKey(groupId)) {
						this.groupToResourceMap.put(groupId, new HashSet<String>());
					}
					for(final AuthorizationResource resource : resources) {
						this.groupToResourceMap.get(groupId).add(resource.getId());
					}
				}
			}
		}
	}
	
	public void setResourceToResourceMap(Map<String, Set<AuthorizationResource>> resourceToResourceMap) {
		if(resourceToResourceMap != null) {
			if(this.resourceToResourceMap == null) {
				this.resourceToResourceMap = new HashMap<String, Set<String>>();
			}
			for(final String resourceId : resourceToResourceMap.keySet()) {
				final Set<AuthorizationResource> resources = resourceToResourceMap.get(resourceId);
				if(CollectionUtils.isNotEmpty(resources)) {
					if(!this.resourceToResourceMap.containsKey(resourceId)) {
						this.resourceToResourceMap.put(resourceId, new HashSet<String>());
					}
					for(final AuthorizationResource resource : resources) {
						this.resourceToResourceMap.get(resourceId).add(resource.getId());
					}
				}
			}
		}
	}
	
	public void setUserId(final String userId) {
		this.userId = userId;
	}
	
	public void addPublicResource(final AuthorizationResource resource) {
		if(resource != null) {
			if(this.publicResourceIds == null) {
				this.publicResourceIds = new HashSet<String>();
			}
			this.publicResourceIds.add(resource.getId());
		}
	}
	
	public void setRoleIds(final Set<String> roleIds) {
		if(roleIds != null) {
			this.roleIds = new HashSet<String>(roleIds);
		}
	}
	
	public void setGroupIds(final Set<String> groupIds) {
		if(groupIds != null) {
			this.groupIds = new HashSet<String>(groupIds);
		}
	}
	
	public void setResourceIds(final Set<String> resourceIds) {
		if(resourceIds != null) {
			this.resourceIds = new HashSet<String>(resourceIds);
		}
	}

	public void setResourceMap(Map<String, AuthorizationResource> resourceMap) {
		if(resourceMap != null) {
			this.resourceMap = new HashMap<String, AuthorizationResource>(resourceMap);
		}
	}

	public void setGroupMap(Map<String, AuthorizationGroup> groupMap) {
		if(groupMap != null) {
			this.groupMap = new HashMap<String, AuthorizationGroup>(groupMap);
		}
	}

	public void setRoleMap(Map<String, AuthorizationRole> roleMap) {
		if(roleMap != null) {
			this.roleMap = new HashMap<String, AuthorizationRole>(roleMap);
		}
	}

	public String getUserId() {
		return userId;
	}

	public Map<String, AuthorizationResource> getResourceMap() {
		return resourceMap;
	}

	public Map<String, AuthorizationGroup> getGroupMap() {
		return groupMap;
	}

	public Map<String, AuthorizationRole> getRoleMap() {
		return roleMap;
	}

	public Set<String> getRoleIds() {
		return roleIds;
	}

	public Set<String> getGroupIds() {
		return groupIds;
	}

	public Set<String> getResourceIds() {
		return resourceIds;
	}

	public Set<String> getPublicResourceIds() {
		return publicResourceIds;
	}

	public Map<String, Set<String>> getResourceToResourceMap() {
		return resourceToResourceMap;
	}

	public Map<String, Set<String>> getGroupToGroupMap() {
		return groupToGroupMap;
	}

	public Map<String, Set<String>> getRoleToRoleMap() {
		return roleToRoleMap;
	}

	public Map<String, Set<String>> getGroupToRoleMap() {
		return groupToRoleMap;
	}

	public Map<String, Set<String>> getGroupToResourceMap() {
		return groupToResourceMap;
	}

	public Map<String, Set<String>> getRoleToResourceMap() {
		return roleToResourceMap;
	}
	
	
}
