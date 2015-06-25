package org.openiam.idm.srvc.mngsys.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.mngsys.domain.MngSysPolicyEntity;

import java.util.List;

public interface MngSysPolicyDAO extends BaseDao<MngSysPolicyEntity, String> {

    List<MngSysPolicyEntity> findByMngSysId(String mngSysId);

    List<MngSysPolicyEntity> findByMngSysIdAndType(String mngSysId, String metadataTypeId);

    MngSysPolicyEntity findPrimaryByMngSysIdAndType(String mngSysId, String metadataTypeId);

}
