package org.openiam.authmanager.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.authmanager.common.SetStringResponse;
import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationOrganization;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.authmanager.common.model.InternalAuthroizationUser;
import org.openiam.authmanager.common.xref.GroupGroupXref;
import org.openiam.authmanager.common.xref.ResourceGroupXref;
import org.openiam.authmanager.common.xref.ResourceResourceXref;
import org.openiam.authmanager.common.xref.ResourceRoleXref;
import org.openiam.authmanager.common.xref.RoleGroupXref;
import org.openiam.authmanager.common.xref.RoleRoleXref;
import org.openiam.authmanager.model.ResourceEntitlementToken;
import org.openiam.authmanager.model.UserEntitlementsMatrix;
import org.openiam.authmanager.service.AuthorizationManagerAdminService;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.membership.GroupToGroupMembershipHibernateDAO;
import org.openiam.idm.srvc.membership.GroupToResourceMembershipHibernateDAO;
import org.openiam.idm.srvc.membership.OrgToGroupMembershipHibernateDAO;
import org.openiam.idm.srvc.membership.OrgToOrgMembershipHibernateDAO;
import org.openiam.idm.srvc.membership.OrgToResourceMembershipHibernateDAO;
import org.openiam.idm.srvc.membership.OrgToRoleMembershipHibernateDAO;
import org.openiam.idm.srvc.membership.ResourceToResourceMembershipHibernateDAO;
import org.openiam.idm.srvc.membership.RoleToGroupMembershipHibernateDAO;
import org.openiam.idm.srvc.membership.RoleToResourceMembershipHibernateDAO;
import org.openiam.idm.srvc.membership.RoleToRoleMembershipHibernateDAO;
import org.openiam.idm.srvc.membership.UserToGroupMembershipHiberanteDAO;
import org.openiam.idm.srvc.membership.UserToOrgMembershipHiberanteDAO;
import org.openiam.idm.srvc.membership.UserToResourceMembershipHiberanteDAO;
import org.openiam.idm.srvc.membership.UserToRoleMembershipHiberanteDAO;
import org.openiam.idm.srvc.membership.domain.AbstractMembershipXrefEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.service.OrganizationDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.domain.UserToGroupMembershipXrefEntity;
import org.openiam.idm.srvc.user.domain.UserToOrganizationMembershipXrefEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("authorizationManagerAdminService")
public class AuthorizationManagerAdminServiceImpl implements AuthorizationManagerAdminService {

	@Autowired
	private OrganizationDAO organizationDAO;
	
	@Autowired
	private ResourceDAO resourceDAO;
	
	@Autowired
	private GroupDAO groupDAO;
	
	@Autowired
	private RoleDAO roleDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private GroupToGroupMembershipHibernateDAO group2GroupHibernateDAO;
	
	@Autowired
	private GroupToResourceMembershipHibernateDAO group2ResourceHibernateDAO;
	
	@Autowired
	private OrgToGroupMembershipHibernateDAO org2GroupHibernateDAO;
	
	@Autowired
	private OrgToOrgMembershipHibernateDAO org2OrgMembershipHibernateDAO;
	
	@Autowired
	private OrgToResourceMembershipHibernateDAO org2ResourceMembershipHibernateDAO;
	
	@Autowired
	private OrgToRoleMembershipHibernateDAO org2RoleMembershipHibernateDAO;
	
	@Autowired
	private ResourceToResourceMembershipHibernateDAO resource2ResourceMembershipHibernateDAO;
	
	@Autowired
	private RoleToGroupMembershipHibernateDAO role2GroupMembershipHibernateDAO;
	
	@Autowired
	private RoleToResourceMembershipHibernateDAO role2ResourceMembershipHibernateDAO;
	
	@Autowired
	private RoleToRoleMembershipHibernateDAO role2RoleMembershipHibernateDAO;
	
	@Autowired
	private UserToGroupMembershipHiberanteDAO user2GroupMembershipHibernateDAO;
	
	@Autowired
	private UserToOrgMembershipHiberanteDAO user2OrgMembershipHibernateDAO;
	
	@Autowired
	private UserToResourceMembershipHiberanteDAO user2ResourceMembershipHibernateDAO;
	
