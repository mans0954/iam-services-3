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

    public ReconciliationConfig addConfig(ReconciliationConfig config);

    public void updateConfig(ReconciliationConfig config);

    public void removeConfig(String configId);

    public ReconciliationConfig getConfigByResourceByType(final String resourceId, final String type);

    public ReconciliationConfig getConfigById(String configId);

    public List<ReconciliationConfig> getConfigsByResource(String resourceId);

    List<ReconciliationConfig> findReconConfig(ReconConfigSearchBean searchBean, int from, int size);

    int countReconConfig(final ReconConfigSearchBean searchBean);

    String getReconciliationReport(ReconciliationConfig config, String reportType);

    ReconciliationResultBean getReconciliationResult(ReconciliationConfig config,
                                                            ManualReconciliationSearchBean searchBean);
}
