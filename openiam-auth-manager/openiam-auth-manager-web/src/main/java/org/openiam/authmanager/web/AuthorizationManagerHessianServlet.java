package org.openiam.authmanager.web;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationManagerLoginId;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.authmanager.service.AuthorizationManagerHessianService;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caucho.hessian.server.HessianServlet;

//@Service("authorizationManagerHessianService")
public class AuthorizationManagerHessianServlet extends HessianServlet implements AuthorizationManagerHessianService {

	private static final Log log = LogFactory.getLog(AuthorizationManagerHessianServlet.class);
	
	@Autowired
	private AuthorizationManagerService authManagerService;
	
	@Override
	public void init(final ServletConfig config) throws ServletException {
		SpringContextProvider.autowire(this);
		super.init(config);
	}
	
	@Override
	public boolean isUserWithIdEntitledToResourceWithId(final String userId, final String resourceId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final AuthorizationResource resource = new AuthorizationResource();
		resource.setId(resourceId);
		final boolean retval = authManagerService.isEntitled(userId, resource);
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("isUserEntitledToResoruceWithId: userId: %s,  resourceId: %s, ThreadId: %s. Took %s ms", 
					userId, resourceId, Thread.currentThread().getId(), sw.getTime()));
		}
		
		return retval;
	}

	@Override
	public boolean isUserWithIdEntitledToResourceWithName(final String userId, final String resourceName) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		
		final AuthorizationResource resource = new AuthorizationResource();
		resource.setName(resourceName);
		final boolean retval = authManagerService.isEntitled(userId, resource);
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("isUserEntitledToResourceWithName: userId: %s,  resourceName: %s, ThreadId: %s. Took %s ms", 
					userId, resourceName, Thread.currentThread().getId(), sw.getTime()));
		}
		
		return retval;
	}

	@Override
	public boolean isUserWithLoginEntitledToResourceWithId(final String domain, final String login, final String managedSysId, final String resourceId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final AuthorizationResource resource = new AuthorizationResource();
		resource.setId(resourceId);
		final boolean retval = authManagerService.isEntitled(new AuthorizationManagerLoginId(domain, login, managedSysId), resource);
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("isUserEntitledToResourceWithId: domain: %s, login: %s, managedSysId: %s, resourceId: %s, ThreadId: %s. Took %s ms", 
					domain, login, managedSysId, resourceId, Thread.currentThread().getId(), sw.getTime()));
		}
		
		return retval;
	}

	@Override
	public boolean isUserWithLoginEntitledToResourceWithName(final String domain,
			final String login, final String managedSysId, final String resourceName) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final AuthorizationResource resource = new AuthorizationResource();
		resource.setName(resourceName);
		final boolean retval = authManagerService.isEntitled(new AuthorizationManagerLoginId(domain, login, managedSysId), resource);
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("isUserEntitledToResourceWithName: domain: %s, login: %s, managedSysId: %s, resourceName: %s, ThreadId: %s. Took %s ms", 
					domain, login, managedSysId, resourceName, Thread.currentThread().getId(), sw.getTime()));
		}
		
		return retval;
	}

	@Override
	public boolean isUserWithIdMemberOfGroupWithId(final String userId, final String groupId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final AuthorizationGroup group = new AuthorizationGroup();
		group.setId(groupId);
		final boolean retval = authManagerService.isMemberOf(userId, group);
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("isUserMemberOfGroupWithId: userId: %s, groupId: %s, ThreadId: %s. Took %s ms", 
					userId, groupId, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}

	@Override
	public boolean isUserWithIdMemberOfGroupWithName(final String userId, final String groupName) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final AuthorizationGroup group = new AuthorizationGroup();
		group.setName(groupName);
		final boolean retval = authManagerService.isMemberOf(userId, group);
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("isUserMemberOfGroupWithName: userId: %s, groupName: %s, ThreadId: %s. Took %s ms", 
					userId, groupName, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}

	@Override
	public boolean isUserWithLoginMemberOfGroupWithId(final String domain, final String login,
			final String managedSysId, final String groupId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final AuthorizationGroup group = new AuthorizationGroup();
		group.setId(groupId);
		final boolean retval = authManagerService.isMemberOf(new AuthorizationManagerLoginId(domain, login, managedSysId), group);
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("isUserMemberOfGroupWithId: domain: %s, login: %s, managedSysId: %s, groupId: %s, ThreadId: %s. Took %s ms", 
					domain, login, managedSysId, groupId, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}

	@Override
	public boolean isUserWithLoginMemberOfGroupWithName(final String domain, final String login,
			final String managedSysId, final String groupName) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final AuthorizationGroup group = new AuthorizationGroup();
		group.setName(groupName);
		final boolean retval = authManagerService.isMemberOf(new AuthorizationManagerLoginId(domain, login, managedSysId), group);
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("isUserMemberOfGroupWithName: domain: %s, login: %s, managedSysId: %s, groupName: %s, ThreadId: %s. Took %s ms", 
					domain, login, managedSysId, groupName, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}

	@Override
	public boolean isUserWithIdMemberOfRoleWithId(final String userId, final String roleId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final AuthorizationRole role = new AuthorizationRole();
		role.setId(roleId);
		final boolean retval = authManagerService.isMemberOf(userId, role);
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("isUserMemberOfRoleWithId: userId: %s, roleId: %s, ThreadId: %s. Took %s ms", 
					userId, roleId, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}

	@Override
	public boolean isUserWithIdMemberOfRoleWithName(final String userId, final String roleName) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final AuthorizationRole role = new AuthorizationRole();
		role.setName(roleName);
		final boolean retval = authManagerService.isMemberOf(userId, role);
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("isUserMemberOfRoleWithName: userId: %s, roleName: %s, ThreadId: %s. Took %s ms", 
					userId, roleName, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}

	@Override
	public boolean isUserWithLoginMemberOfRoleWithId(final String domain, final String login,
			final String managedSysId, final String roleId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final AuthorizationRole role = new AuthorizationRole();
		role.setId(roleId);
		final boolean retval = authManagerService.isMemberOf(new AuthorizationManagerLoginId(domain, login, managedSysId), role);
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("isUserMemberOfRoleWithId: domain: %s, login: %s, managedSysId: %s, roleId: %s, ThreadId: %s. Took %s ms", 
					domain, login, managedSysId, roleId, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}

	@Override
	public boolean isUserWithLoginMemberOfRoleWithName(final String domain, final String login,
			final String managedSysId, final String roleName) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final AuthorizationRole role = new AuthorizationRole();
		role.setName(roleName);
		final boolean retval = authManagerService.isMemberOf(new AuthorizationManagerLoginId(domain, login, managedSysId), role);
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("isUserMemberOfRoleWithName: domain: %s, login: %s, managedSysId: %s, roleName: %s, ThreadId: %s. Took %s ms", 
					domain, login, managedSysId, roleName, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}

	@Override
	public String[] getResourceIdsForUserWithId(final String userId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final Set<AuthorizationResource> resultSet = authManagerService.getResourcesFor(userId);
		final String[] retval = new String[resultSet.size()];
		int i = 0;
		for(final AuthorizationResource resource : resultSet) {
			retval[i++] = resource.getId();
		}
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("getResourceIdsForUser: userId: %s, ThreadId: %s. Took %s ms", 
					userId, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}

	@Override
	public String[] getResourceIdsForUserWithLogin(final String domain, final String login,
			final String managedSysId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final Set<AuthorizationResource> resultSet = authManagerService.getResourcesFor(new AuthorizationManagerLoginId(domain, login, managedSysId));
		final String[] retval = new String[resultSet.size()];
		int i = 0;
		for(final AuthorizationResource resource : resultSet) {
			retval[i++] = resource.getId();
		}
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("getResourceIdsForUser: domain: %s, login: %s, managedSysId: %s, ThreadId: %s. Took %s ms", 
					domain, login, managedSysId, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}

	@Override
	public String[] getResourceNamesForUserWithId(final String userId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final Set<AuthorizationResource> resultSet = authManagerService.getResourcesFor(userId);
		final String[] retval = new String[resultSet.size()];
		int i = 0;
		for(final AuthorizationResource resource : resultSet) {
			retval[i++] = resource.getName();
		}
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("getResourceNamesForUser: userId: %s, ThreadId: %s. Took %s ms", 
					userId, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}

	@Override
	public String[] getResourceNamesForUserWithLogin(final String domain, final String login,
			String managedSysId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final Set<AuthorizationResource> resultSet = authManagerService.getResourcesFor(new AuthorizationManagerLoginId(domain, login, managedSysId));
		final String[] retval = new String[resultSet.size()];
		int i = 0;
		for(final AuthorizationResource resource : resultSet) {
			retval[i++] = resource.getName();
		}
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("getResourceNamesForUser: domain: %s, login: %s, managedSysId: %s, ThreadId: %s. Took %s ms", 
					domain, login, managedSysId, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}

	@Override
	public String[] getGroupIdsForUserWithId(final String userId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final Set<AuthorizationGroup> resultSet = authManagerService.getGroupsFor(userId);
		final String[] retval = new String[resultSet.size()];
		int i = 0;
		for(final AuthorizationGroup group : resultSet) {
			retval[i++] = group.getId();
		}
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("getGroupIdsForUser: userId: %s, ThreadId: %s. Took %s ms", 
					userId, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}

	@Override
	public String[] getGroupIdsForUserWithLogin(final String domain, final String login,
			final String managedSysId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final Set<AuthorizationGroup> resultSet = authManagerService.getGroupsFor(new AuthorizationManagerLoginId(domain, login, managedSysId));
		final String[] retval = new String[resultSet.size()];
		int i = 0;
		for(final AuthorizationGroup group : resultSet) {
			retval[i++] = group.getId();
		}
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("getGroupIdsForUser: domain: %s, login: %s, managedSysId: %s, ThreadId: %s. Took %s ms", 
					domain, login, managedSysId, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}

	@Override
	public String[] getGroupNamesForUserWithId(final String userId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final Set<AuthorizationGroup> resultSet = authManagerService.getGroupsFor(userId);
		final String[] retval = new String[resultSet.size()];
		int i = 0;
		for(final AuthorizationGroup group : resultSet) {
			retval[i++] = group.getName();
		}
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("getGroupNamesForUser: userId: %s, ThreadId: %s. Took %s ms", 
					userId, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}

	@Override
	public String[] getGroupNamesForUserWithLogin(final String domain, final String login,
			final String managedSysId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final Set<AuthorizationGroup> resultSet = authManagerService.getGroupsFor(new AuthorizationManagerLoginId(domain, login, managedSysId));
		final String[] retval = new String[resultSet.size()];
		int i = 0;
		for(final AuthorizationGroup group : resultSet) {
			retval[i++] = group.getName();
		}
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("getGroupIdsForUser: domain: %s, login: %s, managedSysId: %s, ThreadId: %s. Took %s ms", 
					domain, login, managedSysId, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}

	@Override
	public String[] getRoleIdsForUserWithId(final String userId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final Set<AuthorizationRole> resultSet = authManagerService.getRolesFor(userId);
		final String[] retval = new String[resultSet.size()];
		int i = 0;
		for(final AuthorizationRole role : resultSet) {
			retval[i++] = role.getId();
		}
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("getRoleIdsForUser: userId: %s, ThreadId: %s. Took %s ms", 
					userId, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}

	@Override
	public String[] getRoleIdsForUserWithLogin(final String domain, final String login,
			final String managedSysId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final Set<AuthorizationRole> resultSet = authManagerService.getRolesFor(new AuthorizationManagerLoginId(domain, login, managedSysId));
		final String[] retval = new String[resultSet.size()];
		int i = 0;
		for(final AuthorizationRole role : resultSet) {
			retval[i++] = role.getId();
		}
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("getRoleIdsForUser: domain: %s, login: %s, managedSysId: %s, ThreadId: %s. Took %s ms", 
					domain, login, managedSysId, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}

	@Override
	public String[] getRoleNamesForUserWithId(final String userId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final Set<AuthorizationRole> resultSet = authManagerService.getRolesFor(userId);
		final String[] retval = new String[resultSet.size()];
		int i = 0;
		for(final AuthorizationRole role : resultSet) {
			retval[i++] = role.getName();
		}
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("getRoleNamesForUser: userId: %s, ThreadId: %s. Took %s ms", 
					userId, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}

	@Override
	public String[] getRoleNamesForUserWithLogin(String domain, String login,
			String managedSysId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final Set<AuthorizationRole> resultSet = authManagerService.getRolesFor(new AuthorizationManagerLoginId(domain, login, managedSysId));
		final String[] retval = new String[resultSet.size()];
		int i = 0;
		for(final AuthorizationRole role : resultSet) {
			retval[i++] = role.getName();
		}
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("getRoleNamesForUser: domain: %s, login: %s, managedSysId: %s, ThreadId: %s. Took %s ms", 
					domain, login, managedSysId, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}
}
