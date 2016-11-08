package org.openiam.elasticsearch.dao;

import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneElasticSearchRepository extends OpeniamElasticSearchRepository<PhoneEntity, String>, PhoneElasticSearchRepositoryCustom  {

	@Override
	public default Class<PhoneEntity> getDocumentClass() {
		return PhoneEntity.class;
	}
}
