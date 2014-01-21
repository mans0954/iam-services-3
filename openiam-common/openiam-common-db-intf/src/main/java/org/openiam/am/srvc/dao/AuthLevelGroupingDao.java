package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.AuthLevelGroupingEntity;
import org.openiam.core.dao.BaseDao;

public interface AuthLevelGroupingDao extends BaseDao<AuthLevelGroupingEntity, String> {

	public AuthLevelGroupingEntity findByName(final String name);
}
