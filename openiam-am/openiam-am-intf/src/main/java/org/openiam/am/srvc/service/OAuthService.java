package org.openiam.am.srvc.service;

import org.openiam.am.srvc.domain.OAuthTokenEntity;

/**
 * Created by alexander on 24.04.15.
 */
public interface OAuthService {
    OAuthTokenEntity getOAuthTokenByToken(String accessToken);
    OAuthTokenEntity getOAuthTokenByRefreshToken(String refreshToken);
    OAuthTokenEntity saveOAuthToken(OAuthTokenEntity oAuthTokenEntity);
}
