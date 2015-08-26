package org.openiam.authmanager.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.sf.ehcache.Element;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.openiam.authmanager.common.model.AuthorizationAccessRight;
import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationOrganization;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.authmanager.common.model.AuthorizationUser;
import org.openiam.authmanager.common.model.InternalAuthroizationUser;
import org.openiam.authmanager.common.xref.GroupGroupXref;
import org.openiam.authmanager.common.xref.GroupUserXref;
import org.openiam.authmanager.common.xref.OrgGroupXref;
import org.openiam.authmanager.common.xref.OrgOrgXref;
import org.openiam.authmanager.common.xref.OrgResourceXref;
import org.openiam.authmanager.common.xref.OrgRoleXref;
import org.openiam.authmanager.common.xref.OrgUserXref;
import org.openiam.authmanager.common.xref.ResourceGroupXref;
import org.openiam.authmanager.common.xref.ResourceResourceXref;
import org.openiam.authmanager.common.xref.ResourceRoleXref;
import org.openiam.authmanager.common.xref.ResourceUserXref;
import org.openiam.authmanager.common.xref.RoleGroupXref;
import org.openiam.authmanager.common.xref.RoleRoleXref;
import org.openiam.authmanager.common.xref.RoleUserXref;
import org.openiam.authmanager.model.AuthorizationManagerDataModel;
import org.openiam.idm.srvc.membership.domain.AbstractMembershipXrefEntity;
import org.openiam.membership.MembershipDTO;
import org.openiam.membership.MembershipRightDTO;

public abstract class AbstractAuthorizationManagerService {

	protected Set<AuthorizationAccessRight> getAccessRight(final AbstractMembershipXrefEntity<?, ?> xref, 
														   final Map<String, AuthorizationAccessRight> rights) {
		Set<AuthorizationAccessRight> retVal = null;
		if(xref != null && CollectionUtils.isNotEmpty(xref.getRights())) {
			retVal = xref.getRights().stream().map(e -> rights.get(e.getId())).collect(Collectors.toSet());
		}
		return retVal;
	}

	protected Set<AuthorizationAccessRight> getAccessRight(final Set<String> rightIds, 
														   final Map<String, AuthorizationAccessRight> rights) {
		Set<AuthorizationAccessRight> retVal = null;
		if(CollectionUtils.isNotEmpty(rightIds)) {
			retVal = rightIds.stream().map(e -> rights.get(e)).collect(Collectors.toSet());
		}
		return retVal;
	}

	protected Map<String, Set<MembershipDTO>> getMembershipMapByEntityId(final List<MembershipDTO> list) {
		return list.stream().collect(Collectors.groupingBy(MembershipDTO::getEntityId,
				Collectors.mapping(Function.identity(), Collectors.toSet())));
	}

	protected Map<String, Set<MembershipDTO>> getMembershipMapByMemberEntityId(final List<MembershipDTO> list) {
		return list.stream().collect(Collectors.groupingBy(MembershipDTO::getMemberEntityId,
				Collectors.mapping(Function.identity(), Collectors.toSet())));
	}

	protected Map<String, Set<String>> getRightMap(final List<MembershipRightDTO> list) {
		return list.stream().collect(Collectors.groupingBy(MembershipRightDTO::getId,
				Collectors.mapping(MembershipRightDTO::getRightId, Collectors.toSet())));
	}
	
