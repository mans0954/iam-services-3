package org.openiam.elasticsearch.dao;

import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;

public interface OrganizationElasticSearchRepositoryCustom extends AbstractCustomElasticSearchRepository<OrganizationEntity, OrganizationSearchBean, String> {

}
