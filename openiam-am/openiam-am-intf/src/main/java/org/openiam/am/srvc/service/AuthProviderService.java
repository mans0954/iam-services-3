package org.openiam.am.srvc.service;

import org.openiam.am.srvc.domain.AuthAttributeEntity;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.AuthProviderTypeEntity;
import org.openiam.am.srvc.dto.*;
import org.openiam.am.srvc.searchbean.AuthAttributeSearchBean;
import org.openiam.am.srvc.searchbean.AuthProviderSearchBean;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.res.dto.Resource;

import java.util.List;

public interface AuthProviderService {

    /*
    *==================================================
    * AuthProviderType section
    *===================================================
    */
    AuthProviderType getAuthProviderType(String providerType) throws BasicDataServiceException;
    List<AuthProviderType> getAuthProviderTypeList();
    List<AuthProviderType> getSocialAuthProviderTypeList();
    public void addProviderType(AuthProviderType entity) throws BasicDataServiceException;

	AuthProviderTypeEntity getAuthProviderTypeForProvider(final String providerId);

    /*
    *==================================================
    * AuthAttributeEntity section
    *===================================================
    */

    List<AuthAttribute> findAuthAttributeBeans(AuthAttributeSearchBean searchBean, Integer size, Integer from);

    /*
    *==================================================
    *  AuthProviderEntity section
    *===================================================
    */
    List<AuthProvider> findAuthProviderBeans(final AuthProviderSearchBean searchBean, int from, int size);
    int countAuthProviderBeans(final AuthProviderSearchBean entity);
    AuthProvider getProvider(final String id);
    AuthProvider getCachedAuthProvider(final String id);
    String saveAuthProvider(AuthProvider provider, final String requesterId) throws BasicDataServiceException;
    void deleteAuthProvider(String providerId) throws BasicDataServiceException;

    AuthProviderEntity getAuthProvider(final String id);
    /*
    *==================================================
    *  OAuth2 section
    *===================================================
    */

    AuthProvider getOAuthClient(final String clientId);
    List<Resource> getScopesForAuthrorization(String clientId, String userId, Language language) throws BasicDataServiceException;
    public List<Resource> getAuthorizedScopes(String clientId, String userId, Language language);
    void saveClientScopeAuthorization(String providerId, String userId, List<OAuthUserClientXref> oauthUserClientXrefList) throws BasicDataServiceException;
    void saveOAuthCode(OAuthCode oAuthCode);
    OAuthCode getOAuthCode(String code);

    OAuthToken getOAuthToken(String token);
    OAuthToken getOAuthTokenByRefreshToken(String refreshToken);
    OAuthToken saveOAuthToken(OAuthToken oAuthToken);
    
    public List<AuthProvider> getOAuthClients();
}
