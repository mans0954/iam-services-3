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
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
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
import org.openiam.connector.ConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import java.util.HashMap;
import java.util.Map;

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
    private ProvisionConnectorWebService connectorService;

    public ObjectResponse addRequest(ManagedSysDto managedSys,
                                      CrudRequest addReqType,
                                      MuleContext muleContext) {
        ObjectResponse respType = new ObjectResponse();
        respType.setStatus(StatusCodeType.FAILURE);

        try {
            if (managedSys == null) {
                return respType;
            }
            log.info("ConnectorAdapter:addRequest called. Managed sys ="
                    + managedSys.getManagedSysId());

            ProvisionConnectorDto connector = connectorService
                    .getProvisionConnector(managedSys.getConnectorId());
            log.info("Connector found for " + connector.getConnectorId());
            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                            .getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, addReqType,
                        connector.getServiceUrl(), "add", muleContext);

                if (msg.getPayload() instanceof org.mule.transport.NullPayload) {

                    log.debug("MuleMessage is null..");
                    return respType;

                } else {
                    return (ObjectResponse) msg.getPayload();

                }

            }
            return respType;

        } catch (Exception e) {
            // log.error(e);
            respType.setError(ErrorCode.OTHER_ERROR);
            respType.addErrorMessage(e.toString());
            return respType;
        }

    }

    public ObjectResponse modifyRequest(ManagedSysDto managedSys,
                                      CrudRequest modReqType,
                                      MuleContext muleContext) {
        ObjectResponse respType = new ObjectResponse();
        respType.setStatus(StatusCodeType.FAILURE);

        try {
            if (managedSys == null) {
                return respType;
            }
            log.debug("ConnectorAdapter:modifyRequest called. Managed sys ="
                    + managedSys.getManagedSysId());

            ProvisionConnectorDto connector = connectorService
                    .getProvisionConnector(managedSys.getConnectorId());
            log.info("Connector found for " + connector.getConnectorId());
            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                            .getServiceUrl().length() > 0)) {

                // ConnectorService port = getService(connector);
                // port.modify(modReqType);
                MuleMessage msg = getService(connector, modReqType,
                        connector.getServiceUrl(), "modify", muleContext);

                if (msg.getPayload() instanceof org.mule.transport.NullPayload) {

                    log.debug("MuleMessage is null..");
                    return respType;

                } else {
                    return (ObjectResponse) msg.getPayload();

                }

            }
            return respType;
        } catch (Exception e) {
            log.debug("Exception caught in ConnectorAdaptor:modifyRequest");
            log.error(e);

            respType.setError(ErrorCode.OTHER_ERROR);
            respType.addErrorMessage(e.toString());
            return respType;
        }

    }

    public SearchResponse lookupRequest(ManagedSysDto managedSys,
            LookupRequest req,
                                        MuleContext muleContext) {
        SearchResponse resp = new SearchResponse();
        resp.setStatus(StatusCodeType.FAILURE);

        if (managedSys == null) {
            return null;
        }
        log.debug("ConnectorAdapter:lookupRequest called. Managed sys ="
                + managedSys.getManagedSysId());

        try {
            ProvisionConnectorDto connector = connectorService
                    .getProvisionConnector(managedSys.getConnectorId());
            log.info("Connector found for " + connector.getConnectorId());
            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                            .getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, req,
                        connector.getServiceUrl(), "lookup", muleContext);
                if (msg != null) {
                    log.debug("LOOKUP Payload=" + msg.getPayload());
                    if (msg.getPayload() != null
                            && msg.getPayload() instanceof SearchResponse) {

                        return (SearchResponse) msg.getPayload();
                    } else {
                        log.debug("LOOKUP payload is not an instance of LookupResponseType");
                        return resp;
                    }
                } else {
                    log.debug("MuleMessage is null..");
                }

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

    public SearchResponse search(SearchRequest searchRequest, ProvisionConnectorDto connector, MuleContext muleContext) {
        SearchResponse resp = new SearchResponse();
        if (searchRequest == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_CONFIGURATION);
            return resp;
        }
        log.debug("ConnectorAdapter:reconcileRequest called. Resource =" + searchRequest.getSearchQuery());
        try {
            if (connector != null && (connector.getServiceUrl() != null && connector.getServiceUrl().length() > 0)) {
                //Send search to Local Connector to get data (e.g. Active Directory via LDAP)
                MuleMessage msg = getService(connector, searchRequest, connector.getServiceUrl(), "search", muleContext);
                if (msg != null) {
                    log.debug("Test connection Payload=" + msg.getPayload());
                    if (msg.getPayload() != null && msg.getPayload() instanceof ResponseType) {
                        resp = (SearchResponse) msg.getPayload();
                        if(resp.getStatus() == StatusCodeType.SUCCESS) {
                            resp.setStatus(StatusCodeType.SUCCESS);
                            return resp;
                        }
                    }
                    resp.setStatus(StatusCodeType.FAILURE);
                    return resp;
                } else {
                    log.debug("MuleMessage is null..");
                }

            }
        } catch (Exception e) {
            log.error(e);
        }
        resp.setStatus(StatusCodeType.FAILURE);
        return resp;
	}

    public ResponseType reconcileResource(ManagedSysDto managedSys,
            ReconciliationConfig config, MuleContext muleContext) {
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

                MuleMessage msg = getService(connector, config,
                        connector.getServiceUrl(), "reconcile", muleContext);

                log.debug("MuleMessage payload=" + msg);

                if (msg != null) {
                    return (ResponseType) msg.getPayload();
                } else {
                    return type;
                }

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
                                                    LookupRequest config, MuleContext muleContext) {

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

            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                            .getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, config,
                        connector.getServiceUrl(), "lookupAttributes",
                        muleContext);

                log.debug("MuleMessage payload=" + msg);

                if (msg != null) {
                    return (LookupAttributeResponse) msg.getPayload();
                } else {
                    return type;
                }

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
                                      CrudRequest delReqType,
                                      MuleContext muleContext) {
        ObjectResponse type = new ObjectResponse();
        type.setStatus(StatusCodeType.FAILURE);

        if (managedSys == null) {
            return type;
        }
        log.info("ConnectorAdapter:deleteRequest called. Managed sys ="
                + managedSys.getManagedSysId());

        try {

            ProvisionConnectorDto connector = connectorService
                    .getProvisionConnector(managedSys.getConnectorId());
            log.info("Connector found for " + connector.getConnectorId());
            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                            .getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, delReqType,
                        connector.getServiceUrl(), "delete", muleContext);
                if (msg != null) {
                    return (ObjectResponse) msg.getPayload();
                } else {
                    log.debug("MuleMessage is null..");
                    return type;

                }

            }
            return type;
        } catch (Exception e) {
            log.error(e);

            type.setError(ErrorCode.OTHER_ERROR);
            type.addErrorMessage(e.toString());
            return type;

        }

    }

    public ResponseType setPasswordRequest(ManagedSysDto managedSys,
                                           PasswordRequest request,
                                           MuleContext muleContext) {
        ResponseType type = new ResponseType();
        type.setStatus(StatusCodeType.FAILURE);

        if (managedSys == null) {
            return type;
        }
        log.info("ConnectorAdapter:setPasswordRequest called. Managed sys ="
                + managedSys.getManagedSysId());
        try {

            ProvisionConnectorDto connector = connectorService
                    .getProvisionConnector(managedSys.getConnectorId());
            log.info("Connector found for " + connector.getConnectorId());
            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                            .getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, request,
                        connector.getServiceUrl(), "setPassword", muleContext);

                if (msg.getPayload() instanceof org.mule.transport.NullPayload) {
                    // payload is null - exception was thrown
                    log.debug("Exception payload as a string: "
                            + msg.getExceptionPayload().toString());
                    return type;
                } else {
                    // valid payload

                    log.debug("Message payload found on password change: "
                            + msg.getPayload());

                    return (ResponseType) msg.getPayload();

                }

            }
            return type;
        } catch (Exception e) {
            log.error(e);

            type.setError(ErrorCode.OTHER_ERROR);
            type.addErrorMessage(e.toString());
            return type;

        }

    }

    public ResponseType resetPasswordRequest(
            ManagedSysDto managedSys,
            PasswordRequest request,
            MuleContext muleContext) {

        ResponseType type = new ResponseType();
        type.setStatus(StatusCodeType.FAILURE);

        if (managedSys == null) {
            return type;
        }
        log.debug("ConnectorAdapter:resetPasswordRequest called. Managed sys ="
                + managedSys.getManagedSysId());

        try {
            ProvisionConnectorDto connector = connectorService
                    .getProvisionConnector(managedSys.getConnectorId());
            log.debug("Connector found for " + connector.getConnectorId());
            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                            .getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, request,
                        connector.getServiceUrl(), "resetPassword", muleContext);

                if (msg.getPayload() instanceof org.mule.transport.NullPayload) {
                    // payload is null - exception was thrown
                    log.debug("Exception payload as a string: "
                            + msg.getExceptionPayload().toString());
                    return type;
                } else {
                    // valid payload

                    log.debug("Message payload found on password reset: "
                            + msg.getPayload());

                    return (ResponseType) msg.getPayload();
                }

            }
            return type;
        } catch (Exception e) {
            log.error(e);

            type.setError(ErrorCode.OTHER_ERROR);
            type.addErrorMessage(e.toString());
            return type;

        }

    }

    public ResponseType suspendRequest(ManagedSysDto managedSys,
                                       SuspendResumeRequest request,
                                       MuleContext muleContext) {

        ResponseType type = new ResponseType();
        type.setStatus(StatusCodeType.FAILURE);

        if (managedSys == null) {
            return type;
        }
        log.debug("ConnectorAdapter:suspendRequest called. Managed sys ="
                + managedSys.getManagedSysId());

        try {
            ProvisionConnectorDto connector = connectorService
                    .getProvisionConnector(managedSys.getConnectorId());
            log.debug("Connector found for " + connector.getConnectorId());

            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                            .getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, request,
                        connector.getServiceUrl(), "suspend", muleContext);
                if (msg != null) {
                    return (ResponseType) msg.getPayload();
                } else {
                    return type;
                }

            }
            return type;

        } catch (Exception e) {
            log.error(e);

            type.setError(ErrorCode.OTHER_ERROR);
            type.addErrorMessage(e.toString());
            return type;

        }

    }

    public ResponseType resumeRequest(ManagedSysDto managedSys,
                                      SuspendResumeRequest request,
                                      MuleContext muleContext) {

        ResponseType type = new ResponseType();
        type.setStatus(StatusCodeType.FAILURE);

        if (managedSys == null) {
            return type;
        }
        log.debug("ConnectorAdapter:resumeRequest called. Managed sys ="
                + managedSys.getManagedSysId());
        try {
            ProvisionConnectorDto connector = connectorService
                    .getProvisionConnector(managedSys.getConnectorId());
            log.debug("Connector found for " + connector.getConnectorId());

            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                            .getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, request,
                        connector.getServiceUrl(), "resume", muleContext);
                if (msg != null) {
                    return (ResponseType) msg.getPayload();
                } else {
                    return type;
                }

            }
            return type;
        } catch (Exception e) {
            log.error(e);

            type.setError(ErrorCode.OTHER_ERROR);
            type.addErrorMessage(e.toString());
            return type;

        }

    }

    public ResponseType testConnection(ManagedSysDto managedSys,
            MuleContext muleContext) {

        ResponseType type = new ResponseType();
        type.setStatus(StatusCodeType.FAILURE);

        if (managedSys == null) {
            return type;
        }

        log.debug("ConnectorAdapter:testConnection called. Managed sys ="
                + managedSys.getManagedSysId());

        try {
            ProvisionConnectorDto connector = connectorService
                    .getProvisionConnector(managedSys.getConnectorId());

            log.debug("Connector found for " + connector.getConnectorId());

            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                            .getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, managedSys,
                        connector.getServiceUrl(), "testConnection",
                        muleContext);

                log.debug("MuleMessage payload=" + msg);

                if (msg != null) {
                    return (ResponseType) msg.getPayload();
                } else {
                    return type;
                }

            }
            return type;
        } catch (Exception e) {
            log.error("Can't test connection", e);

            type.setError(ErrorCode.OTHER_ERROR);
            type.addErrorMessage(e.toString());
            return type;

        }

    }

    private MuleMessage getService(ProvisionConnectorDto connector,
            Object reqType, String url, String operation,
            MuleContext muleContext) throws MuleException {

        log.debug("getService: calling DynamicEndpoint...");
        // Create a MuleContextFactory

        MuleClient client = new MuleClient(muleContext);

        // Map<?,?> msgPropMap =
        // Collections.singletonMap("serviceName","LDAPConnectorService");
        Map<String, String> msgPropMap = new HashMap<String, String>();
        msgPropMap.put("serviceName", url);

        MuleMessage msg = null;

        log.debug("- Service:: Connector interface- Calling dynamic interface ="
                + url);
        log.debug("- Service:: Operation=" + operation);

        if (operation.equalsIgnoreCase("add")) {

            msg = client.send("vm://dispatchConnectorMessageAdd",
                    (CrudRequest) reqType, msgPropMap);
        }
        if (operation.equalsIgnoreCase("modify")) {

            msg = client.send("vm://dispatchConnectorMessageModify",
                    (CrudRequest) reqType, msgPropMap);
        }
        if (operation.equalsIgnoreCase("lookup")) {

            msg = client.send("vm://dispatchConnectorMessageLookup",
                    (SearchRequest) reqType, msgPropMap);
        }
        if (operation.equalsIgnoreCase("reconcile")) {

            client.sendAsync("vm://dispatchConnectorMessageReconcile",
                    (ReconciliationConfig) reqType, msgPropMap);
        }
        if (operation.equalsIgnoreCase("delete")) {

            msg = client.send("vm://dispatchConnectorMessageDelete",
                    (CrudRequest) reqType, msgPropMap);
        }
        if (operation.equalsIgnoreCase("setPassword")) {

            msg = client.send("vm://dispatchConnectorMessageSetPassword",
                    (PasswordRequest) reqType, msgPropMap);
        }

        if (operation.equalsIgnoreCase("resetPassword")) {

            msg = client.send("vm://dispatchConnectorMessageResetPassword",
                    (PasswordRequest) reqType, msgPropMap);

        }
        if (operation.equalsIgnoreCase("suspend")) {

            msg = client.send("vm://dispatchConnectorMessageSuspend",
                    (PasswordRequest) reqType, msgPropMap);
        }
        if (operation.equalsIgnoreCase("resume")) {
            msg = client.send("vm://dispatchConnectorMessageResume",
                    (PasswordRequest) reqType, msgPropMap);
        }

        if (operation.equalsIgnoreCase("testConnection")) {
            msg = client.send("vm://dispatchConnectorMsgTestConnection",
                    (ManagedSysDto) reqType, msgPropMap);
        }

        if (operation.equalsIgnoreCase("lookupAttributes")) {
            msg = client.send("vm://dispatchConnectorMsgLookupAttributes",
                    (SearchRequest) reqType, msgPropMap);
        }

        if (operation.equalsIgnoreCase("search")) {
            msg = client.send("vm://dispatchConnectorMessageSearch",
                    (SearchRequest) reqType, msgPropMap);
        }
        log.debug("Service:: Mule Message object: " + msg.toString());

        return msg;

    }

    @Deprecated
    private ConnectorService getService(ProvisionConnectorDto connector) {
        try {

            QName SERVICE_NAME = new QName(connector.getServiceUrl());
            QName PORT_NAME = new QName(connector.getServiceNameSpace(),
                    connector.getServicePort());

            Service service = Service.create(SERVICE_NAME);
            service.addPort(PORT_NAME, SOAPBinding.SOAP11HTTP_BINDING,
                    connector.getServiceUrl());

            ConnectorService port = service.getPort(
                    new QName(connector.getServiceNameSpace(), connector
                            .getServicePort()), ConnectorService.class);
            return port;

        } catch (Exception e) {
            log.error(e);

            return null;
        }

    }

}
