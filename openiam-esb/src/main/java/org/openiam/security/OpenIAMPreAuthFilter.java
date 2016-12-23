package org.openiam.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.util.CXFAuthentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

public class OpenIAMPreAuthFilter extends AbstractPreAuthenticatedProcessingFilter {

	private static final Log log = LogFactory.getLog(OpenIAMPreAuthFilter.class);
	
	@Value("${org.openiam.idm.system.user.id}")
	private String systemUserId;

	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		return getAuthentication(request);
	}

	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
		return getAuthentication(request);
	}
	
	private CXFAuthentication getAuthentication(final HttpServletRequest request) {
		String userId = StringUtils.trimToNull(request.getHeader("x-openiam-userId"));
		
		/* this is actually required - the user *must* have some kind of user ID in order to propertly continue - some services
		 * actually require the userId to be there.
		 * 
		 * As of now, we have no reason to protect the individual service calls based on userID
		 */
		if(StringUtils.isBlank(userId)) {
			userId = systemUserId;
		}
		return new CXFAuthentication(userId, StringUtils.trimToNull(request.getHeader("x-openiam-principal")));
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		super.doFilter(request, response, chain);
	}
	
	
}
