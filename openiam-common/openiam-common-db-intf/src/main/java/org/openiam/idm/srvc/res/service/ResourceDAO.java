package org.openiam.idm.srvc.res.service;

import java.util.List;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.res.domain.ResourceEntity;

public interface ResourceDAO extends BaseDao<ResourceEntity, String> {

    public ResourceEntity findByName(final String resourceName);
    
}