package org.openiam.mq.constants.queue.audit;

import org.openiam.mq.constants.RabbitMQVHosts;
import org.openiam.mq.constants.RabbitMqExchange;
import org.openiam.mq.constants.queue.MqQueue;


/**
 * Created by alexander on 09/11/16.
 */
public abstract class AuditQueue extends MqQueue {
    public AuditQueue(String name) {
        super(RabbitMqExchange.AUDIT_EXCHANGE, name, RabbitMQVHosts.AUDIT_HOST);
    }
    public AuditQueue(RabbitMqExchange exchange, String name) {
        super(exchange, name, RabbitMQVHosts.AUDIT_HOST);
    }
}
