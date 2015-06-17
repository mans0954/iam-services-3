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
public interface ReconciliationConfigService {

    ReconciliationConfig addConfig(ReconciliationConfig config);

    void updateConfig(ReconciliationConfig config);

	ReconExecStatusOptions getExecStatus(String configId);

	void updateExecStatus(String configId, ReconExecStatusOptions status);

	void removeConfig(String configId);

    ReconciliationConfig getConfigByResourceByType(final String resourceId, final String type);

    ReconciliationConfig getConfigById(String configId);

    List<ReconciliationConfig> getConfigsByResource(String resourceId);

    List<ReconciliationConfig> findReconConfig(ReconConfigSearchBean searchBean, int from, int size);

    int countReconConfig(final ReconConfigSearchBean searchBean);

    void clearSession();
}
