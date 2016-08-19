package org.openiam.srvc.am;

import org.openiam.am.srvc.dto.*;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.thread.Sweepable;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import java.util.List;

/**
 * Created by alexander on 06/07/15.
 */
@WebService(targetNamespace = "urn:idm.openiam.org/srvc/am/service", name = "OAuthWebService")
public interface OAuthWebService extends Sweepable {

    @WebMethod
    AuthProvider getClient(@WebParam(name = "clientId", targetNamespace = "") String clientId);

    @WebMethod
    OAuthScopesResponse getScopesForAuthrorization(@WebParam(name = "clientId", targetNamespace = "") String clientId,
                                              @WebParam(name = "userId", targetNamespace = "") String userId,
                                              @WebParam(name = "lang", targetNamespace = "")  Language language);

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
                                       @WebParam(name = "userId", targetNamespace = "") String userId,
                                       @WebParam(name = "lang", targetNamespace = "")  Language language);
    
    public AuthProvider getCachedOAuthProviderById(final String id);
    public AuthProvider getCachedOAuthProviderByName(final String name);
}
