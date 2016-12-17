package org.openiam.authmanager.service.integration;

import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.access.dto.AccessRight;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.srvc.idm.ProvisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ProvisioningAuthorizationManagerWebServiceTest extends AbstractAuthorizationManagerValidator {

    @Autowired
    @Qualifier("provisionServiceClient")
    private ProvisionService provisionService;
	
	@BeforeClass
	public void _init() {
		super._init();
		refreshAuthorizationManager();
	}
	
	@Override
	protected void checkUserURLEntitlements(String userId, String url) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean loginAfterUserCreation() {
		return true;
	}
	
	private Set<AccessRight> getRights(final Set<String> rightIds) {
		if(rightIds != null) {
			return rightIds.stream().map(e -> {
				final AccessRight right = new AccessRight();
				right.setId(e);
				return right;
			}).collect(Collectors.toSet());
		}
		return Collections.EMPTY_SET;
	}

	@Override
	protected Response doAddUserToResource(String resourceId, String userId,
			String requestorId, Set<String> rightIds, Date startDate,
			Date endDate) {
		final ProvisionUser pUser = new ProvisionUser(user);
		final Resource resource = new Resource();
		resource.setId(resourceId);
		pUser.addResourceWithRights(resource, getRights(rightIds), startDate, endDate);
		final Response response = provisionService.modifyUser(pUser);
		sleep(10);
		return response;
	}

	@Override
	protected Response doRemoveUserFromResource(String resourceId,
			String userId, String requestorId) {
		final ProvisionUser pUser = new ProvisionUser(user);
		pUser.removeResource(resourceId);
		final Response response = provisionService.modifyUser(pUser);
		sleep(10);
		return response;
	}
	
	@Override
	protected Response doAddUserToRole(String roleId, String userId,
			String requestorId, Set<String> rightIds, Date startDate,
			Date endDate) {
		final ProvisionUser pUser = new ProvisionUser(user);
		final Role role = new Role();
		role.setId(roleId);
		pUser.addRoleWithRights(role, getRights(rightIds), startDate, endDate);
		final Response response = provisionService.modifyUser(pUser);
		sleep(10);
		return response;
	}


	@Override
	protected Response doRemoveUserFromRole(String roleId, String userId,
			String requestorId) {
		final ProvisionUser pUser = new ProvisionUser(user);
		pUser.removeRole(roleId);
		final Response response = provisionService.modifyUser(pUser);
		sleep(10);
		return response;
	}

	@Override
	protected Response doAddUserToGroup(String groupId, String userId,
			String requestorId, Set<String> rightIds, Date startDate,
			Date endDate) {
		final ProvisionUser pUser = new ProvisionUser(user);
		final Group group = new Group();
		group.setId(groupId);
		pUser.addGroupWithRights(group, getRights(rightIds), startDate, endDate);
		final Response response = provisionService.modifyUser(pUser);
		sleep(10);
		return response;
	}

	@Override
	protected Response doRemoveUserFromGroup(String groupId, String userId,
			String requestorId) {
		final ProvisionUser pUser = new ProvisionUser(user);
		pUser.removeGroup(groupId);
		final Response response = provisionService.modifyUser(pUser);
		sleep(10);
		return response;
	}
	
	@Override
	protected Response doAddUserToOrg(String organizationId, String userId,
			String requestorId, Set<String> rightIds, Date startDate,
			Date endDate) {
		final ProvisionUser pUser = new ProvisionUser(user);
		final Organization organization = new Organization();
		organization.setId(organizationId);
		pUser.addAffiliation(organization, rightIds, startDate, endDate);
		final Response response = provisionService.modifyUser(pUser);
		sleep(10);
		return response;
	}

	@Override
	protected Response doRemoveUserFromOrg(String organizationId,
			String userId, String requestorId) {
		final ProvisionUser pUser = new ProvisionUser(user);
		pUser.removeAffiliation(organizationId);
		final Response response = provisionService.modifyUser(pUser);
		sleep(10);
		return response;
	}

	@Override
	protected Response doDeleteResource(Resource resource, String requestorId) {
		return resourceDataService.deleteResource(resource.getId(), requestorId);
	}

	@Override
	protected Response doAddGroupToResource(String resourceId, String groupId,
			String requestorId, Set<String> rightIds, Date startDate,
			Date endDate) {
		return resourceDataService.addGroupToResource(resourceId, groupId, requestorId, rightIds, startDate, endDate);
	}

	@Override
	protected Response doAddRoleToResource(String resourceId, String roleId,
			String requestorId, Set<String> rightIds, Date startDate,
			Date endDate) {
		return resourceDataService.addRoleToResource(resourceId, roleId, requestorId, rightIds, startDate, endDate);
	}

	@Override
	protected Response doAddChildRole(String roleId, String childRoleId,
			String requestorId, Set<String> rightIds, Date startDate,
			Date endDate) {
		return roleServiceClient.addChildRole(roleId, childRoleId, rightIds, startDate, endDate);
	}

	@Override
	protected Response doAddChildGroup(String groupId, String childGroupId,
			String requestorId, Set<String> rightIds, Date startDate,
			Date endDate) {
		return groupServiceClient.addChildGroup(groupId, childGroupId, rightIds, startDate, endDate);
	}

	@Override
	protected Response doAddChildResource(String resourceId,
			String childResourceId, String requestorId, Set<String> rightIds,
			Date startDate, Date endDate) {
		return resourceDataService.addChildResource(resourceId, childResourceId, requestorId, rightIds, startDate, endDate);
	}

	@Override
	protected Response doDeleteGroup(Group group, String requestorId) {
		return groupServiceClient.deleteGroup(group.getId());
	}

	@Override
	protected Response doAddGroupToRole(String roleId, String groupId,
			String requestorId, Set<String> rightIds, Date startDate,
			Date endDate) {
		return roleServiceClient.addGroupToRole(roleId, groupId, rightIds, startDate, endDate);
	}

	@Override
	protected Response doRemoveGroupFromRole(String roleId, String groupId,
			String requestorId) {
		return roleServiceClient.removeGroupFromRole(roleId, groupId);
	}

	@Override
	protected Response doRemoveRoleToResource(String resourceId, String roleId,
			String requestorId) {
		return resourceDataService.removeRoleToResource(resourceId, roleId, requestorId);
	}

	@Override
	protected Response doRemoveGroupToResource(String resourceId,
			String groupId, String requestorId) {
		return resourceDataService.removeGroupToResource(resourceId, groupId, requestorId);
	}

	@Override
	protected Response doRemoveRole(Role role, String requestorId) {
		return roleServiceClient.removeRole(role.getId());
	}

	@Override
	protected Response doRemoveOrganization(Organization organization,
			String requestorId) {
		return organizationServiceClient.deleteOrganization(organization.getId(), requestorId);
	}

	@Test
	public void foo() {}
}
