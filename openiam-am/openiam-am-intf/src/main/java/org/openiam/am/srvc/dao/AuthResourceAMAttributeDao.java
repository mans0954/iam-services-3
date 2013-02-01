package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.AuthResourceAMAttributeEntity;
import org.openiam.core.dao.BaseDao;

public interface AuthResourceAMAttributeDao extends BaseDao<AuthResourceAMAttributeEntity, String> {
    public void deleteById(String attributeId);
}
