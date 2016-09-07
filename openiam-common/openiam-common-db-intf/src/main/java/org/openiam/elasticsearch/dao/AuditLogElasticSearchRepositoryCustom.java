package org.openiam.elasticsearch.dao;

import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogElasticSearchRepositoryCustom extends AbstractCustomElasticSearchRepository<IdmAuditLogEntity, AuditLogSearchBean, String> {

	public Page<IdmAuditLogEntity> find(final AuditLogSearchBean searchBean, final Pageable pageable);
}
