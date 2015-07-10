package org.openiam.am.srvc.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dao.*;
import org.openiam.am.srvc.domain.*;
import org.openiam.am.srvc.dto.AuthProviderType;
import org.openiam.am.srvc.searchbeans.AuthProviderSearchBean;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.mngsys.service.ManagedSysDAO;
import org.openiam.idm.srvc.policy.service.PolicyDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    public AuthProviderEntity getOAuthClient(final String clientId){
        return authProviderDao.getOAuthClient(clientId);
    }
}
