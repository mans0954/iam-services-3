package org.openiam.mq.constants.api;

/**
 * Created by alexander on 28/09/16.
 */
public enum OAuthAPI implements OpenIAMAPI {
    GetScopesForAuthrorization,
    SaveClientScopeAuthorization,
    SaveOAuthCode,
    SaveOAuthToken,
    GetAuthorizedScopes,
    GetOAuthCode,
    GetOAuthToken,
    GetOAuthTokenByRefreshToken,
    GetCachedOAuthProviderById,
    GetCachedOAuthProviderByName,
    GetClient,
    RefreshOAuthCache,
    CleanAuthorizedScopes,
    GetAuthorizedScopesByUser, DeAuthorizeClient;
}
