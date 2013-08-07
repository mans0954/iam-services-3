package org.openiam.idm.srvc.msg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.*;
import org.springframework.stereotype.Component;

@Component("mailDispatcher")
public class MailDispatcher implements SessionAwareMessageListener {

    @Autowired
    MailSenderClient mailSenderClient;

    @Override
    public void onMessage(javax.jms.Message message, Session session) throws JMSException {
        Message mail = (Message)((ObjectMessage)message).getObject();
        mailSenderClient.send(mail);
    }
}
