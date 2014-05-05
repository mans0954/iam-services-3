package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.AuthLevelAttributeEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class AuthLevelAttributeDAOImpl extends BaseDaoImpl<AuthLevelAttributeEntity, String> implements AuthLevelAttributeDAO {

	@Override
	protected String getPKfieldName() {
		return "id";
	}

}
