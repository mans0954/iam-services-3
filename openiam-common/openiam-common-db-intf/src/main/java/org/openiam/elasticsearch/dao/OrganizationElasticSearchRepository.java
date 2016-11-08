package org.openiam.elasticsearch.dao;

import org.openiam.elasticsearch.model.OrganizationDoc;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * 
 * @author lbornova
 * ElasticSearch Repostiory for Organization
 *
 */
@Repository
public interface OrganizationElasticSearchRepository extends OpeniamElasticSearchRepository<OrganizationDoc, String>, OrganizationElasticSearchRepositoryCustom {

}
