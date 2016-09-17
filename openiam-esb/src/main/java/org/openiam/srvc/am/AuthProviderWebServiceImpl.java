package org.openiam.srvc.am;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.cert.groovy.DefaultCertToIdentityConverter;
import org.openiam.am.srvc.domain.AuthAttributeEntity;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.AuthProviderTypeEntity;
import org.openiam.am.srvc.dozer.converter.AuthAttributeDozerConverter;
import org.openiam.am.srvc.dozer.converter.AuthProviderDozerConverter;
import org.openiam.am.srvc.dozer.converter.AuthProviderTypeDozerConverter;
import org.openiam.am.srvc.dto.AuthAttribute;
import org.openiam.am.srvc.dto.AuthProvider;
import org.openiam.am.srvc.dto.AuthProviderAttribute;
import org.openiam.am.srvc.dto.AuthProviderType;
import org.openiam.am.srvc.searchbean.AuthAttributeSearchBean;
import org.openiam.am.srvc.searchbean.AuthProviderSearchBean;
import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.am.srvc.service.ContentProviderService;
import org.openiam.base.request.BaseGrudServiceRequest;
import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.*;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.exception.EsbErrorToken;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.auth.spi.AbstractSMSOTPModule;
import org.openiam.idm.srvc.auth.spi.AbstractScriptableLoginModule;
import org.openiam.idm.srvc.meta.service.MetadataService;
import org.openiam.mq.constants.AuthProviderAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.script.ScriptIntegration;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("authProviderWS")
@WebService(endpointInterface = "org.openiam.srvc.am.AuthProviderWebService",
            targetNamespace = "urn:idm.openiam.org/srvc/am/service", portName = "AuthProviderWebServicePort",
            serviceName = "AuthProviderWebService")
public class AuthProviderWebServiceImpl extends AbstractApiService implements AuthProviderWebService {

    private static final Log LOG = LogFactory.getLog(AuthProviderWebServiceImpl.class);

    private ApplicationContext ctx;

    @Autowired
    private AuthProviderService authProviderService;

    @Autowired
    private AuthProviderTypeDozerConverter authProviderTypeDozerConverter;
    @Autowired
    private AuthAttributeDozerConverter authAttributeDozerConverter;
    @Autowired
    private AuthProviderDozerConverter authProviderDozerConverter;

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private ContentProviderService contentProviderService;

    @Autowired
    private LoginDataService loginDataService;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    protected ScriptIntegration scriptRunner;

    public AuthProviderWebServiceImpl() {
        super(OpenIAMQueue.AuthProviderQueue);
    }

    @Override
//    @Transactional(readOnly = true)
    public List<AuthAttribute> findAuthAttributeBeans(AuthAttributeSearchBean searchBean, int from, int size) {
        BaseSearchServiceRequest<AuthAttributeSearchBean> request = new BaseSearchServiceRequest<>(searchBean, from, size);
        AuthAttributeListResponse response = this.manageApiRequest(AuthProviderAPI.FindAuthAttributes, request, AuthAttributeListResponse.class);
        if(response.isFailure()){
            return Collections.EMPTY_LIST;
        }
        return response.getAttributeList();
//        final List<AuthAttributeEntity> attributeList = authProviderService.findAuthAttributeBeans(searchBean, size, from);
//        return authAttributeDozerConverter.convertToDTOList(attributeList, (searchBean != null) ? searchBean.isDeepCopy() : false);
    }

    /*
    *==================================================
    * AuthProviderType section
    *===================================================
    */
    @Override
//    @Transactional(readOnly = true)
    public AuthProviderType getAuthProviderType(String providerType) {
        IdServiceRequest request = new IdServiceRequest();
        request.setId(providerType);

        AuthProviderTypeResponse response = this.manageApiRequest(AuthProviderAPI.GetAuthProviderType, request, AuthProviderTypeResponse.class);
        if(response.isFailure()){
            return null;
        }
        return response.getAuthProviderType();
//        return authProviderTypeDozerConverter.convertToDTO(authProviderService.getAuthProviderType(providerType), true);
    }