	@Autowired
	private UserToRoleMembershipHiberanteDAO user2RoleMembershipHibernateDAO;
	
	private Set<AbstractMembershipXrefEntity> visitOrganizations(final OrganizationEntity entity, final Set<OrganizationEntity> visitedSet) {
		final Set<AbstractMembershipXrefEntity> retVal = new HashSet<AbstractMembershipXrefEntity>();
		if(!visitedSet.contains(entity)) {
			visitedSet.add(entity);
			if(CollectionUtils.isNotEmpty(entity.getParentOrganizations())) {
				entity.getParentOrganizations().forEach(xref -> {
					retVal.add(xref);
					retVal.addAll(visitOrganizations(xref.getEntity(), visitedSet));
				});
			}
		}
		return retVal;
	}
	
	private Set<AbstractMembershipXrefEntity> visitRoles(final RoleEntity entity, final Set<RoleEntity> visitedSet) {
		final Set<AbstractMembershipXrefEntity> retVal = new HashSet<AbstractMembershipXrefEntity>();
		if(!visitedSet.contains(entity)) {
			visitedSet.add(entity);
			if(CollectionUtils.isNotEmpty(entity.getParentRoles())) {
				entity.getParentRoles().forEach(xref -> {
					retVal.add(xref);
					retVal.addAll(visitRoles(xref.getEntity(), visitedSet));
				});
			}
		}
		return retVal;
	}
	
	private Set<AbstractMembershipXrefEntity> visitGroups(final GroupEntity entity, final Set<GroupEntity> visitedSet) {
		final Set<AbstractMembershipXrefEntity> retVal = new HashSet<AbstractMembershipXrefEntity>();
		if(!visitedSet.contains(entity)) {
			visitedSet.add(entity);
			if(CollectionUtils.isNotEmpty(entity.getParentGroups())) {
				entity.getParentGroups().forEach(xref -> {
					retVal.add(xref);
					retVal.addAll(visitGroups(xref.getEntity(), visitedSet));
				});
			}
		}
		return retVal;
	}
	
	private Set<AbstractMembershipXrefEntity> visitResources(final ResourceEntity entity, final Set<ResourceEntity> visitedSet) {
		final Set<AbstractMembershipXrefEntity> retVal = new HashSet<AbstractMembershipXrefEntity>();
		if(!visitedSet.contains(entity)) {
			visitedSet.add(entity);
			if(CollectionUtils.isNotEmpty(entity.getParentResources())) {
				entity.getParentResources().forEach(xref -> {
					retVal.add(xref);
					retVal.addAll(visitResources(xref.getEntity(), visitedSet));
				});
			}
		}
		return retVal;
	}
	
