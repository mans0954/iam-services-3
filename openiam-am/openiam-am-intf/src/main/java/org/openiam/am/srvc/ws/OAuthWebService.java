package org.openiam.am.srvc.ws;

import org.openiam.am.srvc.dto.AuthProvider;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Created by alexander on 06/07/15.
 */
@WebService(targetNamespace = "urn:idm.openiam.org/srvc/am/service", name = "OAuthWebService")
public interface OAuthWebService {

    @WebMethod
    AuthProvider getClient(@WebParam(name = "clientId", targetNamespace = "") String clientId);
}
