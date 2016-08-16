package org.openiam.authmanager.service.impl;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.am.srvc.dto.jdbc.*;
import org.openiam.base.response.SetStringResponse;
import org.openiam.authmanager.dao.MembershipDAO;
import org.openiam.authmanager.model.*;
import org.openiam.authmanager.provider.AuthorizationManagerDataProvider;
import org.openiam.authmanager.service.AuthorizationManagerAdminService;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.membership.MembershipDTO;
import org.openiam.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("authorizationManagerAdminService")
public class AuthorizationManagerAdminServiceImpl extends AbstractAuthorizationManagerService implements AuthorizationManagerAdminService {

	@Autowired
	private UserDAO userDAO;
	
	@Value("${org.openiam.ui.admin.right.id}")
	private String adminRightId;
	
	@Autowired
	private MembershipDAO membershipDAO;
	
	@Autowired
	private AuthorizationManagerDataProvider dataProvider;
	
	@Override
	public ResourceEntitlementToken getNonCachedEntitlementsForUser(final String userId, final Date date) {
		final ResourceEntitlementToken retVal = new ResourceEntitlementToken();
		if(userId != null) {
			final AuthorizationManagerDataModel model = dataProvider.getModel(date);
			final InternalAuthroizationUser user = membershipDAO.getUser(userId, date);
			if(user != null) {
				final AuthorizationUser entity = super.process(user, model.getTempGroupIdMap(), model.getTempRoleIdMap(), model.getTempResourceIdMap(), model.getTempOrganizationIdMap(), model.getTempAccessRightMap(), new AtomicInteger(0));

				Set<ResourceAuthorizationRight> tempDataSet =  getEntitlementData(entity.getLinearResources(), model.getResourceBitSetMap(), model.getTempAccessRightBitMap(), model.getNumOfRights(), ResourceAuthorizationRight.class);
				fillEntitlementToken(retVal, tempDataSet, user.getResources());
			}
		}
		return retVal;
	}

	@Override
	public ResourceEntitlementToken getNonCachedEntitlementsForGroup(final String groupId, final Date date) {
		final ResourceEntitlementToken retVal = new ResourceEntitlementToken();
		if(groupId != null) {
			final AuthorizationManagerDataModel model = dataProvider.getModel(date);
			if(model.getTempGroupIdMap().containsKey(groupId)) {
				final AuthorizationGroup entity = model.getTempGroupIdMap().get(groupId);
				entity.compile(model.getNumOfRights());

				Set<ResourceAuthorizationRight> tempDataSet =  getEntitlementData(entity.getLinearResources(), model.getResourceBitSetMap(), model.getTempAccessRightBitMap(), model.getNumOfRights(), ResourceAuthorizationRight.class);

				if(CollectionUtils.isNotEmpty(tempDataSet)){
					tempDataSet.forEach(data ->{
						if (entity.hasResource(data.getEntity().getId())) {
							retVal.addDirectEntitlement(data.getEntity(), data.getRights());
						} else {
							retVal.addIndirectEntitlement(data.getEntity(), data.getRights());
						}
						retVal.addEntitlement(data.getEntity(), data.getRights());
					});
				}
			}
		}
		return retVal;
	}

	@Override
	public ResourceEntitlementToken getNonCachedEntitlementsForRole(final String roleId, final Date date) {
		final ResourceEntitlementToken retVal = new ResourceEntitlementToken();
		if(roleId != null) {
			final AuthorizationManagerDataModel model = dataProvider.getModel(date);
			if(model.getTempRoleIdMap().containsKey(roleId)) {
				final AuthorizationRole entity = model.getTempRoleIdMap().get(roleId);
				entity.compile(model.getNumOfRights());

				Set<ResourceAuthorizationRight> tempDataSet =  getEntitlementData(entity.getLinearResources(), model.getResourceBitSetMap(), model.getTempAccessRightBitMap(), model.getNumOfRights(), ResourceAuthorizationRight.class);

				if(CollectionUtils.isNotEmpty(tempDataSet)){
					tempDataSet.forEach(data ->{
						if (entity.hasResource(data.getEntity().getId())) {
							retVal.addDirectEntitlement(data.getEntity(), data.getRights());
						} else {
							retVal.addIndirectEntitlement(data.getEntity(), data.getRights());
						}
						retVal.addEntitlement(data.getEntity(), data.getRights());
					});
				}
			}
		}
		return retVal;
	}
	