	@Override
	public ResourceEntitlementToken getNonCachedEntitlementsForUser(final String userId) {
		final ResourceEntitlementToken retVal = new ResourceEntitlementToken();
		if(userId != null) {
			final UserEntity user = userDAO.findById(userId);
			if(user != null) {
				/* compile organizations */
				
				final Set<OrganizationEntity> visitedOrganizationSet = new HashSet<OrganizationEntity>();
				final Set<AbstractMembershipXrefEntity> compiledOrganizationSet = new HashSet<AbstractMembershipXrefEntity>();
				if(CollectionUtils.isNotEmpty(user.getAffiliations())) {
					user.getAffiliations().forEach(xref -> {
						compiledOrganizationSet.add(xref);
						compiledOrganizationSet.addAll(visitOrganizations(xref.getEntity(), visitedOrganizationSet));
					});
				}
				
				/* compile roles */
				final Set<RoleEntity> visitedRoleSet = new HashSet<RoleEntity>();
				final Set<AbstractMembershipXrefEntity> compiledRoleSet = new HashSet<AbstractMembershipXrefEntity>();
				compiledOrganizationSet.forEach(xref -> {
					if(CollectionUtils.isNotEmpty(((OrganizationEntity)xref.getEntity()).getRoles())) {
						((OrganizationEntity)xref.getEntity()).getRoles().forEach(roleXref -> {
							compiledRoleSet.add(roleXref);
							compiledRoleSet.addAll(visitRoles(roleXref.getMemberEntity(), visitedRoleSet));
						});
					}
				});
				
				if(CollectionUtils.isNotEmpty(user.getRoles())) {
					user.getRoles().forEach(xref -> {
						compiledRoleSet.add(xref);
						compiledRoleSet.addAll(visitRoles(xref.getEntity(), visitedRoleSet));
					});
				}
				
				/* compile groups */
				final Set<GroupEntity> visitedGroupSet = new HashSet<GroupEntity>();
				final Set<AbstractMembershipXrefEntity> compiledGroupSet = new HashSet<AbstractMembershipXrefEntity>();
				compiledOrganizationSet.forEach(xref -> {
					if(CollectionUtils.isNotEmpty(((OrganizationEntity)xref.getEntity()).getRoles())) {
						((OrganizationEntity)xref.getEntity()).getGroups().forEach(eXref -> {
							compiledGroupSet.add(eXref);
							compiledGroupSet.addAll(visitGroups(eXref.getMemberEntity(), visitedGroupSet));
						});
					}
				});
				compiledRoleSet.forEach(xref -> {
					if(CollectionUtils.isNotEmpty(((RoleEntity)xref.getEntity()).getGroups())) {
						((RoleEntity)xref.getEntity()).getGroups().forEach(eXref -> {
							compiledGroupSet.add(eXref);
							compiledGroupSet.addAll(visitGroups(eXref.getMemberEntity(), visitedGroupSet));
						});
					}
				});
				if(CollectionUtils.isNotEmpty(user.getGroups())) {
					user.getGroups().forEach(xref -> {
						compiledGroupSet.add(xref);
						compiledGroupSet.addAll(visitGroups(xref.getEntity(), visitedGroupSet));
					});
				}
				
				/* compile resources */
				final Set<ResourceEntity> visitedResourceSet = new HashSet<ResourceEntity>();
				final Set<AbstractMembershipXrefEntity> compiledResourceSet = new HashSet<AbstractMembershipXrefEntity>();
				compiledOrganizationSet.forEach(xref -> {
					if(CollectionUtils.isNotEmpty(((OrganizationEntity)xref.getEntity()).getResources())) {
						((OrganizationEntity)xref.getEntity()).getResources().forEach(eXref -> {
							compiledResourceSet.add(eXref);
							compiledResourceSet.addAll(visitResources(eXref.getMemberEntity(), visitedResourceSet));
						});
					}
				});
				compiledRoleSet.forEach(xref -> {
					if(CollectionUtils.isNotEmpty(((RoleEntity)xref.getEntity()).getResources())) {
						((RoleEntity)xref.getEntity()).getResources().forEach(eXref -> {
							compiledResourceSet.add(eXref);
							compiledResourceSet.addAll(visitResources(eXref.getMemberEntity(), visitedResourceSet));
						});
					}
				});
				compiledGroupSet.forEach(xref -> {
					if(CollectionUtils.isNotEmpty(((GroupEntity)xref.getEntity()).getResources())) {
						((GroupEntity)xref.getEntity()).getResources().forEach(eXref -> {
							compiledResourceSet.add(eXref);
							compiledResourceSet.addAll(visitResources(eXref.getMemberEntity(), visitedResourceSet));
						});
					}
				});
				if(CollectionUtils.isNotEmpty(user.getResources())) {
					user.getResources().forEach(xref -> {
						//compiledResourceSet.add(xref);
						retVal.addDirectResource(new AuthorizationResource(xref.getEntity()));
						compiledResourceSet.addAll(visitResources(xref.getEntity(), visitedResourceSet));
					});
				}
				
				
				compiledResourceSet.forEach(xref -> {
					retVal.addIndirectResource(new AuthorizationResource((ResourceEntity)xref.getMemberEntity()));
				});
			}
		}
		return retVal;
	}

