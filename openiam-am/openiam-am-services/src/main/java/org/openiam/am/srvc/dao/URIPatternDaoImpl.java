package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class URIPatternDaoImpl extends BaseDaoImpl<URIPatternEntity, String> implements URIPatternDao {

	@Override
	protected String getPKfieldName() {
		return "id";
	}
}
