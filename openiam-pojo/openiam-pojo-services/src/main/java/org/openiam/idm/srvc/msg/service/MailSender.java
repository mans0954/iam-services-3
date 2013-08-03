package org.openiam.idm.srvc.msg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Destination;

@Component("mailSender")
public class MailSender {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier(value = "mailQueue")
    private Destination queue;

    @Autowired
    MailSenderClient mailSenderClient;

    public void send(Message msg) {

        mailSenderClient.send(msg);
    }

}
