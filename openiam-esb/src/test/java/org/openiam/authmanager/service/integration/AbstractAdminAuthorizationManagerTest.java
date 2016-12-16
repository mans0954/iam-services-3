package org.openiam.authmanager.service.integration;

import java.util.Date;
import java.util.Set;

import javax.jws.WebParam;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.dto.Role;

public abstract class AbstractAdminAuthorizationManagerTest extends AbstractAuthorizationManagerValidator {

	@Override
	protected Response doAddUserToResource(final String resourceId, final String userId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate) {
		return resourceDataService.addUserToResource(resourceId, userId, requestorId, rightIds, startDate, endDate);
	}
	
	@Override
	protected Response doRemoveUserFromResource(final String resourceId, final String userId, final String requestorId) {
		return resourceDataService.removeUserFromResource(resourceId, userId, requestorId);
	}
	
	@Override
	protected Response doDeleteResource(final Resource resource, final String requestorId) {
		return resourceDataService.deleteResource(resource.getId(), requestorId);
	}
	
	@Override
	protected Response doAddGroupToResource(final String resourceId, final String groupId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate) {
		return resourceDataService.addGroupToResource(resourceId, groupId, requestorId, rightIds, startDate, endDate);
	}
	
	@Override
	protected Response doAddRoleToResource(final String resourceId, final String roleId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate) {
		return resourceDataService.addRoleToResource(resourceId, roleId, requestorId, rightIds, startDate, endDate);
	}
	
	@Override
	protected Response doAddUserToRole(final String roleId, final String userId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate) {
		return roleServiceClient.addUserToRole(roleId, userId, rightIds, startDate, endDate);
	}
	
	@Override
	protected Response doAddChildRole(final String roleId, final String childRoleId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate) {
		return roleServiceClient.addChildRole(roleId, childRoleId, rightIds, startDate, endDate);
	}
	
	@Override
	protected Response doRemoveUserFromRole(final String roleId, final String userId, final String requestorId) {
		return roleServiceClient.removeUserFromRole(roleId, userId);
	}
	
	@Override
	protected Response doAddUserToGroup(final String groupId, final String userId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate) {
		return groupServiceClient.addUserToGroup(groupId, userId, requestorId, rightIds, startDate, endDate);
	}
	
	@Override
	protected Response doRemoveUserFromGroup(final String groupId, final String userId, final String requestorId) {
		return groupServiceClient.removeUserFromGroup(groupId, userId, requestorId);
	}
	
	@Override
	protected Response doAddChildGroup(final String groupId, final String childGroupId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate) {
		return groupServiceClient.addChildGroup(groupId, childGroupId, requestorId, rightIds, startDate, endDate);
	}
	
	@Override
	protected Response doAddChildResource(final String resourceId, final String childResourceId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate) {
		return resourceDataService.addChildResource(resourceId, childResourceId, requestorId, rightIds, startDate, endDate);
	}
	
	@Override
	protected Response doDeleteGroup(final Group group, final String requestorId) {
		return groupServiceClient.deleteGroup(group.getId(), requestorId);
	}
	
	@Override
	protected Response doAddGroupToRole(final String roleId, final String groupId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate) {
		return roleServiceClient.addGroupToRole(roleId, groupId, rightIds, startDate, endDate);
	}
	
	@Override
	protected Response doRemoveGroupFromRole(final String roleId, final String groupId, final String requestorId) {
		return roleServiceClient.removeGroupFromRole(roleId, groupId);
	}
	
	@Override
	protected Response doAddUserToOrg(final String organizationId, final String userId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate) {
		return organizationServiceClient.addUserToOrg(organizationId, userId, requestorId, rightIds, startDate, endDate);
	}
	
	@Override
	protected Response doRemoveUserFromOrg(final String organizationId, final String userId, final String requestorId) {
		return organizationServiceClient.removeUserFromOrg(organizationId, userId, requestorId);
	}
	
	@Override
	protected Response doRemoveRoleToResource(final String resourceId, final String roleId, final String requestorId) {
		return resourceDataService.removeRoleToResource(resourceId, roleId, requestorId);
	}
	
	@Override
	protected Response doRemoveGroupToResource(final String resourceId, final String groupId, final String requestorId) {
		return resourceDataService.removeGroupToResource(resourceId, groupId, requestorId);
	}
	
	@Override
	protected Response doRemoveRole(final Role role, final String requestorId) {
		return roleServiceClient.removeRole(role.getId());
	}
	
	@Override
	protected Response doRemoveOrganization(final Organization organization, final String requestorId) {
		return organizationServiceClient.deleteOrganization(organization.getId(), requestorId);
	}	
	
}
