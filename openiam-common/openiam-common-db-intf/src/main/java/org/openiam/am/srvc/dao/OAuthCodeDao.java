package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.OAuthCodeEntity;
import org.openiam.am.srvc.domain.pk.OAuthCodeIdEntity;
import org.openiam.core.dao.BaseDao;

/**
 * Created by alexander on 21/07/15.
 */
public interface OAuthCodeDao  extends BaseDao<OAuthCodeEntity, OAuthCodeIdEntity> {
    OAuthCodeEntity getByClientAndUser(String providerId, String userId);
    OAuthCodeEntity getByCode(String code);
}
