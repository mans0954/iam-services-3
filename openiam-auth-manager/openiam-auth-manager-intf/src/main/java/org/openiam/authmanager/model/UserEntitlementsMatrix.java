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

import org.apache.tomcat.util.http.parser.Authorization;
import org.openiam.authmanager.common.model.*;
import org.openiam.authmanager.ws.request.AuthorizationMatrixMapAdapter;
import org.openiam.authmanager.ws.request.AuthorizationMatrixMapToSetAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EntitlementsMatrix", propOrder = {
	"userId",
	"resourceMap",
	"groupMap",
	"roleMap",

	"directRoleIds",
	"compiledRoleIds",

	"directGroupIds",
	"compiledGroupIds",

	"directResourceIds",
	"compiledResourceIds",

	"groupToGroupMap",
	"childGroupToParentGroupMap",
	"groupToResourceMap",

	"roleToResourceMap",
    "roleToGroupMap",
    "roleToRoleMap",
	"childRoleToParentRoleMap",

	"resourceToResourceMap",
	"childResToParentResMap",
	"resourceToRoleMap",
	"resourceToGroupMap"
})
public class UserEntitlementsMatrix implements Serializable {
	private String userId;


	// resourceId -> Resource
	private Map<String, AuthorizationResource> resourceMap;
	// groupId -> Group
	private Map<String, AuthorizationGroup> groupMap;
	// roleId -> Role
	private Map<String, AuthorizationRole> roleMap;


	@XmlJavaTypeAdapter(AuthorizationMatrixMapAdapter.class)
	private Map<String, Set<String>> directRoleIds;
	@XmlJavaTypeAdapter(AuthorizationMatrixMapAdapter.class)
	private Map<String, Set<String>> compiledRoleIds;


	@XmlJavaTypeAdapter(AuthorizationMatrixMapAdapter.class)
	private Map<String, Set<String>> directGroupIds;
	@XmlJavaTypeAdapter(AuthorizationMatrixMapAdapter.class)
	private Map<String, Set<String>> compiledGroupIds;

	@XmlJavaTypeAdapter(AuthorizationMatrixMapAdapter.class)
	private Map<String, Set<String>> directResourceIds;
	@XmlJavaTypeAdapter(AuthorizationMatrixMapAdapter.class)
	private Map<String, Set<String>> compiledResourceIds;



	@XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
	private Map<String, Map<String, Set<String>>> groupToGroupMap = new HashMap<String, Map<String, Set<String>>>();
	@XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
	private Map<String, Map<String, Set<String>>> childGroupToParentGroupMap = new HashMap<String, Map<String,Set<String>>>();
	@XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
	private Map<String, Map<String, Set<String>>> groupToResourceMap = new HashMap<String, Map<String, Set<String>>>();


	@XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
	private Map<String, Map<String, Set<String>>> roleToRoleMap = new HashMap<String, Map<String, Set<String>>>();
	@XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
	private Map<String, Map<String, Set<String>>> childRoleToParentRoleMap = new HashMap<String, Map<String,Set<String>>>();
	@XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
	private Map<String, Map<String, Set<String>>> roleToGroupMap = new HashMap<String, Map<String, Set<String>>>();
	@XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
	private Map<String, Map<String, Set<String>>> roleToResourceMap = new HashMap<String, Map<String, Set<String>>>();

	@XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
	private Map<String, Map<String, Set<String>>> resourceToResourceMap = new HashMap<String, Map<String, Set<String>>>();
	@XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
	private Map<String, Map<String, Set<String>>> childResToParentResMap = new HashMap<String, Map<String,Set<String>>>();
	@XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
	private Map<String, Map<String, Set<String>>> resourceToRoleMap = new HashMap<String, Map<String, Set<String>>>();
	@XmlJavaTypeAdapter(AuthorizationMatrixMapToSetAdapter.class)
	private Map<String, Map<String, Set<String>>> resourceToGroupMap = new HashMap<String, Map<String, Set<String>>>();

	public UserEntitlementsMatrix() {
	}

