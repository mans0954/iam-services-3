package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.AuthResourceAttributeEntity;
import org.openiam.core.dao.BaseDao;

public interface AuthResourceAttributeDao extends BaseDao<AuthResourceAttributeEntity, String> {
    void deleteById(String attributeId);
    int deleteByResourceId(String resourceId);


}
