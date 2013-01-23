package org.openiam.am.srvc.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dao.AuthAttributeDao;
import org.openiam.am.srvc.dao.AuthProviderAttributeDao;
import org.openiam.am.srvc.dao.AuthProviderDao;
import org.openiam.am.srvc.dao.AuthProviderTypeDao;
import org.openiam.am.srvc.domain.AuthAttributeEntity;
import org.openiam.am.srvc.domain.AuthProviderAttributeEntity;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.AuthProviderTypeEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private AuthProviderAttributeDao authProviderAttributeDao;
    @Autowired
    private ResourceDAO resourceDao;
    @Autowired
    private ResourceTypeDAO resourceTypeDAO;


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
    public List<AuthProviderTypeEntity> getAuthProviderTypeList() {
        return authProviderTypeDao.findAll();
    }

    @Override
    @Transactional
    public void addProviderType(AuthProviderTypeEntity entity) {
        if(entity==null)
            throw new NullPointerException("provider type is null");
        authProviderTypeDao.save(entity);
    }

    @Override
    @Transactional
    public void deleteProviderType(String providerType) {
        if(providerType==null)
            throw new NullPointerException("provider type is null");
        AuthProviderTypeEntity entity  = this.getAuthProviderType(providerType);
        if(entity!=null){
            this.deleteAuthAttributesByType(providerType);
            this.deleteAuthProviderByType(providerType);
            authProviderTypeDao.delete(entity);
        }
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
    @Override
    public Integer getNumOfAuthAttributeBeans(AuthAttributeEntity searchBean){
        return authAttributeDao.count(searchBean);
    }

    @Override
    @Transactional
    public void addAuthAttribute(AuthAttributeEntity attribute) {
        if(attribute==null)
            throw new NullPointerException("attribute is null");
        if(attribute.getAttributeName()==null)
            throw new NullPointerException("attribute name is null");
        if(attribute.getProviderType()==null)
            throw new NullPointerException("provider type is null");

        attribute.setAuthAttributeId(null);
        authAttributeDao.add(attribute);
    }

    @Override
    @Transactional
    public void updateAuthAttribute(AuthAttributeEntity attribute) {
        if(attribute==null)
            throw new NullPointerException("attribute is null");
        if(attribute.getAttributeName()==null)
            throw new NullPointerException("attribute name is null");
        if(attribute.getProviderType()==null)
            throw new NullPointerException("provider type is null");

        AuthAttributeEntity entity = authAttributeDao.findById(attribute.getAuthAttributeId());
        if(entity!=null){
            entity.setDataType(attribute.getDataType());
            entity.setDescription(attribute.getDescription());
            entity.setRequired(attribute.isRequired());
            entity.setAttributeName(attribute.getAttributeName());
            entity.setProviderType(attribute.getProviderType());
            authAttributeDao.update(entity);
        }
    }

    @Override
    @Transactional
    public void deleteAuthAttribute(String authAttributeId) {
        if(authAttributeId!=null && !authAttributeId.trim().isEmpty()){
            List<String> pkList = Arrays.asList(new String[]{authAttributeId});
            authProviderAttributeDao.deleteByAttributeList(pkList);
            authAttributeDao.deleteByPkList(pkList);
        }
    }

    @Override
    @Transactional
    public void deleteAuthAttributesByType(String providerType) {
        List<String> pkList = authAttributeDao.getPkListByType(providerType);
        if(pkList!=null && !pkList.isEmpty()){
            authProviderAttributeDao.deleteByAttributeList(pkList);
            authAttributeDao.deleteByPkList(pkList);
        }
    }
    /*
    *==================================================
    *  AuthProviderEntity section
    *===================================================
    */
    @Override
    public List<AuthProviderEntity> findAuthProviderBeans(AuthProviderEntity searchBean, Integer size,
                                                          Integer from) {
        return authProviderDao.getByExample(searchBean,from,size);
    }
    @Override
    public Integer getNumOfAuthProviderBeans(AuthProviderEntity searchBean){
        return authProviderDao.count(searchBean);
    }
    @Override
    @Transactional
    public void addAuthProvider(AuthProviderEntity provider) {
        if(provider==null)
            throw new NullPointerException("provider is null");
        if(provider.getProviderType()==null || provider.getProviderType().trim().isEmpty())
            throw new NullPointerException("provider type is null");
        if(provider.getManagedSysId()==null  || provider.getManagedSysId().trim().isEmpty())
            throw new NullPointerException("ManageSys is not set for provider");
        if(provider.getResource() ==null)
            throw new NullPointerException("Resource is not set for provider");
        if(provider.getName()==null  || provider.getName().trim().isEmpty())
            throw new NullPointerException("provider name is null");

         // add resource to db
        ResourceTypeEntity resourceType = resourceTypeDAO.findById(resourceTypeId);
        if(resourceType==null){
            throw new NullPointerException("Cannot create resource for provider. Resource type is not found");
        }

        ResourceEntity resource = provider.getResource();
        resource.setName(resourceTypeId+"_"+provider.getName());
        resource.setResourceType(resourceType);
        resource.setResourceId(null);
        resource = resourceDao.add(resource);

        provider.setProviderId(null);
        provider.setResourceId(resource.getResourceId());
        
        Set<AuthProviderAttributeEntity> providerAttributeSet = provider.getProviderAttributeSet();
        provider.setProviderAttributeSet(null);
        authProviderDao.add(provider);
        if(providerAttributeSet!=null && !providerAttributeSet.isEmpty()){
            syncAuthProviderAttributes(provider, providerAttributeSet);
        }
//        Set<AuthProviderAttributeEntity> providerAttributeSet = provider.getProviderAttributeSet();
//        entity.setProviderAttributeSet(null);
//        authProviderDao.save(entity);
//        if(provider.getProviderAttributeSet()!=null && !provider.getProviderAttributeSet().isEmpty()){
//            entity.setProviderAttributeSet(provider.getProviderAttributeSet());
//            for (AuthProviderAttributeEntity attribute : providerAttributeSet) {
//                attribute.setProviderId(provider.getProviderId());
//                if(attribute.getProviderAttributeId()!=null){
//                    updateAuthProviderAttribute(attribute);
//                }else{
//                    addAuthProviderAttribute(attribute);
//                }
//            }
//        }
    }

    @Override
    @Transactional
    public void updateAuthProvider(AuthProviderEntity provider) {
        if(provider==null)
            throw new NullPointerException("provider is null");
        if(provider.getProviderType()==null || provider.getProviderType().trim().isEmpty())
            throw new NullPointerException("provider type is null");
        if(provider.getManagedSysId()==null  || provider.getManagedSysId().trim().isEmpty())
            throw new NullPointerException("ManageSys is not set for provider");
        if(provider.getName()==null  || provider.getName().trim().isEmpty())
            throw new NullPointerException("provider name is null");
        AuthProviderEntity entity = authProviderDao.findById(provider.getProviderId());
        if(entity!=null){
            entity.setProviderType(provider.getProviderType());
            entity.setManagedSysId(provider.getManagedSysId());
            entity.setName(provider.getName());
            entity.setDescription(provider.getDescription());
            if(provider.getPrivateKey()!=null && provider.getPrivateKey().length>0){
                entity.setPrivateKey(provider.getPrivateKey());
            }
            if(provider.getPublicKey()!=null && provider.getPublicKey().length>0){
                entity.setPublicKey(provider.getPublicKey());
            }
            entity.setSignRequest(provider.isSignRequest());

            // get resource for provider
            if(provider.getResource()!=null){
                ResourceEntity resource = resourceDao.findById(entity.getResourceId());
                resource.setURL(provider.getResource().getURL());
                resourceDao.save(resource);
            }
        }
        
        entity.setProviderAttributeSet(null);
        authProviderDao.save(entity);
        if(provider.getProviderAttributeSet()!=null && !provider.getProviderAttributeSet().isEmpty()){
            syncAuthProviderAttributes(entity, provider.getProviderAttributeSet());
        }
    }

    @Override
    @Transactional
    public void deleteAuthProvider(String providerId) {
        AuthProviderEntity entity = authProviderDao.findById(providerId);
        if(entity!=null){
            this.deleteAuthProviderAttributes(providerId);
            authProviderDao.deleteByPkList(Arrays.asList(new String[]{providerId}));
        }
    }

    @Override
    @Transactional
    public void deleteAuthProviderByType(String providerType) {
        List<String> pkList = authProviderDao.getPkListByType(providerType);
        if(pkList!=null && !pkList.isEmpty()){
            authProviderAttributeDao.deleteByProviderList(pkList);
            authProviderDao.deleteByPkList(pkList);
        }
    }
    /*
    *==================================================
    *  AuthProviderAttribute section
    *===================================================
    */
    @Override
    public AuthProviderAttributeEntity getAuthProviderAttribute(String providerId, String name) {
        return authProviderAttributeDao.getAuthProviderAttribute(providerId, name);
    }

    @Override
    public List<AuthProviderAttributeEntity> getAuthProviderAttributeList(String providerId, Integer size,
                                                                          Integer from) {
        AuthProviderAttributeEntity entity = new AuthProviderAttributeEntity();
        entity.setProviderId(providerId);
        return authProviderAttributeDao.getByExample(entity, from, size);
    }

    @Override
    public Integer getNumOfAuthProviderAttributes(String providerId){
        AuthProviderAttributeEntity attribute = new AuthProviderAttributeEntity();
        attribute.setProviderId(providerId);
        return authProviderAttributeDao.count(attribute);
    }

    @Override
    @Transactional
    public void addAuthProviderAttribute(AuthProviderAttributeEntity attribute) {
        if(attribute==null)
            throw new NullPointerException("attribute is null");
        if(attribute.getProviderId()==null || attribute.getProviderId().trim().isEmpty())
            throw new NullPointerException("Parent Provider  is not set");
        if(attribute.getAttributeId() ==null || attribute.getAttributeId().isEmpty())
            throw new NullPointerException("attribute name is not set");
        if(attribute.getValue() ==null || attribute.getValue().trim().isEmpty())
            throw new NullPointerException("value is not set");
        attribute.setProviderAttributeId(null);
        attribute.setAttribute(null);
        authProviderAttributeDao.add(attribute);
    }


    @Override
    @Transactional
    public void updateAuthProviderAttribute(AuthProviderAttributeEntity attribute) {
        if(attribute==null)
            throw new NullPointerException("attribute is null");
        if(attribute.getProviderId()==null || attribute.getProviderId().trim().isEmpty())
            throw new NullPointerException("Parent Provider  is not set");
        if(attribute.getAttributeId() ==null || attribute.getAttributeId().trim().isEmpty())
            throw new NullPointerException("attribute name is not set");

        AuthProviderAttributeEntity entity = authProviderAttributeDao.getAuthProviderAttribute(attribute.getProviderId(), attribute.getAttributeId());
        if(entity!=null){
            entity.setAttribute(null);
            if(attribute.getValue() ==null || attribute.getValue().trim().isEmpty()){
                //authProviderAttributeDao.delete(entity);
                deleteAuthProviderAttributeByName(attribute.getProviderId(), attribute.getAttributeId());
                return;
            }
            entity.setValue(attribute.getValue());
            authProviderAttributeDao.merge(entity);
        }
    }

    @Override
    @Transactional
    public void deleteAuthProviderAttributeByName(String providerId, String attributeId) {
        authProviderAttributeDao.deleteByAttribute(providerId, attributeId);
    }

    @Override
    @Transactional
    public void deleteAuthProviderAttributes(String providerId) {
        if(providerId!=null && !providerId.trim().isEmpty()){
            authProviderAttributeDao.deleteByProviderList(Arrays.asList(new String[]{providerId}));
        }
    }

    @Transactional
    private void syncAuthProviderAttributes(AuthProviderEntity provider, Set<AuthProviderAttributeEntity> newAttributes){
        AuthAttributeEntity example = new AuthAttributeEntity();
        example.setProviderType(provider.getProviderType());
        List<AuthAttributeEntity> attributeEntityList = this.findAuthAttributeBeans(example, Integer.MAX_VALUE,0);
        Set<String> newAttributesIds = new HashSet<String>();
        for(AuthProviderAttributeEntity providerAttributeEntity: newAttributes){
            newAttributesIds.add(providerAttributeEntity.getAttributeId());
        }
        for(AuthAttributeEntity attr: attributeEntityList){
            if(!newAttributesIds.contains(attr.getAuthAttributeId())){
                // need to delete attribute from provider.
                AuthProviderAttributeEntity providerAttributeEntity = new AuthProviderAttributeEntity();
                providerAttributeEntity.setProviderId(provider.getProviderId());
                providerAttributeEntity.setAttributeId(attr.getAuthAttributeId());
                providerAttributeEntity.setValue(null);
                providerAttributeEntity.setProviderAttributeId("");
                newAttributes.add(providerAttributeEntity);
            }
        }

        for (AuthProviderAttributeEntity attribute : newAttributes) {
            attribute.setProviderId(provider.getProviderId());
            if(attribute.getProviderAttributeId()!=null){
                updateAuthProviderAttribute(attribute);
            }else{
                addAuthProviderAttribute(attribute);
            }
        }
    }
}
