package org.openiam.idm.srvc.meta.service;

import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.meta.domain.MetadataValidValueEntity;
import org.springframework.stereotype.Repository;

@Repository("metadataValidValueDAO")
public class MetadataValidValueDAOImpl extends BaseDaoImpl<MetadataValidValueEntity, String> implements MetadataValidValueDAO {

	@Override
	protected String getPKfieldName() {
		return "id";
	}

}
