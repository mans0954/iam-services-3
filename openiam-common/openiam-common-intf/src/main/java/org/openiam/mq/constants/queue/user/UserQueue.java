package org.openiam.mq.constants.queue.user;

import org.openiam.mq.constants.RabbitMQVHosts;
import org.openiam.mq.constants.RabbitMqExchange;
import org.openiam.mq.constants.queue.MqQueue;

/**
 * Created by alexander on 18/11/16.
 */
public abstract class UserQueue extends MqQueue {

    public UserQueue(String name) {
        super(RabbitMqExchange.USER_EXCHANGE, name, RabbitMQVHosts.USER_HOST);
    }

    public UserQueue(RabbitMqExchange exchange, String name) {
        super(exchange, name, RabbitMQVHosts.USER_HOST);
    }
}