	protected void populateUser(final AuthorizationUser user,
								final AuthorizationManagerDataModel model) {
		if(user != null) {
			if(CollectionUtils.isNotEmpty(model.getUser2ResourceMap().get(user.getId()))) {
				model.getUser2ResourceMap().get(user.getId()).forEach(e -> {
					final AuthorizationResource resource = model.getTempResourceIdMap().get(e.getEntityId());
					if(resource != null) {
						final ResourceUserXref xref = new ResourceUserXref();
						xref.setUser(user);
						xref.setResource(resource);
						xref.setRights(getAccessRight(model.getUser2ResourceRightMap().get(e.getId()), model.getTempAccessRightMap()));
						user.addResource(xref);
					}
				});
			}
			
			if(CollectionUtils.isNotEmpty(model.getGroup2UserMap().get(user.getId()))) {
				model.getGroup2UserMap().get(user.getId()).forEach(e -> {
					final AuthorizationGroup group = model.getTempGroupIdMap().get(e.getEntityId());
					if(group != null) {
						final GroupUserXref xref = new GroupUserXref();
						xref.setUser(user);
						xref.setGroup(group);
						xref.setRights(getAccessRight(model.getGroup2UserRightMap().get(e.getId()), model.getTempAccessRightMap()));
						user.addGroup(xref);
					}
				});
			}
			
			if(CollectionUtils.isNotEmpty(model.getUser2RoleMap().get(user.getId()))) {
				model.getUser2RoleMap().get(user.getId()).forEach(e -> {
					final AuthorizationRole role = model.getTempRoleIdMap().get(e.getEntityId());
					if(role != null) {
						final RoleUserXref xref = new RoleUserXref();
						xref.setUser(user);
						xref.setRole(role);
						xref.setRights(getAccessRight(model.getUser2RoleRightMap().get(e.getId()), model.getTempAccessRightMap()));
						user.addRole(xref);
					}
				});
			}
			
			if(CollectionUtils.isNotEmpty(model.getUser2OrgMap().get(user.getId()))) {
				model.getUser2OrgMap().get(user.getId()).forEach(e -> {
					final AuthorizationOrganization organization = model.getTempOrganizationIdMap().get(e.getEntityId());
					if(organization != null) {
						final OrgUserXref xref = new OrgUserXref();
						xref.setUser(user);
						xref.setOrganization(organization);
						xref.setRights(getAccessRight(model.getUser2OrgRightMap().get(e.getId()), model.getTempAccessRightMap()));
						user.addOrganization(xref);
					}
				});
			}
		}
	}
	
	protected AuthorizationUser process(final InternalAuthroizationUser user,
										final Map<String, AuthorizationGroup> groupIdCache,
										final Map<String, AuthorizationRole> roleIdCache,
										final Map<String, AuthorizationResource> resourceIdCache,
										final Map<String, AuthorizationOrganization> organizationIdCache,
										final Map<String, AuthorizationAccessRight> accessRightIdCache,
										final AtomicInteger userBitSet) {
		if(user != null) {
			final AuthorizationUser retVal = new AuthorizationUser(user);
			if(MapUtils.isNotEmpty(user.getGroups())) {
				user.getGroups().forEach((entityId, rights) -> {
					final AuthorizationGroup entity = groupIdCache.get(entityId);
					if(entity != null) {
						final GroupUserXref xref = new GroupUserXref();
						xref.setGroup(entity);
						xref.setUser(retVal);
						xref.setRights(getAccessRight(rights, accessRightIdCache));
						retVal.addGroup(xref);
					}
				});
			}
			
			if(MapUtils.isNotEmpty(user.getRoles())) {
				user.getRoles().forEach((entityId, rights) -> {
					final AuthorizationRole entity = roleIdCache.get(entityId);
					if(entity != null) {
						final RoleUserXref xref = new RoleUserXref();
						xref.setRole(entity);
						xref.setUser(retVal);
						xref.setRights(getAccessRight(rights, accessRightIdCache));
						retVal.addRole(xref);
					}
				});
			}
			
			if(MapUtils.isNotEmpty(user.getResources())) {
				user.getResources().forEach((entityId, rights) -> {
					final AuthorizationResource entity = resourceIdCache.get(entityId);
					if(entity != null) {
						final ResourceUserXref xref = new ResourceUserXref();
						xref.setResource(entity);
						xref.setUser(retVal);
						xref.setRights(getAccessRight(rights, accessRightIdCache));
						retVal.addResource(xref);
					}
				});
			}
			
			if(MapUtils.isNotEmpty(user.getOrganizations())) {
				user.getOrganizations().forEach((entityId, rights) -> {
					final AuthorizationOrganization entity = organizationIdCache.get(entityId);
					if(entity != null) {
						final OrgUserXref xref = new OrgUserXref();
						xref.setOrganization(entity);
						xref.setUser(retVal);
						xref.setRights(getAccessRight(rights, accessRightIdCache));
						retVal.addOrganization(xref);
					}
				});
			}
			
			//NEW:  public resources are really public
			/*
			if(CollectionUtils.isNotEmpty(publicResources)) {
				for(final AuthorizationResource resource : publicResources) {
					retVal.addResource(resource);
				}
			}
			*/
			
			final int numOfRights = accessRightIdCache.size();
			retVal.compile(numOfRights, -1);
			retVal.setBitSetIdx(userBitSet.incrementAndGet());
			return retVal;
		} else {
			return null;
		}
	}
}
