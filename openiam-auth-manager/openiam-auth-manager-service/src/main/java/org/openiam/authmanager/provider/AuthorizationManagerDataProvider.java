package org.openiam.authmanager.provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.authmanager.common.model.AuthorizationAccessRight;
import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationOrganization;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.authmanager.common.model.AuthorizationUser;
import org.openiam.authmanager.dao.MembershipDAO;
import org.openiam.authmanager.model.AuthorizationManagerDataModel;
import org.openiam.idm.srvc.membership.domain.AbstractMembershipXrefEntity;
import org.openiam.membership.MembershipDTO;
import org.openiam.membership.MembershipRightDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationManagerDataProvider {

	@Autowired
	private MembershipDAO membershipDAO;

	@Autowired
	private org.openiam.idm.srvc.access.service.AccessRightDAO hibernateAccessRightDAO;
	
	private Set<AuthorizationAccessRight> getAccessRight(final AbstractMembershipXrefEntity<?, ?> xref, 
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
	
	public AuthorizationManagerDataModel getModel() {
		final AuthorizationManagerDataModel model = new AuthorizationManagerDataModel();
		
		final List<AuthorizationResource> hbmResourceList = membershipDAO.getResources();
		final Map<String, Set<MembershipDTO>> resource2ResourceMap = getMembershipMapByEntityId(membershipDAO.getResource2ResourceMembership());
		final Map<String, Set<String>> resource2ResourceRightMap = getRightMap(membershipDAO.getResource2ResourceRights());
		
		final Map<String, Set<MembershipDTO>> user2ResourceMap = getMembershipMapByMemberEntityId(membershipDAO.getUser2ResourceMembership());
		final Map<String, Set<String>> user2ResourceRightMap = getRightMap(membershipDAO.getUser2ResourceRights());
		
		/* groups */
		final List<AuthorizationGroup> hbmGroupList = membershipDAO.getGroups();
		final Map<String, Set<MembershipDTO>> group2GroupMap = getMembershipMapByEntityId(membershipDAO.getGroup2GroupMembership());
		final Map<String, Set<String>> group2GroupRightMap = getRightMap(membershipDAO.getGroup2GroupRights());
		
		final Map<String, Set<MembershipDTO>> group2UserMap = getMembershipMapByMemberEntityId(membershipDAO.getUser2GroupMembership());
		final Map<String, Set<String>> group2UserRightMap = getRightMap(membershipDAO.getUser2GroupRights());
		
		
		final Map<String, Set<MembershipDTO>> group2ResourceMap = getMembershipMapByEntityId(membershipDAO.getGroup2ResourceMembership());
		final Map<String, Set<String>> group2ResourceRightMap = getRightMap(membershipDAO.getGroup2ResourceRights());
		
		final List<AuthorizationRole> hbmRoleList = membershipDAO.getRoles();
		final Map<String, Set<MembershipDTO>> role2RoleMap = getMembershipMapByEntityId(membershipDAO.getRole2RoleMembership());
		final Map<String, Set<String>> role2RoleRightMap = getRightMap(membershipDAO.getRole2RoleRights());
		
		final Map<String, Set<MembershipDTO>> role2GroupMap = getMembershipMapByEntityId(membershipDAO.getRole2GroupMembership());
		final Map<String, Set<String>> role2GroupRightMap = getRightMap(membershipDAO.getRole2GroupRights());
		
		final Map<String, Set<MembershipDTO>> role2ResourceMap = getMembershipMapByEntityId(membershipDAO.getRole2ResourceMembership());
		final Map<String, Set<String>> role2ResourceRightMap = getRightMap(membershipDAO.getRole2ResourceRights());
		
		final Map<String, Set<MembershipDTO>> user2RoleMap = getMembershipMapByMemberEntityId(membershipDAO.getUser2RoleMembership());
		final Map<String, Set<String>> user2RoleRightMap = getRightMap(membershipDAO.getUser2RoleRights());
		
		final List<AuthorizationOrganization> hbmOrganizationList = membershipDAO.getOrganizations();
		final Map<String, Set<MembershipDTO>> user2OrgMap = getMembershipMapByMemberEntityId(membershipDAO.getUser2OrgMembership());
		final Map<String, Set<String>> user2OrgRightMap = getRightMap(membershipDAO.getUser2OrgRights());
		
		final Map<String, Set<MembershipDTO>> role2OrgMap = getMembershipMapByEntityId(membershipDAO.getOrg2RoleMembership());
		final Map<String, Set<String>> role2OrgRightMap = getRightMap(membershipDAO.getOrg2RoleRights());
		
		final Map<String, Set<MembershipDTO>> group2OrgMap = getMembershipMapByEntityId(membershipDAO.getOrg2GroupMembership());
		final Map<String, Set<String>> group2OrgRightMap = getRightMap(membershipDAO.getOrg2GroupRights());
		
		final Map<String, Set<MembershipDTO>> resource2OrgMap = getMembershipMapByEntityId(membershipDAO.getOrg2ResourceMembership());
		final Map<String, Set<String>> resource2OrgRightMap = getRightMap(membershipDAO.getOrg2ResourceRights());
		
		final Map<String, Set<MembershipDTO>> org2Org2Map = getMembershipMapByEntityId(membershipDAO.getOrg2OrgMembership());
		final Map<String, Set<String>> org2OrgRightMap = getRightMap(membershipDAO.getOrg2OrgRights());
		
		final Map<String, AuthorizationAccessRight> tempAccessRightMap = hibernateAccessRightDAO.findAll()
				  .stream()
				  .map(e -> new AuthorizationAccessRight(e, model.getTempAccessRightBitSet().getAndIncrement()))
				  .collect(Collectors.toMap(AuthorizationAccessRight::getId, Function.identity()));
		
		final Map<Integer, AuthorizationAccessRight> tempAccessRightBitMap = new HashMap<Integer, AuthorizationAccessRight>();
		tempAccessRightMap.forEach((key, value) -> {
			tempAccessRightBitMap.put(Integer.valueOf(value.getBitIdx()), value);
		});
		
		final Map<String, AuthorizationOrganization> tempOrganizationIdMap = hbmOrganizationList
				.stream()
				.map(e -> new AuthorizationOrganization(e, model.getTempOrgBitSet().getAndIncrement()))
				.collect(Collectors.toMap(AuthorizationOrganization::getId, Function.identity()));
		
		final Map<String, AuthorizationRole> tempRoleIdMap = hbmRoleList
				.stream()
				.map(e -> new AuthorizationRole(e, model.getTempRoleBitSet().getAndIncrement()))
				.collect(Collectors.toMap(AuthorizationRole::getId, Function.identity()));
		
		final Map<String, AuthorizationResource> tempResourceIdMap = hbmResourceList
				.stream()
				.map(e -> new AuthorizationResource(e, model.getTempResourceBitSet().getAndIncrement()))
				.collect(Collectors.toMap(AuthorizationResource::getId, Function.identity()));
		
		final Map<String, AuthorizationGroup> tempGroupIdMap = hbmGroupList
				.stream()
				.map(e -> new AuthorizationGroup(e, model.getTempGroupBitSet().getAndIncrement()))
				.collect(Collectors.toMap(AuthorizationGroup::getId, Function.identity()));
		
		model.setHbmResourceList(hbmResourceList);
		model.setResource2ResourceMap(resource2ResourceMap);
		model.setResource2ResourceRightMap(resource2ResourceRightMap);
		model.setUser2ResourceMap(user2ResourceMap);
		model.setUser2ResourceRightMap(user2ResourceRightMap);
		model.setHbmGroupList(hbmGroupList);
		model.setGroup2GroupMap(group2GroupMap);
		model.setGroup2GroupRightMap(group2GroupRightMap);
		model.setGroup2UserMap(group2UserMap);
		model.setGroup2UserRightMap(group2UserRightMap);
		model.setGroup2ResourceMap(group2ResourceMap);
		model.setGroup2ResourceRightMap(group2ResourceRightMap);
		model.setHbmRoleList(hbmRoleList);
		model.setRole2RoleMap(role2RoleMap);
		model.setRole2RoleRightMap(role2RoleRightMap);
		model.setRole2GroupMap(role2GroupMap);
		model.setRole2GroupRightMap(role2GroupRightMap);
		model.setRole2ResourceMap(role2ResourceMap);
		model.setRole2ResourceRightMap(role2ResourceRightMap);
		model.setUser2RoleMap(user2RoleMap);
		model.setUser2RoleRightMap(user2RoleRightMap);
		model.setHbmOrganizationList(hbmOrganizationList);
		model.setUser2OrgMap(user2OrgMap);
		model.setUser2OrgRightMap(user2OrgRightMap);
		model.setRole2OrgMap(role2OrgMap);
		model.setRole2OrgRightMap(role2OrgRightMap);
		model.setGroup2OrgMap(group2OrgMap);
		model.setGroup2OrgRightMap(group2OrgRightMap);
		model.setResource2OrgMap(resource2OrgMap);
		model.setResource2OrgRightMap(resource2OrgRightMap);
		model.setOrg2Org2Map(org2Org2Map);
		model.setOrg2OrgRightMap(org2OrgRightMap);
		model.setTempAccessRightMap(tempAccessRightMap);
		model.setTempAccessRightBitMap(tempAccessRightBitMap);
		model.setTempOrganizationIdMap(tempOrganizationIdMap);
		model.setTempRoleIdMap(tempRoleIdMap);
		model.setTempResourceIdMap(tempResourceIdMap);
		model.setTempGroupIdMap(tempGroupIdMap);
		
		
		return model;
	}
}
