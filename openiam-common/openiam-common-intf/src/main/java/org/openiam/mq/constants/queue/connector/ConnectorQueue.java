package org.openiam.mq.constants.queue.connector;

import org.openiam.mq.constants.RabbitMQVHosts;
import org.openiam.mq.constants.RabbitMqExchange;
import org.openiam.mq.constants.queue.MqQueue;

/**
 * Created by alexander on 18/11/16.
 */
public abstract class ConnectorQueue extends MqQueue {
    public ConnectorQueue(String name) {
        super(RabbitMqExchange.CONNECTOR_EXCHANGE, name, RabbitMQVHosts.CONNECTOR_HOST);
    }
    public ConnectorQueue(RabbitMqExchange exchange, String name) {
        super(exchange, name, RabbitMQVHosts.CONNECTOR_HOST);
    }
}