	@Override
	public ResourceEntitlementToken getNonCachedEntitlementsForGroup(final String groupId) {
		final ResourceEntitlementToken retVal = new ResourceEntitlementToken();
		if(groupId != null) {
			final GroupEntity group = groupDAO.findById(groupId);
			
			if(group != null) {
				final Set<AbstractMembershipXrefEntity> compiledGroupSet = new HashSet<AbstractMembershipXrefEntity>();
				final Set<GroupEntity> vistitedGroupSet = new HashSet<GroupEntity>();
				compiledGroupSet.addAll(visitGroups(group, vistitedGroupSet));
				
				final Set<AbstractMembershipXrefEntity> compiledResourceSet = new HashSet<AbstractMembershipXrefEntity>();
				final Set<ResourceEntity> visitedResourceSet = new HashSet<ResourceEntity>();
				compiledGroupSet.forEach(xref -> {
					((GroupEntity)xref.getEntity()).getResources().forEach(eXref -> {
						compiledResourceSet.add(eXref);
						compiledResourceSet.addAll(visitResources(eXref.getMemberEntity(), visitedResourceSet));
					});
				});
				
				if(CollectionUtils.isNotEmpty(group.getResources())) {
					group.getResources().forEach(xref -> {
						compiledResourceSet.add(xref);
						retVal.addDirectResource(new AuthorizationResource(xref.getMemberEntity()));
						compiledResourceSet.addAll(visitResources(xref.getMemberEntity(), visitedResourceSet));
					});
				}
				
				compiledResourceSet.forEach(xref -> {
					retVal.addIndirectResource(new AuthorizationResource((ResourceEntity)xref.getMemberEntity()));
				});
			}
		}
		return retVal;
	}

	@Override
	public ResourceEntitlementToken getNonCachedEntitlementsForRole(final String roleId) {
		ResourceEntitlementToken retVal = null;
		/*
		if(roleId != null) {
			final Map<String, AuthorizationRole> roleMap = getRoleMap();
			
			if(roleMap.containsKey(roleId)) {
				retVal = new ResourceEntitlementToken();
				final Map<String, AuthorizationResource> resourceMap = getResourceMap();
				final Map<String, Set<AuthorizationRole>> role2RoleMap = getRole2RoleMap(roleMap, true);
				final Map<String, Set<AuthorizationResource>> resource2ResourceMap = getResource2ResourceMap(resourceMap, true);
				final Map<String, Set<AuthorizationResource>> role2ResourceMap = getRole2ResourceMap(resourceMap);
				
				final Set<AuthorizationRole> compiledRoles = compileRoles(roleMap.get(roleId), role2RoleMap, new HashSet<AuthorizationRole>());
				
				final Set<AuthorizationResource> resourcesForCompiledRoles = getResourcesForRoles(compiledRoles, role2ResourceMap);
				
				final Set<AuthorizationResource> compiledResources = getCompiledResources(resourcesForCompiledRoles, resource2ResourceMap);
				compiledResources.addAll(resourcesForCompiledRoles);
				
				if(role2ResourceMap.containsKey(roleId)) {
					final Set<AuthorizationResource> directResources = role2ResourceMap.get(roleId);
					retVal.addDirectResources(directResources);
					final Set<AuthorizationResource> compilesResourcesFromResources = getCompiledResources(directResources, resource2ResourceMap);
					compiledResources.addAll(compilesResourcesFromResources);
				}
				
				retVal.addIndirectResource(compiledResources);
			}
		}
		*/
		return retVal;
	}
	
