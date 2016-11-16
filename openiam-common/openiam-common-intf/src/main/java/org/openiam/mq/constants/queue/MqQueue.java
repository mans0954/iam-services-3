package org.openiam.mq.constants.queue;

import org.openiam.mq.constants.RabbitMQVHosts;
import org.openiam.mq.constants.RabbitMqExchange;

/**
 * Created by alexander on 28/09/16.
 */
public class MqQueue {
    private RabbitMqExchange exchange;
    private String name;
    private String routingKey;
    private String vHost;

    public MqQueue(String name) {
        this(RabbitMqExchange.COMMON_EXCHANGE, name, RabbitMQVHosts.COMMON_HOST);
    }
    public MqQueue(String name, String vHost) {
        this(RabbitMqExchange.COMMON_EXCHANGE, name, vHost);
    }
    public MqQueue(RabbitMqExchange exchange, String name, String vHost) {
        this.exchange = exchange;
        this.name = name;
        this.routingKey = name;
        this.vHost = vHost;
    }

    public RabbitMqExchange getExchange() {
        return exchange;
    }

    public String getName() {
        return name;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public String getVHost() {
        return vHost;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }
}
