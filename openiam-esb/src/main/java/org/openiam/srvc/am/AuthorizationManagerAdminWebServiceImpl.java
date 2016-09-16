package org.openiam.srvc.am;

import java.util.*;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.openiam.base.request.EntityOwnerRequest;
import org.openiam.base.request.UserEntitlementsMatrixRequest;
import org.openiam.base.response.EntityOwnerResponse;
import org.openiam.base.response.SetStringResponse;
import org.openiam.base.response.StringResponse;
import org.openiam.base.response.UserEntitlementsMatrixResponse;
import org.openiam.model.UserEntitlementsMatrix;
import org.openiam.authmanager.service.AuthorizationManagerAdminService;
import org.openiam.mq.constants.AMAdminAPI;
import org.openiam.mq.constants.AccessRightAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("authorizationManagerAdminWebService")
@WebService(endpointInterface = "org.openiam.srvc.am.AuthorizationManagerAdminWebService",
			targetNamespace = "urn:idm.openiam.org/authmanager/service",
			portName = "AuthorizationManagerAdminWebServicePort", 
			serviceName = "AuthorizationManagerAdminWebService")
public class AuthorizationManagerAdminWebServiceImpl extends AbstractApiService implements AuthorizationManagerAdminWebService {

    public AuthorizationManagerAdminWebServiceImpl() {
        super(OpenIAMQueue.AMAdminQueue);
    }

    @Override
    @WebMethod
	public UserEntitlementsMatrix getUserEntitlementsMatrix(final String entityId, final Date date) {
        UserEntitlementsMatrixRequest request = new UserEntitlementsMatrixRequest();
        request.setUserId(entityId);
        request.setDate(date);

        UserEntitlementsMatrixResponse response= this.manageApiRequest(AMAdminAPI.UserEntitlementsMatrix, request, UserEntitlementsMatrixResponse.class);
        if(response.isFailure()){
            return null;
        }
		return response.getMatrix();
	}
    @Override
    @WebMethod
    public Set<String> getOwnerIdsForResource(final String resourceId, final Date date){
        Set<String> resIds = new HashSet<String>();
        resIds.add(resourceId);
        HashMap<String, SetStringResponse> retVal = this.getOwnerIdsForResourceSet(resIds, date);
        if(retVal==null || retVal.get(resourceId)==null){
            return Collections.emptySet();
        }
        return retVal.get(resourceId).getSetString();
    }
    @Override
    @WebMethod
    public HashMap<String, SetStringResponse> getOwnerIdsForResourceSet(final Set<String> resourceIdSet, final Date date){
        EntityOwnerRequest request = new EntityOwnerRequest();
        request.setDate(date);
        request.setEntityIdSet(resourceIdSet);
        return getOwnerMap(request, AMAdminAPI.OwnerIdsForResourceSet);
    }
    @Override
    @WebMethod
    public Set<String> getOwnerIdsForGroup(String groupId, final Date date){
        Set<String> groupIds = new HashSet<String>();
        groupIds.add(groupId);
        HashMap<String, SetStringResponse> retVal = this.getOwnerIdsForGroupSet(groupIds, date);
        if(retVal==null || retVal.get(groupId)==null){
            return Collections.emptySet();
        }
        return retVal.get(groupId).getSetString();
    }
    @Override
    @WebMethod
    public HashMap<String, SetStringResponse> getOwnerIdsForGroupSet(final Set<String> groupIdSet, final Date date){
        EntityOwnerRequest request = new EntityOwnerRequest();
        request.setDate(date);
        request.setEntityIdSet(groupIdSet);
        return getOwnerMap(request, AMAdminAPI.OwnerIdsForGroupSet);
    }

    private HashMap<String, SetStringResponse> getOwnerMap(EntityOwnerRequest request, AMAdminAPI api){
        EntityOwnerResponse response= this.manageApiRequest(api, request, EntityOwnerResponse.class);
        if(response.isFailure()){
            return new HashMap<>();
        }
        return response.getOwnersMap();
    }
}
