package org.openiam.elasticsearch.service;

import org.openiam.elasticsearch.model.ElasticsearchReindexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Queue;

/**
 * Created by: Alexander Duckardt
 * Date: 9/18/14.
 */
@Component("elasticsearchSender")
public class ElasticsearchSender {

    @Autowired
    private JmsTemplate jmsTemplate;

//    @Autowired
//    @Qualifier(value = "esReindexQueue")
    private Queue queue;

    public void send(final ElasticsearchReindexRequest request) {
//        jmsTemplate.send(queue, new MessageCreator() {
//            public javax.jms.Message createMessage(Session session) throws JMSException {
//                javax.jms.Message message = session.createObjectMessage(request);
//                return message;
//            }
//        });
    }
}
