package org.openiam.elasticsearch.dao;

import org.openiam.elasticsearch.model.RoleDoc;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleElasticSearchRepository extends OpeniamElasticSearchRepository<RoleDoc, String>, RoleElasticSearchRepositoryCustom {

}
