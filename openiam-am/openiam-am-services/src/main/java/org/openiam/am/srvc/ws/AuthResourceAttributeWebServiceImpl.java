package org.openiam.am.srvc.ws;

import org.apache.commons.lang.StringUtils;
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
import org.openiam.exception.BasicDataServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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



    @Override
    @Transactional(readOnly = true)
    public List<AuthResourceAMAttribute> getAmAttributeList() {
        return authResourceAMAttributeDozerConverter.convertToDTOList(authResourceAttributeService.getAmAttributeList(), true);
    }

    /*
    *==================================================
    * AuthResourceAttributeMap section
    *===================================================
    */

    @Override
    public Response saveAttributeMap(AuthResourceAttributeMap attributeMap) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (attributeMap == null) {
                throw new BasicDataServiceException(ResponseCode.AUTH_RESOURCE_ATTRIBUTE_MAP_NOT_SET);
            }
            if (StringUtils.isBlank(attributeMap.getProviderId())) {
                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NOT_SET);
            }
            if (StringUtils.isBlank(attributeMap.getName())) {
                throw new BasicDataServiceException(ResponseCode.AUTH_RESOURCE_TARGET_ATTRIBUTE_NOT_SET);
            }
            if (attributeMap.getAttributeType() == null) {
                throw new BasicDataServiceException(ResponseCode.AUTH_RESOURCE_ATTRIBUTE_TYPE_NOT_SET);
            }
            if ((StringUtils.isBlank(attributeMap.getAmResAttributeId()))
                 &&(StringUtils.isBlank(attributeMap.getAttributeValue()))
                 &&(StringUtils.isBlank(attributeMap.getAmPolicyUrl()))) {
                throw new BasicDataServiceException(ResponseCode.AUTH_RESOURCE_AM_ATTRIBUTE_NOT_SET);
            }

            final AuthResourceAttributeMapEntity entity = authResourceAttributeMapDozerConverter.convertToEntity(attributeMap, false);
            authResourceAttributeService.saveAttributeMap(entity);
            response.setResponseValue(entity.getId());

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
    public List<SSOAttribute> getSSOAttributes(@WebParam(name = "providerId", targetNamespace = "") String providerId,
                                               @WebParam(name = "userId", targetNamespace = "") String userId) {
        return authResourceAttributeService.getSSOAttributes(providerId, userId);
    }

	@Override
	public AuthResourceAttributeMap getAttribute(String attributeMapId) {
		final AuthResourceAttributeMapEntity entity = authResourceAttributeService.getAttribute(attributeMapId);
		return authResourceAttributeMapDozerConverter.convertToDTO(entity, true);
	}
}
