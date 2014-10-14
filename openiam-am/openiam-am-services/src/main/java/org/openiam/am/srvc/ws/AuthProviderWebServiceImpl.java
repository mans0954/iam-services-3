package org.openiam.am.srvc.ws;

import org.apache.commons.collections.CollectionUtils;
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
import org.openiam.exception.EsbErrorToken;
import org.openiam.idm.srvc.auth.spi.AbstractScriptableLoginModule;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebService;
import java.util.*;

@Service("authProviderWS")
@WebService(endpointInterface = "org.openiam.am.srvc.ws.AuthProviderWebService",
            targetNamespace = "urn:idm.openiam.org/srvc/am/service", portName = "AuthProviderWebServicePort",
            serviceName = "AuthProviderWebService")
public class AuthProviderWebServiceImpl implements AuthProviderWebService, ApplicationContextAware {
	
	private static Logger log = Logger.getLogger(AuthProviderWebServiceImpl.class);
	
	private ApplicationContext ctx;
	
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
    @Qualifier("configurableGroovyScriptEngine")
    protected ScriptIntegration scriptRunner;
    
    @Override
    @Transactional(readOnly = true)
    public List<AuthAttribute> findAuthAttributeBeans(AuthAttributeSearchBean searchBean, int from, int size) {

        final AuthAttributeEntity entity = authAttributeSearchBeanConverter.convert(searchBean);
        final List<AuthAttributeEntity> attributeList = authProviderService.findAuthAttributeBeans(entity, size, from);
        return authAttributeDozerConverter.convertToDTOList(attributeList, (searchBean != null) ? searchBean.isDeepCopy() : false);
    }

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
        final List<AuthProvider> results = authProviderDozerConverter.convertToDTOList(providerList, (searchBean != null) ? searchBean.isDeepCopy() : false);
        return results;
    }
    

	@Override
	public int countAuthProviderBeans(AuthProviderSearchBean searchBean) {
		final AuthProviderEntity entity = authProviderSearchBeanConverter.convert(searchBean);
		return authProviderService.countAuthProviderBeans(entity);
	}

    
    @Override
    public Response saveAuthProvider(AuthProvider provider, final String requestorId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if(provider==null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            if(StringUtils.isBlank(provider.getProviderType())) {
                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_TYPE_NOT_SET);
            }
            if(StringUtils.isBlank(provider.getManagedSysId())) {
                throw new BasicDataServiceException(ResponseCode.MANAGED_SYS_NOT_SET);
            }
            if(StringUtils.isBlank(provider.getName())) {
                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NAME_NOT_SET);
            }
            
            final AuthProviderTypeEntity type = authProviderService.getAuthProviderType(provider.getProviderType());
            if(type == null) {
            	throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_TYPE_NOT_SET);
            }
            
            if(type.isUsesGroovyScript() && StringUtils.isNotBlank(provider.getGroovyScriptURL())) {
            	if(!scriptRunner.scriptExists(provider.getGroovyScriptURL())) {
            		throw new BasicDataServiceException(ResponseCode.FILE_DOES_NOT_EXIST);
            	}
            	
            	if(!(scriptRunner.instantiateClass(null, provider.getGroovyScriptURL()) instanceof AbstractScriptableLoginModule)) {
            		final EsbErrorToken errorToken = new EsbErrorToken();
            		errorToken.setClassName(AbstractScriptableLoginModule.class.getCanonicalName());
            		throw new BasicDataServiceException(ResponseCode.GROOVY_CLASS_MUST_EXTEND_LOGIN_MODULE, errorToken);
            	}
            } else {
            	provider.setGroovyScriptURL(null);
            }
            
            if(type.isUsesSpringBean() && StringUtils.isNotBlank(provider.getSpringBeanName())) {
            	if(!ctx.containsBean(provider.getSpringBeanName())) {
            		throw new BasicDataServiceException(ResponseCode.INVALID_SPRING_BEAN);
            	}
            } else {
            	provider.setSpringBeanName(null);
            }
            
            if(type.isPasswordPolicyRequired()) {
            	if(StringUtils.isBlank(provider.getPolicyId())) {
                	throw new BasicDataServiceException(ResponseCode.POLICY_NOT_SET);
            	}
            }
            
            if(provider.isSignRequest()) {
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
            authProviderService.saveAuthProvider(entity, requestorId);
            response.setResponseValue(entity.getId());
        } catch(BasicDataServiceException e) {
        	log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
        } catch(Throwable e) {
        	log.error("Error while saving auth provider", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }
    

    private void validateAndSyncProviderAttributes(AuthProvider provider) throws BasicDataServiceException{
        final AuthAttributeEntity example = new AuthAttributeEntity();
        if(StringUtils.isNotBlank(provider.getProviderType())) {
        	example.setType(new AuthProviderTypeEntity());
        	example.getType().setId(provider.getProviderType());
        }
        final List<AuthAttributeEntity> attributeEntityList = authProviderService.findAuthAttributeBeans(example, Integer.MAX_VALUE,0);
//        Set<String> newAttributesIds = new HashSet<String>();
        final Map<String, AuthProviderAttribute> attributeMap = new HashMap<String, AuthProviderAttribute>();

        if(CollectionUtils.isNotEmpty(provider.getAttributes())){
            for(final AuthProviderAttribute attr: provider.getAttributes()){
//                newAttributesIds.add(attr.getAttributeId());
                attributeMap.put(attr.getAttributeId(), attr);
            }
        }
        for(final AuthAttributeEntity attr: attributeEntityList){
        	AuthProviderAttribute providerAttribute = attributeMap.get(attr.getId());
        	final boolean isAttributeEmpty= (providerAttribute==null || StringUtils.isEmpty(providerAttribute.getValue()));
            if(attr.isRequired() && isAttributeEmpty)
                throw new BasicDataServiceException(ResponseCode.AUTH_REQUIRED_PROVIDER_ATTRIBUTE_NOT_SET);
            if(isAttributeEmpty){
                // need to delete attribute from provider.
                providerAttribute = new AuthProviderAttribute();
                providerAttribute.setProviderId(provider.getId());
                providerAttribute.setAttributeId(attr.getId());
                providerAttribute.setValue(null);
                providerAttribute.setId("");
                provider.getAttributes().add(providerAttribute);
            }
        }
    }

    @Override
    public Response deleteAuthProvider(String providerId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if(StringUtils.isBlank(providerId)) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            final AuthProviderEntity searchBean = new AuthProviderEntity();
            searchBean.setId(providerId);
            final List<AuthProviderEntity> providers = authProviderService.findAuthProviderBeans(searchBean, 1, 0);
            if(CollectionUtils.isEmpty(providers)) {
            	throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
            }
            
            final AuthProviderEntity entity = providers.get(0);
            if(entity.isDefaultProvider()) {
            	throw new BasicDataServiceException(ResponseCode.CANNOT_DELETE_DEFAULT_AUTH_PROVIDER);
            }

            authProviderService.deleteAuthProvider(providerId);
        } catch(BasicDataServiceException e) {
        	log.warn(e.getMessage(), e);
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
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.ctx = ctx;
	}
}