	@Override
	public UserEntitlementsMatrix getUserEntitlementsMatrix(final String userId) {
		
		final UserEntitlementsMatrix matrix = new UserEntitlementsMatrix();
		/*
		if(userId != null) {
			final InternalAuthroizationUser user = userDAO.getFullUser(userId);
			if(user != null) {
				matrix.setUserId(userId);
				
				final Map<String, AuthorizationResource> resourceMap = getResourceMap();
				final Map<String, AuthorizationGroup> groupMap = getGroupMap();
				final Map<String, AuthorizationRole> roleMap = getRoleMap();
				
				final Map<String, Set<AuthorizationResource>> resource2ResourceMap = getResource2ResourceMap(resourceMap, false);
                final Map<String, Set<AuthorizationResource>> childResource2ParentResourceMap = getChildResource2ParentResourceMap(resourceMap);
                final Map<String, Set<AuthorizationRole>> resource2RoleMap = getResource2RoleMap(roleMap);
                final Map<String, Set<AuthorizationGroup>> resource2GroupMap = getResource2GroupMap(groupMap);
				for(final String resourceId : resourceMap.keySet()) {
					final AuthorizationResource resource = resourceMap.get(resourceId);
					if(resource.isPublic()) {
						matrix.addPublicResource(resource);
					}
				}
				
				final Map<String, Set<AuthorizationResource>> role2ResourceMap = getRole2ResourceMap(resourceMap);
				final Map<String, Set<AuthorizationRole>> role2RoleMap = getRole2RoleMap(roleMap, false);
                final Map<String, Set<AuthorizationRole>> childRole2ParentRoleMap = getRole2RoleMap(roleMap, true);
                final Map<String, Set<AuthorizationGroup>> role2GroupMap = getRole2GroupMap(groupMap);
				
				final Map<String, Set<AuthorizationRole>> group2RoleMap = getGroup2RoleMap(roleMap);
				final Map<String, Set<AuthorizationGroup>> group2GroupMap = getGroup2GroupMap(groupMap, false);
                final Map<String, Set<AuthorizationGroup>> childGroup2ParentGroupMap = getGroup2GroupMap(groupMap, true);
				final Map<String, Set<AuthorizationResource>> group2ResourceMap = getGroup2ResourceMap(resourceMap);

				matrix.setRoleToResourceMap(role2ResourceMap);
				matrix.setRoleToRoleMap(role2RoleMap);
                matrix.setChildRoleToParentRoleMap(childRole2ParentRoleMap);
                matrix.setRoleToGroupMap(role2GroupMap);

				matrix.setGroupToGroupMap(group2GroupMap);
                matrix.setChildGroupToParentGroupMap(childGroup2ParentGroupMap);
				matrix.setGroupToResourceMap(group2ResourceMap);
				matrix.setGroupToRoleMap(group2RoleMap);

				matrix.setResourceToResourceMap(resource2ResourceMap);
                matrix.setChildResourceToParentResourceMap(childResource2ParentResourceMap);
                matrix.setResourceToGroupMap(resource2GroupMap);
                matrix.setResourceToRoleMap(resource2RoleMap);

				matrix.setRoleIds(user.getRoleIds());
				matrix.setGroupIds(user.getGroupIds());
				matrix.setResourceIds(user.getResourceIds());

                matrix.setResourceMap(resourceMap);
				matrix.setGroupMap(groupMap);
				matrix.setRoleMap(roleMap);
			}
		}
		*/
		return matrix;
	}

    public Set<String> getOwnerIdsForResource(String resourceId){
		if(StringUtils.isBlank(resourceId)){
			return Collections.EMPTY_SET;
		}
		/*
        final Map<String, AuthorizationResource> resourceMap = getResourceMap();
        final Map<String, AuthorizationGroup> groupMap = getGroupMap();
        final Map<String, AuthorizationRole> roleMap = getRoleMap();
        final Map<String, Set<AuthorizationRole>> resource2RoleMap = getResource2RoleMap(roleMap);
        final Map<String, Set<AuthorizationGroup>> resource2GroupMap = getResource2GroupMap(groupMap);
        final Map<String, Set<AuthorizationGroup>> group2GroupMap = getGroup2GroupMap(groupMap, true);
        final Map<String, Set<AuthorizationRole>> role2RoleMap = getRole2RoleMap(roleMap, true);
        final Map<String, Set<AuthorizationRole>> group2RoleMap = getGroup2RoleMap(roleMap);

		final AuthorizationResource resource = resourceMap.get(resourceId);
		final String adminResourceId = resource != null ? resource.getAdminResourceId() : null;
        return getUserIdsForResource(adminResourceId, resourceMap, resource2RoleMap, resource2GroupMap,
				group2GroupMap, role2RoleMap, group2RoleMap);
		*/
		return null;
    }

