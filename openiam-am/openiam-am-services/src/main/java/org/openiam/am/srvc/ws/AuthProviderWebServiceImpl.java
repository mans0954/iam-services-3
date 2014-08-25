package org.openiam.am.srvc.ws;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.am.srvc.domain.AuthAttributeEntity;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.AuthProviderTypeEntity;
import org.openiam.am.srvc.dozer.converter.AuthAttributeDozerConverter;
import org.openiam.am.srvc.dozer.converter.AuthProviderAttributeDozerConverter;
import org.openiam.am.srvc.dozer.converter.AuthProviderDozerConverter;
import org.openiam.am.srvc.dozer.converter.AuthProviderTypeDozerConverter;
import org.openiam.am.srvc.dto.AuthAttribute;
import org.openiam.am.srvc.dto.AuthProvider;
import org.openiam.am.srvc.dto.AuthProviderAttribute;
import org.openiam.am.srvc.dto.AuthProviderType;
import org.openiam.am.srvc.searchbeans.AuthAttributeSearchBean;
import org.openiam.am.srvc.searchbeans.AuthProviderSearchBean;
import org.openiam.am.srvc.searchbeans.converter.AuthAttributeSearchBeanConverter;
import org.openiam.am.srvc.searchbeans.converter.AuthProviderSearchBeanConverter;
import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebService;
import java.util.*;

@Service("authProviderWS")
@WebService(endpointInterface = "org.openiam.am.srvc.ws.AuthProviderWebService",
            targetNamespace = "urn:idm.openiam.org/srvc/am/service", portName = "AuthProviderWebServicePort",
            serviceName = "AuthProviderWebService")
public class AuthProviderWebServiceImpl implements AuthProviderWebService {
	
	private static Logger log = Logger.getLogger(AuthProviderWebServiceImpl.class);
	
    @Autowired
    private AuthProviderService authProviderService;

    @Autowired
    private AuthAttributeSearchBeanConverter authAttributeSearchBeanConverter;
    @Autowired
    private AuthProviderSearchBeanConverter authProviderSearchBeanConverter;
    @Autowired
    private AuthProviderTypeDozerConverter authProviderTypeDozerConverter;
    @Autowired
    private AuthAttributeDozerConverter authAttributeDozerConverter;
    @Autowired
    private AuthProviderDozerConverter authProviderDozerConverter;
    @Autowired
    private AuthProviderAttributeDozerConverter authProviderAttributeDozerConverter;

    /*
    *==================================================
    * AuthProviderType section
    *===================================================
    */
    @Override
    @Transactional(readOnly = true)
    public AuthProviderType getAuthProviderType(String providerType) {
        return authProviderTypeDozerConverter.convertToDTO(authProviderService.getAuthProviderType(providerType), true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthProviderType> getAuthProviderTypeList() {
        return authProviderTypeDozerConverter.convertToDTOList(authProviderService.getAuthProviderTypeList(), true);
    }

    @Override
    public Response addProviderType(AuthProviderType providerType) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if(providerType == null || StringUtils.isBlank(providerType.getId())) {
                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_TYPE_NOT_SET);
            }
            authProviderService.addProviderType(authProviderTypeDozerConverter.convertToEntity(providerType, false));
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
    public Response deleteProviderType(String providerType) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if(providerType == null || providerType.trim().isEmpty()) {
                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_TYPE_NOT_SET);
            }
            authProviderService.deleteProviderType(providerType);
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
    * AuthAttributeEntity section
    *===================================================
    */
    @Override
    @Transactional(readOnly = true)
    public List<AuthAttribute> findAuthAttributeBeans(AuthAttributeSearchBean searchBean, Integer size, Integer from) {

        final AuthAttributeEntity entity = authAttributeSearchBeanConverter.convert(searchBean);
        final List<AuthAttributeEntity> attributeList = authProviderService.findAuthAttributeBeans(entity, size, from);
        return authAttributeDozerConverter.convertToDTOList(attributeList, (searchBean.isDeepCopy()));
    }

    @Override
    public Integer getNumOfAuthAttributeBeans(AuthAttributeSearchBean searchBean){
          return authProviderService.getNumOfAuthAttributeBeans(authAttributeSearchBeanConverter.convert(searchBean));
    }

