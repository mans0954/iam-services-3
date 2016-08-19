package org.openiam.provision.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.response.ResponseType;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
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
    private ManagedSystemService managedSystemService;

    Response testConnection(String managedSysId) {
        Response resp = new Response(org.openiam.base.ws.ResponseStatus.SUCCESS);

        ManagedSysDto mSys = managedSystemService.getManagedSys(managedSysId);

        if(log.isDebugEnabled()) {
        	log.debug("Testing connection with localConnector");
        }

        ResponseType localResp = connectorAdapter.testConnection(mSys);
        if (localResp.getStatus() == StatusCodeType.FAILURE) {
        	if(log.isDebugEnabled()) {
        		log.debug("Test connection failed.");
        	}

            resp.setStatus(org.openiam.base.ws.ResponseStatus.FAILURE);
            resp.setErrorText(localResp.getErrorMsgAsStr());
        }

        return resp;
    }

}


