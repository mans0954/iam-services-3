package org.openiam.authmanager.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.authmanager.common.model.InternalAuthroizationUser;
import org.openiam.authmanager.common.xref.GroupGroupXref;
import org.openiam.authmanager.common.xref.ResourceGroupXref;
import org.openiam.authmanager.common.xref.ResourceResourceXref;
import org.openiam.authmanager.common.xref.ResourceRoleXref;
import org.openiam.authmanager.common.xref.RoleGroupXref;
import org.openiam.authmanager.common.xref.RoleRoleXref;
import org.openiam.authmanager.dao.GroupDAO;
import org.openiam.authmanager.dao.GroupGroupXrefDAO;
import org.openiam.authmanager.dao.ResourceDAO;
import org.openiam.authmanager.dao.ResourceGroupXrefDAO;
import org.openiam.authmanager.dao.ResourceResourceXrefDAO;
import org.openiam.authmanager.dao.ResourceRoleXrefDAO;
import org.openiam.authmanager.dao.ResourceUserXrefDAO;
import org.openiam.authmanager.dao.RoleDAO;
import org.openiam.authmanager.dao.RoleGroupXrefDAO;
import org.openiam.authmanager.dao.RoleRoleXrefDAO;
import org.openiam.authmanager.dao.UserDAO;
import org.openiam.authmanager.model.EntitlementsMatrix;
import org.openiam.authmanager.model.ResourceEntitlementToken;
import org.openiam.authmanager.service.AuthorizationManagerAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationManagerAdminServiceImpl implements AuthorizationManagerAdminService {

	@Autowired
	@Qualifier("jdbcGroupDAO")
	private GroupDAO groupDAO;
	
	@Autowired
	@Qualifier("jdbcResourceDAO")
	private ResourceDAO resourceDAO;
	
	@Autowired
	@Qualifier("jdbcRoleDAO")
	private RoleDAO roleDAO;
	
	@Autowired
	@Qualifier("jdbcUserDao")
	private UserDAO userDAO;
	
	@Autowired
	@Qualifier("jdbcGroupGroupXrefDao")
	private GroupGroupXrefDAO groupGroupXrefDAO;
	
	@Autowired
	@Qualifier("jdbcResourceGroupXrefDAO")
	private ResourceGroupXrefDAO resourceGroupXrefDAO;
	
	@Autowired
	@Qualifier("jdbcResourceResourceXrefDAO")
	private ResourceResourceXrefDAO resourceResourceXrefDAO;
	
	@Autowired
	@Qualifier("jdbcResourceRoleXrefDAO")
	private ResourceRoleXrefDAO resourceRoleXrefDAO;
	
	@Autowired
	@Qualifier("jdbcRoleGroupXrefDAO")
	private RoleGroupXrefDAO roleGroupXrefDAO;
	
	@Autowired
	@Qualifier("jdbcRoleRoleXrefDAO")
	private RoleRoleXrefDAO roleRoleXrefDAO;
	
	@Override
	public ResourceEntitlementToken getNonCachedEntitlementsForUser(final String userId) {
		ResourceEntitlementToken retVal = null;
		if(userId != null) {
			final InternalAuthroizationUser user = userDAO.getFullUser(userId);
			if(user != null) {
				retVal = new ResourceEntitlementToken();
				final Map<String, AuthorizationResource> resourceMap = getResourceMap();
				final Map<String, AuthorizationRole> roleMap = getRoleMap();
				final Map<String, AuthorizationGroup> groupMap = getGroupMap();
				
				/* compile groups */
				final Set<AuthorizationGroup> compiledGroups = new HashSet<AuthorizationGroup>();
				final Set<AuthorizationGroup> userGroups = new HashSet<AuthorizationGroup>();
				if(CollectionUtils.isNotEmpty(user.getGroupIds())) {
					for(final String groupId : user.getGroupIds()) {
						if(groupMap.containsKey(groupId)) {
							userGroups.add(groupMap.get(groupId));
						}
					}
				}
				
				final Map<String, Set<AuthorizationGroup>> group2GroupMap = getGroup2GroupMap(groupMap);
				final Set<AuthorizationGroup> visitedGroupSet = new HashSet<AuthorizationGroup>();
				for(final AuthorizationGroup group : userGroups) {
					final Set<AuthorizationGroup> tempCompiledGroups = compileGroups(group, group2GroupMap, visitedGroupSet);
					if(CollectionUtils.isNotEmpty(tempCompiledGroups)) {
						compiledGroups.addAll(tempCompiledGroups);
					}
				}
				compiledGroups.addAll(userGroups);
				
				
				/* compile roles */
				final Set<AuthorizationRole> compiledRoles = new HashSet<AuthorizationRole>();
				final Set<AuthorizationRole> userRoles = new HashSet<AuthorizationRole>();
				if(CollectionUtils.isNotEmpty(user.getRoleIds())) {
					for(final String roleId : user.getRoleIds()) {
						if(roleMap.containsKey(roleId)) {
							userRoles.add(roleMap.get(roleId));
						}
					}
				}
				
				final Map<String, Set<AuthorizationRole>> role2RoleMap = getRole2RoleMap(roleMap);
				final Set<AuthorizationRole> visitedRoleSet = new HashSet<AuthorizationRole>();
				for(final AuthorizationRole role : userRoles) {
					final Set<AuthorizationRole> tempCompiledRoles = compileRoles(role, role2RoleMap, visitedRoleSet);
					if(CollectionUtils.isNotEmpty(tempCompiledRoles)) {
						compiledRoles.addAll(tempCompiledRoles);
					}
				}
				compiledRoles.addAll(userRoles);
				
				final Map<String, Set<AuthorizationRole>> group2RoleMap = getGroup2RoleMap(roleMap);
				final Set<AuthorizationRole> visitedSet = new HashSet<AuthorizationRole>();
				for(final AuthorizationGroup group : compiledGroups) {
					if(group2RoleMap.containsKey(group.getId())) {
						for(final AuthorizationRole role : group2RoleMap.get(group.getId())) {
							compiledRoles.add(role);
							compiledRoles.addAll(compileRoles(role, role2RoleMap, visitedSet));
						}
					}
				}
				
				/* compiles the roles for compiled groups */
				compiledRoles.addAll(getRolesForGroups(compiledGroups, group2RoleMap));
				
				/* resource membership data */
				final Map<String, Set<AuthorizationResource>> role2ResourceMap = getRole2ResourceMap(resourceMap);
				final Map<String, Set<AuthorizationResource>> group2ResoruceMap = getGroup2ResourceMap(resourceMap);
				final Map<String, Set<AuthorizationResource>> resource2ResourceMap = getResource2ResourceMap(resourceMap);
				
				/* get all resources for the compiled roles */
				final Set<AuthorizationResource> resourcesForCompiledRoles = getResourcesForRoles(compiledRoles, role2ResourceMap);
				
				/* get all resources for the compiled groups */
				final Set<AuthorizationResource> resourcesForCompiledGroups = getResourcesForGroups(compiledGroups, group2ResoruceMap);
				
				/* compiles resources for groups and roles */
				final Set<AuthorizationResource> compiledResources = new HashSet<AuthorizationResource>();
				compiledResources.addAll(getCompiledResources(resourcesForCompiledRoles, resource2ResourceMap));
				compiledResources.addAll(getCompiledResources(resourcesForCompiledGroups, resource2ResourceMap));
				compiledResources.addAll(resourcesForCompiledGroups);
				compiledResources.addAll(resourcesForCompiledRoles);
				
				
				/* set the direct resources, and add any indirect resources to the compiled set */
				if(CollectionUtils.isNotEmpty(user.getResourceIds())) {
					final Set<AuthorizationResource> directResources = new HashSet<AuthorizationResource>();
					for(final String resourceId : user.getResourceIds()) {
						directResources.add(resourceMap.get(resourceId));
					}
					retVal.addDirectResources(directResources);
					compiledResources.addAll(getCompiledResources(directResources, resource2ResourceMap));
				}
				
				/* set the indirect set as compiled */
				retVal.addIndirectResource(compiledResources);
				
			}
		}
		return retVal;
	}

	@Override
	public ResourceEntitlementToken getNonCachedEntitlementsForGroup(final String groupId) {
		ResourceEntitlementToken retVal = null;
		if(groupId != null) {
			final Map<String, AuthorizationGroup> groupMap = getGroupMap();
			
			if(groupMap.containsKey(groupId)) {
				retVal = new ResourceEntitlementToken();
				final Map<String, AuthorizationResource> resourceMap = getResourceMap();
				final Map<String, AuthorizationRole> roleMap = getRoleMap();
				final Map<String, Set<AuthorizationGroup>> group2GroupMap = getGroup2GroupMap(groupMap);
				final Map<String, Set<AuthorizationRole>> group2RoleMap = getGroup2RoleMap(roleMap);
				final Map<String, Set<AuthorizationResource>> group2ResoruceMap = getGroup2ResourceMap(resourceMap);
				final Map<String, Set<AuthorizationRole>> role2RoleMap = getRole2RoleMap(roleMap);
				final Map<String, Set<AuthorizationResource>> role2ResourceMap = getRole2ResourceMap(resourceMap);
				final Map<String, Set<AuthorizationResource>> resource2ResourceMap = getResource2ResourceMap(resourceMap);
				
				/* compile roles */
				final Set<AuthorizationRole> compiledRoles = new HashSet<AuthorizationRole>();
				if(group2RoleMap.containsKey(groupId)) {
					final Set<AuthorizationRole> visitedSet = new HashSet<AuthorizationRole>();
					for(final AuthorizationRole role : group2RoleMap.get(groupId)) {
						compiledRoles.add(role);
						compiledRoles.addAll(compileRoles(role, role2RoleMap, visitedSet));
					}
				}
				
				/* compile the roles for this group */
				final Set<AuthorizationGroup> compiledGroups = compileGroups(groupMap.get(groupId), group2GroupMap, new HashSet<AuthorizationGroup>());
				
				/* compiles the roles for compiled groups */
				compiledRoles.addAll(getRolesForGroups(compiledGroups, group2RoleMap));
				
				/* get all resources for the compiled roles */
				final Set<AuthorizationResource> resourcesForCompiledRoles = getResourcesForRoles(compiledRoles, role2ResourceMap);
				
				/* get all resources for the compiled groups */
				final Set<AuthorizationResource> resourcesForCompiledGroups = getResourcesForGroups(compiledGroups, group2ResoruceMap);
				
				/* compiles resources for groups and roles */
				final Set<AuthorizationResource> compiledResources = new HashSet<AuthorizationResource>();
				compiledResources.addAll(getCompiledResources(resourcesForCompiledRoles, resource2ResourceMap));
				compiledResources.addAll(getCompiledResources(resourcesForCompiledGroups, resource2ResourceMap));
				compiledResources.addAll(resourcesForCompiledGroups);
				compiledResources.addAll(resourcesForCompiledRoles);
				
				
				/* set the direct resources, and add any indirect resources to the compiled set */
				if(group2ResoruceMap.containsKey(groupId)) {
					final Set<AuthorizationResource> directResources = group2ResoruceMap.get(groupId);
					retVal.addDirectResources(directResources);
					compiledResources.addAll(getCompiledResources(directResources, resource2ResourceMap));
				}
				
				/* set the indirect set as compiled */
				retVal.addIndirectResource(compiledResources);
			}
		}
		return retVal;
	}

	@Override
	public ResourceEntitlementToken getNonCachedEntitlementsForRole(final String roleId) {
		ResourceEntitlementToken retVal = null;
		if(roleId != null) {
			final Map<String, AuthorizationRole> roleMap = getRoleMap();
			
			if(roleMap.containsKey(roleId)) {
				retVal = new ResourceEntitlementToken();
				final Map<String, AuthorizationResource> resourceMap = getResourceMap();
				final Map<String, Set<AuthorizationRole>> role2RoleMap = getRole2RoleMap(roleMap);
				final Map<String, Set<AuthorizationResource>> resource2ResourceMap = getResource2ResourceMap(resourceMap);
				final Map<String, Set<AuthorizationResource>> role2ResourceMap = getRole2ResourceMap(resourceMap);
				
				/* compile the roles for this role */
				final Set<AuthorizationRole> compiledRoles = compileRoles(roleMap.get(roleId), role2RoleMap, new HashSet<AuthorizationRole>());
				
				/* get all resources for the compiled roles */
				final Set<AuthorizationResource> resourcesForCompiledRoles = getResourcesForRoles(compiledRoles, role2ResourceMap);
				
				/*get all child resources for the already-compiled resoruces */
				final Set<AuthorizationResource> compiledResources = getCompiledResources(resourcesForCompiledRoles, resource2ResourceMap);
				compiledResources.addAll(resourcesForCompiledRoles);
				
				/* set the direct resources, and add any indirect resources to the compiled set */
				if(role2ResourceMap.containsKey(roleId)) {
					final Set<AuthorizationResource> directResources = role2ResourceMap.get(roleId);
					retVal.addDirectResources(directResources);
					compiledResources.addAll(getCompiledResources(directResources, resource2ResourceMap));
				}
				
				/* set the indirect set as compiled */
				retVal.addIndirectResource(compiledResources);
			}
		}
		return retVal;
	}
	
	private Map<String, AuthorizationGroup> getGroupMap() {
		final List<AuthorizationGroup> groupList = groupDAO.getList();
		final Map<String, AuthorizationGroup> groupMap = new HashMap<String, AuthorizationGroup>();
		for(final AuthorizationGroup group : groupList) {
			groupMap.put(group.getId(), group);
		}
		return groupMap;
	}
	
	private Map<String, AuthorizationRole> getRoleMap() {
		final List<AuthorizationRole> roleList = roleDAO.getList();
		final Map<String, AuthorizationRole> roleMap = new HashMap<String, AuthorizationRole>();
		for(final AuthorizationRole role : roleList) {
			roleMap.put(role.getId(), role);
		}
		return roleMap;
	}
	
	private Set<AuthorizationResource> getCompiledResources(final Set<AuthorizationResource> resourceSet,
															final Map<String, Set<AuthorizationResource>> resource2ResourceMap) {
		final Set<AuthorizationResource> retval = new HashSet<AuthorizationResource>();
		if(CollectionUtils.isNotEmpty(resourceSet)) {
			final Set<AuthorizationResource> visitedSet = new HashSet<AuthorizationResource>();
			for(final AuthorizationResource resource : resourceSet) {
				retval.addAll(getCompiledResources(resource, resource2ResourceMap, visitedSet));
			}
		}
		return retval;
	}
	
	private Set<AuthorizationResource> getCompiledResources(final AuthorizationResource resource, 
															final Map<String, Set<AuthorizationResource>> resource2ResourceMap,
															final Set<AuthorizationResource> visitedSet) {
		final Set<AuthorizationResource> retval = new HashSet<AuthorizationResource>();
		if(resource != null && resource2ResourceMap != null && visitedSet != null) {
			if(!visitedSet.contains(resource)) {
				visitedSet.add(resource);
				if(resource.isInheritFromParent()) {
					if(resource2ResourceMap.containsKey(resource.getId())) {
						for(final AuthorizationResource child : resource2ResourceMap.get(resource.getId())) {
							retval.add(child);
							retval.addAll(getCompiledResources(child, resource2ResourceMap, visitedSet));
						}
					}
				}
			}
		}
		return retval;
	}
	
	private Set<AuthorizationResource> getResourcesForGroups(final Set<AuthorizationGroup> compiledGroups,
															 final Map<String, Set<AuthorizationResource>> group2ResourceMap) {
		final Set<AuthorizationResource> retval = new HashSet<AuthorizationResource>();
		if(CollectionUtils.isNotEmpty(compiledGroups)) {
			for(final AuthorizationGroup group : compiledGroups) {
				if(group2ResourceMap.containsKey(group.getId())) {
					retval.addAll(group2ResourceMap.get(group.getId()));
				}
			}
		}
		return retval;
	}
	
	private Set<AuthorizationRole> getRolesForGroups(final Set<AuthorizationGroup> compiledGroups,
													 final Map<String, Set<AuthorizationRole>> group2RoleMap) {
		final Set<AuthorizationRole> retval = new HashSet<AuthorizationRole>();
		if(CollectionUtils.isNotEmpty(compiledGroups)) {
			for(final AuthorizationGroup group : compiledGroups) {
				if(group2RoleMap.containsKey(group.getId())) {
					retval.addAll(group2RoleMap.get(group.getId()));
				}
			}
		}
		return retval;
	}
	
	private Set<AuthorizationResource> getResourcesForRoles(final Set<AuthorizationRole> compiledRoles, 
															final Map<String, Set<AuthorizationResource>> role2ResourceMap) {
		final Set<AuthorizationResource> retval = new HashSet<AuthorizationResource>();
		if(CollectionUtils.isNotEmpty(compiledRoles)) {
			for(final AuthorizationRole role : compiledRoles) {
				if(role2ResourceMap.containsKey(role.getId())) {
					retval.addAll(role2ResourceMap.get(role.getId()));
				}
			}
		}
		return retval;
	}
	
	private Set<AuthorizationGroup> compileGroups(final AuthorizationGroup group, 
												  final Map<String, Set<AuthorizationGroup>> group2GroupMap, 
												  final Set<AuthorizationGroup> visitedSet) {
		final Set<AuthorizationGroup> retval = new HashSet<AuthorizationGroup>();
		if(group != null && group2GroupMap != null && visitedSet != null) {
			if(!visitedSet.contains(group)) {
				visitedSet.add(group);
				if(group2GroupMap.containsKey(group.getId())) {
					for(final AuthorizationGroup child : group2GroupMap.get(group.getId())) {
						retval.add(child);
						retval.addAll(compileGroups(child, group2GroupMap, visitedSet));
					}
				}
			}
		}
		return retval;
	}
	
	private Set<AuthorizationRole> compileRoles(final AuthorizationRole role, 
												final Map<String, Set<AuthorizationRole>> role2RoleMap, 
												final Set<AuthorizationRole> visitedSet) {
		final Set<AuthorizationRole> retval = new HashSet<AuthorizationRole>();
		if(role != null && role2RoleMap != null && visitedSet != null) {
			if(!visitedSet.contains(role)) {
				visitedSet.add(role);
				if(role2RoleMap.containsKey(role.getId())) {
					for(final AuthorizationRole child : role2RoleMap.get(role.getId())) {
						retval.add(child);
						retval.addAll(compileRoles(child, role2RoleMap, visitedSet));
					}
				}
			}
		}
		return retval;
	}
	
	private Map<String, Set<AuthorizationResource>> getGroup2ResourceMap(final Map<String, AuthorizationResource> resourceMap) {
		final Map<String, Set<AuthorizationResource>> group2ResourceMap = new HashMap<String, Set<AuthorizationResource>>();
		final List<ResourceGroupXref> xrefs = resourceGroupXrefDAO.getList();
		for(final ResourceGroupXref xref : xrefs) {
			if(!group2ResourceMap.containsKey(xref.getGroupId())) {
				group2ResourceMap.put(xref.getGroupId(), new HashSet<AuthorizationResource>());
			}
			group2ResourceMap.get(xref.getGroupId()).add(resourceMap.get(xref.getResourceId()));
		}
		return group2ResourceMap;
	}
	
	private Map<String, Set<AuthorizationResource>> getRole2ResourceMap(final Map<String, AuthorizationResource> resourceMap) {
		final List<ResourceRoleXref> xrefs = resourceRoleXrefDAO.getList();
		final Map<String, Set<AuthorizationResource>> role2ResourceMap = new HashMap<String, Set<AuthorizationResource>>();
		for(final ResourceRoleXref xref : xrefs) {
			if(!role2ResourceMap.containsKey(xref.getRoleId())) {
				role2ResourceMap.put(xref.getRoleId(), new HashSet<AuthorizationResource>());
			}
			role2ResourceMap.get(xref.getRoleId()).add(resourceMap.get(xref.getResourceId()));
		}
		return role2ResourceMap;
	}
	
	private Map<String, Set<AuthorizationRole>> getGroup2RoleMap(final Map<String, AuthorizationRole> roleMap) {
		final Map<String, Set<AuthorizationRole>> group2RoleMap = new HashMap<String, Set<AuthorizationRole>>();
		final List<RoleGroupXref> xrefs = roleGroupXrefDAO.getList();
		for(final RoleGroupXref xref : xrefs) {
			if(!group2RoleMap.containsKey(xref.getGroupId())) {
				group2RoleMap.put(xref.getGroupId(), new HashSet<AuthorizationRole>());
			}
			group2RoleMap.get(xref.getGroupId()).add(roleMap.get(xref.getRoleId()));
		}
		return group2RoleMap;
	}
	
	private Map<String, Set<AuthorizationGroup>> getGroup2GroupMap(final Map<String, AuthorizationGroup> groupMap) {
		final List<GroupGroupXref> xrefs = groupGroupXrefDAO.getList();
		final Map<String, Set<AuthorizationGroup>> groupXrefMap = new HashMap<String, Set<AuthorizationGroup>>();
		for(final GroupGroupXref xref : xrefs) {
			if(!groupXrefMap.containsKey(xref.getMemberGroupId())) {
				groupXrefMap.put(xref.getMemberGroupId(), new HashSet<AuthorizationGroup>());
			}
			groupXrefMap.get(xref.getMemberGroupId()).add(groupMap.get(xref.getGroupId()));
		}
		return groupXrefMap;
	}
	
	private Map<String, Set<AuthorizationRole>> getRole2RoleMap(final Map<String, AuthorizationRole> roleMap) {
		final List<RoleRoleXref> roleRoleXrefs = roleRoleXrefDAO.getList();
		final Map<String, Set<AuthorizationRole>> roleXrefMap = new HashMap<String, Set<AuthorizationRole>>();
		for(final RoleRoleXref xref : roleRoleXrefs) {
			if(!roleXrefMap.containsKey(xref.getMemberRoleId())) {
				roleXrefMap.put(xref.getMemberRoleId(), new HashSet<AuthorizationRole>());
			}
			roleXrefMap.get(xref.getMemberRoleId()).add(roleMap.get(xref.getRoleId()));
		}
		return roleXrefMap;
	}
	
	private Map<String, Set<AuthorizationResource>> getResource2ResourceMap(final Map<String, AuthorizationResource> resourceMap) {
		final List<ResourceResourceXref> resourceResourceXrefs = resourceResourceXrefDAO.getList();
		Map<String, Set<AuthorizationResource>> resourceXrefMap = new HashMap<String, Set<AuthorizationResource>>();
		for(final ResourceResourceXref xref : resourceResourceXrefs) {
			if(!resourceXrefMap.containsKey(xref.getMemberResourceId())) {
				resourceXrefMap.put(xref.getMemberResourceId(), new HashSet<AuthorizationResource>());
			}
			resourceXrefMap.get(xref.getMemberResourceId()).add(resourceMap.get(xref.getResourceId()));
		}
		return resourceXrefMap;
	}
	
	private Map<String, AuthorizationResource> getResourceMap() {
		final Map<String, AuthorizationResource> resourceMap = new HashMap<String, AuthorizationResource>();
		final List<AuthorizationResource> resourceList = resourceDAO.getList();
		for(final AuthorizationResource resource : resourceList) {
			resourceMap.put(resource.getId(), resource);
		}
		return resourceMap;
	}

	@Override
	public EntitlementsMatrix getEntitlementsMatrix(final String entityId, final String entityType) {
		return null;
	}
}
