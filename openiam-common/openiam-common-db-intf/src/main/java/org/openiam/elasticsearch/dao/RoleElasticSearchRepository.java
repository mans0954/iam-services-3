package org.openiam.elasticsearch.dao;

import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleElasticSearchRepository extends OpeniamElasticSearchRepository<RoleEntity, String>, RoleElasticSearchRepositoryCustom {

}
