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
package org.openiam.provision.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.ClientImpl;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.openiam.base.id.UUIDGen;
import org.openiam.connector.ConnectorService;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.*;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.connector.type.response.LookupAttributeResponse;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.openiam.idm.srvc.mngsys.ws.ProvisionConnectorWebService;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Wraps around the connector interface and manages the calls to the varous
 * operations for the connectors for provisioning.
 *
 * @author suneet
 */
@Component
public class ConnectorAdapter {

    protected static final Log log = LogFactory.getLog(ConnectorAdapter.class);

    @Autowired
    @Qualifier("provisionConnectorWebService")
    private ProvisionConnectorWebService connectorService;

    public ObjectResponse addRequest(ManagedSysDto managedSys,
                                     CrudRequest addReqType) {
        ObjectResponse resp = new ObjectResponse();
        resp.setStatus(StatusCodeType.FAILURE);

        try {
            if (managedSys == null) {
                resp.setStatus(StatusCodeType.FAILURE);
                resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
                return resp;
            }
            log.info("ConnectorAdapter:addRequest called. Managed sys ="
                    + managedSys.getId());

            ProvisionConnectorDto connector = connectorService
                    .getProvisionConnector(managedSys.getConnectorId());
            log.info("Connector found for " + connector.getConnectorId());
            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                    .getServiceUrl().length() > 0)) {

                JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
                factory.getInInterceptors().add(new LoggingInInterceptor());
                factory.getOutInterceptors().add(new LoggingOutInterceptor());
                factory.setServiceClass(ConnectorService.class);
                factory.setAddress(connector.getServiceUrl());
                ConnectorService connectorService = (ConnectorService) factory.create();
                ObjectResponse objectResponse = connectorService.add(addReqType);

                return objectResponse;
            }
            return resp;

        } catch (Exception e) {

            resp.setError(ErrorCode.OTHER_ERROR);
            resp.addErrorMessage(e.toString());
            return resp;
        }

    }

    public ObjectResponse modifyRequest(ManagedSysDto managedSys,
                                        CrudRequest modReqType) {
        ObjectResponse resp = new ObjectResponse();
        resp.setStatus(StatusCodeType.FAILURE);

        try {
            if (managedSys == null) {
                resp.setStatus(StatusCodeType.FAILURE);
                resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
                return resp;
            }
            log.debug("ConnectorAdapter:modifyRequest called. Managed sys ="
                    + managedSys.getId());

            ProvisionConnectorDto connector = connectorService
                    .getProvisionConnector(managedSys.getConnectorId());
            log.info("Connector found for " + connector.getConnectorId());
            if ((connector.getServiceUrl() != null && connector
                    .getServiceUrl().length() > 0)) {

                JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
                factory.getInInterceptors().add(new LoggingInInterceptor());
                factory.getOutInterceptors().add(new LoggingOutInterceptor());
                factory.setServiceClass(ConnectorService.class);
                factory.setAddress(connector.getServiceUrl());
                ConnectorService connectorService = (ConnectorService) factory.create();
                resp = connectorService.modify(modReqType);
            }
            return resp;
        } catch (Exception e) {
            log.debug("Exception caught in ConnectorAdaptor:modifyRequest");
            log.error(e);

            resp.setError(ErrorCode.OTHER_ERROR);
            resp.addErrorMessage(e.toString());
            return resp;
        }

    }

    public SearchResponse lookupRequest(ManagedSysDto managedSys,
                                        LookupRequest req) {
        SearchResponse resp = new SearchResponse();
        resp.setStatus(StatusCodeType.FAILURE);

        if (managedSys == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
            return resp;
        }
        log.debug("ConnectorAdapter:lookupRequest called. Managed sys ="
                + managedSys.getId());

        try {
            ProvisionConnectorDto connector = connectorService
                    .getProvisionConnector(managedSys.getConnectorId());
            log.info("Connector found for " + connector.getConnectorId());
            if ((connector.getServiceUrl() != null && connector
                    .getServiceUrl().length() > 0)) {
                JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
                factory.getInInterceptors().add(new LoggingInInterceptor());
                factory.getOutInterceptors().add(new LoggingOutInterceptor());
                factory.setServiceClass(ConnectorService.class);
                factory.setAddress(connector.getServiceUrl());
                ConnectorService connectorService = (ConnectorService) factory.create();
                resp = connectorService.lookup(req);
            }
            return resp;

        } catch (Exception e) {
            log.debug("Exception caught in ConnectorAdaptor:lookupRequest");
            log.error(e);
            log.error(e.getStackTrace());
            resp.setError(ErrorCode.OTHER_ERROR);
            resp.addErrorMessage(e.toString());
            return resp;

        }

    }

    public SearchResponse search(SearchRequest searchRequest, ProvisionConnectorDto connector) {
        SearchResponse resp = new SearchResponse();
        if (searchRequest == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_CONFIGURATION);
            return resp;
        }
        log.debug("ConnectorAdapter:reconcileRequest called. Resource =" + searchRequest.getSearchQuery());
        try {
            if (connector != null && (connector.getServiceUrl() != null && connector.getServiceUrl().length() > 0)) {
                JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
                factory.getInInterceptors().add(new LoggingInInterceptor());
                factory.getOutInterceptors().add(new LoggingOutInterceptor());
                factory.setServiceClass(ConnectorService.class);
                factory.setAddress(connector.getServiceUrl());
                ConnectorService connectorService = (ConnectorService) factory.create();
                resp = connectorService.search(searchRequest);
                if (resp.getStatus() == StatusCodeType.SUCCESS
                        || resp.getObjectList().size() > 0) {
                    if (resp.getErrorMessage().size() > 0) {
                        log.debug("Connector Search: error message = "
                                + resp.getErrorMsgAsStr());
                    }
                    log.debug("Connector Search:"
                            + StatusCodeType.SUCCESS);
                    resp.setStatus(StatusCodeType.SUCCESS);
                    return resp;
                }
            }

            resp.setStatus(StatusCodeType.FAILURE);
            return resp;


        } catch (Exception e) {
            log.error(e);
        }
        resp.setStatus(StatusCodeType.FAILURE);
        return resp;
    }

    public ResponseType reconcileResource(ManagedSysDto managedSys,
                                          ReconciliationConfig config) {
        ResponseType type = new ResponseType();
        type.setStatus(StatusCodeType.FAILURE);

        if (config == null) {
            return type;
        }

        log.debug("ConnectorAdapter:reconcile called. Resource ="
                + config.getResourceId());

        try {
            ProvisionConnectorDto connector = connectorService
                    .getProvisionConnector(managedSys.getConnectorId());

            log.debug("Connector found for " + connector.getConnectorId());

            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                    .getServiceUrl().length() > 0)) {
                JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
                factory.getInInterceptors().add(new LoggingInInterceptor());
                factory.getOutInterceptors().add(new LoggingOutInterceptor());
                factory.setServiceClass(ConnectorService.class);
                factory.setAddress(connector.getServiceUrl());
                ConnectorService connectorService = (ConnectorService) factory.create();
                ResponseType resp = connectorService.reconcileResource(config);
                return resp;
            }
            return type;
        } catch (Exception e) {
            log.error(e);

            type.setError(ErrorCode.OTHER_ERROR);
            type.addErrorMessage(e.toString());
            return type;

        }
    }

    public LookupAttributeResponse lookupAttributes(String connectorId,
                                                    LookupRequest config) {

        LookupAttributeResponse type = new LookupAttributeResponse();
        type.setStatus(StatusCodeType.FAILURE);

        if (config == null) {
            return type;
        }

        log.debug("ConnectorAdapter:lookupattributes called");

        try {
            ProvisionConnectorDto connector = connectorService
                    .getProvisionConnector(connectorId);

            log.debug("Connector found for " + connector.getConnectorId());

            if ((connector.getServiceUrl() != null && connector
                    .getServiceUrl().length() > 0)) {
                JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
                factory.getInInterceptors().add(new LoggingInInterceptor());
                factory.getOutInterceptors().add(new LoggingOutInterceptor());
                factory.setServiceClass(ConnectorService.class);
                factory.setAddress(connector.getServiceUrl());
                ConnectorService connectorService = (ConnectorService) factory.create();
                LookupAttributeResponse resp = connectorService.lookupAttributeNames(config);
                return resp;
            }
            return type;
        } catch (Exception e) {
            log.error(e);

            type.setError(ErrorCode.OTHER_ERROR);
            type.addErrorMessage(e.toString());
            return type;

        }
    }

    public ObjectResponse deleteRequest(ManagedSysDto managedSys,
                                        CrudRequest delReqType) {
        ObjectResponse resp = new ObjectResponse();
        resp.setStatus(StatusCodeType.FAILURE);

        if (managedSys == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
            return resp;
        }
        log.info("ConnectorAdapter:deleteRequest called. Managed sys ="
                + managedSys.getId());

        try {

            ProvisionConnectorDto connector = connectorService
                    .getProvisionConnector(managedSys.getConnectorId());
            log.info("Connector found for " + connector.getConnectorId());
            if ((connector.getServiceUrl() != null && connector
                    .getServiceUrl().length() > 0)) {
                JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
                factory.getInInterceptors().add(new LoggingInInterceptor());
                factory.getOutInterceptors().add(new LoggingOutInterceptor());
                factory.setServiceClass(ConnectorService.class);
                factory.setAddress(connector.getServiceUrl());
                ConnectorService connectorService = (ConnectorService) factory.create();
                resp = connectorService.delete(delReqType);
                return resp;
            }
            return resp;
        } catch (Exception e) {
            log.error(e);

            resp.setError(ErrorCode.OTHER_ERROR);
            resp.addErrorMessage(e.toString());
            return resp;

        }

    }

    public ResponseType setPasswordRequest(ManagedSysDto managedSys,
                                           PasswordRequest request) {
        ResponseType resp = new ResponseType();
        resp.setStatus(StatusCodeType.FAILURE);

        if (managedSys == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
            return resp;
        }
        log.info("ConnectorAdapter:setPasswordRequest called. Managed sys ="
                + managedSys.getId());
        try {

            ProvisionConnectorDto connector = connectorService
                    .getProvisionConnector(managedSys.getConnectorId());
            log.info("Connector found for " + connector.getConnectorId());
            if ((connector.getServiceUrl() != null && connector
                    .getServiceUrl().length() > 0)) {
                JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
                factory.getInInterceptors().add(new LoggingInInterceptor());
                factory.getOutInterceptors().add(new LoggingOutInterceptor());
                factory.setServiceClass(ConnectorService.class);
                factory.setAddress(connector.getServiceUrl());
                ConnectorService connectorService = (ConnectorService) factory.create();
                resp = connectorService.setPassword(request);
                return resp;
            }
            return resp;
        } catch (Exception e) {
            log.error(e);

            resp.setError(ErrorCode.OTHER_ERROR);
            resp.addErrorMessage(e.toString());
            return resp;

        }

    }

    public ResponseType resetPasswordRequest(
            ManagedSysDto managedSys,
            PasswordRequest request) {

        ResponseType resp = new ResponseType();
        resp.setStatus(StatusCodeType.FAILURE);

        if (managedSys == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
            return resp;
        }
        log.debug("ConnectorAdapter:resetPasswordRequest called. Managed sys ="
                + managedSys.getId());

        try {
            ProvisionConnectorDto connector = connectorService
                    .getProvisionConnector(managedSys.getConnectorId());
            log.debug("Connector found for " + connector.getConnectorId());
            if ((connector.getServiceUrl() != null && connector
                    .getServiceUrl().length() > 0)) {
                JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
                factory.getInInterceptors().add(new LoggingInInterceptor());
                factory.getOutInterceptors().add(new LoggingOutInterceptor());
                factory.setServiceClass(ConnectorService.class);
                factory.setAddress(connector.getServiceUrl());
                ConnectorService connectorService = (ConnectorService) factory.create();
                resp = connectorService.resetPassword(request);
                return resp;
            }
            return resp;
        } catch (Exception e) {
            log.error(e);

            resp.setError(ErrorCode.OTHER_ERROR);
            resp.addErrorMessage(e.toString());
            return resp;

        }

    }

    public ResponseType suspendRequest(ManagedSysDto managedSys,
                                       SuspendResumeRequest request) {

        ResponseType resp = new ResponseType();
        resp.setStatus(StatusCodeType.FAILURE);

        if (managedSys == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
            return resp;
        }
        log.debug("ConnectorAdapter:suspendRequest called. Managed sys ="
                + managedSys.getId());

        try {
            ProvisionConnectorDto connector = connectorService
                    .getProvisionConnector(managedSys.getConnectorId());
            log.debug("Connector found for " + connector.getConnectorId());

            if ((connector.getServiceUrl() != null && connector
                    .getServiceUrl().length() > 0)) {
                JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
                factory.getInInterceptors().add(new LoggingInInterceptor());
                factory.getOutInterceptors().add(new LoggingOutInterceptor());
                factory.setServiceClass(ConnectorService.class);
                factory.setAddress(connector.getServiceUrl());
                ConnectorService connectorService = (ConnectorService) factory.create();
                resp = connectorService.suspend(request);
                return resp;
            }
            return resp;

        } catch (Exception e) {
            log.error(e);

            resp.setError(ErrorCode.OTHER_ERROR);
            resp.addErrorMessage(e.toString());
            return resp;

        }

    }

    public ResponseType resumeRequest(ManagedSysDto managedSys,
                                      SuspendResumeRequest request) {

        ResponseType type = new ResponseType();
        type.setStatus(StatusCodeType.FAILURE);

        if (managedSys == null) {
            return type;
        }
        log.debug("ConnectorAdapter:resumeRequest called. Managed sys ="
                + managedSys.getId());
        try {
            ProvisionConnectorDto connector = connectorService
                    .getProvisionConnector(managedSys.getConnectorId());
            log.debug("Connector found for " + connector.getConnectorId());

            if ((connector.getServiceUrl() != null && connector
                    .getServiceUrl().length() > 0)) {
                JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
                factory.getInInterceptors().add(new LoggingInInterceptor());
                factory.getOutInterceptors().add(new LoggingOutInterceptor());
                factory.setServiceClass(ConnectorService.class);
                factory.setAddress(connector.getServiceUrl());
                ConnectorService connectorService = (ConnectorService) factory.create();
                type = connectorService.resume(request);
                return type;
            }
            return type;
        } catch (Exception e) {
            log.error(e);

            type.setError(ErrorCode.OTHER_ERROR);
            type.addErrorMessage(e.toString());
            return type;

        }

    }

    public ResponseType validatePassword(
            ManagedSysDto managedSys,
            PasswordRequest request) {
        ResponseType resp = new ResponseType();
        resp.setStatus(StatusCodeType.FAILURE);

        if (managedSys == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
            return resp;
        }
        if(log.isDebugEnabled()) {
            log.debug("ConnectorAdapter:testCredentials called. Managed sys ="
                    + managedSys.getId());
        }
        try {
            ProvisionConnectorDto connector = connectorService
                    .getProvisionConnector(managedSys.getConnectorId());
            if(log.isDebugEnabled()) {
                log.debug("Connector found for " + connector.getConnectorId());
            }
            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                    .getServiceUrl().length() > 0)) {

                JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
                factory.getInInterceptors().add(new LoggingInInterceptor());
                factory.getOutInterceptors().add(new LoggingOutInterceptor());
                factory.setServiceClass(ConnectorService.class);
                factory.setAddress(connector.getServiceUrl());
                ConnectorService connectorService = (ConnectorService) factory.create();
                resp = connectorService.validatePassword(request);
                return resp;

            }
            return resp;
        } catch (Exception e) {
            if(log.isDebugEnabled()) {
                log.debug("Exception caught in ConnectorAdapter:validatePassword"); //SIA 2015-08-01
            }
            log.error(e);
            log.error(e.getStackTrace()); //SIA 2015-08-01
            resp.setError(ErrorCode.OTHER_ERROR);
            resp.addErrorMessage(e.toString());
            return resp;

        }
    }

    public ResponseType testConnection(ManagedSysDto managedSys) {

        ResponseType type = new ResponseType();
        type.setStatus(StatusCodeType.FAILURE);

        if (managedSys == null) {
            return type;
        }

        log.debug("ConnectorAdapter:testConnection called. Managed sys ="
                + managedSys.getId());

        try {
            ProvisionConnectorDto connector = connectorService
                    .getProvisionConnector(managedSys.getConnectorId());

            log.debug("Connector found for " + connector.getConnectorId());

            if ((connector.getServiceUrl() != null && connector
                    .getServiceUrl().length() > 0)) {
                RequestType<ExtensibleUser> rt = new RequestType<ExtensibleUser>();
                rt.setTargetID(managedSys.getId());
                rt.setScriptHandler(managedSys.getTestConnectionHandler());
                rt.setHostPort((managedSys.getPort() != null) ? managedSys.getPort().toString() : null);
                rt.setHostUrl(managedSys.getHostUrl());
                rt.setHostLoginId(managedSys.getUserId());
                rt.setHostLoginPassword(managedSys.getDecryptPassword());

                JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
                factory.getInInterceptors().add(new LoggingInInterceptor());
                factory.getOutInterceptors().add(new LoggingOutInterceptor());
                factory.setServiceClass(ConnectorService.class);
                factory.setAddress(connector.getServiceUrl());
                ConnectorService connectorService = (ConnectorService) factory.create();
                type = connectorService.testConnection(rt);
                return type;
            }
            return type;
        } catch (Exception e) {
            log.error("Can't test connection", e);

            type.setError(ErrorCode.OTHER_ERROR);
            type.addErrorMessage(e.toString());
            return type;

        }

    }

}