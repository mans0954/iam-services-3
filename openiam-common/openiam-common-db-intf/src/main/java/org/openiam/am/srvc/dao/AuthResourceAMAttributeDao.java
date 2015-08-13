package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.AuthResourceAMAttributeEntity;
import org.openiam.core.dao.BaseDao;

public interface AuthResourceAMAttributeDao extends BaseDao<AuthResourceAMAttributeEntity, String> {
    void deleteById(String attributeId);
}
