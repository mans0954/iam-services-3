package org.openiam.srvc.am;

import java.util.Set;

import javax.jws.WebService;

import org.openiam.am.srvc.dto.jdbc.GroupAuthorizationRight;
import org.openiam.am.srvc.dto.jdbc.OrganizationAuthorizationRight;
import org.openiam.am.srvc.dto.jdbc.ResourceAuthorizationRight;
import org.openiam.am.srvc.dto.jdbc.RoleAuthorizationRight;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.thread.Sweepable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@WebService(endpointInterface = "org.openiam.srvc.am.AuthorizationManagerWebService",
			targetNamespace = "urn:idm.openiam.org/srvc/authorizationmanager/service", 
			portName = "AuthorizationManagerWebServicePort",
			serviceName = "AuthorizationManagerWebService")
@Service("authorizationManagerWebService")
public class AuthorizationManagerWebServiceImpl implements AuthorizationManagerWebService {

	@Autowired
	private AuthorizationManagerService authManagerService;
	
	@Override
	public boolean isUserEntitledToResource(String userId, String resourceId) {
		return authManagerService.isEntitled(userId, resourceId);
	}

	@Override
	public boolean isMemberOfGroup(final String userId, final String groupId) {
		return authManagerService.isMemberOfGroup(userId, groupId);
	}

	@Override
	public boolean isMemberOfRole(final String userId, final String roleId) {
		return authManagerService.isMemberOfRole(userId, roleId);
	}

	@Override
	public Set<ResourceAuthorizationRight> getResourcesForUser(final String userId) {
		return authManagerService.getResourcesForUser(userId);
	}

	@Override
	public Set<GroupAuthorizationRight> getGroupsForUser(final String userId) {
		return authManagerService.getGroupsForUser(userId);
	}

	@Override
	public Set<RoleAuthorizationRight> getRolesForUser(final String userId) {
		return authManagerService.getRolesForUser(userId);
	}
	

	@Override
	public void refreshCache() {
		((Sweepable)authManagerService).sweep();
	}

	@Override
	public boolean isMemberOfOrganization(String userId, String organizationId) {
		return authManagerService.isMemberOfOrganization(userId, organizationId);
	}

	@Override
	public boolean isUserEntitledToResourceWithRight(String userId,
			String resourceId, String rightId) {
		return authManagerService.isEntitled(userId, resourceId, rightId);
	}

	@Override
	public boolean isMemberOfGroupWithRight(String userId, String groupId,
			String rightId) {
		return authManagerService.isMemberOfGroup(userId, groupId, rightId);
	}

	@Override
	public boolean isMemberOfRoleWithRight(String userId, String roleId,
			String rightId) {
		return authManagerService.isMemberOfRole(userId, roleId, rightId);
	}

	@Override
	public boolean isMemberOfOrganizationWithRight(String userId,
			String organizationId, String rightId) {
		return authManagerService.isMemberOfOrganization(userId, organizationId, rightId);
	}

	@Override
	public Set<OrganizationAuthorizationRight> getOrganizationsForUser(String userId) {
		return authManagerService.getOrganizationsForUser(userId);
	}
}
