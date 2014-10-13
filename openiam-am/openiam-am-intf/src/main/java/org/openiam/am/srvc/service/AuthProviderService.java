package org.openiam.am.srvc.service;

import org.openiam.am.srvc.domain.AuthAttributeEntity;
import org.openiam.am.srvc.domain.AuthProviderAttributeEntity;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.AuthProviderTypeEntity;
import org.openiam.exception.BasicDataServiceException;

import java.util.List;

public interface AuthProviderService {

    /*
    *==================================================
    * AuthProviderType section
    *===================================================
    */
    public AuthProviderTypeEntity getAuthProviderType(String providerType);
    public List<AuthProviderTypeEntity> getAuthProviderTypeList();

    /*
    *==================================================
    * AuthAttributeEntity section
    *===================================================
    */

    public List<AuthAttributeEntity> findAuthAttributeBeans(AuthAttributeEntity searchBean, Integer size, Integer from);

    /*
    *==================================================
    *  AuthProviderEntity section
    *===================================================
    */
    public int countAuthProviderBeans(final AuthProviderEntity entity);
    public List<AuthProviderEntity> findAuthProviderBeans(AuthProviderEntity searchBean, Integer size, Integer from);
    public void saveAuthProvider(AuthProviderEntity attribute, final String requestorId) throws BasicDataServiceException;
    public void deleteAuthProvider(String providerId) throws BasicDataServiceException;


}
