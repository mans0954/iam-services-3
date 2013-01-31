package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.AuthResourceAttributeMapEntity;
import org.openiam.core.dao.BaseDao;

public interface AuthResourceAttributeMapDao extends BaseDao<AuthResourceAttributeMapEntity, String> {
    void deleteById(String attributeId);
    int deleteByProviderId(String providerId);
}
