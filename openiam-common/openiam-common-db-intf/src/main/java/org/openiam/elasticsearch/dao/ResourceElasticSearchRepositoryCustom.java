package org.openiam.elasticsearch.dao;

import org.openiam.elasticsearch.model.ResourceDoc;
import org.openiam.idm.searchbeans.ResourceSearchBean;

public interface ResourceElasticSearchRepositoryCustom extends AbstractCustomElasticSearchRepository<ResourceDoc, ResourceSearchBean, String> {

}
