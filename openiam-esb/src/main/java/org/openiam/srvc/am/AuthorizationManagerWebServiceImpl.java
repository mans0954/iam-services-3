package org.openiam.srvc.am;

import java.util.Collections;
import java.util.Set;

import javax.jws.WebService;

import org.openiam.am.srvc.dto.jdbc.GroupAuthorizationRight;
import org.openiam.am.srvc.dto.jdbc.OrganizationAuthorizationRight;
import org.openiam.am.srvc.dto.jdbc.ResourceAuthorizationRight;
import org.openiam.am.srvc.dto.jdbc.RoleAuthorizationRight;
import org.openiam.base.request.EmptyServiceRequest;
import org.openiam.base.request.GetEntitlementRequest;
import org.openiam.base.response.*;
import org.openiam.base.response.data.BooleanResponse;
import org.openiam.mq.constants.api.AMCacheAPI;
import org.openiam.mq.constants.api.AMManagerAPI;
import org.openiam.mq.constants.queue.am.AMCacheQueue;
import org.openiam.mq.constants.queue.am.AMManagerQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@WebService(endpointInterface = "org.openiam.srvc.am.AuthorizationManagerWebService",
			targetNamespace = "urn:idm.openiam.org/srvc/authorizationmanager/service", 
			portName = "AuthorizationManagerWebServicePort",
			serviceName = "AuthorizationManagerWebService")
@Service("authorizationManagerWebService")
public class AuthorizationManagerWebServiceImpl extends AbstractApiService implements AuthorizationManagerWebService {
	@Autowired
	private AMCacheQueue amCacheQueue;

	@Autowired
	public AuthorizationManagerWebServiceImpl(AMManagerQueue queue) {
		super(queue);
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
	}

	@Override
	public boolean isMemberOfGroup(final String userId, final String groupId) {
		GetEntitlementRequest  request = new GetEntitlementRequest();
		request.setUserId(userId);
		request.setTargetObjectId(groupId);

		return getBooleanResponse(AMManagerAPI.IsMemberOfGroup, request);
	}

	@Override
	public boolean isMemberOfRole(final String userId, final String roleId) {
		GetEntitlementRequest  request = new GetEntitlementRequest();
		request.setUserId(userId);
		request.setTargetObjectId(roleId);

		return getBooleanResponse(AMManagerAPI.IsMemberOfRole, request);
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
	}
	

	@Override
	public void refreshCache() {
		this.publish(amCacheQueue, AMCacheAPI.RefreshAMManager, new EmptyServiceRequest());
	}

	@Override
	public boolean isMemberOfOrganization(String userId, String organizationId) {
		GetEntitlementRequest  request = new GetEntitlementRequest();
		request.setUserId(userId);
		request.setTargetObjectId(organizationId);

		return getBooleanResponse(AMManagerAPI.IsMemberOfOrganization, request);
	}

	@Override
	public boolean isUserEntitledToResourceWithRight(String userId, String resourceId, String rightId) {
		GetEntitlementRequest  request = new GetEntitlementRequest();
		request.setUserId(userId);
		request.setTargetObjectId(resourceId);
		request.setRightId(rightId);
		return getBooleanResponse(AMManagerAPI.IsUserEntitledToResourceWithRight, request);
	}

	@Override
	public boolean isMemberOfGroupWithRight(String userId, String groupId, String rightId) {
		GetEntitlementRequest  request = new GetEntitlementRequest();
		request.setUserId(userId);
		request.setTargetObjectId(groupId);
		request.setRightId(rightId);
		return getBooleanResponse(AMManagerAPI.IsMemberOfGroupWithRight, request);
	}

	@Override
	public boolean isMemberOfRoleWithRight(String userId, String roleId, String rightId) {
		GetEntitlementRequest  request = new GetEntitlementRequest();
		request.setUserId(userId);
		request.setTargetObjectId(roleId);
		request.setRightId(rightId);
		return getBooleanResponse(AMManagerAPI.IsMemberOfRoleWithRight, request);
	}

	@Override
	public boolean isMemberOfOrganizationWithRight(String userId, String organizationId, String rightId) {
		GetEntitlementRequest  request = new GetEntitlementRequest();
		request.setUserId(userId);
		request.setTargetObjectId(organizationId);
		request.setRightId(rightId);
		return getBooleanResponse(AMManagerAPI.IsMemberOfOrganizationWithRight, request);
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
	}
}
