package org.openiam.security;

import java.util.Collection;

import org.openiam.util.CXFAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService;

public class OpenIAMAuthenticationUserDetailsService extends PreAuthenticatedGrantedAuthoritiesUserDetailsService {
	
	public OpenIAMAuthenticationUserDetailsService() {
		super();
	}

	@Override
	protected UserDetails createUserDetails(Authentication token, Collection<? extends GrantedAuthority> authorities) {
		final CXFAuthentication principal = (CXFAuthentication)token.getPrincipal();
		return new User((principal != null) ? principal.getUserId() : null, "[NOT REQUIRED]", true, true, true, true, authorities);
	}

	/*
	private Set<GrantedAuthority> buildAuthorities() {
		final Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
		grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		return grantedAuthorities;
	}
	*/
}
