package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.core.dao.BaseDao;

import java.util.List;

public interface AuthProviderDao extends BaseDao<AuthProviderEntity, String>{
    List<String> getPkListByType(String providerType);

    void deleteByPkList(List<String> pkList);
    
    public List<AuthProviderEntity> getByResourceId(final String resourceId);
}
