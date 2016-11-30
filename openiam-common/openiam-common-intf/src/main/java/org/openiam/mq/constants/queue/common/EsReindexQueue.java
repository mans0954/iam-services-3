package org.openiam.mq.constants.queue.common;

import org.openiam.mq.constants.RabbitMqExchange;

/**
 * Created by alexander on 17/11/16.
 */
public class EsReindexQueue extends CommonQueue {
    public EsReindexQueue() {
        super(RabbitMqExchange.ELASTIC_SEARCH_EXCHANGE, EsReindexQueue.class.getSimpleName());
    }
}
