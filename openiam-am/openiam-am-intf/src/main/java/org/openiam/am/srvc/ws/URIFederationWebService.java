package org.openiam.am.srvc.ws;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.uriauth.dto.SSOLoginResponse;
import org.openiam.am.srvc.uriauth.dto.URIFederationResponse;
import org.openiam.base.ws.Response;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/am/service", name = "URIFederationWebService")
public interface URIFederationWebService {

	URIFederationResponse getMetadata(@WebParam(name = "proxyURI", targetNamespace = "") final String proxyURI,
									  @WebParam(name = "method", targetNamespace = "") final String method);
	
	ContentProvider getCachedContentProvider(@WebParam(name = "providerId", targetNamespace = "") final String providerId);
	
	URIPattern getCachedURIPattern(@WebParam(name = "patternId", targetNamespace = "") final String patternId);

}
