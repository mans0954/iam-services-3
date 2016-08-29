package org.openiam.authmanager.provider;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.am.srvc.dto.jdbc.AuthorizationAccessRight;
import org.openiam.am.srvc.dto.jdbc.AuthorizationGroup;
import org.openiam.am.srvc.dto.jdbc.AuthorizationOrganization;
import org.openiam.am.srvc.dto.jdbc.AuthorizationResource;
import org.openiam.am.srvc.dto.jdbc.AuthorizationRole;
import org.openiam.am.srvc.dto.jdbc.xref.GroupGroupXref;
import org.openiam.am.srvc.dto.jdbc.xref.OrgGroupXref;
import org.openiam.am.srvc.dto.jdbc.xref.OrgOrgXref;
import org.openiam.am.srvc.dto.jdbc.xref.OrgResourceXref;
import org.openiam.am.srvc.dto.jdbc.xref.OrgRoleXref;
import org.openiam.am.srvc.dto.jdbc.xref.ResourceGroupXref;
import org.openiam.am.srvc.dto.jdbc.xref.ResourceResourceXref;
import org.openiam.am.srvc.dto.jdbc.xref.ResourceRoleXref;
import org.openiam.am.srvc.dto.jdbc.xref.RoleGroupXref;
import org.openiam.am.srvc.dto.jdbc.xref.RoleRoleXref;
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
	
	public AuthorizationManagerDataModel getModel(final Date date) {
		final AuthorizationManagerDataModel model = new AuthorizationManagerDataModel();
		
		final List<AuthorizationResource> hbmResourceList = membershipDAO.getResources();
		final Map<String, Set<MembershipDTO>> resource2ResourceMap = getMembershipMapByEntityId(membershipDAO.getResource2ResourceMembership(date));
		final Map<String, Set<String>> resource2ResourceRightMap = getRightMap(membershipDAO.getResource2ResourceRights());
		
		final Map<String, Set<MembershipDTO>> user2ResourceMap = getMembershipMapByMemberEntityId(membershipDAO.getUser2ResourceMembership(date));
		final Map<String, Set<String>> user2ResourceRightMap = getRightMap(membershipDAO.getUser2ResourceRights());
		
		/* groups */
		final List<AuthorizationGroup> hbmGroupList = membershipDAO.getGroups();
		final Map<String, Set<MembershipDTO>> group2GroupMap = getMembershipMapByEntityId(membershipDAO.getGroup2GroupMembership(date));
		final Map<String, Set<String>> group2GroupRightMap = getRightMap(membershipDAO.getGroup2GroupRights());
		
		final Map<String, Set<MembershipDTO>> group2UserMap = getMembershipMapByMemberEntityId(membershipDAO.getUser2GroupMembership(date));
		final Map<String, Set<String>> group2UserRightMap = getRightMap(membershipDAO.getUser2GroupRights());
		
		
		final Map<String, Set<MembershipDTO>> group2ResourceMap = getMembershipMapByEntityId(membershipDAO.getGroup2ResourceMembership(date));
		final Map<String, Set<String>> group2ResourceRightMap = getRightMap(membershipDAO.getGroup2ResourceRights());
		
		final List<AuthorizationRole> hbmRoleList = membershipDAO.getRoles();
		final Map<String, Set<MembershipDTO>> role2RoleMap = getMembershipMapByEntityId(membershipDAO.getRole2RoleMembership(date));
		final Map<String, Set<String>> role2RoleRightMap = getRightMap(membershipDAO.getRole2RoleRights());
		
		final Map<String, Set<MembershipDTO>> role2GroupMap = getMembershipMapByEntityId(membershipDAO.getRole2GroupMembership(date));
		final Map<String, Set<String>> role2GroupRightMap = getRightMap(membershipDAO.getRole2GroupRights());
		
		final Map<String, Set<MembershipDTO>> role2ResourceMap = getMembershipMapByEntityId(membershipDAO.getRole2ResourceMembership(date));
		final Map<String, Set<String>> role2ResourceRightMap = getRightMap(membershipDAO.getRole2ResourceRights());
		
		final Map<String, Set<MembershipDTO>> user2RoleMap = getMembershipMapByMemberEntityId(membershipDAO.getUser2RoleMembership(date));
		final Map<String, Set<String>> user2RoleRightMap = getRightMap(membershipDAO.getUser2RoleRights());
		
		final List<AuthorizationOrganization> hbmOrganizationList = membershipDAO.getOrganizations();
		final Map<String, Set<MembershipDTO>> user2OrgMap = getMembershipMapByMemberEntityId(membershipDAO.getUser2OrgMembership(date));
		final Map<String, Set<String>> user2OrgRightMap = getRightMap(membershipDAO.getUser2OrgRights());
		
		final Map<String, Set<MembershipDTO>> role2OrgMap = getMembershipMapByEntityId(membershipDAO.getOrg2RoleMembership(date));
		final Map<String, Set<String>> role2OrgRightMap = getRightMap(membershipDAO.getOrg2RoleRights());
		
		final Map<String, Set<MembershipDTO>> group2OrgMap = getMembershipMapByEntityId(membershipDAO.getOrg2GroupMembership(date));
		final Map<String, Set<String>> group2OrgRightMap = getRightMap(membershipDAO.getOrg2GroupRights());
		
		final Map<String, Set<MembershipDTO>> resource2OrgMap = getMembershipMapByEntityId(membershipDAO.getOrg2ResourceMembership(date));
		final Map<String, Set<String>> resource2OrgRightMap = getRightMap(membershipDAO.getOrg2ResourceRights());
		
		final Map<String, Set<MembershipDTO>> org2Org2Map = getMembershipMapByEntityId(membershipDAO.getOrg2OrgMembership(date));
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
		
		
		final Map<Integer, AuthorizationOrganization> tempOrgBitMap = new HashMap<Integer, AuthorizationOrganization>(); 
		final Map<Integer, AuthorizationRole> tempRoleBitMap = new HashMap<Integer, AuthorizationRole>();
		final Map<Integer, AuthorizationGroup> tempGroupBitMap = new HashMap<Integer, AuthorizationGroup>();
		final Map<Integer, AuthorizationResource> tempResourceBitMap = new HashMap<Integer, AuthorizationResource>();
		
		tempOrganizationIdMap.entrySet().forEach(e -> {
			tempOrgBitMap.put(Integer.valueOf(e.getValue().getBitSetIdx()), e.getValue());
		});
		
		tempRoleIdMap.entrySet().forEach(e -> {
			tempRoleBitMap.put(Integer.valueOf(e.getValue().getBitSetIdx()), e.getValue());
		});
		
		tempGroupIdMap.entrySet().forEach(e -> {
			tempGroupBitMap.put(Integer.valueOf(e.getValue().getBitSetIdx()), e.getValue());
		});
		
		tempResourceIdMap.entrySet().forEach(e -> {
			tempResourceBitMap.put(Integer.valueOf(e.getValue().getBitSetIdx()), e.getValue());
		});
		
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
		model.setOrganizationBitSetMap(tempOrgBitMap);
		model.setRoleBitSetMap(tempRoleBitMap);
		model.setGroupBitSetMap(tempGroupBitMap);
		model.setResourceBitSetMap(tempResourceBitMap);
		visit(model);
		return model;
	}
	
	private void visit(final AuthorizationManagerDataModel model) {
		visitOrganizations(model);
		visitRoles(model);
		visitGroups(model);
		visitResources(model);
	}
	
	private void visitResources(final AuthorizationManagerDataModel model) {
		model.getHbmResourceList().forEach(entity -> {
			final AuthorizationResource resource = model.getTempResourceIdMap().get(entity.getId());
			if(resource != null) {
				if(CollectionUtils.isNotEmpty(model.getResource2ResourceMap().get(entity.getId()))) {
					model.getResource2ResourceMap().get(entity.getId()).forEach(e -> {
						final AuthorizationResource child = model.getTempResourceIdMap().get(e.getMemberEntityId());
						if(child != null) {
							final ResourceResourceXref xref = new ResourceResourceXref();
							xref.setResource(resource);
							xref.setMemberResource(child);
							xref.setRights(getAccessRight(model.getResource2ResourceRightMap().get(e.getId()), model.getTempAccessRightMap()));
							child.addParentResoruce(xref);
						}
					});
				}
			}
		});
	}
	
	private void visitGroups(final AuthorizationManagerDataModel model) {
		model.getHbmGroupList().forEach(entity -> {
			final AuthorizationGroup group = model.getTempGroupIdMap().get(entity.getId());
			if(group != null) {
				if(CollectionUtils.isNotEmpty(model.getGroup2ResourceMap().get(entity.getId()))) {
					model.getGroup2ResourceMap().get(entity.getId()).forEach(e -> {
						final AuthorizationResource resource = model.getTempResourceIdMap().get(e.getMemberEntityId());
						if(resource != null) {
							final ResourceGroupXref xref = new ResourceGroupXref();
							xref.setGroup(group);
							xref.setResource(resource);
							xref.setRights(getAccessRight(model.getGroup2ResourceRightMap().get(e.getId()), model.getTempAccessRightMap()));
							group.addResource(xref);
						}
					});
				}
				
				if(CollectionUtils.isNotEmpty(model.getGroup2GroupMap().get(entity.getId()))) {
					model.getGroup2GroupMap().get(entity.getId()).forEach(e -> {
						final AuthorizationGroup child = model.getTempGroupIdMap().get(e.getMemberEntityId());
						if(child != null) {
							final GroupGroupXref xref = new GroupGroupXref();
							xref.setGroup(group);
							xref.setMemberGroup(child);
							xref.setRights(getAccessRight(model.getGroup2GroupRightMap().get(e.getId()), model.getTempAccessRightMap()));
							child.addParentGroup(xref);
						}
					});
				}
			}
		});
	}
	
	private void visitRoles(final AuthorizationManagerDataModel model) {
		model.getHbmRoleList().forEach(entity -> {
			final AuthorizationRole role = model.getTempRoleIdMap().get(entity.getId());
			if(role != null) {
				if(CollectionUtils.isNotEmpty(model.getRole2GroupMap().get(entity.getId()))) {
					model.getRole2GroupMap().get(entity.getId()).forEach(e -> {
						final AuthorizationGroup group = model.getTempGroupIdMap().get(e.getMemberEntityId());
						if(group != null) {
							final RoleGroupXref xref = new RoleGroupXref();
							xref.setRole(role);
							xref.setGroup(group);
							xref.setRights(getAccessRight(model.getRole2GroupRightMap().get(e.getId()), model.getTempAccessRightMap()));
							role.addGroup(xref);
						}
					});
				}
				
				if(CollectionUtils.isNotEmpty(model.getRole2ResourceMap().get(entity.getId()))) {
					model.getRole2ResourceMap().get(entity.getId()).forEach(e -> {
						final AuthorizationResource resource = model.getTempResourceIdMap().get(e.getMemberEntityId());
						if(resource != null) {
							final ResourceRoleXref xref = new ResourceRoleXref();
							xref.setRole(role);
							xref.setResource(resource);
							xref.setRights(getAccessRight(model.getRole2ResourceRightMap().get(e.getId()), model.getTempAccessRightMap()));
							role.addResource(xref);
						}
					});
				}

				if(CollectionUtils.isNotEmpty(model.getRole2RoleMap().get(entity.getId()))) {
					model.getRole2RoleMap().get(entity.getId()).forEach(e -> {
						final AuthorizationRole child = model.getTempRoleIdMap().get(e.getMemberEntityId());
						if(child != null) {
							final RoleRoleXref xref = new RoleRoleXref();
							xref.setRole(role);
							xref.setMemberRole(child);
							xref.setRights(getAccessRight(model.getRole2RoleRightMap().get(e.getId()), model.getTempAccessRightMap()));
							child.addParentRole(xref);
						}
					});
				}
			}
		});
	}
	
	private void visitOrganizations(final AuthorizationManagerDataModel model) {
		model.getHbmOrganizationList().forEach(entity -> {
			final AuthorizationOrganization organization = model.getTempOrganizationIdMap().get(entity.getId());
			if(organization != null) {
				if(CollectionUtils.isNotEmpty(model.getResource2OrgMap().get(entity.getId()))) {
					model.getResource2OrgMap().get(entity.getId()).forEach(e -> {
						final AuthorizationResource resource = model.getTempResourceIdMap().get(e.getMemberEntityId());
						if(resource != null) {
							final OrgResourceXref xref = new OrgResourceXref();
							xref.setOrganization(organization);
							xref.setResource(resource);
							xref.setRights(getAccessRight(model.getResource2OrgRightMap().get(e.getId()), model.getTempAccessRightMap()));
							organization.addResource(xref);
						}
					});
				}
				
				if(CollectionUtils.isNotEmpty(model.getGroup2OrgMap().get(entity.getId()))) {
					model.getGroup2OrgMap().get(entity.getId()).forEach(e -> {
						final AuthorizationGroup group = model.getTempGroupIdMap().get(e.getMemberEntityId());
						if(group != null) {
							final OrgGroupXref xref = new OrgGroupXref();
							xref.setOrganization(organization);
							xref.setGroup(group);
							xref.setRights(getAccessRight(model.getGroup2OrgRightMap().get(e.getId()), model.getTempAccessRightMap()));
							organization.addGroup(xref);
						}
					});
				}
				
				if(CollectionUtils.isNotEmpty(model.getRole2OrgMap().get(entity.getId()))) {
					model.getRole2OrgMap().get(entity.getId()).forEach(e -> {
						final AuthorizationRole role = model.getTempRoleIdMap().get(e.getMemberEntityId());
						if(role != null) {
							final OrgRoleXref xref = new OrgRoleXref();
							xref.setOrganization(organization);
							xref.setRole(role);
							xref.setRights(getAccessRight(model.getRole2OrgRightMap().get(e.getId()), model.getTempAccessRightMap()));
							organization.addRole(xref);
						}
					});
				}
				
				if(CollectionUtils.isNotEmpty(model.getOrg2Org2Map().get(entity.getId()))) {
					model.getOrg2Org2Map().get(entity.getId()).forEach(e -> {
						final AuthorizationOrganization child = model.getTempOrganizationIdMap().get(e.getMemberEntityId());
						if(child != null) {
							final OrgOrgXref xref = new OrgOrgXref();
							xref.setOrganization(organization);
							xref.setMemberOrganization(child);
							xref.setRights(getAccessRight(model.getOrg2OrgRightMap().get(e.getId()), model.getTempAccessRightMap()));
							child.addParentOrganization(xref);
						}
					});
				}
			}
		});
	}
}
