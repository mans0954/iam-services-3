package org.openiam.mq.constants.queue.idm;

import org.openiam.mq.constants.RabbitMQVHosts;
import org.openiam.mq.constants.RabbitMqExchange;
import org.openiam.mq.constants.queue.MqQueue;

/**
 * Created by alexander on 18/11/16.
 */
public abstract class IdmQueue extends MqQueue {
    public IdmQueue(String name) {
        super(RabbitMqExchange.IDM_EXCHANGE, name, RabbitMQVHosts.IDM_HOST);
    }
    public IdmQueue(RabbitMqExchange exchange, String name) {
        super(exchange, name, RabbitMQVHosts.IDM_HOST);
    }
}
