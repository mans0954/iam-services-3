package org.openiam.idm.srvc.recon.service;

import java.io.IOException;

import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.recon.dto.ReconciliationResponse;

public interface ReconciliationProcessor {
    ReconciliationResponse startReconciliation(ReconciliationConfig config, IdmAuditLogEntity idmAuditLog) throws IOException, ScriptEngineException;
}
