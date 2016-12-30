package org.openiam.security;


import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthoritiesContainer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class OpeniamWebAuthenticationDetails extends WebAuthenticationDetails implements GrantedAuthoritiesContainer {
	
	private String languageId;
	
	public OpeniamWebAuthenticationDetails() {
		super(null);
	}
	
	public OpeniamWebAuthenticationDetails(final HttpServletRequest request, final String languageId) {
		super(request);
		this.languageId = StringUtils.trimToNull(languageId);
	}
	
	public OpeniamWebAuthenticationDetails(final HttpServletRequest request) {
		super(request);
		this.languageId = StringUtils.trimToNull(request.getHeader("x-openiam-language-id"));
		if(StringUtils.isBlank(this.languageId)) {
			this.languageId = "1";
		}
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getGrantedAuthorities() {
		return Arrays.asList(new GrantedAuthority[] {
			new SimpleGrantedAuthority("ROLE_USER")
		});
	}

	public String getLanguageId() {
		return languageId;
	}
	
	
}
