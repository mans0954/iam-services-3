package org.openiam.am.srvc.service;

import org.openiam.am.srvc.domain.AuthAttributeEntity;
import org.openiam.am.srvc.domain.AuthProviderAttributeEntity;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.AuthProviderTypeEntity;
import org.openiam.am.srvc.searchbeans.AuthProviderSearchBean;
import org.openiam.exception.BasicDataServiceException;

import java.util.List;

public interface AuthProviderService {

    /*
    *==================================================
    * AuthProviderType section
    *===================================================
    */
    AuthProviderTypeEntity getAuthProviderType(String providerType);
    List<AuthProviderTypeEntity> getAuthProviderTypeList();
    List<AuthProviderTypeEntity> getSocialAuthProviderTypeList();
    /*
    *==================================================
    * AuthAttributeEntity section
    *===================================================
    */

    List<AuthAttributeEntity> findAuthAttributeBeans(AuthAttributeEntity searchBean, Integer size, Integer from);

    /*
    *==================================================
    *  AuthProviderEntity section
    *===================================================
    */
    int countAuthProviderBeans(final AuthProviderSearchBean entity);
    AuthProviderEntity getAuthProvider(final String id);
    List<AuthProviderEntity> findAuthProviderBeans(final AuthProviderSearchBean searchBean, int from, int size);
    void saveAuthProvider(AuthProviderEntity attribute, final String requestorId) throws BasicDataServiceException;
    void deleteAuthProvider(String providerId) throws BasicDataServiceException;

    /*
    *==================================================
    *  OAuth2 section
    *===================================================
    */

    AuthProviderEntity getOAuthClient(final String clientId);

}
