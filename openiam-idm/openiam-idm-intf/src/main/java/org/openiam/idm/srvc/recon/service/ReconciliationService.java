package org.openiam.idm.srvc.recon.service;

import org.openiam.idm.searchbeans.ManualReconciliationSearchBean;
import org.openiam.idm.searchbeans.ReconConfigSearchBean;
import org.openiam.idm.srvc.recon.dto.*;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultBean;

import java.util.List;

/**
 * Interface for <code>ReconciliationService</code>. All reconciliation
 * activities are managed through this service.
 */
public interface ReconciliationService {

    String getReconciliationReport(ReconciliationConfig config, String reportType);

    ReconciliationResultBean getReconciliationResult(ReconciliationConfig config,
                                                            ManualReconciliationSearchBean searchBean);

    ReconciliationResponse startReconciliation(ReconciliationConfig config);
}
