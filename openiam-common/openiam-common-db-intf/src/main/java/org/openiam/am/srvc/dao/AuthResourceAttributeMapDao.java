package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.AuthResourceAttributeMapEntity;
import org.openiam.core.dao.BaseDao;

import java.util.List;

public interface AuthResourceAttributeMapDao extends BaseDao<AuthResourceAttributeMapEntity, String> {
    void deleteById(String attributeId);
    int deleteByProviderId(String providerId);
    int deleteByAMAttributeId(String attributeId);
    void deleteByProviderList(List<String> providerList);

}


