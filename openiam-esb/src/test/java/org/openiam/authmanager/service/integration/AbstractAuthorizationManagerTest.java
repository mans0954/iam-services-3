package org.openiam.authmanager.service.integration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.common.model.AuthorizationManagerLoginId;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.LoginId;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemDataService;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.ws.RoleDataWebService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

public abstract class AbstractAuthorizationManagerTest extends AbstractTestNGSpringContextTests {

	protected static final Log log = LogFactory.getLog(AbstractAuthorizationManagerTest.class);
	
	@Autowired
	@Qualifier("jdbcTemplate")
	protected JdbcTemplate jdbcTemplate;
	
	@Autowired
	@Qualifier("userServiceClient")
	protected UserDataWebService userDataWebService;
	
	@Autowired
	@Qualifier("roleServiceClient")
	protected RoleDataWebService roleDataWebService;
	
	@Autowired
	@Qualifier("groupServiceClient")
	protected GroupDataWebService groupServiceClient;
	
	@Autowired
	@Qualifier("resourceServiceClient")
	protected ResourceDataService resourceServiceClient;
	
	@Autowired
	@Qualifier("managedSysServiceClient")
	protected ManagedSystemDataService managedSysServiceClient;
	
	private static final int MAX_ITERS = 10;
	/*
	private String GET_RESOURCE_DOMAINS_WITH_PATTERNS = "SELECT r.RESOURCE_ID AS RESOURCE_ID, prop.PROP_VALUE AS PATTERN, r.MIN_AUTH_LEVEL AS MIN_AUTH_LEVEL, r.DOMAIN AS DOMAIN, r.IS_PUBLIC AS IS_PUBLIC, r.IS_SSL AS IS_SSL" +
			"	FROM " +
			"		%s.RES r " +
			"		JOIN %s.RESOURCE_PROP prop " +
			"			ON  r.RESOURCE_ID=prop.RESOURCE_ID " +
			"			AND prop.NAME IN('URL_PATTERN')";
	*/
	
	final String[] urlpatterns = new String[] {
		"http://www.google.com/foo/bar.html",
		"http://www.google.com/openiam/selfservice",
		"http://www.google.com/openiam/selfservice.html",
		"http://www.facebook.com/foo/bar.html",
		"http://www.facebook.com/openiam/selfservice",
		"http://www.facebook.com/openiam/selfservice.html",
		"https://www.facebook.com/foo/bar.html",
		"https://www.facebook.com/openiam/selfservice",
		"https://www.facebook.com/openiam/selfservice.html",
		"https://www.google.com/foo/bar.html",
		"https://www.google.com/openiam/selfservice",
		"https://www.google.com/openiam/selfservice.html"
	};
	
	@Test
	public void isUserEntitledToUrlsTest() {
		final List<String> userIds = jdbcTemplate.queryForList("SELECT USER_ID FROM USERS LIMIT " + MAX_ITERS, String.class);
		for(final String userId : userIds) {
			for(final String url : urlpatterns) {
				final User user = userDataWebService.getUserWithDependent(userId, true).getUser();
				
				final List<AuthorizationManagerLoginId> loginIdList = getLoginIdList(user);
				
				checkUserURLEntitlements(userId, null, url);
				for(final AuthorizationManagerLoginId loginId : loginIdList) {
					checkUserURLEntitlements(null, loginId, url);
				}
			}
		}
	}
	
