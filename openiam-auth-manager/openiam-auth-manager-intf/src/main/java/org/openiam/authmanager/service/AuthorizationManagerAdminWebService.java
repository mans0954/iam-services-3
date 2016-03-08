package org.openiam.authmanager.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.openiam.authmanager.common.SetStringResponse;
import org.openiam.authmanager.model.UserEntitlementsMatrix;

@WebService(targetNamespace = "urn:idm.openiam.org/authmanager/service", 
			name = "AuthorizationManagerAdminWebService")
public interface AuthorizationManagerAdminWebService {
    @WebMethod
    UserEntitlementsMatrix getUserEntitlementsMatrix(@WebParam(name = "entityId", targetNamespace = "") String entityId,
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
