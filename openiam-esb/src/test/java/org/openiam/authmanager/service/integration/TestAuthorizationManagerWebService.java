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
import org.openiam.authmanager.ws.request.UserRequest;
import org.openiam.authmanager.ws.request.UserToGroupAccessRequest;
import org.openiam.authmanager.ws.request.UserToResourceAccessRequest;
import org.openiam.authmanager.ws.request.UserToRoleAccessRequest;
import org.openiam.authmanager.ws.response.AccessResponse;
import org.openiam.authmanager.ws.response.ResourcesForUserResponse;
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
public class TestAuthorizationManagerWebService implements InitializingBean {

	private static final Log log = LogFactory.getLog(TestAuthorizationManagerWebService.class);
	
	@Autowired
	@Qualifier("jdbcTemplate")
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	@Qualifier("authorizationManagerServiceClient")
	private AuthorizationManagerWebService authorizationManagerServiceClient;
	
	@Autowired
	@Qualifier("userServiceClient")
	private UserDataWebService userDataWebService;
	
	@Autowired
	@Qualifier("roleServiceClient")
	private RoleDataWebService roleDataWebService;
	
	@Autowired
	@Qualifier("groupServiceClient")
	private GroupDataWebService groupServiceClient;
	
	@Autowired
	@Qualifier("resourceServiceClient")
	private ResourceDataService resourceServiceClient;
	
	@Autowired
	@Qualifier("managedSysServiceClient")
	private ManagedSystemDataService managedSysServiceClient;
	
	/*
	@Autowired
	@Qualifier("loginServiceFactory")
	private LoginDataWebService loginServiceFactory;
	*/
	
	private static final String TEST_USER_ID = "3000";
	
	@Test
	public void isUserEntitledToResource() {
		final List<Map<String, Object>> resourceUserMap = jdbcTemplate.queryForList("SELECT RESOURCE_ID AS RESOURCE_ID, USER_ID AS USER_ID FROM RESOURCE_USER");
		checkResourceMembership(resourceUserMap);
		
		final List<Map<String, Object>> resourceUserMapThroughGroupMembership = jdbcTemplate.queryForList("SELECT ug.USER_ID AS USER_ID, rg.RESOURCE_ID AS RESOURCE_ID FROM USER_GRP ug, RESOURCE_GROUP rg WHERE ug.GRP_ID=rg.GRP_ID");
		checkResourceMembership(resourceUserMapThroughGroupMembership);
		
		final List<Map<String, Object>> resourceUserMapThroughRoleMembership = jdbcTemplate.queryForList("SELECT ur.USER_ID AS USER_ID, rr.RESOURCE_ID AS RESOURCE_ID FROM USER_ROLE ur, RESOURCE_ROLE rr WHERE ur.ROLE_ID=rr.ROLE_ID");
		checkResourceMembership(resourceUserMapThroughRoleMembership);
	}
	
	@Test
	public void isUserMemberOfGroups() {
		final List<Map<String, Object>> groupUserMap = jdbcTemplate.queryForList("SELECT GRP_ID AS GROUP_ID, USER_ID AS USER_ID FROM USER_GRP");
		checkGroupMembership(groupUserMap);
	}
	
	@Test
	public void isUserMemberOfRoles() {
		final List<Map<String, Object>> roleUserMap = jdbcTemplate.queryForList("SELECT USER_ID AS USER_ID, ROLE_ID AS ROLE_ID FROM USER_ROLE");
		checkRoleMembership(roleUserMap);
		
		final List<Map<String, Object>> roleUserMapThroughGroupMembership = jdbcTemplate.queryForList("SELECT ug.USER_ID AS USER_ID, gr.ROLE_ID AS ROLE_ID FROM USER_GRP ug, GRP_ROLE gr WHERE ug.GRP_ID=gr.GRP_ID");
		checkRoleMembership(roleUserMapThroughGroupMembership);
	}
	
