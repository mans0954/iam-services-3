package org.openiam.am.srvc.service;

import org.openiam.am.srvc.uriauth.dto.URIFederationResponse;

public interface URIFederationService {

	public URIFederationResponse federateProxyURI(final String userId, final int authLevel, final String proxyURI);
}
