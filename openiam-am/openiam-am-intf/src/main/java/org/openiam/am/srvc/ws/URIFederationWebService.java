package org.openiam.am.srvc.ws;

import javax.jws.WebParam;
import javax.jws.WebService;

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
	public URIFederationResponse federateProxyURI(@WebParam(name = "userId", targetNamespace = "") final String userId, 
												  @WebParam(name = "authLevel", targetNamespace = "") final int authLevel, 
												  @WebParam(name = "proxyURI", targetNamespace = "") final String proxyURI);
	
	/**
	 * Method called by Reverse Proxy via SOAP Request
	 * Calculates a user's cookie based on the principal and the proxyURI.  If the proxyURI matches an existing Content Provider, the system will look up the user's information based on the given
	 * principal and the Managed System of the Content Provider.
	 * @param proxyURI - the FULL <b>PROXY</b> URI being accessed.  i.e. http://www.openiam.com/appContext/index.html
	 * @param principal - the principal for this request.  Must correspond to the managed system of the Content Provider found from the <b>proxyURI</b> parameter
	 * @return a Response that contains the SSOToken
	 */
	public SSOLoginResponse getCookieFromProxyURIAndPrincipal(@WebParam(name = "proxyURI", targetNamespace = "") final String proxyURI,
								 					  @WebParam(name = "principal", targetNamespace = "") final String principal);
	
	public URIFederationResponse getMetadata(final String proxyURI);
}
