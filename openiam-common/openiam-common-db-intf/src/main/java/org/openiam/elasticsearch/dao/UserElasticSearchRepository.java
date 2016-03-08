package org.openiam.elasticsearch.dao;

import org.openiam.idm.srvc.user.domain.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface UserElasticSearchRepository extends OpeniamElasticSearchRepository<UserEntity, String>, UserElasticSearchRepositoryCustom {

	@Override
	public default Class<UserEntity> getEntityClass() {
		return UserEntity.class;
	}
}
