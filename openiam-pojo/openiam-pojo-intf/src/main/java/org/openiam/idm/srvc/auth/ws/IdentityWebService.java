package org.openiam.idm.srvc.auth.ws;


import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.auth.dto.IdentityDto;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/auth/service", name = "IdentityWebService")
public interface IdentityWebService {

    @WebMethod
    String save(@WebParam(name = "identity", targetNamespace = "") IdentityDto identityDto);

    @WebMethod
    Response isValidIdentity(@WebParam(name = "identity", targetNamespace = "") IdentityDto identityDto);

    @WebMethod
    IdentityDto getIdentity(@WebParam(name = "identityId", targetNamespace = "") String identityId);

    @WebMethod
    IdentityDto getIdentityByManagedSys(@WebParam(name = "referredId", targetNamespace = "") String referredId, @WebParam(name = "managedSysId", targetNamespace = "") String managedSysId);

    @WebMethod
    List<IdentityDto> getIdentities(@WebParam(name = "referredId", targetNamespace = "") String referredId);

    @WebMethod
    void deleteIdentity(@WebParam(name = "identityId", targetNamespace = "") String identityId);

    @WebMethod
    void updateIdentity(@WebParam(name = "identity", targetNamespace = "") IdentityDto identityDto);
}
