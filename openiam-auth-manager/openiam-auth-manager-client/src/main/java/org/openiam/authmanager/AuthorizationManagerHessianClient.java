package org.openiam.authmanager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.service.AuthorizationManagerHessianService;

import com.caucho.hessian.client.HessianProxyFactory;

public class AuthorizationManagerHessianClient implements AuthorizationManagerHessianService {
	
	private static final Log log = LogFactory.getLog(AuthorizationManagerHessianClient.class);
	
	private boolean isTimingEnabled;
	private String url;
	private AuthorizationManagerHessianService client;
	
	public AuthorizationManagerHessianClient() {
		
	}
	
	public AuthorizationManagerHessianClient(final String url) {
		this.url = url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	public void setTimingEnabled(boolean isTimingEnabled) {
		this.isTimingEnabled = isTimingEnabled;
	}

	public void init() {
		if(url == null || url.trim().isEmpty()) {
			throw new IllegalArgumentException("No URL Specified for Authorization Manager Hessian Client");
		}
		try {
			client = (AuthorizationManagerHessianService) new HessianProxyFactory().create(AuthorizationManagerHessianService.class, url);
		} catch(Throwable e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	@Override
	public boolean isUserWithIdEntitledToResourceWithId(final String userId, final String resourceId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		boolean retval = client.isUserWithIdEntitledToResourceWithId(userId, resourceId);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("isUserEntitledToResoruceWithId: userId: %s, resourceId: %s, time: %s ms", userId, resourceId, time);
			log.debug(logMessage);
		}
		return retval;
	}

	@Override
	public boolean isUserWithIdEntitledToResourceWithName(final String userId, final String resourceName) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		boolean retval = client.isUserWithIdEntitledToResourceWithName(userId, resourceName);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("isUserEntitledToResourceWithName: userId: %s, resourceName: %s, time: %s ms", userId, resourceName, time);
			log.debug(logMessage);
		}
		return retval;
	}

