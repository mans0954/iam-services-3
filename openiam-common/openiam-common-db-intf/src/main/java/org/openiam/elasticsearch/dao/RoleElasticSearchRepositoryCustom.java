package org.openiam.elasticsearch.dao;

import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.role.domain.RoleEntity;

public interface RoleElasticSearchRepositoryCustom extends AbstractCustomElasticSearchRepository<RoleEntity, RoleSearchBean, String> {

}
