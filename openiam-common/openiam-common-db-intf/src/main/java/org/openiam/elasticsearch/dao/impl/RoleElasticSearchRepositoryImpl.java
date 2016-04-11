package org.openiam.elasticsearch.dao.impl;

import org.openiam.elasticsearch.dao.RoleElasticSearchRepositoryCustom;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;

@Repository
public class RoleElasticSearchRepositoryImpl extends AbstractElasticSearchRepository<RoleEntity, String, RoleSearchBean> implements RoleElasticSearchRepositoryCustom {

	@Override
	protected CriteriaQuery getCriteria(RoleSearchBean searchBean) {
		/*
		CriteriaQuery query = null;
		if(searchBean != null) {
			//TODO:  this is for a custom search, if any
		}
		return query;
		*/
		throw new RuntimeException("Method not yet implemented");
	}

	@Override
	protected Class<RoleEntity> getEntityClass() {
		return RoleEntity.class;
	}

}
