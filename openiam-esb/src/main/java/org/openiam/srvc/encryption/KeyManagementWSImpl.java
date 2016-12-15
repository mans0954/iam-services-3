package org.openiam.srvc.encryption;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.request.EmptyServiceRequest;
import org.openiam.base.request.StringDataRequest;
import org.openiam.base.response.data.ByteArrayResponse;
import org.openiam.base.response.data.StringResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.mq.constants.api.common.EncryptionAPI;
import org.openiam.mq.constants.queue.MqQueue;
import org.openiam.mq.constants.queue.common.EncryptionQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

/**
 * Created by: Alexander Duckardt
 * Date: 19.10.12
 */
@Service("keyManagementWS")
@WebService(endpointInterface = "org.openiam.srvc.encryption.KeyManagementWS",
            targetNamespace = "urn:idm.openiam.org/srvc/key/service", portName = "KeyManagementWSPort",
            serviceName = "KeyManagementWS")
public class KeyManagementWSImpl extends AbstractApiService implements KeyManagementWS {
    @Autowired
    public KeyManagementWSImpl(EncryptionQueue queue) {
        super(queue);
    }

    @Override
    public Response initKeyManagement(){
        return this.getResponse(EncryptionAPI.InitKeyManagement, new EmptyServiceRequest(), Response.class);
    }

    @Override
    public Response generateMasterKey() {
        return this.getResponse(EncryptionAPI.GenerateMasterKey, new EmptyServiceRequest(), Response.class);
    }

    @Override
    public Response migrateData(String secretKey) {
        StringDataRequest request = new StringDataRequest();
        request.setData(secretKey);
        return this.getResponse(EncryptionAPI.MigrateData, request, Response.class);
    }

    @Override
    public byte[] getCookieKey() throws Exception {
        return this.getValue(EncryptionAPI.GetCookieKey, new EmptyServiceRequest(), ByteArrayResponse.class);
    }

    @Override
    public byte[] generateCookieKey() throws Exception {
        return this.getValue(EncryptionAPI.GenerateCookieKey, new EmptyServiceRequest(), ByteArrayResponse.class);
    }

    @Override
    public String encryptData(String data) {
        StringDataRequest request = new StringDataRequest();
        request.setData(data);
        return this.getValue(EncryptionAPI.EncryptData, request, StringResponse.class);
    }
    @Override
    public String decryptData(String encryptedData) {
        StringDataRequest request = new StringDataRequest();
        request.setData(encryptedData);
        return this.getValue(EncryptionAPI.DecryptData, request, StringResponse.class);
    }
}
