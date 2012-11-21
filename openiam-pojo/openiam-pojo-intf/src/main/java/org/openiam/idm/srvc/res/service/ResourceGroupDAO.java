package org.openiam.idm.srvc.res.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.res.domain.ResourceGroupEntity;

public interface ResourceGroupDAO extends BaseDao<ResourceGroupEntity, String> {

	public ResourceGroupEntity getRecord(final String resourceId, final String groupId);
}
