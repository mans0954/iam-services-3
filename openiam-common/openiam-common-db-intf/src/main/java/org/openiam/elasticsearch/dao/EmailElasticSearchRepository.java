package org.openiam.elasticsearch.dao;

import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailElasticSearchRepository extends OpeniamElasticSearchRepository<EmailAddressEntity, String>, EmailElasticSearchRepositoryCustom {

	@Override
	public default Class<EmailAddressEntity> getDocumentClass() {
		return EmailAddressEntity.class;
	}
}
