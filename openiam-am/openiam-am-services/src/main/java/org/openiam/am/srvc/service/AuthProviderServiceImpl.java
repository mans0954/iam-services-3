package org.openiam.am.srvc.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.cert.groovy.DefaultCertToIdentityConverter;
import org.openiam.am.srvc.dao.AuthAttributeDao;
import org.openiam.am.srvc.dao.AuthProviderDao;
import org.openiam.am.srvc.dao.AuthProviderTypeDao;
import org.openiam.am.srvc.dao.OAuthCodeDao;
import org.openiam.am.srvc.dao.OAuthTokenDao;
import org.openiam.am.srvc.dao.OAuthUserClientXrefDao;
import org.openiam.am.srvc.domain.AuthAttributeEntity;
import org.openiam.am.srvc.domain.AuthProviderAttributeEntity;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.AuthProviderTypeEntity;
import org.openiam.am.srvc.domain.OAuthCodeEntity;
import org.openiam.am.srvc.domain.OAuthTokenEntity;
import org.openiam.am.srvc.domain.OAuthUserClientXrefEntity;
import org.openiam.am.srvc.dozer.converter.*;
import org.openiam.am.srvc.dto.*;
import org.openiam.am.srvc.searchbean.AuthAttributeSearchBean;
import org.openiam.am.srvc.searchbean.AuthProviderSearchBean;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.AuthProviderResponse;
import org.openiam.base.ws.ResponseCode;
import org.openiam.dozer.converter.ResourceDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.exception.EsbErrorToken;
import org.openiam.idm.srvc.auth.spi.AbstractSMSOTPModule;
import org.openiam.idm.srvc.auth.spi.AbstractScriptableLoginModule;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.meta.service.MetadataService;
import org.openiam.idm.srvc.mngsys.service.ManagedSysDAO;
import org.openiam.idm.srvc.policy.service.PolicyDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.mq.constants.OAuthAPI;
import org.openiam.script.ScriptIntegration;
import org.openiam.thread.Sweepable;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("authProviderService")
public class AuthProviderServiceImpl implements AuthProviderService, Sweepable {
    private final Log log = LogFactory.getLog(this.getClass());
    private static final String resourceTypeId="AUTH_PROVIDER";

    @Autowired
    private AuthProviderTypeDao authProviderTypeDao;
    @Autowired
    private AuthAttributeDao authAttributeDao;
    @Autowired
    private AuthProviderDao authProviderDao;
    @Autowired
    private ResourceTypeDAO resourceTypeDAO;
    @Autowired
    private ResourceService resourceService;
    
    @Autowired
    private ManagedSysDAO managedSystemDAO;

    @Autowired
    private MetadataService metadataService;
    
    @Autowired
    private PolicyDAO policyDAO;

    @Autowired
    private AuthProviderDozerConverter authProviderDozerConverter;

    @Autowired
    private ResourceDozerConverter resourceDozerConverter;

    @Autowired
    private AuthorizationManagerService authorizationManagerService;
    @Autowired
    private OAuthUserClientXrefDao oauthUserClientXrefDao;
    @Autowired
    private UserDataService userDataService;
    @Autowired
    private OAuthUserClientXrefDozerConverter oauthUserClientXrefDozerConverter;
    @Autowired
    private OAuthTokenDozerConverter oauthTokenDozerConverter;
    @Autowired
    private OAuthTokenDao oAuthTokenDao;

    @Autowired
    private OAuthCodeDozerConverter oauthCodeDozerConverter;
    @Autowired
    private OAuthCodeDao oAuthCodeDao;
    
    private Map<String, AuthProvider> authProviderCache = new HashMap<String, AuthProvider>();

    private Map<String, AuthProvider> oAuthIdCache = new HashMap<String, AuthProvider>();
    private Map<String, AuthProvider> oAuthNameCache = new HashMap<String, AuthProvider>();

    @Autowired
    private AuthAttributeDozerConverter authAttributeDozerConverter;
    @Autowired
    private AuthProviderTypeDozerConverter authProviderTypeDozerConverter;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    protected ScriptIntegration scriptRunner;

