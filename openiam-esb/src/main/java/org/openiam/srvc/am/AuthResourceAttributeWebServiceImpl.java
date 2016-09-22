package org.openiam.srvc.am;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.domain.AuthResourceAttributeMapEntity;
import org.openiam.am.srvc.dozer.converter.AuthResourceAMAttributeDozerConverter;
import org.openiam.am.srvc.dozer.converter.AuthResourceAttributeMapDozerConverter;
import org.openiam.am.srvc.dto.AuthResourceAMAttribute;
import org.openiam.am.srvc.dto.AuthResourceAttributeMap;
import org.openiam.am.srvc.dto.SSOAttribute;
import org.openiam.am.srvc.service.AuthResourceAttributeService;
import org.openiam.base.request.BaseGrudServiceRequest;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.request.SSOAttributesRequest;
import org.openiam.base.response.AuthResourceAMAttributeListResponse;
import org.openiam.base.response.AuthResourceAttributeMapResponse;
import org.openiam.base.response.SSOAttributeListResponse;
import org.openiam.base.response.StringResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AuthResourceAttributeAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@Service("authResourceAttributeWS")
@WebService(endpointInterface = "org.openiam.srvc.am.AuthResourceAttributeWebService",
            targetNamespace = "urn:idm.openiam.org/srvc/am/service", portName = "AuthResourceAttributeWebServicePort",
            serviceName = "AuthResourceAttributeWebService")
public class AuthResourceAttributeWebServiceImpl extends AbstractApiService implements AuthResourceAttributeWebService{

    public AuthResourceAttributeWebServiceImpl() {
        super(OpenIAMQueue.AuthResourceAttributeQueue);
    }


    @Override
    public List<AuthResourceAMAttribute> getAmAttributeList() {
        AuthResourceAMAttributeListResponse response = this.manageApiRequest(AuthResourceAttributeAPI.GetAmAttributeList, new BaseServiceRequest(), AuthResourceAMAttributeListResponse.class);
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
        StringResponse response= this.manageApiRequest(AuthResourceAttributeAPI.SaveAttributeMap, new BaseGrudServiceRequest<AuthResourceAttributeMap>(attributeMap), StringResponse.class);
        return response.convertToBase();
    }

    @Override
    public Response removeAttributeMap(String attributeMapId) {
        IdServiceRequest request = new IdServiceRequest();
        request.setId(attributeMapId);
        return this.manageApiRequest(AuthResourceAttributeAPI.DeleteAttributeMap, request, Response.class);
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
