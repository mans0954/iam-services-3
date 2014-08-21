package org.openiam.idm.srvc.recon.service;

import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.recon.dto.ReconciliationResponse;

import java.io.IOException;

public interface ReconciliationProcessor {
    ReconciliationResponse startReconciliation(ReconciliationConfig config, IdmAuditLog idmAuditLog) throws IOException, ScriptEngineException;
}
