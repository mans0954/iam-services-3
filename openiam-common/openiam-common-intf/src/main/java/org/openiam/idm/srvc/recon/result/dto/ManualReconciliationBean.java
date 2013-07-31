package org.openiam.idm.srvc.recon.result.dto;

import java.util.Arrays;
import java.util.List;

import org.openiam.idm.searchbeans.ManualReconciliationSearchBean;

public class ManualReconciliationBean {
    private ReconciliationResultBean reconResultBean;
    private ManualReconciliationSearchBean searchBean;
    private List<ReconciliationResultAction> idmNotTarget = Arrays.asList(
            ReconciliationResultAction.ADD_TO_TARGET,
            ReconciliationResultAction.REMOVE_FROM_IDM);
    private List<ReconciliationResultAction> targetNotIdm = Arrays.asList(
            ReconciliationResultAction.ADD_TO_IDM,
            ReconciliationResultAction.REMOVE_FROM_TARGET);
    private String resourceId;
    private String resourceName;
    private String errorMessage;

    public ReconciliationResultBean getReconResultBean() {
        return reconResultBean;
    }

    public void setReconResultBean(ReconciliationResultBean reconResultBean) {
        this.reconResultBean = reconResultBean;
    }

    public ManualReconciliationSearchBean getSearchBean() {
        return searchBean;
    }

    public void setSearchBean(ManualReconciliationSearchBean searchBean) {
        this.searchBean = searchBean;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public List<ReconciliationResultAction> getIdmNotTarget() {
        return idmNotTarget;
    }

    public void setIdmNotTarget(List<ReconciliationResultAction> idmNotTarget) {
        this.idmNotTarget = idmNotTarget;
    }

    public List<ReconciliationResultAction> getTargetNotIdm() {
        return targetNotIdm;
    }

    public void setTargetNotIdm(List<ReconciliationResultAction> targetNotIdm) {
        this.targetNotIdm = targetNotIdm;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getError() {
        return errorMessage;
    }

    public void setError(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
