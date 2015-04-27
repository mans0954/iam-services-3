package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.OAuthCodeEntity;
import org.openiam.core.dao.BaseDao;

/**
 * Created by alexander on 24.04.15.
 */
public interface OAuthCodeDao extends BaseDao<OAuthCodeEntity, String> {
    OAuthCodeEntity getByClientAndUser(String clientId, String userId);
    OAuthCodeEntity getByClientAndCode(String clientId, String code);
}
