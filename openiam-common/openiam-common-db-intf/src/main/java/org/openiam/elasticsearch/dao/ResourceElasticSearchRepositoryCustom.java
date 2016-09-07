package org.openiam.elasticsearch.dao;

import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.res.domain.ResourceEntity;

public interface ResourceElasticSearchRepositoryCustom extends AbstractCustomElasticSearchRepository<ResourceEntity, ResourceSearchBean, String> {

}
