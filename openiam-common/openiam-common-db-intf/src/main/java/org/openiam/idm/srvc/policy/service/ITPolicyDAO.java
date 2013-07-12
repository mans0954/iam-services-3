package org.openiam.idm.srvc.policy.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.policy.domain.ITPolicyEntity;

public interface ITPolicyDAO extends BaseDao<ITPolicyEntity, String> {
    ITPolicyEntity findITPolicy();
}
