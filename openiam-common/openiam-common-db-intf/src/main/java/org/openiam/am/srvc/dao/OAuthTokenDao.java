package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.OAuthTokenEntity;
import org.openiam.core.dao.BaseDao;

/**
 * Created by alexander on 24.04.15.
 */
public interface OAuthTokenDao extends BaseDao<OAuthTokenEntity, String> {
    OAuthTokenEntity getByRefreshToken(String refreshToken);
    OAuthTokenEntity getByAccessToken(String accessToken);
}