	/* really hard to write a test for this without killing your database */
	@Test
	public void testGetResourcesForUser() {
		/*
		final List<String> userIds = jdbcTemplate.queryForList("SELECT USER_ID FROM USERS", String.class);
		for(final String userId : userIds) {
			final List<Map<String, Object>> resourceListMap = new LinkedList<Map<String,Object>>();
			resourceListMap.addAll(jdbcTemplate.queryForList("SELECT RESOURCE_ID AS RESOURCE_ID, USER_ID AS USER_ID FROM RESOURCE_USER WHERE USER_ID=?", userId));
			resourceListMap.addAll(jdbcTemplate.queryForList("SELECT ug.USER_ID AS USER_ID, rg.RESOURCE_ID AS RESOURCE_ID FROM USER_GRP ug, RESOURCE_GROUP rg WHERE ug.GRP_ID=rg.GRP_ID AND ug.USER_ID=?", userId));
			resourceListMap.addAll(jdbcTemplate.queryForList("SELECT ur.USER_ID AS USER_ID, rr.RESOURCE_ID AS RESOURCE_ID FROM USER_ROLE ur, RESOURCE_ROLE rr WHERE ur.ROLE_ID=rr.ROLE_ID AND ur.USER_ID=?", userId));
		
			final Set<String> resourceIdSet = new HashSet<String>();
			for(final Map<String, Object> map : resourceListMap) {
				resourceIdSet.add((String)map.get("RESOURCE_ID"));
			}
		
			visitParentResources(resourceIdSet);
			
			final User user = userDataWebService.getUserWithDependent(userId, true).getUser();
			final List<AuthorizationManagerLoginId> loginIdList = getLoginIdList(user);
			
			confirmUserResources(user.getUserId(), null, resourceIdSet);
			for(final AuthorizationManagerLoginId loginId : loginIdList) {
				confirmUserResources(null, loginId, resourceIdSet);
			}
		}
		*/
	}
	
	@Test
	public void testGetGroupsFor() {
		
	}
	
	@Test
	public void testGetRolesFor() {
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info(jdbcTemplate);
	}
	
