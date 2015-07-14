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
    "childResToParentResMap",
    
    "resourceToGroupMap",
    "resourceToRoleMap",
    "resourceToOrgMap",
    
	"groupToGroupMap",
	"groupToRoleMap",
	"groupToResourceMap",
	"groupToOrgMap",
	
	"roleToResourceMap",
    "roleToGroupMap",
    "roleToRoleMap",
    "roleToOrgMap",
    
    "childGroupToParentGroupMap",
    "childRoleToParentRoleMap",
    
    "orgToOrgMap",
    "childOrgToParentOrgMap",
    "orgToGroupMap",
    "orgToRoleMap",
    "orgToResourceMap"
})
public class UserEntitlementsMatrix implements Serializable {
	private String userId;
	
	private Map<String, AuthorizationOrganization> orgMap;
	private Map<String, AuthorizationResource> resourceMap;
	private Map<String, AuthorizationGroup> groupMap;
	private Map<String, AuthorizationRole> roleMap;
	
	@XmlJavaTypeAdapter(AuthorizationMatrixMapAdapter.class)
	private Map<String, Set<String>> organizationIds;
	
	@XmlJavaTypeAdapter(AuthorizationMatrixMapAdapter.class)
	private Map<String, Set<String>> roleIds;
	
	@XmlJavaTypeAdapter(AuthorizationMatrixMapAdapter.class)
	private Map<String, Set<String>> groupIds;
	
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
	
	@XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
	private Map<String, Map<String, Set<String>>> orgToOrgMap = new HashMap<String, Map<String,Set<String>>>();

	@XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
	private Map<String, Map<String, Set<String>>> childOrgToParentOrgMap = new HashMap<String, Map<String,Set<String>>>();

	@XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
	private Map<String, Map<String, Set<String>>> orgToGroupMap = new HashMap<String, Map<String,Set<String>>>();

	@XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
	private Map<String, Map<String, Set<String>>> orgToRoleMap = new HashMap<String, Map<String,Set<String>>>();

	@XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
	private Map<String, Map<String, Set<String>>> orgToResourceMap = new HashMap<String, Map<String,Set<String>>>();
	
	@XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
	private Map<String, Map<String, Set<String>>> groupToGroupMap = new HashMap<String, Map<String,Set<String>>>();
	
    @XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
    private Map<String, Map<String, Set<String>>> childGroupToParentGroupMap = new HashMap<String, Map<String,Set<String>>>();
    
	@XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
	private Map<String, Map<String, Set<String>>> groupToRoleMap = new HashMap<String, Map<String,Set<String>>>();
	
    @XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
    private Map<String, Map<String, Set<String>>> groupToResourceMap = new HashMap<String, Map<String,Set<String>>>();
    
    @XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
    private Map<String, Map<String, Set<String>>> groupToOrgMap = new HashMap<String, Map<String,Set<String>>>();

    @XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
    private Map<String, Map<String, Set<String>>> childRoleToParentRoleMap = new HashMap<String, Map<String,Set<String>>>();
    
    @XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
    private Map<String, Map<String, Set<String>>> roleToRoleMap = new HashMap<String, Map<String,Set<String>>>();
    
    @XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
    private Map<String, Map<String, Set<String>>> roleToGroupMap = new HashMap<String, Map<String,Set<String>>>();
    
    @XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
    private Map<String, Map<String, Set<String>>> roleToResourceMap = new HashMap<String, Map<String,Set<String>>>();
    
    @XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
    private Map<String, Map<String, Set<String>>> roleToOrgMap = new HashMap<String, Map<String,Set<String>>>();
    
	@XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
	private Map<String, Map<String, Set<String>>> resourceToResourceMap = new HashMap<String, Map<String,Set<String>>>();
	
    @XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
    private Map<String, Map<String, Set<String>>> childResToParentResMap = new HashMap<String, Map<String,Set<String>>>();
    
    @XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
    private Map<String, Map<String, Set<String>>> resourceToGroupMap = new HashMap<String, Map<String,Set<String>>>();
    
    @XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
    private Map<String, Map<String, Set<String>>> resourceToRoleMap = new HashMap<String, Map<String,Set<String>>>();
    
    @XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
    private Map<String, Map<String, Set<String>>> resourceToOrgMap = new HashMap<String, Map<String,Set<String>>>();
    
    private void addRelationship(final Map<String, Map<String, Set<String>>> parent2ChildMap,
    							 final Map<String, Map<String, Set<String>>> child2ParentMap,
    							 final String parentId,
    							 final String childId,
    							 final String rightId) {
    	if(!parent2ChildMap.containsKey(parentId)) {
    		parent2ChildMap.put(parentId, new HashMap<String, Set<String>>());
    	}
    	if(!child2ParentMap.containsKey(childId)) {
    		child2ParentMap.put(childId, new HashMap<String, Set<String>>());
    	}
    	
    	if(!parent2ChildMap.get(parentId).containsKey(childId)) {
    		parent2ChildMap.get(parentId).put(childId, new HashSet<String>());
    	}
    	if(!child2ParentMap.get(childId).containsKey(parentId)) {
    		child2ParentMap.get(childId).put(parentId, new HashSet<String>());
    	}
    	if(rightId != null) {
    		parent2ChildMap.get(parentId).get(childId).add(rightId);
    		child2ParentMap.get(childId).get(parentId).add(rightId);
    	}
    }
    
    public void addOrg2OrgRelationship(final String parentId, final String childId, final String rightId) {
    	addRelationship(orgToOrgMap, childOrgToParentOrgMap, parentId, childId, rightId);
    }
    
