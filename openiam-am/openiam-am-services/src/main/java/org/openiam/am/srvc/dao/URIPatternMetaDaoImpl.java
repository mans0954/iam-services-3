package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.URIPatternMetaEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class URIPatternMetaDaoImpl extends BaseDaoImpl<URIPatternMetaEntity, String> implements URIPatternMetaDao {

	@Override
	protected String getPKfieldName() {
		return "id";
	}
}
