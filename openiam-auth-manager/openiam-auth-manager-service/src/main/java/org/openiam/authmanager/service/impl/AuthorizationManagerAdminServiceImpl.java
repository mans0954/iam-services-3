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
import org.openiam.authmanager.common.model.AuthorizationAccessRight;
import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationOrganization;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.authmanager.common.model.InternalAuthroizationUser;
import org.openiam.authmanager.dao.MembershipDAO;
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
import org.openiam.idm.srvc.membership.domain.GroupAwareMembershipXref;
import org.openiam.idm.srvc.membership.domain.OrganizationAwareMembershipXref;
import org.openiam.idm.srvc.membership.domain.ResourceAwareMembershipXref;
import org.openiam.idm.srvc.membership.domain.RoleAwareMembershipXref;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.service.OrganizationDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.membership.MembershipDTO;
import org.openiam.membership.MembershipRightDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	
	@Value("${org.openiam.ui.admin.right.id}")
	private String adminRightId;
	
	@Autowired
	private MembershipDAO membershipDAO;
	
	private Set<OrganizationAwareMembershipXref> visitOrganizations(final OrganizationEntity entity, final Set<OrganizationEntity> visitedSet) {
		final Set<OrganizationAwareMembershipXref> retVal = new HashSet<OrganizationAwareMembershipXref>();
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
	
	private Set<RoleAwareMembershipXref> visitRoles(final RoleEntity entity, final Set<RoleEntity> visitedSet) {
		final Set<RoleAwareMembershipXref> retVal = new HashSet<RoleAwareMembershipXref>();
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
	
	private Set<GroupAwareMembershipXref> visitGroups(final GroupEntity entity, final Set<GroupEntity> visitedSet) {
		final Set<GroupAwareMembershipXref> retVal = new HashSet<GroupAwareMembershipXref>();
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
	
	private Set<ResourceAwareMembershipXref> visitResources(final ResourceEntity entity, final Set<ResourceEntity> visitedSet) {
		final Set<ResourceAwareMembershipXref> retVal = new HashSet<ResourceAwareMembershipXref>();
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
				final Set<OrganizationAwareMembershipXref> compiledOrganizationSet = new HashSet<OrganizationAwareMembershipXref>();
				if(CollectionUtils.isNotEmpty(user.getAffiliations())) {
					user.getAffiliations().forEach(xref -> {
						compiledOrganizationSet.add(xref);
						compiledOrganizationSet.addAll(visitOrganizations(xref.getEntity(), visitedOrganizationSet));
					});
				}
				
				/* compile roles */
				final Set<RoleEntity> visitedRoleSet = new HashSet<RoleEntity>();
				final Set<RoleAwareMembershipXref> compiledRoleSet = new HashSet<RoleAwareMembershipXref>();
				compiledOrganizationSet.forEach(xref -> {
					if(CollectionUtils.isNotEmpty(xref.getOrganization().getRoles())) {
						xref.getOrganization().getRoles().forEach(roleXref -> {
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
				final Set<GroupAwareMembershipXref> compiledGroupSet = new HashSet<GroupAwareMembershipXref>();
				compiledOrganizationSet.forEach(xref -> {
					if(CollectionUtils.isNotEmpty(xref.getOrganization().getRoles())) {
						xref.getOrganization().getGroups().forEach(eXref -> {
							compiledGroupSet.add(eXref);
							compiledGroupSet.addAll(visitGroups(eXref.getMemberEntity(), visitedGroupSet));
						});
					}
				});
				compiledRoleSet.forEach(xref -> {
					if(CollectionUtils.isNotEmpty(xref.getRole().getGroups())) {
						xref.getRole().getGroups().forEach(eXref -> {
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
				final Set<ResourceAwareMembershipXref> compiledResourceSet = new HashSet<ResourceAwareMembershipXref>();
				compiledOrganizationSet.forEach(xref -> {
					if(CollectionUtils.isNotEmpty(xref.getOrganization().getResources())) {
						xref.getOrganization().getResources().forEach(eXref -> {
							compiledResourceSet.add(eXref);
							compiledResourceSet.addAll(visitResources(eXref.getMemberEntity(), visitedResourceSet));
						});
					}
				});
				compiledRoleSet.forEach(xref -> {
					if(CollectionUtils.isNotEmpty(xref.getRole().getResources())) {
						xref.getRole().getResources().forEach(eXref -> {
							compiledResourceSet.add(eXref);
							compiledResourceSet.addAll(visitResources(eXref.getMemberEntity(), visitedResourceSet));
						});
					}
				});
				compiledGroupSet.forEach(xref -> {
					if(CollectionUtils.isNotEmpty(xref.getGroup().getResources())) {
						xref.getGroup().getResources().forEach(eXref -> {
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
					retVal.addIndirectResource(new AuthorizationResource(xref.getResource()));
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
				final Set<GroupAwareMembershipXref> compiledGroupSet = new HashSet<GroupAwareMembershipXref>();
				final Set<GroupEntity> vistitedGroupSet = new HashSet<GroupEntity>();
				compiledGroupSet.addAll(visitGroups(group, vistitedGroupSet));
				
				final Set<ResourceAwareMembershipXref> compiledResourceSet = new HashSet<ResourceAwareMembershipXref>();
				final Set<ResourceEntity> visitedResourceSet = new HashSet<ResourceEntity>();
				compiledGroupSet.forEach(xref -> {
					xref.getGroup().getResources().forEach(eXref -> {
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
					retVal.addIndirectResource(new AuthorizationResource(xref.getResource()));
				});
			}
		}
		return retVal;
	}

	@Override
	public ResourceEntitlementToken getNonCachedEntitlementsForRole(final String roleId) {
		final ResourceEntitlementToken retVal = new ResourceEntitlementToken();
		if(roleId != null) {
			final RoleEntity role = roleDAO.findById(roleId);
			if(role != null) {
				final Set<RoleAwareMembershipXref> compiledRoleSet = new HashSet<RoleAwareMembershipXref>();
				final Set<RoleEntity> visitedRoleSet = new HashSet<RoleEntity>();
				compiledRoleSet.addAll(visitRoles(role, visitedRoleSet));
				
				final Set<GroupEntity> visitedGroupSet = new HashSet<GroupEntity>();
				final Set<GroupAwareMembershipXref> compiledGroupSet = new HashSet<GroupAwareMembershipXref>();
				if(CollectionUtils.isNotEmpty(role.getGroups())) {
					role.getGroups().forEach(xref -> {
						compiledGroupSet.add(xref);
						compiledGroupSet.addAll(visitGroups(xref.getGroup(), visitedGroupSet));
					});
				}
				
				compiledRoleSet.forEach(xref -> {
					if(CollectionUtils.isNotEmpty(xref.getRole().getGroups())) {
						xref.getRole().getGroups().forEach(eXref -> {
							compiledGroupSet.add(eXref);
							compiledGroupSet.addAll(visitGroups(eXref.getGroup(), visitedGroupSet));
						});
					}
				});
				
				final Set<ResourceEntity> visitedResourceSet = new HashSet<ResourceEntity>();
				final Set<ResourceAwareMembershipXref> compiledResourceSet = new HashSet<ResourceAwareMembershipXref>();
				compiledRoleSet.forEach(xref -> {
					if(CollectionUtils.isNotEmpty(xref.getRole().getResources())) {
						xref.getRole().getResources().forEach(eXref -> {
							compiledResourceSet.add(eXref);
							compiledResourceSet.addAll(visitResources(eXref.getResource(), visitedResourceSet));
						});
					}
				});
				
				compiledGroupSet.forEach(xref -> {
					if(CollectionUtils.isNotEmpty(xref.getGroup().getResources())) {
						xref.getGroup().getResources().forEach(eXref -> {
							compiledResourceSet.add(eXref);
							compiledResourceSet.addAll(visitResources(eXref.getResource(), visitedResourceSet));
						});
					}
				});
				
				if(CollectionUtils.isNotEmpty(role.getResources())) {
					role.getResources().forEach(xref -> {
						retVal.addDirectResource(new AuthorizationResource(xref.getResource()));
						compiledResourceSet.addAll(visitResources(xref.getResource(), visitedResourceSet));
					});
				}
				
				compiledResourceSet.forEach(xref -> {
					retVal.addIndirectResource(new AuthorizationResource(xref.getResource()));
				});
			}
		}
		return retVal;
	}
	
	@Override
	public ResourceEntitlementToken getNonCachedEntitlementsForOrganization(final String organizationId) {
		final ResourceEntitlementToken retVal = new ResourceEntitlementToken();
		if(organizationId != null) {
			final OrganizationEntity organization = organizationDAO.findById(organizationId);
			
			final Set<OrganizationAwareMembershipXref> compiledOrganizationSet = new HashSet<OrganizationAwareMembershipXref>();
			final Set<OrganizationEntity> visitedOrganizationSet = new HashSet<OrganizationEntity>();
			compiledOrganizationSet.addAll(visitOrganizations(organization, visitedOrganizationSet));
			
			
			final Set<RoleAwareMembershipXref> compiledRoleSet = new HashSet<RoleAwareMembershipXref>();
			final Set<RoleEntity> visitedRoleSet = new HashSet<RoleEntity>();
			if(CollectionUtils.isNotEmpty(organization.getRoles())) {
				organization.getRoles().forEach(xref -> {
					compiledRoleSet.add(xref);
					compiledRoleSet.addAll(visitRoles(xref.getRole(), visitedRoleSet));
				});
			}
			
			compiledOrganizationSet.forEach(xref -> {
				if(CollectionUtils.isNotEmpty(xref.getOrganization().getRoles())) {
					xref.getOrganization().getRoles().forEach(eXref -> {
						compiledRoleSet.add(eXref);
						compiledRoleSet.addAll(visitRoles(eXref.getRole(), visitedRoleSet));
					});
				}
			});
			
			final Set<GroupAwareMembershipXref> compiledGroupSet = new HashSet<GroupAwareMembershipXref>();
			final Set<GroupEntity> visitedGroupSet = new HashSet<GroupEntity>();
			if(CollectionUtils.isNotEmpty(organization.getGroups())) {
				organization.getGroups().forEach(xref -> {
					compiledGroupSet.add(xref);
					compiledGroupSet.addAll(visitGroups(xref.getGroup(), visitedGroupSet));
				});
			}
			
			compiledOrganizationSet.forEach(xref -> {
				if(CollectionUtils.isNotEmpty(xref.getOrganization().getGroups())) {
					xref.getOrganization().getGroups().forEach(eXref -> {
						compiledGroupSet.add(eXref);
						compiledGroupSet.addAll(visitGroups(eXref.getGroup(), visitedGroupSet));
					});
				}
			});
			
			compiledRoleSet.forEach(xref -> {
				if(CollectionUtils.isNotEmpty(xref.getRole().getGroups())) {
					xref.getRole().getGroups().forEach(eXref -> {
						compiledGroupSet.add(eXref);
						compiledGroupSet.addAll(visitGroups(eXref.getGroup(), visitedGroupSet));
					});
				}
			});
			
			final Set<ResourceAwareMembershipXref> compiledResourceSet = new HashSet<ResourceAwareMembershipXref>();
			final Set<ResourceEntity> visitedResourceSet = new HashSet<ResourceEntity>();
			compiledOrganizationSet.forEach(xref -> {
				if(CollectionUtils.isNotEmpty(xref.getOrganization().getResources())) {
					xref.getOrganization().getResources().forEach(eXref -> {
						compiledResourceSet.add(eXref);
						compiledResourceSet.addAll(visitResources(eXref.getResource(), visitedResourceSet));
					});
				}
			});
			
			compiledRoleSet.forEach(xref -> {
				if(CollectionUtils.isNotEmpty(xref.getRole().getResources())) {
					xref.getRole().getResources().forEach(eXref -> {
						compiledResourceSet.add(eXref);
						compiledResourceSet.addAll(visitResources(eXref.getResource(), visitedResourceSet));
					});
				}
			});
			
			compiledGroupSet.forEach(xref -> {
				if(CollectionUtils.isNotEmpty(xref.getGroup().getResources())) {
					xref.getGroup().getResources().forEach(eXref -> {
						compiledResourceSet.add(eXref);
						compiledResourceSet.addAll(visitResources(eXref.getResource(), visitedResourceSet));
					});
				}
			});
			
			if(CollectionUtils.isNotEmpty(organization.getResources())) {
				organization.getResources().forEach(xref -> {
					retVal.addDirectResource(new AuthorizationResource(xref.getResource()));
					compiledResourceSet.addAll(visitResources(xref.getResource(), visitedResourceSet));
				});
			}
			
			compiledResourceSet.forEach(xref -> {
				retVal.addIndirectResource(new AuthorizationResource(xref.getResource()));
			});
		}
		return retVal;
	}
	
	@Override
	public UserEntitlementsMatrix getUserEntitlementsMatrix(final String userId) {
		
		final UserEntitlementsMatrix matrix = new UserEntitlementsMatrix();
		if(userId != null) {
			final InternalAuthroizationUser user = membershipDAO.getUser(userId);
			if(user != null) {
				final Map<String, AuthorizationOrganization> orgMap = membershipDAO.getOrganizations()
						.stream()
						.collect(Collectors.toMap(AuthorizationOrganization::getId, Function.identity()));
				
				final Map<String, AuthorizationRole> roleMap = membershipDAO.getRoles()
						.stream()
						.collect(Collectors.toMap(AuthorizationRole::getId, Function.identity()));
				
				final Map<String, AuthorizationResource> resourceMap = membershipDAO.getResources()
						.stream()
						.collect(Collectors.toMap(AuthorizationResource::getId, Function.identity()));
				
				final Map<String, AuthorizationGroup> groupMap = membershipDAO.getGroups()
						.stream()
						.collect(Collectors.toMap(AuthorizationGroup::getId, Function.identity()));

				
				matrix.populate(userId, user.getOrganizations(), user.getRoles(), user.getGroups(), user.getResources(), orgMap, resourceMap, groupMap, roleMap);
				
				getOrg2OrgMap().forEach((childId, membership) -> {
					membership.forEach((parentId, rights) -> {
						if(CollectionUtils.isNotEmpty(rights)) {
							rights.forEach(rightId -> {
								matrix.addOrg2OrgRelationship(parentId, childId, rightId);
							});
						} else {
							matrix.addOrg2OrgRelationship(parentId, childId, null);
						}
					});
				});
				
				getRole2OrgMap().forEach((childId, membership) -> {
					membership.forEach((parentId, rights) -> {
						if(CollectionUtils.isNotEmpty(rights)) {
							rights.forEach(rightId -> {
								matrix.addOrg2RoleRelationship(parentId, childId, rightId);
							});
						} else {
							matrix.addOrg2RoleRelationship(parentId, childId, null);
						}
					});
				});
				
				getGroup2OrgMap().forEach((childId, membership) -> {
					membership.forEach((parentId, rights) -> {
						if(CollectionUtils.isNotEmpty(rights)) {
							rights.forEach(rightId -> {
								matrix.addOrg2GroupRelationship(parentId, childId, rightId);
							});
						} else {
							matrix.addOrg2GroupRelationship(parentId, childId, null);
						}
					});
				});
				
				getResource2OrgMap().forEach((childId, membership) -> {
					membership.forEach((parentId, rights) -> {
						if(CollectionUtils.isNotEmpty(rights)) {
							rights.forEach(rightId -> {
								matrix.addOrg2ResourceRelationship(parentId, childId, rightId);
							});
						} else {
							matrix.addOrg2ResourceRelationship(parentId, childId, null);
						}
					});
				});
				
				getRole2RoleMap().forEach((childId, membership) -> {
					membership.forEach((parentId, rights) -> {
						if(CollectionUtils.isNotEmpty(rights)) {
							rights.forEach(rightId -> {
								matrix.addRole2RoleRelationship(parentId, childId, rightId);
							});
						} else {
							matrix.addRole2RoleRelationship(parentId, childId, null);
						}
					});
				});
				
				getGroup2RoleMap().forEach((childId, membership) -> {
					membership.forEach((parentId, rights) -> {
						if(CollectionUtils.isNotEmpty(rights)) {
							rights.forEach(rightId -> {
								matrix.addRole2GroupRelationship(parentId, childId, rightId);
							});
						} else {
							matrix.addRole2GroupRelationship(parentId, childId, null);
						}
					});
				});
				
				getResource2RoleMap().forEach((childId, membership) -> {
					membership.forEach((parentId, rights) -> {
						if(CollectionUtils.isNotEmpty(rights)) {
							rights.forEach(rightId -> {
								matrix.addRole2ResourceRelationship(parentId, childId, rightId);
							});
						} else {
							matrix.addRole2ResourceRelationship(parentId, childId, null);
						}
					});
				});
				
				getGroup2GroupMap().forEach((childId, membership) -> {
					membership.forEach((parentId, rights) -> {
						if(CollectionUtils.isNotEmpty(rights)) {
							rights.forEach(rightId -> {
								matrix.addGroup2GroupRelationship(parentId, childId, rightId);
							});
						} else {
							matrix.addGroup2GroupRelationship(parentId, childId, null);
						}
					});
				});
				
				getResource2GroupMap().forEach((childId, membership) -> {
					membership.forEach((parentId, rights) -> {
						if(CollectionUtils.isNotEmpty(rights)) {
							rights.forEach(rightId -> {
								matrix.addGroup2ResourceRelationship(parentId, childId, rightId);
							});
						} else {
							matrix.addGroup2ResourceRelationship(parentId, childId, null);
						}
					});
				});
				
				getResource2ResourceMap().forEach((childId, membership) -> {
					membership.forEach((parentId, rights) -> {
						if(CollectionUtils.isNotEmpty(rights)) {
							rights.forEach(rightId -> {
								matrix.addResource2ResourceRelationship(parentId, childId, rightId);
							});
						} else {
							matrix.addResource2ResourceRelationship(parentId, childId, null);
						}
					});
				});
			}
		}
		return matrix;
	}

	@Override
    public Set<String> getOwnerIdsForResource(String resourceId){
        return getUserIdsForResource(resourceId, adminRightId);
    }
	
	private Map<String, Set<MembershipDTO>> getMembershipMapByEntityId(final List<MembershipDTO> list) {
		return list.stream().collect(Collectors.groupingBy(MembershipDTO::getEntityId,
				Collectors.mapping(Function.identity(), Collectors.toSet())));
	}
	
	private Map<String, Set<MembershipDTO>> getMembershipMapByMemberEntityId(final List<MembershipDTO> list) {
		return list.stream().collect(Collectors.groupingBy(MembershipDTO::getMemberEntityId,
				Collectors.mapping(Function.identity(), Collectors.toSet())));
	}
	
	private Map<String, Set<String>> getRightMap(final List<MembershipRightDTO> list) {
		return list.stream().collect(Collectors.groupingBy(MembershipRightDTO::getId,
				Collectors.mapping(MembershipRightDTO::getRightId, Collectors.toSet())));
	}
	
	private Map<String, Map<String, Set<String>>> populateWithEntityIdAsValue(final Map<String, Set<MembershipDTO>> entityMap, 
											 								  final Map<String, Set<String>> rightMap) {
		final Map<String, Map<String, Set<String>>> retval = new HashMap<String, Map<String,Set<String>>>();
		entityMap.forEach((entityId, memberships) -> {
			if(!retval.containsKey(entityId)) {
				retval.put(entityId, new HashMap<String, Set<String>>());
			}
			if(CollectionUtils.isNotEmpty(memberships)) {
				memberships.forEach(membership -> {
					if(!retval.get(entityId).containsKey(membership.getEntityId())) {
						retval.get(entityId).put(membership.getEntityId(), new HashSet<String>());
					}
					if(rightMap.containsKey(membership.getId())) {
						retval.get(entityId).get(membership.getEntityId()).addAll(rightMap.get(membership.getId()));
					}
				});
			}
		});
		return retval;
	}
    
    private Map<String, Map<String, Set<String>>> getResource2ResourceMap() {
		final Map<String, Set<MembershipDTO>> entityMap = getMembershipMapByMemberEntityId(membershipDAO.getResource2ResourceMembership());
		final Map<String, Set<String>> rightMap = getRightMap(membershipDAO.getResource2ResourceRights());
		return populateWithEntityIdAsValue(entityMap, rightMap);
    }
    
    private Map<String, Map<String, Set<String>>> getResource2GroupMap() {
    	final Map<String, Set<MembershipDTO>> entityMap = getMembershipMapByMemberEntityId(membershipDAO.getGroup2ResourceMembership());
    	final Map<String, Set<String>> rightMap = getRightMap(membershipDAO.getGroup2ResourceRights());
    	return populateWithEntityIdAsValue(entityMap, rightMap);
    }
    
    private Map<String, Map<String, Set<String>>> getResource2RoleMap() {
    	final Map<String, Set<MembershipDTO>> entityMap = getMembershipMapByMemberEntityId(membershipDAO.getRole2ResourceMembership());
    	final Map<String, Set<String>> rightMap = getRightMap(membershipDAO.getRole2ResourceRights());
    	return populateWithEntityIdAsValue(entityMap, rightMap);
    }
    
    private Map<String, Map<String, Set<String>>> getResource2OrgMap() {
    	final Map<String, Set<MembershipDTO>> entityMap = getMembershipMapByMemberEntityId(membershipDAO.getOrg2ResourceMembership());
    	final Map<String, Set<String>> rightMap = getRightMap(membershipDAO.getOrg2ResourceRights());
    	return populateWithEntityIdAsValue(entityMap, rightMap);
    }
    
    private Map<String, Map<String, Set<String>>> getGroup2GroupMap() {
    	final Map<String, Set<MembershipDTO>> entityMap = getMembershipMapByMemberEntityId(membershipDAO.getGroup2GroupMembership());
    	final Map<String, Set<String>> rightMap = getRightMap(membershipDAO.getGroup2GroupRights());
    	return populateWithEntityIdAsValue(entityMap, rightMap);
    }
    
    private Map<String, Map<String, Set<String>>> getGroup2RoleMap() {
    	final Map<String, Set<MembershipDTO>> entityMap = getMembershipMapByMemberEntityId(membershipDAO.getRole2GroupMembership());
    	final Map<String, Set<String>> rightMap = getRightMap(membershipDAO.getRole2GroupRights());
    	return populateWithEntityIdAsValue(entityMap, rightMap);
    }
    
    private Map<String, Map<String, Set<String>>> getGroup2OrgMap() {
    	final Map<String, Set<MembershipDTO>> entityMap = getMembershipMapByMemberEntityId(membershipDAO.getOrg2GroupMembership());
    	final Map<String, Set<String>> rightMap = getRightMap(membershipDAO.getOrg2GroupRights());
    	return populateWithEntityIdAsValue(entityMap, rightMap);
    }
    
    private Map<String, Map<String, Set<String>>> getRole2RoleMap() {
    	final Map<String, Set<MembershipDTO>> entityMap = getMembershipMapByMemberEntityId(membershipDAO.getRole2RoleMembership());
    	final Map<String, Set<String>> rightMap = getRightMap(membershipDAO.getRole2RoleRights());
    	return populateWithEntityIdAsValue(entityMap, rightMap);
    }
    
    private Map<String, Map<String, Set<String>>> getRole2OrgMap() {
    	final Map<String, Set<MembershipDTO>> entityMap = getMembershipMapByMemberEntityId(membershipDAO.getOrg2RoleMembership());
    	final Map<String, Set<String>> rightMap = getRightMap(membershipDAO.getOrg2RoleRights());
    	return populateWithEntityIdAsValue(entityMap, rightMap);
    }
    
    private Map<String, Map<String, Set<String>>> getOrg2OrgMap() {
    	final Map<String, Set<MembershipDTO>> entityMap = getMembershipMapByMemberEntityId(membershipDAO.getOrg2OrgMembership());
    	final Map<String, Set<String>> rightMap = getRightMap(membershipDAO.getOrg2OrgRights());
    	return populateWithEntityIdAsValue(entityMap, rightMap);
    }

    @Override
    public HashMap<String, SetStringResponse> getOwnerIdsForResourceSet(Set<String> resourceIdSet){
    	final HashMap<String, SetStringResponse> retVal = new HashMap<String, SetStringResponse>();
    	if(CollectionUtils.isNotEmpty(resourceIdSet)) {
    		resourceIdSet.forEach(resourceId -> {
    			final Set<String> userIds = getUserIdsForResource(resourceId, adminRightId);
    			retVal.put(resourceId, new SetStringResponse(userIds));
    		});
    	}
    	return retVal;
    }

    @Override
	public Set<String> getUserIdsEntitledForResource(String resourceId){
		return getUserIdsForResource(resourceId, null);
	}

	@Override
	public HashMap<String, SetStringResponse> getUserIdsEntitledForResourceSet(Set<String> resourceIdSet){
		final HashMap<String, SetStringResponse> retVal = new HashMap<String, SetStringResponse>();
    	if(CollectionUtils.isNotEmpty(resourceIdSet)) {
    		resourceIdSet.forEach(resourceId -> {
    			final Set<String> userIds = getUserIdsForResource(resourceId, null);
    			retVal.put(resourceId, new SetStringResponse(userIds));
    		});
    	}
    	return retVal;
	}

	@Override
	public Set<String> getOwnerIdsForGroup(String groupId){
		return getUserIdsForGroup(groupId, adminRightId);
	}

	@Override
    public HashMap<String,SetStringResponse> getOwnerIdsForGroupSet(Set<String> groupIdSet){
		final HashMap<String, SetStringResponse> retVal = new HashMap<String, SetStringResponse>();
    	if(CollectionUtils.isNotEmpty(groupIdSet)) {
    		groupIdSet.forEach(groupId -> {
    			final Set<String> userIds = getOwnerIdsForGroup(groupId);
    			retVal.put(groupId, new SetStringResponse(userIds));
    		});
    	}
    	return retVal;
    }

    private Set<String> visitParents(final String entityId,
    								 final String rightId,
    								 final Map<String, Map<String, Set<String>>> entityToParentMap,
    								 final Set<String> visitedSet) {
    	final Set<String> entityIds = new HashSet<String>();
    	if(!visitedSet.contains(entityId)) {
    		visitedSet.add(entityId);
    		entityIds.add(entityId);
    		entityToParentMap.forEach((childId, parentTuple) -> {
				if(parentTuple != null && parentTuple.containsKey(entityId)) {
					if(rightId != null) {
						if(parentTuple.get(entityId).contains(rightId)) {
							entityIds.addAll(visitParents(childId, rightId, entityToParentMap, visitedSet));
						}
					} else {
						entityIds.addAll(visitParents(childId, rightId, entityToParentMap, visitedSet));
					}
				}
			});
    	}
    	return entityIds;
    }
    
    private Set<String> getUserIdsForGroup(final String groupId, final String rightId){
		final Set<String> userIds = new HashSet<String>();
    	
    	final Map<String, Map<String, Set<String>>> group2GroupMap = getGroup2GroupMap();
    	final Map<String, Map<String, Set<String>>> group2RoleMap = getGroup2RoleMap();
    	final Map<String, Map<String, Set<String>>> group2OrgMap = getGroup2OrgMap();
	   
    	final Map<String, Map<String, Set<String>>> role2RoleMap = getRole2RoleMap();
    	final Map<String, Map<String, Set<String>>> role2OrgMap = getRole2OrgMap();
	   
    	final Map<String, Map<String, Set<String>>> org2OrgMap = getOrg2OrgMap();
    	
		if(rightId != null) {
			userIds.addAll(membershipDAO.getUsersForGroup(groupId, rightId));
		} else {
			userIds.addAll(membershipDAO.getUsersForGroup(groupId));
		}
    	
    	if(StringUtils.isNotBlank(groupId)) {
    		final Set<String> groupVisitedSet = new HashSet<String>();
    		final Set<String> groupIds = new HashSet<String>();
    		groupIds.add(groupId);
    		
    		group2GroupMap.forEach((childId, parentTuple) -> {
    			if(parentTuple != null && parentTuple.containsKey(groupId)) {
    				if(rightId != null) {
    					if(parentTuple.get(groupId).contains(rightId)) {
    						groupIds.add(childId);
    						groupIds.addAll(visitParents(childId, null, group2GroupMap, groupVisitedSet));
    					}
    				} else {
    					groupIds.add(childId);
    					groupIds.addAll(visitParents(childId, null, group2GroupMap, groupVisitedSet));
    				}
    			}
    		});
    		
    		final Set<String> visitedRoleSet = new HashSet<String>();
    		final Set<String> roleIds = new HashSet<String>();
			
    		groupIds.forEach(childId -> {
    			if(group2RoleMap.containsKey(childId)) {
    				group2RoleMap.get(childId).forEach((parentId, rights) -> {
    					if(rightId != null) {
    						if(rights.contains(rightId)) {
    							roleIds.add(parentId);
    							roleIds.addAll(visitParents(parentId, null, role2RoleMap, visitedRoleSet));
    						}
    					} else {
	   						  roleIds.add(parentId);
	   						  roleIds.addAll(visitParents(parentId, null, role2RoleMap, visitedRoleSet));
    					}
    				});
    			}
    		});
			
    		final Set<String> visitedOrgSet = new HashSet<String>();
    		final Set<String> orgIds = new HashSet<String>();
			
    		groupIds.forEach(childId -> {
    			if(group2OrgMap.containsKey(childId)) {
    				group2OrgMap.get(childId).forEach((parentId, rights) -> {
    					if(rightId != null) {
    						if(rights.contains(rightId)) {
    							orgIds.add(parentId);
	   							orgIds.addAll(visitParents(parentId, null, org2OrgMap, visitedOrgSet));	
	   						}
    					} else {
	   						orgIds.add(parentId);
	   						orgIds.addAll(visitParents(parentId, null, org2OrgMap, visitedOrgSet));
    					}
    				});
    			}
	   		});
			
   		  	roleIds.forEach(r -> {
   		  		if(role2OrgMap.containsKey(r)) {
   		  			orgIds.addAll(role2OrgMap.get(r).keySet());
   		  			role2OrgMap.get(r).keySet().forEach(entityId -> {
   		  				orgIds.addAll(visitParents(entityId, rightId, org2OrgMap, visitedOrgSet));
   		  			});
   		  		}
   		  	});
		
   		  	groupIds.remove(groupId); /* already did the check for direct entitlements */
   		  	userIds.addAll(userDAO.getUserIdsForGroups(groupIds, 0, Integer.MAX_VALUE));
   		  	userIds.addAll(userDAO.getUserIdsForRoles(roleIds, 0, Integer.MAX_VALUE));
   		  	userIds.addAll(userDAO.getUserIdsForOrganizations(orgIds, 0, Integer.MAX_VALUE));
    	}
    	return userIds;
	}
    
	private Set<String> getUserIdsForResource(final String resourceId, final String rightId){
	   	  final Map<String, Map<String, Set<String>>> resource2ResourceMap = getResource2ResourceMap();
	   	  final Map<String, Map<String, Set<String>>> resource2GroupMap = getResource2GroupMap();
	   	  final Map<String, Map<String, Set<String>>> resource2RoleMap = getResource2RoleMap();
	   	  final Map<String, Map<String, Set<String>>> resource2OrgMap = getResource2OrgMap();
	   
	   	  final Map<String, Map<String, Set<String>>> group2GroupMap = getGroup2GroupMap();
	   	  final Map<String, Map<String, Set<String>>> group2RoleMap = getGroup2RoleMap();
	   	  final Map<String, Map<String, Set<String>>> group2OrgMap = getGroup2OrgMap();
	   
	   	  final Map<String, Map<String, Set<String>>> role2RoleMap = getRole2RoleMap();
	   	  final Map<String, Map<String, Set<String>>> role2OrgMap = getRole2OrgMap();
	   
	   	  final Map<String, Map<String, Set<String>>> org2OrgMap = getOrg2OrgMap();
	   	
	   	  final Set<String> userIds = new HashSet<>();
	   	  if(rightId != null) {
	   		  userIds.addAll(membershipDAO.getUsersForResource(resourceId, rightId));
	   	  } else {
	   		  userIds.addAll(membershipDAO.getUsersForResource(resourceId));
	   	  }

	   	  if(StringUtils.isNotBlank(resourceId)) {
	   		  final Set<String> resourceVisitedSet = new HashSet<String>();
	   		  final Set<String> resourceIds = new HashSet<String>();
	   		  resourceIds.add(resourceId);
	   		  resource2ResourceMap.forEach((childId, parentTuple) -> {
	   			  if(parentTuple != null && parentTuple.containsKey(resourceId)) {
	   				  if(rightId != null) {
	   					  if(parentTuple.get(resourceId).contains(rightId)) {
	   						  resourceIds.add(childId);
	   						  resourceIds.addAll(visitParents(childId, null, resource2ResourceMap, resourceVisitedSet));
	   					  }
	   				  } else {
	   					  resourceIds.add(childId);
	   					  resourceIds.addAll(visitParents(childId, null, resource2ResourceMap, resourceVisitedSet));
	   				  }
	   			  }
	   		  });

	   		  final Set<String> visitedGroupSet = new HashSet<String>();
	   		  final Set<String> groupIds = new HashSet<String>();
	   		  resourceIds.forEach(childId -> {
	   			  if(resource2GroupMap.containsKey(childId)) {
	   				  resource2GroupMap.get(childId).forEach((parentId, rights) -> {
	   					  if(rightId != null) {
	   						  if(rights.contains(rightId)) {
	   							  groupIds.add(parentId);
	   							  groupIds.addAll(visitParents(parentId, null, group2GroupMap, visitedGroupSet));
	   						  }
	   					  } else {
	   						  groupIds.add(parentId);
	   						  groupIds.addAll(visitParents(parentId, null, group2GroupMap, visitedGroupSet));
	   					  }
	   				  });
	   			  }
	   		  });
			
	   		  final Set<String> visitedRoleSet = new HashSet<String>();
	   		  final Set<String> roleIds = new HashSet<String>();
	   		  resourceIds.forEach(childId -> {
	   			if(resource2RoleMap.containsKey(childId)) {
	   				resource2RoleMap.get(childId).forEach((parentId, rights) -> {
	   					if(rightId != null) {
	   						if(rights.contains(rightId)) {
	   							roleIds.add(parentId);
	   							roleIds.addAll(visitParents(parentId, null, role2RoleMap, visitedRoleSet));
	   						}
	   					} else {
	   						roleIds.add(parentId);
	   						roleIds.addAll(visitParents(parentId, null, role2RoleMap, visitedRoleSet));
	   					  }
	   				  });
	   			  }
	   		  });
			
	   		  groupIds.forEach(g -> {
	   			  if(group2RoleMap.containsKey(g)) {
	   				  roleIds.addAll(group2RoleMap.get(g).keySet());
	   				  group2RoleMap.get(g).keySet().forEach(entityId -> {
	   					  roleIds.addAll(visitParents(entityId, rightId, role2RoleMap, visitedRoleSet));
	   				  });
	   			  }
	   		  });
			
	   		  final Set<String> visitedOrgSet = new HashSet<String>();
	   		  final Set<String> orgIds = new HashSet<String>();
	   		  resourceIds.forEach(childId -> {
	   			if(resource2OrgMap.containsKey(childId)) {
	   				resource2OrgMap.get(childId).forEach((parentId, rights) -> {
	   					if(rightId != null) {
	   						if(rights.contains(rightId)) {
	   							orgIds.add(parentId);
	   							orgIds.addAll(visitParents(parentId, null, org2OrgMap, visitedOrgSet));
	   						}
	   					} else {
	   						orgIds.add(parentId);
	   						orgIds.addAll(visitParents(parentId, null, org2OrgMap, visitedOrgSet));
	   					  }
	   				  });
	   			  }
	   		  });
			
	   		  groupIds.forEach(r -> {
	   			  if(group2OrgMap.containsKey(r)) {
	   				  orgIds.addAll(group2OrgMap.get(r).keySet());
	   				  group2OrgMap.get(r).keySet().forEach(entityId -> {
	   					  orgIds.addAll(visitParents(entityId, rightId, org2OrgMap, visitedOrgSet));
	   				  });
	   			  }
	   		  });
			
	   		  roleIds.forEach(r -> {
	   			  if(role2OrgMap.containsKey(r)) {
	   				  orgIds.addAll(role2OrgMap.get(r).keySet());
	   				  role2OrgMap.get(r).keySet().forEach(entityId -> {
	   					  orgIds.addAll(visitParents(entityId, rightId, org2OrgMap, visitedOrgSet));
	   				  });
	   			  }
	   		  });
			
	   		  resourceIds.remove(resourceId); /* already did the check for direct entitlements */
	   		  userIds.addAll(userDAO.getUserIdsForGroups(groupIds, 0, Integer.MAX_VALUE));
	   		  userIds.addAll(userDAO.getUserIdsForRoles(roleIds, 0, Integer.MAX_VALUE));
	   		  userIds.addAll(userDAO.getUserIdsForResources(resourceIds, 0, Integer.MAX_VALUE));
	   		  userIds.addAll(userDAO.getUserIdsForOrganizations(orgIds, 0, Integer.MAX_VALUE));
		}
		return userIds;
	}
}
