package org.openiam.idm.srvc.res.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.res.domain.ResourceEntity;

import java.util.List;

public interface ResourceDAO extends BaseDao<ResourceEntity, String> {

    public ResourceEntity findByName(final String resourceName);


}