package org.openiam.elasticsearch.dao;

import java.util.List;

import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginElasticSearchRepository extends OpeniamElasticSearchRepository<LoginEntity, String>, LoginElasticSearchRepositoryCustom {

	@Override
	public default Class<LoginEntity> getEntityClass() {
		return LoginEntity.class;
	}
	
	public List<LoginEntity> findByUserId(String userId);
}
