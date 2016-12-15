package org.openiam.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

@Component("authenticationDetailsSource")
public class OpenIAMAuthenticationDetailsSource extends WebAuthenticationDetailsSource {

	public OpenIAMAuthenticationDetailsSource() {
		super();
	}
	
	/* no worries - this will be called once - on login */
	@Override
	public WebAuthenticationDetails buildDetails(HttpServletRequest request) {
		final OpeniamWebAuthenticationDetails details = new OpeniamWebAuthenticationDetails(request);
		return details;
	}
}