	public void populate(final String userId,
						 final ResourceEntitlementToken resourceEntitlementToken,
						 final RoleEntitlementToken roleEntitlementToken,
						 final GroupEntitlementToken groupEntitlementToken) {
		this.userId = userId;
		this.directRoleIds = roleEntitlementToken.getDirectEntitlementIds();
		this.compiledRoleIds = roleEntitlementToken.getEntitlementIds();

		this.directGroupIds = groupEntitlementToken.getDirectEntitlementIds();
		this.compiledGroupIds = groupEntitlementToken.getEntitlementIds();

		this.directResourceIds = resourceEntitlementToken.getDirectEntitlementIds();
		this.compiledResourceIds = resourceEntitlementToken.getEntitlementIds();

		this.roleMap = roleEntitlementToken.getEntitlementMap();
		this.groupMap = groupEntitlementToken.getEntitlementMap();
		this.resourceMap = resourceEntitlementToken.getEntitlementMap();
	}


	private void addRelationship(final Map<String, Map<String, Set<String>>> parent2ChildMap,
								 final String parentId,
								 final String childId,
								 final String rightId) {
		addRelationship(parent2ChildMap, null, parentId, childId, rightId);
	}

	private void addRelationship(final Map<String, Map<String, Set<String>>> parent2ChildMap,
								 final Map<String, Map<String, Set<String>>> child2ParentMap,
								 final String parentId,
								 final String childId,
								 final String rightId) {
		if (!parent2ChildMap.containsKey(parentId)) {
			parent2ChildMap.put(parentId, new HashMap<String, Set<String>>());
		}
		if (child2ParentMap != null && !child2ParentMap.containsKey(childId)) {
			child2ParentMap.put(childId, new HashMap<String, Set<String>>());
		}

		if (!parent2ChildMap.get(parentId).containsKey(childId)) {
			parent2ChildMap.get(parentId).put(childId, new HashSet<String>());
		}
		if (child2ParentMap != null && !child2ParentMap.get(childId).containsKey(parentId)) {
			child2ParentMap.get(childId).put(parentId, new HashSet<String>());
		}
		if (rightId != null) {
			parent2ChildMap.get(parentId).get(childId).add(rightId);
			if (child2ParentMap != null) {
				child2ParentMap.get(childId).get(parentId).add(rightId);
			}
		}
	}

	public void addRole2RoleRelationship(final String parentId, final String childId, final String rightId) {
		addRelationship(roleToRoleMap, childRoleToParentRoleMap, parentId, childId, rightId);
	}

	public void addRole2GroupRelationship(final String parentId, final String childId, final String rightId) {
		addRelationship(roleToGroupMap, parentId, childId, rightId);
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


	public Map<String, AuthorizationResource> getResourceMap() {
		return resourceMap;
	}

	public Map<String, AuthorizationGroup> getGroupMap() {
		return groupMap;
	}

	public Map<String, AuthorizationRole> getRoleMap() {
		return roleMap;
	}

	public Map<String, Set<String>> getDirectRoleIds() {
		return directRoleIds;
	}

	public Map<String, Set<String>> getCompiledRoleIds() {
		return compiledRoleIds;
	}

	public Map<String, Set<String>> getDirectGroupIds() {
		return directGroupIds;
	}

	public Map<String, Set<String>> getCompiledGroupIds() {
		return compiledGroupIds;
	}

	public Map<String, Set<String>> getDirectResourceIds() {
		return directResourceIds;
	}

	public Map<String, Set<String>> getCompiledResourceIds() {
		return compiledResourceIds;
	}

	public Map<String, Map<String, Set<String>>> getGroupToGroupMap() {
		return groupToGroupMap;
	}

	public Map<String, Map<String, Set<String>>> getGroupToResourceMap() {
		return groupToResourceMap;
	}

	public Map<String, Map<String, Set<String>>> getRoleToRoleMap() {
		return roleToRoleMap;
	}

	public Map<String, Map<String, Set<String>>> getRoleToGroupMap() {
		return roleToGroupMap;
	}

	public Map<String, Map<String, Set<String>>> getRoleToResourceMap() {
		return roleToResourceMap;
	}

	public Map<String, Map<String, Set<String>>> getResourceToResourceMap() {
		return resourceToResourceMap;
	}

	public Map<String, Map<String, Set<String>>> getResourceToRoleMap() {
		return resourceToRoleMap;
	}

	public Map<String, Map<String, Set<String>>> getResourceToGroupMap() {
		return resourceToGroupMap;
	}

	public Map<String, Map<String, Set<String>>> getChildGroupToParentGroupMap() {
		return childGroupToParentGroupMap;
	}

	public Map<String, Map<String, Set<String>>> getChildRoleToParentRoleMap() {
		return childRoleToParentRoleMap;
	}

	public Map<String, Map<String, Set<String>>> getChildResToParentResMap() {
		return childResToParentResMap;
	}
}