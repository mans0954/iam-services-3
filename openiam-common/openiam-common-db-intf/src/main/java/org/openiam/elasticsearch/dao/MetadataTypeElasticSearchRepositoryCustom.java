package org.openiam.elasticsearch.dao;

import org.openiam.elasticsearch.model.MetadataTypeDoc;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;

public interface MetadataTypeElasticSearchRepositoryCustom extends AbstractCustomElasticSearchRepository<MetadataTypeDoc, MetadataTypeSearchBean, String> {

}