	@Override
	public boolean isUserWithLoginEntitledToResourceWithId(final String login, final String managedSysId, final String resourceId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		boolean retval = client.isUserWithLoginEntitledToResourceWithId(login, managedSysId, resourceId);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("isUserEntitledToResourceWithId: login: %s, managedSysId: %s, resourceId: %s, time: %s ms", login, managedSysId, resourceId, time);
			log.debug(logMessage);
		}
		return retval;
	}

	@Override
	public boolean isUserWithLoginEntitledToResourceWithName(final String login, final String managedSysId, final String resourceName) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		boolean retval = client.isUserWithLoginEntitledToResourceWithName(login, managedSysId, resourceName);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("isUserEntitledToResourceWithName: login: %s, managedSysId: %s, resourceName: %s, time: %s ms", login, managedSysId, resourceName, time);
			log.debug(logMessage);
		}
		return retval;
	}

	@Override
	public boolean isUserWithIdMemberOfGroupWithId(final String userId, final String groupId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		boolean retval = client.isUserWithIdMemberOfGroupWithId(userId, groupId);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("isUserMemberOfGroupWithId: userId: %s, groupId: %s, time: %s ms", userId, groupId, time);
			log.debug(logMessage);
		}
		return retval;
	}

	@Override
	public boolean isUserWithIdMemberOfGroupWithName(final String userId, final String groupName) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		boolean retval = client.isUserWithIdMemberOfGroupWithName(userId, groupName);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("isUserMemberOfGroupWithName: userId: %s, groupName: %s, time: %s ms", userId, groupName, time);
			log.debug(logMessage);
		}
		return retval;
	}

	@Override
	public boolean isUserWithLoginMemberOfGroupWithId(final String login, final String managedSysId, final String groupId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		boolean retval = client.isUserWithLoginMemberOfGroupWithId(login, managedSysId, groupId);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("isUserMemberOfGroupWithId: login: %s, managedSysId: %s, groupId: %s, time: %s ms", login, managedSysId, groupId, time);
			log.debug(logMessage);
		}
		return retval;
	}

	@Override
	public boolean isUserWithLoginMemberOfGroupWithName(final String login, final String managedSysId, final String groupName) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		boolean retval = client.isUserWithLoginMemberOfGroupWithName(login, managedSysId, groupName);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("isUserMemberOfGroupWithName: login: %s, managedSysId: %s, groupName: %s, time: %s ms", login, managedSysId, groupName, time);
			log.debug(logMessage);
		}
		return retval;
	}

	@Override
	public boolean isUserWithIdMemberOfRoleWithId(final String userId, final String roleId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		boolean retval = client.isUserWithIdMemberOfRoleWithId(userId, roleId);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("isUserMemberOfRoleWithId: userId: %s, roleId: %s, time: %s ms", userId, roleId, time);
			log.debug(logMessage);
		}
		return retval;
	}

	@Override
	public boolean isUserWithIdMemberOfRoleWithName(final String userId, final String roleName) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		boolean retval = client.isUserWithIdMemberOfRoleWithName(userId, roleName);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("isUserMemberOfRoleWithName: userId: %s, roleName: %s, time: %s ms", userId, roleName, time);
			log.debug(logMessage);
		}
		return retval;
	}

	@Override
	public boolean isUserWithLoginMemberOfRoleWithId(final String login, final String managedSysId, final String roleId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		boolean retval = client.isUserWithLoginMemberOfRoleWithId(login, managedSysId, roleId);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("isUserMemberOfRoleWithId: login: %s, managedSysId: %s, roleId: %s, time: %s ms", login, managedSysId, roleId, time);
			log.debug(logMessage);
		}
		return retval;
	}

	@Override
	public boolean isUserWithLoginMemberOfRoleWithName(final String login, final String managedSysId, final String roleName) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		boolean retval = client.isUserWithLoginMemberOfRoleWithName(login, managedSysId, roleName);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("isUserMemberOfRoleWithName: login: %s, managedSysId: %s, roleName: %s, time: %s ms", login, managedSysId, roleName, time);
			log.debug(logMessage);
		}
		return retval;
	}

	@Override
	public String[] getResourceIdsForUserWithId(final String userId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		String[] retval = client.getResourceIdsForUserWithId(userId);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("getResourceIdsForUser: userId: %s, time: %s ms", userId, time);
			log.debug(logMessage);
		}
		return retval;
	}

	@Override
	public String[] getResourceIdsForUserWithLogin(final String login, final String managedSysId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		String[] retval = client.getResourceIdsForUserWithLogin(login, managedSysId);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("getResourceIdsForUser: login: %s, managedSysId: %s, time: %s ms", login, managedSysId, time);
			log.debug(logMessage);
		}
		return retval;
	}

	@Override
	public String[] getResourceNamesForUserWithId(final String userId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		String[] retval = client.getResourceNamesForUserWithId(userId);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("getResourceNamesForUser: userId: %s, time: %s ms", userId, time);
			log.debug(logMessage);
		}
		return retval;
	}

	@Override
	public String[] getResourceNamesForUserWithLogin(final String login, final String managedSysId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		String[] retval = client.getResourceNamesForUserWithLogin(login, managedSysId);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("getResourceNamesForUser: login: %s, managedSysId: %s, time: %s ms", login, managedSysId, time);
			log.debug(logMessage);
		}
		return retval;
	}

	@Override
	public String[] getGroupIdsForUserWithId(final String userId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		String[] retval = client.getGroupIdsForUserWithId(userId);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("getGroupIdsForUser: userId: %s, time: %s ms", userId, time);
			log.debug(logMessage);
		}
		return retval;
	}

	@Override
	public String[] getGroupIdsForUserWithLogin(final String login, final String managedSysId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		String[] retval = client.getGroupIdsForUserWithLogin(login, managedSysId);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("getGroupIdsForUser: login: %s, managedSysId: %s, time: %s ms", login, managedSysId, time);
			log.debug(logMessage);
		}
		return retval;
	}

	@Override
	public String[] getGroupNamesForUserWithId(final String userId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		String[] retval = client.getGroupNamesForUserWithId(userId);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("getGroupNamesForUser: userId: %s, time: %s ms", userId, time);
			log.debug(logMessage);
		}
		return retval;
	}

	@Override
	public String[] getGroupNamesForUserWithLogin(final String login, final String managedSysId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		String[] retval = client.getGroupNamesForUserWithLogin(login, managedSysId);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("getGroupNamesForUser: login: %s, managedSysId: %s, time: %s ms", login, managedSysId, time);
			log.debug(logMessage);
		}
		return retval;
	}

	@Override
	public String[] getRoleIdsForUserWithId(final String userId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		String[] retval = client.getRoleIdsForUserWithId(userId);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("getRoleIdsForUser: userId: %s, time: %s ms", userId, time);
			log.debug(logMessage);
		}
		return retval;
	}

	@Override
	public String[] getRoleIdsForUserWithLogin(final String login, final String managedSysId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		String[] retval = client.getRoleIdsForUserWithLogin(login, managedSysId);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("getRoleIdsForUser: login: %s, managedSysId: %s, time: %s ms", login, managedSysId, time);
			log.debug(logMessage);
		}
		return retval;
	}

	@Override
	public String[] getRoleNamesForUserWithId(final String userId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		String[] retval = client.getRoleNamesForUserWithId(userId);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("getRoleNamesForUser: userId: %s, time: %s ms", userId, time);
			log.debug(logMessage);
		}
		return retval;
	}

	@Override
	public String[] getRoleNamesForUserWithLogin(final String login, final String managedSysId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		String[] retval = client.getRoleNamesForUserWithLogin(login, managedSysId);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("getRoleNamesForUser: login: %s, managedSysId: %s, time: %s ms", login, managedSysId, time);
			log.debug(logMessage);
		}
		return retval;
	}

	private void preflightCheck() {
		if(client == null) {
			throw new NullPointerException("Client not initialized (did you call init())?");
		}
	}
}
