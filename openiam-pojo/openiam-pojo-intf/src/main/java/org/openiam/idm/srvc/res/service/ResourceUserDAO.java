package org.openiam.idm.srvc.res.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.domain.ResourceUserEmbeddableId;
import org.openiam.idm.srvc.res.domain.ResourceUserEntity;
import org.openiam.idm.srvc.res.dto.ResourceUserId;

import java.util.List;

public interface ResourceUserDAO extends BaseDao<ResourceUserEntity, ResourceUserEmbeddableId>  {

}