    @Override
//    @Transactional(readOnly = true)
    public List<AuthProviderType> getAuthProviderTypeList() {
        AuthProviderTypeListResponse response = this.manageApiRequest(AuthProviderAPI.GetAuthProviderTypeList, new BaseServiceRequest(), AuthProviderTypeListResponse.class);
        if(response.isFailure()){
            return Collections.EMPTY_LIST;
        }
        return response.getAuthProviderTypeList();
//        return authProviderTypeDozerConverter.convertToDTOList(authProviderService.getAuthProviderTypeList(), true);
    }

    @Override
//    @Transactional(readOnly = true)
    public List<AuthProviderType> getSocialAuthProviderTypeList(){
        AuthProviderTypeListResponse response = this.manageApiRequest(AuthProviderAPI.GetSocialAuthProviderTypeList, new BaseServiceRequest(), AuthProviderTypeListResponse.class);
        if(response.isFailure()){
            return Collections.EMPTY_LIST;
        }
        return response.getAuthProviderTypeList();
//        return authProviderTypeDozerConverter.convertToDTOList(authProviderService.getSocialAuthProviderTypeList(), true);
    }

    @Override
    public Response addProviderType(AuthProviderType providerType) {
        BaseGrudServiceRequest<AuthProviderType> request = new BaseGrudServiceRequest<>(providerType);
        return this.manageApiRequest(AuthProviderAPI.AddProviderType, request, Response.class);

//        final Response response = new Response(ResponseStatus.SUCCESS);
//        try {
//            if(providerType == null || StringUtils.isBlank(providerType.getId())) {
//                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_TYPE_NOT_SET);
//            }
//            authProviderService.addProviderType(authProviderTypeDozerConverter.convertToEntity(providerType, false));
//        } catch(BasicDataServiceException e) {
//            LOG.error(e.getMessage(), e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorCode(e.getCode());
//        } catch(Throwable e) {
//            LOG.error(e.getMessage(), e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//        }
//        return response;
    }

    /*
    *==================================================
    *  AuthProviderEntity section
    *===================================================
    */
    @Override
//    @Transactional(readOnly = true)
    public List<AuthProvider> findAuthProviderBeans(final AuthProviderSearchBean searchBean, final int from, final int size) {
        BaseSearchServiceRequest<AuthProviderSearchBean> request = new BaseSearchServiceRequest<>(searchBean, from, size);

        AuthProviderListResponse response = this.manageApiRequest(AuthProviderAPI.FindAuthProviders, request, AuthProviderListResponse.class);
        if(response.isFailure()){
            return Collections.EMPTY_LIST;
        }
        return response.getAuthProviderList();
//
//
//        final List<AuthProviderEntity> providerList = authProviderService.findAuthProviderBeans(searchBean, from, size);
//        final List<AuthProvider> results = authProviderDozerConverter.convertToDTOList(providerList, (searchBean != null) ? searchBean.isDeepCopy() : false);
//        return results;
    }


    @Override
    public int countAuthProviderBeans(AuthProviderSearchBean searchBean) {
        BaseSearchServiceRequest<AuthProviderSearchBean> request = new BaseSearchServiceRequest<>(searchBean);
        IntResponse response = this.manageApiRequest(AuthProviderAPI.CountAuthProviders, request, IntResponse.class);
        if(response.isFailure()){
            return 0;
        }
        return response.getValue();
//        return authProviderService.countAuthProviderBeans(searchBean);
    }
    @Override
//    @Transactional(readOnly=true)
    public AuthProvider getAuthProvider(String providerId) {
        IdServiceRequest request=new IdServiceRequest();
        request.setId(providerId);

        AuthProviderResponse response = this.manageApiRequest(AuthProviderAPI.GetAuthProvider, request, AuthProviderResponse.class);
        if(response.isFailure()){
            return null;
        }
        return response.getAuthProvider();
//
//        final AuthProviderEntity entity = authProviderService.getAuthProvider(providerId);
//        return authProviderDozerConverter.convertToDTO(entity, true);
    }

