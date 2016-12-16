package org.openiam.util;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;


public class SpringSecurityHelper {

	public static String getRequestorUserId() {
		String userId = null;
		if(SecurityContextHolder.getContext() != null) {
			final SecurityContext ctx = SecurityContextHolder.getContext();
			if(ctx.getAuthentication() != null) {
				final Authentication authentication = ctx.getAuthentication();
				if(authentication != null && authentication.getCredentials() != null) {
					if(authentication.getCredentials() instanceof CXFAuthentication) {
						userId = ((CXFAuthentication)authentication.getCredentials()).getUserId();
					}
				}
			}
		}
		return userId;
	}
	
	public static void setRequesterUserId(final String userId) {
		if(StringUtils.isNotBlank(userId)) {
			final SecurityContext context = new SecurityContextImpl();
			final Authentication authentication = new PreAuthenticatedAuthenticationToken(new CXFAuthentication(userId, null), new CXFAuthentication(userId, null));
			context.setAuthentication(authentication);
			SecurityContextHolder.setContext(context);
		}
	}
	
	public static void clearContext() {
		SecurityContextHolder.clearContext();
	}
}
