package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.core.dao.BaseDao;

import java.util.List;

public interface ContentProviderDao extends BaseDao<ContentProviderEntity, String> {
    public List<ContentProviderEntity> getProviderByDomainPattern(String domainPattern, String contextPath, Boolean isSSL);
    void deleteById(String providerId);
}
