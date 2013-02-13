package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.core.dao.BaseDao;

public interface ContentProviderDao extends BaseDao<ContentProviderEntity, String> {

    void deleteById(String providerId);
}