	@Override
	public ResourceEntitlementToken getNonCachedEntitlementsForOrganization(final String organizationId, final Date date) {
		final ResourceEntitlementToken retVal = new ResourceEntitlementToken();
		if(organizationId != null) {
			final AuthorizationManagerDataModel model = dataProvider.getModel(date);
			if(model.getTempOrganizationIdMap().containsKey(organizationId)) {
				final AuthorizationOrganization entity = model.getTempOrganizationIdMap().get(organizationId);
				entity.compile(model.getNumOfRights());

				Set<ResourceAuthorizationRight> tempDataSet =  getEntitlementData(entity.getLinearResources(), model.getResourceBitSetMap(), model.getTempAccessRightBitMap(), model.getNumOfRights(), ResourceAuthorizationRight.class);

				if(CollectionUtils.isNotEmpty(tempDataSet)){
					tempDataSet.forEach(data ->{
						if (entity.hasResource(data.getEntity().getId())) {
							retVal.addDirectEntitlement(data.getEntity(), data.getRights());
						} else {
							retVal.addIndirectEntitlement(data.getEntity(), data.getRights());
						}
						retVal.addEntitlement(data.getEntity(), data.getRights());
					});
				}
			}
		}
		return retVal;
	}

	private <Entity extends AbstractAuthorizationEntity, AuthEntity extends AbstractAuthorizationRight>
	Set<AuthEntity> getEntitlementData(List<Integer> linearEntitlementBitList,
									   Map<Integer, Entity> bitSetMap, Map<Integer, AuthorizationAccessRight> rightMap, int numOfRights, Class<AuthEntity> clazz){

		Set<AuthEntity> tempDataSet = new HashSet<>();
		AuthEntity currentEntity = null;
		for(int i = 0; i < linearEntitlementBitList.size(); i++) {
			final Integer bit = linearEntitlementBitList.get(i);
			final Integer entityBit = AuthorizationUser.getEntityBit(bit.intValue(), numOfRights);
			if(entityBit != null && bitSetMap.containsKey(entityBit)) {
				currentEntity = (AuthEntity)AbstractAuthorizationRight.getInstance(clazz);
				currentEntity.setEntity(bitSetMap.get(entityBit).shallowCopy());
				tempDataSet.add(currentEntity);
			} else {
				if(currentEntity != null) {
					final int rightBit = AuthorizationUser.getRightBit(bit.intValue(), currentEntity.getEntity(), numOfRights);
					final AuthorizationAccessRight right = rightMap.get(Integer.valueOf(rightBit));
					if(right != null) {
						currentEntity.addRight(right);
					}
				}
			}
		}
		return tempDataSet;
	}

	private void fillEntitlementToken(final AbstractEntitlementToken entitlementToken, final  Set<? extends AbstractAuthorizationRight> tempDataSet, final  Map<String, Set<InternalAuthorizationToken>> directEntitlements){
		if(CollectionUtils.isNotEmpty(tempDataSet)){
			for(AbstractAuthorizationRight data: tempDataSet){
				if (directEntitlements != null && directEntitlements.containsKey(data.getEntity().getId())) {
					entitlementToken.addDirectEntitlement(data.getEntity(), data.getRights());
				} else {
					entitlementToken.addIndirectEntitlement(data.getEntity(), data.getRights());
				}
				entitlementToken.addEntitlement(data.getEntity(), data.getRights());
			}
		}
	}

