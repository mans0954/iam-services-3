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
	public boolean isUserEntitledToResource(final String userId, final String resourceId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		boolean retval = client.isUserEntitledToResource(userId, resourceId);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("isUserEntitledToResource: userId: %s, resourceId: %s, time: %s ms", userId, resourceId, time);
			log.debug(logMessage);
		}
		return retval;
	}

	@Override
	public boolean isUserEntitledToResourceWithRight(final String userId, final String resourceId, final String rightId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		boolean retval = client.isUserEntitledToResourceWithRight(userId, resourceId, rightId);
		if(isTimingEnabled) {
if(log.isDebugEnabled()) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("isUserEntitledToResourceWithRight: userId: %s, resourceId: %s, rightId: %s, time: %s ms", 
					userId, resourceId, rightId, time);
			log.debug(logMessage);
}
		}
		return retval;
	}


	@Override
	public boolean isUserMemberOfGroup(final String userId, final String groupId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		boolean retval = client.isUserMemberOfGroup(userId, groupId);
		if(isTimingEnabled) {
if(log.isDebugEnabled()) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("isUserMemberOfGroup: userId: %s, groupId: %s, time: %s ms", userId, groupId, time);
			log.debug(logMessage);
}
		}
		return retval;
	}
	

	@Override
	public boolean isUserMemberOfGroupWithRight(final String userId, final String groupId, final String rightId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		boolean retval = client.isUserMemberOfGroupWithRight(userId, groupId, rightId);
		if(isTimingEnabled) {
if(log.isDebugEnabled()) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("isUserMemberOfGroupWithRight: userId: %s, groupId: %s, rightId: %s, time: %s ms", 
					userId, groupId, rightId, time);
			log.debug(logMessage);
}
		}
		return retval;
	}


	@Override
	public boolean isUserMemberOfRole(final String userId, final String roleId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		boolean retval = client.isUserMemberOfRole(userId, roleId);
		if(isTimingEnabled) {
if(log.isDebugEnabled()) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("isUserMemberOfRole: userId: %s, roleId: %s, time: %s ms", userId, roleId, time);
			log.debug(logMessage);
}
		}
		return retval;
	}
	

	@Override
	public boolean isUserMemberOfRoleWithRight(final String userId, final String roleId, final String rightId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		boolean retval = client.isUserMemberOfRoleWithRight(userId, roleId, rightId);
		if(isTimingEnabled) {
if(log.isDebugEnabled()) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("isUserMemberOfRoleWithRight: userId: %s, roleId: %s, rightId: %s, time: %s ms", 
					userId, roleId, rightId, time);
			log.debug(logMessage);
}
		}
		return retval;
	}
	

	@Override
	public boolean isUserMemberOfOrganization(final String userId, final String organizationId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		boolean retval = client.isUserMemberOfOrganization(userId, organizationId);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("isUserMemberOfOrganization: userId: %s, roleId: %s, time: %s ms", 
					userId, organizationId, time);
			log.debug(logMessage);
		}
		return retval;
	}

	@Override
	public boolean isUserMemberOfOrganizationWithRight(final String userId, final String organizationId, final String rightId) {
		preflightCheck();
		long start = 0L;
		if(isTimingEnabled) {
			start = System.currentTimeMillis();
		}
		boolean retval = client.isUserMemberOfOrganizationWithRight(userId, organizationId, rightId);
		if(isTimingEnabled) {
			long time = System.currentTimeMillis() - start;
			final String logMessage = String.format("isUserMemberOfOrganizationWithRight: userId: %s, roleId: %s, rightId: %s, time: %s ms", 
					userId, organizationId, rightId, time);
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
