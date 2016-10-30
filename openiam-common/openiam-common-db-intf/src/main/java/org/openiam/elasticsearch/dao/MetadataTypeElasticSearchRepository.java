package org.openiam.elasticsearch.dao;

import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface MetadataTypeElasticSearchRepository extends OpeniamElasticSearchRepository<MetadataTypeEntity, String>, MetadataTypeElasticSearchRepositoryCustom {

}
