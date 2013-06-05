package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.AuthProviderAttributeEntity;
import org.openiam.core.dao.BaseDao;

import java.util.List;

public interface AuthProviderAttributeDao extends BaseDao<AuthProviderAttributeEntity, String> {
    void deleteByProviderList(List<String> pkList);
    void deleteByAttribute(String providerId, String attributeId);

    AuthProviderAttributeEntity getAuthProviderAttribute(String providerId, String attributeId);
    void deleteByAttributeList(List<String> pkList);
}
