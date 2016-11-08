package org.openiam.elasticsearch.dao;

import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogElasticSearchRepository extends OpeniamElasticSearchRepository<IdmAuditLogEntity, String>, AuditLogElasticSearchRepositoryCustom  {

	@Override
	public default Class<IdmAuditLogEntity> getDocumentClass() {
		return IdmAuditLogEntity.class;
	}
}
