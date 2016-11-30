package org.openiam.elasticsearch.dao;

import org.openiam.elasticsearch.model.MetadataTypeDoc;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface MetadataTypeElasticSearchRepository extends OpeniamElasticSearchRepository<MetadataTypeDoc, String>, MetadataTypeElasticSearchRepositoryCustom {

}
