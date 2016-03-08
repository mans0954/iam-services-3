package org.openiam.am.srvc.service;

import org.openiam.am.srvc.domain.AuthAttributeEntity;
import org.openiam.am.srvc.domain.AuthProviderAttributeEntity;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.AuthProviderTypeEntity;
import org.openiam.am.srvc.dto.AuthProvider;
import org.openiam.am.srvc.dto.OAuthCode;
import org.openiam.am.srvc.dto.OAuthToken;
import org.openiam.am.srvc.dto.OAuthUserClientXref;
import org.openiam.am.srvc.searchbeans.AuthAttributeSearchBean;
import org.openiam.am.srvc.searchbeans.AuthProviderSearchBean;
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
	AuthProviderTypeEntity getAuthProviderTypeForProvider(final String providerId);
    AuthProviderTypeEntity getAuthProviderType(String providerType);
    List<AuthProviderTypeEntity> getAuthProviderTypeList();
    List<AuthProviderTypeEntity> getSocialAuthProviderTypeList();
    /*
    *==================================================
    * AuthAttributeEntity section
    *===================================================
    */

    List<AuthAttributeEntity> findAuthAttributeBeans(AuthAttributeSearchBean searchBean, Integer size, Integer from);

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
    public AuthProvider getProvider(final String id);

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
