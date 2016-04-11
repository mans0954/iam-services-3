package org.openiam.elasticsearch.dao;

import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupElasticSearchRepository extends OpeniamElasticSearchRepository<GroupEntity, String>, GroupElasticSearchRepositoryCustom {

}