    public HashMap<String, SetStringResponse> getOwnerIdsForResourceSet(Set<String> resourceIdSet){
    	/*
		HashMap<String, SetStringResponse> ownerIdsMap = new HashMap<String, SetStringResponse>();
		if(CollectionUtils.isEmpty(resourceIdSet)){
			return ownerIdsMap;
		}

        final Map<String, AuthorizationResource> resourceMap = getResourceMap();
        final Map<String, AuthorizationGroup> groupMap = getGroupMap();
        final Map<String, AuthorizationRole> roleMap = getRoleMap();
        final Map<String, Set<AuthorizationRole>> resource2RoleMap = getResource2RoleMap(roleMap);
        final Map<String, Set<AuthorizationGroup>> resource2GroupMap = getResource2GroupMap(groupMap);
        final Map<String, Set<AuthorizationGroup>> group2GroupMap = getGroup2GroupMap(groupMap, true);
        final Map<String, Set<AuthorizationRole>> role2RoleMap = getRole2RoleMap(roleMap, true);
        final Map<String, Set<AuthorizationRole>> group2RoleMap = getGroup2RoleMap(roleMap);

        SetStringResponse setString;

        if(CollectionUtils.isNotEmpty(resourceIdSet)) for (String resourceId : resourceIdSet) {
            setString = new SetStringResponse();
			final AuthorizationResource resource = resourceMap.get(resourceId);
			final String adminResourceId = resource != null ? resource.getAdminResourceId() : null;
			setString.setSetString(getUserIdsForResource(adminResourceId, resourceMap,
					resource2RoleMap, resource2GroupMap,
					group2GroupMap, role2RoleMap, group2RoleMap));
            ownerIdsMap.put(resourceId, setString);
        }
        return ownerIdsMap;
        */
		return null;
    }

	public Set<String> getUserIdsEntitledForResource(String resourceId){
		/*
		if(StringUtils.isBlank(resourceId)){
			return Collections.EMPTY_SET;
		}

		final Map<String, AuthorizationResource> resourceMap = getResourceMap();
		final Map<String, AuthorizationGroup> groupMap = getGroupMap();
		final Map<String, AuthorizationRole> roleMap = getRoleMap();
		final Map<String, Set<AuthorizationRole>> resource2RoleMap = getResource2RoleMap(roleMap);
		final Map<String, Set<AuthorizationGroup>> resource2GroupMap = getResource2GroupMap(groupMap);
		final Map<String, Set<AuthorizationGroup>> group2GroupMap = getGroup2GroupMap(groupMap, true);
		final Map<String, Set<AuthorizationRole>> role2RoleMap = getRole2RoleMap(roleMap, true);
		final Map<String, Set<AuthorizationRole>> group2RoleMap = getGroup2RoleMap(roleMap);

		return getUserIdsForResource(resourceId, resourceMap, resource2RoleMap, resource2GroupMap,
				group2GroupMap, role2RoleMap, group2RoleMap);
		*/
		return null;
	}

	public HashMap<String, SetStringResponse> getUserIdsEntitledForResourceSet(Set<String> resourceIdSet){
		/*
		HashMap<String, SetStringResponse> userIdsMap = new HashMap<String, SetStringResponse>();
		if(CollectionUtils.isEmpty(resourceIdSet)){
			return userIdsMap;
		}

		final Map<String, AuthorizationResource> resourceMap = getResourceMap();
		final Map<String, AuthorizationGroup> groupMap = getGroupMap();
		final Map<String, AuthorizationRole> roleMap = getRoleMap();
		final Map<String, Set<AuthorizationRole>> resource2RoleMap = getResource2RoleMap(roleMap);
		final Map<String, Set<AuthorizationGroup>> resource2GroupMap = getResource2GroupMap(groupMap);
		final Map<String, Set<AuthorizationGroup>> group2GroupMap = getGroup2GroupMap(groupMap, true);
		final Map<String, Set<AuthorizationRole>> role2RoleMap = getRole2RoleMap(roleMap, true);
		final Map<String, Set<AuthorizationRole>> group2RoleMap = getGroup2RoleMap(roleMap);

		SetStringResponse setString;

		if(CollectionUtils.isNotEmpty(resourceIdSet)) for (String resourceId : resourceIdSet) {
			setString = new SetStringResponse();
			setString.setSetString(getUserIdsForResource(resourceId, resourceMap,
					resource2RoleMap, resource2GroupMap,
					group2GroupMap, role2RoleMap, group2RoleMap));
			userIdsMap.put(resourceId, setString);
		}
		return userIdsMap;
		*/
		return null;
	}

