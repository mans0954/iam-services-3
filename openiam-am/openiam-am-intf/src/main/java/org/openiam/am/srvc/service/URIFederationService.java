package org.openiam.am.srvc.service;

import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.uriauth.dto.SSOLoginResponse;
import org.openiam.base.response.LoginResponse;
import org.openiam.base.response.URIFederationResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.base.request.AuthenticationRequest;
import org.springframework.http.HttpMethod;
import org.springframework.web.multipart.MultipartFile;

public interface URIFederationService {

	AuthenticationRequest createAuthenticationRequest(final String principal, final String proxyURI, final HttpMethod method) throws BasicDataServiceException;
	
	URIFederationResponse federateProxyURI(final String userId, final String proxyURI, final HttpMethod method);
	
	URIFederationResponse getMetadata(String proxyURI, final HttpMethod method);
	
	public void sweep();
	
	ContentProvider getCachedContentProvider(final String providerId);
	
	URIPattern getCachedURIPattern(final String patternId);

	LoginResponse getIdentityFromCert(String proxyURI, HttpMethod method, MultipartFile certContents) throws BasicDataServiceException;

	SSOLoginResponse getCookieFromProxyURIAndPrincipal(String proxyURI, HttpMethod method, String principal) throws BasicDataServiceException;
}
