package org.openiam.am.srvc.service;

import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.uriauth.dto.URIFederationResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;

public interface URIFederationService {

	public AuthenticationRequest createAuthenticationRequest(final String principal, final String proxyURI) throws BasicDataServiceException;
	
	public URIFederationResponse federateProxyURI(final String userId, final int authLevel, final String proxyURI);
	
	public URIFederationResponse getMetadata(String proxyURI);
}
