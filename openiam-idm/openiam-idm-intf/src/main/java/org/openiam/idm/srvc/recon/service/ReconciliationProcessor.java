package org.openiam.idm.srvc.recon.service;

import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.recon.dto.ReconciliationResponse;

public interface ReconciliationProcessor {
    ReconciliationResponse startReconciliation(ReconciliationConfig config);
}
