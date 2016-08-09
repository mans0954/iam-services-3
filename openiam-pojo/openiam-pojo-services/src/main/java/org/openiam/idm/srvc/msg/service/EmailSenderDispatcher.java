package org.openiam.idm.srvc.msg.service;

import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 01/08/16.
 */
@Component
public class EmailSenderDispatcher extends AbstractAPIDispatcher<Message, Response> {
    @Autowired
    MailSenderClient mailSenderClient;

    public EmailSenderDispatcher() {
        super(Response.class);
    }

    @Override
    protected void processingApiRequest(final OpenIAMAPI openIAMAPI, final Message message, Response response) throws BasicDataServiceException {
        mailSenderClient.send(message);
    }
}
