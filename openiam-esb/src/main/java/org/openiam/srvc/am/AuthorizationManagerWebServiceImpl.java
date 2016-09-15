package org.openiam.srvc.am;

import java.util.Collections;
import java.util.Set;

import javax.jws.WebService;

import org.openiam.am.srvc.dto.jdbc.GroupAuthorizationRight;
import org.openiam.am.srvc.dto.jdbc.OrganizationAuthorizationRight;
import org.openiam.am.srvc.dto.jdbc.ResourceAuthorizationRight;
import org.openiam.am.srvc.dto.jdbc.RoleAuthorizationRight;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.request.GetEntitlementRequest;
import org.openiam.base.response.*;
import org.openiam.mq.constants.AMManagerAPI;
import org.openiam.mq.constants.AMMenuAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.srvc.AbstractApiService;
import org.openiam.thread.Sweepable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@WebService(endpointInterface = "org.openiam.srvc.am.AuthorizationManagerWebService",
			targetNamespace = "urn:idm.openiam.org/srvc/authorizationmanager/service", 
			portName = "AuthorizationManagerWebServicePort",
			serviceName = "AuthorizationManagerWebService")
@Service("authorizationManagerWebService")
public class AuthorizationManagerWebServiceImpl extends AbstractApiService implements AuthorizationManagerWebService {

	@Autowired
	private AuthorizationManagerService authManagerService;

	public AuthorizationManagerWebServiceImpl() {
		super(OpenIAMQueue.AMManagerQueue);
	}

	private boolean getBooleanResponse(AMManagerAPI apiName, GetEntitlementRequest  request){
		BooleanResponse response= this.manageApiRequest(apiName, request, BooleanResponse.class);
		if(response.isFailure()){
			return false;
		}
		return response.getValue();
	}

	@Override
	public boolean isUserEntitledToResource(String userId, String resourceId) {
		GetEntitlementRequest  request = new GetEntitlementRequest();
		request.setUserId(userId);
		request.setTargetObjectId(resourceId);

		return getBooleanResponse(AMManagerAPI.IsUserEntitledToResource, request);

//		return authManagerService.isEntitled(userId, resourceId);
	}

	@Override
	public boolean isMemberOfGroup(final String userId, final String groupId) {
		GetEntitlementRequest  request = new GetEntitlementRequest();
		request.setUserId(userId);
		request.setTargetObjectId(groupId);

		return getBooleanResponse(AMManagerAPI.IsMemberOfGroup, request);

//		return authManagerService.isMemberOfGroup(userId, groupId);
	}

	@Override
	public boolean isMemberOfRole(final String userId, final String roleId) {
		GetEntitlementRequest  request = new GetEntitlementRequest();
		request.setUserId(userId);
		request.setTargetObjectId(roleId);

		return getBooleanResponse(AMManagerAPI.IsMemberOfRole, request);

//		return authManagerService.isMemberOfRole(userId, roleId);
	}

	@Override
	public Set<ResourceAuthorizationRight> getResourcesForUser(final String userId) {
		GetEntitlementRequest  request = new GetEntitlementRequest();
		request.setUserId(userId);
		ResourceAuthorizationRightSetResponse response= this.manageApiRequest(AMManagerAPI.GetResourcesForUser, request, ResourceAuthorizationRightSetResponse.class);
		if(response.isFailure()){
			return Collections.emptySet();
		}
		return response.getResourceSet();

//		return authManagerService.getResourcesForUser(userId);
	}

	@Override
	public Set<GroupAuthorizationRight> getGroupsForUser(final String userId) {
		GetEntitlementRequest  request = new GetEntitlementRequest();
		request.setUserId(userId);
		GroupAuthorizationRightSetResponse response= this.manageApiRequest(AMManagerAPI.GetGroupsForUser, request, GroupAuthorizationRightSetResponse.class);
		if(response.isFailure()){
			return Collections.emptySet();
		}
		return response.getGroupSet();

//		return authManagerService.getGroupsForUser(userId);
	}

	@Override
	public Set<RoleAuthorizationRight> getRolesForUser(final String userId) {
		GetEntitlementRequest  request = new GetEntitlementRequest();
		request.setUserId(userId);
		RoleAuthorizationRightSetResponse response= this.manageApiRequest(AMManagerAPI.GetRolesForUser, request, RoleAuthorizationRightSetResponse.class);
		if(response.isFailure()){
			return Collections.emptySet();
		}
		return response.getRoleSet();

//		return authManagerService.getRolesForUser(userId);
	}
	

	@Override
	public void refreshCache() {
		this.sendAsync(AMManagerAPI.RefreshCache, new BaseServiceRequest());
//		((Sweepable)authManagerService).sweep();
	}

	@Override
	public boolean isMemberOfOrganization(String userId, String organizationId) {
		GetEntitlementRequest  request = new GetEntitlementRequest();
		request.setUserId(userId);
		request.setTargetObjectId(organizationId);

		return getBooleanResponse(AMManagerAPI.IsMemberOfOrganization, request);

//		return authManagerService.isMemberOfOrganization(userId, organizationId);
	}

	@Override
	public boolean isUserEntitledToResourceWithRight(String userId, String resourceId, String rightId) {
		GetEntitlementRequest  request = new GetEntitlementRequest();
		request.setUserId(userId);
		request.setTargetObjectId(resourceId);
		request.setRightId(rightId);
		return getBooleanResponse(AMManagerAPI.IsUserEntitledToResourceWithRight, request);

//		return authManagerService.isEntitled(userId, resourceId, rightId);
	}

	@Override
	public boolean isMemberOfGroupWithRight(String userId, String groupId, String rightId) {
		GetEntitlementRequest  request = new GetEntitlementRequest();
		request.setUserId(userId);
		request.setTargetObjectId(groupId);
		request.setRightId(rightId);
		return getBooleanResponse(AMManagerAPI.IsMemberOfGroupWithRight, request);

//		return authManagerService.isMemberOfGroup(userId, groupId, rightId);
	}

	@Override
	public boolean isMemberOfRoleWithRight(String userId, String roleId, String rightId) {
		GetEntitlementRequest  request = new GetEntitlementRequest();
		request.setUserId(userId);
		request.setTargetObjectId(roleId);
		request.setRightId(rightId);
		return getBooleanResponse(AMManagerAPI.IsMemberOfRoleWithRight, request);

//		return authManagerService.isMemberOfRole(userId, roleId, rightId);
	}

	@Override
	public boolean isMemberOfOrganizationWithRight(String userId, String organizationId, String rightId) {
		GetEntitlementRequest  request = new GetEntitlementRequest();
		request.setUserId(userId);
		request.setTargetObjectId(organizationId);
		request.setRightId(rightId);
		return getBooleanResponse(AMManagerAPI.IsMemberOfOrganizationWithRight, request);

//		return authManagerService.isMemberOfOrganization(userId, organizationId, rightId);
	}

	@Override
	public Set<OrganizationAuthorizationRight> getOrganizationsForUser(String userId) {
		GetEntitlementRequest  request = new GetEntitlementRequest();
		request.setUserId(userId);
		OrganizationAuthorizationRightSetResponse response= this.manageApiRequest(AMManagerAPI.GetOrganizationsForUser, request, OrganizationAuthorizationRightSetResponse.class);
		if(response.isFailure()){
			return Collections.emptySet();
		}
		return response.getOrganizationSet();

//		return authManagerService.getOrganizationsForUser(userId);
	}
}
