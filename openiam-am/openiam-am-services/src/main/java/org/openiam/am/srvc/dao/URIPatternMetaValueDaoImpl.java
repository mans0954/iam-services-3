package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.URIPatternMetaValueEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class URIPatternMetaValueDaoImpl extends BaseDaoImpl<URIPatternMetaValueEntity, String> implements URIPatternMetaValueDao {

	@Override
	protected String getPKfieldName() {
		return "id";
	}

}
