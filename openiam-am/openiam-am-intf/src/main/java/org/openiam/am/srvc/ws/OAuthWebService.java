package org.openiam.am.srvc.ws;

import org.openiam.am.srvc.dto.AuthProvider;
import org.openiam.am.srvc.dto.OAuthCode;
import org.openiam.am.srvc.dto.OAuthToken;
import org.openiam.am.srvc.dto.OAuthUserClientXref;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.res.dto.Resource;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

/**
 * Created by alexander on 06/07/15.
 */
@WebService(targetNamespace = "urn:idm.openiam.org/srvc/am/service", name = "OAuthWebService")
public interface OAuthWebService {

    @WebMethod
    AuthProvider getClient(@WebParam(name = "clientId", targetNamespace = "") String clientId);

    @WebMethod
    List<Resource> getScopesForAuthrorization(@WebParam(name = "clientId", targetNamespace = "") String clientId,
                                              @WebParam(name = "userId", targetNamespace = "") String userId,
                                              @WebParam(name = "language", targetNamespace = "")  Language language);

    @WebMethod
    Response saveClientScopeAuthorization(@WebParam(name = "providerId", targetNamespace = "") String providerId,
                                          @WebParam(name = "userId", targetNamespace = "") String userId,
                                          @WebParam(name = "oauthUserClientXrefList", targetNamespace = "") List<OAuthUserClientXref> oauthUserClientXrefList);

    @WebMethod
    Response saveOAuthCode(@WebParam(name = "oAuthToken", targetNamespace = "") OAuthCode oAuthToken);

    @WebMethod
    OAuthCode getOAuthCodeByClientAndUser(@WebParam(name = "providerId", targetNamespace = "") String providerId,
                            @WebParam(name = "userId", targetNamespace = "") String userId);

    @WebMethod
    OAuthCode getOAuthCode(@WebParam(name = "code", targetNamespace = "") String code);

    @WebMethod
    Response saveOAuthToken(@WebParam(name = "oAuthToken", targetNamespace = "")  OAuthToken oAuthToken);
}
