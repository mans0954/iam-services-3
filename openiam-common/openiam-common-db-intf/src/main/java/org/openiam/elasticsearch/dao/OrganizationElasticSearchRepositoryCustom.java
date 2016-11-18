package org.openiam.elasticsearch.dao;

import java.util.List;

import org.openiam.elasticsearch.model.OrganizationDoc;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.springframework.data.domain.Pageable;

public interface OrganizationElasticSearchRepositoryCustom extends AbstractCustomElasticSearchRepository<OrganizationDoc, OrganizationSearchBean, String> {

}