	private void confirmUserResources(final String userId, final AuthorizationManagerLoginId loginId, final Set<String> resourceIds) {
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
	
	private void visitParentRoles(final String roleId, final Set<String> visitedEntities) {
		if(!visitedEntities.contains(roleId)) {
			visitedEntities.add(roleId);
			final List<String> parentRoles = jdbcTemplate.queryForList("SELECT ROLE_ID FROM role_to_role_membership WHERE MEMBER_ROLE_ID=?", String.class, roleId);
			if(CollectionUtils.isNotEmpty(parentRoles)) {
				for(final String parentRoleId : parentRoles) {
					visitParentRoles(parentRoleId, visitedEntities);
				}
			}
		}
	}
	
	private void visitParentGroups(final String groupId, final Set<String> visitedEntities) {
		if(!visitedEntities.contains(groupId)) {
			visitedEntities.add(groupId);
			final List<String> parentGroups = jdbcTemplate.queryForList("SELECT GROUP_ID FROM grp_to_grp_membership WHERE MEMBER_GROUP_ID=?", String.class, groupId);
			if(CollectionUtils.isNotEmpty(parentGroups)) {
				for(final String parentGroupId : parentGroups) {
					visitParentGroups(parentGroupId, visitedEntities);
				}
			}
		}
	}
	
	private void visitParentResources(final Set<String> visitedEntities) {
		for(final String entity : visitedEntities) {
			visitParentResources(entity, visitedEntities);
		}
	}
	
	private void visitParentResources(final String resourceId, final Set<String> visitedEntities) {
		if(!visitedEntities.contains(resourceId)) {
			visitedEntities.add(resourceId);
			final List<String> parentResources = jdbcTemplate.queryForList("SELECT RESOURCE_ID FROM res_to_res_membership WHERE MEMBER_RESOURCE_ID=?", String.class, resourceId);
			if(CollectionUtils.isNotEmpty(parentResources)) {
				for(final String parentResourceId : parentResources) {
					visitParentResources(parentResourceId, visitedEntities);
				}
			}
		}
	}
	
	private void checkResourceMembership(final List<Map<String, Object>> resourceUserMap) {
		for(final Map<String, Object> row : resourceUserMap) {
			final String userId = (String)row.get("USER_ID");
			final String resourceId = (String)row.get("RESOURCE_ID");
			
			final Set<String> visitedEntities = new HashSet<String>();
			visitParentResources(resourceId, visitedEntities);
			for(final String id : visitedEntities) {
				checkResourceMembership(userId, id);
			}
		}
	}
	
	private void checkGroupMembership(final List<Map<String, Object>> groupUserMap) {
		for(final Map<String, Object> row : groupUserMap) {
			final String userId = (String)row.get("USER_ID");
			final String groupId = (String)row.get("GROUP_ID");
			
			final Set<String> visitedEntities = new HashSet<String>();
			visitParentGroups(groupId, visitedEntities);
			for(final String id : visitedEntities) {
				checkGroupMembership(userId, id);
			}
		}
	}
	
	private void checkRoleMembership(final List<Map<String, Object>> roleUserMap) {
		for(final Map<String, Object> row : roleUserMap) {
			final String userId = (String)row.get("USER_ID");
			final String roleId = (String)row.get("ROLE_ID");
			
			final Set<String> visitedEntities = new HashSet<String>();
			visitParentRoles(roleId, visitedEntities);
			for(final String id : visitedEntities) {
				checkRoleMembership(userId, id);
			}
		}
	}
	
	private void checkRoleMembership(final String userId, final String roleId) {
		final User user = userDataWebService.getUserWithDependent(userId, true).getUser();
		final Role role = roleDataWebService.getRole(roleId).getRole();
		
		final List<AuthorizationManagerLoginId> loginIdList = getLoginIdList(user);
		
		checkUser2RoleMembership(user.getUserId(), null, role.getRoleId(), null);
		checkUser2RoleMembership(user.getUserId(), null, null, role.getRoleName());
		for(final AuthorizationManagerLoginId loginId : loginIdList) {
			checkUser2RoleMembership(null, loginId, role.getRoleId(), null);
			checkUser2RoleMembership(null, loginId, null, role.getRoleName());
		}
	}
	
	private void checkGroupMembership(final String userId, final String groupId) {
		final User user = userDataWebService.getUserWithDependent(userId, true).getUser();
		final Group group = groupServiceClient.getGroup(groupId).getGroup();
		
		final List<AuthorizationManagerLoginId> loginIdList = getLoginIdList(user);
		
		checkUser2GroupMembership(user.getUserId(), null, group.getGrpId(), null);
		checkUser2GroupMembership(user.getUserId(), null, null, group.getGrpName());
		
		for(final AuthorizationManagerLoginId loginId : loginIdList) {
			checkUser2ResourceEntitlement(null, loginId, group.getGrpId(), null);
			checkUser2ResourceEntitlement(null, loginId, null, group.getGrpName());
		}
	}
	
	private void checkResourceMembership(final String userId, final String resourceId) {
		final Resource resource = resourceServiceClient.getResource(resourceId);
		final User user = userDataWebService.getUserWithDependent(userId, true).getUser();
		
		checkUser2ResourceEntitlement(user.getUserId(), null, resource.getResourceId(), null);
		checkUser2ResourceEntitlement(user.getUserId(), null, null, resource.getName());
		
		final List<AuthorizationManagerLoginId> loginIdList = getLoginIdList(user);
		for(final AuthorizationManagerLoginId loginId : loginIdList) {
			checkUser2ResourceEntitlement(null, loginId, resource.getResourceId(), null);
			checkUser2ResourceEntitlement(null, loginId, null, resource.getName());
		}
	}
	
	private void checkUser2ResourceEntitlement(final String userId, final AuthorizationManagerLoginId loginId, final String resourceId, final String resourceName) {
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
	
	private void checkUser2GroupMembership(final String userId, final AuthorizationManagerLoginId loginId, final String groupId, final String groupName) {
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
	
	private void checkUser2RoleMembership(final String userId, final AuthorizationManagerLoginId loginId, final String roleId, final String roleName) {
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
	
	private List<AuthorizationManagerLoginId> getLoginIdList(final User user) {
		final List<AuthorizationManagerLoginId> loginIdList = new LinkedList<AuthorizationManagerLoginId>();
		if(user != null && CollectionUtils.isNotEmpty(user.getPrincipalList())) {
			for(final Login login : user.getPrincipalList()) {
				final LoginId loginId = login.getId();
				final AuthorizationManagerLoginId authManagerLoginId = new AuthorizationManagerLoginId(loginId.getDomainId(), loginId.getLogin(), loginId.getManagedSysId());
			}
		}
		return loginIdList;
	}
}
