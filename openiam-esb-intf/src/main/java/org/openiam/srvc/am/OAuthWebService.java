package org.openiam.srvc.am;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.openiam.am.srvc.dto.AuthProvider;
import org.openiam.am.srvc.dto.OAuthCode;
import org.openiam.am.srvc.dto.OAuthScopesResponse;
import org.openiam.am.srvc.dto.OAuthToken;
import org.openiam.am.srvc.dto.OAuthUserClientXref;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.res.dto.Resource;

/**
 * Created by alexander on 06/07/15.
 */
@WebService(targetNamespace = "urn:idm.openiam.org/srvc/am/service", name = "OAuthWebService")
public interface OAuthWebService {

    @WebMethod
    AuthProvider getClient(@WebParam(name = "clientId", targetNamespace = "") String clientId);

    @WebMethod
    OAuthScopesResponse getScopesForAuthrorization(@WebParam(name = "clientId", targetNamespace = "") String clientId,
                                              @WebParam(name = "userId", targetNamespace = "") String userId);

    @WebMethod
    Response saveClientScopeAuthorization(@WebParam(name = "providerId", targetNamespace = "") String providerId,
                                          @WebParam(name = "userId", targetNamespace = "") String userId,
                                          @WebParam(name = "oauthUserClientXrefList", targetNamespace = "") List<OAuthUserClientXref> oauthUserClientXrefList);

    @WebMethod
    Response saveOAuthCode(@WebParam(name = "oAuthToken", targetNamespace = "") OAuthCode oAuthToken);

    @WebMethod
    OAuthCode getOAuthCode(@WebParam(name = "code", targetNamespace = "") String code);

    @WebMethod
    OAuthToken getOAuthToken(@WebParam(name = "token", targetNamespace = "") String token);

    @WebMethod
    OAuthToken getOAuthTokenByRefreshToken(@WebParam(name = "refreshToken", targetNamespace = "") String refreshToken);

    @WebMethod
    Response saveOAuthToken(@WebParam(name = "oAuthToken", targetNamespace = "")  OAuthToken oAuthToken);

    @WebMethod
    List<Resource> getAuthorizedScopes(@WebParam(name = "clientId", targetNamespace = "") String clientId,
                                       @WebParam(name = "oAuthToken", targetNamespace = "")  OAuthToken oAuthToken);
    @WebMethod
    List<Resource> getAuthorizedScopesByUser(@WebParam(name = "clientId", targetNamespace = "") String clientId,
                                       @WebParam(name = "userId", targetNamespace = "")  String userId);
    
    AuthProvider getCachedOAuthProviderById(final String id);
    AuthProvider getCachedOAuthProviderByName(final String name);
    @WebMethod
    Response cleanAuthorizedScopes();
    @WebMethod
    Response deAuthorizeClient(String clientId, String userId);
}
