package org.openiam.mq.constants;

/**
 * Created by alexander on 28/09/16.
 */
public interface MqQueue {
    public RabbitMqExchange getExchange();

    public String getName();

    public String getRoutingKey();

    public void setName(String name);

    public boolean getTempQueue();
}
