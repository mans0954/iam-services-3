package org.openiam.am.srvc.dao;

import java.util.List;

import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.core.dao.BaseDao;

public interface URIPatternDao extends BaseDao<URIPatternEntity, String> {
    void deleteByProvider(String providerId);
    void deleteById(String patternId);
    public List<URIPatternEntity> getByResourceId(final String resourceId);
    public List<URIPatternEntity> getURIPatternsForContentProviderMatchingPattern(final String contentProviderId, final String pattern);
}
