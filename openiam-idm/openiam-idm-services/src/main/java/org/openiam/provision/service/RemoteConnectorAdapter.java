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
import org.openiam.connector.type.*;
import org.openiam.connector.type.ResponseType;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.openiam.idm.srvc.mngsys.ws.ProvisionConnectorWebService;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.spml2.interf.ConnectorService;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.msg.suspend.ResumeRequestType;
import org.openiam.spml2.msg.suspend.SuspendRequestType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import java.util.HashMap;
import java.util.Map;


/**
 * Wraps around the connector interface and manages the calls to the varous operations for the
 * connectors for provisioning.
 *
 * @author suneet
 */
public class RemoteConnectorAdapter {

    protected static final Log log = LogFactory.getLog(RemoteConnectorAdapter.class);

    @Autowired
    private ProvisionConnectorWebService connectorService;


    public UserResponse addRequest(ManagedSysDto managedSys, RemoteUserRequest addReqType, ProvisionConnectorDto connector, MuleContext muleContext) {
        UserResponse resp = new UserResponse();
        try {


            if (managedSys == null) {
                resp.setStatus(StatusCodeType.FAILURE);
                resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
                return resp;
            }
            log.debug("ConnectorAdapter:addRequest called. Managed sys =" + managedSys.getManagedSysId());

            if (connector != null && (connector.getServiceUrl() != null && connector.getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, addReqType, connector.getServiceUrl(), "add", muleContext);
                if (msg != null) {
                    log.debug("***Payload=" + msg.getPayload());
                    if (msg.getPayload() != null && msg.getPayload() instanceof UserResponse) {
                        return (UserResponse) msg.getPayload();
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

    public UserResponse modifyRequest(ManagedSysDto managedSys, RemoteUserRequest request, ProvisionConnectorDto connector, MuleContext muleContext) {
        UserResponse resp = new UserResponse();
        try {
            if (managedSys == null) {
                resp.setStatus(StatusCodeType.FAILURE);
                resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
                return resp;
            }
            log.debug("ConnectorAdapter:modifyRequest called. Managed sys =" + managedSys.getManagedSysId());

            if (connector != null && (connector.getServiceUrl() != null && connector.getServiceUrl().length() > 0)) {

                //ConnectorService port = getService(connector);
                //port.modify(modReqType);
                MuleMessage msg = getService(connector, request, connector.getServiceUrl(), "modify", muleContext);
                if (msg != null) {
                    log.debug("***Payload=" + msg.getPayload());
                    if (msg.getPayload() != null && msg.getPayload() instanceof UserResponse) {
                        return (UserResponse) msg.getPayload();
                    }
                    resp.setStatus(StatusCodeType.SUCCESS);
                    return resp;
                } else {
                    log.debug("MuleMessage is null..");
                }

            }
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
        }
        resp.setStatus(StatusCodeType.FAILURE);
        return resp;


    }

    public LookupResponse lookupRequest(ManagedSysDto managedSys, RemoteLookupRequest req, ProvisionConnectorDto connector, MuleContext muleContext) {

        LookupResponse resp = new LookupResponse();

        if (managedSys == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
            return resp;
        }
        log.debug("ConnectorAdapter:lookupRequest called. Managed sys =" + managedSys.getManagedSysId());

        try {

            if (connector != null && (connector.getServiceUrl() != null && connector.getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, req, connector.getServiceUrl(), "lookup", muleContext);
                if (msg != null) {
                    log.debug("***Payload=" + msg.getPayload());
                    if (msg.getPayload() != null && msg.getPayload() instanceof LookupResponse) {
                        return (LookupResponse) msg.getPayload();
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

    public UserResponse deleteRequest(ManagedSysDto managedSys, RemoteUserRequest request, ProvisionConnectorDto connector, MuleContext muleContext) {
        UserResponse resp = new UserResponse();

        if (managedSys == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
            return resp;
        }

        log.debug("ConnectorAdapter:deleteRequest called. Managed sys =" + managedSys.getManagedSysId());

        try {

            if (connector != null && (connector.getServiceUrl() != null && connector.getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, request, connector.getServiceUrl(), "delete", muleContext);
                if (msg != null) {
                    log.debug("***Payload=" + msg.getPayload());
                    if (msg.getPayload() != null && msg.getPayload() instanceof UserResponse) {
                        return (UserResponse) msg.getPayload();
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

    public ResponseType setPasswordRequest(ManagedSysDto managedSys, RemotePasswordRequest request, ProvisionConnectorDto connector, MuleContext muleContext) {
        ResponseType resp = new ResponseType();

        if (managedSys == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
            return resp;
        }

        log.debug("ConnectorAdapter:setPasswordRequest called. Managed sys =" + managedSys.getManagedSysId());
        try {

            if (connector != null && (connector.getServiceUrl() != null && connector.getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, request, connector.getServiceUrl(), "setPassword", muleContext);
                if (msg != null) {
                    log.debug("***Payload=" + msg.getPayload());
                    if (msg.getPayload() != null && msg.getPayload() instanceof ResponseType) {
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

    public ResponseType resetPasswordRequest(ManagedSysDto managedSys, RemotePasswordRequest request, ProvisionConnectorDto connector, MuleContext muleContext) {
        ResponseType resp = new ResponseType();

        if (managedSys == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
            return resp;
        }
        log.debug("ConnectorAdapter:resetPasswordRequest called. Managed sys =" + managedSys.getManagedSysId());

        try {
            if (connector != null && (connector.getServiceUrl() != null && connector.getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, request, connector.getServiceUrl(), "resetPassword", muleContext);
                if (msg != null) {
                    log.debug("***Payload=" + msg.getPayload());
                    if (msg.getPayload() != null && msg.getPayload() instanceof ResponseType) {
                        return (ResponseType) msg.getPayload();
                    }
                    resp.setStatus(StatusCodeType.SUCCESS);
                    return resp;
                } else {
                    log.debug("MuleMessage is null..");
                }


                //ConnectorService port = getService(connector);
                //port.resetPassword(request);

            }
        } catch (Exception e) {
            log.error(e);
        }
        resp.setStatus(StatusCodeType.FAILURE);
        return resp;


    }

    public ResponseType suspend(ManagedSysDto managedSys, SuspendRequestType request, ProvisionConnectorDto connector, MuleContext muleContext) {
        ResponseType resp = new ResponseType();

        if (managedSys == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
            return resp;
        }

        log.debug("ConnectorAdapter:suspendRequest called. Managed sys =" + managedSys.getManagedSysId());

        try {

            if (connector != null && (connector.getServiceUrl() != null && connector.getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, request, connector.getServiceUrl(), "suspend", muleContext);
                if (msg != null) {
                    log.debug("***Payload=" + msg.getPayload());
                    if (msg.getPayload() != null && msg.getPayload() instanceof ResponseType) {
                        return (ResponseType) msg.getPayload();
                    }
                    resp.setStatus(StatusCodeType.SUCCESS);
                    return resp;
                } else {
                    log.debug("MuleMessage is null..");
                }

                //ConnectorService port = getService(connector);
                //port.suspend(request);

            }
        } catch (Exception e) {
            log.error(e);
        }
        resp.setStatus(StatusCodeType.FAILURE);
        return resp;


    }

    public ResponseType resumeRequest(ManagedSysDto managedSys, ResumeRequestType request, ProvisionConnectorDto connector, MuleContext muleContext) {
        ResponseType resp = new ResponseType();

        if (managedSys == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
            return resp;
        }

        log.debug("ConnectorAdapter:resumeRequest called. Managed sys =" + managedSys.getManagedSysId());
        try {


            if (connector != null && (connector.getServiceUrl() != null && connector.getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, request, connector.getServiceUrl(), "resume", muleContext);
                if (msg != null) {
                    log.debug("***Payload=" + msg.getPayload());
                    if (msg.getPayload() != null && msg.getPayload() instanceof ResponseType) {
                        return (ResponseType) msg.getPayload();
                    }
                    resp.setStatus(StatusCodeType.SUCCESS);
                    return resp;
                } else {
                    log.debug("MuleMessage is null..");
                }


                //ConnectorService port = getService(connector);
                //port.resume(request);

            }
        } catch (Exception e) {
            log.error(e);
        }
        resp.setStatus(StatusCodeType.FAILURE);
        return resp;


    }

    public ResponseType testConnection(ManagedSysDto managedSys,ProvisionConnectorDto connector, MuleContext muleContext) {
        ResponseType resp = new ResponseType();

        if (managedSys == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
            return resp;
        }

        log.debug("ConnectorAdapter:resumeRequest called. Managed sys =" + managedSys.getManagedSysId());
        try {


            if (connector != null && (connector.getServiceUrl() != null && connector.getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, managedSys, connector.getServiceUrl(), "testConnection", muleContext);
                if (msg != null) {
                    log.debug("Test connection Payload=" + msg.getPayload());
                    if (msg.getPayload() != null && msg.getPayload() instanceof ResponseType) {
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

    public ResponseType reconcileResource(RemoteReconciliationConfig config, ProvisionConnectorDto connector, MuleContext muleContext){
        ResponseType resp = new ResponseType();
        if (config == null) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.INVALID_CONFIGURATION);
            return resp;
        }
        log.debug("ConnectorAdapter:reconcileRequest called. Resource =" + config.getResourceId());
        try {


            if (connector != null && (connector.getServiceUrl() != null && connector.getServiceUrl().length() > 0)) {

                MuleMessage msg = getService(connector, config, connector.getServiceUrl(), "reconcile", muleContext);
                if (msg != null) {
                    log.debug("Test connection Payload=" + msg.getPayload());
                    if (msg.getPayload() != null && msg.getPayload() instanceof ResponseType) {
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

    private MuleMessage getService(ProvisionConnectorDto connector, Object reqType, String url, String operation, MuleContext muleContext) {
        try {


            MuleClient client = new MuleClient(muleContext);

            //Map<?,?> msgPropMap = Collections.singletonMap("serviceName","LDAPConnectorService");
            Map<String, String> msgPropMap = new HashMap<String, String>();
            msgPropMap.put("serviceName", url);

            MuleMessage msg = null;

            if (operation.equalsIgnoreCase("add")) {

                msg = client.send("vm://remoteConnectorMessageAdd", (UserRequest) reqType, msgPropMap);
            }
            if (operation.equalsIgnoreCase("modify")) {

                msg = client.send("vm://remoteConnectorMessageModify", (UserRequest) reqType, msgPropMap);
            }
            if (operation.equalsIgnoreCase("lookup")) {

                msg = client.send("vm://remoteConnectorMessageLookup", (LookupRequest) reqType, msgPropMap);
            }
            if (operation.equalsIgnoreCase("reconcile")) {

                client.sendAsync("vm://remoteConnectorMessageReconcile", (ReconciliationConfig) reqType, msgPropMap);
            }
            if (operation.equalsIgnoreCase("delete")) {

                msg = client.send("vm://remoteConnectorMessageDelete", (UserRequest) reqType, msgPropMap);
            }
            if (operation.equalsIgnoreCase("setPassword")) {

                msg = client.send("vm://remoteConnectorMessageSetPassword", (PasswordRequest) reqType, msgPropMap);
            }

            if (operation.equalsIgnoreCase("resetPassword")) {

                msg = client.send("vm://remoteConnectorMessageResetPassword", (PasswordRequest) reqType, msgPropMap);
            }
            if (operation.equalsIgnoreCase("suspend")) {

                msg = client.send("vm://remoteConnectorClientSuspend", (SuspendRequestType) reqType, msgPropMap);
            }
            if (operation.equalsIgnoreCase("resume")) {

                msg = client.send("vm://remoteConnectorMessageResume", (ResumeRequestType) reqType, msgPropMap);
            }
            if (operation.equalsIgnoreCase("testConnection")) {

                msg = client.send("vm://remoteConnectorMsgTestConnection", (ManagedSysDto) reqType, msgPropMap);
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
            QName PORT_NAME = new QName(connector.getServiceNameSpace(), connector.getServicePort());

            Service service = Service.create(SERVICE_NAME);
            service.addPort(PORT_NAME, SOAPBinding.SOAP11HTTP_BINDING, connector.getServiceUrl());

            ConnectorService port = service.getPort(new QName(connector.getServiceNameSpace(),
                    connector.getServicePort()),
                    ConnectorService.class);
            return port;


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    public ProvisionConnectorWebService getConnectorService() {
        return connectorService;
    }

    public void setConnectorService(ProvisionConnectorWebService connectorService) {
        this.connectorService = connectorService;
    }
}
