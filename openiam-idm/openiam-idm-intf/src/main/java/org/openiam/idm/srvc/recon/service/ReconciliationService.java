package org.openiam.idm.srvc.recon.service;

import org.mule.api.MuleContext;
import org.openiam.idm.srvc.recon.dto.*;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultBean;
import org.openiam.idm.srvc.report.dto.ReportTable;

/**
 * Interface for <code>ReconciliationService</code>. All reconciliation
 * activities are managed through this service.
 */
public interface ReconciliationService {

    public ReconciliationConfig addConfig(ReconciliationConfig config);

    public void updateConfig(ReconciliationConfig config);

    public void removeConfigByResourceId(String resourceId);

    public void removeConfig(String configId);

    public ReconciliationConfig getConfigByResource(String resourceId);

    public ReconciliationConfig getConfigById(String configId);

    ReconciliationResponse startReconciliation(ReconciliationConfig config);

    public void setMuleContext(MuleContext ctx);

    public String getReconciliationReport(ReconciliationConfig config,
            String reportType);

    public ReconciliationResultBean getReconciliationResult(
            ReconciliationConfig config);

    String manualReconciliation(ReconciliationResultBean reconciledBean,
            String resourceId) throws Exception;

}
