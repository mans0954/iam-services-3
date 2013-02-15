package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.core.dao.BaseDao;

public interface URIPatternDao extends BaseDao<URIPatternEntity, String> {
    void deleteByProvider(String providerId);
    void deleteById(String patternId);
}
