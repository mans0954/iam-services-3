package org.openiam.mq.constants.queue.am;

import org.openiam.mq.constants.RabbitMqExchange;

/**
 * Created by alexander on 15/11/16.
 */
public class AMCacheQueue extends AMQueue {
    public AMCacheQueue() {
        super(RabbitMqExchange.AM_CACHE_EXCHANGE, AMCacheQueue.class.getSimpleName());
    }
}
