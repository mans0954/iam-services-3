package org.openiam.mq;

import org.openiam.base.request.NotificationRequest;
import org.openiam.base.request.SendEmailRequest;
import org.openiam.base.request.TweetMessageRequest;
import org.openiam.base.response.BooleanResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.msg.service.MailDataService;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.api.common.EmailAPI;
import org.openiam.mq.constants.queue.common.MailServiceQueue;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 01/08/16.
 */
@Component
@RabbitListener(id="mailServiceQueueListener",
        queues = "#{MailServiceQueue.name}",
        containerFactory = "commonRabbitListenerContainerFactory")
public class MailServiceQueueListener extends AbstractListener<EmailAPI> {
    @Autowired
    private MailDataService mailDataService;

    @Autowired
    public MailServiceQueueListener(MailServiceQueue queue) {
        super(queue);
    }

    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) EmailAPI api, SendEmailRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<EmailAPI, SendEmailRequest>(){
            @Override
            public Response doProcess(EmailAPI api, SendEmailRequest request) throws BasicDataServiceException {
                Response response = new Response();
                switch (api){
                    case SendEmails:
                        mailDataService.sendEmails(request.getFrom(), request.getTo(), request.getCc(), request.getBcc(), request.getSubject(), request.getMsg(),
                                request.isHtmlFormat(), request.getAttachment(), request.getExecutionDateTime());
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        });
    }
    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) EmailAPI api, NotificationRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<EmailAPI, NotificationRequest>(){
            @Override
            public Response doProcess(EmailAPI api, NotificationRequest request) throws BasicDataServiceException {
                BooleanResponse response = new BooleanResponse();
                response.setValue(mailDataService.sendNotification(request));
                return response;
            }
        });
    }
    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) EmailAPI api, TweetMessageRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<EmailAPI, TweetMessageRequest>(){
            @Override
            public Response doProcess(EmailAPI api, TweetMessageRequest request) throws BasicDataServiceException {
                Response response = new Response();
                switch (api){
                    case TweetPrivateMessage:
                        mailDataService.tweetPrivateMessage(request.getUserid(), request.getMsg());
                        break;
                    case TweetMessage:
                        mailDataService.tweetMessage(request.getMsg());
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        });
    }
}
