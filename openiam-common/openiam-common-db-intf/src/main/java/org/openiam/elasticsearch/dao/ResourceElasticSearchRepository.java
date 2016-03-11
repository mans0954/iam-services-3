package org.openiam.elasticsearch.dao;

import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceElasticSearchRepository extends OpeniamElasticSearchRepository<ResourceEntity, String>, ResourceElasticSearchRepositoryCustom {

}