	@Test
	public void isUserEntitledToResourceTest() {
		final List<Map<String, Object>> resourceUserMap = jdbcTemplate.queryForList("SELECT RESOURCE_ID AS RESOURCE_ID, USER_ID AS USER_ID FROM RESOURCE_USER LIMIT " + MAX_ITERS);
		final List<Map<String, Object>> resourceUserMapThroughGroupMembership = jdbcTemplate.queryForList("SELECT ug.USER_ID AS USER_ID, rg.RESOURCE_ID AS RESOURCE_ID FROM USER_GRP ug, RESOURCE_GROUP rg WHERE ug.GRP_ID=rg.GRP_ID LIMIT " + MAX_ITERS);
		final List<Map<String, Object>> resourceUserMapThroughRoleMembership = jdbcTemplate.queryForList("SELECT ur.USER_ID AS USER_ID, rr.RESOURCE_ID AS RESOURCE_ID FROM USER_ROLE ur, RESOURCE_ROLE rr WHERE ur.ROLE_ID=rr.ROLE_ID LIMIT " + MAX_ITERS);
		
		final Map<String, Set<String>> user2ResourceMap = new HashMap<String, Set<String>>();
		for(final Map<String, Object> map : resourceUserMap) {
			final String userId = (String)map.get("USER_ID");
			final String resourceId = (String)map.get("RESOURCE_ID");
			if(!user2ResourceMap.containsKey(userId)) {
				user2ResourceMap.put(userId, new HashSet<String>());
			}
			user2ResourceMap.get(userId).add(resourceId);
		}
		
		for(final Map<String, Object> map : resourceUserMapThroughGroupMembership) {
			final String userId = (String)map.get("USER_ID");
			final String resourceId = (String)map.get("RESOURCE_ID");
			if(!user2ResourceMap.containsKey(userId)) {
				user2ResourceMap.put(userId, new HashSet<String>());
			}
			user2ResourceMap.get(userId).add(resourceId);
		}
		
		for(final Map<String, Object> map : resourceUserMapThroughRoleMembership) {
			final String userId = (String)map.get("USER_ID");
			final String resourceId = (String)map.get("RESOURCE_ID");
			if(!user2ResourceMap.containsKey(userId)) {
				user2ResourceMap.put(userId, new HashSet<String>());
			}
			user2ResourceMap.get(userId).add(resourceId);
		}
		
		int i = 0;
		for(final String userId : user2ResourceMap.keySet()) {
			checkResourceMembership(userId, user2ResourceMap.get(userId));
			if(i++ > MAX_ITERS) {
				break;
			}
		}
	}
	
	@Test
	public void isUserMemberOfGroupsTest() {
		final Map<String, Set<String>> userId2GroupMap = new HashMap<String, Set<String>>();
		final List<Map<String, Object>> groupUserMap = jdbcTemplate.queryForList("SELECT GRP_ID AS GROUP_ID, USER_ID AS USER_ID FROM USER_GRP LIMIT " + MAX_ITERS);
		for(final Map<String, Object> map : groupUserMap) {
			final String userId = (String)map.get("USER_ID");
			final String groupId = (String)map.get("GROUP_ID");
			if(!userId2GroupMap.containsKey(userId)) {
				userId2GroupMap.put(userId, new HashSet<String>());
			}
			userId2GroupMap.get(userId).add(groupId);
		}
		
		int i = 0;
		for(final String userId : userId2GroupMap.keySet()) {
			checkGroupMembership(userId, userId2GroupMap.get(userId));
			if(i++ > MAX_ITERS) {
				break;
			}
		}
	}
	
	@Test
	public void isUserMemberOfRolesTest() {
		final Map<String, Set<String>> userId2RoleMap = new HashMap<String, Set<String>>();
		final List<Map<String, Object>> roleUserMap = jdbcTemplate.queryForList("SELECT USER_ID AS USER_ID, ROLE_ID AS ROLE_ID FROM USER_ROLE LIMIT " + MAX_ITERS);
		final List<Map<String, Object>> roleUserMapThroughGroupMembership = jdbcTemplate.queryForList("SELECT ug.USER_ID AS USER_ID, gr.ROLE_ID AS ROLE_ID FROM USER_GRP ug, GRP_ROLE gr WHERE ug.GRP_ID=gr.GRP_ID LIMIT " + MAX_ITERS);
		for(final Map<String, Object> map : roleUserMap) {
			final String userId = (String)map.get("USER_ID");
			final String roleId = (String)map.get("ROLE_ID");
			if(!userId2RoleMap.containsKey(userId)) {
				userId2RoleMap.put(userId, new HashSet<String>());
			}
			userId2RoleMap.get(userId).add(roleId);
		}
		
		for(final Map<String, Object> map : roleUserMapThroughGroupMembership) {
			final String userId = (String)map.get("USER_ID");
			final String roleId = (String)map.get("ROLE_ID");
			if(!userId2RoleMap.containsKey(userId)) {
				userId2RoleMap.put(userId, new HashSet<String>());
			}
			userId2RoleMap.get(userId).add(roleId);
		}
		
		int i = 0;
		for(final String userId : userId2RoleMap.keySet()) {
			checkDirectRoleMembership(userId, userId2RoleMap.get(userId));
			
			if(i++ > MAX_ITERS) {
				break;
			}
		}
	}
	
