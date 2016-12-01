package org.openiam.mq;

import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.msg.service.MailSenderClient;
import org.openiam.idm.srvc.msg.service.Message;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.api.common.OpenIAMAPICommon;
import org.openiam.mq.constants.queue.common.MailQueue;
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
@RabbitListener(id="emailSenderListener",
        queues = "#{MailQueue.name}",
        containerFactory = "commonRabbitListenerContainerFactory")
public class EmailSenderListener extends AbstractListener<OpenIAMAPICommon> {
    @Autowired
    private MailSenderClient mailSenderClient;
    @Autowired
    public EmailSenderListener(MailQueue queue) {
        super(queue);
    }

    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) OpenIAMAPICommon api, Message request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new AbstractListener.RequestProcessor<OpenIAMAPICommon, Message>(){
            @Override
            public Response doProcess(OpenIAMAPICommon api, Message request) throws BasicDataServiceException {
                mailSenderClient.send(request);
                return new Response(ResponseStatus.SUCCESS);
            }
        });
    }
}
