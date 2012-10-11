package org.openiam.authmanager.service.integration;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
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

public abstract class AbstractAuthorizationManagerTest {

	
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
	
	private static final int MAX_ITERS = 200;
	
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
	
	@Test
	public void testGetResourcesForUser() {
		final List<String> userIds = jdbcTemplate.queryForList("SELECT USER_ID FROM USERS", String.class);
		int i = 0;
		for(final String userId : userIds) {
			final Set<String> resourceIdSet = getAllResorucesForUser(userId);
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
		final List<String> userIds = jdbcTemplate.queryForList("SELECT USER_ID FROM USERS", String.class);
		for(final String userId : userIds) {
			final Set<String> groupIdSet = getAllGroupsForUser(userId);
			
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
		final List<String> userIds = jdbcTemplate.queryForList("SELECT USER_ID FROM USERS", String.class);
		for(final String userId : userIds) {
			
			final Set<String> roleIdSet = getAllRolesForUser(userId);
			
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
	
	private Set<String> getAllRolesForUser(final String userId) {
		final List<String> groupIds = jdbcTemplate.queryForList("SELECT GRP_ID FROM USER_GRP WHERE USER_ID=?", String.class, userId);
		final Set<String> groupIdSet = new HashSet<String>();
		groupIdSet.addAll(groupIds);
		final Set<String> parentGroups = new HashSet<String>();
		for(final String groupId : groupIdSet) {
			visitParentGroups(groupId, parentGroups);
		}
		groupIdSet.addAll(parentGroups);
		
		final List<String> roleIds = jdbcTemplate.queryForList("SELECT ROLE_ID FROM USER_ROLE WHERE USER_ID=?", String.class, userId);
		final Set<String> roleIdSet = new HashSet<String>();
		roleIdSet.addAll(roleIds);
		for(final String groupId : groupIdSet) {
			roleIdSet.addAll(jdbcTemplate.queryForList("SELECT ROLE_ID FROM GRP_ROLE WHERE GRP_ID=?", String.class, groupId));
		}
		
		final Set<String> parentRoles = new HashSet<String>();
		for(final String roleId : roleIdSet) {
			visitParentRoles(roleId, parentRoles);
		}
		roleIdSet.addAll(parentRoles);
		return roleIdSet;
	}
	
	private Set<String> getAllGroupsForUser(final String userId) {
		final List<String> groupIds = jdbcTemplate.queryForList("SELECT GRP_ID FROM USER_GRP WHERE USER_ID=?", String.class, userId);
		final Set<String> groupIdSet = new HashSet<String>();
		groupIdSet.addAll(groupIds);
		final Set<String> parentGroups = new HashSet<String>();
		for(final String groupId : groupIdSet) {
			visitParentGroups(groupId, parentGroups);
		}
		groupIdSet.addAll(parentGroups);
		return groupIdSet;
	}
	
	private Set<String> getAllResorucesForUser(final String userId) {
		final List<String> groupIds = jdbcTemplate.queryForList("SELECT GRP_ID FROM USER_GRP WHERE USER_ID=?", String.class, userId);
		final Set<String> groupIdSet = new HashSet<String>();
		groupIdSet.addAll(groupIds);
		final Set<String> parentGroups = new HashSet<String>();
		for(final String groupId : groupIdSet) {
			visitParentGroups(groupId, parentGroups);
		}
		groupIdSet.addAll(parentGroups);
		
		final List<String> roleIds = jdbcTemplate.queryForList("SELECT ROLE_ID FROM USER_ROLE WHERE USER_ID=?", String.class, userId);
		final Set<String> roleIdSet = new HashSet<String>();
		roleIdSet.addAll(roleIds);
		for(final String groupId : groupIdSet) {
			roleIdSet.addAll(jdbcTemplate.queryForList("SELECT ROLE_ID FROM GRP_ROLE WHERE GRP_ID=?", String.class, groupId));
		}
		
		final Set<String> parentRoles = new HashSet<String>();
		for(final String roleId : roleIdSet) {
			visitParentRoles(roleId, parentRoles);
		}
		roleIdSet.addAll(parentRoles);
		
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
		
		final Set<String> parentResourceIds = new HashSet<String>();
		for(final String id : resourceIdSet) {
			visitParentResources(id, parentResourceIds);
		}
		resourceIdSet.addAll(parentResourceIds);
		return resourceIdSet;
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
	
	protected abstract void confirmUserRoles(final String userId, final AuthorizationManagerLoginId loginId, final Set<String> roleIds);
	protected abstract void confirmUserGroups(final String userId, final AuthorizationManagerLoginId loginId, final Set<String> groupIds);
	protected abstract void confirmUserResources(final String userId, final AuthorizationManagerLoginId loginId, final Set<String> resourceIds);
	protected abstract void checkUser2ResourceEntitlement(final String userId, final AuthorizationManagerLoginId loginId, final String resourceId, final String resourceName);
	protected abstract void checkUser2GroupMembership(final String userId, final AuthorizationManagerLoginId loginId, final String groupId, final String groupName);
	protected abstract void checkUser2RoleMembership(final String userId, final AuthorizationManagerLoginId loginId, final String roleId, final String roleName);
}
