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
import org.openiam.base.id.UUIDGen;
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
    private ProvisionConnectorWebService connectorService;

    public ObjectResponse addRequest(ManagedSysDto managedSys,
                                     CrudRequest addReqType,
                                     MuleContext muleContext) {
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

                MuleMessage msg = getService(connector, addReqType,
                        connector.getServiceUrl(), "add", muleContext);
                log.debug("***ADD Payload=" + msg);
                if (msg.getPayload() != null
                        && msg.getPayload() instanceof ObjectResponse) {
                    return (ObjectResponse) msg.getPayload();
                }

            }
            return resp;

        } catch (Exception e) {
            log.debug("Exception caught in ConnectorAdapter:addRequest"); //SIA 2015-08-01
            log.error(e);
            log.error(e.getStackTrace()); //SIA 2015-08-01

            resp.setError(ErrorCode.OTHER_ERROR);
            resp.addErrorMessage(e.toString());
            return resp;
        }

    }

    public ObjectResponse modifyRequest(ManagedSysDto managedSys,
                                        CrudRequest modReqType,
                                        MuleContext muleContext) {
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
            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                    .getServiceUrl().length() > 0)) {

                // ConnectorService port = getService(connector);
                // port.modify(modReqType);
                MuleMessage msg = getService(connector, modReqType,
                        connector.getServiceUrl(), "modify", muleContext);

                log.debug("***MODIFY Payload=" + msg);
                if (msg.getPayload() != null
                        && msg.getPayload() instanceof ObjectResponse) {
                    return (ObjectResponse) msg.getPayload();
                }

            }
            return resp;
        } catch (Exception e) {
            log.debug("Exception caught in ConnectorAdaptor:modifyRequest");
            log.error(e);
            log.error(e.getStackTrace()); //SIA 2015-08-01

            resp.setError(ErrorCode.OTHER_ERROR);
            resp.addErrorMessage(e.toString());
            return resp;
        }

    }

    public SearchResponse lookupRequest(ManagedSysDto managedSys,
                                        LookupRequest req,
                                        MuleContext muleContext) {
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
            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                    .getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, req,
                        connector.getServiceUrl(), "lookup", muleContext);
                log.debug("***LOOKUP Payload=" + msg.getPayload());
                if (msg.getPayload() != null
                        && msg.getPayload() instanceof SearchResponse) {

                    return (SearchResponse) msg.getPayload();
                } else {
                    log.debug("LOOKUP payload is not an instance of LookupResponseType");
                    return resp;
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
                    log.debug("***SEARCH Payload=" + msg);
                    if (msg.getPayload() != null
                            && msg.getPayload() instanceof SearchResponse) {
                        resp = (SearchResponse) msg.getPayload();
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
                } else {
                    log.debug("MuleMessage is null..");
                }

            }
        } catch (Exception e) {
            log.debug("Exception caught in ConnectorAdapter:search"); //SIA 2015-08-01
            log.error(e);
            log.error(e.getStackTrace()); //SIA 2015-08-01

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

                log.debug("***RECONCILE payload=" + msg);

                if (msg.getPayload() != null
                        && msg.getPayload() instanceof ResponseType) {
                    return (ResponseType) msg.getPayload();
                }

            }
            return type;
        } catch (Exception e) {
            log.debug("Exception caught in ConnectorAdapter:reconcileResource"); //SIA 2015-08-01
            log.error(e);
            log.error(e.getStackTrace()); //SIA 2015-08-01

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

                log.debug("***Lookup Attributes payload=" + msg);
                if (msg.getPayload() != null
                        && msg.getPayload() instanceof LookupAttributeResponse) {
                    return (LookupAttributeResponse) msg.getPayload();
                }

            }
            return type;
        } catch (Exception e) {
            log.debug("Exception caught in ConnectorAdapter:lookupAttributes"); //SIA 2015-08-01
            log.error(e);
            log.error(e.getStackTrace()); //SIA 2015-08-01

            type.setError(ErrorCode.OTHER_ERROR);
            type.addErrorMessage(e.toString());
            return type;

        }
    }

    public ObjectResponse deleteRequest(ManagedSysDto managedSys,
                                        CrudRequest delReqType,
                                        MuleContext muleContext) {
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
            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                    .getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, delReqType,
                        connector.getServiceUrl(), "delete", muleContext);
                log.debug("***Delete Request =" + msg.getPayload());

                if (msg.getPayload() != null
                        && msg.getPayload() instanceof ObjectResponse) {
                    return (ObjectResponse) msg.getPayload();
                }

            }
            return resp;
        } catch (Exception e) {
            log.debug("Exception caught in ConnectorAdapter:deleteRequest"); //SIA 2015-08-01
            log.error(e);
            log.error(e.getStackTrace()); //SIA 2015-08-01

            resp.setError(ErrorCode.OTHER_ERROR);
            resp.addErrorMessage(e.toString());
            return resp;

        }

    }

    @Deprecated
