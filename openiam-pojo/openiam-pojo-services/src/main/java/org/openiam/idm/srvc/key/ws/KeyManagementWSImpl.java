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
    public Response generateMasterKey() {
        log.debug("Got generateMasterKey request. ");
        Response resp = new Response(ResponseStatus.SUCCESS);
        try {
            keyManagementService.generateMasterKey();

            log.warn("GenerateMasterKey request successfully handled ");
        } catch(Exception e) {
            log.warn("ERROR: " + e.getMessage());
            log.error(e.getMessage(), e);
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorText(e.getMessage());
        }
        return resp;
    }

    @Override
    public Response migrateData(String secretKey) {
        log.debug("Got migrateData request. ");
        Response resp = new Response(ResponseStatus.SUCCESS);
        try {
            keyManagementService.migrateData(secretKey);
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorText(e.getMessage());
        }
        return resp;
    }

    @Override
    public byte[] getCookieKey() throws Exception {
        byte[] key = null;
        try {
            key = keyManagementService.getCookieKey();
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
        return key;
    }

    @Override
    public byte[] generateCookieKey() throws Exception {
        byte[] key = null;
        try {
            key = keyManagementService.generateCookieKey();
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
        return key;
    }
}
