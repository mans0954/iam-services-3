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

import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationOrganization;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.authmanager.ws.request.AuthorizationMatrixMapAdapter;
import org.openiam.authmanager.ws.request.AuthorizationMatrixMapToSetAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EntitlementsMatrix", propOrder = {
	"userId",
	"resourceMap",
	"groupMap",
	"roleMap",
	"orgMap",
	"organizationIds",
	"roleIds",
	"groupIds",
	"resourceIds",
	"resourceToResourceMap",

	"groupToGroupMap",
	"groupToResourceMap",

	"roleToResourceMap",
    "roleToGroupMap",
    "roleToRoleMap",

    "orgToOrgMap",
    "orgToGroupMap",
    "orgToRoleMap",
    "orgToResourceMap"
})
public class UserEntitlementsMatrix implements Serializable {
	private String userId;

	// orgId->Org
	private Map<String, AuthorizationOrganization> orgMap;
	// resourceId -> Resource
	private Map<String, AuthorizationResource> resourceMap;
	// groupId -> Group
	private Map<String, AuthorizationGroup> groupMap;
	// roleId -> Role
	private Map<String, AuthorizationRole> roleMap;

	// direct user orgs with rights
	@XmlJavaTypeAdapter(AuthorizationMatrixMapAdapter.class)
	private Map<String, Set<String>> organizationIds;
	// direct user roles with rights
	@XmlJavaTypeAdapter(AuthorizationMatrixMapAdapter.class)
	private Map<String, Set<String>> roleIds;
	// direct user groups with rights
	@XmlJavaTypeAdapter(AuthorizationMatrixMapAdapter.class)
	private Map<String, Set<String>> groupIds;
	// direct user resource with rights
	@XmlJavaTypeAdapter(AuthorizationMatrixMapAdapter.class)
	private Map<String, Set<String>> resourceIds;
	
	public UserEntitlementsMatrix() {}
	
	public void populate(final String userId,
								  final Map<String, Set<String>> organizationIds, 
								  final Map<String, Set<String>> roleIds, 
								  final Map<String, Set<String>> groupIds, 
								  final Map<String, Set<String>> resourceIds,
								  final Map<String, AuthorizationOrganization> orgMap,
								  final Map<String, AuthorizationResource> resourceMap,
								  final Map<String, AuthorizationGroup> groupMap,
								  final Map<String, AuthorizationRole> roleMap) {
		this.userId = userId;
		this.organizationIds = organizationIds;
		this.roleIds = roleIds;
		this.groupIds = groupIds;
		this.resourceIds = resourceIds;
		
		this.orgMap = orgMap;
		this.roleMap = roleMap;
		this.groupMap = groupMap;
		this.resourceMap = resourceMap;
	}

	// parent orgId -> [childOrgs with right]
	@XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
	private Map<String, Map<String, Set<String>>> orgToOrgMap = new HashMap<String, Map<String,Set<String>>>();

	@XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
	private Map<String, Map<String, Set<String>>> orgToGroupMap = new HashMap<String, Map<String,Set<String>>>();

	@XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
	private Map<String, Map<String, Set<String>>> orgToRoleMap = new HashMap<String, Map<String,Set<String>>>();

	@XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
	private Map<String, Map<String, Set<String>>> orgToResourceMap = new HashMap<String, Map<String,Set<String>>>();
	
	@XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
	private Map<String, Map<String, Set<String>>> groupToGroupMap = new HashMap<String, Map<String,Set<String>>>();
	

    @XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
    private Map<String, Map<String, Set<String>>> groupToResourceMap = new HashMap<String, Map<String,Set<String>>>();
    
    @XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
    private Map<String, Map<String, Set<String>>> roleToRoleMap = new HashMap<String, Map<String,Set<String>>>();
    
    @XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
    private Map<String, Map<String, Set<String>>> roleToGroupMap = new HashMap<String, Map<String,Set<String>>>();
    
    @XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
    private Map<String, Map<String, Set<String>>> roleToResourceMap = new HashMap<String, Map<String,Set<String>>>();
    
	@XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
	private Map<String, Map<String, Set<String>>> resourceToResourceMap = new HashMap<String, Map<String,Set<String>>>();
	

    private void addRelationship(final Map<String, Map<String, Set<String>>> parent2ChildMap,
    							 final String parentId,
    							 final String childId,
    							 final String rightId) {
    	if(!parent2ChildMap.containsKey(parentId)) {
    		parent2ChildMap.put(parentId, new HashMap<String, Set<String>>());
    	}

    	if(!parent2ChildMap.get(parentId).containsKey(childId)) {
    		parent2ChildMap.get(parentId).put(childId, new HashSet<String>());
    	}
    	if(rightId != null) {
    		parent2ChildMap.get(parentId).get(childId).add(rightId);
    	}
    }
    
    public void addOrg2OrgRelationship(final String parentId, final String childId, final String rightId) {
    	addRelationship(orgToOrgMap,  parentId, childId, rightId);
    }
    
    public void addOrg2RoleRelationship(final String parentId, final String childId, final String rightId) {
    	addRelationship(orgToRoleMap,  parentId, childId, rightId);
    }
    
    public void addOrg2GroupRelationship(final String parentId, final String childId, final String rightId) {
    	addRelationship(orgToGroupMap,  parentId, childId, rightId);
    }
    
    public void addOrg2ResourceRelationship(final String parentId, final String childId, final String rightId) {
    	addRelationship(orgToResourceMap,  parentId, childId, rightId);
    }
    
