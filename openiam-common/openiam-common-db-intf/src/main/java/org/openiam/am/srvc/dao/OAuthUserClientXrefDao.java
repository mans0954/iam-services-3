package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.OAuthUserClientXrefEntity;
import org.openiam.core.dao.BaseDao;

import java.util.List;

/**
 * Created by alexander on 15/07/15.
 */
public interface OAuthUserClientXrefDao  extends BaseDao<OAuthUserClientXrefEntity, String> {
    List<OAuthUserClientXrefEntity> getByClientAndUser(String clientId, String userId, Boolean isAuthorized);
}
