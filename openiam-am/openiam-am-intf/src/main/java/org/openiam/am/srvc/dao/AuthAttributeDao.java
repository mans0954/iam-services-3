package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.AuthAttributeEntity;
import org.openiam.core.dao.BaseDao;

import java.util.List;

public interface AuthAttributeDao extends BaseDao<AuthAttributeEntity, String> {
    void deleteByType(String providerType);
    public List<String> getPkListByType(String providerType);

    void deleteByPkList(List<String> pkList);
}
