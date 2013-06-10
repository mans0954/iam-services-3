package org.openiam.idm.srvc.mngsys.service;

import java.util.List;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysRuleEntity;

public interface ManagedSysRuleDAO extends
        BaseDao<ManagedSysRuleEntity, String> {

    List<ManagedSysRuleEntity> findbyManagedSystemId(String managedSysId);

}