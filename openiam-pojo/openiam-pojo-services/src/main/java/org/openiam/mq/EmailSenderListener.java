package org.openiam.mq;

import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.OpenIAMQueue;
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
public class EmailSenderListener extends AbstractRabbitMQListener {
    @Autowired
    private EmailSenderDispatcher emailSenderProcessor;

    public EmailSenderListener() {
        super(OpenIAMQueue.MailQueue);
    }

    @Override
    protected void doOnMessage(MQRequest message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        OpenIAMAPI apiName = message.getRequestApi();
        switch (apiName){
            case SendEmail:
                addTask(emailSenderProcessor, correlationId, message, apiName, isAsync);
                break;
            default:
                break;
        }
    }
}
