package org.openiam.mq;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.mq.constants.OpenIAMAPICommon;
import org.openiam.mq.constants.queue.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.exception.RejectMessageException;
import org.openiam.mq.listener.AbstractRabbitMQListener;
import org.openiam.idm.srvc.msg.service.EmailSenderDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 01/08/16.
 */
@Component
public class EmailSenderListener extends AbstractRabbitMQListener<OpenIAMAPICommon> {
    @Autowired
    private EmailSenderDispatcher emailSenderProcessor;

    public EmailSenderListener() {
        super(OpenIAMQueue.MailQueue);
    }

    @Override
    protected void doOnMessage(MQRequest<BaseServiceRequest, OpenIAMAPICommon> message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        OpenIAMAPICommon apiName = message.getRequestApi();
        switch (apiName){
            case SendEmail:
                addTask(emailSenderProcessor, correlationId, message, apiName, isAsync);
                break;
            default:
                break;
        }
    }
}
