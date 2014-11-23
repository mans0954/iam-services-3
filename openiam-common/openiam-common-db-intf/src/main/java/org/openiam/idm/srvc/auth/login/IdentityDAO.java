package org.openiam.idm.srvc.auth.login;


import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.auth.domain.IdentityEntity;
import org.openiam.idm.srvc.auth.dto.IdentityTypeEnum;

import java.util.List;

public interface IdentityDAO extends BaseDao<IdentityEntity, String> {
   List<IdentityEntity> findByReferredId(String referredId);
   List<IdentityEntity> findByType(IdentityTypeEnum type);
   IdentityEntity findByManagedSysId(String referredId, String managedSysId);
   IdentityEntity getByIdentityManagedSys(String principal, String managedSysId);
}
