package org.openiam.am.srvc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dao.*;
import org.openiam.am.srvc.domain.*;
import org.openiam.am.srvc.dozer.converter.AuthProviderDozerConverter;
import org.openiam.am.srvc.dozer.converter.OAuthCodeDozerConverter;
import org.openiam.am.srvc.dozer.converter.OAuthTokenDozerConverter;
import org.openiam.am.srvc.dozer.converter.OAuthUserClientXrefDozerConverter;
import org.openiam.am.srvc.dto.*;
import org.openiam.am.srvc.searchbeans.AuthProviderSearchBean;
import org.openiam.authmanager.common.model.ResourceAuthorizationRight;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.base.ws.ResponseCode;
import org.openiam.dozer.converter.ResourceDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.mngsys.service.ManagedSysDAO;
import org.openiam.idm.srvc.policy.service.PolicyDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.internationalization.LocalizedServiceGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service("authProviderService")
public class AuthProviderServiceImpl implements AuthProviderService {
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

    /*
    *==================================================
    * AuthProviderType section
    *===================================================
    */
    @Override
    public AuthProviderTypeEntity getAuthProviderType(String providerType) {
        return authProviderTypeDao.findById(providerType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthProviderTypeEntity> getAuthProviderTypeList() {
        return authProviderTypeDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthProviderTypeEntity> getSocialAuthProviderTypeList(){
        List<AuthProviderTypeEntity> allTypes = getAuthProviderTypeList();
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
        return selectedTypes;
    }

    /*
    *==================================================
    * AuthAttributeEntity section
    *===================================================
    */
    @Override
    public List<AuthAttributeEntity> findAuthAttributeBeans(AuthAttributeEntity searchBean, Integer size,
                                                            Integer from) {
        return authAttributeDao.getByExample(searchBean, from, size);
    }
  
    /*
    *==================================================
    *  AuthProviderEntity section
    *===================================================
    */
    @Override
    public List<AuthProviderEntity> findAuthProviderBeans(final AuthProviderSearchBean searchBean, int from, int size) {
        return authProviderDao.getByExample(searchBean,from,size);
    }
    
    @Override
    @Transactional
    public void saveAuthProvider(AuthProviderEntity provider, final String requestorId) throws BasicDataServiceException{
    	provider.setType(authProviderTypeDao.findById(provider.getType().getId()));
    	if(provider.getType() == null) {
    		throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_TYPE_NOT_SET);
    	}
    	
    	if(!provider.getType().isHasPasswordPolicy()) {
    		provider.setPasswordPolicy(null);
    	} else {
    		if(provider.getPasswordPolicy() == null || StringUtils.isBlank(provider.getPasswordPolicy().getId())) {
    			if(provider.getType().isPasswordPolicyRequired()) {
    				throw new IllegalArgumentException("Password Policy not set");
    			}
    			provider.setPasswordPolicy(null);
    		} else {
    			provider.setPasswordPolicy(policyDAO.findById(provider.getPasswordPolicy().getId()));
    		}
    	}
    	
    	if(!provider.getType().isHasAuthnPolicy()) {
    		provider.setAuthenticationPolicy(null);
    	} else {
    		if(provider.getAuthenticationPolicy() == null || StringUtils.isBlank(provider.getAuthenticationPolicy().getId())) {
    			if(provider.getType().isAuthnPolicyRequired()) {
    				throw new IllegalArgumentException("Authenticaiton Policy not set");
    			}
    			provider.setAuthenticationPolicy(null);
    		} else {
    			provider.setAuthenticationPolicy(policyDAO.findById(provider.getAuthenticationPolicy().getId()));
    		}
    	}
    	
    	if(provider.getManagedSystem() != null && StringUtils.isNotBlank(provider.getManagedSystem().getId())) {
    		provider.setManagedSystem(managedSystemDAO.findById(provider.getManagedSystem().getId()));
    	} else {
    		provider.setManagedSystem(null);
    	}
    	
        final AuthProviderEntity dbEntity = authProviderDao.findById(provider.getId());
        if(dbEntity!=null){
        	provider.setResource(dbEntity.getResource());
        	provider.setResourceAttributeMap(dbEntity.getResourceAttributeMap());
        	provider.setDefaultProvider(dbEntity.isDefaultProvider());
        	provider.setContentProviders(dbEntity.getContentProviders());
        	if(CollectionUtils.isEmpty(provider.getAttributes())) {
        		if(dbEntity.getAttributes() != null) {
        			provider.setAttributes(dbEntity.getAttributes());
        			provider.getAttributes().clear();
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
        	provider.setContentProviders(null);
        	provider.setResourceAttributeMap(null);
        	provider.setDefaultProvider(false);
        }
        
        if(provider.getResource() == null || StringUtils.isBlank(provider.getResource().getId())) {
        	ResourceTypeEntity resourceType = resourceTypeDAO.findById(resourceTypeId);
            if(resourceType==null){
                throw new NullPointerException("Cannot create resource for provider. Resource type is not found");
            }
            
            final ResourceEntity resource = new ResourceEntity();
            resource.setName(System.currentTimeMillis() + "_" + provider.getName());
            resource.setResourceType(resourceType);
            resourceService.save(resource, requestorId);
            provider.setResource(resource);
        }
        provider.getResource().setCoorelatedName(provider.getName());
        
        if(CollectionUtils.isNotEmpty(provider.getAttributes())) {
        	for(final Iterator<AuthProviderAttributeEntity> it = provider.getAttributes().iterator(); it.hasNext();) {
        		final AuthProviderAttributeEntity attribute = it.next();
        		if(StringUtils.isNotBlank(attribute.getValue())) {
	        		if(attribute.getAttribute() != null && StringUtils.isNotBlank(attribute.getAttribute().getId())) {
	        			attribute.setAttribute(authAttributeDao.findById(attribute.getAttribute().getId()));
	        		} else {
	        			attribute.setAttribute(null);
	        		}
	        		attribute.setProvider(provider);
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
        
        if(provider.getId() == null) {
        	authProviderDao.save(provider);
        } else {
        	authProviderDao.merge(provider);
        }
    }

    @Override
    @Transactional
    public void deleteAuthProvider(String providerId) throws BasicDataServiceException {
        AuthProviderEntity entity = authProviderDao.findById(providerId);
        if(entity!=null){
        	if(CollectionUtils.isNotEmpty(entity.getContentProviders())) {
        		throw new BasicDataServiceException(ResponseCode.LINKED_TO_ONE_OR_MORE_CONTENT_PROVIDERS);
        	}
        	if(CollectionUtils.isNotEmpty(entity.getUriPatterns())) {
        		throw new BasicDataServiceException(ResponseCode.LINKED_TO_ONE_OR_MORE_URI_PATTERNS);
        	}
        	authProviderDao.delete(entity);
            resourceService.deleteResource(entity.getResource().getId());
        }
    }

	@Override
	public int countAuthProviderBeans(AuthProviderSearchBean searchBean) {
		return authProviderDao.count(searchBean);
	}

	@Override
	public AuthProviderEntity getAuthProvider(String id) {
		return authProviderDao.findById(id);
	}

    /*
    *==================================================
    *  OAuth2 section
    *===================================================
    */
    @Override
    public AuthProvider getOAuthClient(final String clientId){
        return authProviderDozerConverter.convertToDTO(authProviderDao.getOAuthClient(clientId), true);
    }

    @Override
    @LocalizedServiceGet
    public List<Resource> getScopesForAuthrorization(String clientId, String userId, Language language) throws BasicDataServiceException {
        AuthProvider provider = getOAuthClient(clientId);
        Set<String> clientScopesIds = null;
        // determine if the client is authorized for some scopes
        boolean isClientAuthorized = false;
        if(CollectionUtils.isNotEmpty(provider.getAttributes())) {
            AuthProviderAttribute scopes =  provider.getAttributes().stream().filter(attr -> "OAuthClientScopes".equals(attr.getAttributeId())).findFirst().get();
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
                // get user resource
                Set<ResourceAuthorizationRight>  userResources = authorizationManagerService.getResourcesForUser(userId);
                if(CollectionUtils.isNotEmpty(userResources)) {
                    Set<String> userResourceIds = userResources.stream().map(res->res.getEntity().getId()).collect(Collectors.toSet());
                    // do intersection between unauthorized scopes and user resources
                    // leave only those scopes that user have access to
                    clientScopesIds.retainAll(userResourceIds);
                }
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
    public List<Resource> getAuthorizedScopes(String clientId, String userId, Language language){
        List<OAuthUserClientXrefEntity> authorizedResources = oauthUserClientXrefDao.getByClientAndUser(clientId, userId, true);
        if(CollectionUtils.isNotEmpty(authorizedResources)){
            Set<String> authorizedResourcesIds = authorizedResources.stream().map(xref -> xref.getScope().getId()).collect(Collectors.toSet());
            return  resourceDozerConverter.convertToDTOList(resourceService.findResourcesByIds(authorizedResourcesIds), false);
        }
        return null;
    }

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

    public void saveOAuthCode(OAuthCode oAuthCode){
        OAuthCodeEntity entity = oauthCodeDozerConverter.convertToEntity(oAuthCode, true);
        entity.setClient(this.getAuthProvider(oAuthCode.getClientId()));
        entity.setUser(userDataService.getUser(oAuthCode.getUserId()));
        entity.setCode(oAuthCode.getCode());
        entity.setExpiredOn(oAuthCode.getExpiredOn());
        entity.setRedirectUrl(oAuthCode.getRedirectUrl());
        oAuthCodeDao.save(entity);
    }

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
    public OAuthToken getOAuthToken(String token) {
        OAuthTokenEntity tokenEntity = oAuthTokenDao.getByAccessToken(token);
        return oauthTokenDozerConverter.convertToDTO(tokenEntity, false);
    }

    @Override
    public OAuthToken getOAuthTokenByRefreshToken(String refreshToken){
        OAuthTokenEntity tokenEntity = oAuthTokenDao.getByRefreshToken(refreshToken);
        OAuthToken token = null;
        if(tokenEntity!=null){
            token = oauthTokenDozerConverter.convertToDTO(tokenEntity, false);
            oAuthTokenDao.delete(tokenEntity);
        }
        return token;
    }

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
}
