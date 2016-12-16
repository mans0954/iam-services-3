package org.openiam.mq;

import org.openiam.base.request.*;
import org.openiam.base.response.data.PolicyResponse;
import org.openiam.base.response.data.StringResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.policy.dto.PasswordPolicyAssocSearchBean;
import org.openiam.idm.srvc.pswd.service.PasswordService;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.api.user.PasswordAPI;
import org.openiam.mq.constants.queue.user.PasswordQueue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.openiam.mq.listener.AbstractListener;

/**
 * Created by aduckardt on 2016-12-16.
 */
@Component
@RabbitListener(id = "PasswordQueueListener",
        queues = "#{PasswordQueue.name}",
        containerFactory = "userRabbitListenerContainerFactory")
public class PasswordQueueListener extends AbstractListener<PasswordAPI> {
    @Autowired
    private PasswordService passwordDS;


    @Autowired
    public PasswordQueueListener(PasswordQueue queue) {
        super(queue);
    }

    @Override
    protected RequestProcessor<PasswordAPI, BaseSearchServiceRequest> getSearchRequestProcessor() {
        return new RequestProcessor<PasswordAPI, BaseSearchServiceRequest>() {
            @Override
            public Response doProcess(PasswordAPI api, BaseSearchServiceRequest request) throws BasicDataServiceException {
                switch (api){
                    case GetPasswordPolicy:
                        PolicyResponse response = new PolicyResponse();
                        response.setValue(passwordDS.getPasswordPolicy((PasswordPolicyAssocSearchBean)request.getSearchBean()));
                        return response;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
            }
        };
    }

    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) PasswordAPI api, PasswordRequest request)  throws BasicDataServiceException{
        log.debug("Got message in Listener:  {} API. Message:  {}", api, request);
        return  this.processRequest(api, request, new RequestProcessor<PasswordAPI, PasswordRequest>() {
            @Override
            public Response doProcess(PasswordAPI api, PasswordRequest request) throws BasicDataServiceException {
                return passwordDS.isPasswordValid(request.getPassword());
            }
        });
    }
    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) PasswordAPI api, StringDataRequest request)  throws BasicDataServiceException{
        log.debug("Got message in Listener:  {} API. Message:  {}", api, request);
        return  this.processRequest(api, request, new RequestProcessor<PasswordAPI, StringDataRequest>() {
            @Override
            public Response doProcess(PasswordAPI api, StringDataRequest request) throws BasicDataServiceException {
                switch (api){
                    case ValidateResetToken:
                        return passwordDS.validatePasswordResetToken(request.getData());
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
            }
        });
    }

    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) PasswordAPI api, PasswordResetTokenRequest request)  throws BasicDataServiceException{
        log.debug("Got message in Listener:  {} API. Message:  {}", api, request);
        return  this.processRequest(api, request, new RequestProcessor<PasswordAPI, PasswordResetTokenRequest>() {
            @Override
            public Response doProcess(PasswordAPI api, PasswordResetTokenRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case GeneratePasswordResetToken:
                        response = passwordDS.generatePasswordResetToken(request);
                        break;
                    case GetPasswordResetToken:
                        response=new StringResponse();
                        ((StringResponse)response).setValue(passwordDS.getPasswordResetToken(request));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        });
    }
}
