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
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.*;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.openiam.idm.srvc.mngsys.ws.ProvisionConnectorWebService;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.connector.ConnectorService;
import org.openiam.provision.type.ExtensibleUser;
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
public class RemoteConnectorAdapter {

    protected static final Log log = LogFactory
            .getLog(RemoteConnectorAdapter.class);
    public static final String AD_ERROR_THIS_IS_LIKELY_A_TRANSIENT_CONDITION = "This is likely a transient condition";

    @Autowired
    private ProvisionConnectorWebService connectorService;
    private final static int RESEND_COUNT = 3;

    public ObjectResponse addRequest(ManagedSysDto managedSys,
            CrudRequest addReqType, ProvisionConnectorDto connector,
            MuleContext muleContext) {
        ObjectResponse resp = new ObjectResponse();
        try {

            if (managedSys == null) {
                resp.setStatus(StatusCodeType.FAILURE);
                resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
                return resp;
            }
            log.debug("ConnectorAdapter:addRequest called. Managed sys ="
                    + managedSys.getManagedSysId());

            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                            .getServiceUrl().length() > 0)) {
                int i = 0;
                resp = send("add", addReqType, connector, muleContext);
                // Check if response has error =
                // "A connection to the directory on which to process the request was unavailable. This is likely a transient condition."
                // we try to resend request RESEND_COUNT times
                while ((resp.getStatus() == StatusCodeType.FAILURE && resp
                        .getErrorMsgAsStr()
                        .toLowerCase()
                        .contains(
                                AD_ERROR_THIS_IS_LIKELY_A_TRANSIENT_CONDITION
                                        .toLowerCase()))
                        && i < RESEND_COUNT) {
                    Thread.sleep(200);
                    resp = send("add", addReqType, connector, muleContext);
                    i++;
                }
                return resp;
            }

        } catch (Exception e) {
            log.error(e);
        }
        resp.setStatus(StatusCodeType.FAILURE);
        return resp;

    }

    private ObjectResponse send(String eventName, CrudRequest request,
            ProvisionConnectorDto connector, MuleContext muleContext) {
        ObjectResponse resp = new ObjectResponse();
        resp.setStatus(StatusCodeType.FAILURE);
        MuleMessage msg = getService(connector, request,
                connector.getServiceUrl(), eventName, muleContext);
        if (msg != null) {
            log.debug("***Payload=" + msg.getPayload());
            if (msg.getPayload() != null
                    && msg.getPayload() instanceof ObjectResponse) {
                return (ObjectResponse) msg.getPayload();
            }
            return resp;
        } else {
            log.debug("MuleMessage is null..");
        }
        return resp;
    }

    private SearchResponse sendLookup(String eventName, LookupRequest request,
            ProvisionConnectorDto connector, MuleContext muleContext) {
        SearchResponse resp = new SearchResponse();
        resp.setStatus(StatusCodeType.FAILURE);
        MuleMessage msg = getService(connector, request,
                connector.getServiceUrl(), eventName, muleContext);
        if (msg != null) {
            log.debug("***Payload=" + msg.getPayload());
            if (msg.getPayload() != null
                    && msg.getPayload() instanceof SearchResponse) {
                return (SearchResponse) msg.getPayload();
            }
            return resp;
        } else {
            log.debug("MuleMessage is null..");
        }
        return resp;
    }

    public ObjectResponse modifyRequest(ManagedSysDto managedSys,
            CrudRequest request, ProvisionConnectorDto connector,
            MuleContext muleContext) {
        ObjectResponse resp = new ObjectResponse();
        try {
            if (managedSys == null) {
                resp.setStatus(StatusCodeType.FAILURE);
                resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
                return resp;
            }
            log.debug("ConnectorAdapter:modifyRequest called. Managed sys ="
                    + managedSys.getManagedSysId());

            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                            .getServiceUrl().length() > 0)) {
                int i = 0;
                resp = send("modify", request, connector, muleContext);
                // Check if response has error =
                // "A connection to the directory on which to process the request was unavailable. This is likely a transient condition."
                // we try to resend request RESEND_COUNT times
                while ((resp.getStatus() == StatusCodeType.FAILURE && resp
                        .getErrorMsgAsStr()
                        .toLowerCase()
                        .contains(
                                AD_ERROR_THIS_IS_LIKELY_A_TRANSIENT_CONDITION
                                        .toLowerCase()))
                        && i < RESEND_COUNT) {
                    Thread.sleep(200);
                    resp = send("modify", request, connector, muleContext);
                    i++;
                }
                return resp;
            }
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
        }
        resp.setStatus(StatusCodeType.FAILURE);
        return resp;

    }

    public SearchResponse lookupRequest(ManagedSysDto managedSys,
            LookupRequest<ExtensibleUser> req, ProvisionConnectorDto connector,
            MuleContext muleContext) {

        SearchResponse resp = new SearchResponse();

        if (managedSys == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
            return resp;
        }
        log.debug("ConnectorAdapter:lookupRequest called. Managed sys ="
                + managedSys.getManagedSysId());

        try {

            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                            .getServiceUrl().length() > 0)) {
                int i = 0;
                resp = sendLookup("lookup", req, connector, muleContext);
                // Check if response has error =
                // "A connection to the directory on which to process the request was unavailable. This is likely a transient condition."
                // we try to resend request RESEND_COUNT times
                while ((resp.getStatus() == StatusCodeType.FAILURE && resp
                        .getErrorMsgAsStr()
                        .toLowerCase()
                        .contains(
                                AD_ERROR_THIS_IS_LIKELY_A_TRANSIENT_CONDITION
                                        .toLowerCase()))
                        && i < RESEND_COUNT) {
                    Thread.sleep(200);
                    resp = sendLookup("lookup", req, connector, muleContext);
                    i++;
                }
                return resp;
            }
        } catch (Exception e) {
            log.error(e);
        }
        resp.setStatus(StatusCodeType.FAILURE);
        return resp;

    }

    public ObjectResponse deleteRequest(ManagedSysDto managedSys,
            CrudRequest<ExtensibleUser> request,
            ProvisionConnectorDto connector, MuleContext muleContext) {
        ObjectResponse resp = new ObjectResponse();

        if (managedSys == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
            return resp;
        }

        log.debug("ConnectorAdapter:deleteRequest called. Managed sys ="
                + managedSys.getManagedSysId());

        try {

            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                            .getServiceUrl().length() > 0)) {
                int i = 0;
                resp = send("delete", request, connector, muleContext);
                // Check if response has error =
                // "A connection to the directory on which to process the request was unavailable. This is likely a transient condition."
                // we try to resend request RESEND_COUNT times
                while ((resp.getStatus() == StatusCodeType.FAILURE && resp
                        .getErrorMsgAsStr()
                        .toLowerCase()
                        .contains(
                                AD_ERROR_THIS_IS_LIKELY_A_TRANSIENT_CONDITION
                                        .toLowerCase()))
                        && i < RESEND_COUNT) {
                    Thread.sleep(200);
                    resp = send("delete", request, connector, muleContext);
                    i++;
                }
                return resp;
            }
        } catch (Exception e) {
            log.error(e);
        }
        resp.setStatus(StatusCodeType.FAILURE);
        return resp;

    }

    public ResponseType setPasswordRequest(ManagedSysDto managedSys,
            PasswordRequest request, ProvisionConnectorDto connector,
            MuleContext muleContext) {
        ResponseType resp = new ResponseType();

        if (managedSys == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
            return resp;
        }

        log.debug("ConnectorAdapter:setPasswordRequest called. Managed sys ="
                + managedSys.getManagedSysId());
        try {

            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                            .getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, request,
                        connector.getServiceUrl(), "setPassword", muleContext);
                if (msg != null) {
                    log.debug("***Payload=" + msg.getPayload());
                    if (msg.getPayload() != null
                            && msg.getPayload() instanceof ResponseType) {
                        return (ResponseType) msg.getPayload();
                    }
                    resp.setStatus(StatusCodeType.SUCCESS);
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

    public ResponseType resetPasswordRequest(ManagedSysDto managedSys,
            PasswordRequest request, ProvisionConnectorDto connector,
            MuleContext muleContext) {
        ResponseType resp = new ResponseType();

        if (managedSys == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
            return resp;
        }
        log.debug("ConnectorAdapter:resetPasswordRequest called. Managed sys ="
                + managedSys.getManagedSysId());

        try {
            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                            .getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, request,
                        connector.getServiceUrl(), "resetPassword", muleContext);
                if (msg != null) {
                    log.debug("***Payload=" + msg.getPayload());
                    if (msg.getPayload() != null
                            && msg.getPayload() instanceof ResponseType) {
                        return (ResponseType) msg.getPayload();
                    }
                    resp.setStatus(StatusCodeType.SUCCESS);
                    return resp;
                } else {
                    log.debug("MuleMessage is null..");
                }

                // ConnectorService port = getService(connector);
                // port.resetPassword(request);

            }
        } catch (Exception e) {
            log.error(e);
        }
        resp.setStatus(StatusCodeType.FAILURE);
        return resp;

    }

    public ResponseType suspend(ManagedSysDto managedSys,
            SuspendResumeRequest request, ProvisionConnectorDto connector,
            MuleContext muleContext) {
        ResponseType resp = new ResponseType();

        if (managedSys == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
            return resp;
        }

        log.debug("ConnectorAdapter:suspendRequest called. Managed sys ="
                + managedSys.getManagedSysId());

        try {

            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                            .getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, request,
                        connector.getServiceUrl(), "suspend", muleContext);
                if (msg != null) {
                    log.debug("***Payload=" + msg.getPayload());
                    if (msg.getPayload() != null
                            && msg.getPayload() instanceof ResponseType) {
                        return (ResponseType) msg.getPayload();
                    }
                    resp.setStatus(StatusCodeType.SUCCESS);
                    return resp;
                } else {
                    log.debug("MuleMessage is null..");
                }

                // ConnectorService port = getService(connector);
                // port.suspend(request);

            }
        } catch (Exception e) {
            log.error(e);
        }
        resp.setStatus(StatusCodeType.FAILURE);
        return resp;

    }

    public ResponseType resumeRequest(ManagedSysDto managedSys,
            SuspendResumeRequest request, ProvisionConnectorDto connector,
            MuleContext muleContext) {
        ResponseType resp = new ResponseType();

        if (managedSys == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
            return resp;
        }

        log.debug("ConnectorAdapter:resumeRequest called. Managed sys ="
                + managedSys.getManagedSysId());
        try {

            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                            .getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, request,
                        connector.getServiceUrl(), "resume", muleContext);
                if (msg != null) {
                    log.debug("***Payload=" + msg.getPayload());
                    if (msg.getPayload() != null
                            && msg.getPayload() instanceof ResponseType) {
                        return (ResponseType) msg.getPayload();
                    }
                    resp.setStatus(StatusCodeType.SUCCESS);
                    return resp;
                } else {
                    log.debug("MuleMessage is null..");
                }

                // ConnectorService port = getService(connector);
                // port.resume(request);

            }
        } catch (Exception e) {
            log.error(e);
        }
        resp.setStatus(StatusCodeType.FAILURE);
        return resp;

    }

    public ResponseType testConnection(ManagedSysDto managedSys,
            ProvisionConnectorDto connector, MuleContext muleContext) {
        ResponseType resp = new ResponseType();

        if (managedSys == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
            return resp;
        }

        log.debug("ConnectorAdapter:resumeRequest called. Managed sys ="
                + managedSys.getManagedSysId());
        try {

            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                            .getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, managedSys,
                        connector.getServiceUrl(), "testConnection",
                        muleContext);
                if (msg != null) {
                    log.debug("Test connection Payload=" + msg.getPayload());
                    if (msg.getPayload() != null
                            && msg.getPayload() instanceof ResponseType) {
                        return (ResponseType) msg.getPayload();
                    }
                    resp.setStatus(StatusCodeType.SUCCESS);
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

    public SearchResponse search(SearchRequest<ExtensibleUser> searchRequest,
            ProvisionConnectorDto connector, MuleContext muleContext) {
        SearchResponse resp = new SearchResponse();
        if (searchRequest == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_CONFIGURATION);
            return resp;
        }
        log.debug("RemoteConnectorAdapter:searchRequest called. Search query ="
                + searchRequest.getSearchQuery());
        try {
            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                            .getServiceUrl().length() > 0)) {
                // Send search to Remote Connector to get data (e.g. Active
                // Directory via PowershellConnector)
                MuleMessage msg = getService(connector, searchRequest,
                        connector.getServiceUrl(), "search", muleContext);
                if (msg != null) {
                    log.debug("Search Payload=" + msg.getPayload());
                    if (msg.getPayload() != null
                            && msg.getPayload() instanceof ResponseType) {
                        resp = (SearchResponse) msg.getPayload();
                        if (resp.getStatus() == StatusCodeType.SUCCESS
                                || resp.getObjectList().size() > 0) {
                            if (resp.getErrorMessage().size() > 0) {
                                log.debug("RemoteConnector Search: error message = "
                                        + resp.getErrorMsgAsStr());
                            }
                            log.debug("RemoteConnector Search:"
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
            log.error(e);
        }
        resp.setStatus(StatusCodeType.FAILURE);
        return resp;
    }

    public ResponseType reconcileResource(ReconciliationConfig config,
            ProvisionConnectorDto connector, MuleContext muleContext) {
        ResponseType resp = new ResponseType();
        if (config == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_CONFIGURATION);
            return resp;
        }
        log.debug("ConnectorAdapter:reconcileRequest called. Resource ="
                + config.getResourceId());
        try {

            if (connector != null
                    && (connector.getServiceUrl() != null && connector
                            .getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, config,
                        connector.getServiceUrl(), "reconcile", muleContext);
                if (msg != null) {
                    log.debug("Test connection Payload=" + msg.getPayload());
                    if (msg.getPayload() != null
                            && msg.getPayload() instanceof ResponseType) {
                        return (ResponseType) msg.getPayload();
                    }
                    resp.setStatus(StatusCodeType.SUCCESS);
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

    private MuleMessage getService(ProvisionConnectorDto connector,
            Object reqType, String url, String operation,
            MuleContext muleContext) {
        try {

            MuleClient client = new MuleClient(muleContext);

            // Map<?,?> msgPropMap =
            // Collections.singletonMap("serviceName","LDAPConnectorService");
            Map<String, String> msgPropMap = new HashMap<String, String>();
            msgPropMap.put("serviceName", url);

            MuleMessage msg = null;

            if (operation.equalsIgnoreCase("add")) {

                msg = client.send("vm://remoteConnectorMessageAdd",
                        (CrudRequest<ExtensibleUser>) reqType, msgPropMap);
            }
            if (operation.equalsIgnoreCase("modify")) {

                msg = client.send("vm://remoteConnectorMessageModify",
                        (CrudRequest<ExtensibleUser>) reqType, msgPropMap);
            }
            if (operation.equalsIgnoreCase("lookup")) {

                msg = client.send("vm://remoteConnectorMessageLookup",
                        (LookupRequest<ExtensibleUser>) reqType, msgPropMap);
            }
            if (operation.equalsIgnoreCase("reconcile")) {

                client.sendAsync("vm://remoteConnectorMessageReconcile",
                        (ReconciliationConfig) reqType, msgPropMap);
            }
            if (operation.equalsIgnoreCase("delete")) {

                msg = client.send("vm://remoteConnectorMessageDelete",
                        (CrudRequest<ExtensibleUser>) reqType, msgPropMap);
            }
            if (operation.equalsIgnoreCase("setPassword")) {

                msg = client.send("vm://remoteConnectorMessageSetPassword",
                        (PasswordRequest) reqType, msgPropMap);
            }

            if (operation.equalsIgnoreCase("resetPassword")) {

                msg = client.send("vm://remoteConnectorMessageResetPassword",
                        (PasswordRequest) reqType, msgPropMap);
            }
            if (operation.equalsIgnoreCase("suspend")) {

                msg = client.send("vm://remoteConnectorMessageSuspend",
                        (SuspendResumeRequest) reqType, msgPropMap);
            }
            if (operation.equalsIgnoreCase("resume")) {

                msg = client.send("vm://remoteConnectorMessageResume",
                        (SuspendResumeRequest) reqType, msgPropMap);
            }
            if (operation.equalsIgnoreCase("testConnection")) {

                msg = client.send("vm://remoteConnectorMsgTestConnection",
                        (ManagedSysDto) reqType, msgPropMap);
            }
            if (operation.equalsIgnoreCase("search")) {

                msg = client.send("vm://remoteConnectorMessageSearch",
                        (SearchRequest) reqType, msgPropMap);
            }

            return msg;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

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
            e.printStackTrace();
            return null;
        }

    }

}
