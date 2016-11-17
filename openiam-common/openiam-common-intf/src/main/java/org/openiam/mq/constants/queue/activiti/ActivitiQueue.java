package org.openiam.mq.constants.queue.activiti;

import org.openiam.mq.constants.RabbitMQVHosts;
import org.openiam.mq.constants.RabbitMqExchange;
import org.openiam.mq.constants.queue.MqQueue;


/**
 * Created by alexander on 09/11/16.
 */
public abstract class ActivitiQueue extends MqQueue {

    public ActivitiQueue(String name) {
        super(RabbitMqExchange.ACTIVITI_EXCHANGE, name, RabbitMQVHosts.ACTIVITI_HOST);
    }

    public ActivitiQueue(RabbitMqExchange exchange, String name) {
        super(exchange, name, RabbitMQVHosts.ACTIVITI_HOST);
    }

}