    @Override
    public Response saveAuthProvider(AuthProvider provider, final String requestorId) {
        BaseGrudServiceRequest<AuthProvider> request = new BaseGrudServiceRequest<>(provider);
        request.setRequesterId(requestorId);
        StringResponse response = this.manageApiRequest(AuthProviderAPI.SaveAuthProvider, request, StringResponse.class);
        return response.convertToBase();

//        final Response response = new Response(ResponseStatus.SUCCESS);
//        try {
//            if(provider==null) {
//                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
//            }
//            if(StringUtils.isBlank(provider.getProviderType())) {
//                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_TYPE_NOT_SET);
//            }
//            if(StringUtils.isBlank(provider.getManagedSysId())) {
//                throw new BasicDataServiceException(ResponseCode.MANAGED_SYSTEM_NOT_SET);
//            }
//            if(StringUtils.isBlank(provider.getName())) {
//                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NAME_NOT_SET);
//            }
//
//            final AuthProviderTypeEntity type = authProviderService.getAuthProviderType(provider.getProviderType());
//            if(type == null) {
//                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_TYPE_NOT_SET);
//            }
//
//            if(type.isUsesGroovyScript() && StringUtils.isNotBlank(provider.getGroovyScriptURL())) {
//                if(!scriptRunner.scriptExists(provider.getGroovyScriptURL())) {
//                    throw new BasicDataServiceException(ResponseCode.FILE_DOES_NOT_EXIST);
//                }
//
//                if(!(scriptRunner.instantiateClass(null, provider.getGroovyScriptURL()) instanceof AbstractScriptableLoginModule)) {
//                    final EsbErrorToken errorToken = new EsbErrorToken();
//                    errorToken.setClassName(AbstractScriptableLoginModule.class.getCanonicalName());
//                    throw new BasicDataServiceException(ResponseCode.GROOVY_CLASS_MUST_EXTEND_LOGIN_MODULE, errorToken);
//                }
//            } else {
//                provider.setGroovyScriptURL(null);
//            }
//
//            if(type.isUsesSpringBean() && StringUtils.isNotBlank(provider.getSpringBeanName())) {
//                if(!ctx.containsBean(provider.getSpringBeanName())) {
//                    throw new BasicDataServiceException(ResponseCode.INVALID_SPRING_BEAN);
//                }
//            } else {
//                provider.setSpringBeanName(null);
//            }
//
//            if(type.isPasswordPolicyRequired()) {
//                if(StringUtils.isBlank(provider.getPasswordPolicyId())) {
//                    throw new BasicDataServiceException(ResponseCode.PASSWORD_POLICY_NOT_SET);
//                }
//            }
//
//            if(type.isAuthnPolicyRequired()) {
//                if(StringUtils.isBlank(provider.getAuthnPolicyId())) {
//                    throw new BasicDataServiceException(ResponseCode.AUTHN_POLICY_NOT_SET);
//                }
//            }
//
//            if(type.isSupportsSMSOTP()) {
//                if(provider.isSupportsSMSOTP()) {
//                    if(CollectionUtils.isEmpty(metadataService.getPhonesWithSMSOTPEnabled())) {
//                        throw new BasicDataServiceException(ResponseCode.NO_PHONE_TYPES_WITH_OTP_ENABLED);
//                    }
//                } else {
//                    provider.setSupportsSMSOTP(false);
//                }
//
//                if(provider.isSupportsSMSOTP()) {
//                    if(StringUtils.isBlank(provider.getSmsOTPGroovyScript())) {
//                        throw new BasicDataServiceException(ResponseCode.SMS_OTP_GROOVY_SCRIPT_REQUIRED);
//                    } else {
//                        if(!scriptRunner.scriptExists(provider.getSmsOTPGroovyScript())) {
//                            throw new BasicDataServiceException(ResponseCode.FILE_DOES_NOT_EXIST);
//                        }
//
//                        final Object groovyObj = scriptRunner.instantiateClass(null, provider.getSmsOTPGroovyScript());
//                        if(!(groovyObj instanceof AbstractSMSOTPModule)) {
//                            final EsbErrorToken errorToken = new EsbErrorToken();
//                            errorToken.setClassName(AbstractSMSOTPModule.class.getCanonicalName());
//                            throw new BasicDataServiceException(ResponseCode.GROOVY_CLASS_MUST_EXTEND_SMS_OTP_MODULE, errorToken);
//                        }
//                    }
//                }
//            }
//
//            if(type.isSupportsCertAuth()) {
//            	if(provider.isSupportsCertAuth()) {
//            		final EsbErrorToken errorToken = new EsbErrorToken();
//            		errorToken.setClassName(DefaultCertToIdentityConverter.class.getCanonicalName());
//            		if(StringUtils.isNotBlank(provider.getCertGroovyScript())) {
//            			if(!scriptRunner.scriptExists(provider.getCertGroovyScript())) {
//        					throw new BasicDataServiceException(ResponseCode.CERT_CONFIG_INVALID, errorToken);
//        				}
//
//            			final Object o = scriptRunner.instantiateClass(null, provider.getCertGroovyScript());
//            			if(!(o instanceof DefaultCertToIdentityConverter)) {
//            				throw new BasicDataServiceException(ResponseCode.CERT_CONFIG_INVALID, errorToken);
//            			}
//            			provider.setCertRegex(null);
//            		} else if(StringUtils.isNotBlank(provider.getCertRegex())) {
//            			provider.setCertGroovyScript(null);
//            		} else {
//            			throw new BasicDataServiceException(ResponseCode.CERT_CONFIG_INVALID, errorToken);
//            		}
//            	}
//            } else {
//            	provider.setSupportsCertAuth(false);
//            	provider.setCertGroovyScript(null);
//            	provider.setCertRegex(null);
//            }
//
//            if(provider.isSignRequest()) {
//                if((provider.getPrivateKey()==null || provider.getPrivateKey().length==0)) {
//                    if(type.isHasPrivateKey()) {
//                        throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_SECUTITY_KEYS_NOT_SET);
//                    }
//                }
//
//                if(provider.getPublicKey()==null || provider.getPublicKey().length==0) {
//                    if(type.isHasPublicKey()) {
//                        throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_SECUTITY_KEYS_NOT_SET);
//                    }
//                }
//            }
//
//            validateAndSyncProviderAttributes(provider);
//
//            final AuthProviderEntity entity = authProviderDozerConverter.convertToEntity(provider, true);
//            authProviderService.saveAuthProvider(entity, requestorId);
//            response.setResponseValue(entity.getId());
//        } catch(BasicDataServiceException e) {
//            LOG.error(e.getMessage(), e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorCode(e.getCode());
//            response.setErrorTokenList(e.getErrorTokenList());
//        } catch(Throwable e) {
//            LOG.error("Error while saving auth provider", e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//        }
//        return response;
    }


