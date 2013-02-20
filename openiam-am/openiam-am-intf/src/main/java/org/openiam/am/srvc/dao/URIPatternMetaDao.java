package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.URIPatternMetaEntity;
import org.openiam.core.dao.BaseDao;

public interface URIPatternMetaDao extends BaseDao<URIPatternMetaEntity, String> {
    void deleteById(String metaId);

}
