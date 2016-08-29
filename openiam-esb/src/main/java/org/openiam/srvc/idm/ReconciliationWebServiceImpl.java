/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 * 
 */
package org.openiam.srvc.idm;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.searchbeans.ReconConfigSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.recon.dto.ReconciliationResponse;
import org.openiam.idm.srvc.recon.service.ReconciliationConfigService;
import org.openiam.idm.srvc.recon.service.ReconciliationService;
import org.openiam.base.response.ReconciliationConfigResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author suneet
 * 
 */
@WebService(endpointInterface = "org.openiam.srvc.idm.ReconciliationWebService", targetNamespace = "http://www.openiam.org/service/recon", portName = "ReconciliationWebServicePort", serviceName = "ReconciliationWebService")
@Component("reconServiceWS")
public class ReconciliationWebServiceImpl implements ReconciliationWebService {

	@Autowired
    protected ReconciliationService reconService;

	@Autowired
	protected ReconciliationConfigService reconConfigService;

    public ReconciliationConfigResponse addConfig(ReconciliationConfig config) {
        ReconciliationConfigResponse response = new ReconciliationConfigResponse(
                ResponseStatus.SUCCESS);
        ReconciliationConfig cfg = reconConfigService.addConfig(config);
        if (cfg == null || cfg.getId() == null) {
            response.setStatus(ResponseStatus.FAILURE);
        } else {
            response.setConfig(cfg);
        }
        return response;

    }

    public ReconciliationConfigResponse updateConfig(ReconciliationConfig config) {
        ReconciliationConfigResponse response = new ReconciliationConfigResponse(
                ResponseStatus.SUCCESS);

        try {
			reconConfigService.updateConfig(config);
        } catch (Exception e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;

    }

    public ReconciliationConfigResponse getConfigByResourceUserType(final String resourceId) {
        ReconciliationConfigResponse response = new ReconciliationConfigResponse(
                ResponseStatus.SUCCESS);
        ReconciliationConfig cfg = reconConfigService.getConfigByResourceByType(resourceId, "USER");
        if (cfg != null) {
            response.setConfig(cfg);
        }
        return response;

    }

    @Override
    public ReconciliationConfigResponse findReconConfig(ReconConfigSearchBean searchBean, int from, int size, Language language) {
        List<ReconciliationConfig> cfgList = reconConfigService.findReconConfig(searchBean, from, size);
        ReconciliationConfigResponse response = new ReconciliationConfigResponse(ResponseStatus.SUCCESS);
        response.setConfigList(cfgList);
        return response;
    }

    @Override
    public int countReconConfig(@WebParam(name = "searchBean", targetNamespace = "") ReconConfigSearchBean searchBean) {
        return reconConfigService.countReconConfig(searchBean);
    }

    @Override
    public ReconciliationConfigResponse getConfigsByResourceId(@WebParam(name = "resourceId", targetNamespace = "") String resourceId) {
        ReconciliationConfigResponse response = new ReconciliationConfigResponse(
                ResponseStatus.SUCCESS);
        List<ReconciliationConfig> cfgList = reconConfigService.getConfigsByResource(resourceId);
        response.setConfigList(cfgList);
        return response;
    }

    @Override
    public Response removeConfig(String configId, String requesterId) {
        Response response = new Response(ResponseStatus.SUCCESS);
		reconConfigService.removeConfig(configId);
        return response;
    }

    public ReconciliationService getReconService() {
        return reconService;
    }

    public void setReconService(ReconciliationService reconService) {
        this.reconService = reconService;
    }

    public ReconciliationResponse startReconciliation(
            ReconciliationConfig config) {
        return reconService.startReconciliation(config);

    }

    public ReconciliationConfigResponse getConfigById(String configId) {
        ReconciliationConfigResponse response = new ReconciliationConfigResponse(
                ResponseStatus.SUCCESS);
        ReconciliationConfig cfg = reconConfigService.getConfigById(configId);
        if (cfg == null || cfg.getId() == null) {
            response.setStatus(ResponseStatus.FAILURE);
        } else {
            response.setConfig(cfg);
        }
        return response;

    }

}
