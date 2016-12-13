package org.openiam.mq;

import org.openiam.base.request.*;
import org.openiam.base.response.data.ByteArrayResponse;
import org.openiam.base.response.data.StringResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.api.common.EmailAPI;
import org.openiam.mq.constants.api.common.EncryptionAPI;
import org.openiam.mq.constants.queue.common.EncryptionQueue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.openiam.mq.listener.AbstractListener;

/**
 * Created by aduckardt on 2016-12-13.
 */
@Component
@RabbitListener(id = "KeyManagementListener",
        queues = "#{EncryptionQueue.name}",
        containerFactory = "commonRabbitListenerContainerFactory")
public class KeyManagementListener extends AbstractListener<EncryptionAPI> {
    @Autowired
    private KeyManagementService keyManagementService;
    @Autowired
    public KeyManagementListener(EncryptionQueue queue) {
        super(queue);
    }

    @Override
    protected RequestProcessor<EncryptionAPI, EmptyServiceRequest> getEmptyRequestProcessor(){
        return new RequestProcessor<EncryptionAPI, EmptyServiceRequest>(){
            @Override
            public Response doProcess(EncryptionAPI api, EmptyServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case InitKeyManagement:
                        response=new Response();
                        keyManagementService.initKeyManagement();
                        break;
                    case GenerateMasterKey:
                        response=new Response();
                        keyManagementService.generateMasterKey();
                        break;
                    case GenerateCookieKey:
                        response=new ByteArrayResponse();
                        ((ByteArrayResponse)response).setValue(keyManagementService.generateCookieKey());
                        break;
                    case GetCookieKey:
                        response=new ByteArrayResponse();
                        ((ByteArrayResponse)response).setValue(keyManagementService.getCookieKey());
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }

    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) EncryptionAPI api, StringDataRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<EncryptionAPI, StringDataRequest>(){
            @Override
            public Response doProcess(EncryptionAPI api, StringDataRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case MigrateData:
                        response=new Response();
                        keyManagementService.migrateData(request.getData());
                        break;
                    case EncryptData:
                        response=new StringResponse();
                        ((StringResponse)response).setValue(keyManagementService.encryptData(request.getData()));
                        break;
                    case DecryptData:
                        response=new StringResponse();
                        ((StringResponse)response).setValue(keyManagementService.decryptData(request.getData()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        });
    }
}
