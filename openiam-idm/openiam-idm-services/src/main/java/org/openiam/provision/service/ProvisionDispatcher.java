package org.openiam.provision.service;

import org.openiam.thread.Sweepable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

@Component("provDispatcher")
public class ProvisionDispatcher implements Sweepable {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier(value = "provQueue")
    private Queue queue;

    public void sweep() {
        jmsTemplate.browse(queue, new BrowserCallback<Object>() {
            @Override
            public Object doInJms(Session session, QueueBrowser browser) throws JMSException {
                final List<List<ProvisionDataContainer>> batchList = new LinkedList<List<ProvisionDataContainer>>();
                List<ProvisionDataContainer> list = new ArrayList<ProvisionDataContainer>(100);
                Enumeration e = browser.getEnumeration();
                int count = 0;
                while (e.hasMoreElements()) {
                    list.add((ProvisionDataContainer)((ObjectMessage)jmsTemplate.receive(queue)).getObject());
                    if (count++ >= 100) {
                        batchList.add(list);
                        list = new ArrayList<ProvisionDataContainer>(100);
                    }
                    e.nextElement();
                }
                batchList.add(list);
                for(final List<ProvisionDataContainer> entityList : batchList) {
                    process(entityList);
                }

                return null;
            }
        });
    }

    private void provision(ProvisionDataContainer data) {
        Thread t = Thread.currentThread();
        System.out.println(t);
        System.out.println("Processing " + data);
    }

    private void process(List<ProvisionDataContainer> entities) {
        //TODO: add support for batch processing if possible
        for (ProvisionDataContainer data : entities) {
            provision(data);
        }
    }

}
