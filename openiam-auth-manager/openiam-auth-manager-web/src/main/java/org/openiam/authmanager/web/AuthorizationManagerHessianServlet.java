package org.openiam.authmanager.web;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.service.AuthorizationManagerHessianService;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;

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
	public boolean isUserEntitledToResource(final String userId, final String resourceId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final boolean retval = authManagerService.isEntitled(userId, resourceId);
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("isUserEntitledToResoruceWithId: userId: %s,  resourceId: %s, ThreadId: %s. Took %s ms", 
					userId, resourceId, Thread.currentThread().getId(), sw.getTime()));
		}
		
		return retval;
	}

	@Override
	public boolean isUserMemberOfGroup(final String userId, final String groupId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final boolean retval = authManagerService.isMemberOfGroup(userId, groupId);
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("isUserMemberOfGroupWithId: userId: %s, groupId: %s, ThreadId: %s. Took %s ms", 
					userId, groupId, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}

	@Override
	public boolean isUserMemberOfRole(final String userId, final String roleId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final boolean retval = authManagerService.isMemberOfRole(userId, roleId);
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("isUserMemberOfRoleWithId: userId: %s, roleId: %s, ThreadId: %s. Took %s ms", 
					userId, roleId, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}


	@Override
	public boolean isUserEntitledToResourceWithRight(final String userId, final String resourceId, final String rightId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final boolean retval = authManagerService.isEntitled(userId, resourceId, rightId);
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("isUserEntitledToResoruceWithId: userId: %s,  resourceId: %s, rightId: %s, ThreadId: %s. Took %s ms", 
					userId, resourceId, rightId, Thread.currentThread().getId(), sw.getTime()));
		}
		
		return retval;
	}

	@Override
	public boolean isUserMemberOfGroupWithRight(final String userId, final String groupId, final String rightId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final boolean retval = authManagerService.isMemberOfGroup(userId, groupId, rightId);
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("isUserMemberOfGroupWithId: userId: %s, groupId: %s, rightId: %s, ThreadId: %s. Took %s ms", 
					userId, groupId, rightId, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}

	@Override
	public boolean isUserMemberOfRoleWithRight(final String userId, final String roleId, final String rightId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final boolean retval = authManagerService.isMemberOfRole(userId, roleId, rightId);
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("isUserMemberOfRoleWithId: userId: %s, roleId: %s, rightId: %s, ThreadId: %s. Took %s ms", 
					userId, roleId, rightId, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}

	@Override
	public boolean isUserMemberOfOrganization(final String userId, final String organizationId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final boolean retval = authManagerService.isMemberOfOrganization(userId, organizationId);
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("isUserMemberOfOrganization: userId: %s, organizationId: %s, ThreadId: %s. Took %s ms", 
					userId, organizationId, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}

	@Override
	public boolean isUserMemberOfOrganizationWithRight(final String userId, final String organizationId, final String rightId) {
		StopWatch sw = null;
		if(log.isDebugEnabled()) {
			sw = new StopWatch();
			sw.start();
		}
		final boolean retval = authManagerService.isMemberOfOrganization(userId, organizationId, rightId);
		if(log.isDebugEnabled()) {
			sw.stop();
			log.debug(String.format("isUserMemberOfOrganization: userId: %s, organizationId: %s, rightId: %s, ThreadId: %s. Took %s ms", 
					userId, organizationId, rightId, Thread.currentThread().getId(), sw.getTime()));
		}
		return retval;
	}
}