    public void addOrg2RoleRelationship(final String parentId, final String childId, final String rightId) {
    	addRelationship(orgToRoleMap, roleToOrgMap, parentId, childId, rightId);
    }
    
    public void addOrg2GroupRelationship(final String parentId, final String childId, final String rightId) {
    	addRelationship(orgToGroupMap, groupToOrgMap, parentId, childId, rightId);
    }
    
    public void addOrg2ResourceRelationship(final String parentId, final String childId, final String rightId) {
    	addRelationship(orgToResourceMap, resourceToOrgMap, parentId, childId, rightId);
    }
    
    public void addRole2RoleRelationship(final String parentId, final String childId, final String rightId) {
    	addRelationship(roleToRoleMap, childRoleToParentRoleMap, parentId, childId, rightId);
    }
    
    public void addRole2GroupRelationship(final String parentId, final String childId, final String rightId) {
    	addRelationship(roleToGroupMap, groupToRoleMap, parentId, childId, rightId);
    }
    
    public void addRole2ResourceRelationship(final String parentId, final String childId, final String rightId) {
    	addRelationship(roleToResourceMap, resourceToRoleMap, parentId, childId, rightId);
    }
    
    public void addGroup2GroupRelationship(final String parentId, final String childId, final String rightId) {
    	addRelationship(groupToGroupMap, childGroupToParentGroupMap, parentId, childId, rightId);
    }
    
    public void addGroup2ResourceRelationship(final String parentId, final String childId, final String rightId) {
    	addRelationship(groupToResourceMap, resourceToGroupMap, parentId, childId, rightId);
    }
    
    public void addResource2ResourceRelationship(final String parentId, final String childId, final String rightId) {
    	addRelationship(resourceToResourceMap, childResToParentResMap, parentId, childId, rightId);
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

	public Map<String, Map<String, Set<String>>> getChildOrgToParentOrgMap() {
		return childOrgToParentOrgMap;
	}

	public void setChildOrgToParentOrgMap(
			Map<String, Map<String, Set<String>>> childOrgToParentOrgMap) {
		this.childOrgToParentOrgMap = childOrgToParentOrgMap;
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

	public Map<String, Map<String, Set<String>>> getChildGroupToParentGroupMap() {
		return childGroupToParentGroupMap;
	}

	public void setChildGroupToParentGroupMap(
			Map<String, Map<String, Set<String>>> childGroupToParentGroupMap) {
		this.childGroupToParentGroupMap = childGroupToParentGroupMap;
	}

	public Map<String, Map<String, Set<String>>> getGroupToRoleMap() {
		return groupToRoleMap;
	}

	public void setGroupToRoleMap(
			Map<String, Map<String, Set<String>>> groupToRoleMap) {
		this.groupToRoleMap = groupToRoleMap;
	}

	public Map<String, Map<String, Set<String>>> getGroupToResourceMap() {
		return groupToResourceMap;
	}

	public void setGroupToResourceMap(
			Map<String, Map<String, Set<String>>> groupToResourceMap) {
		this.groupToResourceMap = groupToResourceMap;
	}

	public Map<String, Map<String, Set<String>>> getGroupToOrgMap() {
		return groupToOrgMap;
	}

	public void setGroupToOrgMap(Map<String, Map<String, Set<String>>> groupToOrgMap) {
		this.groupToOrgMap = groupToOrgMap;
	}

	public Map<String, Map<String, Set<String>>> getChildRoleToParentRoleMap() {
		return childRoleToParentRoleMap;
	}

	public void setChildRoleToParentRoleMap(
			Map<String, Map<String, Set<String>>> childRoleToParentRoleMap) {
		this.childRoleToParentRoleMap = childRoleToParentRoleMap;
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

	public Map<String, Map<String, Set<String>>> getRoleToOrgMap() {
		return roleToOrgMap;
	}

	public void setRoleToOrgMap(Map<String, Map<String, Set<String>>> roleToOrgMap) {
		this.roleToOrgMap = roleToOrgMap;
	}

	public Map<String, Map<String, Set<String>>> getResourceToResourceMap() {
		return resourceToResourceMap;
	}

	public void setResourceToResourceMap(
			Map<String, Map<String, Set<String>>> resourceToResourceMap) {
		this.resourceToResourceMap = resourceToResourceMap;
	}

	public Map<String, Map<String, Set<String>>> getChildResToParentResMap() {
		return childResToParentResMap;
	}

	public void setChildResToParentResMap(
			Map<String, Map<String, Set<String>>> childResToParentResMap) {
		this.childResToParentResMap = childResToParentResMap;
	}

	public Map<String, Map<String, Set<String>>> getResourceToGroupMap() {
		return resourceToGroupMap;
	}

	public void setResourceToGroupMap(
			Map<String, Map<String, Set<String>>> resourceToGroupMap) {
		this.resourceToGroupMap = resourceToGroupMap;
	}

	public Map<String, Map<String, Set<String>>> getResourceToRoleMap() {
		return resourceToRoleMap;
	}

	public void setResourceToRoleMap(
			Map<String, Map<String, Set<String>>> resourceToRoleMap) {
		this.resourceToRoleMap = resourceToRoleMap;
	}

	public Map<String, Map<String, Set<String>>> getResourceToOrgMap() {
		return resourceToOrgMap;
	}

	public void setResourceToOrgMap(
			Map<String, Map<String, Set<String>>> resourceToOrgMap) {
		this.resourceToOrgMap = resourceToOrgMap;
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
