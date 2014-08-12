package org.openiam.idm.srvc.recon.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.openiam.base.ws.Response;

import org.openiam.idm.searchbeans.ReconConfigSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.recon.dto.ReconciliationResponse;

/**
 * Interface for <code>ReconciliationWebService</code>. Service is responsible
 * for activities related to reconcilation persisted through this service.
 */
@WebService(targetNamespace = "http://www.openiam.org/service/recon", name = "ReconciliationWebService")
public interface ReconciliationWebService {

    @WebMethod
    ReconciliationConfigResponse addConfig(
            @WebParam(name = "config", targetNamespace = "") ReconciliationConfig config);

    @WebMethod
    ReconciliationConfigResponse updateConfig(
            @WebParam(name = "config", targetNamespace = "") ReconciliationConfig config);

    @WebMethod
    ReconciliationConfigResponse getConfigById(
            @WebParam(name = "configId", targetNamespace = "") String configId);

    @WebMethod
    ReconciliationConfigResponse getConfigByResourceUserType(
            @WebParam(name = "resourceId", targetNamespace = "") String resourceId);

    @WebMethod
    ReconciliationConfigResponse findReconConfig(final ReconConfigSearchBean searchBean, final int from, final int size, final Language language);

    @WebMethod
    int countReconConfig(@WebParam(name = "searchBean", targetNamespace = "") ReconConfigSearchBean searchBean);

    @WebMethod
    ReconciliationConfigResponse getConfigsByResourceId(
            @WebParam(name = "resourceId", targetNamespace = "") String resourceId);

    @WebMethod
    Response removeConfig(
            @WebParam(name = "configId", targetNamespace = "") String configId);

    @WebMethod
    ReconciliationResponse startReconciliation(
            @WebParam(name = "config", targetNamespace = "") ReconciliationConfig config);

}