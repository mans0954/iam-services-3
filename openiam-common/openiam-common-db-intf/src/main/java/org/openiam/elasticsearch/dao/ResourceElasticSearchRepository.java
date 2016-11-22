package org.openiam.elasticsearch.dao;

import org.openiam.elasticsearch.model.ResourceDoc;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceElasticSearchRepository extends OpeniamElasticSearchRepository<ResourceDoc, String>, ResourceElasticSearchRepositoryCustom {

}
