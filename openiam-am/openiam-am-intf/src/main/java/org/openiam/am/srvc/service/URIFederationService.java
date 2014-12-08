package org.openiam.am.srvc.service;

import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.uriauth.dto.URIFederationResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.springframework.http.HttpMethod;

public interface URIFederationService {

	public AuthenticationRequest createAuthenticationRequest(final String principal, final String proxyUR, final HttpMethod methodI) throws BasicDataServiceException;
	
	public URIFederationResponse federateProxyURI(final String userId, final String proxyURI, final HttpMethod method);
	
	public URIFederationResponse getMetadata(String proxyURI, final HttpMethod method);
}