    /*
    *==================================================
    * AuthProviderType section
    *===================================================
    */
    @Override
    @Transactional(readOnly=true)
    public AuthProviderType getAuthProviderType(String providerType) throws BasicDataServiceException {
        if(StringUtils.isBlank(providerType)){
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "ProviderType argument is missing");
        }
        AuthProviderTypeEntity type = authProviderTypeDao.findById(providerType);
        return authProviderTypeDozerConverter.convertToDTO(type, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthProviderType> getAuthProviderTypeList() {
        return authProviderTypeDozerConverter.convertToDTOList(authProviderTypeDao.findAll(), true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthProviderType> getSocialAuthProviderTypeList(){
        List<AuthProviderTypeEntity> allTypes = authProviderTypeDao.findAll();
        List<AuthProviderTypeEntity> selectedTypes = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(allTypes)){
            for(AuthProviderTypeEntity type: allTypes){
                if("GOOGLE_AUTH_PROVIDER".equals(type.getId()) || "FACEBOOK_AUTH_PROVIDER".equals(type.getId())){
                    if(CollectionUtils.isNotEmpty(type.getProviderSet())){
                        selectedTypes.add(type);
                    }
                }
            }
        }
        return authProviderTypeDozerConverter.convertToDTOList(selectedTypes, true);
    }

    @Override
    @Transactional
    public void addProviderType(AuthProviderType providerType) throws BasicDataServiceException{
        if(providerType == null || StringUtils.isBlank(providerType.getId())) {
            throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_TYPE_NOT_SET, "provider type is null");
        }
        AuthProviderTypeEntity entity = authProviderTypeDozerConverter.convertToEntity(providerType, false);
        authProviderTypeDao.save(entity);
    }

    /*
    *==================================================
    * AuthAttributeEntity section
    *===================================================
    */
    @Override
    @Transactional(readOnly=true)
    public List<AuthAttribute> findAuthAttributeBeans(AuthAttributeSearchBean searchBean, Integer size, Integer from) {
        List<AuthAttributeEntity> attributeList = authAttributeDao.getByExample(searchBean, from, size);
        return authAttributeDozerConverter.convertToDTOList(attributeList, (searchBean != null) ? searchBean.isDeepCopy() : false);
    }


    /*
    *==================================================
    *  AuthProviderEntity section
    *===================================================
    */
    @Override
    @Transactional(readOnly=true)
    public List<AuthProvider> findAuthProviderBeans(final AuthProviderSearchBean searchBean, int from, int size) {
        final List<AuthProviderEntity> providerList = authProviderDao.getByExample(searchBean,from,size);
        return authProviderDozerConverter.convertToDTOList(providerList, (searchBean != null) ? searchBean.isDeepCopy() : false);
    }
    
    @Override
    @Transactional
    public String saveAuthProvider(AuthProvider provider, final String requesterId) throws BasicDataServiceException {
        if (provider == null) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
        }
        if (StringUtils.isBlank(provider.getProviderType())) {
            throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_TYPE_NOT_SET);
        }
        if (StringUtils.isBlank(provider.getManagedSysId())) {
            throw new BasicDataServiceException(ResponseCode.MANAGED_SYSTEM_NOT_SET);
        }
        if (StringUtils.isBlank(provider.getName())) {
            throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NAME_NOT_SET);
        }

        final AuthProviderTypeEntity type = authProviderTypeDao.findById(provider.getProviderType());
        if (type == null) {
            throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_TYPE_NOT_SET);
        }

        if (type.isUsesGroovyScript() && StringUtils.isNotBlank(provider.getGroovyScriptURL())) {
            if (!scriptRunner.scriptExists(provider.getGroovyScriptURL())) {
                throw new BasicDataServiceException(ResponseCode.FILE_DOES_NOT_EXIST);
            }

            try {
                if (!(scriptRunner.instantiateClass(null, provider.getGroovyScriptURL()) instanceof AbstractScriptableLoginModule)) {
                    final EsbErrorToken errorToken = new EsbErrorToken();
                    errorToken.setClassName(AbstractScriptableLoginModule.class.getCanonicalName());
                    throw new BasicDataServiceException(ResponseCode.GROOVY_CLASS_MUST_EXTEND_LOGIN_MODULE, errorToken);
                }
            } catch (IOException e) {
                throw new BasicDataServiceException(ResponseCode.CANNOT_INSTANTIATE_GROOVY_CLASS, provider.getGroovyScriptURL());
            }
        } else {
            provider.setGroovyScriptURL(null);
        }

        if (type.isUsesSpringBean() && StringUtils.isNotBlank(provider.getSpringBeanName())) {
            if (!SpringContextProvider.getApplicationContext().containsBean(provider.getSpringBeanName())) {
                throw new BasicDataServiceException(ResponseCode.INVALID_SPRING_BEAN);
            }
        } else {
            provider.setSpringBeanName(null);
        }

        if (type.isPasswordPolicyRequired()) {
            if (StringUtils.isBlank(provider.getPasswordPolicyId())) {
                throw new BasicDataServiceException(ResponseCode.PASSWORD_POLICY_NOT_SET);
            }
        }

        if (type.isAuthnPolicyRequired()) {
            if (StringUtils.isBlank(provider.getAuthnPolicyId())) {
                throw new BasicDataServiceException(ResponseCode.AUTHN_POLICY_NOT_SET);
            }
        }

        if (type.isSupportsSMSOTP()) {
            if (provider.isSupportsSMSOTP()) {
                if (CollectionUtils.isEmpty(metadataService.getPhonesWithSMSOTPEnabled())) {
                    throw new BasicDataServiceException(ResponseCode.NO_PHONE_TYPES_WITH_OTP_ENABLED);
                }
            } else {
                provider.setSupportsSMSOTP(false);
            }

            if (provider.isSupportsSMSOTP()) {
                if (StringUtils.isBlank(provider.getSmsOTPGroovyScript())) {
                    throw new BasicDataServiceException(ResponseCode.SMS_OTP_GROOVY_SCRIPT_REQUIRED);
                } else {
                    if (!scriptRunner.scriptExists(provider.getSmsOTPGroovyScript())) {
                        throw new BasicDataServiceException(ResponseCode.FILE_DOES_NOT_EXIST);
                    }

                    try {
                        Object groovyObj = scriptRunner.instantiateClass(null, provider.getSmsOTPGroovyScript());

                        if (!(groovyObj instanceof AbstractSMSOTPModule)) {
                            final EsbErrorToken errorToken = new EsbErrorToken();
                            errorToken.setClassName(AbstractSMSOTPModule.class.getCanonicalName());
                            throw new BasicDataServiceException(ResponseCode.GROOVY_CLASS_MUST_EXTEND_SMS_OTP_MODULE, errorToken);
                        }
                    } catch (IOException e) {
                        throw new BasicDataServiceException(ResponseCode.CANNOT_INSTANTIATE_GROOVY_CLASS, provider.getSmsOTPGroovyScript());
                    }
                }
            }
        }

        if (type.isSupportsCertAuth()) {
            if (provider.isSupportsCertAuth()) {
                final EsbErrorToken errorToken = new EsbErrorToken();
                errorToken.setClassName(DefaultCertToIdentityConverter.class.getCanonicalName());
                if (StringUtils.isNotBlank(provider.getCertGroovyScript())) {
                    if (!scriptRunner.scriptExists(provider.getCertGroovyScript())) {
                        throw new BasicDataServiceException(ResponseCode.CERT_CONFIG_INVALID, errorToken);
                    }
                    try {
                        final Object o = scriptRunner.instantiateClass(null, provider.getCertGroovyScript());
                        if (!(o instanceof DefaultCertToIdentityConverter)) {
                            throw new BasicDataServiceException(ResponseCode.CERT_CONFIG_INVALID, errorToken);
                        }
                    } catch (IOException e) {
                        throw new BasicDataServiceException(ResponseCode.CANNOT_INSTANTIATE_GROOVY_CLASS, provider.getCertGroovyScript());
                    }
                    provider.setCertRegex(null);
                } else if (StringUtils.isNotBlank(provider.getCertRegex())) {
                    provider.setCertGroovyScript(null);
                } else {
                    throw new BasicDataServiceException(ResponseCode.CERT_CONFIG_INVALID, errorToken);
                }
            }
        } else {
            provider.setSupportsCertAuth(false);
            provider.setCertGroovyScript(null);
            provider.setCertRegex(null);
        }

        if (provider.isSignRequest()) {
            if ((provider.getPrivateKey() == null || provider.getPrivateKey().length == 0)) {
                if (type.isHasPrivateKey()) {
                    throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_SECUTITY_KEYS_NOT_SET);
                }
            }

            if (provider.getPublicKey() == null || provider.getPublicKey().length == 0) {
                if (type.isHasPublicKey()) {
                    throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_SECUTITY_KEYS_NOT_SET);
                }
            }
        }
        validateAndSyncProviderAttributes(provider);

        final AuthProviderEntity entity = authProviderDozerConverter.convertToEntity(provider, true);

        entity.setType(type);
        if (entity.getType() == null) {
            throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_TYPE_NOT_SET);
        }

        if (!entity.getType().isHasPasswordPolicy()) {
            entity.setPasswordPolicy(null);
        } else {
            if (entity.getPasswordPolicy() == null || StringUtils.isBlank(entity.getPasswordPolicy().getId())) {
                if (entity.getType().isPasswordPolicyRequired()) {
                    throw new BasicDataServiceException(ResponseCode.PASSWORD_POLICY_NOT_SET, "Password Policy not set");
                }
                entity.setPasswordPolicy(null);
            } else {
                entity.setPasswordPolicy(policyDAO.findById(entity.getPasswordPolicy().getId()));
            }
        }

        if (!entity.getType().isHasAuthnPolicy()) {
            entity.setAuthenticationPolicy(null);
        } else {
            if (entity.getAuthenticationPolicy() == null || StringUtils.isBlank(entity.getAuthenticationPolicy().getId())) {
                if (entity.getType().isAuthnPolicyRequired()) {
                    throw new BasicDataServiceException(ResponseCode.AUTH_POLICY_NOT_SET, "Authentication Policy not set");
                }
                entity.setAuthenticationPolicy(null);
            } else {
                entity.setAuthenticationPolicy(policyDAO.findById(entity.getAuthenticationPolicy().getId()));
            }
        }

        if (entity.getManagedSystem() != null && StringUtils.isNotBlank(entity.getManagedSystem().getId())) {
            entity.setManagedSystem(managedSystemDAO.findById(entity.getManagedSystem().getId()));
        } else {
            entity.setManagedSystem(null);
        }

        final AuthProviderEntity dbEntity = authProviderDao.findById(provider.getId());
        // make a copy of attribute collection for the future reference

        Set<String> oauthScopestoSync = new HashSet<>();
        if ("OAUTH_CLIENT".equals(entity.getType().getId()) && dbEntity != null) {
            // AM-766. Need to delete scopes from users' authorized list only if it is deleted from oauth client.

            final Set<AuthProviderAttributeEntity> dbAttributeEntitySet = dbEntity.getAttributes();

            if (CollectionUtils.isNotEmpty(dbAttributeEntitySet)) {
                Set<String> dbOauthScopeSet = new HashSet<>();
                // get OAuthClientScopes scopes for oauth client before applying changes
                for (AuthProviderAttributeEntity dbAttr : dbAttributeEntitySet) {
                    if ("OAuthClientScopes".equals(dbAttr.getAttribute().getId())) {
                        if (StringUtils.isNotBlank(dbAttr.getValue())) {
                            dbOauthScopeSet = new HashSet<>(Arrays.asList(dbAttr.getValue().split(",")));
                        }
                    }
                }
                // if there were no scopes before then skip this part
                if (CollectionUtils.isNotEmpty(dbOauthScopeSet)) {
                    // get OAuthClientScopes scopes for updated oauth client
                    Set<String> newOauthScopeSet = new HashSet<>();
                    final Set<AuthProviderAttributeEntity> newAttributeEntitySet = entity.getAttributes();
                    if (CollectionUtils.isNotEmpty(newAttributeEntitySet)) {
                        for (AuthProviderAttributeEntity attr : newAttributeEntitySet) {
                            if ("OAuthClientScopes".equals(attr.getAttribute().getId())) {
                                if (StringUtils.isNotBlank(attr.getValue())) {
                                    newOauthScopeSet = new HashSet<>(Arrays.asList(attr.getValue().split(",")));
                                }
                            }
                        }
                    }
                    // compare scopes. if scope was removed then do delete
                    for (String dbScope : dbOauthScopeSet) {
                        if (!newOauthScopeSet.contains(dbScope)) {
                            // the scope has been removed from oauth client
                            oauthScopestoSync.add(dbScope);
                        }
                    }
                }
            }
        }

        if (dbEntity != null) {
            if (dbEntity.isReadOnly()) {
                throw new BasicDataServiceException(ResponseCode.READONLY);
            }
            dbEntity.getResource().setURL(provider.getResource().getURL());
            entity.setResource(dbEntity.getResource());
            entity.setResourceAttributeMap(dbEntity.getResourceAttributeMap());
            entity.setDefaultProvider(dbEntity.isDefaultProvider());
            entity.setContentProviders(dbEntity.getContentProviders());
            entity.setAuthorizedUsers(dbEntity.getAuthorizedUsers());
            entity.setoAuthCodes(dbEntity.getoAuthCodes());
            entity.setoAuthTokens(dbEntity.getoAuthTokens());
            if (CollectionUtils.isEmpty(entity.getAttributes())) {
                if (dbEntity.getAttributes() != null) {
                    entity.setAttributes(dbEntity.getAttributes());
                    entity.getAttributes().clear();
                }
            }
        	
        	/*
            if(provider.getPrivateKey()!=null && provider.getPrivateKey().length>0){
                entity.setPrivateKey(provider.getPrivateKey());
            }
            if(provider.getPublicKey()!=null && provider.getPublicKey().length>0){
                entity.setPublicKey(provider.getPublicKey());
            }
            
            entity.setSignRequest(provider.isSignRequest());
			*/

        } else {
            entity.setContentProviders(null);
            entity.setResourceAttributeMap(null);
            entity.setDefaultProvider(false);
        }

        if (entity.getResource() == null || StringUtils.isBlank(entity.getResource().getId())) {
            ResourceTypeEntity resourceType = resourceTypeDAO.findById(resourceTypeId);
            if (resourceType == null) {
                throw new NullPointerException("Cannot create resource for provider. Resource type is not found");
            }

            final ResourceEntity resource = new ResourceEntity();
            resource.setName(System.currentTimeMillis() + "_" + provider.getName());
            resource.setResourceType(resourceType);
            if (entity.getResource() != null) {
                resource.setURL(entity.getResource().getURL());
            }
            resourceService.save(resource, requesterId);
            entity.setResource(resource);
        }
        entity.getResource().setCoorelatedName(provider.getName());

        if (CollectionUtils.isNotEmpty(entity.getAttributes())) {
            for (final Iterator<AuthProviderAttributeEntity> it = entity.getAttributes().iterator(); it.hasNext(); ) {
                final AuthProviderAttributeEntity attribute = it.next();
                if (StringUtils.isNotBlank(attribute.getValue())) {
                    if (attribute.getAttribute() != null && StringUtils.isNotBlank(attribute.getAttribute().getId())) {
                        attribute.setAttribute(authAttributeDao.findById(attribute.getAttribute().getId()));
                    } else {
                        attribute.setAttribute(null);
                    }
                    attribute.setProvider(entity);
                } else {
                    it.remove();
                }
            }
        }

        /*
        if(MapUtils.isNotEmpty(provider.getResourceAttributeMap())) {
        	for(final AuthResourceAttributeMapEntity attribute : provider.getResourceAttributeMap().values()) {
        		attribute.setProvider(provider);
        		if(attribute.getAmAttribute() != null && StringUtils.isNotBlank(attribute.getAmAttribute().getId())) {
        			attribute.setAmAttribute(authResourceAttributeService.getAmAttribute(attribute.getAmAttribute().getId()));
        		} else {
        			attribute.setAmAttribute(null);
        		}
        	}
        }
        */

        entity.setLastModified(new Date());
        if (provider.getId() == null) {
            authProviderDao.save(entity);
        } else {
            authProviderDao.merge(entity);
        }
        // AM-766. Need to delete scopes from users' authorized list only if it is deleted from oauth client.
        if (CollectionUtils.isNotEmpty(oauthScopestoSync)) {
            for (String dbScope : oauthScopestoSync) {
                oauthUserClientXrefDao.deleteByScopeId(dbScope);
            }
        }

        return entity.getId();
    }

    @Override
    @Transactional
    public void deleteAuthProvider(String providerId) throws BasicDataServiceException {

        if(StringUtils.isBlank(providerId)) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
        }
        AuthProviderEntity entity = authProviderDao.findById(providerId);
        if(entity==null){
            throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
        }
        if(entity.isDefaultProvider()) {
            throw new BasicDataServiceException(ResponseCode.CANNOT_DELETE_DEFAULT_AUTH_PROVIDER);
        }

        if(CollectionUtils.isNotEmpty(entity.getContentProviders())) {
            throw new BasicDataServiceException(ResponseCode.LINKED_TO_ONE_OR_MORE_CONTENT_PROVIDERS);
        }
        if(CollectionUtils.isNotEmpty(entity.getUriPatterns())) {
            throw new BasicDataServiceException(ResponseCode.LINKED_TO_ONE_OR_MORE_URI_PATTERNS);
        }
        authProviderDao.delete(entity);
        resourceService.deleteResource(entity.getResource().getId());
    }

	@Override
	@Transactional(readOnly=true)
	public int countAuthProviderBeans(AuthProviderSearchBean searchBean) {
		return authProviderDao.count(searchBean);
	}

	@Override
	@Transactional(readOnly=true)
	public AuthProviderEntity getAuthProvider(String id) {
		return authProviderDao.findById(id);
	}
	
    @Override
    @Transactional(readOnly=true)
    public AuthProvider getProvider(final String id) {
    	return authProviderDozerConverter.convertToDTO(getAuthProvider(id), true);
    }

    /*
    *==================================================
    *  OAuth2 section
    *===================================================
    */
    @Override
    @Transactional(readOnly=true)
    public AuthProvider getOAuthClient(final String clientId){
        return authProviderDozerConverter.convertToDTO(authProviderDao.getOAuthClient(clientId), true);
    }
    
	@Override
	@Transactional(readOnly=true)
	public List<AuthProvider> getOAuthClients() {
		final List<AuthProviderEntity> entities = authProviderDao.getOAuthClients();
		return authProviderDozerConverter.convertToDTOList(entities, true);
	}

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly=true)
    public List<Resource> getScopesForAuthrorization(String clientId, String userId, Language language) throws BasicDataServiceException {
        AuthProvider provider = getOAuthClient(clientId);
        Set<String> clientScopesIds = null;
        // determine if the client is authorized for some scopes
        boolean isClientAuthorized = false;
        if(CollectionUtils.isNotEmpty(provider.getAttributes())) {
        	final Optional<AuthProviderAttribute> scopeOptional = provider.getAttributes().stream().filter(attr -> "OAuthClientScopes".equals(attr.getAttributeId())).findFirst();
            AuthProviderAttribute scopes = (scopeOptional.isPresent()) ? scopeOptional.get() : null;
            if(scopes !=null && StringUtils.isNotBlank(scopes.getValue())){
                clientScopesIds = new HashSet<>(Arrays.asList(scopes.getValue().split(","))).stream().filter(str -> StringUtils.isNotBlank(str)).collect(Collectors.toSet());
            }
        }
        if(CollectionUtils.isNotEmpty(clientScopesIds)){
            // get already authorized scopes
            List<OAuthUserClientXrefEntity> authorizedResources = oauthUserClientXrefDao.getByClientAndUser(clientId, userId, null);
            if(CollectionUtils.isNotEmpty(authorizedResources)){
                isClientAuthorized = true;
                Set<String> authorizedResourcesIds = authorizedResources.stream().map(xref -> xref.getScope().getId()).collect(Collectors.toSet());
                // leave only unauthorized scopes
                clientScopesIds.removeAll(authorizedResourcesIds);
            }

            if(CollectionUtils.isNotEmpty(clientScopesIds)){
                // if not all scopes authorized

                // do intersection between unauthorized scopes and user resources
                // leave only those scopes that user have access to
                Iterator<String> scopeIter = clientScopesIds.iterator();
                while (scopeIter.hasNext()) {
                    String clientScopesId = scopeIter.next();
                    if (!authorizationManagerService.isEntitled(userId, clientScopesId)) {
                        scopeIter.remove();
                    }
                }
//
//                Set<ResourceAuthorizationRight>  userResources = authorizationManagerService.getResourcesForUser(userId);
//                if(CollectionUtils.isNotEmpty(userResources)) {
//                    Set<String> userResourceIds = userResources.stream().map(res->res.getEntity().getId()).collect(Collectors.toSet());
//
//                    clientScopesIds.retainAll(userResourceIds);
//                }
            }

            if(CollectionUtils.isNotEmpty(clientScopesIds) || isClientAuthorized){
                return  resourceDozerConverter.convertToDTOList(resourceService.findResourcesByIds(clientScopesIds), false);
            } else {
                throw new  BasicDataServiceException(ResponseCode.OAUTH_CLIENT_SCOPE_CALCULATION_ERROR);
            }
        } else {
            throw new  BasicDataServiceException(ResponseCode.OAUTH_CLIENT_SCOPE_CALCULATION_ERROR);
        }
    }

    @LocalizedServiceGet
    @Transactional(readOnly=true)
    public List<Resource> getAuthorizedScopes(String clientId, OAuthToken token, Language language){
        //TODO: need to review scope authorization and add scopes to token as a link.
        //TODO: this will allow to handle scope arg in token endpoint
        if("RESOURCE_OWNER".equals(token.getGrandFlow().trim())){
            AuthProvider client = this.getOAuthClient(clientId);
            final Optional<AuthProviderAttribute> scopeOptional = client.getAttributes().stream().filter(attr -> "OAuthClientScopes".equals(attr.getAttributeId())).findFirst();
            AuthProviderAttribute scopes = (scopeOptional.isPresent()) ? scopeOptional.get() : null;
            if(scopes !=null && StringUtils.isNotBlank(scopes.getValue())){
                Set<String> authorizedResourcesIds = new HashSet<>(Arrays.asList(scopes.getValue().split(","))).stream().filter(str -> StringUtils.isNotBlank(str)).collect(Collectors.toSet());
                // leave only those scopes that user have access to
                Iterator<String> scopeIter = authorizedResourcesIds.iterator();
                while (scopeIter.hasNext()) {
                    String clientScopesId = scopeIter.next();
                    if (!authorizationManagerService.isEntitled(token.getUserId(), clientScopesId)) {
                        scopeIter.remove();
                    }
                }
                return resourceDozerConverter.convertToDTOList(resourceService.findResourcesByIds(authorizedResourcesIds), false);
            }
        } else {
            return getAuthorizedScopesByUser(clientId, token.getUserId(), language);
        }
        return  null;
    }
    @LocalizedServiceGet
    @Transactional(readOnly=true)
    public List<Resource> getAuthorizedScopesByUser(String clientId, String userId, Language language){
        List<OAuthUserClientXrefEntity> authorizedResources = oauthUserClientXrefDao.getByClientAndUser(clientId, userId, true);
        if(CollectionUtils.isNotEmpty(authorizedResources)){
            Set<String> authorizedResourcesIds = authorizedResources.stream().map(xref -> xref.getScope().getId()).collect(Collectors.toSet());
            return resourceDozerConverter.convertToDTOList(resourceService.findResourcesByIds(authorizedResourcesIds), false);
        }
        return null;
    }

    @Override
    @Transactional
    public void saveClientScopeAuthorization(String providerId, String userId, List<OAuthUserClientXref> oauthUserClientXrefList) throws BasicDataServiceException {
        AuthProviderEntity client = this.getAuthProvider(providerId);
        UserEntity user = userDataService.getUser(userId);

        if(client==null)
            throw new BasicDataServiceException(ResponseCode.OAUTH_CLIENT_NOT_FOUND);

        if(CollectionUtils.isNotEmpty(oauthUserClientXrefList)){
            List<OAuthUserClientXrefEntity> xrefEntityList =  oauthUserClientXrefDozerConverter.convertToEntityList(oauthUserClientXrefList, true);

            for(OAuthUserClientXrefEntity xref:xrefEntityList){
                xref.setClient(client);
                xref.setUser(user);
                xref.setScope(resourceService.findResourceById(xref.getScope().getId()));
            }
            oauthUserClientXrefDao.save(xrefEntityList);
        }
    }
    @Transactional
    public void saveOAuthCode(OAuthCode oAuthCode){
        OAuthCodeEntity entity = oauthCodeDozerConverter.convertToEntity(oAuthCode, true);
        entity.setClient(this.getAuthProvider(oAuthCode.getClientId()));
        entity.setUser(userDataService.getUser(oAuthCode.getUserId()));
        entity.setCode(oAuthCode.getCode());
        entity.setExpiredOn(oAuthCode.getExpiredOn());
        entity.setRedirectUrl(oAuthCode.getRedirectUrl());
        oAuthCodeDao.save(entity);
    }
    @Transactional(readOnly = true)
    public OAuthCode getOAuthCode(String code){
        OAuthCodeEntity codeEntity = oAuthCodeDao.getByCode(code);
        OAuthCode codeDto = null;
        if(codeEntity!=null) {
            codeDto = oauthCodeDozerConverter.convertToDTO(codeEntity, true);
            oAuthCodeDao.delete(codeEntity);
        }
        return codeDto;
    }

    @Override
    @Transactional(readOnly=true)
    public OAuthToken getOAuthToken(String token) {
        OAuthTokenEntity tokenEntity = oAuthTokenDao.getByAccessToken(token);
        return oauthTokenDozerConverter.convertToDTO(tokenEntity, false);
    }

    @Override
    @Transactional(readOnly=true)
    public OAuthToken getOAuthTokenByRefreshToken(String refreshToken){
        OAuthTokenEntity tokenEntity = oAuthTokenDao.getByRefreshToken(refreshToken);
        OAuthToken token = null;
        if(tokenEntity!=null){
            token = oauthTokenDozerConverter.convertToDTO(tokenEntity, false);
            oAuthTokenDao.delete(tokenEntity);
        }
        return token;
    }

    @Override
    @Transactional
    public OAuthToken saveOAuthToken(OAuthToken oAuthToken){
        OAuthTokenEntity entity = oauthTokenDozerConverter.convertToEntity(oAuthToken, true);
        OAuthTokenEntity entityDb = null;

        if(StringUtils.isNotBlank(oAuthToken.getToken())){
            entityDb = oAuthTokenDao.getByAccessToken(oAuthToken.getToken());
        } else if(StringUtils.isNotBlank(oAuthToken.getRefreshToken())){
            entityDb = oAuthTokenDao.getByRefreshToken(oAuthToken.getRefreshToken());
        }


        if(entityDb==null){
            entity.setClient(this.getAuthProvider(oAuthToken.getClientId()));
            entity.setUser(userDataService.getUser(oAuthToken.getUserId()));
            entity.setToken(oAuthToken.getToken());
            entity.setExpiredOn(oAuthToken.getExpiredOn());
            entity.setRefreshToken(oAuthToken.getRefreshToken());
            oAuthTokenDao.save(entity);
        } else {
            if(StringUtils.isNotBlank(oAuthToken.getToken())){
                entityDb.setToken(oAuthToken.getToken());
                entityDb.setExpiredOn(oAuthToken.getExpiredOn());
            }
            if(StringUtils.isNotBlank(oAuthToken.getRefreshToken())) {
                entityDb.setRedirectUrl(oAuthToken.getRefreshToken());
            }
            oAuthTokenDao.merge(entityDb);
        }

        return oauthTokenDozerConverter.convertToDTO(oAuthTokenDao.getByAccessToken(oAuthToken.getToken()), true);
    }

	@Override
	@Transactional(readOnly = true)
	public AuthProviderTypeEntity getAuthProviderTypeForProvider(
			String providerId) {
		final AuthProviderEntity provider = authProviderDao.findById(providerId);
		return (provider != null) ? provider.getType() : null;
	}

	@Override
	public AuthProvider getCachedAuthProvider(String id) {
		return authProviderCache.get(id);
	}

	@Override
	@Transactional
	@Scheduled(fixedRateString="${org.openiam.am.uri.federation.threadsweep}", initialDelay=0)
	public void sweep() {
		Map<String, AuthProvider> tempAuthProviderCache = new HashMap<String, AuthProvider>();
		final List<AuthProviderEntity> entities = authProviderDao.findAll();
		if(CollectionUtils.isNotEmpty(entities)) {
			tempAuthProviderCache = 
					entities.stream().map(e -> authProviderDozerConverter.convertToDTO(e, true)).collect(Collectors.toMap(AuthProvider::getId, Function.identity()));
		}
		synchronized(authProviderCache) {
			authProviderCache = tempAuthProviderCache;
		}
	}
    @Override
    @Scheduled(fixedRateString="${org.openiam.am.oauth.client.threadsweep}", initialDelay=0)
    @Transactional(readOnly = true)
    public void sweepOAuthProvider() {
        final Map<String, AuthProvider> tempIdCache = new HashMap<String, AuthProvider>();
        final Map<String, AuthProvider> tempNameCache = new HashMap<String, AuthProvider>();

        final List<AuthProvider> providers = this.getOAuthClients();
        if(CollectionUtils.isNotEmpty(providers)) {
            providers.forEach(provider -> {
                tempIdCache.put(provider.getId(), provider);
                tempNameCache.put(provider.getName(), provider);
                provider.generateId2ValueAttributeMap();
            });
        }

        synchronized(this) {
            oAuthIdCache = tempIdCache;
            oAuthNameCache = tempNameCache;
        }
    }

    @Override
    public AuthProvider getCachedOAuthProviderById(String id) {
		return oAuthIdCache.get(id);
    }

    @Override
    public AuthProvider getCachedOAuthProviderByName(String name) {
		return oAuthNameCache.get(name);
    }

    private void validateAndSyncProviderAttributes(AuthProvider provider) throws BasicDataServiceException{
        final AuthAttributeSearchBean sb = new AuthAttributeSearchBean();
        sb.setProviderType(provider.getProviderType());
        final List<AuthAttributeEntity> attributeEntityList = authAttributeDao.getByExample(sb, 0, Integer.MAX_VALUE);
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
}
