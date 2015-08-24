package org.openiam.authmanager.service.impl;

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
	
	protected void visitResources(final AuthorizationManagerDataModel model) {
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
	
	protected void visitGroups(final AuthorizationManagerDataModel model) {
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
	
	protected void visitRoles(final AuthorizationManagerDataModel model) {
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
	
	protected void visitOrganizations(final AuthorizationManagerDataModel model) {
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
}
