package org.openiam.am.srvc.service;

import org.openiam.am.srvc.domain.AuthAttributeEntity;
import org.openiam.am.srvc.domain.AuthProviderAttributeEntity;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.AuthProviderTypeEntity;

import java.util.List;

public interface AuthProviderService {

    /*
    *==================================================
    * AuthProviderType section
    *===================================================
    */
    public AuthProviderTypeEntity getAuthProviderType(String providerType);
    public List<AuthProviderTypeEntity> getAuthProviderTypeList();

    /**
    * Add new provider type to AM.
    */
    public void addProviderType(AuthProviderTypeEntity entity);

    /**
     * Delete existing provider type from AM
     * @param providerType
     */
    public void deleteProviderType(String providerType);

    /*
    *==================================================
    * AuthAttributeEntity section
    *===================================================
    */

    public List<AuthAttributeEntity> findAuthAttributeBeans(AuthAttributeEntity searchBean, Integer size, Integer from);
    public Integer getNumOfAuthAttributeBeans(AuthAttributeEntity searchBean);
    public void addAuthAttribute(AuthAttributeEntity attribute);
    public void updateAuthAttribute(AuthAttributeEntity attribute);
    public void deleteAuthAttribute(String authAttributeId);
    public void deleteAuthAttributesByType(String providerType);


    /*
    *==================================================
    *  AuthProviderEntity section
    *===================================================
    */
    public List<AuthProviderEntity> findAuthProviderBeans(AuthProviderEntity searchBean, Integer size, Integer from);
    public Integer getNumOfAuthProviderBeans(AuthProviderEntity searchBean);
    public void addAuthProvider(AuthProviderEntity attribute);
    public void updateAuthProvider(AuthProviderEntity attribute);
    public void deleteAuthProvider(String providerId);
    public void deleteAuthProviderByType(String providerType);


    /*
    *==================================================
    *  AuthProviderAttribute section
    *===================================================
    */
    public AuthProviderAttributeEntity getAuthProviderAttribute(String providerId, String name);
    public List<AuthProviderAttributeEntity> getAuthProviderAttributeList(String providerId, Integer size, Integer from);
    public Integer getNumOfAuthProviderAttributes(String providerId);
    public void addAuthProviderAttribute(AuthProviderAttributeEntity attribute);
    public void updateAuthProviderAttribute(AuthProviderAttributeEntity attribute);
    public void deleteAuthProviderAttributeByName(String providerId, String attributeId);
    public void deleteAuthProviderAttributes(String providerId);

}
