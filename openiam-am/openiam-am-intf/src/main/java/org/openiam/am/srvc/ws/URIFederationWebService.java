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

	/**
	 * Method called by the Reverse Proxy via SOAP request
	 * This method checks URI Entitlements, processes Rules, and returns the result in a structured format to the Proxy
	 * @param userId - the UserId that hit the proxy
	 * @param authLevel - current authentication level of the user.
	 * @param proxyURI - the FULL <b>PROXY</b> URI being accessed.  i.e. http://www.openiam.com/appContext/index.html
	 * @return a URIFederationResponse Object.  The proxy should be able to understand the format.
	 */
	URIFederationResponse federateProxyURI(@WebParam(name = "userId", targetNamespace = "") final String userId,
										   @WebParam(name = "proxyURI", targetNamespace = "") final String proxyURI,
										   @WebParam(name = "method", targetNamespace = "") final String method);
	
	URIFederationResponse getMetadata(@WebParam(name = "proxyURI", targetNamespace = "") final String proxyURI,
									  @WebParam(name = "method", targetNamespace = "") final String method);
	
	ContentProvider getCachedContentProvider(@WebParam(name = "providerId", targetNamespace = "") final String providerId);
	
	URIPattern getCachedURIPattern(@WebParam(name = "patternId", targetNamespace = "") final String patternId);

}
