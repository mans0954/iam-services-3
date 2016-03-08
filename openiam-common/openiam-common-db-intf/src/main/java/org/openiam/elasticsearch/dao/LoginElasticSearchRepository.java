package org.openiam.elasticsearch.dao;

import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginElasticSearchRepository extends OpeniamElasticSearchRepository<LoginEntity, String>, LoginElasticSearchRepositoryCustom {

	@Override
	public default Class<LoginEntity> getEntityClass() {
		return LoginEntity.class;
	}
}
