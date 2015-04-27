package org.openiam.am.srvc.service;

import org.openiam.am.srvc.domain.OAuthCodeEntity;
import org.openiam.am.srvc.domain.OAuthTokenEntity;

/**
 * Created by alexander on 24.04.15.
 */
public interface OAuthService {
    public OAuthCodeEntity getOAuthCode(String clientId, String userId);
    public OAuthCodeEntity getOAuthCodeByCode(String clientId, String code);
    public OAuthCodeEntity saveOAuthCode(OAuthCodeEntity oAuthCodeEntity);

    public OAuthTokenEntity getOAuthTokenByToken(String accessToken);
    public OAuthTokenEntity getOAuthTokenByRefreshToken(String refreshToken);
    public OAuthTokenEntity saveOAuthToken(OAuthTokenEntity oAuthTokenEntity);
}
