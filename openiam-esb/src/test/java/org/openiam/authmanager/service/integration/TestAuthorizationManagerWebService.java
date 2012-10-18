package org.openiam.authmanager.service.integration;

import java.net.ResponseCache;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.cms.AuthenticatedData;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationManagerLoginId;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.authmanager.service.AuthorizationManagerWebService;
import org.openiam.authmanager.ws.request.URLRequest;
import org.openiam.authmanager.ws.request.UserRequest;
import org.openiam.authmanager.ws.request.UserToGroupAccessRequest;
import org.openiam.authmanager.ws.request.UserToResourceAccessRequest;
import org.openiam.authmanager.ws.request.UserToRoleAccessRequest;
import org.openiam.authmanager.ws.response.AccessResponse;
import org.openiam.authmanager.ws.response.GroupsForUserResponse;
import org.openiam.authmanager.ws.response.ResourcesForUserResponse;
import org.openiam.authmanager.ws.response.RolesForUserResponse;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.LoginId;
import org.openiam.idm.srvc.auth.ws.LoginDataWebService;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.grp.ws.GroupListResponse;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemDataService;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.ws.RoleDataWebService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.idm.srvc.user.ws.UserResponse;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gdata.client.appsforyourdomain.UserService;
import com.sun.mail.iap.Response;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:test-integration-environment.xml","classpath:test-esb-integration.xml"})
public class TestAuthorizationManagerWebService extends AbstractAuthorizationManagerTest {

	private static final Log log = LogFactory.getLog(TestAuthorizationManagerWebService.class);
	
	@Autowired
	@Qualifier("authorizationManagerServiceClient")
	private AuthorizationManagerWebService authorizationManagerServiceClient;
	
	@Override
	protected void checkUserURLEntitlements(final String userId, final AuthorizationManagerLoginId loginId, final String url) {
		final URLRequest request = new URLRequest();
		request.setUserId(userId);
		request.setLoginId(loginId);
		request.setUrl(url);
		
		final AccessResponse response = authorizationManagerServiceClient.isUserEntitledToURL(request);
		Assert.assertTrue(ResponseStatus.SUCCESS.equals(response.getResponseStatus()));
	}
	
	@Override
	protected void confirmUserRoles(final String userId, final AuthorizationManagerLoginId loginId, final Set<String> roleIds) {
		final UserRequest request = new UserRequest();
		if(userId != null) {
			request.setUserId(userId);
		} else {
			request.setLoginId(loginId);
		}
		
		final RolesForUserResponse response = authorizationManagerServiceClient.getRolesFor(request);
		Assert.assertEquals(ResponseStatus.SUCCESS, response.getResponseStatus());
		final Set<AuthorizationRole> authorizationRoles = response.getRoles();
		
		final Set<String> resultRoleIds = new HashSet<String>();
		for(final AuthorizationRole role : authorizationRoles) {
			resultRoleIds.add(role.getId());
		}
		
		Assert.assertEquals(String.format("The number of DB groups and groups returned from the WS are not equal for user '%s", (userId != null) ? userId : loginId), 
				CollectionUtils.size(roleIds), CollectionUtils.size(resultRoleIds));
		
		for(final String resourceId : roleIds) {
			Assert.assertTrue(String.format("Groups returned from WS did not contain '%s'", resourceId), resultRoleIds.contains(resourceId));
		}
		
		for(final String resourceId : resultRoleIds) {
			Assert.assertTrue(String.format("Groups returned from DB did not contain '%s'", resourceId), roleIds.contains(resourceId));
		}
	}
	
	@Override
	protected void confirmUserGroups(final String userId, final AuthorizationManagerLoginId loginId, final Set<String> groupIds) {
		final UserRequest request = new UserRequest();
		if(userId != null) {
			request.setUserId(userId);
		} else {
			request.setLoginId(loginId);
		}
		
		final GroupsForUserResponse response = authorizationManagerServiceClient.getGroupsFor(request);
		Assert.assertEquals(ResponseStatus.SUCCESS, response.getResponseStatus());
		final Set<AuthorizationGroup> authorizationGroups = response.getGroups();
		
		final Set<String> resultGroupIds = new HashSet<String>();
		for(final AuthorizationGroup group : authorizationGroups) {
			resultGroupIds.add(group.getId());
		}
		
		Assert.assertEquals(String.format("The number of DB groups and groups returned from the WS are not equal for user '%s", (userId != null) ? userId : loginId), 
				CollectionUtils.size(groupIds), CollectionUtils.size(resultGroupIds));
		
		for(final String resourceId : groupIds) {
			Assert.assertTrue(String.format("Groups returned from WS did not contain '%s'", resourceId), resultGroupIds.contains(resourceId));
		}
		
		for(final String resourceId : resultGroupIds) {
			Assert.assertTrue(String.format("Groups returned from DB did not contain '%s'", resourceId), groupIds.contains(resourceId));
		}
	}
	
