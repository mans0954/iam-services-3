package org.openiam.authmanager.service;

import org.openiam.authmanager.common.HashMapResponse;
import org.openiam.authmanager.common.SetStringResponse;
import org.openiam.authmanager.model.ObjectOwnerBean;
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
	public UserEntitlementsMatrix getUserEntitlementsMatrix(@WebParam(name = "entityId", targetNamespace = "") final String entityId);

    @WebMethod
    public Set<String> getOwnerIdsForResource(@WebParam(name = "resourceId", targetNamespace = "") String resourceId);

    @WebMethod
    public ObjectOwnerBean getOwnerIdsForResourceSet(@WebParam(name = "resourceIdSet", targetNamespace = "") Set<String> resourceIdSet);

    @WebMethod
    public Set<String> getOwnerIdsForGroup(@WebParam(name = "groupId", targetNamespace = "") String groupId);

    @WebMethod
    public ObjectOwnerBean getOwnerIdsForGroupSet(@WebParam(name = "groupIdSet", targetNamespace = "") Set<String> groupIdSet);

    @WebMethod
    public Set<String> getOwnerIdsForRole(@WebParam(name = "roleId", targetNamespace = "") String roleId);

    @WebMethod
    public ObjectOwnerBean getOwnerIdsForRoleSet(@WebParam(name = "roleIdSet", targetNamespace = "") Set<String> roleIdSet);
}
