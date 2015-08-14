package org.openiam.idm.srvc.meta.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.meta.domain.MetadataValidValueEntity;

public interface MetadataValidValueDAO extends BaseDao<MetadataValidValueEntity, String> {

	void deleteByMetaElementId(final String metaElementId);
}
