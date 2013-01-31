package org.openiam.am.srvc.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dozer.converter.AuthResourceAttributeMapDozerConverter;
import org.openiam.am.srvc.searchbeans.converter.AuthResourceAttributeMapSearchBeanConverter;
import org.openiam.am.srvc.service.AuthResourceAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("authResourceAttributeWS")
@WebService(endpointInterface = "org.openiam.am.srvc.ws.AuthResourceAttributeWebService",
            targetNamespace = "urn:idm.openiam.org/srvc/am/service", portName = "AuthResourceAttributeWebServicePort",
            serviceName = "AuthResourceAttributeWebService")
public class AuthResourceAttributeWebServiceImpl implements AuthResourceAttributeWebService{
    protected final Log log = LogFactory.getLog(this.getClass());
    @Autowired
    private AuthResourceAttributeMapSearchBeanConverter authResourceAttributeMapSearchBeanConverter;
    @Autowired
    private AuthResourceAttributeMapDozerConverter authResourceAttributeMapDozerConverter;
    @Autowired
    private AuthResourceAttributeService authResourceAttributeBуService;

//    @Override
//    public AttributeMap getAttributeMap(String attributeId) throws
//            Exception {
//        log.debug("Got getAttributeMap request. Params: attributeId="+attributeId);
//        return authResourceAttributeDozerConverter.convertToDTO(authResourceAttributeBуService.getAttributeMap(attributeId), true);
//    }
//
//    @Override
//    public List<AttributeMap> getAllAttributeMapListByResourceId(String resourceId) throws Exception {
//        log.debug("Got getAttributeMapCollection request. Params: resourceId="+resourceId);
//        return authResourceAttributeDozerConverter.convertToDTOList(
//                authResourceAttributeBуService.getAttributeMapCollection(resourceId), true);
//    }
//
//    @Override
//    public List<AttributeMap> getAttributeMapListByResourceId(String resourceId,Integer from,Integer size) throws Exception {
//        log.debug("Got getAttributeMapCollection request. Params: resourceId="+resourceId+"; from="+from+"; size="+size);
//        return authResourceAttributeDozerConverter.convertToDTOList(authResourceAttributeBуService.getAttributeMapCollection(resourceId, from, size), true);
//    }
//
//    @Override
//    public List<AttributeMap> getAllAttributeMapListBySearchCriteria(AuthResourceAttributeSearchBean searchBean) throws
//            Exception {
//          return getAttributeMapListBySearchCriteria(searchBean, 0, Integer.MAX_VALUE);
//    }
//
//    @Override
//    public List<AttributeMap> getAttributeMapListBySearchCriteria(AuthResourceAttributeSearchBean searchBean,
//                                                                  Integer from, Integer size) throws Exception {
//        AuthResourceAttributeEntity entity = authResourceAttributeSearchBeanConverter.convert(searchBean);
//        return authResourceAttributeDozerConverter.convertToDTOList(authResourceAttributeBуService.getAttributeMapCollection(entity, from, size), searchBean.isDeepCopy());
//    }
//
//    @Override
//    public Response addAttributeMap(AttributeMap attribute) throws Exception {
//        final Response response = new Response(ResponseStatus.SUCCESS);
//        try {
//            if(attribute==null)
//                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
//            if (attribute.getResourceId() == null || attribute.getResourceId() .trim().isEmpty())
//                throw new BasicDataServiceException(ResponseCode.RESOURCE_PROP_MISSING);
//            if (attribute.getTargetAttributeName() == null || attribute.getTargetAttributeName().trim().isEmpty())
//                throw new BasicDataServiceException(ResponseCode.AUTH_RESOURCE_ATTRIBUTE_NAME_NOT_SET);
//
//            AuthResourceAttributeEntity entity = authResourceAttributeDozerConverter.convertToEntity(attribute, true);
//            attribute = authResourceAttributeDozerConverter.convertToDTO(authResourceAttributeBуService.addAttributeMap(entity),true);
//
//            response.setResponseValue(attribute);
//        } catch(BasicDataServiceException e) {
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorCode(e.getCode());
//        } catch(Throwable e) {
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//        }
//        return response;
//    }
//
//    @Override
//    public Response addAttributeMapCollection(List<AttributeMap> attributeList) throws Exception {
//        final Response response = new Response(ResponseStatus.SUCCESS);
//        try {
//            if(attributeList==null || attributeList.isEmpty())
//                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
//
//            List<AuthResourceAttributeEntity> entityList = authResourceAttributeDozerConverter.convertToEntityList(
//                    attributeList, true);
//
//            authResourceAttributeBуService.addAttributeMapCollection(entityList);
//
//        } catch(BasicDataServiceException e) {
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorCode(e.getCode());
//        } catch(Throwable e) {
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//        }
//        return response;
//    }
//
//    @Override
//    public Response updateAttributeMap(AttributeMap attribute) throws Exception {
//        final Response response = new Response(ResponseStatus.SUCCESS);
//        try {
//            if(attribute==null || attribute.getAttributeMapId()==null || attribute.getAttributeMapId().trim().isEmpty())
//                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
//            if (attribute.getTargetAttributeName() == null || attribute.getTargetAttributeName().trim().isEmpty())
//                throw new BasicDataServiceException(ResponseCode.AUTH_RESOURCE_ATTRIBUTE_NAME_NOT_SET);
//
//            AuthResourceAttributeEntity entity = authResourceAttributeDozerConverter.convertToEntity(attribute, true);
//            attribute = authResourceAttributeDozerConverter.convertToDTO(authResourceAttributeBуService.updateAttributeMap(
//                    entity),true);
//            response.setResponseValue(attribute);
//        } catch(BasicDataServiceException e) {
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorCode(e.getCode());
//        } catch(Throwable e) {
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//        }
//        return response;
//    }
//
//    @Override
//    public Response removeAttributeMap(String attributeId) throws
//            Exception {
//        final Response response = new Response(ResponseStatus.SUCCESS);
//        try {
//            if(attributeId==null || attributeId.trim().isEmpty())
//                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
//            authResourceAttributeBуService.removeAttributeMap(attributeId);
//        } catch(BasicDataServiceException e) {
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorCode(e.getCode());
//        } catch(Throwable e) {
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//        }
//        return response;
//    }
//
//    @Override
//    public Response removeResourceAttributeMaps(String resourceId) throws Exception {
//        final Response response = new Response(ResponseStatus.SUCCESS);
//        try {
//            if(resourceId==null || resourceId.trim().isEmpty())
//                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
//            Integer count = authResourceAttributeBуService.removeResourceAttributeMaps(resourceId);
//            response.setResponseValue(count);
//        } catch(BasicDataServiceException e) {
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorCode(e.getCode());
//        } catch(Throwable e) {
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//        }
//        return response;
//    }
//
//    @Override
//    public List<Attribute> getSSOAttributes(String resourceId, String principalName, String securityDomain, String managedSysId) {
//        return getSSOAttributesByPages(resourceId, principalName, securityDomain, managedSysId, 0, Integer.MAX_VALUE);
//    }
//
//    @Override
//    public List<Attribute> getSSOAttributesByPages(String resourceId,String principalName,String securityDomain,String managedSysId,
//                                                   Integer from, Integer size) {
//        return authResourceAttributeBуService.getSSOAttributes(resourceId, principalName, securityDomain, managedSysId, from, size);
//    }
//
//    @Override
//    public Integer getNumOfAttributeMapList(String resourceId) throws Exception {
//        return authResourceAttributeBуService.getNumOfAttributeMapList(resourceId);
//    }
//
//    @Override
//    public Integer getNumOfAttributeMapListBySearchCriteria(AuthResourceAttributeSearchBean searchBean) throws
//            Exception {
//        return authResourceAttributeBуService.getNumOfAttributeMapList(authResourceAttributeSearchBeanConverter.convert(searchBean));
//    }
//
//    @Override
//    public Integer getNumOfSSOAttributes(@WebParam(name = "resourceId", targetNamespace = "") String resourceId) throws
//            Exception {
//        return getNumOfAttributeMapList(resourceId);
//    }
}