	@Override
	public UserEntitlementsMatrix getUserEntitlementsMatrix(final String userId, final Date date) {
		
		final UserEntitlementsMatrix matrix = new UserEntitlementsMatrix();
		if(userId != null) {
			final AuthorizationManagerDataModel model = dataProvider.getModel(date);
			final InternalAuthroizationUser user = membershipDAO.getUser(userId, date);
			if(user != null) {
				final AuthorizationUser entity = super.process(user, model.getTempGroupIdMap(), model.getTempRoleIdMap(), model.getTempResourceIdMap(), model.getTempOrganizationIdMap(), model.getTempAccessRightMap(), new AtomicInteger(0));

				// resources for user
				ResourceEntitlementToken userResources = new ResourceEntitlementToken();
				fillEntitlementToken(userResources,
						getEntitlementData(entity.getLinearResources(), model.getResourceBitSetMap(), model.getTempAccessRightBitMap(), model.getNumOfRights(), ResourceAuthorizationRight.class),
						user.getResources());


				// roles for user
				RoleEntitlementToken userRoles = new RoleEntitlementToken();
				fillEntitlementToken(userRoles,
						getEntitlementData(entity.getLinearRoles(), model.getRoleBitSetMap(), model.getTempAccessRightBitMap(), model.getNumOfRights(), RoleAuthorizationRight.class),
						user.getRoles());


				// groups for user
				GroupEntitlementToken userGroups = new GroupEntitlementToken();
				fillEntitlementToken(userGroups,
						getEntitlementData(entity.getLinearGroups(), model.getGroupBitSetMap(), model.getTempAccessRightBitMap(), model.getNumOfRights(), GroupAuthorizationRight.class),
						user.getGroups());


				matrix.populate(userId, userResources, userRoles, userGroups, model.getTempResourceIdMap(), model.getTempGroupIdMap(), model.getTempRoleIdMap());


				populateWithMemberEntityIdAsValue(model.getRole2RoleMap(), model.getRole2RoleRightMap()).forEach((parentId, membership) -> {
					membership.forEach((childId, rights) -> {
						if (CollectionUtils.isNotEmpty(rights)) {
							rights.forEach(rightId -> {
								matrix.addRole2RoleRelationship(parentId, childId, rightId);
							});
						} else {
							matrix.addRole2RoleRelationship(parentId, childId, null);
						}
					});
				});
				populateWithMemberEntityIdAsValue(model.getRole2GroupMap(), model.getRole2GroupRightMap()).forEach((parentId, membership) -> {
					membership.forEach((childId, rights) -> {
						if (CollectionUtils.isNotEmpty(rights)) {
							rights.forEach(rightId -> {
								matrix.addRole2GroupRelationship(parentId, childId, rightId);
							});
						} else {
							matrix.addRole2GroupRelationship(parentId, childId, null);
						}
					});
				});
				populateWithMemberEntityIdAsValue(model.getRole2ResourceMap(), model.getRole2ResourceRightMap()).forEach((parentId, membership) -> {
					membership.forEach((childId, rights) -> {
						if (CollectionUtils.isNotEmpty(rights)) {
							rights.forEach(rightId -> {
								matrix.addRole2ResourceRelationship(parentId, childId, rightId);
							});
						} else {
							matrix.addRole2ResourceRelationship(parentId, childId, null);
						}
					});
				});
				populateWithMemberEntityIdAsValue(model.getGroup2GroupMap(), model.getGroup2GroupRightMap()).forEach((parentId, membership) -> {
					membership.forEach((childId, rights) -> {
						if (CollectionUtils.isNotEmpty(rights)) {
							rights.forEach(rightId -> {
								matrix.addGroup2GroupRelationship(parentId, childId, rightId);
							});
						} else {
							matrix.addGroup2GroupRelationship(parentId, childId, null);
						}
					});
				});
				populateWithMemberEntityIdAsValue(model.getGroup2ResourceMap(), model.getGroup2ResourceRightMap()).forEach((parentId, membership) -> {
					membership.forEach((childId, rights) -> {
						if (CollectionUtils.isNotEmpty(rights)) {
							rights.forEach(rightId -> {
								matrix.addGroup2ResourceRelationship(parentId, childId, rightId);
							});
						} else {
							matrix.addGroup2ResourceRelationship(parentId, childId, null);
						}
					});
				});
				populateWithMemberEntityIdAsValue(model.getResource2ResourceMap(), model.getResource2ResourceRightMap()).forEach((parentId, membership) -> {
					membership.forEach((childId, rights) -> {
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
    public Set<String> getOwnerIdsForResource(final String resourceId, final Date date){
        return getUserIdsForResource(resourceId, adminRightId, date);
    }

	private Map<String, Map<String, Set<String>>> populateWithMemberEntityIdAsValue(final Map<String, Set<MembershipDTO>> entityMap,
																			  final Map<String, Set<String>> rightMap) {
		final Map<String, Map<String, Set<String>>> retval = new HashMap<String, Map<String,Set<String>>>();
		entityMap.forEach((entityId, memberships) -> {
			if(!retval.containsKey(entityId)) {
				retval.put(entityId, new HashMap<String, Set<String>>());
			}
			if(CollectionUtils.isNotEmpty(memberships)) {
				memberships.forEach(membership -> {
					if(!retval.get(entityId).containsKey(membership.getMemberEntityId())) {
						retval.get(entityId).put(membership.getMemberEntityId(), new HashSet<String>());
					}
					if(rightMap.containsKey(membership.getId())) {
						retval.get(entityId).get(membership.getMemberEntityId()).addAll(rightMap.get(membership.getId()));
					}
				});
			}
		});
		return retval;
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
    
    private Map<String, Map<String, Set<String>>> getResource2ResourceMap(final Date date) {
		final Map<String, Set<MembershipDTO>> entityMap = getMembershipMapByMemberEntityId(membershipDAO.getResource2ResourceMembership(date));
		final Map<String, Set<String>> rightMap = getRightMap(membershipDAO.getResource2ResourceRights());
		return populateWithEntityIdAsValue(entityMap, rightMap);
    }
    
    private Map<String, Map<String, Set<String>>> getResource2GroupMap(final Date date) {
    	final Map<String, Set<MembershipDTO>> entityMap = getMembershipMapByMemberEntityId(membershipDAO.getGroup2ResourceMembership(date));
    	final Map<String, Set<String>> rightMap = getRightMap(membershipDAO.getGroup2ResourceRights());
    	return populateWithEntityIdAsValue(entityMap, rightMap);
    }
    
    private Map<String, Map<String, Set<String>>> getResource2RoleMap(final Date date) {
    	final Map<String, Set<MembershipDTO>> entityMap = getMembershipMapByMemberEntityId(membershipDAO.getRole2ResourceMembership(date));
    	final Map<String, Set<String>> rightMap = getRightMap(membershipDAO.getRole2ResourceRights());
    	return populateWithEntityIdAsValue(entityMap, rightMap);
    }
    
    private Map<String, Map<String, Set<String>>> getResource2OrgMap(final Date date) {
    	final Map<String, Set<MembershipDTO>> entityMap = getMembershipMapByMemberEntityId(membershipDAO.getOrg2ResourceMembership(date));
    	final Map<String, Set<String>> rightMap = getRightMap(membershipDAO.getOrg2ResourceRights());
    	return populateWithEntityIdAsValue(entityMap, rightMap);
    }
    
    private Map<String, Map<String, Set<String>>> getGroup2GroupMap(final Date date) {
    	final Map<String, Set<MembershipDTO>> entityMap = getMembershipMapByMemberEntityId(membershipDAO.getGroup2GroupMembership(date));
    	final Map<String, Set<String>> rightMap = getRightMap(membershipDAO.getGroup2GroupRights());
    	return populateWithEntityIdAsValue(entityMap, rightMap);
    }
    
    private Map<String, Map<String, Set<String>>> getGroup2RoleMap(final Date date) {
    	final Map<String, Set<MembershipDTO>> entityMap = getMembershipMapByMemberEntityId(membershipDAO.getRole2GroupMembership(date));
    	final Map<String, Set<String>> rightMap = getRightMap(membershipDAO.getRole2GroupRights());
    	return populateWithEntityIdAsValue(entityMap, rightMap);
    }
    
    private Map<String, Map<String, Set<String>>> getGroup2OrgMap(final Date date) {
    	final Map<String, Set<MembershipDTO>> entityMap = getMembershipMapByMemberEntityId(membershipDAO.getOrg2GroupMembership(date));
    	final Map<String, Set<String>> rightMap = getRightMap(membershipDAO.getOrg2GroupRights());
    	return populateWithEntityIdAsValue(entityMap, rightMap);
    }
    
    private Map<String, Map<String, Set<String>>> getRole2RoleMap(final Date date) {
    	final Map<String, Set<MembershipDTO>> entityMap = getMembershipMapByMemberEntityId(membershipDAO.getRole2RoleMembership(date));
    	final Map<String, Set<String>> rightMap = getRightMap(membershipDAO.getRole2RoleRights());
    	return populateWithEntityIdAsValue(entityMap, rightMap);
    }
    
    private Map<String, Map<String, Set<String>>> getRole2OrgMap(final Date date) {
    	final Map<String, Set<MembershipDTO>> entityMap = getMembershipMapByMemberEntityId(membershipDAO.getOrg2RoleMembership(date));
    	final Map<String, Set<String>> rightMap = getRightMap(membershipDAO.getOrg2RoleRights());
    	return populateWithEntityIdAsValue(entityMap, rightMap);
    }
    
    private Map<String, Map<String, Set<String>>> getOrg2OrgMap(final Date date) {
    	final Map<String, Set<MembershipDTO>> entityMap = getMembershipMapByMemberEntityId(membershipDAO.getOrg2OrgMembership(date));
    	final Map<String, Set<String>> rightMap = getRightMap(membershipDAO.getOrg2OrgRights());
    	return populateWithEntityIdAsValue(entityMap, rightMap);
    }

    @Override
    public HashMap<String, SetStringResponse> getOwnerIdsForResourceSet(final Set<String> resourceIdSet, final Date date){
    	final HashMap<String, SetStringResponse> retVal = new HashMap<String, SetStringResponse>();
    	if(CollectionUtils.isNotEmpty(resourceIdSet)) {
    		resourceIdSet.forEach(resourceId -> {
    			final Set<String> userIds = getUserIdsForResource(resourceId, adminRightId, date);
    			retVal.put(resourceId, new SetStringResponse(userIds));
    		});
    	}
    	return retVal;
    }

    @Override
	public Set<String> getUserIdsEntitledForResource(final String resourceId, final Date date){
		return getUserIdsForResource(resourceId, null, date);
	}

	@Override
	public HashMap<String, SetStringResponse> getUserIdsEntitledForResourceSet(final Set<String> resourceIdSet, final Date date){
		final HashMap<String, SetStringResponse> retVal = new HashMap<String, SetStringResponse>();
    	if(CollectionUtils.isNotEmpty(resourceIdSet)) {
    		resourceIdSet.forEach(resourceId -> {
    			final Set<String> userIds = getUserIdsForResource(resourceId, null, date);
    			retVal.put(resourceId, new SetStringResponse(userIds));
    		});
    	}
    	return retVal;
	}

	@Override
	public Set<String> getOwnerIdsForGroup(final String groupId, final Date date){
		return getUserIdsForGroup(groupId, adminRightId, date);
	}

	@Override
    public HashMap<String,SetStringResponse> getOwnerIdsForGroupSet(final Set<String> groupIdSet, final Date date){
		final HashMap<String, SetStringResponse> retVal = new HashMap<String, SetStringResponse>();
    	if(CollectionUtils.isNotEmpty(groupIdSet)) {
    		groupIdSet.forEach(groupId -> {
    			final Set<String> userIds = getOwnerIdsForGroup(groupId, date);
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
    
    private Set<String> getUserIdsForGroup(final String groupId, final String rightId, final Date date){
		final Set<String> userIds = new HashSet<String>();
    	
    	final Map<String, Map<String, Set<String>>> group2GroupMap = getGroup2GroupMap(date);
    	final Map<String, Map<String, Set<String>>> group2RoleMap = getGroup2RoleMap(date);
    	final Map<String, Map<String, Set<String>>> group2OrgMap = getGroup2OrgMap(date);
	   
    	final Map<String, Map<String, Set<String>>> role2RoleMap = getRole2RoleMap(date);
    	final Map<String, Map<String, Set<String>>> role2OrgMap = getRole2OrgMap(date);
	   
    	final Map<String, Map<String, Set<String>>> org2OrgMap = getOrg2OrgMap(date);
    	
		if(rightId != null) {
			userIds.addAll(membershipDAO.getUsersForGroup(groupId, rightId, date));
		} else {
			userIds.addAll(membershipDAO.getUsersForGroup(groupId, date));
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
    
	private Set<String> getUserIdsForResource(final String resourceId, final String rightId, final Date date){
	   	  final Map<String, Map<String, Set<String>>> resource2ResourceMap = getResource2ResourceMap(date);
	   	  final Map<String, Map<String, Set<String>>> resource2GroupMap = getResource2GroupMap(date);
	   	  final Map<String, Map<String, Set<String>>> resource2RoleMap = getResource2RoleMap(date);
	   	  final Map<String, Map<String, Set<String>>> resource2OrgMap = getResource2OrgMap(date);
	   
	   	  final Map<String, Map<String, Set<String>>> group2GroupMap = getGroup2GroupMap(date);
	   	  final Map<String, Map<String, Set<String>>> group2RoleMap = getGroup2RoleMap(date);
	   	  final Map<String, Map<String, Set<String>>> group2OrgMap = getGroup2OrgMap(date);
	   
	   	  final Map<String, Map<String, Set<String>>> role2RoleMap = getRole2RoleMap(date);
	   	  final Map<String, Map<String, Set<String>>> role2OrgMap = getRole2OrgMap(date);
	   
	   	  final Map<String, Map<String, Set<String>>> org2OrgMap = getOrg2OrgMap(date);
	   	
	   	  final Set<String> userIds = new HashSet<>();
	   	  if(rightId != null) {
	   		  userIds.addAll(membershipDAO.getUsersForResource(resourceId, rightId, date));
	   	  } else {
	   		  userIds.addAll(membershipDAO.getUsersForResource(resourceId, date));
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