	/*
	private Set<String> getUserIdsForResource(String resourceId,
											  Map<String, AuthorizationResource> resourceMap,
											  Map<String, Set<AuthorizationRole>> resource2RoleMap,
											  Map<String, Set<AuthorizationGroup>> resource2GroupMap,
											  Map<String, Set<AuthorizationGroup>> group2GroupMap,
											  Map<String, Set<AuthorizationRole>> role2RoleMap,
											  Map<String, Set<AuthorizationRole>> group2RoleMap){
		if(StringUtils.isBlank(resourceId)){
			return Collections.EMPTY_SET;
		}

		return getEntitledUserIds(resourceId, resourceMap, resource2RoleMap, resource2GroupMap,
				group2GroupMap, role2RoleMap, group2RoleMap);
    }
	*/

	public Set<String> getOwnerIdsForGroup(String groupId){
		/*
		if(StringUtils.isBlank(groupId)){
			return Collections.EMPTY_SET;
		}
		final Map<String, AuthorizationResource> resourceMap = getResourceMap();
		final Map<String, AuthorizationGroup> groupMap = getGroupMap();
		final Map<String, AuthorizationRole> roleMap = getRoleMap();
		final Map<String, Set<AuthorizationRole>> resource2RoleMap = getResource2RoleMap(roleMap);
		final Map<String, Set<AuthorizationGroup>> resource2GroupMap = getResource2GroupMap(groupMap);
		final Map<String, Set<AuthorizationGroup>> group2GroupMap = getGroup2GroupMap(groupMap, true);
		final Map<String, Set<AuthorizationRole>> role2RoleMap = getRole2RoleMap(roleMap, true);
		final Map<String, Set<AuthorizationRole>> group2RoleMap = getGroup2RoleMap(roleMap);

		return getOwnerIdsForGroup(groupMap.get(groupId), resourceMap, resource2RoleMap, resource2GroupMap,
				group2GroupMap, role2RoleMap, group2RoleMap);
		*/
		return null;
	}

    public HashMap<String,SetStringResponse> getOwnerIdsForGroupSet(Set<String> groupIdSet){
    	/*
        HashMap<String, SetStringResponse> ownerIdsMap = new HashMap<String, SetStringResponse>();
        if(CollectionUtils.isEmpty(groupIdSet)){
            return ownerIdsMap;
        }

        final Map<String, AuthorizationResource> resourceMap = getResourceMap();
        final Map<String, AuthorizationGroup> groupMap = getGroupMap();
        final Map<String, AuthorizationRole> roleMap = getRoleMap();
        final Map<String, Set<AuthorizationRole>> resource2RoleMap = getResource2RoleMap(roleMap);
        final Map<String, Set<AuthorizationGroup>> resource2GroupMap = getResource2GroupMap(groupMap);
        final Map<String, Set<AuthorizationGroup>> group2GroupMap = getGroup2GroupMap(groupMap, true);
        final Map<String, Set<AuthorizationRole>> role2RoleMap = getRole2RoleMap(roleMap, true);
        final Map<String, Set<AuthorizationRole>> group2RoleMap = getGroup2RoleMap(roleMap);

        SetStringResponse setString;

        if(CollectionUtils.isNotEmpty(groupIdSet)) {
            for (String groupId : groupIdSet) {
                setString = new SetStringResponse();
                setString.setSetString(getOwnerIdsForGroup(groupMap.get(groupId), resourceMap,
                        resource2RoleMap, resource2GroupMap,
                        group2GroupMap, role2RoleMap, group2RoleMap));
                ownerIdsMap.put(groupId, setString);
            }
        }
        return ownerIdsMap;
        */
    	return null;
    }

