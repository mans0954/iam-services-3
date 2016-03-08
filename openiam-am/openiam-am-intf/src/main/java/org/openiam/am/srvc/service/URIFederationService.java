package org.openiam.am.srvc.service;

import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.uriauth.dto.URIFederationResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.springframework.http.HttpMethod;

public interface URIFederationService {

	AuthenticationRequest createAuthenticationRequest(final String principal, final String proxyUR, final HttpMethod methodI) throws BasicDataServiceException;
	
	URIFederationResponse federateProxyURI(final String userId, final String proxyURI, final HttpMethod method);
	
	URIFederationResponse getMetadata(String proxyURI, final HttpMethod method);
	
	public void sweep();
	
	ContentProvider getCachedContentProvider(final String providerId);
	
	URIPattern getCachedURIPattern(final String patternId);
}
