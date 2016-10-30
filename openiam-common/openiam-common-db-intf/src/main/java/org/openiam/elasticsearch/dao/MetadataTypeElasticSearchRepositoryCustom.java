package org.openiam.elasticsearch.dao;

import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;

public interface MetadataTypeElasticSearchRepositoryCustom extends AbstractCustomElasticSearchRepository<MetadataTypeEntity, MetadataTypeSearchBean, String> {

}
