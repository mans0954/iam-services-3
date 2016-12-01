package org.openiam.mq.constants.queue.common;

import org.openiam.mq.constants.RabbitMQVHosts;
import org.openiam.mq.constants.RabbitMqExchange;
import org.openiam.mq.constants.queue.MqQueue;


/**
 * Created by alexander on 09/11/16.
 */
public abstract class CommonQueue extends MqQueue {

    public CommonQueue(String name) {
        super(RabbitMqExchange.COMMON_EXCHANGE, name, RabbitMQVHosts.COMMON_HOST);
    }

    public CommonQueue(RabbitMqExchange exchange, String name) {
        super(exchange, name, RabbitMQVHosts.COMMON_HOST);
    }

}
