package org.openiam.provision.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.openiam.base.ws.Response;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.mngsys.ws.ProvisionConnectorWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: suneetshah
 * Date: 7/23/11
 * Time: 5:52 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ValidateConnectionConfig {

    protected static final Log log = LogFactory.getLog(ValidateConnectionConfig.class);
    @Autowired
    protected ConnectorAdapter connectorAdapter;
    @Autowired
    protected RemoteConnectorAdapter remoteConnectorAdapter;
    @Autowired
    protected ManagedSystemWebService managedSysService;

    @Autowired
    private ProvisionConnectorWebService connectorService;


    Response testConnection(String managedSysId, MuleContext muleContext) {
        Response resp = new Response(org.openiam.base.ws.ResponseStatus.SUCCESS);

        ManagedSysDto mSys = managedSysService.getManagedSys(managedSysId);
        ProvisionConnectorDto connector = connectorService.getProvisionConnector(mSys.getConnectorId());

        if (connector.getConnectorInterface() != null &&
                connector.getConnectorInterface().equalsIgnoreCase("REMOTE")) {

            log.debug("Testing connection with remoteConnector");

            ResponseType remoteResp = remoteTestConnection(mSys, connector, muleContext);
            if (remoteResp.getStatus() == StatusCodeType.FAILURE) {

                log.debug("Test connection failed.");

                resp.setStatus(org.openiam.base.ws.ResponseStatus.FAILURE);
                resp.setErrorText(remoteResp.getErrorMsgAsStr());

            }
        } else {

            log.debug("Testing connection with localConnector");

            ResponseType localResp = localTestConnection(mSys, muleContext);
            if (localResp.getStatus() == StatusCodeType.FAILURE) {

                log.debug("Test connection failed.");

                resp.setStatus(org.openiam.base.ws.ResponseStatus.FAILURE);
                resp.setErrorText(localResp.getErrorMsgAsStr());
            }

        }

        return resp;

    }

    private ResponseType localTestConnection(ManagedSysDto mSys, MuleContext muleContext) {


        return connectorAdapter.testConnection(mSys, muleContext);

    }

    private ResponseType remoteTestConnection(ManagedSysDto mSys, ProvisionConnectorDto connector,
                                                                         MuleContext muleContext) {


        return remoteConnectorAdapter.testConnection(mSys, connector, muleContext);

    }

}


