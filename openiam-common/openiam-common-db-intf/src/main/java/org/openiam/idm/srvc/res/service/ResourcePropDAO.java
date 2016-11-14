package org.openiam.idm.srvc.res.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;

import java.util.List;


public interface ResourcePropDAO  extends BaseDao<ResourcePropEntity, String> {
    String findValueByName(String resourceId, String name);
    List<ResourcePropEntity> getProperties(String resourceId);
}