package org.openiam.idm.srvc.msg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;

@Component("mailSender")
public class MailSender {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier(value = "mailQueue")
    private Queue queue;

    public void send(final Message mail) {
        jmsTemplate.send(queue, new MessageCreator() {
            public javax.jms.Message createMessage(Session session) throws JMSException {
                javax.jms.Message message = session.createObjectMessage(mail);
                if (mail.getProcessingTime() != null) {
                    message.setLongProperty("_HQ_SCHED_DELIVERY", mail.getProcessingTime().getTime()); //HornetQ Scheduled Delivery Property
                }
                return message;
            }
        });
    }

}