	@Override
	protected void confirmUserResources(final String userId, final AuthorizationManagerLoginId loginId, final Set<String> resourceIds) {
		final UserRequest request = new UserRequest();
		if(userId != null) {
			request.setUserId(userId);
		} else {
			request.setLoginId(loginId);
		}
		
		final ResourcesForUserResponse response = authorizationManagerServiceClient.getResourcesFor(request);
		Assert.assertEquals(ResponseStatus.SUCCESS, response.getResponseStatus());
		final Set<AuthorizationResource> authorizationResources =  response.getResources();
		
		final Set<String> resultResourceIds = new HashSet<String>();
		for(final AuthorizationResource resource : authorizationResources) {
			resultResourceIds.add(resource.getId());
		}
		
		Assert.assertEquals(String.format("The number of DB resoruces and resources returned from the WS are not equal for user '%s", (userId != null) ? userId : loginId), 
				CollectionUtils.size(resourceIds), CollectionUtils.size(resultResourceIds));
		
		for(final String resourceId : resourceIds) {
			Assert.assertTrue(String.format("Resources returned from WS did not contain '%s'", resourceId), resultResourceIds.contains(resourceId));
		}
		
		for(final String resourceId : resultResourceIds) {
			Assert.assertTrue(String.format("Resources returned from DB did not contain '%s'", resourceId), resourceIds.contains(resourceId));
		}
	}
	
	@Override
	protected void checkUser2ResourceEntitlement(final String userId, final AuthorizationManagerLoginId loginId, final String resourceId, final String resourceName) {
		final UserToResourceAccessRequest request = new UserToResourceAccessRequest();
		
		final AuthorizationResource resource = new AuthorizationResource();
		if(resourceId != null) {
			resource.setId(resourceId);
		} else {
			resource.setName(resourceName);
		}
		
		if(userId != null) {
			request.setUserId(userId);
		} else {
			request.setLoginId(loginId);
		}
		request.setResource(resource);
		
		final AccessResponse response = authorizationManagerServiceClient.isUserEntitledTo(request);
		Assert.assertEquals(ResponseStatus.SUCCESS, response.getResponseStatus());
		final boolean result = response.getResult();
		String failMessage = String.format("User is not entitled to resource.  %s", request);
		Assert.assertTrue(failMessage, result);
	}
	
	@Override
	protected void checkUser2GroupMembership(final String userId, final AuthorizationManagerLoginId loginId, final String groupId, final String groupName) {
		final UserToGroupAccessRequest request = new UserToGroupAccessRequest();
		
		final AuthorizationGroup group = new AuthorizationGroup();
		if(groupId != null) {
			group.setId(groupId);
		} else {
			group.setName(groupName);
		}
		
		if(userId != null) {
			request.setUserId(userId);
		} else {
			request.setLoginId(loginId);
		}
		request.setGroup(group);
		
		final AccessResponse response = authorizationManagerServiceClient.isMemberOfGroup(request);
		final boolean result = response.getResult();
		Assert.assertEquals(ResponseStatus.SUCCESS, response.getResponseStatus());
		final String failMessage = String.format("User not part of group: %s", request);
		Assert.assertTrue(failMessage, result);
	}
	
	@Override
	protected void checkUser2RoleMembership(final String userId, final AuthorizationManagerLoginId loginId, final String roleId, final String roleName) {
		final UserToRoleAccessRequest request = new UserToRoleAccessRequest();
		
		final AuthorizationRole role = new AuthorizationRole();
		if(roleId != null) {
			role.setId(roleId);
		} else {
			role.setName(roleName);
		}
		
		if(userId != null) {
			request.setUserId(userId);
		} else {
			request.setLoginId(loginId);
		}
		request.setRole(role);
		
		final AccessResponse response = authorizationManagerServiceClient.isMemberOfRole(request);
		final boolean result = response.getResult();
		Assert.assertEquals(ResponseStatus.SUCCESS, response.getResponseStatus());
		final String failMessage = String.format("User not part of role: %s", request);
		Assert.assertTrue(failMessage, result);
	}
}
