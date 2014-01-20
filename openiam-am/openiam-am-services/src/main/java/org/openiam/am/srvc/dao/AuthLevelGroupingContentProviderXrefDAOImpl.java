package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.AuthLevelGroupingContentProviderXrefEntity;
import org.openiam.am.srvc.domain.AuthProviderAttributeEntity;
import org.openiam.am.srvc.domain.pk.AuthLevelGroupingContentProviderXrefIdEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class AuthLevelGroupingContentProviderXrefDAOImpl 
	extends BaseDaoImpl<AuthLevelGroupingContentProviderXrefEntity, AuthLevelGroupingContentProviderXrefIdEntity>
		implements AuthLevelGroupingContentProviderXrefDAO {

	@Override
	protected String getPKfieldName() {
		return "id";
	}

}
