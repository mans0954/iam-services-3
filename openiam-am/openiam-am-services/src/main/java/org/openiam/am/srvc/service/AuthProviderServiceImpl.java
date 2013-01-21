package org.openiam.am.srvc.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.util.encoders.Hex;
import org.openiam.am.srvc.dao.AuthAttributeDao;
import org.openiam.am.srvc.dao.AuthProviderAttributeDao;
import org.openiam.am.srvc.dao.AuthProviderDao;
import org.openiam.am.srvc.dao.AuthProviderTypeDao;
import org.openiam.am.srvc.domain.AuthAttributeEntity;
import org.openiam.am.srvc.domain.AuthProviderAttributeEntity;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.AuthProviderTypeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service("authProviderService")
public class AuthProviderServiceImpl implements AuthProviderService {
    private final Log log = LogFactory.getLog(this.getClass());
    @Autowired
    private AuthProviderTypeDao authProviderTypeDao;
    @Autowired
    private AuthAttributeDao authAttributeDao;
    @Autowired
    private AuthProviderDao authProviderDao;
    @Autowired
    private AuthProviderAttributeDao authProviderAttributeDao;

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


        authAttributeDao.save(attribute);
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
        AuthAttributeEntity entity = authAttributeDao.findById(authAttributeId);
        if(entity!=null){
            authAttributeDao.delete(entity);
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
        List<AuthProviderEntity> result = authProviderDao.getByExample(searchBean,from,size);

        for (AuthProviderEntity provider: result){
            if(provider.getPrivateKey()!=null && !provider.getPrivateKey().trim().isEmpty()){
                provider.setPrivateKey(new String(this.decodeKey(provider.getPrivateKey())));
            }
            if(provider.getPublicKey()!=null && !provider.getPublicKey().trim().isEmpty()){
                provider.setPublicKey(new String(this.decodeKey(provider.getPublicKey())));
            }
        }

        return result;
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
        if(provider.getResourceId()==null  || provider.getResourceId().trim().isEmpty())
            throw new NullPointerException("Resource is not set for provider");
        if(provider.getName()==null  || provider.getName().trim().isEmpty())
            throw new NullPointerException("provider name is null");




        authProviderDao.save(provider);
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
        if(provider.getResourceId()==null  || provider.getResourceId().trim().isEmpty())
            throw new NullPointerException("Resource is not set for provider");
        if(provider.getName()==null  || provider.getName().trim().isEmpty())
            throw new NullPointerException("provider name is null");
        AuthProviderEntity entity = authProviderDao.findById(provider.getProviderId());
        if(entity!=null){
            entity.setProviderType(provider.getProviderType());
            entity.setManagedSysId(provider.getManagedSysId());
            entity.setResourceId(provider.getResourceId());
            entity.setName(provider.getName());
            entity.setDescription(provider.getDescription());
            if(provider.getPrivateKey()!=null && !provider.getPrivateKey().trim().isEmpty()){
                entity.setPrivateKey(this.encodeKey(provider.getPrivateKey()));
            }
            if(provider.getPublicKey()!=null && !provider.getPublicKey().trim().isEmpty()){
                entity.setPublicKey(this.encodeKey(provider.getPublicKey()));
            }
            entity.setSignRequest(provider.isSignRequest());
        }
        authProviderDao.save(provider);
    }

    @Override
    @Transactional
    public void deleteAuthProvider(String providerId) {
        AuthProviderEntity entity = authProviderDao.findById(providerId);
        if(entity!=null){
            this.deleteAuthProviderAttributes(providerId);
            authProviderDao.delete(entity);
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

        authProviderAttributeDao.save(attribute);
    }

    public Integer getNumOfAuthProviderAttributes(String providerId){
        AuthProviderAttributeEntity attribute = new AuthProviderAttributeEntity();
        attribute.setProviderId(providerId);
        return authProviderAttributeDao.count(attribute);
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
        if(attribute.getValue() ==null || attribute.getValue().trim().isEmpty())
            throw new NullPointerException("value is not set");

        AuthProviderAttributeEntity entity = authProviderAttributeDao.findById(attribute.getProviderAttributeId());
        if(entity!=null){
            entity.setProviderId(attribute.getProviderId());
            entity.setAttributeId(attribute.getAttributeId());
            entity.setDataType(attribute.getDataType());
            entity.setValue(attribute.getValue());
            authProviderAttributeDao.save(entity);
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

    private String encodeKey(String data) {
        return encodeKey(data.getBytes());
    }
    private String encodeKey(byte[] data) {
        return new String(Hex.encode(data));
    }
    private byte[] decodeKey(String data) {
        return Hex.decode(data);
    }


}
