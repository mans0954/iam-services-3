package org.openiam.provision.service;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component("provQueueService")
public class ProvisionQueueService {
    @Autowired
    private JmsTemplate jmsTemplate;

    public void enqueue(final ProvisionDataContainer data) {
        jmsTemplate.send("provQueue", new MessageCreator() {
            public javax.jms.Message createMessage(Session session) throws JMSException {
                return session.createObjectMessage(data);
            }
        });
    }

    public void enqueue(final List<ProvisionDataContainer> dataList) {
        for (final ProvisionDataContainer data : dataList) {
            enqueue(data);
        }
    }
}