    private void validateAndSyncProviderAttributes(AuthProvider provider) throws BasicDataServiceException{
        final AuthAttributeSearchBean sb = new AuthAttributeSearchBean();
        sb.setProviderType(provider.getProviderType());
        final List<AuthAttributeEntity> attributeEntityList = authProviderService.findAuthAttributeBeans(sb, Integer.MAX_VALUE,0);
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
        IdServiceRequest request=new IdServiceRequest();
        request.setId(providerId);

        return this.manageApiRequest(AuthProviderAPI.DeleteAuthProvider, request, Response.class);


//        final Response response = new Response(ResponseStatus.SUCCESS);
//        try {
//            if(StringUtils.isBlank(providerId)) {
//                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
//            }
//            final AuthProviderSearchBean searchBean = new AuthProviderSearchBean();
//            searchBean.setKey(providerId);
//            final List<AuthProviderEntity> providers = authProviderService.findAuthProviderBeans(searchBean, 0, 1);
//            if(CollectionUtils.isEmpty(providers)) {
//                throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
//            }
//
//            final AuthProviderEntity entity = providers.get(0);
//            if(entity.isDefaultProvider()) {
//                throw new BasicDataServiceException(ResponseCode.CANNOT_DELETE_DEFAULT_AUTH_PROVIDER);
//            }
//
//            authProviderService.deleteAuthProvider(providerId);
//        } catch(BasicDataServiceException e) {
//            LOG.warn(e.getMessage(), e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorCode(e.getCode());
//        } catch(Throwable e) {
//            LOG.error(e.getMessage(), e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//        }
//        return response;
    }

//    @Override
//    public void setApplicationContext(ApplicationContext ctx)
//            throws BeansException {
//        this.ctx = ctx;
//    }
}
