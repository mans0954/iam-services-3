package org.openiam.security;

public class CXFAuthentication {
	
	private String userId;
	private String principal;

	public CXFAuthentication(final String userId, final String principal) {
		this.userId = userId;
		this.principal = principal;
	}

	public String getUserId() {
		return userId;
	}

	public String getPrincipal() {
		return principal;
	}
	
	
}
