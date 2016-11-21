package org.openiam.elasticsearch.dao;

import org.openiam.elasticsearch.model.GroupDoc;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupElasticSearchRepository extends OpeniamElasticSearchRepository<GroupDoc, String>, GroupElasticSearchRepositoryCustom {

}
