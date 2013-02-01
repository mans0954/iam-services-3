package org.openiam.am.srvc.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.domain.AuthResourceAMAttributeEntity;
import org.openiam.am.srvc.domain.AuthResourceAttributeMapEntity;
import org.openiam.am.srvc.dozer.converter.AuthResourceAMAttributeDozerConverter;
import org.openiam.am.srvc.dozer.converter.AuthResourceAttributeMapDozerConverter;
import org.openiam.am.srvc.dto.AuthResourceAMAttribute;
import org.openiam.am.srvc.dto.AuthResourceAttributeMap;
import org.openiam.am.srvc.dto.SSOAttribute;
import org.openiam.am.srvc.service.AuthResourceAttributeService;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@Service("authResourceAttributeWS")
@WebService(endpointInterface = "org.openiam.am.srvc.ws.AuthResourceAttributeWebService",
            targetNamespace = "urn:idm.openiam.org/srvc/am/service", portName = "AuthResourceAttributeWebServicePort",
            serviceName = "AuthResourceAttributeWebService")
public class AuthResourceAttributeWebServiceImpl implements AuthResourceAttributeWebService{
    protected final Log log = LogFactory.getLog(this.getClass());
    @Autowired
    private AuthResourceAMAttributeDozerConverter authResourceAMAttributeDozerConverter;
    @Autowired
    private AuthResourceAttributeMapDozerConverter authResourceAttributeMapDozerConverter;
    @Autowired
    private AuthResourceAttributeService authResourceAttributeService;
    /*
    *==================================================
    * AuthResourceAMAttribute section
    *===================================================
    */
    @Override
    public AuthResourceAMAttribute getAmAttribute(String attributeId) {
        return authResourceAMAttributeDozerConverter.convertToDTO(authResourceAttributeService.getAmAttribute(attributeId), true);
    }

    @Override
    public List<AuthResourceAMAttribute> getAmAttributeList() {
        return authResourceAMAttributeDozerConverter.convertToDTOList(authResourceAttributeService.getAmAttributeList(), true);
    }

    @Override
    public Response saveAmAttribute(AuthResourceAMAttribute attribute) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if(attribute==null)
                throw new BasicDataServiceException(ResponseCode.AUTH_RESOURCE_AM_ATTRIBUTE_NOT_SET);
            if(attribute.getAmAttributeId()==null || attribute.getAmAttributeId().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.AUTH_RESOURCE_AM_ATTRIBUTE_ID_NOT_SET);
            if(attribute.getAttributeName()==null || attribute.getAttributeName().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.AUTH_RESOURCE_AM_ATTRIBUTE_NAME_NOT_SET);

            AuthResourceAMAttributeEntity entity = authResourceAttributeService.saveAmAttribute(authResourceAMAttributeDozerConverter.convertToEntity(attribute, false));
            response.setResponseValue(authResourceAMAttributeDozerConverter.convertToDTO(entity, true));
        } catch(BasicDataServiceException e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteAmAttribute(String attributeId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if(attributeId==null || attributeId.trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.AUTH_RESOURCE_AM_ATTRIBUTE_ID_NOT_SET);
            authResourceAttributeService.deleteAmAttribute(attributeId);
        } catch(BasicDataServiceException e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }
    /*
    *==================================================
    * AuthResourceAttributeMap section
    *===================================================
    */
    @Override
    public AuthResourceAttributeMap getAttributeMap(String attributeMapId) {
        return authResourceAttributeMapDozerConverter.convertToDTO(authResourceAttributeService.getAttributeMap(attributeMapId),true);
    }

    @Override
    public List<AuthResourceAttributeMap> getAttributeMapList(String providerId) {
        return authResourceAttributeMapDozerConverter.convertToDTOList(authResourceAttributeService.getAttributeMapList(providerId),true);
    }

    @Override
    public Response saveAttributeMap(AuthResourceAttributeMap attributeMap) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (attributeMap == null)
                throw new BasicDataServiceException(ResponseCode.AUTH_RESOURCE_ATTRIBUTE_MAP_NOT_SET);
            if (attributeMap.getProviderId() == null || attributeMap.getProviderId().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NOT_SET);
            if (attributeMap.getTargetAttributeName() == null || attributeMap.getTargetAttributeName().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.AUTH_RESOURCE_TARGET_ATTRIBUTE_NOT_SET);
            if (attributeMap.getAmAttributeId() == null || attributeMap.getAmAttributeId().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.AUTH_RESOURCE_AM_ATTRIBUTE_NOT_SET);


            AuthResourceAttributeMapEntity entity = authResourceAttributeService.saveAttributeMap(
                    authResourceAttributeMapDozerConverter.convertToEntity(attributeMap, false));
            response.setResponseValue(authResourceAttributeMapDozerConverter.convertToDTO(entity, true));

        } catch(BasicDataServiceException e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    public Response addAttributeMapCollection(List<AuthResourceAttributeMap> attributeMapList) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (attributeMapList == null || attributeMapList.isEmpty())
                throw new BasicDataServiceException(ResponseCode.AUTH_RESOURCE_ATTRIBUTE_MAP_COLLECTION_NOT_SET);

            authResourceAttributeService.saveAttributeMapCollection(authResourceAttributeMapDozerConverter.convertToEntityList(attributeMapList, false));
        } catch(BasicDataServiceException e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;

    }

    @Override
    public Response removeAttributeMap(String attributeMapId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (attributeMapId == null || attributeMapId.trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.AUTH_RESOURCE_ATTRIBUTE_MAP_ID_NOT_SET);

            authResourceAttributeService.removeAttributeMap(attributeMapId);
        } catch(BasicDataServiceException e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    public Response removeAttributeMaps(String providerId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (providerId == null || providerId.trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NOT_SET);

            authResourceAttributeService.removeAttributeMaps(providerId);
        } catch(BasicDataServiceException e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    public List<SSOAttribute> getSSOAttributes(@WebParam(name = "providerId", targetNamespace = "") String providerId,
                                               @WebParam(name = "userId", targetNamespace = "") String userId,
                                               @WebParam(name = "managedSysId", targetNamespace = "")
                                               String managedSysId) {
        return authResourceAttributeService.getSSOAttributes(providerId, userId, managedSysId);
    }
}
