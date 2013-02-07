package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.AuthLevelEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class AuthLevelDaoImpl extends BaseDaoImpl<AuthLevelEntity, String> implements AuthLevelDao {

	@Override
	protected String getPKfieldName() {
		return "id";
	}
}
