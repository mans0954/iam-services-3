package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.core.dao.BaseDao;

import java.util.List;

public interface AuthProviderDao extends BaseDao<AuthProviderEntity, String>{

    List<AuthProviderEntity> getByResourceId(final String resourceId);

    List<AuthProviderEntity> getByManagedSysId(final String managedSysId);

    AuthProviderEntity getOAuthClient(final String clientId);
}