/**
 * Please use ResetPassword instead
 */
    public ResponseType setPasswordRequest(ManagedSysDto managedSys,
                                           PasswordRequest request,
                                           MuleContext muleContext) {
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
            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                    .getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, request,
                        connector.getServiceUrl(), "setPassword", muleContext);

                log.debug("***Set Password Request payload=" + msg);
                if (msg.getPayload() != null
                        && msg.getPayload() instanceof ResponseType) {
                    return (ResponseType) msg.getPayload();
                }

            }
            return resp;
        } catch (Exception e) {
            log.debug("Exception caught in ConnectorAdapter:setPasswordRequest"); //SIA 2015-08-01
            log.error(e);
            log.error(e.getStackTrace()); //SIA 2015-08-01
            resp.setError(ErrorCode.OTHER_ERROR);
            resp.addErrorMessage(e.toString());
            return resp;

        }

    }

    public ResponseType resetPasswordRequest(
            ManagedSysDto managedSys,
            PasswordRequest request,
            MuleContext muleContext) {

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
            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                    .getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, request,
                        connector.getServiceUrl(), "resetPassword", muleContext);

                if (msg != null) {
                    log.debug("***Reset Pasword Payload=" + msg.getPayload());
                    if (msg.getPayload() != null
                            && msg.getPayload() instanceof ResponseType) {
                        return (ResponseType) msg.getPayload();
                    }
                }

            }
            return resp;
        } catch (Exception e) {
            log.debug("Exception caught in ConnectorAdapter:resetPasswordRequest"); //SIA 2015-08-01
            log.error(e);
            log.error(e.getStackTrace()); //SIA 2015-08-01
            resp.setError(ErrorCode.OTHER_ERROR);
            resp.addErrorMessage(e.toString());
            return resp;

        }

    }

    public ResponseType suspendRequest(ManagedSysDto managedSys,
                                       SuspendResumeRequest request,
                                       MuleContext muleContext) {

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

            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                    .getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, request,
                        connector.getServiceUrl(), "suspend", muleContext);
                log.debug("***Suspend Payload=" + msg.getPayload());
                if (msg.getPayload() != null
                        && msg.getPayload() instanceof ResponseType) {
                    return (ResponseType) msg.getPayload();
                }


            }
            return resp;

        } catch (Exception e) {
            log.debug("Exception caught in ConnectorAdapter:suspendRequest"); //SIA 2015-08-01
            log.error(e);
            log.error(e.getStackTrace()); //SIA 2015-08-01
            resp.setError(ErrorCode.OTHER_ERROR);
            resp.addErrorMessage(e.toString());
            return resp;

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
                + managedSys.getId());
        try {
            ProvisionConnectorDto connector = connectorService
                    .getProvisionConnector(managedSys.getConnectorId());
            log.debug("Connector found for " + connector.getConnectorId());

            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                    .getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, request,
                        connector.getServiceUrl(), "resume", muleContext);

                log.debug("***Resume Payload=" + msg.getPayload());
                if (msg.getPayload() != null
                        && msg.getPayload() instanceof ResponseType) {
                    return (ResponseType) msg.getPayload();
                }

            }
            return type;
        } catch (Exception e) {
            log.debug("Exception caught in ConnectorAdapter:resumeRequest"); //SIA 2015-08-01
            log.error(e);
            log.error(e.getStackTrace()); //SIA 2015-08-01
            type.setError(ErrorCode.OTHER_ERROR);
            type.addErrorMessage(e.toString());
            return type;

        }

    }

    public ResponseType testConnection(ManagedSysDto managedSys,
                                       MuleContext muleContext) {

        return testConnection(managedSys.getUserId(), managedSys.getDecryptPassword(), managedSys, muleContext);
    }

    public ResponseType validatePassword(
            ManagedSysDto managedSys,
            PasswordRequest request,
            MuleContext muleContext) {
        ResponseType resp = new ResponseType();
        resp.setStatus(StatusCodeType.FAILURE);

        if (managedSys == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
            return resp;
        }
        log.debug("ConnectorAdapter:testCredentials called. Managed sys ="
                + managedSys.getId());

        try {
            ProvisionConnectorDto connector = connectorService
                    .getProvisionConnector(managedSys.getConnectorId());
            log.debug("Connector found for " + connector.getConnectorId());
            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                    .getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, request,
                        connector.getServiceUrl(), "validatePassword", muleContext);

                if (msg != null) {
                    log.debug("***Test Credentials Payload=" + msg.getPayload());
                    if (msg.getPayload() != null
                            && msg.getPayload() instanceof ResponseType) {
                        return (ResponseType) msg.getPayload();
                    }
                }

            }
            return resp;
        } catch (Exception e) {
            log.debug("Exception caught in ConnectorAdapter:validatePassword"); //SIA 2015-08-01
            log.error(e);
            log.error(e.getStackTrace()); //SIA 2015-08-01
            resp.setError(ErrorCode.OTHER_ERROR);
            resp.addErrorMessage(e.toString());
            return resp;

        }
    }

    public ResponseType testConnection(String login,
                                       String simplePassword,
                                       ManagedSysDto managedSys,
                                       MuleContext muleContext) {

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

            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                    .getServiceUrl().length() > 0)) {
                RequestType<ExtensibleUser> rt = new RequestType<ExtensibleUser>();
                rt.setTargetID(managedSys.getId());
                rt.setScriptHandler(managedSys.getTestConnectionHandler());
                rt.setHostPort((managedSys.getPort() != null) ? managedSys.getPort().toString() : null);
                rt.setHostUrl(managedSys.getHostUrl());
                rt.setHostLoginId(login);
                rt.setHostLoginPassword(simplePassword);

                MuleMessage msg = getService(connector, rt,
                        connector.getServiceUrl(), "testConnection",
                        muleContext);

                log.debug("MuleMessage payload=" + msg);

                log.debug("***Resume Payload=" + msg.getPayload());
                if (msg.getPayload() != null
                        && msg.getPayload() instanceof ResponseType) {
                    return (ResponseType) msg.getPayload();
                }

            }
            return type;
        } catch (Exception e) {
            log.debug("Exception caught in ConnectorAdapter:testConnection"); //SIA 2015-08-01
            log.error(e);
            log.error(e.getStackTrace()); //SIA 2015-08-01
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
        if (operation.equalsIgnoreCase("validatePassword")) {

            msg = client.send("vm://dispatchConnectorMessageValidatePassword",
                    (PasswordRequest) reqType, msgPropMap);

        }
        if (operation.equalsIgnoreCase("suspend")) {

            msg = client.send("vm://dispatchConnectorMessageSuspend",
                    (SuspendResumeRequest) reqType, msgPropMap);
        }
        if (operation.equalsIgnoreCase("resume")) {
            msg = client.send("vm://dispatchConnectorMessageResume",
                    (SuspendResumeRequest) reqType, msgPropMap);
        }

        if (operation.equalsIgnoreCase("testConnection")) {
            msg = client.send("vm://dispatchConnectorMsgTestConnection",
                    (RequestType<ExtensibleUser>) reqType, msgPropMap);
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


}
