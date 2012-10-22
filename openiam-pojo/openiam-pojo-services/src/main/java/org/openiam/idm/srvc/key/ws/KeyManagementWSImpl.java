package org.openiam.idm.srvc.key.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

/**
 * Created by: Alexander Duckardt
 * Date: 19.10.12
 */
@Service("keyManagementWS")
@WebService(endpointInterface = "org.openiam.idm.srvc.key.ws.KeyManagementWS",
            targetNamespace = "urn:idm.openiam.org/srvc/res/service", portName = "KeyManagementWSPort",
            serviceName = "KeyManagementWS")
public class KeyManagementWSImpl implements KeyManagementWS {
    protected final Log log = LogFactory.getLog(this.getClass());
    @Autowired
    private KeyManagementService keyManagementService;

    @Override
    public Response refreshUserKeys() {
        log.debug("Got refreshUserKeys request. ");
        Response resp = new Response(ResponseStatus.SUCCESS);
        try {
            keyManagementService.refreshKeys();
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorText(e.getMessage());
        }
        return resp;
    }
}
