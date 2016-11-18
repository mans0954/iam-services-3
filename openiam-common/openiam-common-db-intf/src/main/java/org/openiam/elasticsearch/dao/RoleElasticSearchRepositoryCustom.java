package org.openiam.elasticsearch.dao;

import org.openiam.elasticsearch.model.RoleDoc;
import org.openiam.idm.searchbeans.RoleSearchBean;

public interface RoleElasticSearchRepositoryCustom extends AbstractCustomElasticSearchRepository<RoleDoc, RoleSearchBean, String> {

}
