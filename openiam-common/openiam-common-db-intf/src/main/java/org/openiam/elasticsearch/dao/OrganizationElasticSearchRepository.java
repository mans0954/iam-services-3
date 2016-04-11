package org.openiam.elasticsearch.dao;

import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationElasticSearchRepository extends OpeniamElasticSearchRepository<OrganizationEntity, String>, OrganizationElasticSearchRepositoryCustom {

}
