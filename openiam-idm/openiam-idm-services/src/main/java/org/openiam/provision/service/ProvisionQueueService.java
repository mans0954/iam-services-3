package org.openiam.provision.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import java.util.List;

@Component("provQueueService")
public class ProvisionQueueService {
    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier(value = "provQueue")
    private Queue queue;

    public void enqueue(final ProvisionDataContainer data) {
        jmsTemplate.send(queue, new MessageCreator() {
            public javax.jms.Message createMessage(Session session) throws JMSException {
                return session.createObjectMessage(data);
            }
        });
    }

    public void enqueue(final List<ProvisionDataContainer> dataList) {
        for (final ProvisionDataContainer data : dataList) {
            jmsTemplate.send(queue, new MessageCreator() {
                public javax.jms.Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(data);
                }
            });
        }
    }
}