    @Override
    public Response addAuthAttribute(AuthAttribute attribute) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if(attribute==null)
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            if(attribute.getAttributeName()==null || attribute.getAttributeName().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.AUTH_ATTRIBUTE_NAME_NOT_SET);
            if(attribute.getProviderType()==null)
                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_TYPE_NOT_SET);
            authProviderService.addAuthAttribute(authAttributeDozerConverter.convertToEntity(attribute,false));
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
    public Response updateAuthAttribute(AuthAttribute attribute) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if(attribute==null)
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            if(attribute.getAttributeName()==null || attribute.getAttributeName().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.AUTH_ATTRIBUTE_NAME_NOT_SET);
            if(attribute.getProviderType()==null)
                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_TYPE_NOT_SET);
            authProviderService.updateAuthAttribute(authAttributeDozerConverter.convertToEntity(attribute, false));
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
    public Response deleteAuthAttribute(String authAttributeId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if(authAttributeId==null || authAttributeId.trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);

            authProviderService.deleteAuthAttribute(authAttributeId);
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
    public Response deleteAuthAttributesByType(String providerType) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if(providerType==null || providerType.trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);

            authProviderService.deleteAuthAttributesByType(providerType);
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
    *  AuthProviderEntity section
    *===================================================
    */
    @Override
    @Transactional(readOnly = true)
    public List<AuthProvider> findAuthProviderBeans(AuthProviderSearchBean searchBean,Integer size,Integer from) {
        final AuthProviderEntity entity = authProviderSearchBeanConverter.convert(searchBean);
        final List<AuthProviderEntity> providerList = authProviderService.findAuthProviderBeans(entity, size, from);
        return authProviderDozerConverter.convertToDTOList(providerList, (searchBean.isDeepCopy()));
    }
    @Override
    public Integer getNumOfAuthProviderBeans(AuthProviderSearchBean searchBean){
         return authProviderService.getNumOfAuthProviderBeans(authProviderSearchBeanConverter.convert(searchBean));
    }

    @Override
    public Response addAuthProvider(AuthProvider provider, final String requestorId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if(provider==null)
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            if(provider.getProviderType()==null || provider.getProviderType().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_TYPE_NOT_SET);
            if(provider.getManagedSysId()==null  || provider.getManagedSysId().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.MANAGED_SYS_NOT_SET);
            if(provider.getName()==null  || provider.getName().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NAME_NOT_SET);
            if(provider.isSignRequest()) {
            	final AuthProviderTypeEntity type = authProviderService.getAuthProviderType(provider.getProviderType());
            	if((provider.getPrivateKey()==null || provider.getPrivateKey().length==0)) {
            		if(type.isHasPrivateKey()) {
            			throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_SECUTITY_KEYS_NOT_SET);
            		}
            	}
            	
            	if(provider.getPublicKey()==null || provider.getPublicKey().length==0) {
            		if(type.isHasPublicKey()) {
            			throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_SECUTITY_KEYS_NOT_SET);
            		}
            	}
            }

            validateAndSyncProviderAttributes(provider);

            final AuthProviderEntity entity = authProviderDozerConverter.convertToEntity(provider, true);
            authProviderService.addAuthProvider(entity, requestorId);
            response.setResponseValue(entity.getProviderId());
        } catch(BasicDataServiceException e) {
        	log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
        	log.error("Error while saving auth provider", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    private void validateAndSyncProviderAttributes(AuthProvider provider) throws BasicDataServiceException{
        AuthAttributeEntity example = new AuthAttributeEntity();
        example.setProviderType(provider.getProviderType());
        List<AuthAttributeEntity> attributeEntityList = authProviderService.findAuthAttributeBeans(example, Integer.MAX_VALUE,0);
//        Set<String> newAttributesIds = new HashSet<String>();
        Map<String, AuthProviderAttribute> attributeMap = new HashMap<String, AuthProviderAttribute>();

        if(provider.getProviderAttributeSet()!=null && !provider.getProviderAttributeSet().isEmpty()){
            for(AuthProviderAttribute attr: provider.getProviderAttributeSet()){
//                newAttributesIds.add(attr.getAttributeId());
                attributeMap.put(attr.getAttributeId(), attr);
            }
        }
        for(AuthAttributeEntity attr: attributeEntityList){
            AuthProviderAttribute providerAttribute = attributeMap.get(attr.getAuthAttributeId());
            boolean isAttributeEmpty= (providerAttribute==null || providerAttribute.getValue()==null || providerAttribute.getValue().trim().isEmpty());
            if(attr.isRequired() && isAttributeEmpty)
                throw new BasicDataServiceException(ResponseCode.AUTH_REQUIRED_PROVIDER_ATTRIBUTE_NOT_SET);
            if(isAttributeEmpty){
                // need to delete attribute from provider.
                providerAttribute = new AuthProviderAttribute();
                providerAttribute.setProviderId(provider.getProviderId());
                providerAttribute.setAttributeId(attr.getAuthAttributeId());
                providerAttribute.setValue(null);
                providerAttribute.setProviderAttributeId("");
                provider.getProviderAttributeSet().add(providerAttribute);
            }
        }
    }

    @Override
    public Response updateAuthProvider(AuthProvider provider, final String requestorId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if(provider==null)
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            if(provider.getProviderType()==null || provider.getProviderType().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_TYPE_NOT_SET);
            if(provider.getManagedSysId()==null  || provider.getManagedSysId().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.MANAGED_SYS_NOT_SET);
            if(provider.getName()==null  || provider.getName().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NAME_NOT_SET);
            if(provider.isSignRequest()) {
            	final AuthProviderTypeEntity type = authProviderService.getAuthProviderType(provider.getProviderType());
            	if((provider.getPrivateKey()==null || provider.getPrivateKey().length==0)) {
            		if(type.isHasPrivateKey()) {
            			throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_SECUTITY_KEYS_NOT_SET);
            		}
            	}
            	
            	if(provider.getPublicKey()==null || provider.getPublicKey().length==0) {
            		if(type.isHasPublicKey()) {
            			throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_SECUTITY_KEYS_NOT_SET);
            		}
            	}
            }

            validateAndSyncProviderAttributes(provider);

            final AuthProviderEntity entity = authProviderDozerConverter.convertToEntity(provider, true);
            authProviderService.updateAuthProvider(entity, requestorId);
            response.setResponseValue(entity.getProviderId());
        } catch(BasicDataServiceException e) {
        	log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
        	log.error("Error while updating auth provider", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteAuthProvider(String providerId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if(providerId==null || providerId.trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);

            authProviderService.deleteAuthProvider(providerId);
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
    public Response deleteAuthProviderByType(String providerType) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if(providerType==null || providerType.trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);

            authProviderService.deleteAuthProviderByType(providerType);
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
    *  AuthProviderAttribute section
    *===================================================
    */
    @Override
    @Transactional(readOnly = true)
    public AuthProviderAttribute getAuthProviderAttribute(String providerId, String name) {
        return authProviderAttributeDozerConverter.convertToDTO(authProviderService.getAuthProviderAttribute(providerId, name), true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthProviderAttribute> getAuthProviderAttributeList(String providerId,Integer size,Integer from) {
        return authProviderAttributeDozerConverter.convertToDTOList(authProviderService.getAuthProviderAttributeList(providerId, size, from), true);
    }

    public Integer getNumOfAuthProviderAttributes(String providerId){
        return authProviderService.getNumOfAuthProviderAttributes(providerId);
    }

    @Override
    public Response addAuthProviderAttribute(AuthProviderAttribute attribute) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if(attribute==null)
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            if(attribute.getProviderId()==null || attribute.getProviderId().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NOT_SET);
            if(attribute.getAttributeId() ==null || attribute.getAttributeId().isEmpty())
                throw new BasicDataServiceException(ResponseCode.AUTH_ATTRIBUTE_NOT_SET);
            if(attribute.getValue() ==null || attribute.getValue().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.AUTH_ATTRIBUTE_VALUE_NOT_SET);


            authProviderService.addAuthProviderAttribute(authProviderAttributeDozerConverter.convertToEntity(attribute,false));
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
    public Response updateAuthProviderAttribute(AuthProviderAttribute attribute) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if(attribute==null)
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            if(attribute.getProviderId()==null || attribute.getProviderId().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NOT_SET);
            if(attribute.getAttributeId() ==null || attribute.getAttributeId().isEmpty())
                throw new BasicDataServiceException(ResponseCode.AUTH_ATTRIBUTE_NOT_SET);
            if(attribute.getValue() ==null || attribute.getValue().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.AUTH_ATTRIBUTE_VALUE_NOT_SET);

            authProviderService.updateAuthProviderAttribute(authProviderAttributeDozerConverter.convertToEntity(attribute,false));
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
    public Response deleteAuthProviderAttributeByName(String providerId, String attributeId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if(providerId==null || providerId.trim().isEmpty() || attributeId==null || attributeId.trim().isEmpty() )
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            authProviderService.deleteAuthProviderAttributeByName(providerId, attributeId);
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
    public Response deleteAuthProviderAttributes(String providerId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if(providerId==null || providerId.trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);

            authProviderService.deleteAuthProviderAttributes(providerId);
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


}
