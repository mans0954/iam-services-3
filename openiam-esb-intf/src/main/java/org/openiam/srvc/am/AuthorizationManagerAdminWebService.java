package org.openiam.srvc.am;

import org.openiam.base.response.SetStringResponse;
import org.openiam.model.UserEntitlementsMatrix;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;


@WebService(targetNamespace = "urn:idm.openiam.org/authmanager/service", 
			name = "AuthorizationManagerAdminWebService")
public interface AuthorizationManagerAdminWebService {
    @WebMethod
    public UserEntitlementsMatrix getUserEntitlementsMatrix(@WebParam(name = "entityId", targetNamespace = "") String entityId,
                                                     @WebParam(name = "date", targetNamespace = "") Date date);

    @WebMethod
    Set<String> getOwnerIdsForResource(@WebParam(name = "resourceId", targetNamespace = "") String resourceId,
    								   @WebParam(name = "date", targetNamespace = "") Date date);

    @WebMethod
    HashMap<String, SetStringResponse> getOwnerIdsForResourceSet(@WebParam(name = "resourceIdSet", targetNamespace = "") Set<String> resourceIdSet,
                                                                 @WebParam(name = "date", targetNamespace = "") Date date);

    @WebMethod
    Set<String> getOwnerIdsForGroup(@WebParam(name = "groupId", targetNamespace = "") String groupId,
    								@WebParam(name = "date", targetNamespace = "") Date date);

    @WebMethod
    HashMap<String, SetStringResponse> getOwnerIdsForGroupSet(@WebParam(name = "groupIdSet", targetNamespace = "") Set<String> groupIdSet,
    														  @WebParam(name = "date", targetNamespace = "") Date date);

}
