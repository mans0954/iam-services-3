package org.openiam.elasticsearch.dao.impl;

import org.openiam.elasticsearch.dao.OrganizationElasticSearchRepositoryCustom;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;

@Repository
public class OrganizationElasticSearchRepositoryImpl extends AbstractElasticSearchRepository<OrganizationEntity, String, OrganizationSearchBean> implements OrganizationElasticSearchRepositoryCustom {

	@Override
	protected CriteriaQuery getCriteria(OrganizationSearchBean searchBean) {
		throw new RuntimeException("Method not yet implemented");
	}

	@Override
	protected Class<OrganizationEntity> getEntityClass() {
		return OrganizationEntity.class;
	}

}
