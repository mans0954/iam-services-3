package org.openiam.security;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthoritiesContainer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class OpeniamWebAuthenticationDetails extends WebAuthenticationDetails implements GrantedAuthoritiesContainer {
	
	public OpeniamWebAuthenticationDetails(final HttpServletRequest request) {
		super(request);
	}

	@Override
	public Collection<? extends GrantedAuthority> getGrantedAuthorities() {
		return Arrays.asList(new GrantedAuthority[] {
			new SimpleGrantedAuthority("ROLE_USER")
		});
	}
}