    /*
	private Set<String> getOwnerIdsForGroup(AuthorizationGroup group,
											   Map<String, AuthorizationResource> resourceMap,
											   Map<String, Set<AuthorizationRole>> resource2RoleMap,
											   Map<String, Set<AuthorizationGroup>> resource2GroupMap,
											   Map<String, Set<AuthorizationGroup>> group2GroupMap,
											   Map<String, Set<AuthorizationRole>> role2RoleMap,
											   Map<String, Set<AuthorizationRole>> group2RoleMap){
		if(group==null){
			return Collections.EMPTY_SET;
		}
		return getEntitledUserIds(group.getAdminResourceId(), resourceMap, resource2RoleMap, resource2GroupMap,
				group2GroupMap, role2RoleMap, group2RoleMap);
	}

	private Set<String> getEntitledUserIds(String resourceId,
										   Map<String, AuthorizationResource> resourceMap,
										   Map<String, Set<AuthorizationRole>> resource2RoleMap,
										   Map<String, Set<AuthorizationGroup>> resource2GroupMap,
										   Map<String, Set<AuthorizationGroup>> group2GroupMap,
										   Map<String, Set<AuthorizationRole>> role2RoleMap,
										   Map<String, Set<AuthorizationRole>> group2RoleMap){
		Set<String> userIds = new HashSet<>();

		AuthorizationResource adminResource = null;
		if(StringUtils.isNotBlank(resourceId)) {
			adminResource = resourceMap.get(resourceId);
			if (adminResource != null) {

				Set<AuthorizationRole> roleSet = resource2RoleMap.get(adminResource.getId());
				Set<AuthorizationGroup> groupSet = resource2GroupMap.get(adminResource.getId());

				final Set<AuthorizationGroup> compiledGroups = new HashSet<AuthorizationGroup>();
				if (CollectionUtils.isNotEmpty(groupSet)) {
					final Set<AuthorizationGroup> visitedGroupSet = new HashSet<AuthorizationGroup>();
					for (final AuthorizationGroup grp : groupSet) {
						final Set<AuthorizationGroup> tempCompiledGroups = compileGroups(grp, group2GroupMap, visitedGroupSet);
						if (CollectionUtils.isNotEmpty(tempCompiledGroups)) {
							compiledGroups.addAll(tempCompiledGroups);
						}
					}
					compiledGroups.addAll(groupSet);
				}

				final Set<AuthorizationRole> compiledRoles = new HashSet<AuthorizationRole>();
				if (CollectionUtils.isNotEmpty(roleSet)) {
					final Set<AuthorizationRole> visitedRoleSet = new HashSet<AuthorizationRole>();
					for (final AuthorizationRole role : roleSet) {
						final Set<AuthorizationRole> tempCompiledRoles = compileRoles(role, role2RoleMap, visitedRoleSet);
						if (CollectionUtils.isNotEmpty(tempCompiledRoles)) {
							compiledRoles.addAll(tempCompiledRoles);
						}
					}
					compiledRoles.addAll(roleSet);
					final Set<AuthorizationRole> visitedSet = new HashSet<AuthorizationRole>();
					for (final AuthorizationGroup grp : compiledGroups) {
						if (group2RoleMap.containsKey(grp.getId())) {
							for (final AuthorizationRole role : group2RoleMap.get(grp.getId())) {
								compiledRoles.add(role);
								compiledRoles.addAll(compileRoles(role, role2RoleMap, visitedSet));
							}
						}
					}

					compiledRoles.addAll(getRolesForGroups(compiledGroups, group2RoleMap));
				}

				// get users for compiled groups
				if (CollectionUtils.isNotEmpty(compiledGroups)) {
					final Set<String> compiledGroupIdSet = new HashSet<String>();
					for (final AuthorizationGroup grp : compiledGroups) {
						compiledGroupIdSet.add(grp.getId());
					}

					userIds.addAll(userDAO.getUserIdsForGroups(compiledGroupIdSet));
				}

				// get users for compiled roles
				if (CollectionUtils.isNotEmpty(compiledRoles)) {
					final Set<String> compiledRoleIdSet = new HashSet<String>();
					for (final AuthorizationRole role : compiledRoles) {
						compiledRoleIdSet.add(role.getId());
					}

					userIds.addAll(userDAO.getUserIdsForRoles(compiledRoleIdSet));
				}

				// get users for admin resource
				final Set<String> resourceIdSet = new HashSet<String>();
				resourceIdSet.add(adminResource.getId());
				userIds.addAll(userDAO.getUserIdsForResources(resourceIdSet));
			}
		}
	}
	*/
}
