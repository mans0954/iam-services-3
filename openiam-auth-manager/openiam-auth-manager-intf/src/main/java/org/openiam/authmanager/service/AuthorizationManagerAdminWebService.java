package org.openiam.authmanager.service;

import org.openiam.authmanager.common.HashMapResponse;
import org.openiam.authmanager.common.SetStringResponse;
import org.openiam.authmanager.model.UserEntitlementsMatrix;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.HashMap;
import java.util.Set;

@WebService(targetNamespace = "urn:idm.openiam.org/authmanager/service", 
			name = "AuthorizationManagerAdminWebService")
public interface AuthorizationManagerAdminWebService {
    @WebMethod
    UserEntitlementsMatrix getUserEntitlementsMatrix(@WebParam(name = "entityId", targetNamespace = "") final String entityId);

    @WebMethod
    Set<String> getOwnerIdsForResource(@WebParam(name = "resourceId", targetNamespace = "") String resourceId);

    @WebMethod
    HashMap<String, SetStringResponse> getOwnerIdsForResourceSet(@WebParam(name = "resourceIdSet", targetNamespace = "") Set<String> resourceIdSet);

    @WebMethod
    Set<String> getOwnerIdsForGroup(@WebParam(name = "groupId", targetNamespace = "") String groupId);

    @WebMethod
    HashMap<String, SetStringResponse> getOwnerIdsForGroupSet(@WebParam(name = "groupIdSet", targetNamespace = "") Set<String> groupIdSet);

}