    public void addRole2RoleRelationship(final String parentId, final String childId, final String rightId) {
    	addRelationship(roleToRoleMap,  parentId, childId, rightId);
    }
    
    public void addRole2GroupRelationship(final String parentId, final String childId, final String rightId) {
    	addRelationship(roleToGroupMap, parentId, childId, rightId);
    }
    
    public void addRole2ResourceRelationship(final String parentId, final String childId, final String rightId) {
    	addRelationship(roleToResourceMap, parentId, childId, rightId);
    }
    
    public void addGroup2GroupRelationship(final String parentId, final String childId, final String rightId) {
    	addRelationship(groupToGroupMap, parentId, childId, rightId);
    }
    
    public void addGroup2ResourceRelationship(final String parentId, final String childId, final String rightId) {
    	addRelationship(groupToResourceMap, parentId, childId, rightId);
    }
    
    public void addResource2ResourceRelationship(final String parentId, final String childId, final String rightId) {
    	addRelationship(resourceToResourceMap, parentId, childId, rightId);
    }

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Map<String, AuthorizationOrganization> getOrgMap() {
		return orgMap;
	}

	public void setOrgMap(Map<String, AuthorizationOrganization> orgMap) {
		this.orgMap = orgMap;
	}

	public Map<String, AuthorizationResource> getResourceMap() {
		return resourceMap;
	}

	public void setResourceMap(Map<String, AuthorizationResource> resourceMap) {
		this.resourceMap = resourceMap;
	}

	public Map<String, AuthorizationGroup> getGroupMap() {
		return groupMap;
	}

	public void setGroupMap(Map<String, AuthorizationGroup> groupMap) {
		this.groupMap = groupMap;
	}

	public Map<String, AuthorizationRole> getRoleMap() {
		return roleMap;
	}

	public void setRoleMap(Map<String, AuthorizationRole> roleMap) {
		this.roleMap = roleMap;
	}

	public Map<String, Map<String, Set<String>>> getOrgToOrgMap() {
		return orgToOrgMap;
	}

	public void setOrgToOrgMap(Map<String, Map<String, Set<String>>> orgToOrgMap) {
		this.orgToOrgMap = orgToOrgMap;
	}

	public Map<String, Map<String, Set<String>>> getOrgToGroupMap() {
		return orgToGroupMap;
	}

	public void setOrgToGroupMap(Map<String, Map<String, Set<String>>> orgToGroupMap) {
		this.orgToGroupMap = orgToGroupMap;
	}

	public Map<String, Map<String, Set<String>>> getOrgToRoleMap() {
		return orgToRoleMap;
	}

	public void setOrgToRoleMap(Map<String, Map<String, Set<String>>> orgToRoleMap) {
		this.orgToRoleMap = orgToRoleMap;
	}

	public Map<String, Map<String, Set<String>>> getOrgToResourceMap() {
		return orgToResourceMap;
	}

	public void setOrgToResourceMap(
			Map<String, Map<String, Set<String>>> orgToResourceMap) {
		this.orgToResourceMap = orgToResourceMap;
	}

	public Map<String, Map<String, Set<String>>> getGroupToGroupMap() {
		return groupToGroupMap;
	}

	public void setGroupToGroupMap(
			Map<String, Map<String, Set<String>>> groupToGroupMap) {
		this.groupToGroupMap = groupToGroupMap;
	}


	public Map<String, Map<String, Set<String>>> getGroupToResourceMap() {
		return groupToResourceMap;
	}

	public void setGroupToResourceMap(
			Map<String, Map<String, Set<String>>> groupToResourceMap) {
		this.groupToResourceMap = groupToResourceMap;
	}

	public Map<String, Map<String, Set<String>>> getRoleToRoleMap() {
		return roleToRoleMap;
	}

	public void setRoleToRoleMap(Map<String, Map<String, Set<String>>> roleToRoleMap) {
		this.roleToRoleMap = roleToRoleMap;
	}

	public Map<String, Map<String, Set<String>>> getRoleToGroupMap() {
		return roleToGroupMap;
	}

	public void setRoleToGroupMap(
			Map<String, Map<String, Set<String>>> roleToGroupMap) {
		this.roleToGroupMap = roleToGroupMap;
	}

	public Map<String, Map<String, Set<String>>> getRoleToResourceMap() {
		return roleToResourceMap;
	}

	public void setRoleToResourceMap(
			Map<String, Map<String, Set<String>>> roleToResourceMap) {
		this.roleToResourceMap = roleToResourceMap;
	}

	public Map<String, Map<String, Set<String>>> getResourceToResourceMap() {
		return resourceToResourceMap;
	}

	public void setResourceToResourceMap(
			Map<String, Map<String, Set<String>>> resourceToResourceMap) {
		this.resourceToResourceMap = resourceToResourceMap;
	}


	public Map<String, Set<String>> getOrganizationIds() {
		return organizationIds;
	}

	public void setOrganizationIds(Map<String, Set<String>> organizationIds) {
		this.organizationIds = organizationIds;
	}

	public Map<String, Set<String>> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(Map<String, Set<String>> roleIds) {
		this.roleIds = roleIds;
	}

	public Map<String, Set<String>> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(Map<String, Set<String>> groupIds) {
		this.groupIds = groupIds;
	}

	public Map<String, Set<String>> getResourceIds() {
		return resourceIds;
	}

	public void setResourceIds(Map<String, Set<String>> resourceIds) {
		this.resourceIds = resourceIds;
	}
    
    
}
