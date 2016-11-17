package org.openiam.srvc.am;

import org.openiam.am.srvc.dto.AuthResourceAMAttribute;
import org.openiam.am.srvc.dto.AuthResourceAttributeMap;
import org.openiam.am.srvc.dto.SSOAttribute;
import org.openiam.base.request.*;
import org.openiam.base.response.AuthResourceAMAttributeListResponse;
import org.openiam.base.response.AuthResourceAttributeMapResponse;
import org.openiam.base.response.SSOAttributeListResponse;
import org.openiam.base.ws.Response;
import org.openiam.mq.constants.api.AuthResourceAttributeAPI;
import org.openiam.mq.constants.queue.am.AuthResourceAttributeQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@Service("authResourceAttributeWS")
@WebService(endpointInterface = "org.openiam.srvc.am.AuthResourceAttributeWebService",
            targetNamespace = "urn:idm.openiam.org/srvc/am/service", portName = "AuthResourceAttributeWebServicePort",
            serviceName = "AuthResourceAttributeWebService")
public class AuthResourceAttributeWebServiceImpl extends AbstractApiService implements AuthResourceAttributeWebService{

    @Autowired
    public AuthResourceAttributeWebServiceImpl(AuthResourceAttributeQueue queue) {
        super(queue);
    }


    @Override
    public List<AuthResourceAMAttribute> getAmAttributeList() {
        AuthResourceAMAttributeListResponse response = this.manageApiRequest(AuthResourceAttributeAPI.GetAmAttributeList, new EmptyServiceRequest(), AuthResourceAMAttributeListResponse.class);
        if(response.isFailure()){
            return null;
        }
        return response.getAmAttributeList();
    }

    /*
    *==================================================
    * AuthResourceAttributeMap section
    *===================================================
    */

    @Override
    public Response saveAttributeMap(AuthResourceAttributeMap attributeMap) {
        return this.manageCrudApiRequest(AuthResourceAttributeAPI.SaveAttributeMap, attributeMap);
    }

    @Override
    public Response removeAttributeMap(String attributeMapId) {
        AuthResourceAttributeMap obj = new AuthResourceAttributeMap();
        obj.setId(attributeMapId);
        return this.manageCrudApiRequest(AuthResourceAttributeAPI.DeleteAttributeMap, obj);
    }

    @Override
    public List<SSOAttribute> getSSOAttributes(@WebParam(name = "providerId", targetNamespace = "") String providerId,
                                               @WebParam(name = "userId", targetNamespace = "") String userId) {
        SSOAttributesRequest request = new SSOAttributesRequest();
        request.setUserId(userId);
        request.setProviderId(providerId);
        SSOAttributeListResponse response = this.manageApiRequest(AuthResourceAttributeAPI.GetSSOAttributes, request, SSOAttributeListResponse.class);
        if(response.isFailure()){
            return null;
        }
        return response.getSsoAttributeList();
    }

	@Override
	public AuthResourceAttributeMap getAttribute(String attributeMapId) {
        IdServiceRequest request = new IdServiceRequest();
        request.setId(attributeMapId);
        AuthResourceAttributeMapResponse response = this.manageApiRequest(AuthResourceAttributeAPI.GetAttribute, request, AuthResourceAttributeMapResponse.class);
        if(response.isFailure()){
            return null;
        }
        return response.getAttributeMap();
	}
}
