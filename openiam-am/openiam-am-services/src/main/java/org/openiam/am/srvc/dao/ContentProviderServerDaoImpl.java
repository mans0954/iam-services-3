package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.ContentProviderServerEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class ContentProviderServerDaoImpl extends BaseDaoImpl<ContentProviderServerEntity, String> implements ContentProviderServerDao {

	@Override
	protected String getPKfieldName() {
		return "id";
	}
}
