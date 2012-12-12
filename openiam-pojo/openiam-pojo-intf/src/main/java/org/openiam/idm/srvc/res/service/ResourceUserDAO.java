package org.openiam.idm.srvc.res.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.res.domain.ResourceUserEntity;


public interface ResourceUserDAO extends BaseDao<ResourceUserEntity, String>  {

	public ResourceUserEntity getRecord(final String resourceId, final String userId);
}