package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.AuthLevelGroupingEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class AuthLevelGroupingDaoImpl extends BaseDaoImpl<AuthLevelGroupingEntity, String> implements AuthLevelGroupingDao {

	@Override
	protected String getPKfieldName() {
		return "id";
	}

}
