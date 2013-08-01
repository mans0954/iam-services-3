/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the Lesser GNU General Public License 
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

package org.openiam.spml2.spi.script;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.connector.type.response.LookupAttributeResponse;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.type.request.*;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemObjectMatchDAO;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.script.ScriptIntegration;
import org.openiam.spml2.base.AbstractSpml2Complete;
import org.openiam.connector.ConnectorService;
import org.openiam.spml2.spi.ldap.LdapConnectorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.jws.WebParam;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Connector shell that can be used to jumpstart the creation of a connector service.
 *
 * @author suneet
 */

//@WebService(endpointInterface = "org.openiam.spml2.interf.ConnectorService",
//        targetNamespace = "http://www.openiam.org/service/connector",
//        portName = "ScriptConnectorServicePort",
//        serviceName = "ScriptConnectorService")
@Deprecated
public class ScriptConnectorImpl extends AbstractSpml2Complete {

    private static final Log log = LogFactory.getLog(LdapConnectorImpl.class);
    protected ManagedSystemWebService managedSysService;
    protected ManagedSystemObjectMatchDAO managedSysObjectMatchDao;
    protected ResourceDataService resourceDataService;
    
    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    public ObjectResponse add(CrudRequest reqType) {
        String targetID = reqType.getTargetID();
        ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);

        try {
            return createConnector(managedSys).add(reqType);
        } catch (Exception e) {
            log.error("Could not add: " + e.toString());

            ObjectResponse resp = new ObjectResponse();
            resp.setStatus(StatusCodeType.FAILURE);
            return resp;

        }
    }

    public ObjectResponse delete(CrudRequest reqType) {
        String targetID = reqType.getTargetID();
        ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);

        try {
            return createConnector(managedSys).delete(reqType);
        } catch (Exception e) {
            log.error("Could not delete: " + e.toString());

            ObjectResponse resp = new ObjectResponse();
            resp.setStatus(StatusCodeType.FAILURE);
            return resp;
        }
    }

    public SearchResponse lookup(LookupRequest reqType) {
        String targetID = reqType.getTargetID();
        ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);

        try {
            return createConnector(managedSys).lookup(reqType);
        } catch (Exception e) {
            log.error("Lookup problem: " + e.toString());

            SearchResponse resp = new SearchResponse();
            resp.setStatus(StatusCodeType.FAILURE);
            return resp;
        }
    }

    /*
* (non-Javadoc)
*
* @see org.openiam.spml2.interf.SpmlCore#lookupAttributeNames(org.openiam.spml2.msg.
* LookupAttributeRequestType)
*/
    public LookupAttributeResponse lookupAttributeNames(LookupRequest reqType){
        LookupAttributeResponse respType = new LookupAttributeResponse();
        respType.setStatus(StatusCodeType.FAILURE);
        respType.setError(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION);

        return respType;
    }

    public ObjectResponse modify(CrudRequest reqType) {
        String targetID = reqType.getTargetID();
        ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);

        try {
            return createConnector(managedSys).modify(reqType);
        } catch (Exception e) {
            log.error("Could not modify: " + e.toString());

            ObjectResponse resp = new ObjectResponse();
            resp.setStatus(StatusCodeType.FAILURE);
            return resp;
        }
    }

    public ResponseType expirePassword(PasswordRequest reqType) {
        String targetID = reqType.getTargetID();
        ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);

        try {
            return createConnector(managedSys).expirePassword(reqType);
        } catch (Exception e) {
            log.error("Could not expire password: " + e.toString());

            ResponseType resp = new ResponseType();
            resp.setStatus(StatusCodeType.FAILURE);
            return resp;
        }
    }

    public ResponseType resetPassword(PasswordRequest reqType) {
        String targetID = reqType.getTargetID();
        ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);

        try {
            return createConnector(managedSys).resetPassword(reqType);
        } catch (Exception e) {
            log.error("Could not reset password: " + e.toString());

            ResponseType resp = new ResponseType();
            resp.setStatus(StatusCodeType.FAILURE);
            return resp;
        }
    }

    public ResponseType setPassword(PasswordRequest reqType) {
        String targetID = reqType.getTargetID();
        ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);

        try {
            return createConnector(managedSys).setPassword(reqType);
        } catch (Exception e) {
            log.error("Could not set password: " + e.toString());

            ResponseType resp = new ResponseType();
            resp.setStatus(StatusCodeType.FAILURE);
            return resp;
        }
    }

    public ResponseType reconcileResource(@WebParam(name = "config", targetNamespace = "") ReconciliationConfig config) {
        ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.FAILURE);
        response.setError(ErrorCode.UNSUPPORTED_OPERATION);
        return response;
    }

    public ResponseType testConnection(ManagedSysDto managedSys) {
        try {
            return null;
          //  return createConnector(managedSys).testConnection(managedSys);
        } catch (Exception e) {
            log.error("Could not test connection: " + e.toString());

            ResponseType resp = new ResponseType();
            resp.setStatus(StatusCodeType.FAILURE);
            return resp;
        }
    }

    public ResponseType suspend(SuspendRequest reqType) {
        String targetID = reqType.getTargetID();
        ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);

        try {
            return createConnector(managedSys).suspend(reqType);
        } catch (Exception e) {
            log.error("Error suspending: " + e.toString());

            ResponseType resp = new ResponseType();
            resp.setStatus(StatusCodeType.FAILURE);
            return resp;
        }
    }

    public ResponseType resume(SuspendResumeRequest reqType) {
        String targetID = reqType.getTargetID();
        ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);

        try {
            return createConnector(managedSys).resume(reqType);
        } catch (Exception e) {
            log.error("Error resuming : " + e.toString());

            ResponseType resp = new ResponseType();
            resp.setStatus(StatusCodeType.FAILURE);
            return resp;
        }
    }

    public ResponseType validatePassword(PasswordRequest reqType) {
        String targetID = reqType.getTargetID();
        ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);

        try {
            return createConnector(managedSys).validatePassword(reqType);
        } catch (Exception e) {
            log.error("Error validating password: " + e.toString());

            ResponseType resp = new ResponseType();
            resp.setStatus(StatusCodeType.FAILURE);
            return resp;
        }
    }

//    @Override
    public SearchResponse search(@WebParam(name = "searchRequest", targetNamespace = "") SearchRequest searchRequest) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    private ConnectorService createConnector(ManagedSysDto managedSys) throws ClassNotFoundException, IOException {
        String connectorPath = "/connector/" + managedSys.getName() + ".groovy";

        Map<String, Object> bindingMap = new HashMap<String, Object>();
        bindingMap.put("managedSys", managedSys);
        return (ConnectorService) scriptRunner.instantiateClass(bindingMap, connectorPath);
    }

    public ManagedSystemWebService getManagedSysService() {
        return managedSysService;
    }

    public void setManagedSysService(ManagedSystemWebService managedSysService) {
        this.managedSysService = managedSysService;
    }

    public ManagedSystemObjectMatchDAO getManagedSysObjectMatchDao() {
        return managedSysObjectMatchDao;
    }

    public void setManagedSysObjectMatchDao(ManagedSystemObjectMatchDAO managedSysObjectMatchDao) {
        this.managedSysObjectMatchDao = managedSysObjectMatchDao;
    }

    public ResourceDataService getResourceDataService() {
        return resourceDataService;
    }

    public void setResourceDataService(ResourceDataService resourceDataService) {
        this.resourceDataService = resourceDataService;
    }
}
