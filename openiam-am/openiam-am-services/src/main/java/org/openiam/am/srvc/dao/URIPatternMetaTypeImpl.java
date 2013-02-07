package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.URIPatternMetaTypeEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class URIPatternMetaTypeImpl extends BaseDaoImpl<URIPatternMetaTypeEntity, String> implements URIPatternMetaTypeDao {

	@Override
	protected String getPKfieldName() {
		return "id";
	}
}