	/*
	@Test
	public void testGetResourcesForUser() {
		final List<String> userIds = jdbcTemplate.queryForList("SELECT USER_ID FROM USERS LIMIT " + MAX_ITERS, String.class);
		int i = 0;
		for(final String userId : userIds) {
			final Set<String> resourceIdSet = getAllDirectResorucesForUser(userId);
			final User user = userDataWebService.getUserWithDependent(userId, true).getUser();
			final List<AuthorizationManagerLoginId> loginIdList = getLoginIdList(user);
			
			confirmUserResources(user.getUserId(), null, resourceIdSet);
			for(final AuthorizationManagerLoginId loginId : loginIdList) {
				confirmUserResources(null, loginId, resourceIdSet);
			}
			if(i++ > MAX_ITERS) {
				break;
			}
		}
	}
	
	@Test
	public void testGetGroupsFor() {
		int i = 0;
		final List<String> userIds = jdbcTemplate.queryForList("SELECT USER_ID FROM USERS LIMIT " + MAX_ITERS, String.class);
		for(final String userId : userIds) {
			final Set<String> groupIdSet = getAllDirectGroupsForUser(userId);
			
			final User user = userDataWebService.getUserWithDependent(userId, true).getUser();
			final List<AuthorizationManagerLoginId> loginIdList = getLoginIdList(user);
			
			confirmUserGroups(user.getUserId(), null, groupIdSet);
			for(final AuthorizationManagerLoginId loginId : loginIdList) {
				confirmUserGroups(null, loginId, groupIdSet);
			}
			if(i++ > MAX_ITERS) {
				break;
			}
		}
	}
	
	@Test
	public void testGetRolesFor() {
		int i = 0;
		final List<String> userIds = jdbcTemplate.queryForList("SELECT USER_ID FROM USERS LIMIT " + MAX_ITERS, String.class);
		for(final String userId : userIds) {
			
			final Set<String> roleIdSet = getAllDirectRolesForUser(userId);
			
			final User user = userDataWebService.getUserWithDependent(userId, true).getUser();
			final List<AuthorizationManagerLoginId> loginIdList = getLoginIdList(user);
			
			confirmUserRoles(user.getUserId(), null, roleIdSet);
			for(final AuthorizationManagerLoginId loginId : loginIdList) {
				confirmUserRoles(null, loginId, roleIdSet);
			}
			if(i++ > MAX_ITERS) {
				break;
			}
		}
	}
	*/
	
	private List<AuthorizationManagerLoginId> getLoginIdList(final User user) {
		final List<AuthorizationManagerLoginId> loginIdList = new LinkedList<AuthorizationManagerLoginId>();
		if(user != null && CollectionUtils.isNotEmpty(user.getPrincipalList())) {
			for(final Login login : user.getPrincipalList()) {
				final LoginId loginId = login.getId();
				final AuthorizationManagerLoginId authManagerLoginId = new AuthorizationManagerLoginId(loginId.getDomainId(), loginId.getLogin(), loginId.getManagedSysId());
				loginIdList.add(authManagerLoginId);
			}
		}
		return loginIdList;
	}
	
	private Set<String> getAllDirectRolesForUser(final String userId) {
		final Set<String> groupIds = getAllDirectGroupsForUser(userId);
		
		final List<String> roleIds = jdbcTemplate.queryForList("SELECT ROLE_ID FROM USER_ROLE WHERE USER_ID=?", String.class, userId);
		final Set<String> roleIdSet = new HashSet<String>();
		roleIdSet.addAll(roleIds);
		for(final String groupId : groupIds) {
			roleIdSet.addAll(jdbcTemplate.queryForList("SELECT ROLE_ID FROM GRP_ROLE WHERE GRP_ID=?", String.class, groupId));
		}
		return roleIdSet;
	}
	
