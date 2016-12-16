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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

public class OpenIAMPreAuthFilter extends AbstractPreAuthenticatedProcessingFilter {

	private static final Log log = LogFactory.getLog(OpenIAMPreAuthFilter.class);

	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		return getAuthentication(request);
	}

	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
		return getAuthentication(request);
	}
	
	private CXFAuthentication getAuthentication(final HttpServletRequest request) {
		CXFAuthentication retVal = null;
		final String userId = StringUtils.trimToNull(request.getHeader("x-openiam-userId"));
		if(userId != null) {
			retVal = new CXFAuthentication(userId, StringUtils.trimToNull(request.getHeader("x-openiam-principal")));
		}
		return retVal;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		super.doFilter(request, response, chain);
	}
	
	
}
