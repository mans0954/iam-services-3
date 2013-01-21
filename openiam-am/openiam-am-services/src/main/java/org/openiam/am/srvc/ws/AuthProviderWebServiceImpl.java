package org.openiam.am.srvc.ws;

import org.openiam.am.srvc.domain.AuthAttributeEntity;
import org.openiam.am.srvc.domain.AuthProviderEntity;
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
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import java.util.List;

@Service("authProviderWS")
@WebService(endpointInterface = "org.openiam.am.srvc.ws.AuthProviderWebService",
            targetNamespace = "urn:idm.openiam.org/srvc/am/service", portName = "AuthProviderWebServicePort",
            serviceName = "AuthProviderWebService")
public class AuthProviderWebServiceImpl implements AuthProviderWebService {
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
    public AuthProviderType getAuthProviderType(String providerType) {
        return authProviderTypeDozerConverter.convertToDTO(authProviderService.getAuthProviderType(providerType), true);
    }

    @Override
    public List<AuthProviderType> getAuthProviderTypeList() {
        return authProviderTypeDozerConverter.convertToDTOList(authProviderService.getAuthProviderTypeList(), true);
    }

    @Override
    public Response addProviderType(AuthProviderType providerType) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if(providerType == null || providerType.getProviderType() == null || providerType.getProviderType().trim().isEmpty()) {
                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_TYPE_NOT_SET);
            }
            authProviderService.addProviderType(authProviderTypeDozerConverter.convertToEntity(providerType, false));
        } catch(BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
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
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
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
    public List<AuthAttribute> findAuthAttributeBeans(AuthAttributeSearchBean searchBean, Integer size, Integer from) {

        final AuthAttributeEntity entity = authAttributeSearchBeanConverter.convert(searchBean);
        final List<AuthAttributeEntity> attributeList = authProviderService.findAuthAttributeBeans(entity, size, from);
        return authAttributeDozerConverter.convertToDTOList(attributeList, (searchBean.isDeepCopy()));
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
            authProviderService.addAuthAttribute(authAttributeDozerConverter.convertToEntity(attribute,true));
        } catch(BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
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
            authProviderService.updateAuthAttribute(authAttributeDozerConverter.convertToEntity(attribute, true));
        } catch(BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
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
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
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
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
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
    public List<AuthProvider> findAuthProviderBeans(AuthProviderSearchBean searchBean,Integer size,Integer from) {
        final AuthProviderEntity entity = authProviderSearchBeanConverter.convert(searchBean);
        final List<AuthProviderEntity> providerList = authProviderService.findAuthProviderBeans(entity, size, from);
        return authProviderDozerConverter.convertToDTOList(providerList, (searchBean.isDeepCopy()));
    }

    @Override
    public Response addAuthProvider(AuthProvider provider) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if(provider==null)
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            if(provider.getProviderType()==null || provider.getProviderType().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_TYPE_NOT_SET);
            if(provider.getManagedSysId()==null  || provider.getManagedSysId().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.MANAGED_SYS_NOT_SET);
            if(provider.getResourceId()==null  || provider.getResourceId().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.RESOURCE_PROP_MISSING);
            if(provider.getName()==null  || provider.getName().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NAME_NOT_SET);


            authProviderService.addAuthProvider(authProviderDozerConverter.convertToEntity(provider, true));
        } catch(BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    public Response updateAuthProvider(AuthProvider provider) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if(provider==null)
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            if(provider.getProviderType()==null || provider.getProviderType().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_TYPE_NOT_SET);
            if(provider.getManagedSysId()==null  || provider.getManagedSysId().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.MANAGED_SYS_NOT_SET);
            if(provider.getResourceId()==null  || provider.getResourceId().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.RESOURCE_PROP_MISSING);
            if(provider.getName()==null  || provider.getName().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NAME_NOT_SET);

            authProviderService.updateAuthProvider(authProviderDozerConverter.convertToEntity(provider, true));
        } catch(BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
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
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
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
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
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
    public AuthProviderAttribute getAuthProviderAttribute(String providerId, String name) {
        return authProviderAttributeDozerConverter.convertToDTO(authProviderService.getAuthProviderAttribute(providerId, name), true);
    }

    @Override
    public List<AuthProviderAttribute> getAuthProviderAttributeList(String providerId,Integer size,Integer from) {
        return authProviderAttributeDozerConverter.convertToDTOList(authProviderService.getAuthProviderAttributeList(providerId, size, from), true);
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


            authProviderService.addAuthProviderAttribute(authProviderAttributeDozerConverter.convertToEntity(attribute,true));
        } catch(BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
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

            authProviderService.updateAuthProviderAttribute(authProviderAttributeDozerConverter.convertToEntity(attribute,true));
        } catch(BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
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
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
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
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }


}