	private Set<String> getAllDirectGroupsForUser(final String userId) {
		return new HashSet<String>(jdbcTemplate.queryForList("SELECT GRP_ID FROM USER_GRP WHERE USER_ID=?", String.class, userId));
	}
	
	private Set<String> getAllDirectResorucesForUser(final String userId) {
		final Set<String> groupIdSet = new HashSet<String>(getAllDirectGroupsForUser(userId));
		
		final List<String> roleIds = jdbcTemplate.queryForList("SELECT ROLE_ID FROM USER_ROLE WHERE USER_ID=?", String.class, userId);
		final Set<String> roleIdSet = new HashSet<String>();
		roleIdSet.addAll(roleIds);
		for(final String groupId : groupIdSet) {
			roleIdSet.addAll(jdbcTemplate.queryForList("SELECT ROLE_ID FROM GRP_ROLE WHERE GRP_ID=?", String.class, groupId));
		}
		
		final List<String> resourceIds = jdbcTemplate.queryForList("SELECT RESOURCE_ID FROM RESOURCE_USER WHERE USER_ID=?", String.class, userId);
		final Set<String> resourceIdSet = new HashSet<String>();
		resourceIdSet.addAll(resourceIds);
		
		if(groupIdSet.size() > 0) {
			final StringBuilder sql = new StringBuilder("SELECT RESOURCE_ID FROM RESOURCE_GROUP WHERE GRP_ID IN(");
			int size = groupIdSet.size();
			for(int i = 0; i < size; i++) {
				sql.append("?");
				if(i < size - 1) {
					sql.append(",");
				}
			}
			sql.append(")");
			resourceIdSet.addAll(jdbcTemplate.queryForList(sql.toString(), String.class, groupIdSet.toArray()));
		}
		
		if(roleIdSet.size() > 0) {
			final StringBuilder sql = new StringBuilder("SELECT RESOURCE_ID FROM RESOURCE_ROLE WHERE ROLE_ID IN(");
			int size = roleIdSet.size();
			for(int i = 0; i < size; i++) {
				sql.append("?");
				if(i < size - 1) {
					sql.append(",");
				}
			}
			sql.append(")");
			resourceIdSet.addAll(jdbcTemplate.queryForList(sql.toString(), String.class, roleIdSet.toArray()));
		}
		return resourceIdSet;
	}
	
	private void checkResourceMembership(final String userId, final Set<String> resourceIds) {
		for(final String id : resourceIds) {
			checkResourceMembership(userId, id);
		}
	}
	
	private void checkGroupMembership(final String userId, final Set<String> groupIdSet) {
		for(final String groupId : groupIdSet) {
			checkGroupMembership(userId, groupId);
		}
	}
	
	private void checkDirectRoleMembership(final String userId, final Set<String> roleIdSet) {
		for(final String roleId : roleIdSet) {
			checkRoleMembership(userId, roleId);
		}
	}
	
	private void checkRoleMembership(final String userId, final String roleId) {
		final User user = userDataWebService.getUserWithDependent(userId, true).getUser();
        final Role role = roleDataWebService.getRole(roleId);
		
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
			checkUser2GroupMembership(null, loginId, group.getGrpId(), null);
			checkUser2GroupMembership(null, loginId, null, group.getGrpName());
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
	
	protected abstract void confirmUserRoles(final String userId, final AuthorizationManagerLoginId loginId, final Set<String> roleIds);
	protected abstract void confirmUserGroups(final String userId, final AuthorizationManagerLoginId loginId, final Set<String> groupIds);
	protected abstract void confirmUserResources(final String userId, final AuthorizationManagerLoginId loginId, final Set<String> resourceIds);
	protected abstract void checkUser2ResourceEntitlement(final String userId, final AuthorizationManagerLoginId loginId, final String resourceId, final String resourceName);
	protected abstract void checkUser2GroupMembership(final String userId, final AuthorizationManagerLoginId loginId, final String groupId, final String groupName);
	protected abstract void checkUser2RoleMembership(final String userId, final AuthorizationManagerLoginId loginId, final String roleId, final String roleName);
	protected abstract void checkUserURLEntitlements(final String userId, final AuthorizationManagerLoginId loginId, final String url);
}
