package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.core.dao.BaseDao;

import java.util.List;

public interface ContentProviderDao extends BaseDao<ContentProviderEntity, String> {
    public List<ContentProviderEntity> getProviderByDomainPattern(String domainPattern, Boolean isSSL);
    void deleteById(String providerId);
    public List<ContentProviderEntity> getByResourceId(final String resourceId);
}
