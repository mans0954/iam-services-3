package org.openiam.mq.constants.queue.am;

import org.openiam.mq.constants.RabbitMQVHosts;
import org.openiam.mq.constants.RabbitMqExchange;
import org.openiam.mq.constants.queue.MqQueue;


/**
 * Created by alexander on 09/11/16.
 */
public abstract class AMQueue extends MqQueue {
    public AMQueue(String name) {
        super(RabbitMqExchange.AM_EXCHANGE, name, RabbitMQVHosts.AM_HOST);
    }
    public AMQueue(RabbitMqExchange exchange, String name) {
        super(exchange, name, RabbitMQVHosts.AM_HOST);
    }
}
